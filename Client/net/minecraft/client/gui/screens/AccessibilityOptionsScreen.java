/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOptionsScreen
extends SimpleOptionsSubScreen {
    private static final String GUIDE_LINK = "https://aka.ms/MinecraftJavaAccessibility";

    private static OptionInstance<?>[] options(Options $$0) {
        return new OptionInstance[]{$$0.narrator(), $$0.showSubtitles(), $$0.textBackgroundOpacity(), $$0.backgroundForChatOnly(), $$0.chatOpacity(), $$0.chatLineSpacing(), $$0.chatDelay(), $$0.notificationDisplayTime(), $$0.toggleCrouch(), $$0.toggleSprint(), $$0.screenEffectScale(), $$0.fovEffectScale(), $$0.darknessEffectScale(), $$0.hideLightningFlash(), $$0.autoJump(), $$0.panoramaSpeed(), $$0.darkMojangStudiosBackground()};
    }

    public AccessibilityOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.accessibility.title"), AccessibilityOptionsScreen.options($$1));
    }

    @Override
    protected void createFooter() {
        this.addRenderableWidget(Button.builder(Component.translatable("options.accessibility.link"), $$02 -> this.minecraft.setScreen(new ConfirmLinkScreen($$0 -> {
            if ($$0) {
                Util.getPlatform().openUri(GUIDE_LINK);
            }
            this.minecraft.setScreen(this);
        }, GUIDE_LINK, true))).bounds(this.width / 2 - 155, this.height - 27, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 5, this.height - 27, 150, 20).build());
    }
}