/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.BitSet
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.stream.LongStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;

public final class BelowZeroRetrogen {
    private static final BitSet EMPTY = new BitSet(0);
    private static final Codec<BitSet> BITSET_CODEC = Codec.LONG_STREAM.xmap($$0 -> BitSet.valueOf((long[])$$0.toArray()), $$0 -> LongStream.of((long[])$$0.toLongArray()));
    private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS = BuiltInRegistries.CHUNK_STATUS.byNameCodec().comapFlatMap($$0 -> $$0 == ChunkStatus.EMPTY ? DataResult.error((String)"target_status cannot be empty") : DataResult.success((Object)$$0), Function.identity());
    public static final Codec<BelowZeroRetrogen> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(BelowZeroRetrogen::targetStatus), (App)BITSET_CODEC.optionalFieldOf("missing_bedrock").forGetter($$0 -> $$0.missingBedrock.isEmpty() ? Optional.empty() : Optional.of((Object)$$0.missingBedrock))).apply((Applicative)$$02, BelowZeroRetrogen::new));
    private static final Set<ResourceKey<Biome>> RETAINED_RETROGEN_BIOMES = Set.of(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES);
    public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor(){

        @Override
        public int getHeight() {
            return 64;
        }

        @Override
        public int getMinBuildHeight() {
            return -64;
        }
    };
    private final ChunkStatus targetStatus;
    private final BitSet missingBedrock;

    private BelowZeroRetrogen(ChunkStatus $$0, Optional<BitSet> $$1) {
        this.targetStatus = $$0;
        this.missingBedrock = (BitSet)$$1.orElse((Object)EMPTY);
    }

    @Nullable
    public static BelowZeroRetrogen read(CompoundTag $$0) {
        ChunkStatus $$1 = ChunkStatus.byName($$0.getString("target_status"));
        if ($$1 == ChunkStatus.EMPTY) {
            return null;
        }
        return new BelowZeroRetrogen($$1, (Optional<BitSet>)Optional.of((Object)BitSet.valueOf((long[])$$0.getLongArray("missing_bedrock"))));
    }

    public static void replaceOldBedrock(ProtoChunk $$0) {
        int $$12 = 4;
        BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach($$1 -> {
            if ($$0.getBlockState((BlockPos)$$1).is(Blocks.BEDROCK)) {
                $$0.setBlockState((BlockPos)$$1, Blocks.DEEPSLATE.defaultBlockState(), false);
            }
        });
    }

    public void applyBedrockMask(ProtoChunk $$0) {
        LevelHeightAccessor $$12 = $$0.getHeightAccessorForGeneration();
        int $$2 = $$12.getMinBuildHeight();
        int $$3 = $$12.getMaxBuildHeight() - 1;
        for (int $$4 = 0; $$4 < 16; ++$$4) {
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                if (!this.hasBedrockHole($$4, $$5)) continue;
                BlockPos.betweenClosed($$4, $$2, $$5, $$4, $$3, $$5).forEach($$1 -> $$0.setBlockState((BlockPos)$$1, Blocks.AIR.defaultBlockState(), false));
            }
        }
    }

    public ChunkStatus targetStatus() {
        return this.targetStatus;
    }

    public boolean hasBedrockHoles() {
        return !this.missingBedrock.isEmpty();
    }

    public boolean hasBedrockHole(int $$0, int $$1) {
        return this.missingBedrock.get(($$1 & 0xF) * 16 + ($$0 & 0xF));
    }

    public static BiomeResolver getBiomeResolver(BiomeResolver $$0, ChunkAccess $$1) {
        if (!$$1.isUpgrading()) {
            return $$0;
        }
        Predicate $$2 = arg_0 -> RETAINED_RETROGEN_BIOMES.contains(arg_0);
        return ($$3, $$4, $$5, $$6) -> {
            Holder<Biome> $$7 = $$0.getNoiseBiome($$3, $$4, $$5, $$6);
            if ($$7.is((Predicate<ResourceKey<Biome>>)$$2)) {
                return $$7;
            }
            return $$1.getNoiseBiome($$3, 0, $$5);
        };
    }
}