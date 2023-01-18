/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation LINK_ICON = new ResourceLocation("realms", "textures/gui/realms/link_icons.png");
    static final ResourceLocation TRAILER_ICON = new ResourceLocation("realms", "textures/gui/realms/trailer_icons.png");
    static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    static final Component PUBLISHER_LINK_TOOLTIP = Component.translatable("mco.template.info.tooltip");
    static final Component TRAILER_LINK_TOOLTIP = Component.translatable("mco.template.trailer.tooltip");
    private final Consumer<WorldTemplate> callback;
    WorldTemplateObjectSelectionList worldTemplateObjectSelectionList;
    int selectedTemplate = -1;
    private Button selectButton;
    private Button trailerButton;
    private Button publisherButton;
    @Nullable
    Component toolTip;
    @Nullable
    String currentLink;
    private final RealmsServer.WorldType worldType;
    int clicks;
    @Nullable
    private Component[] warning;
    private String warningURL;
    boolean displayWarning;
    private boolean hoverWarning;
    @Nullable
    List<TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(Component $$0, Consumer<WorldTemplate> $$1, RealmsServer.WorldType $$2) {
        this($$0, $$1, $$2, null);
    }

    public RealmsSelectWorldTemplateScreen(Component $$0, Consumer<WorldTemplate> $$1, RealmsServer.WorldType $$2, @Nullable WorldTemplatePaginatedList $$3) {
        super($$0);
        this.callback = $$1;
        this.worldType = $$2;
        if ($$3 == null) {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList();
            this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
        } else {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList((Iterable<WorldTemplate>)Lists.newArrayList($$3.templates));
            this.fetchTemplatesAsync($$3);
        }
    }

    public void setWarning(Component ... $$0) {
        this.warning = $$0;
        this.displayWarning = true;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.hoverWarning && this.warningURL != null) {
            Util.getPlatform().openUri("https://www.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public void init() {
        this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList((Iterable<WorldTemplate>)this.worldTemplateObjectSelectionList.getTemplates());
        this.trailerButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.trailer"), $$0 -> this.onTrailer()).bounds(this.width / 2 - 206, this.height - 32, 100, 20).build());
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.select"), $$0 -> this.selectTemplate()).bounds(this.width / 2 - 100, this.height - 32, 100, 20).build());
        Component $$02 = this.worldType == RealmsServer.WorldType.MINIGAME ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_BACK;
        Button $$1 = Button.builder($$02, $$0 -> this.onClose()).bounds(this.width / 2 + 6, this.height - 32, 100, 20).build();
        this.addRenderableWidget($$1);
        this.publisherButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.publisher"), $$0 -> this.onPublish()).bounds(this.width / 2 + 112, this.height - 32, 100, 20).build());
        this.selectButton.active = false;
        this.trailerButton.visible = false;
        this.publisherButton.visible = false;
        this.addWidget(this.worldTemplateObjectSelectionList);
        this.magicalSpecialHackyFocus(this.worldTemplateObjectSelectionList);
    }

    @Override
    public Component getNarrationMessage() {
        ArrayList $$0 = Lists.newArrayListWithCapacity((int)2);
        if (this.title != null) {
            $$0.add((Object)this.title);
        }
        if (this.warning != null) {
            $$0.addAll((Collection)Arrays.asList((Object[])this.warning));
        }
        return CommonComponents.joinLines((Collection<? extends Component>)$$0);
    }

    void updateButtonStates() {
        this.publisherButton.visible = this.shouldPublisherBeVisible();
        this.trailerButton.visible = this.shouldTrailerBeVisible();
        this.selectButton.active = this.shouldSelectButtonBeActive();
    }

    private boolean shouldSelectButtonBeActive() {
        return this.selectedTemplate != -1;
    }

    private boolean shouldPublisherBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
    }

    private WorldTemplate getSelectedTemplate() {
        return this.worldTemplateObjectSelectionList.get(this.selectedTemplate);
    }

    private boolean shouldTrailerBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public void onClose() {
        this.callback.accept(null);
    }

    void selectTemplate() {
        if (this.hasValidTemplate()) {
            this.callback.accept((Object)this.getSelectedTemplate());
        }
    }

    private boolean hasValidTemplate() {
        return this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount();
    }

    private void onTrailer() {
        if (this.hasValidTemplate()) {
            WorldTemplate $$0 = this.getSelectedTemplate();
            if (!"".equals((Object)$$0.trailer)) {
                Util.getPlatform().openUri($$0.trailer);
            }
        }
    }

    private void onPublish() {
        if (this.hasValidTemplate()) {
            WorldTemplate $$0 = this.getSelectedTemplate();
            if (!"".equals((Object)$$0.link)) {
                Util.getPlatform().openUri($$0.link);
            }
        }
    }

    private void fetchTemplatesAsync(final WorldTemplatePaginatedList $$0) {
        new Thread("realms-template-fetcher"){

            public void run() {
                WorldTemplatePaginatedList $$02 = $$0;
                RealmsClient $$1 = RealmsClient.create();
                while ($$02 != null) {
                    Either<WorldTemplatePaginatedList, String> $$2 = RealmsSelectWorldTemplateScreen.this.fetchTemplates($$02, $$1);
                    $$02 = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
                        if ($$2.right().isPresent()) {
                            LOGGER.error("Couldn't fetch templates: {}", $$2.right().get());
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.get("mco.template.select.failure", new Object[0]), new TextRenderingUtils.LineSegment[0]);
                            }
                            return null;
                        }
                        WorldTemplatePaginatedList $$1 = (WorldTemplatePaginatedList)$$2.left().get();
                        for (WorldTemplate $$2 : $$1.templates) {
                            RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry($$2);
                        }
                        if ($$1.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                                String $$3 = I18n.get("mco.template.select.none", "%link");
                                TextRenderingUtils.LineSegment $$4 = TextRenderingUtils.LineSegment.link(I18n.get("mco.template.select.none.linkTitle", new Object[0]), "https://aka.ms/MinecraftRealmsContentCreator");
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose($$3, $$4);
                            }
                            return null;
                        }
                        return $$1;
                    }).join();
                }
            }
        }.start();
    }

    Either<WorldTemplatePaginatedList, String> fetchTemplates(WorldTemplatePaginatedList $$0, RealmsClient $$1) {
        try {
            return Either.left((Object)$$1.fetchWorldTemplates($$0.page + 1, $$0.size, this.worldType));
        }
        catch (RealmsServiceException $$2) {
            return Either.right((Object)$$2.getMessage());
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.currentLink = null;
        this.hoverWarning = false;
        this.renderBackground($$0);
        this.worldTemplateObjectSelectionList.render($$0, $$1, $$2, $$3);
        if (this.noTemplatesMessage != null) {
            this.renderMultilineMessage($$0, $$1, $$2, this.noTemplatesMessage);
        }
        RealmsSelectWorldTemplateScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 13, 0xFFFFFF);
        if (this.displayWarning) {
            Component[] $$4 = this.warning;
            for (int $$5 = 0; $$5 < $$4.length; ++$$5) {
                int $$6 = this.font.width($$4[$$5]);
                int $$7 = this.width / 2 - $$6 / 2;
                int $$8 = RealmsSelectWorldTemplateScreen.row(-1 + $$5);
                if ($$1 < $$7 || $$1 > $$7 + $$6 || $$2 < $$8) continue;
                Objects.requireNonNull((Object)this.font);
                if ($$2 > $$8 + 9) continue;
                this.hoverWarning = true;
            }
            for (int $$9 = 0; $$9 < $$4.length; ++$$9) {
                Component $$10 = $$4[$$9];
                int $$11 = 0xA0A0A0;
                if (this.warningURL != null) {
                    if (this.hoverWarning) {
                        $$11 = 7107012;
                        $$10 = $$10.copy().withStyle(ChatFormatting.STRIKETHROUGH);
                    } else {
                        $$11 = 0x3366BB;
                    }
                }
                RealmsSelectWorldTemplateScreen.drawCenteredString($$0, this.font, $$10, this.width / 2, RealmsSelectWorldTemplateScreen.row(-1 + $$9), $$11);
            }
        }
        super.render($$0, $$1, $$2, $$3);
        this.renderMousehoverTooltip($$0, this.toolTip, $$1, $$2);
    }

    private void renderMultilineMessage(PoseStack $$02, int $$1, int $$2, List<TextRenderingUtils.Line> $$3) {
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            TextRenderingUtils.Line $$5 = (TextRenderingUtils.Line)$$3.get($$4);
            int $$6 = RealmsSelectWorldTemplateScreen.row(4 + $$4);
            int $$7 = $$5.segments.stream().mapToInt($$0 -> this.font.width($$0.renderedText())).sum();
            int $$8 = this.width / 2 - $$7 / 2;
            for (TextRenderingUtils.LineSegment $$9 : $$5.segments) {
                int $$10 = $$9.isLink() ? 0x3366BB : 0xFFFFFF;
                int $$11 = this.font.drawShadow($$02, $$9.renderedText(), (float)$$8, (float)$$6, $$10);
                if ($$9.isLink() && $$1 > $$8 && $$1 < $$11 && $$2 > $$6 - 3 && $$2 < $$6 + 8) {
                    this.toolTip = Component.literal($$9.getLinkUrl());
                    this.currentLink = $$9.getLinkUrl();
                }
                $$8 = $$11;
            }
        }
    }

    protected void renderMousehoverTooltip(PoseStack $$0, @Nullable Component $$1, int $$2, int $$3) {
        if ($$1 == null) {
            return;
        }
        int $$4 = $$2 + 12;
        int $$5 = $$3 - 12;
        int $$6 = this.font.width($$1);
        this.fillGradient($$0, $$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
        this.font.drawShadow($$0, $$1, (float)$$4, (float)$$5, 0xFFFFFF);
    }

    class WorldTemplateObjectSelectionList
    extends RealmsObjectSelectionList<Entry> {
        public WorldTemplateObjectSelectionList() {
            this((Iterable<WorldTemplate>)Collections.emptyList());
        }

        public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> $$0) {
            super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
            $$0.forEach(this::addEntry);
        }

        public void addEntry(WorldTemplate $$0) {
            this.addEntry(new Entry($$0));
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            if ($$2 == 0 && $$1 >= (double)this.y0 && $$1 <= (double)this.y1) {
                int $$3 = this.width / 2 - 150;
                if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                    Util.getPlatform().openUri(RealmsSelectWorldTemplateScreen.this.currentLink);
                }
                int $$4 = (int)Math.floor((double)($$1 - (double)this.y0)) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int $$5 = $$4 / this.itemHeight;
                if ($$0 >= (double)$$3 && $$0 < (double)this.getScrollbarPosition() && $$5 >= 0 && $$4 >= 0 && $$5 < this.getItemCount()) {
                    this.selectItem($$5);
                    this.itemClicked($$4, $$5, $$0, $$1, this.width);
                    if ($$5 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                        return super.mouseClicked($$0, $$1, $$2);
                    }
                    RealmsSelectWorldTemplateScreen.this.clicks += 7;
                    if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                        RealmsSelectWorldTemplateScreen.this.selectTemplate();
                    }
                    return true;
                }
            }
            return super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf((Object)$$0);
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        @Override
        public void renderBackground(PoseStack $$0) {
            RealmsSelectWorldTemplateScreen.this.renderBackground($$0);
        }

        public boolean isEmpty() {
            return this.getItemCount() == 0;
        }

        public WorldTemplate get(int $$0) {
            return ((Entry)this.children().get((int)$$0)).template;
        }

        public List<WorldTemplate> getTemplates() {
            return (List)this.children().stream().map($$0 -> $$0.template).collect(Collectors.toList());
        }
    }

    class Entry
    extends ObjectSelectionList.Entry<Entry> {
        final WorldTemplate template;

        public Entry(WorldTemplate $$0) {
            this.template = $$0;
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.renderWorldTemplateItem($$0, this.template, $$3, $$2, $$6, $$7);
        }

        private void renderWorldTemplateItem(PoseStack $$0, WorldTemplate $$1, int $$2, int $$3, int $$4, int $$5) {
            int $$6 = $$2 + 45 + 20;
            RealmsSelectWorldTemplateScreen.this.font.draw($$0, $$1.name, (float)$$6, (float)($$3 + 2), 0xFFFFFF);
            RealmsSelectWorldTemplateScreen.this.font.draw($$0, $$1.author, (float)$$6, (float)($$3 + 15), 0x6C6C6C);
            RealmsSelectWorldTemplateScreen.this.font.draw($$0, $$1.version, (float)($$6 + 227 - RealmsSelectWorldTemplateScreen.this.font.width($$1.version)), (float)($$3 + 1), 0x6C6C6C);
            if (!("".equals((Object)$$1.link) && "".equals((Object)$$1.trailer) && "".equals((Object)$$1.recommendedPlayers))) {
                this.drawIcons($$0, $$6 - 1, $$3 + 25, $$4, $$5, $$1.link, $$1.trailer, $$1.recommendedPlayers);
            }
            this.drawImage($$0, $$2, $$3 + 1, $$4, $$5, $$1);
        }

        private void drawImage(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, WorldTemplate $$5) {
            RealmsTextureManager.bindWorldTemplate($$5.id, $$5.image);
            GuiComponent.blit($$0, $$1 + 1, $$2 + 1, 0.0f, 0.0f, 38, 38, 38, 38);
            RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
            GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 40, 40, 40, 40);
        }

        private void drawIcons(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, String $$5, String $$6, String $$7) {
            if (!"".equals((Object)$$7)) {
                RealmsSelectWorldTemplateScreen.this.font.draw($$0, $$7, (float)$$1, (float)($$2 + 4), 0x4C4C4C);
            }
            int $$8 = "".equals((Object)$$7) ? 0 : RealmsSelectWorldTemplateScreen.this.font.width($$7) + 2;
            boolean $$9 = false;
            boolean $$10 = false;
            boolean $$11 = "".equals((Object)$$5);
            if ($$3 >= $$1 + $$8 && $$3 <= $$1 + $$8 + 32 && $$4 >= $$2 && $$4 <= $$2 + 15 && $$4 < RealmsSelectWorldTemplateScreen.this.height - 15 && $$4 > 32) {
                if ($$3 <= $$1 + 15 + $$8 && $$3 > $$8) {
                    if ($$11) {
                        $$10 = true;
                    } else {
                        $$9 = true;
                    }
                } else if (!$$11) {
                    $$10 = true;
                }
            }
            if (!$$11) {
                RenderSystem.setShaderTexture(0, LINK_ICON);
                float $$12 = $$9 ? 15.0f : 0.0f;
                GuiComponent.blit($$0, $$1 + $$8, $$2, $$12, 0.0f, 15, 15, 30, 15);
            }
            if (!"".equals((Object)$$6)) {
                RenderSystem.setShaderTexture(0, TRAILER_ICON);
                int $$13 = $$1 + $$8 + ($$11 ? 0 : 17);
                float $$14 = $$10 ? 15.0f : 0.0f;
                GuiComponent.blit($$0, $$13, $$2, $$14, 0.0f, 15, 15, 30, 15);
            }
            if ($$9) {
                RealmsSelectWorldTemplateScreen.this.toolTip = PUBLISHER_LINK_TOOLTIP;
                RealmsSelectWorldTemplateScreen.this.currentLink = $$5;
            } else if ($$10 && !"".equals((Object)$$6)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = TRAILER_LINK_TOOLTIP;
                RealmsSelectWorldTemplateScreen.this.currentLink = $$6;
            }
        }

        @Override
        public Component getNarration() {
            Component $$0 = CommonComponents.joinLines(Component.literal(this.template.name), Component.translatable("mco.template.select.narrate.authors", this.template.author), Component.literal(this.template.recommendedPlayers), Component.translatable("mco.template.select.narrate.version", this.template.version));
            return Component.translatable("narrator.select", $$0);
        }
    }
}