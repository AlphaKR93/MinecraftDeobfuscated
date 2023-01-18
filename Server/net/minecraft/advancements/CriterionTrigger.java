/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
    public ResourceLocation getId();

    public void addPlayerListener(PlayerAdvancements var1, Listener<T> var2);

    public void removePlayerListener(PlayerAdvancements var1, Listener<T> var2);

    public void removePlayerListeners(PlayerAdvancements var1);

    public T createInstance(JsonObject var1, DeserializationContext var2);

    public static class Listener<T extends CriterionTriggerInstance> {
        private final T trigger;
        private final Advancement advancement;
        private final String criterion;

        public Listener(T $$0, Advancement $$1, String $$2) {
            this.trigger = $$0;
            this.advancement = $$1;
            this.criterion = $$2;
        }

        public T getTriggerInstance() {
            return this.trigger;
        }

        public void run(PlayerAdvancements $$0) {
            $$0.award(this.advancement, this.criterion);
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            Listener $$1 = (Listener)$$0;
            if (!this.trigger.equals($$1.trigger)) {
                return false;
            }
            if (!this.advancement.equals($$1.advancement)) {
                return false;
            }
            return this.criterion.equals((Object)$$1.criterion);
        }

        public int hashCode() {
            int $$0 = this.trigger.hashCode();
            $$0 = 31 * $$0 + this.advancement.hashCode();
            $$0 = 31 * $$0 + this.criterion.hashCode();
            return $$0;
        }
    }
}