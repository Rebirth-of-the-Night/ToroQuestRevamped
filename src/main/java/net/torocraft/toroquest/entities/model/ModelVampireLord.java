package net.torocraft.toroquest.entities.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class ModelVampireLord extends ModelBase
{
	private final ModelRenderer head;
	private final ModelRenderer head3_r1;
	private final ModelRenderer horns;
	private final ModelRenderer right_r1;
	private final ModelRenderer left_r1;
	private final ModelRenderer right_r2;
	private final ModelRenderer left_r2;
	private final ModelRenderer right_r3;
	private final ModelRenderer left_r3;
	private final ModelRenderer bot_jaw;
	private final ModelRenderer body;
	private final ModelRenderer cape2_r1;
	private final ModelRenderer right_wing_r1;
	private final ModelRenderer left_wing_r1;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer rib1;
	private final ModelRenderer right_r4;
	private final ModelRenderer left_r4;
	private final ModelRenderer right_r5;
	private final ModelRenderer left_r5;
	private final ModelRenderer right_r6;
	private final ModelRenderer left_r6;
	private final ModelRenderer rib2;
	private final ModelRenderer right_r7;
	private final ModelRenderer left_r7;
	private final ModelRenderer right_r8;
	private final ModelRenderer left_r8;
	private final ModelRenderer right_r9;
	private final ModelRenderer left_r9;
	private final ModelRenderer rib3;
	private final ModelRenderer right_r10;
	private final ModelRenderer left_r10;
	private final ModelRenderer right_r11;
	private final ModelRenderer left_r11;
	private final ModelRenderer right_r12;
	private final ModelRenderer left_r12;
	private final ModelRenderer rib4;
	private final ModelRenderer right_r13;
	private final ModelRenderer left_r13;
	private final ModelRenderer right_r14;
	private final ModelRenderer left_r14;
	private final ModelRenderer right_r15;
	private final ModelRenderer left_r15;
	private final ModelRenderer right_leg;
	private final ModelRenderer left_leg;
	private final ModelRenderer right_arm;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer left_arm;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;

	public ModelVampireLord()
	{
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(-0.3F, -23.8732F, 0.6986F);
		head.cubeList.add(new ModelBox(head, 1, 2, -3.7F, -9.2488F, -5.8676F, 7, 6, 8, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 13, 34, -3.7F, -3.2488F, -5.8676F, 7, 6, 0, 0.0F, false));

		head3_r1 = new ModelRenderer(this);
		head3_r1.setRotationPoint(-0.2F, -3.2488F, -5.8676F);
		head.addChild(head3_r1);
		setRotationAngle(head3_r1, 1.5708F, 0.0F, 0.0F);
		head3_r1.cubeList.add(new ModelBox(head3_r1, 0, 26, -3.5F, 0.0F, 0.0F, 7, 8, 0, 0.0F, false));

		horns = new ModelRenderer(this);
		horns.setRotationPoint(-0.2F, -10.5786F, -9.606F);
		head.addChild(horns);
		setRotationAngle(horns, -0.4363F, 0.0F, 0.0F);
		horns.cubeList.add(new ModelBox(horns, 0, 30, -8.5F, -0.875F, 5.7543F, 17, 2, 2, 0.0F, false));

		right_r1 = new ModelRenderer(this);
		right_r1.setRotationPoint(-6.7461F, -0.375F, -2.5548F);
		horns.addChild(right_r1);
		setRotationAngle(right_r1, 0.0F, 1.4399F, 0.0F);
		right_r1.cubeList.add(new ModelBox(right_r1, 24, 16, -2.5F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));

		left_r1 = new ModelRenderer(this);
		left_r1.setRotationPoint(6.7461F, -0.375F, -2.5548F);
		horns.addChild(left_r1);
		setRotationAngle(left_r1, 0.0F, -1.4399F, 0.0F);
		left_r1.cubeList.add(new ModelBox(left_r1, 24, 16, -2.5F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));

		right_r2 = new ModelRenderer(this);
		right_r2.setRotationPoint(-8.2597F, 28.3518F, -4.2427F);
		horns.addChild(right_r2);
		setRotationAngle(right_r2, 0.0F, -2.0944F, 0.0F);
		right_r2.cubeList.add(new ModelBox(right_r2, 26, 18, 2.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, false));

		left_r2 = new ModelRenderer(this);
		left_r2.setRotationPoint(8.2597F, 28.3518F, -4.2427F);
		horns.addChild(left_r2);
		setRotationAngle(left_r2, 0.0F, 2.0944F, 0.0F);
		left_r2.cubeList.add(new ModelBox(left_r2, 26, 18, -7.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, true));

		right_r3 = new ModelRenderer(this);
		right_r3.setRotationPoint(-8.3239F, 28.3518F, -1.3028F);
		horns.addChild(right_r3);
		setRotationAngle(right_r3, 0.0F, -1.0908F, 0.0F);
		right_r3.cubeList.add(new ModelBox(right_r3, 36, 16, 2.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, false));

		left_r3 = new ModelRenderer(this);
		left_r3.setRotationPoint(8.3239F, 28.3518F, -1.3028F);
		horns.addChild(left_r3);
		setRotationAngle(left_r3, 0.0F, 1.0908F, 0.0F);
		left_r3.cubeList.add(new ModelBox(left_r3, 36, 16, -7.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, true));

		bot_jaw = new ModelRenderer(this);
		bot_jaw.setRotationPoint(-1.2F, 29.0F, -10.5F); //-2.2467F
		head.addChild(bot_jaw);
		setRotationAngle(bot_jaw, 0.2182F, 0.0F, 0.0F);
		bot_jaw.cubeList.add(new ModelBox(bot_jaw, 31, 4, -1.5F, -30.8939F, 2.6603F, 5, 5, 7, 0.0F, false));
		bot_jaw.cubeList.add(new ModelBox(bot_jaw, 0, 26, -1.5F, -26.8939F, 2.6603F, 5, 0, 7, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(3.0F, 6.9F, -0.5F);
		body.cubeList.add(new ModelBox(body, 0, 29, -5.0F, -23.2492F, 1.5103F, 3, 12, 3, 0.0F, false));

		cape2_r1 = new ModelRenderer(this);
		cape2_r1.setRotationPoint(0.0F, -28.0F, -1.0F);
		body.addChild(cape2_r1);
		setRotationAngle(cape2_r1, 0.1745F, 0.0F, 0.0F);
		cape2_r1.cubeList.add(new ModelBox(cape2_r1, 71, 64, -11.0F, -1.0F, 0.0F, 15, 7, 5, 0.0F, false));
		cape2_r1.cubeList.add(new ModelBox(cape2_r1, 92, 76, -11.0F, -1.0F, 5.0F, 15, 16, 0, 0.0F, false));

		right_wing_r1 = new ModelRenderer(this);
		right_wing_r1.setRotationPoint(-5.0F, -21.0F, 3.0F);
		body.addChild(right_wing_r1);
		setRotationAngle(right_wing_r1, -0.3491F, 0.0F, 0.0873F);
		right_wing_r1.cubeList.add(new ModelBox(right_wing_r1, 46, 29, -33.0F, -35.0F, 0.0F, 33, 35, 0, 0.0F, true));

		left_wing_r1 = new ModelRenderer(this);
		left_wing_r1.setRotationPoint(-1.0F, -21.0F, 3.0F);
		body.addChild(left_wing_r1);
		setRotationAngle(left_wing_r1, -0.3491F, 0.0F, -0.0873F);
		left_wing_r1.cubeList.add(new ModelBox(left_wing_r1, 46, 29, -1.0F, -35.0F, 0.0F, 33, 35, 0, 0.0F, false));

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(-3.5F, -11.2492F, 4.5103F);
		body.addChild(body_r1);
		setRotationAngle(body_r1, -0.1745F, 0.0F, 0.0F);
		body_r1.cubeList.add(new ModelBox(body_r1, 0, 18, -5.5F, 0.0F, -4.0F, 11, 6, 4, 0.0F, false));

		body_r2 = new ModelRenderer(this);
		body_r2.setRotationPoint(-3.5F, -26.8297F, 2.1801F);
		body.addChild(body_r2);
		setRotationAngle(body_r2, 0.2182F, 0.0F, 0.0F);
		body_r2.cubeList.add(new ModelBox(body_r2, 0, 16, -1.5F, -4.0F, -1.5F, 3, 8, 3, 0.0F, false));

		rib1 = new ModelRenderer(this);
		rib1.setRotationPoint(-3.5F, 2.3571F, -1.8058F);
		body.addChild(rib1);
		setRotationAngle(rib1, 0.2182F, 0.0F, 0.0F);
		rib1.cubeList.add(new ModelBox(rib1, 0, 16, -5.5F, -29.5839F, 9.1527F, 11, 2, 2, 0.0F, false));

		right_r4 = new ModelRenderer(this);
		right_r4.setRotationPoint(-2.8077F, -1.3571F, -4.2542F);
		rib1.addChild(right_r4);
		setRotationAngle(right_r4, 0.0F, 0.3927F, 0.0F);
		right_r4.cubeList.add(new ModelBox(right_r4, 0, 16, -4.8523F, -28.2268F, 5.179F, 5, 1, 1, 0.0F, false));

		left_r4 = new ModelRenderer(this);
		left_r4.setRotationPoint(2.8077F, -1.3571F, -4.2542F);
		rib1.addChild(left_r4);
		setRotationAngle(left_r4, 0.0F, -0.3927F, 0.0F);
		left_r4.cubeList.add(new ModelBox(left_r4, 0, 16, -0.1477F, -28.2268F, 5.179F, 5, 1, 1, 0.0F, true));

		right_r5 = new ModelRenderer(this);
		right_r5.setRotationPoint(-5.2597F, -0.3571F, -0.8443F);
		rib1.addChild(right_r5);
		setRotationAngle(right_r5, 0.0F, -2.0944F, 0.0F);
		right_r5.cubeList.add(new ModelBox(right_r5, 1, 16, 2.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, false));

		left_r5 = new ModelRenderer(this);
		left_r5.setRotationPoint(5.2597F, -0.3571F, -0.8443F);
		rib1.addChild(left_r5);
		setRotationAngle(left_r5, 0.0F, 2.0944F, 0.0F);
		left_r5.cubeList.add(new ModelBox(left_r5, 1, 16, -7.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, true));

		right_r6 = new ModelRenderer(this);
		right_r6.setRotationPoint(-5.3239F, -0.3571F, 2.0956F);
		rib1.addChild(right_r6);
		setRotationAngle(right_r6, 0.0F, -1.0908F, 0.0F);
		right_r6.cubeList.add(new ModelBox(right_r6, 0, 16, 2.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, false));

		left_r6 = new ModelRenderer(this);
		left_r6.setRotationPoint(5.3239F, -0.3571F, 2.0956F);
		rib1.addChild(left_r6);
		setRotationAngle(left_r6, 0.0F, 1.0908F, 0.0F);
		left_r6.cubeList.add(new ModelBox(left_r6, 0, 16, -7.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, true));

		rib2 = new ModelRenderer(this);
		rib2.setRotationPoint(-3.5F, 3.3571F, -1.5058F);
		body.addChild(rib2);
		setRotationAngle(rib2, 0.2182F, 0.0F, 0.0F);
		rib2.cubeList.add(new ModelBox(rib2, 0, 16, -5.5F, -27.6252F, 9.0762F, 11, 2, 2, 0.0F, false));

		right_r7 = new ModelRenderer(this);
		right_r7.setRotationPoint(-2.8077F, 0.6016F, -4.3306F);
		rib2.addChild(right_r7);
		setRotationAngle(right_r7, 0.0F, 0.3927F, 0.0F);
		right_r7.cubeList.add(new ModelBox(right_r7, 0, 16, -4.8523F, -28.2268F, 5.179F, 5, 1, 1, 0.0F, false));

		left_r7 = new ModelRenderer(this);
		left_r7.setRotationPoint(2.8077F, 0.6016F, -4.3306F);
		rib2.addChild(left_r7);
		setRotationAngle(left_r7, 0.0F, -0.3927F, 0.0F);
		left_r7.cubeList.add(new ModelBox(left_r7, 0, 16, -0.1477F, -28.2268F, 5.179F, 5, 1, 1, 0.0F, true));

		right_r8 = new ModelRenderer(this);
		right_r8.setRotationPoint(-5.2597F, 1.6016F, -0.9208F);
		rib2.addChild(right_r8);
		setRotationAngle(right_r8, 0.0F, -2.0944F, 0.0F);
		right_r8.cubeList.add(new ModelBox(right_r8, 1, 16, 2.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, false));

		left_r8 = new ModelRenderer(this);
		left_r8.setRotationPoint(5.2597F, 1.6016F, -0.9208F);
		rib2.addChild(left_r8);
		setRotationAngle(left_r8, 0.0F, 2.0944F, 0.0F);
		left_r8.cubeList.add(new ModelBox(left_r8, 1, 16, -7.8234F, -29.2268F, -3.5734F, 5, 2, 2, 0.0F, true));

		right_r9 = new ModelRenderer(this);
		right_r9.setRotationPoint(-5.3239F, 1.6016F, 2.0192F);
		rib2.addChild(right_r9);
		setRotationAngle(right_r9, 0.0F, -1.0908F, 0.0F);
		right_r9.cubeList.add(new ModelBox(right_r9, 0, 16, 2.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, false));

		left_r9 = new ModelRenderer(this);
		left_r9.setRotationPoint(5.3239F, 1.6016F, 2.0192F);
		rib2.addChild(left_r9);
		setRotationAngle(left_r9, 0.0F, 1.0908F, 0.0F);
		left_r9.cubeList.add(new ModelBox(left_r9, 0, 16, -7.9524F, -29.2268F, 2.3383F, 5, 2, 2, 0.0F, true));

		rib3 = new ModelRenderer(this);
		rib3.setRotationPoint(-3.5F, 8.3571F, -1.5058F);
		body.addChild(rib3);
		setRotationAngle(rib3, 0.0F, 0.0F, 0.0F);
		rib3.cubeList.add(new ModelBox(rib3, 0, 16, -5.5F, -29.9307F, 3.4931F, 11, 2, 2, 0.0F, false));

		right_r10 = new ModelRenderer(this);
		right_r10.setRotationPoint(-2.346F, -1.0307F, -2.8799F);
		rib3.addChild(right_r10);
		setRotationAngle(right_r10, 0.0F, 0.3927F, 0.0F);
		right_r10.cubeList.add(new ModelBox(right_r10, 0, 16, -2.5F, -28.9F, -0.5F, 4, 1, 1, 0.0F, false));

		left_r10 = new ModelRenderer(this);
		left_r10.setRotationPoint(2.346F, -1.0307F, -2.8799F);
		rib3.addChild(left_r10);
		setRotationAngle(left_r10, 0.0F, -0.3927F, 0.0F);
		left_r10.cubeList.add(new ModelBox(left_r10, 0, 16, -1.5F, -28.9F, -0.5F, 4, 1, 1, 0.0F, true));

		right_r11 = new ModelRenderer(this);
		right_r11.setRotationPoint(-4.798F, -0.0307F, 0.53F);
		rib3.addChild(right_r11);
		setRotationAngle(right_r11, 0.0F, -2.0944F, 0.0F);
		right_r11.cubeList.add(new ModelBox(right_r11, 1, 16, -2.5F, -29.9F, -0.5F, 5, 2, 2, 0.0F, false));

		left_r11 = new ModelRenderer(this);
		left_r11.setRotationPoint(4.798F, -0.0307F, 0.53F);
		rib3.addChild(left_r11);
		setRotationAngle(left_r11, 0.0F, 2.0944F, 0.0F);
		left_r11.cubeList.add(new ModelBox(left_r11, 1, 16, -2.5F, -29.9F, -0.5F, 5, 2, 2, 0.0F, true));

		right_r12 = new ModelRenderer(this);
		right_r12.setRotationPoint(-5.3239F, -0.0307F, 2.5829F);
		rib3.addChild(right_r12);
		setRotationAngle(right_r12, 0.0F, -1.0908F, 0.0F);
		right_r12.cubeList.add(new ModelBox(right_r12, 0, 16, -1.5F, -29.9F, -0.5F, 4, 2, 2, 0.0F, false));

		left_r12 = new ModelRenderer(this);
		left_r12.setRotationPoint(5.3239F, -0.0307F, 2.5829F);
		rib3.addChild(left_r12);
		setRotationAngle(left_r12, 0.0F, 1.0908F, 0.0F);
		left_r12.cubeList.add(new ModelBox(left_r12, 0, 16, -2.5F, -29.9F, -0.5F, 4, 2, 2, 0.0F, true));

		rib4 = new ModelRenderer(this);
		rib4.setRotationPoint(-3.5F, 11.3571F, -1.5058F);
		body.addChild(rib4);
		setRotationAngle(rib4, 0.0F, 0.0F, 0.0F);
		rib4.cubeList.add(new ModelBox(rib4, 0, 16, -5.5F, -29.9307F, 3.4931F, 11, 2, 2, 0.0F, false));

		right_r13 = new ModelRenderer(this);
		right_r13.setRotationPoint(-1.8842F, -0.0307F, -1.9929F);
		rib4.addChild(right_r13);
		setRotationAngle(right_r13, 0.0F, 0.3927F, 0.0F);
		right_r13.cubeList.add(new ModelBox(right_r13, 0, 16, -2.5F, -28.9F, -0.5F, 3, 1, 1, 0.0F, false));

		left_r13 = new ModelRenderer(this);
		left_r13.setRotationPoint(1.8842F, -0.0307F, -1.9929F);
		rib4.addChild(left_r13);
		setRotationAngle(left_r13, 0.0F, -0.3927F, 0.0F);
		left_r13.cubeList.add(new ModelBox(left_r13, 0, 16, -0.5F, -28.9F, -0.5F, 3, 1, 1, 0.0F, true));

		right_r14 = new ModelRenderer(this);
		right_r14.setRotationPoint(-4.3362F, -0.0307F, 1.417F);
		rib4.addChild(right_r14);
		setRotationAngle(right_r14, 0.0F, -2.0944F, 0.0F);
		right_r14.cubeList.add(new ModelBox(right_r14, 1, 16, -2.5F, -29.9F, -0.5F, 5, 2, 2, 0.0F, false));

		left_r14 = new ModelRenderer(this);
		left_r14.setRotationPoint(4.3362F, -0.0307F, 1.417F);
		rib4.addChild(left_r14);
		setRotationAngle(left_r14, 0.0F, 2.0944F, 0.0F);
		left_r14.cubeList.add(new ModelBox(left_r14, 1, 16, -2.5F, -29.9F, -0.5F, 5, 2, 2, 0.0F, true));

		right_r15 = new ModelRenderer(this);
		right_r15.setRotationPoint(-5.3239F, -0.0307F, 2.5829F);
		rib4.addChild(right_r15);
		setRotationAngle(right_r15, 0.0F, -1.0908F, 0.0F);
		right_r15.cubeList.add(new ModelBox(right_r15, 0, 16, -0.5F, -29.9F, -0.5F, 3, 2, 2, 0.0F, false));

		left_r15 = new ModelRenderer(this);
		left_r15.setRotationPoint(5.3239F, -0.0307F, 2.5829F);
		rib4.addChild(left_r15);
		setRotationAngle(left_r15, 0.0F, 1.0908F, 0.0F);
		left_r15.cubeList.add(new ModelBox(left_r15, 0, 16, -2.5F, -29.9F, -0.5F, 3, 2, 2, 0.0F, true));

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-2.0F, 0.0F, 1.0F);
		right_leg.cubeList.add(new ModelBox(right_leg, 0, 19, -3.0F, 0.0F, -1.0F, 3, 23, 3, 0.0F, false));

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(4.0F, 0.0F, 2.0F);
		left_leg.cubeList.add(new ModelBox(left_leg, 0, 19, -3.0F, 0.0F, -2.0F, 3, 23, 3, 0.0F, true));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-10.0F, -21.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.9025F, 10.3158F, -1.2561F);
		right_arm.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.5672F, 0.0F, 0.0873F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 19, -1.0F, -1.0F, -1.0F, 2, 14, 2, 0.0F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -0.1F, 0.0F);
		right_arm.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.0873F, 0.0F, 0.0873F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 112, 61, -2.0F, -1.0F, -2.0F, 4, 11, 4, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		right_arm.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.0873F, 0.0F, 0.0873F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 19, -1.0F, -1.0F, -1.0F, 2, 11, 2, 0.0F, false));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(21.0F, 24.0F, 0.0F);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-9.2605F, -35.161F, -4.8537F);
		left_arm.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.5672F, 0.0F, -0.0873F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 19, -2.8716F, -2.2197F, 1.4496F, 2, 14, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-12.0F, -45.1F, 0.0F);
		left_arm.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.1745F, 0.0F, -0.0873F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 112, 61, -2.0F, -1.0F, -2.0F, 4, 11, 4, 0.0F, true));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-12.0F, -45.0F, 0.0F);
		left_arm.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.1745F, 0.0F, -0.0873F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 19, -1.0F, -1.0F, -1.0F, 2, 11, 2, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		head.render(f5);
		body.render(f5);
		right_leg.render(f5);
		left_leg.render(f5);
		right_arm.render(f5);
		left_arm.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
		this.right_arm.rotateAngleX = -1.25F * limbSwingAmount;
		this.right_arm.rotateAngleY = 0.2F * limbSwingAmount;
        this.right_arm.rotateAngleZ = -0.2F * limbSwingAmount;
		
        this.left_arm.rotateAngleZ = 0.1F * limbSwingAmount;
        this.left_arm.rotateAngleY = 0.1F * limbSwingAmount;
        
        this.right_wing_r1.rotateAngleZ = -0.5F * limbSwingAmount;
        this.left_wing_r1.rotateAngleZ = 0.5F * limbSwingAmount;
        
        this.body.rotateAngleZ = 0.1F * limbSwingAmount;
        this.body_r1.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;
        this.body_r2.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;
        
        this.head.rotateAngleZ = 0.05F * limbSwingAmount - 0.05F;
        this.head.rotateAngleY = -0.1F * limbSwingAmount;
        this.bot_jaw.rotateAngleX = 0.15F * limbSwingAmount - 0.15F;
        //this.bot_jaw.rotateAngleZ = 0.01F * limbSwingAmount - 0.01F;
        this.left_r15.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;

        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
//        this.right_leg.rotateAngleX = -limbSwingAmount;
//        this.left_leg.rotateAngleX = limbSwingAmount;
        
        this.head.rotateAngleY = netHeadYaw * 0.017453292F;
        this.head.rotateAngleX = headPitch * 0.017453292F;
    }
}