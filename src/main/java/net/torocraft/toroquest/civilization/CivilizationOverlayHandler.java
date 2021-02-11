package net.torocraft.toroquest.civilization;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.configuration.ConfigurationHandler;
import net.torocraft.toroquest.util.Hud;
import net.torocraft.toroquest.util.ToroGuiUtils;

public class CivilizationOverlayHandler extends Hud {

	String displayPosition;
	private final int PADDING_FROM_EDGE = 5;
	int screenWidth;
	int screenHeight;

	int badgeWidth = 20;
	int badgeHeight = 25;

	public CivilizationOverlayHandler(Minecraft mc) {
		super(mc, 20, 10);
	}

	@Override
	public void render(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		EntityPlayerSP player = mc.player;

		if (player.dimension != 0) {
			return;
		}

		Province civ = PlayerCivilizationCapabilityImpl.get(player).getInCivilization();

		if (civ == null || civ.civilization == null) {
			return;
		}

		displayPosition = ConfigurationHandler.repDisplayPosition;

		if ("OFF".equals(displayPosition)) {
			return;
		}

		drawCurrentCivilizationIcon(civ, player);
	}

	private void drawCurrentCivilizationIcon(Province civ, EntityPlayerSP player)
	{
		drawReputationText(civ, player);
		drawCivilizationBadge(civ.civilization);
	}

	private void drawReputationText(Province civ, EntityPlayerSP player)
	{
		int textX = determineTextX();
		int textY = determineIconY();
		
		ReputationLevel rep = PlayerCivilizationCapabilityImpl.get(player).getReputationLevel(civ.civilization);
		
		// LEGEND(), HERO(), CHAMPION(), EXALTED(), REVERED(), HONORED(), FRIENDLY(), NEUTRAL(), UNFRIENDLY(), HOSTILE(), HATED(), EXILED();
		
		int color = 0xffffff;
		
		switch ( rep )
		{
		
			case EXILED:
			{
				color = 0x550000;
				break;
			}
			case HATED:
			{
				color = 0xee1122;
				break;
			}
			case HOSTILE:
			{
				color = 0xff5555;
				break;
			}
			case UNFRIENDLY:
			{
				color = 0xffaaaa;
				break;
			}
			case NEUTRAL:
			{
				color = 0xffeeaa;
				break;
			}
			case FRIENDLY:
			{
				color = 0xbbeebb;
				break;
			}
			case HONORED:
			{
				color = 0x99eea0;
				break;
			}
			case REVERED:
			{
				color = 0x50ee60;
				break;
			}
			case EXALTED:
			{
				color = 0x00cc25;
				break;
			}
			case CHAMPION:
			{
				color = 0x99eeff;
				break;
			}
			case HERO:
			{
				color = 0xff88dd;
				break;
			}
			case LEGEND:
			{
				color = 0xffaa00;
				break;
			}
		}
		
		if (displayPosition.contains("RIGHT"))
		{
			textY -= 10;
			textX -= 10;
			drawRightString("§lHouse " + civ.civilization.getLocalizedName(), textX, textY, 0xffffff);
			textY += 10;
			
			// 1
			drawRightString(civ.name, textX, textY, 0xffffff);

			textY += 10;
			
			// 2
			drawRightString(rep.getLocalname(), textX, textY, color);
			textY += 10;
			
			// 3
			drawRightString("Reputation: " + Integer.toString(PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization), 10), textX, textY, 0xffffff);
		}
		else
		{
			textY -= 10;
			textX += 10;
			drawString("§lHouse " + civ.civilization.getLocalizedName(), textX, textY, 0xffffff);
			textY += 10;
			
			// 1
			drawString(civ.name, textX, textY, 0xffffff);

			textY += 10;

			// 2
			drawString(PlayerCivilizationCapabilityImpl.get(player).getReputationLevel(civ.civilization).getLocalname(), textX, textY, color);
			textY += 10;
			
			// 3
			drawString("Reputation: " + Integer.toString(PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization), 10), textX, textY, 0xffffff);
		}
	}

	private void drawCivilizationBadge(CivilizationType civType)
	{
		int badgeX = determineBadgeX();
		int badgeY = determineIconY();
		
		GlStateManager.pushAttrib();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		
		ToroGuiUtils.drawOverlayIcon(mc, badgeX - 2, badgeY, 0, 96, 20, 27);
		ToroGuiUtils.drawOverlayIcon(mc, badgeX, badgeY + 3, iconIndex(civType), 0);
		
		GlStateManager.popAttrib();
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
	}

	private int determineTextX()
	{
		int x = PADDING_FROM_EDGE + badgeWidth;

		if (displayPosition.contains("RIGHT"))
		{
			x = screenWidth - PADDING_FROM_EDGE - badgeWidth;
		}

		if (displayPosition.contains("CENTER"))
		{
			x = (screenWidth + badgeWidth + PADDING_FROM_EDGE) / 2;
		}

		return x + ConfigurationHandler.repDisplayX;
	}

	private int determineBadgeX()
	{
		int x = PADDING_FROM_EDGE;

		if (displayPosition.contains("RIGHT"))
		{
			x = screenWidth - badgeWidth;
		}

		if (displayPosition.contains("CENTER"))
		{
			x = (screenWidth - badgeWidth) / 2;
		}

		return x + ConfigurationHandler.repDisplayX;
	}

	private int determineIconY() {

		int y = PADDING_FROM_EDGE;

		if (displayPosition.contains("BOTTOM")) {
			y = screenHeight - PADDING_FROM_EDGE - badgeHeight;
		}

		return y + ConfigurationHandler.repDisplayY;
	}

	private int iconIndex(CivilizationType civ) {
		switch (civ) {
		case EARTH:
			return 0;
		case FIRE:
			return 5;
		case MOON:
			return 4;
		case SUN:
			return 1;
		case WATER:
			return 2;
		case WIND:
			return 3;
		default:
			return 0;
		}
	}

}

//int i = PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization);
//if ( i < 0 )
//{
//	drawString( "Bounty: " + Integer.toString(PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization), 10), textX, textY,
//	0xffffff);
//}
//else
//{drawString("Reputation: " + Integer.toString(PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization), 10), textX, textY,
//		0xffffff);}

//drawRightString(Integer.toString(PlayerCivilizationCapabilityImpl.get(player).getReputation(civ.civilization), 10) + " Rep", textX, textY,
//		0xffffff);
//textY += 10;
//
//drawRightString(PlayerCivilizationCapabilityImpl.get(player).getReputationLevel(civ.civilization).getLocalname(), textX, textY, 0xffffff);
//textY += 10;
