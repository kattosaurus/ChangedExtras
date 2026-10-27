package com.katt.changedextras.worldgen.biome;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public final class BlueCaveSurfaceRules {
    private static final int SOLID_HEIGHT_LIMIT = 0;

    private static final int FADE_DURATION = 10;

    private static final VerticalAnchor TRANSITION_START = VerticalAnchor.absolute(SOLID_HEIGHT_LIMIT);
    private static final VerticalAnchor TRANSITION_END = VerticalAnchor.absolute(SOLID_HEIGHT_LIMIT + FADE_DURATION);

    private static final SurfaceRules.ConditionSource BLUE_STONE_GRADIENT = SurfaceRules.verticalGradient(
            "blue_stone_fade",
            TRANSITION_START,
            TRANSITION_END
    );

    private static final SurfaceRules.ConditionSource BLUE_STONE_SOLID = SurfaceRules.yBlockCheck(
            TRANSITION_START,
            -1
    );

    private BlueCaveSurfaceRules() {
    }

    public static SurfaceRules.RuleSource wrap(SurfaceRules.RuleSource original) {
        if (original == null) {
            return null;
        }

        SurfaceRules.RuleSource blueStone = SurfaceRules.state(
                ChangedExtras.BLUE_STONE_BLOCK.get().defaultBlockState()
        );

        SurfaceRules.RuleSource blueCaveRules = SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.BLUE_CAVE),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(BLUE_STONE_GRADIENT, blueStone),
                        SurfaceRules.ifTrue(BLUE_STONE_SOLID, blueStone)
                )
        );

        return SurfaceRules.sequence(blueCaveRules, original);
    }
}
