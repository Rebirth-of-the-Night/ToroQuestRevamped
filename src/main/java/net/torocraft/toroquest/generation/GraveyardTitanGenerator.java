package net.torocraft.toroquest.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.entities.EntityGraveTitan;

public class GraveyardTitanGenerator extends GraveyardGenerator
{
	@Override
	public boolean generate(World world, Random rand, BlockPos origin)
	{
		super.generate(world, rand, origin);
		this.addToroSpawner( world, origin, getDefaultEnemies() );
		//this.spawnGraveTitan( world, origin.up() );
		return true;
	}

	private void spawnGraveTitan(World world, BlockPos pos)
	{
		EntityGraveTitan e = new EntityGraveTitan(world);
		e.setPositionAndUpdate(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
		world.spawnEntity(e);
	}
	
	private void addToroSpawner( World world, BlockPos blockpos, List<String> entities)
	{
		blockpos = blockpos.up(2);
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(20);
		}
		else
		{
			System.out.println("tile entity is missing");
			this.spawnGraveTitan( world, blockpos.up() );
		}
	}

	private List<String> getDefaultEnemies()
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_grave_titan");
		return entity;
	}
	
}





