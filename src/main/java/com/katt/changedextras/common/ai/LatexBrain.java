package com.katt.changedextras.common.ai;

import com.katt.changedextras.common.LatexCuddleHelper;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LatexBrain {
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> CHANGED_HUMANOIDS =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("changed", "humanoids"));
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> CHANGED_LATEXES =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("changed", "latexes"));
    private static final double ATTACK_RANGE = 2.9D;
    private static final double CLOSE_PRESSURE_RANGE = 7.5D;
    private static final double SEARCH_REACH = 1.75D;
    private static final double LOOT_RANGE = 10.0D;
    private static final double PICKUP_RANGE = 2.2D;
    private static final double SPEED_SCALE = 0.85D;
    private static final double DIRECT_WALK_SPEED = 0.68D * SPEED_SCALE;
    private static final double DIRECT_RUN_SPEED = 0.76D * SPEED_SCALE;
    private static final double DIRECT_SPRINT_SPEED = 0.82D * SPEED_SCALE;
    private static final double DIRECT_FACE_MIN_DISTANCE_SQR = 0.36D;
    private static final double WATCH_APPROACH_SPEED = 0.74D * SPEED_SCALE;
    private static final double WATCH_SIDE_OFFSET = 1.15D;
    private static final double WATCH_MIN_DISTANCE = 2.6D;
    private static final double WATCH_MAX_DISTANCE = 4.4D;
    private static final double NARROW_FOOTING_SAMPLE = 0.32D;
    private static final double BREAK_MOVE_SPEED = 0.72D * SPEED_SCALE;
    private static final double BUILD_MOVE_SPEED = 0.68D * SPEED_SCALE;
    private static final double SEARCH_MOVE_SPEED = 0.68D * SPEED_SCALE;
    private static final double LOOT_MOVE_SPEED = 0.66D * SPEED_SCALE;
    private static final double RETREAT_MOVE_SPEED = 0.72D * SPEED_SCALE;
    private static final double TOWER_ALIGN_SPEED = 0.46D * SPEED_SCALE;
    private static final double CAUTIOUS_FOOTING_SPEED = 0.42D * SPEED_SCALE;
    private static final double MAX_VISIBLE_TARGET_RANGE = 14.0D;
    private static final double MAX_REMEMBERED_TARGET_RANGE = 20.0D;
    private static final double HARD_TARGET_DROP_RANGE = 24.0D;
    private static final double PLAYER_SPRINT_JUMP_BOOST = 0.18D;
    private static final double PLAYER_SPRINT_JUMP_SPEED_CAP = 0.32D;
    private static final double BREAK_PATH_STEP = 0.5D;
    private static final int BREAK_PATH_SCAN_BLOCKS = 16;
    private static final int IMAGINARY_PATH_SCAN_BLOCKS = 12;
    private static final int IMAGINARY_PATH_MAX_BLOCKS = 8;
    private static final int ALT_PATH_SEARCH_RADIUS = 3;
    private static final int TERRAIN_COMMIT_TICKS = 10;
    private static final int BUILD_BLOCK_RESERVE = 4;
    private static final int SEARCH_TIMEOUT = 80;
    private static final int BLOCK_BREAK_COMMIT = 18;
    private static final int ATTACK_COOLDOWN = 10;
    private static final int DECISION_INTERVAL_TICKS = 4;
    private static final int PATH_CACHE_TICKS = 8;
    private static final int TERRAIN_CACHE_TICKS = 6;
    private static final double BREAK_PREFERENCE_PENALTY = 18.0D;
    private static final double STRAIGHT_ROUTE_ALIGNMENT_WEIGHT = 6.0D;
    private static final double STRAIGHT_ROUTE_PATH_NODE_WEIGHT = 0.35D;
    enum State {
        IDLE,
        CHASE,
        ATTACK,
        BREAK,
        BUILD,
        SEARCH,
        LOOT,
        REPOSITION,
        WATCH_TARGET
    }

    private record TerrainPlan(@Nullable BlockPos breakPos, @Nullable BlockPos buildPos, List<BlockPos> imaginedBuildPath,
                               double breakCost, double buildCost) {}
    private record WaterBucketSource(ItemStack stack, @Nullable InteractionHand hand, int inventorySlot) {}

    private State state = State.IDLE;
    private int thinkCooldown = 0;

    public void tick(ChangedEntity mob, LatexMind mind) {
        tickCooldowns(mind);
        equipBestArmor(mob, mind);
        stopShieldingIfIdle(mob);

        if (tryWaterClutch(mob, mind)) {
            return;
        }

        if (shouldDropTarget(mob, mind)) {
            mob.setTarget(null);
            mind.clearTarget();
        }

        LivingEntity target = resolveTarget(mob, mind);
        if (target != null && target.isAlive()) {
            boolean los = mob.hasLineOfSight(target);
            mind.remember(target, mob.tickCount, los);
            if (!los) {
                mind.lostSightTicks++;
            }
            mind.chaseTime++;
        } else if (mind.remembersRecentTarget(mob)) {
            mind.lostSightTicks++;
            mind.searchTicks++;
        } else {
            mind.clearTarget();
        }

        updateStuck(mob, mind);

        if (thinkCooldown-- <= 0) {
            thinkCooldown = DECISION_INTERVAL_TICKS;
            state = decideState(mob, mind, target);
        }

        switch (state) {
            case CHASE -> chase(mob, target);
            case ATTACK -> attack(mob, target);
            case BREAK -> breakObstacle(mob, mind, target);
            case BUILD -> buildTowardTarget(mob, mind, target);
            case SEARCH -> search(mob, mind);
            case LOOT -> loot(mob);
            case REPOSITION -> reposition(mob, mind, target);
            case WATCH_TARGET -> watchTarget(mob, target);
            default -> idle(mob);
        }
    }

    private void tickCooldowns(LatexMind mind) {
        if (mind.attackCooldown > 0) mind.attackCooldown--;
        if (mind.breakCooldown > 0) mind.breakCooldown--;
        if (mind.buildCooldown > 0) mind.buildCooldown--;
        if (mind.clutchCooldown > 0) mind.clutchCooldown--;
        if (mind.jumpCooldown > 0) mind.jumpCooldown--;
        if (mind.lootScanCooldown > 0) mind.lootScanCooldown--;
        if (mind.equipmentScanCooldown > 0) mind.equipmentScanCooldown--;
        if (mind.allyAlertCooldown > 0) mind.allyAlertCooldown--;
        if (mind.recentBreakTicks > 0) mind.recentBreakTicks--;
    }

    private State decideState(ChangedEntity mob, LatexMind mind, @Nullable LivingEntity target) {
        if (target != null && target.isAlive()) {
            double distance = mob.distanceTo(target);
            boolean hasReachablePath = hasReachablePath(mob, mind, target);

            if (shouldRetreat(mob, target)) {
                return State.REPOSITION;
            }

            if (mind.hasLOS && distance <= ATTACK_RANGE + 0.35D) {
                return State.ATTACK;
            }

            if (shouldTowerUp(mob, target) && shouldBuild(mob, mind, target)) {
                return State.BUILD;
            }

            if (hasReachablePath) {
                mind.plannedBuildPos = null;
                mind.plannedBreakPos = null;
                return State.CHASE;
            }

            if (!mind.pathFailed && mind.stuckTicks < 6) {
                return State.CHASE;
            }

            State terrainState = chooseTerrainAction(mob, mind, target);
            if (terrainState != null) {
                return terrainState;
            }

            if (hasNearbyFistMineableDropBlock(mob, target)
                    && mind.noPathTicks < TERRAIN_COMMIT_TICKS
                    && mind.stuckTicks < 6) {
                return State.CHASE;
            }

            return State.WATCH_TARGET;
        }

        if (mind.remembersRecentTarget(mob) && mind.searchTicks < SEARCH_TIMEOUT) {
            return State.SEARCH;
        }

        return hasInterestingLootNearby(mob) ? State.LOOT : State.IDLE;
    }

    private void idle(ChangedEntity mob) {
        mob.setSprinting(false);
    }

    private void chase(ChangedEntity mob, @Nullable LivingEntity target) {
        if (target == null) return;

        equipBestCombatTool(mob);
        boolean bridgeStraightChase = shouldUseBridgeStraightChase(mob, target);
        boolean narrowFooting = isOnNarrowFooting(mob) || bridgeStraightChase;
        boolean sprintJumpChase = !narrowFooting && canSprintJumpChase(mob, target);
        mob.setSprinting(sprintJumpChase);
        double speed = resolveChaseSpeed(target, sprintJumpChase);
        if (narrowFooting) {
            speed = Math.min(speed, CAUTIOUS_FOOTING_SPEED);
        }
        Path reachPath = bridgeStraightChase ? null : createReachPath(mob, target);
        if (bridgeStraightChase) {
            applyStraightBridgeChaseMovement(mob, target, speed);
        } else if (reachPath != null) {
            mob.setZza(0.0F);
            mob.setXxa(0.0F);
            mob.getNavigation().moveTo(reachPath, speed);
            faceTarget(mob, target, 8.0F);
            mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
        } else if (canUseDirectChase(mob, target)) {
            applyDirectChaseMovement(mob, target, speed);
        } else {
            mob.setZza(0.0F);
            mob.setXxa(0.0F);
            mob.getNavigation().moveTo(target, speed);
            mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
        }
        if (!narrowFooting && !bridgeStraightChase) {
            smartJump(mob, target);
        }
    }

    private void watchTarget(ChangedEntity mob, @Nullable LivingEntity target) {
        mob.setSprinting(false);
        mob.setZza(0.0F);
        mob.setXxa(0.0F);

        if (target != null) {
            moveNearWatchPosition(mob, target);
            faceTarget(mob, target, 10.0F);
            mob.getLookControl().setLookAt(target, 16.0F, 16.0F);
        } else {
            mob.getNavigation().stop();
        }
    }

    private void moveNearWatchPosition(ChangedEntity mob, LivingEntity target) {
        Vec3 toMob = mob.position().subtract(target.position()).multiply(1.0D, 0.0D, 1.0D);
        if (toMob.lengthSqr() < 0.001D) {
            toMob = new Vec3(1.0D, 0.0D, 0.0D);
        }

        double distance = Math.sqrt(toMob.lengthSqr());
        Vec3 radial = toMob.normalize();
        boolean narrowFooting = isOnNarrowFooting(mob);
        Vec3 sideways = narrowFooting
                ? Vec3.ZERO
                : new Vec3(-radial.z, 0.0D, radial.x)
                        .scale(((mob.tickCount / 20) & 1) == 0 ? WATCH_SIDE_OFFSET : -WATCH_SIDE_OFFSET);
        Vec3 watchPos = target.position().add(radial.scale(WATCH_MAX_DISTANCE)).add(sideways);

        if (distance > WATCH_MAX_DISTANCE + 0.4D) {
            moveTowardWatchPos(mob, target, watchPos);
        } else if (distance < WATCH_MIN_DISTANCE) {
            Vec3 retreatPos = target.position().add(radial.scale(WATCH_MIN_DISTANCE + 0.8D));
            moveTowardWatchPos(mob, target, retreatPos);
        } else if (!narrowFooting && mob.tickCount % 24 == 0) {
            moveTowardWatchPos(mob, target, watchPos);
        } else {
            mob.getNavigation().stop();
        }
    }

    private void moveTowardWatchPos(ChangedEntity mob, LivingEntity target, Vec3 watchPos) {
        Path path = mob.getNavigation().createPath(BlockPos.containing(watchPos), 0);
        if (path != null && path.canReach()) {
            mob.getNavigation().moveTo(path, WATCH_APPROACH_SPEED);
        } else if (mob.hasLineOfSight(target)) {
            mob.getNavigation().moveTo(watchPos.x, watchPos.y, watchPos.z, WATCH_APPROACH_SPEED);
        } else {
            mob.getNavigation().stop();
        }
    }

    private void attack(ChangedEntity mob, @Nullable LivingEntity target) {
        if (target == null) return;

        equipBestCombatTool(mob);
        mob.setSprinting(false);

        double distance = mob.distanceTo(target);
        mob.getLookControl().setLookAt(target, 16.0F, 16.0F);

        if (distance > ATTACK_RANGE + 0.35D) {
            chase(mob, target);
            return;
        }

        mob.getNavigation().stop();
        mob.setZza(0.0F);
        mob.setXxa(0.0F);
        faceTarget(mob, target, 10.0F);

        LatexMind mind = LatexMindStore.get(mob);
        if (mind.attackCooldown > 0) return;

        mob.swing(InteractionHand.MAIN_HAND);
        if (mob.distanceTo(target) <= ATTACK_RANGE + 0.15D) {
            mob.doHurtTarget(target);
            Vec3 knock = target.position().subtract(mob.position()).normalize().scale(0.25D);
            target.push(knock.x, 0.08D, knock.z);
            mind.attackCooldown = ATTACK_COOLDOWN;
        }
    }

    private void breakObstacle(ChangedEntity mob, LatexMind mind, @Nullable LivingEntity target) {
        if (target == null) return;
        if (hasReachablePath(mob, mind, target)) {
            resetBlockBreaking(mob, mind);
            mind.plannedBreakPos = null;
            chase(mob, target);
            return;
        }

        BlockPos pos = mind.activeBreakPos;
        if (pos == null) {
            pos = mind.plannedBreakPos != null ? mind.plannedBreakPos : findBreakTarget(mob, target);
        }
        if (pos == null) {
            resetBlockBreaking(mob, mind);
            mind.plannedBreakPos = null;
            stopShieldingIfIdle(mob);
            chase(mob, target);
            return;
        }

        BlockState state = mob.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(mob.level(), pos) < 0 || mind.breakCooldown > 0 || !canBreakBlock(mob, pos, state)) {
            resetBlockBreaking(mob, mind);
            mind.plannedBreakPos = null;
            stopShieldingIfIdle(mob);
            chase(mob, target);
            return;
        }

        if (mind.activeBreakPos == null || !mind.activeBreakPos.equals(pos)) {
            resetBlockBreaking(mob, mind);
            mind.activeBreakPos = pos.immutable();
        }

        Vec3 breakCenter = Vec3.atCenterOf(pos);
        if (mob.distanceToSqr(breakCenter) > 6.25D) {
            mob.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, BREAK_MOVE_SPEED);
        } else {
            mob.getNavigation().stop();
            mob.setDeltaMovement(0.0D, mob.getDeltaMovement().y, 0.0D);
            mob.setZza(0.0F);
            mob.setXxa(0.0F);
        }

        faceBlock(mob, pos, 10.0F);

        ItemStack tool = findBestMiningTool(mob, state);
        if (!tool.isEmpty()) {
            mob.setItemInHand(InteractionHand.MAIN_HAND, tool);
        }

        mob.stopUsingItem();

        mind.blockBreakTicks++;
        if (mind.blockBreakTicks % 4 == 0) {
            mob.swing(InteractionHand.MAIN_HAND);
        }

        mind.blockBreakProgress += getBreakProgressPerTick(mob, pos, state, tool);

        if (mob.level() instanceof ServerLevel server) {
            int stage = Math.min(9, Math.max(0, Mth.floor(mind.blockBreakProgress * 10.0F)));
            server.destroyBlockProgress(mob.getId(), pos, stage);
        }

        if (mind.blockBreakProgress >= 1.0F) {
            mob.swing(InteractionHand.MAIN_HAND);
            mob.level().destroyBlock(pos, true, mob);
            resetBlockBreaking(mob, mind);
            mind.breakCooldown = 8;
            mind.recentBreakTicks = 20;
            mind.plannedBreakPos = null;
            stopShieldingIfIdle(mob);
        }
    }

    private void resetBlockBreaking(ChangedEntity mob, LatexMind mind) {
        if (mob.level() instanceof ServerLevel server && mind.activeBreakPos != null) {
            server.destroyBlockProgress(mob.getId(), mind.activeBreakPos, -1);
        }

        mind.activeBreakPos = null;
        mind.blockBreakTicks = 0;
        mind.blockBreakProgress = 0.0F;
    }

    private void faceBlock(ChangedEntity mob, BlockPos pos, float maxTurnStep) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 horizontalDelta = center.subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
        if (horizontalDelta.lengthSqr() < DIRECT_FACE_MIN_DISTANCE_SQR) {
            return;
        }

        float desiredYaw = (float)(Mth.atan2(horizontalDelta.z, horizontalDelta.x) * (180.0F / Math.PI)) - 90.0F;
        float smoothedYaw = rotlerp(mob.getYRot(), desiredYaw, maxTurnStep);
        mob.setYRot(smoothedYaw);
        mob.setYBodyRot(rotlerp(mob.yBodyRot, smoothedYaw, maxTurnStep));
        mob.yHeadRot = rotlerp(mob.yHeadRot, smoothedYaw, maxTurnStep + 2.0F);
        mob.yHeadRotO = mob.yHeadRot;
    }

    private float getBreakProgressPerTick(ChangedEntity mob, BlockPos pos, BlockState state, ItemStack tool) {
        float hardness = state.getDestroySpeed(mob.level(), pos);
        if (hardness <= 0.0F) {
            return 1.0F;
        }

        float speed = 1.0F;
        if (!tool.isEmpty()) {
            speed = Math.max(1.0F, tool.getDestroySpeed(state));
        }

        if (canBreakBlock(mob, pos, state)) {
            return Math.max(0.08F, speed / (hardness * 8.0F));
        }

        return Math.max(0.03F, speed / (hardness * 24.0F));
    }

    private void buildTowardTarget(ChangedEntity mob, LatexMind mind, @Nullable LivingEntity target) {
        if (target == null) return;

        boolean towerMode = shouldTowerUp(mob, target);
        if (!towerMode) {
            pruneImaginedBuildPath(mob, mind);
        }

        BlockPos placePos = towerMode
                ? findBuildPlacement(mob, target)
                : (!mind.imaginedBuildPath.isEmpty()
                        ? mind.imaginedBuildPath.get(0)
                        : (mind.plannedBuildPos != null ? mind.plannedBuildPos : findBuildPlacement(mob, target)));
        if (placePos == null) {
            mind.plannedBuildPos = null;
            mind.imaginedBuildPath.clear();
            chase(mob, target);
            return;
        }

        ItemStack blockStack = findBestBuildingBlock(mob);
        if (blockStack.isEmpty()) {
            ItemEntity source = findBestBlockPickup(mob);
            if (source != null) {
                mob.getNavigation().moveTo(source, BUILD_MOVE_SPEED);
                if (mob.distanceTo(source) < PICKUP_RANGE) {
                    pickUpItemToHands(mob, source);
                }
            } else {
                mind.plannedBuildPos = null;
                chase(mob, target);
            }
            return;
        }

        if (!(blockStack.getItem() instanceof BlockItem blockItem)) {
            mind.plannedBuildPos = null;
            mind.imaginedBuildPath.clear();
            chase(mob, target);
            return;
        }

        BlockState placeState = blockItem.getBlock().defaultBlockState();
        boolean towerPlacement = towerMode && isTowerPlacement(mob, target, placePos);
        boolean canPlace = towerPlacement ? canPlaceTowerBlockAt(mob, placePos, placeState) : canPlaceBlockAt(mob, placePos, placeState);
        if (!canPlace) {
            if (!towerMode && !mind.imaginedBuildPath.isEmpty() && placePos.equals(mind.imaginedBuildPath.get(0))) {
                mind.imaginedBuildPath.remove(0);
            }
            mind.plannedBuildPos = null;
            chase(mob, target);
            return;
        }

        double placementSpeed = isCautiousPlacement(mob, placePos) ? CAUTIOUS_FOOTING_SPEED : BUILD_MOVE_SPEED;
        if (!moveNearPlacement(mob, placePos)) {
            mob.getNavigation().moveTo(placePos.getX() + 0.5D, placePos.getY(), placePos.getZ() + 0.5D, placementSpeed);
            return;
        }

        if (towerPlacement) {
            if (!handleTowerBuildStep(mob, mind, target, blockStack, placeState)) {
                mind.plannedBuildPos = null;
                chase(mob, target);
            }
            return;
        }

        mob.setItemInHand(InteractionHand.MAIN_HAND, blockStack);
        mob.getLookControl().setLookAt(placePos.getX() + 0.5D, placePos.getY() + 0.5D, placePos.getZ() + 0.5D);
        mob.swing(InteractionHand.MAIN_HAND);

        if (mob.level().setBlock(placePos, placeState, 3)) {
            SoundType sound = placeState.getSoundType();
            mob.level().playSound(null, placePos, sound.getPlaceSound(), mob.getSoundSource(), sound.getVolume(), sound.getPitch());
            consumeOneMatchingItem(mob, blockStack);
            mind.buildCooldown = 10;
            if (!mind.imaginedBuildPath.isEmpty() && placePos.equals(mind.imaginedBuildPath.get(0))) {
                mind.imaginedBuildPath.remove(0);
            }
            mind.plannedBuildPos = mind.imaginedBuildPath.isEmpty() ? null : mind.imaginedBuildPath.get(0);
            if (towerPlacement) {
                mind.jumpCooldown = 8;
            } else if (target.getY() > mob.getY() + 1.2D && mob.onGround()) {
                triggerJump(mob, mind, null, 8);
            }
        }
    }

    private boolean handleTowerBuildStep(ChangedEntity mob, LatexMind mind, LivingEntity target, ItemStack blockStack, BlockState placeState) {
        BlockPos towerPos = mob.blockPosition();
        Vec3 center = Vec3.atBottomCenterOf(towerPos);
        double dx = center.x - mob.getX();
        double dz = center.z - mob.getZ();
        double horizontalOffsetSqr = dx * dx + dz * dz;

        mob.getNavigation().stop();
        mob.setDeltaMovement(0.0D, mob.getDeltaMovement().y, 0.0D);
        mob.getLookControl().setLookAt(target, 8.0F, 8.0F);

        if (horizontalOffsetSqr > 0.03D) {
            mob.getNavigation().moveTo(center.x, mob.getY(), center.z, TOWER_ALIGN_SPEED);
            return true;
        }

        if (!canPlaceTowerBlockAt(mob, towerPos, placeState)) {
            return false;
        }

        if (mob.level().getBlockState(towerPos).canBeReplaced()) {
            mob.setItemInHand(InteractionHand.MAIN_HAND, blockStack);
            mob.swing(InteractionHand.MAIN_HAND);
            if (mob.level().setBlock(towerPos, placeState, 3)) {
                SoundType sound = placeState.getSoundType();
                mob.level().playSound(null, towerPos, sound.getPlaceSound(), mob.getSoundSource(), sound.getVolume(), sound.getPitch());
                consumeOneMatchingItem(mob, blockStack);
                mind.buildCooldown = 6;
                mind.plannedBuildPos = null;
            }
        }

        if (mob.onGround() && mind.jumpCooldown == 0) {
            triggerJump(mob, mind, null, 4);
        }

        return true;
    }

    private void search(ChangedEntity mob, LatexMind mind) {
        if (mind.lastSeenPos == null) return;

        mob.getNavigation().moveTo(
                mind.lastSeenPos.getX() + 0.5D,
                mind.lastSeenPos.getY(),
                mind.lastSeenPos.getZ() + 0.5D,
                SEARCH_MOVE_SPEED
        );

        if (mob.blockPosition().closerThan(mind.lastSeenPos, SEARCH_REACH)) {
            mind.searchTicks = SEARCH_TIMEOUT;
        } else if (mind.stuckTicks > 10) {
            mind.searchTicks += 5;
        }
    }

    private void loot(ChangedEntity mob) {
        ItemEntity item = findBestItemEntity(mob);
        if (item == null) return;

        mob.getNavigation().moveTo(item, LOOT_MOVE_SPEED);
        mob.getLookControl().setLookAt(item, 25.0F, 25.0F);

        if (mob.distanceTo(item) < PICKUP_RANGE) {
            pickUpItemToHands(mob, item);
        }
    }

    private void reposition(ChangedEntity mob, LatexMind mind, @Nullable LivingEntity target) {
        if (target == null) return;
        mob.setSprinting(false);

        Vec3 away = mob.position().subtract(target.position());
        if (away.lengthSqr() < 0.001D) {
            away = new Vec3(1.0D, 0.0D, 0.0D);
        }

        Vec3 retreat = mob.position().add(away.normalize().scale(4.0D));
        mob.getNavigation().moveTo(retreat.x, retreat.y, retreat.z, RETREAT_MOVE_SPEED);
        if (mind.jumpCooldown == 0 && mob.isEyeInFluid(FluidTags.WATER)) {
            mob.setDeltaMovement(0.0D, 0.42D, 0.0D);
            mob.hasImpulse = true;
            mind.jumpCooldown = 8;
        }
    }

    private boolean shouldRetreat(ChangedEntity mob, LivingEntity target) {
        return mob.getHealth() < mob.getMaxHealth() * 0.25F
                && mob.distanceTo(target) < 3.0D
                && !mob.hasLineOfSight(target);
    }

    private boolean canUseDirectChase(ChangedEntity mob, LivingEntity target) {
        return mob.hasLineOfSight(target)
                && Math.abs(target.getY() - mob.getY()) < 1.8D
                && !mob.horizontalCollision
                && !isOnNarrowFooting(mob)
                && mob.distanceTo(target) < 18.0D;
    }

    private void applyDirectChaseMovement(ChangedEntity mob, LivingEntity target, double moveSpeed) {
        mob.setZza(0.0F);
        mob.setXxa(0.0F);
        faceTarget(mob, target, 8.0F);
        mob.getNavigation().moveTo(target, moveSpeed);
    }

    private void applyStraightBridgeChaseMovement(ChangedEntity mob, LivingEntity target, double moveSpeed) {
        Vec3 forward = horizontalForwardToTarget(mob, target);
        Vec3 center = mob.position();
        Vec3 straightTarget = new Vec3(
                center.x + forward.x * Math.max(1.0D, mob.distanceTo(target)),
                mob.getY(),
                center.z + forward.z * Math.max(1.0D, mob.distanceTo(target))
        );
        mob.setZza(0.0F);
        mob.setXxa(0.0F);
        faceTarget(mob, target, 6.0F);
        mob.getNavigation().moveTo(straightTarget.x, straightTarget.y, straightTarget.z, moveSpeed);
        mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
    }

    private double resolveChaseSpeed(LivingEntity target, boolean sprintJumpChase) {
        if (sprintJumpChase) {
            return DIRECT_SPRINT_SPEED;
        }

        return target.isSprinting() ? DIRECT_RUN_SPEED : DIRECT_WALK_SPEED;
    }

    private boolean isOnNarrowFooting(ChangedEntity mob) {
        Vec3 center = mob.position();
        int supportedSamples = 0;
        double[] offsets = {-NARROW_FOOTING_SAMPLE, NARROW_FOOTING_SAMPLE};

        for (double xOffset : offsets) {
            for (double zOffset : offsets) {
                if (hasSupportBelow(mob, center.x + xOffset, center.y, center.z + zOffset)) {
                    supportedSamples++;
                }
            }
        }

        return supportedSamples <= 2;
    }

    private boolean hasSupportBelow(ChangedEntity mob, double x, double y, double z) {
        BlockPos below = BlockPos.containing(x, y - 0.2D, z);
        BlockState state = mob.level().getBlockState(below);
        return !state.isAir() && !state.getCollisionShape(mob.level(), below).isEmpty();
    }

    private boolean shouldUseBridgeStraightChase(ChangedEntity mob, LivingEntity target) {
        if (!mob.onGround() || mob.horizontalCollision) {
            return false;
        }

        Vec3 forward = horizontalForwardToTarget(mob, target);
        Vec3 side = new Vec3(-forward.z, 0.0D, forward.x);
        return isSideDropAirColumn(mob, side) && isSideDropAirColumn(mob, side.scale(-1.0D));
    }

    private boolean isSideDropAirColumn(ChangedEntity mob, Vec3 sideOffset) {
        Vec3 sample = mob.position().add(sideOffset.normalize());
        for (int depth = 1; depth <= 3; depth++) {
            BlockPos pos = BlockPos.containing(sample.x, mob.getY() - depth, sample.z);
            if (!mob.level().getBlockState(pos).isAir()) {
                return false;
            }
        }
        return true;
    }

    private Vec3 horizontalForwardToTarget(ChangedEntity mob, LivingEntity target) {
        Vec3 forward = target.position().subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
        if (forward.lengthSqr() > 1.0E-6D) {
            return forward.normalize();
        }

        float yawRadians = mob.getYRot() * ((float)Math.PI / 180.0F);
        return new Vec3(-Mth.sin(yawRadians), 0.0D, Mth.cos(yawRadians));
    }

    private void faceTarget(ChangedEntity mob, LivingEntity target, float maxTurnStep) {
        Vec3 horizontalDelta = target.position().subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
        if (horizontalDelta.lengthSqr() < DIRECT_FACE_MIN_DISTANCE_SQR) {
            mob.getLookControl().setLookAt(target, maxTurnStep, maxTurnStep);
            return;
        }

        float desiredYaw = (float)(Mth.atan2(horizontalDelta.z, horizontalDelta.x) * (180.0F / Math.PI)) - 90.0F;
        float smoothedYaw = rotlerp(mob.getYRot(), desiredYaw, maxTurnStep);
        mob.setYRot(smoothedYaw);
        mob.setYBodyRot(rotlerp(mob.yBodyRot, smoothedYaw, maxTurnStep));
        mob.yHeadRot = rotlerp(mob.yHeadRot, smoothedYaw, maxTurnStep + 2.0F);
        mob.yHeadRotO = mob.yHeadRot;
        mob.getLookControl().setLookAt(target, maxTurnStep, maxTurnStep);
    }

    private float rotlerp(float current, float target, float maxStep) {
        float delta = Mth.wrapDegrees(target - current);
        if (delta > maxStep) delta = maxStep;
        if (delta < -maxStep) delta = -maxStep;
        return current + delta;
    }

    private boolean shouldBuild(ChangedEntity mob, LatexMind mind, LivingEntity target) {
        if (mind.buildCooldown > 0) return false;
        int blockCount = countUsableBuildingBlocks(mob);
        if (blockCount <= 0) return false;
        BlockPos buildPos = findBuildPlacement(mob, target);
        if (buildPos == null) return false;
        int requiredBlocks = estimateBuildBlocksNeeded(mob, target, buildPos);
        return blockCount - requiredBlocks >= BUILD_BLOCK_RESERVE || requiredBlocks <= 1;
    }

    private boolean shouldTowerUp(ChangedEntity mob, LivingEntity target) {
        double verticalGap = target.getY() - mob.getY();
        double horizontalDistSqr = mob.position().subtract(target.position()).multiply(1.0D, 0.0D, 1.0D).lengthSqr();
        return verticalGap > 1.15D && horizontalDistSqr <= 6.25D;
    }

    private boolean isTowerPlacement(ChangedEntity mob, LivingEntity target, BlockPos placePos) {
        return shouldTowerUp(mob, target) && placePos.equals(mob.blockPosition());
    }

    private void pickUpItemToHands(ChangedEntity mob, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) return;

        if (tryEquipArmorOrOffhand(mob, stack, itemEntity)) {
            return;
        }

        if (tryMergeIntoHand(mob, InteractionHand.MAIN_HAND, stack)) {
            if (stack.isEmpty()) itemEntity.discard();
            else itemEntity.setItem(stack);
            return;
        }

        if (tryMergeIntoHand(mob, InteractionHand.OFF_HAND, stack)) {
            if (stack.isEmpty()) itemEntity.discard();
            else itemEntity.setItem(stack);
            return;
        }
    }

    private boolean tryEquipArmorOrOffhand(ChangedEntity mob, ItemStack stack, ItemEntity itemEntity) {
        if (stack.getItem() instanceof ArmorItem armor) {
            EquipmentSlot slot = armor.getEquipmentSlot();
            ItemStack equipped = mob.getItemBySlot(slot);
            if (equipped.isEmpty() || armorValue(stack) > armorValue(equipped)) {
                mob.setItemSlot(slot, stack.copyWithCount(1));
                stack.shrink(1);
                if (stack.isEmpty()) itemEntity.discard();
                else itemEntity.setItem(stack);
                return true;
            }
        }

        if (stack.getItem() instanceof ShieldItem && mob.getOffhandItem().isEmpty()) {
            mob.setItemSlot(EquipmentSlot.OFFHAND, stack.copyWithCount(1));
            stack.shrink(1);
            if (stack.isEmpty()) itemEntity.discard();
            else itemEntity.setItem(stack);
            return true;
        }

        return false;
    }

    private boolean tryMergeIntoHand(ChangedEntity mob, InteractionHand hand, ItemStack stack) {
        ItemStack held = mob.getItemInHand(hand);
        if (held.isEmpty()) {
            mob.setItemInHand(hand, stack.copy());
            stack.setCount(0);
            return true;
        }

        if (ItemStack.isSameItemSameTags(held, stack) && held.getCount() < held.getMaxStackSize()) {
            int moved = Math.min(stack.getCount(), held.getMaxStackSize() - held.getCount());
            held.grow(moved);
            stack.shrink(moved);
            return moved > 0;
        }

        if (itemUtilityScore(stack) > itemUtilityScore(held)) {
            mob.setItemInHand(hand, stack.copy());
            stack.setCount(0);
            return true;
        }

        return false;
    }

    private boolean tryWaterClutch(ChangedEntity mob, LatexMind mind) {
        if (mind.clutchCooldown > 0) return false;
        if (mob.onGround() || mob.isInWater() || mob.isInLava()) return false;
        if (mob.fallDistance < 6.0F || mob.getDeltaMovement().y > -0.9D) return false;

        WaterBucketSource waterBucket = findWaterBucket(mob);
        if (waterBucket == null || waterBucket.stack().isEmpty()) return false;

        BlockPos landingPos = findWaterClutchPos(mob);
        if (landingPos == null) return false;

        mob.getNavigation().stop();
        equipWaterBucketForClutch(mob, waterBucket);
        mob.getLookControl().setLookAt(landingPos.getX() + 0.5D, landingPos.getY() + 0.5D, landingPos.getZ() + 0.5D);
        mob.swing(InteractionHand.MAIN_HAND);

        if (mob.level().setBlock(landingPos, Blocks.WATER.defaultBlockState(), 3)) {
            consumeWaterBucketSource(mob, waterBucket);
            mob.fallDistance = 0.0F;
            mind.clutchCooldown = 40;
            return true;
        }

        return false;
    }

    private void smartJump(ChangedEntity mob, LivingEntity target) {
        LatexMind mind = LatexMindStore.get(mob);
        if (mind.jumpCooldown > 0 || !mob.onGround() || isOnNarrowFooting(mob) || shouldUseBridgeStraightChase(mob, target)) return;

        if (target instanceof Player && canSprintJumpChase(mob, target)) {
            Vec3 flat = target.position().subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
            if (flat.lengthSqr() > 0.001D) {
                Vec3 forward = flat.normalize().scale(PLAYER_SPRINT_JUMP_BOOST);
                triggerJump(mob, mind, forward, 10);
                return;
            }
        }

        boolean needsJump = mob.horizontalCollision
                || (target.getY() - mob.getY() > 1.1D && mob.distanceTo(target) < 3.25D);

        if (needsJump) {
            triggerJump(mob, mind, null, 16);
        }
    }

    private void triggerJump(ChangedEntity mob, LatexMind mind, @Nullable Vec3 horizontalBoost, int cooldownTicks) {
        if (mind.jumpCooldown > 0 || !mob.onGround()) {
            return;
        }

        Vec3 current = mob.getDeltaMovement();
        Vec3 boost = horizontalBoost == null ? Vec3.ZERO : horizontalBoost;
        Vec3 horizontal = new Vec3(current.x + boost.x, 0.0D, current.z + boost.z);
        if (horizontalBoost != null && horizontal.lengthSqr() > PLAYER_SPRINT_JUMP_SPEED_CAP * PLAYER_SPRINT_JUMP_SPEED_CAP) {
            horizontal = horizontal.normalize().scale(PLAYER_SPRINT_JUMP_SPEED_CAP);
        }
        mob.setJumping(true);
        mob.getJumpControl().jump();
        mob.setDeltaMovement(horizontal.x, Math.max(current.y, 0.42D), horizontal.z);
        mob.hasImpulse = true;
        mob.hurtMarked = true;
        mind.jumpCooldown = cooldownTicks;
    }

    private boolean canSprintJumpChase(ChangedEntity mob, LivingEntity target) {
        double horizontalDistanceSqr = target.position().subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D).lengthSqr();
        return Math.abs(target.getY() - mob.getY()) <= 2.0D
                && horizontalDistanceSqr > 16.0D
                && horizontalDistanceSqr < 196.0D
                && !mob.horizontalCollision;
    }

    @Nullable
    private LivingEntity resolveTarget(ChangedEntity mob, LatexMind mind) {
        LivingEntity currentTarget = mob.getTarget();
        if (currentTarget != null && isValidAggroTarget(mob, currentTarget, mind)) {
            if (mind.targetId == null || !mind.targetId.equals(currentTarget.getUUID())) {
                mind.targetId = currentTarget.getUUID();
            }
            return currentTarget;
        }
        if (currentTarget != null) {
            mob.setTarget(null);
        }

        LivingEntity remembered = findRememberedTarget(mob, mind);
        if (remembered != null) {
            mob.setTarget(remembered);
            return remembered;
        }

        LivingEntity visibleTarget = findVisibleTarget(mob, mind);
        if (visibleTarget != null) {
            mob.setTarget(visibleTarget);
            mind.targetId = visibleTarget.getUUID();
            return visibleTarget;
        }

        return null;
    }

    @Nullable
    private Path createReachPath(ChangedEntity mob, LivingEntity target) {
        Path path = mob.getNavigation().createPath(target, 0);
        if (path != null && path.canReach()) {
            return path;
        }
        return createNearbyReachPath(mob, target);
    }

    @Nullable
    private Path createNearbyReachPath(ChangedEntity mob, LivingEntity target) {
        BlockPos targetPos = target.blockPosition();
        Path bestPath = null;
        Vec3 directLine = target.position().subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 directNormal = directLine.lengthSqr() > 1.0E-6D ? directLine.normalize() : Vec3.ZERO;
        double bestScore = Double.POSITIVE_INFINITY;

        for (int dy = -1; dy <= 1; dy++) {
            for (int radius = 0; radius <= ALT_PATH_SEARCH_RADIUS; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) {
                            continue;
                        }

                        BlockPos candidate = targetPos.offset(dx, dy, dz);
                        if (!isStandableTargetPosition(mob, candidate)) {
                            continue;
                        }

                        Path candidatePath = mob.getNavigation().createPath(candidate, 0);
                        if (candidatePath == null || !candidatePath.canReach()) {
                            continue;
                        }

                        Vec3 candidateOffset = Vec3.atBottomCenterOf(candidate).subtract(target.position()).multiply(1.0D, 0.0D, 1.0D);
                        double distance = candidate.distSqr(targetPos);
                        double lateralOffset = directNormal.equals(Vec3.ZERO)
                                ? 0.0D
                                : candidateOffset.subtract(directNormal.scale(candidateOffset.dot(directNormal))).length();
                        double score = distance
                                + lateralOffset * STRAIGHT_ROUTE_ALIGNMENT_WEIGHT
                                + candidatePath.getNodeCount() * STRAIGHT_ROUTE_PATH_NODE_WEIGHT;
                        if (score < bestScore) {
                            bestScore = score;
                            bestPath = candidatePath;
                        }
                    }
                }
            }
        }

        return bestPath;
    }

    private boolean isStandableTargetPosition(ChangedEntity mob, BlockPos pos) {
        BlockState feet = mob.level().getBlockState(pos);
        BlockState head = mob.level().getBlockState(pos.above());
        if ((!feet.isAir() && !feet.canBeReplaced()) || (!head.isAir() && !head.canBeReplaced())) {
            return false;
        }

        BlockPos below = pos.below();
        BlockState support = mob.level().getBlockState(below);
        return !support.isAir() && !support.getCollisionShape(mob.level(), below).isEmpty();
    }

    private boolean hasReachablePath(ChangedEntity mob, LatexMind mind, LivingEntity target) {
        BlockPos targetPos = target.blockPosition();
        BlockPos sourcePos = mob.blockPosition();
        if (mind.cachedPathTargetPos != null
                && mind.cachedPathSourcePos != null
                && mob.tickCount - mind.cachedPathTick <= PATH_CACHE_TICKS
                && mind.cachedPathTargetPos.closerThan(targetPos, 2.0D)
                && mind.cachedPathSourcePos.closerThan(sourcePos, 2.0D)) {
            return mind.cachedReachablePath;
        }

        mind.cachedPathTick = mob.tickCount;
        mind.cachedPathTargetPos = targetPos.immutable();
        mind.cachedPathSourcePos = sourcePos.immutable();
        mind.cachedReachablePath = createReachPath(mob, target) != null;
        return mind.cachedReachablePath;
    }

    @Nullable
    private State chooseTerrainAction(ChangedEntity mob, LatexMind mind, LivingEntity target) {
        BlockPos targetPos = target.blockPosition();
        BlockPos sourcePos = mob.blockPosition();
        if (mind.cachedTerrainState != null
                && mind.cachedTerrainTargetPos != null
                && mind.cachedTerrainSourcePos != null
                && mob.tickCount - mind.cachedTerrainTick <= TERRAIN_CACHE_TICKS
                && mind.cachedTerrainTargetPos.closerThan(targetPos, 2.0D)
                && mind.cachedTerrainSourcePos.closerThan(sourcePos, 2.0D)) {
            mind.plannedBreakPos = mind.cachedTerrainBreakPos;
            mind.plannedBuildPos = mind.cachedTerrainBuildPos;
            mind.imaginedBuildPath = new ArrayList<>(mind.cachedImaginedBuildPath);
            return mind.cachedTerrainState;
        }

        TerrainPlan plan = analyzeTerrainPlan(mob, mind, target);
        BlockPos breakPos = plan.breakPos();
        BlockPos buildPos = plan.buildPos();
        List<BlockPos> imaginedBuildPath = plan.imaginedBuildPath();
        double breakCost = plan.breakCost();
        double buildCost = plan.buildCost();

        mind.plannedBreakPos = Double.isFinite(breakCost) ? breakPos : null;
        mind.plannedBuildPos = Double.isFinite(buildCost) ? buildPos : null;
        mind.imaginedBuildPath = new ArrayList<>(imaginedBuildPath);

        if (!Double.isFinite(breakCost) && !Double.isFinite(buildCost)) {
            cacheTerrainPlan(mob, mind, targetPos, sourcePos, null, null, List.of(), null);
            return null;
        }

        State chosen;
        if (Double.isFinite(buildCost) && !Double.isFinite(breakCost)) {
            chosen = State.BUILD;
        } else if (Double.isFinite(buildCost) && mind.recentBreakTicks > 0) {
            chosen = State.BUILD;
        } else if (Double.isFinite(buildCost) && buildCost <= breakCost + BREAK_PREFERENCE_PENALTY) {
            chosen = State.BUILD;
        } else if (!Double.isFinite(buildCost) && Double.isFinite(breakCost)) {
            chosen = State.BREAK;
        } else {
            chosen = buildCost < breakCost ? State.BUILD : State.BREAK;
        }
        cacheTerrainPlan(mob, mind, targetPos, sourcePos, mind.plannedBreakPos, mind.plannedBuildPos, mind.imaginedBuildPath, chosen);
        return chosen;
    }

    private void cacheTerrainPlan(ChangedEntity mob, LatexMind mind, BlockPos targetPos, BlockPos sourcePos,
                                  @Nullable BlockPos breakPos, @Nullable BlockPos buildPos, List<BlockPos> imaginedBuildPath,
                                  @Nullable State chosen) {
        mind.cachedTerrainTick = mob.tickCount;
        mind.cachedTerrainTargetPos = targetPos.immutable();
        mind.cachedTerrainSourcePos = sourcePos.immutable();
        mind.cachedTerrainBreakPos = breakPos != null ? breakPos.immutable() : null;
        mind.cachedTerrainBuildPos = buildPos != null ? buildPos.immutable() : null;
        mind.cachedImaginedBuildPath = new ArrayList<>(imaginedBuildPath);
        mind.cachedTerrainState = chosen;
    }

    private TerrainPlan analyzeTerrainPlan(ChangedEntity mob, LatexMind mind, LivingEntity target) {
        BlockPos immediateBreak = findPlannedBreakTarget(mob, target);
        List<BlockPos> imaginedBuildPath = shouldBuild(mob, mind, target)
                ? buildImaginaryBuildPath(mob, target, defaultBuildState(mob))
                : List.of();
        BlockPos immediateBuild = !imaginedBuildPath.isEmpty()
                ? imaginedBuildPath.get(0)
                : (shouldBuild(mob, mind, target) ? findImmediateBuildPlacement(mob, target) : null);

        double breakCost = estimateBreakCost(mob, immediateBreak);
        double buildCost = estimateBuildCost(mob, target, immediateBuild, imaginedBuildPath);

        if (Double.isFinite(breakCost) || Double.isFinite(buildCost)) {
            return new TerrainPlan(immediateBreak, immediateBuild, imaginedBuildPath, breakCost, buildCost);
        }

        BlockPos fallbackBreak = findRaycastBreakTarget(mob, target);
        BlockPos fallbackBuild = !imaginedBuildPath.isEmpty()
                ? imaginedBuildPath.get(0)
                : (shouldBuild(mob, mind, target) ? findFallbackBuildPlacement(mob, target) : null);
        return new TerrainPlan(
                fallbackBreak,
                fallbackBuild,
                imaginedBuildPath,
                estimateBreakCost(mob, fallbackBreak),
                estimateBuildCost(mob, target, fallbackBuild, imaginedBuildPath)
        );
    }

    @Nullable
    private BlockPos findBreakTarget(ChangedEntity mob, LivingEntity target) {
        BlockPos immediate = findPlannedBreakTarget(mob, target);
        if (immediate != null) {
            return immediate;
        }
        return findRaycastBreakTarget(mob, target);
    }

    @Nullable
    private BlockPos findPlannedBreakTarget(ChangedEntity mob, LivingEntity target) {
        BlockPos corridorBlock = findBreakBlockAlongImaginaryPath(mob, target);
        if (corridorBlock != null) {
            return corridorBlock;
        }

        return findImmediateBreakTarget(mob, target);
    }

    @Nullable
    private BlockPos findImmediateBreakTarget(ChangedEntity mob, LivingEntity target) {
        Direction direction = horizontalDirectionToward(mob, target);
        BlockPos origin = mob.blockPosition();
        BlockPos[] candidates = new BlockPos[] {
                origin.relative(direction),
                origin.relative(direction).above(),
                origin.relative(direction).above(2),
                origin.relative(direction, 2),
                origin.relative(direction, 2).above()
        };

        for (BlockPos candidate : candidates) {
            if (isBreakCandidate(mob, candidate)) {
                return candidate;
            }
        }

        return null;
    }

    @Nullable
    private BlockPos findBreakBlockAlongImaginaryPath(ChangedEntity mob, LivingEntity target) {
        Vec3 start = Vec3.atBottomCenterOf(mob.blockPosition());
        Vec3 end = Vec3.atBottomCenterOf(target.blockPosition());
        Vec3 delta = end.subtract(start);
        double horizontalLength = delta.multiply(1.0D, 0.0D, 1.0D).length();
        if (horizontalLength < 0.5D) {
            return null;
        }

        int samples = Math.min((int) Math.ceil(horizontalLength / BREAK_PATH_STEP), BREAK_PATH_SCAN_BLOCKS * 2);
        BlockPos best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (int i = 1; i <= samples; i++) {
            double t = (double) i / (double) samples;
            double x = Mth.lerp(t, start.x, end.x);
            double y = Mth.lerp(t, start.y, end.y);
            double z = Mth.lerp(t, start.z, end.z);
            BlockPos base = BlockPos.containing(x, y, z);

            BlockPos[] corridor = new BlockPos[] {
                    base,
                    base.above(),
                    base.above(2),
                    base.below()
            };

            for (BlockPos candidate : corridor) {
                if (!isPathMiningCandidate(mob, candidate, base)) {
                    continue;
                }

                double dist = mob.distanceToSqr(Vec3.atCenterOf(candidate));
                if (dist < bestDist) {
                    best = candidate;
                    bestDist = dist;
                }
            }

            if (best != null && bestDist <= 12.25D) {
                return best;
            }
        }

        return best;
    }

    private boolean isPathMiningCandidate(ChangedEntity mob, BlockPos candidate, BlockPos corridorBase) {
        BlockState state = mob.level().getBlockState(candidate);
        if (state.isAir() || state.getDestroySpeed(mob.level(), candidate) < 0.0F) {
            return false;
        }
        if (mob.distanceToSqr(Vec3.atCenterOf(candidate)) > 25.0D) {
            return false;
        }

        boolean blocksFeetSpace = candidate.equals(corridorBase) || candidate.equals(corridorBase.above());
        boolean blocksHeadSpace = candidate.equals(corridorBase.above(2));

        return (blocksFeetSpace || blocksHeadSpace) && canBreakBlock(mob, candidate, state);
    }

    private boolean isBreakCandidate(ChangedEntity mob, BlockPos pos) {
        BlockState state = mob.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(mob.level(), pos) < 0.0F) {
            return false;
        }
        if (mob.distanceToSqr(Vec3.atCenterOf(pos)) > 12.25D) {
            return false;
        }
        return canBreakBlock(mob, pos, state);
    }

    private boolean hasNearbyFistMineableDropBlock(ChangedEntity mob, LivingEntity target) {
        BlockPos origin = mob.blockPosition();
        Direction direction = horizontalDirectionToward(mob, target);
        BlockPos[] priorityCandidates = new BlockPos[] {
                origin.relative(direction),
                origin.relative(direction).above(),
                origin.relative(direction).above(2),
                origin.relative(direction, 2),
                origin.relative(direction, 2).above(),
                origin.above(),
                origin.above(2)
        };

        for (BlockPos candidate : priorityCandidates) {
            if (isFistMineableDropCandidate(mob, candidate)) {
                return true;
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (isFistMineableDropCandidate(mob, origin.offset(dx, dy, dz))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isFistMineableDropCandidate(ChangedEntity mob, BlockPos pos) {
        if (mob.distanceToSqr(Vec3.atCenterOf(pos)) > 12.25D) {
            return false;
        }

        BlockState state = mob.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(mob.level(), pos) < 0.0F || state.requiresCorrectToolForDrops()) {
            return false;
        }

        float hardness = state.getDestroySpeed(mob.level(), pos);
        if (hardness > 1.5F) {
            return false;
        }

        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        return !Block.getDrops(state, serverLevel, pos, mob.level().getBlockEntity(pos), mob, ItemStack.EMPTY).isEmpty();
    }

    @Nullable
    private BlockPos findRaycastBreakTarget(ChangedEntity mob, LivingEntity target) {
        BlockHitResult hit = mob.level().clip(new ClipContext(
                mob.getEyePosition(),
                target.getEyePosition(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mob
        ));

        if (hit.getType() != HitResult.Type.BLOCK) return null;

        BlockPos pos = hit.getBlockPos();
        BlockState state = mob.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(mob.level(), pos) < 0) {
            return null;
        }

        if (Block.isShapeFullBlock(state.getCollisionShape(mob.level(), pos))) {
            return pos;
        }

        return mob.distanceToSqr(Vec3.atCenterOf(pos)) <= 9.0D && canBreakBlock(mob, pos, state) ? pos : null;
    }

    @Nullable
    private BlockPos findBuildPlacement(ChangedEntity mob, LivingEntity target) {
        BlockPos immediate = findImmediateBuildPlacement(mob, target);
        if (immediate != null) {
            return immediate;
        }
        return findFallbackBuildPlacement(mob, target);
    }

    @Nullable
    private BlockPos findImmediateBuildPlacement(ChangedEntity mob, LivingEntity target) {
        BlockState buildState = defaultBuildState(mob);
        if (buildState == null) {
            return null;
        }

        if (shouldTowerUp(mob, target) && canPlaceTowerBlockAt(mob, mob.blockPosition(), buildState)) {
            return mob.blockPosition();
        }

        Direction direction = horizontalDirectionToward(mob, target);
        BlockPos origin = mob.blockPosition();
        BlockPos front = origin.relative(direction);
        BlockPos frontBelow = front.below();
        BlockPos secondFrontBelow = origin.relative(direction, 2).below();

        if (target.getY() > mob.getY() + 0.8D && canPlaceBlockAt(mob, front, buildState)) {
            return front;
        }

        if (shouldPlaceBridgeBlock(mob, target, frontBelow, buildState)) {
            return frontBelow;
        }

        if (shouldPlaceBridgeBlock(mob, target, secondFrontBelow, buildState)) {
            return secondFrontBelow;
        }

        return null;
    }

    @Nullable
    private BlockPos findFallbackBuildPlacement(ChangedEntity mob, LivingEntity target) {
        Direction direction = horizontalDirectionToward(mob, target);
        BlockPos front = mob.blockPosition().relative(direction);
        BlockPos bridge = front.below();

        if (target.getY() > mob.getY() + 1.2D) {
            BlockPos step = front;
            if (canPlaceBlockAt(mob, step, defaultBuildState(mob))) {
                return step;
            }
        }

        BlockState buildState = defaultBuildState(mob);
        if (shouldPlaceBridgeBlock(mob, target, bridge, buildState)) {
            return bridge;
        }

        Path path = mob.getNavigation().createPath(target, 0);
        if (path == null && target.getY() > mob.getY() + 1.2D) {
            BlockPos scaffold = mob.blockPosition().relative(direction);
            if (canPlaceBlockAt(mob, scaffold, buildState)) {
                return scaffold;
            }
        }

        return null;
    }

    private Direction horizontalDirectionToward(ChangedEntity mob, LivingEntity target) {
        Vec3 delta = target.position().subtract(mob.position());
        if (Math.abs(delta.x) > Math.abs(delta.z)) {
            return delta.x >= 0.0D ? Direction.EAST : Direction.WEST;
        }
        return delta.z >= 0.0D ? Direction.SOUTH : Direction.NORTH;
    }

    private boolean moveNearPlacement(ChangedEntity mob, BlockPos placePos) {
        return mob.distanceToSqr(placePos.getX() + 0.5D, placePos.getY() + 0.5D, placePos.getZ() + 0.5D) <= 6.25D;
    }

    private void pruneImaginedBuildPath(ChangedEntity mob, LatexMind mind) {
        while (!mind.imaginedBuildPath.isEmpty()) {
            BlockPos next = mind.imaginedBuildPath.get(0);
            BlockState state = mob.level().getBlockState(next);
            if (state.isAir() || state.canBeReplaced()) {
                break;
            }
            mind.imaginedBuildPath.remove(0);
        }
        mind.plannedBuildPos = mind.imaginedBuildPath.isEmpty() ? null : mind.imaginedBuildPath.get(0);
    }

    private List<BlockPos> buildImaginaryBuildPath(ChangedEntity mob, LivingEntity target, @Nullable BlockState buildState) {
        if (buildState == null || isOnNarrowFooting(mob)) {
            return List.of();
        }

        Vec3 start = Vec3.atBottomCenterOf(mob.blockPosition());
        Vec3 end = Vec3.atBottomCenterOf(target.blockPosition());
        Vec3 delta = end.subtract(start);
        double horizontalLength = delta.multiply(1.0D, 0.0D, 1.0D).length();
        if (horizontalLength < 1.0D) {
            return List.of();
        }

        int samples = Math.min(Math.max(2, (int)Math.ceil(horizontalLength)), IMAGINARY_PATH_SCAN_BLOCKS);
        List<BlockPos> placements = new ArrayList<>();
        Set<BlockPos> virtualBlocks = new HashSet<>();
        int previousStandY = mob.blockPosition().getY();

        for (int i = 1; i <= samples; i++) {
            double progress = (double) i / (double) samples;
            double x = Mth.lerp(progress, start.x, end.x);
            double z = Mth.lerp(progress, start.z, end.z);
            int targetStandY = Mth.floor(Mth.lerp(progress, mob.getY(), target.getY()));
            int standY = Mth.clamp(targetStandY, previousStandY - 1, previousStandY + 1);
            BlockPos standPos = BlockPos.containing(x, standY, z);
            BlockPos headPos = standPos.above();
            BlockPos supportPos = standPos.below();

            if (!isImaginaryPassable(mob, standPos, virtualBlocks) || !isImaginaryPassable(mob, headPos, virtualBlocks)) {
                return List.of();
            }

            if (!hasImaginarySupport(mob, supportPos, virtualBlocks)) {
                if (placements.size() >= IMAGINARY_PATH_MAX_BLOCKS || !canPlaceImaginaryBlockAt(mob, supportPos, buildState, virtualBlocks)) {
                    return List.of();
                }
                BlockPos immutable = supportPos.immutable();
                placements.add(immutable);
                virtualBlocks.add(immutable);
            }

            previousStandY = standPos.getY();
        }

        return placements;
    }

    private boolean isImaginaryPassable(ChangedEntity mob, BlockPos pos, Set<BlockPos> virtualBlocks) {
        if (virtualBlocks.contains(pos)) {
            return false;
        }

        BlockState state = mob.level().getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    private boolean hasImaginarySupport(ChangedEntity mob, BlockPos pos, Set<BlockPos> virtualBlocks) {
        if (virtualBlocks.contains(pos)) {
            return true;
        }

        BlockState state = mob.level().getBlockState(pos);
        return !state.isAir() && !state.getCollisionShape(mob.level(), pos).isEmpty();
    }

    private boolean canPlaceImaginaryBlockAt(ChangedEntity mob, BlockPos pos, BlockState placeState, Set<BlockPos> virtualBlocks) {
        if (!mob.level().getBlockState(pos).canBeReplaced()) {
            return false;
        }
        if (!mob.level().getFluidState(pos).isEmpty()) {
            return false;
        }
        if (!mob.level().getWorldBorder().isWithinBounds(pos)) {
            return false;
        }
        if (!hasImaginarySupport(mob, pos.below(), virtualBlocks)) {
            return false;
        }
        return mob.level().isUnobstructed(placeState, pos, net.minecraft.world.phys.shapes.CollisionContext.empty());
    }

    private boolean shouldPlaceBridgeBlock(ChangedEntity mob, LivingEntity target, BlockPos placePos, @Nullable BlockState placeState) {
        if (!mob.onGround() || placeState == null || isOnNarrowFooting(mob)) {
            return false;
        }
        if (target.getY() > mob.getY() + 0.8D) {
            return false;
        }
        if (!mob.level().getBlockState(placePos).canBeReplaced()) {
            return false;
        }
        return canPlaceBlockAt(mob, placePos, placeState);
    }

    private boolean isCautiousPlacement(ChangedEntity mob, BlockPos placePos) {
        return isOnNarrowFooting(mob) || placePos.getY() < mob.blockPosition().getY();
    }

    private boolean canPlaceBlockAt(ChangedEntity mob, BlockPos pos, @Nullable BlockState placeState) {
        if (placeState == null) return false;
        if (!mob.level().getBlockState(pos).canBeReplaced()) return false;
        if (!mob.level().getFluidState(pos).isEmpty()) return false;
        if (!mob.level().getWorldBorder().isWithinBounds(pos)) return false;

        BlockPos below = pos.below();
        BlockState support = mob.level().getBlockState(below);
        if (support.isAir() || support.getCollisionShape(mob.level(), below).isEmpty()) {
            return false;
        }

        return mob.level().isUnobstructed(placeState, pos, net.minecraft.world.phys.shapes.CollisionContext.of(mob));
    }

    private boolean canPlaceTowerBlockAt(ChangedEntity mob, BlockPos pos, @Nullable BlockState placeState) {
        if (placeState == null) return false;
        if (!mob.level().getBlockState(pos).canBeReplaced()) return false;
        if (!mob.level().getFluidState(pos).isEmpty()) return false;
        if (!mob.level().getWorldBorder().isWithinBounds(pos)) return false;

        BlockPos below = pos.below();
        BlockState support = mob.level().getBlockState(below);
        if (support.isAir() || support.getCollisionShape(mob.level(), below).isEmpty()) {
            return false;
        }

        if (!mob.level().isUnobstructed(placeState, pos, net.minecraft.world.phys.shapes.CollisionContext.empty())) {
            return false;
        }

        return mob.level().getEntities(mob, new AABB(pos)).isEmpty();
    }

    @Nullable
    private BlockPos findWaterClutchPos(ChangedEntity mob) {
        int maxDepth = Math.max(4, Math.min(12, (int) Math.ceil(mob.fallDistance)));
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int depth = 1; depth <= maxDepth; depth++) {
            cursor.set(mob.getX(), mob.getY() - depth, mob.getZ());
            BlockState state = mob.level().getBlockState(cursor);
            if (state.isAir() || state.canBeReplaced()) {
                BlockState below = mob.level().getBlockState(cursor.below());
                if (!below.isAir() && !below.getCollisionShape(mob.level(), cursor.below()).isEmpty()) {
                    return cursor.immutable();
                }
                continue;
            }

            if (!state.getCollisionShape(mob.level(), cursor).isEmpty()) {
                BlockPos above = cursor.above();
                if (mob.level().getBlockState(above).canBeReplaced()) {
                    return above;
                }
                return null;
            }
        }

        return null;
    }

    @Nullable
    private BlockState defaultBuildState(ChangedEntity mob) {
        ItemStack stack = findBestBuildingBlock(mob);
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        return blockItem.getBlock().defaultBlockState();
    }

    private ItemStack findBestMiningTool(ChangedEntity mob, BlockState state) {
        ItemStack main = mob.getMainHandItem();
        ItemStack off = mob.getOffhandItem();
        float mainScore = miningScore(main, state);
        float offScore = miningScore(off, state);
        if (offScore <= 1.0F && mainScore <= 1.0F) {
            return ItemStack.EMPTY;
        }
        return offScore > mainScore ? off : main;
    }

    @Nullable
    private WaterBucketSource findWaterBucket(ChangedEntity mob) {
        if (mob.getMainHandItem().is(Items.WATER_BUCKET)) {
            return new WaterBucketSource(mob.getMainHandItem(), InteractionHand.MAIN_HAND, -1);
        }

        if (mob.getOffhandItem().is(Items.WATER_BUCKET)) {
            return new WaterBucketSource(mob.getOffhandItem(), InteractionHand.OFF_HAND, -1);
        }
        return null;
    }

    private void equipBestCombatTool(ChangedEntity mob) {
        ItemStack best = findBestCombatTool(mob);
        if (!best.isEmpty()) {
            mob.setItemInHand(InteractionHand.MAIN_HAND, best);
        }
        setBestShieldOffhand(mob);
    }

    private ItemStack findBestCombatTool(ChangedEntity mob) {
        ItemStack main = mob.getMainHandItem();
        ItemStack off = mob.getOffhandItem();
        return combatScore(off) > combatScore(main) ? off : main;
    }

    private double combatScore(ItemStack stack) {
        if (stack.isEmpty()) return 0.0D;

        double score = 1.0D;
        if (stack.getItem() instanceof SwordItem sword) {
            score += 8.0D + sword.getDamage();
        } else if (stack.getItem() instanceof TieredItem tiered) {
            score += 5.0D + tiered.getTier().getAttackDamageBonus() * 2.0D + tiered.getTier().getLevel();
        } else if (stack.getItem() instanceof TridentItem) {
            score += 9.0D;
        } else if (stack.getMaxDamage() > 0) {
            score += 2.5D;
        }

        score += EnchantmentHelper.getDamageBonus(stack, net.minecraft.world.entity.MobType.UNDEFINED);
        return score;
    }

    private double itemUtilityScore(ItemStack stack) {
        return Math.max(combatScore(stack), Math.max(buildScore(null, stack), stack.is(Items.WATER_BUCKET) ? 8.0D : 0.0D));
    }

    private float miningScore(ItemStack stack, BlockState state) {
        if (stack.isEmpty()) return 1.0F;
        return stack.getDestroySpeed(state) + toolMaterialScore(stack) * 0.1F;
    }

    private boolean canBreakBlock(ChangedEntity mob, BlockPos pos, BlockState state) {
        ItemStack tool = findBestMiningTool(mob, state);
        if (!tool.isEmpty() && tool.getDestroySpeed(state) > 1.0F) {
            return true;
        }

        if (!state.requiresCorrectToolForDrops()) {
            float hardness = state.getDestroySpeed(mob.level(), pos);
            return hardness >= 0.0F && hardness <= 1.5F;
        }

        return false;
    }

    private float toolMaterialScore(ItemStack stack) {
        if (stack.getItem() instanceof TieredItem tiered) {
            return tiered.getTier().getSpeed() + tiered.getTier().getLevel();
        }
        return 0.0F;
    }

    private double buildScore(@Nullable ChangedEntity mob, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return 0.0D;
        BlockState state = blockItem.getBlock().defaultBlockState();
        if (state.isAir() || state.getRenderShape() == RenderShape.INVISIBLE) return 0.0D;
        if (mob != null && !state.isCollisionShapeFullBlock(mob.level(), BlockPos.ZERO)) return 0.0D;
        return stack.getCount() + 1.0D;
    }

    private ItemStack findBestBuildingBlock(ChangedEntity mob) {
        ItemStack main = mob.getMainHandItem();
        ItemStack off = mob.getOffhandItem();
        double mainScore = buildScore(mob, main);
        double offScore = buildScore(mob, off);
        if (mainScore <= 0.0D && offScore <= 0.0D) {
            return ItemStack.EMPTY;
        }
        return offScore > mainScore ? off : main;
    }

    @Nullable
    private ItemEntity findBestBlockPickup(ChangedEntity mob) {
        ItemEntity best = null;
        double bestScore = 0.0D;

        for (ItemEntity item : mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(LOOT_RANGE))) {
            ItemStack stack = item.getItem();
            double score = buildScore(mob, stack) - mob.distanceToSqr(item) * 0.05D;
            if (score > bestScore) {
                bestScore = score;
                best = item;
            }
        }

        return best;
    }

    private int countUsableBuildingBlocks(ChangedEntity mob) {
        int count = 0;
        if (buildScore(mob, mob.getMainHandItem()) > 0.0D) count += mob.getMainHandItem().getCount();
        if (buildScore(mob, mob.getOffhandItem()) > 0.0D) count += mob.getOffhandItem().getCount();
        ItemEntity nearby = findBestBlockPickup(mob);
        if (nearby != null) count += nearby.getItem().getCount();
        return count;
    }

    private int estimateBuildBlocksNeeded(ChangedEntity mob, LivingEntity target, @Nullable BlockPos buildPos) {
        if (buildPos == null) return Integer.MAX_VALUE;
        if (buildPos.equals(mob.blockPosition()) && shouldTowerUp(mob, target)) {
            return Math.max(1, Math.min(8, target.blockPosition().getY() - mob.blockPosition().getY()));
        }
        int verticalGap = Math.max(0, target.blockPosition().getY() - mob.blockPosition().getY());
        if (buildPos.getY() >= mob.blockPosition().getY()) {
            return Math.max(1, Math.min(3, verticalGap));
        }
        return 1;
    }

    private double estimateBuildCost(ChangedEntity mob, LivingEntity target, @Nullable BlockPos buildPos, List<BlockPos> imaginedBuildPath) {
        if (buildPos == null) return Double.POSITIVE_INFINITY;
        int blocksAvailable = countUsableBuildingBlocks(mob);
        int blocksNeeded = imaginedBuildPath.isEmpty() ? estimateBuildBlocksNeeded(mob, target, buildPos) : imaginedBuildPath.size();
        if (blocksAvailable < blocksNeeded) return Double.POSITIVE_INFINITY;
        if (blocksNeeded > 1 && blocksAvailable - blocksNeeded < BUILD_BLOCK_RESERVE) return Double.POSITIVE_INFINITY;
        double verticalPenalty = buildPos.getY() >= mob.blockPosition().getY() ? 4.0D : 0.0D;
        return blocksNeeded * 10.0D + verticalPenalty + mob.distanceToSqr(Vec3.atCenterOf(buildPos)) * 0.25D;
    }

    private double estimateBreakCost(ChangedEntity mob, @Nullable BlockPos breakPos) {
        if (breakPos == null) return Double.POSITIVE_INFINITY;
        BlockState state = mob.level().getBlockState(breakPos);
        float hardness = state.getDestroySpeed(mob.level(), breakPos);
        if (hardness < 0.0F) return Double.POSITIVE_INFINITY;
        if (!canBreakBlock(mob, breakPos, state)) return Double.POSITIVE_INFINITY;
        ItemStack tool = findBestMiningTool(mob, state);
        float speed = tool.isEmpty() ? 1.0F : Math.max(1.0F, tool.getDestroySpeed(state));
        double directnessBonus = mob.blockPosition().closerThan(breakPos, 2.5D) ? -2.0D : 0.0D;
        return hardness * 16.0D / speed + mob.distanceToSqr(Vec3.atCenterOf(breakPos)) * 0.2D + directnessBonus;
    }

    private void consumeOneMatchingItem(ChangedEntity mob, ItemStack targetStack) {
        if (ItemStack.isSameItemSameTags(mob.getMainHandItem(), targetStack)) {
            mob.getMainHandItem().shrink(1);
            if (mob.getMainHandItem().isEmpty()) {
                mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            return;
        }

        if (ItemStack.isSameItemSameTags(mob.getOffhandItem(), targetStack)) {
            mob.getOffhandItem().shrink(1);
            if (mob.getOffhandItem().isEmpty()) {
                mob.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
            return;
        }
    }

    private void equipWaterBucketForClutch(ChangedEntity mob, WaterBucketSource source) {
        if (source.hand() == InteractionHand.OFF_HAND) {
            mob.setItemInHand(InteractionHand.MAIN_HAND, source.stack());
        }
    }

    private void consumeWaterBucketSource(ChangedEntity mob, WaterBucketSource source) {
        if (source.hand() == InteractionHand.OFF_HAND) {
            mob.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.BUCKET));
        }
        mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
    }

    private void setBestShieldOffhand(ChangedEntity mob) {
        ItemStack currentOffhand = mob.getOffhandItem();
        if (currentOffhand.getItem() instanceof ShieldItem) return;

        if (mob.getMainHandItem().getItem() instanceof ShieldItem) {
            ItemStack shield = mob.getMainHandItem().copy();
            mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            mob.setItemInHand(InteractionHand.OFF_HAND, shield);
        }
    }

    private ItemStack findBestShield(ChangedEntity mob) {
        if (mob.getMainHandItem().getItem() instanceof ShieldItem) return mob.getMainHandItem();
        if (mob.getOffhandItem().getItem() instanceof ShieldItem) return mob.getOffhandItem();
        return ItemStack.EMPTY;
    }

    private void raiseShieldIfPossible(ChangedEntity mob) {
        if (mob.getOffhandItem().getItem() instanceof ShieldItem) {
            mob.startUsingItem(InteractionHand.OFF_HAND);
        }
    }

    private void stopShieldingIfIdle(ChangedEntity mob) {
        if (mob.isUsingItem() && !(mob.getOffhandItem().getItem() instanceof ShieldItem && state == State.BREAK)) {
            mob.stopUsingItem();
        }
    }

    private boolean hasInterestingLootNearby(ChangedEntity mob) {
        return findBestItemEntity(mob) != null;
    }

    @Nullable
    private ItemEntity findBestItemEntity(ChangedEntity mob) {
        double bestScore = 0.0D;
        ItemEntity best = null;

        for (ItemEntity item : mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(LOOT_RANGE))) {
            if (!item.isAlive() || item.getItem().isEmpty()) continue;

            double score = scorePickup(mob, item.getItem()) - mob.distanceToSqr(item) * 0.05D;
            if (score > bestScore) {
                bestScore = score;
                best = item;
            }
        }

        return best;
    }

    private double scorePickup(ChangedEntity mob, ItemStack stack) {
        double score = stack.getCount() * 0.05D;

        if (stack.getItem() instanceof ArmorItem armor) {
            score += 8.0D + armor.getDefense() * 2.0D
                    + EnchantmentHelper.getTagEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION, stack);
            ItemStack current = mob.getItemBySlot(armor.getEquipmentSlot());
            if (current.isEmpty()) score += 6.0D;
            else score += Math.max(0.0D, armorValue(stack) - armorValue(current));
            return score;
        }

        if (stack.getItem() instanceof TieredItem tiered) {
            score += 6.0D + tiered.getTier().getLevel() * 3.0D + tiered.getTier().getSpeed() * 0.2D;
            return score;
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.isCollisionShapeFullBlock(mob.level(), BlockPos.ZERO)) {
                score += 4.0D + Math.min(64, stack.getCount()) * 0.2D;
            }
            return score;
        }

        if (stack.getItem() instanceof ShieldItem) {
            return score + 6.0D;
        }

        if (stack.is(Items.WATER_BUCKET)) {
            return score + 8.0D;
        }

        score += stack.getMaxDamage() > 0 ? 1.5D : 0.0D;
        return score;
    }

    private void autoEquipFromGround(ChangedEntity mob, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (!(stack.getItem() instanceof ArmorItem armor)) return;

        EquipmentSlot slot = armor.getEquipmentSlot();
        ItemStack equipped = mob.getItemBySlot(slot);
        if (equipped.isEmpty() || armorValue(stack) > armorValue(equipped)) {
            mob.setItemSlot(slot, stack.copyWithCount(1));
            stack.shrink(1);
            if (stack.isEmpty()) itemEntity.discard();
            else itemEntity.setItem(stack);
        }
    }

    private void equipBestArmor(ChangedEntity mob, LatexMind mind) {
        if (mind.equipmentScanCooldown > 0) return;
        mind.equipmentScanCooldown = 40;
    }

    private double armorValue(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armor)) return 0.0D;
        return armor.getDefense() * 2.0D
                + armor.getToughness()
                + EnchantmentHelper.getTagEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION, stack) * 1.5D;
    }

    @Nullable
    private LivingEntity findVisibleTarget(ChangedEntity mob, LatexMind mind) {
        double followRange = mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE);
        double range = Math.min(Math.max(12.0D, followRange), MAX_VISIBLE_TARGET_RANGE);
        LivingEntity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;

        for (LivingEntity candidate : mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(range))) {
            if (!isValidVisibleTarget(mob, candidate, mind) || !mob.hasLineOfSight(candidate)) {
                continue;
            }

            double distance = mob.distanceToSqr(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestTarget = candidate;
            }
        }

        return bestTarget;
    }

    @Nullable
    private LivingEntity findRememberedTarget(ChangedEntity mob, LatexMind mind) {
        if (mind.targetId == null) {
            return null;
        }

        double followRange = mob.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE);
        double range = Math.min(Math.max(16.0D, followRange + 6.0D), MAX_REMEMBERED_TARGET_RANGE);
        for (LivingEntity candidate : mob.level().getEntitiesOfClass(LivingEntity.class, mob.getBoundingBox().inflate(range))) {
            if (mind.targetId.equals(candidate.getUUID()) && isValidAggroTarget(mob, candidate, mind)) {
                return candidate;
            }
        }

        if (mob.level() instanceof ServerLevel server) {
            Player player = server.getPlayerByUUID(mind.targetId);
            if (player != null && isValidAggroTarget(mob, player, mind)) {
                return player;
            }
        }

        return null;
    }

    private boolean isValidAggroTarget(ChangedEntity mob, LivingEntity target, LatexMind mind) {
        if (target == mob || !target.isAlive() || target instanceof ChangedEntity) {
            return false;
        }
        if (mob.distanceToSqr(target) > HARD_TARGET_DROP_RANGE * HARD_TARGET_DROP_RANGE) {
            return false;
        }

        if (target instanceof Player player) {
            if (mob.getType().getCategory() != MobCategory.MONSTER) {
                return false;
            }
            if (LatexCuddleHelper.isTamingOwner(mob, player)) {
                return false;
            }
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
            if (!LatexAiUtil.isPlayerTransfurred(player)) {
                return true;
            }
            return LatexAiUtil.areHostileLatexFactions(mob, player) || mind.isRetaliationTarget(mob, player);
        }

        return isHumanoidTransfurTarget(target);
    }

    private boolean isValidVisibleTarget(ChangedEntity mob, LivingEntity target, LatexMind mind) {
        return isValidAggroTarget(mob, target, mind);
    }

    private boolean isHumanoidTransfurTarget(LivingEntity target) {
        if (target.getType().is(CHANGED_LATEXES)) {
            return false;
        }

        return target.getType().is(CHANGED_HUMANOIDS)
                || target instanceof Zombie
                || target instanceof AbstractSkeleton
                || target instanceof Villager
                || target instanceof Pillager
                || target instanceof Evoker;
    }

    private boolean shouldDropTarget(ChangedEntity mob, LatexMind mind) {
        LivingEntity currentTarget = mob.getTarget();
        if (currentTarget != null) {
            double distanceSqr = mob.distanceToSqr(currentTarget);
            if (distanceSqr > HARD_TARGET_DROP_RANGE * HARD_TARGET_DROP_RANGE) {
                return true;
            }
            if (!mob.hasLineOfSight(currentTarget) && distanceSqr > MAX_REMEMBERED_TARGET_RANGE * MAX_REMEMBERED_TARGET_RANGE) {
                return true;
            }
        }
        if (currentTarget instanceof Player playerTarget && (!playerTarget.isAlive() || playerTarget.isCreative() || playerTarget.isSpectator())) {
            return true;
        }

        if (mind.targetId != null && mob.level() instanceof ServerLevel server) {
            Player rememberedPlayer = server.getPlayerByUUID(mind.targetId);
            return rememberedPlayer != null && (!rememberedPlayer.isAlive() || rememberedPlayer.isCreative() || rememberedPlayer.isSpectator());
        }

        return false;
    }

    private void updateStuck(ChangedEntity mob, LatexMind mind) {
        if (mob.horizontalCollision && mob.onGround()) {
            mind.stuckTicks++;
        } else {
            mind.stuckTicks = Math.max(0, mind.stuckTicks - 1);
        }

        if (mob.getNavigation().isDone() && mind.hasLOS && mind.remembersRecentTarget(mob) && mind.stuckTicks < 3) {
            mind.pathFailed = false;
            mind.noPathTicks = 0;
        } else if (mob.getNavigation().isDone() && mind.remembersRecentTarget(mob)) {
            mind.noPathTicks++;
            mind.pathFailed = mind.noPathTicks > 12;
        } else {
            mind.noPathTicks = 0;
            mind.pathFailed = false;
        }
    }
}
