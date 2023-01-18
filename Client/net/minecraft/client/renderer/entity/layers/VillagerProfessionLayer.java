/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.UnaryOperator
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerProfessionLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(1, (Object)new ResourceLocation("stone"));
        $$0.put(2, (Object)new ResourceLocation("iron"));
        $$0.put(3, (Object)new ResourceLocation("gold"));
        $$0.put(4, (Object)new ResourceLocation("emerald"));
        $$0.put(5, (Object)new ResourceLocation("diamond"));
    });
    private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap();
    private final ResourceManager resourceManager;
    private final String path;

    public VillagerProfessionLayer(RenderLayerParent<T, M> $$0, ResourceManager $$1, String $$2) {
        super($$0);
        this.resourceManager = $$1;
        this.path = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, T $$3, float $$4, float $$5, float $$6, float $$7, float $$8, float $$9) {
        if (((Entity)$$3).isInvisible()) {
            return;
        }
        VillagerData $$10 = ((VillagerDataHolder)$$3).getVillagerData();
        VillagerType $$11 = $$10.getType();
        VillagerProfession $$12 = $$10.getProfession();
        VillagerMetaDataSection.Hat $$13 = this.getHatData(this.typeHatCache, "type", BuiltInRegistries.VILLAGER_TYPE, $$11);
        VillagerMetaDataSection.Hat $$14 = this.getHatData(this.professionHatCache, "profession", BuiltInRegistries.VILLAGER_PROFESSION, $$12);
        Object $$15 = this.getParentModel();
        ((VillagerHeadModel)$$15).hatVisible($$14 == VillagerMetaDataSection.Hat.NONE || $$14 == VillagerMetaDataSection.Hat.PARTIAL && $$13 != VillagerMetaDataSection.Hat.FULL);
        ResourceLocation $$16 = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey($$11));
        VillagerProfessionLayer.renderColoredCutoutModel($$15, $$16, $$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f);
        ((VillagerHeadModel)$$15).hatVisible(true);
        if ($$12 != VillagerProfession.NONE && !((LivingEntity)$$3).isBaby()) {
            ResourceLocation $$17 = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey($$12));
            VillagerProfessionLayer.renderColoredCutoutModel($$15, $$17, $$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f);
            if ($$12 != VillagerProfession.NITWIT) {
                ResourceLocation $$18 = this.getResourceLocation("profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp($$10.getLevel(), 1, LEVEL_LOCATIONS.size())));
                VillagerProfessionLayer.renderColoredCutoutModel($$15, $$18, $$0, $$1, $$2, $$3, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private ResourceLocation getResourceLocation(String $$0, ResourceLocation $$12) {
        return $$12.withPath((UnaryOperator<String>)((UnaryOperator)$$1 -> "textures/entity/" + this.path + "/" + $$0 + "/" + $$1 + ".png"));
    }

    public <K> VillagerMetaDataSection.Hat getHatData(Object2ObjectMap<K, VillagerMetaDataSection.Hat> $$0, String $$1, DefaultedRegistry<K> $$2, K $$32) {
        return (VillagerMetaDataSection.Hat)((Object)$$0.computeIfAbsent($$32, $$3 -> (VillagerMetaDataSection.Hat)((Object)((Object)this.resourceManager.getResource(this.getResourceLocation($$1, $$2.getKey($$32))).flatMap($$0 -> {
            try {
                return $$0.metadata().getSection(VillagerMetaDataSection.SERIALIZER).map(VillagerMetaDataSection::getHat);
            }
            catch (IOException $$1) {
                return Optional.empty();
            }
        }).orElse((Object)VillagerMetaDataSection.Hat.NONE)))));
    }
}