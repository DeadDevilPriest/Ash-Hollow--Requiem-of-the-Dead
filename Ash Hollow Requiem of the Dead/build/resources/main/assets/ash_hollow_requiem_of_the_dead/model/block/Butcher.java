// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class Butcher<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "butcher"), "main");
	private final ModelPart Body;
	private final ModelPart Head;
	private final ModelPart jaw;
	private final ModelPart teeth;
	private final ModelPart Teeth_left_1;
	private final ModelPart Teeth_left_2;
	private final ModelPart Teeth_right_2;
	private final ModelPart Teeth_right_1;
	private final ModelPart Ears;
	private final ModelPart left;
	private final ModelPart left2;
	private final ModelPart Torso;
	private final ModelPart leg_r;
	private final ModelPart upperbone_r;
	private final ModelPart lowerbone_r;
	private final ModelPart foot_r;
	private final ModelPart leg_l;
	private final ModelPart upperbone_l;
	private final ModelPart lowerbone_l;
	private final ModelPart foot_l;
	private final ModelPart arm_l;
	private final ModelPart upperarm_l;
	private final ModelPart lowerarm_l;
	private final ModelPart hook;
	private final ModelPart arm_r;
	private final ModelPart upperarm_r;
	private final ModelPart lowerarm_r;
	private final ModelPart spik;

	public Butcher(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Head = this.Body.getChild("Head");
		this.jaw = this.Head.getChild("jaw");
		this.teeth = this.jaw.getChild("teeth");
		this.Teeth_left_1 = this.teeth.getChild("Teeth_left_1");
		this.Teeth_left_2 = this.teeth.getChild("Teeth_left_2");
		this.Teeth_right_2 = this.teeth.getChild("Teeth_right_2");
		this.Teeth_right_1 = this.teeth.getChild("Teeth_right_1");
		this.Ears = this.Head.getChild("Ears");
		this.left = this.Ears.getChild("left");
		this.left2 = this.Ears.getChild("left2");
		this.Torso = this.Body.getChild("Torso");
		this.leg_r = this.Torso.getChild("leg_r");
		this.upperbone_r = this.leg_r.getChild("upperbone_r");
		this.lowerbone_r = this.upperbone_r.getChild("lowerbone_r");
		this.foot_r = this.lowerbone_r.getChild("foot_r");
		this.leg_l = this.Torso.getChild("leg_l");
		this.upperbone_l = this.leg_l.getChild("upperbone_l");
		this.lowerbone_l = this.upperbone_l.getChild("lowerbone_l");
		this.foot_l = this.lowerbone_l.getChild("foot_l");
		this.arm_l = this.Torso.getChild("arm_l");
		this.upperarm_l = this.arm_l.getChild("upperarm_l");
		this.lowerarm_l = this.upperarm_l.getChild("lowerarm_l");
		this.hook = this.lowerarm_l.getChild("hook");
		this.arm_r = this.Torso.getChild("arm_r");
		this.upperarm_r = this.arm_r.getChild("upperarm_r");
		this.lowerarm_r = this.upperarm_r.getChild("lowerarm_r");
		this.spik = this.lowerarm_r.getChild("spik");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -6.0F, -16.0F, 14.0F, 5.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -48.0F, 5.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition jaw = Head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 0.0F, -19.0F, 14.0F, 1.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 3.0F));

		PartDefinition teeth = jaw.addOrReplaceChild("teeth", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -8.0F));

		PartDefinition Teeth_left_1 = teeth.addOrReplaceChild("Teeth_left_1", CubeListBuilder.create().texOffs(0, 0).addBox(0.4F, -0.9F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.25F, -7.0F, -4.0F));

		PartDefinition cube_r1 = Teeth_left_1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(1.4314F, 1.9841F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r2 = Teeth_left_1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(0.5689F, -3.9143F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition Teeth_left_2 = teeth.addOrReplaceChild("Teeth_left_2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.1F, 1.1F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.25F, -7.0F, -8.0F));

		PartDefinition cube_r3 = Teeth_left_2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5585F, -1.2579F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r4 = Teeth_left_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(1.4314F, 3.2841F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition Teeth_right_2 = teeth.addOrReplaceChild("Teeth_right_2", CubeListBuilder.create().texOffs(0, 0).addBox(-14.4F, 1.2F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.25F, -7.0F, -8.0F));

		PartDefinition cube_r5 = Teeth_right_2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.4415F, -1.1579F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r6 = Teeth_right_2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-3.4314F, 3.0841F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.4F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition Teeth_right_1 = teeth.addOrReplaceChild("Teeth_right_1", CubeListBuilder.create().texOffs(0, 0).addBox(-14.8F, -0.75F, 3.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.25F, -7.0F, -8.0F));

		PartDefinition cube_r7 = Teeth_right_1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5415F, -3.8579F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.4F, 0.1F, 4.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r8 = Teeth_right_1.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-3.4314F, 1.9841F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.4F, 0.1F, 4.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition Ears = Head.addOrReplaceChild("Ears", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -5.0F));

		PartDefinition left = Ears.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, -8.0F));

		PartDefinition cube_r9 = left.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-0.2F, -0.5F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -5.2F, 13.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition left2 = Ears.addOrReplaceChild("left2", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, -8.0F));

		PartDefinition cube_r10 = left2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 0).addBox(-0.2F, -0.3F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, -5.6F, 13.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition Torso = Body.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leg_r = Torso.addOrReplaceChild("leg_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition upperbone_r = leg_r.addOrReplaceChild("upperbone_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lowerbone_r = upperbone_r.addOrReplaceChild("lowerbone_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition foot_r = lowerbone_r.addOrReplaceChild("foot_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leg_l = Torso.addOrReplaceChild("leg_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition upperbone_l = leg_l.addOrReplaceChild("upperbone_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lowerbone_l = upperbone_l.addOrReplaceChild("lowerbone_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition foot_l = lowerbone_l.addOrReplaceChild("foot_l", CubeListBuilder.create().texOffs(-3, -2).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arm_l = Torso.addOrReplaceChild("arm_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition upperarm_l = arm_l.addOrReplaceChild("upperarm_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lowerarm_l = upperarm_l.addOrReplaceChild("lowerarm_l", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hook = lowerarm_l.addOrReplaceChild("hook", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arm_r = Torso.addOrReplaceChild("arm_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition upperarm_r = arm_r.addOrReplaceChild("upperarm_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lowerarm_r = upperarm_r.addOrReplaceChild("lowerarm_r", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition spik = lowerarm_r.addOrReplaceChild("spik", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}