/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class UniformGenerator
implements NumberProvider {
    final NumberProvider min;
    final NumberProvider max;

    UniformGenerator(NumberProvider $$0, NumberProvider $$1) {
        this.min = $$0;
        this.max = $$1;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.UNIFORM;
    }

    public static UniformGenerator between(float $$0, float $$1) {
        return new UniformGenerator(ConstantValue.exactly($$0), ConstantValue.exactly($$1));
    }

    @Override
    public int getInt(LootContext $$0) {
        return Mth.nextInt($$0.getRandom(), this.min.getInt($$0), this.max.getInt($$0));
    }

    @Override
    public float getFloat(LootContext $$0) {
        return Mth.nextFloat($$0.getRandom(), this.min.getFloat($$0), this.max.getFloat($$0));
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union((Set)this.min.getReferencedContextParams(), (Set)this.max.getReferencedContextParams());
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<UniformGenerator> {
        @Override
        public UniformGenerator deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            NumberProvider $$2 = GsonHelper.getAsObject($$0, "min", $$1, NumberProvider.class);
            NumberProvider $$3 = GsonHelper.getAsObject($$0, "max", $$1, NumberProvider.class);
            return new UniformGenerator($$2, $$3);
        }

        @Override
        public void serialize(JsonObject $$0, UniformGenerator $$1, JsonSerializationContext $$2) {
            $$0.add("min", $$2.serialize((Object)$$1.min));
            $$0.add("max", $$2.serialize((Object)$$1.max));
        }
    }
}