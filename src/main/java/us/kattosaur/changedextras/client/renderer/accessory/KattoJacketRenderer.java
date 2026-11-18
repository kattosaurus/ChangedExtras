package us.kattosaur.changedextras.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.accessory.AccessoryRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.item.Clothing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.client.model.KattoJacketModel;

public class KattoJacketRenderer implements AccessoryRenderer {
    private final KattoJacketModel<LivingEntity> model;

    public KattoJacketRenderer() {
        this.model = new KattoJacketModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(KattoJacketModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            AccessorySlotContext<T> slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = getTexture(slotContext);
        if (texture == null) return;

        M baseModel = renderLayerParent.getModel();
        copyPose(baseModel);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.body.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        model.rightArm.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        model.leftArm.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void renderFirstPersonOnArms(
            AccessorySlotContext<T> slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
            int light, HumanoidArm arm, PartPose armPose, float partialTicks) {
        ResourceLocation texture = getTexture(slotContext);
        if (texture == null) return;

        ModelPart armPart = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        armPart.loadPose(armPose);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        armPart.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void copyPose(EntityModel<?> baseModel) {
        if (baseModel instanceof AdvancedHumanoidModel<?> advanced) {
            copyPart(advanced.getTorso(), model.body);
            copyPart(advanced.getArm(HumanoidArm.RIGHT), model.rightArm);
            copyPart(advanced.getArm(HumanoidArm.LEFT), model.leftArm);
        } else if (baseModel instanceof HumanoidModel<?> humanoid) {
            copyPart(humanoid.body, model.body);
            copyPart(humanoid.rightArm, model.rightArm);
            copyPart(humanoid.leftArm, model.leftArm);
        }
    }

    private static void copyPart(ModelPart from, ModelPart to) {
        to.x = from.x;
        to.y = from.y;
        to.z = from.z;
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
        to.visible = from.visible;
    }

    private static ResourceLocation getTexture(AccessorySlotContext<?> slotContext) {
        ItemStack stack = slotContext.stack();
        if (stack.getItem() instanceof Clothing clothing) {
            return clothing.getTexture(stack, slotContext.wearer());
        }
        return ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "textures/models/accessory/katto_jacket.png");
    }
}
