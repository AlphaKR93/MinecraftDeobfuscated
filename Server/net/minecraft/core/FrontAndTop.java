/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum FrontAndTop implements StringRepresentable
{
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
    UP_EAST("up_east", Direction.UP, Direction.EAST),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
    UP_WEST("up_west", Direction.UP, Direction.WEST),
    WEST_UP("west_up", Direction.WEST, Direction.UP),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

    private static final Int2ObjectMap<FrontAndTop> LOOKUP_TOP_FRONT;
    private final String name;
    private final Direction top;
    private final Direction front;

    private static int lookupKey(Direction $$0, Direction $$1) {
        return $$1.ordinal() << 3 | $$0.ordinal();
    }

    private FrontAndTop(String $$0, Direction $$1, Direction $$2) {
        this.name = $$0;
        this.front = $$1;
        this.top = $$2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FrontAndTop fromFrontAndTop(Direction $$0, Direction $$1) {
        int $$2 = FrontAndTop.lookupKey($$0, $$1);
        return (FrontAndTop)LOOKUP_TOP_FRONT.get($$2);
    }

    public Direction front() {
        return this.front;
    }

    public Direction top() {
        return this.top;
    }

    static {
        LOOKUP_TOP_FRONT = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(FrontAndTop.values().length), $$0 -> {
            for (FrontAndTop $$1 : FrontAndTop.values()) {
                $$0.put(FrontAndTop.lookupKey($$1.front, $$1.top), (Object)$$1);
            }
        });
    }
}