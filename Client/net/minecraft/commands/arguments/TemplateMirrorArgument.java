/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.world.level.block.Mirror;

public class TemplateMirrorArgument
extends StringRepresentableArgument<Mirror> {
    private TemplateMirrorArgument() {
        super(Mirror.CODEC, (Supplier<T[]>)((Supplier)Mirror::values));
    }

    public static StringRepresentableArgument<Mirror> templateMirror() {
        return new TemplateMirrorArgument();
    }

    public static Mirror getMirror(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Mirror)$$0.getArgument($$1, Mirror.class);
    }
}