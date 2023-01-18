/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("brewed_potion");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject $$0, EntityPredicate.Composite $$1, DeserializationContext $$2) {
        Potion $$3 = null;
        if ($$0.has("potion")) {
            ResourceLocation $$4 = new ResourceLocation(GsonHelper.getAsString($$0, "potion"));
            $$3 = (Potion)BuiltInRegistries.POTION.getOptional($$4).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + $$4 + "'"));
        }
        return new TriggerInstance($$1, $$3);
    }

    public void trigger(ServerPlayer $$0, Potion $$12) {
        this.trigger($$0, $$1 -> $$1.matches($$12));
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Potion potion;

        public TriggerInstance(EntityPredicate.Composite $$0, @Nullable Potion $$1) {
            super(ID, $$0);
            this.potion = $$1;
        }

        public static TriggerInstance brewedPotion() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, null);
        }

        public boolean matches(Potion $$0) {
            return this.potion == null || this.potion == $$0;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext $$0) {
            JsonObject $$1 = super.serializeToJson($$0);
            if (this.potion != null) {
                $$1.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
            }
            return $$1;
        }
    }
}