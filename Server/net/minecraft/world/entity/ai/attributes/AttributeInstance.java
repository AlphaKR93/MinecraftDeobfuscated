/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Map
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeInstance {
    private final Attribute attribute;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> modifiersByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> modifierById = new Object2ObjectArrayMap();
    private final Set<AttributeModifier> permanentModifiers = new ObjectArraySet();
    private double baseValue;
    private boolean dirty = true;
    private double cachedValue;
    private final Consumer<AttributeInstance> onDirty;

    public AttributeInstance(Attribute $$0, Consumer<AttributeInstance> $$1) {
        this.attribute = $$0;
        this.onDirty = $$1;
        this.baseValue = $$0.getDefaultValue();
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double $$0) {
        if ($$0 == this.baseValue) {
            return;
        }
        this.baseValue = $$0;
        this.setDirty();
    }

    public Set<AttributeModifier> getModifiers(AttributeModifier.Operation $$02) {
        return (Set)this.modifiersByOperation.computeIfAbsent((Object)$$02, $$0 -> Sets.newHashSet());
    }

    public Set<AttributeModifier> getModifiers() {
        return ImmutableSet.copyOf((Collection)this.modifierById.values());
    }

    @Nullable
    public AttributeModifier getModifier(UUID $$0) {
        return (AttributeModifier)this.modifierById.get((Object)$$0);
    }

    public boolean hasModifier(AttributeModifier $$0) {
        return this.modifierById.get((Object)$$0.getId()) != null;
    }

    private void addModifier(AttributeModifier $$0) {
        AttributeModifier $$1 = (AttributeModifier)this.modifierById.putIfAbsent((Object)$$0.getId(), (Object)$$0);
        if ($$1 != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        this.getModifiers($$0.getOperation()).add((Object)$$0);
        this.setDirty();
    }

    public void addTransientModifier(AttributeModifier $$0) {
        this.addModifier($$0);
    }

    public void addPermanentModifier(AttributeModifier $$0) {
        this.addModifier($$0);
        this.permanentModifiers.add((Object)$$0);
    }

    protected void setDirty() {
        this.dirty = true;
        this.onDirty.accept((Object)this);
    }

    public void removeModifier(AttributeModifier $$0) {
        this.getModifiers($$0.getOperation()).remove((Object)$$0);
        this.modifierById.remove((Object)$$0.getId());
        this.permanentModifiers.remove((Object)$$0);
        this.setDirty();
    }

    public void removeModifier(UUID $$0) {
        AttributeModifier $$1 = this.getModifier($$0);
        if ($$1 != null) {
            this.removeModifier($$1);
        }
    }

    public boolean removePermanentModifier(UUID $$0) {
        AttributeModifier $$1 = this.getModifier($$0);
        if ($$1 != null && this.permanentModifiers.contains((Object)$$1)) {
            this.removeModifier($$1);
            return true;
        }
        return false;
    }

    public void removeModifiers() {
        for (AttributeModifier $$0 : this.getModifiers()) {
            this.removeModifier($$0);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.cachedValue = this.calculateValue();
            this.dirty = false;
        }
        return this.cachedValue;
    }

    private double calculateValue() {
        double $$0 = this.getBaseValue();
        for (AttributeModifier $$1 : this.getModifiersOrEmpty(AttributeModifier.Operation.ADDITION)) {
            $$0 += $$1.getAmount();
        }
        double $$2 = $$0;
        for (AttributeModifier $$3 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_BASE)) {
            $$2 += $$0 * $$3.getAmount();
        }
        for (AttributeModifier $$4 : this.getModifiersOrEmpty(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            $$2 *= 1.0 + $$4.getAmount();
        }
        return this.attribute.sanitizeValue($$2);
    }

    private Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation $$0) {
        return (Collection)this.modifiersByOperation.getOrDefault((Object)$$0, (Object)Collections.emptySet());
    }

    public void replaceFrom(AttributeInstance $$02) {
        this.baseValue = $$02.baseValue;
        this.modifierById.clear();
        this.modifierById.putAll($$02.modifierById);
        this.permanentModifiers.clear();
        this.permanentModifiers.addAll($$02.permanentModifiers);
        this.modifiersByOperation.clear();
        $$02.modifiersByOperation.forEach(($$0, $$1) -> this.getModifiers((AttributeModifier.Operation)((Object)$$0)).addAll((Collection)$$1));
        this.setDirty();
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("Name", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
        $$0.putDouble("Base", this.baseValue);
        if (!this.permanentModifiers.isEmpty()) {
            ListTag $$1 = new ListTag();
            for (AttributeModifier $$2 : this.permanentModifiers) {
                $$1.add($$2.save());
            }
            $$0.put("Modifiers", $$1);
        }
        return $$0;
    }

    public void load(CompoundTag $$0) {
        this.baseValue = $$0.getDouble("Base");
        if ($$0.contains("Modifiers", 9)) {
            ListTag $$1 = $$0.getList("Modifiers", 10);
            for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                AttributeModifier $$3 = AttributeModifier.load($$1.getCompound($$2));
                if ($$3 == null) continue;
                this.modifierById.put((Object)$$3.getId(), (Object)$$3);
                this.getModifiers($$3.getOperation()).add((Object)$$3);
                this.permanentModifiers.add((Object)$$3);
            }
        }
        this.setDirty();
    }
}