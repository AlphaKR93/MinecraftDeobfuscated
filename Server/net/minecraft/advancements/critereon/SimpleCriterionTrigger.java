/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class SimpleCriterionTrigger<T extends AbstractCriterionTriggerInstance>
implements CriterionTrigger<T> {
    private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

    @Override
    public final void addPlayerListener(PlayerAdvancements $$02, CriterionTrigger.Listener<T> $$1) {
        ((Set)this.players.computeIfAbsent((Object)$$02, $$0 -> Sets.newHashSet())).add($$1);
    }

    @Override
    public final void removePlayerListener(PlayerAdvancements $$0, CriterionTrigger.Listener<T> $$1) {
        Set $$2 = (Set)this.players.get((Object)$$0);
        if ($$2 != null) {
            $$2.remove($$1);
            if ($$2.isEmpty()) {
                this.players.remove((Object)$$0);
            }
        }
    }

    @Override
    public final void removePlayerListeners(PlayerAdvancements $$0) {
        this.players.remove((Object)$$0);
    }

    protected abstract T createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3);

    @Override
    public final T createInstance(JsonObject $$0, DeserializationContext $$1) {
        EntityPredicate.Composite $$2 = EntityPredicate.Composite.fromJson($$0, "player", $$1);
        return this.createInstance($$0, $$2, $$1);
    }

    protected void trigger(ServerPlayer $$0, Predicate<T> $$1) {
        PlayerAdvancements $$2 = $$0.getAdvancements();
        Set $$3 = (Set)this.players.get((Object)$$2);
        if ($$3 == null || $$3.isEmpty()) {
            return;
        }
        LootContext $$4 = EntityPredicate.createContext($$0, $$0);
        ArrayList $$5 = null;
        for (CriterionTrigger.Listener $$6 : $$3) {
            AbstractCriterionTriggerInstance $$7 = (AbstractCriterionTriggerInstance)$$6.getTriggerInstance();
            if (!$$1.test((Object)$$7) || !$$7.getPlayerPredicate().matches($$4)) continue;
            if ($$5 == null) {
                $$5 = Lists.newArrayList();
            }
            $$5.add((Object)$$6);
        }
        if ($$5 != null) {
            for (CriterionTrigger.Listener $$8 : $$5) {
                $$8.run($$2);
            }
        }
    }
}