/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer
extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
    private static final ResourceLocation DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
    private static final ResourceLocation[] TEXTURE_LOCATION = (ResourceLocation[])Sheets.SHULKER_TEXTURE_LOCATION.stream().map($$0 -> new ResourceLocation("textures/" + $$0.texture().getPath() + ".png")).toArray(ResourceLocation[]::new);

    public ShulkerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ShulkerModel($$0.bakeLayer(ModelLayers.SHULKER)), 0.0f);
        this.addLayer(new ShulkerHeadLayer(this));
    }

    @Override
    public Vec3 getRenderOffset(Shulker $$0, float $$1) {
        return (Vec3)$$0.getRenderPosition($$1).orElse((Object)super.getRenderOffset($$0, $$1));
    }

    @Override
    public boolean shouldRender(Shulker $$0, Frustum $$1, double $$22, double $$3, double $$4) {
        if (super.shouldRender($$0, $$1, $$22, $$3, $$4)) {
            return true;
        }
        return $$0.getRenderPosition(0.0f).filter($$2 -> {
            EntityType<?> $$3 = $$0.getType();
            float $$4 = $$3.getHeight() / 2.0f;
            float $$5 = $$3.getWidth() / 2.0f;
            Vec3 $$6 = Vec3.atBottomCenterOf($$0.blockPosition());
            return $$1.isVisible(new AABB($$2.x, $$2.y + (double)$$4, $$2.z, $$6.x, $$6.y + (double)$$4, $$6.z).inflate($$5, $$4, $$5));
        }).isPresent();
    }

    @Override
    public ResourceLocation getTextureLocation(Shulker $$0) {
        return ShulkerRenderer.getTextureLocation($$0.getColor());
    }

    public static ResourceLocation getTextureLocation(@Nullable DyeColor $$0) {
        if ($$0 == null) {
            return DEFAULT_TEXTURE_LOCATION;
        }
        return TEXTURE_LOCATION[$$0.getId()];
    }

    @Override
    protected void setupRotations(Shulker $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3 + 180.0f, $$4);
        $$1.translate(0.0, 0.5, 0.0);
        $$1.mulPose($$0.getAttachFace().getOpposite().getRotation());
        $$1.translate(0.0, -0.5, 0.0);
    }
}