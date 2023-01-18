/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public abstract class ContainerOpenersCounter {
    private static final int CHECK_TICK_DELAY = 5;
    private int openCount;

    protected abstract void onOpen(Level var1, BlockPos var2, BlockState var3);

    protected abstract void onClose(Level var1, BlockPos var2, BlockState var3);

    protected abstract void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5);

    protected abstract boolean isOwnContainer(Player var1);

    public void incrementOpeners(Player $$0, Level $$1, BlockPos $$2, BlockState $$3) {
        int $$4;
        if (($$4 = this.openCount++) == 0) {
            this.onOpen($$1, $$2, $$3);
            $$1.gameEvent($$0, GameEvent.CONTAINER_OPEN, $$2);
            ContainerOpenersCounter.scheduleRecheck($$1, $$2, $$3);
        }
        this.openerCountChanged($$1, $$2, $$3, $$4, this.openCount);
    }

    public void decrementOpeners(Player $$0, Level $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = this.openCount--;
        if (this.openCount == 0) {
            this.onClose($$1, $$2, $$3);
            $$1.gameEvent($$0, GameEvent.CONTAINER_CLOSE, $$2);
        }
        this.openerCountChanged($$1, $$2, $$3, $$4, this.openCount);
    }

    private int getOpenCount(Level $$0, BlockPos $$1) {
        int $$2 = $$1.getX();
        int $$3 = $$1.getY();
        int $$4 = $$1.getZ();
        float $$5 = 5.0f;
        AABB $$6 = new AABB((float)$$2 - 5.0f, (float)$$3 - 5.0f, (float)$$4 - 5.0f, (float)($$2 + 1) + 5.0f, (float)($$3 + 1) + 5.0f, (float)($$4 + 1) + 5.0f);
        return $$0.getEntities(EntityTypeTest.forClass(Player.class), $$6, this::isOwnContainer).size();
    }

    public void recheckOpeners(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$4 = this.openCount;
        int $$3 = this.getOpenCount($$0, $$1);
        if ($$4 != $$3) {
            boolean $$6;
            boolean $$5 = $$3 != 0;
            boolean bl = $$6 = $$4 != 0;
            if ($$5 && !$$6) {
                this.onOpen($$0, $$1, $$2);
                $$0.gameEvent(null, GameEvent.CONTAINER_OPEN, $$1);
            } else if (!$$5) {
                this.onClose($$0, $$1, $$2);
                $$0.gameEvent(null, GameEvent.CONTAINER_CLOSE, $$1);
            }
            this.openCount = $$3;
        }
        this.openerCountChanged($$0, $$1, $$2, $$4, $$3);
        if ($$3 > 0) {
            ContainerOpenersCounter.scheduleRecheck($$0, $$1, $$2);
        }
    }

    public int getOpenerCount() {
        return this.openCount;
    }

    private static void scheduleRecheck(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.scheduleTick($$1, $$2.getBlock(), 5);
    }
}