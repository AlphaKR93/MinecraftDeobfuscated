/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer
extends RenderLayer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
    private static final ResourceLocation KOB_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png");
    private static final ResourceLocation SUNSTREAK_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png");
    private static final ResourceLocation SNOOPER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png");
    private static final ResourceLocation DASHER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png");
    private static final ResourceLocation BRINELY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png");
    private static final ResourceLocation SPOTTY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png");
    private static final ResourceLocation FLOPPER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png");
    private static final ResourceLocation STRIPEY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png");
    private static final ResourceLocation GLITTER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png");
    private static final ResourceLocation BLOCKFISH_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png");
    private static final ResourceLocation BETTY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png");
    private static final ResourceLocation CLAYFISH_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png");
    private final TropicalFishModelA<TropicalFish> modelA;
    private final TropicalFishModelB<TropicalFish> modelB;

    public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, ColorableHierarchicalModel<TropicalFish>> $$0, EntityModelSet $$1) {
        super($$0);
        this.modelA = new TropicalFishModelA($$1.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.modelB = new TropicalFishModelB($$1.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, TropicalFish $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        TropicalFish.Pattern $$10 = $$3.getVariant();
        ColorableHierarchicalModel $$11 = switch ($$10.base()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFish.Base.SMALL -> this.modelA;
            case TropicalFish.Base.LARGE -> this.modelB;
        };
        ResourceLocation $$12 = switch ($$10) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFish.Pattern.KOB -> KOB_TEXTURE;
            case TropicalFish.Pattern.SUNSTREAK -> SUNSTREAK_TEXTURE;
            case TropicalFish.Pattern.SNOOPER -> SNOOPER_TEXTURE;
            case TropicalFish.Pattern.DASHER -> DASHER_TEXTURE;
            case TropicalFish.Pattern.BRINELY -> BRINELY_TEXTURE;
            case TropicalFish.Pattern.SPOTTY -> SPOTTY_TEXTURE;
            case TropicalFish.Pattern.FLOPPER -> FLOPPER_TEXTURE;
            case TropicalFish.Pattern.STRIPEY -> STRIPEY_TEXTURE;
            case TropicalFish.Pattern.GLITTER -> GLITTER_TEXTURE;
            case TropicalFish.Pattern.BLOCKFISH -> BLOCKFISH_TEXTURE;
            case TropicalFish.Pattern.BETTY -> BETTY_TEXTURE;
            case TropicalFish.Pattern.CLAYFISH -> CLAYFISH_TEXTURE;
        };
        float[] $$13 = $$3.getPatternColor().getTextureDiffuseColors();
        TropicalFishPatternLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), $$11, $$12, $$0, $$1, $$2, $$3, $$4, $$5, $$7, $$8, $$9, $$6, $$13[0], $$13[1], $$13[2]);
    }
}