// Made with Blockbench 5.2.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class LatexHaku<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "latexhaku"), "main");
	private final ModelPart Head;
	private final ModelPart LeftEar;
	private final ModelPart LeftEarPivot;
	private final ModelPart RightEar;
	private final ModelPart RightEarPivot;
	private final ModelPart Hair;
	private final ModelPart Torso;
	private final ModelPart LowerTorso;
	private final ModelPart Saddle;
	private final ModelPart LeftLeg2;
	private final ModelPart LeftLowerLeg2;
	private final ModelPart LeftFoot2;
	private final ModelPart LeftPad2;
	private final ModelPart RightLeg2;
	private final ModelPart RightLowerLeg2;
	private final ModelPart RightFoot2;
	private final ModelPart RightPad2;
	private final ModelPart LeftLeg;
	private final ModelPart LeftLowerLeg;
	private final ModelPart LeftFoot;
	private final ModelPart RightLeg;
	private final ModelPart RightLowerLeg;
	private final ModelPart RightFoot;
	private final ModelPart Tail;
	private final ModelPart TailPrimary;
	private final ModelPart TailSecondary;
	private final ModelPart TailTertiary;
	private final ModelPart TailQuaternary;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;

	public LatexHaku(ModelPart root) {
		this.Head = root.getChild("Head");
		this.LeftEar = this.Head.getChild("LeftEar");
		this.LeftEarPivot = this.LeftEar.getChild("LeftEarPivot");
		this.RightEar = this.Head.getChild("RightEar");
		this.RightEarPivot = this.RightEar.getChild("RightEarPivot");
		this.Hair = this.Head.getChild("Hair");
		this.Torso = root.getChild("Torso");
		this.LowerTorso = root.getChild("LowerTorso");
		this.Saddle = this.LowerTorso.getChild("Saddle");
		this.LeftLeg2 = this.LowerTorso.getChild("LeftLeg2");
		this.LeftLowerLeg2 = this.LeftLeg2.getChild("LeftLowerLeg2");
		this.LeftFoot2 = this.LeftLowerLeg2.getChild("LeftFoot2");
		this.LeftPad2 = this.LeftFoot2.getChild("LeftPad2");
		this.RightLeg2 = this.LowerTorso.getChild("RightLeg2");
		this.RightLowerLeg2 = this.RightLeg2.getChild("RightLowerLeg2");
		this.RightFoot2 = this.RightLowerLeg2.getChild("RightFoot2");
		this.RightPad2 = this.RightFoot2.getChild("RightPad2");
		this.LeftLeg = this.LowerTorso.getChild("LeftLeg");
		this.LeftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
		this.LeftFoot = this.LeftLowerLeg.getChild("LeftFoot");
		this.RightLeg = this.LowerTorso.getChild("RightLeg");
		this.RightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
		this.RightFoot = this.RightLowerLeg.getChild("RightFoot");
		this.Tail = this.LowerTorso.getChild("Tail");
		this.TailPrimary = this.Tail.getChild("TailPrimary");
		this.TailSecondary = this.TailPrimary.getChild("TailSecondary");
		this.TailTertiary = this.TailSecondary.getChild("TailTertiary");
		this.TailQuaternary = this.TailTertiary.getChild("TailQuaternary");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(36, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(20, 44).addBox(-2.0F, -3.0F, -7.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 81).addBox(0.0F, -7.0F, 2.0F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(18, 68).addBox(0.0F, -11.0F, -3.0F, 0.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(86, 0).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -7.0F));

		PartDefinition CheekTopRight_r1 = Head.addOrReplaceChild("CheekTopRight_r1", CubeListBuilder.create().texOffs(88, 10).mirror().addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.5F, 23.0F, 11.0F, 0.4802F, 0.9719F, 0.5626F));

		PartDefinition CheekRight_r1 = Head.addOrReplaceChild("CheekRight_r1", CubeListBuilder.create().texOffs(88, 10).mirror().addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.5F, 25.0F, -4.0F, -0.4802F, 0.9719F, -0.5626F));

		PartDefinition CheekTopLeft_r1 = Head.addOrReplaceChild("CheekTopLeft_r1", CubeListBuilder.create().texOffs(88, 10).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 23.0F, 11.0F, 0.4802F, -0.9719F, -0.5626F));

		PartDefinition CheekLeft_r1 = Head.addOrReplaceChild("CheekLeft_r1", CubeListBuilder.create().texOffs(88, 10).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 25.0F, -4.0F, -0.4802F, -0.9719F, 0.5626F));

		PartDefinition WhiskerLeft_r1 = Head.addOrReplaceChild("WhiskerLeft_r1", CubeListBuilder.create().texOffs(54, 95).mirror().addBox(-2.0F, -28.0F, -7.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 25.0F, -1.0F, -0.0256F, -0.7408F, 0.1025F));

		PartDefinition WhiskerRight_r1 = Head.addOrReplaceChild("WhiskerRight_r1", CubeListBuilder.create().texOffs(54, 95).addBox(-4.0F, -28.0F, -7.0F, 6.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 25.0F, -1.0F, -0.0256F, 0.7408F, -0.1025F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -8.0F, 3.0F, 0.0F, -0.6545F, 0.0F));

		PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(76, 19).addBox(0.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 25).addBox(0.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(4.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, -0.9F, -0.6F, -0.2182F, -0.1745F, 0.4363F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -8.0F, 3.0F, 0.0F, 0.6545F, 0.0F));

		PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(76, 19).mirror().addBox(-4.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 25).mirror().addBox(-4.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 28).mirror().addBox(-5.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.2F, -0.9F, -0.6F, -0.2182F, 0.1745F, -0.4363F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(35, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
		.texOffs(28, 41).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(10, 85).addBox(0.0F, 3.0F, 2.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -7.0F));

		PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F, new CubeDeformation(0.0F))
		.texOffs(72, 80).addBox(0.0F, -5.0F, 5.0F, 0.0F, 4.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.5F, -7.0F));

		PartDefinition Saddle = LowerTorso.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 3.0F, 0.0F));

		PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 15.875F));

		PartDefinition LeftThigh_r1 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(60, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(71, 29).addBox(-1.01F, -0.125F, -1.9F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition LeftArch_r1 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(45, 76).addBox(-1.0F, -7.45F, 0.275F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(68, 52).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 15.875F));

		PartDefinition RightThigh_r1 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(60, 41).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition RightCalf_r1 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(71, 29).mirror().addBox(-0.99F, -0.125F, -1.9F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition RightArch_r1 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(45, 76).mirror().addBox(-1.0F, -7.45F, 0.275F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.005F)).mirror(false), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(68, 52).mirror().addBox(-1.0F, 0.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 0.0F, -1.7F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(55, 55).addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

		PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(58, 68).addBox(-1.0F, 3.8638F, 3.7342F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(69, 62).addBox(-0.95F, 0.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.7152F, -4.3278F));

		PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 0.0F, -1.7F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(55, 55).mirror().addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(-0.25F, 5.7848F, 3.7028F));

		PartDefinition RightLowerLeg_r1 = RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(58, 68).mirror().addBox(-1.0F, 3.8638F, 3.7342F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(69, 62).mirror().addBox(-1.05F, 0.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 5.7152F, -4.3278F));

		PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(3, 62).addBox(-2.0F, -1.9F, 0.4F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 0.0F, 15.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F))
		.texOffs(24, 71).addBox(-1.5F, -3.0F, 0.4F, 3.0F, 1.0F, 4.0F, new CubeDeformation(-0.18F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition BackFur_r1 = TailPrimary.addOrReplaceChild("BackFur_r1", CubeListBuilder.create().texOffs(86, 80).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

		PartDefinition BackFur_r2 = TailSecondary.addOrReplaceChild("BackFur_r2", CubeListBuilder.create().texOffs(86, 73).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 16.0F, -0.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition Fluff_r1 = TailSecondary.addOrReplaceChild("Fluff_r1", CubeListBuilder.create().texOffs(0, 63).addBox(-1.0F, -1.5F, -2.7F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.02F))
		.texOffs(0, 61).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

		PartDefinition BackFur_r3 = TailTertiary.addOrReplaceChild("BackFur_r3", CubeListBuilder.create().texOffs(86, 73).addBox(1.0F, -19.0F, -5.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.5F, 3.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition Fluff_r2 = TailTertiary.addOrReplaceChild("Fluff_r2", CubeListBuilder.create().texOffs(0, 61).addBox(-1.0F, -13.325F, 6.6F, 2.0F, 1.0F, 6.0F, new CubeDeformation(-0.2F))
		.texOffs(0, 61).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

		PartDefinition BackFur_r4 = TailQuaternary.addOrReplaceChild("BackFur_r4", CubeListBuilder.create().texOffs(88, 75).addBox(1.0F, -19.0F, -3.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.0F, 4.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition Fluff_r3 = TailQuaternary.addOrReplaceChild("Fluff_r3", CubeListBuilder.create().texOffs(0, 78).addBox(-0.5F, -10.45F, 14.1F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.17F))
		.texOffs(60, 43).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.5F, -7.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(39, 55).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.5F, -7.0F));

		return LayerDefinition.create(meshdefinition, 96, 96);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LowerTorso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}