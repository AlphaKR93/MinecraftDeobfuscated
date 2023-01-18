/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.BiConsumer
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class ComponentRenderUtils {
    private static final FormattedCharSequence INDENT = FormattedCharSequence.codepoint(32, Style.EMPTY);

    private static String stripColor(String $$0) {
        return Minecraft.getInstance().options.chatColors().get() != false ? $$0 : ChatFormatting.stripFormatting($$0);
    }

    public static List<FormattedCharSequence> wrapComponents(FormattedText $$0, int $$12, Font $$22) {
        ComponentCollector $$3 = new ComponentCollector();
        $$0.visit(($$1, $$2) -> {
            $$3.append(FormattedText.of(ComponentRenderUtils.stripColor($$2), $$1));
            return Optional.empty();
        }, Style.EMPTY);
        ArrayList $$4 = Lists.newArrayList();
        $$22.getSplitter().splitLines($$3.getResultOrEmpty(), $$12, Style.EMPTY, (BiConsumer<FormattedText, Boolean>)((BiConsumer)(arg_0, arg_1) -> ComponentRenderUtils.lambda$wrapComponents$1((List)$$4, arg_0, arg_1)));
        if ($$4.isEmpty()) {
            return Lists.newArrayList((Object[])new FormattedCharSequence[]{FormattedCharSequence.EMPTY});
        }
        return $$4;
    }

    private static /* synthetic */ void lambda$wrapComponents$1(List $$0, FormattedText $$1, Boolean $$2) {
        FormattedCharSequence $$3 = Language.getInstance().getVisualOrder($$1);
        $$0.add((Object)($$2 != false ? FormattedCharSequence.composite(INDENT, $$3) : $$3));
    }
}