/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlags;

public class ConfirmExperimentalFeaturesScreen
extends Screen {
    private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
    private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
    private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
    private static final int MARGIN = 20;
    private final BooleanConsumer callback;
    final Collection<Pack> enabledPacks;
    private MultiLineLabel multilineMessage = MultiLineLabel.EMPTY;

    public ConfirmExperimentalFeaturesScreen(Collection<Pack> $$0, BooleanConsumer $$1) {
        super(TITLE);
        this.enabledPacks = $$0;
        this.callback = $$1;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
    }

    private int messageHeight() {
        int n = this.multilineMessage.getLineCount();
        Objects.requireNonNull((Object)this.font);
        return n * 9;
    }

    private int titleTop() {
        int $$0 = (this.height - this.messageHeight()) / 2;
        Objects.requireNonNull((Object)this.font);
        return Mth.clamp($$0 - 20 - 9, 10, 80);
    }

    @Override
    protected void init() {
        super.init();
        this.multilineMessage = MultiLineLabel.create(this.font, (FormattedText)MESSAGE, this.width - 50);
        int $$02 = Mth.clamp(this.titleTop() + 20 + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 50 - 105, $$02, 100, 20).build());
        this.addRenderableWidget(Button.builder(DETAILS_BUTTON, $$0 -> this.minecraft.setScreen(new DetailsScreen())).bounds(this.width / 2 - 50, $$02, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 50 + 105, $$02, 100, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        ConfirmExperimentalFeaturesScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, this.titleTop(), 0xFFFFFF);
        this.multilineMessage.renderCentered($$0, this.width / 2, this.titleTop() + 20);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    class DetailsScreen
    extends Screen {
        private PackList packList;

        DetailsScreen() {
            super(Component.translatable("selectWorld.experimental.details.title"));
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
        }

        @Override
        protected void init() {
            super.init();
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
            this.packList = new PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks);
            this.addWidget(this.packList);
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
            this.renderBackground($$0);
            this.packList.render($$0, $$1, $$2, $$3);
            DetailsScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 10, 0xFFFFFF);
            super.render($$0, $$1, $$2, $$3);
        }

        class PackList
        extends ObjectSelectionList<PackListEntry> {
            public PackList(Minecraft $$0, Collection<Pack> $$1) {
                int n = DetailsScreen.this.width;
                int n2 = DetailsScreen.this.height;
                int n3 = DetailsScreen.this.height - 64;
                Objects.requireNonNull((Object)$$0.font);
                super($$0, n, n2, 32, n3, (9 + 2) * 3);
                for (Pack $$2 : $$1) {
                    String $$3 = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, $$2.getRequestedFeatures());
                    if ($$3.isEmpty()) continue;
                    MutableComponent $$4 = ComponentUtils.mergeStyles($$2.getTitle().copy(), Style.EMPTY.withBold(true));
                    MutableComponent $$5 = Component.translatable("selectWorld.experimental.details.entry", $$3);
                    this.addEntry(new PackListEntry($$4, $$5, MultiLineLabel.create(DetailsScreen.this.font, (FormattedText)$$5, this.getRowWidth())));
                }
            }

            @Override
            public int getRowWidth() {
                return this.width * 3 / 4;
            }
        }

        class PackListEntry
        extends ObjectSelectionList.Entry<PackListEntry> {
            private final Component packId;
            private final Component message;
            private final MultiLineLabel splitMessage;

            PackListEntry(Component $$0, Component $$1, MultiLineLabel $$2) {
                this.packId = $$0;
                this.message = $$1;
                this.splitMessage = $$2;
            }

            @Override
            public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                GuiComponent.drawString($$0, ((DetailsScreen)DetailsScreen.this).minecraft.font, this.packId, $$3, $$2, 0xFFFFFF);
                Objects.requireNonNull((Object)DetailsScreen.this.font);
                this.splitMessage.renderLeftAligned($$0, $$3, $$2 + 12, 9, 0xFFFFFF);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
            }
        }
    }
}