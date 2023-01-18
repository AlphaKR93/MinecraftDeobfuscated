/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity;

import net.minecraft.sounds.SoundSource;

public interface Shearable {
    public void shear(SoundSource var1);

    public boolean readyForShearing();
}