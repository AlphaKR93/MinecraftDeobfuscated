/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 */
package net.minecraft.core;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class GlobalPos {
    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), (App)BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply((Applicative)$$0, GlobalPos::of));
    private final ResourceKey<Level> dimension;
    private final BlockPos pos;

    private GlobalPos(ResourceKey<Level> $$0, BlockPos $$1) {
        this.dimension = $$0;
        this.pos = $$1;
    }

    public static GlobalPos of(ResourceKey<Level> $$0, BlockPos $$1) {
        return new GlobalPos($$0, $$1);
    }

    public ResourceKey<Level> dimension() {
        return this.dimension;
    }

    public BlockPos pos() {
        return this.pos;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 == null || this.getClass() != $$0.getClass()) {
            return false;
        }
        GlobalPos $$1 = (GlobalPos)$$0;
        return Objects.equals(this.dimension, $$1.dimension) && Objects.equals((Object)this.pos, (Object)$$1.pos);
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.dimension, this.pos});
    }

    public String toString() {
        return this.dimension + " " + this.pos;
    }
}