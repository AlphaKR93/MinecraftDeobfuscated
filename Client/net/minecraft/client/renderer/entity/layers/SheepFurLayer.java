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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer
extends RenderLayer<Sheep, SheepModel<Sheep>> {
    private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    private final SheepFurModel<Sheep> model;

    public SheepFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> $$0, EntityModelSet $$1) {
        super($$0);
        this.model = new SheepFurModel($$1.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Sheep $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        float $$27;
        float $$26;
        float $$25;
        if ($$3.isSheared()) {
            return;
        }
        if ($$3.isInvisible()) {
            Minecraft $$10 = Minecraft.getInstance();
            boolean $$11 = $$10.shouldEntityAppearGlowing($$3);
            if ($$11) {
                ((SheepModel)this.getParentModel()).copyPropertiesTo(this.model);
                this.model.prepareMobModel($$3, $$4, $$5, $$6);
                this.model.setupAnim($$3, $$4, $$5, $$7, $$8, $$9);
                VertexConsumer $$12 = $$1.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
                this.model.renderToBuffer($$0, $$12, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), 0.0f, 0.0f, 0.0f, 1.0f);
            }
            return;
        }
        if ($$3.hasCustomName() && "jeb_".equals((Object)$$3.getName().getString())) {
            int $$13 = 25;
            int $$14 = $$3.tickCount / 25 + $$3.getId();
            int $$15 = DyeColor.values().length;
            int $$16 = $$14 % $$15;
            int $$17 = ($$14 + 1) % $$15;
            float $$18 = ((float)($$3.tickCount % 25) + $$6) / 25.0f;
            float[] $$19 = Sheep.getColorArray(DyeColor.byId($$16));
            float[] $$20 = Sheep.getColorArray(DyeColor.byId($$17));
            float $$21 = $$19[0] * (1.0f - $$18) + $$20[0] * $$18;
            float $$22 = $$19[1] * (1.0f - $$18) + $$20[1] * $$18;
            float $$23 = $$19[2] * (1.0f - $$18) + $$20[2] * $$18;
        } else {
            float[] $$24 = Sheep.getColorArray($$3.getColor());
            $$25 = $$24[0];
            $$26 = $$24[1];
            $$27 = $$24[2];
        }
        SheepFurLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SHEEP_FUR_LOCATION, $$0, $$1, $$2, $$3, $$4, $$5, $$7, $$8, $$9, $$6, $$25, $$26, $$27);
    }
}