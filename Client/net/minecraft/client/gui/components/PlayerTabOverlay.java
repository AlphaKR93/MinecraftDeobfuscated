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
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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
            if ($$0) {
                MutableComponent $$1 = ComponentUtils.formatList(this.getPlayerInfos(), Component.literal(", "), this::getNameForDisplay);
                this.minecraft.getNarrator().sayNow(Component.translatable("multiplayer.player.list.narration", $$1));
            }
        }
    }

    private List<PlayerInfo> getPlayerInfos() {
        return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
    }

    public void render(PoseStack $$02, int $$12, Scoreboard $$2, @Nullable Objective $$3) {
        int $$16;
        boolean $$13;
        int $$10;
        List<PlayerInfo> $$4 = this.getPlayerInfos();
        int $$5 = 0;
        int $$6 = 0;
        for (PlayerInfo $$7 : $$4) {
            int $$8 = this.minecraft.font.width(this.getNameForDisplay($$7));
            $$5 = Math.max((int)$$5, (int)$$8);
            if ($$3 == null || $$3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) continue;
            $$8 = this.minecraft.font.width(" " + $$2.getOrCreatePlayerScore($$7.getProfile().getName(), $$3).getScore());
            $$6 = Math.max((int)$$6, (int)$$8);
        }
        if (!this.healthStates.isEmpty()) {
            Set $$9 = (Set)$$4.stream().map($$0 -> $$0.getProfile().getId()).collect(Collectors.toSet());
            this.healthStates.keySet().removeIf($$1 -> !$$9.contains($$1));
        }
        int $$11 = $$10 = $$4.size();
        int $$122 = 1;
        while ($$11 > 20) {
            $$11 = ($$10 + ++$$122 - 1) / $$122;
        }
        boolean bl = $$13 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        if ($$3 != null) {
            if ($$3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                int $$14 = 90;
            } else {
                int $$15 = $$6;
            }
        } else {
            $$16 = 0;
        }
        int $$17 = Math.min((int)($$122 * (($$13 ? 9 : 0) + $$5 + $$16 + 13)), (int)($$12 - 50)) / $$122;
        int $$18 = $$12 / 2 - ($$17 * $$122 + ($$122 - 1) * 5) / 2;
        int $$19 = 10;
        int $$20 = $$17 * $$122 + ($$122 - 1) * 5;
        List<FormattedCharSequence> $$21 = null;
        if (this.header != null) {
            $$21 = this.minecraft.font.split(this.header, $$12 - 50);
            for (Object $$22 : $$21) {
                $$20 = Math.max((int)$$20, (int)this.minecraft.font.width((FormattedCharSequence)$$22));
            }
        }
        List<FormattedCharSequence> $$23 = null;
        if (this.footer != null) {
            $$23 = this.minecraft.font.split(this.footer, $$12 - 50);
            for (FormattedCharSequence $$24 : $$23) {
                $$20 = Math.max((int)$$20, (int)this.minecraft.font.width($$24));
            }
        }
        if ($$21 != null) {
            int n = $$12 / 2 - $$20 / 2 - 1;
            int n2 = $$12 / 2 + $$20 / 2 + 1;
            int n3 = $$21.size();
            Objects.requireNonNull((Object)this.minecraft.font);
            PlayerTabOverlay.fill($$02, n, $$19 - 1, n2, $$19 + n3 * 9, Integer.MIN_VALUE);
            for (FormattedCharSequence $$25 : $$21) {
                int $$26 = this.minecraft.font.width($$25);
                this.minecraft.font.drawShadow($$02, $$25, (float)($$12 / 2 - $$26 / 2), (float)$$19, -1);
                Objects.requireNonNull((Object)this.minecraft.font);
                $$19 += 9;
            }
            ++$$19;
        }
        PlayerTabOverlay.fill($$02, $$12 / 2 - $$20 / 2 - 1, $$19 - 1, $$12 / 2 + $$20 / 2 + 1, $$19 + $$11 * 9, Integer.MIN_VALUE);
        int $$27 = this.minecraft.options.getBackgroundColor(0x20FFFFFF);
        for (int $$28 = 0; $$28 < $$10; ++$$28) {
            int $$38;
            int $$39;
            int $$29 = $$28 / $$11;
            int $$30 = $$28 % $$11;
            int $$31 = $$18 + $$29 * $$17 + $$29 * 5;
            int $$32 = $$19 + $$30 * 9;
            PlayerTabOverlay.fill($$02, $$31, $$32, $$31 + $$17, $$32 + 8, $$27);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if ($$28 >= $$4.size()) continue;
            PlayerInfo $$33 = (PlayerInfo)$$4.get($$28);
            GameProfile $$34 = $$33.getProfile();
            if ($$13) {
                Player $$35 = this.minecraft.level.getPlayerByUUID($$34.getId());
                boolean $$36 = $$35 != null && LivingEntityRenderer.isEntityUpsideDown($$35);
                boolean $$37 = $$35 != null && $$35.isModelPartShown(PlayerModelPart.HAT);
                RenderSystem.setShaderTexture(0, $$33.getSkinLocation());
                PlayerFaceRenderer.draw($$02, $$31, $$32, 8, $$37, $$36);
                $$31 += 9;
            }
            this.minecraft.font.drawShadow($$02, this.getNameForDisplay($$33), (float)$$31, (float)$$32, $$33.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if ($$3 != null && $$33.getGameMode() != GameType.SPECTATOR && ($$39 = ($$38 = $$31 + $$5 + 1) + $$16) - $$38 > 5) {
                this.renderTablistScore($$3, $$32, $$34.getName(), $$38, $$39, $$34.getId(), $$02);
            }
            this.renderPingIcon($$02, $$17, $$31 - ($$13 ? 9 : 0), $$32, $$33);
        }
        if ($$23 != null) {
            $$19 += $$11 * 9 + 1;
            int n = $$12 / 2 - $$20 / 2 - 1;
            int n4 = $$12 / 2 + $$20 / 2 + 1;
            int n5 = $$23.size();
            Objects.requireNonNull((Object)this.minecraft.font);
            PlayerTabOverlay.fill($$02, n, $$19 - 1, n4, $$19 + n5 * 9, Integer.MIN_VALUE);
            for (FormattedCharSequence $$40 : $$23) {
                int $$41 = this.minecraft.font.width($$40);
                this.minecraft.font.drawShadow($$02, $$40, (float)($$12 / 2 - $$41 / 2), (float)$$19, -1);
                Objects.requireNonNull((Object)this.minecraft.font);
                $$19 += 9;
            }
        }
    }

    protected void renderPingIcon(PoseStack $$0, int $$1, int $$2, int $$3, PlayerInfo $$4) {
        int $$11;
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