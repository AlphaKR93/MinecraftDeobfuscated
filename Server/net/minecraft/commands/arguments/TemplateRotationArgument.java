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
import net.minecraft.world.level.block.Rotation;

public class TemplateRotationArgument
extends StringRepresentableArgument<Rotation> {
    private TemplateRotationArgument() {
        super(Rotation.CODEC, (Supplier<T[]>)((Supplier)Rotation::values));
    }

    public static TemplateRotationArgument templateRotation() {
        return new TemplateRotationArgument();
    }

    public static Rotation getRotation(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Rotation)$$0.getArgument($$1, Rotation.class);
    }
}