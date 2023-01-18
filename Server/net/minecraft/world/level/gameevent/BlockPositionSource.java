/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource
implements PositionSource {
    public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockPos.CODEC.fieldOf("pos").forGetter($$0 -> $$0.pos)).apply((Applicative)$$02, BlockPositionSource::new));
    final BlockPos pos;

    public BlockPositionSource(BlockPos $$0) {
        this.pos = $$0;
    }

    @Override
    public Optional<Vec3> getPosition(Level $$0) {
        return Optional.of((Object)Vec3.atCenterOf(this.pos));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type
    implements PositionSourceType<BlockPositionSource> {
        @Override
        public BlockPositionSource read(FriendlyByteBuf $$0) {
            return new BlockPositionSource($$0.readBlockPos());
        }

        @Override
        public void write(FriendlyByteBuf $$0, BlockPositionSource $$1) {
            $$0.writeBlockPos($$1.pos);
        }

        @Override
        public Codec<BlockPositionSource> codec() {
            return CODEC;
        }
    }
}