/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 */
package net.minecraft.world.level.timers;

import java.util.Collection;
import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class FunctionTagCallback
implements TimerCallback<MinecraftServer> {
    final ResourceLocation tagId;

    public FunctionTagCallback(ResourceLocation $$0) {
        this.tagId = $$0;
    }

    @Override
    public void handle(MinecraftServer $$0, TimerQueue<MinecraftServer> $$1, long $$2) {
        ServerFunctionManager $$3 = $$0.getFunctions();
        Collection<CommandFunction> $$4 = $$3.getTag(this.tagId);
        for (CommandFunction $$5 : $$4) {
            $$3.execute($$5, $$3.getGameLoopSender());
        }
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
        public Serializer() {
            super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
        }

        @Override
        public void serialize(CompoundTag $$0, FunctionTagCallback $$1) {
            $$0.putString("Name", $$1.tagId.toString());
        }

        @Override
        public FunctionTagCallback deserialize(CompoundTag $$0) {
            ResourceLocation $$1 = new ResourceLocation($$0.getString("Name"));
            return new FunctionTagCallback($$1);
        }
    }
}