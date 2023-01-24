/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.net.InetAddresses
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.Byte
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.InetAddress
 *  java.net.InetSocketAddress
 *  java.net.SocketAddress
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ServerPlayer
extends Player {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_XZ = 32;
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_Y = 10;
    public ServerGamePacketListenerImpl connection;
    public final MinecraftServer server;
    public final ServerPlayerGameMode gameMode;
    private final PlayerAdvancements advancements;
    private final ServerStatsCounter stats;
    private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
    private int lastRecordedFoodLevel = Integer.MIN_VALUE;
    private int lastRecordedAirLevel = Integer.MIN_VALUE;
    private int lastRecordedArmor = Integer.MIN_VALUE;
    private int lastRecordedLevel = Integer.MIN_VALUE;
    private int lastRecordedExperience = Integer.MIN_VALUE;
    private float lastSentHealth = -1.0E8f;
    private int lastSentFood = -99999999;
    private boolean lastFoodSaturationZero = true;
    private int lastSentExp = -99999999;
    private int spawnInvulnerableTime = 60;
    private ChatVisiblity chatVisibility = ChatVisiblity.FULL;
    private boolean canChatColor = true;
    private long lastActionTime = Util.getMillis();
    @Nullable
    private Entity camera;
    private boolean isChangingDimension;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook = new ServerRecipeBook();
    @Nullable
    private Vec3 levitationStartPos;
    private int levitationStartTime;
    private boolean disconnected;
    @Nullable
    private Vec3 startingToFallPosition;
    @Nullable
    private Vec3 enteredNetherPosition;
    @Nullable
    private Vec3 enteredLavaOnVehiclePosition;
    private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
    private ResourceKey<Level> respawnDimension = Level.OVERWORLD;
    @Nullable
    private BlockPos respawnPosition;
    private boolean respawnForced;
    private float respawnAngle;
    private final TextFilter textFilter;
    private boolean textFilteringEnabled;
    private boolean allowsListing = true;
    private WardenSpawnTracker wardenSpawnTracker = new WardenSpawnTracker(0, 0, 0);
    private final ContainerSynchronizer containerSynchronizer = new ContainerSynchronizer(){

        @Override
        public void sendInitialData(AbstractContainerMenu $$0, NonNullList<ItemStack> $$1, ItemStack $$2, int[] $$3) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetContentPacket($$0.containerId, $$0.incrementStateId(), $$1, $$2));
            for (int $$4 = 0; $$4 < $$3.length; ++$$4) {
                this.broadcastDataValue($$0, $$4, $$3[$$4]);
            }
        }

        @Override
        public void sendSlotChange(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetSlotPacket($$0.containerId, $$0.incrementStateId(), $$1, $$2));
        }

        @Override
        public void sendCarriedChange(AbstractContainerMenu $$0, ItemStack $$1) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetSlotPacket(-1, $$0.incrementStateId(), -1, $$1));
        }

        @Override
        public void sendDataChange(AbstractContainerMenu $$0, int $$1, int $$2) {
            this.broadcastDataValue($$0, $$1, $$2);
        }

        private void broadcastDataValue(AbstractContainerMenu $$0, int $$1, int $$2) {
            ServerPlayer.this.connection.send(new ClientboundContainerSetDataPacket($$0.containerId, $$1, $$2));
        }
    };
    private final ContainerListener containerListener = new ContainerListener(){

        @Override
        public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
            Slot $$3 = $$0.getSlot($$1);
            if ($$3 instanceof ResultSlot) {
                return;
            }
            if ($$3.container == ServerPlayer.this.getInventory()) {
                CriteriaTriggers.INVENTORY_CHANGED.trigger(ServerPlayer.this, ServerPlayer.this.getInventory(), $$2);
            }
        }

        @Override
        public void dataChanged(AbstractContainerMenu $$0, int $$1, int $$2) {
        }
    };
    @Nullable
    private RemoteChatSession chatSession;
    private int containerCounter;
    public int latency;
    public boolean wonGame;

    public ServerPlayer(MinecraftServer $$0, ServerLevel $$1, GameProfile $$2) {
        super($$1, $$1.getSharedSpawnPos(), $$1.getSharedSpawnAngle(), $$2);
        this.textFilter = $$0.createTextFilterForPlayer(this);
        this.gameMode = $$0.createGameModeForPlayer(this);
        this.server = $$0;
        this.stats = $$0.getPlayerList().getPlayerStats(this);
        this.advancements = $$0.getPlayerList().getPlayerAdvancements(this);
        this.maxUpStep = 1.0f;
        this.fudgeSpawnLocation($$1);
    }

    private void fudgeSpawnLocation(ServerLevel $$0) {
        BlockPos $$1 = $$0.getSharedSpawnPos();
        if ($$0.dimensionType().hasSkyLight() && $$0.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
            long $$4;
            long $$5;
            int $$2 = Math.max((int)0, (int)this.server.getSpawnRadius($$0));
            int $$3 = Mth.floor($$0.getWorldBorder().getDistanceToBorder($$1.getX(), $$1.getZ()));
            if ($$3 < $$2) {
                $$2 = $$3;
            }
            if ($$3 <= 1) {
                $$2 = 1;
            }
            int $$6 = ($$5 = ($$4 = (long)($$2 * 2 + 1)) * $$4) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)$$5;
            int $$7 = this.getCoprime($$6);
            int $$8 = RandomSource.create().nextInt($$6);
            for (int $$9 = 0; $$9 < $$6; ++$$9) {
                int $$10 = ($$8 + $$7 * $$9) % $$6;
                int $$11 = $$10 % ($$2 * 2 + 1);
                int $$12 = $$10 / ($$2 * 2 + 1);
                BlockPos $$13 = PlayerRespawnLogic.getOverworldRespawnPos($$0, $$1.getX() + $$11 - $$2, $$1.getZ() + $$12 - $$2);
                if ($$13 == null) continue;
                this.moveTo($$13, 0.0f, 0.0f);
                if (!$$0.noCollision(this)) {
                    continue;
                }
                break;
            }
        } else {
            this.moveTo($$1, 0.0f, 0.0f);
            while (!$$0.noCollision(this) && this.getY() < (double)($$0.getMaxBuildHeight() - 1)) {
                this.setPos(this.getX(), this.getY() + 1.0, this.getZ());
            }
        }
    }

    private int getCoprime(int $$0) {
        return $$0 <= 16 ? $$0 - 1 : 17;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$02) {
        super.readAdditionalSaveData($$02);
        if ($$02.contains("warden_spawn_tracker", 10)) {
            WardenSpawnTracker.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$02.get("warden_spawn_tracker"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$0 -> {
                this.wardenSpawnTracker = $$0;
            });
        }
        if ($$02.contains("enteredNetherPosition", 10)) {
            CompoundTag $$1 = $$02.getCompound("enteredNetherPosition");
            this.enteredNetherPosition = new Vec3($$1.getDouble("x"), $$1.getDouble("y"), $$1.getDouble("z"));
        }
        this.seenCredits = $$02.getBoolean("seenCredits");
        if ($$02.contains("recipeBook", 10)) {
            this.recipeBook.fromNbt($$02.getCompound("recipeBook"), this.server.getRecipeManager());
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        if ($$02.contains("SpawnX", 99) && $$02.contains("SpawnY", 99) && $$02.contains("SpawnZ", 99)) {
            this.respawnPosition = new BlockPos($$02.getInt("SpawnX"), $$02.getInt("SpawnY"), $$02.getInt("SpawnZ"));
            this.respawnForced = $$02.getBoolean("SpawnForced");
            this.respawnAngle = $$02.getFloat("SpawnAngle");
            if ($$02.contains("SpawnDimension")) {
                this.respawnDimension = (ResourceKey)Level.RESOURCE_KEY_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$02.get("SpawnDimension")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElse(Level.OVERWORLD);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        WardenSpawnTracker.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.wardenSpawnTracker).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("warden_spawn_tracker", (Tag)$$1));
        this.storeGameTypes($$0);
        $$0.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPosition != null) {
            CompoundTag $$12 = new CompoundTag();
            $$12.putDouble("x", this.enteredNetherPosition.x);
            $$12.putDouble("y", this.enteredNetherPosition.y);
            $$12.putDouble("z", this.enteredNetherPosition.z);
            $$0.put("enteredNetherPosition", $$12);
        }
        Entity $$2 = this.getRootVehicle();
        Entity $$3 = this.getVehicle();
        if ($$3 != null && $$2 != this && $$2.hasExactlyOnePlayerPassenger()) {
            CompoundTag $$4 = new CompoundTag();
            CompoundTag $$5 = new CompoundTag();
            $$2.save($$5);
            $$4.putUUID("Attach", $$3.getUUID());
            $$4.put("Entity", $$5);
            $$0.put("RootVehicle", $$4);
        }
        $$0.put("recipeBook", this.recipeBook.toNbt());
        $$0.putString("Dimension", this.level.dimension().location().toString());
        if (this.respawnPosition != null) {
            $$0.putInt("SpawnX", this.respawnPosition.getX());
            $$0.putInt("SpawnY", this.respawnPosition.getY());
            $$0.putInt("SpawnZ", this.respawnPosition.getZ());
            $$0.putBoolean("SpawnForced", this.respawnForced);
            $$0.putFloat("SpawnAngle", this.respawnAngle);
            ResourceLocation.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.respawnDimension.location()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("SpawnDimension", (Tag)$$1));
        }
    }

    public void setExperiencePoints(int $$0) {
        float $$1 = this.getXpNeededForNextLevel();
        float $$2 = ($$1 - 1.0f) / $$1;
        this.experienceProgress = Mth.clamp((float)$$0 / $$1, 0.0f, $$2);
        this.lastSentExp = -1;
    }

    public void setExperienceLevels(int $$0) {
        this.experienceLevel = $$0;
        this.lastSentExp = -1;
    }

    @Override
    public void giveExperienceLevels(int $$0) {
        super.giveExperienceLevels($$0);
        this.lastSentExp = -1;
    }

    @Override
    public void onEnchantmentPerformed(ItemStack $$0, int $$1) {
        super.onEnchantmentPerformed($$0, $$1);
        this.lastSentExp = -1;
    }

    private void initMenu(AbstractContainerMenu $$0) {
        $$0.addSlotListener(this.containerListener);
        $$0.setSynchronizer(this.containerSynchronizer);
    }

    public void initInventoryMenu() {
        this.initMenu(this.inventoryMenu);
    }

    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        this.connection.send(new ClientboundPlayerCombatEnterPacket());
    }

    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        this.connection.send(new ClientboundPlayerCombatEndPacket(this.getCombatTracker()));
    }

    @Override
    protected void onInsideBlock(BlockState $$0) {
        CriteriaTriggers.ENTER_BLOCK.trigger(this, $$0);
    }

    @Override
    protected ItemCooldowns createItemCooldowns() {
        return new ServerItemCooldowns(this);
    }

    @Override
    public void tick() {
        Entity $$0;
        this.gameMode.tick();
        this.wardenSpawnTracker.tick();
        --this.spawnInvulnerableTime;
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        this.containerMenu.broadcastChanges();
        if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        if (($$0 = this.getCamera()) != this) {
            if ($$0.isAlive()) {
                this.absMoveTo($$0.getX(), $$0.getY(), $$0.getZ(), $$0.getYRot(), $$0.getXRot());
                this.getLevel().getChunkSource().move(this);
                if (this.wantsToStopRiding()) {
                    this.setCamera(this);
                }
            } else {
                this.setCamera(this);
            }
        }
        CriteriaTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }
        this.trackStartFallingPosition();
        this.trackEnteredOrExitedLavaOnVehicle();
        this.advancements.flushDirty(this);
    }

    public void doTick() {
        try {
            if (!this.isSpectator() || !this.touchingUnloadedChunk()) {
                super.tick();
            }
            for (int $$0 = 0; $$0 < this.getInventory().getContainerSize(); ++$$0) {
                Packet<?> $$2;
                ItemStack $$1 = this.getInventory().getItem($$0);
                if (!$$1.getItem().isComplex() || ($$2 = ((ComplexItem)$$1.getItem()).getUpdatePacket($$1, this.level, this)) == null) continue;
                this.connection.send($$2);
            }
            if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0f != this.lastFoodSaturationZero) {
                this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.lastSentHealth = this.getHealth();
                this.lastSentFood = this.foodData.getFoodLevel();
                boolean bl = this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
                this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
                this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
            }
            if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
                this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
                this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil(this.lastRecordedFoodLevel));
            }
            if (this.getAirSupply() != this.lastRecordedAirLevel) {
                this.lastRecordedAirLevel = this.getAirSupply();
                this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil(this.lastRecordedAirLevel));
            }
            if (this.getArmorValue() != this.lastRecordedArmor) {
                this.lastRecordedArmor = this.getArmorValue();
                this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil(this.lastRecordedArmor));
            }
            if (this.totalExperience != this.lastRecordedExperience) {
                this.lastRecordedExperience = this.totalExperience;
                this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil(this.lastRecordedExperience));
            }
            if (this.experienceLevel != this.lastRecordedLevel) {
                this.lastRecordedLevel = this.experienceLevel;
                this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil(this.lastRecordedLevel));
            }
            if (this.totalExperience != this.lastSentExp) {
                this.lastSentExp = this.totalExperience;
                this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.tickCount % 20 == 0) {
                CriteriaTriggers.LOCATION.trigger(this);
            }
        }
        catch (Throwable $$3) {
            CrashReport $$4 = CrashReport.forThrowable($$3, "Ticking player");
            CrashReportCategory $$5 = $$4.addCategory("Player being ticked");
            this.fillCrashReportCategory($$5);
            throw new ReportedException($$4);
        }
    }

    @Override
    public void resetFallDistance() {
        if (this.getHealth() > 0.0f && this.startingToFallPosition != null) {
            CriteriaTriggers.FALL_FROM_HEIGHT.trigger(this, this.startingToFallPosition);
        }
        this.startingToFallPosition = null;
        super.resetFallDistance();
    }

    public void trackStartFallingPosition() {
        if (this.fallDistance > 0.0f && this.startingToFallPosition == null) {
            this.startingToFallPosition = this.position();
        }
    }

    public void trackEnteredOrExitedLavaOnVehicle() {
        if (this.getVehicle() != null && this.getVehicle().isInLava()) {
            if (this.enteredLavaOnVehiclePosition == null) {
                this.enteredLavaOnVehiclePosition = this.position();
            } else {
                CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.trigger(this, this.enteredLavaOnVehiclePosition);
            }
        }
        if (!(this.enteredLavaOnVehiclePosition == null || this.getVehicle() != null && this.getVehicle().isInLava())) {
            this.enteredLavaOnVehiclePosition = null;
        }
    }

    private void updateScoreForCriteria(ObjectiveCriteria $$0, int $$12) {
        this.getScoreboard().forAllObjectives($$0, this.getScoreboardName(), (Consumer<Score>)((Consumer)$$1 -> $$1.setScore($$12)));
    }

    @Override
    public void die(DamageSource $$0) {
        this.gameEvent(GameEvent.ENTITY_DIE);
        boolean $$1 = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if ($$1) {
            Component $$2 = this.getCombatTracker().getDeathMessage();
            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), $$2), PacketSendListener.exceptionallySend(() -> {
                int $$12 = 256;
                String $$2 = $$2.getString(256);
                MutableComponent $$3 = Component.translatable("death.attack.message_too_long", Component.literal($$2).withStyle(ChatFormatting.YELLOW));
                MutableComponent $$4 = Component.translatable("death.attack.even_more_magic", this.getDisplayName()).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, $$3))));
                return new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), $$4);
            }));
            Team $$3 = this.getTeam();
            if ($$3 == null || $$3.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                this.server.getPlayerList().broadcastSystemMessage($$2, false);
            } else if ($$3.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerList().broadcastSystemToTeam(this, $$2);
            } else if ($$3.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerList().broadcastSystemToAllExceptTeam(this, $$2);
            }
        } else {
            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), CommonComponents.EMPTY));
        }
        this.removeEntitiesOnShoulder();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            this.tellNeutralMobsThatIDied();
        }
        if (!this.isSpectator()) {
            this.dropAllDeathLoot($$0);
        }
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this.getScoreboardName(), (Consumer<Score>)((Consumer)Score::increment));
        LivingEntity $$4 = this.getKillCredit();
        if ($$4 != null) {
            this.awardStat(Stats.ENTITY_KILLED_BY.get($$4.getType()));
            $$4.awardKillScore(this, this.deathScore, $$0);
            this.createWitherRose($$4);
        }
        this.level.broadcastEntityEvent(this, (byte)3);
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setTicksFrozen(0);
        this.setSharedFlagOnFire(false);
        this.getCombatTracker().recheckStatus();
        this.setLastDeathLocation((Optional<GlobalPos>)Optional.of((Object)GlobalPos.of(this.level.dimension(), this.blockPosition())));
    }

    private void tellNeutralMobsThatIDied() {
        AABB $$02 = new AABB(this.blockPosition()).inflate(32.0, 10.0, 32.0);
        this.level.getEntitiesOfClass(Mob.class, $$02, EntitySelector.NO_SPECTATORS).stream().filter($$0 -> $$0 instanceof NeutralMob).forEach($$0 -> ((NeutralMob)((Object)$$0)).playerDied(this));
    }

    @Override
    public void awardKillScore(Entity $$0, int $$1, DamageSource $$2) {
        if ($$0 == this) {
            return;
        }
        super.awardKillScore($$0, $$1, $$2);
        this.increaseScore($$1);
        String $$3 = this.getScoreboardName();
        String $$4 = $$0.getScoreboardName();
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, $$3, (Consumer<Score>)((Consumer)Score::increment));
        if ($$0 instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, $$3, (Consumer<Score>)((Consumer)Score::increment));
        } else {
            this.awardStat(Stats.MOB_KILLS);
        }
        this.handleTeamKill($$3, $$4, ObjectiveCriteria.TEAM_KILL);
        this.handleTeamKill($$4, $$3, ObjectiveCriteria.KILLED_BY_TEAM);
        CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, $$0, $$2);
    }

    private void handleTeamKill(String $$0, String $$1, ObjectiveCriteria[] $$2) {
        int $$4;
        PlayerTeam $$3 = this.getScoreboard().getPlayersTeam($$1);
        if ($$3 != null && ($$4 = $$3.getColor().getId()) >= 0 && $$4 < $$2.length) {
            this.getScoreboard().forAllObjectives($$2[$$4], $$0, (Consumer<Score>)((Consumer)Score::increment));
        }
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        boolean $$2;
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        boolean bl = $$2 = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals((Object)$$0.msgId);
        if (!$$2 && this.spawnInvulnerableTime > 0 && $$0 != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if ($$0 instanceof EntityDamageSource) {
            AbstractArrow $$4;
            Entity $$5;
            Entity $$3 = $$0.getEntity();
            if ($$3 instanceof Player && !this.canHarmPlayer((Player)$$3)) {
                return false;
            }
            if ($$3 instanceof AbstractArrow && ($$5 = ($$4 = (AbstractArrow)$$3).getOwner()) instanceof Player && !this.canHarmPlayer((Player)$$5)) {
                return false;
            }
        }
        return super.hurt($$0, $$1);
    }

    @Override
    public boolean canHarmPlayer(Player $$0) {
        if (!this.isPvpAllowed()) {
            return false;
        }
        return super.canHarmPlayer($$0);
    }

    private boolean isPvpAllowed() {
        return this.server.isPvpAllowed();
    }

    @Override
    @Nullable
    protected PortalInfo findDimensionEntryPoint(ServerLevel $$0) {
        PortalInfo $$1 = super.findDimensionEntryPoint($$0);
        if ($$1 != null && this.level.dimension() == Level.OVERWORLD && $$0.dimension() == Level.END) {
            Vec3 $$2 = $$1.pos.add(0.0, -1.0, 0.0);
            return new PortalInfo($$2, Vec3.ZERO, 90.0f, 0.0f);
        }
        return $$1;
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerLevel $$0) {
        this.isChangingDimension = true;
        ServerLevel $$1 = this.getLevel();
        ResourceKey<Level> $$2 = $$1.dimension();
        if ($$2 == Level.END && $$0.dimension() == Level.OVERWORLD) {
            this.unRide();
            this.getLevel().removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
            if (!this.wonGame) {
                this.wonGame = true;
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        LevelData $$3 = $$0.getLevelData();
        this.connection.send(new ClientboundRespawnPacket($$0.dimensionTypeId(), $$0.dimension(), BiomeManager.obfuscateSeed($$0.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), $$0.isDebug(), $$0.isFlat(), 3, this.getLastDeathLocation()));
        this.connection.send(new ClientboundChangeDifficultyPacket($$3.getDifficulty(), $$3.isDifficultyLocked()));
        PlayerList $$4 = this.server.getPlayerList();
        $$4.sendPlayerPermissionLevel(this);
        $$1.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
        this.unsetRemoved();
        PortalInfo $$5 = this.findDimensionEntryPoint($$0);
        if ($$5 != null) {
            $$1.getProfiler().push("moving");
            if ($$2 == Level.OVERWORLD && $$0.dimension() == Level.NETHER) {
                this.enteredNetherPosition = this.position();
            } else if ($$0.dimension() == Level.END) {
                this.createEndPlatform($$0, new BlockPos($$5.pos));
            }
            $$1.getProfiler().pop();
            $$1.getProfiler().push("placing");
            this.setLevel($$0);
            this.connection.teleport($$5.pos.x, $$5.pos.y, $$5.pos.z, $$5.yRot, $$5.xRot);
            this.connection.resetPosition();
            $$0.addDuringPortalTeleport(this);
            $$1.getProfiler().pop();
            this.triggerDimensionChangeTriggers($$1);
            this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
            $$4.sendLevelInfo(this, $$0);
            $$4.sendAllPlayerInfo(this);
            for (MobEffectInstance $$6 : this.getActiveEffects()) {
                this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), $$6));
            }
            this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastSentHealth = -1.0f;
            this.lastSentFood = -1;
        }
        return this;
    }

    private void createEndPlatform(ServerLevel $$0, BlockPos $$1) {
        BlockPos.MutableBlockPos $$2 = $$1.mutable();
        for (int $$3 = -2; $$3 <= 2; ++$$3) {
            for (int $$4 = -2; $$4 <= 2; ++$$4) {
                for (int $$5 = -1; $$5 < 3; ++$$5) {
                    BlockState $$6 = $$5 == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    $$0.setBlockAndUpdate($$2.set($$1).move($$4, $$5, $$3), $$6);
                }
            }
        }
    }

    @Override
    protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel $$0, BlockPos $$1, boolean $$2, WorldBorder $$3) {
        Optional<BlockUtil.FoundRectangle> $$4 = super.getExitPortal($$0, $$1, $$2, $$3);
        if ($$4.isPresent()) {
            return $$4;
        }
        Direction.Axis $$5 = (Direction.Axis)this.level.getBlockState(this.portalEntrancePos).getOptionalValue(NetherPortalBlock.AXIS).orElse((Object)Direction.Axis.X);
        Optional<BlockUtil.FoundRectangle> $$6 = $$0.getPortalForcer().createPortal($$1, $$5);
        if (!$$6.isPresent()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
        }
        return $$6;
    }

    private void triggerDimensionChangeTriggers(ServerLevel $$0) {
        ResourceKey<Level> $$1 = $$0.dimension();
        ResourceKey<Level> $$2 = this.level.dimension();
        CriteriaTriggers.CHANGED_DIMENSION.trigger(this, $$1, $$2);
        if ($$1 == Level.NETHER && $$2 == Level.OVERWORLD && this.enteredNetherPosition != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
        }
        if ($$2 != Level.NETHER) {
            this.enteredNetherPosition = null;
        }
    }

    @Override
    public boolean broadcastToPlayer(ServerPlayer $$0) {
        if ($$0.isSpectator()) {
            return this.getCamera() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.broadcastToPlayer($$0);
    }

    @Override
    public void take(Entity $$0, int $$1) {
        super.take($$0, $$1);
        this.containerMenu.broadcastChanges();
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos $$02) {
        Direction $$1 = this.level.getBlockState($$02).getValue(HorizontalDirectionalBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OTHER_PROBLEM));
        }
        if (!this.level.dimensionType().natural()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_HERE));
        }
        if (!this.bedInRange($$02, $$1)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.TOO_FAR_AWAY));
        }
        if (this.bedBlocked($$02, $$1)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OBSTRUCTED));
        }
        this.setRespawnPosition(this.level.dimension(), $$02, this.getYRot(), false, true);
        if (this.level.isDay()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_NOW));
        }
        if (!this.isCreative()) {
            double $$2 = 8.0;
            double $$3 = 5.0;
            Vec3 $$4 = Vec3.atBottomCenterOf($$02);
            List $$5 = this.level.getEntitiesOfClass(Monster.class, new AABB($$4.x() - 8.0, $$4.y() - 5.0, $$4.z() - 8.0, $$4.x() + 8.0, $$4.y() + 5.0, $$4.z() + 8.0), $$0 -> $$0.isPreventingPlayerRest(this));
            if (!$$5.isEmpty()) {
                return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_SAFE));
            }
        }
        Either $$6 = super.startSleepInBed($$02).ifRight($$0 -> {
            this.awardStat(Stats.SLEEP_IN_BED);
            CriteriaTriggers.SLEPT_IN_BED.trigger(this);
        });
        if (!this.getLevel().canSleepThroughNights()) {
            this.displayClientMessage(Component.translatable("sleep.not_possible"), true);
        }
        ((ServerLevel)this.level).updateSleepingPlayerList();
        return $$6;
    }

    @Override
    public void startSleeping(BlockPos $$0) {
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        super.startSleeping($$0);
    }

    private boolean bedInRange(BlockPos $$0, Direction $$1) {
        return this.isReachableBedBlock($$0) || this.isReachableBedBlock((BlockPos)$$0.relative($$1.getOpposite()));
    }

    private boolean isReachableBedBlock(BlockPos $$0) {
        Vec3 $$1 = Vec3.atBottomCenterOf($$0);
        return Math.abs((double)(this.getX() - $$1.x())) <= 3.0 && Math.abs((double)(this.getY() - $$1.y())) <= 2.0 && Math.abs((double)(this.getZ() - $$1.z())) <= 3.0;
    }

    private boolean bedBlocked(BlockPos $$0, Direction $$1) {
        Vec3i $$2 = $$0.above();
        return !this.freeAt((BlockPos)$$2) || !this.freeAt((BlockPos)((BlockPos)$$2).relative($$1.getOpposite()));
    }

    @Override
    public void stopSleepInBed(boolean $$0, boolean $$1) {
        if (this.isSleeping()) {
            this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
        }
        super.stopSleepInBed($$0, $$1);
        if (this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    @Override
    public boolean startRiding(Entity $$0, boolean $$1) {
        Entity $$2 = this.getVehicle();
        if (!super.startRiding($$0, $$1)) {
            return false;
        }
        Entity $$3 = this.getVehicle();
        if ($$3 != $$2 && this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
        return true;
    }

    @Override
    public void stopRiding() {
        Entity $$0 = this.getVehicle();
        super.stopRiding();
        Entity $$1 = this.getVehicle();
        if ($$1 != $$0 && this.connection != null) {
            this.connection.dismount(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    @Override
    public void dismountTo(double $$0, double $$1, double $$2) {
        this.removeVehicle();
        if (this.connection != null) {
            this.connection.dismount($$0, $$1, $$2, this.getYRot(), this.getXRot());
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource $$0) {
        return super.isInvulnerableTo($$0) || this.isChangingDimension() || this.getAbilities().invulnerable && $$0 == DamageSource.WITHER;
    }

    @Override
    protected void checkFallDamage(double $$0, boolean $$1, BlockState $$2, BlockPos $$3) {
    }

    @Override
    protected void onChangedBlock(BlockPos $$0) {
        if (!this.isSpectator()) {
            super.onChangedBlock($$0);
        }
    }

    public void doCheckFallDamage(double $$0, boolean $$1) {
        if (this.touchingUnloadedChunk()) {
            return;
        }
        BlockPos $$2 = this.getOnPosLegacy();
        super.checkFallDamage($$0, $$1, this.level.getBlockState($$2), $$2);
    }

    @Override
    public void openTextEdit(SignBlockEntity $$0) {
        $$0.setAllowedPlayerEditor(this.getUUID());
        this.connection.send(new ClientboundBlockUpdatePacket(this.level, $$0.getBlockPos()));
        this.connection.send(new ClientboundOpenSignEditorPacket($$0.getBlockPos()));
    }

    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    @Override
    public OptionalInt openMenu(@Nullable MenuProvider $$0) {
        if ($$0 == null) {
            return OptionalInt.empty();
        }
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        AbstractContainerMenu $$1 = $$0.createMenu(this.containerCounter, this.getInventory(), this);
        if ($$1 == null) {
            if (this.isSpectator()) {
                this.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.connection.send(new ClientboundOpenScreenPacket($$1.containerId, $$1.getType(), $$0.getDisplayName()));
        this.initMenu($$1);
        this.containerMenu = $$1;
        return OptionalInt.of((int)this.containerCounter);
    }

    @Override
    public void sendMerchantOffers(int $$0, MerchantOffers $$1, int $$2, int $$3, boolean $$4, boolean $$5) {
        this.connection.send(new ClientboundMerchantOffersPacket($$0, $$1, $$2, $$3, $$4, $$5));
    }

    @Override
    public void openHorseInventory(AbstractHorse $$0, Container $$1) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, $$1.getContainerSize(), $$0.getId()));
        this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.getInventory(), $$1, $$0);
        this.initMenu(this.containerMenu);
    }

    @Override
    public void openItemGui(ItemStack $$0, InteractionHand $$1) {
        if ($$0.is(Items.WRITTEN_BOOK)) {
            if (WrittenBookItem.resolveBookComponents($$0, this.createCommandSourceStack(), this)) {
                this.containerMenu.broadcastChanges();
            }
            this.connection.send(new ClientboundOpenBookPacket($$1));
        }
    }

    @Override
    public void openCommandBlock(CommandBlockEntity $$0) {
        this.connection.send(ClientboundBlockEntityDataPacket.create($$0, (Function<BlockEntity, CompoundTag>)((Function)BlockEntity::saveWithoutMetadata)));
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
        this.doCloseContainer();
    }

    @Override
    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.inventoryMenu.transferState(this.containerMenu);
        this.containerMenu = this.inventoryMenu;
    }

    public void setPlayerInput(float $$0, float $$1, boolean $$2, boolean $$3) {
        if (this.isPassenger()) {
            if ($$0 >= -1.0f && $$0 <= 1.0f) {
                this.xxa = $$0;
            }
            if ($$1 >= -1.0f && $$1 <= 1.0f) {
                this.zza = $$1;
            }
            this.jumping = $$2;
            this.setShiftKeyDown($$3);
        }
    }

    @Override
    public void awardStat(Stat<?> $$0, int $$12) {
        this.stats.increment(this, $$0, $$12);
        this.getScoreboard().forAllObjectives($$0, this.getScoreboardName(), (Consumer<Score>)((Consumer)$$1 -> $$1.add($$12)));
    }

    @Override
    public void resetStat(Stat<?> $$0) {
        this.stats.setValue(this, $$0, 0);
        this.getScoreboard().forAllObjectives($$0, this.getScoreboardName(), (Consumer<Score>)((Consumer)Score::reset));
    }

    @Override
    public int awardRecipes(Collection<Recipe<?>> $$0) {
        return this.recipeBook.addRecipes($$0, this);
    }

    @Override
    public void awardRecipesByKey(ResourceLocation[] $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (ResourceLocation $$2 : $$0) {
            this.server.getRecipeManager().byKey($$2).ifPresent(arg_0 -> ((List)$$1).add(arg_0));
        }
        this.awardRecipes((Collection<Recipe<?>>)$$1);
    }

    @Override
    public int resetRecipes(Collection<Recipe<?>> $$0) {
        return this.recipeBook.removeRecipes($$0, this);
    }

    @Override
    public void giveExperiencePoints(int $$0) {
        super.giveExperiencePoints($$0);
        this.lastSentExp = -1;
    }

    public void disconnect() {
        this.disconnected = true;
        this.ejectPassengers();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, false);
        }
    }

    public boolean hasDisconnected() {
        return this.disconnected;
    }

    public void resetSentInfo() {
        this.lastSentHealth = -1.0E8f;
    }

    @Override
    public void displayClientMessage(Component $$0, boolean $$1) {
        this.sendSystemMessage($$0, $$1);
    }

    @Override
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.connection.send(new ClientboundEntityEventPacket(this, 9));
            super.completeUsingItem();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor $$0, Vec3 $$1) {
        super.lookAt($$0, $$1);
        this.connection.send(new ClientboundPlayerLookAtPacket($$0, $$1.x, $$1.y, $$1.z));
    }

    public void lookAt(EntityAnchorArgument.Anchor $$0, Entity $$1, EntityAnchorArgument.Anchor $$2) {
        Vec3 $$3 = $$2.apply($$1);
        super.lookAt($$0, $$3);
        this.connection.send(new ClientboundPlayerLookAtPacket($$0, $$1, $$2));
    }

    public void restoreFrom(ServerPlayer $$0, boolean $$1) {
        this.wardenSpawnTracker = $$0.wardenSpawnTracker;
        this.textFilteringEnabled = $$0.textFilteringEnabled;
        this.chatSession = $$0.chatSession;
        this.gameMode.setGameModeForPlayer($$0.gameMode.getGameModeForPlayer(), $$0.gameMode.getPreviousGameModeForPlayer());
        this.onUpdateAbilities();
        if ($$1) {
            this.getInventory().replaceWith($$0.getInventory());
            this.setHealth($$0.getHealth());
            this.foodData = $$0.foodData;
            this.experienceLevel = $$0.experienceLevel;
            this.totalExperience = $$0.totalExperience;
            this.experienceProgress = $$0.experienceProgress;
            this.setScore($$0.getScore());
            this.portalEntrancePos = $$0.portalEntrancePos;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || $$0.isSpectator()) {
            this.getInventory().replaceWith($$0.getInventory());
            this.experienceLevel = $$0.experienceLevel;
            this.totalExperience = $$0.totalExperience;
            this.experienceProgress = $$0.experienceProgress;
            this.setScore($$0.getScore());
        }
        this.enchantmentSeed = $$0.enchantmentSeed;
        this.enderChestInventory = $$0.enderChestInventory;
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (Byte)$$0.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        this.recipeBook.copyOverData($$0.recipeBook);
        this.seenCredits = $$0.seenCredits;
        this.enteredNetherPosition = $$0.enteredNetherPosition;
        this.setShoulderEntityLeft($$0.getShoulderEntityLeft());
        this.setShoulderEntityRight($$0.getShoulderEntityRight());
        this.setLastDeathLocation($$0.getLastDeathLocation());
    }

    @Override
    protected void onEffectAdded(MobEffectInstance $$0, @Nullable Entity $$1) {
        super.onEffectAdded($$0, $$1);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), $$0));
        if ($$0.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartTime = this.tickCount;
            this.levitationStartPos = this.position();
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, $$1);
    }

    @Override
    protected void onEffectUpdated(MobEffectInstance $$0, boolean $$1, @Nullable Entity $$2) {
        super.onEffectUpdated($$0, $$1, $$2);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), $$0));
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, $$2);
    }

    @Override
    protected void onEffectRemoved(MobEffectInstance $$0) {
        super.onEffectRemoved($$0);
        this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), $$0.getEffect()));
        if ($$0.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartPos = null;
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this, (Entity)null);
    }

    @Override
    public void teleportTo(double $$0, double $$1, double $$2) {
        this.connection.teleport($$0, $$1, $$2, this.getYRot(), this.getXRot(), RelativeMovement.ROTATION);
    }

    @Override
    public void teleportRelative(double $$0, double $$1, double $$2) {
        this.connection.teleport(this.getX() + $$0, this.getY() + $$1, this.getZ() + $$2, this.getYRot(), this.getXRot(), RelativeMovement.ALL);
    }

    @Override
    public boolean teleportTo(ServerLevel $$0, double $$1, double $$2, double $$3, Set<RelativeMovement> $$4, float $$5, float $$6) {
        ChunkPos $$7 = new ChunkPos(new BlockPos($$1, $$2, $$3));
        $$0.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, $$7, 1, this.getId());
        this.stopRiding();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, true);
        }
        if ($$0 == this.level) {
            this.connection.teleport($$1, $$2, $$3, $$5, $$6, $$4);
        } else {
            this.teleportTo($$0, $$1, $$2, $$3, $$5, $$6);
        }
        this.setYHeadRot($$5);
        return true;
    }

    @Override
    public void moveTo(double $$0, double $$1, double $$2) {
        this.teleportTo($$0, $$1, $$2);
        this.connection.resetPosition();
    }

    @Override
    public void crit(Entity $$0) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket($$0, 4));
    }

    @Override
    public void magicCrit(Entity $$0) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket($$0, 5));
    }

    @Override
    public void onUpdateAbilities() {
        if (this.connection == null) {
            return;
        }
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
        this.updateInvisibilityStatus();
    }

    @Override
    public ServerLevel getLevel() {
        return (ServerLevel)this.level;
    }

    public boolean setGameMode(GameType $$0) {
        if (!this.gameMode.changeGameModeForPlayer($$0)) {
            return false;
        }
        this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, $$0.getId()));
        if ($$0 == GameType.SPECTATOR) {
            this.removeEntitiesOnShoulder();
            this.stopRiding();
        } else {
            this.setCamera(this);
        }
        this.onUpdateAbilities();
        this.updateEffectVisibility();
        return true;
    }

    @Override
    public boolean isSpectator() {
        return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        this.sendSystemMessage($$0, false);
    }

    public void sendSystemMessage(Component $$0, boolean $$1) {
        if (!this.acceptsSystemMessages($$1)) {
            return;
        }
        this.connection.send(new ClientboundSystemChatPacket($$0, $$1), PacketSendListener.exceptionallySend(() -> {
            if (this.acceptsSystemMessages(false)) {
                int $$1 = 256;
                String $$2 = $$0.getString(256);
                MutableComponent $$3 = Component.literal($$2).withStyle(ChatFormatting.YELLOW);
                return new ClientboundSystemChatPacket(Component.translatable("multiplayer.message_not_delivered", $$3).withStyle(ChatFormatting.RED), false);
            }
            return null;
        }));
    }

    public void sendChatMessage(OutgoingChatMessage $$0, boolean $$1, ChatType.Bound $$2) {
        if (this.acceptsChatMessages()) {
            $$0.sendToPlayer(this, $$1, $$2);
        }
    }

    public String getIpAddress() {
        SocketAddress $$0 = this.connection.getRemoteAddress();
        if ($$0 instanceof InetSocketAddress) {
            InetSocketAddress $$1 = (InetSocketAddress)$$0;
            return InetAddresses.toAddrString((InetAddress)$$1.getAddress());
        }
        return "<unknown>";
    }

    public void updateOptions(ServerboundClientInformationPacket $$0) {
        this.chatVisibility = $$0.chatVisibility();
        this.canChatColor = $$0.chatColors();
        this.textFilteringEnabled = $$0.textFilteringEnabled();
        this.allowsListing = $$0.allowsListing();
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)$$0.modelCustomisation());
        this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)($$0.mainHand() != HumanoidArm.LEFT ? 1 : 0));
    }

    public boolean canChatInColor() {
        return this.canChatColor;
    }

    public ChatVisiblity getChatVisibility() {
        return this.chatVisibility;
    }

    private boolean acceptsSystemMessages(boolean $$0) {
        if (this.chatVisibility == ChatVisiblity.HIDDEN) {
            return $$0;
        }
        return true;
    }

    private boolean acceptsChatMessages() {
        return this.chatVisibility == ChatVisiblity.FULL;
    }

    public void sendTexturePack(String $$0, String $$1, boolean $$2, @Nullable Component $$3) {
        this.connection.send(new ClientboundResourcePackPacket($$0, $$1, $$2, $$3));
    }

    public void sendServerStatus(ServerStatus $$0) {
        this.connection.send(new ClientboundServerDataPacket($$0.getDescription(), $$0.getFavicon(), $$0.enforcesSecureChat()));
    }

    @Override
    protected int getPermissionLevel() {
        return this.server.getProfilePermissions(this.getGameProfile());
    }

    public void resetLastActionTime() {
        this.lastActionTime = Util.getMillis();
    }

    public ServerStatsCounter getStats() {
        return this.stats;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    @Override
    protected void updateInvisibilityStatus() {
        if (this.isSpectator()) {
            this.removeEffectParticles();
            this.setInvisible(true);
        } else {
            super.updateInvisibilityStatus();
        }
    }

    public Entity getCamera() {
        return this.camera == null ? this : this.camera;
    }

    public void setCamera(@Nullable Entity $$0) {
        Entity $$1 = this.getCamera();
        Entity entity = this.camera = $$0 == null ? this : $$0;
        if ($$1 != this.camera) {
            Level level = this.camera.getLevel();
            if (level instanceof ServerLevel) {
                ServerLevel $$2 = (ServerLevel)level;
                this.teleportTo($$2, this.camera.getX(), this.camera.getY(), this.camera.getZ(), (Set<RelativeMovement>)Set.of(), this.getYRot(), this.getXRot());
            }
            if ($$0 != null) {
                this.getLevel().getChunkSource().move(this);
            }
            this.connection.send(new ClientboundSetCameraPacket(this.camera));
            this.connection.resetPosition();
        }
    }

    @Override
    protected void processPortalCooldown() {
        if (!this.isChangingDimension) {
            super.processPortalCooldown();
        }
    }

    @Override
    public void attack(Entity $$0) {
        if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            this.setCamera($$0);
        } else {
            super.attack($$0);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Component getTabListDisplayName() {
        return null;
    }

    @Override
    public void swing(InteractionHand $$0) {
        super.swing($$0);
        this.resetAttackStrengthTicker();
    }

    public boolean isChangingDimension() {
        return this.isChangingDimension;
    }

    public void hasChangedDimension() {
        this.isChangingDimension = false;
    }

    public PlayerAdvancements getAdvancements() {
        return this.advancements;
    }

    public void teleportTo(ServerLevel $$0, double $$1, double $$2, double $$3, float $$4, float $$5) {
        this.setCamera(this);
        this.stopRiding();
        if ($$0 == this.level) {
            this.connection.teleport($$1, $$2, $$3, $$4, $$5);
        } else {
            ServerLevel $$6 = this.getLevel();
            LevelData $$7 = $$0.getLevelData();
            this.connection.send(new ClientboundRespawnPacket($$0.dimensionTypeId(), $$0.dimension(), BiomeManager.obfuscateSeed($$0.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), $$0.isDebug(), $$0.isFlat(), 3, this.getLastDeathLocation()));
            this.connection.send(new ClientboundChangeDifficultyPacket($$7.getDifficulty(), $$7.isDifficultyLocked()));
            this.server.getPlayerList().sendPlayerPermissionLevel(this);
            $$6.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
            this.unsetRemoved();
            this.moveTo($$1, $$2, $$3, $$4, $$5);
            this.setLevel($$0);
            $$0.addDuringCommandTeleport(this);
            this.triggerDimensionChangeTriggers($$6);
            this.connection.teleport($$1, $$2, $$3, $$4, $$5);
            this.server.getPlayerList().sendLevelInfo(this, $$0);
            this.server.getPlayerList().sendAllPlayerInfo(this);
        }
    }

    @Nullable
    public BlockPos getRespawnPosition() {
        return this.respawnPosition;
    }

    public float getRespawnAngle() {
        return this.respawnAngle;
    }

    public ResourceKey<Level> getRespawnDimension() {
        return this.respawnDimension;
    }

    public boolean isRespawnForced() {
        return this.respawnForced;
    }

    public void setRespawnPosition(ResourceKey<Level> $$0, @Nullable BlockPos $$1, float $$2, boolean $$3, boolean $$4) {
        if ($$1 != null) {
            boolean $$5;
            boolean bl = $$5 = $$1.equals(this.respawnPosition) && $$0.equals(this.respawnDimension);
            if ($$4 && !$$5) {
                this.sendSystemMessage(Component.translatable("block.minecraft.set_spawn"));
            }
            this.respawnPosition = $$1;
            this.respawnDimension = $$0;
            this.respawnAngle = $$2;
            this.respawnForced = $$3;
        } else {
            this.respawnPosition = null;
            this.respawnDimension = Level.OVERWORLD;
            this.respawnAngle = 0.0f;
            this.respawnForced = false;
        }
    }

    public void trackChunk(ChunkPos $$0, Packet<?> $$1) {
        this.connection.send($$1);
    }

    public void untrackChunk(ChunkPos $$0) {
        if (this.isAlive()) {
            this.connection.send(new ClientboundForgetLevelChunkPacket($$0.x, $$0.z));
        }
    }

    public SectionPos getLastSectionPos() {
        return this.lastSectionPos;
    }

    public void setLastSectionPos(SectionPos $$0) {
        this.lastSectionPos = $$0;
    }

    @Override
    public void playNotifySound(SoundEvent $$0, SoundSource $$1, float $$2, float $$3) {
        this.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$0), $$1, this.getX(), this.getY(), this.getZ(), $$2, $$3, this.random.nextLong()));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddPlayerPacket(this);
    }

    @Override
    public ItemEntity drop(ItemStack $$0, boolean $$1, boolean $$2) {
        ItemEntity $$3 = super.drop($$0, $$1, $$2);
        if ($$3 == null) {
            return null;
        }
        this.level.addFreshEntity($$3);
        ItemStack $$4 = $$3.getItem();
        if ($$2) {
            if (!$$4.isEmpty()) {
                this.awardStat(Stats.ITEM_DROPPED.get($$4.getItem()), $$0.getCount());
            }
            this.awardStat(Stats.DROP);
        }
        return $$3;
    }

    public TextFilter getTextFilter() {
        return this.textFilter;
    }

    public void setLevel(ServerLevel $$0) {
        this.level = $$0;
        this.gameMode.setLevel($$0);
    }

    @Nullable
    private static GameType readPlayerMode(@Nullable CompoundTag $$0, String $$1) {
        return $$0 != null && $$0.contains($$1, 99) ? GameType.byId($$0.getInt($$1)) : null;
    }

    private GameType calculateGameModeForNewPlayer(@Nullable GameType $$0) {
        GameType $$1 = this.server.getForcedGameType();
        if ($$1 != null) {
            return $$1;
        }
        return $$0 != null ? $$0 : this.server.getDefaultGameType();
    }

    public void loadGameTypes(@Nullable CompoundTag $$0) {
        this.gameMode.setGameModeForPlayer(this.calculateGameModeForNewPlayer(ServerPlayer.readPlayerMode($$0, "playerGameType")), ServerPlayer.readPlayerMode($$0, "previousPlayerGameType"));
    }

    private void storeGameTypes(CompoundTag $$0) {
        $$0.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
        GameType $$1 = this.gameMode.getPreviousGameModeForPlayer();
        if ($$1 != null) {
            $$0.putInt("previousPlayerGameType", $$1.getId());
        }
    }

    @Override
    public boolean isTextFilteringEnabled() {
        return this.textFilteringEnabled;
    }

    public boolean shouldFilterMessageTo(ServerPlayer $$0) {
        if ($$0 == this) {
            return false;
        }
        return this.textFilteringEnabled || $$0.textFilteringEnabled;
    }

    @Override
    public boolean mayInteract(Level $$0, BlockPos $$1) {
        return super.mayInteract($$0, $$1) && $$0.mayInteract(this, $$1);
    }

    @Override
    protected void updateUsingItem(ItemStack $$0) {
        CriteriaTriggers.USING_ITEM.trigger(this, $$0);
        super.updateUsingItem($$0);
    }

    public boolean drop(boolean $$0) {
        Inventory $$12 = this.getInventory();
        ItemStack $$2 = $$12.removeFromSelected($$0);
        this.containerMenu.findSlot($$12, $$12.selected).ifPresent($$1 -> this.containerMenu.setRemoteSlot($$1, $$12.getSelected()));
        return this.drop($$2, false, true) != null;
    }

    public boolean allowsListing() {
        return this.allowsListing;
    }

    @Override
    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.of((Object)this.wardenSpawnTracker);
    }

    @Override
    public void onItemPickup(ItemEntity $$0) {
        super.onItemPickup($$0);
        Entity $$1 = $$0.getOwner();
        if ($$1 != null) {
            CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, $$0.getItem(), $$1);
        }
    }

    public void setChatSession(RemoteChatSession $$0) {
        this.chatSession = $$0;
    }

    @Nullable
    public RemoteChatSession getChatSession() {
        return this.chatSession;
    }

    @Override
    public void knockback(double $$0, double $$1, double $$2) {
        super.knockback($$0, $$1, $$2);
        this.hurtDir = (float)(Mth.atan2($$2, $$1) * 57.2957763671875 - (double)this.getYRot());
        this.connection.send(new ClientboundHurtAnimationPacket(this));
    }
}