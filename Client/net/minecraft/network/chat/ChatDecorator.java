/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.concurrent.CompletableFuture
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
    public static final ChatDecorator PLAIN = ($$0, $$1) -> CompletableFuture.completedFuture((Object)$$1);

    public CompletableFuture<Component> decorate(@Nullable ServerPlayer var1, Component var2);
}