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
 *  org.joml.Vector3f
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustParticleOptions
extends DustParticleOptionsBase {
    public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(0xFF0000).toVector3f();
    public static final DustParticleOptions REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0f);
    public static final Codec<DustParticleOptions> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.VECTOR3F.fieldOf("color").forGetter($$0 -> $$0.color), (App)Codec.FLOAT.fieldOf("scale").forGetter($$0 -> Float.valueOf((float)$$0.scale))).apply((Applicative)$$02, DustParticleOptions::new));
    public static final ParticleOptions.Deserializer<DustParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustParticleOptions>(){

        @Override
        public DustParticleOptions fromCommand(ParticleType<DustParticleOptions> $$0, StringReader $$1) throws CommandSyntaxException {
            Vector3f $$2 = DustParticleOptionsBase.readVector3f($$1);
            $$1.expect(' ');
            float $$3 = $$1.readFloat();
            return new DustParticleOptions($$2, $$3);
        }

        @Override
        public DustParticleOptions fromNetwork(ParticleType<DustParticleOptions> $$0, FriendlyByteBuf $$1) {
            return new DustParticleOptions(DustParticleOptionsBase.readVector3f($$1), $$1.readFloat());
        }
    };

    public DustParticleOptions(Vector3f $$0, float $$1) {
        super($$0, $$1);
    }

    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }
}