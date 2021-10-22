package net.torocraft.toroquest.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.generation.village.util.VillagePieceBlockMap;

public class TileEntityBannerSpawner extends TileEntity implements ITickable
{

	protected int triggerDistance = 60;
	protected int spawnRadius = 4;

	public TileEntityBannerSpawner()
	{

	}

	public int getSpawnRadius()
	{
		return spawnRadius;
	}

	public void setSpawnRadius(int spawnRadius)
	{
		this.spawnRadius = spawnRadius;
	}

	public void setTriggerDistance(int triggerDistance)
	{
		this.triggerDistance = triggerDistance;
	}

	public void update()
	{
		if (!world.isRemote && isRunTick() && withinRange())
		{
			triggerSpawner();
		}
	}

	protected void triggerSpawner()
	{
		try
		{
			Block banner = this.world.getBlockState(pos).getBlock();
			if ( banner instanceof BlockSmartBanner )
			{
				VillagePieceBlockMap.setBannerRotation(this.getWorld(), pos, ((BlockSmartBanner)banner).getFacing() );
				this.markDirty();
			}
		}
		catch (Exception e)
		{
			
		}
//		else
//		{
//			this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
//		}
	}
	
	protected boolean isGroundBlock(IBlockState blockState)
	{
		return blockState.isOpaqueCube();
	}

	protected boolean withinRange()
	{
		return this.isAnyPlayerWithinRangeAt( world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, (double) this.triggerDistance);
	}

	public boolean isAnyPlayerWithinRangeAt( World world, double x, double y, double z, double range)
    {
        for (int j2 = 0; j2 < world.playerEntities.size(); ++j2)
        {
            EntityPlayer entityplayer = world.playerEntities.get(j2);
            
            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer))
            {
                double d0 = entityplayer.getDistanceSq(x, y, z);

                if (range < 0.0D || d0 < range * range)
                {
                    return true;
                }
            }
        }

        return false;
    }
	protected boolean isRunTick()
	{
		return world.getWorldTime() % 50 == 0;
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	public int getTriggerDistance()
	{
		return triggerDistance;
	}
		
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 0.0D;
    }
	
	@Override
	public boolean hasFastRenderer()
    {
        return true;
    }
	
	@Override
	public Block getBlockType()
    {
		return Blocks.AIR.getDefaultState().getBlock();
    }

}