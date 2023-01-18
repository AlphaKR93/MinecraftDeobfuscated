/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class GravityProcessor
extends StructureProcessor {
    public static final Codec<GravityProcessor> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Heightmap.Types.CODEC.fieldOf("heightmap").orElse((Object)Heightmap.Types.WORLD_SURFACE_WG).forGetter($$0 -> $$0.heightmap), (App)Codec.INT.fieldOf("offset").orElse((Object)0).forGetter($$0 -> $$0.offset)).apply((Applicative)$$02, GravityProcessor::new));
    private final Heightmap.Types heightmap;
    private final int offset;

    public GravityProcessor(Heightmap.Types $$0, int $$1) {
        this.heightmap = $$0;
        this.offset = $$1;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader $$0, BlockPos $$1, BlockPos $$2, StructureTemplate.StructureBlockInfo $$3, StructureTemplate.StructureBlockInfo $$4, StructurePlaceSettings $$5) {
        Heightmap.Types $$9;
        if ($$0 instanceof ServerLevel) {
            if (this.heightmap == Heightmap.Types.WORLD_SURFACE_WG) {
                Heightmap.Types $$6 = Heightmap.Types.WORLD_SURFACE;
            } else if (this.heightmap == Heightmap.Types.OCEAN_FLOOR_WG) {
                Heightmap.Types $$7 = Heightmap.Types.OCEAN_FLOOR;
            } else {
                Heightmap.Types $$8 = this.heightmap;
            }
        } else {
            $$9 = this.heightmap;
        }
        int $$10 = $$0.getHeight($$9, $$4.pos.getX(), $$4.pos.getZ()) + this.offset;
        int $$11 = $$3.pos.getY();
        return new StructureTemplate.StructureBlockInfo(new BlockPos($$4.pos.getX(), $$10 + $$11, $$4.pos.getZ()), $$4.state, $$4.nbt);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.GRAVITY;
    }
}