/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  io.netty.buffer.Unpooled
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  java.lang.Iterable
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.UUID
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DebugPackets {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void sendGameTestAddMarker(ServerLevel $$0, BlockPos $$1, String $$2, int $$3, int $$4) {
        FriendlyByteBuf $$5 = new FriendlyByteBuf(Unpooled.buffer());
        $$5.writeBlockPos($$1);
        $$5.writeInt($$3);
        $$5.writeUtf($$2);
        $$5.writeInt($$4);
        DebugPackets.sendPacketToAllPlayers($$0, $$5, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER);
    }

    public static void sendGameTestClearPacket(ServerLevel $$0) {
        FriendlyByteBuf $$1 = new FriendlyByteBuf(Unpooled.buffer());
        DebugPackets.sendPacketToAllPlayers($$0, $$1, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR);
    }

    public static void sendPoiPacketsForChunk(ServerLevel $$0, ChunkPos $$1) {
    }

    public static void sendPoiAddedPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    public static void sendPoiRemovedPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    public static void sendPoiTicketCountPacket(ServerLevel $$0, BlockPos $$1) {
        DebugPackets.sendVillageSectionsPacket($$0, $$1);
    }

    private static void sendVillageSectionsPacket(ServerLevel $$0, BlockPos $$1) {
    }

    public static void sendPathFindingPacket(Level $$0, Mob $$1, @Nullable Path $$2, float $$3) {
    }

    public static void sendNeighborsUpdatePacket(Level $$0, BlockPos $$1) {
    }

    public static void sendStructurePacket(WorldGenLevel $$0, StructureStart $$1) {
    }

    public static void sendGoalSelector(Level $$0, Mob $$1, GoalSelector $$2) {
        if (!($$0 instanceof ServerLevel)) {
            return;
        }
    }

    public static void sendRaids(ServerLevel $$0, Collection<Raid> $$1) {
    }

    public static void sendEntityBrain(LivingEntity $$0) {
    }

    public static void sendBeeInfo(Bee $$0) {
    }

    public static void sendGameEventInfo(Level $$0, GameEvent $$1, Vec3 $$2) {
    }

    public static void sendGameEventListenerInfo(Level $$0, GameEventListener $$1) {
    }

    public static void sendHiveInfo(Level $$0, BlockPos $$1, BlockState $$2, BeehiveBlockEntity $$3) {
    }

    private static void writeBrain(LivingEntity $$02, FriendlyByteBuf $$12) {
        Brain<Path> $$2 = $$02.getBrain();
        long $$3 = $$02.level.getGameTime();
        if ($$02 instanceof InventoryCarrier) {
            SimpleContainer $$4 = ((InventoryCarrier)((Object)$$02)).getInventory();
            $$12.writeUtf($$4.isEmpty() ? "" : $$4.toString());
        } else {
            $$12.writeUtf("");
        }
        $$12.writeOptional($$2.hasMemoryValue(MemoryModuleType.PATH) ? $$2.getMemory(MemoryModuleType.PATH) : Optional.empty(), ($$0, $$1) -> $$1.writeToStream((FriendlyByteBuf)((Object)$$0)));
        if ($$02 instanceof Villager) {
            Villager $$5 = (Villager)$$02;
            boolean $$6 = $$5.wantsToSpawnGolem($$3);
            $$12.writeBoolean($$6);
        } else {
            $$12.writeBoolean(false);
        }
        if ($$02.getType() == EntityType.WARDEN) {
            Warden $$7 = (Warden)$$02;
            $$12.writeInt($$7.getClientAngerLevel());
        } else {
            $$12.writeInt(-1);
        }
        $$12.writeCollection($$2.getActiveActivities(), ($$0, $$1) -> $$0.writeUtf($$1.getName()));
        Set $$8 = (Set)$$2.getRunningBehaviors().stream().map(BehaviorControl::debugString).collect(Collectors.toSet());
        $$12.writeCollection($$8, FriendlyByteBuf::writeUtf);
        $$12.writeCollection(DebugPackets.getMemoryDescriptions($$02, $$3), ($$0, $$1) -> {
            String $$2 = StringUtil.truncateStringIfNecessary($$1, 255, true);
            $$0.writeUtf($$2);
        });
        if ($$02 instanceof Villager) {
            Set $$9 = (Set)Stream.of((Object[])new MemoryModuleType[]{MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT}).map($$2::getMemory).flatMap(Optional::stream).map(GlobalPos::pos).collect(Collectors.toSet());
            $$12.writeCollection($$9, FriendlyByteBuf::writeBlockPos);
        } else {
            $$12.writeVarInt(0);
        }
        if ($$02 instanceof Villager) {
            Set $$10 = (Set)Stream.of(MemoryModuleType.POTENTIAL_JOB_SITE).map($$2::getMemory).flatMap(Optional::stream).map(GlobalPos::pos).collect(Collectors.toSet());
            $$12.writeCollection($$10, FriendlyByteBuf::writeBlockPos);
        } else {
            $$12.writeVarInt(0);
        }
        if ($$02 instanceof Villager) {
            Map<UUID, Object2IntMap<GossipType>> $$11 = ((Villager)$$02).getGossips().getGossipEntries();
            ArrayList $$122 = Lists.newArrayList();
            $$11.forEach((arg_0, arg_1) -> DebugPackets.lambda$writeBrain$10((List)$$122, arg_0, arg_1));
            $$12.writeCollection($$122, FriendlyByteBuf::writeUtf);
        } else {
            $$12.writeVarInt(0);
        }
    }

    private static List<String> getMemoryDescriptions(LivingEntity $$0, long $$1) {
        Map<MemoryModuleType<?>, Optional<ExpirableValue<?>>> $$2 = $$0.getBrain().getMemories();
        ArrayList $$3 = Lists.newArrayList();
        for (Map.Entry $$4 : $$2.entrySet()) {
            String $$13;
            MemoryModuleType $$5 = (MemoryModuleType)$$4.getKey();
            Optional $$6 = (Optional)$$4.getValue();
            if ($$6.isPresent()) {
                ExpirableValue $$7 = (ExpirableValue)$$6.get();
                Object $$8 = $$7.getValue();
                if ($$5 == MemoryModuleType.HEARD_BELL_TIME) {
                    long $$9 = $$1 - (Long)$$8;
                    String $$10 = $$9 + " ticks ago";
                } else if ($$7.canExpire()) {
                    String $$11 = DebugPackets.getShortDescription((ServerLevel)$$0.level, $$8) + " (ttl: " + $$7.getTimeToLive() + ")";
                } else {
                    String $$12 = DebugPackets.getShortDescription((ServerLevel)$$0.level, $$8);
                }
            } else {
                $$13 = "-";
            }
            $$3.add((Object)(BuiltInRegistries.MEMORY_MODULE_TYPE.getKey($$5).getPath() + ": " + $$13));
        }
        $$3.sort(String::compareTo);
        return $$3;
    }

    private static String getShortDescription(ServerLevel $$0, @Nullable Object $$1) {
        if ($$1 == null) {
            return "-";
        }
        if ($$1 instanceof UUID) {
            return DebugPackets.getShortDescription($$0, $$0.getEntity((UUID)$$1));
        }
        if ($$1 instanceof LivingEntity) {
            Entity $$2 = (Entity)$$1;
            return DebugEntityNameGenerator.getEntityName($$2);
        }
        if ($$1 instanceof Nameable) {
            return ((Nameable)$$1).getName().getString();
        }
        if ($$1 instanceof WalkTarget) {
            return DebugPackets.getShortDescription($$0, ((WalkTarget)$$1).getTarget());
        }
        if ($$1 instanceof EntityTracker) {
            return DebugPackets.getShortDescription($$0, ((EntityTracker)$$1).getEntity());
        }
        if ($$1 instanceof GlobalPos) {
            return DebugPackets.getShortDescription($$0, ((GlobalPos)$$1).pos());
        }
        if ($$1 instanceof BlockPosTracker) {
            return DebugPackets.getShortDescription($$0, ((BlockPosTracker)$$1).currentBlockPosition());
        }
        if ($$1 instanceof EntityDamageSource) {
            Entity $$3 = ((EntityDamageSource)$$1).getEntity();
            return $$3 == null ? $$1.toString() : DebugPackets.getShortDescription($$0, $$3);
        }
        if ($$1 instanceof Collection) {
            ArrayList $$4 = Lists.newArrayList();
            for (Object $$5 : (Iterable)$$1) {
                $$4.add((Object)DebugPackets.getShortDescription($$0, $$5));
            }
            return $$4.toString();
        }
        return $$1.toString();
    }

    private static void sendPacketToAllPlayers(ServerLevel $$0, FriendlyByteBuf $$1, ResourceLocation $$2) {
        ClientboundCustomPayloadPacket $$3 = new ClientboundCustomPayloadPacket($$2, $$1);
        for (Player $$4 : $$0.players()) {
            ((ServerPlayer)$$4).connection.send($$3);
        }
    }

    private static /* synthetic */ void lambda$writeBrain$10(List $$0, UUID $$1, Object2IntMap $$22) {
        String $$32 = DebugEntityNameGenerator.getEntityName($$1);
        $$22.forEach(($$2, $$3) -> $$0.add((Object)($$32 + ": " + $$2 + ": " + $$3)));
    }

    private static /* synthetic */ void lambda$sendBeeInfo$5(FriendlyByteBuf $$0, Path $$1) {
        $$1.writeToStream($$0);
    }

    private static /* synthetic */ void lambda$sendRaids$4(FriendlyByteBuf $$0, Raid $$1) {
        $$0.writeBlockPos($$1.getCenter());
    }

    private static /* synthetic */ void lambda$sendGoalSelector$3(FriendlyByteBuf $$0, WrappedGoal $$1) {
        $$0.writeInt($$1.getPriority());
        $$0.writeBoolean($$1.isRunning());
        $$0.writeUtf($$1.getGoal().getClass().getSimpleName());
    }

    private static /* synthetic */ String lambda$sendPoiAddedPacket$2(ResourceKey $$0) {
        return $$0.location().toString();
    }

    private static /* synthetic */ void lambda$sendPoiPacketsForChunk$1(ServerLevel $$0, PoiRecord $$1) {
        DebugPackets.sendPoiAddedPacket($$0, $$1.getPos());
    }

    private static /* synthetic */ boolean lambda$sendPoiPacketsForChunk$0(Holder $$0) {
        return true;
    }
}