/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Objects
 *  java.util.UUID
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

public class BossHealthOverlay
extends GuiComponent {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int OVERLAY_OFFSET = 80;
    private final Minecraft minecraft;
    final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

    public BossHealthOverlay(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(PoseStack $$0) {
        if (this.events.isEmpty()) {
            return;
        }
        int $$1 = this.minecraft.getWindow().getGuiScaledWidth();
        int $$2 = 12;
        for (LerpingBossEvent $$3 : this.events.values()) {
            int $$4 = $$1 / 2 - 91;
            int $$5 = $$2;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
            this.drawBar($$0, $$4, $$5, $$3);
            Component $$6 = $$3.getName();
            int $$7 = this.minecraft.font.width($$6);
            int $$8 = $$1 / 2 - $$7 / 2;
            int $$9 = $$5 - 9;
            this.minecraft.font.drawShadow($$0, $$6, (float)$$8, (float)$$9, 0xFFFFFF);
            Objects.requireNonNull((Object)this.minecraft.font);
            if (($$2 += 10 + 9) < this.minecraft.getWindow().getGuiScaledHeight() / 3) continue;
            break;
        }
    }

    private void drawBar(PoseStack $$0, int $$1, int $$2, BossEvent $$3) {
        this.drawBar($$0, $$1, $$2, $$3, 182, 0);
        int $$4 = (int)($$3.getProgress() * 183.0f);
        if ($$4 > 0) {
            this.drawBar($$0, $$1, $$2, $$3, $$4, 5);
        }
    }

    private void drawBar(PoseStack $$0, int $$1, int $$2, BossEvent $$3, int $$4, int $$5) {
        this.blit($$0, $$1, $$2, 0, $$3.getColor().ordinal() * 5 * 2 + $$5, $$4, 5);
        if ($$3.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.blit($$0, $$1, $$2, 0, 80 + ($$3.getOverlay().ordinal() - 1) * 5 * 2 + $$5, $$4, 5);
            RenderSystem.disableBlend();
        }
    }

    public void update(ClientboundBossEventPacket $$0) {
        $$0.dispatch(new ClientboundBossEventPacket.Handler(){

            @Override
            public void add(UUID $$0, Component $$1, float $$2, BossEvent.BossBarColor $$3, BossEvent.BossBarOverlay $$4, boolean $$5, boolean $$6, boolean $$7) {
                BossHealthOverlay.this.events.put((Object)$$0, (Object)new LerpingBossEvent($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            }

            @Override
            public void remove(UUID $$0) {
                BossHealthOverlay.this.events.remove((Object)$$0);
            }

            @Override
            public void updateProgress(UUID $$0, float $$1) {
                ((LerpingBossEvent)BossHealthOverlay.this.events.get((Object)$$0)).setProgress($$1);
            }

            @Override
            public void updateName(UUID $$0, Component $$1) {
                ((LerpingBossEvent)BossHealthOverlay.this.events.get((Object)$$0)).setName($$1);
            }

            @Override
            public void updateStyle(UUID $$0, BossEvent.BossBarColor $$1, BossEvent.BossBarOverlay $$2) {
                LerpingBossEvent $$3 = (LerpingBossEvent)BossHealthOverlay.this.events.get((Object)$$0);
                $$3.setColor($$1);
                $$3.setOverlay($$2);
            }

            @Override
            public void updateProperties(UUID $$0, boolean $$1, boolean $$2, boolean $$3) {
                LerpingBossEvent $$4 = (LerpingBossEvent)BossHealthOverlay.this.events.get((Object)$$0);
                $$4.setDarkenScreen($$1);
                $$4.setPlayBossMusic($$2);
                $$4.setCreateWorldFog($$3);
            }
        });
    }

    public void reset() {
        this.events.clear();
    }

    public boolean shouldPlayMusic() {
        if (!this.events.isEmpty()) {
            for (BossEvent $$0 : this.events.values()) {
                if (!$$0.shouldPlayBossMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenScreen() {
        if (!this.events.isEmpty()) {
            for (BossEvent $$0 : this.events.values()) {
                if (!$$0.shouldDarkenScreen()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldCreateWorldFog() {
        if (!this.events.isEmpty()) {
            for (BossEvent $$0 : this.events.values()) {
                if (!$$0.shouldCreateWorldFog()) continue;
                return true;
            }
        }
        return false;
    }
}