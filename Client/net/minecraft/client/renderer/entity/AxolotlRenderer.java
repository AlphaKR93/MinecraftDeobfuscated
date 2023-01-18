/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.Map
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderer
extends MobRenderer<Axolotl, AxolotlModel<Axolotl>> {
    private static final Map<Axolotl.Variant, ResourceLocation> TEXTURE_BY_TYPE = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        for (Axolotl.Variant $$1 : Axolotl.Variant.values()) {
            $$0.put((Object)$$1, (Object)new ResourceLocation(String.format((Locale)Locale.ROOT, (String)"textures/entity/axolotl/axolotl_%s.png", (Object[])new Object[]{$$1.getName()})));
        }
    });

    public AxolotlRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new AxolotlModel($$0.bakeLayer(ModelLayers.AXOLOTL)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Axolotl $$0) {
        return (ResourceLocation)TEXTURE_BY_TYPE.get((Object)$$0.getVariant());
    }
}