package com.katt.changedextras.domain;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.ability.ParryAbility;
import com.katt.changedextras.block.RouteBlock;
import com.katt.changedextras.common.freeze.EntityFreezeManager;
import com.katt.changedextras.common.freeze.PlayerFreezeManager;
import com.katt.changedextras.entity.ModEntities;
import com.katt.changedextras.entity.beasts.SnepEntity;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.item.DomainTeleporter;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.DomainFlashPacket;
import com.katt.changedextras.network.DomainStatePacket;
import com.katt.changedextras.network.ParryStatePacket;
import com.katt.changedextras.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public class DomainExpansionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainExpansionManager.class);
    private static final DomainExpansionManager INSTANCE = new DomainExpansionManager();

    public static DomainExpansionManager getInstance() {
        return INSTANCE;
    }

    public enum DomainPhase {
        INTRO,
        SCENARIO,
        BREAK,
        JACKPOT,
        COLLAPSED
    }

    public enum Scenario {
        GATE("Ticket Gate", 1.0f / 30.0f,
                "The snow leopard turns from a corner and walks towards the station ticket gate, if they fail to reach it, they fall on their faces, 1/30 probability",
                "domain_scenarios/scenario_1"),
        BATHROOM("Bathroom", 1.0f / 24.0f,
                "Metro station bathroom, if they fail, they get launched backwards, 1/24 probability",
                "domain_scenarios/scenario_2"),
        SURFACE_EXIT("Exit to the surface", 1.0f / 18.0f,
                "Exit towards the surface, if they fail, they fall backwards into the metro station, rolling down the stairs, 1/18 probability",
                "domain_scenarios/scenario_3");

        private final String displayName;
        private final float baseProbability;
        private final String description;
        private final String structureId;

        Scenario(String displayName, float baseProbability, String description, String structureId) {
            this.displayName = displayName;
            this.baseProbability = baseProbability;
            this.description = description;
            this.structureId = structureId;
        }

        public String getDisplayName() { return displayName; }
        public float getBaseProbability() { return baseProbability; }
        public String getDescription() { return description; }
        public String getStructureId() { return structureId; }
    }

    // Explicitly mapped Luck Multipliers: [1.0, 1.5, 2.0, 3.0, 10.0]
    public static final float[] LUCK_MULTIPLIERS = {1.0f, 1.5f, 2.0f, 3.0f, 10.0f};
    public static final int MAX_SCENARIOS = 6;

    // Visual abilities (parries) the caster must use during the intermission to advance.
    public static final int REQUIRED_VISUAL_ABILITIES = 2;

    // Intro overlay: 0.5s fade in + 3.0s hold = 3.5s (70 ticks), then advances and triggers 0.5s fade out
    public static final int INTRO_DURATION_TICKS = 70;

    // After a scenario's Snep finishes (fails on a red marker or reaches the end) the domain waits
    // this long (2 seconds) before transitioning, so the final pose is held on screen.
    public static final int SCENARIO_SETTLE_TICKS = 40;

    // Safety net: if a scenario's Snep never finishes its route, give up after this long
    // (60 seconds) and resolve the scenario with its rolled outcome.
    public static final int SCENARIO_ROUTE_TIMEOUT_TICKS = 1200;
    public static final float SPHERE_DIAMETER = 16.0f;
    public static final float SPHERE_RADIUS = 8.0f;

    // The white barrier arena built in the void dimension by DomainTeleporter
    public static final int ARENA_RADIUS = 32;
    public static final int ARENA_HEIGHT = 32;

    // Everyone trapped in the domain arrives at the same spot inside the void dimension
    public static final BlockPos DOMAIN_ARRIVAL_POS = new BlockPos(0, 1, 0);

    // At the start of every scenario all trapped entities (except the Snep) are pulled back here
    // so nobody can end up outside the scenario build (e.g. outside the bathroom hall).
    public static final BlockPos SCENARIO_TELEPORT_POS = new BlockPos(0, 2, 0);

    // Hidden max-level Slowness duration. Long enough to outlast any domain phase; it is removed
    // explicitly whenever the intro ends or the domain collapses.
    public static final int DOMAIN_ROOT_DURATION_TICKS = 1000000;

    // The "Domain Expansion" voice line plays first; the barrier build, teleport and white
    // overlay are then held back for 2 seconds (40 ticks) so the line lands before everything.
    public static final int DOMAIN_OPEN_DELAY_TICKS = 40;

    // The barrier has to be white (solid White Concrete barrier)
    public static final BlockState WHITE_BARRIER_STATE = Blocks.WHITE_CONCRETE.defaultBlockState();

    public static class ActiveDomain {
        private final UUID casterUUID;
        private final String casterName;
        private final ResourceKey<Level> dimension;
        private final BlockPos center;
        private final Map<BlockPos, BlockState> originalBlockStates = new ConcurrentHashMap<>();
        private final Set<BlockPos> activeBarrierPositions = ConcurrentHashMap.newKeySet();
        private final Set<UUID> playersInSphere = new HashSet<>();
        private final List<TrappedEntity> trappedEntities = new ArrayList<>();

        // Spawning by patches queue
        private final List<List<BlockPos>> pendingPatches = new ArrayList<>();
        private int currentPatchIndex = 0;
        private int patchDelayTicks = 2; // Ticks between each patch spawning
        private int patchTimer = 0;

        private int currentScenarioIndex = 1; // 1 to 6
        private int breakParriesCount = 0;
        private DomainPhase phase = DomainPhase.INTRO;
        private int phaseTicks = 0;
        private Scenario currentScenario = null;

        // Set when the current scenario is rolled; the Snep then plays it out (walk to the end on a
        // win, stop on the first red failed-route marker on a loss).
        private boolean scenarioWon = false;

        // Countdown (in ticks) started once a scenario's Snep has finished, before the
        // domain transitions into the intermission. -1 means "not finished yet".
        private int settleTicks = -1;

        // Bounding box of the scenario build currently placed in the arena, so the next
        // scenario can clear it before pasting its own placeholder structure.
        private BlockPos scenarioOrigin = null;
        private Vec3i scenarioSize = null;

        // Route markers recovered from the scenario build (they are replaced with air on paste).
        // The Snep walks snepRoute; snepFailedRoute holds the red failed-route positions.
        private final List<BlockPos> snepRoute = new ArrayList<>();
        private final Set<Long> snepFailedRoute = new HashSet<>();
        private SnepEntity snep = null;

        public ActiveDomain(UUID casterUUID, String casterName, ResourceKey<Level> dimension, BlockPos center) {
            this.casterUUID = casterUUID;
            this.casterName = casterName;
            this.dimension = dimension;
            this.center = center;
        }

        public UUID getCasterUUID() { return casterUUID; }
        public String getCasterName() { return casterName; }
        public ResourceKey<Level> getDimension() { return dimension; }
        public BlockPos getCenter() { return center; }
        public int getCurrentScenarioIndex() { return currentScenarioIndex; }
        public int getBreakParriesCount() { return breakParriesCount; }
        public DomainPhase getPhase() { return phase; }
    }

    /**
     * Remembers where a trapped entity came from so it can be sent back when the domain collapses.
     */
    public static class TrappedEntity {
        private final Entity entity;
        private final ResourceKey<Level> originDimension;
        private final Vec3 originPosition;
        private final float yaw;
        private final float pitch;

        public TrappedEntity(Entity entity, ResourceKey<Level> originDimension, Vec3 originPosition, float yaw, float pitch) {
            this.entity = entity;
            this.originDimension = originDimension;
            this.originPosition = originPosition;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    private final Map<UUID, ActiveDomain> activeDomainsByCaster = new ConcurrentHashMap<>();

    // Domains whose voice line has played but whose arena/teleport/overlay is still pending.
    private final List<PendingOpen> pendingOpens = new ArrayList<>();

    /**
     * A domain open in progress: the voice line has fired and the rest of the opening waits out
     * {@link #DOMAIN_OPEN_DELAY_TICKS} before building the barrier, trapping everyone and showing
     * the white overlay.
     */
    private static final class PendingOpen {
        private final UUID casterUUID;
        private final ServerLevel originLevel;
        private final BlockPos originCenter;
        private final ServerLevel domainLevel;
        private final ActiveDomain domain;
        private int ticksRemaining;

        private PendingOpen(UUID casterUUID, ServerLevel originLevel, BlockPos originCenter,
                            ServerLevel domainLevel, ActiveDomain domain, int ticksRemaining) {
            this.casterUUID = casterUUID;
            this.originLevel = originLevel;
            this.originCenter = originCenter;
            this.domainLevel = domainLevel;
            this.domain = domain;
            this.ticksRemaining = ticksRemaining;
        }
    }

    public boolean isDomainActive(UUID casterUUID) {
        return activeDomainsByCaster.containsKey(casterUUID);
    }

    public boolean hasPendingOpen(UUID casterUUID) {
        if (casterUUID == null) return false;
        for (PendingOpen pending : this.pendingOpens) {
            if (pending.casterUUID.equals(casterUUID)) {
                return true;
            }
        }
        return false;
    }

    public ActiveDomain getDomainByCaster(UUID casterUUID) {
        return activeDomainsByCaster.get(casterUUID);
    }

    public boolean isBarrierBlock(BlockPos pos) {
        for (ActiveDomain domain : activeDomainsByCaster.values()) {
            if (domain.activeBarrierPositions.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True when the given position falls inside the arena of an active domain in that level.
     * Blocks inside the domain are unbreakable; only the barrier shell may be mined (slowly).
     */
    public boolean isInsideActiveDomain(ServerLevel level, BlockPos pos) {
        for (ActiveDomain domain : activeDomainsByCaster.values()) {
            if (!domain.dimension.equals(level.dimension())) continue;
            BlockPos center = domain.center;
            double dx = pos.getX() - center.getX();
            double dz = pos.getZ() - center.getZ();
            if (dx * dx + dz * dz <= (double) ARENA_RADIUS * ARENA_RADIUS
                    && pos.getY() >= center.getY() - 1
                    && pos.getY() <= center.getY() + ARENA_HEIGHT) {
                return true;
            }
        }
        return false;
    }

    /**
     * True while the given entity is trapped and held still during a domain intro,
     * during which it should not be able to take any damage.
     */
    public boolean isTrappedDuringIntro(Entity entity) {
        if (entity == null) return false;
        for (ActiveDomain domain : activeDomainsByCaster.values()) {
            if (domain.phase != DomainPhase.INTRO) continue;
            for (TrappedEntity trapped : domain.trappedEntities) {
                if (trapped.entity == entity) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Triggers the opening of "Idle Death Gamble" Domain Expansion.
     */
    public boolean openDomain(ServerPlayer caster) {
        if (caster == null || !caster.isAlive()) return false;
        if (isDomainActive(caster.getUUID()) || hasPendingOpen(caster.getUUID())) return false;

        ServerLevel originLevel = caster.serverLevel();
        BlockPos originCenter = caster.blockPosition();

        ServerLevel domainLevel = originLevel.getServer().getLevel(ModDimensions.DM_KEY);
        if (domainLevel == null) {
            LOGGER.warn("[DomainExpansionManager] Domain dimension '{}' is not loaded, cannot open domain",
                    ModDimensions.DM_KEY.location());
            return false;
        }

        // The arena is always built at the same spot in the void dimension
        BlockPos arenaCenter = new BlockPos(0, 1, 0);
        ActiveDomain domain = new ActiveDomain(caster.getUUID(), caster.getGameProfile().getName(),
                domainLevel.dimension(), arenaCenter);

        // 1. The "Domain Expansion" voice line plays first, before anything else (barrier build,
        //    teleport and white overlay all wait for it).
        playDomainExpansionSound(originLevel, caster);

        // 2. Queue the barrier build, trapping and overlay for 2 seconds later.
        this.pendingOpens.add(new PendingOpen(caster.getUUID(), originLevel, originCenter,
                domainLevel, domain, DOMAIN_OPEN_DELAY_TICKS));

        // 3. The caster is held still and made invulnerable for the 2-second opening delay so the
        //    voice line can play out without them being interrupted. Cleared in finishOpeningDomain.
        caster.setInvulnerable(true);
        applyRootSlow(caster);

        LOGGER.info("[DomainExpansionManager] {} began opening domain 'Idle Death Gamble'",
                domain.casterName);
        return true;
    }

    /**
     * Finishes a two-second-deferred domain open: registers the domain, traps everyone inside the
     * void dimension (building the barrier arena) and applies the white overlay.
     */
    private void finishOpeningDomain(PendingOpen pending) {
        ServerPlayer caster = pending.originLevel.getServer().getPlayerList().getPlayer(pending.casterUUID);
        if (caster != null) {
            // End the 2-second pre-open hold before the intro hold is applied just below.
            caster.setInvulnerable(false);
            clearRootSlow(caster);
        }
        if (caster == null || !caster.isAlive()) {
            return;
        }

        ActiveDomain domain = pending.domain;
        activeDomainsByCaster.put(domain.casterUUID, domain);

        // Trap everyone (players and entities) inside the domain and teleport them into the dimension
        teleportTrappedEntities(pending.originLevel, pending.originCenter, pending.domainLevel, domain);

        // UI & render mask overlay for players caught within the domain
        applyDomainOverlayAndBlindness(pending.domainLevel, domain);

        LOGGER.info("[DomainExpansionManager] {} opened domain 'Idle Death Gamble' and trapped {} entities",
                domain.casterName, domain.trappedEntities.size());
    }

    /**
     * Teleports every player and entity within the domain radius from the origin level into the
     * dedicated void dimension, building the barrier arena around them on arrival.
     */
    private void teleportTrappedEntities(ServerLevel originLevel, BlockPos originCenter, ServerLevel domainLevel, ActiveDomain domain) {
        Vec3 centerVec = Vec3.atCenterOf(originCenter);
        AABB bounds = new AABB(
                originCenter.getX() - SPHERE_RADIUS, originCenter.getY() - SPHERE_RADIUS, originCenter.getZ() - SPHERE_RADIUS,
                originCenter.getX() + SPHERE_RADIUS, originCenter.getY() + SPHERE_RADIUS, originCenter.getZ() + SPHERE_RADIUS
        );

        List<Entity> trapped = originLevel.getEntitiesOfClass(Entity.class, bounds,
                e -> e.position().distanceTo(centerVec) <= SPHERE_RADIUS + 0.5);

        int count = Math.max(1, trapped.size());
        double ringRadius = count <= 1 ? 0.0 : 2.0;
        int index = 0;

        for (Entity entity : trapped) {
            if (entity.level() != originLevel) continue;

            ResourceKey<Level> originDimension = originLevel.dimension();
            Vec3 originPosition = entity.position();
            float yaw = entity.getYRot();
            float pitch = entity.getXRot();

            // Spread arrivals around the center so trapped players don't stack on one block
            double angle = (2.0 * Math.PI * index) / count;
            index++;
            Vec3 arrival = new Vec3(
                    DOMAIN_ARRIVAL_POS.getX() + 0.5 + Math.cos(angle) * ringRadius,
                    DOMAIN_ARRIVAL_POS.getY(),
                    DOMAIN_ARRIVAL_POS.getZ() + 0.5 + Math.sin(angle) * ringRadius
            );

            Entity moved = entity.changeDimension(domainLevel, new DomainTeleporter(arrival, true));
            if (moved == null) continue;

            domain.trappedEntities.add(new TrappedEntity(moved, originDimension, originPosition, yaw, pitch));

            if (moved instanceof ServerPlayer serverPlayer) {
                domain.playersInSphere.add(serverPlayer.getUUID());
                // Root the player for the whole intro with hidden max-level Slowness instead of a
                // hard position freeze.
                applyRootSlow(serverPlayer);
            }
            // Non-player entities are left free to act so trapped mobs can still attack the caster,
            // which is what lets the caster land the visual abilities used as phase gates.
        }
    }

    /**
     * Plays the Japanese "Domain Expansion" (領域展開 - Ryoiki Tenkai) voice line for everyone
     * caught inside the domain the instant it opens.
     */
    public static void playDomainExpansionSound(ServerLevel level, LivingEntity caster) {
        if (caster == null) return;
        level.playSound(null,
                caster.getX(), caster.getY(), caster.getZ(),
                ChangedExtrasSounds.DOMAIN_EXPANSION.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
    }

    /**
     * Roots the player in place using max-level Slowness with the particles and icon hidden. This
     * replaces the old teleport-lock freeze. Removed via {@link #clearRootSlow(ServerPlayer)}.
     */
    private static void applyRootSlow(ServerPlayer player) {
        if (player == null) return;
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN, DOMAIN_ROOT_DURATION_TICKS, 255, false, false, false));
    }

    private static void clearRootSlow(ServerPlayer player) {
        if (player == null) return;
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    /**
     * Pulls every trapped entity (except the Snep) back to the scenario start so nobody can end up
     * outside the scenario build.
     */
    private void teleportTrappedToScenarioStart(ActiveDomain domain, ServerLevel level) {
        if (!domain.dimension.equals(level.dimension())) return;

        double x = SCENARIO_TELEPORT_POS.getX() + 0.5D;
        double y = SCENARIO_TELEPORT_POS.getY();
        double z = SCENARIO_TELEPORT_POS.getZ() + 0.5D;

        for (TrappedEntity trapped : domain.trappedEntities) {
            Entity entity = trapped.entity;
            if (entity == null || entity.isRemoved()) continue;
            if (entity == domain.snep) continue; // the Snep keeps walking its route

            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(level, x, y, z, serverPlayer.getYRot(), serverPlayer.getXRot());
            } else {
                entity.teleportTo(x, y, z);
            }
            entity.setDeltaMovement(Vec3.ZERO);
        }
    }

    /**
     * Safely teleports trapped players and entities back to the arena center floor during the intermission.
     */
    private void teleportTrappedToArenaCenter(ActiveDomain domain, ServerLevel level) {
        if (!domain.dimension.equals(level.dimension())) return;

        double x = DOMAIN_ARRIVAL_POS.getX() + 0.5D;
        double y = DOMAIN_ARRIVAL_POS.getY();
        double z = DOMAIN_ARRIVAL_POS.getZ() + 0.5D;

        for (TrappedEntity trapped : domain.trappedEntities) {
            Entity entity = trapped.entity;
            if (entity == null || entity.isRemoved()) continue;
            if (entity == domain.snep) continue;

            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(level, x, y, z, serverPlayer.getYRot(), serverPlayer.getXRot());
            } else {
                entity.teleportTo(x, y, z);
            }
            entity.setDeltaMovement(Vec3.ZERO);
        }
    }

    /**
     * Clusters the hollow sphere barrier into distinct organic patches that spawn sequentially.
     */
    private void prepareSpherePatches(ServerLevel level, ActiveDomain domain, BlockPos center, Vec3 casterLook) {
        List<Vec3> seedDirections = new ArrayList<>();

        // 10 Voronoi seed directions distributed across the sphere
        Vec3 forward = casterLook.normalize();
        Vec3 backward = forward.scale(-1.0);
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 down = new Vec3(0, -1, 0);
        Vec3 right = forward.cross(up).normalize();
        Vec3 left = right.scale(-1.0);

        seedDirections.add(forward);
        seedDirections.add(backward);
        seedDirections.add(up);
        seedDirections.add(down);
        seedDirections.add(right);
        seedDirections.add(left);
        seedDirections.add(forward.add(up).add(right).normalize());
        seedDirections.add(forward.add(down).add(left).normalize());
        seedDirections.add(backward.add(up).add(left).normalize());
        seedDirections.add(backward.add(down).add(right).normalize());

        Map<Integer, List<BlockPos>> patchMap = new HashMap<>();
        for (int i = 0; i < seedDirections.size(); i++) {
            patchMap.put(i, new ArrayList<>());
        }

        int r = (int) Math.ceil(SPHERE_RADIUS);
        double rInnerSq = (SPHERE_RADIUS - 0.75) * (SPHERE_RADIUS - 0.75);
        double rOuterSq = (SPHERE_RADIUS + 0.5) * (SPHERE_RADIUS + 0.5);

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    double distSq = x * x + y * y + z * z;
                    if (distSq >= rInnerSq && distSq <= rOuterSq) {
                        BlockPos targetPos = center.offset(x, y, z);
                        BlockState currentState = level.getBlockState(targetPos);
                        if (!currentState.is(Blocks.BEDROCK)) {
                            Vec3 dir = new Vec3(x, y, z).normalize();
                            int bestSeed = 0;
                            double bestDot = -2.0;
                            for (int i = 0; i < seedDirections.size(); i++) {
                                double dot = dir.dot(seedDirections.get(i));
                                if (dot > bestDot) {
                                    bestDot = dot;
                                    bestSeed = i;
                                }
                            }
                            patchMap.get(bestSeed).add(targetPos);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < seedDirections.size(); i++) {
            List<BlockPos> list = patchMap.get(i);
            if (!list.isEmpty()) {
                domain.pendingPatches.add(list);
            }
        }
    }

    private void spawnNextPatch(ServerLevel level, ActiveDomain domain) {
        if (domain.currentPatchIndex >= domain.pendingPatches.size()) {
            return;
        }

        List<BlockPos> patch = domain.pendingPatches.get(domain.currentPatchIndex);
        domain.currentPatchIndex++;

        double avgX = 0, avgY = 0, avgZ = 0;
        for (BlockPos pos : patch) {
            BlockState currentState = level.getBlockState(pos);
            domain.originalBlockStates.putIfAbsent(pos, currentState);
            domain.activeBarrierPositions.add(pos);

            // Set to White Concrete barrier block
            level.setBlock(pos, WHITE_BARRIER_STATE, 3);

            avgX += pos.getX() + 0.5;
            avgY += pos.getY() + 0.5;
            avgZ += pos.getZ() + 0.5;
        }

        if (!patch.isEmpty()) {
            avgX /= patch.size();
            avgY /= patch.size();
            avgZ /= patch.size();

            // Patch placement audio & white particles
            level.playSound(null, avgX, avgY, avgZ, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.2F, 1.1F);
            level.sendParticles(ParticleTypes.CLOUD, avgX, avgY, avgZ, 12, 0.6, 0.6, 0.6, 0.05);
            level.sendParticles(ParticleTypes.END_ROD, avgX, avgY, avgZ, 8, 0.4, 0.4, 0.4, 0.03);
        }
    }

    private void applyDomainOverlayAndBlindness(ServerLevel level, ActiveDomain domain) {
        for (UUID playerId : domain.playersInSphere) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerId);
            if (player == null) continue;

            // Short blindness just to mask the initial teleport; the white overlay handles the rest
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false, false));

            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new DomainStatePacket(
                            true,
                            domain.casterUUID,
                            domain.casterName,
                            true,
                            domain.currentScenarioIndex,
                            "Opening Rules",
                            LUCK_MULTIPLIERS[0],
                            false,
                            false
                    )
            );
        }
    }

    /**
     * Called when the caster uses a visual ability (such as summoning the parry doors).
     * Visual abilities only advance phases during the intermission: two of them advance
     * to the next scenario. During a scenario they are ignored entirely.
     */
    public void onVisualAbilityUsed(LivingEntity caster) {
        if (caster == null) return;
        ActiveDomain domain = activeDomainsByCaster.get(caster.getUUID());
        if (domain == null) return;
        if (!(caster.level() instanceof ServerLevel level)) return;

        if (domain.phase == DomainPhase.BREAK) {
            domain.breakParriesCount++;
            int visualCounter = caster.getPersistentData().getInt(ParryAbility.VISUAL_ABILITIES_COUNTER_TAG) + 1;
            caster.getPersistentData().putInt(ParryAbility.VISUAL_ABILITIES_COUNTER_TAG, visualCounter);

            if (caster instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(
                        Component.literal("§6§l[Intermission] Visual Abilities: §e" + domain.breakParriesCount
                                + " §7/ §e" + REQUIRED_VISUAL_ABILITIES),
                        true
                );
            }

            // Between scenarios, the caster has to use 2 visual abilities to reach the next one
            if (domain.breakParriesCount >= REQUIRED_VISUAL_ABILITIES) {
                if (caster instanceof ServerPlayer serverPlayer) {
                    PlayerFreezeManager.unfreeze(serverPlayer.getUUID());
                }
                caster.getPersistentData().remove(ParryAbility.PARRY_SEQUENCE_ACTIVE_TAG);
                caster.getPersistentData().remove(ParryAbility.PARRY_TICKS_TAG);
                ParryStatePacket.broadcast(level, caster.getUUID(), false, false);

                triggerNextScenario(domain, level);
            }
        }
    }

    /**
     * Compatibility hook for when a parry counterattack occurs.
     */
    public void onParrySuccess(LivingEntity caster) {
        // Intentionally kept for event handler compatibility;
        // visual ability activations are already counted in startUsing via onVisualAbilityUsed.
    }

    /**
     * Dismisses the intro white overlay / blindness and starts the first scenario.
     */
    private void advanceFromIntro(ActiveDomain domain, ServerLevel level) {
        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p == null) continue;
            clearRootSlow(p);
            p.removeEffect(MobEffects.BLINDNESS);
            ChangedExtrasNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> p),
                    new DomainStatePacket(
                            true,
                            domain.casterUUID,
                            domain.casterName,
                            false, // overlay dismissed -> client fades out over 0.5s
                            1,
                            "Starting Scenarios",
                            LUCK_MULTIPLIERS[0],
                            false,
                            false
                    )
            );
        }
        for (TrappedEntity trapped : domain.trappedEntities) {
            if (trapped.entity != null && !(trapped.entity instanceof ServerPlayer)) {
                EntityFreezeManager.unfreeze(trapped.entity.getUUID());
            }
        }
        triggerNextScenario(domain, level);
    }

    private void triggerNextScenario(ActiveDomain domain, ServerLevel level) {
        if (domain.currentScenarioIndex > 5) {
            // Reached 6th scenario without jackpot -> domain breaks
            collapseDomain(domain, level, false, true);
            return;
        }

        domain.phase = DomainPhase.SCENARIO;
        domain.phaseTicks = 0;

        // Swap transition: play the scenario_swap sting and flash everyone's screen white
        broadcastScenarioSwap(level, domain);

        // Randomly pick 1 of the 3 scenarios, never the same one twice in a row
        Scenario[] scenarios = Scenario.values();
        Scenario chosen;
        do {
            chosen = scenarios[level.random.nextInt(scenarios.length)];
        } while (scenarios.length > 1 && chosen == domain.currentScenario);
        domain.currentScenario = chosen;

        // Paste this scenario's build into the arena (empty placeholders until built)
        placeScenarioStructure(level, domain, chosen);

        // Pull every trapped entity (except the Snep) to the scenario start so nobody ends up
        // outside the scenario build.
        teleportTrappedToScenarioStart(domain, level);

        // Current luck multiplier: [1.0, 1.5, 2.0, 3.0, 10.0]
        float multiplier = LUCK_MULTIPLIERS[Math.min(domain.currentScenarioIndex - 1, LUCK_MULTIPLIERS.length - 1)];
        float winProbability = chosen.getBaseProbability() * multiplier;

        // Roll the scenario outcome once. Luck still decides: on a win the Snep walks its whole
        // route to the end, on a loss it stops on the first red failed-route marker. The domain
        // stays in the SCENARIO phase until the Snep has finished (see onServerTick); visual
        // abilities do nothing during this time.
        domain.scenarioWon = level.random.nextFloat() < winProbability;
        domain.settleTicks = -1;

        if (domain.snep != null) {
            domain.snep.setFailed(!domain.scenarioWon);
        }

        broadcastDomainMessage(level, domain,
                "§d§l[Scenario " + domain.currentScenarioIndex + "/5] §f" + chosen.getDisplayName() +
                        " §7(Luck: " + multiplier + "x | Chance: " + String.format(Locale.ROOT, "%.1f%%", winProbability * 100.0f) + ")");

        // Sync state packet with current scenario name and luck multiplier
        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p != null) {
                ChangedExtrasNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> p),
                        new DomainStatePacket(
                                true,
                                domain.casterUUID,
                                domain.casterName,
                                false,
                                domain.currentScenarioIndex,
                                chosen.getDisplayName(),
                                multiplier,
                                false,
                                false
                        )
                );
            }
        }
    }

    /**
     * Ends the current scenario as a jackpot: the Snep reached the end of its route. The jackpot
     * fanfare plays and the domain collapses after a short beat (see the JACKPOT branch of
     * onServerTick).
     */
    private void beginJackpot(ActiveDomain domain, ServerLevel level) {
        domain.phase = DomainPhase.JACKPOT;
        domain.phaseTicks = 0;
        domain.settleTicks = -1;

        Scenario scenario = domain.currentScenario;
        broadcastDomainMessage(level, domain,
                "§6§l★ JACKPOT! " + domain.casterName + " reached the end of "
                        + (scenario != null ? scenario.getDisplayName() : "the scenario") + "! ★");

        Player caster = level.getPlayerByUUID(domain.casterUUID);
        if (caster != null) {
            ChangedExtrasAbilities.activateJackpot(caster);
        }
    }

    /**
     * Ends the current scenario as a failure after its Snep has stopped on a red marker or finished
     * without winning. Clears the scenario NBT structure, resets caster visual ability NBT,
     * teleports trapped entities safely to the arena center floor, and begins the intermission.
     */
    private void finishFailedScenario(ActiveDomain domain, ServerLevel level) {
        Scenario scenario = domain.currentScenario;
        broadcastDomainMessage(level, domain,
                "§c§lFailed: " + (scenario != null ? scenario.getDescription() : ""));

        domain.currentScenarioIndex++;
        if (domain.currentScenarioIndex >= MAX_SCENARIOS) {
            broadcastDomainMessage(level, domain, "§c§lThe Domain breaks at the 6th scenario without a Jackpot!");
            collapseDomain(domain, level, false, true);
            return;
        }

        // Enter the intermission: the caster must use 2 visual abilities to continue
        domain.phase = DomainPhase.BREAK;
        domain.phaseTicks = 0;
        domain.breakParriesCount = 0;

        // 1. Clear the scenario build (NBT structure) from the arena so the arena is cleared
        clearScenarioStructure(level, domain);

        // 2. Clear visual abilities / parry NBT tags on the caster
        Player caster = level.getPlayerByUUID(domain.casterUUID);
        if (caster != null) {
            caster.getPersistentData().remove(ParryAbility.VISUAL_ABILITIES_COUNTER_TAG);
        }

        // 3. Teleport trapped players safely onto the arena floor
        teleportTrappedToArenaCenter(domain, level);

        broadcastDomainMessage(level, domain,
                "§e§lIntermission started: " + domain.casterName + " must use " + REQUIRED_VISUAL_ABILITIES
                        + " visual abilities to enter Scenario " + domain.currentScenarioIndex + "!");

        // Sync state packet
        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p != null) {
                ChangedExtrasNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> p),
                        new DomainStatePacket(
                                true,
                                domain.casterUUID,
                                domain.casterName,
                                false,
                                domain.currentScenarioIndex,
                                "Intermission",
                                LUCK_MULTIPLIERS[Math.min(domain.currentScenarioIndex - 1, 4)],
                                false,
                                false
                        )
                );
            }
        }
    }

    /**
     * Clears the previously pasted scenario build (if any) and pastes the chosen scenario's
     * build in the centre of the arena. The built structures live at
     * data/changedextras/structures/domain_scenarios/scenario_N.nbt.
     */
    private void placeScenarioStructure(ServerLevel level, ActiveDomain domain, Scenario scenario) {
        clearScenarioStructure(level, domain);

        StructureTemplateManager manager = level.getStructureManager();
        ResourceLocation id = new ResourceLocation(ChangedExtras.MODID, scenario.getStructureId());
        Optional<StructureTemplate> optional = manager.get(id);
        if (optional.isEmpty()) {
            LOGGER.warn("[DomainExpansionManager] Scenario structure {} is missing", id);
            return;
        }

        StructureTemplate template = optional.get();
        Vec3i size = template.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
            // Empty placeholder: nothing to paste yet
            return;
        }

        // Paste the build so its lowest layer (the stone/floor layer) sits on the arena floor at
        // centre.y - 1. This keeps the build's floor under the players instead of spawning it at
        // head height, which would crush anyone standing in the arena.
        BlockPos origin = domain.center.offset(
                -size.getX() / 2,
                -1,
                -size.getZ() / 2);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(true)
                .setKeepLiquids(false);

        template.placeInWorld(level, origin, origin, settings, level.random, 2);

        domain.scenarioOrigin = origin;
        domain.scenarioSize = size;

        // The build marks the Snep's path with route / failed-route blocks; harvest them,
        // replace them with air and hand the recorded path to a freshly spawned Snep.
        collectRouteAndSpawnSnep(level, domain, origin, size);
    }

    /**
     * Scans the freshly pasted scenario build for {@code changedextras:route} (green) and
     * {@code changedextras:failed_route} (red) marker blocks. Every marker is replaced with air,
     * its position is recorded, and a Snep is spawned to walk the path. If the build contains no
     * markers at all, no Snep is spawned. A route marker right-clicked as the start (see
     * {@link RouteBlock#START}) becomes the first waypoint, so the Snep spawns there.
     */
    private void collectRouteAndSpawnSnep(ServerLevel level, ActiveDomain domain, BlockPos origin, Vec3i size) {
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos markedStart = null;

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(ChangedExtras.ROUTE.get())) {
                        // A route marker right-clicked as the start is where the Snep spawns and begins.
                        if (markedStart == null && state.getValue(RouteBlock.START)) {
                            markedStart = pos;
                        }
                        domain.snepRoute.add(pos);
                        level.setBlock(pos, air, 2);
                    } else if (state.is(ChangedExtras.FAILED_ROUTE.get())) {
                        domain.snepRoute.add(pos);
                        domain.snepFailedRoute.add(pos.asLong());
                        level.setBlock(pos, air, 2);
                    }
                }
            }
        }

        // No route markers in this build -> no Snep appears.
        if (domain.snepRoute.isEmpty()) {
            return;
        }

        // If the builder marked a start marker, make it the first waypoint (the Snep's spawn point).
        if (markedStart != null) {
            domain.snepRoute.remove(markedStart);
            domain.snepRoute.add(0, markedStart);
        }

        SnepEntity snep = ModEntities.SNEP.get().create(level);
        if (snep == null) {
            LOGGER.warn("[DomainExpansionManager] Failed to create Snep for scenario");
            return;
        }

        BlockPos start = domain.snepRoute.get(0);
        snep.moveTo(start.getX() + 0.5D, start.getY(), start.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F, 0.0F);
        snep.setRoute(domain.snepRoute, domain.snepFailedRoute);
        level.addFreshEntity(snep);
        domain.snep = snep;
    }

    private void clearScenarioStructure(ServerLevel level, ActiveDomain domain) {
        if (domain.snep != null) {
            domain.snep.discard();
            domain.snep = null;
        }
        domain.snepRoute.clear();
        domain.snepFailedRoute.clear();

        if (domain.scenarioOrigin == null || domain.scenarioSize == null) return;

        BlockPos origin = domain.scenarioOrigin;
        Vec3i size = domain.scenarioSize;
        BlockState air = Blocks.AIR.defaultBlockState();
        // The arena floor sits at centre.y - 1; the scenario build replaces it with its own floor
        // layer. Restore the barrier there instead of clearing it to air so the arena stays solid.
        int floorY = domain.center.getY() - 1;
        BlockState floor = ChangedExtras.DOMAIN_BARRIER.get().defaultBlockState();

        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, pos.getY() == floorY ? floor : air, 2);
                    }
                }
            }
        }

        domain.scenarioOrigin = null;
        domain.scenarioSize = null;
    }

    public void collapseDomain(ActiveDomain domain, ServerLevel level, boolean isJackpot, boolean isBroken) {
        if (domain.phase == DomainPhase.COLLAPSED) return;
        domain.phase = DomainPhase.COLLAPSED;

        LOGGER.info("[DomainExpansionManager] Collapsing domain for {}, isJackpot={}, isBroken={}", domain.casterName, isJackpot, isBroken);

        // 1. Break / Destroy the 16-block sphere barrier, restore original blocks
        for (Map.Entry<BlockPos, BlockState> entry : domain.originalBlockStates.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 3);
        }
        domain.originalBlockStates.clear();
        domain.activeBarrierPositions.clear();

        // 1b. Remove the scenario build that was pasted into the arena
        clearScenarioStructure(level, domain);

        // 2. Play the domain shatter sound for everyone the moment they are sent back (see below).
        //    Playing it here, before the teleport, gets cut off by the dimension change.

        // 3. Release the intro root effect, clear blindness and white overlay from all affected players, stop jackpot_song
        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p != null) {
                clearRootSlow(p);
                p.removeEffect(MobEffects.BLINDNESS);
                ChangedExtrasNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> p),
                        new DomainStatePacket(
                                false,
                                domain.casterUUID,
                                domain.casterName,
                                false,
                                domain.currentScenarioIndex,
                                "",
                                1.0f,
                                isJackpot,
                                isBroken
                        )
                );
            }
        }

        // 4. Release everyone trapped inside the domain back to where they came from
        for (TrappedEntity trapped : domain.trappedEntities) {
            Entity entity = trapped.entity;
            if (entity == null || entity.isRemoved()) continue;

            boolean isPlayer = entity instanceof ServerPlayer;
            if (!isPlayer) {
                EntityFreezeManager.unfreeze(entity.getUUID());
            }

            ServerLevel originLevel = level.getServer().getLevel(trapped.originDimension);
            if (originLevel == null) continue;

            entity.changeDimension(originLevel,
                    new DomainTeleporter(trapped.originPosition, false));

            // Play the shatter sound now that the player is back in their own world, so the
            // dimension change no longer cuts it off.
            if (isPlayer) {
                originLevel.playSound(null,
                        trapped.originPosition.x, trapped.originPosition.y, trapped.originPosition.z,
                        ChangedExtrasSounds.DOMAIN_BREAK.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            }
        }
        domain.trappedEntities.clear();

        // Reset domain state
        activeDomainsByCaster.remove(domain.casterUUID);
    }

    /**
     * Plays the scenario swap sting and triggers the white screen flash for everyone inside the domain.
     */
    private void broadcastScenarioSwap(ServerLevel level, ActiveDomain domain) {
        level.playSound(null,
                domain.center.getX() + 0.5, domain.center.getY() + 0.5, domain.center.getZ() + 0.5,
                ChangedExtrasSounds.SCENARIO_SWAP.get(), SoundSource.PLAYERS, 4.0F, 1.0F);

        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p != null) {
                ChangedExtrasNetwork.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> p),
                        new DomainFlashPacket()
                );
            }
        }
    }

    private void broadcastDomainMessage(ServerLevel level, ActiveDomain domain, String msg) {
        Component comp = Component.literal(msg);
        for (UUID pid : domain.playersInSphere) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(pid);
            if (p != null) {
                p.displayClientMessage(comp, false);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer().level() instanceof ServerLevel level)) return;

        // Mining the outer barrier of a running domain breaches it, collapsing the domain
        if (event.getState().is(ChangedExtras.DOMAIN_BARRIER.get())) {
            List<ActiveDomain> breached = new ArrayList<>();
            for (ActiveDomain domain : INSTANCE.activeDomainsByCaster.values()) {
                if (domain.dimension.equals(level.dimension())) {
                    breached.add(domain);
                }
            }
            for (ActiveDomain domain : breached) {
                INSTANCE.collapseDomain(domain, level, false, true);
            }
            return;
        }

        // Everything else inside an active domain is protected
        if (INSTANCE.isInsideActiveDomain(level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        DomainExpansionManager mgr = INSTANCE;

        // Finish any domain opens whose 2-second voice-line delay has elapsed.
        Iterator<PendingOpen> pendingIterator = mgr.pendingOpens.iterator();
        while (pendingIterator.hasNext()) {
            PendingOpen pending = pendingIterator.next();
            if (--pending.ticksRemaining > 0) {
                continue;
            }
            pendingIterator.remove();
            mgr.finishOpeningDomain(pending);
        }

        for (ActiveDomain domain : mgr.activeDomainsByCaster.values()) {
            ServerLevel level = event.getServer().getLevel(domain.dimension);
            if (level == null) continue;

            Player caster = level.getPlayerByUUID(domain.casterUUID);
            // If caster died or disconnected, break sphere, clear overlay, stop song, reset
            if (caster == null || !caster.isAlive() || caster.isDeadOrDying()) {
                mgr.collapseDomain(domain, level, false, true);
                continue;
            }

            // Spawn pending patches every few ticks until the white sphere is completely formed
            if (domain.currentPatchIndex < domain.pendingPatches.size()) {
                domain.patchTimer--;
                if (domain.patchTimer <= 0) {
                    domain.patchTimer = domain.patchDelayTicks;
                    mgr.spawnNextPatch(level, domain);
                }
            }

            domain.phaseTicks++;

            // Phase machine
            if (domain.phase == DomainPhase.INTRO) {
                // Intro white overlay holds for INTRO_DURATION_TICKS, then advances and triggers fade out
                if (domain.phaseTicks >= INTRO_DURATION_TICKS) {
                    mgr.advanceFromIntro(domain, level);
                }
            } else if (domain.phase == DomainPhase.SCENARIO) {
                // SCENARIO phase: ends when its Snep finishes its route, then holds for 2 extra seconds (40 ticks)
                if (domain.settleTicks < 0) {
                    boolean snepDone = domain.snep == null || domain.snep.isFinishedRoute();
                    if (snepDone || domain.phaseTicks >= SCENARIO_ROUTE_TIMEOUT_TICKS) {
                        if (domain.scenarioWon) {
                            mgr.beginJackpot(domain, level);
                        } else {
                            // Hold the failed pose for 2 seconds before the intermission.
                            domain.settleTicks = SCENARIO_SETTLE_TICKS;
                        }
                    }
                } else if (--domain.settleTicks <= 0) {
                    mgr.finishFailedScenario(domain, level);
                }
            } else if (domain.phase == DomainPhase.JACKPOT) {
                // The Snep has already reached the end; the jackpot fanfare plays for a beat
                // (2 seconds) before the domain breaks.
                if (domain.phaseTicks >= SCENARIO_SETTLE_TICKS) {
                    mgr.collapseDomain(domain, level, true, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        // Everyone held still inside the domain intro is invulnerable until it ends
        if (INSTANCE.isTrappedDuringIntro(entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            ActiveDomain domain = INSTANCE.activeDomainsByCaster.get(player.getUUID());
            if (domain != null && player.level() instanceof ServerLevel serverLevel) {
                INSTANCE.collapseDomain(domain, serverLevel, false, true);
            }
        }
    }
}
