package com.katt.changedextras.ability;

/**
 * Exposes the client-side grab reach timer added to Changed's grab ability.
 *
 * <p>This interface lives outside the mixin package on purpose: Mixin forbids
 * linking classes that share a package with a registered mixin config, so the
 * animation layer cannot reference it from {@code com.katt.changedextras.mixin}.</p>
 */
public interface GrabReachAccess {
    /**
     * Number of ticks the reach animation lasts before the grab is attempted.
     * Matches the 0.5s {@code Attack2} strike used for the reach.
     */
    int REACH_TICKS = 10;

    int changedextras$getGrabReachTicks();

    void changedextras$setGrabReachTicks(int ticks);

    default boolean changedextras$isGrabReaching() {
        return this.changedextras$getGrabReachTicks() > 0;
    }
}
