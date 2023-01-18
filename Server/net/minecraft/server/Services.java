/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  java.io.File
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.SignatureValidator;

public record Services(MinecraftSessionService sessionService, SignatureValidator serviceSignatureValidator, GameProfileRepository profileRepository, GameProfileCache profileCache) {
    private static final String USERID_CACHE_FILE = "usercache.json";

    public static Services create(YggdrasilAuthenticationService $$0, File $$1) {
        MinecraftSessionService $$2 = $$0.createMinecraftSessionService();
        GameProfileRepository $$3 = $$0.createProfileRepository();
        GameProfileCache $$4 = new GameProfileCache($$3, new File($$1, USERID_CACHE_FILE));
        SignatureValidator $$5 = SignatureValidator.from($$0.getServicesKey());
        return new Services($$2, $$5, $$3, $$4);
    }
}