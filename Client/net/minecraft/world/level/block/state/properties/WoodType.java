/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class WoodType {
    private static final Set<WoodType> VALUES = new ObjectArraySet();
    public static final WoodType OAK = WoodType.register(new WoodType("oak"));
    public static final WoodType SPRUCE = WoodType.register(new WoodType("spruce"));
    public static final WoodType BIRCH = WoodType.register(new WoodType("birch"));
    public static final WoodType ACACIA = WoodType.register(new WoodType("acacia"));
    public static final WoodType JUNGLE = WoodType.register(new WoodType("jungle"));
    public static final WoodType DARK_OAK = WoodType.register(new WoodType("dark_oak"));
    public static final WoodType CRIMSON = WoodType.register(new WoodType("crimson"));
    public static final WoodType WARPED = WoodType.register(new WoodType("warped"));
    public static final WoodType MANGROVE = WoodType.register(new WoodType("mangrove"));
    public static final WoodType BAMBOO = WoodType.register(new WoodType("bamboo"));
    private final String name;

    protected WoodType(String $$0) {
        this.name = $$0;
    }

    private static WoodType register(WoodType $$0) {
        VALUES.add((Object)$$0);
        return $$0;
    }

    public static Stream<WoodType> values() {
        return VALUES.stream();
    }

    public String name() {
        return this.name;
    }
}