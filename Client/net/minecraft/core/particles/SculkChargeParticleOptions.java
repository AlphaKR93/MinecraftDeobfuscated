/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record SculkChargeParticleOptions(float roll) implements ParticleOptions
{
    public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("roll").forGetter($$0 -> Float.valueOf((float)$$0.roll))).apply((Applicative)$$02, SculkChargeParticleOptions::new));
    public static final ParticleOptions.Deserializer<SculkChargeParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<SculkChargeParticleOptions>(){

        @Override
        public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> $$0, StringReader $$1) throws CommandSyntaxException {
            $$1.expect(' ');
            float $$2 = $$1.readFloat();
            return new SculkChargeParticleOptions($$2);
        }

        @Override
        public SculkChargeParticleOptions fromNetwork(ParticleType<SculkChargeParticleOptions> $$0, FriendlyByteBuf $$1) {
            return new SculkChargeParticleOptions($$1.readFloat());
        }
    };

    public ParticleType<SculkChargeParticleOptions> getType() {
        return ParticleTypes.SCULK_CHARGE;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.roll);
    }

    @Override
    public String writeToString() {
        return String.format((Locale)Locale.ROOT, (String)"%s %.2f", (Object[])new Object[]{BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf((float)this.roll)});
    }
}