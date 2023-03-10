/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.material;

import com.google.common.base.Preconditions;

public class MaterialColor {
    private static final MaterialColor[] MATERIAL_COLORS = new MaterialColor[64];
    public static final MaterialColor NONE = new MaterialColor(0, 0);
    public static final MaterialColor GRASS = new MaterialColor(1, 8368696);
    public static final MaterialColor SAND = new MaterialColor(2, 16247203);
    public static final MaterialColor WOOL = new MaterialColor(3, 0xC7C7C7);
    public static final MaterialColor FIRE = new MaterialColor(4, 0xFF0000);
    public static final MaterialColor ICE = new MaterialColor(5, 0xA0A0FF);
    public static final MaterialColor METAL = new MaterialColor(6, 0xA7A7A7);
    public static final MaterialColor PLANT = new MaterialColor(7, 31744);
    public static final MaterialColor SNOW = new MaterialColor(8, 0xFFFFFF);
    public static final MaterialColor CLAY = new MaterialColor(9, 10791096);
    public static final MaterialColor DIRT = new MaterialColor(10, 9923917);
    public static final MaterialColor STONE = new MaterialColor(11, 0x707070);
    public static final MaterialColor WATER = new MaterialColor(12, 0x4040FF);
    public static final MaterialColor WOOD = new MaterialColor(13, 9402184);
    public static final MaterialColor QUARTZ = new MaterialColor(14, 0xFFFCF5);
    public static final MaterialColor COLOR_ORANGE = new MaterialColor(15, 14188339);
    public static final MaterialColor COLOR_MAGENTA = new MaterialColor(16, 11685080);
    public static final MaterialColor COLOR_LIGHT_BLUE = new MaterialColor(17, 6724056);
    public static final MaterialColor COLOR_YELLOW = new MaterialColor(18, 0xE5E533);
    public static final MaterialColor COLOR_LIGHT_GREEN = new MaterialColor(19, 8375321);
    public static final MaterialColor COLOR_PINK = new MaterialColor(20, 15892389);
    public static final MaterialColor COLOR_GRAY = new MaterialColor(21, 0x4C4C4C);
    public static final MaterialColor COLOR_LIGHT_GRAY = new MaterialColor(22, 0x999999);
    public static final MaterialColor COLOR_CYAN = new MaterialColor(23, 5013401);
    public static final MaterialColor COLOR_PURPLE = new MaterialColor(24, 8339378);
    public static final MaterialColor COLOR_BLUE = new MaterialColor(25, 3361970);
    public static final MaterialColor COLOR_BROWN = new MaterialColor(26, 6704179);
    public static final MaterialColor COLOR_GREEN = new MaterialColor(27, 6717235);
    public static final MaterialColor COLOR_RED = new MaterialColor(28, 0x993333);
    public static final MaterialColor COLOR_BLACK = new MaterialColor(29, 0x191919);
    public static final MaterialColor GOLD = new MaterialColor(30, 16445005);
    public static final MaterialColor DIAMOND = new MaterialColor(31, 6085589);
    public static final MaterialColor LAPIS = new MaterialColor(32, 4882687);
    public static final MaterialColor EMERALD = new MaterialColor(33, 55610);
    public static final MaterialColor PODZOL = new MaterialColor(34, 8476209);
    public static final MaterialColor NETHER = new MaterialColor(35, 0x700200);
    public static final MaterialColor TERRACOTTA_WHITE = new MaterialColor(36, 13742497);
    public static final MaterialColor TERRACOTTA_ORANGE = new MaterialColor(37, 10441252);
    public static final MaterialColor TERRACOTTA_MAGENTA = new MaterialColor(38, 9787244);
    public static final MaterialColor TERRACOTTA_LIGHT_BLUE = new MaterialColor(39, 7367818);
    public static final MaterialColor TERRACOTTA_YELLOW = new MaterialColor(40, 12223780);
    public static final MaterialColor TERRACOTTA_LIGHT_GREEN = new MaterialColor(41, 6780213);
    public static final MaterialColor TERRACOTTA_PINK = new MaterialColor(42, 10505550);
    public static final MaterialColor TERRACOTTA_GRAY = new MaterialColor(43, 0x392923);
    public static final MaterialColor TERRACOTTA_LIGHT_GRAY = new MaterialColor(44, 8874850);
    public static final MaterialColor TERRACOTTA_CYAN = new MaterialColor(45, 0x575C5C);
    public static final MaterialColor TERRACOTTA_PURPLE = new MaterialColor(46, 8014168);
    public static final MaterialColor TERRACOTTA_BLUE = new MaterialColor(47, 4996700);
    public static final MaterialColor TERRACOTTA_BROWN = new MaterialColor(48, 4993571);
    public static final MaterialColor TERRACOTTA_GREEN = new MaterialColor(49, 5001770);
    public static final MaterialColor TERRACOTTA_RED = new MaterialColor(50, 9321518);
    public static final MaterialColor TERRACOTTA_BLACK = new MaterialColor(51, 2430480);
    public static final MaterialColor CRIMSON_NYLIUM = new MaterialColor(52, 12398641);
    public static final MaterialColor CRIMSON_STEM = new MaterialColor(53, 9715553);
    public static final MaterialColor CRIMSON_HYPHAE = new MaterialColor(54, 6035741);
    public static final MaterialColor WARPED_NYLIUM = new MaterialColor(55, 1474182);
    public static final MaterialColor WARPED_STEM = new MaterialColor(56, 3837580);
    public static final MaterialColor WARPED_HYPHAE = new MaterialColor(57, 5647422);
    public static final MaterialColor WARPED_WART_BLOCK = new MaterialColor(58, 1356933);
    public static final MaterialColor DEEPSLATE = new MaterialColor(59, 0x646464);
    public static final MaterialColor RAW_IRON = new MaterialColor(60, 14200723);
    public static final MaterialColor GLOW_LICHEN = new MaterialColor(61, 8365974);
    public final int col;
    public final int id;

    private MaterialColor(int $$0, int $$1) {
        if ($$0 < 0 || $$0 > 63) {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
        this.id = $$0;
        this.col = $$1;
        MaterialColor.MATERIAL_COLORS[$$0] = this;
    }

    public int calculateRGBColor(Brightness $$0) {
        if (this == NONE) {
            return 0;
        }
        int $$1 = $$0.modifier;
        int $$2 = (this.col >> 16 & 0xFF) * $$1 / 255;
        int $$3 = (this.col >> 8 & 0xFF) * $$1 / 255;
        int $$4 = (this.col & 0xFF) * $$1 / 255;
        return 0xFF000000 | $$4 << 16 | $$3 << 8 | $$2;
    }

    public static MaterialColor byId(int $$0) {
        Preconditions.checkPositionIndex((int)$$0, (int)MATERIAL_COLORS.length, (String)"material id");
        return MaterialColor.byIdUnsafe($$0);
    }

    private static MaterialColor byIdUnsafe(int $$0) {
        MaterialColor $$1 = MATERIAL_COLORS[$$0];
        return $$1 != null ? $$1 : NONE;
    }

    public static int getColorFromPackedId(int $$0) {
        int $$1 = $$0 & 0xFF;
        return MaterialColor.byIdUnsafe($$1 >> 2).calculateRGBColor(Brightness.byIdUnsafe($$1 & 3));
    }

    public byte getPackedId(Brightness $$0) {
        return (byte)(this.id << 2 | $$0.id & 3);
    }

    public static enum Brightness {
        LOW(0, 180),
        NORMAL(1, 220),
        HIGH(2, 255),
        LOWEST(3, 135);

        private static final Brightness[] VALUES;
        public final int id;
        public final int modifier;

        private Brightness(int $$0, int $$1) {
            this.id = $$0;
            this.modifier = $$1;
        }

        public static Brightness byId(int $$0) {
            Preconditions.checkPositionIndex((int)$$0, (int)VALUES.length, (String)"brightness id");
            return Brightness.byIdUnsafe($$0);
        }

        static Brightness byIdUnsafe(int $$0) {
            return VALUES[$$0];
        }

        static {
            VALUES = new Brightness[]{LOW, NORMAL, HIGH, LOWEST};
        }
    }
}