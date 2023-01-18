/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.util.Set
 *  java.util.function.Function
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SetNameFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Component name;
    @Nullable
    final LootContext.EntityTarget resolutionContext;

    SetNameFunction(LootItemCondition[] $$0, @Nullable Component $$1, @Nullable LootContext.EntityTarget $$2) {
        super($$0);
        this.name = $$1;
        this.resolutionContext = $$2;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_NAME;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
    }

    public static UnaryOperator<Component> createResolver(LootContext $$02, @Nullable LootContext.EntityTarget $$1) {
        Entity $$22;
        if ($$1 != null && ($$22 = $$02.getParamOrNull($$1.getParam())) != null) {
            CommandSourceStack $$3 = $$22.createCommandSourceStack().withPermission(2);
            return $$2 -> {
                try {
                    return ComponentUtils.updateForEntity($$3, $$2, $$22, 0);
                }
                catch (CommandSyntaxException $$3) {
                    LOGGER.warn("Failed to resolve text component", (Throwable)$$3);
                    return $$2;
                }
            };
        }
        return $$0 -> $$0;
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$1) {
        if (this.name != null) {
            $$0.setHoverName((Component)SetNameFunction.createResolver($$1, this.resolutionContext).apply((Object)this.name));
        }
        return $$0;
    }

    public static LootItemConditionalFunction.Builder<?> setName(Component $$0) {
        return SetNameFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$1 -> new SetNameFunction((LootItemCondition[])$$1, $$0, null)));
    }

    public static LootItemConditionalFunction.Builder<?> setName(Component $$0, LootContext.EntityTarget $$1) {
        return SetNameFunction.simpleBuilder((Function<LootItemCondition[], LootItemFunction>)((Function)$$2 -> new SetNameFunction((LootItemCondition[])$$2, $$0, $$1)));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetNameFunction> {
        @Override
        public void serialize(JsonObject $$0, SetNameFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
            if ($$1.name != null) {
                $$0.add("name", Component.Serializer.toJsonTree($$1.name));
            }
            if ($$1.resolutionContext != null) {
                $$0.add("entity", $$2.serialize((Object)$$1.resolutionContext));
            }
        }

        @Override
        public SetNameFunction deserialize(JsonObject $$0, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
            MutableComponent $$3 = Component.Serializer.fromJson($$0.get("name"));
            LootContext.EntityTarget $$4 = GsonHelper.getAsObject($$0, "entity", null, $$1, LootContext.EntityTarget.class);
            return new SetNameFunction($$2, $$3, $$4);
        }
    }
}