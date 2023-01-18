/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.OptionalLong
 */
package net.minecraft.data.worldgen;

import java.util.OptionalLong;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypes {
    public static void bootstrap(BootstapContext<DimensionType> $$0) {
        $$0.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0f, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
        $$0.register(BuiltinDimensionTypes.NETHER, new DimensionType(OptionalLong.of((long)18000L), false, true, true, false, 8.0, false, true, 0, 256, 128, BlockTags.INFINIBURN_NETHER, BuiltinDimensionTypes.NETHER_EFFECTS, 0.1f, new DimensionType.MonsterSettings(true, false, ConstantInt.of(7), 15)));
        $$0.register(BuiltinDimensionTypes.END, new DimensionType(OptionalLong.of((long)6000L), false, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_END, BuiltinDimensionTypes.END_EFFECTS, 0.0f, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
        $$0.register(BuiltinDimensionTypes.OVERWORLD_CAVES, new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0f, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
    }
}