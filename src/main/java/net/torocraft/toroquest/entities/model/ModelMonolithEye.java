package net.torocraft.toroquest.entities.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntityMonolithEye;

@SideOnly(Side.CLIENT)
public class ModelMonolithEye extends ModelBase
{
	private ModelRenderer guardianBody;
	// private ModelRenderer guardianEye;
	private EntityLivingBase attackTarget;
	
	private ModelRenderer frame0;
	private ModelRenderer frame1;
	private ModelRenderer frame2;
	private ModelRenderer frame3;
	private ModelRenderer frame4;
	private ModelRenderer frame5;
	private ModelRenderer frame6;
	private ModelRenderer frame7;
	private ModelRenderer frame8;

	public ModelMonolithEye()
	{
		this.textureWidth = 264;
		this.textureHeight = 264;
		
//		this.textureWidth = 64;
//		this.textureHeight = 64;
		this.guardianBody = new ModelRenderer(this);
//		this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
//		this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
//		this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
//		this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
//		this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);
//
//		//(float offX, float offY, float offZ, int width, int height, int depth)
//		this.guardianEye.addBox(-3.5F, 11.5F, 0.0F, 9, 9, 1);
		
		this.frame0 = new ModelRenderer(this, 0, 0);
		this.frame1 = new ModelRenderer(this, 0, 0);
		this.frame2 = new ModelRenderer(this, 0, 0);
		this.frame3 = new ModelRenderer(this, 0, 0);
		this.frame4 = new ModelRenderer(this, 0, 0);
		this.frame5 = new ModelRenderer(this, 0, 0);
		this.frame6 = new ModelRenderer(this, 0, 0);
		this.frame7 = new ModelRenderer(this, 0, 0);
		this.frame8 = new ModelRenderer(this, 0, 0);
		
		this.guardianBody.addChild(this.frame0);
		this.guardianBody.addChild(this.frame1);
		this.guardianBody.addChild(this.frame2);
		this.guardianBody.addChild(this.frame3);
		this.guardianBody.addChild(this.frame4);
		this.guardianBody.addChild(this.frame5);
		this.guardianBody.addChild(this.frame6);
		this.guardianBody.addChild(this.frame7);
		this.guardianBody.addChild(this.frame8);
		
		this.frame0.setTextureOffset(0, 0).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame1.setTextureOffset(88, 0).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame2.setTextureOffset(176, 0).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame3.setTextureOffset(0, 88).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame4.setTextureOffset(88, 88).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame5.setTextureOffset(176, 88).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame6.setTextureOffset(0, 176).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame7.setTextureOffset(88, 176).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);
		this.frame8.setTextureOffset(176, 176).addBox(-52.0F, -58.0F, -52.0F, 88, 88, 1, true);

		
		
	}

	public EntityLivingBase getAttackTarget()
    {
        return this.attackTarget;
    }
	public void setAttackTarget(EntityLivingBase entity)
    {
        this.attackTarget = entity;
    }
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.setRotationAngles(p_78088_2_, limbSwing, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		this.guardianBody.render(scale);
	}
	
	protected int attackTimer = 0;

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		EntityMonolithEye entityMonolithEye = (EntityMonolithEye) entityIn;
		this.guardianBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.guardianBody.rotateAngleY = netHeadYaw * 0.017453292F;
		this.guardianBody.rotateAngleX = headPitch * 0.017453292F - 0.5F;

		// this.guardianEye.rotationPointZ = -8.25F;
		
		
// this.guardianEye.rotationPointZ = 0.0F;



//Entity entity = // Minecraft.getMinecraft().getRenderViewEntity();

//		if (entityMonolithEye.hasTargetedEntity()) {
//			entity = entityMonolithEye.getTargetedEntity();
//		}
		
		
		

//										EntityLivingBase entity = entityMonolithEye.getAttackTarget();
//										
//										if (entity != null)
//										{
//											Vec3d vec3d = entity.getPositionEyes(0.0F);
//											Vec3d vec3d1 = entityIn.getPositionEyes(0.0F);
//											double d0 = vec3d.y - vec3d1.y;
//								
//											if (d0 > 0.0D)
//											{
//												this.guardianEye.rotationPointY = 0.0F;
//											}
//											else
//											{
//												this.guardianEye.rotationPointY = 1.0F;
//											}
//								
//											Vec3d vec3d2 = entityIn.getLook(0.0F);
//											vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
//											Vec3d vec3d3 = (new Vec3d(vec3d1.x - vec3d.x, 0.0D, vec3d1.z - vec3d.z)).normalize().rotateYaw(((float) Math.PI / 2F));
//											double d1 = vec3d2.dotProduct(vec3d3);
//											this.guardianEye.rotationPointX = MathHelper.sqrt((float) Math.abs(d1)) * 2.0F * (float) Math.signum(d1);
//										}

		attackTimer = entityMonolithEye.ticksExisted % 68;
		
		if ( attackTimer < 8 )
		{
			this.frame0.showModel = true;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 12 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = true;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 16 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = true;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 20 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = true;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 24 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = true;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 28 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = true;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 32 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = true;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 36 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = true;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 40 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = true;
		}
		else if ( attackTimer < 44 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = true;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 48 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = true;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 52 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = true;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 56 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = true;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 60 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = false;
			this.frame3.showModel = true;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else if ( attackTimer < 64 )
		{
			this.frame0.showModel = false;
			this.frame1.showModel = false;
			this.frame2.showModel = true;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
		else
		{
			this.frame0.showModel = false;
			this.frame1.showModel = true;
			this.frame2.showModel = false;
			this.frame3.showModel = false;
			this.frame4.showModel = false;
			this.frame5.showModel = false;
			this.frame6.showModel = false;
			this.frame7.showModel = false;
			this.frame8.showModel = false;
		}
	}
}