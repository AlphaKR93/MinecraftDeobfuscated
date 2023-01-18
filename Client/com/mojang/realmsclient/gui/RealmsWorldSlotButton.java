/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class RealmsWorldSlotButton
extends Button {
    public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
    public static final ResourceLocation CHECK_MARK_LOCATION = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
    private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
    private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
    private final Supplier<RealmsServer> serverDataProvider;
    private final Consumer<Component> toolTipSetter;
    private final int slotIndex;
    @Nullable
    private State state;

    public RealmsWorldSlotButton(int $$0, int $$1, int $$2, int $$3, Supplier<RealmsServer> $$4, Consumer<Component> $$5, int $$6, Button.OnPress $$7) {
        super($$0, $$1, $$2, $$3, CommonComponents.EMPTY, $$7, DEFAULT_NARRATION);
        this.serverDataProvider = $$4;
        this.slotIndex = $$6;
        this.toolTipSetter = $$5;
    }

    @Nullable
    public State getState() {
        return this.state;
    }

    public void tick() {
        boolean $$12;
        String $$11;
        long $$10;
        String $$9;
        boolean $$8;
        boolean $$2;
        RealmsServer $$0 = (RealmsServer)this.serverDataProvider.get();
        if ($$0 == null) {
            return;
        }
        RealmsWorldOptions $$1 = (RealmsWorldOptions)$$0.slots.get((Object)this.slotIndex);
        boolean bl = $$2 = this.slotIndex == 4;
        if ($$2) {
            boolean $$3 = $$0.worldType == RealmsServer.WorldType.MINIGAME;
            String $$4 = "Minigame";
            long $$5 = $$0.minigameId;
            String $$6 = $$0.minigameImage;
            boolean $$7 = $$0.minigameId == -1;
        } else {
            $$8 = $$0.activeSlot == this.slotIndex && $$0.worldType != RealmsServer.WorldType.MINIGAME;
            $$9 = $$1.getSlotName(this.slotIndex);
            $$10 = $$1.templateId;
            $$11 = $$1.templateImage;
            $$12 = $$1.empty;
        }
        Action $$13 = RealmsWorldSlotButton.getAction($$0, $$8, $$2);
        Pair<Component, Component> $$14 = this.getTooltipAndNarration($$0, $$9, $$12, $$2, $$13);
        this.state = new State($$8, $$9, $$10, $$11, $$12, $$2, $$13, (Component)$$14.getFirst());
        this.setMessage((Component)$$14.getSecond());
    }

    private static Action getAction(RealmsServer $$0, boolean $$1, boolean $$2) {
        if ($$1) {
            if (!$$0.expired && $$0.state != RealmsServer.State.UNINITIALIZED) {
                return Action.JOIN;
            }
        } else if ($$2) {
            if (!$$0.expired) {
                return Action.SWITCH_SLOT;
            }
        } else {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    private Pair<Component, Component> getTooltipAndNarration(RealmsServer $$0, String $$1, boolean $$2, boolean $$3, Action $$4) {
        Component $$9;
        MutableComponent $$7;
        if ($$4 == Action.NOTHING) {
            return Pair.of(null, (Object)Component.literal($$1));
        }
        if ($$3) {
            if ($$2) {
                Component $$5 = CommonComponents.EMPTY;
            } else {
                MutableComponent $$6 = CommonComponents.space().append($$1).append(CommonComponents.SPACE).append($$0.minigameName);
            }
        } else {
            $$7 = CommonComponents.space().append($$1);
        }
        if ($$4 == Action.JOIN) {
            Component $$8 = SLOT_ACTIVE_TOOLTIP;
        } else {
            $$9 = $$3 ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
        }
        MutableComponent $$10 = $$9.copy().append($$7);
        return Pair.of((Object)$$9, (Object)$$10);
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.state == null) {
            return;
        }
        this.drawSlotFrame($$0, this.getX(), this.getY(), $$1, $$2, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
    }

    private void drawSlotFrame(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5, String $$6, int $$7, long $$8, @Nullable String $$9, boolean $$10, boolean $$11, Action $$12, @Nullable Component $$13) {
        boolean $$16;
        boolean $$14 = this.isHoveredOrFocused();
        if (this.isMouseOver($$3, $$4) && $$13 != null) {
            this.toolTipSetter.accept((Object)$$13);
        }
        Minecraft $$15 = Minecraft.getInstance();
        if ($$11) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf((long)$$8), $$9);
        } else if ($$10) {
            RenderSystem.setShaderTexture(0, EMPTY_SLOT_LOCATION);
        } else if ($$9 != null && $$8 != -1L) {
            RealmsTextureManager.bindWorldTemplate(String.valueOf((long)$$8), $$9);
        } else if ($$7 == 1) {
            RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_1);
        } else if ($$7 == 2) {
            RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_2);
        } else if ($$7 == 3) {
            RenderSystem.setShaderTexture(0, DEFAULT_WORLD_SLOT_3);
        }
        if ($$5) {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.blit($$0, $$1 + 3, $$2 + 3, 0.0f, 0.0f, 74, 74, 74, 74);
        RenderSystem.setShaderTexture(0, SLOT_FRAME_LOCATION);
        boolean bl = $$16 = $$14 && $$12 != Action.NOTHING;
        if ($$16) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else if ($$5) {
            RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
        } else {
            RenderSystem.setShaderColor(0.56f, 0.56f, 0.56f, 1.0f);
        }
        RealmsWorldSlotButton.blit($$0, $$1, $$2, 0.0f, 0.0f, 80, 80, 80, 80);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if ($$5) {
            this.renderCheckMark($$0, $$1, $$2);
        }
        RealmsWorldSlotButton.drawCenteredString($$0, $$15.font, $$6, $$1 + 40, $$2 + 66, 0xFFFFFF);
    }

    private void renderCheckMark(PoseStack $$0, int $$1, int $$2) {
        RenderSystem.setShaderTexture(0, CHECK_MARK_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RealmsWorldSlotButton.blit($$0, $$1 + 67, $$2 + 4, 0.0f, 0.0f, 9, 8, 9, 8);
        RenderSystem.disableBlend();
    }

    public static class State {
        final boolean isCurrentlyActiveSlot;
        final String slotName;
        final long imageId;
        @Nullable
        final String image;
        public final boolean empty;
        public final boolean minigame;
        public final Action action;
        @Nullable
        final Component actionPrompt;

        State(boolean $$0, String $$1, long $$2, @Nullable String $$3, boolean $$4, boolean $$5, Action $$6, @Nullable Component $$7) {
            this.isCurrentlyActiveSlot = $$0;
            this.slotName = $$1;
            this.imageId = $$2;
            this.image = $$3;
            this.empty = $$4;
            this.minigame = $$5;
            this.action = $$6;
            this.actionPrompt = $$7;
        }
    }

    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;

    }
}