/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CatVariant(ResourceLocation texture) {
    public static final ResourceKey<CatVariant> TABBY = CatVariant.createKey("tabby");
    public static final ResourceKey<CatVariant> BLACK = CatVariant.createKey("black");
    public static final ResourceKey<CatVariant> RED = CatVariant.createKey("red");
    public static final ResourceKey<CatVariant> SIAMESE = CatVariant.createKey("siamese");
    public static final ResourceKey<CatVariant> BRITISH_SHORTHAIR = CatVariant.createKey("british_shorthair");
    public static final ResourceKey<CatVariant> CALICO = CatVariant.createKey("calico");
    public static final ResourceKey<CatVariant> PERSIAN = CatVariant.createKey("persian");
    public static final ResourceKey<CatVariant> RAGDOLL = CatVariant.createKey("ragdoll");
    public static final ResourceKey<CatVariant> WHITE = CatVariant.createKey("white");
    public static final ResourceKey<CatVariant> JELLIE = CatVariant.createKey("jellie");
    public static final ResourceKey<CatVariant> ALL_BLACK = CatVariant.createKey("all_black");

    private static ResourceKey<CatVariant> createKey(String $$0) {
        return ResourceKey.create(Registries.CAT_VARIANT, new ResourceLocation($$0));
    }

    public static CatVariant bootstrap(Registry<CatVariant> $$0) {
        CatVariant.register($$0, TABBY, "textures/entity/cat/tabby.png");
        CatVariant.register($$0, BLACK, "textures/entity/cat/black.png");
        CatVariant.register($$0, RED, "textures/entity/cat/red.png");
        CatVariant.register($$0, SIAMESE, "textures/entity/cat/siamese.png");
        CatVariant.register($$0, BRITISH_SHORTHAIR, "textures/entity/cat/british_shorthair.png");
        CatVariant.register($$0, CALICO, "textures/entity/cat/calico.png");
        CatVariant.register($$0, PERSIAN, "textures/entity/cat/persian.png");
        CatVariant.register($$0, RAGDOLL, "textures/entity/cat/ragdoll.png");
        CatVariant.register($$0, WHITE, "textures/entity/cat/white.png");
        CatVariant.register($$0, JELLIE, "textures/entity/cat/jellie.png");
        return CatVariant.register($$0, ALL_BLACK, "textures/entity/cat/all_black.png");
    }

    private static CatVariant register(Registry<CatVariant> $$0, ResourceKey<CatVariant> $$1, String $$2) {
        return Registry.register($$0, $$1, new CatVariant(new ResourceLocation($$2)));
    }
}