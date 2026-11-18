package us.kattosaur.changedextras.client.model;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.client.renderer.GlobalAnimationHelper;
import us.kattosaur.changedextras.entity.beasts.KattEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KattEntityModel extends AdvancedHumanoidModel<KattEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "katt"), "main");

    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;
    private final ModelPart torso;
    private final HumanoidAnimator<KattEntity, KattEntityModel> animator;

    public KattEntityModel(ModelPart root) {
        super(root);
        this.rightLeg = root.getChild("RightLeg");
        this.leftLeg = root.getChild("LeftLeg");
        this.head = root.getChild("Head");
        this.torso = root.getChild("Torso");
        this.rightArm = root.getChild("RightArm");
        this.leftArm = root.getChild("LeftArm");

        // Tail chain
        var tail = torso.getChild("Tail");
        var tailPrimary = tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");
        var tailQuaternary = tailTertiary.getChild("TailQuaternary");

        // Leg chain
        var leftLowerLeg = leftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = rightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        head, head.getChild("LeftEar"), head.getChild("RightEar"),
                        torso, leftArm, rightArm,
                        tail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary),
                        leftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"),
                        rightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // --- LEGS ---
        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));
        RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 50).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));
        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(44, 60).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));
        RightPad.addOrReplaceChild("RightFootPawBeans", CubeListBuilder.create().texOffs(8, 87).mirror().addBox(-2.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 83).mirror().addBox(-2.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 81).mirror().addBox(-1.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 85).mirror().addBox(-0.225F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(1.0F, -8.5F, -0.05F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));
        LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));
        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));
        LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));
        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));
        LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(13, 56).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));
        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));
        LeftPad.addOrReplaceChild("LeftFootPawBeans", CubeListBuilder.create().texOffs(8, 87).addBox(0.0F, 9.475F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                .texOffs(8, 83).addBox(1.8F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(8, 81).addBox(0.5F, 9.475F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(8, 85).addBox(-0.775F, 9.475F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-1.0F, -8.5F, -0.05F));

        // --- HEAD ---
        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 22).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -1.0F, 0.0F));
        RightEar.addOrReplaceChild("right ear_r1", CubeListBuilder.create().texOffs(0, 59).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -1.0F, 0.0F));
        LeftEar.addOrReplaceChild("left ear_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

        Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(24, 8).addBox(-4.5F, -34.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.5F, 26.0F, 0.0F));

        PartDefinition LeftCheekFur = Head.addOrReplaceChild("LeftCheekFur", CubeListBuilder.create(), PartPose.offset(3.85F, -2.0F, -2.8F));
        LeftCheekFur.addOrReplaceChild("Lower_r1", CubeListBuilder.create().texOffs(84, 1).addBox(0.45F, -1.5F, -1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.4451F, 0.0F));
        LeftCheekFur.addOrReplaceChild("Upper_r1", CubeListBuilder.create().texOffs(84, -3).addBox(0.45F, -0.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.5995F, 0.2317F, 0.3404F));
        LeftCheekFur.addOrReplaceChild("Middle_r1", CubeListBuilder.create().texOffs(84, -1).addBox(0.45F, -2.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.1F, 1.3F, 0.1309F, 0.3403F, 0.0F));

        PartDefinition RightCheekFur = Head.addOrReplaceChild("RightCheekFur", CubeListBuilder.create(), PartPose.offset(-3.85F, -2.0F, -2.8F));
        RightCheekFur.addOrReplaceChild("Lower_r2", CubeListBuilder.create().texOffs(84, 1).mirror().addBox(-0.45F, -1.5F, -1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, -0.4451F, 0.0F));
        RightCheekFur.addOrReplaceChild("Upper_r2", CubeListBuilder.create().texOffs(84, -3).mirror().addBox(-0.45F, -0.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.5995F, -0.2317F, -0.3404F));
        RightCheekFur.addOrReplaceChild("Middle_r2", CubeListBuilder.create().texOffs(84, -1).mirror().addBox(-0.45F, -2.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-0.1F, 0.1F, 1.3F, 0.1309F, -0.3403F, 0.0F));

        // --- TORSO & TAIL ---
        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));
        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));
        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
        TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(28, 55).addBox(-2.0F, 0.75F, -1.4F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));
        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));
        TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 22).addBox(-2.0F, -0.25F, -1.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));
        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));
        TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 5.05F, -2.8F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5184F, 0.0F, 0.0F));
        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));
        TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, 4.6F, -3.6F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.12F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.6581F, 0.0F, 0.0F));

        // --- ARMS ---
        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));
        RightArm.addOrReplaceChild("RightPawBeans", CubeListBuilder.create().texOffs(8, 87).mirror().addBox(-2.0F, 9.975F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 83).mirror().addBox(-2.8F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 81).mirror().addBox(-1.5F, 9.975F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
                .texOffs(8, 85).mirror().addBox(-0.225F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(0.0F, -0.9F, 0.0F));
        PartDefinition RightHandFur = RightArm.addOrReplaceChild("RightHandFur", CubeListBuilder.create(), PartPose.offset(-2.0F, 7.5F, -2.3F));
        RightHandFur.addOrReplaceChild("RightArmFur_r1", CubeListBuilder.create().texOffs(86, 27).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(3.3F, 0.0F, 3.3F, 0.0F, -1.5708F, 0.0873F));
        RightHandFur.addOrReplaceChild("RightArmFur_r2", CubeListBuilder.create().texOffs(86, 22).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-1.3F, 0.0F, 3.3F, 0.0F, 1.5708F, -0.0873F));
        RightHandFur.addOrReplaceChild("RightArmFur_r3", CubeListBuilder.create().texOffs(86, 17).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 3.6F, -0.0873F, 0.0F, 0.0F));
        RightHandFur.addOrReplaceChild("RightArmFur_r4", CubeListBuilder.create().texOffs(86, 12).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));
        LeftArm.addOrReplaceChild("LeftPawBeans", CubeListBuilder.create().texOffs(8, 87).addBox(0.0F, 9.975F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                .texOffs(8, 83).addBox(1.8F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(8, 81).addBox(0.5F, 9.975F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                .texOffs(8, 85).addBox(-0.775F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(0.0F, -0.9F, 0.0F));
        PartDefinition LeftArmFurLower = LeftArm.addOrReplaceChild("LeftArmFurLower", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition LeftHandFur = LeftArmFurLower.addOrReplaceChild("LeftHandFur", CubeListBuilder.create(), PartPose.offset(2.0F, 7.5F, -2.3F));
        LeftHandFur.addOrReplaceChild("LeftArmFur_r1", CubeListBuilder.create().texOffs(86, 27).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-3.3F, 0.0F, 3.3F, 0.0F, 1.5708F, -0.0873F));
        LeftHandFur.addOrReplaceChild("LeftArmFur_r2", CubeListBuilder.create().texOffs(86, 22).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.3F, 0.0F, 3.3F, 0.0F, -1.5708F, 0.0873F));
        LeftHandFur.addOrReplaceChild("LeftArmFur_r3", CubeListBuilder.create().texOffs(86, 17).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.6F, -0.0873F, 0.0F, 0.0F));
        LeftHandFur.addOrReplaceChild("LeftArmFur_r4", CubeListBuilder.create().texOffs(86, 12).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void setupHand(KattEntity entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull KattEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        GlobalAnimationHelper.apply(this, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, false);
    }

    @Override public ModelPart getArm(HumanoidArm side) { return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm; }
    @Override public ModelPart getLeg(HumanoidArm side) { return side == HumanoidArm.LEFT ? this.leftLeg : this.rightLeg; }
    @Override public ModelPart getHead() { return this.head; }
    @Override public ModelPart getTorso() { return this.torso; }
    @Override public HumanoidAnimator<KattEntity, ?> getAnimator(KattEntity entity) { return animator; }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
