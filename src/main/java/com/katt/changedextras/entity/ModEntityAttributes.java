package com.katt.changedextras.entity;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModEntityAttributes {
    private ModEntityAttributes() {
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CONEKAT_MALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.CONEKAT_FEMALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.MINT_CONEKAT_MALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.MINT_CONEKAT_FEMALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.CHOCO_CONEKAT_MALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.CHOCO_CONEKAT_FEMALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.STRAWBERRY_CONEKAT_MALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.STRAWBERRY_CONEKAT_FEMALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.NEAPOLITAN_CONEKAT_MALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.NEAPOLITAN_CONEKAT_FEMALE.get(), AbstractConeKatEntity.createAttributes()
                .build());
        event.put(ModEntities.WHITE_CAT.get(), AbstractWhiteCatEntity.createAttributes()
                .build());
        event.put(ModEntities.JAMMER.get(), AbstractWhiteCatEntity.createAttributes()
                .build());
        event.put(ModEntities.PROTO_BEE.get(), AbstractWhiteCatEntity.createAttributes()
                .build());
        event.put(ModEntities.ARTIST.get(), ArtistEntity.createAttributes()
                .build());
        event.put(ModEntities.SCP_009.get(), AbstractWhiteCatEntity.createAttributes()
                .build());
        event.put(ModEntities.LATEX_HAZZY.get(), AbstractWhiteCatEntity.createAttributes()
                .build());
        event.put(ModEntities.LATEX_CATTE.get(), AbstractLatexCatteEntity.createAttributes()
                .build());
        event.put(ModEntities.LATEX_HAKU.get(), LatexHakuEntity.createAttributes()
                .build());
        event.put(ModEntities.LATEX_THORNIII.get(), LatexThorniiiEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .build());
        event.put(ModEntities.LATEX_CARNELINE.get(), LatexCarnelineEntity.createAttributes()
                .build());
        event.put(ModEntities.LATEX_PANTHER.get(), LatexPantherEntity.createAttributes()
                .build());

        event.put(ModEntities.KATT.get(), KattEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .build());

        event.put(ModEntities.SNEP.get(), SnepEntity.createAttributes()
                .build());
    }
}
