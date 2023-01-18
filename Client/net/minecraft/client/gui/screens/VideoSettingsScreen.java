/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.PopupScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen
extends OptionsSubScreen {
    private static final Component FABULOUS = Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
    private static final Component WARNING_MESSAGE = Component.translatable("options.graphics.warning.message", FABULOUS, FABULOUS);
    private static final Component WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
    private static final Component BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
    private static final Component BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
    private OptionsList list;
    private final GpuWarnlistManager gpuWarnlistManager;
    private final int oldMipmaps;

    private static OptionInstance<?>[] options(Options $$0) {
        return new OptionInstance[]{$$0.graphicsMode(), $$0.renderDistance(), $$0.prioritizeChunkUpdates(), $$0.simulationDistance(), $$0.ambientOcclusion(), $$0.framerateLimit(), $$0.enableVsync(), $$0.bobView(), $$0.guiScale(), $$0.attackIndicator(), $$0.gamma(), $$0.cloudStatus(), $$0.fullscreen(), $$0.particles(), $$0.mipmapLevels(), $$0.entityShadows(), $$0.screenEffectScale(), $$0.entityDistanceScaling(), $$0.fovEffectScale(), $$0.showAutosaveIndicator()};
    }

    public VideoSettingsScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.videoTitle"));
        this.gpuWarnlistManager = $$0.minecraft.getGpuWarnlistManager();
        this.gpuWarnlistManager.resetWarnings();
        if ($$1.graphicsMode().get() == GraphicsStatus.FABULOUS) {
            this.gpuWarnlistManager.dismissWarning();
        }
        this.oldMipmaps = $$1.mipmapLevels().get();
    }

    @Override
    protected void init() {
        int $$5;
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        int $$0 = -1;
        Window $$12 = this.minecraft.getWindow();
        Monitor $$22 = $$12.findBestMonitor();
        if ($$22 == null) {
            int $$3 = -1;
        } else {
            Optional<VideoMode> $$4 = $$12.getPreferredFullscreenVideoMode();
            $$5 = (Integer)$$4.map($$22::getVideoModeIndex).orElse((Object)-1);
        }
        OptionInstance<Integer> $$6 = new OptionInstance<Integer>("options.fullscreen.resolution", OptionInstance.noTooltip(), ($$1, $$2) -> {
            if ($$22 == null) {
                return Component.translatable("options.fullscreen.unavailable");
            }
            if ($$2 == -1) {
                return Options.genericValueLabel($$1, Component.translatable("options.fullscreen.current"));
            }
            return Options.genericValueLabel($$1, Component.literal($$22.getMode((int)$$2).toString()));
        }, new OptionInstance.IntRange(-1, $$22 != null ? $$22.getModeCount() - 1 : -1), $$5, $$2 -> {
            if ($$22 == null) {
                return;
            }
            $$12.setPreferredFullscreenVideoMode((Optional<VideoMode>)($$2 == -1 ? Optional.empty() : Optional.of((Object)$$22.getMode((int)$$2))));
        });
        this.list.addBig($$6);
        this.list.addBig(this.options.biomeBlendRadius());
        this.list.addSmall(VideoSettingsScreen.options(this.options));
        this.addWidget(this.list);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$1 -> {
            this.minecraft.options.save();
            $$12.changeFullscreenVideoMode();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void removed() {
        if (this.options.mipmapLevels().get() != this.oldMipmaps) {
            this.minecraft.updateMaxMipLevel(this.options.mipmapLevels().get());
            this.minecraft.delayTextureReload();
        }
        super.removed();
    }

    @Override
    public boolean mouseClicked(double $$02, double $$1, int $$2) {
        int $$3 = this.options.guiScale().get();
        if (super.mouseClicked($$02, $$1, $$2)) {
            if (this.options.guiScale().get() != $$3) {
                this.minecraft.resizeDisplay();
            }
            if (this.gpuWarnlistManager.isShowingWarning()) {
                String $$7;
                String $$6;
                ArrayList $$4 = Lists.newArrayList((Object[])new Component[]{WARNING_MESSAGE, CommonComponents.NEW_LINE});
                String $$5 = this.gpuWarnlistManager.getRendererWarnings();
                if ($$5 != null) {
                    $$4.add((Object)CommonComponents.NEW_LINE);
                    $$4.add((Object)Component.translatable("options.graphics.warning.renderer", $$5).withStyle(ChatFormatting.GRAY));
                }
                if (($$6 = this.gpuWarnlistManager.getVendorWarnings()) != null) {
                    $$4.add((Object)CommonComponents.NEW_LINE);
                    $$4.add((Object)Component.translatable("options.graphics.warning.vendor", $$6).withStyle(ChatFormatting.GRAY));
                }
                if (($$7 = this.gpuWarnlistManager.getVersionWarnings()) != null) {
                    $$4.add((Object)CommonComponents.NEW_LINE);
                    $$4.add((Object)Component.translatable("options.graphics.warning.version", $$7).withStyle(ChatFormatting.GRAY));
                }
                this.minecraft.setScreen(new PopupScreen(WARNING_TITLE, (List<Component>)$$4, (ImmutableList<PopupScreen.ButtonOption>)ImmutableList.of((Object)new PopupScreen.ButtonOption(BUTTON_ACCEPT, $$0 -> {
                    this.options.graphicsMode().set(GraphicsStatus.FABULOUS);
                    Minecraft.getInstance().levelRenderer.allChanged();
                    this.gpuWarnlistManager.dismissWarning();
                    this.minecraft.setScreen(this);
                }), (Object)new PopupScreen.ButtonOption(BUTTON_CANCEL, $$0 -> {
                    this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
                    this.minecraft.setScreen(this);
                }))));
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.basicListRender($$0, this.list, $$1, $$2, $$3);
    }
}