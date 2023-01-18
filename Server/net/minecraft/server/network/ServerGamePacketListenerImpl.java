/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Floats
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Double
 *  java.lang.Enum
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.time.Duration
 *  java.time.Instant
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.EnumSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CancellationException
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.UnaryOperator
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.server.network.TextFilter;
import net.minecraft.util.FutureChain;
import net.minecraft.util.Mth;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class ServerGamePacketListenerImpl
implements ServerPlayerConnection,
TickablePacketListener,
ServerGamePacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int LATENCY_CHECK_INTERVAL = 15000;
    public static final double MAX_INTERACTION_DISTANCE = Mth.square(6.0);
    private static final int NO_BLOCK_UPDATES_TO_ACK = -1;
    private static final int TRACKED_MESSAGE_DISCONNECT_THRESHOLD = 4096;
    private static final Component CHAT_VALIDATION_FAILED = Component.translatable("multiplayer.disconnect.chat_validation_failed");
    public final Connection connection;
    private final MinecraftServer server;
    public ServerPlayer player;
    private int tickCount;
    private int ackBlockChangesUpTo = -1;
    private long keepAliveTime;
    private boolean keepAlivePending;
    private long keepAliveChallenge;
    private int chatSpamTickCount;
    private int dropSpamTickCount;
    private double firstGoodX;
    private double firstGoodY;
    private double firstGoodZ;
    private double lastGoodX;
    private double lastGoodY;
    private double lastGoodZ;
    @Nullable
    private Entity lastVehicle;
    private double vehicleFirstGoodX;
    private double vehicleFirstGoodY;
    private double vehicleFirstGoodZ;
    private double vehicleLastGoodX;
    private double vehicleLastGoodY;
    private double vehicleLastGoodZ;
    @Nullable
    private Vec3 awaitingPositionFromClient;
    private int awaitingTeleport;
    private int awaitingTeleportTime;
    private boolean clientIsFloating;
    private int aboveGroundTickCount;
    private boolean clientVehicleIsFloating;
    private int aboveGroundVehicleTickCount;
    private int receivedMovePacketCount;
    private int knownMovePacketCount;
    private final AtomicReference<Instant> lastChatTimeStamp = new AtomicReference((Object)Instant.EPOCH);
    @Nullable
    private RemoteChatSession chatSession;
    private SignedMessageChain.Decoder signedMessageDecoder;
    private final LastSeenMessagesValidator lastSeenMessages = new LastSeenMessagesValidator(20);
    private final MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
    private final FutureChain chatMessageChain;

    public ServerGamePacketListenerImpl(MinecraftServer $$0, Connection $$1, ServerPlayer $$2) {
        this.server = $$0;
        this.connection = $$1;
        $$1.setListener(this);
        this.player = $$2;
        $$2.connection = this;
        this.keepAliveTime = Util.getMillis();
        $$2.getTextFilter().join();
        this.signedMessageDecoder = $$0.enforceSecureProfile() ? SignedMessageChain.Decoder.REJECT_ALL : SignedMessageChain.Decoder.unsigned($$2.getUUID());
        this.chatMessageChain = new FutureChain($$0);
    }

    @Override
    public void tick() {
        if (this.ackBlockChangesUpTo > -1) {
            this.send(new ClientboundBlockChangedAckPacket(this.ackBlockChangesUpTo));
            this.ackBlockChangesUpTo = -1;
        }
        this.resetPosition();
        this.player.xo = this.player.getX();
        this.player.yo = this.player.getY();
        this.player.zo = this.player.getZ();
        this.player.doTick();
        this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
        ++this.tickCount;
        this.knownMovePacketCount = this.receivedMovePacketCount;
        if (this.clientIsFloating && !this.player.isSleeping() && !this.player.isPassenger()) {
            if (++this.aboveGroundTickCount > 80) {
                LOGGER.warn("{} was kicked for floating too long!", (Object)this.player.getName().getString());
                this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.clientIsFloating = false;
            this.aboveGroundTickCount = 0;
        }
        this.lastVehicle = this.player.getRootVehicle();
        if (this.lastVehicle == this.player || this.lastVehicle.getControllingPassenger() != this.player) {
            this.lastVehicle = null;
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
        } else {
            this.vehicleFirstGoodX = this.lastVehicle.getX();
            this.vehicleFirstGoodY = this.lastVehicle.getY();
            this.vehicleFirstGoodZ = this.lastVehicle.getZ();
            this.vehicleLastGoodX = this.lastVehicle.getX();
            this.vehicleLastGoodY = this.lastVehicle.getY();
            this.vehicleLastGoodZ = this.lastVehicle.getZ();
            if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
                if (++this.aboveGroundVehicleTickCount > 80) {
                    LOGGER.warn("{} was kicked for floating a vehicle too long!", (Object)this.player.getName().getString());
                    this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
                    return;
                }
            } else {
                this.clientVehicleIsFloating = false;
                this.aboveGroundVehicleTickCount = 0;
            }
        }
        this.server.getProfiler().push("keepAlive");
        long $$0 = Util.getMillis();
        if ($$0 - this.keepAliveTime >= 15000L) {
            if (this.keepAlivePending) {
                this.disconnect(Component.translatable("disconnect.timeout"));
            } else {
                this.keepAlivePending = true;
                this.keepAliveTime = $$0;
                this.keepAliveChallenge = $$0;
                this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
            }
        }
        this.server.getProfiler().pop();
        if (this.chatSpamTickCount > 0) {
            --this.chatSpamTickCount;
        }
        if (this.dropSpamTickCount > 0) {
            --this.dropSpamTickCount;
        }
        if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
            this.disconnect(Component.translatable("multiplayer.disconnect.idling"));
        }
    }

    public void resetPosition() {
        this.firstGoodX = this.player.getX();
        this.firstGoodY = this.player.getY();
        this.firstGoodZ = this.player.getZ();
        this.lastGoodX = this.player.getX();
        this.lastGoodY = this.player.getY();
        this.lastGoodZ = this.player.getZ();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    private boolean isSingleplayerOwner() {
        return this.server.isSingleplayerOwner(this.player.getGameProfile());
    }

    public void disconnect(Component $$0) {
        this.connection.send(new ClientboundDisconnectPacket($$0), PacketSendListener.thenRun(() -> this.connection.disconnect($$0)));
        this.connection.setReadOnly();
        this.server.executeBlocking(this.connection::handleDisconnection);
    }

    private <T, R> CompletableFuture<R> filterTextPacket(T $$02, BiFunction<TextFilter, T, CompletableFuture<R>> $$1) {
        return ((CompletableFuture)$$1.apply((Object)this.player.getTextFilter(), $$02)).thenApply($$0 -> {
            if (!this.getConnection().isConnected()) {
                LOGGER.debug("Ignoring packet due to disconnection");
                throw new CancellationException("disconnected");
            }
            return $$0;
        });
    }

    private CompletableFuture<FilteredText> filterTextPacket(String $$0) {
        return this.filterTextPacket($$0, TextFilter::processStreamMessage);
    }

    private CompletableFuture<List<FilteredText>> filterTextPacket(List<String> $$0) {
        return this.filterTextPacket($$0, TextFilter::processMessageBundle);
    }

    @Override
    public void handlePlayerInput(ServerboundPlayerInputPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.setPlayerInput($$0.getXxa(), $$0.getZza(), $$0.isJumping(), $$0.isShiftKeyDown());
    }

    private static boolean containsInvalidValues(double $$0, double $$1, double $$2, float $$3, float $$4) {
        return Double.isNaN((double)$$0) || Double.isNaN((double)$$1) || Double.isNaN((double)$$2) || !Floats.isFinite((float)$$4) || !Floats.isFinite((float)$$3);
    }

    private static double clampHorizontal(double $$0) {
        return Mth.clamp($$0, -3.0E7, 3.0E7);
    }

    private static double clampVertical(double $$0) {
        return Mth.clamp($$0, -2.0E7, 2.0E7);
    }

    @Override
    public void handleMoveVehicle(ServerboundMoveVehiclePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (ServerGamePacketListenerImpl.containsInvalidValues($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getYRot(), $$0.getXRot())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
            return;
        }
        Entity $$1 = this.player.getRootVehicle();
        if ($$1 != this.player && $$1.getControllingPassenger() == this.player && $$1 == this.lastVehicle) {
            ServerLevel $$2 = this.player.getLevel();
            double $$3 = $$1.getX();
            double $$4 = $$1.getY();
            double $$5 = $$1.getZ();
            double $$6 = ServerGamePacketListenerImpl.clampHorizontal($$0.getX());
            double $$7 = ServerGamePacketListenerImpl.clampVertical($$0.getY());
            double $$8 = ServerGamePacketListenerImpl.clampHorizontal($$0.getZ());
            float $$9 = Mth.wrapDegrees($$0.getYRot());
            float $$10 = Mth.wrapDegrees($$0.getXRot());
            double $$11 = $$6 - this.vehicleFirstGoodX;
            double $$12 = $$7 - this.vehicleFirstGoodY;
            double $$13 = $$8 - this.vehicleFirstGoodZ;
            double $$15 = $$11 * $$11 + $$12 * $$12 + $$13 * $$13;
            double $$14 = $$1.getDeltaMovement().lengthSqr();
            if ($$15 - $$14 > 100.0 && !this.isSingleplayerOwner()) {
                LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", new Object[]{$$1.getName().getString(), this.player.getName().getString(), $$11, $$12, $$13});
                this.connection.send(new ClientboundMoveVehiclePacket($$1));
                return;
            }
            boolean $$16 = $$2.noCollision($$1, $$1.getBoundingBox().deflate(0.0625));
            $$11 = $$6 - this.vehicleLastGoodX;
            $$12 = $$7 - this.vehicleLastGoodY - 1.0E-6;
            $$13 = $$8 - this.vehicleLastGoodZ;
            boolean $$17 = $$1.verticalCollisionBelow;
            $$1.move(MoverType.PLAYER, new Vec3($$11, $$12, $$13));
            double $$18 = $$12;
            $$11 = $$6 - $$1.getX();
            $$12 = $$7 - $$1.getY();
            if ($$12 > -0.5 || $$12 < 0.5) {
                $$12 = 0.0;
            }
            $$13 = $$8 - $$1.getZ();
            $$15 = $$11 * $$11 + $$12 * $$12 + $$13 * $$13;
            boolean $$19 = false;
            if ($$15 > 0.0625) {
                $$19 = true;
                LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", new Object[]{$$1.getName().getString(), this.player.getName().getString(), Math.sqrt((double)$$15)});
            }
            $$1.absMoveTo($$6, $$7, $$8, $$9, $$10);
            boolean $$20 = $$2.noCollision($$1, $$1.getBoundingBox().deflate(0.0625));
            if ($$16 && ($$19 || !$$20)) {
                $$1.absMoveTo($$3, $$4, $$5, $$9, $$10);
                this.connection.send(new ClientboundMoveVehiclePacket($$1));
                return;
            }
            this.player.getLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.getX() - $$3, this.player.getY() - $$4, this.player.getZ() - $$5);
            this.clientVehicleIsFloating = $$18 >= -0.03125 && !$$17 && !this.server.isFlightAllowed() && !$$1.isNoGravity() && this.noBlocksAround($$1);
            this.vehicleLastGoodX = $$1.getX();
            this.vehicleLastGoodY = $$1.getY();
            this.vehicleLastGoodZ = $$1.getZ();
        }
    }

    private boolean noBlocksAround(Entity $$0) {
        return $$0.level.getBlockStates($$0.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)).allMatch(BlockBehaviour.BlockStateBase::isAir);
    }

    @Override
    public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if ($$0.getId() == this.awaitingTeleport) {
            if (this.awaitingPositionFromClient == null) {
                this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
            this.lastGoodX = this.awaitingPositionFromClient.x;
            this.lastGoodY = this.awaitingPositionFromClient.y;
            this.lastGoodZ = this.awaitingPositionFromClient.z;
            if (this.player.isChangingDimension()) {
                this.player.hasChangedDimension();
            }
            this.awaitingPositionFromClient = null;
        }
    }

    @Override
    public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.server.getRecipeManager().byKey($$0.getRecipe()).ifPresent(this.player.getRecipeBook()::removeHighlight);
    }

    @Override
    public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.getRecipeBook().setBookSetting($$0.getBookType(), $$0.isOpen(), $$0.isFiltering());
    }

    @Override
    public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if ($$0.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
            ResourceLocation $$1 = $$0.getTab();
            Advancement $$2 = this.server.getAdvancements().getAdvancement($$1);
            if ($$2 != null) {
                this.player.getAdvancements().setSelectedTab($$2);
            }
        }
    }

    @Override
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        StringReader $$12 = new StringReader($$0.getCommand());
        if ($$12.canRead() && $$12.peek() == '/') {
            $$12.skip();
        }
        ParseResults $$2 = this.server.getCommands().getDispatcher().parse($$12, (Object)this.player.createCommandSourceStack());
        this.server.getCommands().getDispatcher().getCompletionSuggestions($$2).thenAccept($$1 -> this.connection.send(new ClientboundCommandSuggestionsPacket($$0.getId(), (Suggestions)$$1)));
    }

    @Override
    public void handleSetCommandBlock(ServerboundSetCommandBlockPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
            return;
        }
        if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
            return;
        }
        BaseCommandBlock $$1 = null;
        CommandBlockEntity $$2 = null;
        BlockPos $$3 = $$0.getPos();
        BlockEntity $$4 = this.player.level.getBlockEntity($$3);
        if ($$4 instanceof CommandBlockEntity) {
            $$2 = (CommandBlockEntity)$$4;
            $$1 = $$2.getCommandBlock();
        }
        String $$5 = $$0.getCommand();
        boolean $$6 = $$0.isTrackOutput();
        if ($$1 != null) {
            BlockState $$12;
            CommandBlockEntity.Mode $$7 = $$2.getMode();
            BlockState $$8 = this.player.level.getBlockState($$3);
            Direction $$9 = $$8.getValue(CommandBlock.FACING);
            switch ($$0.getMode()) {
                case SEQUENCE: {
                    BlockState $$10 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
                    break;
                }
                case AUTO: {
                    BlockState $$11 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
                    break;
                }
                default: {
                    $$12 = Blocks.COMMAND_BLOCK.defaultBlockState();
                }
            }
            BlockState $$13 = (BlockState)((BlockState)$$12.setValue(CommandBlock.FACING, $$9)).setValue(CommandBlock.CONDITIONAL, $$0.isConditional());
            if ($$13 != $$8) {
                this.player.level.setBlock($$3, $$13, 2);
                $$4.setBlockState($$13);
                this.player.level.getChunkAt($$3).setBlockEntity($$4);
            }
            $$1.setCommand($$5);
            $$1.setTrackOutput($$6);
            if (!$$6) {
                $$1.setLastOutput(null);
            }
            $$2.setAutomatic($$0.isAutomatic());
            if ($$7 != $$0.getMode()) {
                $$2.onModeSwitch();
            }
            $$1.onUpdated();
            if (!StringUtil.isNullOrEmpty($$5)) {
                this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", $$5));
            }
        }
    }

    @Override
    public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
            return;
        }
        if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
            return;
        }
        BaseCommandBlock $$1 = $$0.getCommandBlock(this.player.level);
        if ($$1 != null) {
            $$1.setCommand($$0.getCommand());
            $$1.setTrackOutput($$0.isTrackOutput());
            if (!$$0.isTrackOutput()) {
                $$1.setLastOutput(null);
            }
            $$1.onUpdated();
            this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", $$0.getCommand()));
        }
    }

    @Override
    public void handlePickItem(ServerboundPickItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.getInventory().pickSlot($$0.getSlot());
        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)));
        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, $$0.getSlot(), this.player.getInventory().getItem($$0.getSlot())));
        this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.getInventory().selected));
    }

    @Override
    public void handleRenameItem(ServerboundRenameItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof AnvilMenu) {
            AnvilMenu $$1 = (AnvilMenu)abstractContainerMenu;
            if (!$$1.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)$$1);
                return;
            }
            String $$2 = SharedConstants.filterText($$0.getName());
            if ($$2.length() <= 50) {
                $$1.setItemName($$2);
            }
        }
    }

    @Override
    public void handleSetBeaconPacket(ServerboundSetBeaconPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof BeaconMenu) {
            BeaconMenu $$1 = (BeaconMenu)abstractContainerMenu;
            if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.containerMenu);
                return;
            }
            $$1.updateEffects($$0.getPrimary(), $$0.getSecondary());
        }
    }

    @Override
    public void handleSetStructureBlock(ServerboundSetStructureBlockPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.canUseGameMasterBlocks()) {
            return;
        }
        BlockPos $$1 = $$0.getPos();
        BlockState $$2 = this.player.level.getBlockState($$1);
        BlockEntity $$3 = this.player.level.getBlockEntity($$1);
        if ($$3 instanceof StructureBlockEntity) {
            StructureBlockEntity $$4 = (StructureBlockEntity)$$3;
            $$4.setMode($$0.getMode());
            $$4.setStructureName($$0.getName());
            $$4.setStructurePos($$0.getOffset());
            $$4.setStructureSize($$0.getSize());
            $$4.setMirror($$0.getMirror());
            $$4.setRotation($$0.getRotation());
            $$4.setMetaData($$0.getData());
            $$4.setIgnoreEntities($$0.isIgnoreEntities());
            $$4.setShowAir($$0.isShowAir());
            $$4.setShowBoundingBox($$0.isShowBoundingBox());
            $$4.setIntegrity($$0.getIntegrity());
            $$4.setSeed($$0.getSeed());
            if ($$4.hasStructureName()) {
                String $$5 = $$4.getStructureName();
                if ($$0.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                    if ($$4.saveStructure()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.save_success", $$5), false);
                    } else {
                        this.player.displayClientMessage(Component.translatable("structure_block.save_failure", $$5), false);
                    }
                } else if ($$0.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                    if (!$$4.isStructureLoadable()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.load_not_found", $$5), false);
                    } else if ($$4.loadStructure(this.player.getLevel())) {
                        this.player.displayClientMessage(Component.translatable("structure_block.load_success", $$5), false);
                    } else {
                        this.player.displayClientMessage(Component.translatable("structure_block.load_prepare", $$5), false);
                    }
                } else if ($$0.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                    if ($$4.detectSize()) {
                        this.player.displayClientMessage(Component.translatable("structure_block.size_success", $$5), false);
                    } else {
                        this.player.displayClientMessage(Component.translatable("structure_block.size_failure"), false);
                    }
                }
            } else {
                this.player.displayClientMessage(Component.translatable("structure_block.invalid_structure_name", $$0.getName()), false);
            }
            $$4.setChanged();
            this.player.level.sendBlockUpdated($$1, $$2, $$2, 3);
        }
    }

    @Override
    public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.canUseGameMasterBlocks()) {
            return;
        }
        BlockPos $$1 = $$0.getPos();
        BlockState $$2 = this.player.level.getBlockState($$1);
        BlockEntity $$3 = this.player.level.getBlockEntity($$1);
        if ($$3 instanceof JigsawBlockEntity) {
            JigsawBlockEntity $$4 = (JigsawBlockEntity)$$3;
            $$4.setName($$0.getName());
            $$4.setTarget($$0.getTarget());
            $$4.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, $$0.getPool()));
            $$4.setFinalState($$0.getFinalState());
            $$4.setJoint($$0.getJoint());
            $$4.setChanged();
            this.player.level.sendBlockUpdated($$1, $$2, $$2, 3);
        }
    }

    @Override
    public void handleJigsawGenerate(ServerboundJigsawGeneratePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.canUseGameMasterBlocks()) {
            return;
        }
        BlockPos $$1 = $$0.getPos();
        BlockEntity $$2 = this.player.level.getBlockEntity($$1);
        if ($$2 instanceof JigsawBlockEntity) {
            JigsawBlockEntity $$3 = (JigsawBlockEntity)$$2;
            $$3.generate(this.player.getLevel(), $$0.levels(), $$0.keepJigsaws());
        }
    }

    @Override
    public void handleSelectTrade(ServerboundSelectTradePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        int $$1 = $$0.getItem();
        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof MerchantMenu) {
            MerchantMenu $$2 = (MerchantMenu)abstractContainerMenu;
            if (!$$2.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)$$2);
                return;
            }
            $$2.setSelectionHint($$1);
            $$2.tryMoveItems($$1);
        }
    }

    @Override
    public void handleEditBook(ServerboundEditBookPacket $$0) {
        int $$12 = $$0.getSlot();
        if (!Inventory.isHotbarSlot($$12) && $$12 != 40) {
            return;
        }
        ArrayList $$2 = Lists.newArrayList();
        Optional<String> $$3 = $$0.getTitle();
        $$3.ifPresent(arg_0 -> ((List)$$2).add(arg_0));
        $$0.getPages().stream().limit(100L).forEach(arg_0 -> ((List)$$2).add(arg_0));
        Consumer $$4 = $$3.isPresent() ? $$1 -> this.signBook((FilteredText)((Object)((Object)$$1.get(0))), (List<FilteredText>)$$1.subList(1, $$1.size()), $$12) : $$1 -> this.updateBookContents((List<FilteredText>)$$1, $$12);
        this.filterTextPacket((List<String>)$$2).thenAcceptAsync($$4, (Executor)this.server);
    }

    private void updateBookContents(List<FilteredText> $$0, int $$1) {
        ItemStack $$2 = this.player.getInventory().getItem($$1);
        if (!$$2.is(Items.WRITABLE_BOOK)) {
            return;
        }
        this.updateBookPages($$0, (UnaryOperator<String>)UnaryOperator.identity(), $$2);
    }

    private void signBook(FilteredText $$02, List<FilteredText> $$1, int $$2) {
        ItemStack $$3 = this.player.getInventory().getItem($$2);
        if (!$$3.is(Items.WRITABLE_BOOK)) {
            return;
        }
        ItemStack $$4 = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag $$5 = $$3.getTag();
        if ($$5 != null) {
            $$4.setTag($$5.copy());
        }
        $$4.addTagElement("author", StringTag.valueOf(this.player.getName().getString()));
        if (this.player.isTextFilteringEnabled()) {
            $$4.addTagElement("title", StringTag.valueOf($$02.filteredOrEmpty()));
        } else {
            $$4.addTagElement("filtered_title", StringTag.valueOf($$02.filteredOrEmpty()));
            $$4.addTagElement("title", StringTag.valueOf($$02.raw()));
        }
        this.updateBookPages($$1, (UnaryOperator<String>)((UnaryOperator)$$0 -> Component.Serializer.toJson(Component.literal($$0))), $$4);
        this.player.getInventory().setItem($$2, $$4);
    }

    private void updateBookPages(List<FilteredText> $$0, UnaryOperator<String> $$12, ItemStack $$2) {
        ListTag $$3 = new ListTag();
        if (this.player.isTextFilteringEnabled()) {
            $$0.stream().map($$1 -> StringTag.valueOf((String)$$12.apply((Object)$$1.filteredOrEmpty()))).forEach(arg_0 -> ((ListTag)$$3).add(arg_0));
        } else {
            CompoundTag $$4 = new CompoundTag();
            int $$6 = $$0.size();
            for (int $$5 = 0; $$5 < $$6; ++$$5) {
                FilteredText $$7 = (FilteredText)((Object)$$0.get($$5));
                String $$8 = $$7.raw();
                $$3.add(StringTag.valueOf((String)$$12.apply((Object)$$8)));
                if (!$$7.isFiltered()) continue;
                $$4.putString(String.valueOf((int)$$5), (String)$$12.apply((Object)$$7.filteredOrEmpty()));
            }
            if (!$$4.isEmpty()) {
                $$2.addTagElement("filtered_pages", $$4);
            }
        }
        $$2.addTagElement("pages", $$3);
    }

    @Override
    public void handleEntityTagQuery(ServerboundEntityTagQuery $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.hasPermissions(2)) {
            return;
        }
        Entity $$1 = this.player.getLevel().getEntity($$0.getEntityId());
        if ($$1 != null) {
            CompoundTag $$2 = $$1.saveWithoutId(new CompoundTag());
            this.player.connection.send(new ClientboundTagQueryPacket($$0.getTransactionId(), $$2));
        }
    }

    @Override
    public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.hasPermissions(2)) {
            return;
        }
        BlockEntity $$1 = this.player.getLevel().getBlockEntity($$0.getPos());
        CompoundTag $$2 = $$1 != null ? $$1.saveWithoutMetadata() : null;
        this.player.connection.send(new ClientboundTagQueryPacket($$0.getTransactionId(), $$2));
    }

    @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket $$0) {
        boolean $$19;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (ServerGamePacketListenerImpl.containsInvalidValues($$0.getX(0.0), $$0.getY(0.0), $$0.getZ(0.0), $$0.getYRot(0.0f), $$0.getXRot(0.0f))) {
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
            return;
        }
        ServerLevel $$1 = this.player.getLevel();
        if (this.player.wonGame) {
            return;
        }
        if (this.tickCount == 0) {
            this.resetPosition();
        }
        if (this.awaitingPositionFromClient != null) {
            if (this.tickCount - this.awaitingTeleportTime > 20) {
                this.awaitingTeleportTime = this.tickCount;
                this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.getYRot(), this.player.getXRot());
            }
            return;
        }
        this.awaitingTeleportTime = this.tickCount;
        double $$2 = ServerGamePacketListenerImpl.clampHorizontal($$0.getX(this.player.getX()));
        double $$3 = ServerGamePacketListenerImpl.clampVertical($$0.getY(this.player.getY()));
        double $$4 = ServerGamePacketListenerImpl.clampHorizontal($$0.getZ(this.player.getZ()));
        float $$5 = Mth.wrapDegrees($$0.getYRot(this.player.getYRot()));
        float $$6 = Mth.wrapDegrees($$0.getXRot(this.player.getXRot()));
        if (this.player.isPassenger()) {
            this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), $$5, $$6);
            this.player.getLevel().getChunkSource().move(this.player);
            return;
        }
        double $$7 = this.player.getX();
        double $$8 = this.player.getY();
        double $$9 = this.player.getZ();
        double $$10 = this.player.getY();
        double $$11 = $$2 - this.firstGoodX;
        double $$12 = $$3 - this.firstGoodY;
        double $$13 = $$4 - this.firstGoodZ;
        double $$14 = this.player.getDeltaMovement().lengthSqr();
        double $$15 = $$11 * $$11 + $$12 * $$12 + $$13 * $$13;
        if (this.player.isSleeping()) {
            if ($$15 > 1.0) {
                this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), $$5, $$6);
            }
            return;
        }
        ++this.receivedMovePacketCount;
        int $$16 = this.receivedMovePacketCount - this.knownMovePacketCount;
        if ($$16 > 5) {
            LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", (Object)this.player.getName().getString(), (Object)$$16);
            $$16 = 1;
        }
        if (!(this.player.isChangingDimension() || this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) && this.player.isFallFlying())) {
            float $$17;
            float f = $$17 = this.player.isFallFlying() ? 300.0f : 100.0f;
            if ($$15 - $$14 > (double)($$17 * (float)$$16) && !this.isSingleplayerOwner()) {
                LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), $$11, $$12, $$13});
                this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                return;
            }
        }
        AABB $$18 = this.player.getBoundingBox();
        $$11 = $$2 - this.lastGoodX;
        $$12 = $$3 - this.lastGoodY;
        $$13 = $$4 - this.lastGoodZ;
        boolean bl = $$19 = $$12 > 0.0;
        if (this.player.isOnGround() && !$$0.isOnGround() && $$19) {
            this.player.jumpFromGround();
        }
        boolean $$20 = this.player.verticalCollisionBelow;
        this.player.move(MoverType.PLAYER, new Vec3($$11, $$12, $$13));
        double $$21 = $$12;
        $$11 = $$2 - this.player.getX();
        $$12 = $$3 - this.player.getY();
        if ($$12 > -0.5 || $$12 < 0.5) {
            $$12 = 0.0;
        }
        $$13 = $$4 - this.player.getZ();
        $$15 = $$11 * $$11 + $$12 * $$12 + $$13 * $$13;
        boolean $$22 = false;
        if (!this.player.isChangingDimension() && $$15 > 0.0625 && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
            $$22 = true;
            LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
        }
        this.player.absMoveTo($$2, $$3, $$4, $$5, $$6);
        if (!this.player.noPhysics && !this.player.isSleeping() && ($$22 && $$1.noCollision(this.player, $$18) || this.isPlayerCollidingWithAnythingNew($$1, $$18))) {
            this.teleport($$7, $$8, $$9, $$5, $$6);
            this.player.doCheckFallDamage(this.player.getY() - $$10, $$0.isOnGround());
            return;
        }
        this.clientIsFloating = $$21 >= -0.03125 && !$$20 && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.getAbilities().mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && !this.player.isAutoSpinAttack() && this.noBlocksAround(this.player);
        this.player.getLevel().getChunkSource().move(this.player);
        this.player.doCheckFallDamage(this.player.getY() - $$10, $$0.isOnGround());
        this.player.setOnGround($$0.isOnGround());
        if ($$19) {
            this.player.resetFallDistance();
        }
        this.player.checkMovementStatistics(this.player.getX() - $$7, this.player.getY() - $$8, this.player.getZ() - $$9);
        this.lastGoodX = this.player.getX();
        this.lastGoodY = this.player.getY();
        this.lastGoodZ = this.player.getZ();
    }

    private boolean isPlayerCollidingWithAnythingNew(LevelReader $$0, AABB $$1) {
        Iterable $$2 = $$0.getCollisions(this.player, this.player.getBoundingBox().deflate(1.0E-5f));
        VoxelShape $$3 = Shapes.create($$1.deflate(1.0E-5f));
        for (VoxelShape $$4 : $$2) {
            if (Shapes.joinIsNotEmpty($$4, $$3, BooleanOp.AND)) continue;
            return true;
        }
        return false;
    }

    public void dismount(double $$0, double $$1, double $$2, float $$3, float $$4) {
        this.teleport($$0, $$1, $$2, $$3, $$4, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)Collections.emptySet(), true);
    }

    public void teleport(double $$0, double $$1, double $$2, float $$3, float $$4) {
        this.teleport($$0, $$1, $$2, $$3, $$4, (Set<ClientboundPlayerPositionPacket.RelativeArgument>)Collections.emptySet(), false);
    }

    public void teleport(double $$0, double $$1, double $$2, float $$3, float $$4, Set<ClientboundPlayerPositionPacket.RelativeArgument> $$5) {
        this.teleport($$0, $$1, $$2, $$3, $$4, $$5, false);
    }

    public void teleport(double $$0, double $$1, double $$2, float $$3, float $$4, Set<ClientboundPlayerPositionPacket.RelativeArgument> $$5, boolean $$6) {
        double $$7 = $$5.contains((Object)ClientboundPlayerPositionPacket.RelativeArgument.X) ? this.player.getX() : 0.0;
        double $$8 = $$5.contains((Object)ClientboundPlayerPositionPacket.RelativeArgument.Y) ? this.player.getY() : 0.0;
        double $$9 = $$5.contains((Object)ClientboundPlayerPositionPacket.RelativeArgument.Z) ? this.player.getZ() : 0.0;
        float $$10 = $$5.contains((Object)ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT) ? this.player.getYRot() : 0.0f;
        float $$11 = $$5.contains((Object)ClientboundPlayerPositionPacket.RelativeArgument.X_ROT) ? this.player.getXRot() : 0.0f;
        this.awaitingPositionFromClient = new Vec3($$0, $$1, $$2);
        if (++this.awaitingTeleport == Integer.MAX_VALUE) {
            this.awaitingTeleport = 0;
        }
        this.awaitingTeleportTime = this.tickCount;
        this.player.absMoveTo($$0, $$1, $$2, $$3, $$4);
        this.player.connection.send(new ClientboundPlayerPositionPacket($$0 - $$7, $$1 - $$8, $$2 - $$9, $$3 - $$10, $$4 - $$11, $$5, this.awaitingTeleport, $$6));
    }

    @Override
    public void handlePlayerAction(ServerboundPlayerActionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        BlockPos $$1 = $$0.getPos();
        this.player.resetLastActionTime();
        ServerboundPlayerActionPacket.Action $$2 = $$0.getAction();
        switch ($$2) {
            case SWAP_ITEM_WITH_OFFHAND: {
                if (!this.player.isSpectator()) {
                    ItemStack $$3 = this.player.getItemInHand(InteractionHand.OFF_HAND);
                    this.player.setItemInHand(InteractionHand.OFF_HAND, this.player.getItemInHand(InteractionHand.MAIN_HAND));
                    this.player.setItemInHand(InteractionHand.MAIN_HAND, $$3);
                    this.player.stopUsingItem();
                }
                return;
            }
            case DROP_ITEM: {
                if (!this.player.isSpectator()) {
                    this.player.drop(false);
                }
                return;
            }
            case DROP_ALL_ITEMS: {
                if (!this.player.isSpectator()) {
                    this.player.drop(true);
                }
                return;
            }
            case RELEASE_USE_ITEM: {
                this.player.releaseUsingItem();
                return;
            }
            case START_DESTROY_BLOCK: 
            case ABORT_DESTROY_BLOCK: 
            case STOP_DESTROY_BLOCK: {
                this.player.gameMode.handleBlockBreakAction($$1, $$2, $$0.getDirection(), this.player.level.getMaxBuildHeight(), $$0.getSequence());
                this.player.connection.ackBlockChangesUpTo($$0.getSequence());
                return;
            }
        }
        throw new IllegalArgumentException("Invalid player action");
    }

    private static boolean wasBlockPlacementAttempt(ServerPlayer $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return false;
        }
        Item $$2 = $$1.getItem();
        return ($$2 instanceof BlockItem || $$2 instanceof BucketItem) && !$$0.getCooldowns().isOnCooldown($$2);
    }

    @Override
    public void handleUseItemOn(ServerboundUseItemOnPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.connection.ackBlockChangesUpTo($$0.getSequence());
        ServerLevel $$1 = this.player.getLevel();
        InteractionHand $$2 = $$0.getHand();
        ItemStack $$3 = this.player.getItemInHand($$2);
        if (!$$3.isItemEnabled($$1.enabledFeatures())) {
            return;
        }
        BlockHitResult $$4 = $$0.getHitResult();
        Vec3 $$5 = $$4.getLocation();
        BlockPos $$6 = $$4.getBlockPos();
        Vec3 $$7 = Vec3.atCenterOf($$6);
        if (this.player.getEyePosition().distanceToSqr($$7) > MAX_INTERACTION_DISTANCE) {
            return;
        }
        Vec3 $$8 = $$5.subtract($$7);
        double $$9 = 1.0000001;
        if (!(Math.abs((double)$$8.x()) < 1.0000001 && Math.abs((double)$$8.y()) < 1.0000001 && Math.abs((double)$$8.z()) < 1.0000001)) {
            LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", new Object[]{this.player.getGameProfile().getName(), $$5, $$6});
            return;
        }
        Direction $$10 = $$4.getDirection();
        this.player.resetLastActionTime();
        int $$11 = this.player.level.getMaxBuildHeight();
        if ($$6.getY() < $$11) {
            if (this.awaitingPositionFromClient == null && this.player.distanceToSqr((double)$$6.getX() + 0.5, (double)$$6.getY() + 0.5, (double)$$6.getZ() + 0.5) < 64.0 && $$1.mayInteract(this.player, $$6)) {
                InteractionResult $$12 = this.player.gameMode.useItemOn(this.player, $$1, $$3, $$2, $$4);
                if ($$10 == Direction.UP && !$$12.consumesAction() && $$6.getY() >= $$11 - 1 && ServerGamePacketListenerImpl.wasBlockPlacementAttempt(this.player, $$3)) {
                    MutableComponent $$13 = Component.translatable("build.tooHigh", $$11 - 1).withStyle(ChatFormatting.RED);
                    this.player.sendSystemMessage($$13, true);
                } else if ($$12.shouldSwing()) {
                    this.player.swing($$2, true);
                }
            }
        } else {
            MutableComponent $$14 = Component.translatable("build.tooHigh", $$11 - 1).withStyle(ChatFormatting.RED);
            this.player.sendSystemMessage($$14, true);
        }
        this.player.connection.send(new ClientboundBlockUpdatePacket($$1, $$6));
        this.player.connection.send(new ClientboundBlockUpdatePacket($$1, (BlockPos)$$6.relative($$10)));
    }

    @Override
    public void handleUseItem(ServerboundUseItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.ackBlockChangesUpTo($$0.getSequence());
        ServerLevel $$1 = this.player.getLevel();
        InteractionHand $$2 = $$0.getHand();
        ItemStack $$3 = this.player.getItemInHand($$2);
        this.player.resetLastActionTime();
        if ($$3.isEmpty() || !$$3.isItemEnabled($$1.enabledFeatures())) {
            return;
        }
        InteractionResult $$4 = this.player.gameMode.useItem(this.player, $$1, $$3, $$2);
        if ($$4.shouldSwing()) {
            this.player.swing($$2, true);
        }
    }

    @Override
    public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (this.player.isSpectator()) {
            for (ServerLevel $$1 : this.server.getAllLevels()) {
                Entity $$2 = $$0.getEntity($$1);
                if ($$2 == null) continue;
                this.player.teleportTo($$1, $$2.getX(), $$2.getY(), $$2.getZ(), $$2.getYRot(), $$2.getXRot());
                return;
            }
        }
    }

    @Override
    public void handleResourcePackResponse(ServerboundResourcePackPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if ($$0.getAction() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) {
            LOGGER.info("Disconnecting {} due to resource pack rejection", (Object)this.player.getName());
            this.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
        }
    }

    @Override
    public void handlePaddleBoat(ServerboundPaddleBoatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        Entity $$1 = this.player.getVehicle();
        if ($$1 instanceof Boat) {
            ((Boat)$$1).setPaddleState($$0.getLeft(), $$0.getRight());
        }
    }

    @Override
    public void handlePong(ServerboundPongPacket $$0) {
    }

    @Override
    public void onDisconnect(Component $$0) {
        this.chatMessageChain.close();
        LOGGER.info("{} lost connection: {}", (Object)this.player.getName().getString(), (Object)$$0.getString());
        this.server.invalidateStatus();
        this.server.getPlayerList().broadcastSystemMessage(Component.translatable("multiplayer.player.left", this.player.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
        this.player.disconnect();
        this.server.getPlayerList().remove(this.player);
        this.player.getTextFilter().leave();
        if (this.isSingleplayerOwner()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.halt(false);
        }
    }

    public void ackBlockChangesUpTo(int $$0) {
        if ($$0 < 0) {
            throw new IllegalArgumentException("Expected packet sequence nr >= 0");
        }
        this.ackBlockChangesUpTo = Math.max((int)$$0, (int)this.ackBlockChangesUpTo);
    }

    @Override
    public void send(Packet<?> $$0) {
        this.send($$0, null);
    }

    public void send(Packet<?> $$0, @Nullable PacketSendListener $$1) {
        try {
            this.connection.send($$0, $$1);
        }
        catch (Throwable $$2) {
            CrashReport $$3 = CrashReport.forThrowable($$2, "Sending packet");
            CrashReportCategory $$4 = $$3.addCategory("Packet being sent");
            $$4.setDetail("Packet class", () -> $$0.getClass().getCanonicalName());
            throw new ReportedException($$3);
        }
    }

    @Override
    public void handleSetCarriedItem(ServerboundSetCarriedItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if ($$0.getSlot() < 0 || $$0.getSlot() >= Inventory.getSelectionSize()) {
            LOGGER.warn("{} tried to set an invalid carried item", (Object)this.player.getName().getString());
            return;
        }
        if (this.player.getInventory().selected != $$0.getSlot() && this.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            this.player.stopUsingItem();
        }
        this.player.getInventory().selected = $$0.getSlot();
        this.player.resetLastActionTime();
    }

    @Override
    public void handleChat(ServerboundChatPacket $$0) {
        if (ServerGamePacketListenerImpl.isChatMessageIllegal($$0.message())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
            return;
        }
        Optional<LastSeenMessages> $$1 = this.tryHandleChat($$0.message(), $$0.timeStamp(), $$0.lastSeenMessages());
        if ($$1.isPresent()) {
            this.server.submit(() -> {
                void $$4;
                try {
                    PlayerChatMessage $$2 = this.getSignedMessage($$0, (LastSeenMessages)((Object)((Object)$$1.get())));
                }
                catch (SignedMessageChain.DecodeException $$3) {
                    this.handleMessageDecodeFailure($$3);
                    return;
                }
                CompletableFuture<FilteredText> $$5 = this.filterTextPacket($$4.signedContent());
                CompletableFuture<Component> $$6 = this.server.getChatDecorator().decorate(this.player, $$4.decoratedContent());
                this.chatMessageChain.append(arg_0 -> this.lambda$handleChat$9($$5, $$6, (PlayerChatMessage)$$4, arg_0));
            });
        }
    }

    @Override
    public void handleChatCommand(ServerboundChatCommandPacket $$0) {
        if (ServerGamePacketListenerImpl.isChatMessageIllegal($$0.command())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
            return;
        }
        Optional<LastSeenMessages> $$1 = this.tryHandleChat($$0.command(), $$0.timeStamp(), $$0.lastSeenMessages());
        if ($$1.isPresent()) {
            this.server.submit(() -> {
                this.performChatCommand($$0, (LastSeenMessages)((Object)((Object)$$1.get())));
                this.detectRateSpam();
            });
        }
    }

    /*
     * WARNING - void declaration
     */
    private void performChatCommand(ServerboundChatCommandPacket $$0, LastSeenMessages $$12) {
        void $$5;
        ParseResults<CommandSourceStack> $$2 = this.parseCommand($$0.command());
        try {
            Map<String, PlayerChatMessage> $$3 = this.collectSignedArguments($$0, SignableCommand.of($$2), $$12);
        }
        catch (SignedMessageChain.DecodeException $$4) {
            this.handleMessageDecodeFailure($$4);
            return;
        }
        CommandSigningContext.SignedArguments $$6 = new CommandSigningContext.SignedArguments((Map<String, PlayerChatMessage>)$$5);
        $$2 = Commands.mapSource($$2, $$1 -> $$1.withSigningContext($$6));
        this.server.getCommands().performCommand($$2, $$0.command());
    }

    private void handleMessageDecodeFailure(SignedMessageChain.DecodeException $$0) {
        if ($$0.shouldDisconnect()) {
            this.disconnect($$0.getComponent());
        } else {
            this.player.sendSystemMessage($$0.getComponent().copy().withStyle(ChatFormatting.RED));
        }
    }

    private Map<String, PlayerChatMessage> collectSignedArguments(ServerboundChatCommandPacket $$0, SignableCommand<?> $$1, LastSeenMessages $$2) throws SignedMessageChain.DecodeException {
        Object2ObjectOpenHashMap $$3 = new Object2ObjectOpenHashMap();
        for (SignableCommand.Argument $$4 : $$1.arguments()) {
            MessageSignature $$5 = $$0.argumentSignatures().get($$4.name());
            SignedMessageBody $$6 = new SignedMessageBody($$4.value(), $$0.timeStamp(), $$0.salt(), $$2);
            $$3.put((Object)$$4.name(), (Object)this.signedMessageDecoder.unpack($$5, $$6));
        }
        return $$3;
    }

    private ParseResults<CommandSourceStack> parseCommand(String $$0) {
        CommandDispatcher<CommandSourceStack> $$1 = this.server.getCommands().getDispatcher();
        return $$1.parse($$0, (Object)this.player.createCommandSourceStack());
    }

    private Optional<LastSeenMessages> tryHandleChat(String $$0, Instant $$1, LastSeenMessages.Update $$2) {
        if (!this.updateChatOrder($$1)) {
            LOGGER.warn("{} sent out-of-order chat: '{}'", (Object)this.player.getName().getString(), (Object)$$0);
            this.disconnect(Component.translatable("multiplayer.disconnect.out_of_order_chat"));
            return Optional.empty();
        }
        if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
            this.send(new ClientboundSystemChatPacket(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED), false));
            return Optional.empty();
        }
        Optional<LastSeenMessages> $$3 = this.unpackAndApplyLastSeen($$2);
        this.player.resetLastActionTime();
        return $$3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Optional<LastSeenMessages> unpackAndApplyLastSeen(LastSeenMessages.Update $$0) {
        LastSeenMessagesValidator lastSeenMessagesValidator = this.lastSeenMessages;
        synchronized (lastSeenMessagesValidator) {
            Optional<LastSeenMessages> $$1 = this.lastSeenMessages.applyUpdate($$0);
            if ($$1.isEmpty()) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", (Object)this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED);
            }
            return $$1;
        }
    }

    private boolean updateChatOrder(Instant $$0) {
        Instant $$1;
        do {
            if (!$$0.isBefore($$1 = (Instant)this.lastChatTimeStamp.get())) continue;
            return false;
        } while (!this.lastChatTimeStamp.compareAndSet((Object)$$1, (Object)$$0));
        return true;
    }

    private static boolean isChatMessageIllegal(String $$0) {
        for (int $$1 = 0; $$1 < $$0.length(); ++$$1) {
            if (SharedConstants.isAllowedChatCharacter($$0.charAt($$1))) continue;
            return true;
        }
        return false;
    }

    private PlayerChatMessage getSignedMessage(ServerboundChatPacket $$0, LastSeenMessages $$1) throws SignedMessageChain.DecodeException {
        SignedMessageBody $$2 = new SignedMessageBody($$0.message(), $$0.timeStamp(), $$0.salt(), $$1);
        return this.signedMessageDecoder.unpack($$0.signature(), $$2);
    }

    private void broadcastChatMessage(PlayerChatMessage $$0) {
        this.server.getPlayerList().broadcastChatMessage($$0, this.player, ChatType.bind(ChatType.CHAT, this.player));
        this.detectRateSpam();
    }

    private void detectRateSpam() {
        this.chatSpamTickCount += 20;
        if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(Component.translatable("disconnect.spam"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleChatAck(ServerboundChatAckPacket $$0) {
        LastSeenMessagesValidator lastSeenMessagesValidator = this.lastSeenMessages;
        synchronized (lastSeenMessagesValidator) {
            if (!this.lastSeenMessages.applyOffset($$0.offset())) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", (Object)this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED);
            }
        }
    }

    @Override
    public void handleAnimate(ServerboundSwingPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        this.player.swing($$0.getHand());
    }

    @Override
    public void handlePlayerCommand(ServerboundPlayerCommandPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        switch ($$0.getAction()) {
            case PRESS_SHIFT_KEY: {
                this.player.setShiftKeyDown(true);
                break;
            }
            case RELEASE_SHIFT_KEY: {
                this.player.setShiftKeyDown(false);
                break;
            }
            case START_SPRINTING: {
                this.player.setSprinting(true);
                break;
            }
            case STOP_SPRINTING: {
                this.player.setSprinting(false);
                break;
            }
            case STOP_SLEEPING: {
                if (!this.player.isSleeping()) break;
                this.player.stopSleepInBed(false, true);
                this.awaitingPositionFromClient = this.player.position();
                break;
            }
            case START_RIDING_JUMP: {
                if (!(this.player.getVehicle() instanceof PlayerRideableJumping)) break;
                PlayerRideableJumping $$1 = (PlayerRideableJumping)((Object)this.player.getVehicle());
                int $$2 = $$0.getData();
                if (!$$1.canJump(this.player) || $$2 <= 0) break;
                $$1.handleStartJump($$2);
                break;
            }
            case STOP_RIDING_JUMP: {
                if (!(this.player.getVehicle() instanceof PlayerRideableJumping)) break;
                PlayerRideableJumping $$3 = (PlayerRideableJumping)((Object)this.player.getVehicle());
                $$3.handleStopJump();
                break;
            }
            case OPEN_INVENTORY: {
                Entity entity = this.player.getVehicle();
                if (!(entity instanceof HasCustomInventoryScreen)) break;
                HasCustomInventoryScreen $$4 = (HasCustomInventoryScreen)((Object)entity);
                $$4.openCustomInventoryScreen(this.player);
                break;
            }
            case START_FALL_FLYING: {
                if (this.player.tryToStartFallFlying()) break;
                this.player.stopFallFlying();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid client command!");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public void addPendingMessage(PlayerChatMessage $$0) {
        void $$3;
        MessageSignature $$1 = $$0.signature();
        if ($$1 == null) {
            return;
        }
        this.messageSignatureCache.push($$0);
        LastSeenMessagesValidator lastSeenMessagesValidator = this.lastSeenMessages;
        synchronized (lastSeenMessagesValidator) {
            this.lastSeenMessages.addPending($$1);
            int $$2 = this.lastSeenMessages.trackedMessagesCount();
        }
        if ($$3 > 4096) {
            this.disconnect(Component.translatable("multiplayer.disconnect.too_many_pending_chats"));
        }
    }

    public void sendPlayerChatMessage(PlayerChatMessage $$0, ChatType.Bound $$1) {
        this.send(new ClientboundPlayerChatPacket($$0.link().sender(), $$0.link().index(), $$0.signature(), $$0.signedBody().pack(this.messageSignatureCache), $$0.unsignedContent(), $$0.filterMask(), $$1.toNetwork(this.player.level.registryAccess())));
        this.addPendingMessage($$0);
    }

    public void sendDisguisedChatMessage(Component $$0, ChatType.Bound $$1) {
        this.send(new ClientboundDisguisedChatPacket($$0, $$1.toNetwork(this.player.level.registryAccess())));
    }

    @Override
    public void handleInteract(ServerboundInteractPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        final ServerLevel $$1 = this.player.getLevel();
        final Entity $$2 = $$0.getTarget($$1);
        this.player.resetLastActionTime();
        this.player.setShiftKeyDown($$0.isUsingSecondaryAction());
        if ($$2 != null) {
            if (!$$1.getWorldBorder().isWithinBounds($$2.blockPosition())) {
                return;
            }
            if ($$2.distanceToSqr(this.player.getEyePosition()) < MAX_INTERACTION_DISTANCE) {
                $$0.dispatch(new ServerboundInteractPacket.Handler(){

                    private void performInteraction(InteractionHand $$0, EntityInteraction $$12) {
                        ItemStack $$22 = ServerGamePacketListenerImpl.this.player.getItemInHand($$0);
                        if (!$$22.isItemEnabled($$1.enabledFeatures())) {
                            return;
                        }
                        ItemStack $$3 = $$22.copy();
                        InteractionResult $$4 = $$12.run(ServerGamePacketListenerImpl.this.player, $$2, $$0);
                        if ($$4.consumesAction()) {
                            CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerGamePacketListenerImpl.this.player, $$3, $$2);
                            if ($$4.shouldSwing()) {
                                ServerGamePacketListenerImpl.this.player.swing($$0, true);
                            }
                        }
                    }

                    @Override
                    public void onInteraction(InteractionHand $$0) {
                        this.performInteraction($$0, Player::interactOn);
                    }

                    @Override
                    public void onInteraction(InteractionHand $$0, Vec3 $$12) {
                        this.performInteraction($$0, ($$1, $$2, $$3) -> $$2.interactAt($$1, $$12, $$3));
                    }

                    @Override
                    public void onAttack() {
                        if ($$2 instanceof ItemEntity || $$2 instanceof ExperienceOrb || $$2 instanceof AbstractArrow || $$2 == ServerGamePacketListenerImpl.this.player) {
                            ServerGamePacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                            LOGGER.warn("Player {} tried to attack an invalid entity", (Object)ServerGamePacketListenerImpl.this.player.getName().getString());
                            return;
                        }
                        ItemStack $$0 = ServerGamePacketListenerImpl.this.player.getItemInHand(InteractionHand.MAIN_HAND);
                        if (!$$0.isItemEnabled($$1.enabledFeatures())) {
                            return;
                        }
                        ServerGamePacketListenerImpl.this.player.attack($$2);
                    }
                });
            }
        }
    }

    @Override
    public void handleClientCommand(ServerboundClientCommandPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        ServerboundClientCommandPacket.Action $$1 = $$0.getAction();
        switch ($$1) {
            case PERFORM_RESPAWN: {
                if (this.player.wonGame) {
                    this.player.wonGame = false;
                    this.player = this.server.getPlayerList().respawn(this.player, true);
                    CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, Level.END, Level.OVERWORLD);
                    break;
                }
                if (this.player.getHealth() > 0.0f) {
                    return;
                }
                this.player = this.server.getPlayerList().respawn(this.player, false);
                if (!this.server.isHardcore()) break;
                this.player.setGameMode(GameType.SPECTATOR);
                this.player.getLevel().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(false, this.server);
                break;
            }
            case REQUEST_STATS: {
                this.player.getStats().sendStats(this.player);
            }
        }
    }

    @Override
    public void handleContainerClose(ServerboundContainerClosePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.doCloseContainer();
    }

    @Override
    public void handleContainerClick(ServerboundContainerClickPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId != $$0.getContainerId()) {
            return;
        }
        if (this.player.isSpectator()) {
            this.player.containerMenu.sendAllDataToRemote();
            return;
        }
        if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.containerMenu);
            return;
        }
        int $$1 = $$0.getSlotNum();
        if (!this.player.containerMenu.isValidSlotIndex($$1)) {
            LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", new Object[]{this.player.getName(), $$1, this.player.containerMenu.slots.size()});
            return;
        }
        boolean $$2 = $$0.getStateId() != this.player.containerMenu.getStateId();
        this.player.containerMenu.suppressRemoteUpdates();
        this.player.containerMenu.clicked($$1, $$0.getButtonNum(), $$0.getClickType(), this.player);
        for (Int2ObjectMap.Entry $$3 : Int2ObjectMaps.fastIterable($$0.getChangedSlots())) {
            this.player.containerMenu.setRemoteSlotNoCopy($$3.getIntKey(), (ItemStack)$$3.getValue());
        }
        this.player.containerMenu.setRemoteCarried($$0.getCarriedItem());
        this.player.containerMenu.resumeRemoteUpdates();
        if ($$2) {
            this.player.containerMenu.broadcastFullState();
        } else {
            this.player.containerMenu.broadcastChanges();
        }
    }

    @Override
    public void handlePlaceRecipe(ServerboundPlaceRecipePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.isSpectator() || this.player.containerMenu.containerId != $$0.getContainerId() || !(this.player.containerMenu instanceof RecipeBookMenu)) {
            return;
        }
        if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.containerMenu);
            return;
        }
        this.server.getRecipeManager().byKey($$0.getRecipe()).ifPresent($$1 -> ((RecipeBookMenu)this.player.containerMenu).handlePlacement($$0.isShiftDown(), (Recipe<?>)$$1, this.player));
    }

    @Override
    public void handleContainerButtonClick(ServerboundContainerButtonClickPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId != $$0.getContainerId() || this.player.isSpectator()) {
            return;
        }
        if (!this.player.containerMenu.stillValid(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", (Object)this.player, (Object)this.player.containerMenu);
            return;
        }
        boolean $$1 = this.player.containerMenu.clickMenuButton(this.player, $$0.getButtonId());
        if ($$1) {
            this.player.containerMenu.broadcastChanges();
        }
    }

    @Override
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (this.player.gameMode.isCreative()) {
            boolean $$7;
            BlockEntity $$5;
            BlockPos $$4;
            boolean $$1 = $$0.getSlotNum() < 0;
            ItemStack $$2 = $$0.getItem();
            if (!$$2.isItemEnabled(this.player.getLevel().enabledFeatures())) {
                return;
            }
            CompoundTag $$3 = BlockItem.getBlockEntityData($$2);
            if (!$$2.isEmpty() && $$3 != null && $$3.contains("x") && $$3.contains("y") && $$3.contains("z") && this.player.level.isLoaded($$4 = BlockEntity.getPosFromTag($$3)) && ($$5 = this.player.level.getBlockEntity($$4)) != null) {
                $$5.saveToItem($$2);
            }
            boolean $$6 = $$0.getSlotNum() >= 1 && $$0.getSlotNum() <= 45;
            boolean bl = $$7 = $$2.isEmpty() || $$2.getDamageValue() >= 0 && $$2.getCount() <= 64 && !$$2.isEmpty();
            if ($$6 && $$7) {
                this.player.inventoryMenu.getSlot($$0.getSlotNum()).set($$2);
                this.player.inventoryMenu.broadcastChanges();
            } else if ($$1 && $$7 && this.dropSpamTickCount < 200) {
                this.dropSpamTickCount += 20;
                this.player.drop($$2, true);
            }
        }
    }

    @Override
    public void handleSignUpdate(ServerboundSignUpdatePacket $$0) {
        List $$12 = (List)Stream.of((Object[])$$0.getLines()).map(ChatFormatting::stripFormatting).collect(Collectors.toList());
        this.filterTextPacket((List<String>)$$12).thenAcceptAsync($$1 -> this.updateSignText($$0, (List<FilteredText>)$$1), (Executor)this.server);
    }

    private void updateSignText(ServerboundSignUpdatePacket $$0, List<FilteredText> $$1) {
        this.player.resetLastActionTime();
        ServerLevel $$2 = this.player.getLevel();
        BlockPos $$3 = $$0.getPos();
        if ($$2.hasChunkAt($$3)) {
            BlockState $$4 = $$2.getBlockState($$3);
            BlockEntity $$5 = $$2.getBlockEntity($$3);
            if (!($$5 instanceof SignBlockEntity)) {
                return;
            }
            SignBlockEntity $$6 = (SignBlockEntity)$$5;
            if (!$$6.isEditable() || !this.player.getUUID().equals((Object)$$6.getPlayerWhoMayEdit())) {
                LOGGER.warn("Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
                return;
            }
            for (int $$7 = 0; $$7 < $$1.size(); ++$$7) {
                FilteredText $$8 = (FilteredText)((Object)$$1.get($$7));
                if (this.player.isTextFilteringEnabled()) {
                    $$6.setMessage($$7, Component.literal($$8.filteredOrEmpty()));
                    continue;
                }
                $$6.setMessage($$7, Component.literal($$8.raw()), Component.literal($$8.filteredOrEmpty()));
            }
            $$6.setChanged();
            $$2.sendBlockUpdated($$3, $$4, $$4, 3);
        }
    }

    @Override
    public void handleKeepAlive(ServerboundKeepAlivePacket $$0) {
        if (this.keepAlivePending && $$0.getId() == this.keepAliveChallenge) {
            int $$1 = (int)(Util.getMillis() - this.keepAliveTime);
            this.player.latency = (this.player.latency * 3 + $$1) / 4;
            this.keepAlivePending = false;
        } else if (!this.isSingleplayerOwner()) {
            this.disconnect(Component.translatable("disconnect.timeout"));
        }
    }

    @Override
    public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.getAbilities().flying = $$0.isFlying() && this.player.getAbilities().mayfly;
    }

    @Override
    public void handleClientInformation(ServerboundClientInformationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        this.player.updateOptions($$0);
    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket $$0) {
    }

    @Override
    public void handleChangeDifficulty(ServerboundChangeDifficultyPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.hasPermissions(2) && !this.isSingleplayerOwner()) {
            return;
        }
        this.server.setDifficulty($$0.getDifficulty(), false);
    }

    @Override
    public void handleLockDifficulty(ServerboundLockDifficultyPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        if (!this.player.hasPermissions(2) && !this.isSingleplayerOwner()) {
            return;
        }
        this.server.setDifficultyLocked($$0.isLocked());
    }

    @Override
    public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.player.getLevel());
        RemoteChatSession.Data $$1 = $$0.chatSession();
        ProfilePublicKey.Data $$2 = this.chatSession != null ? this.chatSession.profilePublicKey().data() : null;
        ProfilePublicKey.Data $$3 = $$1.profilePublicKey();
        if (Objects.equals((Object)((Object)$$2), (Object)((Object)$$3))) {
            return;
        }
        if ($$2 != null && $$3.expiresAt().isBefore($$2.expiresAt())) {
            this.disconnect(ProfilePublicKey.EXPIRED_PROFILE_PUBLIC_KEY);
            return;
        }
        try {
            SignatureValidator $$4 = this.server.getServiceSignatureValidator();
            this.resetPlayerChatState($$1.validate(this.player.getGameProfile(), $$4, Duration.ZERO));
        }
        catch (ProfilePublicKey.ValidationException $$5) {
            LOGGER.error("Failed to validate profile key: {}", (Object)$$5.getMessage());
            this.disconnect($$5.getComponent());
        }
    }

    private void resetPlayerChatState(RemoteChatSession $$0) {
        this.chatSession = $$0;
        this.signedMessageDecoder = $$0.createMessageDecoder(this.player.getUUID());
        this.chatMessageChain.append($$1 -> {
            this.player.setChatSession($$0);
            this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket((EnumSet<ClientboundPlayerInfoUpdatePacket.Action>)EnumSet.of((Enum)ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT), (Collection<ServerPlayer>)List.of((Object)this.player)));
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }

    private /* synthetic */ CompletableFuture lambda$handleChat$9(CompletableFuture $$0, CompletableFuture $$1, PlayerChatMessage $$2, Executor $$32) {
        return CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{$$0, $$1}).thenAcceptAsync($$3 -> {
            PlayerChatMessage $$4 = $$2.withUnsignedContent((Component)$$1.join()).filter(((FilteredText)((Object)((Object)$$0.join()))).mask());
            this.broadcastChatMessage($$4);
        }, $$32);
    }

    @FunctionalInterface
    static interface EntityInteraction {
        public InteractionResult run(ServerPlayer var1, Entity var2, InteractionHand var3);
    }
}