/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadItem
extends StandingAndWallBlockItem {
    public static final String TAG_SKULL_OWNER = "SkullOwner";

    public PlayerHeadItem(Block $$0, Block $$1, Item.Properties $$2) {
        super($$0, $$1, $$2, Direction.DOWN);
    }

    @Override
    public Component getName(ItemStack $$0) {
        if ($$0.is(Items.PLAYER_HEAD) && $$0.hasTag()) {
            CompoundTag $$3;
            String $$1 = null;
            CompoundTag $$2 = $$0.getTag();
            if ($$2.contains(TAG_SKULL_OWNER, 8)) {
                $$1 = $$2.getString(TAG_SKULL_OWNER);
            } else if ($$2.contains(TAG_SKULL_OWNER, 10) && ($$3 = $$2.getCompound(TAG_SKULL_OWNER)).contains("Name", 8)) {
                $$1 = $$3.getString("Name");
            }
            if ($$1 != null) {
                return Component.translatable(this.getDescriptionId() + ".named", $$1);
            }
        }
        return super.getName($$0);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag $$0) {
        super.verifyTagAfterLoad($$0);
        if ($$0.contains(TAG_SKULL_OWNER, 8) && !StringUtils.isBlank((CharSequence)$$0.getString(TAG_SKULL_OWNER))) {
            GameProfile $$12 = new GameProfile(null, $$0.getString(TAG_SKULL_OWNER));
            SkullBlockEntity.updateGameprofile($$12, (Consumer<GameProfile>)((Consumer)$$1 -> $$0.put(TAG_SKULL_OWNER, NbtUtils.writeGameProfile(new CompoundTag(), $$1))));
        }
    }
}