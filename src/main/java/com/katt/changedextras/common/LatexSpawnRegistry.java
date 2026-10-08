package com.katt.changedextras.common;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import com.katt.changedextras.entity.ModTransfurVariants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LatexSpawnRegistry {
    private LatexSpawnRegistry() {
    }

    public static List<TransfurVariant<?>> getAllVariants() {
        Map<ResourceLocation, TransfurVariant<?>> variantsById = new LinkedHashMap<>();

        for (TransfurVariant<?> variant : ChangedRegistry.TRANSFUR_VARIANT.get().getValues()) {
            addVariant(variantsById, variant);
        }

        if (variantsById.isEmpty()) {
            addRegistryObject(variantsById, ModTransfurVariants.CONEKAT_MALE);
            addRegistryObject(variantsById, ModTransfurVariants.CONEKAT_FEMALE);
            addRegistryObject(variantsById, ModTransfurVariants.WHITE_CAT);
            addRegistryObject(variantsById, ModTransfurVariants.KATT);
            addRegistryObject(variantsById, ModTransfurVariants.JAMMER);
            addRegistryObject(variantsById, ModTransfurVariants.PROTO_BEE);
            addRegistryObject(variantsById, ModTransfurVariants.FURRED_LATEX_TIGER_SHARK);
            addRegistryObject(variantsById, ModTransfurVariants.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE);
            addRegistryObject(variantsById, ModTransfurVariants.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE);
            addRegistryObject(variantsById, ModTransfurVariants.ARTIST);
            addRegistryObject(variantsById, ModTransfurVariants.SCP_009);
            addRegistryObject(variantsById, ModTransfurVariants.LATEX_HAZZY);
        }

        return variantsById.values().stream()
                .sorted(Comparator.comparing(variant -> variant.getFormId().toString()))
                .toList();
    }

    public static Map<EntityType<?>, ResourceLocation> buildEntityTypeMap() {
        Map<EntityType<?>, ResourceLocation> mappings = new IdentityHashMap<>();
        for (TransfurVariant<?> variant : getAllVariants()) {
            ResourceLocation entityTypeId = getEntityTypeId(variant);
            if (entityTypeId != null) {
                mappings.put(variant.getEntityType(), entityTypeId);
            }
        }
        return mappings;
    }

    public static ResourceLocation getEntityTypeId(TransfurVariant<?> variant) {
        if (variant == null || variant.getEntityType() == null) {
            return null;
        }
        return ForgeRegistries.ENTITY_TYPES.getKey(variant.getEntityType());
    }

    public static boolean isSpawnableVariant(TransfurVariant<?> variant) {
        return variant != null
                && variant.getEntityType() != null
                && ChangedEntity.class.isAssignableFrom(variant.getEntityType().getBaseClass());
    }

    private static boolean shouldExposeVariant(TransfurVariant<?> variant) {
        return isSpawnableVariant(variant)
                && variant.getFormId() != null
                && !TransfurVariant.SPECIAL_LATEX.equals(variant.getFormId());
    }

    private static void addRegistryObject(Map<ResourceLocation, TransfurVariant<?>> variantsById, RegistryObject<? extends TransfurVariant<?>> registryObject) {
        if (registryObject.isPresent()) {
            addVariant(variantsById, registryObject.get());
        }
    }

    private static void addVariant(Map<ResourceLocation, TransfurVariant<?>> variantsById, TransfurVariant<?> variant) {
        if (!shouldExposeVariant(variant)) {
            return;
        }

        variantsById.putIfAbsent(variant.getFormId(), variant);
    }
}
