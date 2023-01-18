/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.presets;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

public class WorldPreset {
    public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter($$0 -> $$0.dimensions)).apply((Applicative)$$02, WorldPreset::new)).flatXmap(WorldPreset::requireOverworld, WorldPreset::requireOverworld);
    public static final Codec<Holder<WorldPreset>> CODEC = RegistryFileCodec.create(Registries.WORLD_PRESET, DIRECT_CODEC);
    private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;

    public WorldPreset(Map<ResourceKey<LevelStem>, LevelStem> $$0) {
        this.dimensions = $$0;
    }

    private Registry<LevelStem> createRegistry() {
        MappedRegistry<LevelStem> $$0 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.experimental());
        WorldDimensions.keysInOrder((Stream<ResourceKey<LevelStem>>)this.dimensions.keySet().stream()).forEach($$1 -> {
            LevelStem $$2 = (LevelStem)((Object)((Object)this.dimensions.get($$1)));
            if ($$2 != null) {
                $$0.register((ResourceKey<LevelStem>)$$1, $$2, Lifecycle.stable());
            }
        });
        return $$0.freeze();
    }

    public WorldDimensions createWorldDimensions() {
        return new WorldDimensions(this.createRegistry());
    }

    public Optional<LevelStem> overworld() {
        return Optional.ofNullable((Object)((Object)((LevelStem)((Object)this.dimensions.get(LevelStem.OVERWORLD)))));
    }

    private static DataResult<WorldPreset> requireOverworld(WorldPreset $$0) {
        if ($$0.overworld().isEmpty()) {
            return DataResult.error((String)"Missing overworld dimension");
        }
        return DataResult.success((Object)$$0, (Lifecycle)Lifecycle.stable());
    }
}