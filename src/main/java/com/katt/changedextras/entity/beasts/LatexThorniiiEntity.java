package com.katt.changedextras.entity.beasts;

import net.foxyas.changedaddon.entity.api.IDynamicRideOffsetEntity;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractAquaticEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class LatexThorniiiEntity extends AbstractLatexThorniii implements IDynamicRideOffsetEntity {
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
        return getTorsoYOffsetForFallFly(this);
    }
}