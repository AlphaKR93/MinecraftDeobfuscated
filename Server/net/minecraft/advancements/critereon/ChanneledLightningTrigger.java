/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        EntityPredicate.Composite[] $$3 = EntityPredicate.Composite.fromJsonArray($$0, "victims", $$2);
        return new TriggerInstance($$1, $$3);
    }

    @Override
    public void trigger(ServerPlayer $$0, Collection<? extends Entity> $$12) {
        List $$2 = (List)$$12.stream().map($$1 -> EntityPredicate.createContext($$0, $$1)).collect(Collectors.toList());
        this.trigger($$0, $$1 -> $$1.matches((Collection<? extends LootContext>)$$2));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite[] victims;

        public TriggerInstance(EntityPredicate.Composite $$0, EntityPredicate.Composite[] $$1) {
            super(ID, $$0);
            this.victims = $$1;
        }

        public static TriggerInstance channeledLightning(EntityPredicate ... $$0) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, (EntityPredicate.Composite[])Stream.of((Object[])$$0).map(EntityPredicate.Composite::wrap).toArray(EntityPredicate.Composite[]::new));
        }

        public boolean matches(Collection<? extends LootContext> $$0) {
            for (EntityPredicate.Composite $$1 : this.victims) {
                boolean $$2 = false;
                for (LootContext $$3 : $$0) {
                    if (!$$1.matches($$3)) continue;
                    $$2 = true;
                    break;
                }
                if ($$2) continue;
                return false;
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            $$1.add("victims", EntityPredicate.Composite.toJson(this.victims, $$0));
            return $$1;
        }
    }
}