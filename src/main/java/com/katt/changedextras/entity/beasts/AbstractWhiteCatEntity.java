package com.katt.changedextras.entity.beasts;

import com.katt.changedextras.inventory.LatexCreatureInventory;
import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractWhiteCatEntity extends ChangedEntity {

    private final LatexCreatureInventory inventory = new LatexCreatureInventory(this);

    protected AbstractWhiteCatEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ChangedEntity.createLatexAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.27D);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        AttributeInstance maxHealthAttribute = attributes.getInstance(Attributes.MAX_HEALTH);
        double configuredMaxHealth = maxHealthAttribute != null ? maxHealthAttribute.getBaseValue() : this.getMaxHealth();
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.setBaseValue(configuredMaxHealth);
            this.setHealth((float) configuredMaxHealth);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public LatexCreatureInventory getCreatureInventory() {
        return inventory;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD  -> inventory.getHelmet();
            case CHEST -> inventory.getChestplate();
            case LEGS  -> inventory.getLeggings();
            case FEET  -> inventory.getBoots();
            default    -> super.getItemBySlot(slot);
        };
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        switch (slot) {
            case HEAD  -> inventory.setHelmet(stack);
            case CHEST -> inventory.setChestplate(stack);
            case LEGS  -> inventory.setLeggings(stack);
            case FEET  -> inventory.setBoots(stack);
            default    -> super.setItemSlot(slot, stack);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        inventory.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        inventory.load(tag);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        inventory.dropAllItems();
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.WHITE_LATEX.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }
}
