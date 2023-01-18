/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.client.multiplayer.resolver;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.AddressCheck;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddressResolver;
import net.minecraft.client.multiplayer.resolver.ServerRedirectHandler;

public class ServerNameResolver {
    public static final ServerNameResolver DEFAULT = new ServerNameResolver(ServerAddressResolver.SYSTEM, ServerRedirectHandler.createDnsSrvRedirectHandler(), AddressCheck.createFromService());
    private final ServerAddressResolver resolver;
    private final ServerRedirectHandler redirectHandler;
    private final AddressCheck addressCheck;

    @VisibleForTesting
    ServerNameResolver(ServerAddressResolver $$0, ServerRedirectHandler $$1, AddressCheck $$2) {
        this.resolver = $$0;
        this.redirectHandler = $$1;
        this.addressCheck = $$2;
    }

    public Optional<ResolvedServerAddress> resolveAddress(ServerAddress $$0) {
        Optional $$1 = this.resolver.resolve($$0);
        if ($$1.isPresent() && !this.addressCheck.isAllowed((ResolvedServerAddress)$$1.get()) || !this.addressCheck.isAllowed($$0)) {
            return Optional.empty();
        }
        Optional<ServerAddress> $$2 = this.redirectHandler.lookupRedirect($$0);
        if ($$2.isPresent()) {
            $$1 = this.resolver.resolve((ServerAddress)$$2.get()).filter(this.addressCheck::isAllowed);
        }
        return $$1;
    }
}