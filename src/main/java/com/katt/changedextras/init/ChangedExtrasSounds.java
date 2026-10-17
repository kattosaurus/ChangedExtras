package com.katt.changedextras.init;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChangedExtrasSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ChangedExtras.MODID);

    /**
     * Voice line / sting played the moment a Domain Expansion opens.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/domain_expansion.ogg
     */
    public static final RegistryObject<SoundEvent> DOMAIN_EXPANSION =
            REGISTRY.register("domain_expansion", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "domain_expansion")));

    /**
     * Sting played when the domain swaps to a new scenario.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/scenario_swap.ogg
     */
    public static final RegistryObject<SoundEvent> SCENARIO_SWAP =
            REGISTRY.register("scenario_swap", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "scenario_swap")));

    /**
     * Sting played when a Domain Expansion shatters.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/domain_break.ogg
     */
    public static final RegistryObject<SoundEvent> DOMAIN_BREAK =
            REGISTRY.register("domain_break", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "domain_break")));

    /**
     * The looping song that plays when Jackpot is active.
     * Provide your audio file at:
     *   src/main/resources/assets/changedextras/sounds/jackpot_song.ogg
     */
    public static final RegistryObject<SoundEvent> JACKPOT_SONG =
            REGISTRY.register("jackpot_song", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "jackpot_song")));

    /**
     * The looping song that plays while Jackpot is active.
     * Provide your audio file at:
     *   src/main/resources/assets/changedextras/sounds/jackpot_loop.ogg
     */
    public static final RegistryObject<SoundEvent> JACKPOT_LOOP =
            REGISTRY.register("jackpot_loop", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "jackpot_loop")));

    /**
     * Short "disc stop" sound when Jackpot ends.
     * Provide your audio file at:
     *   src/main/resources/assets/changedextras/sounds/jackpot_stop.ogg
     */
    public static final RegistryObject<SoundEvent> JACKPOT_STOP =
            REGISTRY.register("jackpot_stop", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "jackpot_stop")));

    /**
     * The looping song that plays during the Artist boss fight.
     * Provide your audio file at:
     *   src/main/resources/assets/changedextras/sounds/artist_theme.ogg
     */
    public static final RegistryObject<SoundEvent> ARTIST_THEME =
            REGISTRY.register("artist_theme", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "artist_theme")));

    /**
     * Sound played when successfully parrying an attack.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/parry.ogg
     */
    public static final RegistryObject<SoundEvent> PARRY =
            REGISTRY.register("parry", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "parry")));

    /**
     * Heartbeat buildup sound played while spamming parry for Jackpot.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/heartbeat.ogg
     */
    public static final RegistryObject<SoundEvent> HEARTBEAT =
            REGISTRY.register("heartbeat", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "heartbeat")));

    /**
     * Scream sound played by the Artist when entering her second phase.
     * Audio file at:
     *   src/main/resources/assets/changedextras/sounds/scream.ogg
     */
    public static final RegistryObject<SoundEvent> SCREAM =
            REGISTRY.register("scream", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "scream")));

    private ChangedExtrasSounds() {}
}
