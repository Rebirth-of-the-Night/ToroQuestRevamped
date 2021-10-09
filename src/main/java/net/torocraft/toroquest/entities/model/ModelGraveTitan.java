package net.torocraft.toroquest.entities.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports


public class ModelGraveTitan extends ModelBase
{
	private final ModelRenderer mainBody;
	private final ModelRenderer body1;
	private final ModelRenderer limbs;
	private final ModelRenderer body_r1;
	private final ModelRenderer body_r2;
	private final ModelRenderer body_r3;
	private final ModelRenderer arm_r1;
	private final ModelRenderer arm_r2;
	private final ModelRenderer arm_r3;
	private final ModelRenderer arm_r4;
	private final ModelRenderer arm_r5;
	private final ModelRenderer arm_r6;
	private final ModelRenderer arm_r7;
	private final ModelRenderer arm_r8;
	private final ModelRenderer arm_r9;
	private final ModelRenderer arm_r10;
	private final ModelRenderer arm_r11;
	private final ModelRenderer arm_r12;
	private final ModelRenderer arm_r13;
	private final ModelRenderer arm_r14;
	private final ModelRenderer arm_r15;
	private final ModelRenderer arm_r16;
	private final ModelRenderer arm_r17;
	private final ModelRenderer arm_r18;
	private final ModelRenderer arm_r19;
	private final ModelRenderer arm_r20;
	private final ModelRenderer arm_r21;
	private final ModelRenderer arm_r22;
	private final ModelRenderer arm_r23;
	private final ModelRenderer arm_r24;
	private final ModelRenderer arm_r25;
	private final ModelRenderer arm_r26;
	private final ModelRenderer arm_r27;
	private final ModelRenderer arm_r28;
	private final ModelRenderer arm_r29;
	private final ModelRenderer arm_r30;
	private final ModelRenderer arm_r31;
	private final ModelRenderer arm_r32;
	private final ModelRenderer arm_r33;
	private final ModelRenderer arm_r34;
	private final ModelRenderer arm_r35;
	private final ModelRenderer arm_r36;
	private final ModelRenderer arm_r37;
	private final ModelRenderer arm_r38;
	private final ModelRenderer arm_r39;
	private final ModelRenderer arm_r40;
	private final ModelRenderer arm_r41;
	private final ModelRenderer arm_r42;
	private final ModelRenderer arm_r43;
	private final ModelRenderer arm_r44;
	private final ModelRenderer arm_r45;
	private final ModelRenderer arm_r46;
	private final ModelRenderer zombie1;
	private final ModelRenderer body_r4;
	private final ModelRenderer bone2;
	private final ModelRenderer leftArm_r1;
	private final ModelRenderer rightArm_r1;
	private final ModelRenderer zombie2;
	private final ModelRenderer bone3;
	private final ModelRenderer zombie3;
	private final ModelRenderer body_r5;
	private final ModelRenderer zombie4;
	private final ModelRenderer head_r1;
	private final ModelRenderer body_r6;
	private final ModelRenderer zombie5;
	private final ModelRenderer body_r7;
	private final ModelRenderer bone;
	private final ModelRenderer legs;
	private final ModelRenderer rightLeg1;
	private final ModelRenderer top;
	private final ModelRenderer zombie7;
	private final ModelRenderer arm_r47;
	private final ModelRenderer zombie10;
	private final ModelRenderer arm_r48;
	private final ModelRenderer zombie6;
	private final ModelRenderer arm_r49;
	private final ModelRenderer zombie11;
	private final ModelRenderer arm_r50;
	private final ModelRenderer bot;
	private final ModelRenderer zombie8;
	private final ModelRenderer arm_r51;
	private final ModelRenderer zombie13;
	private final ModelRenderer arm_r52;
	private final ModelRenderer zombie9;
	private final ModelRenderer arm_r53;
	private final ModelRenderer zombie12;
	private final ModelRenderer arm_r54;
	private final ModelRenderer leftLeg1;
	private final ModelRenderer top3;
	private final ModelRenderer zombie22;
	private final ModelRenderer arm_r55;
	private final ModelRenderer zombie23;
	private final ModelRenderer arm_r56;
	private final ModelRenderer zombie24;
	private final ModelRenderer arm_r57;
	private final ModelRenderer zombie25;
	private final ModelRenderer arm_r58;
	private final ModelRenderer bot3;
	private final ModelRenderer zombie26;
	private final ModelRenderer arm_r59;
	private final ModelRenderer zombie27;
	private final ModelRenderer arm_r60;
	private final ModelRenderer zombie28;
	private final ModelRenderer arm_r61;
	private final ModelRenderer zombie29;
	private final ModelRenderer arm_r62;
	private final ModelRenderer rightLeg2;
	private final ModelRenderer top2;
	private final ModelRenderer zombie14;
	private final ModelRenderer arm_r63;
	private final ModelRenderer zombie15;
	private final ModelRenderer arm_r64;
	private final ModelRenderer zombie16;
	private final ModelRenderer arm_r65;
	private final ModelRenderer zombie17;
	private final ModelRenderer arm_r66;
	private final ModelRenderer bot2;
	private final ModelRenderer zombie18;
	private final ModelRenderer arm_r67;
	private final ModelRenderer zombie19;
	private final ModelRenderer arm_r68;
	private final ModelRenderer zombie20;
	private final ModelRenderer arm_r69;
	private final ModelRenderer zombie21;
	private final ModelRenderer arm_r70;
	private final ModelRenderer leftLeg2;
	private final ModelRenderer top4;
	private final ModelRenderer zombie30;
	private final ModelRenderer arm_r71;
	private final ModelRenderer zombie31;
	private final ModelRenderer arm_r72;
	private final ModelRenderer zombie32;
	private final ModelRenderer arm_r73;
	private final ModelRenderer zombie33;
	private final ModelRenderer arm_r74;
	private final ModelRenderer bot4;
	private final ModelRenderer zombie34;
	private final ModelRenderer arm_r75;
	private final ModelRenderer zombie35;
	private final ModelRenderer arm_r76;
	private final ModelRenderer zombie36;
	private final ModelRenderer arm_r77;
	private final ModelRenderer zombie37;
	private final ModelRenderer arm_r78;

	public ModelGraveTitan()
	{
		textureWidth = 248;
		textureHeight = 248;

		mainBody = new ModelRenderer(this);
		mainBody.setRotationPoint(0.0F, 63.0F, -0.5F);
		

		body1 = new ModelRenderer(this);
		body1.setRotationPoint(0.0F, 0.0F, 0.0F);
		mainBody.addChild(body1);
		body1.cubeList.add(new ModelBox(body1, 0, 118, -23.0F, -159.5F, -25.5F, 48, 80, 51, 0.0F, false));
		body1.cubeList.add(new ModelBox(body1, 0, 118, -23.0F, -159.5F, -25.5F, 48, 80, 51, 0.0F, false));
		body1.cubeList.add(new ModelBox(body1, 63, 0, -21.5F, -155.0F, -24.0F, 45, 70, 48, 0.0F, false));

		limbs = new ModelRenderer(this);
		limbs.setRotationPoint(0.0F, 0.0F, 0.0F);
		body1.addChild(limbs);
		

		body_r1 = new ModelRenderer(this);
		body_r1.setRotationPoint(2.7614F, -98.0549F, -11.1069F);
		limbs.addChild(body_r1);
		setRotationAngle(body_r1, 1.5382F, 0.324F, -2.1017F);
		body_r1.cubeList.add(new ModelBox(body_r1, 0, 0, -4.0F, -17.0F, -3.0F, 8, 34, 6, 0.5F, true));

		body_r2 = new ModelRenderer(this);
		body_r2.setRotationPoint(-11.4572F, -115.4469F, -6.4128F);
		limbs.addChild(body_r2);
		setRotationAngle(body_r2, -0.6598F, 1.115F, -2.729F);
		body_r2.cubeList.add(new ModelBox(body_r2, 0, 0, -4.0F, -17.0F, -3.0F, 8, 34, 6, 0.5F, true));

		body_r3 = new ModelRenderer(this);
		body_r3.setRotationPoint(4.5F, -126.9274F, 16.0228F);
		limbs.addChild(body_r3);
		setRotationAngle(body_r3, -1.2601F, 0.6358F, -1.0751F);
		body_r3.cubeList.add(new ModelBox(body_r3, 0, 0, -4.0F, -17.0F, -3.0F, 8, 34, 6, 0.5F, true));

		arm_r1 = new ModelRenderer(this);
		arm_r1.setRotationPoint(-22.9818F, -108.9666F, 12.0F);
		limbs.addChild(arm_r1);
		setRotationAngle(arm_r1, 2.1385F, -1.0052F, -0.8399F);
		arm_r1.cubeList.add(new ModelBox(arm_r1, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r2 = new ModelRenderer(this);
		arm_r2.setRotationPoint(-11.9818F, -91.9666F, -23.0F);
		limbs.addChild(arm_r2);
		setRotationAngle(arm_r2, -2.4221F, -0.9557F, -1.4617F);
		arm_r2.cubeList.add(new ModelBox(arm_r2, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r3 = new ModelRenderer(this);
		arm_r3.setRotationPoint(-22.9818F, -131.9666F, 12.0F);
		limbs.addChild(arm_r3);
		setRotationAngle(arm_r3, -2.4221F, -0.9557F, -1.4617F);
		arm_r3.cubeList.add(new ModelBox(arm_r3, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r4 = new ModelRenderer(this);
		arm_r4.setRotationPoint(-22.9818F, -146.9666F, -1.0F);
		limbs.addChild(arm_r4);
		setRotationAngle(arm_r4, 2.6881F, -0.8634F, -0.9748F);
		arm_r4.cubeList.add(new ModelBox(arm_r4, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r5 = new ModelRenderer(this);
		arm_r5.setRotationPoint(2.0182F, -80.9666F, -19.0F);
		limbs.addChild(arm_r5);
		setRotationAngle(arm_r5, -1.6015F, -1.0203F, 1.9278F);
		arm_r5.cubeList.add(new ModelBox(arm_r5, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r6 = new ModelRenderer(this);
		arm_r6.setRotationPoint(2.0182F, -88.9666F, -24.0F);
		limbs.addChild(arm_r6);
		setRotationAngle(arm_r6, -1.5033F, 0.3299F, 1.6234F);
		arm_r6.cubeList.add(new ModelBox(arm_r6, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r7 = new ModelRenderer(this);
		arm_r7.setRotationPoint(18.0182F, -107.9666F, -22.0F);
		limbs.addChild(arm_r7);
		setRotationAngle(arm_r7, -1.2559F, -0.3555F, 1.7548F);
		arm_r7.cubeList.add(new ModelBox(arm_r7, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r8 = new ModelRenderer(this);
		arm_r8.setRotationPoint(-19.9818F, -156.9666F, 10.0F);
		limbs.addChild(arm_r8);
		setRotationAngle(arm_r8, 2.2264F, -1.0555F, 0.4599F);
		arm_r8.cubeList.add(new ModelBox(arm_r8, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r9 = new ModelRenderer(this);
		arm_r9.setRotationPoint(2.0182F, -156.9666F, 21.0F);
		limbs.addChild(arm_r9);
		setRotationAngle(arm_r9, 2.6022F, -0.1378F, 0.2226F);
		arm_r9.cubeList.add(new ModelBox(arm_r9, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r10 = new ModelRenderer(this);
		arm_r10.setRotationPoint(2.0182F, -156.9666F, -5.0F);
		limbs.addChild(arm_r10);
		setRotationAngle(arm_r10, -3.0041F, 0.2942F, 0.2922F);
		arm_r10.cubeList.add(new ModelBox(arm_r10, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r11 = new ModelRenderer(this);
		arm_r11.setRotationPoint(18.0182F, -158.9666F, -21.0F);
		limbs.addChild(arm_r11);
		setRotationAngle(arm_r11, -2.7062F, 0.1949F, 0.3649F);
		arm_r11.cubeList.add(new ModelBox(arm_r11, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r12 = new ModelRenderer(this);
		arm_r12.setRotationPoint(15.0182F, -156.9666F, -5.0F);
		limbs.addChild(arm_r12);
		setRotationAngle(arm_r12, -2.6236F, 0.1631F, 0.8596F);
		arm_r12.cubeList.add(new ModelBox(arm_r12, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r13 = new ModelRenderer(this);
		arm_r13.setRotationPoint(27.0182F, -144.9666F, -7.0F);
		limbs.addChild(arm_r13);
		setRotationAngle(arm_r13, -2.6236F, 0.1631F, 1.4268F);
		arm_r13.cubeList.add(new ModelBox(arm_r13, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r14 = new ModelRenderer(this);
		arm_r14.setRotationPoint(28.0182F, -85.9666F, -9.0F);
		limbs.addChild(arm_r14);
		setRotationAngle(arm_r14, -2.9891F, -0.0758F, 1.964F);
		arm_r14.cubeList.add(new ModelBox(arm_r14, 30, 0, -2.0F, -10.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r15 = new ModelRenderer(this);
		arm_r15.setRotationPoint(28.0182F, -110.9666F, -3.0F);
		limbs.addChild(arm_r15);
		setRotationAngle(arm_r15, 3.0124F, 0.0427F, 1.9687F);
		arm_r15.cubeList.add(new ModelBox(arm_r15, 30, 0, -2.0F, -11.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r16 = new ModelRenderer(this);
		arm_r16.setRotationPoint(28.0182F, -98.9666F, -19.0F);
		limbs.addChild(arm_r16);
		setRotationAngle(arm_r16, -2.9277F, 0.1125F, 1.3847F);
		arm_r16.cubeList.add(new ModelBox(arm_r16, 30, 0, -2.0F, -9.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r17 = new ModelRenderer(this);
		arm_r17.setRotationPoint(28.0182F, -123.9666F, 8.0F);
		limbs.addChild(arm_r17);
		setRotationAngle(arm_r17, -2.9277F, 0.1125F, 1.3847F);
		arm_r17.cubeList.add(new ModelBox(arm_r17, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r18 = new ModelRenderer(this);
		arm_r18.setRotationPoint(28.0182F, -87.9666F, 10.0F);
		limbs.addChild(arm_r18);
		setRotationAngle(arm_r18, -2.9277F, 0.1125F, 1.6028F);
		arm_r18.cubeList.add(new ModelBox(arm_r18, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r19 = new ModelRenderer(this);
		arm_r19.setRotationPoint(28.0182F, -118.9666F, -3.0F);
		limbs.addChild(arm_r19);
		setRotationAngle(arm_r19, -2.9277F, 0.1125F, 1.3847F);
		arm_r19.cubeList.add(new ModelBox(arm_r19, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r20 = new ModelRenderer(this);
		arm_r20.setRotationPoint(21.0182F, -122.9666F, -22.0F);
		limbs.addChild(arm_r20);
		setRotationAngle(arm_r20, -2.376F, 0.287F, 1.3228F);
		arm_r20.cubeList.add(new ModelBox(arm_r20, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r21 = new ModelRenderer(this);
		arm_r21.setRotationPoint(3.0182F, -95.9666F, 26.0F);
		limbs.addChild(arm_r21);
		setRotationAngle(arm_r21, 1.5148F, 0.1922F, 1.3272F);
		arm_r21.cubeList.add(new ModelBox(arm_r21, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r22 = new ModelRenderer(this);
		arm_r22.setRotationPoint(23.0182F, -109.9666F, 26.0F);
		limbs.addChild(arm_r22);
		setRotationAngle(arm_r22, 1.8657F, 0.2635F, 1.4073F);
		arm_r22.cubeList.add(new ModelBox(arm_r22, 30, 0, -2.0F, -9.0F, -3.0F, 4, 12, 4, 0.0F, false));

		arm_r23 = new ModelRenderer(this);
		arm_r23.setRotationPoint(23.0182F, -132.9666F, 26.0F);
		limbs.addChild(arm_r23);
		setRotationAngle(arm_r23, 2.3215F, -0.2622F, 1.2966F);
		arm_r23.cubeList.add(new ModelBox(arm_r23, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r24 = new ModelRenderer(this);
		arm_r24.setRotationPoint(-10.9818F, -132.9666F, 26.0F);
		limbs.addChild(arm_r24);
		setRotationAngle(arm_r24, 1.8732F, -0.3527F, 1.4345F);
		arm_r24.cubeList.add(new ModelBox(arm_r24, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r25 = new ModelRenderer(this);
		arm_r25.setRotationPoint(-21.9818F, -118.9666F, -24.0F);
		limbs.addChild(arm_r25);
		setRotationAngle(arm_r25, -0.6908F, 0.2247F, 1.8762F);
		arm_r25.cubeList.add(new ModelBox(arm_r25, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r26 = new ModelRenderer(this);
		arm_r26.setRotationPoint(21.0182F, -138.9666F, -26.0F);
		limbs.addChild(arm_r26);
		setRotationAngle(arm_r26, -2.0689F, 0.2535F, 1.3012F);
		arm_r26.cubeList.add(new ModelBox(arm_r26, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r27 = new ModelRenderer(this);
		arm_r27.setRotationPoint(-19.9818F, -137.9666F, -26.0F);
		limbs.addChild(arm_r27);
		setRotationAngle(arm_r27, -1.3341F, 0.58F, 1.6869F);
		arm_r27.cubeList.add(new ModelBox(arm_r27, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r28 = new ModelRenderer(this);
		arm_r28.setRotationPoint(-10.9818F, -155.9666F, -24.0F);
		limbs.addChild(arm_r28);
		setRotationAngle(arm_r28, -1.3341F, 0.58F, 1.6869F);
		arm_r28.cubeList.add(new ModelBox(arm_r28, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r29 = new ModelRenderer(this);
		arm_r29.setRotationPoint(-9.9818F, -109.9666F, -24.0F);
		limbs.addChild(arm_r29);
		setRotationAngle(arm_r29, -1.5093F, -0.8514F, 1.7184F);
		arm_r29.cubeList.add(new ModelBox(arm_r29, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r30 = new ModelRenderer(this);
		arm_r30.setRotationPoint(20.0182F, -96.9666F, -24.0F);
		limbs.addChild(arm_r30);
		setRotationAngle(arm_r30, -2.3048F, -0.5678F, 2.2579F);
		arm_r30.cubeList.add(new ModelBox(arm_r30, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r31 = new ModelRenderer(this);
		arm_r31.setRotationPoint(-20.9818F, -96.9666F, -24.0F);
		limbs.addChild(arm_r31);
		setRotationAngle(arm_r31, -0.8417F, -0.1261F, 2.3751F);
		arm_r31.cubeList.add(new ModelBox(arm_r31, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r32 = new ModelRenderer(this);
		arm_r32.setRotationPoint(19.0182F, -96.9666F, 26.0F);
		limbs.addChild(arm_r32);
		setRotationAngle(arm_r32, 1.0083F, -0.0344F, 0.2134F);
		arm_r32.cubeList.add(new ModelBox(arm_r32, 30, 0, -2.0F, -9.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r33 = new ModelRenderer(this);
		arm_r33.setRotationPoint(-11.9818F, -147.9666F, 26.0F);
		limbs.addChild(arm_r33);
		setRotationAngle(arm_r33, 1.0083F, -0.0344F, 0.2134F);
		arm_r33.cubeList.add(new ModelBox(arm_r33, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r34 = new ModelRenderer(this);
		arm_r34.setRotationPoint(-10.9818F, -87.9666F, 26.0F);
		limbs.addChild(arm_r34);
		setRotationAngle(arm_r34, 1.2803F, 0.3742F, -0.1315F);
		arm_r34.cubeList.add(new ModelBox(arm_r34, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r35 = new ModelRenderer(this);
		arm_r35.setRotationPoint(-10.9818F, -110.9666F, 27.0F);
		limbs.addChild(arm_r35);
		setRotationAngle(arm_r35, 1.9388F, 0.3784F, 0.1183F);
		arm_r35.cubeList.add(new ModelBox(arm_r35, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r36 = new ModelRenderer(this);
		arm_r36.setRotationPoint(2.0182F, -119.9666F, 23.0F);
		limbs.addChild(arm_r36);
		setRotationAngle(arm_r36, 1.9388F, 0.3784F, 0.1183F);
		arm_r36.cubeList.add(new ModelBox(arm_r36, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r37 = new ModelRenderer(this);
		arm_r37.setRotationPoint(27.0182F, -111.9666F, 14.0F);
		limbs.addChild(arm_r37);
		setRotationAngle(arm_r37, 2.1457F, 1.1523F, 0.2733F);
		arm_r37.cubeList.add(new ModelBox(arm_r37, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r38 = new ModelRenderer(this);
		arm_r38.setRotationPoint(27.0182F, -147.9666F, 14.0F);
		limbs.addChild(arm_r38);
		setRotationAngle(arm_r38, 2.1457F, 1.1523F, 0.2733F);
		arm_r38.cubeList.add(new ModelBox(arm_r38, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r39 = new ModelRenderer(this);
		arm_r39.setRotationPoint(21.0182F, -145.9666F, 23.0F);
		limbs.addChild(arm_r39);
		setRotationAngle(arm_r39, 2.0465F, 0.9397F, 0.7979F);
		arm_r39.cubeList.add(new ModelBox(arm_r39, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r40 = new ModelRenderer(this);
		arm_r40.setRotationPoint(-20.9818F, -86.9666F, 23.0F);
		limbs.addChild(arm_r40);
		setRotationAngle(arm_r40, 0.9912F, -0.6522F, 0.5603F);
		arm_r40.cubeList.add(new ModelBox(arm_r40, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r41 = new ModelRenderer(this);
		arm_r41.setRotationPoint(-22.9818F, -86.9666F, -4.0F);
		limbs.addChild(arm_r41);
		setRotationAngle(arm_r41, -0.606F, -0.877F, 1.5476F);
		arm_r41.cubeList.add(new ModelBox(arm_r41, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r42 = new ModelRenderer(this);
		arm_r42.setRotationPoint(-22.9818F, -108.9666F, -19.0F);
		limbs.addChild(arm_r42);
		setRotationAngle(arm_r42, -0.606F, -0.877F, 1.4167F);
		arm_r42.cubeList.add(new ModelBox(arm_r42, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r43 = new ModelRenderer(this);
		arm_r43.setRotationPoint(-22.9818F, -131.9666F, -19.0F);
		limbs.addChild(arm_r43);
		setRotationAngle(arm_r43, 0.2745F, -0.5242F, 0.8182F);
		arm_r43.cubeList.add(new ModelBox(arm_r43, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r44 = new ModelRenderer(this);
		arm_r44.setRotationPoint(-22.9818F, -143.9666F, 12.0F);
		limbs.addChild(arm_r44);
		setRotationAngle(arm_r44, 0.2745F, -0.5242F, 0.8182F);
		arm_r44.cubeList.add(new ModelBox(arm_r44, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r45 = new ModelRenderer(this);
		arm_r45.setRotationPoint(-22.9818F, -143.9666F, -5.0F);
		limbs.addChild(arm_r45);
		setRotationAngle(arm_r45, -0.3441F, -0.2907F, 2.0754F);
		arm_r45.cubeList.add(new ModelBox(arm_r45, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		arm_r46 = new ModelRenderer(this);
		arm_r46.setRotationPoint(-22.9818F, -144.9666F, -18.0F);
		limbs.addChild(arm_r46);
		setRotationAngle(arm_r46, 0.2921F, -0.0905F, 1.2084F);
		arm_r46.cubeList.add(new ModelBox(arm_r46, 30, 0, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie1 = new ModelRenderer(this);
		zombie1.setRotationPoint(18.0F, -144.0F, -24.75F);
		mainBody.addChild(zombie1);
		setRotationAngle(zombie1, 3.1416F, 0.0F, 1.3963F);
		zombie1.cubeList.add(new ModelBox(zombie1, 24, 0, -1.0F, -7.5F, -4.75F, 2, 4, 2, 0.25F, true));

		body_r4 = new ModelRenderer(this);
		body_r4.setRotationPoint(0.0F, 19.5F, 1.25F);
		zombie1.addChild(body_r4);
		setRotationAngle(body_r4, -1.8326F, 0.0F, -1.1781F);
		body_r4.cubeList.add(new ModelBox(body_r4, 0, 0, 22.0F, -4.0F, -16.0F, 8, 22, 6, 0.5F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -7.5F, 1.25F);
		zombie1.addChild(bone2);
		setRotationAngle(bone2, -2.9234F, 0.0F, 0.0F);
		

		leftArm_r1 = new ModelRenderer(this);
		leftArm_r1.setRotationPoint(0.0F, 18.0F, 0.0F);
		bone2.addChild(leftArm_r1);
		setRotationAngle(leftArm_r1, 0.9599F, 0.2618F, -0.3927F);
		leftArm_r1.cubeList.add(new ModelBox(leftArm_r1, 30, 0, -8.0F, -16.0F, 5.0F, 4, 12, 4, 0.0F, false));

		rightArm_r1 = new ModelRenderer(this);
		rightArm_r1.setRotationPoint(0.0F, 18.0F, 0.0F);
		bone2.addChild(rightArm_r1);
		setRotationAngle(rightArm_r1, -0.5672F, -2.7925F, -0.4363F);
		rightArm_r1.cubeList.add(new ModelBox(rightArm_r1, 30, 0, 4.0F, -24.0F, -11.0F, 4, 12, 4, 0.0F, true));

		zombie2 = new ModelRenderer(this);
		zombie2.setRotationPoint(-14.0F, -144.0F, -24.75F);
		mainBody.addChild(zombie2);
		setRotationAngle(zombie2, 3.1416F, 0.0F, -3.0107F);
		zombie2.cubeList.add(new ModelBox(zombie2, 0, 0, -4.0F, -14.5F, -1.75F, 8, 23, 6, 0.5F, true));
		zombie2.cubeList.add(new ModelBox(zombie2, 24, 0, -1.0F, -7.5F, -4.75F, 2, 4, 2, 0.25F, true));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -8.5F, 0.25F);
		zombie2.addChild(bone3);
		setRotationAngle(bone3, -2.8798F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 30, 0, 4.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bone3.cubeList.add(new ModelBox(bone3, 30, 0, -8.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie3 = new ModelRenderer(this);
		zombie3.setRotationPoint(-6.0F, -123.5F, 11.5F);
		mainBody.addChild(zombie3);
		setRotationAngle(zombie3, 1.5708F, 0.0F, 3.1416F);
		zombie3.cubeList.add(new ModelBox(zombie3, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));
		zombie3.cubeList.add(new ModelBox(zombie3, 30, 0, -13.0F, -24.0F, -2.0F, 4, 12, 4, 0.0F, true));
		zombie3.cubeList.add(new ModelBox(zombie3, 30, 0, -25.0F, -24.0F, -2.0F, 4, 12, 4, 0.0F, false));

		body_r5 = new ModelRenderer(this);
		body_r5.setRotationPoint(-17.0F, 0.0F, 0.0F);
		zombie3.addChild(body_r5);
		setRotationAngle(body_r5, 10.0F, 0.0F, -1.0036F);
		body_r5.cubeList.add(new ModelBox(body_r5, 0, 0, 5.0F, -29.0F, -3.0F, 8, 34, 6, 0.5F, true));

		zombie4 = new ModelRenderer(this);
		zombie4.setRotationPoint(-0.0F, -133.5F, -1.5F);
		mainBody.addChild(zombie4);
		setRotationAngle(zombie4, 23.9F, 0.0F, 3.1416F);
		zombie4.cubeList.add(new ModelBox(zombie4, 30, 0, -13.0F, -24.0F, -2.0F, 4, 12, 4, 0.0F, true));
		zombie4.cubeList.add(new ModelBox(zombie4, 30, 0, -25.0F, -24.0F, -2.0F, 4, 12, 4, 0.0F, false));

		head_r1 = new ModelRenderer(this);
		head_r1.setRotationPoint(-17.0F, 0.0F, 0.0F);
		zombie4.addChild(head_r1);
		setRotationAngle(head_r1, 0.0436F, -0.1309F, -0.1745F);
		head_r1.cubeList.add(new ModelBox(head_r1, 7, 11, -2.0F, -30.0F, -9.0F, 5, 10, 5, 0.25F, true));

		body_r6 = new ModelRenderer(this);
		body_r6.setRotationPoint(-17.0F, 0.0F, 0.0F);
		zombie4.addChild(body_r6);
		setRotationAngle(body_r6, 0.0F, 0.3927F, 0.0F);
		body_r6.cubeList.add(new ModelBox(body_r6, 0, 0, -4.0F, -32.0F, 9.0F, 8, 34, 6, 0.5F, true));

		zombie5 = new ModelRenderer(this);
		zombie5.setRotationPoint(2.0F, -120.1F, -26.15F);
		mainBody.addChild(zombie5);
		setRotationAngle(zombie5, -2.7925F, 0.0F, 3.1416F);
		zombie5.cubeList.add(new ModelBox(zombie5, 24, 0, -1.0F, -7.5F, -4.75F, 2, 4, 2, 0.25F, true));

		body_r7 = new ModelRenderer(this);
		body_r7.setRotationPoint(0.0F, 19.5F, 1.25F);
		zombie5.addChild(body_r7);
		setRotationAngle(body_r7, -0.2182F, 0.0F, 0.0F);
		body_r7.cubeList.add(new ModelBox(body_r7, 0, 0, -4.0F, -34.0F, -4.0F, 8, 34, 6, 0.5F, true));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -5.7619F, 1.7648F);
		zombie5.addChild(bone);
		setRotationAngle(bone, -2.9671F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 30, 0, 4.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bone.cubeList.add(new ModelBox(bone, 30, 0, -8.0F, -6.0F, -2.0F, 4, 12, 4, 0.0F, false));

		legs = new ModelRenderer(this);
		legs.setRotationPoint(0.0F, 53.5F, 0.0F);
		

		rightLeg1 = new ModelRenderer(this);
		rightLeg1.setRotationPoint(-13.0F, -85.0F, -15.0F);
		legs.addChild(rightLeg1);
		rightLeg1.cubeList.add(new ModelBox(rightLeg1, 0, 228, -13.0F, 33.0F, -8.0F, 27, 3, 17, 0.0F, false));

		top = new ModelRenderer(this);
		top.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg1.addChild(top);
		

		zombie7 = new ModelRenderer(this);
		zombie7.setRotationPoint(-12.0F, 0.0F, -3.0F);
		top.addChild(zombie7);
		setRotationAngle(zombie7, 0.0F, 0.0F, -3.1416F);
		zombie7.cubeList.add(new ModelBox(zombie7, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie7.cubeList.add(new ModelBox(zombie7, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r47 = new ModelRenderer(this);
		arm_r47.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie7.addChild(arm_r47);
		setRotationAngle(arm_r47, 3.1416F, 0.0F, -0.1309F);
		arm_r47.cubeList.add(new ModelBox(arm_r47, 30, 0, -1.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie10 = new ModelRenderer(this);
		zombie10.setRotationPoint(13.0F, 0.0F, 4.0F);
		top.addChild(zombie10);
		setRotationAngle(zombie10, 3.1416F, 0.0F, 0.0F);
		zombie10.cubeList.add(new ModelBox(zombie10, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie10.cubeList.add(new ModelBox(zombie10, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r48 = new ModelRenderer(this);
		arm_r48.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie10.addChild(arm_r48);
		setRotationAngle(arm_r48, -2.9671F, 0.0F, -0.2182F);
		arm_r48.cubeList.add(new ModelBox(arm_r48, 30, 0, -1.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie6 = new ModelRenderer(this);
		zombie6.setRotationPoint(-21.0F, 0.0F, -3.0F);
		top.addChild(zombie6);
		setRotationAngle(zombie6, 0.0F, 0.0F, -3.1416F);
		zombie6.cubeList.add(new ModelBox(zombie6, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie6.cubeList.add(new ModelBox(zombie6, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r49 = new ModelRenderer(this);
		arm_r49.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie6.addChild(arm_r49);
		setRotationAngle(arm_r49, -3.0543F, 0.0F, 0.1745F);
		arm_r49.cubeList.add(new ModelBox(arm_r49, 30, 0, 9.0F, 0.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie11 = new ModelRenderer(this);
		zombie11.setRotationPoint(22.0F, 0.0F, 4.0F);
		top.addChild(zombie11);
		setRotationAngle(zombie11, 3.1416F, 0.0F, 0.0F);
		zombie11.cubeList.add(new ModelBox(zombie11, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie11.cubeList.add(new ModelBox(zombie11, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r50 = new ModelRenderer(this);
		arm_r50.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie11.addChild(arm_r50);
		setRotationAngle(arm_r50, 3.1416F, 0.0F, 0.0F);
		arm_r50.cubeList.add(new ModelBox(arm_r50, 30, 0, 10.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		bot = new ModelRenderer(this);
		bot.setRotationPoint(0.0F, 35.0F, 0.0F);
		rightLeg1.addChild(bot);
		

		zombie8 = new ModelRenderer(this);
		zombie8.setRotationPoint(-6.0F, 14.0F, -4.6667F);
		bot.addChild(zombie8);
		zombie8.cubeList.add(new ModelBox(zombie8, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie8.cubeList.add(new ModelBox(zombie8, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r51 = new ModelRenderer(this);
		arm_r51.setRotationPoint(-4.0F, -2.5F, 1.6667F);
		zombie8.addChild(arm_r51);
		setRotationAngle(arm_r51, -2.8798F, 0.0F, -0.3054F);
		arm_r51.cubeList.add(new ModelBox(arm_r51, 30, 0, -1.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie13 = new ModelRenderer(this);
		zombie13.setRotationPoint(7.0F, 14.0F, 5.3333F);
		bot.addChild(zombie13);
		setRotationAngle(zombie13, -3.1416F, 0.0F, 3.1416F);
		zombie13.cubeList.add(new ModelBox(zombie13, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie13.cubeList.add(new ModelBox(zombie13, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r52 = new ModelRenderer(this);
		arm_r52.setRotationPoint(-4.0F, -2.5F, 1.3333F);
		zombie13.addChild(arm_r52);
		setRotationAngle(arm_r52, 3.1416F, 0.0F, 0.0F);
		arm_r52.cubeList.add(new ModelBox(arm_r52, 30, 0, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie9 = new ModelRenderer(this);
		zombie9.setRotationPoint(3.0F, 14.0F, -4.6667F);
		bot.addChild(zombie9);
		zombie9.cubeList.add(new ModelBox(zombie9, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie9.cubeList.add(new ModelBox(zombie9, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r53 = new ModelRenderer(this);
		arm_r53.setRotationPoint(8.0F, -2.5F, 1.6667F);
		zombie9.addChild(arm_r53);
		setRotationAngle(arm_r53, 3.1416F, 0.0F, 0.48F);
		arm_r53.cubeList.add(new ModelBox(arm_r53, 30, 0, -5.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie12 = new ModelRenderer(this);
		zombie12.setRotationPoint(-2.0F, 14.0F, 5.3333F);
		bot.addChild(zombie12);
		setRotationAngle(zombie12, -3.1416F, 0.0F, 3.1416F);
		zombie12.cubeList.add(new ModelBox(zombie12, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie12.cubeList.add(new ModelBox(zombie12, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r54 = new ModelRenderer(this);
		arm_r54.setRotationPoint(8.0F, -2.5F, 1.3333F);
		zombie12.addChild(arm_r54);
		setRotationAngle(arm_r54, 3.1416F, 0.0F, 0.2618F);
		arm_r54.cubeList.add(new ModelBox(arm_r54, 30, 0, -3.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftLeg1 = new ModelRenderer(this);
		leftLeg1.setRotationPoint(14.0F, -85.0F, -15.0F);
		legs.addChild(leftLeg1);
		leftLeg1.cubeList.add(new ModelBox(leftLeg1, 0, 228, -13.0F, 33.0F, -8.0F, 27, 3, 17, 0.0F, false));

		top3 = new ModelRenderer(this);
		top3.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg1.addChild(top3);
		

		zombie22 = new ModelRenderer(this);
		zombie22.setRotationPoint(-12.0F, 0.0F, -3.0F);
		top3.addChild(zombie22);
		setRotationAngle(zombie22, 0.0F, 0.0F, -3.1416F);
		zombie22.cubeList.add(new ModelBox(zombie22, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie22.cubeList.add(new ModelBox(zombie22, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r55 = new ModelRenderer(this);
		arm_r55.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie22.addChild(arm_r55);
		setRotationAngle(arm_r55, 3.1416F, 0.0F, -0.4363F);
		arm_r55.cubeList.add(new ModelBox(arm_r55, 30, 0, 0.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie23 = new ModelRenderer(this);
		zombie23.setRotationPoint(13.0F, 0.0F, 4.0F);
		top3.addChild(zombie23);
		setRotationAngle(zombie23, 3.1416F, 0.0F, 0.0F);
		zombie23.cubeList.add(new ModelBox(zombie23, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie23.cubeList.add(new ModelBox(zombie23, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r56 = new ModelRenderer(this);
		arm_r56.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie23.addChild(arm_r56);
		setRotationAngle(arm_r56, 3.1416F, 0.0F, 0.0F);
		arm_r56.cubeList.add(new ModelBox(arm_r56, 30, 0, -2.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie24 = new ModelRenderer(this);
		zombie24.setRotationPoint(-21.0F, 0.0F, -3.0F);
		top3.addChild(zombie24);
		setRotationAngle(zombie24, 0.0F, 0.0F, -3.1416F);
		zombie24.cubeList.add(new ModelBox(zombie24, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie24.cubeList.add(new ModelBox(zombie24, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r57 = new ModelRenderer(this);
		arm_r57.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie24.addChild(arm_r57);
		setRotationAngle(arm_r57, 3.1416F, 0.0F, 0.1309F);
		arm_r57.cubeList.add(new ModelBox(arm_r57, 30, 0, 9.0F, 0.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie25 = new ModelRenderer(this);
		zombie25.setRotationPoint(22.0F, 0.0F, 4.0F);
		top3.addChild(zombie25);
		setRotationAngle(zombie25, 3.1416F, 0.0F, 0.0F);
		zombie25.cubeList.add(new ModelBox(zombie25, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie25.cubeList.add(new ModelBox(zombie25, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r58 = new ModelRenderer(this);
		arm_r58.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie25.addChild(arm_r58);
		setRotationAngle(arm_r58, -2.9671F, 0.3054F, 0.3054F);
		arm_r58.cubeList.add(new ModelBox(arm_r58, 30, 0, 8.0F, 1.5F, -5.0F, 4, 12, 4, 0.0F, false));

		bot3 = new ModelRenderer(this);
		bot3.setRotationPoint(0.0F, 35.0F, 0.0F);
		leftLeg1.addChild(bot3);
		

		zombie26 = new ModelRenderer(this);
		zombie26.setRotationPoint(-6.0F, 14.0F, -4.6667F);
		bot3.addChild(zombie26);
		zombie26.cubeList.add(new ModelBox(zombie26, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie26.cubeList.add(new ModelBox(zombie26, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r59 = new ModelRenderer(this);
		arm_r59.setRotationPoint(-4.0F, -2.5F, 1.6667F);
		zombie26.addChild(arm_r59);
		setRotationAngle(arm_r59, 3.1416F, 0.2182F, -0.1309F);
		arm_r59.cubeList.add(new ModelBox(arm_r59, 30, 0, 0.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie27 = new ModelRenderer(this);
		zombie27.setRotationPoint(7.0F, 14.0F, 5.3333F);
		bot3.addChild(zombie27);
		setRotationAngle(zombie27, -3.1416F, 0.0F, 3.1416F);
		zombie27.cubeList.add(new ModelBox(zombie27, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie27.cubeList.add(new ModelBox(zombie27, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r60 = new ModelRenderer(this);
		arm_r60.setRotationPoint(-4.0F, -2.5F, 1.3333F);
		zombie27.addChild(arm_r60);
		setRotationAngle(arm_r60, 3.1416F, 0.0F, -0.3054F);
		arm_r60.cubeList.add(new ModelBox(arm_r60, 30, 0, -1.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie28 = new ModelRenderer(this);
		zombie28.setRotationPoint(3.0F, 14.0F, -4.6667F);
		bot3.addChild(zombie28);
		zombie28.cubeList.add(new ModelBox(zombie28, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie28.cubeList.add(new ModelBox(zombie28, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r61 = new ModelRenderer(this);
		arm_r61.setRotationPoint(8.0F, -2.5F, 1.6667F);
		zombie28.addChild(arm_r61);
		setRotationAngle(arm_r61, -2.8362F, 0.0F, 0.1745F);
		arm_r61.cubeList.add(new ModelBox(arm_r61, 30, 0, -3.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie29 = new ModelRenderer(this);
		zombie29.setRotationPoint(-2.0F, 14.0F, 5.3333F);
		bot3.addChild(zombie29);
		setRotationAngle(zombie29, -3.1416F, 0.0F, 3.1416F);
		zombie29.cubeList.add(new ModelBox(zombie29, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie29.cubeList.add(new ModelBox(zombie29, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r62 = new ModelRenderer(this);
		arm_r62.setRotationPoint(8.0F, -2.5F, 1.3333F);
		zombie29.addChild(arm_r62);
		setRotationAngle(arm_r62, 3.1416F, 0.0F, 0.0F);
		arm_r62.cubeList.add(new ModelBox(arm_r62, 30, 0, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightLeg2 = new ModelRenderer(this);
		rightLeg2.setRotationPoint(-13.0F, -85.0F, 14.0F);
		legs.addChild(rightLeg2);
		rightLeg2.cubeList.add(new ModelBox(rightLeg2, 0, 228, -13.0F, 33.0F, -9.0F, 27, 3, 17, 0.0F, false));

		top2 = new ModelRenderer(this);
		top2.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg2.addChild(top2);
		

		zombie14 = new ModelRenderer(this);
		zombie14.setRotationPoint(-12.0F, 0.0F, -3.0F);
		top2.addChild(zombie14);
		setRotationAngle(zombie14, 0.0F, 0.0F, -3.1416F);
		zombie14.cubeList.add(new ModelBox(zombie14, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie14.cubeList.add(new ModelBox(zombie14, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r63 = new ModelRenderer(this);
		arm_r63.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie14.addChild(arm_r63);
		setRotationAngle(arm_r63, 3.1416F, 0.0F, 0.0F);
		arm_r63.cubeList.add(new ModelBox(arm_r63, 30, 0, -2.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie15 = new ModelRenderer(this);
		zombie15.setRotationPoint(13.0F, 0.0F, 4.0F);
		top2.addChild(zombie15);
		setRotationAngle(zombie15, 3.1416F, 0.0F, 0.0F);
		zombie15.cubeList.add(new ModelBox(zombie15, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie15.cubeList.add(new ModelBox(zombie15, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r64 = new ModelRenderer(this);
		arm_r64.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie15.addChild(arm_r64);
		setRotationAngle(arm_r64, 3.1416F, 0.0F, -0.3927F);
		arm_r64.cubeList.add(new ModelBox(arm_r64, 30, 0, 0.0F, -3.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie16 = new ModelRenderer(this);
		zombie16.setRotationPoint(-21.0F, 0.0F, -3.0F);
		top2.addChild(zombie16);
		setRotationAngle(zombie16, 0.0F, 0.0F, -3.1416F);
		zombie16.cubeList.add(new ModelBox(zombie16, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie16.cubeList.add(new ModelBox(zombie16, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r65 = new ModelRenderer(this);
		arm_r65.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie16.addChild(arm_r65);
		setRotationAngle(arm_r65, -2.9234F, 0.1309F, 0.2618F);
		arm_r65.cubeList.add(new ModelBox(arm_r65, 30, 0, 7.0F, 0.5F, -4.0F, 4, 12, 4, 0.0F, false));

		zombie17 = new ModelRenderer(this);
		zombie17.setRotationPoint(22.0F, 0.0F, 4.0F);
		top2.addChild(zombie17);
		setRotationAngle(zombie17, 3.1416F, 0.0F, 0.0F);
		zombie17.cubeList.add(new ModelBox(zombie17, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie17.cubeList.add(new ModelBox(zombie17, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r66 = new ModelRenderer(this);
		arm_r66.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie17.addChild(arm_r66);
		setRotationAngle(arm_r66, 3.1416F, 0.0F, 0.2618F);
		arm_r66.cubeList.add(new ModelBox(arm_r66, 30, 0, 8.0F, 0.5F, -2.0F, 4, 12, 4, 0.0F, false));

		bot2 = new ModelRenderer(this);
		bot2.setRotationPoint(0.0F, 35.0F, 0.0F);
		rightLeg2.addChild(bot2);
		

		zombie18 = new ModelRenderer(this);
		zombie18.setRotationPoint(-6.0F, 14.0F, -4.6667F);
		bot2.addChild(zombie18);
		zombie18.cubeList.add(new ModelBox(zombie18, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie18.cubeList.add(new ModelBox(zombie18, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r67 = new ModelRenderer(this);
		arm_r67.setRotationPoint(-4.0F, -2.5F, 1.6667F);
		zombie18.addChild(arm_r67);
		setRotationAngle(arm_r67, 3.1416F, 0.0F, -0.3927F);
		arm_r67.cubeList.add(new ModelBox(arm_r67, 30, 0, 1.0F, -1.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie19 = new ModelRenderer(this);
		zombie19.setRotationPoint(7.0F, 14.0F, 5.3333F);
		bot2.addChild(zombie19);
		setRotationAngle(zombie19, -3.1416F, 0.0F, 3.1416F);
		zombie19.cubeList.add(new ModelBox(zombie19, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie19.cubeList.add(new ModelBox(zombie19, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r68 = new ModelRenderer(this);
		arm_r68.setRotationPoint(-4.0F, -2.5F, 1.3333F);
		zombie19.addChild(arm_r68);
		setRotationAngle(arm_r68, 3.1416F, 0.0F, -0.1745F);
		arm_r68.cubeList.add(new ModelBox(arm_r68, 30, 0, 0.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie20 = new ModelRenderer(this);
		zombie20.setRotationPoint(3.0F, 14.0F, -4.6667F);
		bot2.addChild(zombie20);
		zombie20.cubeList.add(new ModelBox(zombie20, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie20.cubeList.add(new ModelBox(zombie20, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r69 = new ModelRenderer(this);
		arm_r69.setRotationPoint(8.0F, -2.5F, 1.6667F);
		zombie20.addChild(arm_r69);
		setRotationAngle(arm_r69, 3.1416F, 0.0F, 0.0F);
		arm_r69.cubeList.add(new ModelBox(arm_r69, 30, 0, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie21 = new ModelRenderer(this);
		zombie21.setRotationPoint(-2.0F, 14.0F, 5.3333F);
		bot2.addChild(zombie21);
		setRotationAngle(zombie21, -3.1416F, 0.0F, 3.1416F);
		zombie21.cubeList.add(new ModelBox(zombie21, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie21.cubeList.add(new ModelBox(zombie21, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r70 = new ModelRenderer(this);
		arm_r70.setRotationPoint(8.0F, -2.5F, 1.3333F);
		zombie21.addChild(arm_r70);
		setRotationAngle(arm_r70, 3.1416F, 0.0F, 0.2182F);
		arm_r70.cubeList.add(new ModelBox(arm_r70, 30, 0, -4.0F, -1.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftLeg2 = new ModelRenderer(this);
		leftLeg2.setRotationPoint(14.0F, -85.0F, 13.0F);
		legs.addChild(leftLeg2);
		leftLeg2.cubeList.add(new ModelBox(leftLeg2, 0, 228, -13.0F, 33.0F, -8.0F, 27, 3, 17, 0.0F, false));

		top4 = new ModelRenderer(this);
		top4.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg2.addChild(top4);
		

		zombie30 = new ModelRenderer(this);
		zombie30.setRotationPoint(-12.0F, 0.0F, -3.0F);
		top4.addChild(zombie30);
		setRotationAngle(zombie30, 0.0F, 0.0F, -3.1416F);
		zombie30.cubeList.add(new ModelBox(zombie30, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie30.cubeList.add(new ModelBox(zombie30, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r71 = new ModelRenderer(this);
		arm_r71.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie30.addChild(arm_r71);
		setRotationAngle(arm_r71, 3.1416F, 0.0F, -0.3054F);
		arm_r71.cubeList.add(new ModelBox(arm_r71, 30, 0, 0.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie31 = new ModelRenderer(this);
		zombie31.setRotationPoint(13.0F, 0.0F, 4.0F);
		top4.addChild(zombie31);
		setRotationAngle(zombie31, 3.1416F, 0.0F, 0.0F);
		zombie31.cubeList.add(new ModelBox(zombie31, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie31.cubeList.add(new ModelBox(zombie31, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r72 = new ModelRenderer(this);
		arm_r72.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie31.addChild(arm_r72);
		setRotationAngle(arm_r72, 3.1416F, 0.0F, -0.1745F);
		arm_r72.cubeList.add(new ModelBox(arm_r72, 30, 0, 0.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie32 = new ModelRenderer(this);
		zombie32.setRotationPoint(-21.0F, 0.0F, -3.0F);
		top4.addChild(zombie32);
		setRotationAngle(zombie32, 0.0F, 0.0F, -3.1416F);
		zombie32.cubeList.add(new ModelBox(zombie32, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie32.cubeList.add(new ModelBox(zombie32, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r73 = new ModelRenderer(this);
		arm_r73.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie32.addChild(arm_r73);
		setRotationAngle(arm_r73, 3.1416F, 0.0F, 0.0F);
		arm_r73.cubeList.add(new ModelBox(arm_r73, 30, 0, 10.0F, -1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie33 = new ModelRenderer(this);
		zombie33.setRotationPoint(22.0F, 0.0F, 4.0F);
		top4.addChild(zombie33);
		setRotationAngle(zombie33, 3.1416F, 0.0F, 0.0F);
		zombie33.cubeList.add(new ModelBox(zombie33, 0, 0, -21.0F, -34.0F, -3.0F, 8, 23, 6, 0.5F, true));
		zombie33.cubeList.add(new ModelBox(zombie33, 24, 0, -18.0F, -27.0F, -6.0F, 2, 4, 2, 0.25F, true));

		arm_r74 = new ModelRenderer(this);
		arm_r74.setRotationPoint(-23.0F, -24.0F, 0.0F);
		zombie33.addChild(arm_r74);
		setRotationAngle(arm_r74, 3.1416F, 0.0F, 0.1745F);
		arm_r74.cubeList.add(new ModelBox(arm_r74, 30, 0, 8.0F, 1.5F, -2.0F, 4, 12, 4, 0.0F, false));

		bot4 = new ModelRenderer(this);
		bot4.setRotationPoint(0.0F, 35.0F, 0.0F);
		leftLeg2.addChild(bot4);
		

		zombie34 = new ModelRenderer(this);
		zombie34.setRotationPoint(-6.0F, 14.0F, -4.6667F);
		bot4.addChild(zombie34);
		zombie34.cubeList.add(new ModelBox(zombie34, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie34.cubeList.add(new ModelBox(zombie34, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r75 = new ModelRenderer(this);
		arm_r75.setRotationPoint(-4.0F, -2.5F, 1.6667F);
		zombie34.addChild(arm_r75);
		setRotationAngle(arm_r75, 3.1416F, 0.0F, 0.0F);
		arm_r75.cubeList.add(new ModelBox(arm_r75, 30, 0, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie35 = new ModelRenderer(this);
		zombie35.setRotationPoint(7.0F, 14.0F, 5.3333F);
		bot4.addChild(zombie35);
		setRotationAngle(zombie35, -3.1416F, 0.0F, 3.1416F);
		zombie35.cubeList.add(new ModelBox(zombie35, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie35.cubeList.add(new ModelBox(zombie35, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r76 = new ModelRenderer(this);
		arm_r76.setRotationPoint(-4.0F, -2.5F, 1.3333F);
		zombie35.addChild(arm_r76);
		setRotationAngle(arm_r76, 3.1416F, 0.0F, -0.2618F);
		arm_r76.cubeList.add(new ModelBox(arm_r76, 30, 0, 0.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie36 = new ModelRenderer(this);
		zombie36.setRotationPoint(3.0F, 14.0F, -4.6667F);
		bot4.addChild(zombie36);
		zombie36.cubeList.add(new ModelBox(zombie36, 0, 0, -2.0F, -14.0F, -1.3333F, 8, 34, 6, 0.5F, true));
		zombie36.cubeList.add(new ModelBox(zombie36, 24, 0, 1.0F, -7.0F, -4.3333F, 2, 4, 2, 0.25F, true));

		arm_r77 = new ModelRenderer(this);
		arm_r77.setRotationPoint(8.0F, -2.5F, 1.6667F);
		zombie36.addChild(arm_r77);
		setRotationAngle(arm_r77, 3.1416F, 0.0F, 0.2618F);
		arm_r77.cubeList.add(new ModelBox(arm_r77, 30, 0, -3.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		zombie37 = new ModelRenderer(this);
		zombie37.setRotationPoint(-2.0F, 14.0F, 5.3333F);
		bot4.addChild(zombie37);
		setRotationAngle(zombie37, -3.1416F, 0.0F, 3.1416F);
		zombie37.cubeList.add(new ModelBox(zombie37, 0, 0, -2.0F, -14.0F, -1.6667F, 8, 34, 6, 0.5F, true));
		zombie37.cubeList.add(new ModelBox(zombie37, 24, 0, 1.0F, -7.0F, -4.6667F, 2, 4, 2, 0.25F, true));

		arm_r78 = new ModelRenderer(this);
		arm_r78.setRotationPoint(8.0F, -2.5F, 1.3333F);
		zombie37.addChild(arm_r78);
		setRotationAngle(arm_r78, 3.1416F, 0.0F, 0.3054F);
		arm_r78.cubeList.add(new ModelBox(arm_r78, 30, 0, -5.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		mainBody.render(f5);
		legs.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
		
//		this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 66F62F) * limbSwingAmount;
//      this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 66F62F + (float)Math.PI) * limbSwingAmount;
        
		
		// 0.6662F
		
		
		// B
		this.rightLeg1.rotateAngleX = MathHelper.cos(limbSwing * 0.56F) * limbSwingAmount/2.0F - 0.25F;
		this.rightLeg1.rotateAngleY = 0.25F * limbSwingAmount + 0.25F;
        this.rightLeg1.rotateAngleZ = 0.25F * limbSwingAmount + 0.5F;
        
        this.bot.rotateAngleX = -MathHelper.cos(limbSwing * 0.76F) * limbSwingAmount/4.0F + 6.6F;
        this.bot.rotateAngleY = MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount/4.0F - 12.0F;
        this.bot.rotateAngleZ = MathHelper.cos(limbSwing * 0.7F) * limbSwingAmount/4.0F;

        // AA
        this.rightLeg2.rotateAngleX = -MathHelper.cos(limbSwing * 0.56F) * limbSwingAmount/2.0F - 10.0F;
		this.rightLeg2.rotateAngleY = 0.25F * limbSwingAmount + 20.0F;
        this.rightLeg2.rotateAngleZ = 0.25F * limbSwingAmount + 9.5F; //
        
        this.bot2.rotateAngleX = MathHelper.cos(limbSwing * 0.76F) * limbSwingAmount/4.0F - 5.5F;
        this.bot2.rotateAngleY = MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount/4.0F;
        this.bot2.rotateAngleZ = MathHelper.cos(limbSwing * 0.7F) * limbSwingAmount/4.0F + 6.0F;

        // B
        this.leftLeg1.rotateAngleX = -MathHelper.cos(limbSwing * 0.56F + (float)Math.PI) * limbSwingAmount/2.0F - 0.25F;
		this.leftLeg1.rotateAngleY = -0.25F * limbSwingAmount + 0.25F;
        this.leftLeg1.rotateAngleZ = -0.25F * limbSwingAmount - 0.5F;
        
        this.bot3.rotateAngleX = MathHelper.cos(limbSwing * 0.76F) * limbSwingAmount/4.0F + 6.6F;
        this.bot3.rotateAngleY = MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount/4.0F + 12.0F;
        this.bot3.rotateAngleZ = MathHelper.cos(limbSwing * 0.7F) * limbSwingAmount/4.0F;
        
        // AA
        this.leftLeg2.rotateAngleX = MathHelper.cos(limbSwing * 0.56F + (float)Math.PI) * limbSwingAmount/2.0F - 10.0F;
		this.leftLeg2.rotateAngleY = -0.25F * limbSwingAmount - 20.0F;
        this.leftLeg2.rotateAngleZ = -0.25F * limbSwingAmount - 9.5F; //
        
        this.bot4.rotateAngleX = MathHelper.cos(limbSwing * 0.76F) * limbSwingAmount/4.0F - 5.5F;
        this.bot4.rotateAngleY = MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount/4.0F;
        this.bot4.rotateAngleZ = MathHelper.cos(limbSwing * 0.7F) * limbSwingAmount/4.0F - 6.0F;

//        this.body_r1.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;
//        this.body_r2.rotateAngleZ = 0.05F * limbSwingAmount - 0.05F;
//        this.body_r3.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;
//        this.body_r4.rotateAngleZ = 0.05F * limbSwingAmount - 0.05F;
//        this.body_r5.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;
//        this.body_r6.rotateAngleZ = 0.05F * limbSwingAmount - 0.05F;
//        this.body_r7.rotateAngleZ = -0.05F * limbSwingAmount + 0.05F;

        float armSwing = MathHelper.cos((ageInTicks % 314) * 0.66F);
        this.zombie5.rotateAngleY = -0.05F * armSwing;// + 0.05F;
        this.zombie3.rotateAngleY = -0.05F * armSwing + 0.05F;
        this.arm_r1.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r1.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r2.rotateAngleY = 0.15F * armSwing;// - 0.1F;
        this.arm_r3.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r4.rotateAngleY = 0.12F * armSwing;// - 0.1F;
        this.arm_r5.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r6.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r7.rotateAngleY = 0.08F * armSwing;// - 0.1F;
        this.arm_r8.rotateAngleY = -0.12F * armSwing;// + 0.1F;
        this.arm_r9.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r10.rotateAngleY = 0.02F * armSwing;// - 0.1F;
        this.arm_r11.rotateAngleY = -0.16F * armSwing;// + 0.1F;
        this.arm_r12.rotateAngleY = -0.14F * armSwing;// + 0.1F;
        this.arm_r13.rotateAngleY = 0.1F * armSwing;// - 0.1F;
        this.arm_r14.rotateAngleY = -0.16F * armSwing;// + 0.1F;
        this.arm_r15.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r16.rotateAngleY = 0.13F * armSwing;// - 0.1F;
        this.arm_r17.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r18.rotateAngleY = -0.18F * armSwing;// + 0.1F;
        this.arm_r19.rotateAngleY = 0.14F * armSwing;// - 0.1F;
        this.arm_r20.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r21.rotateAngleY = -0.05F * armSwing;// + 0.1F;
        this.arm_r22.rotateAngleY = 0.1F * armSwing;// - 0.1F;
        this.arm_r23.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r24.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r25.rotateAngleY = 0.11F * armSwing;// - 0.1F;
        this.arm_r26.rotateAngleY = -0.12F * armSwing;// + 0.1F;
        this.arm_r27.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r28.rotateAngleY = 0.05F * armSwing;// - 0.1F;
        this.arm_r29.rotateAngleY = -0.3F * armSwing;// + 0.1F;
        this.arm_r30.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r31.rotateAngleY = 0.03F * armSwing;// - 0.1F;
        this.arm_r32.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r33.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r34.rotateAngleY = 0.1F * armSwing;// - 0.1F;
        this.arm_r35.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r36.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r37.rotateAngleY = 0.09F * armSwing;// - 0.1F;
        this.arm_r38.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r39.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r40.rotateAngleY = 0.1F * armSwing;// - 0.1F;
        this.arm_r41.rotateAngleY = -0.08F * armSwing;// + 0.1F;
        this.arm_r42.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r43.rotateAngleY = 0.05F * armSwing;// - 0.1F;
        this.arm_r44.rotateAngleY = -0.1F * armSwing;// + 0.1F;
        this.arm_r45.rotateAngleY = -0.12F * armSwing;// + 0.1F;
        this.arm_r46.rotateAngleY = 0.03F * armSwing;//;// - 0.1F;
//        this.arm_r47.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r48.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r49.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r50.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r51.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r52.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r53.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r54.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r55.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r56.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r57.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r58.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r59.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r60.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r61.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r62.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r63.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r64.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r65.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r66.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r67.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r68.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r69.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r70.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r71.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r72.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r73.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r74.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r75.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r76.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r77.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
//        this.arm_r78.rotateAngleY = -0.05F * limbSwingAmount + 0.05F;
        
//        this.right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
//        this.left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
//        this.right_leg.rotateAngleX = -limbSwingAmount;
//        this.left_leg.rotateAngleX = limbSwingAmount;

        this.mainBody.rotateAngleX = armSwing * (8+headPitch) * 0.0005F;
        this.mainBody.rotateAngleY = armSwing * (8+netHeadYaw) * 0.001F;
        this.mainBody.rotateAngleZ = -armSwing * (8+headPitch) * 0.0005F;
        
    }
}