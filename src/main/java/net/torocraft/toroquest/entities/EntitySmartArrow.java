package net.torocraft.toroquest.entities;


import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.ToroQuest;

public class EntitySmartArrow extends EntityTippedArrow
{
    // private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityTippedArrow.class, DataSerializers.VARINT);
    // private PotionType potion = PotionTypes.EMPTY;
    // private final Set<PotionEffect> customPotionEffects = Sets.<PotionEffect>newHashSet();
    // private boolean fixedColor;

	public static String NAME = "smart_arrow";

	
	// init
    public static void init(int entityId)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ToroQuest.MODID, NAME), EntitySmartArrow.class, NAME, entityId, ToroQuest.INSTANCE, 80, 2,
				true);
	}
    
    
    public EntitySmartArrow(World worldIn)
    {
        super(worldIn);
    }

    public EntitySmartArrow(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public EntitySmartArrow(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter);
    }

    private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntitySmartArrow.class, DataSerializers.VARINT);
    private PotionType potion = PotionTypes.EMPTY;
    private final Set<PotionEffect> customPotionEffects = Sets.<PotionEffect>newHashSet();
    private boolean fixedColor;
    
    @Override
    public void setPotionEffect(ItemStack stack)
    {
        this.potion = PotionTypes.EMPTY;
        this.customPotionEffects.clear();
        this.dataManager.set(COLOR, Integer.valueOf(-1));
    }

//    private void refreshColor()
//    {
//        //this.fixedColor = false;
//        //this.dataManager.set(COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
//    }

    @Override
    public void addEffect(PotionEffect effect)
    {
        //this.customPotionEffects.add(effect);
        //this.getDataManager().set(COLOR, Integer.valueOf(PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects))));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        //this.dataManager.register(COLOR, Integer.valueOf(-1));
        this.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
        this.setIsCritical(true);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.timeInGround >= 200)
        {
            this.setDead();
        }
    }

//    private void setFixedColor(int p_191507_1_)
//    {
//        //this.fixedColor = true;
//        //this.dataManager.set(COLOR, Integer.valueOf(p_191507_1_));
//    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

//        if (this.potion != PotionTypes.EMPTY && this.potion != null)
//        {
//            compound.setString("Potion", ((ResourceLocation)PotionType.REGISTRY.getNameForObject(this.potion)).toString());
//        }
//
//        if (this.fixedColor)
//        {
//            compound.setInteger("Color", this.getColor());
//        }
//
//        if (!this.customPotionEffects.isEmpty())
//        {
//            NBTTagList nbttaglist = new NBTTagList();
//
//            for (PotionEffect potioneffect : this.customPotionEffects)
//            {
//                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
//            }
//
//            compound.setTag("CustomPotionEffects", nbttaglist);
//        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

//        if (compound.hasKey("Potion", 8))
//        {
//            this.potion = PotionUtils.getPotionTypeFromNBT(compound);
//        }

//        for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromTag(compound))
//        {
//            this.addEffect(potioneffect);
//        }
//
//        if (compound.hasKey("Color", 99))
//        {
//            this.setFixedColor(compound.getInteger("Color"));
//        }
//        else
//        {
//            this.refreshColor();
//        }
    }
    
//    @Override
//    public int getColor()
//    {
//        return ((Integer)this.dataManager.get(COLOR)).intValue();
//    }

    @Override
    protected void arrowHit(EntityLivingBase living)
    {
        super.arrowHit(living);
    }

    @Override
    protected ItemStack getArrowStack()
    {
        return new ItemStack(Items.ARROW);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
    }
}