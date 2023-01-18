/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;

public class ComponentCollector {
    private final List<FormattedText> parts = Lists.newArrayList();

    public void append(FormattedText $$0) {
        this.parts.add((Object)$$0);
    }

    @Nullable
    public FormattedText getResult() {
        if (this.parts.isEmpty()) {
            return null;
        }
        if (this.parts.size() == 1) {
            return (FormattedText)this.parts.get(0);
        }
        return FormattedText.composite(this.parts);
    }

    public FormattedText getResultOrEmpty() {
        FormattedText $$0 = this.getResult();
        return $$0 != null ? $$0 : FormattedText.EMPTY;
    }

    public void reset() {
        this.parts.clear();
    }
}