/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.joml.Vector3f
 */
package net.minecraft.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import org.joml.Vector3f;

public record Keyframe(float timestamp, Vector3f target, AnimationChannel.Interpolation interpolation) {
}