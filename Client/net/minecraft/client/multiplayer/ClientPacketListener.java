/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.Unpooled
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  java.lang.Boolean
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.MalformedURLException
 *  java.net.URL
 *  java.text.ParseException
 *  java.time.Instant
 *  java.util.ArrayList
 *  java.util.BitSet
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BiConsumer
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Crypt;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ClientPacketListener
implements TickablePacketListener,
ClientGamePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
    private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable("multiplayer.unsecureserver.toast.title");
    private static final Component UNSERURE_SERVER_TOAST = Component.translatable("multiplayer.unsecureserver.toast");
    private static final Component INVALID_PACKET = Component.translatable("multiplayer.disconnect.invalid_packet");
    private static final Component CHAT_VALIDATION_FAILED_ERROR = Component.translatable("multiplayer.disconnect.chat_validation_failed");
    private static final int PENDING_OFFSET_THRESHOLD = 64;
    private final Connection connection;
    @Nullable
    private final ServerData serverData;
    private final GameProfile localGameProfile;
    private final Screen callbackScreen;
    private final Minecraft minecraft;
    private ClientLevel level;
    private ClientLevel.ClientLevelData levelData;
    private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
    private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet();
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
    private int serverChunkRadius = 3;
    private int serverSimulationDistance = 3;
    private final RandomSource random = RandomSource.createThreadSafe();
    private CommandDispatcher<SharedSuggestionProvider> commands = new CommandDispatcher();
    private final RecipeManager recipeManager = new RecipeManager();
    private final UUID id = UUID.randomUUID();
    private Set<ResourceKey<Level>> levels;
    private LayeredRegistryAccess<ClientRegistryLayer> registryAccess = ClientRegistryLayer.createRegistryAccess();
    private FeatureFlagSet enabledFeatures = FeatureFlags.DEFAULT_FLAGS;
    private final WorldSessionTelemetryManager telemetryManager;
    @Nullable
    private LocalChatSession chatSession;
    private SignedMessageChain.Encoder signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
    private LastSeenMessagesTracker lastSeenMessages = new LastSeenMessagesTracker(20);
    private MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();

    public ClientPacketListener(Minecraft $$0, Screen $$1, Connection $$2, @Nullable ServerData $$3, GameProfile $$4, WorldSessionTelemetryManager $$5) {
        this.minecraft = $$0;
        this.callbackScreen = $$1;
        this.connection = $$2;
        this.serverData = $$3;
        this.localGameProfile = $$4;
        this.advancements = new ClientAdvancements($$0);
        this.suggestionsProvider = new ClientSuggestionProvider(this, $$0);
        this.telemetryManager = $$5;
    }

    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public void close() {
        this.level = null;
        this.telemetryManager.onDisconnect();
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    @Override
    public void handleLogin(ClientboundLoginPacket $$02) {
        ClientLevel.ClientLevelData $$6;
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        this.registryAccess = this.registryAccess.replaceFrom(ClientRegistryLayer.REMOTE, $$02.registryHolder());
        if (!this.connection.isMemoryConnection()) {
            this.registryAccess.compositeAccess().registries().forEach($$0 -> $$0.value().resetTags());
        }
        ArrayList $$1 = Lists.newArrayList($$02.levels());
        Collections.shuffle((List)$$1);
        this.levels = Sets.newLinkedHashSet((Iterable)$$1);
        ResourceKey<Level> $$2 = $$02.dimension();
        Holder.Reference<DimensionType> $$3 = this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow($$02.dimensionType());
        this.serverChunkRadius = $$02.chunkRadius();
        this.serverSimulationDistance = $$02.simulationDistance();
        boolean $$4 = $$02.isDebug();
        boolean $$5 = $$02.isFlat();
        this.levelData = $$6 = new ClientLevel.ClientLevelData(Difficulty.NORMAL, $$02.hardcore(), $$5);
        this.level = new ClientLevel(this, $$6, $$2, $$3, this.serverChunkRadius, this.serverSimulationDistance, (Supplier<ProfilerFiller>)((Supplier)this.minecraft::getProfiler), this.minecraft.levelRenderer, $$4, $$02.seed());
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0f);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }
        this.minecraft.debugRenderer.clear();
        this.minecraft.player.resetPos();
        int $$7 = $$02.playerId();
        this.minecraft.player.setId($$7);
        this.level.addPlayer($$7, this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.cameraEntity = this.minecraft.player;
        this.minecraft.setScreen(new ReceivingLevelScreen());
        this.minecraft.player.setReducedDebugInfo($$02.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen($$02.showDeathScreen());
        this.minecraft.player.setLastDeathLocation($$02.lastDeathLocation());
        this.minecraft.gameMode.setLocalMode($$02.gameType(), $$02.previousGameType());
        this.minecraft.options.setServerRenderDistance($$02.chunkRadius());
        this.minecraft.options.broadcastOptions();
        this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(ClientBrandRetriever.getClientModName())));
        this.chatSession = null;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        if (this.connection.isEncrypted()) {
            this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync($$0 -> $$0.ifPresent(this::setKeyPair), (Executor)this.minecraft);
        }
        this.telemetryManager.onPlayerInfoReceived($$02.gameType(), $$02.hardcore());
    }

    @Override
    public void handleAddEntity(ClientboundAddEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        EntityType<?> $$1 = $$0.getType();
        Object $$2 = $$1.create(this.level);
        if ($$2 != null) {
            ((Entity)$$2).recreateFromPacket($$0);
            int $$3 = $$0.getId();
            this.level.putNonPlayerEntity($$3, (Entity)$$2);
            this.postAddEntitySoundInstance((Entity)$$2);
        } else {
            LOGGER.warn("Skipping Entity with id {}", $$1);
        }
    }

    private void postAddEntitySoundInstance(Entity $$0) {
        if ($$0 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)$$0));
        } else if ($$0 instanceof Bee) {
            BeeFlyingSoundInstance $$3;
            boolean $$1 = ((Bee)$$0).isAngry();
            if ($$1) {
                BeeAggressiveSoundInstance $$2 = new BeeAggressiveSoundInstance((Bee)$$0);
            } else {
                $$3 = new BeeFlyingSoundInstance((Bee)$$0);
            }
            this.minecraft.getSoundManager().queueTickingSound($$3);
        }
    }

    @Override
    public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        double $$1 = $$0.getX();
        double $$2 = $$0.getY();
        double $$3 = $$0.getZ();
        ExperienceOrb $$4 = new ExperienceOrb(this.level, $$1, $$2, $$3, $$0.getValue());
        $$4.syncPacketPositionCodec($$1, $$2, $$3);
        $$4.setYRot(0.0f);
        $$4.setXRot(0.0f);
        $$4.setId($$0.getId());
        this.level.putNonPlayerEntity($$0.getId(), $$4);
    }

    @Override
    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        $$1.lerpMotion((double)$$0.getXa() / 8000.0, (double)$$0.getYa() / 8000.0, (double)$$0.getZa() / 8000.0);
    }

    @Override
    public void handleSetEntityData(ClientboundSetEntityDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 != null) {
            $$1.getEntityData().assignValues($$0.packedItems());
        }
    }

    @Override
    public void handleAddPlayer(ClientboundAddPlayerPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        PlayerInfo $$1 = this.getPlayerInfo($$0.getPlayerId());
        if ($$1 == null) {
            LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", (Object)$$0.getPlayerId());
            return;
        }
        double $$2 = $$0.getX();
        double $$3 = $$0.getY();
        double $$4 = $$0.getZ();
        float $$5 = (float)($$0.getyRot() * 360) / 256.0f;
        float $$6 = (float)($$0.getxRot() * 360) / 256.0f;
        int $$7 = $$0.getEntityId();
        RemotePlayer $$8 = new RemotePlayer(this.minecraft.level, $$1.getProfile());
        $$8.setId($$7);
        $$8.syncPacketPositionCodec($$2, $$3, $$4);
        $$8.absMoveTo($$2, $$3, $$4, $$5, $$6);
        $$8.setOldPosAndRot();
        this.level.addPlayer($$7, $$8);
    }

    @Override
    public void handleTeleportEntity(ClientboundTeleportEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        double $$2 = $$0.getX();
        double $$3 = $$0.getY();
        double $$4 = $$0.getZ();
        $$1.syncPacketPositionCodec($$2, $$3, $$4);
        if (!$$1.isControlledByLocalInstance()) {
            float $$5 = (float)($$0.getyRot() * 360) / 256.0f;
            float $$6 = (float)($$0.getxRot() * 360) / 256.0f;
            $$1.lerpTo($$2, $$3, $$4, $$5, $$6, 3, true);
            $$1.setOnGround($$0.isOnGround());
        }
    }

    @Override
    public void handleSetCarriedItem(ClientboundSetCarriedItemPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (Inventory.isHotbarSlot($$0.getSlot())) {
            this.minecraft.player.getInventory().selected = $$0.getSlot();
        }
    }

    @Override
    public void handleMoveEntity(ClientboundMoveEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 == null) {
            return;
        }
        if (!$$1.isControlledByLocalInstance()) {
            if ($$0.hasPosition()) {
                VecDeltaCodec $$2 = $$1.getPositionCodec();
                Vec3 $$3 = $$2.decode($$0.getXa(), $$0.getYa(), $$0.getZa());
                $$2.setBase($$3);
                float $$4 = $$0.hasRotation() ? (float)($$0.getyRot() * 360) / 256.0f : $$1.getYRot();
                float $$5 = $$0.hasRotation() ? (float)($$0.getxRot() * 360) / 256.0f : $$1.getXRot();
                $$1.lerpTo($$3.x(), $$3.y(), $$3.z(), $$4, $$5, 3, false);
            } else if ($$0.hasRotation()) {
                float $$6 = (float)($$0.getyRot() * 360) / 256.0f;
                float $$7 = (float)($$0.getxRot() * 360) / 256.0f;
                $$1.lerpTo($$1.getX(), $$1.getY(), $$1.getZ(), $$6, $$7, 3, false);
            }
            $$1.setOnGround($$0.isOnGround());
        }
    }

    @Override
    public void handleRotateMob(ClientboundRotateHeadPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 == null) {
            return;
        }
        float $$2 = (float)($$0.getYHeadRot() * 360) / 256.0f;
        $$1.lerpHeadTo($$2, 3);
    }

    @Override
    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        $$02.getEntityIds().forEach($$0 -> this.level.removeEntity($$0, Entity.RemovalReason.DISCARDED));
    }

    @Override
    public void handleMovePlayer(ClientboundPlayerPositionPacket $$0) {
        double $$17;
        double $$16;
        double $$13;
        double $$12;
        double $$9;
        double $$8;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if ($$0.requestDismountVehicle()) {
            ((Player)$$1).removeVehicle();
        }
        Vec3 $$2 = $$1.getDeltaMovement();
        boolean $$3 = $$0.getRelativeArguments().contains((Object)RelativeMovement.X);
        boolean $$4 = $$0.getRelativeArguments().contains((Object)RelativeMovement.Y);
        boolean $$5 = $$0.getRelativeArguments().contains((Object)RelativeMovement.Z);
        if ($$3) {
            double $$6 = $$2.x();
            double $$7 = $$1.getX() + $$0.getX();
            $$1.xOld += $$0.getX();
            $$1.xo += $$0.getX();
        } else {
            $$8 = 0.0;
            $$1.xOld = $$9 = $$0.getX();
            $$1.xo = $$9;
        }
        if ($$4) {
            double $$10 = $$2.y();
            double $$11 = $$1.getY() + $$0.getY();
            $$1.yOld += $$0.getY();
            $$1.yo += $$0.getY();
        } else {
            $$12 = 0.0;
            $$1.yOld = $$13 = $$0.getY();
            $$1.yo = $$13;
        }
        if ($$5) {
            double $$14 = $$2.z();
            double $$15 = $$1.getZ() + $$0.getZ();
            $$1.zOld += $$0.getZ();
            $$1.zo += $$0.getZ();
        } else {
            $$16 = 0.0;
            $$1.zOld = $$17 = $$0.getZ();
            $$1.zo = $$17;
        }
        $$1.setPos($$9, $$13, $$17);
        $$1.setDeltaMovement($$8, $$12, $$16);
        float $$18 = $$0.getYRot();
        float $$19 = $$0.getXRot();
        if ($$0.getRelativeArguments().contains((Object)RelativeMovement.X_ROT)) {
            $$1.setXRot($$1.getXRot() + $$19);
            $$1.xRotO += $$19;
        } else {
            $$1.setXRot($$19);
            $$1.xRotO = $$19;
        }
        if ($$0.getRelativeArguments().contains((Object)RelativeMovement.Y_ROT)) {
            $$1.setYRot($$1.getYRot() + $$18);
            $$1.yRotO += $$18;
        } else {
            $$1.setYRot($$18);
            $$1.yRotO = $$18;
        }
        this.connection.send(new ServerboundAcceptTeleportationPacket($$0.getId()));
        this.connection.send(new ServerboundMovePlayerPacket.PosRot($$1.getX(), $$1.getY(), $$1.getZ(), $$1.getYRot(), $$1.getXRot(), false));
    }

    @Override
    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$12 = 0x13 | ($$0.shouldSuppressLightUpdates() ? 128 : 0);
        $$0.runUpdates((BiConsumer<BlockPos, BlockState>)((BiConsumer)($$1, $$2) -> this.level.setServerVerifiedBlockState((BlockPos)$$1, (BlockState)$$2, $$12)));
    }

    @Override
    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.updateLevelChunk($$0.getX(), $$0.getZ(), $$0.getChunkData());
        this.queueLightUpdate($$0.getX(), $$0.getZ(), $$0.getLightData());
    }

    private void updateLevelChunk(int $$0, int $$1, ClientboundLevelChunkPacketData $$2) {
        this.level.getChunkSource().replaceWithPacketData($$0, $$1, $$2.getReadBuffer(), $$2.getHeightmaps(), $$2.getBlockEntitiesTagsConsumer($$0, $$1));
    }

    private void queueLightUpdate(int $$0, int $$1, ClientboundLightUpdatePacketData $$2) {
        this.level.queueLightUpdate(() -> {
            this.applyLightData($$0, $$1, $$2);
            LevelChunk $$3 = this.level.getChunkSource().getChunk($$0, $$1, false);
            if ($$3 != null) {
                this.enableChunkLight($$3, $$0, $$1);
            }
        });
    }

    private void enableChunkLight(LevelChunk $$0, int $$1, int $$2) {
        LevelLightEngine $$3 = this.level.getChunkSource().getLightEngine();
        LevelChunkSection[] $$4 = $$0.getSections();
        ChunkPos $$5 = $$0.getPos();
        $$3.enableLightSources($$5, true);
        for (int $$6 = 0; $$6 < $$4.length; ++$$6) {
            LevelChunkSection $$7 = $$4[$$6];
            int $$8 = this.level.getSectionYFromSectionIndex($$6);
            $$3.updateSectionStatus(SectionPos.of($$5, $$8), $$7.hasOnlyAir());
            this.level.setSectionDirtyWithNeighbors($$1, $$8, $$2);
        }
        this.level.setLightReady($$1, $$2);
    }

    @Override
    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$1 = $$0.getX();
        int $$2 = $$0.getZ();
        ClientChunkCache $$3 = this.level.getChunkSource();
        $$3.drop($$1, $$2);
        this.queueLightUpdate($$0);
    }

    private void queueLightUpdate(ClientboundForgetLevelChunkPacket $$0) {
        this.level.queueLightUpdate(() -> {
            LevelLightEngine $$1 = this.level.getLightEngine();
            for (int $$2 = this.level.getMinSection(); $$2 < this.level.getMaxSection(); ++$$2) {
                $$1.updateSectionStatus(SectionPos.of($$0.getX(), $$2, $$0.getZ()), true);
            }
            $$1.enableLightSources(new ChunkPos($$0.getX(), $$0.getZ()), false);
            this.level.setLightReady($$0.getX(), $$0.getZ());
        });
    }

    @Override
    public void handleBlockUpdate(ClientboundBlockUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.setServerVerifiedBlockState($$0.getPos(), $$0.getBlockState(), 19);
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket $$0) {
        this.connection.disconnect($$0.getReason());
    }

    @Override
    public void onDisconnect(Component $$0) {
        this.minecraft.clearLevel();
        this.telemetryManager.onDisconnect();
        if (this.callbackScreen != null) {
            if (this.callbackScreen instanceof RealmsScreen) {
                this.minecraft.setScreen(new DisconnectedRealmsScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, $$0));
            } else {
                this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, $$0));
            }
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), GENERIC_DISCONNECT_MESSAGE, $$0));
        }
    }

    public void send(Packet<?> $$0) {
        this.connection.send($$0);
    }

    @Override
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getItemId());
        LivingEntity $$2 = (LivingEntity)this.level.getEntity($$0.getPlayerId());
        if ($$2 == null) {
            $$2 = this.minecraft.player;
        }
        if ($$1 != null) {
            if ($$1 instanceof ExperienceOrb) {
                this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, $$1, $$2));
            if ($$1 instanceof ItemEntity) {
                ItemEntity $$3 = (ItemEntity)$$1;
                ItemStack $$4 = $$3.getItem();
                $$4.shrink($$0.getAmount());
                if ($$4.isEmpty()) {
                    this.level.removeEntity($$0.getItemId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!($$1 instanceof ExperienceOrb)) {
                this.level.removeEntity($$0.getItemId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void handleSystemChat(ClientboundSystemChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getChatListener().handleSystemMessage($$0.content(), $$0.overlay());
    }

    @Override
    public void handlePlayerChat(ClientboundPlayerChatPacket $$0) {
        SignedMessageLink $$7;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Optional<SignedMessageBody> $$1 = $$0.body().unpack(this.messageSignatureCache);
        Optional<ChatType.Bound> $$2 = $$0.chatType().resolve(this.registryAccess.compositeAccess());
        if ($$1.isEmpty() || $$2.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        UUID $$3 = $$0.sender();
        PlayerInfo $$4 = this.getPlayerInfo($$3);
        if ($$4 == null) {
            this.connection.disconnect(CHAT_VALIDATION_FAILED_ERROR);
            return;
        }
        RemoteChatSession $$5 = $$4.getChatSession();
        if ($$5 != null) {
            SignedMessageLink $$6 = new SignedMessageLink($$0.index(), $$3, $$5.sessionId());
        } else {
            $$7 = SignedMessageLink.unsigned($$3);
        }
        PlayerChatMessage $$8 = new PlayerChatMessage($$7, $$0.signature(), (SignedMessageBody)((Object)$$1.get()), $$0.unsignedContent(), $$0.filterMask());
        if (!$$4.getMessageValidator().updateAndValidate($$8)) {
            this.connection.disconnect(CHAT_VALIDATION_FAILED_ERROR);
            return;
        }
        this.minecraft.getChatListener().handlePlayerChatMessage($$8, $$4.getProfile(), (ChatType.Bound)((Object)$$2.get()));
        this.messageSignatureCache.push($$8);
    }

    @Override
    public void handleDisguisedChat(ClientboundDisguisedChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Optional<ChatType.Bound> $$1 = $$0.chatType().resolve(this.registryAccess.compositeAccess());
        if ($$1.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.minecraft.getChatListener().handleDisguisedChatMessage($$0.message(), (ChatType.Bound)((Object)$$1.get()));
    }

    @Override
    public void handleDeleteChat(ClientboundDeleteChatPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Optional<MessageSignature> $$1 = $$0.messageSignature().unpack(this.messageSignatureCache);
        if ($$1.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
            return;
        }
        this.lastSeenMessages.ignorePending((MessageSignature)((Object)$$1.get()));
        if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue((MessageSignature)((Object)$$1.get()))) {
            this.minecraft.gui.getChat().deleteMessage((MessageSignature)((Object)$$1.get()));
        }
    }

    @Override
    public void handleAnimate(ClientboundAnimatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        if ($$0.getAction() == 0) {
            LivingEntity $$2 = (LivingEntity)$$1;
            $$2.swing(InteractionHand.MAIN_HAND);
        } else if ($$0.getAction() == 3) {
            LivingEntity $$3 = (LivingEntity)$$1;
            $$3.swing(InteractionHand.OFF_HAND);
        } else if ($$0.getAction() == 2) {
            Player $$4 = (Player)$$1;
            $$4.stopSleepInBed(false, false);
        } else if ($$0.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.CRIT);
        } else if ($$0.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.ENCHANTED_HIT);
        }
    }

    @Override
    public void handleHurtAnimation(ClientboundHurtAnimationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.id());
        if ($$1 == null) {
            return;
        }
        $$1.animateHurt($$0.yaw());
    }

    @Override
    public void handleSetTime(ClientboundSetTimePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.setGameTime($$0.getGameTime());
        this.minecraft.level.setDayTime($$0.getDayTime());
        this.telemetryManager.setTime($$0.getGameTime());
    }

    @Override
    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.setDefaultSpawnPos($$0.getPos(), $$0.getAngle());
        Screen screen = this.minecraft.screen;
        if (screen instanceof ReceivingLevelScreen) {
            ReceivingLevelScreen $$1 = (ReceivingLevelScreen)screen;
            $$1.loadingPacketsReceived();
        }
    }

    @Override
    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getVehicle());
        if ($$1 == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean $$2 = $$1.hasIndirectPassenger(this.minecraft.player);
        $$1.ejectPassengers();
        for (int $$3 : $$0.getPassengers()) {
            Entity $$4 = this.level.getEntity($$3);
            if ($$4 == null) continue;
            $$4.startRiding($$1, true);
            if ($$4 != this.minecraft.player || $$2) continue;
            if ($$1 instanceof Boat) {
                this.minecraft.player.yRotO = $$1.getYRot();
                this.minecraft.player.setYRot($$1.getYRot());
                this.minecraft.player.setYHeadRot($$1.getYRot());
            }
            MutableComponent $$5 = Component.translatable("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage());
            this.minecraft.gui.setOverlayMessage($$5, false);
            this.minecraft.getNarrator().sayNow($$5);
        }
    }

    @Override
    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getSourceId());
        if ($$1 instanceof Mob) {
            ((Mob)$$1).setDelayedLeashHolderId($$0.getDestId());
        }
    }

    private static ItemStack findTotem(Player $$0) {
        for (InteractionHand $$1 : InteractionHand.values()) {
            ItemStack $$2 = $$0.getItemInHand($$1);
            if (!$$2.is(Items.TOTEM_OF_UNDYING)) continue;
            return $$2;
        }
        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void handleEntityEvent(ClientboundEntityEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 != null) {
            if ($$0.getEventId() == 21) {
                this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)$$1));
            } else if ($$0.getEventId() == 35) {
                int $$2 = 40;
                this.minecraft.particleEngine.createTrackingEmitter($$1, ParticleTypes.TOTEM_OF_UNDYING, 30);
                this.level.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.TOTEM_USE, $$1.getSoundSource(), 1.0f, 1.0f, false);
                if ($$1 == this.minecraft.player) {
                    this.minecraft.gameRenderer.displayItemActivation(ClientPacketListener.findTotem(this.minecraft.player));
                }
            } else {
                $$1.handleEntityEvent($$0.getEventId());
            }
        }
    }

    @Override
    public void handleSetHealth(ClientboundSetHealthPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.hurtTo($$0.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel($$0.getFood());
        this.minecraft.player.getFoodData().setSaturation($$0.getSaturation());
    }

    @Override
    public void handleSetExperience(ClientboundSetExperiencePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.setExperienceValues($$0.getExperienceProgress(), $$0.getTotalExperience(), $$0.getExperienceLevel());
    }

    @Override
    public void handleRespawn(ClientboundRespawnPacket $$0) {
        List<SynchedEntityData.DataValue<?>> $$12;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ResourceKey<Level> $$1 = $$0.getDimension();
        Holder.Reference<DimensionType> $$2 = this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow($$0.getDimensionType());
        LocalPlayer $$3 = this.minecraft.player;
        int $$4 = $$3.getId();
        if ($$1 != $$3.level.dimension()) {
            ClientLevel.ClientLevelData $$9;
            Scoreboard $$5 = this.level.getScoreboard();
            Map<String, MapItemSavedData> $$6 = this.level.getAllMapData();
            boolean $$7 = $$0.isDebug();
            boolean $$8 = $$0.isFlat();
            this.levelData = $$9 = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), $$8);
            this.level = new ClientLevel(this, $$9, $$1, $$2, this.serverChunkRadius, this.serverSimulationDistance, (Supplier<ProfilerFiller>)((Supplier)this.minecraft::getProfiler), this.minecraft.levelRenderer, $$7, $$0.getSeed());
            this.level.setScoreboard($$5);
            this.level.addMapData($$6);
            this.minecraft.setLevel(this.level);
            this.minecraft.setScreen(new ReceivingLevelScreen());
        }
        String $$10 = $$3.getServerBrand();
        this.minecraft.cameraEntity = null;
        if ($$3.hasContainerOpen()) {
            $$3.closeContainer();
        }
        LocalPlayer $$11 = this.minecraft.gameMode.createPlayer(this.level, $$3.getStats(), $$3.getRecipeBook(), $$3.isShiftKeyDown(), $$3.isSprinting());
        $$11.setId($$4);
        this.minecraft.player = $$11;
        if ($$1 != $$3.level.dimension()) {
            this.minecraft.getMusicManager().stopPlaying();
        }
        this.minecraft.cameraEntity = $$11;
        if ($$0.shouldKeep((byte)2) && ($$12 = $$3.getEntityData().getNonDefaultValues()) != null) {
            $$11.getEntityData().assignValues($$12);
        }
        if ($$0.shouldKeep((byte)1)) {
            $$11.getAttributes().assignValues($$3.getAttributes());
        }
        $$11.resetPos();
        $$11.setServerBrand($$10);
        this.level.addPlayer($$4, $$11);
        $$11.setYRot(-180.0f);
        $$11.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer($$11);
        $$11.setReducedDebugInfo($$3.isReducedDebugInfo());
        $$11.setShowDeathScreen($$3.shouldShowDeathScreen());
        $$11.setLastDeathLocation($$0.getLastDeathLocation());
        if (this.minecraft.screen instanceof DeathScreen) {
            this.minecraft.setScreen(null);
        }
        this.minecraft.gameMode.setLocalMode($$0.getPlayerGameType(), $$0.getPreviousPlayerGameType());
    }

    @Override
    public void handleExplosion(ClientboundExplodePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Explosion $$1 = new Explosion(this.minecraft.level, null, $$0.getX(), $$0.getY(), $$0.getZ(), $$0.getPower(), $$0.getToBlow());
        $$1.finalizeExplosion(true);
        this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add($$0.getKnockbackX(), $$0.getKnockbackY(), $$0.getKnockbackZ()));
    }

    @Override
    public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if ($$1 instanceof AbstractHorse) {
            LocalPlayer $$2 = this.minecraft.player;
            AbstractHorse $$3 = (AbstractHorse)$$1;
            SimpleContainer $$4 = new SimpleContainer($$0.getSize());
            HorseInventoryMenu $$5 = new HorseInventoryMenu($$0.getContainerId(), $$2.getInventory(), $$4, $$3);
            $$2.containerMenu = $$5;
            this.minecraft.setScreen(new HorseInventoryScreen($$5, $$2.getInventory(), $$3));
        }
    }

    @Override
    public void handleOpenScreen(ClientboundOpenScreenPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        MenuScreens.create($$0.getType(), this.minecraft, $$0.getContainerId(), $$0.getTitle());
    }

    @Override
    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        ItemStack $$2 = $$0.getItem();
        int $$3 = $$0.getSlot();
        this.minecraft.getTutorial().onGetItem($$2);
        if ($$0.getContainerId() == -1) {
            if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
                $$1.containerMenu.setCarried($$2);
            }
        } else if ($$0.getContainerId() == -2) {
            $$1.getInventory().setItem($$3, $$2);
        } else {
            boolean $$4 = false;
            Screen screen = this.minecraft.screen;
            if (screen instanceof CreativeModeInventoryScreen) {
                CreativeModeInventoryScreen $$5 = (CreativeModeInventoryScreen)screen;
                boolean bl = $$4 = !$$5.isInventoryOpen();
            }
            if ($$0.getContainerId() == 0 && InventoryMenu.isHotbarSlot($$3)) {
                ItemStack $$6;
                if (!$$2.isEmpty() && (($$6 = $$1.inventoryMenu.getSlot($$3).getItem()).isEmpty() || $$6.getCount() < $$2.getCount())) {
                    $$2.setPopTime(5);
                }
                $$1.inventoryMenu.setItem($$3, $$0.getStateId(), $$2);
            } else if (!($$0.getContainerId() != $$1.containerMenu.containerId || $$0.getContainerId() == 0 && $$4)) {
                $$1.containerMenu.setItem($$3, $$0.getStateId(), $$2);
            }
        }
    }

    @Override
    public void handleContainerContent(ClientboundContainerSetContentPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if ($$0.getContainerId() == 0) {
            $$1.inventoryMenu.initializeContents($$0.getStateId(), $$0.getItems(), $$0.getCarriedItem());
        } else if ($$0.getContainerId() == $$1.containerMenu.containerId) {
            $$1.containerMenu.initializeContents($$0.getStateId(), $$0.getItems(), $$0.getCarriedItem());
        }
    }

    @Override
    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        BlockPos $$1 = $$0.getPos();
        BlockEntity $$2 = this.level.getBlockEntity($$1);
        if (!($$2 instanceof SignBlockEntity)) {
            BlockState $$3 = this.level.getBlockState($$1);
            $$2 = new SignBlockEntity($$1, $$3);
            $$2.setLevel(this.level);
        }
        this.minecraft.player.openTextEdit((SignBlockEntity)$$2);
    }

    @Override
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        BlockPos $$12 = $$0.getPos();
        this.minecraft.level.getBlockEntity($$12, $$0.getType()).ifPresent($$1 -> {
            CompoundTag $$2 = $$0.getTag();
            if ($$2 != null) {
                $$1.load($$2);
            }
            if ($$1 instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }
        });
    }

    @Override
    public void handleContainerSetData(ClientboundContainerSetDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        if ($$1.containerMenu != null && $$1.containerMenu.containerId == $$0.getContainerId()) {
            $$1.containerMenu.setData($$0.getId(), $$0.getValue());
        }
    }

    @Override
    public void handleSetEquipment(ClientboundSetEquipmentPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$12 = this.level.getEntity($$0.getEntity());
        if ($$12 != null) {
            $$0.getSlots().forEach($$1 -> $$12.setItemSlot((EquipmentSlot)((Object)((Object)$$1.getFirst())), (ItemStack)$$1.getSecond()));
        }
    }

    @Override
    public void handleContainerClose(ClientboundContainerClosePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.player.clientSideCloseContainer();
    }

    @Override
    public void handleBlockEvent(ClientboundBlockEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.blockEvent($$0.getPos(), $$0.getBlock(), $$0.getB0(), $$0.getB1());
    }

    @Override
    public void handleBlockDestruction(ClientboundBlockDestructionPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.destroyBlockProgress($$0.getId(), $$0.getPos(), $$0.getProgress());
    }

    @Override
    public void handleGameEvent(ClientboundGameEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        ClientboundGameEventPacket.Type $$2 = $$0.getEvent();
        float $$3 = $$0.getParam();
        int $$4 = Mth.floor($$3 + 0.5f);
        if ($$2 == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
            ((Player)$$1).displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid"), false);
        } else if ($$2 == ClientboundGameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0f);
        } else if ($$2 == ClientboundGameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0f);
        } else if ($$2 == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
            this.minecraft.gameMode.setLocalMode(GameType.byId($$4));
        } else if ($$2 == ClientboundGameEventPacket.WIN_GAME) {
            if ($$4 == 0) {
                this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(new ReceivingLevelScreen());
            } else if ($$4 == 1) {
                this.minecraft.setScreen(new WinScreen(true, new LogoRenderer(false), () -> this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN))));
            }
        } else if ($$2 == ClientboundGameEventPacket.DEMO_EVENT) {
            Options $$5 = this.minecraft.options;
            if ($$3 == 0.0f) {
                this.minecraft.setScreen(new DemoIntroScreen());
            } else if ($$3 == 101.0f) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.movement", $$5.keyUp.getTranslatedKeyMessage(), $$5.keyLeft.getTranslatedKeyMessage(), $$5.keyDown.getTranslatedKeyMessage(), $$5.keyRight.getTranslatedKeyMessage()));
            } else if ($$3 == 102.0f) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.jump", $$5.keyJump.getTranslatedKeyMessage()));
            } else if ($$3 == 103.0f) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.inventory", $$5.keyInventory.getTranslatedKeyMessage()));
            } else if ($$3 == 104.0f) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.day.6", $$5.keyScreenshot.getTranslatedKeyMessage()));
            }
        } else if ($$2 == ClientboundGameEventPacket.ARROW_HIT_PLAYER) {
            this.level.playSound($$1, $$1.getX(), $$1.getEyeY(), $$1.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18f, 0.45f);
        } else if ($$2 == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
            this.level.setRainLevel($$3);
        } else if ($$2 == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            this.level.setThunderLevel($$3);
        } else if ($$2 == ClientboundGameEventPacket.PUFFER_FISH_STING) {
            this.level.playSound($$1, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0f, 1.0f);
        } else if ($$2 == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
            this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, $$1.getX(), $$1.getY(), $$1.getZ(), 0.0, 0.0, 0.0);
            if ($$4 == 1) {
                this.level.playSound($$1, $$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0f, 1.0f);
            }
        } else if ($$2 == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
            this.minecraft.player.setShowDeathScreen($$3 == 0.0f);
        }
    }

    @Override
    public void handleMapItemData(ClientboundMapItemDataPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        MapRenderer $$1 = this.minecraft.gameRenderer.getMapRenderer();
        int $$2 = $$0.getMapId();
        String $$3 = MapItem.makeKey($$2);
        MapItemSavedData $$4 = this.minecraft.level.getMapData($$3);
        if ($$4 == null) {
            $$4 = MapItemSavedData.createForClient($$0.getScale(), $$0.isLocked(), this.minecraft.level.dimension());
            this.minecraft.level.overrideMapData($$3, $$4);
        }
        $$0.applyToMap($$4);
        $$1.update($$2, $$4);
    }

    @Override
    public void handleLevelEvent(ClientboundLevelEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent($$0.getType(), $$0.getPos(), $$0.getData());
        } else {
            this.minecraft.level.levelEvent($$0.getType(), $$0.getPos(), $$0.getData());
        }
    }

    @Override
    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.advancements.update($$0);
    }

    @Override
    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ResourceLocation $$1 = $$0.getTab();
        if ($$1 == null) {
            this.advancements.setSelectedTab(null, false);
        } else {
            Advancement $$2 = this.advancements.getAdvancements().get($$1);
            this.advancements.setSelectedTab($$2, false);
        }
    }

    @Override
    public void handleCommands(ClientboundCommandsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.commands = new CommandDispatcher($$0.getRoot(CommandBuildContext.simple(this.registryAccess.compositeAccess(), this.enabledFeatures)));
    }

    @Override
    public void handleStopSoundEvent(ClientboundStopSoundPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.getSoundManager().stop($$0.getName(), $$0.getSource());
    }

    @Override
    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.suggestionsProvider.completeCustomSuggestions($$0.getId(), $$0.getSuggestions());
    }

    @Override
    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.recipeManager.replaceRecipes((Iterable<Recipe<?>>)$$0.getRecipes());
        ClientRecipeBook $$1 = this.minecraft.player.getRecipeBook();
        $$1.setupCollections((Iterable<Recipe<?>>)this.recipeManager.getRecipes(), this.minecraft.level.registryAccess());
        this.minecraft.populateSearchTree(SearchRegistry.RECIPE_COLLECTIONS, $$1.getCollections());
    }

    @Override
    public void handleLookAt(ClientboundPlayerLookAtPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Vec3 $$1 = $$0.getPosition(this.level);
        if ($$1 != null) {
            this.minecraft.player.lookAt($$0.getFromAnchor(), $$1);
        }
    }

    @Override
    public void handleTagQueryPacket(ClientboundTagQueryPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if (!this.debugQueryHandler.handleResponse($$0.getTransactionId(), $$0.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)$$0.getTransactionId());
        }
    }

    @Override
    public void handleAwardStats(ClientboundAwardStatsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (Map.Entry $$1 : $$0.getStats().entrySet()) {
            Stat $$2 = (Stat)$$1.getKey();
            int $$3 = (Integer)$$1.getValue();
            this.minecraft.player.getStats().setValue(this.minecraft.player, $$2, $$3);
        }
        if (this.minecraft.screen instanceof StatsUpdateListener) {
            ((StatsUpdateListener)((Object)this.minecraft.screen)).onStatsUpdated();
        }
    }

    @Override
    public void handleAddOrRemoveRecipes(ClientboundRecipePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ClientRecipeBook $$12 = this.minecraft.player.getRecipeBook();
        $$12.setBookSettings($$0.getBookSettings());
        ClientboundRecipePacket.State $$2 = $$0.getState();
        switch ($$2) {
            case REMOVE: {
                for (ResourceLocation $$3 : $$0.getRecipes()) {
                    this.recipeManager.byKey($$3).ifPresent($$12::remove);
                }
                break;
            }
            case INIT: {
                for (ResourceLocation $$4 : $$0.getRecipes()) {
                    this.recipeManager.byKey($$4).ifPresent($$12::add);
                }
                for (ResourceLocation $$5 : $$0.getHighlights()) {
                    this.recipeManager.byKey($$5).ifPresent($$12::addHighlight);
                }
                break;
            }
            case ADD: {
                for (ResourceLocation $$6 : $$0.getRecipes()) {
                    this.recipeManager.byKey($$6).ifPresent($$1 -> {
                        $$12.add((Recipe<?>)$$1);
                        $$12.addHighlight((Recipe<?>)$$1);
                        RecipeToast.addOrUpdate(this.minecraft.getToasts(), $$1);
                    });
                }
                break;
            }
        }
        $$12.getCollections().forEach($$1 -> $$1.updateKnownRecipes($$12));
        if (this.minecraft.screen instanceof RecipeUpdateListener) {
            ((RecipeUpdateListener)((Object)this.minecraft.screen)).recipesUpdated();
        }
    }

    @Override
    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if (!($$1 instanceof LivingEntity)) {
            return;
        }
        MobEffect $$2 = $$0.getEffect();
        if ($$2 == null) {
            return;
        }
        MobEffectInstance $$3 = new MobEffectInstance($$2, $$0.getEffectDurationTicks(), $$0.getEffectAmplifier(), $$0.isEffectAmbient(), $$0.isEffectVisible(), $$0.effectShowsIcon(), null, (Optional<MobEffectInstance.FactorData>)Optional.ofNullable((Object)$$0.getFactorData()));
        ((LivingEntity)$$1).forceAddEffect($$3, null);
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        $$0.getTags().forEach(this::updateTagsForRegistry);
        if (!this.connection.isMemoryConnection()) {
            Blocks.rebuildCache();
        }
        CreativeModeTabs.searchTab().rebuildSearchTree();
    }

    @Override
    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.enabledFeatures = FeatureFlags.REGISTRY.fromNames((Iterable<ResourceLocation>)$$0.features());
    }

    private <T> void updateTagsForRegistry(ResourceKey<? extends Registry<? extends T>> $$0, TagNetworkSerialization.NetworkPayload $$1) {
        if ($$1.isEmpty()) {
            return;
        }
        Registry $$2 = (Registry)this.registryAccess.compositeAccess().registry($$0).orElseThrow(() -> new IllegalStateException("Unknown registry " + $$0));
        ResourceKey $$3 = $$0;
        HashMap $$4 = new HashMap();
        TagNetworkSerialization.deserializeTagsFromNetwork($$3, $$2, $$1, (arg_0, arg_1) -> ((Map)$$4).put(arg_0, arg_1));
        $$2.bindTags($$4);
    }

    @Override
    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket $$0) {
    }

    @Override
    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket $$0) {
    }

    @Override
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getPlayerId());
        if ($$1 == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.setScreen(new DeathScreen($$0.getMessage(), this.level.getLevelData().isHardcore()));
            } else {
                this.minecraft.player.respawn();
            }
        }
    }

    @Override
    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.levelData.setDifficulty($$0.getDifficulty());
        this.levelData.setDifficultyLocked($$0.isLocked());
    }

    @Override
    public void handleSetCamera(ClientboundSetCameraPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 != null) {
            this.minecraft.setCameraEntity($$1);
        }
    }

    @Override
    public void handleInitializeBorder(ClientboundInitializeBorderPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        WorldBorder $$1 = this.level.getWorldBorder();
        $$1.setCenter($$0.getNewCenterX(), $$0.getNewCenterZ());
        long $$2 = $$0.getLerpTime();
        if ($$2 > 0L) {
            $$1.lerpSizeBetween($$0.getOldSize(), $$0.getNewSize(), $$2);
        } else {
            $$1.setSize($$0.getNewSize());
        }
        $$1.setAbsoluteMaxSize($$0.getNewAbsoluteMaxSize());
        $$1.setWarningBlocks($$0.getWarningBlocks());
        $$1.setWarningTime($$0.getWarningTime());
    }

    @Override
    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setCenter($$0.getNewCenterX(), $$0.getNewCenterZ());
    }

    @Override
    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().lerpSizeBetween($$0.getOldSize(), $$0.getNewSize(), $$0.getLerpTime());
    }

    @Override
    public void handleSetBorderSize(ClientboundSetBorderSizePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setSize($$0.getSize());
    }

    @Override
    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setWarningBlocks($$0.getWarningBlocks());
    }

    @Override
    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getWorldBorder().setWarningTime($$0.getWarningDelay());
    }

    @Override
    public void handleTitlesClear(ClientboundClearTitlesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.clear();
        if ($$0.shouldResetTimes()) {
            this.minecraft.gui.resetTitleTimes();
        }
    }

    @Override
    public void handleServerData(ClientboundServerDataPacket $$02) {
        PacketUtils.ensureRunningOnSameThread($$02, this, this.minecraft);
        if (this.serverData == null) {
            return;
        }
        $$02.getMotd().ifPresent($$0 -> {
            this.serverData.motd = $$0;
        });
        $$02.getIconBase64().ifPresent($$0 -> {
            try {
                this.serverData.setIconB64(ServerData.parseFavicon($$0));
            }
            catch (ParseException $$1) {
                LOGGER.error("Invalid server icon", (Throwable)$$1);
            }
        });
        this.serverData.setEnforcesSecureChat($$02.enforcesSecureChat());
        ServerList.saveSingleServer(this.serverData);
        if (!$$02.enforcesSecureChat()) {
            SystemToast $$1 = SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST);
            this.minecraft.getToasts().addToast($$1);
        }
    }

    @Override
    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.suggestionsProvider.modifyCustomCompletions($$0.action(), $$0.entries());
    }

    @Override
    public void setActionBarText(ClientboundSetActionBarTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setOverlayMessage($$0.getText(), false);
    }

    @Override
    public void setTitleText(ClientboundSetTitleTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setTitle($$0.getText());
    }

    @Override
    public void setSubtitleText(ClientboundSetSubtitleTextPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setSubtitle($$0.getText());
    }

    @Override
    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.setTimes($$0.getFadeIn(), $$0.getStay(), $$0.getFadeOut());
    }

    @Override
    public void handleTabListCustomisation(ClientboundTabListPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.getTabList().setHeader($$0.getHeader().getString().isEmpty() ? null : $$0.getHeader());
        this.minecraft.gui.getTabList().setFooter($$0.getFooter().getString().isEmpty() ? null : $$0.getFooter());
    }

    @Override
    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = $$0.getEntity(this.level);
        if ($$1 instanceof LivingEntity) {
            ((LivingEntity)$$1).removeEffectNoUpdate($$0.getEffect());
        }
    }

    @Override
    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (UUID $$1 : $$0.profileIds()) {
            this.minecraft.getPlayerSocialManager().removePlayer($$1);
            PlayerInfo $$2 = (PlayerInfo)this.playerInfoMap.remove((Object)$$1);
            if ($$2 == null) continue;
            this.listedPlayers.remove((Object)$$2);
        }
    }

    @Override
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (ClientboundPlayerInfoUpdatePacket.Entry $$1 : $$0.newEntries()) {
            PlayerInfo $$2 = new PlayerInfo($$1.profile(), this.enforcesSecureChat());
            if (this.playerInfoMap.putIfAbsent((Object)$$1.profileId(), (Object)$$2) != null) continue;
            this.minecraft.getPlayerSocialManager().addPlayer($$2);
        }
        for (ClientboundPlayerInfoUpdatePacket.Entry $$3 : $$0.entries()) {
            PlayerInfo $$4 = (PlayerInfo)this.playerInfoMap.get((Object)$$3.profileId());
            if ($$4 == null) {
                LOGGER.warn("Ignoring player info update for unknown player {}", (Object)$$3.profileId());
                continue;
            }
            for (ClientboundPlayerInfoUpdatePacket.Action $$5 : $$0.actions()) {
                this.applyPlayerInfoUpdate($$5, $$3, $$4);
            }
        }
    }

    private void applyPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket.Action $$0, ClientboundPlayerInfoUpdatePacket.Entry $$1, PlayerInfo $$2) {
        switch ($$0) {
            case INITIALIZE_CHAT: {
                this.initializeChatSession($$1, $$2);
                break;
            }
            case UPDATE_GAME_MODE: {
                $$2.setGameMode($$1.gameMode());
                break;
            }
            case UPDATE_LISTED: {
                if ($$1.listed()) {
                    this.listedPlayers.add((Object)$$2);
                    break;
                }
                this.listedPlayers.remove((Object)$$2);
                break;
            }
            case UPDATE_LATENCY: {
                $$2.setLatency($$1.latency());
                break;
            }
            case UPDATE_DISPLAY_NAME: {
                $$2.setTabListDisplayName($$1.displayName());
            }
        }
    }

    private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry $$0, PlayerInfo $$1) {
        GameProfile $$2 = $$1.getProfile();
        RemoteChatSession.Data $$3 = $$0.chatSession();
        if ($$3 != null) {
            try {
                RemoteChatSession $$4 = $$3.validate($$2, this.minecraft.getServiceSignatureValidator(), ProfilePublicKey.EXPIRY_GRACE_PERIOD);
                $$1.setChatSession($$4);
            }
            catch (ProfilePublicKey.ValidationException $$5) {
                LOGGER.error("Failed to validate profile key for player: '{}'", (Object)$$2.getName(), (Object)$$5);
                this.connection.disconnect($$5.getComponent());
            }
        } else {
            $$1.clearChatSession(this.enforcesSecureChat());
        }
    }

    private boolean enforcesSecureChat() {
        return this.serverData != null && this.serverData.enforcesSecureChat();
    }

    @Override
    public void handleKeepAlive(ClientboundKeepAlivePacket $$0) {
        this.send(new ServerboundKeepAlivePacket($$0.getId()));
    }

    @Override
    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        LocalPlayer $$1 = this.minecraft.player;
        $$1.getAbilities().flying = $$0.isFlying();
        $$1.getAbilities().instabuild = $$0.canInstabuild();
        $$1.getAbilities().invulnerable = $$0.isInvulnerable();
        $$1.getAbilities().mayfly = $$0.canFly();
        $$1.getAbilities().setFlyingSpeed($$0.getFlyingSpeed());
        $$1.getAbilities().setWalkingSpeed($$0.getWalkingSpeed());
    }

    @Override
    public void handleSoundEvent(ClientboundSoundPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.level.playSeededSound((Player)this.minecraft.player, $$0.getX(), $$0.getY(), $$0.getZ(), $$0.getSound(), $$0.getSource(), $$0.getVolume(), $$0.getPitch(), $$0.getSeed());
    }

    @Override
    public void handleSoundEntityEvent(ClientboundSoundEntityPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getId());
        if ($$1 == null) {
            return;
        }
        this.minecraft.level.playSeededSound(this.minecraft.player, $$1, $$0.getSound(), $$0.getSource(), $$0.getVolume(), $$0.getPitch(), $$0.getSeed());
    }

    @Override
    public void handleResourcePack(ClientboundResourcePackPacket $$0) {
        URL $$1 = ClientPacketListener.parseResourcePackUrl($$0.getUrl());
        if ($$1 == null) {
            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return;
        }
        String $$2 = $$0.getHash();
        boolean $$3 = $$0.isRequired();
        if (this.serverData != null && this.serverData.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.downloadCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack($$1, $$2, true));
        } else if (this.serverData == null || this.serverData.getResourcePackStatus() == ServerData.ServerPackStatus.PROMPT || $$3 && this.serverData.getResourcePackStatus() == ServerData.ServerPackStatus.DISABLED) {
            this.minecraft.execute(() -> this.minecraft.setScreen(new ConfirmScreen($$3 -> {
                this.minecraft.setScreen(null);
                if ($$3) {
                    if (this.serverData != null) {
                        this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                    }
                    this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                    this.downloadCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack($$1, $$2, true));
                } else {
                    this.send(ServerboundResourcePackPacket.Action.DECLINED);
                    if ($$3) {
                        this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                    } else if (this.serverData != null) {
                        this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                    }
                }
                if (this.serverData != null) {
                    ServerList.saveSingleServer(this.serverData);
                }
            }, $$3 ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"), ClientPacketListener.preparePackPrompt($$3 ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD) : Component.translatable("multiplayer.texturePrompt.line2"), $$0.getPrompt()), $$3 ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES, $$3 ? Component.translatable("menu.disconnect") : CommonComponents.GUI_NO)));
        } else {
            this.send(ServerboundResourcePackPacket.Action.DECLINED);
            if ($$3) {
                this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
            }
        }
    }

    private static Component preparePackPrompt(Component $$0, @Nullable Component $$1) {
        if ($$1 == null) {
            return $$0;
        }
        return Component.translatable("multiplayer.texturePrompt.serverPrompt", $$0, $$1);
    }

    @Nullable
    private static URL parseResourcePackUrl(String $$0) {
        try {
            URL $$1 = new URL($$0);
            String $$2 = $$1.getProtocol();
            if ("http".equals((Object)$$2) || "https".equals((Object)$$2)) {
                return $$1;
            }
        }
        catch (MalformedURLException $$3) {
            return null;
        }
        return null;
    }

    private void downloadCallback(CompletableFuture<?> $$02) {
        $$02.thenRun(() -> this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)).exceptionally($$0 -> {
            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return null;
        });
    }

    private void send(ServerboundResourcePackPacket.Action $$0) {
        this.connection.send(new ServerboundResourcePackPacket($$0));
    }

    @Override
    public void handleBossUpdate(ClientboundBossEventPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.minecraft.gui.getBossOverlay().update($$0);
    }

    @Override
    public void handleItemCooldown(ClientboundCooldownPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.getDuration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown($$0.getItem());
        } else {
            this.minecraft.player.getCooldowns().addCooldown($$0.getItem(), $$0.getDuration());
        }
    }

    @Override
    public void handleMoveVehicle(ClientboundMoveVehiclePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.minecraft.player.getRootVehicle();
        if ($$1 != this.minecraft.player && $$1.isControlledByLocalInstance()) {
            $$1.absMoveTo($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getYRot(), $$0.getXRot());
            this.connection.send(new ServerboundMoveVehiclePacket($$1));
        }
    }

    @Override
    public void handleOpenBook(ClientboundOpenBookPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ItemStack $$1 = this.minecraft.player.getItemInHand($$0.getHand());
        if ($$1.is(Items.WRITTEN_BOOK)) {
            this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess($$1)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleCustomPayload(ClientboundCustomPayloadPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        ResourceLocation $$1 = $$0.getIdentifier();
        FriendlyByteBuf $$2 = null;
        try {
            $$2 = $$0.getData();
            if (ClientboundCustomPayloadPacket.BRAND.equals($$1)) {
                String $$3 = $$2.readUtf();
                this.minecraft.player.setServerBrand($$3);
                this.telemetryManager.onServerBrandReceived($$3);
            } else if (ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals($$1)) {
                int $$4 = $$2.readInt();
                float $$5 = $$2.readFloat();
                Path $$6 = Path.createFromStream($$2);
                this.minecraft.debugRenderer.pathfindingRenderer.addPath($$4, $$6, $$5);
            } else if (ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals($$1)) {
                long $$7 = $$2.readVarLong();
                BlockPos $$8 = $$2.readBlockPos();
                ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate($$7, $$8);
            } else if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals($$1)) {
                DimensionType $$9 = (DimensionType)((Object)this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).get($$2.readResourceLocation()));
                BoundingBox $$10 = new BoundingBox($$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt());
                int $$11 = $$2.readInt();
                ArrayList $$12 = Lists.newArrayList();
                ArrayList $$13 = Lists.newArrayList();
                for (int $$14 = 0; $$14 < $$11; ++$$14) {
                    $$12.add((Object)new BoundingBox($$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt(), $$2.readInt()));
                    $$13.add((Object)$$2.readBoolean());
                }
                this.minecraft.debugRenderer.structureRenderer.addBoundingBox($$10, (List<BoundingBox>)$$12, (List<Boolean>)$$13, $$9);
            } else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals($$1)) {
                ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos($$2.readBlockPos(), $$2.readFloat(), $$2.readFloat(), $$2.readFloat(), $$2.readFloat(), $$2.readFloat());
            } else if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals($$1)) {
                int $$15 = $$2.readInt();
                for (int $$16 = 0; $$16 < $$15; ++$$16) {
                    this.minecraft.debugRenderer.villageSectionsDebugRenderer.setVillageSection($$2.readSectionPos());
                }
                int $$17 = $$2.readInt();
                for (int $$18 = 0; $$18 < $$17; ++$$18) {
                    this.minecraft.debugRenderer.villageSectionsDebugRenderer.setNotVillageSection($$2.readSectionPos());
                }
            } else if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals($$1)) {
                BlockPos $$19 = $$2.readBlockPos();
                String $$20 = $$2.readUtf();
                int $$21 = $$2.readInt();
                BrainDebugRenderer.PoiInfo $$22 = new BrainDebugRenderer.PoiInfo($$19, $$20, $$21);
                this.minecraft.debugRenderer.brainDebugRenderer.addPoi($$22);
            } else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals($$1)) {
                BlockPos $$23 = $$2.readBlockPos();
                this.minecraft.debugRenderer.brainDebugRenderer.removePoi($$23);
            } else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals($$1)) {
                BlockPos $$24 = $$2.readBlockPos();
                int $$25 = $$2.readInt();
                this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount($$24, $$25);
            } else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals($$1)) {
                BlockPos $$26 = $$2.readBlockPos();
                int $$27 = $$2.readInt();
                int $$28 = $$2.readInt();
                ArrayList $$29 = Lists.newArrayList();
                for (int $$30 = 0; $$30 < $$28; ++$$30) {
                    int $$31 = $$2.readInt();
                    boolean $$32 = $$2.readBoolean();
                    String $$33 = $$2.readUtf(255);
                    $$29.add((Object)new GoalSelectorDebugRenderer.DebugGoal($$26, $$31, $$33, $$32));
                }
                this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector($$27, (List<GoalSelectorDebugRenderer.DebugGoal>)$$29);
            } else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals($$1)) {
                int $$34 = $$2.readInt();
                ArrayList $$35 = Lists.newArrayList();
                for (int $$36 = 0; $$36 < $$34; ++$$36) {
                    $$35.add((Object)$$2.readBlockPos());
                }
                this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters((Collection<BlockPos>)$$35);
            } else if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals($$1)) {
                double $$37 = $$2.readDouble();
                double $$38 = $$2.readDouble();
                double $$39 = $$2.readDouble();
                PositionImpl $$40 = new PositionImpl($$37, $$38, $$39);
                UUID $$41 = $$2.readUUID();
                int $$42 = $$2.readInt();
                String $$43 = $$2.readUtf();
                String $$44 = $$2.readUtf();
                int $$45 = $$2.readInt();
                float $$46 = $$2.readFloat();
                float $$47 = $$2.readFloat();
                String $$48 = $$2.readUtf();
                Path $$49 = (Path)$$2.readNullable(Path::createFromStream);
                boolean $$50 = $$2.readBoolean();
                int $$51 = $$2.readInt();
                BrainDebugRenderer.BrainDump $$52 = new BrainDebugRenderer.BrainDump($$41, $$42, $$43, $$44, $$45, $$46, $$47, $$40, $$48, $$49, $$50, $$51);
                int $$53 = $$2.readVarInt();
                for (int $$54 = 0; $$54 < $$53; ++$$54) {
                    String $$55 = $$2.readUtf();
                    $$52.activities.add((Object)$$55);
                }
                int $$56 = $$2.readVarInt();
                for (int $$57 = 0; $$57 < $$56; ++$$57) {
                    String $$58 = $$2.readUtf();
                    $$52.behaviors.add((Object)$$58);
                }
                int $$59 = $$2.readVarInt();
                for (int $$60 = 0; $$60 < $$59; ++$$60) {
                    String $$61 = $$2.readUtf();
                    $$52.memories.add((Object)$$61);
                }
                int $$62 = $$2.readVarInt();
                for (int $$63 = 0; $$63 < $$62; ++$$63) {
                    BlockPos $$64 = $$2.readBlockPos();
                    $$52.pois.add((Object)$$64);
                }
                int $$65 = $$2.readVarInt();
                for (int $$66 = 0; $$66 < $$65; ++$$66) {
                    BlockPos $$67 = $$2.readBlockPos();
                    $$52.potentialPois.add((Object)$$67);
                }
                int $$68 = $$2.readVarInt();
                for (int $$69 = 0; $$69 < $$68; ++$$69) {
                    String $$70 = $$2.readUtf();
                    $$52.gossips.add((Object)$$70);
                }
                this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump($$52);
            } else if (ClientboundCustomPayloadPacket.DEBUG_BEE.equals($$1)) {
                double $$71 = $$2.readDouble();
                double $$72 = $$2.readDouble();
                double $$73 = $$2.readDouble();
                PositionImpl $$74 = new PositionImpl($$71, $$72, $$73);
                UUID $$75 = $$2.readUUID();
                int $$76 = $$2.readInt();
                BlockPos $$77 = (BlockPos)$$2.readNullable(FriendlyByteBuf::readBlockPos);
                BlockPos $$78 = (BlockPos)$$2.readNullable(FriendlyByteBuf::readBlockPos);
                int $$79 = $$2.readInt();
                Path $$80 = (Path)$$2.readNullable(Path::createFromStream);
                BeeDebugRenderer.BeeInfo $$81 = new BeeDebugRenderer.BeeInfo($$75, $$76, $$74, $$80, $$77, $$78, $$79);
                int $$82 = $$2.readVarInt();
                for (int $$83 = 0; $$83 < $$82; ++$$83) {
                    String $$84 = $$2.readUtf();
                    $$81.goals.add((Object)$$84);
                }
                int $$85 = $$2.readVarInt();
                for (int $$86 = 0; $$86 < $$85; ++$$86) {
                    BlockPos $$87 = $$2.readBlockPos();
                    $$81.blacklistedHives.add((Object)$$87);
                }
                this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo($$81);
            } else if (ClientboundCustomPayloadPacket.DEBUG_HIVE.equals($$1)) {
                BlockPos $$88 = $$2.readBlockPos();
                String $$89 = $$2.readUtf();
                int $$90 = $$2.readInt();
                int $$91 = $$2.readInt();
                boolean $$92 = $$2.readBoolean();
                BeeDebugRenderer.HiveInfo $$93 = new BeeDebugRenderer.HiveInfo($$88, $$89, $$90, $$91, $$92, this.level.getGameTime());
                this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo($$93);
            } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR.equals($$1)) {
                this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
            } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER.equals($$1)) {
                BlockPos $$94 = $$2.readBlockPos();
                int $$95 = $$2.readInt();
                String $$96 = $$2.readUtf();
                int $$97 = $$2.readInt();
                this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker($$94, $$95, $$96, $$97);
            } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT.equals($$1)) {
                GameEvent $$98 = BuiltInRegistries.GAME_EVENT.get(new ResourceLocation($$2.readUtf()));
                Vec3 $$99 = new Vec3($$2.readDouble(), $$2.readDouble(), $$2.readDouble());
                this.minecraft.debugRenderer.gameEventListenerRenderer.trackGameEvent($$98, $$99);
            } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT_LISTENER.equals($$1)) {
                ResourceLocation $$100 = $$2.readResourceLocation();
                Object $$101 = ((PositionSourceType)BuiltInRegistries.POSITION_SOURCE_TYPE.getOptional($$100).orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + $$100))).read($$2);
                int $$102 = $$2.readVarInt();
                this.minecraft.debugRenderer.gameEventListenerRenderer.trackListener((PositionSource)$$101, $$102);
            } else {
                LOGGER.warn("Unknown custom packed identifier: {}", (Object)$$1);
            }
        }
        finally {
            if ($$2 != null) {
                $$2.release();
            }
        }
    }

    @Override
    public void handleAddObjective(ClientboundSetObjectivePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Scoreboard $$1 = this.level.getScoreboard();
        String $$2 = $$0.getObjectiveName();
        if ($$0.getMethod() == 0) {
            $$1.addObjective($$2, ObjectiveCriteria.DUMMY, $$0.getDisplayName(), $$0.getRenderType());
        } else if ($$1.hasObjective($$2)) {
            Objective $$3 = $$1.getObjective($$2);
            if ($$0.getMethod() == 1) {
                $$1.removeObjective($$3);
            } else if ($$0.getMethod() == 2) {
                $$3.setRenderType($$0.getRenderType());
                $$3.setDisplayName($$0.getDisplayName());
            }
        }
    }

    @Override
    public void handleSetScore(ClientboundSetScorePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Scoreboard $$1 = this.level.getScoreboard();
        String $$2 = $$0.getObjectiveName();
        switch ($$0.getMethod()) {
            case CHANGE: {
                Objective $$3 = $$1.getOrCreateObjective($$2);
                Score $$4 = $$1.getOrCreatePlayerScore($$0.getOwner(), $$3);
                $$4.setScore($$0.getScore());
                break;
            }
            case REMOVE: {
                $$1.resetPlayerScore($$0.getOwner(), $$1.getObjective($$2));
            }
        }
    }

    @Override
    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Scoreboard $$1 = this.level.getScoreboard();
        String $$2 = $$0.getObjectiveName();
        Objective $$3 = $$2 == null ? null : $$1.getOrCreateObjective($$2);
        $$1.setDisplayObjective($$0.getSlot(), $$3);
    }

    @Override
    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket $$0) {
        PlayerTeam $$4;
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Scoreboard $$12 = this.level.getScoreboard();
        ClientboundSetPlayerTeamPacket.Action $$2 = $$0.getTeamAction();
        if ($$2 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            PlayerTeam $$3 = $$12.addPlayerTeam($$0.getName());
        } else {
            $$4 = $$12.getPlayerTeam($$0.getName());
            if ($$4 == null) {
                LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{$$0.getName(), $$0.getTeamAction(), $$0.getPlayerAction()});
                return;
            }
        }
        Optional<ClientboundSetPlayerTeamPacket.Parameters> $$5 = $$0.getParameters();
        $$5.ifPresent($$1 -> {
            Team.CollisionRule $$3;
            $$4.setDisplayName($$1.getDisplayName());
            $$4.setColor($$1.getColor());
            $$4.unpackOptions($$1.getOptions());
            Team.Visibility $$2 = Team.Visibility.byName($$1.getNametagVisibility());
            if ($$2 != null) {
                $$4.setNameTagVisibility($$2);
            }
            if (($$3 = Team.CollisionRule.byName($$1.getCollisionRule())) != null) {
                $$4.setCollisionRule($$3);
            }
            $$4.setPlayerPrefix($$1.getPlayerPrefix());
            $$4.setPlayerSuffix($$1.getPlayerSuffix());
        });
        ClientboundSetPlayerTeamPacket.Action $$6 = $$0.getPlayerAction();
        if ($$6 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            for (String $$7 : $$0.getPlayers()) {
                $$12.addPlayerToTeam($$7, $$4);
            }
        } else if ($$6 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            for (String $$8 : $$0.getPlayers()) {
                $$12.removePlayerFromTeam($$8, $$4);
            }
        }
        if ($$2 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            $$12.removePlayerTeam($$4);
        }
    }

    @Override
    public void handleParticleEvent(ClientboundLevelParticlesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        if ($$0.getCount() == 0) {
            double $$1 = $$0.getMaxSpeed() * $$0.getXDist();
            double $$2 = $$0.getMaxSpeed() * $$0.getYDist();
            double $$3 = $$0.getMaxSpeed() * $$0.getZDist();
            try {
                this.level.addParticle($$0.getParticle(), $$0.isOverrideLimiter(), $$0.getX(), $$0.getY(), $$0.getZ(), $$1, $$2, $$3);
            }
            catch (Throwable $$4) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)$$0.getParticle());
            }
        } else {
            for (int $$5 = 0; $$5 < $$0.getCount(); ++$$5) {
                double $$6 = this.random.nextGaussian() * (double)$$0.getXDist();
                double $$7 = this.random.nextGaussian() * (double)$$0.getYDist();
                double $$8 = this.random.nextGaussian() * (double)$$0.getZDist();
                double $$9 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                double $$10 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                double $$11 = this.random.nextGaussian() * (double)$$0.getMaxSpeed();
                try {
                    this.level.addParticle($$0.getParticle(), $$0.isOverrideLimiter(), $$0.getX() + $$6, $$0.getY() + $$7, $$0.getZ() + $$8, $$9, $$10, $$11);
                    continue;
                }
                catch (Throwable $$12) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)$$0.getParticle());
                    return;
                }
            }
        }
    }

    @Override
    public void handlePing(ClientboundPingPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.send(new ServerboundPongPacket($$0.getId()));
    }

    @Override
    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        Entity $$1 = this.level.getEntity($$0.getEntityId());
        if ($$1 == null) {
            return;
        }
        if (!($$1 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + $$1 + ")");
        }
        AttributeMap $$2 = ((LivingEntity)$$1).getAttributes();
        for (ClientboundUpdateAttributesPacket.AttributeSnapshot $$3 : $$0.getValues()) {
            AttributeInstance $$4 = $$2.getInstance($$3.getAttribute());
            if ($$4 == null) {
                LOGGER.warn("Entity {} does not have attribute {}", (Object)$$1, (Object)BuiltInRegistries.ATTRIBUTE.getKey($$3.getAttribute()));
                continue;
            }
            $$4.setBaseValue($$3.getBase());
            $$4.removeModifiers();
            for (AttributeModifier $$5 : $$3.getModifiers()) {
                $$4.addTransientModifier($$5);
            }
        }
    }

    @Override
    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        AbstractContainerMenu $$12 = this.minecraft.player.containerMenu;
        if ($$12.containerId != $$0.getContainerId()) {
            return;
        }
        this.recipeManager.byKey($$0.getRecipe()).ifPresent($$1 -> {
            if (this.minecraft.screen instanceof RecipeUpdateListener) {
                RecipeBookComponent $$2 = ((RecipeUpdateListener)((Object)this.minecraft.screen)).getRecipeBookComponent();
                $$2.setupGhostRecipe((Recipe<?>)$$1, (List<Slot>)$$0.slots);
            }
        });
    }

    @Override
    public void handleLightUpdatePacket(ClientboundLightUpdatePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        int $$1 = $$0.getX();
        int $$2 = $$0.getZ();
        ClientboundLightUpdatePacketData $$3 = $$0.getLightData();
        this.level.queueLightUpdate(() -> this.applyLightData($$1, $$2, $$3));
    }

    private void applyLightData(int $$0, int $$1, ClientboundLightUpdatePacketData $$2) {
        LevelLightEngine $$3 = this.level.getChunkSource().getLightEngine();
        BitSet $$4 = $$2.getSkyYMask();
        BitSet $$5 = $$2.getEmptySkyYMask();
        Iterator $$6 = $$2.getSkyUpdates().iterator();
        this.readSectionList($$0, $$1, $$3, LightLayer.SKY, $$4, $$5, (Iterator<byte[]>)$$6, $$2.getTrustEdges());
        BitSet $$7 = $$2.getBlockYMask();
        BitSet $$8 = $$2.getEmptyBlockYMask();
        Iterator $$9 = $$2.getBlockUpdates().iterator();
        this.readSectionList($$0, $$1, $$3, LightLayer.BLOCK, $$7, $$8, (Iterator<byte[]>)$$9, $$2.getTrustEdges());
        this.level.setLightReady($$0, $$1);
    }

    @Override
    public void handleMerchantOffers(ClientboundMerchantOffersPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        AbstractContainerMenu $$1 = this.minecraft.player.containerMenu;
        if ($$0.getContainerId() == $$1.containerId && $$1 instanceof MerchantMenu) {
            MerchantMenu $$2 = (MerchantMenu)$$1;
            $$2.setOffers(new MerchantOffers($$0.getOffers().createTag()));
            $$2.setXp($$0.getVillagerXp());
            $$2.setMerchantLevel($$0.getVillagerLevel());
            $$2.setShowProgressBar($$0.showProgress());
            $$2.setCanRestock($$0.canRestock());
        }
    }

    @Override
    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.serverChunkRadius = $$0.getRadius();
        this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
        this.level.getChunkSource().updateViewRadius($$0.getRadius());
    }

    @Override
    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.serverSimulationDistance = $$0.simulationDistance();
        this.level.setServerSimulationDistance(this.serverSimulationDistance);
    }

    @Override
    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.getChunkSource().updateViewCenter($$0.getX(), $$0.getZ());
    }

    @Override
    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        this.level.handleBlockChangedAck($$0.sequence());
    }

    @Override
    public void handleBundlePacket(ClientboundBundlePacket $$0) {
        PacketUtils.ensureRunningOnSameThread($$0, this, this.minecraft);
        for (Packet $$1 : $$0.subPackets()) {
            $$1.handle(this);
        }
    }

    private void readSectionList(int $$0, int $$1, LevelLightEngine $$2, LightLayer $$3, BitSet $$4, BitSet $$5, Iterator<byte[]> $$6, boolean $$7) {
        for (int $$8 = 0; $$8 < $$2.getLightSectionCount(); ++$$8) {
            int $$9 = $$2.getMinLightSection() + $$8;
            boolean $$10 = $$4.get($$8);
            boolean $$11 = $$5.get($$8);
            if (!$$10 && !$$11) continue;
            $$2.queueSectionData($$3, SectionPos.of($$0, $$9, $$1), $$10 ? new DataLayer((byte[])((byte[])$$6.next()).clone()) : new DataLayer(), $$7);
            this.level.setSectionDirtyWithNeighbors($$0, $$9, $$1);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public Collection<PlayerInfo> getListedOnlinePlayers() {
        return this.listedPlayers;
    }

    public Collection<PlayerInfo> getOnlinePlayers() {
        return this.playerInfoMap.values();
    }

    public Collection<UUID> getOnlinePlayerIds() {
        return this.playerInfoMap.keySet();
    }

    @Nullable
    public PlayerInfo getPlayerInfo(UUID $$0) {
        return (PlayerInfo)this.playerInfoMap.get((Object)$$0);
    }

    @Nullable
    public PlayerInfo getPlayerInfo(String $$0) {
        for (PlayerInfo $$1 : this.playerInfoMap.values()) {
            if (!$$1.getProfile().getName().equals((Object)$$0)) continue;
            return $$1;
        }
        return null;
    }

    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }

    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }

    public CommandDispatcher<SharedSuggestionProvider> getCommands() {
        return this.commands;
    }

    public ClientLevel getLevel() {
        return this.level;
    }

    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }

    public UUID getId() {
        return this.id;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess.compositeAccess();
    }

    public void markMessageAsProcessed(PlayerChatMessage $$0, boolean $$1) {
        MessageSignature $$2 = $$0.signature();
        if ($$2 != null && this.lastSeenMessages.addPending($$2, $$1) && this.lastSeenMessages.offset() > 64) {
            this.sendChatAcknowledgement();
        }
    }

    private void sendChatAcknowledgement() {
        int $$0 = this.lastSeenMessages.getAndClearOffset();
        if ($$0 > 0) {
            this.send(new ServerboundChatAckPacket($$0));
        }
    }

    public void sendChat(String $$0) {
        Instant $$1 = Instant.now();
        long $$2 = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update $$3 = this.lastSeenMessages.generateAndApplyUpdate();
        MessageSignature $$4 = this.signedMessageEncoder.pack(new SignedMessageBody($$0, $$1, $$2, $$3.lastSeen()));
        this.send(new ServerboundChatPacket($$0, $$1, $$2, $$4, $$3.update()));
    }

    public void sendCommand(String $$0) {
        Instant $$1 = Instant.now();
        long $$2 = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update $$32 = this.lastSeenMessages.generateAndApplyUpdate();
        ArgumentSignatures $$4 = ArgumentSignatures.signCommand(SignableCommand.of(this.parseCommand($$0)), $$3 -> {
            SignedMessageBody $$4 = new SignedMessageBody($$3, $$1, $$2, $$32.lastSeen());
            return this.signedMessageEncoder.pack($$4);
        });
        this.send(new ServerboundChatCommandPacket($$0, $$1, $$2, $$4, $$32.update()));
    }

    public boolean sendUnsignedCommand(String $$0) {
        if (SignableCommand.of(this.parseCommand($$0)).arguments().isEmpty()) {
            LastSeenMessagesTracker.Update $$1 = this.lastSeenMessages.generateAndApplyUpdate();
            this.send(new ServerboundChatCommandPacket($$0, Instant.now(), 0L, ArgumentSignatures.EMPTY, $$1.update()));
            return true;
        }
        return false;
    }

    private ParseResults<SharedSuggestionProvider> parseCommand(String $$0) {
        return this.commands.parse($$0, (Object)this.suggestionsProvider);
    }

    @Override
    public void tick() {
        ProfileKeyPairManager $$02;
        if (this.connection.isEncrypted() && ($$02 = this.minecraft.getProfileKeyPairManager()).shouldRefreshKeyPair()) {
            $$02.prepareKeyPair().thenAcceptAsync($$0 -> $$0.ifPresent(this::setKeyPair), (Executor)this.minecraft);
        }
        this.telemetryManager.tick();
    }

    public void setKeyPair(ProfileKeyPair $$0) {
        if (!this.localGameProfile.getId().equals((Object)this.minecraft.getUser().getProfileId())) {
            return;
        }
        if (this.chatSession != null && this.chatSession.keyPair().equals((Object)$$0)) {
            return;
        }
        this.chatSession = LocalChatSession.create($$0);
        this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.getId());
        this.send(new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData()));
    }

    @Nullable
    public ServerData getServerData() {
        return this.serverData;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean isFeatureEnabled(FeatureFlagSet $$0) {
        return $$0.isSubsetOf(this.enabledFeatures());
    }
}