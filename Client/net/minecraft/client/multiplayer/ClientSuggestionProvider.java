/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientSuggestionProvider
implements SharedSuggestionProvider {
    private final ClientPacketListener connection;
    private final Minecraft minecraft;
    private int pendingSuggestionsId = -1;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestionsFuture;
    private final Set<String> customCompletionSuggestions = new HashSet();

    public ClientSuggestionProvider(ClientPacketListener $$0, Minecraft $$1) {
        this.connection = $$0;
        this.minecraft = $$1;
    }

    @Override
    public Collection<String> getOnlinePlayerNames() {
        ArrayList $$0 = Lists.newArrayList();
        for (PlayerInfo $$1 : this.connection.getOnlinePlayers()) {
            $$0.add((Object)$$1.getProfile().getName());
        }
        return $$0;
    }

    @Override
    public Collection<String> getCustomTabSugggestions() {
        if (this.customCompletionSuggestions.isEmpty()) {
            return this.getOnlinePlayerNames();
        }
        HashSet $$0 = new HashSet(this.getOnlinePlayerNames());
        $$0.addAll(this.customCompletionSuggestions);
        return $$0;
    }

    @Override
    public Collection<String> getSelectedEntities() {
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            return Collections.singleton((Object)((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getAllTeams() {
        return this.connection.getLevel().getScoreboard().getTeamNames();
    }

    @Override
    public Stream<ResourceLocation> getAvailableSounds() {
        return this.minecraft.getSoundManager().getAvailableSounds().stream();
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames() {
        return this.connection.getRecipeManager().getRecipeIds();
    }

    @Override
    public boolean hasPermission(int $$0) {
        LocalPlayer $$1 = this.minecraft.player;
        return $$1 != null ? $$1.hasPermissions($$0) : $$0 == 0;
    }

    @Override
    public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> $$0, SharedSuggestionProvider.ElementSuggestionType $$1, SuggestionsBuilder $$22, CommandContext<?> $$3) {
        return (CompletableFuture)this.registryAccess().registry($$0).map($$2 -> {
            this.suggestRegistryElements((Registry)$$2, $$1, $$22);
            return $$22.buildFuture();
        }).orElseGet(() -> this.customSuggestion($$3));
    }

    @Override
    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> $$0) {
        if (this.pendingSuggestionsFuture != null) {
            this.pendingSuggestionsFuture.cancel(false);
        }
        this.pendingSuggestionsFuture = new CompletableFuture();
        int $$1 = ++this.pendingSuggestionsId;
        this.connection.send(new ServerboundCommandSuggestionPacket($$1, $$0.getInput()));
        return this.pendingSuggestionsFuture;
    }

    private static String prettyPrint(double $$0) {
        return String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$0});
    }

    private static String prettyPrint(int $$0) {
        return Integer.toString((int)$$0);
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 == null || $$0.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getRelevantCoordinates();
        }
        BlockPos $$1 = ((BlockHitResult)$$0).getBlockPos();
        return Collections.singleton((Object)new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint($$1.getX()), ClientSuggestionProvider.prettyPrint($$1.getY()), ClientSuggestionProvider.prettyPrint($$1.getZ())));
    }

    @Override
    public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 == null || $$0.getType() != HitResult.Type.BLOCK) {
            return SharedSuggestionProvider.super.getAbsoluteCoordinates();
        }
        Vec3 $$1 = $$0.getLocation();
        return Collections.singleton((Object)new SharedSuggestionProvider.TextCoordinates(ClientSuggestionProvider.prettyPrint($$1.x), ClientSuggestionProvider.prettyPrint($$1.y), ClientSuggestionProvider.prettyPrint($$1.z)));
    }

    @Override
    public Set<ResourceKey<Level>> levels() {
        return this.connection.levels();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return this.connection.enabledFeatures();
    }

    public void completeCustomSuggestions(int $$0, Suggestions $$1) {
        if ($$0 == this.pendingSuggestionsId) {
            this.pendingSuggestionsFuture.complete((Object)$$1);
            this.pendingSuggestionsFuture = null;
            this.pendingSuggestionsId = -1;
        }
    }

    public void modifyCustomCompletions(ClientboundCustomChatCompletionsPacket.Action $$0, List<String> $$1) {
        switch ($$0) {
            case ADD: {
                this.customCompletionSuggestions.addAll($$1);
                break;
            }
            case REMOVE: {
                $$1.forEach(arg_0 -> this.customCompletionSuggestions.remove(arg_0));
                break;
            }
            case SET: {
                this.customCompletionSuggestions.clear();
                this.customCompletionSuggestions.addAll($$1);
            }
        }
    }
}