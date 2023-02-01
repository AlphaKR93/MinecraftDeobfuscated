/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetNormalWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectFileToUploadScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.ResettingGeneratedWorldTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsResetWorldScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private Component subtitle = Component.translatable("mco.reset.world.warning");
    private Component buttonTitle = CommonComponents.GUI_CANCEL;
    private int subtitleColor = 0xFF0000;
    private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/upload.png");
    private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/adventure.png");
    private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/survival_spawn.png");
    private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("realms", "textures/gui/realms/new_world.png");
    private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/experience.png");
    private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("realms", "textures/gui/realms/inspiration.png");
    WorldTemplatePaginatedList templates;
    WorldTemplatePaginatedList adventuremaps;
    WorldTemplatePaginatedList experiences;
    WorldTemplatePaginatedList inspirations;
    public int slot = -1;
    private Component resetTitle = Component.translatable("mco.reset.world.resetting.screen.title");
    private final Runnable resetWorldRunnable;
    private final Runnable callback;

    public RealmsResetWorldScreen(Screen $$0, RealmsServer $$1, Component $$2, Runnable $$3, Runnable $$4) {
        super($$2);
        this.lastScreen = $$0;
        this.serverData = $$1;
        this.resetWorldRunnable = $$3;
        this.callback = $$4;
    }

    public RealmsResetWorldScreen(Screen $$0, RealmsServer $$1, Runnable $$2, Runnable $$3) {
        this($$0, $$1, Component.translatable("mco.reset.world.title"), $$2, $$3);
    }

    public RealmsResetWorldScreen(Screen $$0, RealmsServer $$1, Component $$2, Component $$3, int $$4, Component $$5, Runnable $$6, Runnable $$7) {
        this($$0, $$1, $$2, $$6, $$7);
        this.subtitle = $$3;
        this.subtitleColor = $$4;
        this.buttonTitle = $$5;
    }

    public void setSlot(int $$0) {
        this.slot = $$0;
    }

    public void setResetTitle(Component $$0) {
        this.resetTitle = $$0;
    }

    @Override
    public void init() {
        this.addRenderableWidget(Button.builder(this.buttonTitle, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 40, RealmsResetWorldScreen.row(14) - 10, 80, 20).build());
        new Thread("Realms-reset-world-fetcher"){

            public void run() {
                RealmsClient $$0 = RealmsClient.create();
                try {
                    WorldTemplatePaginatedList $$1 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList $$2 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList $$3 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList $$4 = $$0.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsResetWorldScreen.this.minecraft.execute(() -> {
                        RealmsResetWorldScreen.this.templates = $$1;
                        RealmsResetWorldScreen.this.adventuremaps = $$2;
                        RealmsResetWorldScreen.this.experiences = $$3;
                        RealmsResetWorldScreen.this.inspirations = $$4;
                    });
                }
                catch (RealmsServiceException $$5) {
                    LOGGER.error("Couldn't fetch templates in reset world", (Throwable)$$5);
                }
            }
        }.start();
        this.addLabel(new RealmsLabel(this.subtitle, this.width / 2, 22, this.subtitleColor));
        this.addRenderableWidget(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(0) + 10, Component.translatable("mco.reset.world.generate"), NEW_WORLD_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsResetNormalWorldScreen((Consumer<WorldGenerationInfo>)((Consumer)this::generationSelectionCallback), this.title))));
        this.addRenderableWidget(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(0) + 10, Component.translatable("mco.reset.world.upload"), UPLOAD_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.serverData.id, this.slot != -1 ? this.slot : this.serverData.activeSlot, this, this.callback))));
        this.addRenderableWidget(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(0) + 10, Component.translatable("mco.reset.world.template"), SURVIVAL_SPAWN_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.reset.world.template"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.NORMAL, this.templates))));
        this.addRenderableWidget(new FrameButton(this.frame(1), RealmsResetWorldScreen.row(6) + 20, Component.translatable("mco.reset.world.adventure"), ADVENTURE_MAP_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.reset.world.adventure"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps))));
        this.addRenderableWidget(new FrameButton(this.frame(2), RealmsResetWorldScreen.row(6) + 20, Component.translatable("mco.reset.world.experience"), EXPERIENCE_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.reset.world.experience"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.EXPERIENCE, this.experiences))));
        this.addRenderableWidget(new FrameButton(this.frame(3), RealmsResetWorldScreen.row(6) + 20, Component.translatable("mco.reset.world.inspiration"), INSPIRATION_LOCATION, $$0 -> this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.reset.world.inspiration"), (Consumer<WorldTemplate>)((Consumer)this::templateSelectionCallback), RealmsServer.WorldType.INSPIRATION, this.inspirations))));
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private int frame(int $$0) {
        return this.width / 2 - 130 + ($$0 - 1) * 100;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsResetWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 7, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    void drawFrame(PoseStack $$0, int $$1, int $$2, Component $$3, ResourceLocation $$4, boolean $$5, boolean $$6) {
        RenderSystem.setShaderTexture(0, $$4);
        if ($$5) {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        GuiComponent.blit($$0, $$1 + 2, $$2 + 14, 0.0f, 0.0f, 56, 56, 56, 56);
        RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
        GuiComponent.blit($$0, $$1, $$2 + 12, 0.0f, 0.0f, 60, 60, 60, 60);
        int $$7 = $$5 ? 0xA0A0A0 : 0xFFFFFF;
        RealmsResetWorldScreen.drawCenteredString($$0, this.font, $$3, $$1 + 30, $$2, $$7);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void startTask(LongRunningTask $$0) {
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, $$0));
    }

    public void switchSlot(Runnable $$0) {
        this.startTask(new SwitchSlotTask(this.serverData.id, this.slot, () -> this.minecraft.execute($$0)));
    }

    private void templateSelectionCallback(@Nullable WorldTemplate $$0) {
        this.minecraft.setScreen(this);
        if ($$0 != null) {
            this.resetWorld(() -> this.startTask(new ResettingTemplateWorldTask($$0, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
        }
    }

    private void generationSelectionCallback(@Nullable WorldGenerationInfo $$0) {
        this.minecraft.setScreen(this);
        if ($$0 != null) {
            this.resetWorld(() -> this.startTask(new ResettingGeneratedWorldTask($$0, this.serverData.id, this.resetTitle, this.resetWorldRunnable)));
        }
    }

    private void resetWorld(Runnable $$0) {
        if (this.slot == -1) {
            $$0.run();
        } else {
            this.switchSlot($$0);
        }
    }

    class FrameButton
    extends Button {
        private final ResourceLocation image;

        public FrameButton(int $$0, int $$1, Component $$2, ResourceLocation $$3, Button.OnPress $$4) {
            super($$0, $$1, 60, 72, $$2, $$4, DEFAULT_NARRATION);
            this.image = $$3;
        }

        @Override
        public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
            RealmsResetWorldScreen.this.drawFrame($$0, this.getX(), this.getY(), this.getMessage(), this.image, this.isHoveredOrFocused(), this.isMouseOver($$1, $$2));
        }
    }
}