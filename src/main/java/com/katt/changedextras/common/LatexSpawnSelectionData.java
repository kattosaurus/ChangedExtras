package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class LatexSpawnSelectionData extends SavedData {
    private static final String DATA_NAME = ChangedExtras.MODID + "_latex_spawn_selection";
    private static final String DISABLED_VARIANTS_TAG = "DisabledVariants";
    private static final String DEFAULTS_VERSION_TAG = "DefaultsVersion";
    private static final String REVISION_TAG = "Revision";
    private static final int CURRENT_DEFAULTS_VERSION = 3;
    private static final String ARTIST_FORM_ID = ChangedExtras.MODID + ":latex_artist";
    private static final String EXPERIMENT_009_FORM_ID = "changed_addon:form_experiment_009";
    private static final String EXPERIMENT_10_FORM_ID = "changed_addon:form_experiment_10";
    private static final String EXPERIMENT_009_BOSS_FORM_ID = "changed_addon:form_experiment_009_boss";
    private static final String EXPERIMENT_10_BOSS_FORM_ID = "changed_addon:form_experiment_10_boss";
    private static final String VOID_FOX_FORM_ID = "changed_addon:void_fox";

    private final Set<String> disabledVariantIds = new HashSet<>();
    private int defaultsVersion;
    private int revision;

    public LatexSpawnSelectionData() {
        this(true);
    }

    private LatexSpawnSelectionData(boolean applyDefaults) {
        if (applyDefaults) {
            applyDefaultBlacklist();
        }
    }

    public static LatexSpawnSelectionData createLoaded() {
        return new LatexSpawnSelectionData(false);
    }

    public static LatexSpawnSelectionData load(CompoundTag tag) {
        LatexSpawnSelectionData data = createLoaded();
        ListTag disabledVariants = tag.getList(DISABLED_VARIANTS_TAG, Tag.TAG_STRING);
        for (int i = 0; i < disabledVariants.size(); i++) {
            data.disabledVariantIds.add(disabledVariants.getString(i));
        }
        data.defaultsVersion = tag.contains(DEFAULTS_VERSION_TAG, Tag.TAG_INT) ? tag.getInt(DEFAULTS_VERSION_TAG) : 0;
        data.revision = tag.contains(REVISION_TAG, Tag.TAG_INT) ? tag.getInt(REVISION_TAG) : 0;
        data.migrateLegacyIds();
        if (data.defaultsVersion < CURRENT_DEFAULTS_VERSION) {
            data.applyDefaultBlacklist();
        }
        return data;
    }

    public static LatexSpawnSelectionData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(LatexSpawnSelectionData::load, LatexSpawnSelectionData::new, DATA_NAME);
    }

    public boolean isEnabled(ResourceLocation formId) {
        return !disabledVariantIds.contains(formId.toString());
    }

    public boolean isEnabled(EntityType<?> entityType) {
        ResourceLocation entityTypeId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        return entityTypeId == null || isEnabled(entityTypeId);
    }

    public void setEnabled(ResourceLocation formId, boolean enabled) {
        if (enabled) {
            disabledVariantIds.remove(formId.toString());
        } else {
            disabledVariantIds.add(formId.toString());
        }
        revision++;
        setDirty();
    }

    public Set<String> getDisabledVariantIds() {
        return Set.copyOf(disabledVariantIds);
    }

    public int getRevision() {
        return revision;
    }

    private void applyDefaultBlacklist() {
        disabledVariantIds.add(resolveLegacyId(ARTIST_FORM_ID));
        disabledVariantIds.add(resolveLegacyId(EXPERIMENT_009_FORM_ID));
        disabledVariantIds.add(resolveLegacyId(EXPERIMENT_10_FORM_ID));
        disabledVariantIds.add(resolveLegacyId(EXPERIMENT_009_BOSS_FORM_ID));
        disabledVariantIds.add(resolveLegacyId(EXPERIMENT_10_BOSS_FORM_ID));
        disabledVariantIds.add(resolveLegacyId(VOID_FOX_FORM_ID));
        defaultsVersion = CURRENT_DEFAULTS_VERSION;
        revision++;
    }

    private void migrateLegacyIds() {
        Set<String> migratedIds = new HashSet<>();
        boolean changed = false;
        for (String id : disabledVariantIds) {
            String migratedId = resolveLegacyId(id);
            migratedIds.add(migratedId);
            changed |= !migratedId.equals(id);
        }

        if (changed) {
            disabledVariantIds.clear();
            disabledVariantIds.addAll(migratedIds);
            revision++;
            setDirty();
        }
    }

    private static String resolveLegacyId(String id) {
        ResourceLocation key = ResourceLocation.tryParse(id);
        if (key == null) {
            return id;
        }

        EntityType<?> directMatch = ForgeRegistries.ENTITY_TYPES.getValue(key);
        if (directMatch != null) {
            return id;
        }

        for (var variant : LatexSpawnRegistry.getAllVariants()) {
            if (key.equals(variant.getFormId())) {
                ResourceLocation entityTypeId = LatexSpawnRegistry.getEntityTypeId(variant);
                if (entityTypeId != null) {
                    return entityTypeId.toString();
                }
            }
        }

        return id;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag disabledVariants = new ListTag();
        disabledVariantIds.stream()
                .sorted()
                .map(StringTag::valueOf)
                .forEach(disabledVariants::add);
        tag.put(DISABLED_VARIANTS_TAG, disabledVariants);
        tag.putInt(DEFAULTS_VERSION_TAG, defaultsVersion);
        tag.putInt(REVISION_TAG, revision);
        return tag;
    }
}
