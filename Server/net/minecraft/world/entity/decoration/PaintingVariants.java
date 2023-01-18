/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity.decoration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariants {
    public static final ResourceKey<PaintingVariant> KEBAB = PaintingVariants.create("kebab");
    public static final ResourceKey<PaintingVariant> AZTEC = PaintingVariants.create("aztec");
    public static final ResourceKey<PaintingVariant> ALBAN = PaintingVariants.create("alban");
    public static final ResourceKey<PaintingVariant> AZTEC2 = PaintingVariants.create("aztec2");
    public static final ResourceKey<PaintingVariant> BOMB = PaintingVariants.create("bomb");
    public static final ResourceKey<PaintingVariant> PLANT = PaintingVariants.create("plant");
    public static final ResourceKey<PaintingVariant> WASTELAND = PaintingVariants.create("wasteland");
    public static final ResourceKey<PaintingVariant> POOL = PaintingVariants.create("pool");
    public static final ResourceKey<PaintingVariant> COURBET = PaintingVariants.create("courbet");
    public static final ResourceKey<PaintingVariant> SEA = PaintingVariants.create("sea");
    public static final ResourceKey<PaintingVariant> SUNSET = PaintingVariants.create("sunset");
    public static final ResourceKey<PaintingVariant> CREEBET = PaintingVariants.create("creebet");
    public static final ResourceKey<PaintingVariant> WANDERER = PaintingVariants.create("wanderer");
    public static final ResourceKey<PaintingVariant> GRAHAM = PaintingVariants.create("graham");
    public static final ResourceKey<PaintingVariant> MATCH = PaintingVariants.create("match");
    public static final ResourceKey<PaintingVariant> BUST = PaintingVariants.create("bust");
    public static final ResourceKey<PaintingVariant> STAGE = PaintingVariants.create("stage");
    public static final ResourceKey<PaintingVariant> VOID = PaintingVariants.create("void");
    public static final ResourceKey<PaintingVariant> SKULL_AND_ROSES = PaintingVariants.create("skull_and_roses");
    public static final ResourceKey<PaintingVariant> WITHER = PaintingVariants.create("wither");
    public static final ResourceKey<PaintingVariant> FIGHTERS = PaintingVariants.create("fighters");
    public static final ResourceKey<PaintingVariant> POINTER = PaintingVariants.create("pointer");
    public static final ResourceKey<PaintingVariant> PIGSCENE = PaintingVariants.create("pigscene");
    public static final ResourceKey<PaintingVariant> BURNING_SKULL = PaintingVariants.create("burning_skull");
    public static final ResourceKey<PaintingVariant> SKELETON = PaintingVariants.create("skeleton");
    public static final ResourceKey<PaintingVariant> DONKEY_KONG = PaintingVariants.create("donkey_kong");
    public static final ResourceKey<PaintingVariant> EARTH = PaintingVariants.create("earth");
    public static final ResourceKey<PaintingVariant> WIND = PaintingVariants.create("wind");
    public static final ResourceKey<PaintingVariant> WATER = PaintingVariants.create("water");
    public static final ResourceKey<PaintingVariant> FIRE = PaintingVariants.create("fire");

    public static PaintingVariant bootstrap(Registry<PaintingVariant> $$0) {
        Registry.register($$0, KEBAB, new PaintingVariant(16, 16));
        Registry.register($$0, AZTEC, new PaintingVariant(16, 16));
        Registry.register($$0, ALBAN, new PaintingVariant(16, 16));
        Registry.register($$0, AZTEC2, new PaintingVariant(16, 16));
        Registry.register($$0, BOMB, new PaintingVariant(16, 16));
        Registry.register($$0, PLANT, new PaintingVariant(16, 16));
        Registry.register($$0, WASTELAND, new PaintingVariant(16, 16));
        Registry.register($$0, POOL, new PaintingVariant(32, 16));
        Registry.register($$0, COURBET, new PaintingVariant(32, 16));
        Registry.register($$0, SEA, new PaintingVariant(32, 16));
        Registry.register($$0, SUNSET, new PaintingVariant(32, 16));
        Registry.register($$0, CREEBET, new PaintingVariant(32, 16));
        Registry.register($$0, WANDERER, new PaintingVariant(16, 32));
        Registry.register($$0, GRAHAM, new PaintingVariant(16, 32));
        Registry.register($$0, MATCH, new PaintingVariant(32, 32));
        Registry.register($$0, BUST, new PaintingVariant(32, 32));
        Registry.register($$0, STAGE, new PaintingVariant(32, 32));
        Registry.register($$0, VOID, new PaintingVariant(32, 32));
        Registry.register($$0, SKULL_AND_ROSES, new PaintingVariant(32, 32));
        Registry.register($$0, WITHER, new PaintingVariant(32, 32));
        Registry.register($$0, FIGHTERS, new PaintingVariant(64, 32));
        Registry.register($$0, POINTER, new PaintingVariant(64, 64));
        Registry.register($$0, PIGSCENE, new PaintingVariant(64, 64));
        Registry.register($$0, BURNING_SKULL, new PaintingVariant(64, 64));
        Registry.register($$0, SKELETON, new PaintingVariant(64, 48));
        Registry.register($$0, EARTH, new PaintingVariant(32, 32));
        Registry.register($$0, WIND, new PaintingVariant(32, 32));
        Registry.register($$0, WATER, new PaintingVariant(32, 32));
        Registry.register($$0, FIRE, new PaintingVariant(32, 32));
        return Registry.register($$0, DONKEY_KONG, new PaintingVariant(64, 48));
    }

    private static ResourceKey<PaintingVariant> create(String $$0) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, new ResourceLocation($$0));
    }
}