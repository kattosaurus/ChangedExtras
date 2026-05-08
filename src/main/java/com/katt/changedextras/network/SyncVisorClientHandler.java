package com.katt.changedextras.network;

import com.katt.changedextras.common.ExoskeletonVisorColorHolder;
import com.katt.changedextras.common.ExoskeletonVisorStyle;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class SyncVisorClientHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handlePacket(SyncVisorPacket msg, Supplier<NetworkEvent.Context> ctx) {
        LOGGER.info("SyncVisorPacket received for UUID {} patternId {}", msg.getEntityUUID(), msg.getPatternId());
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        // Build data first
        String patternId = msg.getPatternId();
        ExoskeletonVisorStyle.Pattern pattern = ExoskeletonVisorStyle.Pattern.byId(patternId);
        if (pattern == null) pattern = ExoskeletonVisorStyle.Pattern.PATTERN2;
        ExoskeletonVisorStyle.Data data = new ExoskeletonVisorStyle.Data(pattern, msg.getPrimaryColor(), msg.getSecondaryColor(), msg.isCustomColors());

        // Try player first (most common case)
        Player target = level.getPlayerByUUID(msg.getEntityUUID());
        if (target != null) {
            // Write into persistent data so renderers that use readWornData() pick it up
            CompoundTag persistent = target.getPersistentData();
            ExoskeletonVisorStyle.write(persistent, data);
            LOGGER.info("Wrote visor style to local player persistent data: pattern={} primary={} secondary={} custom={}", data.pattern(), data.primaryColor(), data.secondaryColor(), data.customColors());
            return;
        }

        // Fallback: look for any loaded entity by UUID (covers Exoskeleton NPCs)
        for (Entity entity : level.entitiesForRendering()) {
            if (entity.getUUID().equals(msg.getEntityUUID())) {
                if (entity instanceof Exoskeleton exoskeleton) {
                    // Update the synced entity data directly
                    ExoskeletonVisorColorHolder holder = (ExoskeletonVisorColorHolder) exoskeleton;
                    holder.changedextras$setVisorPattern(data.pattern());
                    holder.changedextras$setVisorColor(data.primaryColor());
                    holder.changedextras$setVisorSecondaryColor(data.secondaryColor());
                    holder.changedextras$setCustomVisorColors(data.customColors());
                    LOGGER.info("Updated exoskeleton entity visor data for {}", entity.getUUID());
                } else if (entity instanceof LivingEntity living) {
                    ExoskeletonVisorStyle.write(living.getPersistentData(), data);
                    LOGGER.info("Wrote visor style to living entity persistent data for {}", entity.getUUID());
                }
                return;
            }
        }
    }
}