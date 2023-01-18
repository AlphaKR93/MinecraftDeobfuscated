/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  java.io.File
 *  java.lang.CharSequence
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.concurrent.TimeUnit
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
    public static final String PROPERTY_TEXTURES = "textures";
    private final TextureManager textureManager;
    private final File skinsDirectory;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> insecureSkinCache;

    public SkinManager(TextureManager $$0, File $$1, final MinecraftSessionService $$2) {
        this.textureManager = $$0;
        this.skinsDirectory = $$1;
        this.sessionService = $$2;
        this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String $$0) {
                GameProfile $$1 = new GameProfile(null, "dummy_mcdummyface");
                $$1.getProperties().put((Object)SkinManager.PROPERTY_TEXTURES, (Object)new Property(SkinManager.PROPERTY_TEXTURES, $$0, ""));
                try {
                    return $$2.getTextures($$1, false);
                }
                catch (Throwable $$22) {
                    return ImmutableMap.of();
                }
            }
        });
    }

    public ResourceLocation registerTexture(MinecraftProfileTexture $$0, MinecraftProfileTexture.Type $$1) {
        return this.registerTexture($$0, $$1, null);
    }

    private ResourceLocation registerTexture(MinecraftProfileTexture $$0, MinecraftProfileTexture.Type $$1, @Nullable SkinTextureCallback $$2) {
        String $$3 = Hashing.sha1().hashUnencodedChars((CharSequence)$$0.getHash()).toString();
        ResourceLocation $$4 = SkinManager.getTextureLocation($$1, $$3);
        AbstractTexture $$5 = this.textureManager.getTexture($$4, MissingTextureAtlasSprite.getTexture());
        if ($$5 == MissingTextureAtlasSprite.getTexture()) {
            File $$6 = new File(this.skinsDirectory, $$3.length() > 2 ? $$3.substring(0, 2) : "xx");
            File $$7 = new File($$6, $$3);
            HttpTexture $$8 = new HttpTexture($$7, $$0.getUrl(), DefaultPlayerSkin.getDefaultSkin(), $$1 == MinecraftProfileTexture.Type.SKIN, () -> {
                if ($$2 != null) {
                    $$2.onSkinTextureAvailable($$1, $$4, $$0);
                }
            });
            this.textureManager.register($$4, $$8);
        } else if ($$2 != null) {
            $$2.onSkinTextureAvailable($$1, $$4, $$0);
        }
        return $$4;
    }

    private static ResourceLocation getTextureLocation(MinecraftProfileTexture.Type $$0, String $$1) {
        String $$2 = switch ($$0) {
            default -> throw new IncompatibleClassChangeError();
            case MinecraftProfileTexture.Type.SKIN -> "skins";
            case MinecraftProfileTexture.Type.CAPE -> "capes";
            case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
        };
        return new ResourceLocation($$2 + "/" + $$1);
    }

    public void registerSkins(GameProfile $$0, SkinTextureCallback $$1, boolean $$2) {
        Runnable $$3 = () -> {
            HashMap $$3 = Maps.newHashMap();
            try {
                $$3.putAll(this.sessionService.getTextures($$0, $$2));
            }
            catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if ($$3.isEmpty()) {
                $$0.getProperties().clear();
                if ($$0.getId().equals((Object)Minecraft.getInstance().getUser().getGameProfile().getId())) {
                    $$0.getProperties().putAll((Multimap)Minecraft.getInstance().getProfileProperties());
                    $$3.putAll(this.sessionService.getTextures($$0, false));
                } else {
                    this.sessionService.fillProfileProperties($$0, $$2);
                    try {
                        $$3.putAll(this.sessionService.getTextures($$0, $$2));
                    }
                    catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            Minecraft.getInstance().execute(() -> this.lambda$registerSkins$3((Map)$$3, $$1));
        };
        Util.backgroundExecutor().execute($$3);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile $$0) {
        Property $$1 = (Property)Iterables.getFirst((Iterable)$$0.getProperties().get((Object)PROPERTY_TEXTURES), null);
        if ($$1 == null) {
            return ImmutableMap.of();
        }
        return (Map)this.insecureSkinCache.getUnchecked((Object)$$1.getValue());
    }

    public ResourceLocation getInsecureSkinLocation(GameProfile $$0) {
        MinecraftProfileTexture $$1 = (MinecraftProfileTexture)this.getInsecureSkinInformation($$0).get((Object)MinecraftProfileTexture.Type.SKIN);
        if ($$1 != null) {
            return this.registerTexture($$1, MinecraftProfileTexture.Type.SKIN);
        }
        return DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID($$0));
    }

    private /* synthetic */ void lambda$registerSkins$3(Map $$0, SkinTextureCallback $$1) {
        RenderSystem.recordRenderCall(() -> ImmutableList.of((Object)MinecraftProfileTexture.Type.SKIN, (Object)MinecraftProfileTexture.Type.CAPE).forEach($$2 -> {
            if ($$0.containsKey($$2)) {
                this.registerTexture((MinecraftProfileTexture)$$0.get($$2), (MinecraftProfileTexture.Type)$$2, $$1);
            }
        }));
    }

    public static interface SkinTextureCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
    }
}