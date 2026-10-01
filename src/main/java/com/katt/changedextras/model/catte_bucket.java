package com.katt.changedextras.model;

import com.katt.changedextras.ChangedExtras;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class catte_bucket<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "catte_bucket"), "main");

    public catte_bucket(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = meshdefinition.getRoot();

        for (String name : new String[]{"hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"}) {
            root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);
        }

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition bucket = head.addOrReplaceChild("catte_bucket_part", CubeListBuilder.create(),
                PartPose.offset(0.0F, -8.0F, 0.0F));

        bucket.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 28)
                        .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 9.0F, 2.0F, -0.0873F, 0.0F, 0.0F));

        bucket.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 12)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        bucket.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}