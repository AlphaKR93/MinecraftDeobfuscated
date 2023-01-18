/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  java.lang.Double
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentInfo
implements ArgumentTypeInfo<DoubleArgumentType, Template> {
    @Override
    public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
        boolean $$2 = $$0.min != -1.7976931348623157E308;
        boolean $$3 = $$0.max != Double.MAX_VALUE;
        $$1.writeByte(ArgumentUtils.createNumberFlags($$2, $$3));
        if ($$2) {
            $$1.writeDouble($$0.min);
        }
        if ($$3) {
            $$1.writeDouble($$0.max);
        }
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        double $$2 = ArgumentUtils.numberHasMin($$1) ? $$0.readDouble() : -1.7976931348623157E308;
        double $$3 = ArgumentUtils.numberHasMax($$1) ? $$0.readDouble() : Double.MAX_VALUE;
        return new Template($$2, $$3);
    }

    @Override
    public void serializeToJson(Template $$0, JsonObject $$1) {
        if ($$0.min != -1.7976931348623157E308) {
            $$1.addProperty("min", (Number)Double.valueOf((double)$$0.min));
        }
        if ($$0.max != Double.MAX_VALUE) {
            $$1.addProperty("max", (Number)Double.valueOf((double)$$0.max));
        }
    }

    @Override
    public Template unpack(DoubleArgumentType $$0) {
        return new Template($$0.getMinimum(), $$0.getMaximum());
    }

    public final class Template
    implements ArgumentTypeInfo.Template<DoubleArgumentType> {
        final double min;
        final double max;

        Template(double $$1, double $$2) {
            this.min = $$1;
            this.max = $$2;
        }

        @Override
        public DoubleArgumentType instantiate(CommandBuildContext $$0) {
            return DoubleArgumentType.doubleArg((double)this.min, (double)this.max);
        }

        @Override
        public ArgumentTypeInfo<DoubleArgumentType, ?> type() {
            return DoubleArgumentInfo.this;
        }
    }
}