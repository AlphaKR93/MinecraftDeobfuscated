/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper
extends ChangeOverTimeBlock<WeatherState> {
    public static final Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> ImmutableBiMap.builder().put((Object)Blocks.COPPER_BLOCK, (Object)Blocks.EXPOSED_COPPER).put((Object)Blocks.EXPOSED_COPPER, (Object)Blocks.WEATHERED_COPPER).put((Object)Blocks.WEATHERED_COPPER, (Object)Blocks.OXIDIZED_COPPER).put((Object)Blocks.CUT_COPPER, (Object)Blocks.EXPOSED_CUT_COPPER).put((Object)Blocks.EXPOSED_CUT_COPPER, (Object)Blocks.WEATHERED_CUT_COPPER).put((Object)Blocks.WEATHERED_CUT_COPPER, (Object)Blocks.OXIDIZED_CUT_COPPER).put((Object)Blocks.CUT_COPPER_SLAB, (Object)Blocks.EXPOSED_CUT_COPPER_SLAB).put((Object)Blocks.EXPOSED_CUT_COPPER_SLAB, (Object)Blocks.WEATHERED_CUT_COPPER_SLAB).put((Object)Blocks.WEATHERED_CUT_COPPER_SLAB, (Object)Blocks.OXIDIZED_CUT_COPPER_SLAB).put((Object)Blocks.CUT_COPPER_STAIRS, (Object)Blocks.EXPOSED_CUT_COPPER_STAIRS).put((Object)Blocks.EXPOSED_CUT_COPPER_STAIRS, (Object)Blocks.WEATHERED_CUT_COPPER_STAIRS).put((Object)Blocks.WEATHERED_CUT_COPPER_STAIRS, (Object)Blocks.OXIDIZED_CUT_COPPER_STAIRS).build());
    public static final Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> ((BiMap)NEXT_BY_BLOCK.get()).inverse());

    public static Optional<Block> getPrevious(Block $$0) {
        return Optional.ofNullable((Object)((Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get((Object)$$0)));
    }

    public static Block getFirst(Block $$0) {
        Block $$1 = $$0;
        Block $$2 = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get((Object)$$1);
        while ($$2 != null) {
            $$1 = $$2;
            $$2 = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get((Object)$$1);
        }
        return $$1;
    }

    public static Optional<BlockState> getPrevious(BlockState $$0) {
        return WeatheringCopper.getPrevious($$0.getBlock()).map($$1 -> $$1.withPropertiesOf($$0));
    }

    public static Optional<Block> getNext(Block $$0) {
        return Optional.ofNullable((Object)((Block)((BiMap)NEXT_BY_BLOCK.get()).get((Object)$$0)));
    }

    public static BlockState getFirst(BlockState $$0) {
        return WeatheringCopper.getFirst($$0.getBlock()).withPropertiesOf($$0);
    }

    @Override
    default public Optional<BlockState> getNext(BlockState $$0) {
        return WeatheringCopper.getNext($$0.getBlock()).map($$1 -> $$1.withPropertiesOf($$0));
    }

    @Override
    default public float getChanceModifier() {
        if (this.getAge() == WeatherState.UNAFFECTED) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static enum WeatherState {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        OXIDIZED;

    }
}