/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 *  java.util.function.Supplier
 */
package net.minecraft.network.chat.contents;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class KeybindResolver {
    static Function<String, Supplier<Component>> keyResolver = $$0 -> () -> Component.literal($$0);

    public static void setKeyResolver(Function<String, Supplier<Component>> $$0) {
        keyResolver = $$0;
    }
}