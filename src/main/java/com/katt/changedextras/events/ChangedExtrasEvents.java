package com.katt.changedextras.events;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.ExoskeletonVisorStyle;
import com.katt.changedextras.entity.beasts.KattEntity;
import com.katt.changedextras.network.JackpotStatePacket;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.item.ExoskeletonItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = "changedextras", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangedExtrasEvents {

    private static final String NBT_TAG = "JackpotActive";
    public static final String JACKPOT_TICKS_TAG = "JackpotTicksRemaining";
    private static final String JACKPOT_SLOWNESS_TICKS_TAG = "JackpotSlownessTicksRemaining";
    private static final String JACKPOT_NAUSEA_TICKS_TAG = "JackpotNauseaTicksRemaining";
    public static final int JACKPOT_DURATION_TICKS = (4 * 60 + 11) * 20;
    private static final int JACKPOT_SLOWNESS_DURATION_TICKS = 2 * 60 * 20;
    private static final int JACKPOT_NAUSEA_DURATION_TICKS = 60 * 20;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();

        // Server side only
        if (living.level().isClientSide()) return;

        syncExoskeletonVisorColor(living);
        tickJackpot(living);
        tickJackpotAftermath(living);
    }

    private static void syncExoskeletonVisorColor(LivingEntity living) {
        Optional<Pair<ItemStack, ExoskeletonItem<?>>> optional = Exoskeleton.getEntityExoskeleton(living);
        if (optional.isEmpty()) {
            return;
        }

        ItemStack stack = optional.get().getFirst();
        CompoundTag persistentData = living.getPersistentData();
        ExoskeletonVisorStyle.Data stackData = ExoskeletonVisorStyle.read(stack);

        ExoskeletonVisorStyle.Data entityData;
        if (persistentData.contains(ExoskeletonVisorStyle.PATTERN_TAG) || persistentData.contains("visorPattern")) {
            entityData = ExoskeletonVisorStyle.read(persistentData);
            if (!entityData.equals(stackData)) {
                ExoskeletonVisorStyle.write(stack, entityData);
            }
        } else if (stack.getTag() != null && (stack.getTag().contains(ExoskeletonVisorStyle.PATTERN_TAG) || stack.getTag().contains("visorPattern"))) {
            ExoskeletonVisorStyle.write(persistentData, stackData);
            entityData = ExoskeletonVisorStyle.read(persistentData);
        } else {
            entityData = stackData;
        }

        // If the player is wearing the hypnosis pattern, attempt to temporarily
        // make Changed think they're a wolf for rendering without performing a
        // full transfur. Only apply when the player is not already transfurred.
        if (living instanceof ServerPlayer serverPlayer) {
            boolean isHypno = entityData.pattern() == ExoskeletonVisorStyle.Pattern.PATTERN1;
            boolean alreadyFooled = serverPlayer.getPersistentData().getBoolean("changedextras.fooled_wolf");
            try {
                if (isHypno && !net.ltxprogrammer.changed.process.ProcessTransfur.isPlayerTransfurred(serverPlayer) && !alreadyFooled) {
                    net.ltxprogrammer.changed.entity.variant.TransfurVariant<?> wolfVariant = findWolfVariant();
                    if (wolfVariant != null) {
                        net.ltxprogrammer.changed.process.ProcessTransfur.setPlayerTransfurVariant(serverPlayer, wolfVariant);
                        serverPlayer.getPersistentData().putBoolean("changedextras.fooled_wolf", true);
                        ChangedExtras.LOGGER.info("Temporarily fooled Changed into treating player {} as wolf variant {}", serverPlayer.getUUID(), wolfVariant == null ? "null" : wolfVariant.toString());
                    }
                } else if (!isHypno && alreadyFooled) {
                    // clear our temporary override if we had set it
                    net.ltxprogrammer.changed.process.ProcessTransfur.setPlayerTransfurVariant(serverPlayer, null);
                    serverPlayer.getPersistentData().remove("changedextras.fooled_wolf");
                    ChangedExtras.LOGGER.info("Restored original transfur state for player {}", serverPlayer.getUUID());
                }
            } catch (Throwable t) {
                ChangedExtras.LOGGER.warn("Failed to toggle temporary wolf variant for player: {}", t.getMessage());
            }
        }

        // Broadcast to clients so client-side renderers have the persistent data
        if (living.level() instanceof ServerLevel serverLevel) {
            com.katt.changedextras.network.SyncVisorPacket.broadcast(living, entityData);
        }
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static net.ltxprogrammer.changed.entity.variant.TransfurVariant<?> findWolfVariant() {
        // Preferred exact resource name we need so Changed triggers the hypnosis texture
        final String preferredResource = "form_latex_benign_wolf";

        String[] candidates = new String[]{
                "FORM_LATEX_BENIGN_WOLF",
                "FORM_BENIGN_WOLF",
                "FORM_WHITE_WOLF",
                "WHITE_WOLF_MALE",
                "WHITE_WOLF_FEMALE",
                "WHITE_LATEX_WOLF_MALE",
                "WHITE_LATEX_WOLF_FEMALE",
                "DARK_LATEX_WOLF_MALE",
                "DARK_LATEX_WOLF_FEMALE",
                "PURE_WHITE_LATEX_WOLF",
                "WHITE_WOLF",
                "dark_latex_wolf_male",
        };

        try {
            Class<?> cls = Class.forName("net.ltxprogrammer.changed.init.ChangedTransfurVariants");
            // First pass: try to find the preferred resource by inspecting variant.toString()
            for (java.lang.reflect.Field field : cls.getFields()) {
                Object val;
                try {
                    val = field.get(null);
                } catch (IllegalAccessException iae) {
                    continue;
                }
                if (val == null) continue;
                Object variant = null;
                try {
                    java.lang.reflect.Method m = val.getClass().getMethod("get");
                    try {
                        variant = m.invoke(val);
                    } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException ignored) {
                    }
                } catch (NoSuchMethodException ignored) {
                    if (val instanceof net.ltxprogrammer.changed.entity.variant.TransfurVariant) {
                        variant = val;
                    }
                }
                if (variant instanceof net.ltxprogrammer.changed.entity.variant.TransfurVariant) {
                    String s = variant.toString();
                    ChangedExtras.LOGGER.info("Inspecting ChangedTransfurVariants field {} -> variant {}", field.getName(), s);
                    if (s != null && s.contains(preferredResource)) {
                        ChangedExtras.LOGGER.info("Found preferred variant {} in field {}", s, field.getName());
                        return (net.ltxprogrammer.changed.entity.variant.TransfurVariant<?>) variant;
                    }
                }
            }

            // Second pass: fallback to a list of likely field names
            for (String name : candidates) {
                try {
                    java.lang.reflect.Field field = cls.getField(name);
                    Object val;
                    try {
                        val = field.get(null);
                    } catch (IllegalAccessException iae) {
                        // can't access this field, skip
                        continue;
                    }
                    if (val == null) continue;
                    // many registries expose RegistryObject or Supplier; try to call get()
                    try {
                        java.lang.reflect.Method m = val.getClass().getMethod("get");
                        Object variant;
                        try {
                            variant = m.invoke(val);
                        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                            // can't invoke get(), skip this candidate
                            continue;
                        }
                        if (variant != null && variant instanceof net.ltxprogrammer.changed.entity.variant.TransfurVariant) {
                            return (net.ltxprogrammer.changed.entity.variant.TransfurVariant<?>) variant;
                        }
                    } catch (NoSuchMethodException ignored) {
                        if (val instanceof net.ltxprogrammer.changed.entity.variant.TransfurVariant) {
                            return (net.ltxprogrammer.changed.entity.variant.TransfurVariant<?>) val;
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                    // try next candidate
                }
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    private static void tickJackpot(LivingEntity living) {
        if (!living.getPersistentData().getBoolean(NBT_TAG)) return;

        int ticksRemaining = living.getPersistentData().getInt(JACKPOT_TICKS_TAG);
        if (ticksRemaining <= 0) {
            endJackpot(living);
            return;
        }

        living.getPersistentData().putInt(JACKPOT_TICKS_TAG, ticksRemaining - 1);

        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30, 9, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 2, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, 1, false, false));

        if (living instanceof Player player) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0f);
        }
    }

    private static void endJackpot(LivingEntity living) {
        living.getPersistentData().putBoolean(NBT_TAG, false);
        living.getPersistentData().remove(JACKPOT_TICKS_TAG);
        living.getPersistentData().putInt(JACKPOT_SLOWNESS_TICKS_TAG, JACKPOT_SLOWNESS_DURATION_TICKS);
        living.getPersistentData().putInt(JACKPOT_NAUSEA_TICKS_TAG, JACKPOT_NAUSEA_DURATION_TICKS);

        if (living instanceof KattEntity katt) {
            katt.setJackpot(false);
        }

        if (living instanceof ServerPlayer player && living.level() instanceof ServerLevel level) {
            JackpotStatePacket.broadcast(level, player.getUUID(), false);
        }
    }

    private static void tickJackpotAftermath(LivingEntity living) {
        int slownessTicks = living.getPersistentData().getInt(JACKPOT_SLOWNESS_TICKS_TAG);
        if (slownessTicks > 0) {
            living.getPersistentData().putInt(JACKPOT_SLOWNESS_TICKS_TAG, slownessTicks - 1);
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0, false, false));
        } else if (slownessTicks < 0) {
            living.getPersistentData().remove(JACKPOT_SLOWNESS_TICKS_TAG);
        }

        int nauseaTicks = living.getPersistentData().getInt(JACKPOT_NAUSEA_TICKS_TAG);
        if (nauseaTicks > 0) {
            living.getPersistentData().putInt(JACKPOT_NAUSEA_TICKS_TAG, nauseaTicks - 1);
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30, 1, false, false));
        } else if (nauseaTicks < 0) {
            living.getPersistentData().remove(JACKPOT_NAUSEA_TICKS_TAG);
        }
    }
}
