/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenEmissiveLayer<T extends Warden, M extends WardenModel<T>>
extends RenderLayer<T, M> {
    private final ResourceLocation texture;
    private final AlphaFunction<T> alphaFunction;
    private final DrawSelector<T, M> drawSelector;

    public WardenEmissiveLayer(RenderLayerParent<T, M> $$0, ResourceLocation $$1, AlphaFunction<T> $$2, DrawSelector<T, M> $$3) {
        super($$0);
        this.texture = $$1;
        this.alphaFunction = $$2;
        this.drawSelector = $$3;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (((Entity)$$3).isInvisible()) {
            return;
        }
        this.onlyDrawSelectedParts();
        VertexConsumer $$10 = $$1.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
        ((WardenModel)this.getParentModel()).renderToBuffer($$0, $$10, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), 1.0f, 1.0f, 1.0f, this.alphaFunction.apply($$3, $$6, $$7));
        this.resetDrawForAllParts();
    }

    private void onlyDrawSelectedParts() {
        List<ModelPart> $$02 = this.drawSelector.getPartsToDraw((WardenModel)this.getParentModel());
        ((WardenModel)this.getParentModel()).root().getAllParts().forEach($$0 -> {
            $$0.skipDraw = true;
        });
        $$02.forEach($$0 -> {
            $$0.skipDraw = false;
        });
    }

    private void resetDrawForAllParts() {
        ((WardenModel)this.getParentModel()).root().getAllParts().forEach($$0 -> {
            $$0.skipDraw = false;
        });
    }

    public static interface AlphaFunction<T extends Warden> {
        public float apply(T var1, float var2, float var3);
    }

    public static interface DrawSelector<T extends Warden, M extends EntityModel<T>> {
        public List<ModelPart> getPartsToDraw(M var1);
    }
}