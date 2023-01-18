/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;

public class StorageNbtProvider
implements NbtProvider {
    final ResourceLocation id;

    StorageNbtProvider(ResourceLocation $$0) {
        this.id = $$0;
    }

    @Override
    public LootNbtProviderType getType() {
        return NbtProviders.STORAGE;
    }

    @Override
    @Nullable
    public Tag get(LootContext $$0) {
        return $$0.getLevel().getServer().getCommandStorage().get(this.id);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<StorageNbtProvider> {
        @Override
        public void serialize(JsonObject $$0, StorageNbtProvider $$1, JsonSerializationContext $$2) {
            $$0.addProperty("source", $$1.id.toString());
        }

        @Override
        public StorageNbtProvider deserialize(JsonObject $$0, JsonDeserializationContext $$1) {
            String $$2 = GsonHelper.getAsString($$0, "source");
            return new StorageNbtProvider(new ResourceLocation($$2));
        }
    }
}