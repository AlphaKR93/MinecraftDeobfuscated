/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;

public class ParrotOnShoulderLayer<T extends Player>
extends RenderLayer<T, PlayerModel<T>> {
    private final ParrotModel model;

    public ParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new ParrotModel($$1.bakeLayer(ModelLayers.PARROT));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        this.render($$0, $$1, $$2, $$3, $$4, $$5, $$8, $$9, true);
        this.render($$0, $$1, $$2, $$3, $$4, $$5, $$8, $$9, false);
    }

    private void render(PoseStack $$02, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, boolean $$8) {
        CompoundTag $$9 = $$8 ? ((Player)$$3).getShoulderEntityLeft() : ((Player)$$3).getShoulderEntityRight();
        EntityType.byString($$9.getString("id")).filter($$0 -> $$0 == EntityType.PARROT).ifPresent($$10 -> {
            $$02.pushPose();
            $$02.translate($$8 ? 0.4f : -0.4f, $$3.isCrouching() ? -1.3f : -1.5f, 0.0f);
            Parrot.Variant $$11 = Parrot.Variant.byId($$9.getInt("Variant"));
            VertexConsumer $$12 = $$1.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture($$11)));
            this.model.renderOnShoulder($$02, $$12, $$2, OverlayTexture.NO_OVERLAY, $$4, $$5, $$6, $$7, $$2.tickCount);
            $$02.popPose();
        });
    }
}