package com.katt.changedextras.entity.beasts;

import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexHazzyEntity extends AbstractWhiteCatEntity {
    public LatexHazzyEntity(EntityType<? extends LatexHazzyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void initializeBPI(BasicPlayerInfo bpi, RandomSource random) {
        super.initializeBPI(bpi, random);
        bpi.setLeftIrisColor(Color3.fromInt(0xFF5858));
        bpi.setRightIrisColor(Color3.fromInt(0xFF5858));
        bpi.setScleraColor(Color3.fromInt(0xFFFF75));
    }
}
