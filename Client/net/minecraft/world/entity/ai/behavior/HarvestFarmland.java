/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HarvestFarmland
extends Behavior<Villager> {
    private static final int HARVEST_DURATION = 200;
    public static final float SPEED_MODIFIER = 0.5f;
    @Nullable
    private BlockPos aboveFarmlandPos;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPos> validFarmlandAroundVillager = Lists.newArrayList();

    public HarvestFarmland() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.SECONDARY_JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Villager $$1) {
        if (!$$0.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if ($$1.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        BlockPos.MutableBlockPos $$2 = $$1.blockPosition().mutable();
        this.validFarmlandAroundVillager.clear();
        for (int $$3 = -1; $$3 <= 1; ++$$3) {
            for (int $$4 = -1; $$4 <= 1; ++$$4) {
                for (int $$5 = -1; $$5 <= 1; ++$$5) {
                    $$2.set($$1.getX() + (double)$$3, $$1.getY() + (double)$$4, $$1.getZ() + (double)$$5);
                    if (!this.validPos($$2, $$0)) continue;
                    this.validFarmlandAroundVillager.add((Object)new BlockPos($$2));
                }
            }
        }
        this.aboveFarmlandPos = this.getValidFarmland($$0);
        return this.aboveFarmlandPos != null;
    }

    @Nullable
    private BlockPos getValidFarmland(ServerLevel $$0) {
        return this.validFarmlandAroundVillager.isEmpty() ? null : (BlockPos)this.validFarmlandAroundVillager.get($$0.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
    }

    private boolean validPos(BlockPos $$0, ServerLevel $$1) {
        BlockState $$2 = $$1.getBlockState($$0);
        Block $$3 = $$2.getBlock();
        Block $$4 = $$1.getBlockState((BlockPos)$$0.below()).getBlock();
        return $$3 instanceof CropBlock && ((CropBlock)$$3).isMaxAge($$2) || $$2.isAir() && $$4 instanceof FarmBlock;
    }

    @Override
    protected void start(ServerLevel $$0, Villager $$1, long $$2) {
        if ($$2 > this.nextOkStartTime && this.aboveFarmlandPos != null) {
            $$1.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.aboveFarmlandPos));
            $$1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5f, 1));
        }
    }

    @Override
    protected void stop(ServerLevel $$0, Villager $$1, long $$2) {
        $$1.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        $$1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = $$2 + 40L;
    }

    @Override
    protected void tick(ServerLevel $$0, Villager $$1, long $$2) {
        if (this.aboveFarmlandPos != null && !this.aboveFarmlandPos.closerToCenterThan($$1.position(), 1.0)) {
            return;
        }
        if (this.aboveFarmlandPos != null && $$2 > this.nextOkStartTime) {
            BlockState $$3 = $$0.getBlockState(this.aboveFarmlandPos);
            Block $$4 = $$3.getBlock();
            Block $$5 = $$0.getBlockState((BlockPos)this.aboveFarmlandPos.below()).getBlock();
            if ($$4 instanceof CropBlock && ((CropBlock)$$4).isMaxAge($$3)) {
                $$0.destroyBlock(this.aboveFarmlandPos, true, $$1);
            }
            if ($$3.isAir() && $$5 instanceof FarmBlock && $$1.hasFarmSeeds()) {
                SimpleContainer $$6 = $$1.getInventory();
                for (int $$7 = 0; $$7 < $$6.getContainerSize(); ++$$7) {
                    ItemStack $$8 = $$6.getItem($$7);
                    boolean $$9 = false;
                    if (!$$8.isEmpty()) {
                        if ($$8.is(Items.WHEAT_SEEDS)) {
                            BlockState $$10 = Blocks.WHEAT.defaultBlockState();
                            $$0.setBlockAndUpdate(this.aboveFarmlandPos, $$10);
                            $$0.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of($$1, $$10));
                            $$9 = true;
                        } else if ($$8.is(Items.POTATO)) {
                            BlockState $$11 = Blocks.POTATOES.defaultBlockState();
                            $$0.setBlockAndUpdate(this.aboveFarmlandPos, $$11);
                            $$0.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of($$1, $$11));
                            $$9 = true;
                        } else if ($$8.is(Items.CARROT)) {
                            BlockState $$12 = Blocks.CARROTS.defaultBlockState();
                            $$0.setBlockAndUpdate(this.aboveFarmlandPos, $$12);
                            $$0.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of($$1, $$12));
                            $$9 = true;
                        } else if ($$8.is(Items.BEETROOT_SEEDS)) {
                            BlockState $$13 = Blocks.BEETROOTS.defaultBlockState();
                            $$0.setBlockAndUpdate(this.aboveFarmlandPos, $$13);
                            $$0.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of($$1, $$13));
                            $$9 = true;
                        }
                    }
                    if (!$$9) continue;
                    $$0.playSound(null, this.aboveFarmlandPos.getX(), this.aboveFarmlandPos.getY(), this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);
                    $$8.shrink(1);
                    if (!$$8.isEmpty()) break;
                    $$6.setItem($$7, ItemStack.EMPTY);
                    break;
                }
            }
            if ($$4 instanceof CropBlock && !((CropBlock)$$4).isMaxAge($$3)) {
                this.validFarmlandAroundVillager.remove((Object)this.aboveFarmlandPos);
                this.aboveFarmlandPos = this.getValidFarmland($$0);
                if (this.aboveFarmlandPos != null) {
                    this.nextOkStartTime = $$2 + 20L;
                    $$1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5f, 1));
                    $$1.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.aboveFarmlandPos));
                }
            }
        }
        ++this.timeWorkedSoFar;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        return this.timeWorkedSoFar < 200;
    }
}