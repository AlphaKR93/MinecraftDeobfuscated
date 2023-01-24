/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocation
implements Comparable<ResourceLocation> {
    public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType((Message)Component.translatable("argument.id.invalid"));
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String REALMS_NAMESPACE = "realms";
    private final String namespace;
    private final String path;

    protected ResourceLocation(String $$0, String $$1, @Nullable Dummy $$2) {
        this.namespace = $$0;
        this.path = $$1;
    }

    public ResourceLocation(String $$0, String $$1) {
        this(ResourceLocation.assertValidNamespace($$0, $$1), ResourceLocation.assertValidPath($$0, $$1), null);
    }

    private ResourceLocation(String[] $$0) {
        this($$0[0], $$0[1]);
    }

    public ResourceLocation(String $$0) {
        this(ResourceLocation.decompose($$0, ':'));
    }

    public static ResourceLocation of(String $$0, char $$1) {
        return new ResourceLocation(ResourceLocation.decompose($$0, $$1));
    }

    @Nullable
    public static ResourceLocation tryParse(String $$0) {
        try {
            return new ResourceLocation($$0);
        }
        catch (ResourceLocationException $$1) {
            return null;
        }
    }

    @Nullable
    public static ResourceLocation tryBuild(String $$0, String $$1) {
        try {
            return new ResourceLocation($$0, $$1);
        }
        catch (ResourceLocationException $$2) {
            return null;
        }
    }

    protected static String[] decompose(String $$0, char $$1) {
        String[] $$2 = new String[]{DEFAULT_NAMESPACE, $$0};
        int $$3 = $$0.indexOf((int)$$1);
        if ($$3 >= 0) {
            $$2[1] = $$0.substring($$3 + 1);
            if ($$3 >= 1) {
                $$2[0] = $$0.substring(0, $$3);
            }
        }
        return $$2;
    }

    public static DataResult<ResourceLocation> read(String $$0) {
        try {
            return DataResult.success((Object)new ResourceLocation($$0));
        }
        catch (ResourceLocationException $$1) {
            return DataResult.error((String)("Not a valid resource location: " + $$0 + " " + $$1.getMessage()));
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ResourceLocation withPath(String $$0) {
        return new ResourceLocation(this.namespace, ResourceLocation.assertValidPath(this.namespace, $$0), null);
    }

    public ResourceLocation withPath(UnaryOperator<String> $$0) {
        return this.withPath((String)$$0.apply((Object)this.path));
    }

    public ResourceLocation withPrefix(String $$0) {
        return this.withPath($$0 + this.path);
    }

    public ResourceLocation withSuffix(String $$0) {
        return this.withPath(this.path + $$0);
    }

    public String toString() {
        return this.namespace + ":" + this.path;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ResourceLocation) {
            ResourceLocation $$1 = (ResourceLocation)$$0;
            return this.namespace.equals((Object)$$1.namespace) && this.path.equals((Object)$$1.path);
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    public int compareTo(ResourceLocation $$0) {
        int $$1 = this.path.compareTo($$0.path);
        if ($$1 == 0) {
            $$1 = this.namespace.compareTo($$0.namespace);
        }
        return $$1;
    }

    public String toDebugFileName() {
        return this.toString().replace('/', '_').replace(':', '_');
    }

    public String toLanguageKey() {
        return this.namespace + "." + this.path;
    }

    public String toShortLanguageKey() {
        return this.namespace.equals((Object)DEFAULT_NAMESPACE) ? this.path : this.toLanguageKey();
    }

    public String toLanguageKey(String $$0) {
        return $$0 + "." + this.toLanguageKey();
    }

    public static ResourceLocation read(StringReader $$0) throws CommandSyntaxException {
        int $$1 = $$0.getCursor();
        while ($$0.canRead() && ResourceLocation.isAllowedInResourceLocation($$0.peek())) {
            $$0.skip();
        }
        String $$2 = $$0.getString().substring($$1, $$0.getCursor());
        try {
            return new ResourceLocation($$2);
        }
        catch (ResourceLocationException $$3) {
            $$0.setCursor($$1);
            throw ERROR_INVALID.createWithContext((ImmutableStringReader)$$0);
        }
    }

    public static boolean isAllowedInResourceLocation(char $$0) {
        return $$0 >= '0' && $$0 <= '9' || $$0 >= 'a' && $$0 <= 'z' || $$0 == '_' || $$0 == ':' || $$0 == '/' || $$0 == '.' || $$0 == '-';
    }

    private static boolean isValidPath(String $$0) {
        for (int $$1 = 0; $$1 < $$0.length(); ++$$1) {
            if (ResourceLocation.validPathChar($$0.charAt($$1))) continue;
            return false;
        }
        return true;
    }

    private static boolean isValidNamespace(String $$0) {
        for (int $$1 = 0; $$1 < $$0.length(); ++$$1) {
            if (ResourceLocation.validNamespaceChar($$0.charAt($$1))) continue;
            return false;
        }
        return true;
    }

    private static String assertValidNamespace(String $$0, String $$1) {
        if (!ResourceLocation.isValidNamespace($$0)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + $$0 + ":" + $$1);
        }
        return $$0;
    }

    public static boolean validPathChar(char $$0) {
        return $$0 == '_' || $$0 == '-' || $$0 >= 'a' && $$0 <= 'z' || $$0 >= '0' && $$0 <= '9' || $$0 == '/' || $$0 == '.';
    }

    private static boolean validNamespaceChar(char $$0) {
        return $$0 == '_' || $$0 == '-' || $$0 >= 'a' && $$0 <= 'z' || $$0 >= '0' && $$0 <= '9' || $$0 == '.';
    }

    public static boolean isValidResourceLocation(String $$0) {
        String[] $$1 = ResourceLocation.decompose($$0, ':');
        return ResourceLocation.isValidNamespace(StringUtils.isEmpty((CharSequence)$$1[0]) ? DEFAULT_NAMESPACE : $$1[0]) && ResourceLocation.isValidPath($$1[1]);
    }

    private static String assertValidPath(String $$0, String $$1) {
        if (!ResourceLocation.isValidPath($$1)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + $$0 + ":" + $$1);
        }
        return $$1;
    }

    protected static interface Dummy {
    }

    public static class Serializer
    implements JsonDeserializer<ResourceLocation>,
    JsonSerializer<ResourceLocation> {
        public ResourceLocation deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            return new ResourceLocation(GsonHelper.convertToString($$0, "location"));
        }

        public JsonElement serialize(ResourceLocation $$0, Type $$1, JsonSerializationContext $$2) {
            return new JsonPrimitive($$0.toString());
        }
    }
}