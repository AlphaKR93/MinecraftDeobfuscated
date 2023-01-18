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
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public final class HorseRenderer
extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = (Map)Util.make(Maps.newEnumMap(Variant.class), $$0 -> {
        $$0.put((Enum)Variant.WHITE, (Object)new ResourceLocation("textures/entity/horse/horse_white.png"));
        $$0.put((Enum)Variant.CREAMY, (Object)new ResourceLocation("textures/entity/horse/horse_creamy.png"));
        $$0.put((Enum)Variant.CHESTNUT, (Object)new ResourceLocation("textures/entity/horse/horse_chestnut.png"));
        $$0.put((Enum)Variant.BROWN, (Object)new ResourceLocation("textures/entity/horse/horse_brown.png"));
        $$0.put((Enum)Variant.BLACK, (Object)new ResourceLocation("textures/entity/horse/horse_black.png"));
        $$0.put((Enum)Variant.GRAY, (Object)new ResourceLocation("textures/entity/horse/horse_gray.png"));
        $$0.put((Enum)Variant.DARK_BROWN, (Object)new ResourceLocation("textures/entity/horse/horse_darkbrown.png"));
    });

    public HorseRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new HorseModel($$0.bakeLayer(ModelLayers.HORSE)), 1.1f);
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(new HorseArmorLayer(this, $$0.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Horse $$0) {
        return (ResourceLocation)LOCATION_BY_VARIANT.get((Object)$$0.getVariant());
    }
}