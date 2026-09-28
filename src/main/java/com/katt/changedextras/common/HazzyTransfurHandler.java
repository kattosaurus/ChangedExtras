package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.foxyas.changedaddon.init.ChangedAddonItems;
import net.foxyas.changedaddon.item.armor.HazardBodySuit;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.process.TransfurEvents;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class HazzyTransfurHandler {

    private HazzyTransfurHandler() {
    }

    public static boolean isHazardSuitItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof HazardBodySuit) {
            return true;
        }
        try {
            if (ChangedAddonItems.HAZARD_BODY_SUIT.isPresent() && stack.is(ChangedAddonItems.HAZARD_BODY_SUIT.get())) {
                return true;
            }
        } catch (Throwable ignored) {
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id != null) {
            String path = id.getPath().toLowerCase(Locale.ROOT);
            if (path.contains("hazard") || path.contains("hazmat")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWearingHazmatSuit(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        LivingEntity underlying = EntityUtil.maybeGetUnderlying(entity);
        if (underlying == null) {
            underlying = entity;
        }

        // 1. Check Changed accessory / clothing slots on entity and underlying
        try {
            if (AccessorySlots.isWearing(entity, HazzyTransfurHandler::isHazardSuitItem) ||
                AccessorySlots.isWearing(underlying, HazzyTransfurHandler::isHazardSuitItem)) {
                return true;
            }
            if (AccessorySlots.getForEntity(entity).map(slots -> slots.anyItemMatches(HazzyTransfurHandler::isHazardSuitItem)).orElse(false) ||
                AccessorySlots.getForEntity(underlying).map(slots -> slots.anyItemMatches(HazzyTransfurHandler::isHazardSuitItem)).orElse(false)) {
                return true;
            }
        } catch (Throwable ignored) {
        }

        // 2. Check standard armor slots
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                if (isHazardSuitItem(entity.getItemBySlot(slot)) || isHazardSuitItem(underlying.getItemBySlot(slot))) {
                    return true;
                }
            }
        }

        // 3. Check held items
        if (isHazardSuitItem(entity.getMainHandItem()) || isHazardSuitItem(entity.getOffhandItem()) ||
            isHazardSuitItem(underlying.getMainHandItem()) || isHazardSuitItem(underlying.getOffhandItem())) {
            return true;
        }

        return false;
    }

    public static void ripWornHazmatSuits(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        LivingEntity underlying = EntityUtil.maybeGetUnderlying(entity);
        if (underlying == null) {
            underlying = entity;
        }

        ripFromEntity(entity);
        if (underlying != entity) {
            ripFromEntity(underlying);
        }
    }

    private static void ripFromEntity(LivingEntity entity) {
        // 1. Accessory Slots
        try {
            AccessorySlots.getForEntity(entity).ifPresent(slots -> {
                for (AccessorySlotType slotType : slots.getOrderedSlots()) {
                    slots.getItem(slotType).ifPresent(stack -> {
                        if (isHazardSuitItem(stack)) {
                            playRipEffects(entity, stack);
                            AccessorySlots.onBrokenAccessory(entity, slotType);
                            slots.setItem(slotType, ItemStack.EMPTY);
                        }
                    });
                }
            });
        } catch (Throwable ignored) {
        }

        // 2. Armor Slots
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (isHazardSuitItem(stack)) {
                    playRipEffects(entity, stack);
                    if (entity instanceof Player player) {
                        player.awardStat(Stats.ITEM_BROKEN.get(stack.getItem()));
                    }
                    entity.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }
    }

    private static void playRipEffects(LivingEntity entity, ItemStack stack) {
        Level level = entity.level();
        SoundEvent breakSound = null;
        if (stack.getItem() instanceof HazardBodySuit hazardBodySuit) {
            try {
                breakSound = hazardBodySuit.getBreakSound(stack);
            } catch (Throwable ignored) {
            }
        }
        if (breakSound == null) {
            try {
                if (ChangedSounds.WETSUIT_BREAK.isPresent()) {
                    breakSound = ChangedSounds.WETSUIT_BREAK.get();
                }
            } catch (Throwable ignored) {
            }
        }
        if (breakSound == null) {
            breakSound = SoundEvents.ITEM_BREAK;
        }

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                breakSound, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, stack),
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() * 0.5D,
                    entity.getZ(),
                    25,
                    0.25D,
                    0.4D,
                    0.25D,
                    0.05D
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
        if (event.variant == null) {
            return;
        }
        LivingEntity entity = event.livingEntity;
        if (event.variant == ModTransfurVariants.LATEX_HAZZY.get()) {
            ripWornHazmatSuits(entity);
            return;
        }
        if (isWearingHazmatSuit(entity)) {
            ripWornHazmatSuits(entity);
            event.variant = ModTransfurVariants.LATEX_HAZZY.get();
        }
    }

    @SubscribeEvent
    public static void onNewlyTransfurred(TransfurEvents.NewlyTransfurredEntityEvent event) {
        if (event.entity != null && event.entity.getSelfVariant() == ModTransfurVariants.LATEX_HAZZY.get()) {
            LivingEntity living = event.entity.getEntity();
            if (living != null) {
                ripWornHazmatSuits(living);
            }
        }
    }
}
