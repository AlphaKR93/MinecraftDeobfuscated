/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Record
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public interface VerticalAnchor {
    public static final Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(Absolute.CODEC, ExtraCodecs.xor(AboveBottom.CODEC, BelowTop.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
    public static final VerticalAnchor BOTTOM = VerticalAnchor.aboveBottom(0);
    public static final VerticalAnchor TOP = VerticalAnchor.belowTop(0);

    public static VerticalAnchor absolute(int $$0) {
        return new Absolute($$0);
    }

    public static VerticalAnchor aboveBottom(int $$0) {
        return new AboveBottom($$0);
    }

    public static VerticalAnchor belowTop(int $$0) {
        return new BelowTop($$0);
    }

    public static VerticalAnchor bottom() {
        return BOTTOM;
    }

    public static VerticalAnchor top() {
        return TOP;
    }

    private static VerticalAnchor merge(Either<Absolute, Either<AboveBottom, BelowTop>> $$02) {
        return (VerticalAnchor)$$02.map(Function.identity(), $$0 -> (Record)$$0.map(Function.identity(), Function.identity()));
    }

    private static Either<Absolute, Either<AboveBottom, BelowTop>> split(VerticalAnchor $$0) {
        if ($$0 instanceof Absolute) {
            return Either.left((Object)((Absolute)$$0));
        }
        return Either.right((Object)($$0 instanceof AboveBottom ? Either.left((Object)((AboveBottom)$$0)) : Either.right((Object)((BelowTop)$$0))));
    }

    public int resolveY(WorldGenerationContext var1);

    public record Absolute(int y) implements VerticalAnchor
    {
        public static final Codec<Absolute> CODEC = Codec.intRange((int)DimensionType.MIN_Y, (int)DimensionType.MAX_Y).fieldOf("absolute").xmap(Absolute::new, Absolute::y).codec();

        @Override
        public int resolveY(WorldGenerationContext $$0) {
            return this.y;
        }

        public String toString() {
            return this.y + " absolute";
        }
    }

    public record AboveBottom(int offset) implements VerticalAnchor
    {
        public static final Codec<AboveBottom> CODEC = Codec.intRange((int)DimensionType.MIN_Y, (int)DimensionType.MAX_Y).fieldOf("above_bottom").xmap(AboveBottom::new, AboveBottom::offset).codec();

        @Override
        public int resolveY(WorldGenerationContext $$0) {
            return $$0.getMinGenY() + this.offset;
        }

        public String toString() {
            return this.offset + " above bottom";
        }
    }

    public record BelowTop(int offset) implements VerticalAnchor
    {
        public static final Codec<BelowTop> CODEC = Codec.intRange((int)DimensionType.MIN_Y, (int)DimensionType.MAX_Y).fieldOf("below_top").xmap(BelowTop::new, BelowTop::offset).codec();

        @Override
        public int resolveY(WorldGenerationContext $$0) {
            return $$0.getGenDepth() - 1 + $$0.getMinGenY() - this.offset;
        }

        public String toString() {
            return this.offset + " below top";
        }
    }
}