package com.katt.changedextras.client.model;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.entity.beasts.LatexKaijuEntity;
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

public class LatexKaijuEntityModel<T extends LatexKaijuEntity> extends AdvancedHumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "latex_kaiju"), "main");

    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart leftFoot;
    private final ModelPart leftPad;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final HumanoidAnimator<T, LatexKaijuEntityModel<T>> animator;

    public LatexKaijuEntityModel(ModelPart root) {
        super(root);
        this.rightLeg = root.getChild("RightLeg");
        this.leftLeg = root.getChild("LeftLeg");
        this.head = root.getChild("Head");
        this.torso = root.getChild("Torso");
        this.rightArm = root.getChild("RightArm");
        this.leftArm = root.getChild("LeftArm");

        this.tail = torso.getChild("Tail");
        var tailPrimary = tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");
        var tailQuaternary = tailTertiary.getChild("TailQuaternary");
        this.rightEar = head.getChild("RightEar");
        this.leftEar = head.getChild("LeftEar");

        var leftLowerLeg = leftLeg.getChild("LeftLowerLeg");
        this.leftFoot = leftLowerLeg.getChild("LeftFoot");
        this.leftPad = leftFoot.getChild("LeftPad");
        var rightLowerLeg = rightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        this.animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.catLike(
                        head, leftEar, rightEar,
                        torso, leftArm, rightArm,
                        tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
                        leftLeg, leftLowerLeg, leftFoot, leftPad,
                        rightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));
        RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(40, 20).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(50, 30).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));
        LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(40, 9).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(32, 50).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(50, 46).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 30).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        Head.addOrReplaceChild("Crystal_r1", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -8.6F, 2.3F, 0.0F, -0.7854F, -1.5708F));
        Head.addOrReplaceChild("Crystal_r2", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -2.6F, 4.3F, 0.0F, -0.7854F, -1.5708F));
        Head.addOrReplaceChild("Crystal_r3", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -5.6F, 4.3F, 0.0F, -0.7854F, -1.5708F));

        // The Kaiju model has no ears, but the cat-like animator expects them.
        Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));
        Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(-0.1F, 25.0F, 0.0F));

        Hair.addOrReplaceChild("Hair_r1", CubeListBuilder.create().texOffs(32, 59).mirror().addBox(-1.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)).mirror(false), PartPose.offsetAndRotation(3.8F, -28.0566F, 1.2163F, -0.8958F, -0.057F, -0.6635F));
        Hair.addOrReplaceChild("Hair_r2", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false)
                .texOffs(0, 56).mirror().addBox(1.3493F, -5.2508F, 0.1136F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false), PartPose.offsetAndRotation(2.7F, -27.9146F, 0.7522F, -0.1637F, 0.5044F, -0.4422F));
        Hair.addOrReplaceChild("Hair_r3", CubeListBuilder.create().texOffs(50, 39).mirror().addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(3.9F, -27.5735F, 1.1689F, -0.457F, 0.4276F, -0.7087F));
        Hair.addOrReplaceChild("Hair_r4", CubeListBuilder.create().texOffs(32, 59).addBox(0.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-3.6F, -28.0566F, 1.2163F, -0.8958F, 0.057F, 0.6635F));
        Hair.addOrReplaceChild("Hair_r5", CubeListBuilder.create().texOffs(0, 56).addBox(-2.3493F, -5.2508F, 0.1136F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F))
                .texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -27.9146F, 0.7522F, -0.1637F, -0.5044F, 0.4422F));
        Hair.addOrReplaceChild("Hair_r6", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-3.7F, -27.5735F, 1.1689F, -0.457F, -0.4276F, 0.7087F));
        Hair.addOrReplaceChild("Hair_r7", CubeListBuilder.create().texOffs(32, 59).addBox(0.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-3.6F, -28.275F, -0.3F, -0.6545F, -0.1309F, 0.6545F));
        Hair.addOrReplaceChild("Hair_r8", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false), PartPose.offsetAndRotation(2.7F, -30.0F, -0.7F, 0.1745F, 0.6109F, -0.2618F));
        Hair.addOrReplaceChild("Hair_r9", CubeListBuilder.create().texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -30.0F, -0.7F, 0.1745F, -0.6109F, 0.2618F));
        Hair.addOrReplaceChild("Hair_r10", CubeListBuilder.create().texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -28.0F, -0.7F, 0.1745F, -0.6109F, 0.2618F));
        Hair.addOrReplaceChild("Hair_r11", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-3.7F, -27.8F, -0.2F, -0.1745F, -0.6109F, 0.5672F));
        Hair.addOrReplaceChild("Hair_r12", CubeListBuilder.create().texOffs(62, 46).mirror().addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F)).mirror(false)
                .texOffs(62, 46).addBox(3.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F))
                .texOffs(62, 46).addBox(1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(-1.9F, -26.6F, -0.7F, -1.2217F, 0.0F, 0.0F));
        Hair.addOrReplaceChild("Hair_r13", CubeListBuilder.create().texOffs(42, 62).mirror().addBox(-1.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F)).mirror(false)
                .texOffs(42, 62).addBox(3.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F))
                .texOffs(42, 62).addBox(1.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(-1.9F, -30.7F, -2.6F, -1.0472F, 0.0F, 0.0F));
        Hair.addOrReplaceChild("Hair_r14", CubeListBuilder.create().texOffs(0, 1).mirror().addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F)).mirror(false)
                .texOffs(0, 1).addBox(3.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F))
                .texOffs(0, 1).addBox(1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(-1.9F, -27.4F, 1.5F, 0.2618F, 0.0F, 0.0F));
        Hair.addOrReplaceChild("Hair_r15", CubeListBuilder.create().texOffs(56, 13).mirror().addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F)).mirror(false)
                .texOffs(56, 13).addBox(3.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F))
                .texOffs(56, 13).addBox(1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F)), PartPose.offsetAndRotation(-1.9F, -27.3F, -1.5F, 0.4363F, 0.0F, 0.0F));
        Hair.addOrReplaceChild("Hair_r16", CubeListBuilder.create().texOffs(56, 18).mirror().addBox(-1.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F)).mirror(false)
                .texOffs(56, 18).addBox(3.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F))
                .texOffs(56, 18).addBox(1.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(-1.9F, -27.5F, -1.1F, 0.6545F, 0.0F, 0.0F));
        Hair.addOrReplaceChild("Hair_r17", CubeListBuilder.create().texOffs(46, 50).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(3.9F, -27.8F, -0.2F, -0.1745F, 0.6109F, -0.5672F));
        Hair.addOrReplaceChild("Hair_r18", CubeListBuilder.create().texOffs(16, 51).addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(2.7F, -28.0F, -0.7F, 0.1745F, 0.6109F, -0.2618F));
        Hair.addOrReplaceChild("Hair_r19", CubeListBuilder.create().texOffs(22, 58).addBox(-1.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(3.8F, -28.275F, -0.3F, -0.6545F, 0.1309F, -0.6545F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        Torso.addOrReplaceChild("Crystal_r4", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 7.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));
        Torso.addOrReplaceChild("Crystal_r5", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 4.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));
        Torso.addOrReplaceChild("Crystal_r6", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 1.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));

        Torso.addOrReplaceChild("FluffLeft_r1", CubeListBuilder.create().texOffs(25, 0).mirror().addBox(1.0F, -24.0F, -2.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 24.0F, 0.25F, 0.0F, 0.3054F, 0.0F));
        Torso.addOrReplaceChild("FluffRight_r1", CubeListBuilder.create().texOffs(25, 0).addBox(-3.0F, -24.0F, -2.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 24.0F, 0.25F, 0.0F, -0.3054F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        TailPrimary.addOrReplaceChild("Crystal_r7", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -1.1F, 3.0F, 0.0F, -0.7854F, -1.5708F));
        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        TailSecondary.addOrReplaceChild("Crystal_r8", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 0.9F, 4.5F, 0.0F, -0.7854F, -1.5708F));
        TailSecondary.addOrReplaceChild("Crystal_r9", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.1F, 2.5F, 0.0F, -0.7854F, -1.5708F));
        TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        TailTertiary.addOrReplaceChild("Crystal_r10", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.6F, 3.5F, 0.0F, -0.7854F, -1.5708F));
        TailTertiary.addOrReplaceChild("Crystal_r11", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.6F, 1.5F, 0.0F, -0.7854F, -1.5708F));
        TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        TailQuaternary.addOrReplaceChild("Crystal_r12", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -1.1F, 2.0F, 0.0F, -0.7854F, -1.5708F));
        TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 7).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

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
        return this.animator;
    }
}
