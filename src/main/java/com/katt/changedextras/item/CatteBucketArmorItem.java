package com.katt.changedextras.item;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.katt.changedextras.model.catte_bucket;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

public class CatteBucketArmorItem extends ArmorItem {

    public CatteBucketArmorItem(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties);
    }

    // AGREGADO: Este método permite colocar el bloque en el mundo al hacer clic derecho sobre una superficie
    @Override
    public InteractionResult useOn(UseOnContext context) {
        var block = ChangedExtras.CATTE_BUCKET_BLOCK.get();
        BlockPlaceContext blockContext = new BlockPlaceContext(context);

        if (blockContext.canPlace()) {
            var level = context.getLevel();
            var pos = blockContext.getClickedPos();
            var state = block.getStateForPlacement(blockContext);

            if (state != null && level.setBlock(pos, state, 11)) {
                // Si el jugador no está en creativo, reducimos el ítem de la mano
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.useOn(context);
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
        return "changedextras:textures/models/armor/catte_bucket.png";
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
        return LatexAssimilationDecision.fromBlockOrItem(
                ModTransfurVariants.LATEX_CATTE.get(),
                TransfurContext.hazard(TransfurCause.CEILING_HAZARD),
                2.5f);
    }
    public void wearTick(LivingEntity entity, ItemStack itemStack) {
        if (ProcessTransfur.progressTransfur(entity, this.makeAssimilationDecision(entity)))
            itemStack.shrink(1);
    }

    @Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
    public static class WearHandler {
        @SubscribeEvent
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();
            if (entity.level().isClientSide) return;

            ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (head.getItem() instanceof CatteBucketArmorItem bucket) {
                bucket.wearTick(entity, head);
            }
        }
    }
}
