/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  java.io.IOException
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.NoSuchElementException
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
    private final RandomSource random;
    private final float luck;
    private final ServerLevel level;
    private final Function<ResourceLocation, LootTable> lootTables;
    private final Set<LootTable> visitedTables = Sets.newLinkedHashSet();
    private final Function<ResourceLocation, LootItemCondition> conditions;
    private final Set<LootItemCondition> visitedConditions = Sets.newLinkedHashSet();
    private final Map<LootContextParam<?>, Object> params;
    private final Map<ResourceLocation, DynamicDrop> dynamicDrops;

    LootContext(RandomSource $$0, float $$1, ServerLevel $$2, Function<ResourceLocation, LootTable> $$3, Function<ResourceLocation, LootItemCondition> $$4, Map<LootContextParam<?>, Object> $$5, Map<ResourceLocation, DynamicDrop> $$6) {
        this.random = $$0;
        this.luck = $$1;
        this.level = $$2;
        this.lootTables = $$3;
        this.conditions = $$4;
        this.params = ImmutableMap.copyOf($$5);
        this.dynamicDrops = ImmutableMap.copyOf($$6);
    }

    public boolean hasParam(LootContextParam<?> $$0) {
        return this.params.containsKey($$0);
    }

    public <T> T getParam(LootContextParam<T> $$0) {
        Object $$1 = this.params.get($$0);
        if ($$1 == null) {
            throw new NoSuchElementException($$0.getName().toString());
        }
        return (T)$$1;
    }

    public void addDynamicDrops(ResourceLocation $$0, Consumer<ItemStack> $$1) {
        DynamicDrop $$2 = (DynamicDrop)this.dynamicDrops.get((Object)$$0);
        if ($$2 != null) {
            $$2.add(this, $$1);
        }
    }

    @Nullable
    public <T> T getParamOrNull(LootContextParam<T> $$0) {
        return (T)this.params.get($$0);
    }

    public boolean addVisitedTable(LootTable $$0) {
        return this.visitedTables.add((Object)$$0);
    }

    public void removeVisitedTable(LootTable $$0) {
        this.visitedTables.remove((Object)$$0);
    }

    public boolean addVisitedCondition(LootItemCondition $$0) {
        return this.visitedConditions.add((Object)$$0);
    }

    public void removeVisitedCondition(LootItemCondition $$0) {
        this.visitedConditions.remove((Object)$$0);
    }

    public LootTable getLootTable(ResourceLocation $$0) {
        return (LootTable)this.lootTables.apply((Object)$$0);
    }

    @Nullable
    public LootItemCondition getCondition(ResourceLocation $$0) {
        return (LootItemCondition)this.conditions.apply((Object)$$0);
    }

    public RandomSource getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.luck;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @FunctionalInterface
    public static interface DynamicDrop {
        public void add(LootContext var1, Consumer<ItemStack> var2);
    }

    public static enum EntityTarget {
        THIS("this", LootContextParams.THIS_ENTITY),
        KILLER("killer", LootContextParams.KILLER_ENTITY),
        DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

        final String name;
        private final LootContextParam<? extends Entity> param;

        private EntityTarget(String $$0, LootContextParam<? extends Entity> $$1) {
            this.name = $$0;
            this.param = $$1;
        }

        public LootContextParam<? extends Entity> getParam() {
            return this.param;
        }

        public static EntityTarget getByName(String $$0) {
            for (EntityTarget $$1 : EntityTarget.values()) {
                if (!$$1.name.equals((Object)$$0)) continue;
                return $$1;
            }
            throw new IllegalArgumentException("Invalid entity target " + $$0);
        }

        public static class Serializer
        extends TypeAdapter<EntityTarget> {
            public void write(JsonWriter $$0, EntityTarget $$1) throws IOException {
                $$0.value($$1.name);
            }

            public EntityTarget read(JsonReader $$0) throws IOException {
                return EntityTarget.getByName($$0.nextString());
            }
        }
    }

    public static class Builder {
        private final ServerLevel level;
        private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
        private final Map<ResourceLocation, DynamicDrop> dynamicDrops = Maps.newHashMap();
        private RandomSource random;
        private float luck;

        public Builder(ServerLevel $$0) {
            this.level = $$0;
        }

        public Builder withRandom(RandomSource $$0) {
            this.random = $$0;
            return this;
        }

        public Builder withOptionalRandomSeed(long $$0) {
            if ($$0 != 0L) {
                this.random = RandomSource.create($$0);
            }
            return this;
        }

        public Builder withOptionalRandomSeed(long $$0, RandomSource $$1) {
            this.random = $$0 == 0L ? $$1 : RandomSource.create($$0);
            return this;
        }

        public Builder withLuck(float $$0) {
            this.luck = $$0;
            return this;
        }

        public <T> Builder withParameter(LootContextParam<T> $$0, T $$1) {
            this.params.put($$0, $$1);
            return this;
        }

        public <T> Builder withOptionalParameter(LootContextParam<T> $$0, @Nullable T $$1) {
            if ($$1 == null) {
                this.params.remove($$0);
            } else {
                this.params.put($$0, $$1);
            }
            return this;
        }

        public Builder withDynamicDrop(ResourceLocation $$0, DynamicDrop $$1) {
            DynamicDrop $$2 = (DynamicDrop)this.dynamicDrops.put((Object)$$0, (Object)$$1);
            if ($$2 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
            }
            return this;
        }

        public ServerLevel getLevel() {
            return this.level;
        }

        public <T> T getParameter(LootContextParam<T> $$0) {
            Object $$1 = this.params.get($$0);
            if ($$1 == null) {
                throw new IllegalArgumentException("No parameter " + $$0);
            }
            return (T)$$1;
        }

        @Nullable
        public <T> T getOptionalParameter(LootContextParam<T> $$0) {
            return (T)this.params.get($$0);
        }

        public LootContext create(LootContextParamSet $$0) {
            Sets.SetView $$1 = Sets.difference((Set)this.params.keySet(), $$0.getAllowed());
            if (!$$1.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + (Set)$$1);
            }
            Sets.SetView $$2 = Sets.difference($$0.getRequired(), (Set)this.params.keySet());
            if (!$$2.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + (Set)$$2);
            }
            RandomSource $$3 = this.random;
            if ($$3 == null) {
                $$3 = RandomSource.create();
            }
            MinecraftServer $$4 = this.level.getServer();
            return new LootContext($$3, this.luck, this.level, (Function<ResourceLocation, LootTable>)((Function)$$4.getLootTables()::get), (Function<ResourceLocation, LootItemCondition>)((Function)$$4.getPredicateManager()::get), this.params, this.dynamicDrops);
        }
    }
}