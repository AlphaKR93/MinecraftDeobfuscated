/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CommonButtons {
    public static TextAndImageButton languageTextAndImage(Minecraft $$0, Screen $$1) {
        return TextAndImageButton.builder(Component.translatable("options.language"), Button.WIDGETS_LOCATION, $$2 -> $$0.setScreen(new LanguageSelectScreen($$1, $$0.options, $$0.getLanguageManager()))).texStart(4, 110).offset(65, 3).yDiffTex(20).usedTextureSize(13, 13).textureSize(256, 256).build();
    }

    public static TextAndImageButton accessibilityTextAndImage(Minecraft $$0, Screen $$1) {
        return TextAndImageButton.builder(Component.translatable("options.accessibility.title"), Button.ACCESSIBILITY_TEXTURE, $$2 -> $$0.setScreen(new AccessibilityOptionsScreen($$1, $$0.options))).texStart(3, 3).offset(65, 3).yDiffTex(20).usedTextureSize(15, 15).textureSize(32, 64).build();
    }
}