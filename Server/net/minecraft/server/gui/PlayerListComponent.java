/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Vector
 *  javax.swing.JList
 */
package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerListComponent
extends JList<String> {
    private final MinecraftServer server;
    private int tickCount;

    public PlayerListComponent(MinecraftServer $$0) {
        this.server = $$0;
        $$0.addTickable(this::tick);
    }

    public void tick() {
        if (this.tickCount++ % 20 == 0) {
            Vector $$0 = new Vector();
            for (int $$1 = 0; $$1 < this.server.getPlayerList().getPlayers().size(); ++$$1) {
                $$0.add((Object)((ServerPlayer)this.server.getPlayerList().getPlayers().get($$1)).getGameProfile().getName());
            }
            this.setListData($$0);
        }
    }
}