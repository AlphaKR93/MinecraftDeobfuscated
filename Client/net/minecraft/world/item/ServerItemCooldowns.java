/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

public class ServerItemCooldowns
extends ItemCooldowns {
    private final ServerPlayer player;

    public ServerItemCooldowns(ServerPlayer $$0) {
        this.player = $$0;
    }

    @Override
    protected void onCooldownStarted(Item $$0, int $$1) {
        super.onCooldownStarted($$0, $$1);
        this.player.connection.send(new ClientboundCooldownPacket($$0, $$1));
    }

    @Override
    protected void onCooldownEnded(Item $$0) {
        super.onCooldownEnded($$0);
        this.player.connection.send(new ClientboundCooldownPacket($$0, 0));
    }
}