/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
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
import java.util.Locale;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public abstract class DustParticleOptionsBase
implements ParticleOptions {
    public static final float MIN_SCALE = 0.01f;
    public static final float MAX_SCALE = 4.0f;
    protected final Vector3f color;
    protected final float scale;

    public DustParticleOptionsBase(Vector3f $$0, float $$1) {
        this.color = $$0;
        this.scale = Mth.clamp($$1, 0.01f, 4.0f);
    }

    public static Vector3f readVector3f(StringReader $$0) throws CommandSyntaxException {
        $$0.expect(' ');
        float $$1 = $$0.readFloat();
        $$0.expect(' ');
        float $$2 = $$0.readFloat();
        $$0.expect(' ');
        float $$3 = $$0.readFloat();
        return new Vector3f($$1, $$2, $$3);
    }

    public static Vector3f readVector3f(FriendlyByteBuf $$0) {
        return new Vector3f($$0.readFloat(), $$0.readFloat(), $$0.readFloat());
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeFloat(this.color.x());
        $$0.writeFloat(this.color.y());
        $$0.writeFloat(this.color.z());
        $$0.writeFloat(this.scale);
    }

    @Override
    public String writeToString() {
        return String.format((Locale)Locale.ROOT, (String)"%s %.2f %.2f %.2f %.2f", (Object[])new Object[]{BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf((float)this.color.x()), Float.valueOf((float)this.color.y()), Float.valueOf((float)this.color.z()), Float.valueOf((float)this.scale)});
    }

    public Vector3f getColor() {
        return this.color;
    }

    public float getScale() {
        return this.scale;
    }
}