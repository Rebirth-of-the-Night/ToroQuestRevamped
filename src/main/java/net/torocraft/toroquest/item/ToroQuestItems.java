package net.torocraft.toroquest.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.toroquest.block.BlockVillageSpawner;
import net.torocraft.toroquest.block.TileEntityVillageSpawner;
import net.torocraft.toroquest.item.armor.ItemBanditArmor;
import net.torocraft.toroquest.item.armor.ItemLegendaryBanditArmor;
import net.torocraft.toroquest.item.armor.ItemRoyalArmor;
import net.torocraft.toroquest.item.armor.ItemSamuraiArmor;
import net.torocraft.toroquest.item.armor.ItemToroArmor;

public class ToroQuestItems
{

	@SideOnly(Side.CLIENT)
	public static final void registerRenders()
	{
		ItemRoyalArmor.registerRenders();
		ItemToroArmor.registerRenders();
		ItemSamuraiArmor.registerRenders();
		ItemBanditArmor.registerRenders();
		ItemLegendaryBanditArmor.registerRenders();
		ItemToroLeather.registerRenders();
		ItemEnderIdol.registerRenders();
		ItemRecruitmentPapers.registerRenders();
		ItemTrophyPig.registerRenders();
		ItemTrophySkeleton.registerRenders();
		ItemTrophyMage.registerRenders();
		ItemTrophyBeholder.registerRenders();
		ItemTrophyTitan.registerRenders();
		ItemTrophySpider.registerRenders();
		ItemTownScroll.registerRenders();
		ItemScrollEarth.registerRenders();
		ItemScrollFire.registerRenders();
		ItemScrollWind.registerRenders();
		ItemScrollWater.registerRenders();
		ItemScrollSun.registerRenders();
		ItemScrollMoon.registerRenders();
		ItemCityKey.registerRenders();
		ItemArtifactRed.registerRenders();
		ItemArtifactBlack.registerRenders();
		ItemArtifactGreen.registerRenders();
		ItemArtifactBlue.registerRenders();
		ItemArtifactBrown.registerRenders();
		ItemArtifactYellow.registerRenders();
		ItemDwarvenArtifact.registerRenders();
	}
	
	
	
	
	
}
