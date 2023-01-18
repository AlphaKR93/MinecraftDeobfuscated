/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
    public static final int SECTION_WIDTH = 16;
    public static final int SECTION_HEIGHT = 16;
    public static final int SECTION_SIZE = 4096;
    public static final int BIOME_CONTAINER_BITS = 2;
    private final int bottomBlockY;
    private short nonEmptyBlockCount;
    private short tickingBlockCount;
    private short tickingFluidCount;
    private final PalettedContainer<BlockState> states;
    private PalettedContainerRO<Holder<Biome>> biomes;

    public LevelChunkSection(int $$0, PalettedContainer<BlockState> $$1, PalettedContainerRO<Holder<Biome>> $$2) {
        this.bottomBlockY = LevelChunkSection.getBottomBlockY($$0);
        this.states = $$1;
        this.biomes = $$2;
        this.recalcBlockCounts();
    }

    public LevelChunkSection(int $$0, Registry<Biome> $$1) {
        this.bottomBlockY = LevelChunkSection.getBottomBlockY($$0);
        this.states = new PalettedContainer<BlockState>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        this.biomes = new PalettedContainer<Holder.Reference<Biome>>($$1.asHolderIdMap(), $$1.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
    }

    public static int getBottomBlockY(int $$0) {
        return $$0 << 4;
    }

    public BlockState getBlockState(int $$0, int $$1, int $$2) {
        return this.states.get($$0, $$1, $$2);
    }

    public FluidState getFluidState(int $$0, int $$1, int $$2) {
        return this.states.get($$0, $$1, $$2).getFluidState();
    }

    public void acquire() {
        this.states.acquire();
    }

    public void release() {
        this.states.release();
    }

    public BlockState setBlockState(int $$0, int $$1, int $$2, BlockState $$3) {
        return this.setBlockState($$0, $$1, $$2, $$3, true);
    }

    public BlockState setBlockState(int $$0, int $$1, int $$2, BlockState $$3, boolean $$4) {
        BlockState $$6;
        if ($$4) {
            BlockState $$5 = this.states.getAndSet($$0, $$1, $$2, $$3);
        } else {
            $$6 = this.states.getAndSetUnchecked($$0, $$1, $$2, $$3);
        }
        FluidState $$7 = $$6.getFluidState();
        FluidState $$8 = $$3.getFluidState();
        if (!$$6.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount - 1);
            if ($$6.isRandomlyTicking()) {
                this.tickingBlockCount = (short)(this.tickingBlockCount - 1);
            }
        }
        if (!$$7.isEmpty()) {
            this.tickingFluidCount = (short)(this.tickingFluidCount - 1);
        }
        if (!$$3.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + 1);
            if ($$3.isRandomlyTicking()) {
                this.tickingBlockCount = (short)(this.tickingBlockCount + 1);
            }
        }
        if (!$$8.isEmpty()) {
            this.tickingFluidCount = (short)(this.tickingFluidCount + 1);
        }
        return $$6;
    }

    public boolean hasOnlyAir() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean isRandomlyTicking() {
        return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
    }

    public boolean isRandomlyTickingBlocks() {
        return this.tickingBlockCount > 0;
    }

    public boolean isRandomlyTickingFluids() {
        return this.tickingFluidCount > 0;
    }

    public int bottomBlockY() {
        return this.bottomBlockY;
    }

    public void recalcBlockCounts() {
        class BlockCounter
        implements PalettedContainer.CountConsumer<BlockState> {
            public int nonEmptyBlockCount;
            public int tickingBlockCount;
            public int tickingFluidCount;

            BlockCounter() {
            }

            @Override
            public void accept(BlockState $$0, int $$1) {
                FluidState $$2 = $$0.getFluidState();
                if (!$$0.isAir()) {
                    this.nonEmptyBlockCount += $$1;
                    if ($$0.isRandomlyTicking()) {
                        this.tickingBlockCount += $$1;
                    }
                }
                if (!$$2.isEmpty()) {
                    this.nonEmptyBlockCount += $$1;
                    if ($$2.isRandomlyTicking()) {
                        this.tickingFluidCount += $$1;
                    }
                }
            }
        }
        BlockCounter $$0 = new BlockCounter();
        this.states.count($$0);
        this.nonEmptyBlockCount = (short)$$0.nonEmptyBlockCount;
        this.tickingBlockCount = (short)$$0.tickingBlockCount;
        this.tickingFluidCount = (short)$$0.tickingFluidCount;
    }

    public PalettedContainer<BlockState> getStates() {
        return this.states;
    }

    public PalettedContainerRO<Holder<Biome>> getBiomes() {
        return this.biomes;
    }

    public void read(FriendlyByteBuf $$0) {
        this.nonEmptyBlockCount = $$0.readShort();
        this.states.read($$0);
        PalettedContainer<Holder<Biome>> $$1 = this.biomes.recreate();
        $$1.read($$0);
        this.biomes = $$1;
    }

    public void write(FriendlyByteBuf $$0) {
        $$0.writeShort(this.nonEmptyBlockCount);
        this.states.write($$0);
        this.biomes.write($$0);
    }

    public int getSerializedSize() {
        return 2 + this.states.getSerializedSize() + this.biomes.getSerializedSize();
    }

    public boolean maybeHas(Predicate<BlockState> $$0) {
        return this.states.maybeHas($$0);
    }

    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2) {
        return this.biomes.get($$0, $$1, $$2);
    }

    public void fillBiomesFromNoise(BiomeResolver $$0, Climate.Sampler $$1, int $$2, int $$3) {
        PalettedContainer<Holder<Biome>> $$4 = this.biomes.recreate();
        int $$5 = QuartPos.fromBlock(this.bottomBlockY());
        int $$6 = 4;
        for (int $$7 = 0; $$7 < 4; ++$$7) {
            for (int $$8 = 0; $$8 < 4; ++$$8) {
                for (int $$9 = 0; $$9 < 4; ++$$9) {
                    $$4.getAndSetUnchecked($$7, $$8, $$9, $$0.getNoiseBiome($$2 + $$7, $$5 + $$8, $$3 + $$9, $$1));
                }
            }
        }
        this.biomes = $$4;
    }
}