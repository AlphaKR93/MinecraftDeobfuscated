/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace
extends AbstractMinecart {
    private static final EntityDataAccessor<Boolean> DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
    private int fuel;
    public double xPush;
    public double zPush;
    private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

    public MinecartFurnace(EntityType<? extends MinecartFurnace> $$0, Level $$1) {
        super($$0, $$1);
    }

    public MinecartFurnace(Level $$0, double $$1, double $$2, double $$3) {
        super(EntityType.FURNACE_MINECART, $$0, $$1, $$2, $$3);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.FURNACE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FUEL, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            if (this.fuel > 0) {
                --this.fuel;
            }
            if (this.fuel <= 0) {
                this.xPush = 0.0;
                this.zPush = 0.0;
            }
            this.setHasFuel(this.fuel > 0);
        }
        if (this.hasFuel() && this.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected double getMaxSpeed() {
        return (this.isInWater() ? 3.0 : 4.0) / 20.0;
    }

    @Override
    protected Item getDropItem() {
        return Items.FURNACE_MINECART;
    }

    @Override
    protected void moveAlongTrack(BlockPos $$0, BlockState $$1) {
        double $$2 = 1.0E-4;
        double $$3 = 0.001;
        super.moveAlongTrack($$0, $$1);
        Vec3 $$4 = this.getDeltaMovement();
        double $$5 = $$4.horizontalDistanceSqr();
        double $$6 = this.xPush * this.xPush + this.zPush * this.zPush;
        if ($$6 > 1.0E-4 && $$5 > 0.001) {
            double $$7 = Math.sqrt((double)$$5);
            double $$8 = Math.sqrt((double)$$6);
            this.xPush = $$4.x / $$7 * $$8;
            this.zPush = $$4.z / $$7 * $$8;
        }
    }

    @Override
    protected void applyNaturalSlowdown() {
        double $$0 = this.xPush * this.xPush + this.zPush * this.zPush;
        if ($$0 > 1.0E-7) {
            $$0 = Math.sqrt((double)$$0);
            this.xPush /= $$0;
            this.zPush /= $$0;
            Vec3 $$1 = this.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(this.xPush, 0.0, this.zPush);
            if (this.isInWater()) {
                $$1 = $$1.scale(0.1);
            }
            this.setDeltaMovement($$1);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.0, 0.98));
        }
        super.applyNaturalSlowdown();
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        ItemStack $$2 = $$0.getItemInHand($$1);
        if (INGREDIENT.test($$2) && this.fuel + 3600 <= 32000) {
            if (!$$0.getAbilities().instabuild) {
                $$2.shrink(1);
            }
            this.fuel += 3600;
        }
        if (this.fuel > 0) {
            this.xPush = this.getX() - $$0.getX();
            this.zPush = this.getZ() - $$0.getZ();
        }
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        $$0.putDouble("PushX", this.xPush);
        $$0.putDouble("PushZ", this.zPush);
        $$0.putShort("Fuel", (short)this.fuel);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        this.xPush = $$0.getDouble("PushX");
        this.zPush = $$0.getDouble("PushZ");
        this.fuel = $$0.getShort("Fuel");
    }

    protected boolean hasFuel() {
        return this.entityData.get(DATA_ID_FUEL);
    }

    protected void setHasFuel(boolean $$0) {
        this.entityData.set(DATA_ID_FUEL, $$0);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return (BlockState)((BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH)).setValue(FurnaceBlock.LIT, this.hasFuel());
    }
}