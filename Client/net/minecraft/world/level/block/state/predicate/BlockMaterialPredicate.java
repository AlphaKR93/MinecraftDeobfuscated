/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMaterialPredicate
implements Predicate<BlockState> {
    private static final BlockMaterialPredicate AIR = new BlockMaterialPredicate(Material.AIR){

        @Override
        public boolean test(@Nullable BlockState $$0) {
            return $$0 != null && $$0.isAir();
        }
    };
    private final Material material;

    BlockMaterialPredicate(Material $$0) {
        this.material = $$0;
    }

    public static BlockMaterialPredicate forMaterial(Material $$0) {
        return $$0 == Material.AIR ? AIR : new BlockMaterialPredicate($$0);
    }

    public boolean test(@Nullable BlockState $$0) {
        return $$0 != null && $$0.getMaterial() == this.material;
    }
}