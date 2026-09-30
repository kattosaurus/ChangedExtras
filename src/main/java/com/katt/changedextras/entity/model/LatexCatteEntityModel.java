package com.katt.changedextras.entity.model;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.AbstractLatexCatteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatexCatteEntityModel<T extends AbstractLatexCatteEntity> extends AdvancedHumanoidModel<T> {
    //                              ^^^^^^^^^^^^^^^^^^^^^^^^^
    //  More specific than the raw ChangedEntity bound, and matches WhiteCatEntityModel.
    //  AbstractLatexCatteEntity extends ChangedEntity, so the parent's bound is satisfied.

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_catte"),
                    "main");

    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;
    private final ModelPart torso;

    private final HumanoidAnimator<T, LatexCatteEntityModel<T>> animator;

    public LatexCatteEntityModel(ModelPart root) {
        super(root);

        this.rightLeg = root.getChild("RightLeg");
        this.leftLeg  = root.getChild("LeftLeg");
        this.head     = root.getChild("Head");
        this.torso    = root.getChild("Torso");
        this.rightArm = root.getChild("RightArm");
        this.leftArm  = root.getChild("LeftArm");

        var tail          = torso.getChild("Tail");
        var tailPrimary   = tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary  = tailSecondary.getChild("TailTertiary");

        var leftLowerLeg  = leftLeg.getChild("LeftLowerLeg");
        var leftFoot      = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = rightLeg.getChild("RightLowerLeg");
        var rightFoot     = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        head, head.getChild("LeftEar"), head.getChild("RightEar"),
                        torso, leftArm, rightArm,
                        tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        leftLeg, leftLowerLeg, leftFoot,  leftFoot.getChild("LeftPad"),
                        rightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // ===== RIGHT LEG =====================================================
        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg",
                CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg",
                CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create()
                        .texOffs(48, 40).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot",
                CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create()
                        .texOffs(56, 11).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)),
                PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create()
                        .texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.325F, -4.425F));

        // ===== LEFT LEG ======================================================
        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg",
                CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create()
                        .texOffs(48, 0).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg",
                CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create()
                        .texOffs(48, 40).mirror()
                        .addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot",
                CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create()
                        .texOffs(56, 11).mirror()
                        .addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F))
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create()
                        .texOffs(52, 32).mirror()
                        .addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(0.0F, 4.325F, -4.425F));

        // ===== HEAD ==========================================================
        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(15, 32).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 22).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -0.5F, 0.0F));

        Head.addOrReplaceChild("RightTopCheek_r1", CubeListBuilder.create()
                        .texOffs(39, 22).mirror()
                        .addBox(-2.0F, -31.0F, -4.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-0.2F, 26.0F, 0.0F, 0.0F, 1.3526F, 0.0F));

        Head.addOrReplaceChild("RightCheek_r1", CubeListBuilder.create()
                        .texOffs(39, 22).mirror()
                        .addBox(-2.0F, -31.0F, -4.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-0.2F, 27.0F, -2.0F, 0.0F, 1.3526F, 0.0F));

        Head.addOrReplaceChild("LeftTopCheek_r1", CubeListBuilder.create()
                        .texOffs(39, 22)
                        .addBox(-1.0F, -31.0F, -4.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.2F, 26.0F, 0.0F, 0.0F, -1.3526F, 0.0F));

        Head.addOrReplaceChild("LeftCheek_r1", CubeListBuilder.create()
                        .texOffs(39, 22)
                        .addBox(-1.0F, -31.0F, -4.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.2F, 27.0F, -2.0F, 0.0F, -1.3526F, 0.0F));

        Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create()
                        .texOffs(74, 0)
                        .addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        // Right ear
        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(),
                PartPose.offset(-3.0F, -7.5F, 0.0F));

        RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create()
                        .texOffs(0, 4).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                        .texOffs(0, 16).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                        .texOffs(32, 22).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                        .texOffs(24, 0).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        // Left ear
        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(),
                PartPose.offset(3.0F, -7.5F, 0.0F));

        LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                        .texOffs(0, 20).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                        .texOffs(32, 24).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                        .texOffs(0, 32).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        // Hair
        Head.addOrReplaceChild("Hair", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                        .texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // ===== TORSO =========================================================
        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create()
                        .texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -0.5F, 0.0F));

        Torso.addOrReplaceChild("LeftChest_r1", CubeListBuilder.create()
                        .texOffs(37, 91).mirror()
                        .addBox(0.0F, -26.0F, -3.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(2.0F, 25.0F, 1.1F, 0.0F, 0.2618F, 0.0F));

        Torso.addOrReplaceChild("RightChest_r1", CubeListBuilder.create()
                        .texOffs(37, 91)
                        .addBox(-3.0F, -26.0F, -3.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 25.0F, 1.1F, 0.0F, -0.2618F, 0.0F));

        // Tail
        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(),
                PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create()
                        .texOffs(48, 50)
                        .addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(),
                PartPose.offset(0.0F, 1.25F, 4.5F));

        TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)),
                PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.75F, 1.5F));

        TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create()
                        .texOffs(58, 60).addBox(-2.0F, 6.8F, 1.05F, 4.0F, 2.0F, 1.0F, new CubeDeformation(-0.15F))
                        .texOffs(46, 60).addBox(-2.0F, 5.8F, -0.95F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.15F))
                        .texOffs(28, 56).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 8.0F, 4.0F, new CubeDeformation(-0.15F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        // ===== ARMS ==========================================================
        partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, 1.5F, 0.0F));

        partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create()
                        .texOffs(0, 44).mirror()
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(@NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
    }

    @Override
    public void setupHand(T entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public ModelPart getArm(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public ModelPart getLeg(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftLeg : this.rightLeg;
    }

    @Override
    public ModelPart getHead()  { return this.head; }

    @Override
    public ModelPart getTorso() { return this.torso; }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render (poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render    (poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        torso.render   (poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render (poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<T, ?> getAnimator(T entity) {
        return animator;
    }
}