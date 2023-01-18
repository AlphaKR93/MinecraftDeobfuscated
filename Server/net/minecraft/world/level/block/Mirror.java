/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;

public enum Mirror implements StringRepresentable
{
    NONE("none", OctahedralGroup.IDENTITY),
    LEFT_RIGHT("left_right", OctahedralGroup.INVERT_Z),
    FRONT_BACK("front_back", OctahedralGroup.INVERT_X);

    public static final Codec<Mirror> CODEC;
    private final String id;
    private final Component symbol;
    private final OctahedralGroup rotation;

    private Mirror(String $$0, OctahedralGroup $$1) {
        this.id = $$0;
        this.symbol = Component.translatable("mirror." + $$0);
        this.rotation = $$1;
    }

    public int mirror(int $$0, int $$1) {
        int $$2 = $$1 / 2;
        int $$3 = $$0 > $$2 ? $$0 - $$1 : $$0;
        switch (this) {
            case FRONT_BACK: {
                return ($$1 - $$3) % $$1;
            }
            case LEFT_RIGHT: {
                return ($$2 - $$3 + $$1) % $$1;
            }
        }
        return $$0;
    }

    public Rotation getRotation(Direction $$0) {
        Direction.Axis $$1 = $$0.getAxis();
        return this == LEFT_RIGHT && $$1 == Direction.Axis.Z || this == FRONT_BACK && $$1 == Direction.Axis.X ? Rotation.CLOCKWISE_180 : Rotation.NONE;
    }

    public Direction mirror(Direction $$0) {
        if (this == FRONT_BACK && $$0.getAxis() == Direction.Axis.X) {
            return $$0.getOpposite();
        }
        if (this == LEFT_RIGHT && $$0.getAxis() == Direction.Axis.Z) {
            return $$0.getOpposite();
        }
        return $$0;
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

    public Component symbol() {
        return this.symbol;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Mirror::values));
    }
}