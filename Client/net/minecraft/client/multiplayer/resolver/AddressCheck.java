/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.mojang.blocklist.BlockListSupplier
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.ServiceLoader
 */
package net.minecraft.client.multiplayer.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public interface AddressCheck {
    public boolean isAllowed(ResolvedServerAddress var1);

    public boolean isAllowed(ServerAddress var1);

    public static AddressCheck createFromService() {
        final ImmutableList $$0 = (ImmutableList)Streams.stream((Iterable)ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
        return new AddressCheck(){

            @Override
            public boolean isAllowed(ResolvedServerAddress $$02) {
                String $$1 = $$02.getHostName();
                String $$22 = $$02.getHostIp();
                return $$0.stream().noneMatch($$2 -> $$2.test((Object)$$1) || $$2.test((Object)$$22));
            }

            @Override
            public boolean isAllowed(ServerAddress $$02) {
                String $$12 = $$02.getHost();
                return $$0.stream().noneMatch($$1 -> $$1.test((Object)$$12));
            }
        };
    }
}