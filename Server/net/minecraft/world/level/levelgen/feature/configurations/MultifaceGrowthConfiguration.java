/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class MultifaceGrowthConfiguration
implements FeatureConfiguration {
    public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).orElse((Object)((MultifaceBlock)Blocks.GLOW_LICHEN)).forGetter($$0 -> $$0.placeBlock), (App)Codec.intRange((int)1, (int)64).fieldOf("search_range").orElse((Object)10).forGetter($$0 -> $$0.searchRange), (App)Codec.BOOL.fieldOf("can_place_on_floor").orElse((Object)false).forGetter($$0 -> $$0.canPlaceOnFloor), (App)Codec.BOOL.fieldOf("can_place_on_ceiling").orElse((Object)false).forGetter($$0 -> $$0.canPlaceOnCeiling), (App)Codec.BOOL.fieldOf("can_place_on_wall").orElse((Object)false).forGetter($$0 -> $$0.canPlaceOnWall), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_spreading").orElse((Object)Float.valueOf((float)0.5f)).forGetter($$0 -> Float.valueOf((float)$$0.chanceOfSpreading)), (App)RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter($$0 -> $$0.canBePlacedOn)).apply((Applicative)$$02, MultifaceGrowthConfiguration::new));
    public final MultifaceBlock placeBlock;
    public final int searchRange;
    public final boolean canPlaceOnFloor;
    public final boolean canPlaceOnCeiling;
    public final boolean canPlaceOnWall;
    public final float chanceOfSpreading;
    public final HolderSet<Block> canBePlacedOn;
    private final ObjectArrayList<Direction> validDirections;

    private static DataResult<MultifaceBlock> apply(Block $$0) {
        DataResult dataResult;
        if ($$0 instanceof MultifaceBlock) {
            MultifaceBlock $$1 = (MultifaceBlock)$$0;
            dataResult = DataResult.success((Object)$$1);
        } else {
            dataResult = DataResult.error((String)"Growth block should be a multiface block");
        }
        return dataResult;
    }

    public MultifaceGrowthConfiguration(MultifaceBlock $$0, int $$1, boolean $$2, boolean $$3, boolean $$4, float $$5, HolderSet<Block> $$6) {
        this.placeBlock = $$0;
        this.searchRange = $$1;
        this.canPlaceOnFloor = $$2;
        this.canPlaceOnCeiling = $$3;
        this.canPlaceOnWall = $$4;
        this.chanceOfSpreading = $$5;
        this.canBePlacedOn = $$6;
        this.validDirections = new ObjectArrayList(6);
        if ($$3) {
            this.validDirections.add((Object)Direction.UP);
        }
        if ($$2) {
            this.validDirections.add((Object)Direction.DOWN);
        }
        if ($$4) {
            Direction.Plane.HORIZONTAL.forEach(arg_0 -> this.validDirections.add(arg_0));
        }
    }

    public List<Direction> getShuffledDirectionsExcept(RandomSource $$0, Direction $$12) {
        return Util.toShuffledList(this.validDirections.stream().filter($$1 -> $$1 != $$12), $$0);
    }

    public List<Direction> getShuffledDirections(RandomSource $$0) {
        return Util.shuffledCopy(this.validDirections, $$0);
    }
}