package com.katt.changedextras.item;

import com.katt.changedextras.model.catte_bucket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class CatteBucketArmorItem extends ArmorItem {

    public CatteBucketArmorItem(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private catte_bucket<?> cachedModel;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.cachedModel == null) {
                    this.cachedModel = new catte_bucket<>(Minecraft.getInstance().getEntityModels().bakeLayer(catte_bucket.LAYER_LOCATION));
                }

                this.cachedModel.crouching = original.crouching;
                this.cachedModel.riding = original.riding;
                this.cachedModel.young = original.young;
                this.cachedModel.head.copyFrom(original.head);

                return this.cachedModel;
            }
        });
    }

    @Override
    public String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return "changedextras:textures/entity/armor/catte_bucket.png";
    }
}