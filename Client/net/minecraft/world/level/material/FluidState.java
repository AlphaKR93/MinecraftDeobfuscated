/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FluidState
extends StateHolder<Fluid, FluidState> {
    public static final Codec<FluidState> CODEC = FluidState.codec(BuiltInRegistries.FLUID.byNameCodec(), Fluid::defaultFluidState).stable();
    public static final int AMOUNT_MAX = 9;
    public static final int AMOUNT_FULL = 8;

    public FluidState(Fluid $$0, ImmutableMap<Property<?>, Comparable<?>> $$1, MapCodec<FluidState> $$2) {
        super($$0, $$1, $$2);
    }

    public Fluid getType() {
        return (Fluid)this.owner;
    }

    public boolean isSource() {
        return this.getType().isSource(this);
    }

    public boolean isSourceOfType(Fluid $$0) {
        return this.owner == $$0 && ((Fluid)this.owner).isSource(this);
    }

    public boolean isEmpty() {
        return this.getType().isEmpty();
    }

    public float getHeight(BlockGetter $$0, BlockPos $$1) {
        return this.getType().getHeight(this, $$0, $$1);
    }

    public float getOwnHeight() {
        return this.getType().getOwnHeight(this);
    }

    public int getAmount() {
        return this.getType().getAmount(this);
    }

    public boolean shouldRenderBackwardUpFace(BlockGetter $$0, BlockPos $$1) {
        for (int $$2 = -1; $$2 <= 1; ++$$2) {
            for (int $$3 = -1; $$3 <= 1; ++$$3) {
                BlockPos $$4 = $$1.offset($$2, 0, $$3);
                FluidState $$5 = $$0.getFluidState($$4);
                if ($$5.getType().isSame(this.getType()) || $$0.getBlockState($$4).isSolidRender($$0, $$4)) continue;
                return true;
            }
        }
        return false;
    }

    public void tick(Level $$0, BlockPos $$1) {
        this.getType().tick($$0, $$1, this);
    }

    public void animateTick(Level $$0, BlockPos $$1, RandomSource $$2) {
        this.getType().animateTick($$0, $$1, this, $$2);
    }

    public boolean isRandomlyTicking() {
        return this.getType().isRandomlyTicking();
    }

    public void randomTick(Level $$0, BlockPos $$1, RandomSource $$2) {
        this.getType().randomTick($$0, $$1, this, $$2);
    }

    public Vec3 getFlow(BlockGetter $$0, BlockPos $$1) {
        return this.getType().getFlow($$0, $$1, this);
    }

    public BlockState createLegacyBlock() {
        return this.getType().createLegacyBlock(this);
    }

    @Nullable
    public ParticleOptions getDripParticle() {
        return this.getType().getDripParticle();
    }

    public boolean is(TagKey<Fluid> $$0) {
        return this.getType().builtInRegistryHolder().is($$0);
    }

    public boolean is(HolderSet<Fluid> $$0) {
        return $$0.contains(this.getType().builtInRegistryHolder());
    }

    public boolean is(Fluid $$0) {
        return this.getType() == $$0;
    }

    public float getExplosionResistance() {
        return this.getType().getExplosionResistance();
    }

    public boolean canBeReplacedWith(BlockGetter $$0, BlockPos $$1, Fluid $$2, Direction $$3) {
        return this.getType().canBeReplacedWith(this, $$0, $$1, $$2, $$3);
    }

    public VoxelShape getShape(BlockGetter $$0, BlockPos $$1) {
        return this.getType().getShape(this, $$0, $$1);
    }

    public Holder<Fluid> holder() {
        return ((Fluid)this.owner).builtInRegistryHolder();
    }

    public Stream<TagKey<Fluid>> getTags() {
        return ((Fluid)this.owner).builtInRegistryHolder().tags();
    }
}