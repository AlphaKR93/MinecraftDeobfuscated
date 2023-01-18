/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock
extends BaseEntityBlock {
    protected SpawnerBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new SpawnerBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return SpawnerBlock.createTickerHelper($$2, BlockEntityType.MOB_SPAWNER, $$0.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
    }

    @Override
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$4) {
            int $$5 = 15 + $$1.random.nextInt(15) + $$1.random.nextInt(15);
            this.popExperience($$1, $$2, $$5);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable BlockGetter $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        Optional<Component> $$4 = this.getSpawnEntityDisplayName($$0);
        if ($$4.isPresent()) {
            $$2.add((Object)((Component)$$4.get()));
        } else {
            $$2.add((Object)CommonComponents.EMPTY);
            $$2.add((Object)Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            $$2.add((Object)Component.literal(" ").append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }
    }

    private Optional<Component> getSpawnEntityDisplayName(ItemStack $$02) {
        String $$2;
        ResourceLocation $$3;
        CompoundTag $$1 = BlockItem.getBlockEntityData($$02);
        if ($$1 != null && $$1.contains("SpawnData", 10) && ($$3 = ResourceLocation.tryParse($$2 = $$1.getCompound("SpawnData").getCompound("entity").getString("id"))) != null) {
            return BuiltInRegistries.ENTITY_TYPE.getOptional($$3).map($$0 -> Component.translatable($$0.getDescriptionId()).withStyle(ChatFormatting.GRAY));
        }
        return Optional.empty();
    }
}