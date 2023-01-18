/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Set
 *  java.util.UUID
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PlayerTabOverlay
extends GuiComponent {
    private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.comparingInt($$0 -> $$0.getGameMode() == GameType.SPECTATOR ? 1 : 0).thenComparing($$0 -> Util.mapNullable($$0.getTeam(), PlayerTeam::getName, "")).thenComparing($$0 -> $$0.getProfile().getName(), String::compareToIgnoreCase);
    public static final int MAX_ROWS_PER_COL = 20;
    public static final int HEART_EMPTY_CONTAINER = 16;
    public static final int HEART_EMPTY_CONTAINER_BLINKING = 25;
    public static final int HEART_FULL = 52;
    public static final int HEART_HALF_FULL = 61;
    public static final int HEART_GOLDEN_FULL = 160;
    public static final int HEART_GOLDEN_HALF_FULL = 169;
    public static final int HEART_GHOST_FULL = 70;
    public static final int HEART_GHOST_HALF_FULL = 79;
    private final Minecraft minecraft;
    private final Gui gui;
    @Nullable
    private Component footer;
    @Nullable
    private Component header;
    private boolean visible;
    private final Map<UUID, HealthState> healthStates = new Object2ObjectOpenHashMap();

    public PlayerTabOverlay(Minecraft $$0, Gui $$1) {
        this.minecraft = $$0;
        this.gui = $$1;
    }

    public Component getNameForDisplay(PlayerInfo $$0) {
        if ($$0.getTabListDisplayName() != null) {
            return this.decorateName($$0, $$0.getTabListDisplayName().copy());
        }
        return this.decorateName($$0, PlayerTeam.formatNameForTeam($$0.getTeam(), Component.literal($$0.getProfile().getName())));
    }

    private Component decorateName(PlayerInfo $$0, MutableComponent $$1) {
        return $$0.getGameMode() == GameType.SPECTATOR ? $$1.withStyle(ChatFormatting.ITALIC) : $$1;
    }

    public void setVisible(boolean $$0) {
        if (this.visible != $$0) {
            this.healthStates.clear();
            this.visible = $$0;
        }
    }

    public void render(PoseStack $$02, int $$12, Scoreboard $$2, @Nullable Objective $$3) {
        int $$17;
        boolean $$14;
        int $$11;
        ClientPacketListener $$4 = this.minecraft.player.connection;
        List $$5 = $$4.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
        int $$6 = 0;
        int $$7 = 0;
        for (PlayerInfo $$8 : $$5) {
            int $$9 = this.minecraft.font.width(this.getNameForDisplay($$8));
            $$6 = Math.max((int)$$6, (int)$$9);
            if ($$3 == null || $$3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) continue;
            $$9 = this.minecraft.font.width(" " + $$2.getOrCreatePlayerScore($$8.getProfile().getName(), $$3).getScore());
            $$7 = Math.max((int)$$7, (int)$$9);
        }
        if (!this.healthStates.isEmpty()) {
            Set $$10 = (Set)$$5.stream().map($$0 -> $$0.getProfile().getId()).collect(Collectors.toSet());
            this.healthStates.keySet().removeIf($$1 -> !$$10.contains($$1));
        }
        int $$122 = $$11 = $$5.size();
        int $$13 = 1;
        while ($$122 > 20) {
            $$122 = ($$11 + ++$$13 - 1) / $$13;
        }
        boolean bl = $$14 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        if ($$3 != null) {
            if ($$3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                int $$15 = 90;
            } else {
                int $$16 = $$7;
            }
        } else {
            $$17 = 0;
        }
        int $$18 = Math.min((int)($$13 * (($$14 ? 9 : 0) + $$6 + $$17 + 13)), (int)($$12 - 50)) / $$13;
        int $$19 = $$12 / 2 - ($$18 * $$13 + ($$13 - 1) * 5) / 2;
        int $$20 = 10;
        int $$21 = $$18 * $$13 + ($$13 - 1) * 5;
        List<FormattedCharSequence> $$22 = null;
        if (this.header != null) {
            $$22 = this.minecraft.font.split(this.header, $$12 - 50);
            for (Object $$23 : $$22) {
                $$21 = Math.max((int)$$21, (int)this.minecraft.font.width((FormattedCharSequence)$$23));
            }
        }
        List<FormattedCharSequence> $$24 = null;
        if (this.footer != null) {
            $$24 = this.minecraft.font.split(this.footer, $$12 - 50);
            for (FormattedCharSequence $$25 : $$24) {
                $$21 = Math.max((int)$$21, (int)this.minecraft.font.width($$25));
            }
        }
        if ($$22 != null) {
            int n = $$12 / 2 - $$21 / 2 - 1;
            int n2 = $$12 / 2 + $$21 / 2 + 1;
            int n3 = $$22.size();
            Objects.requireNonNull((Object)this.minecraft.font);
            PlayerTabOverlay.fill($$02, n, $$20 - 1, n2, $$20 + n3 * 9, Integer.MIN_VALUE);
            for (FormattedCharSequence $$26 : $$22) {
                int $$27 = this.minecraft.font.width($$26);
                this.minecraft.font.drawShadow($$02, $$26, (float)($$12 / 2 - $$27 / 2), (float)$$20, -1);
                Objects.requireNonNull((Object)this.minecraft.font);
                $$20 += 9;
            }
            ++$$20;
        }
        PlayerTabOverlay.fill($$02, $$12 / 2 - $$21 / 2 - 1, $$20 - 1, $$12 / 2 + $$21 / 2 + 1, $$20 + $$122 * 9, Integer.MIN_VALUE);
        int $$28 = this.minecraft.options.getBackgroundColor(0x20FFFFFF);
        for (int $$29 = 0; $$29 < $$11; ++$$29) {
            int $$39;
            int $$40;
            int $$30 = $$29 / $$122;
            int $$31 = $$29 % $$122;
            int $$32 = $$19 + $$30 * $$18 + $$30 * 5;
            int $$33 = $$20 + $$31 * 9;
            PlayerTabOverlay.fill($$02, $$32, $$33, $$32 + $$18, $$33 + 8, $$28);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if ($$29 >= $$5.size()) continue;
            PlayerInfo $$34 = (PlayerInfo)$$5.get($$29);
            GameProfile $$35 = $$34.getProfile();
            if ($$14) {
                Player $$36 = this.minecraft.level.getPlayerByUUID($$35.getId());
                boolean $$37 = $$36 != null && LivingEntityRenderer.isEntityUpsideDown($$36);
                boolean $$38 = $$36 != null && $$36.isModelPartShown(PlayerModelPart.HAT);
                RenderSystem.setShaderTexture(0, $$34.getSkinLocation());
                PlayerFaceRenderer.draw($$02, $$32, $$33, 8, $$38, $$37);
                $$32 += 9;
            }
            this.minecraft.font.drawShadow($$02, this.getNameForDisplay($$34), (float)$$32, (float)$$33, $$34.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if ($$3 != null && $$34.getGameMode() != GameType.SPECTATOR && ($$40 = ($$39 = $$32 + $$6 + 1) + $$17) - $$39 > 5) {
                this.renderTablistScore($$3, $$33, $$35.getName(), $$39, $$40, $$35.getId(), $$02);
            }
            this.renderPingIcon($$02, $$18, $$32 - ($$14 ? 9 : 0), $$33, $$34);
        }
        if ($$24 != null) {
            $$20 += $$122 * 9 + 1;
            int n = $$12 / 2 - $$21 / 2 - 1;
            int n4 = $$12 / 2 + $$21 / 2 + 1;
            int n5 = $$24.size();
            Objects.requireNonNull((Object)this.minecraft.font);
            PlayerTabOverlay.fill($$02, n, $$20 - 1, n4, $$20 + n5 * 9, Integer.MIN_VALUE);
            for (FormattedCharSequence $$41 : $$24) {
                int $$42 = this.minecraft.font.width($$41);
                this.minecraft.font.drawShadow($$02, $$41, (float)($$12 / 2 - $$42 / 2), (float)$$20, -1);
                Objects.requireNonNull((Object)this.minecraft.font);
                $$20 += 9;
            }
        }
    }

    protected void renderPingIcon(PoseStack $$0, int $$1, int $$2, int $$3, PlayerInfo $$4) {
        int $$11;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
        boolean $$5 = false;
        if ($$4.getLatency() < 0) {
            int $$6 = 5;
        } else if ($$4.getLatency() < 150) {
            boolean $$7 = false;
        } else if ($$4.getLatency() < 300) {
            boolean $$8 = true;
        } else if ($$4.getLatency() < 600) {
            int $$9 = 2;
        } else if ($$4.getLatency() < 1000) {
            int $$10 = 3;
        } else {
            $$11 = 4;
        }
        this.setBlitOffset(this.getBlitOffset() + 100);
        this.blit($$0, $$2 + $$1 - 11, $$3, 0, 176 + $$11 * 8, 10, 8);
        this.setBlitOffset(this.getBlitOffset() - 100);
    }

    private void renderTablistScore(Objective $$0, int $$1, String $$2, int $$3, int $$4, UUID $$5, PoseStack $$6) {
        int $$7 = $$0.getScoreboard().getOrCreatePlayerScore($$2, $$0).getScore();
        if ($$0.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            this.renderTablistHearts($$1, $$3, $$4, $$5, $$6, $$7);
            return;
        }
        String $$8 = "" + ChatFormatting.YELLOW + $$7;
        this.minecraft.font.drawShadow($$6, $$8, (float)($$4 - this.minecraft.font.width($$8)), (float)$$1, 0xFFFFFF);
    }

    private void renderTablistHearts(int $$0, int $$12, int $$2, UUID $$3, PoseStack $$4, int $$5) {
        HealthState $$6 = (HealthState)this.healthStates.computeIfAbsent((Object)$$3, $$1 -> new HealthState($$5));
        $$6.update($$5, this.gui.getGuiTicks());
        int $$7 = Mth.positiveCeilDiv(Math.max((int)$$5, (int)$$6.displayedValue()), 2);
        int $$8 = Math.max((int)$$5, (int)Math.max((int)$$6.displayedValue(), (int)20)) / 2;
        boolean $$9 = $$6.isBlinking(this.gui.getGuiTicks());
        if ($$7 <= 0) {
            return;
        }
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
        int $$10 = Mth.floor(Math.min((float)((float)($$2 - $$12 - 4) / (float)$$8), (float)9.0f));
        if ($$10 <= 3) {
            float $$11 = Mth.clamp((float)$$5 / 20.0f, 0.0f, 1.0f);
            int $$122 = (int)((1.0f - $$11) * 255.0f) << 16 | (int)($$11 * 255.0f) << 8;
            String $$13 = "" + (float)$$5 / 2.0f;
            if ($$2 - this.minecraft.font.width($$13 + "hp") >= $$12) {
                $$13 = $$13 + "hp";
            }
            this.minecraft.font.drawShadow($$4, $$13, (float)(($$2 + $$12 - this.minecraft.font.width($$13)) / 2), (float)$$0, $$122);
            return;
        }
        for (int $$14 = $$7; $$14 < $$8; ++$$14) {
            this.blit($$4, $$12 + $$14 * $$10, $$0, $$9 ? 25 : 16, 0, 9, 9);
        }
        for (int $$15 = 0; $$15 < $$7; ++$$15) {
            this.blit($$4, $$12 + $$15 * $$10, $$0, $$9 ? 25 : 16, 0, 9, 9);
            if ($$9) {
                if ($$15 * 2 + 1 < $$6.displayedValue()) {
                    this.blit($$4, $$12 + $$15 * $$10, $$0, 70, 0, 9, 9);
                }
                if ($$15 * 2 + 1 == $$6.displayedValue()) {
                    this.blit($$4, $$12 + $$15 * $$10, $$0, 79, 0, 9, 9);
                }
            }
            if ($$15 * 2 + 1 < $$5) {
                this.blit($$4, $$12 + $$15 * $$10, $$0, $$15 >= 10 ? 160 : 52, 0, 9, 9);
            }
            if ($$15 * 2 + 1 != $$5) continue;
            this.blit($$4, $$12 + $$15 * $$10, $$0, $$15 >= 10 ? 169 : 61, 0, 9, 9);
        }
    }

    public void setFooter(@Nullable Component $$0) {
        this.footer = $$0;
    }

    public void setHeader(@Nullable Component $$0) {
        this.header = $$0;
    }

    public void reset() {
        this.header = null;
        this.footer = null;
    }

    static class HealthState {
        private static final long DISPLAY_UPDATE_DELAY = 20L;
        private static final long DECREASE_BLINK_DURATION = 20L;
        private static final long INCREASE_BLINK_DURATION = 10L;
        private int lastValue;
        private int displayedValue;
        private long lastUpdateTick;
        private long blinkUntilTick;

        public HealthState(int $$0) {
            this.displayedValue = $$0;
            this.lastValue = $$0;
        }

        public void update(int $$0, long $$1) {
            if ($$0 != this.lastValue) {
                long $$2 = $$0 < this.lastValue ? 20L : 10L;
                this.blinkUntilTick = $$1 + $$2;
                this.lastValue = $$0;
                this.lastUpdateTick = $$1;
            }
            if ($$1 - this.lastUpdateTick > 20L) {
                this.displayedValue = $$0;
            }
        }

        public int displayedValue() {
            return this.displayedValue;
        }

        public boolean isBlinking(long $$0) {
            return this.blinkUntilTick > $$0 && (this.blinkUntilTick - $$0) % 6L >= 3L;
        }
    }
}