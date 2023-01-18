/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.text2speech.Narrator
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AccessibilityOnboardingTextWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AccessibilityOnboardingScreen
extends Screen {
    private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
    private static final int PADDING = 4;
    private static final int TITLE_PADDING = 16;
    private final PanoramaRenderer panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
    private final LogoRenderer logoRenderer;
    private final Options options;
    private boolean hasNarrated;
    private float timer;
    @Nullable
    private AccessibilityOnboardingTextWidget textWidget;

    public AccessibilityOnboardingScreen(Options $$0) {
        super(Component.translatable("accessibility.onboarding.screen.title"));
        this.options = $$0;
        this.logoRenderer = new LogoRenderer(true);
    }

    @Override
    public void init() {
        FrameLayout $$02 = new FrameLayout();
        $$02.defaultChildLayoutSetting().alignVerticallyTop().padding(4);
        $$02.setMinDimensions(this.width, this.height - this.initTitleYPos());
        GridLayout $$1 = $$02.addChild(new GridLayout());
        $$1.defaultCellSetting().alignHorizontallyCenter().padding(4);
        GridLayout.RowHelper $$2 = $$1.createRowHelper(1);
        this.textWidget = new AccessibilityOnboardingTextWidget(this.font, this.title, this.width);
        $$2.addChild(this.textWidget, $$2.newCellSettings().padding(16));
        AbstractWidget $$3 = this.options.narrator().createButton(this.options, 0, 0, 150);
        $$2.addChild($$3);
        this.setInitialFocus($$3);
        $$2.addChild(Button.builder(Component.translatable("options.accessibility.title"), $$0 -> this.minecraft.setScreen(new AccessibilityOptionsScreen(new TitleScreen(true), this.minecraft.options))).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_CONTINUE, $$0 -> this.minecraft.setScreen(new TitleScreen(true, this.logoRenderer))).build(), $$02.newChildLayoutSettings().alignVerticallyBottom().padding(8));
        $$02.arrangeElements();
        FrameLayout.alignInRectangle($$02, 0, this.initTitleYPos(), this.width, this.height, 0.5f, 0.0f);
        $$02.visitWidgets((Consumer<AbstractWidget>)((Consumer)this::addRenderableWidget));
    }

    private int initTitleYPos() {
        return 90;
    }

    @Override
    public void onClose() {
        this.minecraft.getNarrator().clear();
        this.minecraft.setScreen(new TitleScreen(true, this.logoRenderer));
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.handleInitialNarrationDelay();
        this.panorama.render(0.0f, 1.0f);
        AccessibilityOnboardingScreen.fill($$0, 0, 0, this.width, this.height, -1877995504);
        this.logoRenderer.renderLogo($$0, this.width, 1.0f);
        if (this.textWidget != null) {
            this.textWidget.render($$0, $$1, $$2, $$3);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void handleInitialNarrationDelay() {
        if (!this.hasNarrated) {
            if (this.timer < 40.0f) {
                this.timer += 1.0f;
            } else {
                Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
                this.hasNarrated = true;
            }
        }
    }
}