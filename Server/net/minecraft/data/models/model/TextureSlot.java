/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.data.models.model;

import javax.annotation.Nullable;

public final class TextureSlot {
    public static final TextureSlot ALL = TextureSlot.create("all");
    public static final TextureSlot TEXTURE = TextureSlot.create("texture", ALL);
    public static final TextureSlot PARTICLE = TextureSlot.create("particle", TEXTURE);
    public static final TextureSlot END = TextureSlot.create("end", ALL);
    public static final TextureSlot BOTTOM = TextureSlot.create("bottom", END);
    public static final TextureSlot TOP = TextureSlot.create("top", END);
    public static final TextureSlot FRONT = TextureSlot.create("front", ALL);
    public static final TextureSlot BACK = TextureSlot.create("back", ALL);
    public static final TextureSlot SIDE = TextureSlot.create("side", ALL);
    public static final TextureSlot NORTH = TextureSlot.create("north", SIDE);
    public static final TextureSlot SOUTH = TextureSlot.create("south", SIDE);
    public static final TextureSlot EAST = TextureSlot.create("east", SIDE);
    public static final TextureSlot WEST = TextureSlot.create("west", SIDE);
    public static final TextureSlot UP = TextureSlot.create("up");
    public static final TextureSlot DOWN = TextureSlot.create("down");
    public static final TextureSlot CROSS = TextureSlot.create("cross");
    public static final TextureSlot PLANT = TextureSlot.create("plant");
    public static final TextureSlot WALL = TextureSlot.create("wall", ALL);
    public static final TextureSlot RAIL = TextureSlot.create("rail");
    public static final TextureSlot WOOL = TextureSlot.create("wool");
    public static final TextureSlot PATTERN = TextureSlot.create("pattern");
    public static final TextureSlot PANE = TextureSlot.create("pane");
    public static final TextureSlot EDGE = TextureSlot.create("edge");
    public static final TextureSlot FAN = TextureSlot.create("fan");
    public static final TextureSlot STEM = TextureSlot.create("stem");
    public static final TextureSlot UPPER_STEM = TextureSlot.create("upperstem");
    public static final TextureSlot CROP = TextureSlot.create("crop");
    public static final TextureSlot DIRT = TextureSlot.create("dirt");
    public static final TextureSlot FIRE = TextureSlot.create("fire");
    public static final TextureSlot LANTERN = TextureSlot.create("lantern");
    public static final TextureSlot PLATFORM = TextureSlot.create("platform");
    public static final TextureSlot UNSTICKY = TextureSlot.create("unsticky");
    public static final TextureSlot TORCH = TextureSlot.create("torch");
    public static final TextureSlot LAYER0 = TextureSlot.create("layer0");
    public static final TextureSlot LIT_LOG = TextureSlot.create("lit_log");
    public static final TextureSlot CANDLE = TextureSlot.create("candle");
    public static final TextureSlot INSIDE = TextureSlot.create("inside");
    public static final TextureSlot CONTENT = TextureSlot.create("content");
    public static final TextureSlot INNER_TOP = TextureSlot.create("inner_top");
    private final String id;
    @Nullable
    private final TextureSlot parent;

    private static TextureSlot create(String $$0) {
        return new TextureSlot($$0, null);
    }

    private static TextureSlot create(String $$0, TextureSlot $$1) {
        return new TextureSlot($$0, $$1);
    }

    private TextureSlot(String $$0, @Nullable TextureSlot $$1) {
        this.id = $$0;
        this.parent = $$1;
    }

    public String getId() {
        return this.id;
    }

    @Nullable
    public TextureSlot getParent() {
        return this.parent;
    }

    public String toString() {
        return "#" + this.id;
    }
}