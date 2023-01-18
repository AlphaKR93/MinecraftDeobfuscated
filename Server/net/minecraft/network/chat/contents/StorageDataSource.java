/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.stream.Stream
 */
package net.minecraft.network.chat.contents;

import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceLocation;

public record StorageDataSource(ResourceLocation id) implements DataSource
{
    @Override
    public Stream<CompoundTag> getData(CommandSourceStack $$0) {
        CompoundTag $$1 = $$0.getServer().getCommandStorage().get(this.id);
        return Stream.of((Object)$$1);
    }

    public String toString() {
        return "storage=" + this.id;
    }
}