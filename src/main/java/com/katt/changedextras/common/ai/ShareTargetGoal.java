package com.katt.changedextras.common.ai;

import com.katt.changedextras.common.ChangedExtrasGameRules;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class ShareTargetGoal extends Goal {
    private static final Set<String> WHITE_WOLF_GROUP = Set.of(
            "changed:pure_white_latex_wolf",
            "changed:pure_white_latex_wolf_pup",
            "changed:white_latex_wolf_male",
            "changed:white_latex_wolf_female",
            "changed:white_wolf_male",
            "changed:white_wolf_female"
    );
    private static final Set<String> DARK_WOLF_GROUP = Set.of(
            "changed:dark_latex_wolf_male",
            "changed:dark_latex_wolf_female",
            "changed:dark_latex_wolf_pup"
    );

    private final ChangedEntity mob;
    private final double radius;
    private final int cooldownTicks;
    private int nextShareTick;

    public ShareTargetGoal(ChangedEntity mob, double radius, int cooldownTicks) {
        this.mob = mob;
        this.radius = radius;
        this.cooldownTicks = cooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!ChangedExtrasGameRules.isSmartLatexAiEnabled(mob.level().getGameRules())) {
            return false;
        }

        LivingEntity target = mob.getTarget();
        return target != null
                && target.isAlive()
                && !(target instanceof ChangedEntity)
                && !(target instanceof net.minecraft.world.entity.player.Player player
                && LatexAiUtil.isPlayerTransfurred(player)
                && !LatexAiUtil.areHostileLatexFactions(mob, player))
                && mob.tickCount >= nextShareTick;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }

        nextShareTick = mob.tickCount + cooldownTicks;

        var box = mob.getBoundingBox().inflate(radius);
        for (ChangedEntity ally : mob.level().getEntitiesOfClass(ChangedEntity.class, box, entity -> entity != mob && entity.isAlive())) {
            if (!shouldShareTarget(mob, ally)) {
                continue;
            }

            ally.setTarget(target);
            LatexMind allyMind = LatexMindStore.get(ally);
            allyMind.remember(target, ally.tickCount, ally.hasLineOfSight(target));
        }
    }

    public static boolean shouldShareTarget(ChangedEntity source, ChangedEntity ally) {
        String sourceForm = getFormId(source);
        String allyForm = getFormId(ally);
        if (sourceForm == null || allyForm == null) {
            return source.getType() == ally.getType();
        }

        if (WHITE_WOLF_GROUP.contains(sourceForm)) {
            return WHITE_WOLF_GROUP.contains(allyForm);
        }

        if (DARK_WOLF_GROUP.contains(sourceForm)) {
            return DARK_WOLF_GROUP.contains(allyForm);
        }

        return sourceForm.equals(allyForm);
    }

    @Nullable
    private static String getFormId(ChangedEntity entity) {
        TransfurVariant<?> variant = TransfurVariant.findEntityTransfurVariant(entity);
        if (variant == null) {
            return null;
        }

        ResourceLocation formId = variant.getFormId();
        return formId != null ? formId.toString() : null;
    }
}
