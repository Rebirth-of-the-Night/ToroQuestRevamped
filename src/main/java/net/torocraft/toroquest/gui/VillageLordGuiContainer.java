package net.torocraft.toroquest.gui;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.torocraft.toroquest.civilization.CivilizationType;
import net.torocraft.toroquest.civilization.CivilizationUtil;
import net.torocraft.toroquest.civilization.Province;
import net.torocraft.toroquest.civilization.ReputationLevel;
import net.torocraft.toroquest.civilization.player.PlayerCivilizationCapabilityImpl;
import net.torocraft.toroquest.civilization.quests.QuestBase;
import net.torocraft.toroquest.config.ToroQuestConfiguration;
import net.torocraft.toroquest.inventory.IVillageLordInventory;
import net.torocraft.toroquest.network.ToroQuestPacketHandler;
import net.torocraft.toroquest.network.message.MessageQuestUpdate;
import net.torocraft.toroquest.network.message.MessageQuestUpdate.Action;
import net.torocraft.toroquest.network.message.MessageSetItemReputationAmount;
import net.torocraft.toroquest.network.message.MessageSetItemReputationAmount.MessageCode;

public class VillageLordGuiContainer extends GuiContainer
{

	private static final ResourceLocation guiTexture = new ResourceLocation("toroquest:textures/gui/lord_gui.png");

	private static final int buttonWidth = 59;
	private static final int buttonHeight = 20;

	private static final int MOUSE_COOLDOWN = 200;
	private static long mousePressed = 0;

	private static int donateRepForItem = 0;
	private static MessageCode donateMessageCode = MessageCode.EMPTY;
	
	private static String civName = "";
	private static String questTitle = "";
	private static String questDescription = "";
	private static boolean questAccepted = false;
	private static boolean questCompleted = false;
	
	private static CivilizationType civ = null;
	
	private EntityPlayer p = null;

	private final IVillageLordInventory inventory;
    
	public VillageLordGuiContainer()
	{
		this(null, null, null);
	}

	public VillageLordGuiContainer(EntityPlayer player, IVillageLordInventory inventory, World world)
	{
		super(new VillageLordContainer(player, inventory, world));
		
		this.inventory = inventory;
		this.p = player;
		xSize = 238;
		ySize = 240;
		mousePressed = Minecraft.getSystemTime();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(guiTexture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawDonateButton(mouseX, mouseY);

		if ( questAccepted )
		{			
			if ( questCompleted )
			{
				drawQuestCompleteButton(mouseX, mouseY);
				drawQuestAbandonButton(mouseX, mouseY);
			}
			else
			{
				drawCompleteButton(mouseX, mouseY);
				drawAbandonButton(mouseX, mouseY);
			}
		}
		else
		{
			drawAcceptButton(mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		final int LABEL_XPOS = 5;
		final int LABEL_YPOS = 5;
		drawGuiTitle(LABEL_XPOS, LABEL_YPOS);
		updateReputationDisplay(LABEL_XPOS, LABEL_YPOS);
		drawQuestTitle(LABEL_XPOS, LABEL_YPOS);
	}
	
	private void drawGuiTitle(int xPos, int yPos)
	{
		String text = I18n.format("quest.gui.title", VillageLordGuiContainer.civName);
		
		fontRenderer.drawString("§l"+text+"§r", xPos + 2, yPos, Color.darkGray.getRGB());
		if ( this.p != null && civ != null )
		{
			int rep = PlayerCivilizationCapabilityImpl.get(this.p).getReputation(civ);

			fontRenderer.drawStringWithShadow("§lPermissions & Perks:§r", xPos + 178, yPos + 134, -200);

			if ( rep >= 0 )
			{
				fontRenderer.drawStringWithShadow("§lRecruit guards§r", xPos + 178, yPos + 145, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lRecruit guards§r", xPos + 178, yPos + 145, Color.darkGray.getRGB());
			}
			
			if ( rep >= 50 || !ToroQuestConfiguration.loseReputationForCropGrief )
			{
				fontRenderer.drawStringWithShadow("§lHarvest crops§r", xPos + 178, yPos + 156, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lHarvest crops§r", xPos + 178, yPos + 156, Color.darkGray.getRGB());
			}
			
			
			if ( rep >= 100 || !ToroQuestConfiguration.loseReputationForAnimalGrief )
			{
				fontRenderer.drawStringWithShadow("§lButcher livestock§r", xPos + 178, yPos + 167, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lButcher livestock§r", xPos + 178, yPos + 167, Color.darkGray.getRGB());
			}
			
			
			if ( rep >= 250 || !ToroQuestConfiguration.loseReputationForBlockGrief )
			{
				fontRenderer.drawStringWithShadow("§lGrief & firespread§r", xPos + 178, yPos + 178, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lGrief & firespread§r", xPos + 178, yPos + 178, Color.darkGray.getRGB());
			}
			
			
			if ( rep >= 500 )
			{
				fontRenderer.drawStringWithShadow("§lRename province§r", xPos + 178, yPos + 189, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lRename province§r", xPos + 178, yPos + 189, Color.darkGray.getRGB());
			}
			
			if ( rep >= 1000 )
			{
				fontRenderer.drawStringWithShadow("§lLegendary quests§r", xPos + 178, yPos + 200, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lLegendary quests§r", xPos + 178, yPos + 200, Color.darkGray.getRGB());
			}
			
			if ( rep >= 2000 )
			{
				fontRenderer.drawStringWithShadow("§lDouble quest rewards§r", xPos + 178, yPos + 211, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lDouble quest rewards§r", xPos + 178, yPos + 211, Color.darkGray.getRGB());
			}
			
			if ( rep >= 3000 )
			{
				fontRenderer.drawStringWithShadow("§lQuests grant experience§r", xPos + 178, yPos + 222, Color.white.getRGB());
			}
			else
			{
				fontRenderer.drawStringWithShadow("§lQuests grant experience§r", xPos + 178, yPos + 222, Color.darkGray.getRGB());
			}
		}
	}

	private void drawDonateButton(int mouseX, int mouseY)
	{
		GuiButton submitButton = new GuiButton(0, guiLeft + 84, guiTop + 16, buttonWidth, buttonHeight, I18n.format("quest.gui.button.donate"));
		submitButton.drawButton(mc, mouseX, mouseY, 1);
		if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1)
		{
			if (submitButton.mousePressed(mc, mouseX, mouseY) && mouseCooldownOver())
			{
				mousePressed = Minecraft.getSystemTime();
				MessageQuestUpdate message = new MessageQuestUpdate();
				message.action = Action.DONATE;
				message.lordEntityId = inventory.getEntityId();
				ToroQuestPacketHandler.INSTANCE.sendToServer(message);
			}
		}
	}

	private void drawAcceptButton(int mouseX, int mouseY)
	{
		drawActionButton("quest.gui.button.accept", Action.ACCEPT, mouseX, mouseY, 0);
	}

	private void drawCompleteButton(int mouseX, int mouseY)
	{
		drawActionButton("quest.gui.button.complete", Action.COMPLETE, mouseX, mouseY, 0);
	}
	
	private void drawQuestCompleteButton(int mouseX, int mouseY)
	{
		drawActionButton("quest.gui.button.questcomplete", Action.COMPLETE, mouseX, mouseY, 0);
	}

	private void drawAbandonButton(int mouseX, int mouseY)
	{
		drawActionButton("quest.gui.button.reject", Action.REJECT, mouseX, mouseY, -65);
	}
	
	private void drawQuestAbandonButton(int mouseX, int mouseY)
	{
		drawActionButton("quest.gui.button.rejectcomplete", Action.REJECT, mouseX, mouseY, -65);
	}

	protected void drawActionButton(String label, Action action, int mouseX, int mouseY, int xOffset) {
		GuiButton abandonButton = new GuiButton(0, guiLeft + 84 + xOffset, guiTop + 131, buttonWidth, buttonHeight, I18n.format(label));
		abandonButton.drawButton(mc, mouseX, mouseY, 1);
		if (Mouse.getEventButtonState() && Mouse.getEventButton() != -1) {
			if (abandonButton.mousePressed(mc, mouseX, mouseY) && mouseCooldownOver()) {
				mousePressed = Minecraft.getSystemTime();
				MessageQuestUpdate message = new MessageQuestUpdate();
				message.action = action;
				message.lordEntityId = inventory.getEntityId();
				ToroQuestPacketHandler.INSTANCE.sendToServer(message);
			}
		}
	}

	private void updateReputationDisplay(int xPos, int yPos)
	{
		// XXX
		if (MessageCode.DONATION.equals(donateMessageCode))
		{
			fontRenderer.drawString(I18n.format("quest.gui.rep_for", donateRepForItem), xPos + 2, yPos + 17, Color.darkGray.getRGB());
		}
		else if (MessageCode.NOTE.equals(donateMessageCode))
		{
			fontRenderer.drawString(I18n.format("quest.gui.reply"), xPos + 2, yPos + 17, Color.darkGray.getRGB());
		}
		else if (MessageCode.STOLEN_ITEM.equals(donateMessageCode))
		{
			fontRenderer.drawString(I18n.format("quest.gui.stolen_item"), xPos + 2, yPos + 17, Color.darkGray.getRGB());
		}
		else if (MessageCode.TROPHY.equals(donateMessageCode))
		{
			fontRenderer.drawString(I18n.format("quest.gui.trophy"), xPos + 2, yPos + 17, Color.darkGray.getRGB());
		}
		else
		{
			fontRenderer.drawString(I18n.format("quest.gui.empty"), xPos + 2, yPos + 17, Color.darkGray.getRGB());
		}
	}

	private void drawQuestTitle(int xPos, int yPos)
	{
		fontRenderer.drawString(questTitle, xPos + 2, yPos + 35, Color.darkGray.getRGB());
		fontRenderer.drawSplitString(questDescription, xPos + 25, yPos + 50, 115, Color.darkGray.getRGB());
	}

	private static String translate(String in) {
		if (in == null || in.trim().length() < 1) {
			return "";
		}
		String[] parts = in.split("\\|");
		if (parts.length == 1) {
			return I18n.format(parts[0]);
		}
		Object[] parameters = Arrays.copyOfRange(parts, 1, parts.length);

		processComplexParamters(parameters);

		return I18n.format(parts[0], parameters);
	}

	private static void processComplexParamters(Object[] parameters) {
		Object o;
		for (int i = 0; i < parameters.length; i++) {
			o = parameters[i];
			if (o != null && o instanceof String && o.toString().startsWith("L:")) {
				parameters[i] = processListParamter(o.toString());
			} else if (o != null && o instanceof String && o.toString().startsWith("D:")) {
				parameters[i] = processDirectionsParamter(o.toString());
			}
		}
	}

	private static Object processDirectionsParamter(String s) {
		String[] parts = s.substring(2).split(";");

		if (parts.length != 3) {
			return s;
		}

		return I18n.format("quest.directions", parts[0], parts[1], parts[2]);
	}

	private static String processListParamter(String s) {
		StringBuilder sb = new StringBuilder();
		String[] sStacks = s.substring(2).split(";");

		boolean isFirst = true;

		for (String sStack : sStacks) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(", ");
			}

			String[] sStackParts = sStack.split(",");

			if (sStackParts.length == 2)
			{
				sb.append(I18n.format(sStackParts[0] + ".name"));
				sb.append("[").append(sStackParts[1]).append("]");
			}
			else
			{
				sb.append(sStackParts);
			}
		}

		return sb.toString();
	}

	public static void setProvinceName(String name)
	{
		civName = name;
	}
	
	public static void setCivilization(CivilizationType name)
	{
		civ = name;
	}

	public static void setDonateInfo(MessageSetItemReputationAmount message)
	{
		donateRepForItem = message.reputation;
		donateMessageCode = message.messageCode;
	}

	public static void setQuestData( String title, String description, boolean accepted, boolean complete )
	{
		questTitle = translate(title);
		questDescription = translate(description);
		questAccepted = accepted;
		questCompleted = complete;
	}

	private boolean mouseCooldownOver()
	{
		return Minecraft.getSystemTime() - mousePressed > MOUSE_COOLDOWN;
	}
}