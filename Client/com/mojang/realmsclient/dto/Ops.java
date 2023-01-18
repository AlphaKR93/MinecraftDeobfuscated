/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Set;

public class Ops
extends ValueObject {
    public Set<String> ops = Sets.newHashSet();

    public static Ops parse(String $$0) {
        Ops $$1 = new Ops();
        JsonParser $$2 = new JsonParser();
        try {
            JsonElement $$3 = $$2.parse($$0);
            JsonObject $$4 = $$3.getAsJsonObject();
            JsonElement $$5 = $$4.get("ops");
            if ($$5.isJsonArray()) {
                for (JsonElement $$6 : $$5.getAsJsonArray()) {
                    $$1.ops.add((Object)$$6.getAsString());
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return $$1;
    }
}