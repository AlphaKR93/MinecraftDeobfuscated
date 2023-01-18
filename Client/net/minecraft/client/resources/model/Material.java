/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.Objects
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class Material {
    public static final Comparator<Material> COMPARATOR = Comparator.comparing(Material::atlasLocation).thenComparing(Material::texture);
    private final ResourceLocation atlasLocation;
    private final ResourceLocation texture;
    @Nullable
    private RenderType renderType;

    public Material(ResourceLocation $$0, ResourceLocation $$1) {
        this.atlasLocation = $$0;
        this.texture = $$1;
    }

    public ResourceLocation atlasLocation() {
        return this.atlasLocation;
    }

    public ResourceLocation texture() {
        return this.texture;
    }

    public TextureAtlasSprite sprite() {
        return (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(this.atlasLocation()).apply((Object)this.texture());
    }

    public RenderType renderType(Function<ResourceLocation, RenderType> $$0) {
        if (this.renderType == null) {
            this.renderType = (RenderType)$$0.apply((Object)this.atlasLocation);
        }
        return this.renderType;
    }

    public VertexConsumer buffer(MultiBufferSource $$0, Function<ResourceLocation, RenderType> $$1) {
        return this.sprite().wrap($$0.getBuffer(this.renderType($$1)));
    }

    public VertexConsumer buffer(MultiBufferSource $$0, Function<ResourceLocation, RenderType> $$1, boolean $$2) {
        return this.sprite().wrap(ItemRenderer.getFoilBufferDirect($$0, this.renderType($$1), true, $$2));
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        Material $$1 = (Material)$$0;
        return this.atlasLocation.equals($$1.atlasLocation) && this.texture.equals($$1.texture);
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.atlasLocation, this.texture});
    }

    public String toString() {
        return "Material{atlasLocation=" + this.atlasLocation + ", texture=" + this.texture + "}";
    }
}