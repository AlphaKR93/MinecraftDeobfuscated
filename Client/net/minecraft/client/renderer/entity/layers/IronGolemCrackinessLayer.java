/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemCrackinessLayer
extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    private static final Map<IronGolem.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of((Object)((Object)IronGolem.Crackiness.LOW), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), (Object)((Object)IronGolem.Crackiness.MEDIUM), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), (Object)((Object)IronGolem.Crackiness.HIGH), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCrackinessLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> $$0) {
        super($$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, IronGolem $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if ($$3.isInvisible()) {
            return;
        }
        IronGolem.Crackiness $$10 = $$3.getCrackiness();
        if ($$10 == IronGolem.Crackiness.NONE) {
            return;
        }
        ResourceLocation $$11 = (ResourceLocation)resourceLocations.get((Object)$$10);
        IronGolemCrackinessLayer.renderColoredCutoutModel(this.getParentModel(), $$11, $$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f);
    }
}