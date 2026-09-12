// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class FluffedUpLatexSnowLeopardMale<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "fluffeduplatexsnowleopardmale"), "main");
	private final ModelPart RightLeg;
	private final ModelPart RightLowerLeg;
	private final ModelPart RightFoot;
	private final ModelPart RightPad;
	private final ModelPart LeftLeg;
	private final ModelPart LeftLowerLeg;
	private final ModelPart LeftFoot;
	private final ModelPart LeftPad;
	private final ModelPart Head;
	private final ModelPart LeftEar;
	private final ModelPart RightEar;
	private final ModelPart Hair;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final ModelPart TailPrimary;
	private final ModelPart TailSecondary;
	private final ModelPart TailTertiary;
	private final ModelPart TailQuaternary;
	private final ModelPart Plantoids;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;

	public FluffedUpLatexSnowLeopardMale(ModelPart root) {
		this.RightLeg = root.getChild("RightLeg");
		this.RightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
		this.RightFoot = this.RightLowerLeg.getChild("RightFoot");
		this.RightPad = this.RightFoot.getChild("RightPad");
		this.LeftLeg = root.getChild("LeftLeg");
		this.LeftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
		this.LeftFoot = this.LeftLowerLeg.getChild("LeftFoot");
		this.LeftPad = this.LeftFoot.getChild("LeftPad");
		this.Head = root.getChild("Head");
		this.LeftEar = this.Head.getChild("LeftEar");
		this.RightEar = this.Head.getChild("RightEar");
		this.Hair = this.Head.getChild("Hair");
		this.Torso = root.getChild("Torso");
		this.Tail = this.Torso.getChild("Tail");
		this.TailPrimary = this.Tail.getChild("TailPrimary");
		this.TailSecondary = this.TailPrimary.getChild("TailSecondary");
		this.TailTertiary = this.TailSecondary.getChild("TailTertiary");
		this.TailQuaternary = this.TailTertiary.getChild("TailQuaternary");
		this.Plantoids = this.Torso.getChild("Plantoids");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

		PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 51).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

		PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(16, 57).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(15, 32).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(0, 20).mirror().addBox(-2.0F, -30.0F, -6.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 26.0F, 3.0F, 0.0F, -0.48F, 0.0F));

		PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(24, 19).mirror().addBox(-2.0F, -31.0F, -6.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 26.0F, 4.0F, 0.0F, -0.48F, 0.0F));

		PartDefinition Snout_r4 = Head.addOrReplaceChild("Snout_r4", CubeListBuilder.create().texOffs(24, 19).addBox(-3.0F, -31.0F, -6.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 26.0F, 4.0F, 0.0F, 0.48F, 0.0F));

		PartDefinition Snout_r5 = Head.addOrReplaceChild("Snout_r5", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -30.0F, -6.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 26.0F, 3.0F, 0.0F, 0.48F, 0.0F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));

		PartDefinition left ear_r1 = LeftEar.addOrReplaceChild("left ear_r1", CubeListBuilder.create().texOffs(46, 61).addBox(-7.75F, -34.75F, -1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));

		PartDefinition right ear_r1 = RightEar.addOrReplaceChild("right ear_r1", CubeListBuilder.create().texOffs(61, 48).addBox(5.75F, -34.75F, -1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 78).addBox(-4.5F, -32.9F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.35F))
		.texOffs(39, 77).addBox(-4.5F, -33.9F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.5F, 26.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 37).addBox(-4.0F, 9.1F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.1F))
		.texOffs(28, 28).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 31).addBox(-4.0F, 4.5F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

		PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 0).addBox(-2.5F, -0.45F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

		PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5272F, 0.0F, 0.0F));

		PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));

		PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(30, 60).addBox(-2.0F, 5.5F, -3.8F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.7017F, 0.0F, 0.0F));

		PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -2.0F));

		PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
		.texOffs(30, 64).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

		PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(39, 79).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 41).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 96, 96);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

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