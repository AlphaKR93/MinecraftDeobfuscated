/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RecordItem
extends Item {
    private static final Map<SoundEvent, RecordItem> BY_NAME = Maps.newHashMap();
    private final int analogOutput;
    private final SoundEvent sound;
    private final int lengthInTicks;

    protected RecordItem(int $$0, SoundEvent $$1, Item.Properties $$2, int $$3) {
        super($$2);
        this.analogOutput = $$0;
        this.sound = $$1;
        this.lengthInTicks = $$3 * 20;
        BY_NAME.put((Object)this.sound, (Object)this);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if (!$$3.is(Blocks.JUKEBOX) || $$3.getValue(JukeboxBlock.HAS_RECORD).booleanValue()) {
            return InteractionResult.PASS;
        }
        ItemStack $$4 = $$0.getItemInHand();
        if (!$$1.isClientSide) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord($$0.getPlayer(), $$1, $$2, $$3, $$4);
            $$1.levelEvent(null, 1010, $$2, Item.getId(this));
            $$4.shrink(1);
            Player $$5 = $$0.getPlayer();
            if ($$5 != null) {
                $$5.awardStat(Stats.PLAY_RECORD);
            }
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    public int getAnalogOutput() {
        return this.analogOutput;
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        $$2.add((Object)this.getDisplayName().withStyle(ChatFormatting.GRAY));
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(this.getDescriptionId() + ".desc");
    }

    @Nullable
    public static RecordItem getBySound(SoundEvent $$0) {
        return (RecordItem)BY_NAME.get((Object)$$0);
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public int getLengthInTicks() {
        return this.lengthInTicks;
    }
}