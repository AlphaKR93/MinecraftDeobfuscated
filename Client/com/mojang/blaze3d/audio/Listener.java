/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.joml.Vector3f
 *  org.lwjgl.openal.AL10
 */
package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class Listener {
    private float gain = 1.0f;
    private Vec3 position = Vec3.ZERO;

    public void setListenerPosition(Vec3 $$0) {
        this.position = $$0;
        AL10.alListener3f((int)4100, (float)((float)$$0.x), (float)((float)$$0.y), (float)((float)$$0.z));
    }

    public Vec3 getListenerPosition() {
        return this.position;
    }

    public void setListenerOrientation(Vector3f $$0, Vector3f $$1) {
        AL10.alListenerfv((int)4111, (float[])new float[]{$$0.x(), $$0.y(), $$0.z(), $$1.x(), $$1.y(), $$1.z()});
    }

    public void setGain(float $$0) {
        AL10.alListenerf((int)4106, (float)$$0);
        this.gain = $$0;
    }

    public float getGain() {
        return this.gain;
    }

    public void reset() {
        this.setListenerPosition(Vec3.ZERO);
        this.setListenerOrientation(new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f));
    }
}