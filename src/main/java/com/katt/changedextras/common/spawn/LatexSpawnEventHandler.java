package com.katt.changedextras.common.spawn;

import com.katt.changedextras.ChangedExtras;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LatexSpawnEventHandler {
    private LatexSpawnEventHandler() {
    }

    @SubscribeEvent
    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (!(event.getEntity() instanceof ChangedEntity changedEntity)) {
            return;
        }

        if (!LatexSpawnRules.isNaturalSpawn(event.getSpawnType())) {
            return;
        }

        if (!LatexSpawnRules.isVariantEnabled(event.getLevel().getLevel().getServer(), changedEntity.getType())) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if (LatexSpawnRules.shouldOverrideToDaySpawn(event.getLevel(), event.getSpawnType())
                && passesDaySpawnRules(changedEntity.getType(), event.getLevel(), event.getSpawnType(), changedEntity.blockPosition(), event.getLevel().getRandom())) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof ChangedEntity changedEntity)) {
            return;
        }

        if (!LatexSpawnRules.isNaturalSpawn(event.getSpawnType())) {
            return;
        }

        if (!LatexSpawnRules.isVariantEnabled(event.getLevel().getLevel().getServer(), changedEntity.getType())) {
            event.setSpawnCancelled(true);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean passesDaySpawnRules(EntityType<?> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return LatexSpawnRules.checkDaySpawnRules((EntityType<? extends ChangedEntity>) entityType, level, spawnType, pos, random);
    }
}
