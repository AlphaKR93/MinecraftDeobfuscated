/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SinglePoolElement
extends StructurePoolElement {
    private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(SinglePoolElement::encodeTemplate, (Decoder)ResourceLocation.CODEC.map(Either::left));
    public static final Codec<SinglePoolElement> CODEC = RecordCodecBuilder.create($$0 -> $$0.group(SinglePoolElement.templateCodec(), SinglePoolElement.processorsCodec(), SinglePoolElement.projectionCodec()).apply((Applicative)$$0, SinglePoolElement::new));
    protected final Either<ResourceLocation, StructureTemplate> template;
    protected final Holder<StructureProcessorList> processors;

    private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> $$0, DynamicOps<T> $$1, T $$2) {
        Optional $$3 = $$0.left();
        if (!$$3.isPresent()) {
            return DataResult.error((String)"Can not serialize a runtime pool element");
        }
        return ResourceLocation.CODEC.encode((Object)((ResourceLocation)$$3.get()), $$1, $$2);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
        return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter($$0 -> $$0.processors);
    }

    protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
        return TEMPLATE_CODEC.fieldOf("location").forGetter($$0 -> $$0.template);
    }

    protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> $$0, Holder<StructureProcessorList> $$1, StructureTemplatePool.Projection $$2) {
        super($$2);
        this.template = $$0;
        this.processors = $$1;
    }

    @Override
    public Vec3i getSize(StructureTemplateManager $$0, Rotation $$1) {
        StructureTemplate $$2 = this.getTemplate($$0);
        return $$2.getSize($$1);
    }

    private StructureTemplate getTemplate(StructureTemplateManager $$0) {
        return (StructureTemplate)this.template.map($$0::getOrCreate, Function.identity());
    }

    public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, boolean $$3) {
        StructureTemplate $$4 = this.getTemplate($$0);
        ObjectArrayList<StructureTemplate.StructureBlockInfo> $$5 = $$4.filterBlocks($$1, new StructurePlaceSettings().setRotation($$2), Blocks.STRUCTURE_BLOCK, $$3);
        ArrayList $$6 = Lists.newArrayList();
        for (StructureTemplate.StructureBlockInfo $$7 : $$5) {
            StructureMode $$8;
            if ($$7.nbt == null || ($$8 = StructureMode.valueOf($$7.nbt.getString("mode"))) != StructureMode.DATA) continue;
            $$6.add((Object)$$7);
        }
        return $$6;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, RandomSource $$3) {
        StructureTemplate $$4 = this.getTemplate($$0);
        ObjectArrayList<StructureTemplate.StructureBlockInfo> $$5 = $$4.filterBlocks($$1, new StructurePlaceSettings().setRotation($$2), Blocks.JIGSAW, true);
        Util.shuffle($$5, $$3);
        return $$5;
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2) {
        StructureTemplate $$3 = this.getTemplate($$0);
        return $$3.getBoundingBox(new StructurePlaceSettings().setRotation($$2), $$1);
    }

    @Override
    public boolean place(StructureTemplateManager $$0, WorldGenLevel $$1, StructureManager $$2, ChunkGenerator $$3, BlockPos $$4, BlockPos $$5, Rotation $$6, BoundingBox $$7, RandomSource $$8, boolean $$9) {
        StructurePlaceSettings $$11;
        StructureTemplate $$10 = this.getTemplate($$0);
        if ($$10.placeInWorld($$1, $$4, $$5, $$11 = this.getSettings($$6, $$7, $$9), $$8, 18)) {
            List<StructureTemplate.StructureBlockInfo> $$12 = StructureTemplate.processBlockInfos($$1, $$4, $$5, $$11, this.getDataMarkers($$0, $$4, $$6, false));
            for (StructureTemplate.StructureBlockInfo $$13 : $$12) {
                this.handleDataMarker($$1, $$13, $$4, $$6, $$8, $$7);
            }
            return true;
        }
        return false;
    }

    protected StructurePlaceSettings getSettings(Rotation $$0, BoundingBox $$1, boolean $$2) {
        StructurePlaceSettings $$3 = new StructurePlaceSettings();
        $$3.setBoundingBox($$1);
        $$3.setRotation($$0);
        $$3.setKnownShape(true);
        $$3.setIgnoreEntities(false);
        $$3.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        $$3.setFinalizeEntities(true);
        if (!$$2) {
            $$3.addProcessor(JigsawReplacementProcessor.INSTANCE);
        }
        this.processors.value().list().forEach($$3::addProcessor);
        this.getProjection().getProcessors().forEach($$3::addProcessor);
        return $$3;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.SINGLE;
    }

    public String toString() {
        return "Single[" + this.template + "]";
    }
}