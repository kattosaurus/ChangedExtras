package us.kattosaur.changedextras.entity.beasts;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.common.artist.ArtistTemporaryTransfur;
import us.kattosaur.changedextras.compat.ChangedAddonCompat;
import us.kattosaur.changedextras.entity.ai.ArtistFleeGoal;
import us.kattosaur.changedextras.init.ChangedExtrasEffects;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ArtistEntity extends AbstractWhiteCatEntity {

    // ==================================================================
    // TUNABLES - all boss-fight numbers live here.
    // ==================================================================

    // Phase 1: EXP 012, the friendly half.
    /** Minimum melee damage in phase 1 (0.5 heart). */
    public static final float PHASE_ONE_ATTACK_MIN = 1.0F;
    /** Maximum melee damage in phase 1 (1 heart). */
    public static final float PHASE_ONE_ATTACK_MAX = 2.0F;
    /** Chance that a phase-1 hit gives a beneficial effect rather than a harmful one. */
    public static final float PHASE_ONE_POSITIVE_EFFECT_CHANCE = 0.75F;
    /** Time between her "take control" windows (30 seconds). */
    public static final int CONTROL_INTERVAL_TICKS = 600;
    /** Length of a "take control" window (5 seconds). */
    public static final int CONTROL_DURATION_TICKS = 100;

    // Phase 1 -> 2 transition timeline, aligned to the RipOut clip.
    /** Blockbench RipOut clip: 10.5 seconds. */
    public static final int RIP_OUT_TICKS = 210;
    /** Blockbench Stretch clip: 3 seconds. */
    public static final int STRETCH_TICKS = 60;
    /** Blockbench CleaveGround clip: 2.0417 seconds, rounded up. */
    public static final int CLEAVE_GROUND_TICKS = 41;
    /** The beast holds still before reaching into the remains. */
    public static final int TRANSITION_IDLE_TICKS = 75;
    /** Reaching into the remains. */
    public static final int TRANSITION_PUNCTURE_TICKS = 15;
    /** Pulling out the latex brain. */
    public static final int TRANSITION_PULL_TICKS = 70;
    /** Winding up the stomp. */
    public static final int TRANSITION_STOMP_TICKS = 30;
    /** Final crack of the knuckles and neck. */
    public static final int TRANSITION_WARMUP_TICKS = 20;
    public static final int TRANSITION_TOTAL_TICKS = TRANSITION_IDLE_TICKS + TRANSITION_PUNCTURE_TICKS
            + TRANSITION_PULL_TICKS + TRANSITION_STOMP_TICKS + TRANSITION_WARMUP_TICKS;
    private static final int PUNCTURE_AT = TRANSITION_IDLE_TICKS;
    private static final int PULL_AT = PUNCTURE_AT + TRANSITION_PUNCTURE_TICKS;
    private static final int STOMP_AT = PULL_AT + TRANSITION_PULL_TICKS;
    private static final int WARMUP_AT = STOMP_AT + TRANSITION_STOMP_TICKS;

    // Phase 2: the beast.
    /** Health at which the beast gives up (15 hearts on the second bar). */
    public static final float FLEE_HEALTH = 30.0F;
    /** Base delay between special attacks. */
    public static final int ATTACK_SELECTOR_BASE_TICKS = 90;
    /** Levitation duration applied by the neck grab. The effect expires naturally. */
    public static final int NECK_GRAB_LEVITATION_TICKS = 60;
    /** Levitation level - 1 (100 => amplifier 99). */
    public static final int NECK_GRAB_LEVITATION_AMPLIFIER = 99;
    public static final int NECK_GRAB_COOLDOWN_TICKS = 240;
    /** Radius of the punctured, effect-emitting floor. */
    public static final float GROUND_PUNCTURE_RADIUS = 4.0F;
    public static final int GROUND_PUNCTURE_CLOUD_TICKS = 160;
    public static final int GROUND_PUNCTURE_COOLDOWN_TICKS = 220;
    /** How long the forced white-cat form lasts. */
    public static final int TEMP_TRANSFUR_DURATION_TICKS = 600;
    public static final int WHITE_CAT_COOLDOWN_TICKS = 700;
    /** Upward velocity of the brush-meteor jump (20+ blocks). */
    public static final double METEOR_JUMP_VELOCITY = 2.4D;
    public static final int METEOR_FLIGHT_TICKS = 60;
    public static final int METEOR_IMPACT_REMAINING_TICKS = 20;
    public static final int METEOR_EFFECT_COUNT = 5;
    public static final int METEOR_EFFECT_DURATION_TICKS = 200;
    public static final float METEOR_IMPACT_RADIUS = 3.0F;
    public static final float METEOR_IMPACT_DAMAGE = 12.0F;
    public static final float METEOR_SHOCKWAVE_RADIUS = 6.0F;
    public static final int METEOR_SHOCKWAVE_CLOUD_TICKS = 140;
    public static final int METEOR_COOLDOWN_TICKS = 600;

    // End state.
    public static final double FLEE_SPEED = 0.55D;
    public static final int FLEE_REPLY_COOLDOWN_TICKS = 400;

    // Existing boss stats (kept from the original fight).
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

    // Dialogue.
    private static final String[] PAT_LINES = {
            "entity.changedextras.artist.pat.1",
            "entity.changedextras.artist.pat.2",
            "entity.changedextras.artist.pat.3"
    };
    private static final String[] SPAWN_LINES = {
            "entity.changedextras.artist.spawn.1"
    };
    private static final String[] DEATH_LINES = {
            "entity.changedextras.artist.death.1"
    };
    private static final String[] SCREAM_LINES = {
            "entity.changedextras.artist.scream.1",
            "entity.changedextras.artist.scream.2",
            "entity.changedextras.artist.scream.3",
            "entity.changedextras.artist.scream.4"
    };
    private static final String RECOGNIZE_KATTO_LINE = "entity.changedextras.artist.recognize_katto.friendly";
    private static final String RECOGNIZE_KATTO_RED_LINE = "entity.changedextras.artist.recognize_katto.red";
    private static final String CONTROL_LINE = "entity.changedextras.artist.control";
    private static final String CONTROL_REPLY_LINE = "entity.changedextras.artist.control.red";
    private static final String TRANSITION_START_LINE = "entity.changedextras.artist.transition.start";
    private static final String TRANSITION_PUNCTURE_LINE = "entity.changedextras.artist.transition.puncture";
    private static final String TRANSITION_SCREAM_LINE = "entity.changedextras.artist.transition.scream";
    private static final String TRANSITION_DEATH_LINE = "entity.changedextras.artist.transition_death";
    private static final String BEAST_TRANSFUR_LINE = "entity.changedextras.artist.beast_transfur";
    private static final String[] NECK_GRAB_LINES = {
            "entity.changedextras.artist.neck_grab.1",
            "entity.changedextras.artist.neck_grab.2",
            "entity.changedextras.artist.neck_grab.3"
    };
    private static final String[] GROUND_PUNCTURE_LINES = {
            "entity.changedextras.artist.ground_puncture.1",
            "entity.changedextras.artist.ground_puncture.2"
    };
    private static final String GROUND_PUNCTURE_EASTER_EGG = "entity.changedextras.artist.ground_puncture.easter_egg";
    private static final String[] METEOR_LINES = {
            "entity.changedextras.artist.meteor.1",
            "entity.changedextras.artist.meteor.2",
            "entity.changedextras.artist.meteor.3"
    };
    private static final String METEOR_EASTER_EGG = "entity.changedextras.artist.meteor.easter_egg";
    private static final String[] FLEE_LINES = {
            "entity.changedextras.artist.flee.1",
            "entity.changedextras.artist.flee.2",
            "entity.changedextras.artist.flee.3"
    };
    private static final String FLEE_REPLY_LINE = "entity.changedextras.artist.flee_reply";
    /** [TUNABLE] Chance of picking the rare easter-egg attack line instead of a normal one. */
    private static final float EASTER_EGG_LINE_CHANCE = 0.05F;
    private static final double SPEECH_RANGE = 32.0D;
    private static final int PAT_LINE_COOLDOWN_TICKS = 60;
    private static final int SCREAM_LINE_COOLDOWN_TICKS = 40;

    // Latex Katto recognition / passivity.
    private static final ResourceLocation LATEX_KATTO_FORM =
            ResourceLocation.fromNamespaceAndPath("changedextras", "latex_katto");
    private static final double RECOGNIZE_RANGE = 16.0D;
    private static final double RECOGNIZE_FORGET_RANGE_SQR = 24.0D * 24.0D;

    // Synched data.
    private static final EntityDataAccessor<Integer> ATTACK_POSE =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_POSE_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OPENING_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> METEOR_CASTING =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CONTROL_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRANSITION_TICKS =
            SynchedEntityData.defineId(ArtistEntity.class, EntityDataSerializers.INT);

    // Attack poses (the model reads these).
    public static final int ATTACK_POSE_NONE = 0;
    public static final int ATTACK_POSE_BRUSH = 1;
    public static final int ATTACK_POSE_DASH = 2;
    public static final int ATTACK_POSE_COMBO = 3;
    public static final int ATTACK_POSE_TELEPORT = 4;
    public static final int ATTACK_POSE_RELOAD = 5;
    public static final int ATTACK_POSE_GRAB = 10;
    public static final int ATTACK_POSE_GROUND_PUNCTURE = 11;

    /** The fight's state machine. Only {@link #setPhase(FightPhase)} changes it. */
    public enum FightPhase {
        EXP_012, TRANSITION, BEAST, FLEEING
    }

    /** Special attacks; the selector never picks the same one twice in a row. */
    private enum BeastAttack {
        NECK_GRAB, GROUND_PUNCTURE, WHITE_CAT, BRUSH_METEOR
    }

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.changedextras.artist"),
            BossEvent.BossBarColor.PINK,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private FightPhase phase = FightPhase.EXP_012;
    private int dashCooldown = 40;
    private int dashTicks = 0;
    private int comboCooldown = 30;
    private int teleportCooldown = 90;
    private int patLineCooldown = 0;
    private int screamLineCooldown = 0;
    private int controlCooldown = CONTROL_INTERVAL_TICKS;
    private int transitionTicks = 0;

    private int attackCooldown = 60;
    private BeastAttack lastAttack = null;
    private int grabCooldown = 0;
    private int punctureCooldown = 0;
    private int whiteCatCooldown = 0;
    private int meteorCooldown = 0;
    private int meteorTicks = 0;
    private boolean meteorImpactDone = false;
    private Vec3 meteorTarget = null;

    private int fleeReplyCooldown = 0;
    private int fleePauseTicks = 0;
    private UUID brainItemId = null;
    /** Name substituted into the red "now let's actually fight" line after the transition. */
    private String transitionTargetName = "";

    // Katto-form players she has already greeted / who attacked her.
    private final Set<UUID> greetedKattos = new HashSet<>();
    private final Set<UUID> provokedKattos = new HashSet<>();

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
        this.entityData.define(METEOR_CASTING, false);
        this.entityData.define(PHASE, FightPhase.EXP_012.ordinal());
        this.entityData.define(CONTROL_TICKS, 0);
        this.entityData.define(TRANSITION_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractWhiteCatEntity.createAttributes();
    }

    private double maxHealthForPhase() {
        return phase == FightPhase.BEAST || phase == FightPhase.FLEEING ? PHASE_TWO_HEALTH : PHASE_ONE_HEALTH;
    }

    /** Spawns and explicit resets refill the bar; saves and phase changes do not. */
    public void applyBossStats() {
        applyBossStats(true);
    }

    private void applyBossStats(boolean refillHealth) {
        if (this.getUnderlyingPlayer() != null) {
            return;
        }
        var maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealthForPhase());
            if (refillHealth) {
                this.setHealth((float) maxHealthForPhase());
            }
        }
        var attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(phase == FightPhase.EXP_012
                    ? PHASE_ONE_ATTACK_MAX : 10.0D);
        }
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(currentSpeed());
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

    private double currentSpeed() {
        return switch (phase) {
            case EXP_012, TRANSITION -> PHASE_ONE_SPEED;
            case BEAST -> PHASE_TWO_SPEED;
            case FLEEING -> FLEE_SPEED;
        };
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag) {
        SpawnGroupData finalData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.applyBossStats();
        if (this.getUnderlyingPlayer() == null) {
            this.sayFriendly(SPAWN_LINES);
        }
        return finalData;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ArtistFleeGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, target ->
                target instanceof Player player
                        && !player.isCreative()
                        && !player.isSpectator()
                        && this.canAttack()
                        && !this.isPassiveTowards(player)));
    }

    private boolean canAttack() {
        return (phase == FightPhase.EXP_012 || phase == FightPhase.BEAST)
                && this.getControlTicks() == 0
                && this.getUnderlyingPlayer() == null;
    }

    @Override
    public void setTarget(LivingEntity target) {
        if (target != null && !canAttack()) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (!canAttack() || this.getOpening()) {
            return false;
        }
        boolean phaseOne = phase == FightPhase.EXP_012;
        this.triggerAttackPose(ATTACK_POSE_BRUSH, 8);

        // Phase 1: 0.5 - 1 heart per hit, rolled per swing.
        var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        float previousDamage = damage != null ? (float) damage.getBaseValue() : 0.0F;
        if (phaseOne && damage != null) {
            damage.setBaseValue(PHASE_ONE_ATTACK_MIN
                    + this.random.nextFloat() * (PHASE_ONE_ATTACK_MAX - PHASE_ONE_ATTACK_MIN));
        }
        boolean hit = super.doHurtTarget(target);
        if (phaseOne && damage != null) {
            damage.setBaseValue(previousDamage);
        }

        if (hit && !this.level().isClientSide) {
            if (phaseOne && target instanceof Player) {
                float lifesteal = (float) Math.max(1.0D, this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.35D);
                this.heal(lifesteal);
            }
            if (target instanceof LivingEntity living) {
                living.addEffect(phaseOne ? rollPhaseOneEffect() : rollBadEffect(100, 0));
                if (!phaseOne && this.random.nextFloat() < 0.25F) {
                    living.addEffect(new MobEffectInstance(ChangedExtrasEffects.ARTIST_FEAR.get(), 100, 0));
                }
            }
            for (int i = 0; i < 4; i++) {
                double offsetX = (this.random.nextDouble() - 0.5D) * 1.6D;
                double offsetZ = (this.random.nextDouble() - 0.5D) * 1.6D;
                spawnInkPool(target.getX() + offsetX, target.getY(), target.getZ() + offsetZ);
            }
        }
        return hit;
    }

    /** Phase 1 is weighted towards gifts: the tragic half does not want to hurt you. */
    private MobEffectInstance rollPhaseOneEffect() {
        if (this.random.nextFloat() < PHASE_ONE_POSITIVE_EFFECT_CHANCE) {
            return switch (this.random.nextInt(6)) {
                case 0 -> new MobEffectInstance(MobEffects.REGENERATION, 120, 1);
                case 1 -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 1);
                case 2 -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 140, 0);
                case 3 -> new MobEffectInstance(MobEffects.ABSORPTION, 160, 1);
                case 4 -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 0);
                default -> new MobEffectInstance(MobEffects.SATURATION, 100, 0);
            };
        }
        return switch (this.random.nextInt(4)) {
            case 0 -> new MobEffectInstance(MobEffects.WEAKNESS, 100, 0);
            case 1 -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0);
            case 2 -> new MobEffectInstance(MobEffects.BLINDNESS, 60, 0);
            default -> new MobEffectInstance(MobEffects.HUNGER, 100, 0);
        };
    }

    private MobEffectInstance rollBadEffect(int duration, int amplifier) {
        return switch (this.random.nextInt(9)) {
            case 0 -> new MobEffectInstance(MobEffects.WITHER, duration, amplifier);
            case 1 -> new MobEffectInstance(MobEffects.POISON, duration, amplifier);
            case 2 -> new MobEffectInstance(MobEffects.HARM, 1, 0);
            case 3 -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier + 1);
            case 4 -> new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier + 1);
            case 5 -> new MobEffectInstance(MobEffects.BLINDNESS, duration / 2, 0);
            case 6 -> new MobEffectInstance(MobEffects.CONFUSION, duration, 0);
            case 7 -> new MobEffectInstance(MobEffects.HUNGER, duration, amplifier);
            default -> new MobEffectInstance(ChangedExtrasEffects.ARTIST_FEAR.get(), duration, 0);
        };
    }

    // ------------------------------------------------------------------
    // Phase state machine
    // ------------------------------------------------------------------

    public FightPhase getFightPhase() {
        return phase;
    }

    /** The one and only place where the fight phase changes. */
    private void setPhase(FightPhase next) {
        if (this.phase == next) {
            return;
        }
        this.phase = next;
        this.entityData.set(PHASE, next.ordinal());

        this.setTarget(null);
        this.lastAttack = null;
        this.attackCooldown = 60;
        this.controlCooldown = CONTROL_INTERVAL_TICKS;
        this.setControlTicks(0);
        this.transitionTicks = 0;
        this.entityData.set(TRANSITION_TICKS, 0);
        this.meteorTicks = 0;
        this.meteorTarget = null;
        this.meteorImpactDone = false;
        this.setCastingMeteor(false);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
        this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);

        switch (next) {
            case EXP_012 -> {
                bossEvent.setColor(BossEvent.BossBarColor.PINK);
                this.applyBossStats(false);
            }
            case TRANSITION -> {
                bossEvent.setColor(BossEvent.BossBarColor.PINK);
                this.applyBossStats(false);
                this.setNoGravity(false);
                this.transitionTicks = TRANSITION_TOTAL_TICKS;
                this.entityData.set(TRANSITION_TICKS, transitionTicks);
                this.sayFriendly(TRANSITION_START_LINE);
            }
            case BEAST -> {
                bossEvent.setColor(BossEvent.BossBarColor.RED);
                // Second health bar begins full.
                this.applyBossStats(true);
                this.setNoGravity(true);
                this.dashCooldown = 35;
                this.dashTicks = 0;
                this.comboCooldown = 35;
                this.teleportCooldown = 45;
            }
            case FLEEING -> {
                bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
                this.applyBossStats(false);
                this.setNoGravity(false);
                this.fleePauseTicks = 0;
                if (this.level() instanceof ServerLevel serverLevel) {
                    ArtistTemporaryTransfur.restoreAll(serverLevel);
                }
                this.sayRed(FLEE_LINES);
            }
        }
    }

    private void setControlTicks(int ticks) {
        this.entityData.set(CONTROL_TICKS, ticks);
    }

    private int getControlTicks() {
        return this.entityData.get(CONTROL_TICKS);
    }

    private void beginTransition(Player attacker) {
        if (attacker != null) {
            this.transitionTargetName = attacker.getName().getString();
        } else if (this.getTarget() instanceof Player player) {
            this.transitionTargetName = player.getName().getString();
        }
        this.setPhase(FightPhase.TRANSITION);
    }

    /** Called by the chat handler; the beast snaps back, then keeps running. */
    public void onFleeTaunt() {
        if (phase != FightPhase.FLEEING || fleeReplyCooldown > 0) {
            return;
        }
        fleeReplyCooldown = FLEE_REPLY_COOLDOWN_TICKS;
        fleePauseTicks = 20;
        this.sayRed(FLEE_REPLY_LINE);
    }

    public boolean isFleePaused() {
        return fleePauseTicks > 0;
    }

    // ------------------------------------------------------------------
    // Server AI
    // ------------------------------------------------------------------

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getMainHandItem().isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ChangedExtras.ARTIST_BRUSH.get()));
        }

        LivingEntity target = this.getTarget();
        bossEvent.setProgress(this.getHealth() / Math.max(1.0F, this.getMaxHealth()));
        bossEvent.setName(this.getDisplayName());

        var maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (this.getUnderlyingPlayer() == null && maxHealth != null
                && Math.abs(maxHealth.getBaseValue() - maxHealthForPhase()) > 0.01D) {
            this.applyBossStats(false);
        }
        var speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setBaseValue(currentSpeed());
        }
        this.setNoGravity(phase == FightPhase.BEAST && meteorTicks == 0);

        tickSharedCooldowns();
        tickAttackPose();

        if (meteorTicks > 0) {
            tickMeteor();
            return;
        }

        if (phase == FightPhase.TRANSITION) {
            tickTransition();
            return;
        }

        if (this.getUnderlyingPlayer() == null) {
            if (phase == FightPhase.EXP_012 && this.tickCount % 10 == 0) {
                this.recognizeKattos();
            }
            if (target != null && (!this.canAttack() || this.isPassiveTowards(target))) {
                this.setTarget(null);
                target = null;
            }
        }

        if (phase == FightPhase.EXP_012) {
            tickPhaseOne(target);
        } else if (phase == FightPhase.BEAST) {
            tickPhaseTwo(target);
        } else if (phase == FightPhase.FLEEING) {
            // The flee goal handles everything. No attacks, no pressure.
            this.setTarget(null);
        }
    }

    private void tickSharedCooldowns() {
        if (comboCooldown > 0) comboCooldown--;
        if (teleportCooldown > 0) teleportCooldown--;
        if (patLineCooldown > 0) patLineCooldown--;
        if (screamLineCooldown > 0) screamLineCooldown--;
        if (fleeReplyCooldown > 0) fleeReplyCooldown--;
        if (fleePauseTicks > 0) fleePauseTicks--;
        if (attackCooldown > 0) attackCooldown--;
        if (grabCooldown > 0) grabCooldown--;
        if (punctureCooldown > 0) punctureCooldown--;
        if (whiteCatCooldown > 0) whiteCatCooldown--;
        if (meteorCooldown > 0) meteorCooldown--;
        if (dashCooldown > 0) dashCooldown--;
    }

    /**
     * Pose timers must only run on the server. If the client decrements the synced value itself it
     * clears the pose locally before the server-synced pose is rendered, so Blockbench clips never
     * get a chance to play. This mirrors the old server-only scream timer.
     */
    private void tickAttackPose() {
        int openingTicks = this.getOpeningTicks();
        if (openingTicks > 0) {
            int remaining = openingTicks - 1;
            this.entityData.set(OPENING_TICKS, remaining);
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = true;
            if (remaining <= 0 && this.getAttackPose() == ATTACK_POSE_RELOAD) {
                this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);
                dashCooldown = phase == FightPhase.BEAST ? 6 : 12;
            }
        }

        int poseTicks = this.getAttackPoseTicks();
        if (poseTicks > 0) {
            poseTicks--;
            this.entityData.set(ATTACK_POSE_TICKS, poseTicks);
            if (poseTicks <= 0 && this.getAttackPose() != ATTACK_POSE_RELOAD) {
                this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);
            }
        }
    }

    private void tickPhaseOne(LivingEntity target) {
        // "Every so often she takes control" - passive, no attacks, free hits.
        int control = this.getControlTicks();
        if (control > 0) {
            this.setControlTicks(control - 1);
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = true;
            this.setTarget(null);
            return;
        }

        if (target == null || !target.isAlive()) {
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, 1.0D, 0.7D));
            }
            return;
        }

        controlCooldown--;
        if (controlCooldown <= 0) {
            controlCooldown = CONTROL_INTERVAL_TICKS;
            this.setControlTicks(CONTROL_DURATION_TICKS);
            this.setTarget(null);
            this.triggerAttackPose(ATTACK_POSE_NONE, 0);
            this.sayFriendly(CONTROL_LINE);
            this.sayRed(CONTROL_REPLY_LINE);
            return;
        }

        if (this.getOpeningTicks() > 0) {
            this.lookAt(target, 180.0F, 180.0F);
            return;
        }

        tickDash(target, false);
    }

    private void tickPhaseTwo(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            this.hoverNearGround();
            return;
        }

        this.applySecondPhasePressure(target);

        if (this.trySelectBeastAttack(target)) {
            return;
        }
        if (dashTicks > 0) {
            tickDash(target, true);
            return;
        }
        if (this.tryTeleportBehindTarget(target)) {
            return;
        }
        this.tryPerformCombo(target);
        tickDash(target, true);
    }

    private void tickDash(LivingEntity target, boolean beast) {
        if (target == null || !target.isAlive()) {
            return;
        }
        if (dashTicks > 0) {
            dashTicks--;
            if ((this.tickCount & (beast ? 0 : 1)) == 0) {
                spawnInkPool(this.getX(), this.getY(), this.getZ());
            }
            if (beast && this.distanceToSqr(target) < 9.0D && this.tickCount % 8 == 0) {
                this.doHurtTarget(target);
            }
            if (dashTicks <= 0) {
                beginPaintReload(beast ? PHASE_TWO_RELOAD_TICKS : PHASE_ONE_RELOAD_TICKS);
            }
            return;
        }
        if (this.getOpeningTicks() > 0 || dashCooldown > 0) {
            return;
        }

        double distanceSqr = this.distanceToSqr(target);
        if (distanceSqr < MIN_DASH_RANGE_SQR || distanceSqr > MAX_DASH_RANGE_SQR || !this.hasLineOfSight(target)) {
            dashCooldown = beast ? 8 : 14;
            return;
        }

        Vec3 targetPos = beast ? target.position().add(0.0D, 1.2D, 0.0D) : target.position();
        Vec3 dash = targetPos.subtract(this.position()).normalize().scale(beast ? PHASE_TWO_DASH_SPEED : PHASE_ONE_DASH_SPEED);
        this.setDeltaMovement(dash.x, beast ? Math.max(-0.08D, Math.min(0.16D, dash.y)) : 0.22D, dash.z);
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.1F, 0.6F + this.random.nextFloat() * 0.25F);
        this.hasImpulse = true;
        this.triggerAttackPose(ATTACK_POSE_DASH, beast ? 12 : 9);
        dashTicks = beast ? 12 : 8;
        dashCooldown = beast ? 18 + this.random.nextInt(10) : 36 + this.random.nextInt(16);
    }

    // ------------------------------------------------------------------
    // Transition: the beast kills the remains of EXP 012
    // ------------------------------------------------------------------

    private void tickTransition() {
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
        this.fallDistance = 0.0F;

        int elapsed = TRANSITION_TOTAL_TICKS - transitionTicks;
        if (elapsed == PUNCTURE_AT) {
            // RipOut ~3.75s: reach into the remains of her brain. Fear only; Blindness was removed.
            this.sayRed(TRANSITION_PUNCTURE_LINE);
            this.applyTransitionFear();
            this.playSound(SoundEvents.SLIME_SQUISH, 1.4F, 0.6F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1.2D, this.getZ(),
                        8, 0.4D, 0.4D, 0.4D, 0.03D);
            }
        } else if (elapsed == PULL_AT) {
            // RipOut ~4.5s: lift what remains of the brain as latex.
            spawnBrainRemains();
            this.sayFriendly(TRANSITION_SCREAM_LINE);
        } else if (elapsed == STOMP_AT) {
            // RipOut ~8s: bring the leg down on it.
            stompBrainRemains();
            this.playSound(SoundEvents.BONE_BLOCK_BREAK, 0.7F, 0.7F);
        } else if (elapsed == WARMUP_AT) {
            this.playSound(SoundEvents.BONE_BLOCK_BREAK, 0.7F, 0.85F);
        } else if (elapsed == WARMUP_AT + 8) {
            this.playSound(SoundEvents.BONE_BLOCK_BREAK, 0.7F, 1.0F);
        }

        transitionTicks--;
        this.entityData.set(TRANSITION_TICKS, transitionTicks);
        if (transitionTicks <= 0) {
            Object name = transitionTargetName.isEmpty() ? "Player" : transitionTargetName;
            this.sayRed(TRANSITION_DEATH_LINE, name);
            this.setPhase(FightPhase.BEAST);
        }
    }

    private void spawnBrainRemains() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack goo = new ItemStack(ForgeRegistries.ITEMS.getValue(
                ResourceLocation.fromNamespaceAndPath("changed", "white_latex_goo")));
        if (goo.isEmpty()) {
            goo = new ItemStack(net.minecraft.world.item.Items.SLIME_BALL);
        }
        ItemEntity entity = new ItemEntity(serverLevel, this.getX(), this.getY() + 1.1D, this.getZ(), goo);
        entity.setPickUpDelay(32767);
        entity.setDeltaMovement(0.0D, 0.12D, 0.0D);
        serverLevel.addFreshEntity(entity);
        this.brainItemId = entity.getUUID();
    }

    private void stompBrainRemains() {
        if (brainItemId == null) {
            return;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            Entity brain = serverLevel.getEntity(brainItemId);
            if (brain != null) {
                brain.discard();
            }
            serverLevel.sendParticles(ParticleTypes.SNEEZE, this.getX(), this.getY() + 0.2D, this.getZ(),
                    12, 0.5D, 0.1D, 0.5D, 0.05D);
        }
        this.brainItemId = null;
        this.playSound(SoundEvents.SLIME_BLOCK_FALL, 1.2F, 0.75F);
    }

    // ------------------------------------------------------------------
    // Phase 2: attack selector
    // ------------------------------------------------------------------

    private boolean trySelectBeastAttack(LivingEntity target) {
        if (attackCooldown > 0 || this.getOpening() || dashTicks > 0 || this.isCastingMeteor()) {
            return false;
        }
        double distanceSqr = this.distanceToSqr(target);
        List<BeastAttack> candidates = new ArrayList<>();
        if (grabCooldown <= 0 && distanceSqr < 9.0D) {
            candidates.add(BeastAttack.NECK_GRAB);
        }
        if (punctureCooldown <= 0 && distanceSqr < 64.0D) {
            candidates.add(BeastAttack.GROUND_PUNCTURE);
        }
        if (whiteCatCooldown <= 0 && distanceSqr < 16.0D && target instanceof ServerPlayer) {
            candidates.add(BeastAttack.WHITE_CAT);
        }
        if (meteorCooldown <= 0 && distanceSqr < 576.0D) {
            candidates.add(BeastAttack.BRUSH_METEOR);
        }
        candidates.remove(lastAttack); // never the same attack twice in a row
        if (candidates.isEmpty()) {
            return false;
        }

        BeastAttack attack = candidates.get(this.random.nextInt(candidates.size()));
        switch (attack) {
            case NECK_GRAB -> doNeckGrab(target);
            case GROUND_PUNCTURE -> doGroundPuncture(target);
            case WHITE_CAT -> doWhiteCatTransfur((ServerPlayer) target);
            case BRUSH_METEOR -> doBrushMeteor(target);
        }
        lastAttack = attack;
        attackCooldown = ATTACK_SELECTOR_BASE_TICKS + this.random.nextInt(40);
        return true;
    }

    private void doNeckGrab(LivingEntity target) {
        grabCooldown = NECK_GRAB_COOLDOWN_TICKS;
        this.triggerAttackPose(ATTACK_POSE_GRAB, STRETCH_TICKS);
        this.sayRed(NECK_GRAB_LINES, target.getName().getString());
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION,
                NECK_GRAB_LEVITATION_TICKS, NECK_GRAB_LEVITATION_AMPLIFIER));
        target.addEffect(new MobEffectInstance(ChangedExtrasEffects.ARTIST_FEAR.get(), 120, 0));
        if (target instanceof Player player) {
            player.hurt(this.level().damageSources().mobAttack(this), 3.0F);
        }
        this.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2F, 0.7F);
    }

    private void doGroundPuncture(LivingEntity target) {
        punctureCooldown = GROUND_PUNCTURE_COOLDOWN_TICKS;
        this.triggerAttackPose(ATTACK_POSE_GROUND_PUNCTURE, CLEAVE_GROUND_TICKS);
        this.sayRedWithEasterEgg(GROUND_PUNCTURE_LINES, GROUND_PUNCTURE_EASTER_EGG);
        Vec3 toward = target.position().subtract(this.position());
        toward = new Vec3(toward.x, 0.0D, toward.z);
        if (toward.lengthSqr() > 1.0E-4D) {
            toward = toward.normalize();
        } else {
            toward = new Vec3(0.0D, 0.0D, 1.0D);
        }
        Vec3 impact = this.position().add(toward.scale(Math.min(2.0D, this.distanceTo(target) * 0.5D)));

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, impact.x, impact.y, impact.z,
                    10, 0.6D, 0.2D, 0.6D, 0.04D);
            for (int i = 0; i < 7; i++) {
                double angle = (Math.PI * 2.0D * i) / 7.0D + this.random.nextFloat() * 0.4F;
                double radius = 0.8D + this.random.nextFloat() * GROUND_PUNCTURE_RADIUS;
                double x = impact.x + Math.cos(angle) * radius;
                double z = impact.z + Math.sin(angle) * radius;
                BlockPos ground = BlockPos.containing(x, impact.y, z);
                int y = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, ground.getX(), ground.getZ());
                AreaEffectCloud cloud = new AreaEffectCloud(this.level(), x, y + 0.1D, z);
                cloud.setRadius(1.5F);
                cloud.setDuration(GROUND_PUNCTURE_CLOUD_TICKS);
                cloud.setWaitTime(0);
                cloud.setRadiusPerTick(-1.5F / GROUND_PUNCTURE_CLOUD_TICKS);
                cloud.setFixedColor(0x4B0082);
                cloud.addEffect(rollBadEffect(120, 0));
                serverLevel.addFreshEntity(cloud);
            }
            for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class,
                    new net.minecraft.world.phys.AABB(impact, impact).inflate(GROUND_PUNCTURE_RADIUS, 3.0D, GROUND_PUNCTURE_RADIUS))) {
                player.addEffect(new MobEffectInstance(ChangedExtrasEffects.ARTIST_FEAR.get(), 100, 0));
                player.addEffect(rollBadEffect(100, 0));
            }
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE, 0.8F, 0.7F);
    }

    private void doWhiteCatTransfur(ServerPlayer target) {
        whiteCatCooldown = WHITE_CAT_COOLDOWN_TICKS;
        this.triggerAttackPose(ATTACK_POSE_BRUSH, 20);
        this.sayRed(BEAST_TRANSFUR_LINE);
        ArtistTemporaryTransfur.apply(target, TEMP_TRANSFUR_DURATION_TICKS);
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 0.8F);
    }

    private void doBrushMeteor(LivingEntity target) {
        meteorCooldown = METEOR_COOLDOWN_TICKS;
        meteorTicks = METEOR_FLIGHT_TICKS;
        meteorImpactDone = false;
        meteorTarget = target.position().add(target.getDeltaMovement().scale(6.0D));
        this.setNoGravity(false);
        this.setDeltaMovement(0.0D, METEOR_JUMP_VELOCITY, 0.0D);
        this.hasImpulse = true;
        this.fallDistance = 0.0F;
        this.setCastingMeteor(true);
        this.sayRedWithEasterEgg(METEOR_LINES, METEOR_EASTER_EGG);
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4F, 0.6F);
    }

    private void tickMeteor() {
        this.fallDistance = 0.0F;
        int remaining = --meteorTicks;
        if (meteorTarget == null) {
            return;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            double t = 1.0D - (double) remaining / METEOR_FLIGHT_TICKS;
            double x = meteorTarget.x + (this.getX() - meteorTarget.x) * (1.0D - t);
            double y = this.getY() + 6.0D;
            double z = meteorTarget.z + (this.getZ() - meteorTarget.z) * (1.0D - t);
            serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 3,
                    0.3D, 0.3D, 0.3D, 0.01D);
            serverLevel.sendParticles(ParticleTypes.ASH, meteorTarget.x, meteorTarget.y + 8.0D, meteorTarget.z,
                    2, 0.6D, 0.4D, 0.6D, 0.01D);
        }

        // "Does something with the brush" at the apex; the EXP 009 chant owns the pose.
        if (remaining == METEOR_FLIGHT_TICKS / 2) {
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.6F, 1.2F);
        }

        if (!meteorImpactDone && remaining <= METEOR_IMPACT_REMAINING_TICKS) {
            meteorImpactDone = true;
            impactMeteor();
        }

        if (remaining <= 0) {
            meteorTarget = null;
            this.setCastingMeteor(false);
            this.entityData.set(ATTACK_POSE, ATTACK_POSE_NONE);
        }
    }

    private void impactMeteor() {
        if (!(this.level() instanceof ServerLevel serverLevel) || meteorTarget == null) {
            return;
        }
        double x = meteorTarget.x;
        double y = meteorTarget.y;
        double z = meteorTarget.z;
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1.0D, z, 1, 0, 0, 0, 0);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y + 0.5D, z, 16, 0.9D, 0.5D, 0.9D, 0.05D);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.6F, 0.7F);

        for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class,
                new net.minecraft.world.phys.AABB(x, y, z, x, y, z).inflate(METEOR_IMPACT_RADIUS, 4.0D, METEOR_IMPACT_RADIUS))) {
            player.hurt(this.level().damageSources().mobAttack(this), METEOR_IMPACT_DAMAGE);
            for (int i = 0; i < METEOR_EFFECT_COUNT; i++) {
                player.addEffect(rollBadEffect(METEOR_EFFECT_DURATION_TICKS, 1));
            }
        }

        // Shockwave of negative effects around the impact.
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2.0D * i) / 8.0D;
            double cx = x + Math.cos(angle) * METEOR_SHOCKWAVE_RADIUS;
            double cz = z + Math.sin(angle) * METEOR_SHOCKWAVE_RADIUS;
            BlockPos ground = BlockPos.containing(cx, y, cz);
            int cy = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, ground.getX(), ground.getZ());
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), cx, cy + 0.1D, cz);
            cloud.setRadius(1.8F);
            cloud.setDuration(METEOR_SHOCKWAVE_CLOUD_TICKS);
            cloud.setWaitTime(0);
            cloud.setRadiusPerTick(-1.8F / METEOR_SHOCKWAVE_CLOUD_TICKS);
            cloud.setFixedColor(0x8B0000);
            cloud.addEffect(rollBadEffect(120, 0));
            serverLevel.addFreshEntity(cloud);
        }
    }

    // ------------------------------------------------------------------
    // Shared helpers
    // ------------------------------------------------------------------

    private void spawnInkPool(double x, double y, double z) {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), x, y, z);
        cloud.setRadius(phase == FightPhase.BEAST ? 1.8F : 1.2F);
        cloud.setDuration(phase == FightPhase.BEAST ? 100 : 70);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.setFixedColor(0x101010 | this.random.nextInt(0xEFEFEF));
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                phase == FightPhase.BEAST ? 120 : 80, phase == FightPhase.BEAST ? 2 : 1));
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
        this.setDeltaMovement(movement.x, 0.0D, movement.z);
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

    // ------------------------------------------------------------------
    // Dialogue / recognition (unchanged where the design does not replace it)
    // ------------------------------------------------------------------

    private void sayFriendly(String translationKey, Object... args) {
        say(ChatFormatting.WHITE, false, translationKey, args);
    }

    private void sayFriendly(String[] translationKeys, Object... args) {
        say(ChatFormatting.WHITE, false, randomLine(translationKeys), args);
    }

    private void sayRed(String translationKey, Object... args) {
        say(ChatFormatting.RED, true, translationKey, args);
    }

    private void sayRed(String[] translationKeys, Object... args) {
        say(ChatFormatting.RED, true, randomLine(translationKeys), args);
    }

    private void sayRedWithEasterEgg(String[] normalKeys, String easterEggKey, Object... args) {
        String key = this.random.nextFloat() < EASTER_EGG_LINE_CHANCE
                ? easterEggKey
                : randomLine(normalKeys);
        say(ChatFormatting.RED, true, key, args);
    }

    private String randomLine(String[] translationKeys) {
        return translationKeys[this.random.nextInt(translationKeys.length)];
    }

    private void say(ChatFormatting color, boolean redSpeaker, String translationKey, Object... args) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Component line = Component.translatable(translationKey, args).withStyle(color);
        Component message = Component.literal("<").withStyle(color)
                .append(Component.translatable("entity.changedextras.latex_artist").withStyle(color))
                .append(Component.literal(redSpeaker ? " (Red)> " : "> ").withStyle(color))
                .append(line);
        for (ServerPlayer nearby : serverLevel.getEntitiesOfClass(
                ServerPlayer.class, this.getBoundingBox().inflate(SPEECH_RANGE))) {
            nearby.sendSystemMessage(message);
        }
    }

    public void whenPattedReaction(LivingEntity patter, InteractionHand hand) {
        if (this.level().isClientSide || this.getUnderlyingPlayer() != null || phase != FightPhase.EXP_012) {
            return;
        }
        if (patLineCooldown > 0) {
            return;
        }
        patLineCooldown = PAT_LINE_COOLDOWN_TICKS;
        this.sayFriendly(PAT_LINES);
    }

    private static boolean isKattoForm(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        return variant != null && LATEX_KATTO_FORM.equals(variant.getParent().getFormId());
    }

    /** She leaves unprovoked Latex Katto players alone in every phase. */
    private boolean isPassiveTowards(LivingEntity entity) {
        return isKattoForm(entity) && !provokedKattos.contains(entity.getUUID());
    }

    private void recognizeKattos() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        greetedKattos.removeIf(id -> {
            Player p = serverLevel.getPlayerByUUID(id);
            return p == null || !isKattoForm(p) || this.distanceToSqr(p) > RECOGNIZE_FORGET_RANGE_SQR;
        });
        for (ServerPlayer player : serverLevel.getEntitiesOfClass(
                ServerPlayer.class, this.getBoundingBox().inflate(RECOGNIZE_RANGE))) {
            if (isKattoForm(player) && !player.isSpectator() && greetedKattos.add(player.getUUID())) {
                this.sayFriendly(RECOGNIZE_KATTO_LINE);
                this.sayRed(RECOGNIZE_KATTO_RED_LINE);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (phase == FightPhase.BEAST && meteorTicks == 0) {
            this.fallDistance = 0.0F;
        }

        // Pose timers are advanced in customServerAiStep only; doing it here would let the
        // client clear its own synced pose before the Blockbench clip is rendered.
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (phase == FightPhase.TRANSITION) {
            return false;
        }
        if (!this.level().isClientSide && this.getUnderlyingPlayer() == null
                && phase == FightPhase.EXP_012
                && source.getEntity() instanceof Player attacker && isKattoForm(attacker)) {
            provokedKattos.add(attacker.getUUID());
            this.setTarget(attacker);
        }
        boolean latexSolventAttack = this.isLatexSolventAttack(source);
        if (latexSolventAttack && !this.level().isClientSide && phase == FightPhase.EXP_012) {
            this.scream();
        }
        if (phase == FightPhase.EXP_012 && this.getHealth() - amount <= 0.0F) {
            // Latex solvent bypasses phase-one immunity, as before.
            if (!latexSolventAttack) {
                if (!this.level().isClientSide) {
                    beginTransition(source.getEntity() instanceof Player attacker ? attacker : null);
                }
                return false;
            }
        }

        boolean hurt = super.hurt(source, amount);
        if (hurt && phase == FightPhase.BEAST && this.getHealth() > 0.0F
                && this.getHealth() <= FLEE_HEALTH && !this.level().isClientSide) {
            setPhase(FightPhase.FLEEING);
        }
        return hurt;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return phase == FightPhase.TRANSITION || super.isInvulnerableTo(source);
    }

    private boolean isLatexSolventAttack(DamageSource source) {
        Entity attacker = source.getDirectEntity();
        if (attacker == null) {
            attacker = source.getEntity();
        }
        return attacker != null && ChangedAddonCompat.getLatexSolventLevel(attacker) > 0.0D;
    }

    private void scream() {
        if (screamLineCooldown > 0 || this.getUnderlyingPlayer() != null) {
            return;
        }
        screamLineCooldown = SCREAM_LINE_COOLDOWN_TICKS;
        this.sayFriendly(SCREAM_LINES);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            if (this.getUnderlyingPlayer() == null) {
                if (phase == FightPhase.EXP_012) {
                    this.sayFriendly(DEATH_LINES);
                } else {
                    this.sayRed(DEATH_LINES);
                }
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                ArtistTemporaryTransfur.restoreAll(serverLevel);
            }
        }
        super.die(source);
    }

    // ------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ArtistFightPhase", phase.ordinal());
        tag.putInt("ArtistTransitionTicks", transitionTicks);
        tag.putInt("ArtistControlTicks", this.getControlTicks());
        tag.putInt("ArtistControlCooldown", controlCooldown);
        tag.putInt("ArtistAttackCooldown", attackCooldown);
        tag.putInt("ArtistLastAttack", lastAttack == null ? -1 : lastAttack.ordinal());
        tag.putInt("ArtistGrabCooldown", grabCooldown);
        tag.putInt("ArtistPunctureCooldown", punctureCooldown);
        tag.putInt("ArtistWhiteCatCooldown", whiteCatCooldown);
        tag.putInt("ArtistMeteorCooldown", meteorCooldown);
        tag.putInt("ArtistMeteorTicks", meteorTicks);
        if (meteorTarget != null) {
            tag.putDouble("ArtistMeteorX", meteorTarget.x);
            tag.putDouble("ArtistMeteorY", meteorTarget.y);
            tag.putDouble("ArtistMeteorZ", meteorTarget.z);
        }
        if (brainItemId != null) {
            tag.putString("ArtistBrainItem", brainItemId.toString());
        }
        tag.putString("ArtistTransitionTarget", transitionTargetName);
        ListTag kattos = new ListTag();
        for (UUID id : provokedKattos) {
            kattos.add(StringTag.valueOf(id.toString()));
        }
        tag.put("ArtistProvokedKattos", kattos);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ArtistFightPhase")) {
            int ordinal = tag.getInt("ArtistFightPhase");
            phase = FightPhase.values()[Math.max(0, Math.min(FightPhase.values().length - 1, ordinal))];
        } else if (tag.getBoolean("ArtistSecondPhase")) {
            phase = FightPhase.BEAST; // legacy save
        }
        this.entityData.set(PHASE, phase.ordinal());

        transitionTicks = tag.getInt("ArtistTransitionTicks");
        this.entityData.set(TRANSITION_TICKS, transitionTicks);
        this.setControlTicks(tag.getInt("ArtistControlTicks"));
        controlCooldown = tag.contains("ArtistControlCooldown")
                ? tag.getInt("ArtistControlCooldown") : CONTROL_INTERVAL_TICKS;
        attackCooldown = tag.getInt("ArtistAttackCooldown");
        int last = tag.getInt("ArtistLastAttack");
        lastAttack = last >= 0 && last < BeastAttack.values().length ? BeastAttack.values()[last] : null;
        grabCooldown = tag.getInt("ArtistGrabCooldown");
        punctureCooldown = tag.getInt("ArtistPunctureCooldown");
        whiteCatCooldown = tag.getInt("ArtistWhiteCatCooldown");
        meteorCooldown = tag.getInt("ArtistMeteorCooldown");
        meteorTicks = tag.getInt("ArtistMeteorTicks");
        meteorImpactDone = false;
        this.setCastingMeteor(meteorTicks > 0);
        if (tag.contains("ArtistMeteorX")) {
            meteorTarget = new Vec3(tag.getDouble("ArtistMeteorX"),
                    tag.getDouble("ArtistMeteorY"), tag.getDouble("ArtistMeteorZ"));
        }
        brainItemId = parseUuid(tag.getString("ArtistBrainItem"));
        transitionTargetName = tag.getString("ArtistTransitionTarget");

        provokedKattos.clear();
        ListTag kattos = tag.getList("ArtistProvokedKattos", net.minecraft.nbt.Tag.TAG_STRING);
        for (int i = 0; i < kattos.size(); i++) {
            UUID id = parseUuid(kattos.getString(i));
            if (id != null) {
                provokedKattos.add(id);
            }
        }

        if (this.getUnderlyingPlayer() == null) {
            this.applyBossStats(false); // never refill a half-eaten boss bar on relog
        }
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
        bossEvent.setColor(switch (phase) {
            case EXP_012, TRANSITION -> BossEvent.BossBarColor.PINK;
            case BEAST -> BossEvent.BossBarColor.RED;
            case FLEEING -> BossEvent.BossBarColor.YELLOW;
        });
        this.setNoGravity(phase == FightPhase.BEAST);
    }

    private static UUID parseUuid(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
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

    /** Synced flag that hands the model over to Changed Addon Plus's EXP 009 chant animator. */
    public boolean isCastingMeteor() {
        return this.entityData.get(METEOR_CASTING);
    }

    private void setCastingMeteor(boolean casting) {
        this.entityData.set(METEOR_CASTING, casting);
    }

    public int getTransitionTicks() {
        return this.entityData.get(TRANSITION_TICKS);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    /** Existing Fear effect, now played as EXP 012 dies in the transition. Blindness was removed. */
    private void applyTransitionFear() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (LivingEntity living : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32.0D))) {
            if (living == this) {
                continue;
            }
            living.addEffect(new MobEffectInstance(ChangedExtrasEffects.ARTIST_FEAR.get(), 200, 0));
        }
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
