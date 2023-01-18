/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity
extends BlockEntity
implements GameEventListener {
    private final BlockPositionSource blockPosSource;
    private final SculkSpreader sculkSpreader;

    public SculkCatalystBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.SCULK_CATALYST, $$0, $$1);
        this.blockPosSource = new BlockPositionSource(this.worldPosition);
        this.sculkSpreader = SculkSpreader.createLevelSpreader();
    }

    @Override
    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    @Override
    public GameEventListener.DeliveryMode getDeliveryMode() {
        return GameEventListener.DeliveryMode.BY_DISTANCE;
    }

    @Override
    public boolean handleGameEvent(ServerLevel $$0, GameEvent $$1, GameEvent.Context $$2, Vec3 $$3) {
        Entity entity;
        if ($$1 == GameEvent.ENTITY_DIE && (entity = $$2.sourceEntity()) instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)entity;
            if (!$$4.wasExperienceConsumed()) {
                int $$5 = $$4.getExperienceReward();
                if ($$4.shouldDropExperience() && $$5 > 0) {
                    this.sculkSpreader.addCursors(new BlockPos($$3.relative(Direction.UP, 0.5)), $$5);
                    this.tryAwardItSpreadsAdvancement($$4);
                }
                $$4.skipDropExperience();
                SculkCatalystBlock.bloom($$0, this.worldPosition, this.getBlockState(), $$0.getRandom());
            }
            return true;
        }
        return false;
    }

    private void tryAwardItSpreadsAdvancement(LivingEntity $$0) {
        LivingEntity $$1 = $$0.getLastHurtByMob();
        if ($$1 instanceof ServerPlayer) {
            ServerPlayer $$2 = (ServerPlayer)$$1;
            DamageSource $$3 = $$0.getLastDamageSource() == null ? DamageSource.playerAttack($$2) : $$0.getLastDamageSource();
            CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger($$2, $$0, $$3);
        }
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, SculkCatalystBlockEntity $$3) {
        $$3.sculkSpreader.updateCursors($$0, $$1, $$0.getRandom(), true);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.sculkSpreader.load($$0);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        this.sculkSpreader.save($$0);
        super.saveAdditional($$0);
    }

    @VisibleForTesting
    public SculkSpreader getSculkSpreader() {
        return this.sculkSpreader;
    }

    private static /* synthetic */ Integer lambda$saveAdditional$0(SculkSpreader.ChargeCursor $$0) {
        return 1;
    }
}