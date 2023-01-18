/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
    public static final Codec<ChatType> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat), (App)ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)).apply((Applicative)$$0, ChatType::new));
    public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
    public static final ResourceKey<ChatType> CHAT = ChatType.create("chat");
    public static final ResourceKey<ChatType> SAY_COMMAND = ChatType.create("say_command");
    public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING = ChatType.create("msg_command_incoming");
    public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING = ChatType.create("msg_command_outgoing");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING = ChatType.create("team_msg_command_incoming");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING = ChatType.create("team_msg_command_outgoing");
    public static final ResourceKey<ChatType> EMOTE_COMMAND = ChatType.create("emote_command");

    private static ResourceKey<ChatType> create(String $$0) {
        return ResourceKey.create(Registries.CHAT_TYPE, new ResourceLocation($$0));
    }

    public static void bootstrap(BootstapContext<ChatType> $$0) {
        $$0.register(CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(TEAM_MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(TEAM_MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        $$0.register(EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote")));
    }

    public static Bound bind(ResourceKey<ChatType> $$0, Entity $$1) {
        return ChatType.bind($$0, $$1.level.registryAccess(), $$1.getDisplayName());
    }

    public static Bound bind(ResourceKey<ChatType> $$0, CommandSourceStack $$1) {
        return ChatType.bind($$0, $$1.registryAccess(), $$1.getDisplayName());
    }

    public static Bound bind(ResourceKey<ChatType> $$0, RegistryAccess $$1, Component $$2) {
        Registry<ChatType> $$3 = $$1.registryOrThrow(Registries.CHAT_TYPE);
        return $$3.getOrThrow($$0).bind($$2);
    }

    public Bound bind(Component $$0) {
        return new Bound(this, $$0);
    }

    public record Bound(ChatType chatType, Component name, @Nullable Component targetName) {
        Bound(ChatType $$0, Component $$1) {
            this($$0, $$1, null);
        }

        public Component decorate(Component $$0) {
            return this.chatType.chat().decorate($$0, this);
        }

        public Component decorateNarration(Component $$0) {
            return this.chatType.narration().decorate($$0, this);
        }

        public Bound withTargetName(Component $$0) {
            return new Bound(this.chatType, this.name, $$0);
        }

        public BoundNetwork toNetwork(RegistryAccess $$0) {
            Registry<ChatType> $$1 = $$0.registryOrThrow(Registries.CHAT_TYPE);
            return new BoundNetwork($$1.getId(this.chatType), this.name, this.targetName);
        }
    }

    public record BoundNetwork(int chatType, Component name, @Nullable Component targetName) {
        public BoundNetwork(FriendlyByteBuf $$0) {
            this($$0.readVarInt(), $$0.readComponent(), (Component)$$0.readNullable(FriendlyByteBuf::readComponent));
        }

        public void write(FriendlyByteBuf $$0) {
            $$0.writeVarInt(this.chatType);
            $$0.writeComponent(this.name);
            $$0.writeNullable(this.targetName, FriendlyByteBuf::writeComponent);
        }

        public Optional<Bound> resolve(RegistryAccess $$02) {
            Registry<ChatType> $$1 = $$02.registryOrThrow(Registries.CHAT_TYPE);
            ChatType $$2 = (ChatType)((Object)$$1.byId(this.chatType));
            return Optional.ofNullable((Object)((Object)$$2)).map($$0 -> new Bound((ChatType)((Object)$$0), this.name, this.targetName));
        }
    }
}