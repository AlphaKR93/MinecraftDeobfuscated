/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class DeathScreen
extends Screen {
    private int delayTicker;
    private final Component causeOfDeath;
    private final boolean hardcore;
    private Component deathScore;
    private final List<Button> exitButtons = Lists.newArrayList();
    @Nullable
    private Button exitToTitleButton;

    public DeathScreen(@Nullable Component $$0, boolean $$1) {
        super(Component.translatable($$1 ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.causeOfDeath = $$0;
        this.hardcore = $$1;
    }

    @Override
    protected void init() {
        this.delayTicker = 0;
        this.exitButtons.clear();
        MutableComponent $$02 = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
        this.exitButtons.add((Object)this.addRenderableWidget(Button.builder($$02, $$0 -> {
            this.minecraft.player.respawn();
            this.minecraft.setScreen(null);
        }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
        this.exitToTitleButton = this.addRenderableWidget(Button.builder(Component.translatable("deathScreen.titleScreen"), $$0 -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::handleExitToTitleScreen, true)).bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
        this.exitButtons.add((Object)this.exitToTitleButton);
        for (Button $$1 : this.exitButtons) {
            $$1.active = false;
        }
        this.deathScore = Component.translatable("deathScreen.score").append(": ").append(Component.literal(Integer.toString((int)this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void handleExitToTitleScreen() {
        if (this.hardcore) {
            this.exitToTitleScreen();
            return;
        }
        ConfirmScreen $$02 = new ConfirmScreen($$0 -> {
            if ($$0) {
                this.exitToTitleScreen();
            } else {
                this.minecraft.player.respawn();
                this.minecraft.setScreen(null);
            }
        }, Component.translatable("deathScreen.quit.confirm"), CommonComponents.EMPTY, Component.translatable("deathScreen.titleScreen"), Component.translatable("deathScreen.respawn"));
        this.minecraft.setScreen($$02);
        $$02.setDelay(20);
    }

    private void exitToTitleScreen() {
        if (this.minecraft.level != null) {
            this.minecraft.level.disconnect();
        }
        this.minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        this.minecraft.setScreen(new TitleScreen());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.fillGradient($$0, 0, 0, this.width, this.height, 0x60500000, -1602211792);
        $$0.pushPose();
        $$0.scale(2.0f, 2.0f, 2.0f);
        DeathScreen.drawCenteredString($$0, this.font, this.title, this.width / 2 / 2, 30, 0xFFFFFF);
        $$0.popPose();
        if (this.causeOfDeath != null) {
            DeathScreen.drawCenteredString($$0, this.font, this.causeOfDeath, this.width / 2, 85, 0xFFFFFF);
        }
        DeathScreen.drawCenteredString($$0, this.font, this.deathScore, this.width / 2, 100, 0xFFFFFF);
        if (this.causeOfDeath != null && $$2 > 85) {
            Objects.requireNonNull((Object)this.font);
            if ($$2 < 85 + 9) {
                Style $$4 = this.getClickedComponentStyleAt($$1);
                this.renderComponentHoverEffect($$0, $$4, $$1, $$2);
            }
        }
        super.render($$0, $$1, $$2, $$3);
        if (this.exitToTitleButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
            RenderSystem.setShaderTexture(0, AbstractWidget.WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit($$0, this.exitToTitleButton.getX() + this.exitToTitleButton.getWidth() - 17, this.exitToTitleButton.getY() + 3, 182, 24, 15, 15);
        }
    }

    @Nullable
    private Style getClickedComponentStyleAt(int $$0) {
        if (this.causeOfDeath == null) {
            return null;
        }
        int $$1 = this.minecraft.font.width(this.causeOfDeath);
        int $$2 = this.width / 2 - $$1 / 2;
        int $$3 = this.width / 2 + $$1 / 2;
        if ($$0 < $$2 || $$0 > $$3) {
            return null;
        }
        return this.minecraft.font.getSplitter().componentStyleAtWidth(this.causeOfDeath, $$0 - $$2);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (this.causeOfDeath != null && $$1 > 85.0) {
            Style $$3;
            Objects.requireNonNull((Object)this.font);
            if ($$1 < (double)(85 + 9) && ($$3 = this.getClickedComponentStyleAt((int)$$0)) != null && $$3.getClickEvent() != null && $$3.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleComponentClicked($$3);
                return false;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.delayTicker;
        if (this.delayTicker == 20) {
            for (Button $$0 : this.exitButtons) {
                $$0.active = true;
            }
        }
    }
}