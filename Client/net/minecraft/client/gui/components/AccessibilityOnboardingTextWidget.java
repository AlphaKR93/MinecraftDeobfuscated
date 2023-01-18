/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class AccessibilityOnboardingTextWidget
extends MultiLineTextWidget {
    private final Component message;
    private static final int BORDER_COLOR_FOCUSED = -1;
    private static final int BORDER_COLOR = -6250336;
    private static final int BACKGROUND_COLOR = 0x55000000;
    private static final int PADDING = 3;
    private static final int BORDER = 1;

    public AccessibilityOnboardingTextWidget(Font $$0, Component $$1, int $$2) {
        super(MultiLineLabel.create($$0, (FormattedText)$$1, $$2), $$0, $$1, true);
        this.message = $$1;
        this.active = true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, this.message);
    }

    @Override
    protected void renderBg(PoseStack $$0, Minecraft $$1, int $$2, int $$3) {
        int $$4 = this.getX() - 3;
        int $$5 = this.getY() - 3;
        int $$6 = this.getX() + this.width + 3;
        int $$7 = this.getY() + this.height + 3;
        int $$8 = this.isFocused() ? -1 : -6250336;
        AccessibilityOnboardingTextWidget.fill($$0, $$4 - 1, $$5 - 1, $$4, $$7 + 1, $$8);
        AccessibilityOnboardingTextWidget.fill($$0, $$6, $$5 - 1, $$6 + 1, $$7 + 1, $$8);
        AccessibilityOnboardingTextWidget.fill($$0, $$4, $$5, $$6, $$5 - 1, $$8);
        AccessibilityOnboardingTextWidget.fill($$0, $$4, $$7, $$6, $$7 + 1, $$8);
        AccessibilityOnboardingTextWidget.fill($$0, $$4, $$5, $$6, $$7, 0x55000000);
        super.renderBg($$0, $$1, $$2, $$3);
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBg($$0, Minecraft.getInstance(), $$1, $$2);
        super.renderButton($$0, $$1, $$2, $$3);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
    }
}