/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ChatOptionsScreen
extends SimpleOptionsSubScreen {
    public ChatOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.chat.title"), new OptionInstance[]{$$1.chatVisibility(), $$1.chatColors(), $$1.chatLinks(), $$1.chatLinksPrompt(), $$1.chatOpacity(), $$1.textBackgroundOpacity(), $$1.chatScale(), $$1.chatLineSpacing(), $$1.chatDelay(), $$1.chatWidth(), $$1.chatHeightFocused(), $$1.chatHeightUnfocused(), $$1.narrator(), $$1.autoSuggestions(), $$1.hideMatchedNames(), $$1.reducedDebugInfo(), $$1.onlyShowSecureChat()});
    }
}