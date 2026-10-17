package com.katt.changedextras.entity.beasts;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Decorative mover rendered as a 2-block-tall billboard. The scenario NBT marks a path with
 * {@code changedextras:route} and {@code changedextras:failed_route} blocks; those markers are
 * replaced with air on paste and their positions are handed to this entity (see
 * {@link #setRoute}). The Snep glides straight from waypoint to waypoint in nearest-unvisited
 * order, ignoring gravity and collisions so it never falls or gets stuck on the scenario geometry.
 * <p>
 * If {@link #setFailed(boolean)} is set (the jackpot roll failed), the Snep stops the moment it
 * reaches a {@code failed_route} waypoint; otherwise the red waypoints are walked like any other.
 */
public class SnepEntity extends PathfinderMob {
    private static final String FAILED_TAG = "changedextras.snep_failed";
    private static final double ARRIVE_DISTANCE_SQ = 0.64D; // 0.8 block radius for reliable arrival
    private static final double GLIDE_SPEED = 0.15D;

    private static final EntityDataAccessor<Boolean> FAILED =
            SynchedEntityData.defineId(SnepEntity.class, EntityDataSerializers.BOOLEAN);

    private final List<BlockPos> routePoints = new ArrayList<>();
    private final Set<Long> failedPoints = new HashSet<>();
    private final Set<Long> visitedRoute = new HashSet<>();
    private BlockPos currentTarget = null;
    private boolean stopped;

    public SnepEntity(EntityType<? extends SnepEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        // The Snep glides along its route like a billboard: no gravity, no collisions.
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FAILED, false);
    }

    /**
     * Hands the Snep the route recorded from the scenario build. {@code failedPoints} are the
     * positions that were marked with the red {@code failed_route} block.
     */
    public void setRoute(List<BlockPos> route, Set<Long> failedPoints) {
        this.routePoints.clear();
        this.routePoints.addAll(route);
        this.failedPoints.clear();
        this.failedPoints.addAll(failedPoints);
        this.visitedRoute.clear();
        this.currentTarget = null;
        this.stopped = false;
    }

    public boolean hasRoute() {
        return !this.routePoints.isEmpty();
    }

    /**
     * True once the Snep has no waypoints left to walk (it stopped, or every route point has been
     * visited). Used by the domain to know when the jackpot walk is finished and it can break.
     */
    public boolean isFinishedRoute() {
        if (this.stopped) {
            return true;
        }
        if (this.routePoints.isEmpty()) {
            return true;
        }
        if (this.visitedRoute.size() >= this.routePoints.size()) {
            return true;
        }
        return findNearestUnvisited() == null;
    }

    public boolean isFailed() {
        return this.entityData.get(FAILED);
    }

    public void setFailed(boolean failed) {
        this.entityData.set(FAILED, failed);
        if (!failed) {
            this.stopped = false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(FAILED_TAG, isFailed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFailed(tag.getBoolean(FAILED_TAG));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.stopped || this.routePoints.isEmpty()) {
            if (this.stopped) {
                this.setDeltaMovement(Vec3.ZERO);
                this.hurtMarked = true;
            }
            return;
        }

        // Keep current target unless arrived or unassigned
        if (this.currentTarget == null || this.visitedRoute.contains(this.currentTarget.asLong())) {
            this.currentTarget = findNearestUnvisited();
            if (this.currentTarget == null) {
                this.stopped = true;
                this.setDeltaMovement(Vec3.ZERO);
                this.hurtMarked = true;
                return;
            }
        }

        BlockPos target = this.currentTarget;
        double tx = target.getX() + 0.5D;
        double ty = target.getY();
        double tz = target.getZ() + 0.5D;

        if (this.distanceToSqr(tx, ty, tz) < ARRIVE_DISTANCE_SQ) {
            if (isFailed() && this.failedPoints.contains(target.asLong())) {
                // Jackpot failed: hold position on the red failed-route marker.
                this.stopped = true;
                this.setDeltaMovement(Vec3.ZERO);
                this.hurtMarked = true;
                return;
            }

            this.visitedRoute.add(target.asLong());
            this.currentTarget = findNearestUnvisited();
            if (this.currentTarget == null) {
                this.stopped = true;
                this.setDeltaMovement(Vec3.ZERO);
                this.hurtMarked = true;
                return;
            }
            target = this.currentTarget;
            tx = target.getX() + 0.5D;
            ty = target.getY();
            tz = target.getZ() + 0.5D;
        }

        Vec3 current = this.position();
        double dx = tx - current.x;
        double dy = ty - current.y;
        double dz = tz - current.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 1.0E-4D) {
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        // Glide straight toward the waypoint, ignoring gravity and collisions.
        Vec3 motion = new Vec3(dx / dist * GLIDE_SPEED, dy / dist * GLIDE_SPEED, dz / dist * GLIDE_SPEED);
        this.setDeltaMovement(motion);
        this.hurtMarked = true;

        float yaw = (float) (Mth.atan2(motion.z, motion.x) * (180.0D / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    private BlockPos findNearestUnvisited() {
        BlockPos origin = this.blockPosition();
        BlockPos best = null;
        double bestSq = Double.MAX_VALUE;

        for (BlockPos point : this.routePoints) {
            if (this.visitedRoute.contains(point.asLong())) {
                continue;
            }
            double distSq = origin.distSqr(point);
            if (distSq < bestSq) {
                bestSq = distSq;
                best = point;
            }
        }

        return best;
    }
}
