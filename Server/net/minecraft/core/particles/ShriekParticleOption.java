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

public class ShriekParticleOption
implements ParticleOptions {
    public static final Codec<ShriekParticleOption> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("delay").forGetter($$0 -> $$0.delay)).apply((Applicative)$$02, ShriekParticleOption::new));
    public static final ParticleOptions.Deserializer<ShriekParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ShriekParticleOption>(){

        @Override
        public ShriekParticleOption fromCommand(ParticleType<ShriekParticleOption> $$0, StringReader $$1) throws CommandSyntaxException {
            $$1.expect(' ');
            int $$2 = $$1.readInt();
            return new ShriekParticleOption($$2);
        }

        @Override
        public ShriekParticleOption fromNetwork(ParticleType<ShriekParticleOption> $$0, FriendlyByteBuf $$1) {
            return new ShriekParticleOption($$1.readVarInt());
        }
    };
    private final int delay;

    public ShriekParticleOption(int $$0) {
        this.delay = $$0;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.delay);
    }

    @Override
    public String writeToString() {
        return String.format((Locale)Locale.ROOT, (String)"%s %d", (Object[])new Object[]{BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.delay});
    }

    public ParticleType<ShriekParticleOption> getType() {
        return ParticleTypes.SHRIEK;
    }

    public int getDelay() {
        return this.delay;
    }
}