/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption
implements ParticleOptions {
    public static final ParticleOptions.Deserializer<BlockParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<BlockParticleOption>(){

        @Override
        public BlockParticleOption fromCommand(ParticleType<BlockParticleOption> $$0, StringReader $$1) throws CommandSyntaxException {
            $$1.expect(' ');
            return new BlockParticleOption($$0, BlockStateParser.parseForBlock((HolderLookup<Block>)BuiltInRegistries.BLOCK.asLookup(), $$1, false).blockState());
        }

        @Override
        public BlockParticleOption fromNetwork(ParticleType<BlockParticleOption> $$0, FriendlyByteBuf $$1) {
            return new BlockParticleOption($$0, $$1.readById(Block.BLOCK_STATE_REGISTRY));
        }
    };
    private final ParticleType<BlockParticleOption> type;
    private final BlockState state;

    public static Codec<BlockParticleOption> codec(ParticleType<BlockParticleOption> $$02) {
        return BlockState.CODEC.xmap($$1 -> new BlockParticleOption($$02, (BlockState)$$1), $$0 -> $$0.state);
    }

    public BlockParticleOption(ParticleType<BlockParticleOption> $$0, BlockState $$1) {
        this.type = $$0;
        this.state = $$1;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeId(Block.BLOCK_STATE_REGISTRY, this.state);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + BlockStateParser.serialize(this.state);
    }

    public ParticleType<BlockParticleOption> getType() {
        return this.type;
    }

    public BlockState getState() {
        return this.state;
    }
}