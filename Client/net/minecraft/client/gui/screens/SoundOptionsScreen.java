/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen
extends OptionsSubScreen {
    private OptionsList list;

    private static OptionInstance<?>[] buttonOptions(Options $$0) {
        return new OptionInstance[]{$$0.showSubtitles(), $$0.directionalAudio()};
    }

    public SoundOptionsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.sounds.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(this.options.getSoundSourceOptionInstance(SoundSource.MASTER));
        this.list.addSmall(this.getAllSoundOptionsExceptMaster());
        this.list.addBig(this.options.soundDevice());
        this.list.addSmall(SoundOptionsScreen.buttonOptions(this.options));
        this.addWidget(this.list);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private OptionInstance<?>[] getAllSoundOptionsExceptMaster() {
        return (OptionInstance[])Arrays.stream((Object[])SoundSource.values()).filter($$0 -> $$0 != SoundSource.MASTER).map($$0 -> this.options.getSoundSourceOptionInstance((SoundSource)((Object)$$0))).toArray(OptionInstance[]::new);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.basicListRender($$0, this.list, $$1, $$2, $$3);
    }
}