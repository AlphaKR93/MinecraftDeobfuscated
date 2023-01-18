/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity
extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private final BaseCommandBlock commandBlock = new BaseCommandBlock(){

        @Override
        public void setCommand(String $$0) {
            super.setCommand($$0);
            CommandBlockEntity.this.setChanged();
        }

        @Override
        public ServerLevel getLevel() {
            return (ServerLevel)CommandBlockEntity.this.level;
        }

        @Override
        public void onUpdated() {
            BlockState $$0 = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
            this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, $$0, $$0, 3);
        }

        @Override
        public Vec3 getPosition() {
            return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
        }

        @Override
        public CommandSourceStack createCommandSourceStack() {
            return new CommandSourceStack(this, Vec3.atCenterOf(CommandBlockEntity.this.worldPosition), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), null);
        }
    };

    public CommandBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.COMMAND_BLOCK, $$0, $$1);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        this.commandBlock.save($$0);
        $$0.putBoolean("powered", this.isPowered());
        $$0.putBoolean("conditionMet", this.wasConditionMet());
        $$0.putBoolean("auto", this.isAutomatic());
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.commandBlock.load($$0);
        this.powered = $$0.getBoolean("powered");
        this.conditionMet = $$0.getBoolean("conditionMet");
        this.setAutomatic($$0.getBoolean("auto"));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BaseCommandBlock getCommandBlock() {
        return this.commandBlock;
    }

    public void setPowered(boolean $$0) {
        this.powered = $$0;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAutomatic() {
        return this.auto;
    }

    public void setAutomatic(boolean $$0) {
        boolean $$1 = this.auto;
        this.auto = $$0;
        if (!$$1 && $$0 && !this.powered && this.level != null && this.getMode() != Mode.SEQUENCE) {
            this.scheduleTick();
        }
    }

    public void onModeSwitch() {
        Mode $$0 = this.getMode();
        if ($$0 == Mode.AUTO && (this.powered || this.auto) && this.level != null) {
            this.scheduleTick();
        }
    }

    private void scheduleTick() {
        Block $$0 = this.getBlockState().getBlock();
        if ($$0 instanceof CommandBlock) {
            this.markConditionMet();
            this.level.scheduleTick(this.worldPosition, $$0, 1);
        }
    }

    public boolean wasConditionMet() {
        return this.conditionMet;
    }

    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            BlockEntity $$1;
            Vec3i $$0 = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING).getOpposite());
            this.conditionMet = this.level.getBlockState((BlockPos)$$0).getBlock() instanceof CommandBlock ? ($$1 = this.level.getBlockEntity((BlockPos)$$0)) instanceof CommandBlockEntity && ((CommandBlockEntity)$$1).getCommandBlock().getSuccessCount() > 0 : false;
        }
        return this.conditionMet;
    }

    public Mode getMode() {
        BlockState $$0 = this.getBlockState();
        if ($$0.is(Blocks.COMMAND_BLOCK)) {
            return Mode.REDSTONE;
        }
        if ($$0.is(Blocks.REPEATING_COMMAND_BLOCK)) {
            return Mode.AUTO;
        }
        if ($$0.is(Blocks.CHAIN_COMMAND_BLOCK)) {
            return Mode.SEQUENCE;
        }
        return Mode.REDSTONE;
    }

    public boolean isConditional() {
        BlockState $$0 = this.level.getBlockState(this.getBlockPos());
        if ($$0.getBlock() instanceof CommandBlock) {
            return $$0.getValue(CommandBlock.CONDITIONAL);
        }
        return false;
    }

    public static enum Mode {
        SEQUENCE,
        AUTO,
        REDSTONE;

    }
}