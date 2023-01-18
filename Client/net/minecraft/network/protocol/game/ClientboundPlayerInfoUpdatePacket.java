/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoUpdatePacket
implements Packet<ClientGamePacketListener> {
    private final EnumSet<Action> actions;
    private final List<Entry> entries;

    public ClientboundPlayerInfoUpdatePacket(EnumSet<Action> $$0, Collection<ServerPlayer> $$1) {
        this.actions = $$0;
        this.entries = $$1.stream().map(Entry::new).toList();
    }

    public ClientboundPlayerInfoUpdatePacket(Action $$0, ServerPlayer $$1) {
        this.actions = EnumSet.of((Enum)$$0);
        this.entries = List.of((Object)((Object)new Entry($$1)));
    }

    public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> $$0) {
        EnumSet $$1 = EnumSet.of((Enum)Action.ADD_PLAYER, (Enum[])new Action[]{Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LISTED, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME});
        return new ClientboundPlayerInfoUpdatePacket((EnumSet<Action>)$$1, $$0);
    }

    public ClientboundPlayerInfoUpdatePacket(FriendlyByteBuf $$02) {
        this.actions = $$02.readEnumSet(Action.class);
        this.entries = $$02.readList($$0 -> {
            EntryBuilder $$1 = new EntryBuilder($$0.readUUID());
            for (Action $$2 : this.actions) {
                $$2.reader.read($$1, (FriendlyByteBuf)((Object)$$0));
            }
            return $$1.build();
        });
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeEnumSet(this.actions, Action.class);
        $$02.writeCollection(this.entries, ($$0, $$1) -> {
            $$0.writeUUID($$1.profileId());
            for (Action $$2 : this.actions) {
                $$2.writer.write((FriendlyByteBuf)((Object)$$0), (Entry)((Object)$$1));
            }
        });
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handlePlayerInfoUpdate(this);
    }

    public EnumSet<Action> actions() {
        return this.actions;
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public List<Entry> newEntries() {
        return this.actions.contains((Object)Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public record Entry(UUID profileId, GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, @Nullable RemoteChatSession.Data chatSession) {
        Entry(ServerPlayer $$0) {
            this($$0.getUUID(), $$0.getGameProfile(), true, $$0.latency, $$0.gameMode.getGameModeForPlayer(), $$0.getTabListDisplayName(), (RemoteChatSession.Data)((Object)Util.mapNullable($$0.getChatSession(), RemoteChatSession::asData)));
        }
    }

    public static enum Action {
        ADD_PLAYER(($$0, $$1) -> {
            GameProfile $$2 = new GameProfile($$0.profileId, $$1.readUtf(16));
            $$2.getProperties().putAll((Multimap)$$1.readGameProfileProperties());
            $$0.profile = $$2;
        }, ($$0, $$1) -> {
            $$0.writeUtf($$1.profile().getName(), 16);
            $$0.writeGameProfileProperties($$1.profile().getProperties());
        }),
        INITIALIZE_CHAT(($$0, $$1) -> {
            $$0.chatSession = (RemoteChatSession.Data)((Object)((Object)$$1.readNullable(RemoteChatSession.Data::read)));
        }, ($$0, $$1) -> $$0.writeNullable($$1.chatSession, RemoteChatSession.Data::write)),
        UPDATE_GAME_MODE(($$0, $$1) -> {
            $$0.gameMode = GameType.byId($$1.readVarInt());
        }, ($$0, $$1) -> $$0.writeVarInt($$1.gameMode().getId())),
        UPDATE_LISTED(($$0, $$1) -> {
            $$0.listed = $$1.readBoolean();
        }, ($$0, $$1) -> $$0.writeBoolean($$1.listed())),
        UPDATE_LATENCY(($$0, $$1) -> {
            $$0.latency = $$1.readVarInt();
        }, ($$0, $$1) -> $$0.writeVarInt($$1.latency())),
        UPDATE_DISPLAY_NAME(($$0, $$1) -> {
            $$0.displayName = (Component)$$1.readNullable(FriendlyByteBuf::readComponent);
        }, ($$0, $$1) -> $$0.writeNullable($$1.displayName(), FriendlyByteBuf::writeComponent));

        final Reader reader;
        final Writer writer;

        private Action(Reader $$0, Writer $$1) {
            this.reader = $$0;
            this.writer = $$1;
        }

        public static interface Reader {
            public void read(EntryBuilder var1, FriendlyByteBuf var2);
        }

        public static interface Writer {
            public void write(FriendlyByteBuf var1, Entry var2);
        }
    }

    static class EntryBuilder {
        final UUID profileId;
        GameProfile profile;
        boolean listed;
        int latency;
        GameType gameMode = GameType.DEFAULT_MODE;
        @Nullable
        Component displayName;
        @Nullable
        RemoteChatSession.Data chatSession;

        EntryBuilder(UUID $$0) {
            this.profileId = $$0;
            this.profile = new GameProfile($$0, null);
        }

        Entry build() {
            return new Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.chatSession);
        }
    }
}