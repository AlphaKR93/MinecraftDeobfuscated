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
 */
package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class HasSturdyFacePredicate
implements BlockPredicate {
    private final Vec3i offset;
    private final Direction direction;
    public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Vec3i.offsetCodec(16).optionalFieldOf("offset", (Object)Vec3i.ZERO).forGetter($$0 -> $$0.offset), (App)Direction.CODEC.fieldOf("direction").forGetter($$0 -> $$0.direction)).apply((Applicative)$$02, HasSturdyFacePredicate::new));

    public HasSturdyFacePredicate(Vec3i $$0, Direction $$1) {
        this.offset = $$0;
        this.direction = $$1;
    }

    public boolean test(WorldGenLevel $$0, BlockPos $$1) {
        Vec3i $$2 = $$1.offset(this.offset);
        return $$0.getBlockState((BlockPos)$$2).isFaceSturdy($$0, (BlockPos)$$2, this.direction);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.HAS_STURDY_FACE;
    }
}