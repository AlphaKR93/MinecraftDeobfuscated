/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer
extends MobRenderer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
    private final ColorableHierarchicalModel<TropicalFish> modelA = (ColorableHierarchicalModel)this.getModel();
    private final ColorableHierarchicalModel<TropicalFish> modelB;
    private static final ResourceLocation MODEL_A_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a.png");
    private static final ResourceLocation MODEL_B_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b.png");

    public TropicalFishRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new TropicalFishModelA($$0.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.modelB = new TropicalFishModelB<TropicalFish>($$0.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
        this.addLayer(new TropicalFishPatternLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(TropicalFish $$0) {
        return switch ($$0.getVariant().base()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFish.Base.SMALL -> MODEL_A_TEXTURE;
            case TropicalFish.Base.LARGE -> MODEL_B_TEXTURE;
        };
    }

    @Override
    public void render(TropicalFish $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        ColorableHierarchicalModel<TropicalFish> $$6;
        this.model = $$6 = (switch ($$0.getVariant().base()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFish.Base.SMALL -> this.modelA;
            case TropicalFish.Base.LARGE -> this.modelB;
        });
        float[] $$7 = $$0.getBaseColor().getTextureDiffuseColors();
        $$6.setColor($$7[0], $$7[1], $$7[2]);
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
        $$6.setColor(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void setupRotations(TropicalFish $$0, PoseStack $$1, float $$2, float $$3, float $$4) {
        super.setupRotations($$0, $$1, $$2, $$3, $$4);
        float $$5 = 4.3f * Mth.sin(0.6f * $$2);
        $$1.mulPose(Axis.YP.rotationDegrees($$5));
        if (!$$0.isInWater()) {
            $$1.translate(0.2f, 0.1f, 0.0f);
            $$1.mulPose(Axis.ZP.rotationDegrees(90.0f));
        }
    }
}