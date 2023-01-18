/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
    public Collection<ResourceLocation> getDependencies();

    public void resolveParents(Function<ResourceLocation, UnbakedModel> var1);

    @Nullable
    public BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4);
}