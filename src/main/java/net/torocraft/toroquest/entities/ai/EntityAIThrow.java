package net.torocraft.toroquest.entities.ai;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class EntityAIThrow extends EntityAIAttackMelee
{
	protected double strength = 1.0;
	protected boolean isPassive = false;
	protected int widthRange = 6;
	protected int attackCooldown = 20;
	
    public EntityAIThrow(EntityCreature creature, double speedIn, boolean useLongMemory)
    {
		super(creature, speedIn, useLongMemory);
	}
    
    public EntityAIThrow(EntityCreature creature, double speedIn, boolean useLongMemory, double strength)
    {
		super(creature, speedIn, useLongMemory);
		this.strength = strength;
	}
    
    public EntityAIThrow(EntityCreature creature, double speedIn, boolean useLongMemory, boolean passive)
    {
		super(creature, speedIn, useLongMemory);
		this.isPassive = passive;
	}
    
    public EntityAIThrow(EntityCreature creature, double speedIn, boolean useLongMemory, double strength, int widthRange, int attackCooldown)
    {
		super(creature, speedIn, useLongMemory);
		this.strength = strength;
		this.widthRange = widthRange;
		this.attackCooldown = 40;
	}

    @Override
	protected void checkAndPerformAttack(EntityLivingBase e, double range)
    {
    	if ( this.isPassive ) return;
        double dist = this.getAttackReachSqr(e) + this.widthRange;

        if (range <= dist && this.attackTick <= 0)
        {
            this.attackTick = this.attackCooldown;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.swingArm(EnumHand.OFF_HAND);
            this.attacker.attackEntityAsMob(e);
			if ( !this.attacker.world.isRemote )
			{
                Vec3d veloctiyVector = new Vec3d(e.posX - this.attacker.posX, 0, e.posZ - this.attacker.posZ);
                e.addVelocity( (veloctiyVector.x) * strength,(0.2F + this.attacker.world.rand.nextFloat()) * strength,(veloctiyVector.z) * strength );
                e.velocityChanged = true;
			}
        }
    }
}