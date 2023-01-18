/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket
implements Packet<ClientGamePacketListener> {
    private final int containerId;
    private final MerchantOffers offers;
    private final int villagerLevel;
    private final int villagerXp;
    private final boolean showProgress;
    private final boolean canRestock;

    public ClientboundMerchantOffersPacket(int $$0, MerchantOffers $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
        this.containerId = $$0;
        this.offers = $$1;
        this.villagerLevel = $$2;
        this.villagerXp = $$3;
        this.showProgress = $$4;
        this.canRestock = $$5;
    }

    public ClientboundMerchantOffersPacket(FriendlyByteBuf $$0) {
        this.containerId = $$0.readVarInt();
        this.offers = MerchantOffers.createFromStream($$0);
        this.villagerLevel = $$0.readVarInt();
        this.villagerXp = $$0.readVarInt();
        this.showProgress = $$0.readBoolean();
        this.canRestock = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.containerId);
        this.offers.writeToStream($$0);
        $$0.writeVarInt(this.villagerLevel);
        $$0.writeVarInt(this.villagerXp);
        $$0.writeBoolean(this.showProgress);
        $$0.writeBoolean(this.canRestock);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleMerchantOffers(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MerchantOffers getOffers() {
        return this.offers;
    }

    public int getVillagerLevel() {
        return this.villagerLevel;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public boolean showProgress() {
        return this.showProgress;
    }

    public boolean canRestock() {
        return this.canRestock;
    }
}