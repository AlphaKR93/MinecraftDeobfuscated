/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  java.io.IOException
 *  java.io.Reader
 *  java.io.StringReader
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.AssertionError
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.math.BigDecimal
 *  java.math.BigInteger
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Contract
 */
package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

public class GsonHelper {
    private static final Gson GSON = new GsonBuilder().create();

    public static boolean isStringValue(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidPrimitive($$0, $$1)) {
            return false;
        }
        return $$0.getAsJsonPrimitive($$1).isString();
    }

    public static boolean isStringValue(JsonElement $$0) {
        if (!$$0.isJsonPrimitive()) {
            return false;
        }
        return $$0.getAsJsonPrimitive().isString();
    }

    public static boolean isNumberValue(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidPrimitive($$0, $$1)) {
            return false;
        }
        return $$0.getAsJsonPrimitive($$1).isNumber();
    }

    public static boolean isNumberValue(JsonElement $$0) {
        if (!$$0.isJsonPrimitive()) {
            return false;
        }
        return $$0.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBooleanValue(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidPrimitive($$0, $$1)) {
            return false;
        }
        return $$0.getAsJsonPrimitive($$1).isBoolean();
    }

    public static boolean isBooleanValue(JsonElement $$0) {
        if (!$$0.isJsonPrimitive()) {
            return false;
        }
        return $$0.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isArrayNode(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidNode($$0, $$1)) {
            return false;
        }
        return $$0.get($$1).isJsonArray();
    }

    public static boolean isObjectNode(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidNode($$0, $$1)) {
            return false;
        }
        return $$0.get($$1).isJsonObject();
    }

    public static boolean isValidPrimitive(JsonObject $$0, String $$1) {
        if (!GsonHelper.isValidNode($$0, $$1)) {
            return false;
        }
        return $$0.get($$1).isJsonPrimitive();
    }

    public static boolean isValidNode(JsonObject $$0, String $$1) {
        if ($$0 == null) {
            return false;
        }
        return $$0.get($$1) != null;
    }

    public static String convertToString(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive()) {
            return $$0.getAsString();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a string, was " + GsonHelper.getType($$0));
    }

    public static String getAsString(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToString($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a string");
    }

    @Nullable
    @Contract(value="_,_,!null->!null;_,_,null->_")
    public static String getAsString(JsonObject $$0, String $$1, @Nullable String $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToString($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static Item convertToItem(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive()) {
            String $$2 = $$0.getAsString();
            return (Item)BuiltInRegistries.ITEM.getOptional(new ResourceLocation($$2)).orElseThrow(() -> new JsonSyntaxException("Expected " + $$1 + " to be an item, was unknown string '" + $$2 + "'"));
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be an item, was " + GsonHelper.getType($$0));
    }

    public static Item getAsItem(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToItem($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find an item");
    }

    @Nullable
    @Contract(value="_,_,!null->!null;_,_,null->_")
    public static Item getAsItem(JsonObject $$0, String $$1, @Nullable Item $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToItem($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static boolean convertToBoolean(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive()) {
            return $$0.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Boolean, was " + GsonHelper.getType($$0));
    }

    public static boolean getAsBoolean(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBoolean($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Boolean");
    }

    public static boolean getAsBoolean(JsonObject $$0, String $$1, boolean $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBoolean($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static double convertToDouble(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsDouble();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Double, was " + GsonHelper.getType($$0));
    }

    public static double getAsDouble(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToDouble($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Double");
    }

    public static double getAsDouble(JsonObject $$0, String $$1, double $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToDouble($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static float convertToFloat(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Float, was " + GsonHelper.getType($$0));
    }

    public static float getAsFloat(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToFloat($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Float");
    }

    public static float getAsFloat(JsonObject $$0, String $$1, float $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToFloat($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static long convertToLong(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Long, was " + GsonHelper.getType($$0));
    }

    public static long getAsLong(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToLong($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Long");
    }

    public static long getAsLong(JsonObject $$0, String $$1, long $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToLong($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static int convertToInt(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Int, was " + GsonHelper.getType($$0));
    }

    public static int getAsInt(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToInt($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Int");
    }

    public static int getAsInt(JsonObject $$0, String $$1, int $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToInt($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static byte convertToByte(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Byte, was " + GsonHelper.getType($$0));
    }

    public static byte getAsByte(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToByte($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Byte");
    }

    public static byte getAsByte(JsonObject $$0, String $$1, byte $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToByte($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static char convertToCharacter(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsCharacter();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Character, was " + GsonHelper.getType($$0));
    }

    public static char getAsCharacter(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToCharacter($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Character");
    }

    public static char getAsCharacter(JsonObject $$0, String $$1, char $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToCharacter($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static BigDecimal convertToBigDecimal(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsBigDecimal();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a BigDecimal, was " + GsonHelper.getType($$0));
    }

    public static BigDecimal getAsBigDecimal(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBigDecimal($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a BigDecimal");
    }

    public static BigDecimal getAsBigDecimal(JsonObject $$0, String $$1, BigDecimal $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBigDecimal($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static BigInteger convertToBigInteger(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsBigInteger();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a BigInteger, was " + GsonHelper.getType($$0));
    }

    public static BigInteger getAsBigInteger(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBigInteger($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a BigInteger");
    }

    public static BigInteger getAsBigInteger(JsonObject $$0, String $$1, BigInteger $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToBigInteger($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static short convertToShort(JsonElement $$0, String $$1) {
        if ($$0.isJsonPrimitive() && $$0.getAsJsonPrimitive().isNumber()) {
            return $$0.getAsShort();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a Short, was " + GsonHelper.getType($$0));
    }

    public static short getAsShort(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToShort($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a Short");
    }

    public static short getAsShort(JsonObject $$0, String $$1, short $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToShort($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static JsonObject convertToJsonObject(JsonElement $$0, String $$1) {
        if ($$0.isJsonObject()) {
            return $$0.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a JsonObject, was " + GsonHelper.getType($$0));
    }

    public static JsonObject getAsJsonObject(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToJsonObject($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a JsonObject");
    }

    @Nullable
    @Contract(value="_,_,!null->!null;_,_,null->_")
    public static JsonObject getAsJsonObject(JsonObject $$0, String $$1, @Nullable JsonObject $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToJsonObject($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static JsonArray convertToJsonArray(JsonElement $$0, String $$1) {
        if ($$0.isJsonArray()) {
            return $$0.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + $$1 + " to be a JsonArray, was " + GsonHelper.getType($$0));
    }

    public static JsonArray getAsJsonArray(JsonObject $$0, String $$1) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToJsonArray($$0.get($$1), $$1);
        }
        throw new JsonSyntaxException("Missing " + $$1 + ", expected to find a JsonArray");
    }

    @Nullable
    @Contract(value="_,_,!null->!null;_,_,null->_")
    public static JsonArray getAsJsonArray(JsonObject $$0, String $$1, @Nullable JsonArray $$2) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToJsonArray($$0.get($$1), $$1);
        }
        return $$2;
    }

    public static <T> T convertToObject(@Nullable JsonElement $$0, String $$1, JsonDeserializationContext $$2, Class<? extends T> $$3) {
        if ($$0 != null) {
            return (T)$$2.deserialize($$0, $$3);
        }
        throw new JsonSyntaxException("Missing " + $$1);
    }

    public static <T> T getAsObject(JsonObject $$0, String $$1, JsonDeserializationContext $$2, Class<? extends T> $$3) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToObject($$0.get($$1), $$1, $$2, $$3);
        }
        throw new JsonSyntaxException("Missing " + $$1);
    }

    @Nullable
    @Contract(value="_,_,!null,_,_->!null;_,_,null,_,_->_")
    public static <T> T getAsObject(JsonObject $$0, String $$1, @Nullable T $$2, JsonDeserializationContext $$3, Class<? extends T> $$4) {
        if ($$0.has($$1)) {
            return GsonHelper.convertToObject($$0.get($$1), $$1, $$3, $$4);
        }
        return $$2;
    }

    public static String getType(@Nullable JsonElement $$0) {
        String $$1 = StringUtils.abbreviateMiddle((String)String.valueOf((Object)$$0), (String)"...", (int)10);
        if ($$0 == null) {
            return "null (missing)";
        }
        if ($$0.isJsonNull()) {
            return "null (json)";
        }
        if ($$0.isJsonArray()) {
            return "an array (" + $$1 + ")";
        }
        if ($$0.isJsonObject()) {
            return "an object (" + $$1 + ")";
        }
        if ($$0.isJsonPrimitive()) {
            JsonPrimitive $$2 = $$0.getAsJsonPrimitive();
            if ($$2.isNumber()) {
                return "a number (" + $$1 + ")";
            }
            if ($$2.isBoolean()) {
                return "a boolean (" + $$1 + ")";
            }
        }
        return $$1;
    }

    @Nullable
    public static <T> T fromNullableJson(Gson $$0, Reader $$1, Class<T> $$2, boolean $$3) {
        try {
            JsonReader $$4 = new JsonReader($$1);
            $$4.setLenient($$3);
            return (T)$$0.getAdapter($$2).read($$4);
        }
        catch (IOException $$5) {
            throw new JsonParseException((Throwable)$$5);
        }
    }

    public static <T> T fromJson(Gson $$0, Reader $$1, Class<T> $$2, boolean $$3) {
        T $$4 = GsonHelper.fromNullableJson($$0, $$1, $$2, $$3);
        if ($$4 == null) {
            throw new JsonParseException("JSON data was null or empty");
        }
        return $$4;
    }

    @Nullable
    public static <T> T fromNullableJson(Gson $$0, Reader $$1, TypeToken<T> $$2, boolean $$3) {
        try {
            JsonReader $$4 = new JsonReader($$1);
            $$4.setLenient($$3);
            return (T)$$0.getAdapter($$2).read($$4);
        }
        catch (IOException $$5) {
            throw new JsonParseException((Throwable)$$5);
        }
    }

    public static <T> T fromJson(Gson $$0, Reader $$1, TypeToken<T> $$2, boolean $$3) {
        T $$4 = GsonHelper.fromNullableJson($$0, $$1, $$2, $$3);
        if ($$4 == null) {
            throw new JsonParseException("JSON data was null or empty");
        }
        return $$4;
    }

    @Nullable
    public static <T> T fromNullableJson(Gson $$0, String $$1, TypeToken<T> $$2, boolean $$3) {
        return GsonHelper.fromNullableJson($$0, (Reader)new StringReader($$1), $$2, $$3);
    }

    public static <T> T fromJson(Gson $$0, String $$1, Class<T> $$2, boolean $$3) {
        return GsonHelper.fromJson($$0, (Reader)new StringReader($$1), $$2, $$3);
    }

    @Nullable
    public static <T> T fromNullableJson(Gson $$0, String $$1, Class<T> $$2, boolean $$3) {
        return GsonHelper.fromNullableJson($$0, (Reader)new StringReader($$1), $$2, $$3);
    }

    public static <T> T fromJson(Gson $$0, Reader $$1, TypeToken<T> $$2) {
        return GsonHelper.fromJson($$0, $$1, $$2, false);
    }

    @Nullable
    public static <T> T fromNullableJson(Gson $$0, String $$1, TypeToken<T> $$2) {
        return GsonHelper.fromNullableJson($$0, $$1, $$2, false);
    }

    public static <T> T fromJson(Gson $$0, Reader $$1, Class<T> $$2) {
        return GsonHelper.fromJson($$0, $$1, $$2, false);
    }

    public static <T> T fromJson(Gson $$0, String $$1, Class<T> $$2) {
        return GsonHelper.fromJson($$0, $$1, $$2, false);
    }

    public static JsonObject parse(String $$0, boolean $$1) {
        return GsonHelper.parse((Reader)new StringReader($$0), $$1);
    }

    public static JsonObject parse(Reader $$0, boolean $$1) {
        return GsonHelper.fromJson(GSON, $$0, JsonObject.class, $$1);
    }

    public static JsonObject parse(String $$0) {
        return GsonHelper.parse($$0, false);
    }

    public static JsonObject parse(Reader $$0) {
        return GsonHelper.parse($$0, false);
    }

    public static JsonArray parseArray(String $$0) {
        return GsonHelper.parseArray((Reader)new StringReader($$0));
    }

    public static JsonArray parseArray(Reader $$0) {
        return GsonHelper.fromJson(GSON, $$0, JsonArray.class, false);
    }

    public static String toStableString(JsonElement $$0) {
        StringWriter $$1 = new StringWriter();
        JsonWriter $$2 = new JsonWriter((Writer)$$1);
        try {
            GsonHelper.writeValue($$2, $$0, (Comparator<String>)Comparator.naturalOrder());
        }
        catch (IOException $$3) {
            throw new AssertionError((Object)$$3);
        }
        return $$1.toString();
    }

    public static void writeValue(JsonWriter $$0, @Nullable JsonElement $$1, @Nullable Comparator<String> $$2) throws IOException {
        if ($$1 == null || $$1.isJsonNull()) {
            $$0.nullValue();
        } else if ($$1.isJsonPrimitive()) {
            JsonPrimitive $$3 = $$1.getAsJsonPrimitive();
            if ($$3.isNumber()) {
                $$0.value($$3.getAsNumber());
            } else if ($$3.isBoolean()) {
                $$0.value($$3.getAsBoolean());
            } else {
                $$0.value($$3.getAsString());
            }
        } else if ($$1.isJsonArray()) {
            $$0.beginArray();
            for (JsonElement $$4 : $$1.getAsJsonArray()) {
                GsonHelper.writeValue($$0, $$4, $$2);
            }
            $$0.endArray();
        } else if ($$1.isJsonObject()) {
            $$0.beginObject();
            for (Map.Entry $$5 : GsonHelper.sortByKeyIfNeeded((Collection<Map.Entry<String, JsonElement>>)$$1.getAsJsonObject().entrySet(), $$2)) {
                $$0.name((String)$$5.getKey());
                GsonHelper.writeValue($$0, (JsonElement)$$5.getValue(), $$2);
            }
            $$0.endObject();
        } else {
            throw new IllegalArgumentException("Couldn't write " + $$1.getClass());
        }
    }

    private static Collection<Map.Entry<String, JsonElement>> sortByKeyIfNeeded(Collection<Map.Entry<String, JsonElement>> $$0, @Nullable Comparator<String> $$1) {
        if ($$1 == null) {
            return $$0;
        }
        ArrayList $$2 = new ArrayList($$0);
        $$2.sort(Map.Entry.comparingByKey($$1));
        return $$2;
    }
}