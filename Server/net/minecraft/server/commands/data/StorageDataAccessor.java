/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  java.util.function.Function
 */
package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.level.storage.CommandStorage;

public class StorageDataAccessor
implements DataAccessor {
    static final SuggestionProvider<CommandSourceStack> SUGGEST_STORAGE = ($$0, $$1) -> SharedSuggestionProvider.suggestResource(StorageDataAccessor.getGlobalTags((CommandContext<CommandSourceStack>)$$0).keys(), $$1);
    public static final Function<String, DataCommands.DataProvider> PROVIDER = $$0 -> new DataCommands.DataProvider((String)$$0){
        final /* synthetic */ String val$arg;
        {
            this.val$arg = string;
        }

        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> $$0) {
            return new StorageDataAccessor(StorageDataAccessor.getGlobalTags($$0), ResourceLocationArgument.getId($$0, this.val$arg));
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> $$0, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> $$1) {
            return $$0.then(Commands.literal("storage").then((ArgumentBuilder)$$1.apply((Object)Commands.argument(this.val$arg, ResourceLocationArgument.id()).suggests(SUGGEST_STORAGE))));
        }
    };
    private final CommandStorage storage;
    private final ResourceLocation id;

    static CommandStorage getGlobalTags(CommandContext<CommandSourceStack> $$0) {
        return ((CommandSourceStack)$$0.getSource()).getServer().getCommandStorage();
    }

    StorageDataAccessor(CommandStorage $$0, ResourceLocation $$1) {
        this.storage = $$0;
        this.id = $$1;
    }

    @Override
    public void setData(CompoundTag $$0) {
        this.storage.set(this.id, $$0);
    }

    @Override
    public CompoundTag getData() {
        return this.storage.get(this.id);
    }

    @Override
    public Component getModifiedSuccess() {
        return Component.translatable("commands.data.storage.modified", this.id);
    }

    @Override
    public Component getPrintSuccess(Tag $$0) {
        return Component.translatable("commands.data.storage.query", this.id, NbtUtils.toPrettyComponent($$0));
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath $$0, double $$1, int $$2) {
        return Component.translatable("commands.data.storage.get", $$0, this.id, String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$1}), $$2);
    }
}