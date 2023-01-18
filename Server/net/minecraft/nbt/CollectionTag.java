/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.AbstractList
 */
package net.minecraft.nbt;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;

public abstract class CollectionTag<T extends Tag>
extends AbstractList<T>
implements Tag {
    public abstract T set(int var1, T var2);

    public abstract void add(int var1, T var2);

    public abstract T remove(int var1);

    public abstract boolean setTag(int var1, Tag var2);

    public abstract boolean addTag(int var1, Tag var2);

    public abstract byte getElementType();
}