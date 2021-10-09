package net.torocraft.toroquest.entities.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.entities.EntityToroNpc;

@SideOnly(Side.CLIENT)
public class ModelGuard extends ModelBiped
{
	private final float modelSize;

	private CivilizationType civ;

	private static final float DEFAULT_CAPE_ANGLE = 0.08f;

	public ModelGuard()
	{
		this(0.0F);
	}

	public ModelGuard(float modelSize)
	{
		super(modelSize, 0, 64, 64);
		this.modelSize = modelSize;
		//for (CivilizationType civ : CivilizationType.values())
		//{
		// if ( ToroQuestConfiguration.renderGuardCape )
		this.buildCape(civ);
		//}
	}

//	public void setCivilization(CivilizationType civ)
//	{
//		this.civ = civ;
//	}

//	protected CivilizationType randomCivilizationType()
//	{
//		Random rand = new Random();
//
//		return CivilizationType.values()[rand.nextInt(CivilizationType.values().length)];
//	}

	protected void buildCape(CivilizationType civ)
	{
		ModelRenderer cape = new ModelRenderer(this, 0, 32);
		cape.setTextureSize(64, 64);
		cape.setTextureOffset(0, 32);
//		switch (civ)
//		{
//		case SUN:
//			cape.setTextureOffset(0, 32);
//			break;
//		case EARTH:
//			cape.setTextureOffset(20, 32);
//			break;
//		case WATER:
//			cape.setTextureOffset(40, 32);
//			break;
//		case WIND:
//			cape.setTextureOffset(0, 47);
//			break;
//		case MOON:
//			cape.setTextureOffset(20, 47);
//			break;
//		case FIRE:
//			cape.setTextureOffset(40, 47);
//			break;
//		default:
//			cape.setTextureOffset(0, 32);
//			break;
//		}
		cape.addBox(-4.5F, 0.0F, 0F, 9, 14, 1, modelSize);
		cape.setRotationPoint(0, 0, 0);
		cape.offsetZ = 0.17f;
		cape.offsetY = +0.05f;
		cape.rotateAngleX = DEFAULT_CAPE_ANGLE;
		bipedBody.addChild(cape);
//		cape.isHidden = true;
//		capes[civ.ordinal()] = cape;
	}


	private static final float MAX_CAPE_ANI = 0.07f;


	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, EntityToroNpc entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		//if ( entityIn.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.TORCH) )
//		{
//            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 15.0F;
//            this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
//		}
		this.capeAnimation(entityIn);
	}

	protected void capeAnimation(EntityToroNpc entityIn)
	{
		if (civ == null)
		{
			return;
		}

		//showCurrentCape();

		float speed = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionZ * entityIn.motionZ);

		float rot = speed * 40;

		if (rot > 0.6f) {
			rot = 0.6f;
		}

		if (entityIn.capeAniUp && entityIn.capeAni > MAX_CAPE_ANI)
		{
			entityIn.capeAniUp = false;

		}
		else if (!entityIn.capeAniUp && entityIn.capeAni < 0)
		{
			entityIn.capeAniUp = true;
		}

		if (entityIn.capeAniUp)
		{
			entityIn.capeAni += 0.00025;
		}
		else
		{
			entityIn.capeAni -= 0.00025;
		}

		rot += entityIn.capeAni;

		//capes[civ.ordinal()].rotateAngleX = rot;
	}

//	private void showCurrentCape()
//	{
//		for (ModelRenderer cape : capes)
//		{
//			cape.isHidden = true;
//		}
//		capes[civ.ordinal()].isHidden = false;
//	}
}