/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.slf4j.Logger;

public class AttributeMap {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<Attribute, AttributeInstance> attributes = Maps.newHashMap();
    private final Set<AttributeInstance> dirtyAttributes = Sets.newHashSet();
    private final AttributeSupplier supplier;

    public AttributeMap(AttributeSupplier $$0) {
        this.supplier = $$0;
    }

    private void onAttributeModified(AttributeInstance $$0) {
        if ($$0.getAttribute().isClientSyncable()) {
            this.dirtyAttributes.add((Object)$$0);
        }
    }

    public Set<AttributeInstance> getDirtyAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeInstance> getSyncableAttributes() {
        return (Collection)this.attributes.values().stream().filter($$0 -> $$0.getAttribute().isClientSyncable()).collect(Collectors.toList());
    }

    @Nullable
    public AttributeInstance getInstance(Attribute $$02) {
        return (AttributeInstance)this.attributes.computeIfAbsent((Object)$$02, $$0 -> this.supplier.createInstance((Consumer<AttributeInstance>)((Consumer)this::onAttributeModified), (Attribute)$$0));
    }

    @Nullable
    public AttributeInstance getInstance(Holder<Attribute> $$0) {
        return this.getInstance($$0.value());
    }

    public boolean hasAttribute(Attribute $$0) {
        return this.attributes.get((Object)$$0) != null || this.supplier.hasAttribute($$0);
    }

    public boolean hasAttribute(Holder<Attribute> $$0) {
        return this.hasAttribute($$0.value());
    }

    public boolean hasModifier(Attribute $$0, UUID $$1) {
        AttributeInstance $$2 = (AttributeInstance)this.attributes.get((Object)$$0);
        return $$2 != null ? $$2.getModifier($$1) != null : this.supplier.hasModifier($$0, $$1);
    }

    public boolean hasModifier(Holder<Attribute> $$0, UUID $$1) {
        return this.hasModifier($$0.value(), $$1);
    }

    public double getValue(Attribute $$0) {
        AttributeInstance $$1 = (AttributeInstance)this.attributes.get((Object)$$0);
        return $$1 != null ? $$1.getValue() : this.supplier.getValue($$0);
    }

    public double getBaseValue(Attribute $$0) {
        AttributeInstance $$1 = (AttributeInstance)this.attributes.get((Object)$$0);
        return $$1 != null ? $$1.getBaseValue() : this.supplier.getBaseValue($$0);
    }

    public double getModifierValue(Attribute $$0, UUID $$1) {
        AttributeInstance $$2 = (AttributeInstance)this.attributes.get((Object)$$0);
        return $$2 != null ? $$2.getModifier($$1).getAmount() : this.supplier.getModifierValue($$0, $$1);
    }

    public double getModifierValue(Holder<Attribute> $$0, UUID $$1) {
        return this.getModifierValue($$0.value(), $$1);
    }

    public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> $$02) {
        $$02.asMap().forEach(($$0, $$1) -> {
            AttributeInstance $$2 = (AttributeInstance)this.attributes.get($$0);
            if ($$2 != null) {
                $$1.forEach($$2::removeModifier);
            }
        });
    }

    public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> $$02) {
        $$02.forEach(($$0, $$1) -> {
            AttributeInstance $$2 = this.getInstance((Attribute)$$0);
            if ($$2 != null) {
                $$2.removeModifier((AttributeModifier)$$1);
                $$2.addTransientModifier((AttributeModifier)$$1);
            }
        });
    }

    public void assignValues(AttributeMap $$02) {
        $$02.attributes.values().forEach($$0 -> {
            AttributeInstance $$1 = this.getInstance($$0.getAttribute());
            if ($$1 != null) {
                $$1.replaceFrom((AttributeInstance)$$0);
            }
        });
    }

    public ListTag save() {
        ListTag $$0 = new ListTag();
        for (AttributeInstance $$1 : this.attributes.values()) {
            $$0.add($$1.save());
        }
        return $$0;
    }

    public void load(ListTag $$0) {
        for (int $$12 = 0; $$12 < $$0.size(); ++$$12) {
            CompoundTag $$2 = $$0.getCompound($$12);
            String $$3 = $$2.getString("Name");
            Util.ifElse(BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse($$3)), $$1 -> {
                AttributeInstance $$2 = this.getInstance((Attribute)$$1);
                if ($$2 != null) {
                    $$2.load($$2);
                }
            }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", (Object)$$3));
        }
    }
}