/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentInfo
implements ArgumentTypeInfo<LongArgumentType, Template> {
    @Override
    public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
        boolean $$2 = $$0.min != Long.MIN_VALUE;
        boolean $$3 = $$0.max != Long.MAX_VALUE;
        $$1.writeByte(ArgumentUtils.createNumberFlags($$2, $$3));
        if ($$2) {
            $$1.writeLong($$0.min);
        }
        if ($$3) {
            $$1.writeLong($$0.max);
        }
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        long $$2 = ArgumentUtils.numberHasMin($$1) ? $$0.readLong() : Long.MIN_VALUE;
        long $$3 = ArgumentUtils.numberHasMax($$1) ? $$0.readLong() : Long.MAX_VALUE;
        return new Template($$2, $$3);
    }

    @Override
    public void serializeToJson(Template $$0, JsonObject $$1) {
        if ($$0.min != Long.MIN_VALUE) {
            $$1.addProperty("min", (Number)Long.valueOf((long)$$0.min));
        }
        if ($$0.max != Long.MAX_VALUE) {
            $$1.addProperty("max", (Number)Long.valueOf((long)$$0.max));
        }
    }

    @Override
    public Template unpack(LongArgumentType $$0) {
        return new Template($$0.getMinimum(), $$0.getMaximum());
    }

    public final class Template
    implements ArgumentTypeInfo.Template<LongArgumentType> {
        final long min;
        final long max;

        Template(long $$1, long $$2) {
            this.min = $$1;
            this.max = $$2;
        }

        @Override
        public LongArgumentType instantiate(CommandBuildContext $$0) {
            return LongArgumentType.longArg((long)this.min, (long)this.max);
        }

        @Override
        public ArgumentTypeInfo<LongArgumentType, ?> type() {
            return LongArgumentInfo.this;
        }
    }
}