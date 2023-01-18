/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class JoinMultiplayerScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ServerStatusPinger pinger = new ServerStatusPinger();
    private final Screen lastScreen;
    protected ServerSelectionList serverSelectionList;
    private ServerList servers;
    private Button editButton;
    private Button selectButton;
    private Button deleteButton;
    @Nullable
    private List<Component> toolTip;
    private ServerData editingServer;
    private LanServerDetection.LanServerList lanServerList;
    @Nullable
    private LanServerDetection.LanServerDetector lanServerDetector;
    private boolean initedOnce;

    public JoinMultiplayerScreen(Screen $$0) {
        super(Component.translatable("multiplayer.title"));
        this.lastScreen = $$0;
    }

    @Override
    protected void init() {
        if (this.initedOnce) {
            this.serverSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initedOnce = true;
            this.servers = new ServerList(this.minecraft);
            this.servers.load();
            this.lanServerList = new LanServerDetection.LanServerList();
            try {
                this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception $$02) {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)$$02.getMessage());
            }
            this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.addWidget(this.serverSelectionList);
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), $$0 -> this.joinSelectedServer()).bounds(this.width / 2 - 154, this.height - 52, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectServer.direct"), $$0 -> {
            this.editingServer = new ServerData(I18n.get("selectServer.defaultName", new Object[0]), "", false);
            this.minecraft.setScreen((Screen)new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
        }).bounds(this.width / 2 - 50, this.height - 52, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectServer.add"), $$0 -> {
            this.editingServer = new ServerData(I18n.get("selectServer.defaultName", new Object[0]), "", false);
            this.minecraft.setScreen((Screen)new EditServerScreen(this, this::addServerCallback, this.editingServer));
        }).bounds(this.width / 2 + 4 + 50, this.height - 52, 100, 20).build());
        this.editButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.edit"), $$0 -> {
            ServerSelectionList.Entry $$1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
            if ($$1 instanceof ServerSelectionList.OnlineServerEntry) {
                ServerData $$2 = ((ServerSelectionList.OnlineServerEntry)$$1).getServerData();
                this.editingServer = new ServerData($$2.name, $$2.ip, false);
                this.editingServer.copyFrom($$2);
                this.minecraft.setScreen((Screen)new EditServerScreen(this, this::editServerCallback, this.editingServer));
            }
        }).bounds(this.width / 2 - 154, this.height - 28, 70, 20).build());
        this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.delete"), $$0 -> {
            String $$2;
            ServerSelectionList.Entry $$1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
            if ($$1 instanceof ServerSelectionList.OnlineServerEntry && ($$2 = ((ServerSelectionList.OnlineServerEntry)$$1).getServerData().name) != null) {
                MutableComponent $$3 = Component.translatable("selectServer.deleteQuestion");
                MutableComponent $$4 = Component.translatable("selectServer.deleteWarning", $$2);
                MutableComponent $$5 = Component.translatable("selectServer.deleteButton");
                Component $$6 = CommonComponents.GUI_CANCEL;
                this.minecraft.setScreen((Screen)new ConfirmScreen(this::deleteCallback, $$3, $$4, $$5, $$6));
            }
        }).bounds(this.width / 2 - 74, this.height - 28, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectServer.refresh"), $$0 -> this.refreshServerList()).bounds(this.width / 2 + 4, this.height - 28, 70, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 + 4 + 76, this.height - 28, 75, 20).build());
        this.onSelectedChange();
    }

    @Override
    public void tick() {
        super.tick();
        List<LanServer> $$0 = this.lanServerList.takeDirtyServers();
        if ($$0 != null) {
            this.serverSelectionList.updateNetworkServers($$0);
        }
        this.pinger.tick();
    }

    @Override
    public void removed() {
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.pinger.removeAll();
    }

    private void refreshServerList() {
        this.minecraft.setScreen((Screen)new JoinMultiplayerScreen(this.lastScreen));
    }

    private void deleteCallback(boolean $$0) {
        ServerSelectionList.Entry $$1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
        if ($$0 && $$1 instanceof ServerSelectionList.OnlineServerEntry) {
            this.servers.remove(((ServerSelectionList.OnlineServerEntry)$$1).getServerData());
            this.servers.save();
            this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen((Screen)this);
    }

    private void editServerCallback(boolean $$0) {
        ServerSelectionList.Entry $$1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
        if ($$0 && $$1 instanceof ServerSelectionList.OnlineServerEntry) {
            ServerData $$2 = ((ServerSelectionList.OnlineServerEntry)$$1).getServerData();
            $$2.name = this.editingServer.name;
            $$2.ip = this.editingServer.ip;
            $$2.copyFrom(this.editingServer);
            this.servers.save();
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen((Screen)this);
    }

    private void addServerCallback(boolean $$0) {
        if ($$0) {
            ServerData $$1 = this.servers.unhide(this.editingServer.ip);
            if ($$1 != null) {
                $$1.copyNameIconFrom(this.editingServer);
                this.servers.save();
            } else {
                this.servers.add(this.editingServer, false);
                this.servers.save();
            }
            this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }
        this.minecraft.setScreen((Screen)this);
    }

    private void directJoinCallback(boolean $$0) {
        if ($$0) {
            ServerData $$1 = this.servers.get(this.editingServer.ip);
            if ($$1 == null) {
                this.servers.add(this.editingServer, true);
                this.servers.save();
                this.join(this.editingServer);
            } else {
                this.join($$1);
            }
        } else {
            this.minecraft.setScreen((Screen)this);
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if ($$0 == 294) {
            this.refreshServerList();
            return true;
        }
        if (this.serverSelectionList.getSelected() != null) {
            if ($$0 == 257 || $$0 == 335) {
                this.joinSelectedServer();
                return true;
            }
            return this.serverSelectionList.keyPressed($$0, $$1, $$2);
        }
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.toolTip = null;
        this.renderBackground($$0);
        this.serverSelectionList.render($$0, $$1, $$2, $$3);
        JoinMultiplayerScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
        if (this.toolTip != null) {
            this.renderComponentTooltip($$0, this.toolTip, $$1, $$2);
        }
    }

    public void joinSelectedServer() {
        ServerSelectionList.Entry $$0 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
        if ($$0 instanceof ServerSelectionList.OnlineServerEntry) {
            this.join(((ServerSelectionList.OnlineServerEntry)$$0).getServerData());
        } else if ($$0 instanceof ServerSelectionList.NetworkServerEntry) {
            LanServer $$1 = ((ServerSelectionList.NetworkServerEntry)$$0).getServerData();
            this.join(new ServerData($$1.getMotd(), $$1.getAddress(), true));
        }
    }

    private void join(ServerData $$0) {
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString($$0.ip), $$0);
    }

    public void setSelected(ServerSelectionList.Entry $$0) {
        this.serverSelectionList.setSelected($$0);
        this.onSelectedChange();
    }

    protected void onSelectedChange() {
        this.selectButton.active = false;
        this.editButton.active = false;
        this.deleteButton.active = false;
        ServerSelectionList.Entry $$0 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
        if ($$0 != null && !($$0 instanceof ServerSelectionList.LANHeader)) {
            this.selectButton.active = true;
            if ($$0 instanceof ServerSelectionList.OnlineServerEntry) {
                this.editButton.active = true;
                this.deleteButton.active = true;
            }
        }
    }

    public ServerStatusPinger getPinger() {
        return this.pinger;
    }

    public void setToolTip(List<Component> $$0) {
        this.toolTip = $$0;
    }

    public ServerList getServers() {
        return this.servers;
    }
}