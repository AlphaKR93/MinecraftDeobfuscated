/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 *  java.util.function.Function
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead
extends LootItemConditionalFunction {
    final LootContext.EntityTarget entityTarget;

    public FillPlayerHead(LootItemCondition[] $$0, LootContext.EntityTarget $$1) {
        super($$0);
        this.entityTarget = $$1;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FILL_PLAYER_HEAD;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.entityTarget.getParam());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        Entity $$2;
        if ($$0.is(Items.PLAYER_HEAD) && ($$2 = $$1.getParamOrNull(this.entityTarget.getParam())) instanceof Player) {
            GameProfile $$3 = ((Player)$$2).getGameProfile();
            $$0.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), $$3));
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> fillPlayerHead(LootContext.EntityTarget $$0) {
        return FillPlayerHead.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new FillPlayerHead((LootItemCondition[])$$1, $$0)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<FillPlayerHead> {
        @Override
        public void serialize(JsonObject $$0, FillPlayerHead $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            $$0.add("entity", $$2.serialize((Object)$$1.entityTarget));
        }

        @Override
        public FillPlayerHead deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            LootContext.EntityTarget $$3 = GsonHelper.getAsObject($$0, "entity", $$1, LootContext.EntityTarget.class);
            return new FillPlayerHead($$2, $$3);
        }
    }
}