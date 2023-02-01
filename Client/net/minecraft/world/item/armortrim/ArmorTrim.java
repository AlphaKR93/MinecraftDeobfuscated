/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  org.slf4j.Logger
 */
package net.minecraft.world.item.armortrim;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.slf4j.Logger;

public class ArmorTrim {
    public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), (App)TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply((Applicative)$$0, ArmorTrim::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_TRIM_ID = "Trim";
    private static final Component UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
    private final Holder<TrimMaterial> material;
    private final Holder<TrimPattern> pattern;
    private final Function<ArmorMaterial, ResourceLocation> innerTexture;
    private final Function<ArmorMaterial, ResourceLocation> outerTexture;

    public ArmorTrim(Holder<TrimMaterial> $$0, Holder<TrimPattern> $$1) {
        this.material = $$0;
        this.pattern = $$1;
        this.innerTexture = Util.memoize($$12 -> {
            ResourceLocation $$2 = ((TrimPattern)((Object)((Object)$$1.value()))).assetId();
            String $$3 = this.getColorPaletteSuffix((ArmorMaterial)$$12);
            return $$2.withPath((UnaryOperator<String>)((UnaryOperator)$$1 -> "trims/models/armor/" + $$1 + "_leggings_" + $$3));
        });
        this.outerTexture = Util.memoize($$12 -> {
            ResourceLocation $$2 = ((TrimPattern)((Object)((Object)$$1.value()))).assetId();
            String $$3 = this.getColorPaletteSuffix((ArmorMaterial)$$12);
            return $$2.withPath((UnaryOperator<String>)((UnaryOperator)$$1 -> "trims/models/armor/" + $$1 + "_" + $$3));
        });
    }

    private String getColorPaletteSuffix(ArmorMaterial $$0) {
        Map<ArmorMaterials, String> $$1 = this.material.value().overrideArmorMaterials();
        if ($$0 instanceof ArmorMaterials && $$1.containsKey((Object)$$0)) {
            return (String)$$1.get((Object)$$0);
        }
        return this.material.value().assetName();
    }

    public boolean hasPatternAndMaterial(Holder<TrimPattern> $$0, Holder<TrimMaterial> $$1) {
        return $$0 == this.pattern && $$1 == this.material;
    }

    public Holder<TrimPattern> pattern() {
        return this.pattern;
    }

    public Holder<TrimMaterial> material() {
        return this.material;
    }

    public ResourceLocation innerTexture(ArmorMaterial $$0) {
        return (ResourceLocation)this.innerTexture.apply((Object)$$0);
    }

    public ResourceLocation outerTexture(ArmorMaterial $$0) {
        return (ResourceLocation)this.outerTexture.apply((Object)$$0);
    }

    /*
     * WARNING - void declaration
     */
    public boolean equals(Object $$0) {
        void $$2;
        if (!($$0 instanceof ArmorTrim)) {
            return false;
        }
        ArmorTrim $$1 = (ArmorTrim)$$0;
        return $$2.pattern == this.pattern && $$2.material == this.material;
    }

    public static boolean setTrim(RegistryAccess $$0, ItemStack $$1, ArmorTrim $$2) {
        if ($$1.is(ItemTags.TRIMMABLE_ARMOR)) {
            $$1.getOrCreateTag().put(TAG_TRIM_ID, (Tag)CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, $$0), (Object)$$2).result().orElseThrow());
            return true;
        }
        return false;
    }

    public static Optional<ArmorTrim> getTrim(RegistryAccess $$0, ItemStack $$1) {
        if ($$1.is(ItemTags.TRIMMABLE_ARMOR) && $$1.getTag() != null && $$1.getTag().contains(TAG_TRIM_ID)) {
            CompoundTag $$2 = $$1.getTagElement(TAG_TRIM_ID);
            ArmorTrim $$3 = (ArmorTrim)CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, $$0), (Object)$$2).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse(null);
            return Optional.ofNullable((Object)$$3);
        }
        return Optional.empty();
    }

    public static void appendUpgradeHoverText(ItemStack $$0, RegistryAccess $$1, List<Component> $$2) {
        Optional<ArmorTrim> $$3 = ArmorTrim.getTrim($$1, $$0);
        if ($$3.isPresent()) {
            ArmorTrim $$4 = (ArmorTrim)$$3.get();
            $$2.add((Object)UPGRADE_TITLE);
            $$2.add((Object)CommonComponents.space().append($$4.pattern().value().copyWithStyle($$4.material())));
            $$2.add((Object)CommonComponents.space().append($$4.material().value().description()));
        }
    }
}