// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class LatexKaiju<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "latexkaiju"), "main");
	private final ModelPart RightLeg;
	private final ModelPart RightLowerLeg;
	private final ModelPart RightFoot;
	private final ModelPart RightPad;
	private final ModelPart LeftLeg;
	private final ModelPart LeftLowerLeg;
	private final ModelPart LeftFoot;
	private final ModelPart LeftPad;
	private final ModelPart Head;
	private final ModelPart Hair;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final ModelPart TailPrimary;
	private final ModelPart TailSecondary;
	private final ModelPart TailTertiary;
	private final ModelPart TailQuaternary;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;

	public LatexKaiju(ModelPart root) {
		this.RightLeg = root.getChild("RightLeg");
		this.RightLowerLeg = this.RightLeg.getChild("RightLowerLeg");
		this.RightFoot = this.RightLowerLeg.getChild("RightFoot");
		this.RightPad = this.RightFoot.getChild("RightPad");
		this.LeftLeg = root.getChild("LeftLeg");
		this.LeftLowerLeg = this.LeftLeg.getChild("LeftLowerLeg");
		this.LeftFoot = this.LeftLowerLeg.getChild("LeftFoot");
		this.LeftPad = this.LeftFoot.getChild("LeftPad");
		this.Head = root.getChild("Head");
		this.Hair = this.Head.getChild("Hair");
		this.Torso = root.getChild("Torso");
		this.Tail = this.Torso.getChild("Tail");
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

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

		PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(40, 20).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(50, 30).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

		PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(40, 9).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

		PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

		PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

		PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(32, 50).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(50, 46).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 30).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Crystal_r1 = Head.addOrReplaceChild("Crystal_r1", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -8.6F, 2.3F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r2 = Head.addOrReplaceChild("Crystal_r2", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -2.6F, 4.3F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r3 = Head.addOrReplaceChild("Crystal_r3", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -5.6F, 4.3F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(-0.1F, 25.0F, 0.0F));

		PartDefinition Hair_r1 = Hair.addOrReplaceChild("Hair_r1", CubeListBuilder.create().texOffs(32, 59).mirror().addBox(-1.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)).mirror(false), PartPose.offsetAndRotation(3.8F, -28.0566F, 1.2163F, -0.8958F, -0.057F, -0.6635F));

		PartDefinition Hair_r2 = Hair.addOrReplaceChild("Hair_r2", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false)
		.texOffs(0, 56).mirror().addBox(1.3493F, -5.2508F, 0.1136F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false), PartPose.offsetAndRotation(2.7F, -27.9146F, 0.7522F, -0.1637F, 0.5044F, -0.4422F));

		PartDefinition Hair_r3 = Hair.addOrReplaceChild("Hair_r3", CubeListBuilder.create().texOffs(50, 39).mirror().addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(3.9F, -27.5735F, 1.1689F, -0.457F, 0.4276F, -0.7087F));

		PartDefinition Hair_r4 = Hair.addOrReplaceChild("Hair_r4", CubeListBuilder.create().texOffs(32, 59).addBox(0.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-3.6F, -28.0566F, 1.2163F, -0.8958F, 0.057F, 0.6635F));

		PartDefinition Hair_r5 = Hair.addOrReplaceChild("Hair_r5", CubeListBuilder.create().texOffs(0, 56).addBox(-2.3493F, -5.2508F, 0.1136F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F))
		.texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -27.9146F, 0.7522F, -0.1637F, -0.5044F, 0.4422F));

		PartDefinition Hair_r6 = Hair.addOrReplaceChild("Hair_r6", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-3.7F, -27.5735F, 1.1689F, -0.457F, -0.4276F, 0.7087F));

		PartDefinition Hair_r7 = Hair.addOrReplaceChild("Hair_r7", CubeListBuilder.create().texOffs(32, 59).addBox(0.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-3.6F, -28.275F, -0.3F, -0.6545F, -0.1309F, 0.6545F));

		PartDefinition Hair_r8 = Hair.addOrReplaceChild("Hair_r8", CubeListBuilder.create().texOffs(0, 56).mirror().addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)).mirror(false), PartPose.offsetAndRotation(2.7F, -30.0F, -0.7F, 0.1745F, 0.6109F, -0.2618F));

		PartDefinition Hair_r9 = Hair.addOrReplaceChild("Hair_r9", CubeListBuilder.create().texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -30.0F, -0.7F, 0.1745F, -0.6109F, 0.2618F));

		PartDefinition Hair_r10 = Hair.addOrReplaceChild("Hair_r10", CubeListBuilder.create().texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.5F, -28.0F, -0.7F, 0.1745F, -0.6109F, 0.2618F));

		PartDefinition Hair_r11 = Hair.addOrReplaceChild("Hair_r11", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-3.7F, -27.8F, -0.2F, -0.1745F, -0.6109F, 0.5672F));

		PartDefinition Hair_r12 = Hair.addOrReplaceChild("Hair_r12", CubeListBuilder.create().texOffs(62, 46).mirror().addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F)).mirror(false)
		.texOffs(62, 46).addBox(3.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F))
		.texOffs(62, 46).addBox(1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(-1.9F, -26.6F, -0.7F, -1.2217F, 0.0F, 0.0F));

		PartDefinition Hair_r13 = Hair.addOrReplaceChild("Hair_r13", CubeListBuilder.create().texOffs(42, 62).mirror().addBox(-1.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F)).mirror(false)
		.texOffs(42, 62).addBox(3.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F))
		.texOffs(42, 62).addBox(1.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(-1.9F, -30.7F, -2.6F, -1.0472F, 0.0F, 0.0F));

		PartDefinition Hair_r14 = Hair.addOrReplaceChild("Hair_r14", CubeListBuilder.create().texOffs(0, 1).mirror().addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F)).mirror(false)
		.texOffs(0, 1).addBox(3.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F))
		.texOffs(0, 1).addBox(1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(-1.9F, -27.4F, 1.5F, 0.2618F, 0.0F, 0.0F));

		PartDefinition Hair_r15 = Hair.addOrReplaceChild("Hair_r15", CubeListBuilder.create().texOffs(56, 13).mirror().addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F)).mirror(false)
		.texOffs(56, 13).addBox(3.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F))
		.texOffs(56, 13).addBox(1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F)), PartPose.offsetAndRotation(-1.9F, -27.3F, -1.5F, 0.4363F, 0.0F, 0.0F));

		PartDefinition Hair_r16 = Hair.addOrReplaceChild("Hair_r16", CubeListBuilder.create().texOffs(56, 18).mirror().addBox(-1.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F)).mirror(false)
		.texOffs(56, 18).addBox(3.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F))
		.texOffs(56, 18).addBox(1.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(-1.9F, -27.5F, -1.1F, 0.6545F, 0.0F, 0.0F));

		PartDefinition Hair_r17 = Hair.addOrReplaceChild("Hair_r17", CubeListBuilder.create().texOffs(46, 50).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(3.9F, -27.8F, -0.2F, -0.1745F, 0.6109F, -0.5672F));

		PartDefinition Hair_r18 = Hair.addOrReplaceChild("Hair_r18", CubeListBuilder.create().texOffs(16, 51).addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(2.7F, -28.0F, -0.7F, 0.1745F, 0.6109F, -0.2618F));

		PartDefinition Hair_r19 = Hair.addOrReplaceChild("Hair_r19", CubeListBuilder.create().texOffs(22, 58).addBox(-1.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(3.8F, -28.275F, -0.3F, -0.6545F, 0.1309F, -0.6545F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

		PartDefinition Crystal_r4 = Torso.addOrReplaceChild("Crystal_r4", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 7.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r5 = Torso.addOrReplaceChild("Crystal_r5", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 4.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r6 = Torso.addOrReplaceChild("Crystal_r6", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 1.9F, 2.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition FluffLeft_r1 = Torso.addOrReplaceChild("FluffLeft_r1", CubeListBuilder.create().texOffs(25, 0).mirror().addBox(1.0F, -24.0F, -2.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 24.0F, 0.25F, 0.0F, 0.3054F, 0.0F));

		PartDefinition FluffRight_r1 = Torso.addOrReplaceChild("FluffRight_r1", CubeListBuilder.create().texOffs(25, 0).addBox(-3.0F, -24.0F, -2.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 24.0F, 0.25F, 0.0F, -0.3054F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Crystal_r7 = TailPrimary.addOrReplaceChild("Crystal_r7", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -1.1F, 3.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

		PartDefinition Crystal_r8 = TailSecondary.addOrReplaceChild("Crystal_r8", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, 0.9F, 4.5F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r9 = TailSecondary.addOrReplaceChild("Crystal_r9", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.1F, 2.5F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

		PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

		PartDefinition Crystal_r10 = TailTertiary.addOrReplaceChild("Crystal_r10", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.6F, 3.5F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Crystal_r11 = TailTertiary.addOrReplaceChild("Crystal_r11", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -0.6F, 1.5F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

		PartDefinition Crystal_r12 = TailQuaternary.addOrReplaceChild("Crystal_r12", CubeListBuilder.create().texOffs(66, 42).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(5.0F, -1.1F, 2.0F, 0.0F, -0.7854F, -1.5708F));

		PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 7).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

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