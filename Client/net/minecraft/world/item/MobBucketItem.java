/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

public class MobBucketItem
extends BucketItem {
    private final EntityType<?> type;
    private final SoundEvent emptySound;

    public MobBucketItem(EntityType<?> $$0, Fluid $$1, SoundEvent $$2, Item.Properties $$3) {
        super($$1, $$3);
        this.type = $$0;
        this.emptySound = $$2;
    }

    @Override
    public void checkExtraContent(@Nullable Player $$0, Level $$1, ItemStack $$2, BlockPos $$3) {
        if ($$1 instanceof ServerLevel) {
            this.spawn((ServerLevel)$$1, $$2, $$3);
            $$1.gameEvent($$0, GameEvent.ENTITY_PLACE, $$3);
        }
    }

    @Override
    protected void playEmptySound(@Nullable Player $$0, LevelAccessor $$1, BlockPos $$2) {
        $$1.playSound($$0, $$2, this.emptySound, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawn(ServerLevel $$0, ItemStack $$1, BlockPos $$2) {
        Object $$3 = this.type.spawn($$0, $$1, null, $$2, MobSpawnType.BUCKET, true, false);
        if ($$3 instanceof Bucketable) {
            Bucketable $$4 = (Bucketable)$$3;
            $$4.loadFromBucketTag($$1.getOrCreateTag());
            $$4.setFromBucket(true);
        }
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        CompoundTag $$4;
        if (this.type == EntityType.TROPICAL_FISH && ($$4 = $$0.getTag()) != null && $$4.contains("BucketVariantTag", 3)) {
            int $$5 = $$4.getInt("BucketVariantTag");
            ChatFormatting[] $$6 = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String $$7 = "color.minecraft." + TropicalFish.getBaseColor($$5);
            String $$8 = "color.minecraft." + TropicalFish.getPatternColor($$5);
            for (int $$9 = 0; $$9 < TropicalFish.COMMON_VARIANTS.size(); ++$$9) {
                if ($$5 != ((TropicalFish.Variant)((Object)TropicalFish.COMMON_VARIANTS.get($$9))).getPackedId()) continue;
                $$2.add((Object)Component.translatable(TropicalFish.getPredefinedName($$9)).withStyle($$6));
                return;
            }
            $$2.add((Object)TropicalFish.getPattern($$5).displayName().plainCopy().withStyle($$6));
            MutableComponent $$10 = Component.translatable($$7);
            if (!$$7.equals((Object)$$8)) {
                $$10.append(", ").append(Component.translatable($$8));
            }
            $$10.withStyle($$6);
            $$2.add((Object)$$10);
        }
    }
}