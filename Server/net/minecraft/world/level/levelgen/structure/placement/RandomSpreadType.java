/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum RandomSpreadType implements StringRepresentable
{
    LINEAR("linear"),
    TRIANGULAR("triangular");

    public static final Codec<RandomSpreadType> CODEC;
    private final String id;

    private RandomSpreadType(String $$0) {
        this.id = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public int evaluate(RandomSource $$0, int $$1) {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case LINEAR -> $$0.nextInt($$1);
            case TRIANGULAR -> ($$0.nextInt($$1) + $$0.nextInt($$1)) / 2;
        };
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)RandomSpreadType::values));
    }
}