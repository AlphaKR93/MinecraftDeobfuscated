/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class RotationSegment {
    private static final int MAX_SEGMENT_INDEX = 15;
    private static final int NORTH_0 = 0;
    private static final int EAST_90 = 4;
    private static final int SOUTH_180 = 8;
    private static final int WEST_270 = 12;

    public static int getMaxSegmentIndex() {
        return 15;
    }

    public static int convertToSegment(Direction $$0) {
        return $$0.getAxis().isVertical() ? 0 : $$0.getOpposite().get2DDataValue() * 4;
    }

    public static int convertToSegment(float $$0) {
        return Mth.floor((double)((180.0f + $$0) * 16.0f / 360.0f) + 0.5) & 0xF;
    }

    public static Optional<Direction> convertToDirection(int $$0) {
        Direction $$1 = switch ($$0) {
            case 0 -> Direction.NORTH;
            case 4 -> Direction.EAST;
            case 8 -> Direction.SOUTH;
            case 12 -> Direction.WEST;
            default -> null;
        };
        return Optional.ofNullable((Object)$$1);
    }

    public static float convertToDegrees(int $$0) {
        return (float)$$0 * 22.5f;
    }
}