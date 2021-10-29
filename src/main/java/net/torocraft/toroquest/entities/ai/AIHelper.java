package net.torocraft.toroquest.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

public interface AIHelper
{
	
	
	
	public static void faceEntitySmart(EntityLivingBase in, EntityLivingBase p)
    {
    	try
    	{
	        double d0 = (p.getPositionVector().x - in.getPositionVector().x) * 2;
	        double d2 = (p.getPositionVector().z - in.getPositionVector().z) * 2;
	        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
	        in.rotationYaw = f;
	        in.prevRotationYaw = f;
    	}
    	catch ( Exception e ) {}
    }
	
    public static boolean faceMovingDirection(EntityLiving in)
    {
    	if ( !in.getNavigator().noPath() )
        {
	    	try
	    	{
		    	PathPoint p = in.getNavigator().getPath().getFinalPathPoint();
		    	PathPoint p2 = in.getNavigator().getPath().getPathPointFromIndex(in.getNavigator().getPath().getCurrentPathIndex());

		        double d0 = (p.x - in.posX) + (p2.x - in.posX);
		        double d2 = (p.z - in.posZ) + (p2.z - in.posZ);
		        //double d1 = p.y - in.posY;
		
		        //double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
		        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
		        //float f1 = (float)(-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
		        //in.rotationPitch = f1;
		        in.rotationYaw = f;
		        in.prevRotationYaw = f;

		        return true;
	    	}
	    	catch ( Exception e )
	    	{
	    		in.prevRotationYaw = in.rotationYaw;
	    	}
        }
    	else
    	{
    		in.prevRotationYaw = in.rotationYaw;
    	}
    	return false;
    }
    
    
    
//	public static void faceEntitySmart(EntityLivingBase in, EntityLivingBase p)
//    {
//    	try
//    	{
//	        double d0 = (p.getPositionVector().x - in.getPositionVector().x) * 2;
//	        double d2 = (p.getPositionVector().z - in.getPositionVector().z) * 2;
//	        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
//	        in.rotationYaw = f;
//	        in.prevRotationYaw = f;
//	    }
//    	catch ( Exception e ) {}
//    }
//	
//    public static boolean faceMovingDirection(EntityLiving in)
//    {
//    	if ( !in.getNavigator().noPath() )
//        {
//	    	try
//	    	{
//		    	PathPoint p = in.getNavigator().getPath().getFinalPathPoint();
//
//		        double d0 = (p.x - in.posX) * 2;
//		        double d2 = (p.z - in.posZ) * 2;
//		        //double d1 = p.y - in.posY;
//		
//		        //double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
//		        float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
//		        //float f1 = (float)(-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
//		        //in.rotationPitch = f1;
//		        in.rotationYaw = f;
//		        return true;
//	    	}
//	    	catch ( Exception e ) {}
//        }
//    	return false;
//    }
}
