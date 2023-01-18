/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft.world.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.Contract;

public enum DyeColor implements StringRepresentable
{
    WHITE(0, "white", 0xF9FFFE, MaterialColor.SNOW, 0xF0F0F0, 0xFFFFFF),
    ORANGE(1, "orange", 16351261, MaterialColor.COLOR_ORANGE, 15435844, 16738335),
    MAGENTA(2, "magenta", 13061821, MaterialColor.COLOR_MAGENTA, 12801229, 0xFF00FF),
    LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.COLOR_LIGHT_BLUE, 6719955, 10141901),
    YELLOW(4, "yellow", 16701501, MaterialColor.COLOR_YELLOW, 14602026, 0xFFFF00),
    LIME(5, "lime", 8439583, MaterialColor.COLOR_LIGHT_GREEN, 4312372, 0xBFFF00),
    PINK(6, "pink", 15961002, MaterialColor.COLOR_PINK, 14188952, 16738740),
    GRAY(7, "gray", 4673362, MaterialColor.COLOR_GRAY, 0x434343, 0x808080),
    LIGHT_GRAY(8, "light_gray", 0x9D9D97, MaterialColor.COLOR_LIGHT_GRAY, 0xABABAB, 0xD3D3D3),
    CYAN(9, "cyan", 1481884, MaterialColor.COLOR_CYAN, 2651799, 65535),
    PURPLE(10, "purple", 8991416, MaterialColor.COLOR_PURPLE, 8073150, 10494192),
    BLUE(11, "blue", 3949738, MaterialColor.COLOR_BLUE, 2437522, 255),
    BROWN(12, "brown", 8606770, MaterialColor.COLOR_BROWN, 5320730, 9127187),
    GREEN(13, "green", 6192150, MaterialColor.COLOR_GREEN, 3887386, 65280),
    RED(14, "red", 11546150, MaterialColor.COLOR_RED, 11743532, 0xFF0000),
    BLACK(15, "black", 0x1D1D21, MaterialColor.COLOR_BLACK, 0x1E1B1B, 0);

    private static final IntFunction<DyeColor> BY_ID;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    public static final StringRepresentable.EnumCodec<DyeColor> CODEC;
    private final int id;
    private final String name;
    private final MaterialColor color;
    private final float[] textureDiffuseColors;
    private final int fireworkColor;
    private final int textColor;

    private DyeColor(int $$0, String $$1, int $$2, MaterialColor $$3, int $$4, int $$5) {
        this.id = $$0;
        this.name = $$1;
        this.color = $$3;
        this.textColor = $$5;
        int $$6 = ($$2 & 0xFF0000) >> 16;
        int $$7 = ($$2 & 0xFF00) >> 8;
        int $$8 = ($$2 & 0xFF) >> 0;
        this.textureDiffuseColors = new float[]{(float)$$6 / 255.0f, (float)$$7 / 255.0f, (float)$$8 / 255.0f};
        this.fireworkColor = $$4;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public float[] getTextureDiffuseColors() {
        return this.textureDiffuseColors;
    }

    public MaterialColor getMaterialColor() {
        return this.color;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public static DyeColor byId(int $$0) {
        return (DyeColor)BY_ID.apply($$0);
    }

    @Nullable
    @Contract(value="_,!null->!null;_,null->_")
    public static DyeColor byName(String $$0, @Nullable DyeColor $$1) {
        DyeColor $$2 = CODEC.byName($$0);
        return $$2 != null ? $$2 : $$1;
    }

    @Nullable
    public static DyeColor byFireworkColor(int $$0) {
        return (DyeColor)BY_FIREWORK_COLOR.get($$0);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        BY_ID = ByIdMap.continuous(DyeColor::getId, DyeColor.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap((Map)Arrays.stream((Object[])DyeColor.values()).collect(Collectors.toMap($$0 -> $$0.fireworkColor, $$0 -> $$0)));
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)DyeColor::values));
    }
}