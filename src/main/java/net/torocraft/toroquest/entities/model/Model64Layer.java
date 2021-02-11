package net.torocraft.toroquest.entities.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Model64Layer extends ModelBiped
{
		public Model64Layer()
		{
			this(0.0F, false);
		}
		
	    public ModelRenderer bipedLeftArmwear;
	    public ModelRenderer bipedRightArmwear;
	    public ModelRenderer bipedLeftLegwear;
	    public ModelRenderer bipedRightLegwear;
	    public ModelRenderer bipedBodyWear;
	    // private final ModelRenderer bipedCape;
	    // private boolean smallArms = false;
	    
	    public Model64Layer(float modelSize)
	    {
	        super(modelSize, 0.0F, 64, 64);
	        //this.smallArms = smallArmsIn;
	        
	        //this.bipedCape = new ModelRenderer(this, 0, 0);
	        //this.bipedCape.setTextureSize(64, 32);
	        //this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, modelSize);
	        
//	        if (smallArms)
//	        {
//		        this.bipedLeftArm = new ModelRenderer(this, 32, 48);
//			    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
//			    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
//			    this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
//			    this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.3F);
//			    this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
//			    
//			    this.bipedRightArm = new ModelRenderer(this, 40, 16);
//			    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
//			    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
//			    this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
//			    this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.3F);
//			    this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
//	        }
//	        else
	        {
	        	this.bipedLeftArm = new ModelRenderer(this, 32, 48);
	            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
	            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
	            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
	            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
	            
	            this.bipedRightArm = new ModelRenderer(this, 40, 16);
	            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
	            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
	            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
	            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
	        }

	        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
	        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
	        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
	        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
	        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
	        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
	        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
	        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
	        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.3F);
	        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
	    }
	    
	    public Model64Layer(float modelSize, boolean p_i1168_2_)
	    {
			super(modelSize, 0.0F, 64, p_i1168_2_ ? 32 : 64);
			
	        //this.bipedCape = new ModelRenderer(this, 0, 0);
	        //this.bipedCape.setTextureSize(64, 32);
	        //this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, modelSize);
	        
//	        if (smallArms)
//	        {
//		        this.bipedLeftArm = new ModelRenderer(this, 32, 48);
//			    this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
//			    this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
//			    this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
//			    this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.3F);
//			    this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
//			    
//			    this.bipedRightArm = new ModelRenderer(this, 40, 16);
//			    this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
//			    this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
//			    this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
//			    this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.3F);
//			    this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
//	        }
//	        else
	        {
	        	this.bipedLeftArm = new ModelRenderer(this, 32, 48);
	            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
	            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
	            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
	            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	            this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
	            
	            this.bipedRightArm = new ModelRenderer(this, 40, 16);
	            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
	            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
	            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
	            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	            this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
	        }

	        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
	        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
	        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
	        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
	        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	        this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
	        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
	        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.3F);
	        this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
	        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
	        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.3F);
	        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
	    }

	    /**
	     * Sets the models various rotation angles then renders the model.
	     */
	    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	    {
	        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	        GlStateManager.pushMatrix();
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.bipedLeftLegwear.render(scale);
            this.bipedRightLegwear.render(scale);
            this.bipedLeftArmwear.render(scale);
            this.bipedRightArmwear.render(scale);
            this.bipedBodyWear.render(scale);
	        GlStateManager.popMatrix();
	    }

//	    public void renderCape(float scale)
//	    {
//	        this.bipedCape.render(scale);
//	    }

	    /**
	     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	     * "far" arms and legs can swing at most.
	     */
	    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	    {
	        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
	        copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
	        copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
	        copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
	        copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
	        copyModelAngles(this.bipedBody, this.bipedBodyWear);

//	        if (entityIn.isSneaking())
//	        {
//	            this.bipedCape.rotationPointY = 2.0F;
//	        }
//	        else
//	        {
//	            this.bipedCape.rotationPointY = 0.0F;
//	        }
	    }
}