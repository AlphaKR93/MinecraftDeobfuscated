/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonParser
 *  com.mojang.authlib.exceptions.MinecraftClientException
 *  com.mojang.authlib.minecraft.InsecurePublicKeyException$MissingException
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.yggdrasil.response.KeyPairResponse
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.ByteBuffer
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.security.PublicKey
 *  java.time.DateTimeException
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.TemporalAmount
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class AccountProfileKeyPairManager
implements ProfileKeyPairManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Duration MINIMUM_PROFILE_KEY_REFRESH_INTERVAL = Duration.ofHours((long)1L);
    private static final Path PROFILE_KEY_PAIR_DIR = Path.of((String)"profilekeys", (String[])new String[0]);
    private final UserApiService userApiService;
    private final Path profileKeyPairPath;
    private CompletableFuture<Optional<ProfileKeyPair>> keyPair;
    private Instant nextProfileKeyRefreshTime = Instant.EPOCH;

    public AccountProfileKeyPairManager(UserApiService $$0, UUID $$1, Path $$2) {
        this.userApiService = $$0;
        this.profileKeyPairPath = $$2.resolve(PROFILE_KEY_PAIR_DIR).resolve($$1 + ".json");
        this.keyPair = CompletableFuture.supplyAsync(() -> this.readProfileKeyPair().filter($$0 -> !$$0.publicKey().data().hasExpired()), (Executor)Util.backgroundExecutor()).thenCompose(this::readOrFetchProfileKeyPair);
    }

    @Override
    public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
        this.nextProfileKeyRefreshTime = Instant.now().plus((TemporalAmount)MINIMUM_PROFILE_KEY_REFRESH_INTERVAL);
        this.keyPair = this.keyPair.thenCompose(this::readOrFetchProfileKeyPair);
        return this.keyPair;
    }

    @Override
    public boolean shouldRefreshKeyPair() {
        if (this.keyPair.isDone() && Instant.now().isAfter(this.nextProfileKeyRefreshTime)) {
            return (Boolean)((Optional)this.keyPair.join()).map(ProfileKeyPair::dueRefresh).orElse((Object)true);
        }
        return false;
    }

    private CompletableFuture<Optional<ProfileKeyPair>> readOrFetchProfileKeyPair(Optional<ProfileKeyPair> $$0) {
        return CompletableFuture.supplyAsync(() -> {
            if ($$0.isPresent() && !((ProfileKeyPair)((Object)((Object)$$0.get()))).dueRefresh()) {
                if (!SharedConstants.IS_RUNNING_IN_IDE) {
                    this.writeProfileKeyPair(null);
                }
                return $$0;
            }
            try {
                ProfileKeyPair $$1 = this.fetchProfileKeyPair(this.userApiService);
                this.writeProfileKeyPair($$1);
                return Optional.of((Object)((Object)$$1));
            }
            catch (MinecraftClientException | IOException | CryptException $$2) {
                LOGGER.error("Failed to retrieve profile key pair", (Throwable)$$2);
                this.writeProfileKeyPair(null);
                return $$0;
            }
        }, (Executor)Util.backgroundExecutor());
    }

    private Optional<ProfileKeyPair> readProfileKeyPair() {
        Optional optional;
        block9: {
            if (Files.notExists((Path)this.profileKeyPairPath, (LinkOption[])new LinkOption[0])) {
                return Optional.empty();
            }
            BufferedReader $$0 = Files.newBufferedReader((Path)this.profileKeyPairPath);
            try {
                optional = ProfileKeyPair.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)JsonParser.parseReader((Reader)$$0)).result();
                if ($$0 == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if ($$0 != null) {
                        try {
                            $$0.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception $$1) {
                    LOGGER.error("Failed to read profile key pair file {}", (Object)this.profileKeyPairPath, (Object)$$1);
                    return Optional.empty();
                }
            }
            $$0.close();
        }
        return optional;
    }

    private void writeProfileKeyPair(@Nullable ProfileKeyPair $$02) {
        try {
            Files.deleteIfExists((Path)this.profileKeyPairPath);
        }
        catch (IOException $$1) {
            LOGGER.error("Failed to delete profile key pair file {}", (Object)this.profileKeyPairPath, (Object)$$1);
        }
        if ($$02 == null) {
            return;
        }
        if (!SharedConstants.IS_RUNNING_IN_IDE) {
            return;
        }
        ProfileKeyPair.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)$$02).result().ifPresent($$0 -> {
            try {
                Files.createDirectories((Path)this.profileKeyPairPath.getParent(), (FileAttribute[])new FileAttribute[0]);
                Files.writeString((Path)this.profileKeyPairPath, (CharSequence)$$0.toString(), (OpenOption[])new OpenOption[0]);
            }
            catch (Exception $$1) {
                LOGGER.error("Failed to write profile key pair file {}", (Object)this.profileKeyPairPath, (Object)$$1);
            }
        });
    }

    private ProfileKeyPair fetchProfileKeyPair(UserApiService $$0) throws CryptException, IOException {
        KeyPairResponse $$1 = $$0.getKeyPair();
        if ($$1 != null) {
            ProfilePublicKey.Data $$2 = AccountProfileKeyPairManager.parsePublicKey($$1);
            return new ProfileKeyPair(Crypt.stringToPemRsaPrivateKey($$1.getPrivateKey()), new ProfilePublicKey($$2), Instant.parse((CharSequence)$$1.getRefreshedAfter()));
        }
        throw new IOException("Could not retrieve profile key pair");
    }

    private static ProfilePublicKey.Data parsePublicKey(KeyPairResponse $$0) throws CryptException {
        if (Strings.isNullOrEmpty((String)$$0.getPublicKey()) || $$0.getPublicKeySignature() == null || $$0.getPublicKeySignature().array().length == 0) {
            throw new CryptException((Throwable)new InsecurePublicKeyException.MissingException());
        }
        try {
            Instant $$1 = Instant.parse((CharSequence)$$0.getExpiresAt());
            PublicKey $$2 = Crypt.stringToRsaPublicKey($$0.getPublicKey());
            ByteBuffer $$3 = $$0.getPublicKeySignature();
            return new ProfilePublicKey.Data($$1, $$2, $$3.array());
        }
        catch (IllegalArgumentException | DateTimeException $$4) {
            throw new CryptException($$4);
        }
    }
}