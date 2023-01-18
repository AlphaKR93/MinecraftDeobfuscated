/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.Hashing
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.net.UnknownHostException
 *  java.util.Collections
 *  java.util.List
 *  java.util.Objects
 *  java.util.concurrent.ScheduledThreadPoolExecutor
 *  java.util.concurrent.ThreadPoolExecutor
 *  java.util.function.Supplier
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerSelectionList
extends ObjectSelectionList<Entry> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER)).build());
    static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
    static final Component SCANNING_LABEL = Component.translatable("lanServer.scanning");
    static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withColor(-65536)));
    static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withColor(-65536)));
    static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
    static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
    static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
    static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
    private final JoinMultiplayerScreen screen;
    private final List<OnlineServerEntry> onlineServers = Lists.newArrayList();
    private final Entry lanHeader = new LANHeader();
    private final List<NetworkServerEntry> networkServers = Lists.newArrayList();

    public ServerSelectionList(JoinMultiplayerScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        super($$1, $$2, $$3, $$4, $$5, $$6);
        this.screen = $$0;
    }

    private void refreshEntries() {
        this.clearEntries();
        this.onlineServers.forEach($$1 -> this.addEntry($$1));
        this.addEntry(this.lanHeader);
        this.networkServers.forEach($$1 -> this.addEntry($$1));
    }

    @Override
    public void setSelected(@Nullable Entry $$0) {
        super.setSelected($$0);
        this.screen.onSelectedChange();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        Entry $$3 = (Entry)this.getSelected();
        return $$3 != null && $$3.keyPressed($$0, $$1, $$2) || super.keyPressed($$0, $$1, $$2);
    }

    public void updateOnlineServers(ServerList $$0) {
        this.onlineServers.clear();
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            this.onlineServers.add((Object)new OnlineServerEntry(this.screen, $$0.get($$1)));
        }
        this.refreshEntries();
    }

    public void updateNetworkServers(List<LanServer> $$0) {
        int $$1 = $$0.size() - this.networkServers.size();
        this.networkServers.clear();
        for (LanServer $$2 : $$0) {
            this.networkServers.add((Object)new NetworkServerEntry(this.screen, $$2));
        }
        this.refreshEntries();
        for (int $$3 = this.networkServers.size() - $$1; $$3 < this.networkServers.size(); ++$$3) {
            NetworkServerEntry $$4 = (NetworkServerEntry)this.networkServers.get($$3);
            int $$5 = $$3 - this.networkServers.size() + this.children().size();
            int $$6 = this.getRowTop($$5);
            int $$7 = this.getRowBottom($$5);
            if ($$7 < this.y0 || $$6 > this.y1) continue;
            this.minecraft.getNarrator().say(Component.translatable("multiplayer.lan.server_found", $$4.getServerNarration()));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    public static class LANHeader
    extends Entry {
        private final Minecraft minecraft = Minecraft.getInstance();

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            int n = $$2 + $$5 / 2;
            Objects.requireNonNull((Object)this.minecraft.font);
            int $$10 = n - 9 / 2;
            this.minecraft.font.draw($$0, SCANNING_LABEL, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(SCANNING_LABEL) / 2), (float)$$10, 0xFFFFFF);
            String $$11 = LoadingDotsText.get(Util.getMillis());
            Font font = this.minecraft.font;
            float f = this.minecraft.screen.width / 2 - this.minecraft.font.width($$11) / 2;
            Objects.requireNonNull((Object)this.minecraft.font);
            font.draw($$0, $$11, f, (float)($$10 + 9), 0x808080);
        }

        @Override
        public Component getNarration() {
            return SCANNING_LABEL;
        }
    }

    public static abstract class Entry
    extends ObjectSelectionList.Entry<Entry> {
    }

    public class OnlineServerEntry
    extends Entry {
        private static final int ICON_WIDTH = 32;
        private static final int ICON_HEIGHT = 32;
        private static final int ICON_OVERLAY_X_MOVE_RIGHT = 0;
        private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
        private static final int ICON_OVERLAY_X_MOVE_DOWN = 64;
        private static final int ICON_OVERLAY_X_MOVE_UP = 96;
        private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
        private static final int ICON_OVERLAY_Y_SELECTED = 32;
        private final JoinMultiplayerScreen screen;
        private final Minecraft minecraft;
        private final ServerData serverData;
        private final ResourceLocation iconLocation;
        @Nullable
        private String lastIconB64;
        @Nullable
        private DynamicTexture icon;
        private long lastClickTime;

        protected OnlineServerEntry(JoinMultiplayerScreen $$1, ServerData $$2) {
            this.screen = $$1;
            this.serverData = $$2;
            this.minecraft = Minecraft.getInstance();
            this.iconLocation = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars((CharSequence)$$2.ip) + "/icon");
            AbstractTexture $$3 = this.minecraft.getTextureManager().getTexture(this.iconLocation, MissingTextureAtlasSprite.getTexture());
            if ($$3 != MissingTextureAtlasSprite.getTexture() && $$3 instanceof DynamicTexture) {
                this.icon = (DynamicTexture)$$3;
            }
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            List $$31;
            Component $$30;
            int $$29;
            if (!this.serverData.pinged) {
                this.serverData.pinged = true;
                this.serverData.ping = -2L;
                this.serverData.motd = CommonComponents.EMPTY;
                this.serverData.status = CommonComponents.EMPTY;
                THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList));
                    }
                    catch (UnknownHostException $$0) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_RESOLVE_TEXT;
                    }
                    catch (Exception $$1) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_CONNECT_TEXT;
                    }
                });
            }
            boolean $$10 = !this.isCompatible();
            this.minecraft.font.draw($$0, this.serverData.name, (float)($$3 + 32 + 3), (float)($$2 + 1), 0xFFFFFF);
            List<FormattedCharSequence> $$11 = this.minecraft.font.split(this.serverData.motd, $$4 - 32 - 2);
            for (int $$12 = 0; $$12 < Math.min((int)$$11.size(), (int)2); ++$$12) {
                Font font = this.minecraft.font;
                FormattedCharSequence formattedCharSequence = (FormattedCharSequence)$$11.get($$12);
                float f = $$3 + 32 + 3;
                Objects.requireNonNull((Object)this.minecraft.font);
                font.draw($$0, formattedCharSequence, f, (float)($$2 + 12 + 9 * $$12), 0x808080);
            }
            Component $$13 = $$10 ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
            int $$14 = this.minecraft.font.width($$13);
            this.minecraft.font.draw($$0, $$13, (float)($$3 + $$4 - $$14 - 15 - 2), (float)($$2 + 1), 0x808080);
            int $$15 = 0;
            if ($$10) {
                int $$16 = 5;
                Component $$17 = INCOMPATIBLE_STATUS;
                List<Component> $$18 = this.serverData.playerList;
            } else if (this.pingCompleted()) {
                if (this.serverData.ping < 0L) {
                    int $$19 = 5;
                } else if (this.serverData.ping < 150L) {
                    boolean $$20 = false;
                } else if (this.serverData.ping < 300L) {
                    boolean $$21 = true;
                } else if (this.serverData.ping < 600L) {
                    int $$22 = 2;
                } else if (this.serverData.ping < 1000L) {
                    int $$23 = 3;
                } else {
                    int $$24 = 4;
                }
                if (this.serverData.ping < 0L) {
                    Component $$25 = NO_CONNECTION_STATUS;
                    List $$26 = Collections.emptyList();
                } else {
                    MutableComponent $$27 = Component.translatable("multiplayer.status.ping", this.serverData.ping);
                    List<Component> $$28 = this.serverData.playerList;
                }
            } else {
                $$15 = 1;
                $$29 = (int)(Util.getMillis() / 100L + (long)($$1 * 2) & 7L);
                if ($$29 > 4) {
                    $$29 = 8 - $$29;
                }
                $$30 = PINGING_STATUS;
                $$31 = Collections.emptyList();
            }
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            GuiComponent.blit($$0, $$3 + $$4 - 15, $$2, $$15 * 10, 176 + $$29 * 8, 10, 8, 256, 256);
            String $$32 = this.serverData.getIconB64();
            if (!Objects.equals((Object)$$32, (Object)this.lastIconB64)) {
                if (this.uploadServerIcon($$32)) {
                    this.lastIconB64 = $$32;
                } else {
                    this.serverData.setIconB64(null);
                    this.updateServerList();
                }
            }
            if (this.icon == null) {
                this.drawIcon($$0, $$3, $$2, ICON_MISSING);
            } else {
                this.drawIcon($$0, $$3, $$2, this.iconLocation);
            }
            int $$33 = $$6 - $$3;
            int $$34 = $$7 - $$2;
            if ($$33 >= $$4 - 15 && $$33 <= $$4 - 5 && $$34 >= 0 && $$34 <= 8) {
                this.screen.setToolTip((List<Component>)Collections.singletonList((Object)$$30));
            } else if ($$33 >= $$4 - $$14 - 15 - 2 && $$33 <= $$4 - 15 - 2 && $$34 >= 0 && $$34 <= 8) {
                this.screen.setToolTip((List<Component>)$$31);
            }
            if (this.minecraft.options.touchscreen().get().booleanValue() || $$8) {
                RenderSystem.setShaderTexture(0, ICON_OVERLAY_LOCATION);
                GuiComponent.fill($$0, $$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
                int $$35 = $$6 - $$3;
                int $$36 = $$7 - $$2;
                if (this.canJoin()) {
                    if ($$35 < 32 && $$35 > 16) {
                        GuiComponent.blit($$0, $$3, $$2, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit($$0, $$3, $$2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if ($$1 > 0) {
                    if ($$35 < 16 && $$36 < 16) {
                        GuiComponent.blit($$0, $$3, $$2, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit($$0, $$3, $$2, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if ($$1 < this.screen.getServers().size() - 1) {
                    if ($$35 < 16 && $$36 > 16) {
                        GuiComponent.blit($$0, $$3, $$2, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit($$0, $$3, $$2, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        private boolean pingCompleted() {
            return this.serverData.pinged && this.serverData.ping != -2L;
        }

        private boolean isCompatible() {
            return this.serverData.protocol == SharedConstants.getCurrentVersion().getProtocolVersion();
        }

        public void updateServerList() {
            this.screen.getServers().save();
        }

        protected void drawIcon(PoseStack $$0, int $$1, int $$2, ResourceLocation $$3) {
            RenderSystem.setShaderTexture(0, $$3);
            RenderSystem.enableBlend();
            GuiComponent.blit($$0, $$1, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean canJoin() {
            return true;
        }

        private boolean uploadServerIcon(@Nullable String $$0) {
            if ($$0 == null) {
                this.minecraft.getTextureManager().release(this.iconLocation);
                if (this.icon != null && this.icon.getPixels() != null) {
                    this.icon.getPixels().close();
                }
                this.icon = null;
            } else {
                try {
                    NativeImage $$1 = NativeImage.fromBase64($$0);
                    Validate.validState(($$1.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                    Validate.validState(($$1.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                    if (this.icon == null) {
                        this.icon = new DynamicTexture($$1);
                    } else {
                        this.icon.setPixels($$1);
                        this.icon.upload();
                    }
                    this.minecraft.getTextureManager().register(this.iconLocation, (AbstractTexture)this.icon);
                }
                catch (Throwable $$2) {
                    LOGGER.error("Invalid icon for server {} ({})", new Object[]{this.serverData.name, this.serverData.ip, $$2});
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(int $$0, int $$1, int $$2) {
            if (Screen.hasShiftDown()) {
                ServerSelectionList $$3 = this.screen.serverSelectionList;
                int $$4 = $$3.children().indexOf((Object)this);
                if ($$4 == -1) {
                    return true;
                }
                if ($$0 == 264 && $$4 < this.screen.getServers().size() - 1 || $$0 == 265 && $$4 > 0) {
                    this.swap($$4, $$0 == 264 ? $$4 + 1 : $$4 - 1);
                    return true;
                }
            }
            return super.keyPressed($$0, $$1, $$2);
        }

        private void swap(int $$0, int $$1) {
            this.screen.getServers().swap($$0, $$1);
            this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
            Entry $$2 = (Entry)this.screen.serverSelectionList.children().get($$1);
            this.screen.serverSelectionList.setSelected($$2);
            ServerSelectionList.this.ensureVisible($$2);
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            double $$3 = $$0 - (double)ServerSelectionList.this.getRowLeft();
            double $$4 = $$1 - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf((Object)this));
            if ($$3 <= 32.0) {
                if ($$3 < 32.0 && $$3 > 16.0 && this.canJoin()) {
                    this.screen.setSelected(this);
                    this.screen.joinSelectedServer();
                    return true;
                }
                int $$5 = this.screen.serverSelectionList.children().indexOf((Object)this);
                if ($$3 < 16.0 && $$4 < 16.0 && $$5 > 0) {
                    this.swap($$5, $$5 - 1);
                    return true;
                }
                if ($$3 < 16.0 && $$4 > 16.0 && $$5 < this.screen.getServers().size() - 1) {
                    this.swap($$5, $$5 + 1);
                    return true;
                }
            }
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return true;
        }

        public ServerData getServerData() {
            return this.serverData;
        }

        @Override
        public Component getNarration() {
            MutableComponent $$0 = Component.empty();
            $$0.append(Component.translatable("narrator.select", this.serverData.name));
            $$0.append(CommonComponents.NARRATION_SEPARATOR);
            if (!this.isCompatible()) {
                $$0.append(INCOMPATIBLE_STATUS);
                $$0.append(CommonComponents.NARRATION_SEPARATOR);
                $$0.append(Component.translatable("multiplayer.status.version.narration", this.serverData.version));
                $$0.append(CommonComponents.NARRATION_SEPARATOR);
                $$0.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
            } else if (this.serverData.ping < 0L) {
                $$0.append(NO_CONNECTION_STATUS);
            } else if (!this.pingCompleted()) {
                $$0.append(PINGING_STATUS);
            } else {
                $$0.append(ONLINE_STATUS);
                $$0.append(CommonComponents.NARRATION_SEPARATOR);
                $$0.append(Component.translatable("multiplayer.status.ping.narration", this.serverData.ping));
                $$0.append(CommonComponents.NARRATION_SEPARATOR);
                $$0.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
                if (this.serverData.players != null) {
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(Component.translatable("multiplayer.status.player_count.narration", this.serverData.players.getNumPlayers(), this.serverData.players.getMaxPlayers()));
                    $$0.append(CommonComponents.NARRATION_SEPARATOR);
                    $$0.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
                }
            }
            return $$0;
        }
    }

    public static class NetworkServerEntry
    extends Entry {
        private static final int ICON_WIDTH = 32;
        private static final Component LAN_SERVER_HEADER = Component.translatable("lanServer.title");
        private static final Component HIDDEN_ADDRESS_TEXT = Component.translatable("selectServer.hiddenAddress");
        private final JoinMultiplayerScreen screen;
        protected final Minecraft minecraft;
        protected final LanServer serverData;
        private long lastClickTime;

        protected NetworkServerEntry(JoinMultiplayerScreen $$0, LanServer $$1) {
            this.screen = $$0;
            this.serverData = $$1;
            this.minecraft = Minecraft.getInstance();
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            this.minecraft.font.draw($$0, LAN_SERVER_HEADER, (float)($$3 + 32 + 3), (float)($$2 + 1), 0xFFFFFF);
            this.minecraft.font.draw($$0, this.serverData.getMotd(), (float)($$3 + 32 + 3), (float)($$2 + 12), 0x808080);
            if (this.minecraft.options.hideServerAddress) {
                this.minecraft.font.draw($$0, HIDDEN_ADDRESS_TEXT, (float)($$3 + 32 + 3), (float)($$2 + 12 + 11), 0x303030);
            } else {
                this.minecraft.font.draw($$0, this.serverData.getAddress(), (float)($$3 + 32 + 3), (float)($$2 + 12 + 11), 0x303030);
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedServer();
            }
            this.lastClickTime = Util.getMillis();
            return false;
        }

        public LanServer getServerData() {
            return this.serverData;
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.getServerNarration());
        }

        public Component getServerNarration() {
            return Component.empty().append(LAN_SERVER_HEADER).append(CommonComponents.SPACE).append(this.serverData.getMotd());
        }
    }
}