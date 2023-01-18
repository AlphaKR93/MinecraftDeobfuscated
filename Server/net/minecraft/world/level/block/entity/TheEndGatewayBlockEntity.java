/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TheEndGatewayBlockEntity
extends TheEndPortalBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SPAWN_TIME = 200;
    private static final int COOLDOWN_TIME = 40;
    private static final int ATTENTION_INTERVAL = 2400;
    private static final int EVENT_COOLDOWN = 1;
    private static final int GATEWAY_HEIGHT_ABOVE_SURFACE = 10;
    private long age;
    private int teleportCooldown;
    @Nullable
    private BlockPos exitPortal;
    private boolean exactTeleport;

    public TheEndGatewayBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.END_GATEWAY, $$0, $$1);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putLong("Age", this.age);
        if (this.exitPortal != null) {
            $$0.put("ExitPortal", NbtUtils.writeBlockPos(this.exitPortal));
        }
        if (this.exactTeleport) {
            $$0.putBoolean("ExactTeleport", true);
        }
    }

    @Override
    public void load(CompoundTag $$0) {
        BlockPos $$1;
        super.load($$0);
        this.age = $$0.getLong("Age");
        if ($$0.contains("ExitPortal", 10) && Level.isInSpawnableBounds($$1 = NbtUtils.readBlockPos($$0.getCompound("ExitPortal")))) {
            this.exitPortal = $$1;
        }
        this.exactTeleport = $$0.getBoolean("ExactTeleport");
    }

    public static void beamAnimationTick(Level $$0, BlockPos $$1, BlockState $$2, TheEndGatewayBlockEntity $$3) {
        ++$$3.age;
        if ($$3.isCoolingDown()) {
            --$$3.teleportCooldown;
        }
    }

    public static void teleportTick(Level $$0, BlockPos $$1, BlockState $$2, TheEndGatewayBlockEntity $$3) {
        boolean $$4 = $$3.isSpawning();
        boolean $$5 = $$3.isCoolingDown();
        ++$$3.age;
        if ($$5) {
            --$$3.teleportCooldown;
        } else {
            List $$6 = $$0.getEntitiesOfClass(Entity.class, new AABB($$1), TheEndGatewayBlockEntity::canEntityTeleport);
            if (!$$6.isEmpty()) {
                TheEndGatewayBlockEntity.teleportEntity($$0, $$1, $$2, (Entity)$$6.get($$0.random.nextInt($$6.size())), $$3);
            }
            if ($$3.age % 2400L == 0L) {
                TheEndGatewayBlockEntity.triggerCooldown($$0, $$1, $$2, $$3);
            }
        }
        if ($$4 != $$3.isSpawning() || $$5 != $$3.isCoolingDown()) {
            TheEndGatewayBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    public static boolean canEntityTeleport(Entity $$0) {
        return EntitySelector.NO_SPECTATORS.test((Object)$$0) && !$$0.getRootVehicle().isOnPortalCooldown();
    }

    public boolean isSpawning() {
        return this.age < 200L;
    }

    public boolean isCoolingDown() {
        return this.teleportCooldown > 0;
    }

    public float getSpawnPercent(float $$0) {
        return Mth.clamp(((float)this.age + $$0) / 200.0f, 0.0f, 1.0f);
    }

    public float getCooldownPercent(float $$0) {
        return 1.0f - Mth.clamp(((float)this.teleportCooldown - $$0) / 40.0f, 0.0f, 1.0f);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    private static void triggerCooldown(Level $$0, BlockPos $$1, BlockState $$2, TheEndGatewayBlockEntity $$3) {
        if (!$$0.isClientSide) {
            $$3.teleportCooldown = 40;
            $$0.blockEvent($$1, $$2.getBlock(), 1, 0);
            TheEndGatewayBlockEntity.setChanged($$0, $$1, $$2);
        }
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if ($$0 == 1) {
            this.teleportCooldown = 40;
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    public static void teleportEntity(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3, TheEndGatewayBlockEntity $$4) {
        if (!($$0 instanceof ServerLevel) || $$4.isCoolingDown()) {
            return;
        }
        ServerLevel $$5 = (ServerLevel)$$0;
        $$4.teleportCooldown = 100;
        if ($$4.exitPortal == null && $$0.dimension() == Level.END) {
            Vec3i $$6 = TheEndGatewayBlockEntity.findOrCreateValidTeleportPos($$5, $$1);
            $$6 = $$6.above(10);
            LOGGER.debug("Creating portal at {}", (Object)$$6);
            TheEndGatewayBlockEntity.spawnGatewayPortal($$5, $$6, EndGatewayConfiguration.knownExit($$1, false));
            $$4.exitPortal = $$6;
        }
        if ($$4.exitPortal != null) {
            Entity $$11;
            BlockPos $$7;
            BlockPos blockPos = $$7 = $$4.exactTeleport ? $$4.exitPortal : TheEndGatewayBlockEntity.findExitPosition($$0, $$4.exitPortal);
            if ($$3 instanceof ThrownEnderpearl) {
                Entity $$8 = ((ThrownEnderpearl)$$3).getOwner();
                if ($$8 instanceof ServerPlayer) {
                    CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer)$$8, $$2);
                }
                if ($$8 != null) {
                    Entity $$9 = $$8;
                    $$3.discard();
                } else {
                    Entity $$10 = $$3;
                }
            } else {
                $$11 = $$3.getRootVehicle();
            }
            $$11.setPortalCooldown();
            $$11.teleportToWithTicket((double)$$7.getX() + 0.5, $$7.getY(), (double)$$7.getZ() + 0.5);
        }
        TheEndGatewayBlockEntity.triggerCooldown($$0, $$1, $$2, $$4);
    }

    private static BlockPos findExitPosition(Level $$0, BlockPos $$1) {
        BlockPos $$2 = TheEndGatewayBlockEntity.findTallestBlock($$0, $$1.offset(0, 2, 0), 5, false);
        LOGGER.debug("Best exit position for portal at {} is {}", (Object)$$1, (Object)$$2);
        return $$2.above();
    }

    private static BlockPos findOrCreateValidTeleportPos(ServerLevel $$02, BlockPos $$1) {
        Vec3 $$22 = TheEndGatewayBlockEntity.findExitPortalXZPosTentative($$02, $$1);
        LevelChunk $$3 = TheEndGatewayBlockEntity.getChunk($$02, $$22);
        BlockPos $$4 = TheEndGatewayBlockEntity.findValidSpawnInChunk($$3);
        if ($$4 == null) {
            BlockPos $$5 = new BlockPos($$22.x + 0.5, 75.0, $$22.z + 0.5);
            LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", (Object)$$5);
            $$02.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.getHolder(EndFeatures.END_ISLAND)).ifPresent($$2 -> ((ConfiguredFeature)((Object)((Object)$$2.value()))).place($$02, $$02.getChunkSource().getGenerator(), RandomSource.create($$5.asLong()), $$5));
            $$4 = $$5;
        } else {
            LOGGER.debug("Found suitable block to teleport to: {}", (Object)$$4);
        }
        return TheEndGatewayBlockEntity.findTallestBlock($$02, $$4, 16, true);
    }

    private static Vec3 findExitPortalXZPosTentative(ServerLevel $$0, BlockPos $$1) {
        Vec3 $$2 = new Vec3($$1.getX(), 0.0, $$1.getZ()).normalize();
        int $$3 = 1024;
        Vec3 $$4 = $$2.scale(1024.0);
        int $$5 = 16;
        while (!TheEndGatewayBlockEntity.isChunkEmpty($$0, $$4) && $$5-- > 0) {
            LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)$$4);
            $$4 = $$4.add($$2.scale(-16.0));
        }
        $$5 = 16;
        while (TheEndGatewayBlockEntity.isChunkEmpty($$0, $$4) && $$5-- > 0) {
            LOGGER.debug("Skipping forward past empty chunk at {}", (Object)$$4);
            $$4 = $$4.add($$2.scale(16.0));
        }
        LOGGER.debug("Found chunk at {}", (Object)$$4);
        return $$4;
    }

    private static boolean isChunkEmpty(ServerLevel $$0, Vec3 $$1) {
        return TheEndGatewayBlockEntity.getChunk($$0, $$1).getHighestSectionPosition() <= $$0.getMinBuildHeight();
    }

    private static BlockPos findTallestBlock(BlockGetter $$0, BlockPos $$1, int $$2, boolean $$3) {
        Vec3i $$4 = null;
        for (int $$5 = -$$2; $$5 <= $$2; ++$$5) {
            block1: for (int $$6 = -$$2; $$6 <= $$2; ++$$6) {
                if ($$5 == 0 && $$6 == 0 && !$$3) continue;
                for (int $$7 = $$0.getMaxBuildHeight() - 1; $$7 > ($$4 == null ? $$0.getMinBuildHeight() : $$4.getY()); --$$7) {
                    BlockPos $$8 = new BlockPos($$1.getX() + $$5, $$7, $$1.getZ() + $$6);
                    BlockState $$9 = $$0.getBlockState($$8);
                    if (!$$9.isCollisionShapeFullBlock($$0, $$8) || !$$3 && $$9.is(Blocks.BEDROCK)) continue;
                    $$4 = $$8;
                    continue block1;
                }
            }
        }
        return $$4 == null ? $$1 : $$4;
    }

    private static LevelChunk getChunk(Level $$0, Vec3 $$1) {
        return $$0.getChunk(Mth.floor($$1.x / 16.0), Mth.floor($$1.z / 16.0));
    }

    @Nullable
    private static BlockPos findValidSpawnInChunk(LevelChunk $$0) {
        ChunkPos $$1 = $$0.getPos();
        BlockPos $$2 = new BlockPos($$1.getMinBlockX(), 30, $$1.getMinBlockZ());
        int $$3 = $$0.getHighestSectionPosition() + 16 - 1;
        BlockPos $$4 = new BlockPos($$1.getMaxBlockX(), $$3, $$1.getMaxBlockZ());
        BlockPos $$5 = null;
        double $$6 = 0.0;
        for (BlockPos $$7 : BlockPos.betweenClosed($$2, $$4)) {
            BlockState $$8 = $$0.getBlockState($$7);
            Vec3i $$9 = $$7.above();
            Vec3i $$10 = $$7.above(2);
            if (!$$8.is(Blocks.END_STONE) || $$0.getBlockState((BlockPos)$$9).isCollisionShapeFullBlock($$0, (BlockPos)$$9) || $$0.getBlockState((BlockPos)$$10).isCollisionShapeFullBlock($$0, (BlockPos)$$10)) continue;
            double $$11 = $$7.distToCenterSqr(0.0, 0.0, 0.0);
            if ($$5 != null && !($$11 < $$6)) continue;
            $$5 = $$7;
            $$6 = $$11;
        }
        return $$5;
    }

    private static void spawnGatewayPortal(ServerLevel $$0, BlockPos $$1, EndGatewayConfiguration $$2) {
        Feature.END_GATEWAY.place($$2, $$0, $$0.getChunkSource().getGenerator(), RandomSource.create(), $$1);
    }

    @Override
    public boolean shouldRenderFace(Direction $$0) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), $$0, (BlockPos)this.getBlockPos().relative($$0));
    }

    public int getParticleAmount() {
        int $$0 = 0;
        for (Direction $$1 : Direction.values()) {
            $$0 += this.shouldRenderFace($$1) ? 1 : 0;
        }
        return $$0;
    }

    public void setExitPosition(BlockPos $$0, boolean $$1) {
        this.exactTeleport = $$1;
        this.exitPortal = $$0;
    }
}