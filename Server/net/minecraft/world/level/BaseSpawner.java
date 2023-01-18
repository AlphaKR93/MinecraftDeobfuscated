/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public abstract class BaseSpawner {
    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EVENT_SPAWN = 1;
    private int spawnDelay = 20;
    private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
    @Nullable
    private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity displayEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    public void setEntityId(EntityType<?> $$0, @Nullable Level $$1, RandomSource $$2, BlockPos $$3) {
        this.getOrCreateNextSpawnData($$1, $$2, $$3).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey($$0).toString());
    }

    private boolean isNearPlayer(Level $$0, BlockPos $$1) {
        return $$0.hasNearbyAlivePlayer((double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void clientTick(Level $$0, BlockPos $$1) {
        if (!this.isNearPlayer($$0, $$1)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null) {
            RandomSource $$2 = $$0.getRandom();
            double $$3 = (double)$$1.getX() + $$2.nextDouble();
            double $$4 = (double)$$1.getY() + $$2.nextDouble();
            double $$5 = (double)$$1.getZ() + $$2.nextDouble();
            $$0.addParticle(ParticleTypes.SMOKE, $$3, $$4, $$5, 0.0, 0.0, 0.0);
            $$0.addParticle(ParticleTypes.FLAME, $$3, $$4, $$5, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        }
    }

    public void serverTick(ServerLevel $$0, BlockPos $$1) {
        if (!this.isNearPlayer($$0, $$1)) {
            return;
        }
        if (this.spawnDelay == -1) {
            this.delay($$0, $$1);
        }
        if (this.spawnDelay > 0) {
            --this.spawnDelay;
            return;
        }
        boolean $$2 = false;
        RandomSource $$32 = $$0.getRandom();
        SpawnData $$4 = this.getOrCreateNextSpawnData($$0, $$32, $$1);
        for (int $$5 = 0; $$5 < this.spawnCount; ++$$5) {
            SpawnData.CustomSpawnRules $$14;
            double $$12;
            CompoundTag $$6 = $$4.getEntityToSpawn();
            Optional<EntityType<?>> $$7 = EntityType.by($$6);
            if ($$7.isEmpty()) {
                this.delay($$0, $$1);
                return;
            }
            ListTag $$8 = $$6.getList("Pos", 6);
            int $$9 = $$8.size();
            double $$10 = $$9 >= 1 ? $$8.getDouble(0) : (double)$$1.getX() + ($$32.nextDouble() - $$32.nextDouble()) * (double)this.spawnRange + 0.5;
            double $$11 = $$9 >= 2 ? $$8.getDouble(1) : (double)($$1.getY() + $$32.nextInt(3) - 1);
            double d = $$12 = $$9 >= 3 ? $$8.getDouble(2) : (double)$$1.getZ() + ($$32.nextDouble() - $$32.nextDouble()) * (double)this.spawnRange + 0.5;
            if (!$$0.noCollision(((EntityType)$$7.get()).getAABB($$10, $$11, $$12))) continue;
            BlockPos $$13 = new BlockPos($$10, $$11, $$12);
            if (!$$4.getCustomSpawnRules().isPresent() ? !SpawnPlacements.checkSpawnRules((EntityType)$$7.get(), $$0, MobSpawnType.SPAWNER, $$13, $$0.getRandom()) : !((EntityType)$$7.get()).getCategory().isFriendly() && $$0.getDifficulty() == Difficulty.PEACEFUL || !($$14 = (SpawnData.CustomSpawnRules)((Object)$$4.getCustomSpawnRules().get())).blockLightLimit().isValueInRange($$0.getBrightness(LightLayer.BLOCK, $$13)) || !$$14.skyLightLimit().isValueInRange($$0.getBrightness(LightLayer.SKY, $$13))) continue;
            Entity $$15 = EntityType.loadEntityRecursive($$6, $$0, (Function<Entity, Entity>)((Function)$$3 -> {
                $$3.moveTo($$10, $$11, $$12, $$3.getYRot(), $$3.getXRot());
                return $$3;
            }));
            if ($$15 == null) {
                this.delay($$0, $$1);
                return;
            }
            int $$16 = $$0.getEntitiesOfClass($$15.getClass(), new AABB($$1.getX(), $$1.getY(), $$1.getZ(), $$1.getX() + 1, $$1.getY() + 1, $$1.getZ() + 1).inflate(this.spawnRange)).size();
            if ($$16 >= this.maxNearbyEntities) {
                this.delay($$0, $$1);
                return;
            }
            $$15.moveTo($$15.getX(), $$15.getY(), $$15.getZ(), $$32.nextFloat() * 360.0f, 0.0f);
            if ($$15 instanceof Mob) {
                Mob $$17 = (Mob)$$15;
                if ($$4.getCustomSpawnRules().isEmpty() && !$$17.checkSpawnRules($$0, MobSpawnType.SPAWNER) || !$$17.checkSpawnObstruction($$0)) continue;
                if ($$4.getEntityToSpawn().size() == 1 && $$4.getEntityToSpawn().contains("id", 8)) {
                    ((Mob)$$15).finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$15.blockPosition()), MobSpawnType.SPAWNER, null, null);
                }
            }
            if (!$$0.tryAddFreshEntityWithPassengers($$15)) {
                this.delay($$0, $$1);
                return;
            }
            $$0.levelEvent(2004, $$1, 0);
            $$0.gameEvent($$15, GameEvent.ENTITY_PLACE, $$13);
            if ($$15 instanceof Mob) {
                ((Mob)$$15).spawnAnim();
            }
            $$2 = true;
        }
        if ($$2) {
            this.delay($$0, $$1);
        }
    }

    private void delay(Level $$0, BlockPos $$1) {
        RandomSource $$22 = $$0.random;
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + $$22.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        this.spawnPotentials.getRandom($$22).ifPresent($$2 -> this.setNextSpawnData($$0, $$1, (SpawnData)((Object)((Object)$$2.getData()))));
        this.broadcastEvent($$0, $$1, 1);
    }

    public void load(@Nullable Level $$02, BlockPos $$1, CompoundTag $$2) {
        boolean $$5;
        this.spawnDelay = $$2.getShort("Delay");
        boolean $$3 = $$2.contains(SPAWN_DATA_TAG, 10);
        if ($$3) {
            SpawnData $$4 = (SpawnData)((Object)SpawnData.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$2.getCompound(SPAWN_DATA_TAG)).resultOrPartial($$0 -> LOGGER.warn("Invalid SpawnData: {}", $$0)).orElseGet(SpawnData::new));
            this.setNextSpawnData($$02, $$1, $$4);
        }
        if ($$5 = $$2.contains("SpawnPotentials", 9)) {
            ListTag $$6 = $$2.getList("SpawnPotentials", 10);
            this.spawnPotentials = (SimpleWeightedRandomList)SpawnData.LIST_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)$$6).resultOrPartial($$0 -> LOGGER.warn("Invalid SpawnPotentials list: {}", $$0)).orElseGet(SimpleWeightedRandomList::empty);
        } else {
            this.spawnPotentials = SimpleWeightedRandomList.single(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData());
        }
        if ($$2.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = $$2.getShort("MinSpawnDelay");
            this.maxSpawnDelay = $$2.getShort("MaxSpawnDelay");
            this.spawnCount = $$2.getShort("SpawnCount");
        }
        if ($$2.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = $$2.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = $$2.getShort("RequiredPlayerRange");
        }
        if ($$2.contains("SpawnRange", 99)) {
            this.spawnRange = $$2.getShort("SpawnRange");
        }
        this.displayEntity = null;
    }

    public CompoundTag save(CompoundTag $$0) {
        $$0.putShort("Delay", (short)this.spawnDelay);
        $$0.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        $$0.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        $$0.putShort("SpawnCount", (short)this.spawnCount);
        $$0.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        $$0.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        $$0.putShort("SpawnRange", (short)this.spawnRange);
        if (this.nextSpawnData != null) {
            $$0.put(SPAWN_DATA_TAG, (Tag)SpawnData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.nextSpawnData).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData")));
        }
        $$0.put("SpawnPotentials", (Tag)SpawnData.LIST_CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
        return $$0;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level $$0, RandomSource $$1, BlockPos $$2) {
        if (this.displayEntity == null) {
            CompoundTag $$3 = this.getOrCreateNextSpawnData($$0, $$1, $$2).getEntityToSpawn();
            if (!$$3.contains("id", 8)) {
                return null;
            }
            this.displayEntity = EntityType.loadEntityRecursive($$3, $$0, (Function<Entity, Entity>)Function.identity());
            if ($$3.size() != 1 || this.displayEntity instanceof Mob) {
                // empty if block
            }
        }
        return this.displayEntity;
    }

    public boolean onEventTriggered(Level $$0, int $$1) {
        if ($$1 == 1) {
            if ($$0.isClientSide) {
                this.spawnDelay = this.minSpawnDelay;
            }
            return true;
        }
        return false;
    }

    protected void setNextSpawnData(@Nullable Level $$0, BlockPos $$1, SpawnData $$2) {
        this.nextSpawnData = $$2;
    }

    private SpawnData getOrCreateNextSpawnData(@Nullable Level $$0, RandomSource $$1, BlockPos $$2) {
        if (this.nextSpawnData != null) {
            return this.nextSpawnData;
        }
        this.setNextSpawnData($$0, $$2, (SpawnData)((Object)this.spawnPotentials.getRandom($$1).map(WeightedEntry.Wrapper::getData).orElseGet(SpawnData::new)));
        return this.nextSpawnData;
    }

    public abstract void broadcastEvent(Level var1, BlockPos var2, int var3);

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }
}