/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;

public class SpriteSources {
    private static final BiMap<ResourceLocation, SpriteSourceType> TYPES = HashBiMap.create();
    public static final SpriteSourceType SINGLE_FILE = SpriteSources.register("single", SingleFile.CODEC);
    public static final SpriteSourceType DIRECTORY = SpriteSources.register("directory", DirectoryLister.CODEC);
    public static final SpriteSourceType FILTER = SpriteSources.register("filter", SourceFilter.CODEC);
    public static final SpriteSourceType UNSTITCHER = SpriteSources.register("unstitch", Unstitcher.CODEC);
    public static final SpriteSourceType PALETTED_PERMUTATIONS = SpriteSources.register("paletted_permutations", PalettedPermutations.CODEC);
    public static Codec<SpriteSourceType> TYPE_CODEC = ResourceLocation.CODEC.flatXmap($$0 -> {
        SpriteSourceType $$1 = (SpriteSourceType)((Object)((Object)TYPES.get($$0)));
        return $$1 != null ? DataResult.success((Object)((Object)$$1)) : DataResult.error((String)("Unknown type " + $$0));
    }, $$0 -> {
        ResourceLocation $$1 = (ResourceLocation)TYPES.inverse().get((Object)$$0);
        return $$0 != null ? DataResult.success((Object)$$1) : DataResult.error((String)("Unknown type " + $$1));
    });
    public static Codec<SpriteSource> CODEC = TYPE_CODEC.dispatch(SpriteSource::type, SpriteSourceType::codec);
    public static Codec<List<SpriteSource>> FILE_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CODEC.listOf().fieldOf("sources").forGetter($$0 -> $$0)).apply((Applicative)$$02, $$0 -> $$0));

    private static SpriteSourceType register(String $$0, Codec<? extends SpriteSource> $$1) {
        ResourceLocation $$3 = new ResourceLocation($$0);
        SpriteSourceType $$2 = new SpriteSourceType($$1);
        SpriteSourceType $$4 = (SpriteSourceType)((Object)TYPES.putIfAbsent((Object)$$3, (Object)$$2));
        if ($$4 != null) {
            throw new IllegalStateException("Duplicate registration " + $$3);
        }
        return $$2;
    }
}