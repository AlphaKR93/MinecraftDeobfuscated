/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LanguageSelectScreen
extends OptionsSubScreen {
    private static final Component WARNING_LABEL = Component.literal("(").append(Component.translatable("options.languageWarning")).append(")").withStyle(ChatFormatting.GRAY);
    private LanguageSelectionList packSelectionList;
    final LanguageManager languageManager;

    public LanguageSelectScreen(Screen $$0, Options $$1, LanguageManager $$2) {
        super($$0, $$1, Component.translatable("options.language"));
        this.languageManager = $$2;
    }

    @Override
    protected void init() {
        this.packSelectionList = new LanguageSelectionList(this.minecraft);
        this.addWidget(this.packSelectionList);
        this.addRenderableWidget(this.options.forceUnicodeFont().createButton(this.options, this.width / 2 - 155, this.height - 38, 150));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            LanguageSelectionList.Entry $$1 = (LanguageSelectionList.Entry)this.packSelectionList.getSelected();
            if ($$1 != null && !$$1.code.equals((Object)this.languageManager.getSelected())) {
                this.languageManager.setSelected($$1.code);
                this.options.languageCode = $$1.code;
                this.minecraft.reloadResourcePacks();
                this.options.save();
            }
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build());
        super.init();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.packSelectionList.render($$0, $$1, $$2, $$3);
        LanguageSelectScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        LanguageSelectScreen.drawCenteredString($$0, this.font, WARNING_LABEL, this.width / 2, this.height - 56, 0x808080);
        super.render($$0, $$1, $$2, $$3);
    }

    class LanguageSelectionList
    extends ObjectSelectionList<Entry> {
        public LanguageSelectionList(Minecraft $$0) {
            super($$0, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);
            String $$12 = LanguageSelectScreen.this.languageManager.getSelected();
            LanguageSelectScreen.this.languageManager.getLanguages().forEach(($$1, $$2) -> {
                Entry $$3 = new Entry((String)$$1, (LanguageInfo)((Object)$$2));
                this.addEntry($$3);
                if ($$12.equals($$1)) {
                    this.setSelected($$3);
                }
            });
            if (this.getSelected() != null) {
                this.centerScrollOn((Entry)this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        protected void renderBackground(PoseStack $$0) {
            LanguageSelectScreen.this.renderBackground($$0);
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final String code;
            private final Component language;

            public Entry(String $$1, LanguageInfo $$2) {
                this.code = $$1;
                this.language = $$2.toComponent();
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                LanguageSelectScreen.this.font.drawShadow($$0, this.language, (float)(LanguageSelectionList.this.width / 2 - LanguageSelectScreen.this.font.width(this.language) / 2), (float)($$2 + 1), 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                if ($$2 == 0) {
                    this.select();
                    return true;
                }
                return false;
            }

            private void select() {
                LanguageSelectionList.this.setSelected(this);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", this.language);
            }
        }
    }
}