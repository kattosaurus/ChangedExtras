// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package us.kattosaur.changedextras.client.model;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.client.renderer.GlobalAnimationHelper;
import us.kattosaur.changedextras.entity.beasts.VoidCatEntity;
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

public class VoidCatEntityModel extends AdvancedHumanoidModel<VoidCatEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "void_cat"), "main");
	public static final ModelLayerLocation OUTLINE_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ChangedExtras.MODID, "void_cat_outline"), "main");
	private final ModelPart RightLeg;
	private final ModelPart RightLowerLeg;
	private final ModelPart RightFoot;
	private final ModelPart RightPad;
	private final ModelPart LeftLeg;
	private final ModelPart LeftLowerLeg;
	private final ModelPart LeftFoot;
	private final ModelPart LeftPad;
	private final ModelPart Head;
	private final ModelPart RightEar;
	private final ModelPart LeftEar;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final ModelPart TailPrimary;
	private final ModelPart TailSecondary;
	private final ModelPart TailTertiary;
	private final ModelPart TailQuaternary;
	private final ModelPart RightArm;
	private final ModelPart RightPawBeans;
	private final ModelPart LeftArm;
	private final ModelPart LeftPawBeans;

	private final HumanoidAnimator<VoidCatEntity, VoidCatEntityModel> animator;

	public VoidCatEntityModel(ModelPart root) {
		super(root);
		this.RightLeg = root.getChild("RightLeg");
		this.RightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
		this.RightFoot = this.RightLowerLeg.getChild("RightFoot");
		this.RightPad = this.RightFoot.getChild("RightPad");
		this.LeftLeg = root.getChild("LeftLeg");
		this.LeftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
		this.LeftFoot = this.LeftLowerLeg.getChild("LeftFoot");
		this.LeftPad = this.LeftFoot.getChild("LeftPad");
		this.Head = root.getChild("Head");
		this.RightEar = this.Head.getChild("RightEar");
		this.LeftEar = this.Head.getChild("LeftEar");
		this.Torso = root.getChild("Torso");
		this.Tail = this.Torso.getChild("Tail");
		this.TailPrimary = this.Tail.getChild("TailPrimary");
		this.TailSecondary = this.TailPrimary.getChild("TailSecondary");
		this.TailTertiary = this.TailSecondary.getChild("TailTertiary");
		this.TailQuaternary = this.TailTertiary.getChild("TailQuaternary");
		this.RightArm = root.getChild("RightArm");
		this.RightPawBeans = this.RightArm.getChild("RightPawBeans");
		this.LeftArm = root.getChild("LeftArm");
		this.LeftPawBeans = this.LeftArm.getChild("LeftPawBeans");
		this.animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
				.addPreset(AnimatorPresets.catLike(Head, LeftEar, RightEar, Torso, LeftArm, RightArm,
						Tail, List.of(TailPrimary, TailSecondary, TailTertiary, TailQuaternary),
						LeftLeg, LeftLowerLeg, LeftFoot, LeftPad, RightLeg, RightLowerLeg, RightFoot, RightPad));
	}

	public static LayerDefinition createBodyLayer() { return createBodyLayer(false); }
	public static LayerDefinition createOutlineLayer() { return createBodyLayer(true); }
	private static LayerDefinition createBodyLayer(boolean outline) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

		PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 50).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(44, 60).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition RightPawPads = RightPad.addOrReplaceChild("RightPawPads", CubeListBuilder.create().texOffs(8, 87).mirror().addBox(-7.0F, -13.025F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 83).mirror().addBox(-7.8F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 81).mirror().addBox(-6.5F, -13.025F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 85).mirror().addBox(-5.225F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(6.0F, 14.0F, -0.05F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

		PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(13, 56).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition LeftPawPads = LeftPad.addOrReplaceChild("LeftPawPads", CubeListBuilder.create().texOffs(8, 87).addBox(5.0F, -13.025F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
		.texOffs(8, 83).addBox(6.8F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
		.texOffs(8, 81).addBox(5.5F, -13.025F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
		.texOffs(8, 85).addBox(4.225F, -13.025F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-6.0F, 14.0F, -0.05F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(outline ? 0.1F : 0.0F))
		.texOffs(0, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(outline ? 0.1F : 0.0F))
		.texOffs(32, 22).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));

		PartDefinition RightEarFur_r1 = RightEar.addOrReplaceChild("RightEarFur_r1", CubeListBuilder.create().texOffs(86, 35).mirror().addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -1.8F, 0.75F, -0.0417F, 0.9992F, -0.3409F));

		PartDefinition rightEar_r1 = RightEar.addOrReplaceChild("rightEar_r1", CubeListBuilder.create().texOffs(0, 59).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));

		PartDefinition LeftEarFur_r1 = LeftEar.addOrReplaceChild("LeftEarFur_r1", CubeListBuilder.create().texOffs(86, 35).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(2.0F, -1.8F, 0.75F, -0.0417F, -0.9992F, 0.3409F));

		PartDefinition leftEar_r1 = LeftEar.addOrReplaceChild("leftEar_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.5F, 26.0F, 0.0F));

		PartDefinition LeftCheekFur = Head.addOrReplaceChild("LeftCheekFur", CubeListBuilder.create(), PartPose.offset(3.5F, -2.0F, -2.8F));

		PartDefinition Lower_r1 = LeftCheekFur.addOrReplaceChild("Lower_r1", CubeListBuilder.create().texOffs(84, 1).addBox(0.45F, -1.5F, -1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, 0.4451F, 0.0F));

		PartDefinition Upper_r1 = LeftCheekFur.addOrReplaceChild("Upper_r1", CubeListBuilder.create().texOffs(84, -3).addBox(0.45F, -0.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.5995F, 0.2317F, 0.3404F));

		PartDefinition Middle_r1 = LeftCheekFur.addOrReplaceChild("Middle_r1", CubeListBuilder.create().texOffs(84, -1).addBox(0.45F, -2.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.1F, 0.1F, 1.3F, 0.1309F, 0.3403F, 0.0F));

		PartDefinition RightCheekFur = Head.addOrReplaceChild("RightCheekFur", CubeListBuilder.create(), PartPose.offset(-3.5F, -2.0F, -2.8F));

		PartDefinition Lower_r2 = RightCheekFur.addOrReplaceChild("Lower_r2", CubeListBuilder.create().texOffs(84, 1).mirror().addBox(-0.45F, -1.5F, -1.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1047F, -0.4451F, 0.0F));

		PartDefinition Upper_r2 = RightCheekFur.addOrReplaceChild("Upper_r2", CubeListBuilder.create().texOffs(84, -3).mirror().addBox(-0.45F, -0.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.5995F, -0.2317F, -0.3404F));

		PartDefinition Middle_r2 = RightCheekFur.addOrReplaceChild("Middle_r2", CubeListBuilder.create().texOffs(84, -1).mirror().addBox(-0.45F, -2.5F, -1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(-0.1F, 0.1F, 1.3F, 0.1309F, -0.3403F, 0.0F));

		PartDefinition WhiskersLeft = Head.addOrReplaceChild("WhiskersLeft", CubeListBuilder.create(), PartPose.offset(4.5F, -1.0F, -2.5F));

		PartDefinition whiskerleft_r1 = WhiskersLeft.addOrReplaceChild("whiskerleft_r1", CubeListBuilder.create().texOffs(0, 67).addBox(-1.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1931F, -0.2926F, -0.5958F));

		PartDefinition whiskerleft_r2 = WhiskersLeft.addOrReplaceChild("whiskerleft_r2", CubeListBuilder.create().texOffs(0, 67).addBox(-1.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, -0.3491F, 0.0F));

		PartDefinition whiskerleft_r3 = WhiskersLeft.addOrReplaceChild("whiskerleft_r3", CubeListBuilder.create().texOffs(0, 67).addBox(-1.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(-2.0F, 1.0F, -1.0F, -0.2298F, -0.2651F, 0.7289F));

		PartDefinition WhiskersRight = Head.addOrReplaceChild("WhiskersRight", CubeListBuilder.create(), PartPose.offset(-4.5F, -1.0F, -2.5F));

		PartDefinition whiskerright_r1 = WhiskersRight.addOrReplaceChild("whiskerright_r1", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1931F, 0.2926F, 0.5958F));

		PartDefinition whiskerright_r2 = WhiskersRight.addOrReplaceChild("whiskerright_r2", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

		PartDefinition whiskerright_r3 = WhiskersRight.addOrReplaceChild("whiskerright_r3", CubeListBuilder.create().texOffs(0, 67).mirror().addBox(-2.0F, -2.5F, -0.5F, 3.0F, 1.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 1.0F, -1.0F, -0.2298F, 0.2651F, -0.7289F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition NeckFur_r1 = Torso.addOrReplaceChild("NeckFur_r1", CubeListBuilder.create().texOffs(11, 71).addBox(-2.5F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(-0.5F, 5.0F, -1.9F, 0.2618F, 0.0F, 0.0F));

		PartDefinition NeckFur_r2 = Torso.addOrReplaceChild("NeckFur_r2", CubeListBuilder.create().texOffs(11, 66).addBox(-2.5F, -2.5F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offsetAndRotation(-0.5F, 2.5F, -2.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(28, 55).addBox(-2.0F, 0.75F, -1.4F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

		PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 22).addBox(-2.0F, -0.25F, -1.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

		PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 5.05F, -2.8F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5184F, 0.0F, 0.0F));

		PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));

		PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, 4.6F, -3.6F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.12F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.6581F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

		PartDefinition RightHandFur = RightArm.addOrReplaceChild("RightHandFur", CubeListBuilder.create(), PartPose.offset(-2.0F, 7.5F, -2.3F));

		PartDefinition RightArmFur_r1 = RightHandFur.addOrReplaceChild("RightArmFur_r1", CubeListBuilder.create().texOffs(86, 27).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(3.3F, 0.0F, 3.3F, 0.0F, -1.5708F, 0.0873F));

		PartDefinition RightArmFur_r2 = RightHandFur.addOrReplaceChild("RightArmFur_r2", CubeListBuilder.create().texOffs(86, 22).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-1.3F, 0.0F, 3.3F, 0.0F, 1.5708F, -0.0873F));

		PartDefinition RightArmFur_r3 = RightHandFur.addOrReplaceChild("RightArmFur_r3", CubeListBuilder.create().texOffs(86, 17).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 3.6F, -0.0873F, 0.0F, 0.0F));

		PartDefinition RightArmFur_r4 = RightHandFur.addOrReplaceChild("RightArmFur_r4", CubeListBuilder.create().texOffs(86, 12).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition RightPawBeans = RightArm.addOrReplaceChild("RightPawBeans", CubeListBuilder.create().texOffs(8, 87).mirror().addBox(-2.0F, 9.975F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 83).mirror().addBox(-2.8F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 81).mirror().addBox(-1.5F, 9.975F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false)
		.texOffs(8, 85).mirror().addBox(-0.225F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)).mirror(false), PartPose.offset(0.0F, -0.9F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(outline ? 0.1F : 0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

		PartDefinition LeftHandFur = LeftArm.addOrReplaceChild("LeftHandFur", CubeListBuilder.create(), PartPose.offset(2.0F, 7.5F, -2.3F));

		PartDefinition LeftArmFur_r1 = LeftHandFur.addOrReplaceChild("LeftArmFur_r1", CubeListBuilder.create().texOffs(86, 27).mirror().addBox(-1.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)).mirror(false), PartPose.offsetAndRotation(-3.3F, 0.0F, 3.3F, 0.0F, 1.5708F, -0.0873F));

		PartDefinition LeftArmFur_r2 = LeftHandFur.addOrReplaceChild("LeftArmFur_r2", CubeListBuilder.create().texOffs(86, 22).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.3F, 0.0F, 3.3F, 0.0F, -1.5708F, 0.0873F));

		PartDefinition LeftArmFur_r3 = LeftHandFur.addOrReplaceChild("LeftArmFur_r3", CubeListBuilder.create().texOffs(86, 17).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 3.6F, -0.0873F, 0.0F, 0.0F));

		PartDefinition LeftArmFur_r4 = LeftHandFur.addOrReplaceChild("LeftArmFur_r4", CubeListBuilder.create().texOffs(86, 12).addBox(-3.0F, -3.0F, 0.025F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition LeftPawBeans = LeftArm.addOrReplaceChild("LeftPawBeans", CubeListBuilder.create().texOffs(8, 87).addBox(0.0F, 9.975F, -0.375F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
		.texOffs(8, 83).addBox(1.8F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
		.texOffs(8, 81).addBox(0.5F, 9.975F, -1.875F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
		.texOffs(8, 85).addBox(-0.775F, 9.975F, -1.625F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(0.0F, -0.9F, 0.0F));

		return LayerDefinition.create(meshdefinition, 96, 96);
	}

	@Override
	public void setupHand(VoidCatEntity entity) { animator.setupHand(); }
	public void setupAnim(@NotNull VoidCatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		GlobalAnimationHelper.apply(this, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, true);
	}
	public ModelPart getArm(HumanoidArm side) { return side == HumanoidArm.LEFT ? LeftArm : RightArm; }
	public ModelPart getLeg(HumanoidArm side) { return side == HumanoidArm.LEFT ? LeftLeg : RightLeg; }
	public ModelPart getHead() { return Head; }
	public ModelPart getTorso() { return Torso; }
	public HumanoidAnimator<VoidCatEntity, ?> getAnimator(VoidCatEntity entity) { return animator; }
	public void hideDecorationsForOutline() {
		Head.getChild("Hair").visible = false;
		Head.getChild("LeftCheekFur").visible = false;
		Head.getChild("RightCheekFur").visible = false;
		Head.getChild("WhiskersLeft").visible = false;
		Head.getChild("WhiskersRight").visible = false;
		Head.getChild("RightEar").getChild("RightEarFur_r1").visible = false;
		Head.getChild("LeftEar").getChild("LeftEarFur_r1").visible = false;
		Torso.getChild("NeckFur_r1").visible = false;
		Torso.getChild("NeckFur_r2").visible = false;
		RightArm.getChild("RightHandFur").visible = false;
		LeftArm.getChild("LeftHandFur").visible = false;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
