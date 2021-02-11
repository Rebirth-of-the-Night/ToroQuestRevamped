package net.torocraft.toroquest.entities;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.entities.ai.EntityAIToroMate;

public class EntityToroCow extends EntityCow
{

	public static String NAME = "toro_cow";

	public EntityToroCow(World worldIn)
	{
		super(worldIn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if ( !(otherAnimal instanceof EntityToro) )
        {
            return false;
        }
        else
        {
            return this.isInLove() && otherAnimal.isInLove();
        }
    }
	
	@Override
	public EntityCow createChild(EntityAgeable ageable)
    {
		if ( rand.nextInt(3) == 0)
		{
			EntityToro toro = new EntityToro(world);
			toro.setPosition(this.posX, this.posY, this.posZ);
			toro.setGrowingAge(-24000);
			world.spawnEntity(toro);
		}
        return new EntityCow(this.world);
    }
	
	@Override
	protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		this.tasks.addTask(1, new EntityAIAvoidEntity<EntityWolf>(this, EntityWolf.class, 16.0F, 0.5D, 0.5D));
    	this.tasks.addTask(2, new EntityAIToroMate(this, 1.0D, new EntityToro(world)));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.WHEAT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

	
	public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntityToroCow.class, NAME, entityId, ToroQuest.INSTANCE, 80, 3,
				true, 0x203090, 0xe09939);
	}

}
