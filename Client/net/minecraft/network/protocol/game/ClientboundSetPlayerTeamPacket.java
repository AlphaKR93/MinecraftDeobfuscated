/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.PlayerTeam;

public class ClientboundSetPlayerTeamPacket
implements Packet<ClientGamePacketListener> {
    private static final int METHOD_ADD = 0;
    private static final int METHOD_REMOVE = 1;
    private static final int METHOD_CHANGE = 2;
    private static final int METHOD_JOIN = 3;
    private static final int METHOD_LEAVE = 4;
    private static final int MAX_VISIBILITY_LENGTH = 40;
    private static final int MAX_COLLISION_LENGTH = 40;
    private final int method;
    private final String name;
    private final Collection<String> players;
    private final Optional<Parameters> parameters;

    private ClientboundSetPlayerTeamPacket(String $$0, int $$1, Optional<Parameters> $$2, Collection<String> $$3) {
        this.name = $$0;
        this.method = $$1;
        this.parameters = $$2;
        this.players = ImmutableList.copyOf($$3);
    }

    public static ClientboundSetPlayerTeamPacket createAddOrModifyPacket(PlayerTeam $$0, boolean $$1) {
        return new ClientboundSetPlayerTeamPacket($$0.getName(), $$1 ? 0 : 2, (Optional<Parameters>)Optional.of((Object)new Parameters($$0)), $$1 ? $$0.getPlayers() : ImmutableList.of());
    }

    public static ClientboundSetPlayerTeamPacket createRemovePacket(PlayerTeam $$0) {
        return new ClientboundSetPlayerTeamPacket($$0.getName(), 1, (Optional<Parameters>)Optional.empty(), (Collection<String>)ImmutableList.of());
    }

    public static ClientboundSetPlayerTeamPacket createPlayerPacket(PlayerTeam $$0, String $$1, Action $$2) {
        return new ClientboundSetPlayerTeamPacket($$0.getName(), $$2 == Action.ADD ? 3 : 4, (Optional<Parameters>)Optional.empty(), (Collection<String>)ImmutableList.of((Object)$$1));
    }

    public ClientboundSetPlayerTeamPacket(FriendlyByteBuf $$0) {
        this.name = $$0.readUtf();
        this.method = $$0.readByte();
        this.parameters = ClientboundSetPlayerTeamPacket.shouldHaveParameters(this.method) ? Optional.of((Object)new Parameters($$0)) : Optional.empty();
        this.players = ClientboundSetPlayerTeamPacket.shouldHavePlayerList(this.method) ? $$0.readList(FriendlyByteBuf::readUtf) : ImmutableList.of();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.name);
        $$0.writeByte(this.method);
        if (ClientboundSetPlayerTeamPacket.shouldHaveParameters(this.method)) {
            ((Parameters)this.parameters.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.method))).write($$0);
        }
        if (ClientboundSetPlayerTeamPacket.shouldHavePlayerList(this.method)) {
            $$0.writeCollection(this.players, FriendlyByteBuf::writeUtf);
        }
    }

    private static boolean shouldHavePlayerList(int $$0) {
        return $$0 == 0 || $$0 == 3 || $$0 == 4;
    }

    private static boolean shouldHaveParameters(int $$0) {
        return $$0 == 0 || $$0 == 2;
    }

    @Nullable
    public Action getPlayerAction() {
        switch (this.method) {
            case 0: 
            case 3: {
                return Action.ADD;
            }
            case 4: {
                return Action.REMOVE;
            }
        }
        return null;
    }

    @Nullable
    public Action getTeamAction() {
        switch (this.method) {
            case 0: {
                return Action.ADD;
            }
            case 1: {
                return Action.REMOVE;
            }
        }
        return null;
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleSetPlayerTeamPacket(this);
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public Optional<Parameters> getParameters() {
        return this.parameters;
    }

    public static class Parameters {
        private final Component displayName;
        private final Component playerPrefix;
        private final Component playerSuffix;
        private final String nametagVisibility;
        private final String collisionRule;
        private final ChatFormatting color;
        private final int options;

        public Parameters(PlayerTeam $$0) {
            this.displayName = $$0.getDisplayName();
            this.options = $$0.packOptions();
            this.nametagVisibility = $$0.getNameTagVisibility().name;
            this.collisionRule = $$0.getCollisionRule().name;
            this.color = $$0.getColor();
            this.playerPrefix = $$0.getPlayerPrefix();
            this.playerSuffix = $$0.getPlayerSuffix();
        }

        public Parameters(FriendlyByteBuf $$0) {
            this.displayName = $$0.readComponent();
            this.options = $$0.readByte();
            this.nametagVisibility = $$0.readUtf(40);
            this.collisionRule = $$0.readUtf(40);
            this.color = $$0.readEnum(ChatFormatting.class);
            this.playerPrefix = $$0.readComponent();
            this.playerSuffix = $$0.readComponent();
        }

        public Component getDisplayName() {
            return this.displayName;
        }

        public int getOptions() {
            return this.options;
        }

        public ChatFormatting getColor() {
            return this.color;
        }

        public String getNametagVisibility() {
            return this.nametagVisibility;
        }

        public String getCollisionRule() {
            return this.collisionRule;
        }

        public Component getPlayerPrefix() {
            return this.playerPrefix;
        }

        public Component getPlayerSuffix() {
            return this.playerSuffix;
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeComponent(this.displayName);
            $$0.writeByte(this.options);
            $$0.writeUtf(this.nametagVisibility);
            $$0.writeUtf(this.collisionRule);
            $$0.writeEnum(this.color);
            $$0.writeComponent(this.playerPrefix);
            $$0.writeComponent(this.playerSuffix);
        }
    }

    public static enum Action {
        ADD,
        REMOVE;

    }
}