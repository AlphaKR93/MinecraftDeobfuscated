/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.OptionalInt
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.decoration;

import com.mojang.logging.LogUtils;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ItemFrame
extends HangingEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
    public static final int NUM_ROTATIONS = 8;
    private float dropChance = 1.0f;
    private boolean fixed;

    public ItemFrame(EntityType<? extends ItemFrame> $$0, Level $$1) {
        super((EntityType<? extends HangingEntity>)$$0, $$1);
    }

    public ItemFrame(Level $$0, BlockPos $$1, Direction $$2) {
        this(EntityType.ITEM_FRAME, $$0, $$1, $$2);
    }

    public ItemFrame(EntityType<? extends ItemFrame> $$0, Level $$1, BlockPos $$2, Direction $$3) {
        super($$0, $$1, $$2);
        this.setDirection($$3);
    }

    @Override
    protected float getEyeHeight(Pose $$0, EntityDimensions $$1) {
        return 0.0f;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_ROTATION, 0);
    }

    @Override
    protected void setDirection(Direction $$0) {
        Validate.notNull((Object)$$0);
        this.direction = $$0;
        if ($$0.getAxis().isHorizontal()) {
            this.setXRot(0.0f);
            this.setYRot(this.direction.get2DDataValue() * 90);
        } else {
            this.setXRot(-90 * $$0.getAxisDirection().getStep());
            this.setYRot(0.0f);
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        double $$0 = 0.46875;
        double $$1 = (double)this.pos.getX() + 0.5 - (double)this.direction.getStepX() * 0.46875;
        double $$2 = (double)this.pos.getY() + 0.5 - (double)this.direction.getStepY() * 0.46875;
        double $$3 = (double)this.pos.getZ() + 0.5 - (double)this.direction.getStepZ() * 0.46875;
        this.setPosRaw($$1, $$2, $$3);
        double $$4 = this.getWidth();
        double $$5 = this.getHeight();
        double $$6 = this.getWidth();
        Direction.Axis $$7 = this.direction.getAxis();
        switch ($$7) {
            case X: {
                $$4 = 1.0;
                break;
            }
            case Y: {
                $$5 = 1.0;
                break;
            }
            case Z: {
                $$6 = 1.0;
            }
        }
        this.setBoundingBox(new AABB($$1 - ($$4 /= 32.0), $$2 - ($$5 /= 32.0), $$3 - ($$6 /= 32.0), $$1 + $$4, $$2 + $$5, $$3 + $$6));
    }

    @Override
    public boolean survives() {
        if (this.fixed) {
            return true;
        }
        if (!this.level.noCollision(this)) {
            return false;
        }
        BlockState $$0 = this.level.getBlockState((BlockPos)this.pos.relative(this.direction.getOpposite()));
        if (!($$0.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode($$0))) {
            return false;
        }
        return this.level.getEntities(this, this.getBoundingBox(), (Predicate<? super Entity>)HANGING_ENTITY).isEmpty();
    }

    @Override
    public void move(MoverType $$0, Vec3 $$1) {
        if (!this.fixed) {
            super.move($$0, $$1);
        }
    }

    @Override
    public void push(double $$0, double $$1, double $$2) {
        if (!this.fixed) {
            super.push($$0, $$1, $$2);
        }
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    @Override
    public void kill() {
        this.removeFramedMap(this.getItem());
        super.kill();
    }

    @Override
    public boolean hurt(DamageSource $$0, float $$1) {
        if (this.fixed) {
            if ($$0 == DamageSource.OUT_OF_WORLD || $$0.isCreativePlayer()) {
                return super.hurt($$0, $$1);
            }
            return false;
        }
        if (this.isInvulnerableTo($$0)) {
            return false;
        }
        if (!$$0.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem($$0.getEntity(), false);
                this.playSound(this.getRemoveItemSound(), 1.0f, 1.0f);
            }
            return true;
        }
        return super.hurt($$0, $$1);
    }

    public SoundEvent getRemoveItemSound() {
        return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double $$0) {
        double $$1 = 16.0;
        return $$0 < ($$1 *= 64.0 * ItemFrame.getViewScale()) * $$1;
    }

    @Override
    public void dropItem(@Nullable Entity $$0) {
        this.playSound(this.getBreakSound(), 1.0f, 1.0f);
        this.dropItem($$0, true);
    }

    public SoundEvent getBreakSound() {
        return SoundEvents.ITEM_FRAME_BREAK;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(this.getPlaceSound(), 1.0f, 1.0f);
    }

    public SoundEvent getPlaceSound() {
        return SoundEvents.ITEM_FRAME_PLACE;
    }

    private void dropItem(@Nullable Entity $$0, boolean $$1) {
        if (this.fixed) {
            return;
        }
        ItemStack $$2 = this.getItem();
        this.setItem(ItemStack.EMPTY);
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if ($$0 == null) {
                this.removeFramedMap($$2);
            }
            return;
        }
        if ($$0 instanceof Player) {
            Player $$3 = (Player)$$0;
            if ($$3.getAbilities().instabuild) {
                this.removeFramedMap($$2);
                return;
            }
        }
        if ($$1) {
            this.spawnAtLocation(this.getFrameItemStack());
        }
        if (!$$2.isEmpty()) {
            $$2 = $$2.copy();
            this.removeFramedMap($$2);
            if (this.random.nextFloat() < this.dropChance) {
                this.spawnAtLocation($$2);
            }
        }
    }

    private void removeFramedMap(ItemStack $$02) {
        this.getFramedMapId().ifPresent($$0 -> {
            MapItemSavedData $$1 = MapItem.getSavedData($$0, this.level);
            if ($$1 != null) {
                $$1.removedFromFrame(this.pos, this.getId());
                $$1.setDirty(true);
            }
        });
        $$02.setEntityRepresentation(null);
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public OptionalInt getFramedMapId() {
        Integer $$1;
        ItemStack $$0 = this.getItem();
        if ($$0.is(Items.FILLED_MAP) && ($$1 = MapItem.getMapId($$0)) != null) {
            return OptionalInt.of((int)$$1);
        }
        return OptionalInt.empty();
    }

    public boolean hasFramedMap() {
        return this.getFramedMapId().isPresent();
    }

    public void setItem(ItemStack $$0) {
        this.setItem($$0, true);
    }

    public void setItem(ItemStack $$0, boolean $$1) {
        if (!$$0.isEmpty()) {
            $$0 = $$0.copy();
            $$0.setCount(1);
        }
        this.onItemChanged($$0);
        this.getEntityData().set(DATA_ITEM, $$0);
        if (!$$0.isEmpty()) {
            this.playSound(this.getAddItemSound(), 1.0f, 1.0f);
        }
        if ($$1 && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    public SoundEvent getAddItemSound() {
        return SoundEvents.ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public SlotAccess getSlot(int $$0) {
        if ($$0 == 0) {
            return new SlotAccess(){

                @Override
                public ItemStack get() {
                    return ItemFrame.this.getItem();
                }

                @Override
                public boolean set(ItemStack $$0) {
                    ItemFrame.this.setItem($$0);
                    return true;
                }
            };
        }
        return super.getSlot($$0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if ($$0.equals(DATA_ITEM)) {
            this.onItemChanged(this.getItem());
        }
    }

    private void onItemChanged(ItemStack $$0) {
        if (!$$0.isEmpty() && $$0.getFrame() != this) {
            $$0.setEntityRepresentation(this);
        }
        this.recalculateBoundingBox();
    }

    public int getRotation() {
        return this.getEntityData().get(DATA_ROTATION);
    }

    public void setRotation(int $$0) {
        this.setRotation($$0, true);
    }

    private void setRotation(int $$0, boolean $$1) {
        this.getEntityData().set(DATA_ROTATION, $$0 % 8);
        if ($$1 && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag $$0) {
        super.addAdditionalSaveData($$0);
        if (!this.getItem().isEmpty()) {
            $$0.put("Item", this.getItem().save(new CompoundTag()));
            $$0.putByte("ItemRotation", (byte)this.getRotation());
            $$0.putFloat("ItemDropChance", this.dropChance);
        }
        $$0.putByte("Facing", (byte)this.direction.get3DDataValue());
        $$0.putBoolean("Invisible", this.isInvisible());
        $$0.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag $$0) {
        super.readAdditionalSaveData($$0);
        CompoundTag $$1 = $$0.getCompound("Item");
        if ($$1 != null && !$$1.isEmpty()) {
            ItemStack $$3;
            ItemStack $$2 = ItemStack.of($$1);
            if ($$2.isEmpty()) {
                LOGGER.warn("Unable to load item from: {}", (Object)$$1);
            }
            if (!($$3 = this.getItem()).isEmpty() && !ItemStack.matches($$2, $$3)) {
                this.removeFramedMap($$3);
            }
            this.setItem($$2, false);
            this.setRotation($$0.getByte("ItemRotation"), false);
            if ($$0.contains("ItemDropChance", 99)) {
                this.dropChance = $$0.getFloat("ItemDropChance");
            }
        }
        this.setDirection(Direction.from3DDataValue($$0.getByte("Facing")));
        this.setInvisible($$0.getBoolean("Invisible"));
        this.fixed = $$0.getBoolean("Fixed");
    }

    @Override
    public InteractionResult interact(Player $$0, InteractionHand $$1) {
        boolean $$4;
        ItemStack $$2 = $$0.getItemInHand($$1);
        boolean $$3 = !this.getItem().isEmpty();
        boolean bl = $$4 = !$$2.isEmpty();
        if (this.fixed) {
            return InteractionResult.PASS;
        }
        if (this.level.isClientSide) {
            return $$3 || $$4 ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!$$3) {
            if ($$4 && !this.isRemoved()) {
                MapItemSavedData $$5;
                if ($$2.is(Items.FILLED_MAP) && ($$5 = MapItem.getSavedData($$2, this.level)) != null && $$5.isTrackedCountOverLimit(256)) {
                    return InteractionResult.FAIL;
                }
                this.setItem($$2);
                if (!$$0.getAbilities().instabuild) {
                    $$2.shrink(1);
                }
            }
        } else {
            this.playSound(this.getRotateItemSound(), 1.0f, 1.0f);
            this.setRotation(this.getRotation() + 1);
        }
        return InteractionResult.CONSUME;
    }

    public SoundEvent getRotateItemSound() {
        return SoundEvents.ITEM_FRAME_ROTATE_ITEM;
    }

    public int getAnalogOutput() {
        if (this.getItem().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket $$0) {
        super.recreateFromPacket($$0);
        this.setDirection(Direction.from3DDataValue($$0.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack $$0 = this.getItem();
        if ($$0.isEmpty()) {
            return this.getFrameItemStack();
        }
        return $$0.copy();
    }

    protected ItemStack getFrameItemStack() {
        return new ItemStack(Items.ITEM_FRAME);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        Direction $$0 = this.getDirection();
        int $$1 = $$0.getAxis().isVertical() ? 90 * $$0.getAxisDirection().getStep() : 0;
        return Mth.wrapDegrees(180 + $$0.get2DDataValue() * 90 + this.getRotation() * 45 + $$1);
    }
}