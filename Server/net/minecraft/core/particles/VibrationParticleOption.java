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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class VibrationParticleOption
implements ParticleOptions {
    public static final Codec<VibrationParticleOption> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)PositionSource.CODEC.fieldOf("destination").forGetter($$0 -> $$0.destination), (App)Codec.INT.fieldOf("arrival_in_ticks").forGetter($$0 -> $$0.arrivalInTicks)).apply((Applicative)$$02, VibrationParticleOption::new));
    public static final ParticleOptions.Deserializer<VibrationParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<VibrationParticleOption>(){

        @Override
        public VibrationParticleOption fromCommand(ParticleType<VibrationParticleOption> $$0, StringReader $$1) throws CommandSyntaxException {
            $$1.expect(' ');
            float $$2 = (float)$$1.readDouble();
            $$1.expect(' ');
            float $$3 = (float)$$1.readDouble();
            $$1.expect(' ');
            float $$4 = (float)$$1.readDouble();
            $$1.expect(' ');
            int $$5 = $$1.readInt();
            BlockPos $$6 = new BlockPos($$2, $$3, $$4);
            return new VibrationParticleOption(new BlockPositionSource($$6), $$5);
        }

        @Override
        public VibrationParticleOption fromNetwork(ParticleType<VibrationParticleOption> $$0, FriendlyByteBuf $$1) {
            PositionSource $$2 = PositionSourceType.fromNetwork($$1);
            int $$3 = $$1.readVarInt();
            return new VibrationParticleOption($$2, $$3);
        }
    };
    private final PositionSource destination;
    private final int arrivalInTicks;

    public VibrationParticleOption(PositionSource $$0, int $$1) {
        this.destination = $$0;
        this.arrivalInTicks = $$1;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        PositionSourceType.toNetwork(this.destination, $$0);
        $$0.writeVarInt(this.arrivalInTicks);
    }

    @Override
    public String writeToString() {
        Vec3 $$0 = (Vec3)this.destination.getPosition(null).get();
        double $$1 = $$0.x();
        double $$2 = $$0.y();
        double $$3 = $$0.z();
        return String.format((Locale)Locale.ROOT, (String)"%s %.2f %.2f %.2f %d", (Object[])new Object[]{BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), $$1, $$2, $$3, this.arrivalInTicks});
    }

    public ParticleType<VibrationParticleOption> getType() {
        return ParticleTypes.VIBRATION;
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}