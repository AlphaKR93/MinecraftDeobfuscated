/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.network.chat.Component;

public interface NarrationElementOutput {
    default public void add(NarratedElementType $$0, Component $$1) {
        this.add($$0, NarrationThunk.from($$1.getString()));
    }

    default public void add(NarratedElementType $$0, String $$1) {
        this.add($$0, NarrationThunk.from($$1));
    }

    default public void add(NarratedElementType $$0, Component ... $$1) {
        this.add($$0, NarrationThunk.from((List<Component>)ImmutableList.copyOf((Object[])$$1)));
    }

    public void add(NarratedElementType var1, NarrationThunk<?> var2);

    public NarrationElementOutput nest();
}