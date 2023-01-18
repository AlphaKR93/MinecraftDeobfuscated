/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.dto;

import com.google.gson.Gson;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import javax.annotation.Nullable;

public class GuardedSerializer {
    private final Gson gson = new Gson();

    public String toJson(ReflectionBasedSerialization $$0) {
        return this.gson.toJson((Object)$$0);
    }

    @Nullable
    public <T extends ReflectionBasedSerialization> T fromJson(String $$0, Class<T> $$1) {
        return (T)((ReflectionBasedSerialization)this.gson.fromJson($$0, $$1));
    }
}