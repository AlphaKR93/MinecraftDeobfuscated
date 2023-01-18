/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.level.block;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadBlock
extends SkullBlock {
    protected PlayerHeadBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.PLAYER, $$0);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        BlockEntity $$5 = $$0.getBlockEntity($$1);
        if ($$5 instanceof SkullBlockEntity) {
            SkullBlockEntity $$6 = (SkullBlockEntity)$$5;
            GameProfile $$7 = null;
            if ($$4.hasTag()) {
                CompoundTag $$8 = $$4.getTag();
                if ($$8.contains("SkullOwner", 10)) {
                    $$7 = NbtUtils.readGameProfile($$8.getCompound("SkullOwner"));
                } else if ($$8.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)$$8.getString("SkullOwner"))) {
                    $$7 = new GameProfile(null, $$8.getString("SkullOwner"));
                }
            }
            $$6.setOwner($$7);
        }
    }
}