/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Function
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class SkullModelBase
extends Model {
    public SkullModelBase() {
        super((Function<ResourceLocation, RenderType>)((Function)RenderType::entityTranslucent));
    }

    public abstract void setupAnim(float var1, float var2, float var3);
}