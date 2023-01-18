/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1f;
    public static final WeightedRandomList<SpawnerData> EMPTY_MOB_LIST = WeightedRandomList.create();
    public static final MobSpawnSettings EMPTY = new Builder().build();
    public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)0.9999999f).optionalFieldOf("creature_spawn_probability", (Object)Float.valueOf((float)0.1f)).forGetter($$0 -> Float.valueOf((float)$$0.creatureGenerationProbability)), (App)Codec.simpleMap(MobCategory.CODEC, (Codec)WeightedRandomList.codec(SpawnerData.CODEC).promotePartial(Util.prefix("Spawn data: ", (Consumer<String>)((Consumer)arg_0 -> ((Logger)LOGGER).error(arg_0)))), (Keyable)StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter($$0 -> $$0.spawners), (App)Codec.simpleMap((Codec)BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter($$0 -> $$0.mobSpawnCosts)).apply((Applicative)$$02, MobSpawnSettings::new));
    private final float creatureGenerationProbability;
    private final Map<MobCategory, WeightedRandomList<SpawnerData>> spawners;
    private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts;

    MobSpawnSettings(float $$0, Map<MobCategory, WeightedRandomList<SpawnerData>> $$1, Map<EntityType<?>, MobSpawnCost> $$2) {
        this.creatureGenerationProbability = $$0;
        this.spawners = ImmutableMap.copyOf($$1);
        this.mobSpawnCosts = ImmutableMap.copyOf($$2);
    }

    public WeightedRandomList<SpawnerData> getMobs(MobCategory $$0) {
        return (WeightedRandomList)this.spawners.getOrDefault((Object)$$0, EMPTY_MOB_LIST);
    }

    @Nullable
    public MobSpawnCost getMobSpawnCost(EntityType<?> $$0) {
        return (MobSpawnCost)this.mobSpawnCosts.get($$0);
    }

    public float getCreatureProbability() {
        return this.creatureGenerationProbability;
    }

    public static class MobSpawnCost {
        public static final Codec<MobSpawnCost> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter($$0 -> $$0.energyBudget), (App)Codec.DOUBLE.fieldOf("charge").forGetter($$0 -> $$0.charge)).apply((Applicative)$$02, MobSpawnCost::new));
        private final double energyBudget;
        private final double charge;

        MobSpawnCost(double $$0, double $$1) {
            this.energyBudget = $$0;
            this.charge = $$1;
        }

        public double getEnergyBudget() {
            return this.energyBudget;
        }

        public double getCharge() {
            return this.charge;
        }
    }

    public static class SpawnerData
    extends WeightedEntry.IntrusiveBase {
        public static final Codec<SpawnerData> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter($$0 -> $$0.type), (App)Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight), (App)Codec.INT.fieldOf("minCount").forGetter($$0 -> $$0.minCount), (App)Codec.INT.fieldOf("maxCount").forGetter($$0 -> $$0.maxCount)).apply((Applicative)$$02, SpawnerData::new));
        public final EntityType<?> type;
        public final int minCount;
        public final int maxCount;

        public SpawnerData(EntityType<?> $$0, int $$1, int $$2, int $$3) {
            this($$0, Weight.of($$1), $$2, $$3);
        }

        public SpawnerData(EntityType<?> $$0, Weight $$1, int $$2, int $$3) {
            super($$1);
            this.type = $$0.getCategory() == MobCategory.MISC ? EntityType.PIG : $$0;
            this.minCount = $$2;
            this.maxCount = $$3;
        }

        public String toString() {
            return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.getWeight();
        }
    }

    public static class Builder {
        private final Map<MobCategory, List<SpawnerData>> spawners = (Map)Stream.of((Object[])MobCategory.values()).collect(ImmutableMap.toImmutableMap($$0 -> $$0, $$0 -> Lists.newArrayList()));
        private final Map<EntityType<?>, MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
        private float creatureGenerationProbability = 0.1f;

        public Builder addSpawn(MobCategory $$0, SpawnerData $$1) {
            ((List)this.spawners.get((Object)$$0)).add((Object)$$1);
            return this;
        }

        public Builder addMobCharge(EntityType<?> $$0, double $$1, double $$2) {
            this.mobSpawnCosts.put($$0, (Object)new MobSpawnCost($$2, $$1));
            return this;
        }

        public Builder creatureGenerationProbability(float $$0) {
            this.creatureGenerationProbability = $$0;
            return this;
        }

        public MobSpawnSettings build() {
            return new MobSpawnSettings(this.creatureGenerationProbability, (Map<MobCategory, WeightedRandomList<SpawnerData>>)((Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> WeightedRandomList.create((List)$$0.getValue())))), (Map<EntityType<?>, MobSpawnCost>)ImmutableMap.copyOf(this.mobSpawnCosts));
        }
    }
}