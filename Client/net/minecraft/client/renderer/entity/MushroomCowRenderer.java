/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowRenderer
extends MobRenderer<MushroomCow, CowModel<MushroomCow>> {
    private static final Map<MushroomCow.MushroomType, ResourceLocation> TEXTURES = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)MushroomCow.MushroomType.BROWN, (Object)new ResourceLocation("textures/entity/cow/brown_mooshroom.png"));
        $$0.put((Object)MushroomCow.MushroomType.RED, (Object)new ResourceLocation("textures/entity/cow/red_mooshroom.png"));
    });

    public MushroomCowRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new CowModel($$0.bakeLayer(ModelLayers.MOOSHROOM)), 0.7f);
        this.addLayer(new MushroomCowMushroomLayer<MushroomCow>(this, $$0.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(MushroomCow $$0) {
        return (ResourceLocation)TEXTURES.get((Object)$$0.getVariant());
    }
}