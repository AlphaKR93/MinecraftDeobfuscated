/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class InfestedBlock
extends Block {
    private final Block hostBlock;
    private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
    private static final Map<BlockState, BlockState> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

    public InfestedBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1.destroyTime($$0.defaultDestroyTime() / 2.0f).explosionResistance(0.75f));
        this.hostBlock = $$0;
        BLOCK_BY_HOST_BLOCK.put((Object)$$0, (Object)this);
    }

    public Block getHostBlock() {
        return this.hostBlock;
    }

    public static boolean isCompatibleHostBlock(BlockState $$0) {
        return BLOCK_BY_HOST_BLOCK.containsKey((Object)$$0.getBlock());
    }

    private void spawnInfestation(ServerLevel $$0, BlockPos $$1) {
        Silverfish $$2 = EntityType.SILVERFISH.create($$0);
        if ($$2 != null) {
            $$2.moveTo((double)$$1.getX() + 0.5, $$1.getY(), (double)$$1.getZ() + 0.5, 0.0f, 0.0f);
            $$0.addFreshEntity($$2);
            $$2.spawnAnim();
        }
    }

    @Override
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, $$3) == 0) {
            this.spawnInfestation($$1, $$2);
        }
    }

    public static BlockState infestedStateByHost(BlockState $$0) {
        return InfestedBlock.getNewStateWithProperties(HOST_TO_INFESTED_STATES, $$0, (Supplier<BlockState>)((Supplier)() -> ((Block)BLOCK_BY_HOST_BLOCK.get((Object)$$0.getBlock())).defaultBlockState()));
    }

    public BlockState hostStateByInfested(BlockState $$0) {
        return InfestedBlock.getNewStateWithProperties(INFESTED_TO_HOST_STATES, $$0, (Supplier<BlockState>)((Supplier)() -> this.getHostBlock().defaultBlockState()));
    }

    private static BlockState getNewStateWithProperties(Map<BlockState, BlockState> $$0, BlockState $$12, Supplier<BlockState> $$2) {
        return (BlockState)$$0.computeIfAbsent((Object)$$12, $$1 -> {
            BlockState $$2 = (BlockState)$$2.get();
            for (Property $$3 : $$1.getProperties()) {
                $$2 = $$2.hasProperty($$3) ? (BlockState)$$2.setValue($$3, $$1.getValue($$3)) : $$2;
            }
            return $$2;
        });
    }
}