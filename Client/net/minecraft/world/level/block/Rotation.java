/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum Rotation implements StringRepresentable
{
    NONE("none", OctahedralGroup.IDENTITY),
    CLOCKWISE_90("clockwise_90", OctahedralGroup.ROT_90_Y_NEG),
    CLOCKWISE_180("180", OctahedralGroup.ROT_180_FACE_XZ),
    COUNTERCLOCKWISE_90("counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);

    public static final Codec<Rotation> CODEC;
    private final String id;
    private final OctahedralGroup rotation;

    private Rotation(String $$0, OctahedralGroup $$1) {
        this.id = $$0;
        this.rotation = $$1;
    }

    public Rotation getRotated(Rotation $$0) {
        switch ($$0) {
            case CLOCKWISE_180: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_90: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_180: {
                        return NONE;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_90;
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return NONE;
                    }
                    case CLOCKWISE_180: {
                        return CLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (this) {
                    case NONE: {
                        return CLOCKWISE_90;
                    }
                    case CLOCKWISE_90: {
                        return CLOCKWISE_180;
                    }
                    case CLOCKWISE_180: {
                        return COUNTERCLOCKWISE_90;
                    }
                    case COUNTERCLOCKWISE_90: {
                        return NONE;
                    }
                }
            }
        }
        return this;
    }

    public OctahedralGroup rotation() {
        return this.rotation;
    }

    public Direction rotate(Direction $$0) {
        if ($$0.getAxis() == Direction.Axis.Y) {
            return $$0;
        }
        switch (this) {
            case CLOCKWISE_180: {
                return $$0.getOpposite();
            }
            case COUNTERCLOCKWISE_90: {
                return $$0.getCounterClockWise();
            }
            case CLOCKWISE_90: {
                return $$0.getClockWise();
            }
        }
        return $$0;
    }

    public int rotate(int $$0, int $$1) {
        switch (this) {
            case CLOCKWISE_180: {
                return ($$0 + $$1 / 2) % $$1;
            }
            case COUNTERCLOCKWISE_90: {
                return ($$0 + $$1 * 3 / 4) % $$1;
            }
            case CLOCKWISE_90: {
                return ($$0 + $$1 / 4) % $$1;
            }
        }
        return $$0;
    }

    public static Rotation getRandom(RandomSource $$0) {
        return Util.getRandom(Rotation.values(), $$0);
    }

    public static List<Rotation> getShuffled(RandomSource $$0) {
        return Util.shuffledCopy(Rotation.values(), $$0);
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Rotation::values));
    }
}