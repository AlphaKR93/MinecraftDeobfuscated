/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.animation.definitions;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class CamelAnimation {
    public static final AnimationDefinition CAMEL_WALK = AnimationDefinition.Builder.withLength(1.5f).looping().addAnimation("root", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 2.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -2.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 2.5f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(-2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.125f, KeyframeAnimations.degreeVec(-2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.4583f, KeyframeAnimations.posVec(0.0f, 4.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.2083f, KeyframeAnimations.posVec(0.0f, 4.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-20.4f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.375f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-20.4f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -0.21f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.0833f, KeyframeAnimations.posVec(0.0f, 4.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.375f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, -0.21f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.625f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.posVec(0.0f, 4.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.625f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.125f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.125f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(15.94102f, -8.42106f, 20.94102f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.75f, KeyframeAnimations.degreeVec(15.94102f, 8.42106f, -20.94102f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.5f, KeyframeAnimations.degreeVec(15.94102f, -8.42106f, 20.94102f), AnimationChannel.Interpolations.CATMULLROM))).build();
    public static final AnimationDefinition CAMEL_SIT = AnimationDefinition.Builder.withLength(2.0f).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.3f, KeyframeAnimations.degreeVec(30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.degreeVec(24.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.3f, KeyframeAnimations.posVec(0.0f, 0.0f, 1.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.posVec(0.0f, -6.0f, 1.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, -19.9f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(-90.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -2.0f, 11.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, -2.0f, 11.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.posVec(0.0f, -8.4f, 11.4f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(-90.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -2.0f, 11.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, -2.0f, 11.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.posVec(0.0f, -8.4f, 11.4f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.degreeVec(-15.0f, -3.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.degreeVec(-65.0f, -9.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(-90.0f, -15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 1.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.posVec(1.0f, -0.62f, 0.25f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.posVec(0.5f, -11.25f, 2.5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.posVec(1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.degreeVec(-15.0f, 3.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.degreeVec(-65.0f, 9.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(-90.0f, 15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(0.0f, 0.0f, 1.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.posVec(-1.0f, -0.62f, 0.25f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.posVec(-0.5f, -11.25f, 2.5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.posVec(-1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7f, KeyframeAnimations.degreeVec(-27.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(-21.25f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.7f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.degreeVec(80.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(50.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition CAMEL_SIT_POSE = AnimationDefinition.Builder.withLength(1.0f).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -19.9f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -19.9f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-90.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-90.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, -15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-90.0f, -15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, 15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(-90.0f, 15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(-1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.posVec(-1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(50.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0f, KeyframeAnimations.degreeVec(50.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition CAMEL_STANDUP = AnimationDefinition.Builder.withLength(2.6f).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7f, KeyframeAnimations.degreeVec(-17.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.8f, KeyframeAnimations.degreeVec(-17.83f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.3f, KeyframeAnimations.degreeVec(-5.83f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -19.9f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7f, KeyframeAnimations.posVec(0.0f, -19.9f, -3.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.4f, KeyframeAnimations.posVec(0.0f, -12.76f, -4.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.8f, KeyframeAnimations.posVec(0.0f, -10.1f, -4.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.3f, KeyframeAnimations.posVec(0.0f, -2.9f, -2.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(-90.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.degreeVec(-49.06f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.degreeVec(-22.5f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.3f, KeyframeAnimations.degreeVec(-25.0f, 10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, -20.6f, 8.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.posVec(0.0f, -7.14f, 4.42f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.posVec(0.0f, -1.27f, -1.33f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.3f, KeyframeAnimations.posVec(0.0f, -1.27f, -0.33f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(-90.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.degreeVec(-49.06f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.degreeVec(-22.5f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.3f, KeyframeAnimations.degreeVec(-25.0f, -10.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, -20.6f, 12.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.posVec(0.0f, -20.6f, 8.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.posVec(0.0f, -7.14f, 4.42f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.8f, KeyframeAnimations.posVec(0.0f, -1.27f, -1.33f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.3f, KeyframeAnimations.posVec(0.0f, -1.27f, -0.33f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, -15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.6f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.degreeVec(-60.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.degreeVec(35.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.2f, KeyframeAnimations.degreeVec(30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3f, KeyframeAnimations.posVec(-2.0f, -20.5f, 3.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.6f, KeyframeAnimations.posVec(-2.0f, -20.5f, 3.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.posVec(-2.0f, -10.5f, 2.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(-2.0f, -0.4f, -3.9f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.posVec(-2.0f, -4.3f, -9.8f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.2f, KeyframeAnimations.posVec(-1.0f, -2.5f, -5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, 15.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.6f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.degreeVec(-60.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.degreeVec(35.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.2f, KeyframeAnimations.degreeVec(30.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0f, KeyframeAnimations.posVec(-1.0f, -20.5f, 5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3f, KeyframeAnimations.posVec(2.0f, -20.5f, 3.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.6f, KeyframeAnimations.posVec(2.0f, -20.5f, 3.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.1f, KeyframeAnimations.posVec(2.0f, -10.5f, 2.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.posVec(2.0f, -0.4f, -3.9f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.9f, KeyframeAnimations.posVec(2.0f, -4.3f, -9.8f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.2f, KeyframeAnimations.posVec(1.0f, -2.5f, -5.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.8f, KeyframeAnimations.degreeVec(55.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.0f, KeyframeAnimations.degreeVec(65.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.4f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(50.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4f, KeyframeAnimations.degreeVec(55.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.9f, KeyframeAnimations.degreeVec(55.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.5f, KeyframeAnimations.degreeVec(17.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.6f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition CAMEL_DASH = AnimationDefinition.Builder.withLength(0.5f).looping().addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(67.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(112.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(67.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(112.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(67.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(10.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(44.97272f, 1.76749f, -1.76833f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(44.97272f, 1.76749f, -1.76833f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(44.97272f, 1.76749f, -1.76833f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(44.97272f, -1.76749f, 1.76833f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(44.97272f, -1.76749f, 1.76833f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(-90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.125f, KeyframeAnimations.degreeVec(90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.25f, KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.375f, KeyframeAnimations.degreeVec(90.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(0.5f, KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, -67.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, -67.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).addAnimation("right_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 67.5f, 0.0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 67.5f, 0.0f), AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition CAMEL_IDLE = AnimationDefinition.Builder.withLength(4.0f).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(1.0f, KeyframeAnimations.degreeVec(4.98107f, 0.43523f, -4.98107f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(3.0f, KeyframeAnimations.degreeVec(4.9872f, -0.29424f, 3.36745f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(4.0f, KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.0f, KeyframeAnimations.degreeVec(-2.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(4.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("left_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(2.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -45.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.625f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.75f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -45.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.875f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(3.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -45.0f), AnimationChannel.Interpolations.CATMULLROM))).addAnimation("right_ear", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(2.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.625f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.75f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(2.875f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -22.5f), AnimationChannel.Interpolations.CATMULLROM), new Keyframe(3.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f), AnimationChannel.Interpolations.CATMULLROM))).build();
}