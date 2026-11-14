package com.katt.changedextras.entity.beasts;

import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractAquaticEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class LatexThorniiiEntity extends AbstractLatexThorniii {
    public LatexThorniiiEntity(EntityType<? extends AbstractAquaticEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ChangedEntity.createLatexAttributes().add(Attributes.MAX_HEALTH, 40.0);
    }

    @Override
    protected void initializeBPI(BasicPlayerInfo bpi, RandomSource random) {
        super.initializeBPI(bpi, random);
        bpi.setLeftIrisColor(Color3.fromInt(0x8dc7ee));
        bpi.setRightIrisColor(Color3.fromInt(0x8dc7ee));
        bpi.setScleraColor(Color3.fromInt(0x000000));
    }

    @Override
    public double getPassengersRidingOffset() {
        if (this.getPose() == Pose.STANDING || this.getPose() == Pose.CROUCHING) {
            return super.getPassengersRidingOffset() + this.getTorsoYOffset(this) + (this.isCrouching() ? 1.2 : 1.15);
        }
        return this.getTorsoYOffsetForFallFly(this);
    }

    // Mirrors Changed Addon's IDynamicRideOffsetEntity default formulas so this entity
    // behaves identically when the addon is installed without depending on it.
    private double getTorsoYOffset(ChangedEntity entity) {
        float height = entity.getBbHeight();
        float phase = entity.tickCount * 0.33333334F * 0.25F * 0.15F;
        float sin = Mth.sin(phase * (float) Math.PI * 0.5F);
        float cos = Mth.cos(phase * (float) Math.PI * 0.5F);
        float sizeOffset = (entity.getBasicPlayerInfo().getSize(entity) - 1.0F) * 2.0F;
        float wave = 1.0F - Mth.abs(1.0F - Mth.positiveModulo(phase, 2.0F));
        float sin4 = sin * sin * sin * sin;
        float cos4 = cos * cos * cos * cos;
        float blend = Mth.lerp(wave, sin4, 1.0F - cos4);
        float offset = Mth.lerp(blend, 0.95F, 0.87F) + sizeOffset;
        return (double) offset * height;
    }

    private double getTorsoYOffsetForFallFly(ChangedEntity entity) {
        float sizeOffset = (entity.getBasicPlayerInfo().getSize(entity) - 1.0F) * 2.0F;
        return (0.375D + sizeOffset) * entity.getBbHeight();
    }
}