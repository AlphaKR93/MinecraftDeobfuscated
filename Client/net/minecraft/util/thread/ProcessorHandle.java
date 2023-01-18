/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  java.lang.AutoCloseable
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Consumer
 *  java.util.function.Function
 */
package net.minecraft.util.thread;

import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ProcessorHandle<Msg>
extends AutoCloseable {
    public String name();

    public void tell(Msg var1);

    default public void close() {
    }

    default public <Source> CompletableFuture<Source> ask(Function<? super ProcessorHandle<Source>, ? extends Msg> $$0) {
        CompletableFuture $$1 = new CompletableFuture();
        Object $$2 = $$0.apply(ProcessorHandle.of("ask future procesor handle", arg_0 -> ((CompletableFuture)$$1).complete(arg_0)));
        this.tell($$2);
        return $$1;
    }

    default public <Source> CompletableFuture<Source> askEither(Function<? super ProcessorHandle<Either<Source, Exception>>, ? extends Msg> $$0) {
        CompletableFuture $$12 = new CompletableFuture();
        Object $$2 = $$0.apply(ProcessorHandle.of("ask future procesor handle", $$1 -> {
            $$1.ifLeft(arg_0 -> ((CompletableFuture)$$12).complete(arg_0));
            $$1.ifRight(arg_0 -> ((CompletableFuture)$$12).completeExceptionally(arg_0));
        }));
        this.tell($$2);
        return $$12;
    }

    public static <Msg> ProcessorHandle<Msg> of(final String $$0, final Consumer<Msg> $$1) {
        return new ProcessorHandle<Msg>(){

            @Override
            public String name() {
                return $$0;
            }

            @Override
            public void tell(Msg $$02) {
                $$1.accept($$02);
            }

            public String toString() {
                return $$0;
            }
        };
    }
}