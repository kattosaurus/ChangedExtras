package com.katt.changedextras.entity.beasts;

import com.katt.changedextras.ChangedExtras;
import net.foxyas.changedaddon.entity.api.ICustomPatReaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ArtistEntity extends AbstractWhiteCatEntity implements ICustomPatReaction {
    public static final int ATTACK_POSE_NONE = 0;
    public static final int ATTACK_POSE_BRUSH = 1;
    public static final int ATTACK_POSE_DASH = 2;
    public static final int ATTACK_POSE_COMBO = 3;
    public static final int ATTACK_POSE_TELEPORT = 4;
    public static final int ATTACK_POSE_RELOAD = 5;

    private static final double MIN_DASH_RANGE_SQR = 9.0D;
    private static final double MAX_DASH_RANGE_SQR = 256.0D;
    private static final double PHASE_ONE_HEALTH = 620.0D;
    private static final double PHASE_ONE_SPEED = 0.32D;
    private static final double PHASE_TWO_SPEED = 0.46D;
    private static final double PHASE_ONE_DASH_SPEED = 1.45D;
    private static final double PHASE_TWO_DASH_SPEED = 1.85D;
    private static final double PHASE_TWO_HEALTH = 460.0D;
    private static final int PHASE_ONE_RELOAD_TICKS = 36;
    private static final int PHASE_TWO_RELOAD_TICKS = 34;

    // Dialogue
    private static final String[] PAT_LINES = {
            "entity.changedextras.artist.pat.1",
            "entity.changedextras.artist.pat.2",
            "entity.changedextras.artist.pat.3"
    };
    private static final String[] PHASE_TWO_LINES = {
            "entity.changedextras.artist.phase_two.1",
            "entity.changedextras.artist.phase_two.2",
            "entity.changedextras.artist.phase_two.3"
    };
    private static final String[] SPAWN_LINES = {
            "entity.changedextras.artist.spawn.1"
    };
    private static final String[] DEATH_LINES = {
            "entity.changedextras.artist.death.1"
    };
    private static final double SPEECH_RANGE = 32.0D;
    private static final int PAT_LINE_COOLDOWN_TICKS = 60;

    private static final EntityDataAccessor<Integer> ATTACK_POSE =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_POSE_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OPENING_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.changedextras.artist"),
            BossEvent.BossBarColor.PINK,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private int dashCooldown = 40;
    private int dashTicks = 0;
    private int comboCooldown = 30;
    private int teleportCooldown = 90;
    private int patLineCooldown = 0;
    private boolean secondPhaseTriggered = false;

    public ArtistEntity(EntityType<? extends ArtistEntity> type, Level level) {
        super(type, level);
        this.xpReward = 120;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ChangedExtras.ARTIST_BRUSH.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_POSE, ATTACK_POSE_NONE);
        this.entityData.define(ATTACK_POSE_TICKS, 0);
        this.entityData.define(OPENING_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractWhiteCatEntity.createAttributes();
    }

    public void applyBossStats() {
        if (this.getUnderlyingPlayer() != null) {
            return;
        }
        var maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(secondPhaseTriggered ? PHASE_TWO_HEALTH : PHASE_ONE_HEALTH);
            this.setHealth((float) (secondPhaseTriggered ? PHASE_TWO_HEALTH : PHASE_ONE_HEALTH));
        }
        var attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(secondPhaseTriggered ? 10.0D : 11.0D);
        }
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(secondPhaseTriggered ? PHASE_TWO_SPEED : PHASE_ONE_SPEED);
        }
        var followRange = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(48.0D);
        }
        var knockbackRes = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackRes != null) {
            knockbackRes.setBaseValue(0.4D);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag) {
        SpawnGroupData finalData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.applyBossStats();
        if (this.getUnderlyingPlayer() == null) {
            this.speak(SPAWN_LINES);
        }
        return finalData;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.35D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, target ->
                target instanceof Player player
                        && !player.isCreative()
                        && !player.isSpectator()));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (this.getOpening()) {
            return false;
        }
        this.triggerAttackPose(ATTACK_POSE_BRUSH, 8);
        boolean hit = super.doHurtTarget(target);
        if (hit && !this.level().isClientSide) {
            if (target instanceof Player) {
                float lifesteal = (float)Math.max(1.0D, this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.35D);
                this.heal(lifesteal);
            }
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            }
            for (int i = 0; i < 4; i++) {
                double offsetX = (this.random.nextDouble() - 0.5D) * 1.6D;
                double offsetZ = (this.random.nextDouble() - 0.5D) * 1.6D;
                spawnInkPool(target.getX() + offsetX, target.getY(), target.getZ() + offsetZ);
            }
        }
        return hit;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getUnderlyingPlayer() == null) {
            var maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null && maxHealth.getBaseValue() < PHASE_TWO_HEALTH) {
                this.applyBossStats();
            }
        }
        if (this.getMainHandItem().isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ChangedExtras.ARTIST_BRUSH.get()));
        }

        LivingEntity target = this.getTarget();
        boolean phaseTwo = this.isPhaseTwo();
        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        bossEvent.setName(this.getDisplayName());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(phaseTwo ? PHASE_TWO_SPEED : PHASE_ONE_SPEED);
        this.setNoGravity(phaseTwo);
        if (comboCooldown > 0) {
            comboCooldown--;
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        if (patLineCooldown > 0) {
            patLineCooldown--;
        }
        if (this.getOpeningTicks() > 0) {
            int remainingOpeningTicks = this.getOpeningTicks() - 1;
            this.entityData.set(OPENING_TICKS, remainingOpeningTicks);
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = true;
            if (remainingOpeningTicks <= 0 && this.getAttackPose() == ATTACK_POSE_RELOAD) {
                this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);
                dashCooldown = phaseTwo ? 6 : 12;
            }
        }
        if (this.getAttackPoseTicks() > 0) {
            int remainingTicks = this.getAttackPoseTicks() - 1;
            this.entityData.set(ATTACK_POSE_TICKS, remainingTicks);
            if (remainingTicks <= 0) {
                this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);
            }
        }

        if (target == null || !target.isAlive()) {
            if (phaseTwo) {
                this.hoverNearGround();
            } else if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, 1.0D, 0.7D));
            }
            if (dashCooldown > 0) {
                dashCooldown--;
            }
            return;
        }

        if (this.getOpeningTicks() > 0) {
            this.lookAt(target, 180.0F, 180.0F);
            return;
        }

        if (phaseTwo) {
            this.applySecondPhasePressure(target);
        }

        if (dashTicks > 0) {
            dashTicks--;
            if ((this.tickCount & (phaseTwo ? 0 : 1)) == 0) {
                spawnInkPool(this.getX(), this.getY(), this.getZ());
            }

            if (phaseTwo && this.distanceToSqr(target) < 9.0D && this.tickCount % 8 == 0) {
                this.doHurtTarget(target);
            }
            if (dashTicks <= 0) {
                this.beginPaintReload(phaseTwo ? PHASE_TWO_RELOAD_TICKS : PHASE_ONE_RELOAD_TICKS);
            }
            return;
        }

        if (phaseTwo && this.tryTeleportBehindTarget(target)) {
            return;
        }

        if (phaseTwo && this.tryPerformCombo(target)) {
            return;
        }

        if (dashCooldown > 0) {
            dashCooldown--;
            return;
        }

        double distanceSqr = this.distanceToSqr(target);
        if (distanceSqr < MIN_DASH_RANGE_SQR || distanceSqr > MAX_DASH_RANGE_SQR || !this.hasLineOfSight(target)) {
            dashCooldown = phaseTwo ? 8 : 14;
            return;
        }

        Vec3 targetPos = phaseTwo ? target.position().add(0.0D, 1.2D, 0.0D) : target.position();
        Vec3 dash = targetPos.subtract(this.position()).normalize().scale(phaseTwo ? PHASE_TWO_DASH_SPEED : PHASE_ONE_DASH_SPEED);
        this.setDeltaMovement(dash.x, phaseTwo ? Math.max(-0.08D, Math.min(0.16D, dash.y)) : 0.22D, dash.z);
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.1F, 0.6F + this.random.nextFloat() * 0.25F);
        this.hasImpulse = true;
        this.triggerAttackPose(ATTACK_POSE_DASH, phaseTwo ? 12 : 9);
        dashTicks = phaseTwo ? 12 : 8;
        dashCooldown = phaseTwo ? 18 + this.random.nextInt(10) : 36 + this.random.nextInt(16);
    }

    // ------------------------------------------------------------------
    // Dialogue
    // ------------------------------------------------------------------

    /**
     * Sends a random line from the given translation keys to every player nearby,
     * formatted like a chat message: <Artist> line
     */
    private void speak(String[] translationKeys) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Component line = Component.translatable(translationKeys[this.random.nextInt(translationKeys.length)]);
        Component message = Component.literal("<")
                .append(this.getDisplayName())
                .append(Component.literal("> "))
                .append(line);
        for (ServerPlayer nearby : serverLevel.getEntitiesOfClass(
                ServerPlayer.class, this.getBoundingBox().inflate(SPEECH_RANGE))) {
            nearby.sendSystemMessage(message);
        }
    }

    /**
     * Called by Changed Addon's PatFeatureHandle when the Artist gets patted.
     * Only this method is overridden (not the Specific/Simple variants) so she speaks once per pat.
     */
    @Override
    public void whenPattedReaction(LivingEntity patter, InteractionHand hand) {
        if (this.level().isClientSide || this.getUnderlyingPlayer() != null) {
            return;
        }
        if (patLineCooldown > 0) {
            return;
        }
        patLineCooldown = PAT_LINE_COOLDOWN_TICKS;
        this.speak(PAT_LINES);
    }

    // ------------------------------------------------------------------

    private void spawnInkPool(double x, double y, double z) {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), x, y, z);
        cloud.setRadius(this.isPhaseTwo() ? 1.8F : 1.2F);
        cloud.setDuration(this.isPhaseTwo() ? 100 : 70);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.setFixedColor(0x101010 | this.random.nextInt(0xEFEFEF));
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, this.isPhaseTwo() ? 120 : 80, this.isPhaseTwo() ? 2 : 1));
        this.level().addFreshEntity(cloud);
    }

    private void beginPaintReload(int ticks) {
        this.entityData.set(OPENING_TICKS, ticks);
        this.entityData.set(ATTACK_POSE, ATTACK_POSE_RELOAD);
        this.entityData.set(ATTACK_POSE_TICKS, ticks);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
        this.playSound(SoundEvents.BUCKET_FILL, 0.8F, 0.75F + this.random.nextFloat() * 0.15F);
    }

    private boolean isPhaseTwo() {
        return secondPhaseTriggered;
    }

    private void beginSecondPhase(LivingEntity target) {
        secondPhaseTriggered = true;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(PHASE_TWO_HEALTH);
        this.setHealth((float)PHASE_TWO_HEALTH);
        dashCooldown = 35;
        dashTicks = 0;
        comboCooldown = 35;
        teleportCooldown = 45;
        this.entityData.set(OPENING_TICKS, 0);
        if (target != null) {
            this.teleportAroundTarget(target, 1.6D);
        }
        this.triggerAttackPose(ATTACK_POSE_TELEPORT, 14);
        this.playSound(SoundEvents.WITHER_SPAWN, 1.0F, 1.35F);
        this.speak(PHASE_TWO_LINES);
        patLineCooldown = PAT_LINE_COOLDOWN_TICKS;
    }

    private void applySecondPhasePressure(LivingEntity target) {
        double desiredHeight = target.getY() + 0.9D;
        double verticalDelta = desiredHeight - this.getY();
        Vec3 movement = this.getDeltaMovement();
        double verticalSpeed = Math.max(-0.08D, Math.min(0.08D, verticalDelta * 0.05D));
        this.setDeltaMovement(movement.x, verticalSpeed, movement.z);
        this.fallDistance = 0.0F;
    }

    private void hoverNearGround() {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x, Math.max(-0.08D, Math.min(0.08D, -this.getY() * 0.0D)), movement.z);
        this.fallDistance = 0.0F;
    }

    private boolean tryTeleportBehindTarget(LivingEntity target) {
        if (teleportCooldown > 0 || this.distanceToSqr(target) < 16.0D) {
            return false;
        }

        boolean teleported = this.teleportAroundTarget(target, -1.6D);
        if (teleported) {
            teleportCooldown = 70 + this.random.nextInt(25);
            dashCooldown = 14;
            this.triggerAttackPose(ATTACK_POSE_TELEPORT, 10);
            this.beginPaintReload(14);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.2F);
        }
        return teleported;
    }

    private boolean tryPerformCombo(LivingEntity target) {
        if (comboCooldown > 0 || this.distanceToSqr(target) > 25.0D) {
            return false;
        }

        Vec3 towardTarget = target.position().subtract(this.position()).normalize();
        Vec3 sideways = new Vec3(-towardTarget.z, 0.0D, towardTarget.x).scale(this.random.nextBoolean() ? 0.9D : -0.9D);
        Vec3 comboMotion = towardTarget.scale(1.05D).add(sideways.scale(0.75D));
        this.setDeltaMovement(comboMotion.x, 0.05D, comboMotion.z);
        this.hasImpulse = true;
        this.triggerAttackPose(ATTACK_POSE_COMBO, 12);
        dashTicks = 6;
        comboCooldown = 45 + this.random.nextInt(18);
        spawnInkPool(target.getX(), target.getY(), target.getZ());
        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 0.85F + this.random.nextFloat() * 0.3F);
        return true;
    }

    private boolean teleportAroundTarget(LivingEntity target, double behindDistance) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 look = target.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0.0D, look.z);
        if (horizontalLook.lengthSqr() < 1.0E-4D) {
            horizontalLook = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            horizontalLook = horizontalLook.normalize();
        }

        Vec3 destination = target.position().subtract(horizontalLook.scale(behindDistance)).add(0.0D, 0.1D, 0.0D);
        boolean teleported = this.randomTeleport(destination.x, destination.y, destination.z, true);
        if (teleported) {
            serverLevel.broadcastEntityEvent(this, (byte) 46);
            this.lookAt(target, 180.0F, 180.0F);
        }
        return teleported;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isPhaseTwo()) {
            this.fallDistance = 0.0F;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!secondPhaseTriggered && this.getHealth() - amount <= 0.0F) {
            if (!this.level().isClientSide) {
                this.beginSecondPhase(this.getTarget());
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.getUnderlyingPlayer() == null) {
            this.speak(DEATH_LINES);
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ArtistSecondPhase", secondPhaseTriggered);
        tag.putInt("ArtistOpeningTicks", this.getOpeningTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        secondPhaseTriggered = tag.getBoolean("ArtistSecondPhase");
        this.entityData.set(OPENING_TICKS, tag.getInt("ArtistOpeningTicks"));
        if (this.getUnderlyingPlayer() == null) {
            this.applyBossStats();
        }
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    public int getAttackPose() {
        return this.entityData.get(ATTACK_POSE);
    }

    public int getAttackPoseTicks() {
        return this.entityData.get(ATTACK_POSE_TICKS);
    }

    public boolean getOpening() {
        return this.getOpeningTicks() > 0;
    }

    public int getOpeningTicks() {
        return this.entityData.get(OPENING_TICKS);
    }

    private void triggerAttackPose(int pose, int ticks) {
        this.entityData.set(ATTACK_POSE, pose);
        this.entityData.set(ATTACK_POSE_TICKS, ticks);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this.getUnderlyingPlayer() == null) {
            bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return ResourceLocation.fromNamespaceAndPath("changedextras", "entities/latex_artist");
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
