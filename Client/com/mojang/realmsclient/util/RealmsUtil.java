/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.util.UUIDTypeAdapter
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.Date
 *  java.util.Map
 *  java.util.concurrent.TimeUnit
 */
package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class RealmsUtil {
    private static final YggdrasilAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
    static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, GameProfile>(){

        public GameProfile load(String $$0) throws Exception {
            GameProfile $$1 = SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString((String)$$0), null), false);
            if ($$1 == null) {
                throw new Exception("Couldn't get profile");
            }
            return $$1;
        }
    });
    private static final int MINUTES = 60;
    private static final int HOURS = 3600;
    private static final int DAYS = 86400;

    public static String uuidToName(String $$0) throws Exception {
        GameProfile $$1 = (GameProfile)gameProfileCache.get((Object)$$0);
        return $$1.getName();
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(String $$0) {
        try {
            GameProfile $$1 = (GameProfile)gameProfileCache.get((Object)$$0);
            return SESSION_SERVICE.getTextures($$1, false);
        }
        catch (Exception $$2) {
            return Maps.newHashMap();
        }
    }

    public static String convertToAgePresentation(long $$0) {
        if ($$0 < 0L) {
            return "right now";
        }
        long $$1 = $$0 / 1000L;
        if ($$1 < 60L) {
            return ($$1 == 1L ? "1 second" : $$1 + " seconds") + " ago";
        }
        if ($$1 < 3600L) {
            long $$2 = $$1 / 60L;
            return ($$2 == 1L ? "1 minute" : $$2 + " minutes") + " ago";
        }
        if ($$1 < 86400L) {
            long $$3 = $$1 / 3600L;
            return ($$3 == 1L ? "1 hour" : $$3 + " hours") + " ago";
        }
        long $$4 = $$1 / 86400L;
        return ($$4 == 1L ? "1 day" : $$4 + " days") + " ago";
    }

    public static String convertToAgePresentationFromInstant(Date $$0) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - $$0.getTime());
    }
}