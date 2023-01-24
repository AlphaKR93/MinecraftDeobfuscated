/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 */
package net.minecraft.world.item.armortrim;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public record TrimMaterial(String assetName, Holder<Item> ingredient, float itemModelIndex, Optional<ArmorMaterials> incompatibleArmorMaterial, Component description) {
    public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.STRING.fieldOf("asset_name").forGetter(TrimMaterial::assetName), (App)RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(TrimMaterial::ingredient), (App)Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::itemModelIndex), (App)ArmorMaterials.CODEC.optionalFieldOf("incompatible_armor_material").forGetter(TrimMaterial::incompatibleArmorMaterial), (App)ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimMaterial::description)).apply((Applicative)$$0, TrimMaterial::new));
    public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);

    public static TrimMaterial create(String $$0, Item $$1, float $$2, Optional<ArmorMaterials> $$3, Component $$4) {
        return new TrimMaterial($$0, BuiltInRegistries.ITEM.wrapAsHolder($$1), $$2, $$3, $$4);
    }
}