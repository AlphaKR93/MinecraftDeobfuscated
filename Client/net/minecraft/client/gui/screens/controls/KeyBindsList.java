/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collections
 *  java.util.List
 *  java.util.Objects
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.gui.screens.controls;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class KeyBindsList
extends ContainerObjectSelectionList<Entry> {
    final KeyBindsScreen keyBindsScreen;
    int maxNameWidth;

    public KeyBindsList(KeyBindsScreen $$0, Minecraft $$1) {
        super($$1, $$0.width + 45, $$0.height, 20, $$0.height - 32, 20);
        this.keyBindsScreen = $$0;
        Object[] $$2 = (KeyMapping[])ArrayUtils.clone((Object[])$$1.options.keyMappings);
        Arrays.sort((Object[])$$2);
        String $$3 = null;
        for (Object $$4 : $$2) {
            MutableComponent $$6;
            int $$7;
            String $$5 = ((KeyMapping)$$4).getCategory();
            if (!$$5.equals($$3)) {
                $$3 = $$5;
                this.addEntry(new CategoryEntry(Component.translatable($$5)));
            }
            if (($$7 = $$1.font.width($$6 = Component.translatable(((KeyMapping)$$4).getName()))) > this.maxNameWidth) {
                this.maxNameWidth = $$7;
            }
            this.addEntry(new KeyEntry((KeyMapping)$$4, $$6));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    public class CategoryEntry
    extends Entry {
        final Component name;
        private final int width;

        public CategoryEntry(Component $$1) {
            this.name = $$1;
            this.width = ((KeyBindsList)KeyBindsList.this).minecraft.font.width(this.name);
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            Font font = ((KeyBindsList)KeyBindsList.this).minecraft.font;
            float f = ((KeyBindsList)KeyBindsList.this).minecraft.screen.width / 2 - this.width / 2;
            Objects.requireNonNull((Object)((KeyBindsList)KeyBindsList.this).minecraft.font);
            font.draw($$0, this.name, f, (float)($$2 + $$5 - 9 - 1), 0xFFFFFF);
        }

        @Override
        public boolean changeFocus(boolean $$0) {
            return false;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of((Object)new NarratableEntry(){

                @Override
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput $$0) {
                    $$0.add(NarratedElementType.TITLE, CategoryEntry.this.name);
                }
            });
        }
    }

    public class KeyEntry
    extends Entry {
        private final KeyMapping key;
        private final Component name;
        private final Button changeButton;
        private final Button resetButton;

        KeyEntry(KeyMapping $$12, Component $$22) {
            this.key = $$12;
            this.name = $$22;
            this.changeButton = Button.builder($$22, $$1 -> {
                KeyBindsList.this.keyBindsScreen.selectedKey = $$12;
            }).bounds(0, 0, 75, 20).createNarration($$2 -> {
                if ($$12.isUnbound()) {
                    return Component.translatable("narrator.controls.unbound", $$22);
                }
                return Component.translatable("narrator.controls.bound", $$22, $$2.get());
            }).build();
            this.resetButton = Button.builder(Component.translatable("controls.reset"), $$1 -> {
                ((KeyBindsList)KeyBindsList.this).minecraft.options.setKey($$12, $$12.getDefaultKey());
                KeyMapping.resetMapping();
            }).bounds(0, 0, 50, 20).createNarration($$1 -> Component.translatable("narrator.controls.reset", $$22)).build();
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            boolean $$10 = KeyBindsList.this.keyBindsScreen.selectedKey == this.key;
            Font font = ((KeyBindsList)KeyBindsList.this).minecraft.font;
            float f = $$3 + 90 - KeyBindsList.this.maxNameWidth;
            int n = $$2 + $$5 / 2;
            Objects.requireNonNull((Object)((KeyBindsList)KeyBindsList.this).minecraft.font);
            font.draw($$0, this.name, f, (float)(n - 9 / 2), 0xFFFFFF);
            this.resetButton.setX($$3 + 190);
            this.resetButton.setY($$2);
            this.resetButton.active = !this.key.isDefault();
            this.resetButton.render($$0, $$6, $$7, $$9);
            this.changeButton.setX($$3 + 105);
            this.changeButton.setY($$2);
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            boolean $$11 = false;
            if (!this.key.isUnbound()) {
                for (KeyMapping $$12 : ((KeyBindsList)KeyBindsList.this).minecraft.options.keyMappings) {
                    if ($$12 == this.key || !this.key.same($$12)) continue;
                    $$11 = true;
                    break;
                }
            }
            if ($$10) {
                this.changeButton.setMessage(Component.literal("> ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            } else if ($$11) {
                this.changeButton.setMessage(this.changeButton.getMessage().copy().withStyle(ChatFormatting.RED));
            }
            this.changeButton.render($$0, $$6, $$7, $$9);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of((Object)this.changeButton, (Object)this.resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of((Object)this.changeButton, (Object)this.resetButton);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if (this.changeButton.mouseClicked($$0, $$1, $$2)) {
                return true;
            }
            return this.resetButton.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public boolean mouseReleased(double $$0, double $$1, int $$2) {
            return this.changeButton.mouseReleased($$0, $$1, $$2) || this.resetButton.mouseReleased($$0, $$1, $$2);
        }
    }

    public static abstract class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
    }
}