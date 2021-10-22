package net.torocraft.toroquest.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.entities.EntitySentry;

public class TileEntityToroSpawner extends TileEntity implements ITickable
{

	protected int triggerDistance = 60;
	protected List<String> entityIds = new ArrayList<String>();
	protected int spawnRadius = 0;
	protected Integer color = 0;
	protected List<String> entityTags = new ArrayList<String>();

	public TileEntityToroSpawner()
	{

	}

	public int getSpawnRadius()
	{
		return spawnRadius;
	}

	public void setSpawnRadius(int spawnRadius) {
		this.spawnRadius = spawnRadius;
	}

	public void setTriggerDistance(int triggerDistance) {
		this.triggerDistance = triggerDistance;
	}

	public void setEntityIds(List<String> entityIds)
	{
		this.entityIds = entityIds;
	}
	
	public void setColor(Integer c)
	{
		color = c;
		writeToNBT(new NBTTagCompound());
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		triggerDistance = compound.getInteger("trigger_distance");
		spawnRadius = compound.getInteger("spawn_radius");
		
		// extra //
		color = compound.getInteger("color");

		entityIds = new ArrayList<String>();
		entityTags = new ArrayList<String>();

		NBTTagList list;
		
		try
		{
			list = (NBTTagList) compound.getTag("entity_ids");
		}
		catch (Exception e)
		{
			list = new NBTTagList();
		}
		for (int i = 0; i < list.tagCount(); i++)
		{
			entityIds.add(list.getStringTagAt(i));
		}

		try
		{
			list = (NBTTagList) compound.getTag("entity_tags");
		}
		catch (Exception e)
		{
			list = new NBTTagList();
		}
		for (int i = 0; i < list.tagCount(); i++)
		{
			entityTags.add(list.getStringTagAt(i));
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		compound.setInteger("trigger_distance", triggerDistance);
		compound.setInteger("spawn_radius", spawnRadius);

		compound.setInteger("color", color);

		NBTTagList list = new NBTTagList();
		for (String id : entityIds) {
			list.appendTag(new NBTTagString(id));
		}
		compound.setTag("entity_ids", list);

		list = new NBTTagList();
		if (entityTags != null) {
			for (String id : entityTags) {
				list.appendTag(new NBTTagString(id));
			}
		}
		compound.setTag("entity_tags", list);

		return compound;
	}

	protected void storeItemStack(String key, ItemStack stack, NBTTagCompound compound) {
		if (stack == null) {
			return;
		}
		NBTTagCompound c = new NBTTagCompound();
		stack.writeToNBT(c);
		compound.setTag(key, c);
	}

	protected ItemStack readItemStack(String key, NBTTagCompound compound) {
		if (!compound.hasKey(key)) {
			return null;
		}
		return new ItemStack(compound.getCompoundTag(key));
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
		for (String entityId : entityIds)
		{
			spawnCreature(entityId);
		}
		world.setBlockToAir(pos);
		this.markDirty();
	}

	public void spawnCreature(String entityID)
	{
		Entity entity = getEntityForId(getWorld(), entityID);

		if ( !(entity instanceof EntityLivingBase) )
		{
			System.out.println("entity not EntityLivingBase: " + entityID);
			return;
		}
		else if ( entity instanceof EntitySentry )
		{
			((EntitySentry)(entity)).setRaidLocation((int)entity.posX, (int)entity.posZ);
		}
		
		spawnEntityLiving((EntityLiving) entity, findSuitableSpawnLocation());
	}

	public BlockPos findSuitableSpawnLocation()
	{
		Random rand = world.rand;

		if (spawnRadius < 1)
		{
			return getPos();
		}

		int degrees, distance, x, z;

		BlockPos pos;

		for (int i = 0; i < 16; i++)
		{
			distance = rand.nextInt(spawnRadius);
			degrees = rand.nextInt(360);
			x = distance * (int) Math.round(Math.cos(Math.toRadians(degrees)));
			z = distance * (int) Math.round(Math.sin(Math.toRadians(degrees)));
			pos = findSurface(x, z);
			if (pos != null)
			{
				return pos;
			}
		}
		return getPos();
	}

	public BlockPos findSurface(int x, int z)
	{
		BlockPos pos = getPos().add(x, -3, z);
		IBlockState blockState;
		int yOffset = 0;
		boolean groundFound = false;
		boolean[] airSpace = { false, false };

		while (yOffset <= 16)
		{
			blockState = world.getBlockState(pos);
			if (isGroundBlock(blockState))
			{
				groundFound = true;
				airSpace[0] = false;
				airSpace[1] = false;

			}
			else if (airSpace[0] && airSpace[1] && groundFound)
			{
				return pos.down();

			}
			else if (Blocks.AIR.equals(blockState.getBlock()))
			{
				if (airSpace[0])
				{
					airSpace[1] = true;
				}
				else
				{
					airSpace[0] = true;
				}
			}
			pos = pos.up();
			yOffset++;
		}
		return null;
	}

	protected boolean isGroundBlock(IBlockState blockState)
	{
		if (blockState.getBlock() == Blocks.LEAVES || blockState.getBlock() == Blocks.LEAVES2 || blockState.getBlock() == Blocks.LOG || blockState.getBlock() instanceof BlockBush || blockState.getBlock() == Blocks.WOOL )
		{
			return false;
		}
		return blockState.isOpaqueCube();
	}

	public static Entity getEntityForId(World world, String entityID)
	{
		String[] parts = entityID.split(":");

		String domain, entityName;

		if (parts.length == 2) {
			domain = parts[0];
			entityName = parts[1];
		} else {
			domain = "minecraft";
			entityName = entityID;
		}

		return EntityList.createEntityByIDFromName(new ResourceLocation(domain, entityName), world);
	}

	protected boolean spawnEntityLiving(EntityLiving entity, BlockPos pos) {

		double x = pos.getX() + 0.5D;
		double y = pos.getY();
		double z = pos.getZ() + 0.5D;

		entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData) null);

		entity.enablePersistence();

		if (entityTags != null) {
			for (String tag : entityTags) {
				entity.addTag(tag);
			}
		}
		
		if ( color != null && color != 0 && entity instanceof EntitySheep )
		{
			EntitySheep sheep = (EntitySheep)entity;
			
			switch ( color )
			{
				case 0:
				{
					sheep.setFleeceColor(EnumDyeColor.WHITE);
					break;
				}
				case 1:
				{
					sheep.setFleeceColor(EnumDyeColor.RED);
					break;
				}
				case 2:
				{
					sheep.setFleeceColor(EnumDyeColor.GREEN);
					break;
				}
				case 3:
				{
					sheep.setFleeceColor(EnumDyeColor.BLUE);
					break;
				}
				case 4:
				{
					sheep.setFleeceColor(EnumDyeColor.BLACK);
					break;
				}
				case 5:
				{
					sheep.setFleeceColor(EnumDyeColor.YELLOW);
					break;
				}
				case 6:
				{
					sheep.setFleeceColor(EnumDyeColor.BROWN);
					break;
				}
				default:
				{
					sheep.setFleeceColor(EnumDyeColor.WHITE);
					break;
				}
			}
			sheep.setCustomNameTag("§e§l!");
			sheep.setAlwaysRenderNameTag(true);
			sheep.setGlowing(true);
			world.spawnEntity(sheep);
			entity.playLivingSound();
		}
		else
		{
			world.spawnEntity(entity);
			entity.playLivingSound();
		}
		return true;
	}

	protected boolean withinRange()
	{
		return world.isAnyPlayerWithinRangeAt((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, (double) this.triggerDistance);
	}

	protected boolean isRunTick()
	{
		return world.getWorldTime() % 75 == 0;
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound nbttagcompound = this.writeToNBT(new NBTTagCompound());
		nbttagcompound.removeTag("SpawnPotentials");
		return nbttagcompound;
	}

	public boolean onlyOpsCanSetNbt()
	{
		return true;
	}

	public int getTriggerDistance()
	{
		return triggerDistance;
	}

	public List<String> getEntityTags()
	{
		return entityTags;
	}

	public void setEntityTags(List<String> entityTags)
	{
		this.entityTags = entityTags;
	}

	public void addEntityTag(String tag)
	{
		if (entityTags == null) 
		{
			entityTags = new ArrayList<String>();
		}
		entityTags.add(tag);
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