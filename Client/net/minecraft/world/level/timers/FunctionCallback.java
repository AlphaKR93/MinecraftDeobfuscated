/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.timers;

import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class FunctionCallback
implements TimerCallback<MinecraftServer> {
    final ResourceLocation functionId;

    public FunctionCallback(ResourceLocation $$0) {
        this.functionId = $$0;
    }

    @Override
    public void handle(MinecraftServer $$0, TimerQueue<MinecraftServer> $$12, long $$2) {
        ServerFunctionManager $$3 = $$0.getFunctions();
        $$3.get(this.functionId).ifPresent($$1 -> $$3.execute((CommandFunction)$$1, $$3.getGameLoopSender()));
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionCallback> {
        public Serializer() {
            super(new ResourceLocation("function"), FunctionCallback.class);
        }

        @Override
        public void serialize(CompoundTag $$0, FunctionCallback $$1) {
            $$0.putString("Name", $$1.functionId.toString());
        }

        @Override
        public FunctionCallback deserialize(CompoundTag $$0) {
            ResourceLocation $$1 = new ResourceLocation($$0.getString("Name"));
            return new FunctionCallback($$1);
        }
    }
}