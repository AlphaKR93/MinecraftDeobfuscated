/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.util.UUIDTypeAdapter
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class User {
    private final String name;
    private final String uuid;
    private final String accessToken;
    private final Optional<String> xuid;
    private final Optional<String> clientId;
    private final Type type;

    public User(String $$0, String $$1, String $$2, Optional<String> $$3, Optional<String> $$4, Type $$5) {
        this.name = $$0;
        this.uuid = $$1;
        this.accessToken = $$2;
        this.xuid = $$3;
        this.clientId = $$4;
        this.type = $$5;
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + this.uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Optional<String> getClientId() {
        return this.clientId;
    }

    public Optional<String> getXuid() {
        return this.xuid;
    }

    @Nullable
    public UUID getProfileId() {
        try {
            return UUIDTypeAdapter.fromString((String)this.getUuid());
        }
        catch (IllegalArgumentException $$0) {
            return null;
        }
    }

    public GameProfile getGameProfile() {
        return new GameProfile(this.getProfileId(), this.getName());
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        LEGACY("legacy"),
        MOJANG("mojang"),
        MSA("msa");

        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String $$0) {
            this.name = $$0;
        }

        @Nullable
        public static Type byName(String $$0) {
            return (Type)((Object)BY_NAME.get((Object)$$0.toLowerCase(Locale.ROOT)));
        }

        public String getName() {
            return this.name;
        }

        static {
            BY_NAME = (Map)Arrays.stream((Object[])Type.values()).collect(Collectors.toMap($$0 -> $$0.name, (Function)Function.identity()));
        }
    }
}