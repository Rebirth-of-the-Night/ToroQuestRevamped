package net.torocraft.toroquest.entities.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntityConstruct;

@SideOnly(Side.CLIENT)
public class ModelConstruct extends ModelBase
{
    /** The head model for the iron golem. */
    public ModelRenderer ironGolemHead;
    /** The body model for the iron golem. */
    public ModelRenderer ironGolemBody;
    /** The right arm model for the iron golem. */
    public ModelRenderer ironGolemRightArm;
    /** The left arm model for the iron golem. */
    public ModelRenderer ironGolemLeftArm;
    /** The left leg model for the Iron Golem. */
    public ModelRenderer ironGolemLeftLeg;
    /** The right leg model for the Iron Golem. */
    public ModelRenderer ironGolemRightLeg;
    
    public ModelRenderer ironGolemRightPlate;
    public ModelRenderer ironGolemLeftPlate;
    
    //public ModelRenderer ironGolemRightPlate2;
    //public ModelRenderer ironGolemLeftPlate2;
    
    public ModelRenderer ironGolemHeadwear;

    public ModelConstruct()
    {
        this(0.0F);
    }

    public ModelConstruct(float p_i1161_1_)
    {
        this(p_i1161_1_, -7.0F);
    }

    public ModelConstruct(float p_i46362_1_, float p_i46362_2_)
    {	
    	// ModelIronGolem
        this.ironGolemHead = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemHead.setRotationPoint(0.0F, 0.0F + p_i46362_2_, -2.0F);
        this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, p_i46362_1_);
        
        // mohawk
        this.ironGolemHead.setTextureOffset(0, 16).addBox(-1.0F, -14.0F, -6.5F, 2, 6, 10, p_i46362_1_);
        
        this.ironGolemHeadwear = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemHeadwear.setTextureOffset(82, 0).addBox(-4.0F, -9.0F, -5.5F, 8, 10, 8, p_i46362_1_ + 0.5F);
        this.ironGolemHeadwear.setRotationPoint(0.0F, 0.0F + p_i46362_2_, 0.0F);
        
        this.ironGolemBody = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemBody.setRotationPoint(0.0F, 0.0F + p_i46362_2_, 0.0F);
        this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, p_i46362_1_);
        this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, p_i46362_1_ + 0.5F);
        
        // pipe
        this.ironGolemBody.setTextureOffset(0, 27).addBox(-8.5F, -9.0F, 0.5F, 2, 9, 2, p_i46362_1_);

        this.ironGolemRightPlate = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemRightPlate.mirror = true;
        this.ironGolemRightPlate.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemRightPlate.setTextureOffset(-2, 74).addBox(-15.5F, -6.0F, -4.0F, 9, 3, 8, p_i46362_1_); // 60 21
        this.ironGolemRightPlate.rotateAngleZ = 270.0F;
        
        // right bar
        this.ironGolemRightPlate.setTextureOffset(0, 19).addBox(-14.5F, -3.0F, -1.0F, 2, 11, 2, p_i46362_1_);
        
        this.ironGolemLeftPlate = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemLeftPlate.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemLeftPlate.setTextureOffset(-2, 74).addBox(6.5F, -6.0F, -4.0F, 9, 3, 8, p_i46362_1_); // 60 58
        this.ironGolemLeftPlate.rotateAngleZ = -270.0F;
        
        // left bar
        this.ironGolemLeftPlate.setTextureOffset(0, 19).addBox(12.5F, -3.0F, -1.0F, 2, 11, 2, p_i46362_1_);

        this.ironGolemRightArm = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 26, 6, p_i46362_1_);

        this.ironGolemLeftArm = (new ModelRenderer(this)).setTextureSize(128, 128);
        this.ironGolemLeftArm.mirror = true;
        this.ironGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 26, 6, p_i46362_1_);
        
        this.ironGolemLeftLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
        this.ironGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + p_i46362_2_, 0.0F);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, p_i46362_1_);
        this.ironGolemRightLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + p_i46362_2_, 0.0F);
        this.ironGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, p_i46362_1_);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.ironGolemHead.render(scale);
        this.ironGolemBody.render(scale);
        this.ironGolemLeftLeg.render(scale);
        this.ironGolemRightLeg.render(scale);
        this.ironGolemRightArm.render(scale);
        this.ironGolemLeftArm.render(scale);
        this.ironGolemRightPlate.render(scale);
        this.ironGolemLeftPlate.render(scale);
        //this.ironGolemRightPlate2.render(scale);
        //this.ironGolemLeftPlate2.render(scale);
        this.ironGolemHeadwear.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.ironGolemHead.rotateAngleY = netHeadYaw * 0.017453292F;
        this.ironGolemHead.rotateAngleX = headPitch * 0.017453292F;
        this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.ironGolemRightLeg.rotateAngleX = 1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.ironGolemLeftLeg.rotateAngleY = 0.0F;
        this.ironGolemRightLeg.rotateAngleY = 0.0F;
        copyModelAngles(this.ironGolemHead, this.ironGolemHeadwear);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
    {
        EntityConstruct entityirongolem = (EntityConstruct)entitylivingbaseIn;
        int i = entityirongolem.getAttackTimer();


        if (i > 0)
        {
            float x = -2.0F + 1.5F * this.triangleWave((float)i - partialTickTime, 10.0F);
            this.ironGolemRightArm.rotateAngleX = x;
            this.ironGolemLeftArm.rotateAngleX = x;
            this.ironGolemRightPlate.rotateAngleX = x;
            this.ironGolemLeftPlate.rotateAngleX = x;
            //this.ironGolemRightPlate2.rotateAngleX = x;
            //this.ironGolemLeftPlate2.rotateAngleX = x;
        }
        else
        {
            float x = (-0.2F + 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            float xx = (-0.2F - 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.ironGolemRightArm.rotateAngleX = x;
            this.ironGolemLeftArm.rotateAngleX = xx;
            this.ironGolemRightPlate.rotateAngleX = x;
            this.ironGolemLeftPlate.rotateAngleX = xx;
            //this.ironGolemRightPlate2.rotateAngleX = x;
            //this.ironGolemLeftPlate2.rotateAngleX = xx;
        }
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}