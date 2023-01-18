/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.UUID
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class HoverEvent {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Action<?> action;
    private final Object value;

    public <T> HoverEvent(Action<T> $$0, T $$1) {
        this.action = $$0;
        this.value = $$1;
    }

    public Action<?> getAction() {
        return this.action;
    }

    @Nullable
    public <T> T getValue(Action<T> $$0) {
        if (this.action == $$0) {
            return $$0.cast(this.value);
        }
        return null;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        HoverEvent $$1 = (HoverEvent)$$0;
        return this.action == $$1.action && Objects.equals((Object)this.value, (Object)$$1.value);
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int $$0 = this.action.hashCode();
        $$0 = 31 * $$0 + (this.value != null ? this.value.hashCode() : 0);
        return $$0;
    }

    @Nullable
    public static HoverEvent deserialize(JsonObject $$0) {
        String $$1 = GsonHelper.getAsString($$0, "action", null);
        if ($$1 == null) {
            return null;
        }
        Action<?> $$2 = Action.getByName($$1);
        if ($$2 == null) {
            return null;
        }
        JsonElement $$3 = $$0.get("contents");
        if ($$3 != null) {
            return $$2.deserialize($$3);
        }
        MutableComponent $$4 = Component.Serializer.fromJson($$0.get("value"));
        if ($$4 != null) {
            return $$2.deserializeFromLegacy($$4);
        }
        return null;
    }

    public JsonObject serialize() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("action", this.action.getName());
        $$0.add("contents", this.action.serializeArg(this.value));
        return $$0;
    }

    public static class Action<T> {
        public static final Action<Component> SHOW_TEXT = new Action("show_text", true, Component.Serializer::fromJson, Component.Serializer::toJsonTree, Function.identity());
        public static final Action<ItemStackInfo> SHOW_ITEM = new Action("show_item", true, ItemStackInfo::create, ItemStackInfo::serialize, ItemStackInfo::create);
        public static final Action<EntityTooltipInfo> SHOW_ENTITY = new Action("show_entity", true, EntityTooltipInfo::create, EntityTooltipInfo::serialize, EntityTooltipInfo::create);
        private static final Map<String, Action<?>> LOOKUP = (Map)Stream.of((Object[])new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY}).collect(ImmutableMap.toImmutableMap(Action::getName, $$0 -> $$0));
        private final String name;
        private final boolean allowFromServer;
        private final Function<JsonElement, T> argDeserializer;
        private final Function<T, JsonElement> argSerializer;
        private final Function<Component, T> legacyArgDeserializer;

        public Action(String $$0, boolean $$1, Function<JsonElement, T> $$2, Function<T, JsonElement> $$3, Function<Component, T> $$4) {
            this.name = $$0;
            this.allowFromServer = $$1;
            this.argDeserializer = $$2;
            this.argSerializer = $$3;
            this.legacyArgDeserializer = $$4;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Action<?> getByName(String $$0) {
            return (Action)LOOKUP.get((Object)$$0);
        }

        T cast(Object $$0) {
            return (T)$$0;
        }

        @Nullable
        public HoverEvent deserialize(JsonElement $$0) {
            Object $$1 = this.argDeserializer.apply((Object)$$0);
            if ($$1 == null) {
                return null;
            }
            return new HoverEvent(this, $$1);
        }

        @Nullable
        public HoverEvent deserializeFromLegacy(Component $$0) {
            Object $$1 = this.legacyArgDeserializer.apply((Object)$$0);
            if ($$1 == null) {
                return null;
            }
            return new HoverEvent(this, $$1);
        }

        public JsonElement serializeArg(Object $$0) {
            return (JsonElement)this.argSerializer.apply(this.cast($$0));
        }

        public String toString() {
            return "<action " + this.name + ">";
        }
    }

    public static class ItemStackInfo {
        private final Item item;
        private final int count;
        @Nullable
        private final CompoundTag tag;
        @Nullable
        private ItemStack itemStack;

        ItemStackInfo(Item $$0, int $$1, @Nullable CompoundTag $$2) {
            this.item = $$0;
            this.count = $$1;
            this.tag = $$2;
        }

        public ItemStackInfo(ItemStack $$0) {
            this($$0.getItem(), $$0.getCount(), $$0.getTag() != null ? $$0.getTag().copy() : null);
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            ItemStackInfo $$1 = (ItemStackInfo)$$0;
            return this.count == $$1.count && this.item.equals($$1.item) && Objects.equals((Object)this.tag, (Object)$$1.tag);
        }

        public int hashCode() {
            int $$0 = this.item.hashCode();
            $$0 = 31 * $$0 + this.count;
            $$0 = 31 * $$0 + (this.tag != null ? this.tag.hashCode() : 0);
            return $$0;
        }

        public ItemStack getItemStack() {
            if (this.itemStack == null) {
                this.itemStack = new ItemStack(this.item, this.count);
                if (this.tag != null) {
                    this.itemStack.setTag(this.tag);
                }
            }
            return this.itemStack;
        }

        private static ItemStackInfo create(JsonElement $$0) {
            if ($$0.isJsonPrimitive()) {
                return new ItemStackInfo(BuiltInRegistries.ITEM.get(new ResourceLocation($$0.getAsString())), 1, null);
            }
            JsonObject $$1 = GsonHelper.convertToJsonObject($$0, "item");
            Item $$2 = BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString($$1, "id")));
            int $$3 = GsonHelper.getAsInt($$1, "count", 1);
            if ($$1.has("tag")) {
                String $$4 = GsonHelper.getAsString($$1, "tag");
                try {
                    CompoundTag $$5 = TagParser.parseTag($$4);
                    return new ItemStackInfo($$2, $$3, $$5);
                }
                catch (CommandSyntaxException $$6) {
                    LOGGER.warn("Failed to parse tag: {}", (Object)$$4, (Object)$$6);
                }
            }
            return new ItemStackInfo($$2, $$3, null);
        }

        @Nullable
        private static ItemStackInfo create(Component $$0) {
            try {
                CompoundTag $$1 = TagParser.parseTag($$0.getString());
                return new ItemStackInfo(ItemStack.of($$1));
            }
            catch (CommandSyntaxException $$2) {
                LOGGER.warn("Failed to parse item tag: {}", (Object)$$0, (Object)$$2);
                return null;
            }
        }

        private JsonElement serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("id", BuiltInRegistries.ITEM.getKey(this.item).toString());
            if (this.count != 1) {
                $$0.addProperty("count", (Number)Integer.valueOf((int)this.count));
            }
            if (this.tag != null) {
                $$0.addProperty("tag", this.tag.toString());
            }
            return $$0;
        }
    }

    public static class EntityTooltipInfo {
        public final EntityType<?> type;
        public final UUID id;
        @Nullable
        public final Component name;
        @Nullable
        private List<Component> linesCache;

        public EntityTooltipInfo(EntityType<?> $$0, UUID $$1, @Nullable Component $$2) {
            this.type = $$0;
            this.id = $$1;
            this.name = $$2;
        }

        @Nullable
        public static EntityTooltipInfo create(JsonElement $$0) {
            if (!$$0.isJsonObject()) {
                return null;
            }
            JsonObject $$1 = $$0.getAsJsonObject();
            EntityType<?> $$2 = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(GsonHelper.getAsString($$1, "type")));
            UUID $$3 = UUID.fromString((String)GsonHelper.getAsString($$1, "id"));
            MutableComponent $$4 = Component.Serializer.fromJson($$1.get("name"));
            return new EntityTooltipInfo($$2, $$3, $$4);
        }

        @Nullable
        public static EntityTooltipInfo create(Component $$0) {
            try {
                CompoundTag $$1 = TagParser.parseTag($$0.getString());
                MutableComponent $$2 = Component.Serializer.fromJson($$1.getString("name"));
                EntityType<?> $$3 = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation($$1.getString("type")));
                UUID $$4 = UUID.fromString((String)$$1.getString("id"));
                return new EntityTooltipInfo($$3, $$4, $$2);
            }
            catch (Exception $$5) {
                return null;
            }
        }

        public JsonElement serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("type", BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
            $$0.addProperty("id", this.id.toString());
            if (this.name != null) {
                $$0.add("name", Component.Serializer.toJsonTree(this.name));
            }
            return $$0;
        }

        public List<Component> getTooltipLines() {
            if (this.linesCache == null) {
                this.linesCache = Lists.newArrayList();
                if (this.name != null) {
                    this.linesCache.add((Object)this.name);
                }
                this.linesCache.add((Object)Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
                this.linesCache.add((Object)Component.literal(this.id.toString()));
            }
            return this.linesCache;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 == null || this.getClass() != $$0.getClass()) {
                return false;
            }
            EntityTooltipInfo $$1 = (EntityTooltipInfo)$$0;
            return this.type.equals($$1.type) && this.id.equals((Object)$$1.id) && Objects.equals((Object)this.name, (Object)$$1.name);
        }

        public int hashCode() {
            int $$0 = this.type.hashCode();
            $$0 = 31 * $$0 + this.id.hashCode();
            $$0 = 31 * $$0 + (this.name != null ? this.name.hashCode() : 0);
            return $$0;
        }
    }
}