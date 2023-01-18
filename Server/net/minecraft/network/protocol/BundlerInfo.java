/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.util.AttributeKey
 *  java.lang.Class
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol;

import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.BundleDelimiterPacket;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public interface BundlerInfo {
    public static final AttributeKey<Provider> BUNDLER_PROVIDER = AttributeKey.valueOf((String)"bundler");
    public static final int BUNDLE_SIZE_LIMIT = 4096;
    public static final BundlerInfo EMPTY = new BundlerInfo(){

        @Override
        public void unbundlePacket(Packet<?> $$0, Consumer<Packet<?>> $$1) {
            $$1.accept($$0);
        }

        @Override
        @Nullable
        public Bundler startPacketBundling(Packet<?> $$0) {
            return null;
        }
    };

    public static <T extends PacketListener, P extends BundlePacket<T>> BundlerInfo createForPacket(final Class<P> $$0, final Function<Iterable<Packet<T>>, P> $$1, final BundleDelimiterPacket<T> $$2) {
        return new BundlerInfo(){

            @Override
            public void unbundlePacket(Packet<?> $$02, Consumer<Packet<?>> $$12) {
                if ($$02.getClass() == $$0) {
                    BundlePacket $$22 = (BundlePacket)$$02;
                    $$12.accept((Object)$$2);
                    $$22.subPackets().forEach($$12);
                    $$12.accept((Object)$$2);
                } else {
                    $$12.accept($$02);
                }
            }

            @Override
            @Nullable
            public Bundler startPacketBundling(Packet<?> $$02) {
                if ($$02 == $$2) {
                    return new Bundler(){
                        private final List<Packet<T>> bundlePackets = new ArrayList();

                        @Override
                        @Nullable
                        public Packet<?> addPacket(Packet<?> $$0) {
                            if ($$0 == $$2) {
                                return (Packet)$$1.apply(this.bundlePackets);
                            }
                            Packet<?> $$1 = $$0;
                            if (this.bundlePackets.size() >= 4096) {
                                throw new IllegalStateException("Too many packets in a bundle");
                            }
                            this.bundlePackets.add($$1);
                            return null;
                        }
                    };
                }
                return null;
            }
        };
    }

    public void unbundlePacket(Packet<?> var1, Consumer<Packet<?>> var2);

    @Nullable
    public Bundler startPacketBundling(Packet<?> var1);

    public static interface Provider {
        public BundlerInfo getBundlerInfo(PacketFlow var1);
    }

    public static interface Bundler {
        @Nullable
        public Packet<?> addPacket(Packet<?> var1);
    }
}