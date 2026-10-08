package com.katt.changedextras.entity.beasts;

import com.katt.changedextras.inventory.LatexCreatureInventory;
import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.LatexTaur;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexHakuEntity extends ChangedEntity implements LatexTaur<LatexHakuEntity> {
    private static final Color3 FORCED_IRIS_COLOR = Color3.fromInt(0x8B0000);
    private final LatexCreatureInventory inventory = new LatexCreatureInventory(this);

    public LatexHakuEntity(EntityType<? extends LatexHakuEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ChangedEntity.createLatexAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
        AttributeInstance maxHealth = attributes.getInstance(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(30.0D);
            this.setHealth(30.0F);
        }
        AttributeInstance speed = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(1.15D);
        }
        AttributeInstance swim = attributes.getInstance(ForgeMod.SWIM_SPEED.get());
        if (swim != null) {
            swim.setBaseValue(0.9D);
        }
        AttributeInstance step = attributes.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (step != null) {
            step.setBaseValue(computeStepHeightOffset(1.1D));
        }
        AttributeInstance jump = attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get());
        if (jump != null) {
            jump.setBaseValue(1.25D);
        }
        AttributeInstance fall = attributes.getInstance(ChangedAttributes.FALL_RESISTANCE.get());
        if (fall != null) {
            fall.setBaseValue(2.5D);
        }
    }

    @Override
    protected void initializeBPI(BasicPlayerInfo bpi, RandomSource random) {
        super.initializeBPI(bpi, random);
        bpi.setLeftIrisColor(FORCED_IRIS_COLOR);
        bpi.setRightIrisColor(FORCED_IRIS_COLOR);
    }

    @Override
    public BasicPlayerInfo getBasicPlayerInfo() {
        BasicPlayerInfo bpi = super.getBasicPlayerInfo();
        bpi.setLeftIrisColor(FORCED_IRIS_COLOR);
        bpi.setRightIrisColor(FORCED_IRIS_COLOR);
        return bpi;
    }

    @Override
    public EntityShape getEntityShape() {
        return EntityShape.TAUR;
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void equipSaddle(SoundSource soundSource) {
        equipSaddle(this, soundSource);
    }

    @Override
    public boolean isSaddled() {
        return isSaddled(this);
    }

    protected void doPlayerRide(Player player) {
        doPlayerRide(this, player);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + this.getTorsoYOffset(this) - 0.125d;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isSaddled()) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.WHITE_LATEX.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
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
}
