/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Objects
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BinaryOperator
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack
implements SharedSuggestionProvider {
    public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType((Message)Component.translatable("permissions.requires.player"));
    public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType((Message)Component.translatable("permissions.requires.entity"));
    private final CommandSource source;
    private final Vec3 worldPosition;
    private final ServerLevel level;
    private final int permissionLevel;
    private final String textName;
    private final Component displayName;
    private final MinecraftServer server;
    private final boolean silent;
    @Nullable
    private final Entity entity;
    @Nullable
    private final ResultConsumer<CommandSourceStack> consumer;
    private final EntityAnchorArgument.Anchor anchor;
    private final Vec2 rotation;
    private final CommandSigningContext signingContext;
    private final TaskChainer chatMessageChainer;

    public CommandSourceStack(CommandSource $$02, Vec3 $$12, Vec2 $$22, ServerLevel $$3, int $$4, String $$5, Component $$6, MinecraftServer $$7, @Nullable Entity $$8) {
        this($$02, $$12, $$22, $$3, $$4, $$5, $$6, $$7, $$8, false, (ResultConsumer<CommandSourceStack>)((ResultConsumer)($$0, $$1, $$2) -> {}), EntityAnchorArgument.Anchor.FEET, CommandSigningContext.ANONYMOUS, TaskChainer.immediate($$7));
    }

    protected CommandSourceStack(CommandSource $$0, Vec3 $$1, Vec2 $$2, ServerLevel $$3, int $$4, String $$5, Component $$6, MinecraftServer $$7, @Nullable Entity $$8, boolean $$9, @Nullable ResultConsumer<CommandSourceStack> $$10, EntityAnchorArgument.Anchor $$11, CommandSigningContext $$12, TaskChainer $$13) {
        this.source = $$0;
        this.worldPosition = $$1;
        this.level = $$3;
        this.silent = $$9;
        this.entity = $$8;
        this.permissionLevel = $$4;
        this.textName = $$5;
        this.displayName = $$6;
        this.server = $$7;
        this.consumer = $$10;
        this.anchor = $$11;
        this.rotation = $$2;
        this.signingContext = $$12;
        this.chatMessageChainer = $$13;
    }

    public CommandSourceStack withSource(CommandSource $$0) {
        if (this.source == $$0) {
            return this;
        }
        return new CommandSourceStack($$0, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withEntity(Entity $$0) {
        if (this.entity == $$0) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, $$0.getName().getString(), $$0.getDisplayName(), this.server, $$0, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withPosition(Vec3 $$0) {
        if (this.worldPosition.equals($$0)) {
            return this;
        }
        return new CommandSourceStack(this.source, $$0, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withRotation(Vec2 $$0) {
        if (this.rotation.equals($$0)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, $$0, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> $$0) {
        if (Objects.equals(this.consumer, $$0)) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, $$0, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> $$0, BinaryOperator<ResultConsumer<CommandSourceStack>> $$1) {
        ResultConsumer $$2 = (ResultConsumer)$$1.apply(this.consumer, $$0);
        return this.withCallback((ResultConsumer<CommandSourceStack>)$$2);
    }

    public CommandSourceStack withSuppressedOutput() {
        if (this.silent || this.source.alwaysAccepts()) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withPermission(int $$0) {
        if ($$0 == this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, $$0, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withMaximumPermission(int $$0) {
        if ($$0 <= this.permissionLevel) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, $$0, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor $$0) {
        if ($$0 == this.anchor) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, $$0, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack withLevel(ServerLevel $$0) {
        if ($$0 == this.level) {
            return this;
        }
        double $$1 = DimensionType.getTeleportationScale(this.level.dimensionType(), $$0.dimensionType());
        Vec3 $$2 = new Vec3(this.worldPosition.x * $$1, this.worldPosition.y, this.worldPosition.z * $$1);
        return new CommandSourceStack(this.source, $$2, this.rotation, $$0, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, this.chatMessageChainer);
    }

    public CommandSourceStack facing(Entity $$0, EntityAnchorArgument.Anchor $$1) {
        return this.facing($$1.apply($$0));
    }

    public CommandSourceStack facing(Vec3 $$0) {
        Vec3 $$1 = this.anchor.apply(this);
        double $$2 = $$0.x - $$1.x;
        double $$3 = $$0.y - $$1.y;
        double $$4 = $$0.z - $$1.z;
        double $$5 = Math.sqrt((double)($$2 * $$2 + $$4 * $$4));
        float $$6 = Mth.wrapDegrees((float)(-(Mth.atan2($$3, $$5) * 57.2957763671875)));
        float $$7 = Mth.wrapDegrees((float)(Mth.atan2($$4, $$2) * 57.2957763671875) - 90.0f);
        return this.withRotation(new Vec2($$6, $$7));
    }

    public CommandSourceStack withSigningContext(CommandSigningContext $$0) {
        if ($$0 == this.signingContext) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, $$0, this.chatMessageChainer);
    }

    public CommandSourceStack withChatMessageChainer(TaskChainer $$0) {
        if ($$0 == this.chatMessageChainer) {
            return this;
        }
        return new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor, this.signingContext, $$0);
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public String getTextName() {
        return this.textName;
    }

    @Override
    public boolean hasPermission(int $$0) {
        return this.permissionLevel >= $$0;
    }

    public Vec3 getPosition() {
        return this.worldPosition;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public Entity getEntityOrException() throws CommandSyntaxException {
        if (this.entity == null) {
            throw ERROR_NOT_ENTITY.create();
        }
        return this.entity;
    }

    public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
        Entity entity = this.entity;
        if (entity instanceof ServerPlayer) {
            ServerPlayer $$0 = (ServerPlayer)entity;
            return $$0;
        }
        throw ERROR_NOT_PLAYER.create();
    }

    @Nullable
    public ServerPlayer getPlayer() {
        ServerPlayer $$0;
        Entity entity = this.entity;
        return entity instanceof ServerPlayer ? ($$0 = (ServerPlayer)entity) : null;
    }

    public boolean isPlayer() {
        return this.entity instanceof ServerPlayer;
    }

    public Vec2 getRotation() {
        return this.rotation;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public EntityAnchorArgument.Anchor getAnchor() {
        return this.anchor;
    }

    public CommandSigningContext getSigningContext() {
        return this.signingContext;
    }

    public TaskChainer getChatMessageChainer() {
        return this.chatMessageChainer;
    }

    public boolean shouldFilterMessageTo(ServerPlayer $$0) {
        ServerPlayer $$1 = this.getPlayer();
        if ($$0 == $$1) {
            return false;
        }
        return $$1 != null && $$1.isTextFilteringEnabled() || $$0.isTextFilteringEnabled();
    }

    public void sendChatMessage(OutgoingChatMessage $$0, boolean $$1, ChatType.Bound $$2) {
        if (this.silent) {
            return;
        }
        ServerPlayer $$3 = this.getPlayer();
        if ($$3 != null) {
            $$3.sendChatMessage($$0, $$1, $$2);
        } else {
            this.source.sendSystemMessage($$2.decorate($$0.content()));
        }
    }

    public void sendSystemMessage(Component $$0) {
        if (this.silent) {
            return;
        }
        ServerPlayer $$1 = this.getPlayer();
        if ($$1 != null) {
            $$1.sendSystemMessage($$0);
        } else {
            this.source.sendSystemMessage($$0);
        }
    }

    public void sendSuccess(Component $$0, boolean $$1) {
        if (this.source.acceptsSuccess() && !this.silent) {
            this.source.sendSystemMessage($$0);
        }
        if ($$1 && this.source.shouldInformAdmins() && !this.silent) {
            this.broadcastToAdmins($$0);
        }
    }

    private void broadcastToAdmins(Component $$0) {
        MutableComponent $$1 = Component.translatable("chat.type.admin", this.getDisplayName(), $$0).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            for (ServerPlayer $$2 : this.server.getPlayerList().getPlayers()) {
                if ($$2 == this.source || !this.server.getPlayerList().isOp($$2.getGameProfile())) continue;
                $$2.sendSystemMessage($$1);
            }
        }
        if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            this.server.sendSystemMessage($$1);
        }
    }

    public void sendFailure(Component $$0) {
        if (this.source.acceptsFailure() && !this.silent) {
            this.source.sendSystemMessage(Component.empty().append($$0).withStyle(ChatFormatting.RED));
        }
    }

    public void onCommandComplete(CommandContext<CommandSourceStack> $$0, boolean $$1, int $$2) {
        if (this.consumer != null) {
            this.consumer.onCommandComplete($$0, $$1, $$2);
        }
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Lists.newArrayList((Object[])this.server.getPlayerNames());
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.server.getScoreboard().getTeamNames();
    }

    @Override
    public Stream<ResourceLocation> getAvailableSounds() {
        return BuiltInRegistries.SOUND_EVENT.stream().map(SoundEvent::getLocation);
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return this.server.getRecipeManager().getRecipeIds();
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> $$0) {
        return Suggestions.empty();
    }

    @Override
    public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> $$0, SharedSuggestionProvider.ElementSuggestionType $$1, SuggestionsBuilder $$22, CommandContext<?> $$3) {
        return (CompletableFuture)this.registryAccess().registry($$0).map($$2 -> {
            this.suggestRegistryElements((Registry)$$2, $$1, $$22);
            return $$22.buildFuture();
        }).orElseGet(Suggestions::empty);
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.server.levelKeys();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.server.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.level.enabledFeatures();
    }
}