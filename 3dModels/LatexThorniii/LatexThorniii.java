// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class LatexThorniii<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "latexthorniii"), "main");
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
	private final ModelPart RightEarPivot;
	private final ModelPart LeftEar;
	private final ModelPart LeftEarPivot;
	private final ModelPart Hair;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final ModelPart TailPrimary;
	private final ModelPart TailSecondary;
	private final ModelPart TailTertiary;
	private final ModelPart RightUpperTentacle;
	private final ModelPart TentacleSecondaryRU;
	private final ModelPart TentacleTertiaryRU;
	private final ModelPart TentacleQuaternaryRU;
	private final ModelPart TentaclePadRU;
	private final ModelPart RightLowerTentacle;
	private final ModelPart TentacleSecondaryRL;
	private final ModelPart TentacleTertiaryRL;
	private final ModelPart TentacleQuaternaryRL;
	private final ModelPart TentaclePadRL;
	private final ModelPart LeftUpperTentacle;
	private final ModelPart TentacleSecondaryLU;
	private final ModelPart TentacleTertiaryLU;
	private final ModelPart TentacleQuaternaryLU;
	private final ModelPart TentaclePadLU;
	private final ModelPart LeftLowerTentacle;
	private final ModelPart TentacleSecondaryLL;
	private final ModelPart TentacleTertiaryLL;
	private final ModelPart TentacleQuaternaryLL;
	private final ModelPart TentaclePadLL;
	private final ModelPart RightArm;
	private final ModelPart RightArm2;
	private final ModelPart LeftArm;
	private final ModelPart LeftArm2;

	public LatexThorniii(ModelPart root) {
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
		this.RightEarPivot = this.RightEar.getChild("RightEarPivot");
		this.LeftEar = this.Head.getChild("LeftEar");
		this.LeftEarPivot = this.LeftEar.getChild("LeftEarPivot");
		this.Hair = this.Head.getChild("Hair");
		this.Torso = root.getChild("Torso");
		this.Tail = this.Torso.getChild("Tail");
		this.TailPrimary = this.Tail.getChild("TailPrimary");
		this.TailSecondary = this.TailPrimary.getChild("TailSecondary");
		this.TailTertiary = this.TailSecondary.getChild("TailTertiary");
		this.RightUpperTentacle = this.Torso.getChild("RightUpperTentacle");
		this.TentacleSecondaryRU = this.RightUpperTentacle.getChild("TentacleSecondaryRU");
		this.TentacleTertiaryRU = this.TentacleSecondaryRU.getChild("TentacleTertiaryRU");
		this.TentacleQuaternaryRU = this.TentacleTertiaryRU.getChild("TentacleQuaternaryRU");
		this.TentaclePadRU = this.TentacleQuaternaryRU.getChild("TentaclePadRU");
		this.RightLowerTentacle = this.Torso.getChild("RightLowerTentacle");
		this.TentacleSecondaryRL = this.RightLowerTentacle.getChild("TentacleSecondaryRL");
		this.TentacleTertiaryRL = this.TentacleSecondaryRL.getChild("TentacleTertiaryRL");
		this.TentacleQuaternaryRL = this.TentacleTertiaryRL.getChild("TentacleQuaternaryRL");
		this.TentaclePadRL = this.TentacleQuaternaryRL.getChild("TentaclePadRL");
		this.LeftUpperTentacle = this.Torso.getChild("LeftUpperTentacle");
		this.TentacleSecondaryLU = this.LeftUpperTentacle.getChild("TentacleSecondaryLU");
		this.TentacleTertiaryLU = this.TentacleSecondaryLU.getChild("TentacleTertiaryLU");
		this.TentacleQuaternaryLU = this.TentacleTertiaryLU.getChild("TentacleQuaternaryLU");
		this.TentaclePadLU = this.TentacleQuaternaryLU.getChild("TentaclePadLU");
		this.LeftLowerTentacle = this.Torso.getChild("LeftLowerTentacle");
		this.TentacleSecondaryLL = this.LeftLowerTentacle.getChild("TentacleSecondaryLL");
		this.TentacleTertiaryLL = this.TentacleSecondaryLL.getChild("TentacleTertiaryLL");
		this.TentacleQuaternaryLL = this.TentacleTertiaryLL.getChild("TentacleQuaternaryLL");
		this.TentaclePadLL = this.TentacleQuaternaryLL.getChild("TentaclePadLL");
		this.RightArm = root.getChild("RightArm");
		this.RightArm2 = root.getChild("RightArm2");
		this.LeftArm = root.getChild("LeftArm");
		this.LeftArm2 = root.getChild("LeftArm2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.7F, 9.3F, 0.0F));

		PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 51).addBox(-2.0F, -0.9F, -2.55F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 1.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.75F, -4.1F));

		PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(56, 12).addBox(-1.99F, -0.9F, -2.4F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.3F, 2.6F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, 8.2F));

		PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-2.0F, -8.95F, -0.825F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.255F)), PartPose.offsetAndRotation(0.0F, 7.8F, -5.35F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 4.55F, -4.8F));

		PartDefinition PawBeans_r1 = RightPad.addOrReplaceChild("PawBeans_r1", CubeListBuilder.create().texOffs(0, 93).addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -3.1732F, -0.167F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r2 = RightPad.addOrReplaceChild("PawBeans_r2", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F))
		.texOffs(0, 93).addBox(1.175F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.775F, -2.7232F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r3 = RightPad.addOrReplaceChild("PawBeans_r3", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.575F, -2.7232F, -1.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.7F, 9.3F, 0.0F));

		PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-2.0F, -0.9F, -2.55F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 1.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.75F, -4.1F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.01F, -0.9F, -2.4F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.3F, 2.6F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, 8.2F));

		PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(61, 59).addBox(-2.0F, -8.95F, -0.825F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.255F)), PartPose.offsetAndRotation(0.0F, 7.8F, -5.35F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 4.55F, -4.8F));

		PartDefinition PawBeans_r4 = LeftPad.addOrReplaceChild("PawBeans_r4", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, -3.1732F, -0.167F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r5 = LeftPad.addOrReplaceChild("PawBeans_r5", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false)
		.texOffs(0, 93).mirror().addBox(2.225F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(-1.925F, -2.7232F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r6 = LeftPad.addOrReplaceChild("PawBeans_r6", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(-0.575F, -2.7232F, -1.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(37, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 5).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.2F, 0.0F));

		PartDefinition Fluff_r1 = Head.addOrReplaceChild("Fluff_r1", CubeListBuilder.create().texOffs(88, 27).addBox(-7.0F, -4.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -5.3F, -4.0F, 0.0F, 0.6545F, 0.0F));

		PartDefinition Fluff_r2 = Head.addOrReplaceChild("Fluff_r2", CubeListBuilder.create().texOffs(88, 37).mirror().addBox(3.0F, -4.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -0.3F, -2.0F, 0.0F, -0.6109F, 0.0F));

		PartDefinition Fluff_r3 = Head.addOrReplaceChild("Fluff_r3", CubeListBuilder.create().texOffs(88, 18).mirror().addBox(3.0F, -4.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -1.3F, -1.0F, 0.0F, -0.6109F, 0.0F));

		PartDefinition Fluff_r4 = Head.addOrReplaceChild("Fluff_r4", CubeListBuilder.create().texOffs(88, 18).addBox(-7.0F, -4.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.3F, -1.0F, 0.0F, 0.6109F, 0.0F));

		PartDefinition Fluff_r5 = Head.addOrReplaceChild("Fluff_r5", CubeListBuilder.create().texOffs(88, 37).addBox(-7.0F, -4.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -0.3F, -2.0F, 0.0F, 0.6109F, 0.0F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

		PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(40, 22).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(42, 4).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
		.texOffs(0, 21).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(24, 0).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

		PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(12, 32).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(48, 12).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
		.texOffs(24, 22).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(24, 2).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
		.texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.1F, -2.2F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -2.2F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(16, 56).addBox(-2.0F, 1.15F, -1.4F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 5.3F));

		PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 0).addBox(-2.5F, 0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.5F, -0.8F, 1.4835F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 5.2F));

		PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(32, 60).addBox(-2.0F, 0.1F, -2.35F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.8326F, 0.0F, 0.0F));

		PartDefinition RightUpperTentacle = Torso.addOrReplaceChild("RightUpperTentacle", CubeListBuilder.create(), PartPose.offset(-2.5F, 2.7F, 1.0F));

		PartDefinition TentaclePart_r1 = RightUpperTentacle.addOrReplaceChild("TentaclePart_r1", CubeListBuilder.create().texOffs(68, 68).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.192F, -0.4363F, -0.0524F));

		PartDefinition TentacleSecondaryRU = RightUpperTentacle.addOrReplaceChild("TentacleSecondaryRU", CubeListBuilder.create(), PartPose.offset(-1.5F, -0.5F, 3.3F));

		PartDefinition TentaclePart_r2 = TentacleSecondaryRU.addOrReplaceChild("TentaclePart_r2", CubeListBuilder.create().texOffs(32, 22).addBox(0.0F, -1.0F, 3.55F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.5F, -3.3F, 0.2269F, -0.6981F, -0.1134F));

		PartDefinition TentacleTertiaryRU = TentacleSecondaryRU.addOrReplaceChild("TentacleTertiaryRU", CubeListBuilder.create(), PartPose.offset(-2.4F, -0.7F, 2.7F));

		PartDefinition TentaclePart_r3 = TentacleTertiaryRU.addOrReplaceChild("TentaclePart_r3", CubeListBuilder.create().texOffs(56, 68).addBox(1.8F, -1.0F, 6.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, 1.2F, -6.0F, 0.2967F, -0.9425F, -0.2094F));

		PartDefinition TentacleQuaternaryRU = TentacleTertiaryRU.addOrReplaceChild("TentacleQuaternaryRU", CubeListBuilder.create(), PartPose.offset(-3.0F, -0.5F, 2.2F));

		PartDefinition TentaclePart_r4 = TentacleQuaternaryRU.addOrReplaceChild("TentaclePart_r4", CubeListBuilder.create().texOffs(34, 68).addBox(4.45F, -1.0F, 9.375F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.9F, 1.7F, -8.2F, 0.4712F, -1.1868F, -0.4102F));

		PartDefinition TentaclePadRU = TentacleQuaternaryRU.addOrReplaceChild("TentaclePadRU", CubeListBuilder.create(), PartPose.offset(-3.6F, -0.4F, 1.3F));

		PartDefinition TentaclePart_r5 = TentaclePadRU.addOrReplaceChild("TentaclePart_r5", CubeListBuilder.create().texOffs(0, 16).addBox(6.075F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 22).addBox(6.075F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.5F, 2.1F, -9.5F, 0.6807F, -1.2915F, -0.6283F));

		PartDefinition RightLowerTentacle = Torso.addOrReplaceChild("RightLowerTentacle", CubeListBuilder.create(), PartPose.offset(-2.5F, 6.7F, 1.0F));

		PartDefinition TentaclePart_r6 = RightLowerTentacle.addOrReplaceChild("TentaclePart_r6", CubeListBuilder.create().texOffs(72, 44).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.192F, -0.4276F, 0.0524F));

		PartDefinition TentacleSecondaryRL = RightLowerTentacle.addOrReplaceChild("TentacleSecondaryRL", CubeListBuilder.create(), PartPose.offset(-1.5F, 0.5F, 3.3F));

		PartDefinition TentaclePart_r7 = TentacleSecondaryRL.addOrReplaceChild("TentaclePart_r7", CubeListBuilder.create().texOffs(65, 31).addBox(0.0F, -1.0F, 3.6F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -0.5F, -3.3F, -0.2182F, -0.6894F, 0.1134F));

		PartDefinition TentacleTertiaryRL = TentacleSecondaryRL.addOrReplaceChild("TentacleTertiaryRL", CubeListBuilder.create(), PartPose.offset(-2.4F, 0.7F, 2.9F));

		PartDefinition TentaclePart_r8 = TentacleTertiaryRL.addOrReplaceChild("TentaclePart_r8", CubeListBuilder.create().texOffs(72, 37).addBox(1.9F, -1.05F, 6.8F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -1.2F, -6.2F, -0.2967F, -0.9425F, 0.2094F));

		PartDefinition TentacleQuaternaryRL = TentacleTertiaryRL.addOrReplaceChild("TentacleQuaternaryRL", CubeListBuilder.create(), PartPose.offset(-2.9F, 0.4F, 2.0F));

		PartDefinition TentaclePart_r9 = TentacleQuaternaryRL.addOrReplaceChild("TentaclePart_r9", CubeListBuilder.create().texOffs(72, 18).addBox(4.525F, -1.0F, 9.3F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.8F, -1.6F, -8.2F, -0.4712F, -1.1868F, 0.4102F));

		PartDefinition TentaclePadRL = TentacleQuaternaryRL.addOrReplaceChild("TentaclePadRL", CubeListBuilder.create(), PartPose.offset(-3.7F, 0.3F, 1.3F));

		PartDefinition TentaclePart_r10 = TentaclePadRL.addOrReplaceChild("TentaclePart_r10", CubeListBuilder.create().texOffs(0, 73).addBox(6.15F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 48).addBox(6.15F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.5F, -1.9F, -9.5F, -0.6807F, -1.2915F, 0.6283F));

		PartDefinition LeftUpperTentacle = Torso.addOrReplaceChild("LeftUpperTentacle", CubeListBuilder.create(), PartPose.offset(2.5F, 2.7F, 1.0F));

		PartDefinition TentaclePart_r11 = LeftUpperTentacle.addOrReplaceChild("TentaclePart_r11", CubeListBuilder.create().texOffs(68, 6).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.192F, 0.4363F, 0.0524F));

		PartDefinition TentacleSecondaryLU = LeftUpperTentacle.addOrReplaceChild("TentacleSecondaryLU", CubeListBuilder.create(), PartPose.offset(1.5F, -0.5F, 3.3F));

		PartDefinition TentaclePart_r12 = TentacleSecondaryLU.addOrReplaceChild("TentaclePart_r12", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -1.0F, 3.55F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 0.5F, -3.3F, 0.2269F, 0.6981F, 0.1134F));

		PartDefinition TentacleTertiaryLU = TentacleSecondaryLU.addOrReplaceChild("TentacleTertiaryLU", CubeListBuilder.create(), PartPose.offset(2.4F, -0.7F, 2.7F));

		PartDefinition TentaclePart_r13 = TentacleTertiaryLU.addOrReplaceChild("TentaclePart_r13", CubeListBuilder.create().texOffs(68, 0).addBox(-3.8F, -1.0F, 6.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9F, 1.2F, -6.0F, 0.2967F, 0.9425F, 0.2094F));

		PartDefinition TentacleQuaternaryLU = TentacleTertiaryLU.addOrReplaceChild("TentacleQuaternaryLU", CubeListBuilder.create(), PartPose.offset(3.0F, -0.5F, 2.2F));

		PartDefinition TentaclePart_r14 = TentacleQuaternaryLU.addOrReplaceChild("TentaclePart_r14", CubeListBuilder.create().texOffs(22, 67).addBox(-6.45F, -1.0F, 9.375F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.9F, 1.7F, -8.2F, 0.4712F, 1.1868F, 0.4102F));

		PartDefinition TentaclePadLU = TentacleQuaternaryLU.addOrReplaceChild("TentaclePadLU", CubeListBuilder.create(), PartPose.offset(3.6F, -0.4F, 1.3F));

		PartDefinition TentaclePart_r15 = TentaclePadLU.addOrReplaceChild("TentaclePart_r15", CubeListBuilder.create().texOffs(0, 0).addBox(-8.075F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 62).addBox(-8.075F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.5F, 2.1F, -9.5F, 0.6807F, 1.2915F, 0.6283F));

		PartDefinition LeftLowerTentacle = Torso.addOrReplaceChild("LeftLowerTentacle", CubeListBuilder.create(), PartPose.offset(2.5F, 6.7F, 1.0F));

		PartDefinition TentaclePart_r16 = LeftLowerTentacle.addOrReplaceChild("TentaclePart_r16", CubeListBuilder.create().texOffs(72, 12).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.192F, 0.4276F, -0.0524F));

		PartDefinition TentacleSecondaryLL = LeftLowerTentacle.addOrReplaceChild("TentacleSecondaryLL", CubeListBuilder.create(), PartPose.offset(1.5F, 0.5F, 3.3F));

		PartDefinition TentaclePart_r17 = TentacleSecondaryLL.addOrReplaceChild("TentaclePart_r17", CubeListBuilder.create().texOffs(14, 65).addBox(-2.0F, -1.0F, 3.6F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.5F, -3.3F, -0.2182F, 0.6894F, -0.1134F));

		PartDefinition TentacleTertiaryLL = TentacleSecondaryLL.addOrReplaceChild("TentacleTertiaryLL", CubeListBuilder.create(), PartPose.offset(2.4F, 0.7F, 2.9F));

		PartDefinition TentaclePart_r18 = TentacleTertiaryLL.addOrReplaceChild("TentaclePart_r18", CubeListBuilder.create().texOffs(42, 71).addBox(-3.9F, -1.05F, 6.8F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9F, -1.2F, -6.2F, -0.2967F, 0.9425F, -0.2094F));

		PartDefinition TentacleQuaternaryLL = TentacleTertiaryLL.addOrReplaceChild("TentacleQuaternaryLL", CubeListBuilder.create(), PartPose.offset(2.9F, 0.4F, 2.0F));

		PartDefinition TentaclePart_r19 = TentacleQuaternaryLL.addOrReplaceChild("TentaclePart_r19", CubeListBuilder.create().texOffs(10, 71).addBox(-6.525F, -1.0F, 9.3F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, -1.6F, -8.2F, -0.4712F, 1.1868F, -0.4102F));

		PartDefinition TentaclePadLL = TentacleQuaternaryLL.addOrReplaceChild("TentaclePadLL", CubeListBuilder.create(), PartPose.offset(3.7F, 0.3F, 1.3F));

		PartDefinition TentaclePart_r20 = TentaclePadLL.addOrReplaceChild("TentaclePart_r20", CubeListBuilder.create().texOffs(72, 57).addBox(-8.15F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(64, 39).addBox(-8.15F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.5F, -1.9F, -9.5F, -0.6807F, 1.2915F, -0.6283F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(32, 44).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.2F, 3.9F, -0.2F));

		PartDefinition PawBeans_r7 = RightArm.addOrReplaceChild("PawBeans_r7", CubeListBuilder.create().texOffs(0, 93).addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.05F, 4.4268F, 0.333F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r8 = RightArm.addOrReplaceChild("PawBeans_r8", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F))
		.texOffs(0, 93).addBox(1.175F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-1.825F, 4.8768F, -0.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r9 = RightArm.addOrReplaceChild("PawBeans_r9", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.475F, 4.8768F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(0, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.2F, -0.1F, -0.2F));

		PartDefinition PawBeans_r10 = RightArm2.addOrReplaceChild("PawBeans_r10", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F))
		.texOffs(0, 93).addBox(-4.225F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.875F, 4.8768F, -0.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r11 = RightArm2.addOrReplaceChild("PawBeans_r11", CubeListBuilder.create().texOffs(0, 93).addBox(-1.525F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.475F, 4.8768F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r12 = RightArm2.addOrReplaceChild("PawBeans_r12", CubeListBuilder.create().texOffs(0, 93).addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.05F, 4.4268F, 0.333F, 1.5708F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 40).addBox(-0.8F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, 3.9F, -0.2F));

		PartDefinition PawBeans_r13 = LeftArm.addOrReplaceChild("PawBeans_r13", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(1.25F, 4.4268F, 0.333F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r14 = LeftArm.addOrReplaceChild("PawBeans_r14", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false)
		.texOffs(0, 93).mirror().addBox(2.225F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(-0.675F, 4.8768F, -0.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r15 = LeftArm.addOrReplaceChild("PawBeans_r15", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(0.675F, 4.8768F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(0, 32).addBox(-0.8F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, -0.1F, -0.2F));

		PartDefinition PawBeans_r16 = LeftArm2.addOrReplaceChild("PawBeans_r16", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false)
		.texOffs(0, 93).mirror().addBox(-3.175F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(2.025F, 4.8768F, -0.6F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r17 = LeftArm2.addOrReplaceChild("PawBeans_r17", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-0.475F, -1.1768F, -5.7F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.4F)).mirror(false), PartPose.offsetAndRotation(0.675F, 4.8768F, -1.1F, 1.5708F, 0.0F, 0.0F));

		PartDefinition PawBeans_r18 = LeftArm2.addOrReplaceChild("PawBeans_r18", CubeListBuilder.create().texOffs(0, 93).mirror().addBox(-1.0F, -0.4268F, -5.833F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(false), PartPose.offsetAndRotation(1.25F, 4.4268F, 0.333F, 1.5708F, 0.0F, 0.0F));

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
		RightArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}