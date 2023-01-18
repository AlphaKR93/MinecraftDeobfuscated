/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MapDecoration {
    private final Type type;
    private final byte x;
    private final byte y;
    private final byte rot;
    @Nullable
    private final Component name;

    public MapDecoration(Type $$0, byte $$1, byte $$2, byte $$3, @Nullable Component $$4) {
        this.type = $$0;
        this.x = $$1;
        this.y = $$2;
        this.rot = $$3;
        this.name = $$4;
    }

    public byte getImage() {
        return this.type.getIcon();
    }

    public Type getType() {
        return this.type;
    }

    public byte getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public byte getRot() {
        return this.rot;
    }

    public boolean renderOnFrame() {
        return this.type.isRenderedOnFrame();
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof MapDecoration)) {
            return false;
        }
        MapDecoration $$1 = (MapDecoration)$$0;
        return this.type == $$1.type && this.rot == $$1.rot && this.x == $$1.x && this.y == $$1.y && Objects.equals((Object)this.name, (Object)$$1.name);
    }

    public int hashCode() {
        int $$0 = this.type.getIcon();
        $$0 = 31 * $$0 + this.x;
        $$0 = 31 * $$0 + this.y;
        $$0 = 31 * $$0 + this.rot;
        $$0 = 31 * $$0 + Objects.hashCode((Object)this.name);
        return $$0;
    }

    public static enum Type {
        PLAYER(false, true),
        FRAME(true, true),
        RED_MARKER(false, true),
        BLUE_MARKER(false, true),
        TARGET_X(true, false),
        TARGET_POINT(true, false),
        PLAYER_OFF_MAP(false, true),
        PLAYER_OFF_LIMITS(false, true),
        MANSION(true, 5393476, false),
        MONUMENT(true, 3830373, false),
        BANNER_WHITE(true, true),
        BANNER_ORANGE(true, true),
        BANNER_MAGENTA(true, true),
        BANNER_LIGHT_BLUE(true, true),
        BANNER_YELLOW(true, true),
        BANNER_LIME(true, true),
        BANNER_PINK(true, true),
        BANNER_GRAY(true, true),
        BANNER_LIGHT_GRAY(true, true),
        BANNER_CYAN(true, true),
        BANNER_PURPLE(true, true),
        BANNER_BLUE(true, true),
        BANNER_BROWN(true, true),
        BANNER_GREEN(true, true),
        BANNER_RED(true, true),
        BANNER_BLACK(true, true),
        RED_X(true, false);

        private final byte icon;
        private final boolean renderedOnFrame;
        private final int mapColor;
        private final boolean trackCount;

        private Type(boolean $$0, boolean $$1) {
            this($$0, -1, $$1);
        }

        private Type(boolean $$0, int $$1, boolean $$2) {
            this.trackCount = $$2;
            this.icon = (byte)this.ordinal();
            this.renderedOnFrame = $$0;
            this.mapColor = $$1;
        }

        public byte getIcon() {
            return this.icon;
        }

        public boolean isRenderedOnFrame() {
            return this.renderedOnFrame;
        }

        public boolean hasMapColor() {
            return this.mapColor >= 0;
        }

        public int getMapColor() {
            return this.mapColor;
        }

        public static Type byIcon(byte $$0) {
            return Type.values()[Mth.clamp((int)$$0, 0, Type.values().length - 1)];
        }

        public boolean shouldTrackCount() {
            return this.trackCount;
        }
    }
}