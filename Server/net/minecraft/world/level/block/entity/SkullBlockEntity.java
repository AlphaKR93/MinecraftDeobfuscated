/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Optional
 *  java.util.concurrent.Executor
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity
extends BlockEntity {
    public static final String TAG_SKULL_OWNER = "SkullOwner";
    public static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
    @Nullable
    private static GameProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private GameProfile owner;
    @Nullable
    private ResourceLocation noteBlockSound;
    private int animationTickCount;
    private boolean isAnimating;

    public SkullBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SKULL, $$0, $$1);
    }

    public static void setup(Services $$0, Executor $$1) {
        profileCache = $$0.profileCache();
        sessionService = $$0.sessionService();
        mainThreadExecutor = $$1;
    }

    public static void clear() {
        profileCache = null;
        sessionService = null;
        mainThreadExecutor = null;
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (this.owner != null) {
            CompoundTag $$1 = new CompoundTag();
            NbtUtils.writeGameProfile($$1, this.owner);
            $$0.put(TAG_SKULL_OWNER, $$1);
        }
        if (this.noteBlockSound != null) {
            $$0.putString(TAG_NOTE_BLOCK_SOUND, this.noteBlockSound.toString());
        }
    }

    @Override
    public void load(CompoundTag $$0) {
        String $$1;
        super.load($$0);
        if ($$0.contains(TAG_SKULL_OWNER, 10)) {
            this.setOwner(NbtUtils.readGameProfile($$0.getCompound(TAG_SKULL_OWNER)));
        } else if ($$0.contains("ExtraType", 8) && !StringUtil.isNullOrEmpty($$1 = $$0.getString("ExtraType"))) {
            this.setOwner(new GameProfile(null, $$1));
        }
        if ($$0.contains(TAG_NOTE_BLOCK_SOUND, 8)) {
            this.noteBlockSound = ResourceLocation.tryParse($$0.getString(TAG_NOTE_BLOCK_SOUND));
        }
    }

    public static void animation(Level $$0, BlockPos $$1, BlockState $$2, SkullBlockEntity $$3) {
        if ($$0.hasNeighborSignal($$1)) {
            $$3.isAnimating = true;
            ++$$3.animationTickCount;
        } else {
            $$3.isAnimating = false;
        }
    }

    public float getAnimation(float $$0) {
        if (this.isAnimating) {
            return (float)this.animationTickCount + $$0;
        }
        return this.animationTickCount;
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    @Nullable
    public ResourceLocation getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable GameProfile $$0) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = $$0;
        }
        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        SkullBlockEntity.updateGameprofile(this.owner, (Consumer<GameProfile>)((Consumer)$$0 -> {
            this.owner = $$0;
            this.setChanged();
        }));
    }

    public static void updateGameprofile(@Nullable GameProfile $$0, Consumer<GameProfile> $$1) {
        if ($$0 == null || StringUtil.isNullOrEmpty($$0.getName()) || $$0.isComplete() && $$0.getProperties().containsKey((Object)"textures") || profileCache == null || sessionService == null) {
            $$1.accept((Object)$$0);
            return;
        }
        profileCache.getAsync($$0.getName(), (Consumer<Optional<GameProfile>>)((Consumer)$$2 -> Util.backgroundExecutor().execute(() -> Util.ifElse($$2, $$1 -> {
            GameProfile $$0 = (Property)Iterables.getFirst((Iterable)$$1.getProperties().get((Object)"textures"), null);
            if ($$0 == null) {
                $$1 = sessionService.fillProfileProperties($$1, true);
            }
            GameProfile $$3 = $$1;
            mainThreadExecutor.execute(() -> {
                profileCache.add($$3);
                $$1.accept((Object)$$3);
            });
        }, () -> mainThreadExecutor.execute(() -> $$1.accept((Object)$$0))))));
    }
}