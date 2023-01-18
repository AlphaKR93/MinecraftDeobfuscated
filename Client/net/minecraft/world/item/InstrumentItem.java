/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class InstrumentItem
extends Item {
    private static final String TAG_INSTRUMENT = "instrument";
    private final TagKey<Instrument> instruments;

    public InstrumentItem(Item.Properties $$0, TagKey<Instrument> $$1) {
        super($$0);
        this.instruments = $$1;
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        Optional $$4 = this.getInstrument($$0).flatMap(Holder::unwrapKey);
        if ($$4.isPresent()) {
            MutableComponent $$5 = Component.translatable(Util.makeDescriptionId(TAG_INSTRUMENT, ((ResourceKey)$$4.get()).location()));
            $$2.add((Object)$$5.withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemStack create(Item $$0, Holder<Instrument> $$1) {
        ItemStack $$2 = new ItemStack($$0);
        InstrumentItem.setSoundVariantId($$2, $$1);
        return $$2;
    }

    public static void setRandom(ItemStack $$0, TagKey<Instrument> $$12, RandomSource $$2) {
        Optional $$3 = BuiltInRegistries.INSTRUMENT.getTag($$12).flatMap($$1 -> $$1.getRandomElement($$2));
        $$3.ifPresent($$1 -> InstrumentItem.setSoundVariantId($$0, $$1));
    }

    private static void setSoundVariantId(ItemStack $$0, Holder<Instrument> $$1) {
        CompoundTag $$2 = $$0.getOrCreateTag();
        $$2.putString(TAG_INSTRUMENT, ((ResourceKey)$$1.unwrapKey().orElseThrow(() -> new IllegalStateException("Invalid instrument"))).location().toString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        Optional<? extends Holder<Instrument>> $$4 = this.getInstrument($$3);
        if ($$4.isPresent()) {
            Instrument $$5 = (Instrument)((Object)((Holder)$$4.get()).value());
            $$1.startUsingItem($$2);
            InstrumentItem.play($$0, $$1, $$5);
            $$1.getCooldowns().addCooldown(this, $$5.useDuration());
            $$1.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume($$3);
        }
        return InteractionResultHolder.fail($$3);
    }

    @Override
    public int getUseDuration(ItemStack $$02) {
        Optional<? extends Holder<Instrument>> $$1 = this.getInstrument($$02);
        return (Integer)$$1.map($$0 -> ((Instrument)((Object)((Object)$$0.value()))).useDuration()).orElse((Object)0);
    }

    private Optional<? extends Holder<Instrument>> getInstrument(ItemStack $$0) {
        ResourceLocation $$2;
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null && ($$2 = ResourceLocation.tryParse($$1.getString(TAG_INSTRUMENT))) != null) {
            return BuiltInRegistries.INSTRUMENT.getHolder(ResourceKey.create(Registries.INSTRUMENT, $$2));
        }
        Iterator $$3 = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
        if ($$3.hasNext()) {
            return Optional.of((Object)((Holder)$$3.next()));
        }
        return Optional.empty();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.TOOT_HORN;
    }

    private static void play(Level $$0, Player $$1, Instrument $$2) {
        SoundEvent $$3 = $$2.soundEvent().value();
        float $$4 = $$2.range() / 16.0f;
        $$0.playSound($$1, $$1, $$3, SoundSource.RECORDS, $$4, 1.0f);
        $$0.gameEvent(GameEvent.INSTRUMENT_PLAY, $$1.position(), GameEvent.Context.of($$1));
    }
}