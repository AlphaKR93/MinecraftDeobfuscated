/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.Entity;

public record EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) implements DataSource
{
    public EntityDataSource(String $$0) {
        this($$0, EntityDataSource.compileSelector($$0));
    }

    @Nullable
    private static EntitySelector compileSelector(String $$0) {
        try {
            EntitySelectorParser $$1 = new EntitySelectorParser(new StringReader($$0));
            return $$1.parse();
        }
        catch (CommandSyntaxException $$2) {
            return null;
        }
    }

    @Override
    public Stream<CompoundTag> getData(CommandSourceStack $$0) throws CommandSyntaxException {
        if (this.compiledSelector != null) {
            List<? extends Entity> $$1 = this.compiledSelector.findEntities($$0);
            return $$1.stream().map(NbtPredicate::getEntityTagToCompare);
        }
        return Stream.empty();
    }

    public String toString() {
        return "entity=" + this.selectorPattern;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof EntityDataSource)) return false;
        EntityDataSource $$1 = (EntityDataSource)$$0;
        if (!this.selectorPattern.equals((Object)$$1.selectorPattern)) return false;
        return true;
    }

    public int hashCode() {
        return this.selectorPattern.hashCode();
    }
}