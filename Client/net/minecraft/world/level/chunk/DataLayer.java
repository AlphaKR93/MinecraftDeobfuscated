/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.VisibleForDebug;

public final class DataLayer {
    public static final int LAYER_COUNT = 16;
    public static final int LAYER_SIZE = 128;
    public static final int SIZE = 2048;
    private static final int NIBBLE_SIZE = 4;
    @Nullable
    protected byte[] data;

    public DataLayer() {
    }

    public DataLayer(byte[] $$0) {
        this.data = $$0;
        if ($$0.length != 2048) {
            throw Util.pauseInIde(new IllegalArgumentException("DataLayer should be 2048 bytes not: " + $$0.length));
        }
    }

    protected DataLayer(int $$0) {
        this.data = new byte[$$0];
    }

    public int get(int $$0, int $$1, int $$2) {
        return this.get(DataLayer.getIndex($$0, $$1, $$2));
    }

    public void set(int $$0, int $$1, int $$2, int $$3) {
        this.set(DataLayer.getIndex($$0, $$1, $$2), $$3);
    }

    private static int getIndex(int $$0, int $$1, int $$2) {
        return $$1 << 8 | $$2 << 4 | $$0;
    }

    private int get(int $$0) {
        if (this.data == null) {
            return 0;
        }
        int $$1 = DataLayer.getByteIndex($$0);
        int $$2 = DataLayer.getNibbleIndex($$0);
        return this.data[$$1] >> 4 * $$2 & 0xF;
    }

    private void set(int $$0, int $$1) {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        int $$2 = DataLayer.getByteIndex($$0);
        int $$3 = DataLayer.getNibbleIndex($$0);
        int $$4 = ~(15 << 4 * $$3);
        int $$5 = ($$1 & 0xF) << 4 * $$3;
        this.data[$$2] = (byte)(this.data[$$2] & $$4 | $$5);
    }

    private static int getNibbleIndex(int $$0) {
        return $$0 & 1;
    }

    private static int getByteIndex(int $$0) {
        return $$0 >> 1;
    }

    public byte[] getData() {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        return this.data;
    }

    public DataLayer copy() {
        if (this.data == null) {
            return new DataLayer();
        }
        return new DataLayer((byte[])this.data.clone());
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        for (int $$1 = 0; $$1 < 4096; ++$$1) {
            $$0.append(Integer.toHexString((int)this.get($$1)));
            if (($$1 & 0xF) == 15) {
                $$0.append("\n");
            }
            if (($$1 & 0xFF) != 255) continue;
            $$0.append("\n");
        }
        return $$0.toString();
    }

    @VisibleForDebug
    public String layerToString(int $$0) {
        StringBuilder $$1 = new StringBuilder();
        for (int $$2 = 0; $$2 < 256; ++$$2) {
            $$1.append(Integer.toHexString((int)this.get($$2)));
            if (($$2 & 0xF) != 15) continue;
            $$1.append("\n");
        }
        return $$1.toString();
    }

    public boolean isEmpty() {
        return this.data == null;
    }
}