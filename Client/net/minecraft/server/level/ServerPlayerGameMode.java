/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.level;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerPlayerGameMode {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected ServerLevel level;
    protected final ServerPlayer player;
    private GameType gameModeForPlayer = GameType.DEFAULT_MODE;
    @Nullable
    private GameType previousGameModeForPlayer;
    private boolean isDestroyingBlock;
    private int destroyProgressStart;
    private BlockPos destroyPos = BlockPos.ZERO;
    private int gameTicks;
    private boolean hasDelayedDestroy;
    private BlockPos delayedDestroyPos = BlockPos.ZERO;
    private int delayedTickStart;
    private int lastSentState = -1;

    public ServerPlayerGameMode(ServerPlayer $$0) {
        this.player = $$0;
        this.level = $$0.getLevel();
    }

    public boolean changeGameModeForPlayer(GameType $$0) {
        if ($$0 == this.gameModeForPlayer) {
            return false;
        }
        this.setGameModeForPlayer($$0, this.previousGameModeForPlayer);
        this.player.onUpdateAbilities();
        this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, this.player));
        this.level.updateSleepingPlayerList();
        return true;
    }

    protected void setGameModeForPlayer(GameType $$0, @Nullable GameType $$1) {
        this.previousGameModeForPlayer = $$1;
        this.gameModeForPlayer = $$0;
        $$0.updatePlayerAbilities(this.player.getAbilities());
    }

    public GameType getGameModeForPlayer() {
        return this.gameModeForPlayer;
    }

    @Nullable
    public GameType getPreviousGameModeForPlayer() {
        return this.previousGameModeForPlayer;
    }

    public boolean isSurvival() {
        return this.gameModeForPlayer.isSurvival();
    }

    public boolean isCreative() {
        return this.gameModeForPlayer.isCreative();
    }

    public void tick() {
        ++this.gameTicks;
        if (this.hasDelayedDestroy) {
            BlockState $$0 = this.level.getBlockState(this.delayedDestroyPos);
            if ($$0.isAir()) {
                this.hasDelayedDestroy = false;
            } else {
                float $$1 = this.incrementDestroyProgress($$0, this.delayedDestroyPos, this.delayedTickStart);
                if ($$1 >= 1.0f) {
                    this.hasDelayedDestroy = false;
                    this.destroyBlock(this.delayedDestroyPos);
                }
            }
        } else if (this.isDestroyingBlock) {
            BlockState $$2 = this.level.getBlockState(this.destroyPos);
            if ($$2.isAir()) {
                this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                this.lastSentState = -1;
                this.isDestroyingBlock = false;
            } else {
                this.incrementDestroyProgress($$2, this.destroyPos, this.destroyProgressStart);
            }
        }
    }

    private float incrementDestroyProgress(BlockState $$0, BlockPos $$1, int $$2) {
        int $$3 = this.gameTicks - $$2;
        float $$4 = $$0.getDestroyProgress(this.player, this.player.level, $$1) * (float)($$3 + 1);
        int $$5 = (int)($$4 * 10.0f);
        if ($$5 != this.lastSentState) {
            this.level.destroyBlockProgress(this.player.getId(), $$1, $$5);
            this.lastSentState = $$5;
        }
        return $$4;
    }

    private void debugLogging(BlockPos $$0, boolean $$1, int $$2, String $$3) {
    }

    public void handleBlockBreakAction(BlockPos $$0, ServerboundPlayerActionPacket.Action $$1, Direction $$2, int $$3, int $$4) {
        if (this.player.getEyePosition().distanceToSqr(Vec3.atCenterOf($$0)) > ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) {
            this.debugLogging($$0, false, $$4, "too far");
            return;
        }
        if ($$0.getY() >= $$3) {
            this.player.connection.send(new ClientboundBlockUpdatePacket($$0, this.level.getBlockState($$0)));
            this.debugLogging($$0, false, $$4, "too high");
            return;
        }
        if ($$1 == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, $$0)) {
                this.player.connection.send(new ClientboundBlockUpdatePacket($$0, this.level.getBlockState($$0)));
                this.debugLogging($$0, false, $$4, "may not interact");
                return;
            }
            if (this.isCreative()) {
                this.destroyAndAck($$0, $$4, "creative destroy");
                return;
            }
            if (this.player.blockActionRestricted(this.level, $$0, this.gameModeForPlayer)) {
                this.player.connection.send(new ClientboundBlockUpdatePacket($$0, this.level.getBlockState($$0)));
                this.debugLogging($$0, false, $$4, "block action restricted");
                return;
            }
            this.destroyProgressStart = this.gameTicks;
            float $$5 = 1.0f;
            BlockState $$6 = this.level.getBlockState($$0);
            if (!$$6.isAir()) {
                $$6.attack(this.level, $$0, this.player);
                $$5 = $$6.getDestroyProgress(this.player, this.player.level, $$0);
            }
            if (!$$6.isAir() && $$5 >= 1.0f) {
                this.destroyAndAck($$0, $$4, "insta mine");
            } else {
                if (this.isDestroyingBlock) {
                    this.player.connection.send(new ClientboundBlockUpdatePacket(this.destroyPos, this.level.getBlockState(this.destroyPos)));
                    this.debugLogging($$0, false, $$4, "abort destroying since another started (client insta mine, server disagreed)");
                }
                this.isDestroyingBlock = true;
                this.destroyPos = $$0.immutable();
                int $$7 = (int)($$5 * 10.0f);
                this.level.destroyBlockProgress(this.player.getId(), $$0, $$7);
                this.debugLogging($$0, true, $$4, "actual start of destroying");
                this.lastSentState = $$7;
            }
        } else if ($$1 == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if ($$0.equals(this.destroyPos)) {
                int $$8 = this.gameTicks - this.destroyProgressStart;
                BlockState $$9 = this.level.getBlockState($$0);
                if (!$$9.isAir()) {
                    float $$10 = $$9.getDestroyProgress(this.player, this.player.level, $$0) * (float)($$8 + 1);
                    if ($$10 >= 0.7f) {
                        this.isDestroyingBlock = false;
                        this.level.destroyBlockProgress(this.player.getId(), $$0, -1);
                        this.destroyAndAck($$0, $$4, "destroyed");
                        return;
                    }
                    if (!this.hasDelayedDestroy) {
                        this.isDestroyingBlock = false;
                        this.hasDelayedDestroy = true;
                        this.delayedDestroyPos = $$0;
                        this.delayedTickStart = this.destroyProgressStart;
                    }
                }
            }
            this.debugLogging($$0, true, $$4, "stopped destroying");
        } else if ($$1 == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals((Object)this.destroyPos, (Object)$$0)) {
                LOGGER.warn("Mismatch in destroy block pos: {} {}", (Object)this.destroyPos, (Object)$$0);
                this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
                this.debugLogging($$0, true, $$4, "aborted mismatched destroying");
            }
            this.level.destroyBlockProgress(this.player.getId(), $$0, -1);
            this.debugLogging($$0, true, $$4, "aborted destroying");
        }
    }

    public void destroyAndAck(BlockPos $$0, int $$1, String $$2) {
        if (this.destroyBlock($$0)) {
            this.debugLogging($$0, true, $$1, $$2);
        } else {
            this.player.connection.send(new ClientboundBlockUpdatePacket($$0, this.level.getBlockState($$0)));
            this.debugLogging($$0, false, $$1, $$2);
        }
    }

    public boolean destroyBlock(BlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        if (!this.player.getMainHandItem().getItem().canAttackBlock($$1, this.level, $$0, this.player)) {
            return false;
        }
        BlockEntity $$2 = this.level.getBlockEntity($$0);
        Block $$3 = $$1.getBlock();
        if ($$3 instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated($$0, $$1, $$1, 3);
            return false;
        }
        if (this.player.blockActionRestricted(this.level, $$0, this.gameModeForPlayer)) {
            return false;
        }
        $$3.playerWillDestroy(this.level, $$0, $$1, this.player);
        boolean $$4 = this.level.removeBlock($$0, false);
        if ($$4) {
            $$3.destroy(this.level, $$0, $$1);
        }
        if (this.isCreative()) {
            return true;
        }
        ItemStack $$5 = this.player.getMainHandItem();
        ItemStack $$6 = $$5.copy();
        boolean $$7 = this.player.hasCorrectToolForDrops($$1);
        $$5.mineBlock(this.level, $$1, $$0, this.player);
        if ($$4 && $$7) {
            $$3.playerDestroy(this.level, this.player, $$0, $$1, $$2, $$6);
        }
        return true;
    }

    public InteractionResult useItem(ServerPlayer $$0, Level $$1, ItemStack $$2, InteractionHand $$3) {
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            return InteractionResult.PASS;
        }
        if ($$0.getCooldowns().isOnCooldown($$2.getItem())) {
            return InteractionResult.PASS;
        }
        int $$4 = $$2.getCount();
        int $$5 = $$2.getDamageValue();
        InteractionResultHolder<ItemStack> $$6 = $$2.use($$1, $$0, $$3);
        ItemStack $$7 = $$6.getObject();
        if ($$7 == $$2 && $$7.getCount() == $$4 && $$7.getUseDuration() <= 0 && $$7.getDamageValue() == $$5) {
            return $$6.getResult();
        }
        if ($$6.getResult() == InteractionResult.FAIL && $$7.getUseDuration() > 0 && !$$0.isUsingItem()) {
            return $$6.getResult();
        }
        if ($$2 != $$7) {
            $$0.setItemInHand($$3, $$7);
        }
        if (this.isCreative()) {
            $$7.setCount($$4);
            if ($$7.isDamageableItem() && $$7.getDamageValue() != $$5) {
                $$7.setDamageValue($$5);
            }
        }
        if ($$7.isEmpty()) {
            $$0.setItemInHand($$3, ItemStack.EMPTY);
        }
        if (!$$0.isUsingItem()) {
            $$0.inventoryMenu.sendAllDataToRemote();
        }
        return $$6.getResult();
    }

    public InteractionResult useItemOn(ServerPlayer $$0, Level $$1, ItemStack $$2, InteractionHand $$3, BlockHitResult $$4) {
        InteractionResult $$15;
        InteractionResult $$11;
        BlockPos $$5 = $$4.getBlockPos();
        BlockState $$6 = $$1.getBlockState($$5);
        if (!$$6.getBlock().isEnabled($$1.enabledFeatures())) {
            return InteractionResult.FAIL;
        }
        if (this.gameModeForPlayer == GameType.SPECTATOR) {
            MenuProvider $$7 = $$6.getMenuProvider($$1, $$5);
            if ($$7 != null) {
                $$0.openMenu($$7);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        boolean $$8 = !$$0.getMainHandItem().isEmpty() || !$$0.getOffhandItem().isEmpty();
        boolean $$9 = $$0.isSecondaryUseActive() && $$8;
        ItemStack $$10 = $$2.copy();
        if (!$$9 && ($$11 = $$6.use($$1, $$0, $$3, $$4)).consumesAction()) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger($$0, $$5, $$10);
            return $$11;
        }
        if ($$2.isEmpty() || $$0.getCooldowns().isOnCooldown($$2.getItem())) {
            return InteractionResult.PASS;
        }
        UseOnContext $$12 = new UseOnContext($$0, $$3, $$4);
        if (this.isCreative()) {
            int $$13 = $$2.getCount();
            InteractionResult $$14 = $$2.useOn($$12);
            $$2.setCount($$13);
        } else {
            $$15 = $$2.useOn($$12);
        }
        if ($$15.consumesAction()) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger($$0, $$5, $$10);
        }
        return $$15;
    }

    public void setLevel(ServerLevel $$0) {
        this.level = $$0;
    }
}