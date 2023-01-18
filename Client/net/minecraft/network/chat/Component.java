/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.stream.JsonReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.Boolean
 *  java.lang.IllegalAccessException
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.NoSuchFieldException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.StackOverflowError
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.lang.reflect.Field
 *  java.lang.reflect.Type
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  java.util.Map$Entry
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component
extends Message,
FormattedText {
    public Style getStyle();

    public ComponentContents getContents();

    @Override
    default public String getString() {
        return FormattedText.super.getString();
    }

    default public String getString(int $$0) {
        StringBuilder $$1 = new StringBuilder();
        this.visit($$2 -> {
            int $$3 = $$0 - $$1.length();
            if ($$3 <= 0) {
                return STOP_ITERATION;
            }
            $$1.append($$2.length() <= $$3 ? $$2 : $$2.substring(0, $$3));
            return Optional.empty();
        });
        return $$1.toString();
    }

    public List<Component> getSiblings();

    default public MutableComponent plainCopy() {
        return MutableComponent.create(this.getContents());
    }

    default public MutableComponent copy() {
        return new MutableComponent(this.getContents(), (List<Component>)new ArrayList(this.getSiblings()), this.getStyle());
    }

    public FormattedCharSequence getVisualOrderText();

    @Override
    default public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> $$0, Style $$1) {
        Style $$2 = this.getStyle().applyTo($$1);
        Optional<T> $$3 = this.getContents().visit($$0, $$2);
        if ($$3.isPresent()) {
            return $$3;
        }
        for (Component $$4 : this.getSiblings()) {
            Optional<T> $$5 = $$4.visit($$0, $$2);
            if (!$$5.isPresent()) continue;
            return $$5;
        }
        return Optional.empty();
    }

    @Override
    default public <T> Optional<T> visit(FormattedText.ContentConsumer<T> $$0) {
        Optional<T> $$1 = this.getContents().visit($$0);
        if ($$1.isPresent()) {
            return $$1;
        }
        for (Component $$2 : this.getSiblings()) {
            Optional<T> $$3 = $$2.visit($$0);
            if (!$$3.isPresent()) continue;
            return $$3;
        }
        return Optional.empty();
    }

    default public List<Component> toFlatList() {
        return this.toFlatList(Style.EMPTY);
    }

    default public List<Component> toFlatList(Style $$0) {
        ArrayList $$1 = Lists.newArrayList();
        this.visit((arg_0, arg_1) -> Component.lambda$toFlatList$1((List)$$1, arg_0, arg_1), $$0);
        return $$1;
    }

    default public boolean contains(Component $$0) {
        List<Component> $$2;
        if (this.equals($$0)) {
            return true;
        }
        List<Component> $$1 = this.toFlatList();
        return Collections.indexOfSubList($$1, $$2 = $$0.toFlatList(this.getStyle())) != -1;
    }

    public static Component nullToEmpty(@Nullable String $$0) {
        return $$0 != null ? Component.literal($$0) : CommonComponents.EMPTY;
    }

    public static MutableComponent literal(String $$0) {
        return MutableComponent.create(new LiteralContents($$0));
    }

    public static MutableComponent translatable(String $$0) {
        return MutableComponent.create(new TranslatableContents($$0));
    }

    public static MutableComponent translatable(String $$0, Object ... $$1) {
        return MutableComponent.create(new TranslatableContents($$0, $$1));
    }

    public static MutableComponent empty() {
        return MutableComponent.create(ComponentContents.EMPTY);
    }

    public static MutableComponent keybind(String $$0) {
        return MutableComponent.create(new KeybindContents($$0));
    }

    public static MutableComponent nbt(String $$0, boolean $$1, Optional<Component> $$2, DataSource $$3) {
        return MutableComponent.create(new NbtContents($$0, $$1, $$2, $$3));
    }

    public static MutableComponent score(String $$0, String $$1) {
        return MutableComponent.create(new ScoreContents($$0, $$1));
    }

    public static MutableComponent selector(String $$0, Optional<Component> $$1) {
        return MutableComponent.create(new SelectorContents($$0, $$1));
    }

    private static /* synthetic */ Optional lambda$toFlatList$1(List $$0, Style $$1, String $$2) {
        if (!$$2.isEmpty()) {
            $$0.add((Object)Component.literal($$2).withStyle($$1));
        }
        return Optional.empty();
    }

    public static class Serializer
    implements JsonDeserializer<MutableComponent>,
    JsonSerializer<Component> {
        private static final Gson GSON = (Gson)Util.make(() -> {
            GsonBuilder $$0 = new GsonBuilder();
            $$0.disableHtmlEscaping();
            $$0.registerTypeHierarchyAdapter(Component.class, (Object)new Serializer());
            $$0.registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer());
            $$0.registerTypeAdapterFactory((TypeAdapterFactory)new LowerCaseEnumTypeAdapterFactory());
            return $$0.create();
        });
        private static final Field JSON_READER_POS = (Field)Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field $$0 = JsonReader.class.getDeclaredField("pos");
                $$0.setAccessible(true);
                return $$0;
            }
            catch (NoSuchFieldException $$1) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", (Throwable)$$1);
            }
        });
        private static final Field JSON_READER_LINESTART = (Field)Util.make(() -> {
            try {
                new JsonReader((Reader)new StringReader(""));
                Field $$0 = JsonReader.class.getDeclaredField("lineStart");
                $$0.setAccessible(true);
                return $$0;
            }
            catch (NoSuchFieldException $$1) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", (Throwable)$$1);
            }
        });

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public MutableComponent deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            if ($$0.isJsonPrimitive()) {
                return Component.literal($$0.getAsString());
            }
            if ($$0.isJsonObject()) {
                void $$26;
                JsonObject $$3 = $$0.getAsJsonObject();
                if ($$3.has("text")) {
                    String $$4 = GsonHelper.getAsString($$3, "text");
                    MutableComponent $$5 = $$4.isEmpty() ? Component.empty() : Component.literal($$4);
                } else if ($$3.has("translate")) {
                    String $$6 = GsonHelper.getAsString($$3, "translate");
                    if ($$3.has("with")) {
                        JsonArray $$7 = GsonHelper.getAsJsonArray($$3, "with");
                        Object[] $$8 = new Object[$$7.size()];
                        for (int $$9 = 0; $$9 < $$8.length; ++$$9) {
                            $$8[$$9] = Serializer.unwrapTextArgument(this.deserialize($$7.get($$9), $$1, $$2));
                        }
                        MutableComponent $$10 = Component.translatable($$6, $$8);
                    } else {
                        MutableComponent $$11 = Component.translatable($$6);
                    }
                } else if ($$3.has("score")) {
                    JsonObject $$12 = GsonHelper.getAsJsonObject($$3, "score");
                    if (!$$12.has("name") || !$$12.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    MutableComponent $$13 = Component.score(GsonHelper.getAsString($$12, "name"), GsonHelper.getAsString($$12, "objective"));
                } else if ($$3.has("selector")) {
                    Optional<Component> $$15 = this.parseSeparator($$1, $$2, $$3);
                    MutableComponent $$16 = Component.selector(GsonHelper.getAsString($$3, "selector"), $$15);
                } else if ($$3.has("keybind")) {
                    MutableComponent $$17 = Component.keybind(GsonHelper.getAsString($$3, "keybind"));
                } else {
                    void $$24;
                    if (!$$3.has("nbt")) throw new JsonParseException("Don't know how to turn " + $$0 + " into a Component");
                    String $$18 = GsonHelper.getAsString($$3, "nbt");
                    Optional<Component> $$19 = this.parseSeparator($$1, $$2, $$3);
                    boolean $$20 = GsonHelper.getAsBoolean($$3, "interpret", false);
                    if ($$3.has("block")) {
                        BlockDataSource $$21 = new BlockDataSource(GsonHelper.getAsString($$3, "block"));
                    } else if ($$3.has("entity")) {
                        EntityDataSource $$22 = new EntityDataSource(GsonHelper.getAsString($$3, "entity"));
                    } else {
                        if (!$$3.has("storage")) throw new JsonParseException("Don't know how to turn " + $$0 + " into a Component");
                        StorageDataSource $$23 = new StorageDataSource(new ResourceLocation(GsonHelper.getAsString($$3, "storage")));
                    }
                    MutableComponent $$25 = Component.nbt($$18, $$20, $$19, (DataSource)$$24);
                }
                if ($$3.has("extra")) {
                    JsonArray $$27 = GsonHelper.getAsJsonArray($$3, "extra");
                    if ($$27.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int $$28 = 0; $$28 < $$27.size(); ++$$28) {
                        $$26.append(this.deserialize($$27.get($$28), $$1, $$2));
                    }
                }
                $$26.setStyle((Style)$$2.deserialize($$0, Style.class));
                return $$26;
            }
            if (!$$0.isJsonArray()) throw new JsonParseException("Don't know how to turn " + $$0 + " into a Component");
            JsonArray $$29 = $$0.getAsJsonArray();
            MutableComponent $$30 = null;
            for (JsonElement $$31 : $$29) {
                MutableComponent $$32 = this.deserialize($$31, (Type)$$31.getClass(), $$2);
                if ($$30 == null) {
                    $$30 = $$32;
                    continue;
                }
                $$30.append($$32);
            }
            return $$30;
        }

        private static Object unwrapTextArgument(Object $$0) {
            ComponentContents $$2;
            Component $$1;
            if ($$0 instanceof Component && ($$1 = (Component)$$0).getStyle().isEmpty() && $$1.getSiblings().isEmpty() && ($$2 = $$1.getContents()) instanceof LiteralContents) {
                LiteralContents $$3 = (LiteralContents)$$2;
                return $$3.text();
            }
            return $$0;
        }

        private Optional<Component> parseSeparator(Type $$0, JsonDeserializationContext $$1, JsonObject $$2) {
            if ($$2.has("separator")) {
                return Optional.of((Object)this.deserialize($$2.get("separator"), $$0, $$1));
            }
            return Optional.empty();
        }

        private void serializeStyle(Style $$0, JsonObject $$1, JsonSerializationContext $$2) {
            JsonElement $$3 = $$2.serialize((Object)$$0);
            if ($$3.isJsonObject()) {
                JsonObject $$4 = (JsonObject)$$3;
                for (Map.Entry $$5 : $$4.entrySet()) {
                    $$1.add((String)$$5.getKey(), (JsonElement)$$5.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public JsonElement serialize(Component $$0, Type $$1, JsonSerializationContext $$2) {
            ComponentContents $$6;
            JsonObject $$3 = new JsonObject();
            if (!$$0.getStyle().isEmpty()) {
                this.serializeStyle($$0.getStyle(), $$3, $$2);
            }
            if (!$$0.getSiblings().isEmpty()) {
                JsonArray $$4 = new JsonArray();
                for (Component $$5 : $$0.getSiblings()) {
                    $$4.add(this.serialize($$5, (Type)Component.class, $$2));
                }
                $$3.add("extra", (JsonElement)$$4);
            }
            if (($$6 = $$0.getContents()) == ComponentContents.EMPTY) {
                $$3.addProperty("text", "");
                return $$3;
            } else if ($$6 instanceof LiteralContents) {
                LiteralContents $$7 = (LiteralContents)$$6;
                $$3.addProperty("text", $$7.text());
                return $$3;
            } else if ($$6 instanceof TranslatableContents) {
                TranslatableContents $$8 = (TranslatableContents)$$6;
                $$3.addProperty("translate", $$8.getKey());
                if ($$8.getArgs().length <= 0) return $$3;
                JsonArray $$9 = new JsonArray();
                for (Object $$10 : $$8.getArgs()) {
                    if ($$10 instanceof Component) {
                        $$9.add(this.serialize((Component)$$10, (Type)$$10.getClass(), $$2));
                        continue;
                    }
                    $$9.add((JsonElement)new JsonPrimitive(String.valueOf((Object)$$10)));
                }
                $$3.add("with", (JsonElement)$$9);
                return $$3;
            } else if ($$6 instanceof ScoreContents) {
                ScoreContents $$11 = (ScoreContents)$$6;
                JsonObject $$12 = new JsonObject();
                $$12.addProperty("name", $$11.getName());
                $$12.addProperty("objective", $$11.getObjective());
                $$3.add("score", (JsonElement)$$12);
                return $$3;
            } else if ($$6 instanceof SelectorContents) {
                SelectorContents $$13 = (SelectorContents)$$6;
                $$3.addProperty("selector", $$13.getPattern());
                this.serializeSeparator($$2, $$3, $$13.getSeparator());
                return $$3;
            } else if ($$6 instanceof KeybindContents) {
                KeybindContents $$14 = (KeybindContents)$$6;
                $$3.addProperty("keybind", $$14.getName());
                return $$3;
            } else {
                if (!($$6 instanceof NbtContents)) throw new IllegalArgumentException("Don't know how to serialize " + $$6 + " as a Component");
                NbtContents $$15 = (NbtContents)$$6;
                $$3.addProperty("nbt", $$15.getNbtPath());
                $$3.addProperty("interpret", Boolean.valueOf((boolean)$$15.isInterpreting()));
                this.serializeSeparator($$2, $$3, $$15.getSeparator());
                DataSource $$16 = $$15.getDataSource();
                if ($$16 instanceof BlockDataSource) {
                    BlockDataSource $$17 = (BlockDataSource)$$16;
                    $$3.addProperty("block", $$17.posPattern());
                    return $$3;
                } else if ($$16 instanceof EntityDataSource) {
                    EntityDataSource $$18 = (EntityDataSource)$$16;
                    $$3.addProperty("entity", $$18.selectorPattern());
                    return $$3;
                } else {
                    if (!($$16 instanceof StorageDataSource)) throw new IllegalArgumentException("Don't know how to serialize " + $$6 + " as a Component");
                    StorageDataSource $$19 = (StorageDataSource)$$16;
                    $$3.addProperty("storage", $$19.id().toString());
                }
            }
            return $$3;
        }

        private void serializeSeparator(JsonSerializationContext $$0, JsonObject $$1, Optional<Component> $$22) {
            $$22.ifPresent($$2 -> $$1.add("separator", this.serialize((Component)$$2, (Type)$$2.getClass(), $$0)));
        }

        public static String toJson(Component $$0) {
            return GSON.toJson((Object)$$0);
        }

        public static String toStableJson(Component $$0) {
            return GsonHelper.toStableString(Serializer.toJsonTree($$0));
        }

        public static JsonElement toJsonTree(Component $$0) {
            return GSON.toJsonTree((Object)$$0);
        }

        @Nullable
        public static MutableComponent fromJson(String $$0) {
            return GsonHelper.fromNullableJson(GSON, $$0, MutableComponent.class, false);
        }

        @Nullable
        public static MutableComponent fromJson(JsonElement $$0) {
            return (MutableComponent)GSON.fromJson($$0, MutableComponent.class);
        }

        @Nullable
        public static MutableComponent fromJsonLenient(String $$0) {
            return GsonHelper.fromNullableJson(GSON, $$0, MutableComponent.class, true);
        }

        public static MutableComponent fromJson(com.mojang.brigadier.StringReader $$0) {
            try {
                JsonReader $$1 = new JsonReader((Reader)new StringReader($$0.getRemaining()));
                $$1.setLenient(false);
                MutableComponent $$2 = (MutableComponent)GSON.getAdapter(MutableComponent.class).read($$1);
                $$0.setCursor($$0.getCursor() + Serializer.getPos($$1));
                return $$2;
            }
            catch (IOException | StackOverflowError $$3) {
                throw new JsonParseException($$3);
            }
        }

        private static int getPos(JsonReader $$0) {
            try {
                return JSON_READER_POS.getInt((Object)$$0) - JSON_READER_LINESTART.getInt((Object)$$0) + 1;
            }
            catch (IllegalAccessException $$1) {
                throw new IllegalStateException("Couldn't read position of JsonReader", (Throwable)$$1);
            }
        }
    }
}