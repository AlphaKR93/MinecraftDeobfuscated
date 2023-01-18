/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Stitcher<T extends Entry> {
    private static final Comparator<Holder<?>> HOLDER_COMPARATOR = Comparator.comparing($$0 -> -$$0.height).thenComparing($$0 -> -$$0.width).thenComparing($$0 -> $$0.entry.name());
    private final int mipLevel;
    private final List<Holder<T>> texturesToBeStitched = new ArrayList();
    private final List<Region<T>> storage = new ArrayList();
    private int storageX;
    private int storageY;
    private final int maxWidth;
    private final int maxHeight;

    public Stitcher(int $$0, int $$1, int $$2) {
        this.mipLevel = $$2;
        this.maxWidth = $$0;
        this.maxHeight = $$1;
    }

    public int getWidth() {
        return this.storageX;
    }

    public int getHeight() {
        return this.storageY;
    }

    public void registerSprite(T $$0) {
        Holder<T> $$1 = new Holder<T>($$0, this.mipLevel);
        this.texturesToBeStitched.add($$1);
    }

    public void stitch() {
        ArrayList $$02 = new ArrayList(this.texturesToBeStitched);
        $$02.sort(HOLDER_COMPARATOR);
        for (Holder $$1 : $$02) {
            if (this.addToStorage($$1)) continue;
            throw new StitcherException((Entry)$$1.entry, (Collection<Entry>)((Collection)$$02.stream().map($$0 -> $$0.entry).collect(ImmutableList.toImmutableList())));
        }
        this.storageX = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        this.storageY = Mth.smallestEncompassingPowerOfTwo(this.storageY);
    }

    public void gatherSprites(SpriteLoader<T> $$0) {
        for (Region $$1 : this.storage) {
            $$1.walk($$0);
        }
    }

    static int smallestFittingMinTexel(int $$0, int $$1) {
        return ($$0 >> $$1) + (($$0 & (1 << $$1) - 1) == 0 ? 0 : 1) << $$1;
    }

    private boolean addToStorage(Holder<T> $$0) {
        for (Region $$1 : this.storage) {
            if (!$$1.add($$0)) continue;
            return true;
        }
        return this.expand($$0);
    }

    private boolean expand(Holder<T> $$0) {
        Region<T> $$12;
        boolean $$10;
        boolean $$8;
        boolean $$6;
        int $$1 = Mth.smallestEncompassingPowerOfTwo(this.storageX);
        int $$2 = Mth.smallestEncompassingPowerOfTwo(this.storageY);
        int $$3 = Mth.smallestEncompassingPowerOfTwo(this.storageX + $$0.width);
        int $$4 = Mth.smallestEncompassingPowerOfTwo(this.storageY + $$0.height);
        boolean $$5 = $$3 <= this.maxWidth;
        boolean bl = $$6 = $$4 <= this.maxHeight;
        if (!$$5 && !$$6) {
            return false;
        }
        boolean $$7 = $$5 && $$1 != $$3;
        boolean bl2 = $$8 = $$6 && $$2 != $$4;
        if ($$7 ^ $$8) {
            boolean $$9 = $$7;
        } else {
            boolean bl3 = $$10 = $$5 && $$1 <= $$2;
        }
        if ($$10) {
            if (this.storageY == 0) {
                this.storageY = $$0.height;
            }
            Region $$11 = new Region(this.storageX, 0, $$0.width, this.storageY);
            this.storageX += $$0.width;
        } else {
            $$12 = new Region<T>(0, this.storageY, this.storageX, $$0.height);
            this.storageY += $$0.height;
        }
        $$12.add($$0);
        this.storage.add($$12);
        return true;
    }

    record Holder<T extends Entry>(T entry, int width, int height) {
        public Holder(T $$0, int $$1) {
            this($$0, Stitcher.smallestFittingMinTexel($$0.width(), $$1), Stitcher.smallestFittingMinTexel($$0.height(), $$1));
        }
    }

    public static interface Entry {
        public int width();

        public int height();

        public ResourceLocation name();
    }

    public static class Region<T extends Entry> {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        @Nullable
        private List<Region<T>> subSlots;
        @Nullable
        private Holder<T> holder;

        public Region(int $$0, int $$1, int $$2, int $$3) {
            this.originX = $$0;
            this.originY = $$1;
            this.width = $$2;
            this.height = $$3;
        }

        public int getX() {
            return this.originX;
        }

        public int getY() {
            return this.originY;
        }

        public boolean add(Holder<T> $$0) {
            if (this.holder != null) {
                return false;
            }
            int $$1 = $$0.width;
            int $$2 = $$0.height;
            if ($$1 > this.width || $$2 > this.height) {
                return false;
            }
            if ($$1 == this.width && $$2 == this.height) {
                this.holder = $$0;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = new ArrayList(1);
                this.subSlots.add(new Region<T>(this.originX, this.originY, $$1, $$2));
                int $$3 = this.width - $$1;
                int $$4 = this.height - $$2;
                if ($$4 > 0 && $$3 > 0) {
                    int $$6;
                    int $$5 = Math.max((int)this.height, (int)$$3);
                    if ($$5 >= ($$6 = Math.max((int)this.width, (int)$$4))) {
                        this.subSlots.add(new Region<T>(this.originX, this.originY + $$2, $$1, $$4));
                        this.subSlots.add(new Region<T>(this.originX + $$1, this.originY, $$3, this.height));
                    } else {
                        this.subSlots.add(new Region<T>(this.originX + $$1, this.originY, $$3, $$2));
                        this.subSlots.add(new Region<T>(this.originX, this.originY + $$2, this.width, $$4));
                    }
                } else if ($$3 == 0) {
                    this.subSlots.add(new Region<T>(this.originX, this.originY + $$2, $$1, $$4));
                } else if ($$4 == 0) {
                    this.subSlots.add(new Region<T>(this.originX + $$1, this.originY, $$3, $$2));
                }
            }
            for (Region $$7 : this.subSlots) {
                if (!$$7.add($$0)) continue;
                return true;
            }
            return false;
        }

        public void walk(SpriteLoader<T> $$0) {
            if (this.holder != null) {
                $$0.load(this.holder.entry, this.getX(), this.getY());
            } else if (this.subSlots != null) {
                for (Region $$1 : this.subSlots) {
                    $$1.walk($$0);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + "}";
        }
    }

    public static interface SpriteLoader<T extends Entry> {
        public void load(T var1, int var2, int var3);
    }
}