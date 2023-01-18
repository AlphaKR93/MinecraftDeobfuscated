/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.UnsupportedOperationException
 *  java.util.Map
 *  java.util.UUID
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeSupplier {
    private final Map<Attribute, AttributeInstance> instances;

    public AttributeSupplier(Map<Attribute, AttributeInstance> $$0) {
        this.instances = ImmutableMap.copyOf($$0);
    }

    private AttributeInstance getAttributeInstance(Attribute $$0) {
        AttributeInstance $$1 = (AttributeInstance)this.instances.get((Object)$$0);
        if ($$1 == null) {
            throw new IllegalArgumentException("Can't find attribute " + BuiltInRegistries.ATTRIBUTE.getKey($$0));
        }
        return $$1;
    }

    public double getValue(Attribute $$0) {
        return this.getAttributeInstance($$0).getValue();
    }

    public double getBaseValue(Attribute $$0) {
        return this.getAttributeInstance($$0).getBaseValue();
    }

    public double getModifierValue(Attribute $$0, UUID $$1) {
        AttributeModifier $$2 = this.getAttributeInstance($$0).getModifier($$1);
        if ($$2 == null) {
            throw new IllegalArgumentException("Can't find modifier " + $$1 + " on attribute " + BuiltInRegistries.ATTRIBUTE.getKey($$0));
        }
        return $$2.getAmount();
    }

    @Nullable
    public AttributeInstance createInstance(Consumer<AttributeInstance> $$0, Attribute $$1) {
        AttributeInstance $$2 = (AttributeInstance)this.instances.get((Object)$$1);
        if ($$2 == null) {
            return null;
        }
        AttributeInstance $$3 = new AttributeInstance($$1, $$0);
        $$3.replaceFrom($$2);
        return $$3;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasAttribute(Attribute $$0) {
        return this.instances.containsKey((Object)$$0);
    }

    public boolean hasModifier(Attribute $$0, UUID $$1) {
        AttributeInstance $$2 = (AttributeInstance)this.instances.get((Object)$$0);
        return $$2 != null && $$2.getModifier($$1) != null;
    }

    public static class Builder {
        private final Map<Attribute, AttributeInstance> builder = Maps.newHashMap();
        private boolean instanceFrozen;

        private AttributeInstance create(Attribute $$0) {
            AttributeInstance $$12 = new AttributeInstance($$0, (Consumer<AttributeInstance>)((Consumer)$$1 -> {
                if (this.instanceFrozen) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + BuiltInRegistries.ATTRIBUTE.getKey($$0));
                }
            }));
            this.builder.put((Object)$$0, (Object)$$12);
            return $$12;
        }

        public Builder add(Attribute $$0) {
            this.create($$0);
            return this;
        }

        public Builder add(Attribute $$0, double $$1) {
            AttributeInstance $$2 = this.create($$0);
            $$2.setBaseValue($$1);
            return this;
        }

        public AttributeSupplier build() {
            this.instanceFrozen = true;
            return new AttributeSupplier(this.builder);
        }
    }
}