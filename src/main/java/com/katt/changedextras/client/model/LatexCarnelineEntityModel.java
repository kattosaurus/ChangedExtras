package com.katt.changedextras.client.model;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexCarnelineEntity;
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

public class LatexCarnelineEntityModel<T extends LatexCarnelineEntity> extends AdvancedHumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_carneline"), "main");

    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;
    private final ModelPart torso;
    private final HumanoidAnimator<T, LatexCarnelineEntityModel<T>> animator;

    public LatexCarnelineEntityModel(ModelPart root) {
        super(root);
        this.rightLeg = root.getChild("RightLeg");
        this.leftLeg = root.getChild("LeftLeg");
        this.head = root.getChild("Head");
        this.torso = root.getChild("Torso");
        this.rightArm = root.getChild("RightArm");
        this.leftArm = root.getChild("LeftArm");

        var tail = torso.getChild("Tail");
        var tailPrimary = tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");
        var tailQuaternary = tailTertiary.getChild("TailQuaternary");

        var leftLowerLeg = leftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = rightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.catLike(
                        head, head.getChild("LeftEar"), head.getChild("RightEar"),
                        torso, leftArm, rightArm,
                        tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
                        leftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"),
                        rightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));
        RightLeg.addOrReplaceChild("FurRight_r1", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.35F, 32.0F, 2.2F, -0.0278F, -0.3487F, -0.1337F));
        RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 50).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(44, 60).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).mirror().addBox(0.2F, 1.2F, -2.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(14, 33).mirror().addBox(-1.8F, 1.2F, -2.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

        RightPad.addOrReplaceChild("PawBeans_r1", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(0.505F, -3.1732F, -1.3F, 1.5708F, 0.0F, 0.0F));
        RightPad.addOrReplaceChild("PawBeans_r2", CubeListBuilder.create().texOffs(0, 93).addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.02F, -3.6232F, -0.167F, 1.5708F, 0.0F, 0.0F));
        RightPad.addOrReplaceChild("PawBeans_r3", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F))
                .texOffs(0, 93).addBox(1.175F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(-0.845F, -3.1732F, -0.8F, 1.5708F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));
        LeftLeg.addOrReplaceChild("FurLeft_r1", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.35F, 32.0F, 2.2F, -0.0278F, 0.3487F, 0.1337F));
        LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(13, 56).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).addBox(-1.2F, 1.2F, -2.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).addBox(0.8F, 1.2F, -2.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        LeftPad.addOrReplaceChild("PawBeans_r4", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offsetAndRotation(0.02F, -3.6232F, -0.167F, 1.5708F, 0.0F, 0.0F));
        LeftPad.addOrReplaceChild("PawBeans_r5", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false)
                .texOffs(0, 93).mirror().addBox(2.225F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false), PartPose.offsetAndRotation(-1.855F, -3.1732F, -0.8F, 1.5708F, 0.0F, 0.0F));
        LeftPad.addOrReplaceChild("PawBeans_r6", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false), PartPose.offsetAndRotation(-0.505F, -3.1732F, -1.3F, 1.5708F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 22).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));
        Head.addOrReplaceChild("CheekDownRight_r1", CubeListBuilder.create().texOffs(41, 8).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.85F, 26.0F, -3.0F, -0.3228F, 1.2949F, -0.3228F));
        Head.addOrReplaceChild("CheekRight_r1", CubeListBuilder.create().texOffs(41, 8).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.35F, 23.0F, 5.0F, 0.6754F, 1.2335F, 0.7156F));
        Head.addOrReplaceChild("HornRight_r1", CubeListBuilder.create().texOffs(46, 0).mirror().addBox(0.0F, -30.0F, -7.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.35F, 19.0F, 7.0F, 0.2518F, 0.5906F, 0.2106F));
        Head.addOrReplaceChild("HornLeft_r1", CubeListBuilder.create().texOffs(46, 0).addBox(-1.0F, -30.0F, -7.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, 19.0F, 7.0F, 0.2518F, -0.5906F, -0.2106F));
        Head.addOrReplaceChild("CheekLeft_r1", CubeListBuilder.create().texOffs(41, 8).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.35F, 23.0F, 5.0F, 0.6754F, -1.2335F, -0.7156F));
        Head.addOrReplaceChild("CheekDownLeft_r1", CubeListBuilder.create().texOffs(41, 8).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.85F, 26.0F, -3.0F, -0.3228F, -1.2949F, 0.3228F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));
        RightEar.addOrReplaceChild("RightEar_r1", CubeListBuilder.create().texOffs(0, 59).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));
        LeftEar.addOrReplaceChild("LeftEar_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

        Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));
        Torso.addOrReplaceChild("FurRight_r2", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.85F, 32.0F, 4.8F, 0.0475F, -0.346F, -0.2265F));
        Torso.addOrReplaceChild("FurTopRight_r1", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.15F, 31.0F, 3.5F, 0.0F, -0.3491F, -0.0873F));
        Torso.addOrReplaceChild("FurLeft_r2", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.85F, 32.0F, 4.8F, 0.0475F, 0.346F, 0.2265F));
        Torso.addOrReplaceChild("FurTopLeft_r1", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.15F, 31.0F, 3.5F, 0.0F, 0.3491F, 0.0873F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));
        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(28, 55).addBox(-2.0F, 0.75F, -1.4F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));
        TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 22).addBox(-2.0F, -0.25F, -1.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));
        TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 5.05F, -2.8F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5184F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));
        TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, 4.6F, -3.6F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.12F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.6581F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).mirror().addBox(-0.3F, 9.1F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(14, 33).mirror().addBox(-2.3F, 9.1F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, 1.5F, 0.0F));

        RightArm.addOrReplaceChild("PawBeans_r7", CubeListBuilder.create().texOffs(0, 93).addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-1.0F, 4.4268F, 0.133F, 1.5708F, 0.0F, 0.0F));
        RightArm.addOrReplaceChild("PawBeans_r8", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F))
                .texOffs(0, 93).addBox(-4.225F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(0.875F, 4.8768F, -0.5F, 1.5708F, 0.0F, 0.0F));
        RightArm.addOrReplaceChild("PawBeans_r9", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offsetAndRotation(-0.475F, 4.8768F, -1.0F, 1.5708F, 0.0F, 0.0F));
        RightArm.addOrReplaceChild("Fluff_r1", CubeListBuilder.create().texOffs(41, 11).mirror().addBox(-2.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-29.15F, 10.0F, 3.8F, 0.0F, -0.212F, 1.2812F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).addBox(-0.7F, 9.1F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 33).addBox(1.3F, 9.1F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        LeftArm.addOrReplaceChild("Fluff_r2", CubeListBuilder.create().texOffs(41, 11).addBox(-1.0F, -30.0F, -6.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(29.15F, 10.0F, 3.8F, 0.0F, 0.212F, -1.2812F));
        LeftArm.addOrReplaceChild("PawBeans_r10", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false)
                .texOffs(0, 93).mirror().addBox(-3.175F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false), PartPose.offsetAndRotation(1.825F, 4.8768F, -0.5F, 1.5708F, 0.0F, 0.0F));
        LeftArm.addOrReplaceChild("PawBeans_r11", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.5F)).mirror(false), PartPose.offsetAndRotation(0.475F, 4.8768F, -1.0F, 1.5708F, 0.0F, 0.0F));
        LeftArm.addOrReplaceChild("PawBeans_r12", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.2F)).mirror(false), PartPose.offsetAndRotation(1.0F, 4.4268F, 0.133F, 1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void setupHand(T entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public ModelPart getTorso() {
        return torso;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<T, ?> getAnimator(T entity) {
        return animator;
    }
}
