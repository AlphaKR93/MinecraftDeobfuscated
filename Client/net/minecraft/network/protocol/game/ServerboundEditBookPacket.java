/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundEditBookPacket
implements Packet<ServerGamePacketListener> {
    public static final int MAX_BYTES_PER_CHAR = 4;
    private static final int TITLE_MAX_CHARS = 128;
    private static final int PAGE_MAX_CHARS = 8192;
    private static final int MAX_PAGES_COUNT = 200;
    private final int slot;
    private final List<String> pages;
    private final Optional<String> title;

    public ServerboundEditBookPacket(int $$0, List<String> $$1, Optional<String> $$2) {
        this.slot = $$0;
        this.pages = ImmutableList.copyOf($$1);
        this.title = $$2;
    }

    public ServerboundEditBookPacket(FriendlyByteBuf $$02) {
        this.slot = $$02.readVarInt();
        this.pages = (List)$$02.readCollection(FriendlyByteBuf.limitValue(Lists::newArrayListWithCapacity, 200), $$0 -> $$0.readUtf(8192));
        this.title = $$02.readOptional($$0 -> $$0.readUtf(128));
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeVarInt(this.slot);
        $$02.writeCollection(this.pages, ($$0, $$1) -> $$0.writeUtf((String)$$1, 8192));
        $$02.writeOptional(this.title, ($$0, $$1) -> $$0.writeUtf((String)$$1, 128));
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleEditBook(this);
    }

    public List<String> getPages() {
        return this.pages;
    }

    public Optional<String> getTitle() {
        return this.title;
    }

    public int getSlot() {
        return this.slot;
    }
}