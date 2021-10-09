package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.torocraft.toroquest.entities.EntityVillageLord;

public class EntityAILookQuestingPlayer extends EntityAIWatchClosest
{
    private final EntityVillageLord lord;

    public EntityAILookQuestingPlayer(EntityVillageLord villagerIn)
    {
        super(villagerIn, EntityPlayer.class, 8.0F);
        this.lord = villagerIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if ( this.lord.currentPlayer != null )
        {
            this.closestEntity = this.lord.currentPlayer;
            return true;
        }
        else
        {
            return false;
        }
    }
}