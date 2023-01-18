/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  org.joml.Vector3f
 */
package net.minecraft.world.entity;

import java.util.Map;
import org.joml.Vector3f;

public interface LerpingModel {
    public Map<String, Vector3f> getModelRotationValues();
}