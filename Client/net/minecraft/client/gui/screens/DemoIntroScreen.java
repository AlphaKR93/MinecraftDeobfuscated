/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class DemoIntroScreen
extends Screen {
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    private MultiLineLabel movementMessage = MultiLineLabel.EMPTY;
    private MultiLineLabel durationMessage = MultiLineLabel.EMPTY;

    public DemoIntroScreen() {
        super(Component.translatable("demo.help.title"));
    }

    @Override
    protected void init() {
        int $$02 = -16;
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), $$0 -> {
            $$0.active = false;
            Util.getPlatform().openUri("https://aka.ms/BuyMinecraftJava");
        }).bounds(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), $$0 -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }).bounds(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
        Options $$1 = this.minecraft.options;
        this.movementMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.movementShort", $$1.keyUp.getTranslatedKeyMessage(), $$1.keyLeft.getTranslatedKeyMessage(), $$1.keyDown.getTranslatedKeyMessage(), $$1.keyRight.getTranslatedKeyMessage()), Component.translatable("demo.help.movementMouse"), Component.translatable("demo.help.jump", $$1.keyJump.getTranslatedKeyMessage()), Component.translatable("demo.help.inventory", $$1.keyInventory.getTranslatedKeyMessage()));
        this.durationMessage = MultiLineLabel.create(this.font, (FormattedText)Component.translatable("demo.help.fullWrapped"), 218);
    }

    @Override
    public void renderBackground(PoseStack $$0) {
        super.renderBackground($$0);
        RenderSystem.setShaderTexture(0, DEMO_BACKGROUND_LOCATION);
        int $$1 = (this.width - 248) / 2;
        int $$2 = (this.height - 166) / 2;
        this.blit($$0, $$1, $$2, 0, 0, 248, 166);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        int $$4 = (this.width - 248) / 2 + 10;
        int $$5 = (this.height - 166) / 2 + 8;
        this.font.draw($$0, this.title, (float)$$4, (float)$$5, 0x1F1F1F);
        $$5 = this.movementMessage.renderLeftAlignedNoShadow($$0, $$4, $$5 + 12, 12, 0x4F4F4F);
        Objects.requireNonNull((Object)this.font);
        this.durationMessage.renderLeftAlignedNoShadow($$0, $$4, $$5 + 20, 9, 0x1F1F1F);
        super.render($$0, $$1, $$2, $$3);
    }
}