/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class StructureUpdater
implements SnbtToNbt.Filter {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public CompoundTag apply(String $$0, CompoundTag $$1) {
        if ($$0.startsWith("data/minecraft/structures/")) {
            return StructureUpdater.update($$0, $$1);
        }
        return $$1;
    }

    public static CompoundTag update(String $$0, CompoundTag $$1) {
        StructureTemplate $$2 = new StructureTemplate();
        int $$3 = NbtUtils.getDataVersion($$1, 500);
        int $$4 = 3318;
        if ($$3 < 3318) {
            LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{$$3, 3318, $$0});
        }
        CompoundTag $$5 = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), $$1, $$3);
        $$2.load(BuiltInRegistries.BLOCK.asLookup(), $$5);
        return $$2.save(new CompoundTag());
    }
}