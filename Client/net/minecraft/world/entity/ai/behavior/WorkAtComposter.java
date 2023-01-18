/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WorkAtComposter
extends WorkAtPoi {
    private static final List<Item> COMPOSTABLE_ITEMS = ImmutableList.of((Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS);

    @Override
    protected void useWorkstation(ServerLevel $$0, Villager $$1) {
        Optional<GlobalPos> $$2 = $$1.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (!$$2.isPresent()) {
            return;
        }
        GlobalPos $$3 = (GlobalPos)$$2.get();
        BlockState $$4 = $$0.getBlockState($$3.pos());
        if ($$4.is(Blocks.COMPOSTER)) {
            this.makeBread($$1);
            this.compostItems($$0, $$1, $$3, $$4);
        }
    }

    private void compostItems(ServerLevel $$0, Villager $$1, GlobalPos $$2, BlockState $$3) {
        BlockPos $$4 = $$2.pos();
        if ($$3.getValue(ComposterBlock.LEVEL) == 8) {
            $$3 = ComposterBlock.extractProduce($$3, $$0, $$4);
        }
        int $$5 = 20;
        int $$6 = 10;
        int[] $$7 = new int[COMPOSTABLE_ITEMS.size()];
        SimpleContainer $$8 = $$1.getInventory();
        int $$9 = $$8.getContainerSize();
        BlockState $$10 = $$3;
        for (int $$11 = $$9 - 1; $$11 >= 0 && $$5 > 0; --$$11) {
            int $$15;
            ItemStack $$12 = $$8.getItem($$11);
            int $$13 = COMPOSTABLE_ITEMS.indexOf((Object)$$12.getItem());
            if ($$13 == -1) continue;
            int $$14 = $$12.getCount();
            $$7[$$13] = $$15 = $$7[$$13] + $$14;
            int $$16 = Math.min((int)Math.min((int)($$15 - 10), (int)$$5), (int)$$14);
            if ($$16 <= 0) continue;
            $$5 -= $$16;
            for (int $$17 = 0; $$17 < $$16; ++$$17) {
                if (($$10 = ComposterBlock.insertItem($$10, $$0, $$12, $$4)).getValue(ComposterBlock.LEVEL) != 7) continue;
                this.spawnComposterFillEffects($$0, $$3, $$4, $$10);
                return;
            }
        }
        this.spawnComposterFillEffects($$0, $$3, $$4, $$10);
    }

    private void spawnComposterFillEffects(ServerLevel $$0, BlockState $$1, BlockPos $$2, BlockState $$3) {
        $$0.levelEvent(1500, $$2, $$3 != $$1 ? 1 : 0);
    }

    private void makeBread(Villager $$0) {
        SimpleContainer $$1 = $$0.getInventory();
        if ($$1.countItem(Items.BREAD) > 36) {
            return;
        }
        int $$2 = $$1.countItem(Items.WHEAT);
        int $$3 = 3;
        int $$4 = 3;
        int $$5 = Math.min((int)3, (int)($$2 / 3));
        if ($$5 == 0) {
            return;
        }
        int $$6 = $$5 * 3;
        $$1.removeItemType(Items.WHEAT, $$6);
        ItemStack $$7 = $$1.addItem(new ItemStack(Items.BREAD, $$5));
        if (!$$7.isEmpty()) {
            $$0.spawnAtLocation($$7, 0.5f);
        }
    }
}