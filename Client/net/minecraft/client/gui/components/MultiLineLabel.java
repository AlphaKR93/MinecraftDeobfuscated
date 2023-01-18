/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Objects
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
    public static final MultiLineLabel EMPTY = new MultiLineLabel(){

        @Override
        public int renderCentered(PoseStack $$0, int $$1, int $$2) {
            return $$2;
        }

        @Override
        public int renderCentered(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
            return $$2;
        }

        @Override
        public int renderLeftAligned(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
            return $$2;
        }

        @Override
        public int renderLeftAlignedNoShadow(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
            return $$2;
        }

        @Override
        public void renderBackgroundCentered(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }
    };

    public static MultiLineLabel create(Font $$0, FormattedText $$12, int $$2) {
        return MultiLineLabel.createFixed($$0, (List<TextWithWidth>)((List)$$0.split($$12, $$2).stream().map($$1 -> new TextWithWidth((FormattedCharSequence)$$1, $$0.width((FormattedCharSequence)$$1))).collect(ImmutableList.toImmutableList())));
    }

    public static MultiLineLabel create(Font $$0, FormattedText $$12, int $$2, int $$3) {
        return MultiLineLabel.createFixed($$0, (List<TextWithWidth>)((List)$$0.split($$12, $$2).stream().limit((long)$$3).map($$1 -> new TextWithWidth((FormattedCharSequence)$$1, $$0.width((FormattedCharSequence)$$1))).collect(ImmutableList.toImmutableList())));
    }

    public static MultiLineLabel create(Font $$0, Component ... $$12) {
        return MultiLineLabel.createFixed($$0, (List<TextWithWidth>)((List)Arrays.stream((Object[])$$12).map(Component::getVisualOrderText).map($$1 -> new TextWithWidth((FormattedCharSequence)$$1, $$0.width((FormattedCharSequence)$$1))).collect(ImmutableList.toImmutableList())));
    }

    public static MultiLineLabel create(Font $$0, List<Component> $$12) {
        return MultiLineLabel.createFixed($$0, (List<TextWithWidth>)((List)$$12.stream().map(Component::getVisualOrderText).map($$1 -> new TextWithWidth((FormattedCharSequence)$$1, $$0.width((FormattedCharSequence)$$1))).collect(ImmutableList.toImmutableList())));
    }

    public static MultiLineLabel createFixed(final Font $$0, final List<TextWithWidth> $$1) {
        if ($$1.isEmpty()) {
            return EMPTY;
        }
        return new MultiLineLabel(){
            private final int width;
            {
                this.width = $$1.stream().mapToInt($$0 -> $$02.width).max().orElse(0);
            }

            @Override
            public int renderCentered(PoseStack $$02, int $$12, int $$2) {
                Objects.requireNonNull((Object)$$0);
                return this.renderCentered($$02, $$12, $$2, 9, 0xFFFFFF);
            }

            @Override
            public int renderCentered(PoseStack $$02, int $$12, int $$2, int $$3, int $$4) {
                int $$5 = $$2;
                for (TextWithWidth $$6 : $$1) {
                    $$0.drawShadow($$02, $$6.text, (float)($$12 - $$6.width / 2), (float)$$5, $$4);
                    $$5 += $$3;
                }
                return $$5;
            }

            @Override
            public int renderLeftAligned(PoseStack $$02, int $$12, int $$2, int $$3, int $$4) {
                int $$5 = $$2;
                for (TextWithWidth $$6 : $$1) {
                    $$0.drawShadow($$02, $$6.text, (float)$$12, (float)$$5, $$4);
                    $$5 += $$3;
                }
                return $$5;
            }

            @Override
            public int renderLeftAlignedNoShadow(PoseStack $$02, int $$12, int $$2, int $$3, int $$4) {
                int $$5 = $$2;
                for (TextWithWidth $$6 : $$1) {
                    $$0.draw($$02, $$6.text, (float)$$12, (float)$$5, $$4);
                    $$5 += $$3;
                }
                return $$5;
            }

            @Override
            public void renderBackgroundCentered(PoseStack $$02, int $$12, int $$2, int $$3, int $$4, int $$5) {
                int $$6 = $$1.stream().mapToInt($$0 -> $$02.width).max().orElse(0);
                if ($$6 > 0) {
                    GuiComponent.fill($$02, $$12 - $$6 / 2 - $$4, $$2 - $$4, $$12 + $$6 / 2 + $$4, $$2 + $$1.size() * $$3 + $$4, $$5);
                }
            }

            @Override
            public int getLineCount() {
                return $$1.size();
            }

            @Override
            public int getWidth() {
                return this.width;
            }
        };
    }

    public int renderCentered(PoseStack var1, int var2, int var3);

    public int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5);

    public int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5);

    public int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5);

    public void renderBackgroundCentered(PoseStack var1, int var2, int var3, int var4, int var5, int var6);

    public int getLineCount();

    public int getWidth();

    public static class TextWithWidth {
        final FormattedCharSequence text;
        final int width;

        TextWithWidth(FormattedCharSequence $$0, int $$1) {
            this.text = $$0;
            this.width = $$1;
        }
    }
}