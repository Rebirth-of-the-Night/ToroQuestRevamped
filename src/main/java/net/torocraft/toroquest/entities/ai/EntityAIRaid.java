package net.torocraft.toroquest.entities.ai;

import java.util.Random;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIRaid extends EntityAIBase
{
	public final EntityCreature entity;
	private final int minDistanceFromCenter;
	private final int moveDistance;
	private final double movementSpeed;
	
	private Integer centerX;
	private Integer centerZ;

//	private double movePosX;
//	private double movePosY;
//	private double movePosZ;
	
	private Random rand = new Random();

	public EntityAIRaid(EntityCreature entity, double speedIn, int md, int mdfc )
	{
		this.entity = entity;
		this.movementSpeed = speedIn;
		this.minDistanceFromCenter = mdfc;
		this.moveDistance = md;
		this.setMutexBits(1);
	}

	public void setCenter(Integer centerX, Integer centerZ)
	{
		this.centerX = centerX;
		this.centerZ = centerZ;
	}
	
	private boolean move(World world, BlockPos start) // call mult times
	{
		double x = this.centerX - start.getX();
		double z = this.centerZ - start.getZ();
		
		double xz = Math.abs(x) + Math.abs(z);
		
		if ( xz < this.minDistanceFromCenter )
		{
			return false;
		}
		
		x = x/xz * this.moveDistance + start.getX() + (this.rand.nextInt(5)-2);
		z = z/xz * this.moveDistance + start.getZ() + (this.rand.nextInt(5)-2);
				
		BlockPos moveTo = findValidSurface(world, new BlockPos(x, start.getY(), z), 8);
		
		if ( moveTo != null )
		{
			if ( this.entity.getNavigator().tryMoveToXYZ(moveTo.getX(), moveTo.getY(), moveTo.getZ(), this.movementSpeed) )
			{
				return true;
			}
		}
		
		if ( this.rand.nextBoolean() )
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 20, 8, new Vec3d(x,this.entity.posY,z));
			
			if ( vec3d == null || !this.entity.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.movementSpeed) )
	        {
				vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 8, new Vec3d(x,this.entity.posY,z));
				
				if ( vec3d == null || !this.entity.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.movementSpeed ) )
				{
					vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 8, 8, new Vec3d(x,this.entity.posY,z));
					
					if ( vec3d == null || !this.entity.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.movementSpeed ) )
					{
						return false;
					}
				}
	        }
			return true;
		}
		return false;
	}
	
	public static BlockPos findValidSurface( World world, BlockPos startPos, int yOffset )
	{
		IBlockState blockState;
		
		// =-=-=-=-=-= SEARCH UP =-=-=-=-=-=
		BlockPos pos = startPos.down();
		boolean airspace = false;
		boolean floor =  false;
		int y = 0;
		
		while ( yOffset > y )
		{
			blockState = world.getBlockState(pos);
			if ( blockState.getBlock() instanceof BlockLiquid && blockState.getBlock().getDefaultState() != Blocks.WATER )
			{
				return null;
			}
			
			if ( !blockState.getBlock().getDefaultState().isFullCube() )
			{
				if ( floor )
				{
					if ( airspace )
					{
						return pos.down();
					}
					else
					{
						airspace = true;
					}
				}
			}
			else
			{
				floor = true;
				airspace = false;
			}
			pos = pos.up();
			y++;
		}
		// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
		
		// =-=-=-=-= SEARCH DOWN =-=-=-=-=
		pos = startPos.up();
		airspace = false;
		floor = false;
		y = 0;
		
		while ( yOffset > y )
		{
			blockState = world.getBlockState(pos);
			if ( blockState.getBlock() instanceof BlockLiquid && blockState.getBlock().getDefaultState() != Blocks.WATER )
			{
				return null;
			}
			
			if ( !blockState.getBlock().getDefaultState().isFullCube() )
			{
				if ( airspace )
				{
					floor = true;
				}
				else
				{
					airspace = true;
				}
			}
			else if ( airspace && floor )
			{
				return pos;
			}
			else
			{
				airspace = false;
				floor = false;
			}
			
			pos = pos.down();
			y++;
		}
		return null;
	}

	public boolean shouldExecute()
	{
		if ( this.centerX == null || this.centerZ == null )
		{
			return false;
		}
		
		if ( this.inCorrectPosition() )
		{
			return false;
		}
		
		return true;
	}
	
	
	public void updateTask()
    {
		if ( ( this.entity.getNavigator().noPath() && this.rand.nextInt(8) == 0 ) || this.rand.nextInt(32) == 0 )
		{
			this.move(this.entity.world, this.entity.getPosition());
		}
    }
	
	public boolean inCorrectPosition()
	{
		return this.minDistanceFromCenter > this.entity.getDistance(this.entity.posX - centerX, this.entity.posY, this.entity.posX - centerX);
	}
	
	public void startExecuting()
	{
		this.move(this.entity.world, this.entity.getPosition());
	}
}