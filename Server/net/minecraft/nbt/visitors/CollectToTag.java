/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayDeque
 *  java.util.Deque
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;

public class CollectToTag
implements StreamTagVisitor {
    private String lastId = "";
    @Nullable
    private Tag rootTag;
    private final Deque<Consumer<Tag>> consumerStack = new ArrayDeque();

    @Nullable
    public Tag getResult() {
        return this.rootTag;
    }

    protected int depth() {
        return this.consumerStack.size();
    }

    private void appendEntry(Tag $$0) {
        ((Consumer)this.consumerStack.getLast()).accept((Object)$$0);
    }

    @Override
    public StreamTagVisitor.ValueResult visitEnd() {
        this.appendEntry(EndTag.INSTANCE);
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(String $$0) {
        this.appendEntry(StringTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(byte $$0) {
        this.appendEntry(ByteTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(short $$0) {
        this.appendEntry(ShortTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(int $$0) {
        this.appendEntry(IntTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(long $$0) {
        this.appendEntry(LongTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(float $$0) {
        this.appendEntry(FloatTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(double $$0) {
        this.appendEntry(DoubleTag.valueOf($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(byte[] $$0) {
        this.appendEntry(new ByteArrayTag($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(int[] $$0) {
        this.appendEntry(new IntArrayTag($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visit(long[] $$0) {
        this.appendEntry(new LongArrayTag($$0));
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visitList(TagType<?> $$0, int $$1) {
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.EntryResult visitElement(TagType<?> $$0, int $$1) {
        this.enterContainerIfNeeded($$0);
        return StreamTagVisitor.EntryResult.ENTER;
    }

    @Override
    public StreamTagVisitor.EntryResult visitEntry(TagType<?> $$0) {
        return StreamTagVisitor.EntryResult.ENTER;
    }

    @Override
    public StreamTagVisitor.EntryResult visitEntry(TagType<?> $$0, String $$1) {
        this.lastId = $$1;
        this.enterContainerIfNeeded($$0);
        return StreamTagVisitor.EntryResult.ENTER;
    }

    private void enterContainerIfNeeded(TagType<?> $$0) {
        if ($$0 == ListTag.TYPE) {
            ListTag $$12 = new ListTag();
            this.appendEntry($$12);
            this.consumerStack.addLast(arg_0 -> ((ListTag)$$12).add(arg_0));
        } else if ($$0 == CompoundTag.TYPE) {
            CompoundTag $$2 = new CompoundTag();
            this.appendEntry($$2);
            this.consumerStack.addLast($$1 -> $$2.put(this.lastId, (Tag)$$1));
        }
    }

    @Override
    public StreamTagVisitor.ValueResult visitContainerEnd() {
        this.consumerStack.removeLast();
        return StreamTagVisitor.ValueResult.CONTINUE;
    }

    @Override
    public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> $$02) {
        if ($$02 == ListTag.TYPE) {
            ListTag $$12 = new ListTag();
            this.rootTag = $$12;
            this.consumerStack.addLast(arg_0 -> ((ListTag)$$12).add(arg_0));
        } else if ($$02 == CompoundTag.TYPE) {
            CompoundTag $$2 = new CompoundTag();
            this.rootTag = $$2;
            this.consumerStack.addLast($$1 -> $$2.put(this.lastId, (Tag)$$1));
        } else {
            this.consumerStack.addLast($$0 -> {
                this.rootTag = $$0;
            });
        }
        return StreamTagVisitor.ValueResult.CONTINUE;
    }
}