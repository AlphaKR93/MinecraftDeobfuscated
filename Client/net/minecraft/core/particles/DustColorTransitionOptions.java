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
 *  org.joml.Vector3f
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustColorTransitionOptions
extends DustParticleOptionsBase {
    public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
    public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0f);
    public static final Codec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.VECTOR3F.fieldOf("fromColor").forGetter($$0 -> $$0.color), (App)ExtraCodecs.VECTOR3F.fieldOf("toColor").forGetter($$0 -> $$0.toColor), (App)Codec.FLOAT.fieldOf("scale").forGetter($$0 -> Float.valueOf((float)$$0.scale))).apply((Applicative)$$02, DustColorTransitionOptions::new));
    public static final ParticleOptions.Deserializer<DustColorTransitionOptions> DESERIALIZER = new ParticleOptions.Deserializer<DustColorTransitionOptions>(){

        @Override
        public DustColorTransitionOptions fromCommand(ParticleType<DustColorTransitionOptions> $$0, StringReader $$1) throws CommandSyntaxException {
            Vector3f $$2 = DustParticleOptionsBase.readVector3f($$1);
            $$1.expect(' ');
            float $$3 = $$1.readFloat();
            Vector3f $$4 = DustParticleOptionsBase.readVector3f($$1);
            return new DustColorTransitionOptions($$2, $$4, $$3);
        }

        @Override
        public DustColorTransitionOptions fromNetwork(ParticleType<DustColorTransitionOptions> $$0, FriendlyByteBuf $$1) {
            Vector3f $$2 = DustParticleOptionsBase.readVector3f($$1);
            float $$3 = $$1.readFloat();
            Vector3f $$4 = DustParticleOptionsBase.readVector3f($$1);
            return new DustColorTransitionOptions($$2, $$4, $$3);
        }
    };
    private final Vector3f toColor;

    public DustColorTransitionOptions(Vector3f $$0, Vector3f $$1, float $$2) {
        super($$0, $$2);
        this.toColor = $$1;
    }

    public Vector3f getFromColor() {
        return this.color;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        super.writeToNetwork($$0);
        $$0.writeFloat(this.toColor.x());
        $$0.writeFloat(this.toColor.y());
        $$0.writeFloat(this.toColor.z());
    }

    @Override
    public String writeToString() {
        return String.format((Locale)Locale.ROOT, (String)"%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", (Object[])new Object[]{BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf((float)this.color.x()), Float.valueOf((float)this.color.y()), Float.valueOf((float)this.color.z()), Float.valueOf((float)this.scale), Float.valueOf((float)this.toColor.x()), Float.valueOf((float)this.toColor.y()), Float.valueOf((float)this.toColor.z())});
    }

    public ParticleType<DustColorTransitionOptions> getType() {
        return ParticleTypes.DUST_COLOR_TRANSITION;
    }
}