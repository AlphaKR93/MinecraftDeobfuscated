/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption
implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ItemParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ItemParticleOption>(){

        @Override
        public ItemParticleOption fromCommand(ParticleType<ItemParticleOption> $$0, StringReader $$1) throws CommandSyntaxException {
            $$1.expect(' ');
            ItemParser.ItemResult $$2 = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), $$1);
            ItemStack $$3 = new ItemInput($$2.item(), $$2.nbt()).createItemStack(1, false);
            return new ItemParticleOption($$0, $$3);
        }

        @Override
        public ItemParticleOption fromNetwork(ParticleType<ItemParticleOption> $$0, FriendlyByteBuf $$1) {
            return new ItemParticleOption($$0, $$1.readItem());
        }
    };
    private final ParticleType<ItemParticleOption> type;
    private final ItemStack itemStack;

    public static Codec<ItemParticleOption> codec(ParticleType<ItemParticleOption> $$02) {
        return ItemStack.CODEC.xmap($$1 -> new ItemParticleOption($$02, (ItemStack)$$1), $$0 -> $$0.itemStack);
    }

    public ItemParticleOption(ParticleType<ItemParticleOption> $$0, ItemStack $$1) {
        this.type = $$0;
        this.itemStack = $$1;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf $$0) {
        $$0.writeItem(this.itemStack);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + new ItemInput(this.itemStack.getItemHolder(), this.itemStack.getTag()).serialize();
    }

    public ParticleType<ItemParticleOption> getType() {
        return this.type;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}