package com.katt.changedextras.entity.model;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexHakuEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.tail.WolfTailInitAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LowerTorsoedModel;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;

public class LatexHakuEntityModel extends AdvancedHumanoidModel<LatexHakuEntity> implements LowerTorsoedModel {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_haku"), "main");

    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart lowerTorso;
    private final ModelPart saddle;
    private final ModelPart tail;

    private final HumanoidAnimator<LatexHakuEntity, LatexHakuEntityModel> animator;

    public LatexHakuEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("Head");
        this.torso = root.getChild("Torso");
        this.lowerTorso = root.getChild("LowerTorso");
        this.saddle = this.lowerTorso.getChild("Saddle");
        this.rightArm = root.getChild("RightArm");
        this.leftArm = root.getChild("LeftArm");
        this.frontRightLeg = this.lowerTorso.getChild("RightLeg");
        this.frontLeftLeg = this.lowerTorso.getChild("LeftLeg");
        this.backRightLeg = this.lowerTorso.getChild("RightLeg2");
        this.backLeftLeg = this.lowerTorso.getChild("LeftLeg2");
        this.tail = this.lowerTorso.getChild("Tail");

        var tailPrimary = this.tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");
        var tailQuaternary = tailTertiary.getChild("TailQuaternary");

        var frontLeftLowerLeg = this.frontLeftLeg.getChild("LeftLowerLeg");
        var frontRightLowerLeg = this.frontRightLeg.getChild("RightLowerLeg");
        var backLeftLowerLeg = this.backLeftLeg.getChild("LeftLowerLeg2");
        var backLeftFoot = backLeftLowerLeg.getChild("LeftFoot2");
        var backRightLowerLeg = this.backRightLeg.getChild("RightLowerLeg2");
        var backRightFoot = backRightLowerLeg.getChild("RightFoot2");

        animator = HumanoidAnimator.of(this)
                .addPreset(AnimatorPresets.taurLike(
                        this.head,
                        this.head.getChild("LeftEar"),
                        this.head.getChild("RightEar"),
                        this.torso,
                        this.leftArm,
                        this.rightArm,
                        this.lowerTorso,
                        this.frontLeftLeg,
                        frontLeftLowerLeg,
                        frontLeftLowerLeg.getChild("LeftFoot"),
                        this.frontRightLeg,
                        frontRightLowerLeg,
                        frontRightLowerLeg.getChild("RightFoot"),
                        this.backLeftLeg,
                        backLeftLowerLeg,
                        backLeftFoot,
                        backLeftFoot.getChild("LeftPad2"),
                        this.backRightLeg,
                        backRightLowerLeg,
                        backRightFoot,
                        backRightFoot.getChild("RightPad2")
                ))
                .addAnimator(new WolfTailInitAnimator<>(this.tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary)))
                .forwardOffset(-7.0f)
                .hipOffset(-1.5f)
                .legLength(13.5f)
                .torsoLength(11.05f);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(36, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(20, 44).addBox(-2.0F, -3.0F, -7.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(0.0F, -7.0F, 2.0F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(18, 68).addBox(0.0F, -11.0F, -3.0F, 0.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(86, 0).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -7.0F));

        Head.addOrReplaceChild("CheekTopRight_r1", CubeListBuilder.create().texOffs(88, 10).mirror().addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 23.0F, 11.0F, 0.4802F, 0.9719F, 0.5626F));
        Head.addOrReplaceChild("CheekRight_r1", CubeListBuilder.create().texOffs(88, 10).mirror().addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.5F, 25.0F, -4.0F, -0.4802F, 0.9719F, -0.5626F));
        Head.addOrReplaceChild("CheekTopLeft_r1", CubeListBuilder.create().texOffs(88, 10).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 23.0F, 11.0F, 0.4802F, -0.9719F, -0.5626F));
        Head.addOrReplaceChild("CheekLeft_r1", CubeListBuilder.create().texOffs(88, 10).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 25.0F, -4.0F, -0.4802F, -0.9719F, 0.5626F));
        Head.addOrReplaceChild("WhiskerLeft_r1", CubeListBuilder.create().texOffs(54, 95).mirror().addBox(-2.0F, -28.0F, -7.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 25.0F, -1.0F, -0.0256F, -0.7408F, 0.1025F));
        Head.addOrReplaceChild("WhiskerRight_r1", CubeListBuilder.create().texOffs(54, 95).addBox(-4.0F, -28.0F, -7.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 25.0F, -1.0F, -0.0256F, 0.7408F, -0.1025F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, -0.6545F, 0.0F));
        LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(76, 19).addBox(0.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(0.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(4.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, -0.9F, -0.6F, -0.2182F, -0.1745F, 0.4363F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -8.0F, 3.0F, 0.0F, 0.6545F, 0.0F));
        RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(76, 19).mirror().addBox(-4.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 25).mirror().addBox(-4.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 28).mirror().addBox(-5.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.2F, -0.9F, -0.6F, -0.2182F, 0.1745F, -0.4363F));

        Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(35, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(28, 41).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 85).addBox(0.0F, 3.0F, 2.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -7.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(72, 80).addBox(0.0F, -5.0F, 5.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.5F, -7.0F));

        LowerTorso.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 15.875F));
        LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(60, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(71, 29).addBox(-1.01F, -0.125F, -1.9F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(45, 76).addBox(-1.0F, -7.45F, 0.275F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(68, 52).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 15.875F));
        RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(60, 41).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(71, 29).mirror().addBox(-0.99F, -0.125F, -1.9F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(45, 76).mirror().addBox(-1.0F, -7.45F, 0.275F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(68, 52).mirror().addBox(-1.0F, 0.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 0.0F, -1.7F));
        LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(55, 55).addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));
        LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(58, 68).addBox(-1.0F, 3.8638F, 3.7342F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));
        LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(69, 62).addBox(-0.95F, 0.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 0.0F, -1.7F));
        RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(55, 55).mirror().addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(-0.25F, 5.7848F, 3.7028F));
        RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(58, 68).mirror().addBox(-1.0F, 3.8638F, 3.7342F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));
        RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(69, 62).mirror().addBox(-1.05F, 0.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 15.0F));
        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        TailPrimary.addOrReplaceChild("BackFur_r1", CubeListBuilder.create().texOffs(86, 80).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, -0.5236F, 0.0F, 0.0F));
        // Renamed from "Fluff_r1" so it matches the "Base_r1" child of TailPrimary in Changed's TransfurHelper TaurTorso.
        // Without a same-named child carrying cubes, the transfur animator crashes with IndexOutOfBoundsException.
        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 71).addBox(-1.5F, -3.0F, 0.4F, 3.0F, 1.0F, 4.0F, new CubeDeformation(-0.18F))
                .texOffs(0, 59).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));
        TailSecondary.addOrReplaceChild("BackFur_r2", CubeListBuilder.create().texOffs(86, 73).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 16.0F, -0.5F, -0.3927F, 0.0F, 0.0F));
        TailSecondary.addOrReplaceChild("Fluff_r2", CubeListBuilder.create().texOffs(0, 63).addBox(-1.0F, -1.5F, -2.7F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.02F))
                .texOffs(0, 61).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));
        TailTertiary.addOrReplaceChild("BackFur_r3", CubeListBuilder.create().texOffs(86, 73).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.5F, 3.5F, -0.1309F, 0.0F, 0.0F));
        TailTertiary.addOrReplaceChild("Fluff_r3", CubeListBuilder.create().texOffs(0, 61).addBox(-1.0F, -13.325F, 6.6F, 2.0F, 1.0F, 6.0F, new CubeDeformation(-0.2F))
                .texOffs(0, 61).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));
        TailQuaternary.addOrReplaceChild("BackFur_r4", CubeListBuilder.create().texOffs(88, 75).addBox(1.0F, -19.0F, -3.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.0F, 4.0F, 0.0436F, 0.0F, 0.0F));
        TailQuaternary.addOrReplaceChild("Fluff_r4", CubeListBuilder.create().texOffs(0, 78).addBox(-0.5F, -10.45F, 14.1F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.17F))
                .texOffs(60, 43).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.5F, -7.0F));
        partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(39, 55).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.5F, -7.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public ModelPart getArm(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public ModelPart getLeg(HumanoidArm side) {
        return null;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public ModelPart getTorso() {
        return this.torso;
    }

    @Override
    public ModelPart getLowerTorso() {
        return this.lowerTorso;
    }

    @Override
    public HelperModel getTransfurHelperModel(Limb limb) {
        if (limb == Limb.LOWER_TORSO) {
            return TransfurHelper.getTaurTorso();
        }
        if (limb == Limb.TORSO) {
            return null;
        }
        return super.getTransfurHelperModel(limb);
    }

    @Override
    public boolean shouldPartTransfur(ModelPart part) {
        return super.shouldPartTransfur(part) && part != this.saddle;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.swapResetPoseStack(poseStack);
        this.lowerTorso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.swapResetPoseStack(poseStack);
    }

    @Override
    public HumanoidAnimator<LatexHakuEntity, ?> getAnimator(LatexHakuEntity entity) {
        return this.animator;
    }
}