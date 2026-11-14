package com.katt.changedextras.common.combat;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks the three-hit attack combo used by the shared animation layer and the
 * server-side lunge on the final hit.
 *
 * <p>The client and server keep separate state because an integrated server shares
 * this class with the client. A combo advances once per new swing and falls back to
 * the first hit when no swing has happened for a short while.</p>
 *
 * <p>Swinging faster than the previous clip lasted is treated as spam. Spam swings
 * use the vanilla preset animations and never receive the final-hit boost, so the
 * {@code Attack2} clip and its lunge can only happen together.</p>
 */
public final class AttackComboTracker {
    public static final int COMBO_SIZE = 3;

    /** The finisher hits twice as hard as the two opening strikes. */
    public static final float FINISHER_DAMAGE_MULTIPLIER = 2.0F;

    /** Maximum gap between hits while chaining Attack - Attack1 - Attack2. */
    private static final int COMBO_CONTINUE_TICKS = 40;
    /** Cooldown after Attack2 before a new combo can start. */
    private static final int COMBO_COOLDOWN_TICKS = 28;
    /**
     * Minimum gap after Attack/Attack1. Those clips are 0.2083s, which is a
     * little over four ticks, so anything faster is a spam click.
     */
    private static final int FAST_HIT_TICKS = 5;
    /** Quiet time required to leave spam mode after the last spam swing. */
    private static final int SPAM_RELEASE_TICKS = 10;
    private static final int PRUNE_INTERVAL_TICKS = 200;
    private static final float MILLIS_PER_TICK = 50.0F;
    private static final long ATTACK2_TICKS = 10L;

    private static final Map<UUID, State> CLIENT_STATE = new HashMap<>();
    private static final Map<UUID, State> SERVER_STATE = new HashMap<>();

    private static long clientPrunedAt = 0L;
    private static long serverPrunedAt = 0L;

    private AttackComboTracker() {
    }

    /**
     * Advances the client combo for the swing currently being rendered and reports
     * which clip to play. {@code combo} is {@code 0} for Attack, {@code 1} for
     * Attack1 and {@code 2} for Attack2.
     *
     * <p>{@code elapsedMillis} is the time since that swing started. It keeps
     * counting past the vanilla swing duration so the 0.5s {@code Attack2} clip can
     * finish instead of being cut off when {@code attackTime} reaches zero.</p>
     */
    public static ClientAttack clientAttack(ChangedEntity entity, float ageInTicks) {
        tickClient(entity);

        UUID id = key(entity);
        long now = entity.level().getGameTime();

        State state = CLIENT_STATE.get(id);
        if (state == null) {
            return new ClientAttack(0, 0L, false);
        }

        if (now < clientPrunedAt || now - clientPrunedAt > PRUNE_INTERVAL_TICKS) {
            clientPrunedAt = now;
            prune(CLIENT_STATE, now);
        }

        if (state.spam) {
            return new ClientAttack(0, 0L, true);
        }

        int combo = Math.max(0, state.displayCombo);
        long elapsedMillis = 0L;
        if (combo == COMBO_SIZE - 1) {
            // ageInTicks is tickCount + partialTick, so its fraction smooths the
            // clip between ticks just like the renderer's own interpolation.
            float partial = ageInTicks - Mth.floor(ageInTicks);
            long ticks = state.displayStartTick == Long.MIN_VALUE
                    ? 0L
                    : Math.max(0L, now - state.displayStartTick);
            elapsedMillis = Math.max(0L, (long) ((ticks + partial) * MILLIS_PER_TICK));
        }

        return new ClientAttack(combo, elapsedMillis, false);
    }

    /**
     * Advances the client display state from the entity's swing. This is driven by
     * the client tick rather than only by rendering, so first-person play and
     * off-screen entities cannot fall behind the server.
     */
    public static void tickClient(ChangedEntity entity) {
        if (entity == null || entity.level() == null) {
            return;
        }

        UUID id = key(entity);
        long now = entity.level().getGameTime();
        State state = CLIENT_STATE.computeIfAbsent(id, ignored -> new State());
        state.clearExpiredSpam(now);

        boolean newSwing = state.acceptSwing(entity.swinging, entity.swingTime);
        if (state.confirmPending) {
            // The server already reported this swing as the finisher, so the
            // first swing seen after the packet is that same swing, not a new hit.
            state.confirmPending = false;
            if (newSwing) {
                return;
            }
        }

        if (!newSwing) {
            return;
        }

        Outcome outcome = state.advance(now);
        state.displayStartTick = now;
        if (outcome.spam()) {
            state.displayCombo = -1;
        } else {
            int display = outcome.combo();
            if (display >= COMBO_SIZE - 1) {
                // Wait for the server's confirmation before showing the finisher,
                // so Attack2 can never play without the boost that goes with it.
                display = COMBO_SIZE - 2;
            }
            state.displayCombo = display;
        }
    }

    /**
     * Applies the server's authoritative combo index for a confirmed final hit.
     * The server only sends this when it actually applies the boost, which keeps
     * {@code Attack2} and the lunge in sync even when client and server see the
     * swing on slightly different ticks.
     */
    public static void applyServerCombo(UUID id, int combo, long serverTick, long clientTickNow) {
        if (id == null || combo < COMBO_SIZE - 1) {
            return;
        }

        State state = CLIENT_STATE.computeIfAbsent(id, ignored -> new State());
        state.spam = false;
        state.combo = combo;
        state.lastSwingTick = serverTick;
        state.displayCombo = combo;
        state.confirmPending = true;

        long elapsedTicks = clientTickNow - serverTick;
        if (elapsedTicks < 0L) {
            elapsedTicks = 0L;
        } else if (elapsedTicks > ATTACK2_TICKS) {
            elapsedTicks = ATTACK2_TICKS;
        }
        state.displayStartTick = clientTickNow - elapsedTicks;
    }

    /**
     * Advances the server combo for the entity's current swing and reports what the
     * server decided. Players and pure variants share the same tracker, keyed by
     * their own UUID.
     *
     * <p>When the hurt event has already counted the strike (which is the normal
     * path for a player, because the attack packet is handled before the swing
     * packet), this replays that decision instead of advancing again. That replay
     * is what applies the boost, sound and confirmation packet exactly once.</p>
     */
    public static ServerSwing serverSwing(LivingEntity entity) {
        UUID id = entity.getUUID();
        long now = entity.level().getGameTime();

        State state = SERVER_STATE.computeIfAbsent(id, ignored -> new State());
        state.clearExpiredSpam(now);

        ServerSwing result = ServerSwing.NONE;
        boolean newSwing = state.acceptSwing(entity.swinging, entity.swingTime);
        if (state.hasPendingHurt(now)) {
            result = state.takePendingHurt();
        } else if (newSwing) {
            Outcome outcome = state.advance(now);
            result = new ServerSwing(outcome.advanced(), outcome.combo(), outcome.spam());
        }

        if (now < serverPrunedAt || now - serverPrunedAt > PRUNE_INTERVAL_TICKS) {
            serverPrunedAt = now;
            prune(SERVER_STATE, now);
        }

        return result;
    }

    /**
     * Counts a bare-handed melee strike that dealt damage and reports whether it was
     * the finisher. The hurt event runs before the swing is visible for players, so
     * the strike is counted here; {@link #serverSwing} then replays the decision and
     * applies the boost/sound/packet, keeping the damage bonus and {@code Attack2}
     * paired.
     */
    public static boolean serverHurtStrike(LivingEntity entity) {
        if (!isBareHanded(entity)) {
            return false;
        }

        UUID id = entity.getUUID();
        long now = entity.level().getGameTime();

        State state = SERVER_STATE.computeIfAbsent(id, ignored -> new State());
        state.clearExpiredSpam(now);

        if (state.hasPendingHurt(now)) {
            // A second target from the same strike (sweep or multi-hit).
            return state.pendingIsThirdHit();
        }

        if (entity instanceof Player) {
            // The attack packet is handled after the level tick and before the swing
            // packet, so players never show a fresh swing here. Count it now; the
            // swing packet replays this decision instead of advancing again.
            Outcome outcome = state.advance(now);
            state.setPendingHurt(outcome, now);
            return outcome.advanced() && outcome.combo() == COMBO_SIZE - 1;
        }

        if (!state.acceptSwing(entity.swinging, entity.swingTime)) {
            // Mobs swing before they hurt. If the tick tracker already counted this
            // swing there is no new strike to count here.
            return false;
        }

        Outcome outcome = state.advance(now);
        state.setPendingHurt(outcome, now);
        return outcome.advanced() && outcome.combo() == COMBO_SIZE - 1;
    }

    /** Combos only build while the main hand is empty, so item swings stay vanilla. */
    public static boolean isBareHanded(LivingEntity entity) {
        return entity.getMainHandItem().isEmpty();
    }

    /** Drops the display state of one entity (e.g. after it picks up an item). */
    public static void resetClient(ChangedEntity entity) {
        CLIENT_STATE.remove(key(entity));
    }

    /** Drops the server combo of one entity (e.g. after it picks up an item). */
    public static void resetServer(LivingEntity entity) {
        SERVER_STATE.remove(entity.getUUID());
    }

    /** Clears all client display state (e.g. when leaving a world). */
    public static void clearClient() {
        CLIENT_STATE.clear();
        clientPrunedAt = 0L;
    }

    private static UUID key(ChangedEntity entity) {
        Player underlying = entity.getUnderlyingPlayer();
        return underlying != null ? underlying.getUUID() : entity.getUUID();
    }

    private static void prune(Map<UUID, State> states, long now) {
        states.entrySet().removeIf(entry -> {
            long lastSwing = entry.getValue().lastSwingTick;
            return lastSwing == Long.MIN_VALUE || now < lastSwing || now - lastSwing > PRUNE_INTERVAL_TICKS;
        });
    }

    /** The clip to render for the current client swing. */
    public record ClientAttack(int combo, long elapsedMillis, boolean spam) {
    }

    /** The server's decision for a swing. */
    public record ServerSwing(boolean advanced, int combo, boolean spam) {
        public static final ServerSwing NONE = new ServerSwing(false, -1, false);

        public boolean isThirdHit() {
            return this.advanced && this.combo == COMBO_SIZE - 1;
        }
    }

    private record Outcome(boolean advanced, int combo, boolean spam) {
        private static Outcome spamOutcome() {
            return new Outcome(false, -1, true);
        }
    }

    private static final class State {
        private boolean swinging;
        private int swingTime;
        private int combo = -1;
        private long lastSwingTick = Long.MIN_VALUE;
        private boolean spam;
        private long spamReleaseTick = Long.MIN_VALUE;
        private int displayCombo = -1;
        private long displayStartTick = Long.MIN_VALUE;
        private boolean confirmPending;
        private Outcome pendingHurtOutcome;
        private long pendingHurtTick = Long.MIN_VALUE;

        private boolean acceptSwing(boolean swingingNow, int swingTimeNow) {
            // swingTime is set to -1 when a new swing begins, then counts up. A new
            // swing is either the false -> true transition or a backwards jump.
            boolean newSwing = swingingNow && (!this.swinging || swingTimeNow < this.swingTime);
            this.swinging = swingingNow;
            this.swingTime = swingTimeNow;
            return newSwing;
        }

        private void clearExpiredSpam(long now) {
            if (this.spam && now >= this.spamReleaseTick) {
                this.spam = false;
                this.combo = -1;
            }
        }

        private boolean hasPendingHurt(long now) {
            // The hurt and the tick tracker can land in the same tick or one tick
            // apart, so the decision is kept for a single tick.
            return this.pendingHurtOutcome != null
                    && this.pendingHurtTick != Long.MIN_VALUE
                    && now >= this.pendingHurtTick
                    && now - this.pendingHurtTick <= 1L;
        }

        private void setPendingHurt(Outcome outcome, long now) {
            this.pendingHurtOutcome = outcome;
            this.pendingHurtTick = now;
        }

        private ServerSwing takePendingHurt() {
            Outcome outcome = this.pendingHurtOutcome;
            this.pendingHurtOutcome = null;
            this.pendingHurtTick = Long.MIN_VALUE;
            return outcome == null
                    ? ServerSwing.NONE
                    : new ServerSwing(outcome.advanced(), outcome.combo(), outcome.spam());
        }

        private boolean pendingIsThirdHit() {
            return this.pendingHurtOutcome != null
                    && this.pendingHurtOutcome.advanced()
                    && this.pendingHurtOutcome.combo() == COMBO_SIZE - 1;
        }

        /**
         * Advances the combo for one new swing. Swings made before the previous
         * clip has finished put the tracker into spam mode, which suppresses the
         * shared layer and the boost until the player pauses.
         */
        private Outcome advance(long now) {
            if (this.spam) {
                if (now < this.spamReleaseTick) {
                    this.spamReleaseTick = now + SPAM_RELEASE_TICKS;
                    this.lastSwingTick = now;
                    return Outcome.spamOutcome();
                }
                this.spam = false;
                this.combo = -1;
            }

            long gap = this.lastSwingTick == Long.MIN_VALUE || now < this.lastSwingTick
                    ? Long.MAX_VALUE
                    : now - this.lastSwingTick;

            int previous = this.combo;
            if (previous >= 0 && gap < requiredGap(previous)) {
                this.spam = true;
                this.spamReleaseTick = now + SPAM_RELEASE_TICKS;
                this.combo = -1;
                this.lastSwingTick = now;
                return Outcome.spamOutcome();
            }

            if (previous < 0 || previous >= COMBO_SIZE - 1 || gap > COMBO_CONTINUE_TICKS) {
                this.combo = 0;
            } else {
                this.combo = previous + 1;
            }

            this.lastSwingTick = now;
            return new Outcome(true, this.combo, false);
        }

        private static int requiredGap(int previous) {
            if (previous < 0) {
                return 0;
            }
            return previous >= COMBO_SIZE - 1 ? COMBO_COOLDOWN_TICKS : FAST_HIT_TICKS;
        }
    }
}
