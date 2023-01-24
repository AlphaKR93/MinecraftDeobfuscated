/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NbtPredicate {
    public static final NbtPredicate ANY = new NbtPredicate(null);
    @Nullable
    private final CompoundTag tag;

    public NbtPredicate(@Nullable CompoundTag $$0) {
        this.tag = $$0;
    }

    public boolean matches(ItemStack $$0) {
        if (this == ANY) {
            return true;
        }
        return this.matches($$0.getTag());
    }

    public boolean matches(Entity $$0) {
        if (this == ANY) {
            return true;
        }
        return this.matches(NbtPredicate.getEntityTagToCompare($$0));
    }

    public boolean matches(@Nullable Tag $$0) {
        if ($$0 == null) {
            return this == ANY;
        }
        return this.tag == null || NbtUtils.compareNbt(this.tag, $$0, true);
    }

    public JsonElement serializeToJson() {
        if (this == ANY || this.tag == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(this.tag.toString());
    }

    /*
     * WARNING - void declaration
     */
    public static NbtPredicate fromJson(@Nullable JsonElement $$0) {
        void $$3;
        if ($$0 == null || $$0.isJsonNull()) {
            return ANY;
        }
        try {
            CompoundTag $$1 = TagParser.parseTag(GsonHelper.convertToString($$0, "nbt"));
        }
        catch (CommandSyntaxException $$2) {
            throw new JsonSyntaxException("Invalid nbt tag: " + $$2.getMessage());
        }
        return new NbtPredicate((CompoundTag)$$3);
    }

    public static CompoundTag getEntityTagToCompare(Entity $$0) {
        ItemStack $$2;
        CompoundTag $$1 = $$0.saveWithoutId(new CompoundTag());
        if ($$0 instanceof Player && !($$2 = ((Player)$$0).getInventory().getSelected()).isEmpty()) {
            $$1.put("SelectedItem", $$2.save(new CompoundTag()));
        }
        return $$1;
    }
}