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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public final class BinomialDistributionGenerator
implements NumberProvider {
    final NumberProvider n;
    final NumberProvider p;

    BinomialDistributionGenerator(NumberProvider $$0, NumberProvider $$1) {
        this.n = $$0;
        this.p = $$1;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.BINOMIAL;
    }

    @Override
    public int getInt(LootContext $$0) {
        int $$1 = this.n.getInt($$0);
        float $$2 = this.p.getFloat($$0);
        RandomSource $$3 = $$0.getRandom();
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$1; ++$$5) {
            if (!($$3.nextFloat() < $$2)) continue;
            ++$$4;
        }
        return $$4;
    }

    @Override
    public float getFloat(LootContext $$0) {
        return this.getInt($$0);
    }

    public static BinomialDistributionGenerator binomial(int $$0, float $$1) {
        return new BinomialDistributionGenerator(ConstantValue.exactly($$0), ConstantValue.exactly($$1));
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union((Set)this.n.getReferencedContextParams(), (Set)this.p.getReferencedContextParams());
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<BinomialDistributionGenerator> {
        @Override
        public BinomialDistributionGenerator deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            NumberProvider $$2 = GsonHelper.getAsObject($$0, "n", $$1, NumberProvider.class);
            NumberProvider $$3 = GsonHelper.getAsObject($$0, "p", $$1, NumberProvider.class);
            return new BinomialDistributionGenerator($$2, $$3);
        }

        @Override
        public void serialize(JsonObject $$0, BinomialDistributionGenerator $$1, JsonSerializationContext $$2) {
            $$0.add("n", $$2.serialize((Object)$$1.n));
            $$0.add("p", $$2.serialize((Object)$$1.p));
        }
    }
}