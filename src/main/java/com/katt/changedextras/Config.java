package com.katt.changedextras;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    /** Fewest rooms a Biological Studies Facility will try to generate. Also the floor of the max-rooms setting. */
    public static final int BIOLOGICAL_FACILITY_MIN_ROOMS = 10;
    /** Default value of the max-rooms setting. */
    public static final int BIOLOGICAL_FACILITY_DEFAULT_MAX_ROOMS = 50;
    /** Highest value the max-rooms setting can be raised to. */
    public static final int BIOLOGICAL_FACILITY_ABSOLUTE_MAX_ROOMS = 100;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue SMART_LATEX_AI_ENABLED = BUILDER
            .comment("When enabled, Changed Extras latex creatures use the smart AI behavior package.")
            .define("smartLatexAiEnabled", false);

    private static final ForgeConfigSpec.IntValue LATEX_ATTACKER_MEMORY_TICKS = BUILDER
            .comment("How long smart latex creatures remember and pursue a player after being attacked or otherwise acquiring a target. Set to 0 to disable attacker memory.")
            .defineInRange("latexAttackerMemoryTicks", 160, 0, 20 * 60 * 30);

    private static final ForgeConfigSpec.BooleanValue LATEX_ALWAYS_ATTACK_PLAYERS = BUILDER
            .comment("When enabled, latex creatures always attack players on sight, including players that are already "
                    + "transfurred (infected). Creative and spectator players, and the owner of a tame latex, are still ignored.")
            .define("latexAlwaysAttackPlayers", false);

    private static final ForgeConfigSpec.BooleanValue USE_CUSTOM_DEATH_SCREEN = BUILDER
            .comment("Determines if the custom death screen with the death messages is used")
            .define("useCustomDeathScreen", false);

    private static final ForgeConfigSpec.IntValue BIOLOGICAL_FACILITY_MAX_ROOMS = BUILDER
            .comment("The maximum number of rooms that can generate in a Biological Studies Facility. A facility always aims for at least "
                    + BIOLOGICAL_FACILITY_MIN_ROOMS + " rooms, so this cannot be set lower than that.")
            .defineInRange("biologicalFacilityMaxRooms", BIOLOGICAL_FACILITY_DEFAULT_MAX_ROOMS,
                    BIOLOGICAL_FACILITY_MIN_ROOMS, BIOLOGICAL_FACILITY_ABSOLUTE_MAX_ROOMS);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean smartLatexAiEnabled = false;
    public static boolean useCustomDeathScreen = false;
    public static boolean latexAlwaysAttackPlayers = false;
    public static int latexAttackerMemoryTicks = 160;
    public static int biologicalFacilityMaxRooms = BIOLOGICAL_FACILITY_DEFAULT_MAX_ROOMS;

    private Config() {
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }

        smartLatexAiEnabled = SMART_LATEX_AI_ENABLED.get();
        latexAttackerMemoryTicks = LATEX_ATTACKER_MEMORY_TICKS.get();
        latexAlwaysAttackPlayers = LATEX_ALWAYS_ATTACK_PLAYERS.get();
        useCustomDeathScreen = USE_CUSTOM_DEATH_SCREEN.get();
        biologicalFacilityMaxRooms = BIOLOGICAL_FACILITY_MAX_ROOMS.get();
    }
}