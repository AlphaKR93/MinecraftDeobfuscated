/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.Unpooled
 *  java.io.File
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.net.SocketAddress
 *  java.nio.file.Path
 *  java.text.SimpleDateFormat
 *  java.time.Instant
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.EnumSet
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class PlayerList {
    public static final File USERBANLIST_FILE = new File("banned-players.json");
    public static final File IPBANLIST_FILE = new File("banned-ips.json");
    public static final File OPLIST_FILE = new File("ops.json");
    public static final File WHITELIST_FILE = new File("whitelist.json");
    public static final Component CHAT_FILTERED_FULL = Component.translatable("chat.filtered_full");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SEND_PLAYER_INFO_INTERVAL = 600;
    private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayer> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();
    private final UserBanList bans = new UserBanList(USERBANLIST_FILE);
    private final IpBanList ipBans = new IpBanList(IPBANLIST_FILE);
    private final ServerOpList ops = new ServerOpList(OPLIST_FILE);
    private final UserWhiteList whitelist = new UserWhiteList(WHITELIST_FILE);
    private final Map<UUID, ServerStatsCounter> stats = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
    private final PlayerDataStorage playerIo;
    private boolean doWhiteList;
    private final LayeredRegistryAccess<RegistryLayer> registries;
    private final RegistryAccess.Frozen synchronizedRegistries;
    protected final int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean allowCheatsForAllPlayers;
    private static final boolean ALLOW_LOGOUTIVATOR = false;
    private int sendAllPlayerInfoIn;

    public PlayerList(MinecraftServer $$0, LayeredRegistryAccess<RegistryLayer> $$1, PlayerDataStorage $$2, int $$3) {
        this.server = $$0;
        this.registries = $$1;
        this.synchronizedRegistries = new RegistryAccess.ImmutableRegistryAccess(RegistrySynchronization.networkedRegistries($$1)).freeze();
        this.maxPlayers = $$3;
        this.playerIo = $$2;
    }

    public void placeNewPlayer(Connection $$0, ServerPlayer $$12) {
        CompoundTag $$20;
        Entity $$21;
        MutableComponent $$18;
        ServerLevel $$10;
        GameProfile $$2 = $$12.getGameProfile();
        GameProfileCache $$3 = this.server.getProfileCache();
        Optional<GameProfile> $$4 = $$3.get($$2.getId());
        String $$5 = (String)$$4.map(GameProfile::getName).orElse((Object)$$2.getName());
        $$3.add($$2);
        CompoundTag $$6 = this.load($$12);
        ResourceKey $$7 = $$6 != null ? (ResourceKey)DimensionType.parseLegacy(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$6.get("Dimension"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse(Level.OVERWORLD) : Level.OVERWORLD;
        ServerLevel $$8 = this.server.getLevel($$7);
        if ($$8 == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", $$7);
            ServerLevel $$9 = this.server.overworld();
        } else {
            $$10 = $$8;
        }
        $$12.setLevel($$10);
        String $$11 = "local";
        if ($$0.getRemoteAddress() != null) {
            $$11 = $$0.getRemoteAddress().toString();
        }
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{$$12.getName().getString(), $$11, $$12.getId(), $$12.getX(), $$12.getY(), $$12.getZ()});
        LevelData $$122 = $$10.getLevelData();
        $$12.loadGameTypes($$6);
        ServerGamePacketListenerImpl $$13 = new ServerGamePacketListenerImpl(this.server, $$0, $$12);
        GameRules $$14 = $$10.getGameRules();
        boolean $$15 = $$14.getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN);
        boolean $$16 = $$14.getBoolean(GameRules.RULE_REDUCEDDEBUGINFO);
        $$13.send(new ClientboundLoginPacket($$12.getId(), $$122.isHardcore(), $$12.gameMode.getGameModeForPlayer(), $$12.gameMode.getPreviousGameModeForPlayer(), this.server.levelKeys(), this.synchronizedRegistries, $$10.dimensionTypeId(), $$10.dimension(), BiomeManager.obfuscateSeed($$10.getSeed()), this.getMaxPlayers(), this.viewDistance, this.simulationDistance, $$16, !$$15, $$10.isDebug(), $$10.isFlat(), $$12.getLastDeathLocation()));
        $$13.send(new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames($$10.enabledFeatures())));
        $$13.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, new FriendlyByteBuf(Unpooled.buffer()).writeUtf(this.getServer().getServerModName())));
        $$13.send(new ClientboundChangeDifficultyPacket($$122.getDifficulty(), $$122.isDifficultyLocked()));
        $$13.send(new ClientboundPlayerAbilitiesPacket($$12.getAbilities()));
        $$13.send(new ClientboundSetCarriedItemPacket($$12.getInventory().selected));
        $$13.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        $$13.send(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
        this.sendPlayerPermissionLevel($$12);
        $$12.getStats().markAllDirty();
        $$12.getRecipeBook().sendInitialRecipeBook($$12);
        this.updateEntireScoreboard($$10.getScoreboard(), $$12);
        this.server.invalidateStatus();
        if ($$12.getGameProfile().getName().equalsIgnoreCase($$5)) {
            MutableComponent $$17 = Component.translatable("multiplayer.player.joined", $$12.getDisplayName());
        } else {
            $$18 = Component.translatable("multiplayer.player.joined.renamed", $$12.getDisplayName(), $$5);
        }
        this.broadcastSystemMessage($$18.withStyle(ChatFormatting.YELLOW), false);
        $$13.teleport($$12.getX(), $$12.getY(), $$12.getZ(), $$12.getYRot(), $$12.getXRot());
        $$12.sendServerStatus(this.server.getStatus());
        $$12.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(this.players));
        this.players.add((Object)$$12);
        this.playersByUUID.put((Object)$$12.getUUID(), (Object)$$12);
        this.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing((Collection<ServerPlayer>)List.of((Object)$$12)));
        $$10.addNewPlayer($$12);
        this.server.getCustomBossEvents().onPlayerConnect($$12);
        this.sendLevelInfo($$12, $$10);
        this.server.getServerResourcePack().ifPresent($$1 -> $$12.sendTexturePack($$1.url(), $$1.hash(), $$1.isRequired(), $$1.prompt()));
        for (MobEffectInstance $$19 : $$12.getActiveEffects()) {
            $$13.send(new ClientboundUpdateMobEffectPacket($$12.getId(), $$19));
        }
        if ($$6 != null && $$6.contains("RootVehicle", 10) && ($$21 = EntityType.loadEntityRecursive(($$20 = $$6.getCompound("RootVehicle")).getCompound("Entity"), $$10, (Function<Entity, Entity>)((Function)$$1 -> {
            if (!$$10.addWithUUID((Entity)$$1)) {
                return null;
            }
            return $$1;
        }))) != null) {
            Object $$23;
            if ($$20.hasUUID("Attach")) {
                UUID $$22 = $$20.getUUID("Attach");
            } else {
                $$23 = null;
            }
            if ($$21.getUUID().equals($$23)) {
                $$12.startRiding($$21, true);
            } else {
                for (Entity $$24 : $$21.getIndirectPassengers()) {
                    if (!$$24.getUUID().equals($$23)) continue;
                    $$12.startRiding($$24, true);
                    break;
                }
            }
            if (!$$12.isPassenger()) {
                LOGGER.warn("Couldn't reattach entity to player");
                $$21.discard();
                for (Entity $$25 : $$21.getIndirectPassengers()) {
                    $$25.discard();
                }
            }
        }
        $$12.initInventoryMenu();
    }

    protected void updateEntireScoreboard(ServerScoreboard $$0, ServerPlayer $$1) {
        HashSet $$2 = Sets.newHashSet();
        for (PlayerTeam $$3 : $$0.getPlayerTeams()) {
            $$1.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket($$3, true));
        }
        for (int $$4 = 0; $$4 < 19; ++$$4) {
            Objective $$5 = $$0.getDisplayObjective($$4);
            if ($$5 == null || $$2.contains((Object)$$5)) continue;
            List<Packet<?>> $$6 = $$0.getStartTrackingPackets($$5);
            for (Packet $$7 : $$6) {
                $$1.connection.send($$7);
            }
            $$2.add((Object)$$5);
        }
    }

    public void addWorldborderListener(ServerLevel $$0) {
        $$0.getWorldBorder().addListener(new BorderChangeListener(){

            @Override
            public void onBorderSizeSet(WorldBorder $$0, double $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderSizePacket($$0));
            }

            @Override
            public void onBorderSizeLerping(WorldBorder $$0, double $$1, double $$2, long $$3) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderLerpSizePacket($$0));
            }

            @Override
            public void onBorderCenterSet(WorldBorder $$0, double $$1, double $$2) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderCenterPacket($$0));
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder $$0, int $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDelayPacket($$0));
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder $$0, int $$1) {
                PlayerList.this.broadcastAll(new ClientboundSetBorderWarningDistancePacket($$0));
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder $$0, double $$1) {
            }

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder $$0, double $$1) {
            }
        });
    }

    @Nullable
    public CompoundTag load(ServerPlayer $$0) {
        CompoundTag $$3;
        CompoundTag $$1 = this.server.getWorldData().getLoadedPlayerTag();
        if (this.server.isSingleplayerOwner($$0.getGameProfile()) && $$1 != null) {
            CompoundTag $$2 = $$1;
            $$0.load($$2);
            LOGGER.debug("loading single player");
        } else {
            $$3 = this.playerIo.load($$0);
        }
        return $$3;
    }

    protected void save(ServerPlayer $$0) {
        PlayerAdvancements $$2;
        this.playerIo.save($$0);
        ServerStatsCounter $$1 = (ServerStatsCounter)this.stats.get((Object)$$0.getUUID());
        if ($$1 != null) {
            $$1.save();
        }
        if (($$2 = (PlayerAdvancements)this.advancements.get((Object)$$0.getUUID())) != null) {
            $$2.save();
        }
    }

    public void remove(ServerPlayer $$02) {
        Entity $$2;
        ServerLevel $$1 = $$02.getLevel();
        $$02.awardStat(Stats.LEAVE_GAME);
        this.save($$02);
        if ($$02.isPassenger() && ($$2 = $$02.getRootVehicle()).hasExactlyOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            $$02.stopRiding();
            $$2.getPassengersAndSelf().forEach($$0 -> $$0.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        }
        $$02.unRide();
        $$1.removePlayerImmediately($$02, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        $$02.getAdvancements().stopListening();
        this.players.remove((Object)$$02);
        this.server.getCustomBossEvents().onPlayerDisconnect($$02);
        UUID $$3 = $$02.getUUID();
        ServerPlayer $$4 = (ServerPlayer)this.playersByUUID.get((Object)$$3);
        if ($$4 == $$02) {
            this.playersByUUID.remove((Object)$$3);
            this.stats.remove((Object)$$3);
            this.advancements.remove((Object)$$3);
        }
        this.broadcastAll(new ClientboundPlayerInfoRemovePacket((List<UUID>)List.of((Object)$$02.getUUID())));
    }

    @Nullable
    public Component canPlayerLogin(SocketAddress $$0, GameProfile $$1) {
        if (this.bans.isBanned($$1)) {
            UserBanListEntry $$2 = (UserBanListEntry)this.bans.get($$1);
            MutableComponent $$3 = Component.translatable("multiplayer.disconnect.banned.reason", $$2.getReason());
            if ($$2.getExpires() != null) {
                $$3.append(Component.translatable("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format($$2.getExpires())));
            }
            return $$3;
        }
        if (!this.isWhiteListed($$1)) {
            return Component.translatable("multiplayer.disconnect.not_whitelisted");
        }
        if (this.ipBans.isBanned($$0)) {
            IpBanListEntry $$4 = this.ipBans.get($$0);
            MutableComponent $$5 = Component.translatable("multiplayer.disconnect.banned_ip.reason", $$4.getReason());
            if ($$4.getExpires() != null) {
                $$5.append(Component.translatable("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format($$4.getExpires())));
            }
            return $$5;
        }
        if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit($$1)) {
            return Component.translatable("multiplayer.disconnect.server_full");
        }
        return null;
    }

    public ServerPlayer getPlayerForLogin(GameProfile $$0) {
        UUID $$1 = UUIDUtil.getOrCreatePlayerUUID($$0);
        ArrayList $$2 = Lists.newArrayList();
        for (int $$3 = 0; $$3 < this.players.size(); ++$$3) {
            ServerPlayer $$4 = (ServerPlayer)this.players.get($$3);
            if (!$$4.getUUID().equals((Object)$$1)) continue;
            $$2.add((Object)$$4);
        }
        ServerPlayer $$5 = (ServerPlayer)this.playersByUUID.get((Object)$$0.getId());
        if ($$5 != null && !$$2.contains((Object)$$5)) {
            $$2.add((Object)$$5);
        }
        for (ServerPlayer $$6 : $$2) {
            $$6.connection.disconnect(Component.translatable("multiplayer.disconnect.duplicate_login"));
        }
        return new ServerPlayer(this.server, this.server.overworld(), $$0);
    }

    public ServerPlayer respawn(ServerPlayer $$0, boolean $$1) {
        Optional $$7;
        this.players.remove((Object)$$0);
        $$0.getLevel().removePlayerImmediately($$0, Entity.RemovalReason.DISCARDED);
        BlockPos $$2 = $$0.getRespawnPosition();
        float $$3 = $$0.getRespawnAngle();
        boolean $$4 = $$0.isRespawnForced();
        ServerLevel $$5 = this.server.getLevel($$0.getRespawnDimension());
        if ($$5 != null && $$2 != null) {
            Optional<Vec3> $$6 = Player.findRespawnPositionAndUseSpawnBlock($$5, $$2, $$3, $$4, $$1);
        } else {
            $$7 = Optional.empty();
        }
        ServerLevel $$8 = $$5 != null && $$7.isPresent() ? $$5 : this.server.overworld();
        ServerPlayer $$9 = new ServerPlayer(this.server, $$8, $$0.getGameProfile());
        $$9.connection = $$0.connection;
        $$9.restoreFrom($$0, $$1);
        $$9.setId($$0.getId());
        $$9.setMainArm($$0.getMainArm());
        for (String $$10 : $$0.getTags()) {
            $$9.addTag($$10);
        }
        boolean $$11 = false;
        if ($$7.isPresent()) {
            float $$17;
            BlockState $$12 = $$8.getBlockState($$2);
            boolean $$13 = $$12.is(Blocks.RESPAWN_ANCHOR);
            Vec3 $$14 = (Vec3)$$7.get();
            if ($$12.is(BlockTags.BEDS) || $$13) {
                Vec3 $$15 = Vec3.atBottomCenterOf($$2).subtract($$14).normalize();
                float $$16 = (float)Mth.wrapDegrees(Mth.atan2($$15.z, $$15.x) * 57.2957763671875 - 90.0);
            } else {
                $$17 = $$3;
            }
            $$9.moveTo($$14.x, $$14.y, $$14.z, $$17, 0.0f);
            $$9.setRespawnPosition($$8.dimension(), $$2, $$3, $$4, false);
            $$11 = !$$1 && $$13;
        } else if ($$2 != null) {
            $$9.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
        }
        while (!$$8.noCollision($$9) && $$9.getY() < (double)$$8.getMaxBuildHeight()) {
            $$9.setPos($$9.getX(), $$9.getY() + 1.0, $$9.getZ());
        }
        byte $$18 = $$1 ? (byte)1 : 0;
        LevelData $$19 = $$9.level.getLevelData();
        $$9.connection.send(new ClientboundRespawnPacket($$9.level.dimensionTypeId(), $$9.level.dimension(), BiomeManager.obfuscateSeed($$9.getLevel().getSeed()), $$9.gameMode.getGameModeForPlayer(), $$9.gameMode.getPreviousGameModeForPlayer(), $$9.getLevel().isDebug(), $$9.getLevel().isFlat(), $$18, $$9.getLastDeathLocation()));
        $$9.connection.teleport($$9.getX(), $$9.getY(), $$9.getZ(), $$9.getYRot(), $$9.getXRot());
        $$9.connection.send(new ClientboundSetDefaultSpawnPositionPacket($$8.getSharedSpawnPos(), $$8.getSharedSpawnAngle()));
        $$9.connection.send(new ClientboundChangeDifficultyPacket($$19.getDifficulty(), $$19.isDifficultyLocked()));
        $$9.connection.send(new ClientboundSetExperiencePacket($$9.experienceProgress, $$9.totalExperience, $$9.experienceLevel));
        this.sendLevelInfo($$9, $$8);
        this.sendPlayerPermissionLevel($$9);
        $$8.addRespawnedPlayer($$9);
        this.players.add((Object)$$9);
        this.playersByUUID.put((Object)$$9.getUUID(), (Object)$$9);
        $$9.initInventoryMenu();
        $$9.setHealth($$9.getHealth());
        if ($$11) {
            $$9.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, $$2.getX(), $$2.getY(), $$2.getZ(), 1.0f, 1.0f, $$8.getRandom().nextLong()));
        }
        return $$9;
    }

    public void sendPlayerPermissionLevel(ServerPlayer $$0) {
        GameProfile $$1 = $$0.getGameProfile();
        int $$2 = this.server.getProfilePermissions($$1);
        this.sendPlayerPermissionLevel($$0, $$2);
    }

    public void tick() {
        if (++this.sendAllPlayerInfoIn > 600) {
            this.broadcastAll(new ClientboundPlayerInfoUpdatePacket((EnumSet<ClientboundPlayerInfoUpdatePacket.Action>)EnumSet.of((Enum)ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), (Collection<ServerPlayer>)this.players));
            this.sendAllPlayerInfoIn = 0;
        }
    }

    public void broadcastAll(Packet<?> $$0) {
        for (ServerPlayer $$1 : this.players) {
            $$1.connection.send($$0);
        }
    }

    public void broadcastAll(Packet<?> $$0, ResourceKey<Level> $$1) {
        for (ServerPlayer $$2 : this.players) {
            if ($$2.level.dimension() != $$1) continue;
            $$2.connection.send($$0);
        }
    }

    public void broadcastSystemToTeam(Player $$0, Component $$1) {
        Team $$2 = $$0.getTeam();
        if ($$2 == null) {
            return;
        }
        Collection<String> $$3 = $$2.getPlayers();
        for (String $$4 : $$3) {
            ServerPlayer $$5 = this.getPlayerByName($$4);
            if ($$5 == null || $$5 == $$0) continue;
            $$5.sendSystemMessage($$1);
        }
    }

    public void broadcastSystemToAllExceptTeam(Player $$0, Component $$1) {
        Team $$2 = $$0.getTeam();
        if ($$2 == null) {
            this.broadcastSystemMessage($$1, false);
            return;
        }
        for (int $$3 = 0; $$3 < this.players.size(); ++$$3) {
            ServerPlayer $$4 = (ServerPlayer)this.players.get($$3);
            if ($$4.getTeam() == $$2) continue;
            $$4.sendSystemMessage($$1);
        }
    }

    public String[] getPlayerNamesArray() {
        String[] $$0 = new String[this.players.size()];
        for (int $$1 = 0; $$1 < this.players.size(); ++$$1) {
            $$0[$$1] = ((ServerPlayer)this.players.get($$1)).getGameProfile().getName();
        }
        return $$0;
    }

    public UserBanList getBans() {
        return this.bans;
    }

    public IpBanList getIpBans() {
        return this.ipBans;
    }

    public void op(GameProfile $$0) {
        this.ops.add(new ServerOpListEntry($$0, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit($$0)));
        ServerPlayer $$1 = this.getPlayer($$0.getId());
        if ($$1 != null) {
            this.sendPlayerPermissionLevel($$1);
        }
    }

    public void deop(GameProfile $$0) {
        this.ops.remove($$0);
        ServerPlayer $$1 = this.getPlayer($$0.getId());
        if ($$1 != null) {
            this.sendPlayerPermissionLevel($$1);
        }
    }

    private void sendPlayerPermissionLevel(ServerPlayer $$0, int $$1) {
        if ($$0.connection != null) {
            byte $$4;
            if ($$1 <= 0) {
                int $$2 = 24;
            } else if ($$1 >= 4) {
                int $$3 = 28;
            } else {
                $$4 = (byte)(24 + $$1);
            }
            $$0.connection.send(new ClientboundEntityEventPacket($$0, $$4));
        }
        this.server.getCommands().sendCommands($$0);
    }

    public boolean isWhiteListed(GameProfile $$0) {
        return !this.doWhiteList || this.ops.contains($$0) || this.whitelist.contains($$0);
    }

    public boolean isOp(GameProfile $$0) {
        return this.ops.contains($$0) || this.server.isSingleplayerOwner($$0) && this.server.getWorldData().getAllowCommands() || this.allowCheatsForAllPlayers;
    }

    @Nullable
    public ServerPlayer getPlayerByName(String $$0) {
        for (ServerPlayer $$1 : this.players) {
            if (!$$1.getGameProfile().getName().equalsIgnoreCase($$0)) continue;
            return $$1;
        }
        return null;
    }

    public void broadcast(@Nullable Player $$0, double $$1, double $$2, double $$3, double $$4, ResourceKey<Level> $$5, Packet<?> $$6) {
        for (int $$7 = 0; $$7 < this.players.size(); ++$$7) {
            double $$11;
            double $$10;
            double $$9;
            ServerPlayer $$8 = (ServerPlayer)this.players.get($$7);
            if ($$8 == $$0 || $$8.level.dimension() != $$5 || !(($$9 = $$1 - $$8.getX()) * $$9 + ($$10 = $$2 - $$8.getY()) * $$10 + ($$11 = $$3 - $$8.getZ()) * $$11 < $$4 * $$4)) continue;
            $$8.connection.send($$6);
        }
    }

    public void saveAll() {
        for (int $$0 = 0; $$0 < this.players.size(); ++$$0) {
            this.save((ServerPlayer)this.players.get($$0));
        }
    }

    public UserWhiteList getWhiteList() {
        return this.whitelist;
    }

    public String[] getWhiteListNames() {
        return this.whitelist.getUserList();
    }

    public ServerOpList getOps() {
        return this.ops;
    }

    public String[] getOpNames() {
        return this.ops.getUserList();
    }

    public void reloadWhiteList() {
    }

    public void sendLevelInfo(ServerPlayer $$0, ServerLevel $$1) {
        WorldBorder $$2 = this.server.overworld().getWorldBorder();
        $$0.connection.send(new ClientboundInitializeBorderPacket($$2));
        $$0.connection.send(new ClientboundSetTimePacket($$1.getGameTime(), $$1.getDayTime(), $$1.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        $$0.connection.send(new ClientboundSetDefaultSpawnPositionPacket($$1.getSharedSpawnPos(), $$1.getSharedSpawnAngle()));
        if ($$1.isRaining()) {
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, $$1.getRainLevel(1.0f)));
            $$0.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, $$1.getThunderLevel(1.0f)));
        }
    }

    public void sendAllPlayerInfo(ServerPlayer $$0) {
        $$0.inventoryMenu.sendAllDataToRemote();
        $$0.resetSentInfo();
        $$0.connection.send(new ClientboundSetCarriedItemPacket($$0.getInventory().selected));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean isUsingWhitelist() {
        return this.doWhiteList;
    }

    public void setUsingWhiteList(boolean $$0) {
        this.doWhiteList = $$0;
    }

    public List<ServerPlayer> getPlayersWithAddress(String $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (ServerPlayer $$2 : this.players) {
            if (!$$2.getIpAddress().equals((Object)$$0)) continue;
            $$1.add((Object)$$2);
        }
        return $$1;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getSimulationDistance() {
        return this.simulationDistance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    @Nullable
    public CompoundTag getSingleplayerData() {
        return null;
    }

    public void setAllowCheatsForAllPlayers(boolean $$0) {
        this.allowCheatsForAllPlayers = $$0;
    }

    public void removeAll() {
        for (int $$0 = 0; $$0 < this.players.size(); ++$$0) {
            ((ServerPlayer)this.players.get((int)$$0)).connection.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void broadcastSystemMessage(Component $$0, boolean $$12) {
        this.broadcastSystemMessage($$0, (Function<ServerPlayer, Component>)((Function)$$1 -> $$0), $$12);
    }

    public void broadcastSystemMessage(Component $$0, Function<ServerPlayer, Component> $$1, boolean $$2) {
        this.server.sendSystemMessage($$0);
        for (ServerPlayer $$3 : this.players) {
            Component $$4 = (Component)$$1.apply((Object)$$3);
            if ($$4 == null) continue;
            $$3.sendSystemMessage($$4, $$2);
        }
    }

    public void broadcastChatMessage(PlayerChatMessage $$0, CommandSourceStack $$1, ChatType.Bound $$2) {
        this.broadcastChatMessage($$0, (Predicate<ServerPlayer>)((Predicate)$$1::shouldFilterMessageTo), $$1.getPlayer(), $$2);
    }

    public void broadcastChatMessage(PlayerChatMessage $$0, ServerPlayer $$1, ChatType.Bound $$2) {
        this.broadcastChatMessage($$0, (Predicate<ServerPlayer>)((Predicate)$$1::shouldFilterMessageTo), $$1, $$2);
    }

    private void broadcastChatMessage(PlayerChatMessage $$0, Predicate<ServerPlayer> $$1, @Nullable ServerPlayer $$2, ChatType.Bound $$3) {
        boolean $$4 = this.verifyChatTrusted($$0);
        this.server.logChatMessage($$0.decoratedContent(), $$3, $$4 ? null : "Not Secure");
        OutgoingChatMessage $$5 = OutgoingChatMessage.create($$0);
        boolean $$6 = false;
        for (ServerPlayer $$7 : this.players) {
            boolean $$8 = $$1.test((Object)$$7);
            $$7.sendChatMessage($$5, $$8, $$3);
            $$6 |= $$8 && $$0.isFullyFiltered();
        }
        if ($$6 && $$2 != null) {
            $$2.sendSystemMessage(CHAT_FILTERED_FULL);
        }
    }

    private boolean verifyChatTrusted(PlayerChatMessage $$0) {
        return $$0.hasSignature() && !$$0.hasExpiredServer(Instant.now());
    }

    public ServerStatsCounter getPlayerStats(Player $$0) {
        UUID $$1 = $$0.getUUID();
        ServerStatsCounter $$2 = (ServerStatsCounter)this.stats.get((Object)$$1);
        if ($$2 == null) {
            File $$5;
            Path $$6;
            File $$3 = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
            File $$4 = new File($$3, $$1 + ".json");
            if (!$$4.exists() && FileUtil.isPathNormalized($$6 = ($$5 = new File($$3, $$0.getName().getString() + ".json")).toPath()) && FileUtil.isPathPortable($$6) && $$6.startsWith($$3.getPath()) && $$5.isFile()) {
                $$5.renameTo($$4);
            }
            $$2 = new ServerStatsCounter(this.server, $$4);
            this.stats.put((Object)$$1, (Object)$$2);
        }
        return $$2;
    }

    public PlayerAdvancements getPlayerAdvancements(ServerPlayer $$0) {
        UUID $$1 = $$0.getUUID();
        PlayerAdvancements $$2 = (PlayerAdvancements)this.advancements.get((Object)$$1);
        if ($$2 == null) {
            File $$3 = this.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile();
            File $$4 = new File($$3, $$1 + ".json");
            $$2 = new PlayerAdvancements(this.server.getFixerUpper(), this, this.server.getAdvancements(), $$4, $$0);
            this.advancements.put((Object)$$1, (Object)$$2);
        }
        $$2.setPlayer($$0);
        return $$2;
    }

    public void setViewDistance(int $$0) {
        this.viewDistance = $$0;
        this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket($$0));
        for (ServerLevel $$1 : this.server.getAllLevels()) {
            if ($$1 == null) continue;
            $$1.getChunkSource().setViewDistance($$0);
        }
    }

    public void setSimulationDistance(int $$0) {
        this.simulationDistance = $$0;
        this.broadcastAll(new ClientboundSetSimulationDistancePacket($$0));
        for (ServerLevel $$1 : this.server.getAllLevels()) {
            if ($$1 == null) continue;
            $$1.getChunkSource().setSimulationDistance($$0);
        }
    }

    public List<ServerPlayer> getPlayers() {
        return this.players;
    }

    @Nullable
    public ServerPlayer getPlayer(UUID $$0) {
        return (ServerPlayer)this.playersByUUID.get((Object)$$0);
    }

    public boolean canBypassPlayerLimit(GameProfile $$0) {
        return false;
    }

    public void reloadResources() {
        for (PlayerAdvancements $$0 : this.advancements.values()) {
            $$0.reload(this.server.getAdvancements());
        }
        this.broadcastAll(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
        ClientboundUpdateRecipesPacket $$1 = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());
        for (ServerPlayer $$2 : this.players) {
            $$2.connection.send($$1);
            $$2.getRecipeBook().sendInitialRecipeBook($$2);
        }
    }

    public boolean isAllowCheatsForAllPlayers() {
        return this.allowCheatsForAllPlayers;
    }
}