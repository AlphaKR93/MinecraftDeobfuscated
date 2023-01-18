/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;

public class DebugQueryHandler {
    private final ClientPacketListener connection;
    private int transactionId = -1;
    @Nullable
    private Consumer<CompoundTag> callback;

    public DebugQueryHandler(ClientPacketListener $$0) {
        this.connection = $$0;
    }

    public boolean handleResponse(int $$0, @Nullable CompoundTag $$1) {
        if (this.transactionId == $$0 && this.callback != null) {
            this.callback.accept((Object)$$1);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int startTransaction(Consumer<CompoundTag> $$0) {
        this.callback = $$0;
        return ++this.transactionId;
    }

    public void queryEntityTag(int $$0, Consumer<CompoundTag> $$1) {
        int $$2 = this.startTransaction($$1);
        this.connection.send(new ServerboundEntityTagQuery($$2, $$0));
    }

    public void queryBlockEntityTag(BlockPos $$0, Consumer<CompoundTag> $$1) {
        int $$2 = this.startTransaction($$1);
        this.connection.send(new ServerboundBlockEntityTagQuery($$2, $$0));
    }
}