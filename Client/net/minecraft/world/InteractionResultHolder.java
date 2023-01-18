/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world;

import net.minecraft.world.InteractionResult;

public class InteractionResultHolder<T> {
    private final InteractionResult result;
    private final T object;

    public InteractionResultHolder(InteractionResult $$0, T $$1) {
        this.result = $$0;
        this.object = $$1;
    }

    public InteractionResult getResult() {
        return this.result;
    }

    public T getObject() {
        return this.object;
    }

    public static <T> InteractionResultHolder<T> success(T $$0) {
        return new InteractionResultHolder<T>(InteractionResult.SUCCESS, $$0);
    }

    public static <T> InteractionResultHolder<T> consume(T $$0) {
        return new InteractionResultHolder<T>(InteractionResult.CONSUME, $$0);
    }

    public static <T> InteractionResultHolder<T> pass(T $$0) {
        return new InteractionResultHolder<T>(InteractionResult.PASS, $$0);
    }

    public static <T> InteractionResultHolder<T> fail(T $$0) {
        return new InteractionResultHolder<T>(InteractionResult.FAIL, $$0);
    }

    public static <T> InteractionResultHolder<T> sidedSuccess(T $$0, boolean $$1) {
        return $$1 ? InteractionResultHolder.success($$0) : InteractionResultHolder.consume($$0);
    }
}