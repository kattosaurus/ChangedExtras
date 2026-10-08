package com.katt.changedextras.common.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public final class PlayerAggroTargetGoal extends NearestAttackableTargetGoal<Player> {
    public PlayerAggroTargetGoal(ChangedEntity mob) {
        super(mob, Player.class, true, target -> LatexAiUtil.isAlwaysAggroPlayerTarget(mob, target));
    }
}
