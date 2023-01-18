/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
    public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1);

    public static CommandBuildContext simple(final HolderLookup.Provider $$0, final FeatureFlagSet $$1) {
        return new CommandBuildContext(){

            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> $$02) {
                return $$0.lookupOrThrow($$02).filterFeatures($$1);
            }
        };
    }

    public static Configurable configurable(final RegistryAccess $$0, final FeatureFlagSet $$1) {
        return new Configurable(){
            MissingTagAccessPolicy missingTagAccessPolicy = MissingTagAccessPolicy.FAIL;

            @Override
            public void missingTagAccessPolicy(MissingTagAccessPolicy $$02) {
                this.missingTagAccessPolicy = $$02;
            }

            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> $$02) {
                Registry $$12 = $$0.registryOrThrow($$02);
                final HolderLookup.RegistryLookup $$2 = $$12.asLookup();
                final HolderLookup.RegistryLookup $$3 = $$12.asTagAddingLookup();
                HolderLookup.RegistryLookup.Delegate $$4 = new HolderLookup.RegistryLookup.Delegate<T>(){

                    @Override
                    protected HolderLookup.RegistryLookup<T> parent() {
                        return switch (missingTagAccessPolicy) {
                            default -> throw new IncompatibleClassChangeError();
                            case MissingTagAccessPolicy.FAIL -> $$2;
                            case MissingTagAccessPolicy.CREATE_NEW -> $$3;
                        };
                    }
                };
                return $$4.filterFeatures($$1);
            }
        };
    }

    public static interface Configurable
    extends CommandBuildContext {
        public void missingTagAccessPolicy(MissingTagAccessPolicy var1);
    }

    public static enum MissingTagAccessPolicy {
        CREATE_NEW,
        FAIL;

    }
}