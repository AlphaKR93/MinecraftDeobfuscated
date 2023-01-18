/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.String
 *  java.util.EnumMap
 *  java.util.Map
 */
package net.minecraft.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.RecipeBookType;

public final class RecipeBookSettings {
    private static final Map<RecipeBookType, Pair<String, String>> TAG_FIELDS = ImmutableMap.of((Object)((Object)RecipeBookType.CRAFTING), (Object)Pair.of((Object)"isGuiOpen", (Object)"isFilteringCraftable"), (Object)((Object)RecipeBookType.FURNACE), (Object)Pair.of((Object)"isFurnaceGuiOpen", (Object)"isFurnaceFilteringCraftable"), (Object)((Object)RecipeBookType.BLAST_FURNACE), (Object)Pair.of((Object)"isBlastingFurnaceGuiOpen", (Object)"isBlastingFurnaceFilteringCraftable"), (Object)((Object)RecipeBookType.SMOKER), (Object)Pair.of((Object)"isSmokerGuiOpen", (Object)"isSmokerFilteringCraftable"));
    private final Map<RecipeBookType, TypeSettings> states;

    private RecipeBookSettings(Map<RecipeBookType, TypeSettings> $$0) {
        this.states = $$0;
    }

    public RecipeBookSettings() {
        this((Map<RecipeBookType, TypeSettings>)((Map)Util.make(Maps.newEnumMap(RecipeBookType.class), $$0 -> {
            for (RecipeBookType $$1 : RecipeBookType.values()) {
                $$0.put((Enum)$$1, (Object)new TypeSettings(false, false));
            }
        })));
    }

    public boolean isOpen(RecipeBookType $$0) {
        return ((TypeSettings)this.states.get((Object)((Object)$$0))).open;
    }

    public void setOpen(RecipeBookType $$0, boolean $$1) {
        ((TypeSettings)this.states.get((Object)((Object)$$0))).open = $$1;
    }

    public boolean isFiltering(RecipeBookType $$0) {
        return ((TypeSettings)this.states.get((Object)((Object)$$0))).filtering;
    }

    public void setFiltering(RecipeBookType $$0, boolean $$1) {
        ((TypeSettings)this.states.get((Object)((Object)$$0))).filtering = $$1;
    }

    public static RecipeBookSettings read(FriendlyByteBuf $$0) {
        EnumMap $$1 = Maps.newEnumMap(RecipeBookType.class);
        for (RecipeBookType $$2 : RecipeBookType.values()) {
            boolean $$3 = $$0.readBoolean();
            boolean $$4 = $$0.readBoolean();
            $$1.put((Object)$$2, (Object)new TypeSettings($$3, $$4));
        }
        return new RecipeBookSettings((Map<RecipeBookType, TypeSettings>)$$1);
    }

    public void write(FriendlyByteBuf $$0) {
        for (RecipeBookType $$1 : RecipeBookType.values()) {
            TypeSettings $$2 = (TypeSettings)this.states.get((Object)$$1);
            if ($$2 == null) {
                $$0.writeBoolean(false);
                $$0.writeBoolean(false);
                continue;
            }
            $$0.writeBoolean($$2.open);
            $$0.writeBoolean($$2.filtering);
        }
    }

    public static RecipeBookSettings read(CompoundTag $$0) {
        EnumMap $$1 = Maps.newEnumMap(RecipeBookType.class);
        TAG_FIELDS.forEach((arg_0, arg_1) -> RecipeBookSettings.lambda$read$1($$0, (Map)$$1, arg_0, arg_1));
        return new RecipeBookSettings((Map<RecipeBookType, TypeSettings>)$$1);
    }

    public void write(CompoundTag $$0) {
        TAG_FIELDS.forEach(($$1, $$2) -> {
            TypeSettings $$3 = (TypeSettings)this.states.get((Object)$$1);
            $$0.putBoolean((String)$$2.getFirst(), $$3.open);
            $$0.putBoolean((String)$$2.getSecond(), $$3.filtering);
        });
    }

    public RecipeBookSettings copy() {
        EnumMap $$0 = Maps.newEnumMap(RecipeBookType.class);
        for (RecipeBookType $$1 : RecipeBookType.values()) {
            TypeSettings $$2 = (TypeSettings)this.states.get((Object)$$1);
            $$0.put((Object)$$1, (Object)$$2.copy());
        }
        return new RecipeBookSettings((Map<RecipeBookType, TypeSettings>)$$0);
    }

    public void replaceFrom(RecipeBookSettings $$0) {
        this.states.clear();
        for (RecipeBookType $$1 : RecipeBookType.values()) {
            TypeSettings $$2 = (TypeSettings)$$0.states.get((Object)$$1);
            this.states.put((Object)$$1, (Object)$$2.copy());
        }
    }

    public boolean equals(Object $$0) {
        return this == $$0 || $$0 instanceof RecipeBookSettings && this.states.equals(((RecipeBookSettings)$$0).states);
    }

    public int hashCode() {
        return this.states.hashCode();
    }

    private static /* synthetic */ void lambda$read$1(CompoundTag $$0, Map $$1, RecipeBookType $$2, Pair $$3) {
        boolean $$4 = $$0.getBoolean((String)$$3.getFirst());
        boolean $$5 = $$0.getBoolean((String)$$3.getSecond());
        $$1.put((Object)$$2, (Object)new TypeSettings($$4, $$5));
    }

    static final class TypeSettings {
        boolean open;
        boolean filtering;

        public TypeSettings(boolean $$0, boolean $$1) {
            this.open = $$0;
            this.filtering = $$1;
        }

        public TypeSettings copy() {
            return new TypeSettings(this.open, this.filtering);
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 instanceof TypeSettings) {
                TypeSettings $$1 = (TypeSettings)$$0;
                return this.open == $$1.open && this.filtering == $$1.filtering;
            }
            return false;
        }

        public int hashCode() {
            int $$0 = this.open ? 1 : 0;
            $$0 = 31 * $$0 + (this.filtering ? 1 : 0);
            return $$0;
        }

        public String toString() {
            return "[open=" + this.open + ", filtering=" + this.filtering + "]";
        }
    }
}