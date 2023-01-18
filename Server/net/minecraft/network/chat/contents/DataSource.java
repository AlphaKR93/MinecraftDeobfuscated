/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.stream.Stream
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface DataSource {
    public Stream<CompoundTag> getData(CommandSourceStack var1) throws CommandSyntaxException;
}