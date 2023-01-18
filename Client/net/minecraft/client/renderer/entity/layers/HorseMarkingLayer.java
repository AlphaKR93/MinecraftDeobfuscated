/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;

public class HorseMarkingLayer
extends RenderLayer<Horse, HorseModel<Horse>> {
    private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = (Map)Util.make(Maps.newEnumMap(Markings.class), $$0 -> {
        $$0.put((Enum)Markings.NONE, null);
        $$0.put((Enum)Markings.WHITE, (Object)new ResourceLocation("textures/entity/horse/horse_markings_white.png"));
        $$0.put((Enum)Markings.WHITE_FIELD, (Object)new ResourceLocation("textures/entity/horse/horse_markings_whitefield.png"));
        $$0.put((Enum)Markings.WHITE_DOTS, (Object)new ResourceLocation("textures/entity/horse/horse_markings_whitedots.png"));
        $$0.put((Enum)Markings.BLACK_DOTS, (Object)new ResourceLocation("textures/entity/horse/horse_markings_blackdots.png"));
    });

    public HorseMarkingLayer(RenderLayerParent<Horse, HorseModel<Horse>> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, Horse $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        ResourceLocation $$10 = (ResourceLocation)LOCATION_BY_MARKINGS.get((Object)$$3.getMarkings());
        if ($$10 == null || $$3.isInvisible()) {
            return;
        }
        VertexConsumer $$11 = $$1.getBuffer(RenderType.entityTranslucent($$10));
        ((HorseModel)this.getParentModel()).renderToBuffer($$0, $$11, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
    }
}