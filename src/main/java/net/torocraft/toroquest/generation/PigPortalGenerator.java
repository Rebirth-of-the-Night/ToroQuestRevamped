package net.torocraft.toroquest.generation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.torocraft.toroquest.ToroQuest;
import net.torocraft.toroquest.block.BlockToroSpawner;
import net.torocraft.toroquest.block.TileEntityToroSpawner;
import net.torocraft.toroquest.entities.EntityPigLord;

public class PigPortalGenerator extends WorldGenerator
{


	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		if ( pos == null )
		{
			return false;
		}
		
		pos = getSurface( world, pos );
		
		this.createPatches(world, rand, pos);
		this.generatePortal(world, pos);
		this.spawnPigLord(world, rand, pos);
		//this.addToroSpawner( world, pos, getDefaultEnemies() );
		this.addPigs( world, pos, getPigs() );
		return true;
	}
	
	private void spawnPigLord(World world, Random rand, BlockPos origin)
	{
		EntityPigLord e = new EntityPigLord(world);
		e.setPosition(origin.getX() + 0.5, origin.getY() + 1, origin.getZ() + 0.5);
		e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), (IEntityLivingData) null);
		e.setRaidLocation( origin.getX(), origin.getZ(), origin.getY() );
		world.spawnEntity(e);
	}
	
	protected void createPortalPatch( World world, BlockPos start, boolean netherrack, int r )
	{
		int radius = world.rand.nextInt(r)+r;
		int x = start.getX();
		int z = start.getZ();
		for ( int xx = -radius; xx < radius; xx++ )
		{
			for ( int zz = -radius; zz < radius; zz++ )
			{
				int distFromCenter = (int)(MathHelper.sqrt((xx*xx+zz*zz)));
				if ( radius >= distFromCenter )
				{
					BlockPos pos = getCorruptionSurface( world, x+xx, z+zz );
					if ( pos != null )
					{
						if ( netherrack )
						{
							if ( world.rand.nextInt(3) == 0 )
							{
								world.setBlockState(pos, Blocks.MAGMA.getDefaultState());
//								if ( world.rand.nextInt(9) == 0 )
//								{
//									world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
//								}
							}
							else
							{
								world.setBlockState( pos, Blocks.NETHERRACK.getDefaultState() );
							}
						}
						else
						{
							world.setBlockState( pos, Blocks.SOUL_SAND.getDefaultState() );
							if ( world.rand.nextInt(9) == 0 )
							{
								world.setBlockState(pos.up(), Blocks.NETHER_WART.getDefaultState());
							}
						}
					}
				}
			}
		}
	}
	
	protected void createPatches(World world, Random rand, BlockPos origin)
	{
		double x = origin.getX();
    	double z = origin.getZ();    	
    	for ( int patch = 0; patch < 8; patch++ )
		{
			int xx = (rand.nextInt(32))*(rand.nextInt(2)*2-1);
			int zz = (rand.nextInt(32))*(rand.nextInt(2)*2-1);
			BlockPos pos = new BlockPos(new BlockPos(x+xx,0,z+zz));
			this.createPortalPatch( world, pos, false, 6 );
			xx = (rand.nextInt(28))*(rand.nextInt(2)*2-1);
			zz = (rand.nextInt(28))*(rand.nextInt(2)*2-1);
			pos = new BlockPos(new BlockPos(x+xx,0,z+zz));
			this.createPortalPatch(world, pos, true, 4 );
		}
		this.createPortalPatch(world, origin, true, 8 );
	}
	
	
	
	private BlockPos getCorruptionSurface( World world, int x, int z )
	{
		Block block;
		BlockPos search = new BlockPos(x, world.getActualHeight()/2, z);
		while ( search.getY() > 4 )
		{
			search = search.down();
			block = world.getBlockState(search).getBlock();
			if ( block instanceof BlockLiquid )
			{
				world.setBlockState( search, Blocks.LAVA.getDefaultState(), 0 );
			}
			else if ( block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockStone || block instanceof BlockSand )
			{
				break;
			}
		}
		return search;
	}
	
	private BlockPos getSurface(World world, BlockPos start)
	{
		IBlockState blockState;
		BlockPos search = new BlockPos(start.getX(), world.getActualHeight(), start.getZ());
		while ( search.getY() > 40 )
		{
			search = search.down();
			blockState = world.getBlockState(search);
			if ( isLiquid(blockState) )
			{
				break;
			}
			if ( (blockState).isOpaqueCube() && !( blockState.getBlock() instanceof BlockLog ) )
			{
				break;
			}
		}
		return search;
	}
	
	private boolean isLiquid( IBlockState blockState )
	{
		return blockState.getBlock() instanceof BlockLiquid;
	}
	
	private void addToroSpawner( World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(16);
			// spawner.addEntityTag(data.getQuestId().toString());
			// spawner.addEntityTag("capture_fugitives");
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}
	
	private void addPigs( World world, BlockPos blockpos, List<String> entities)
	{
		world.setBlockState(blockpos, BlockToroSpawner.INSTANCE.getDefaultState());
		TileEntity tileentity = world.getTileEntity(blockpos);
		if (tileentity instanceof TileEntityToroSpawner)
		{
			TileEntityToroSpawner spawner = (TileEntityToroSpawner) tileentity;
			spawner.setTriggerDistance(80);
			spawner.setEntityIds(entities);
			spawner.setSpawnRadius(40);
			// spawner.addEntityTag(data.getQuestId().toString());
			// spawner.addEntityTag("capture_fugitives");
		}
		else
		{
			System.out.println("tile entity is missing");
		}
	}

	private List<String> getDefaultEnemies()
	{
		List<String> entity = new ArrayList<String>();
		entity.add("toroquest:toroquest_pig_lord");
		return entity;
	}
	
	private List<String> getPigs()
	{
		List<String> entity = new ArrayList<String>();
		for ( int i = 0; 64 > i; i++ )
		{
			entity.add("minecraft:pig");
		}
		return entity;
	}
	
	public void generatePortal( World world, BlockPos origin )
	{
		BufferedReader reader;
		int x = origin.getX()-16;
		int y = origin.getY()-12;
		int z = origin.getZ()+8;
		try
		{
			reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/" + ToroQuest.MODID + "/structures/portal.txt"), "UTF-8"));
			String line = null;
			int yy = 0;
			int zz = 0;
			
			while ( (line = reader.readLine()) != null )
			{
				zz++;
				String[] s = line.split("");
				int sLength = s.length;
				for ( int xx = 0; sLength > xx; xx++  )
				{
					if ( xx == 0 && s[0].equals("#") )
					{
						yy++;
						zz = 0;
						break;
					}
					
					IBlockState block = getBlock( s[xx] );
					if ( block != null )
					{
						world.setBlockState( new BlockPos(xx+x,yy+y,zz+z), block, 0 );
					}
				}
			}
		}
		catch ( Exception e )
		{
			System.out.println( "ERROR:" + e );
		}
	}
	
	private IBlockState getBlock( String block )
	{
		switch ( block )
		{
			case "O":
			{
				return Blocks.OBSIDIAN.getDefaultState();
			}
			case "X":
			{
				return Blocks.PORTAL.getDefaultState().withProperty(BlockPortal.AXIS, EnumFacing.Axis.X);
			}
		}
		return null;
		// return Blocks.AIR.getDefaultState();
	}
}