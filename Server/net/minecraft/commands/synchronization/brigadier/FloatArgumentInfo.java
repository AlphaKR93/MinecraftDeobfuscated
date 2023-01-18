/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  java.lang.Float
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentInfo
implements ArgumentTypeInfo<FloatArgumentType, Template> {
    @Override
    public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
        boolean $$2 = $$0.min != -3.4028235E38f;
        boolean $$3 = $$0.max != Float.MAX_VALUE;
        $$1.writeByte(ArgumentUtils.createNumberFlags($$2, $$3));
        if ($$2) {
            $$1.writeFloat($$0.min);
        }
        if ($$3) {
            $$1.writeFloat($$0.max);
        }
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
        byte $$1 = $$0.readByte();
        float $$2 = ArgumentUtils.numberHasMin($$1) ? $$0.readFloat() : -3.4028235E38f;
        float $$3 = ArgumentUtils.numberHasMax($$1) ? $$0.readFloat() : Float.MAX_VALUE;
        return new Template($$2, $$3);
    }

    @Override
    public void serializeToJson(Template $$0, JsonObject $$1) {
        if ($$0.min != -3.4028235E38f) {
            $$1.addProperty("min", (Number)Float.valueOf((float)$$0.min));
        }
        if ($$0.max != Float.MAX_VALUE) {
            $$1.addProperty("max", (Number)Float.valueOf((float)$$0.max));
        }
    }

    @Override
    public Template unpack(FloatArgumentType $$0) {
        return new Template($$0.getMinimum(), $$0.getMaximum());
    }

    public final class Template
    implements ArgumentTypeInfo.Template<FloatArgumentType> {
        final float min;
        final float max;

        Template(float $$1, float $$2) {
            this.min = $$1;
            this.max = $$2;
        }

        @Override
        public FloatArgumentType instantiate(CommandBuildContext $$0) {
            return FloatArgumentType.floatArg((float)this.min, (float)this.max);
        }

        @Override
        public ArgumentTypeInfo<FloatArgumentType, ?> type() {
            return FloatArgumentInfo.this;
        }
    }
}