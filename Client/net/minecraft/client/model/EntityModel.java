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
import net.minecraft.world.entity.Entity;

public abstract class EntityModel<T extends Entity>
extends Model {
    public float attackTime;
    public boolean riding;
    public boolean young = true;

    protected EntityModel() {
        this((Function<ResourceLocation, RenderType>)((Function)RenderType::entityCutoutNoCull));
    }

    protected EntityModel(Function<ResourceLocation, RenderType> $$0) {
        super($$0);
    }

    public abstract void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6);

    public void prepareMobModel(T $$0, float $$1, float $$2, float $$3) {
    }

    public void copyPropertiesTo(EntityModel<T> $$0) {
        $$0.attackTime = this.attackTime;
        $$0.riding = this.riding;
        $$0.young = this.young;
    }
}