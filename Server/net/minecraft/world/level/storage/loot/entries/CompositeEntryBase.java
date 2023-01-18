/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase
extends LootPoolEntryContainer {
    protected final LootPoolEntryContainer[] children;
    private final ComposableEntryContainer composedChildren;

    protected CompositeEntryBase(LootPoolEntryContainer[] $$0, LootItemCondition[] $$1) {
        super($$1);
        this.children = $$0;
        this.composedChildren = this.compose($$0);
    }

    @Override
    public void validate(ValidationContext $$0) {
        super.validate($$0);
        if (this.children.length == 0) {
            $$0.reportProblem("Empty children list");
        }
        for (int $$1 = 0; $$1 < this.children.length; ++$$1) {
            this.children[$$1].validate($$0.forChild(".entry[" + $$1 + "]"));
        }
    }

    protected abstract ComposableEntryContainer compose(ComposableEntryContainer[] var1);

    @Override
    public final boolean expand(LootContext $$0, Consumer<LootPoolEntry> $$1) {
        if (!this.canRun($$0)) {
            return false;
        }
        return this.composedChildren.expand($$0, $$1);
    }

    public static <T extends CompositeEntryBase> LootPoolEntryContainer.Serializer<T> createSerializer(final CompositeEntryConstructor<T> $$0) {
        return new LootPoolEntryContainer.Serializer<T>(){

            @Override
            public void serialize(JsonObject $$02, T $$1, JsonSerializationContext $$2) {
                $$02.add("children", $$2.serialize((Object)((CompositeEntryBase)$$1).children));
            }

            @Override
            public final T deserializeCustom(JsonObject $$02, JsonDeserializationContext $$1, LootItemCondition[] $$2) {
                LootPoolEntryContainer[] $$3 = GsonHelper.getAsObject($$02, "children", $$1, LootPoolEntryContainer[].class);
                return $$0.create($$3, $$2);
            }
        };
    }

    @FunctionalInterface
    public static interface CompositeEntryConstructor<T extends CompositeEntryBase> {
        public T create(LootPoolEntryContainer[] var1, LootItemCondition[] var2);
    }
}