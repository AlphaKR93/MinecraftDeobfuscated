/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.reflect.Type
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix
extends NamedEntityFix {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Component.class, (Object)new JsonDeserializer<Component>(){

        public MutableComponent deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            if ($$0.isJsonPrimitive()) {
                return Component.literal($$0.getAsString());
            }
            if ($$0.isJsonArray()) {
                JsonArray $$3 = $$0.getAsJsonArray();
                MutableComponent $$4 = null;
                for (JsonElement $$5 : $$3) {
                    MutableComponent $$6 = this.deserialize($$5, (Type)$$5.getClass(), $$2);
                    if ($$4 == null) {
                        $$4 = $$6;
                        continue;
                    }
                    $$4.append($$6);
                }
                return $$4;
            }
            throw new JsonParseException("Don't know how to turn " + $$0 + " into a Component");
        }
    }).create();

    public BlockEntitySignTextStrictJsonFix(Schema $$0, boolean $$1) {
        super($$0, $$1, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> updateLine(Dynamic<?> $$0, String $$1) {
        String $$2 = $$0.get($$1).asString("");
        Component $$3 = null;
        if ("null".equals((Object)$$2) || StringUtils.isEmpty((CharSequence)$$2)) {
            $$3 = CommonComponents.EMPTY;
        } else if ($$2.charAt(0) == '\"' && $$2.charAt($$2.length() - 1) == '\"' || $$2.charAt(0) == '{' && $$2.charAt($$2.length() - 1) == '}') {
            try {
                $$3 = GsonHelper.fromNullableJson(GSON, $$2, Component.class, true);
                if ($$3 == null) {
                    $$3 = CommonComponents.EMPTY;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            if ($$3 == null) {
                try {
                    $$3 = Component.Serializer.fromJson($$2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if ($$3 == null) {
                try {
                    $$3 = Component.Serializer.fromJsonLenient($$2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if ($$3 == null) {
                $$3 = Component.literal($$2);
            }
        } else {
            $$3 = Component.literal($$2);
        }
        return $$0.set($$1, $$0.createString(Component.Serializer.toJson($$3)));
    }

    @Override
    protected Typed<?> fix(Typed<?> $$02) {
        return $$02.update(DSL.remainderFinder(), $$0 -> {
            $$0 = this.updateLine((Dynamic<?>)$$0, "Text1");
            $$0 = this.updateLine((Dynamic<?>)$$0, "Text2");
            $$0 = this.updateLine((Dynamic<?>)$$0, "Text3");
            $$0 = this.updateLine((Dynamic<?>)$$0, "Text4");
            return $$0;
        });
    }
}