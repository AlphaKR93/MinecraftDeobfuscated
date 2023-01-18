/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.util.LevelType;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen
extends RealmsScreen {
    private static final Component SEED_LABEL = Component.translatable("mco.reset.world.seed");
    private final Consumer<WorldGenerationInfo> callback;
    private EditBox seedEdit;
    private LevelType levelType = LevelType.DEFAULT;
    private boolean generateStructures = true;
    private final Component buttonTitle;

    public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> $$0, Component $$1) {
        super(Component.translatable("mco.reset.world.generate"));
        this.callback = $$0;
        this.buttonTitle = $$1;
    }

    @Override
    public void tick() {
        this.seedEdit.tick();
        super.tick();
    }

    @Override
    public void init() {
        this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, RealmsResetNormalWorldScreen.row(2), 200, 20, null, Component.translatable("mco.reset.world.seed"));
        this.seedEdit.setMaxLength(32);
        this.addWidget(this.seedEdit);
        this.setInitialFocus(this.seedEdit);
        this.addRenderableWidget(CycleButton.builder(LevelType::getName).withValues(LevelType.values()).withInitialValue(this.levelType).create(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(4), 205, 20, Component.translatable("selectWorld.mapType"), ($$0, $$1) -> {
            this.levelType = $$1;
        }));
        this.addRenderableWidget(CycleButton.onOffBuilder(this.generateStructures).create(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(6) - 2, 205, 20, Component.translatable("selectWorld.mapFeatures"), ($$0, $$1) -> {
            this.generateStructures = $$1;
        }));
        this.addRenderableWidget(Button.builder(this.buttonTitle, $$0 -> this.callback.accept((Object)new WorldGenerationInfo(this.seedEdit.getValue(), this.levelType, this.generateStructures))).bounds(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(12), 97, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).bounds(this.width / 2 + 8, RealmsResetNormalWorldScreen.row(12), 97, 20).build());
    }

    @Override
    public void onClose() {
        this.callback.accept(null);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsResetNormalWorldScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        this.font.draw($$0, SEED_LABEL, (float)(this.width / 2 - 100), (float)RealmsResetNormalWorldScreen.row(1), 0xA0A0A0);
        this.seedEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }
}