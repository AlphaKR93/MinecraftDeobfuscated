/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite[] $$3 = EntityPredicate.Composite.fromJsonArray($$0, "victims", $$2);
        MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson($$0.get("unique_entity_types"));
        return new TriggerInstance($$1, $$3, $$4);
    }

    @Override
    public void trigger(ServerPlayer $$0, Collection<Entity> $$1) {
        ArrayList $$2 = Lists.newArrayList();
        HashSet $$3 = Sets.newHashSet();
        for (Entity $$4 : $$1) {
            $$3.add($$4.getType());
            $$2.add((Object)EntityPredicate.createContext($$0, $$4));
        }
        this.trigger($$0, arg_0 -> KilledByCrossbowTrigger.lambda$trigger$0((List)$$2, (Set)$$3, arg_0));
    }

    private static /* synthetic */ boolean lambda$trigger$0(List $$0, Set $$1, TriggerInstance $$2) {
        return $$2.matches((Collection<LootContext>)$$0, $$1.size());
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite[] victims;
        private final MinMaxBounds.Ints uniqueEntityTypes;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite[] $$1, MinMaxBounds.Ints $$2) {
            super(ID, $$0);
            this.victims = $$1;
            this.uniqueEntityTypes = $$2;
        }

        public static TriggerInstance crossbowKilled(EntityPredicate.Builder ... $$0) {
            EntityPredicate.Composite[] $$1 = new EntityPredicate.Composite[$$0.length];
            for (int $$2 = 0; $$2 < $$0.length; ++$$2) {
                EntityPredicate.Builder $$3 = $$0[$$2];
                $$1[$$2] = EntityPredicate.Composite.wrap($$3.build());
            }
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$1, MinMaxBounds.Ints.ANY);
        }

        public static TriggerInstance crossbowKilled(MinMaxBounds.Ints $$0) {
            EntityPredicate.Composite[] $$1 = new EntityPredicate.Composite[]{};
            return new TriggerInstance(EntityPredicate.Composite.ANY, $$1, $$0);
        }

        public boolean matches(Collection<LootContext> $$0, int $$1) {
            if (this.victims.length > 0) {
                ArrayList $$2 = Lists.newArrayList($$0);
                for (EntityPredicate.Composite $$3 : this.victims) {
                    boolean $$4 = false;
                    Iterator $$5 = $$2.iterator();
                    while ($$5.hasNext()) {
                        LootContext $$6 = (LootContext)$$5.next();
                        if (!$$3.matches($$6)) continue;
                        $$5.remove();
                        $$4 = true;
                        break;
                    }
                    if ($$4) continue;
                    return false;
                }
            }
            return this.uniqueEntityTypes.matches($$1);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("victims", EntityPredicate.Composite.toJson(this.victims, $$0));
            $$1.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
            return $$1;
        }
    }
}