/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ListPoolElement
extends StructurePoolElement {
    public static final Codec<ListPoolElement> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter($$0 -> $$0.elements), ListPoolElement.projectionCodec()).apply((Applicative)$$02, ListPoolElement::new));
    private final List<StructurePoolElement> elements;

    public ListPoolElement(List<StructurePoolElement> $$0, StructureTemplatePool.Projection $$1) {
        super($$1);
        if ($$0.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = $$0;
        this.setProjectionOnEachElement($$1);
    }

    @Override
    public Vec3i getSize(StructureTemplateManager $$0, Rotation $$1) {
        int $$2 = 0;
        int $$3 = 0;
        int $$4 = 0;
        for (StructurePoolElement $$5 : this.elements) {
            Vec3i $$6 = $$5.getSize($$0, $$1);
            $$2 = Math.max((int)$$2, (int)$$6.getX());
            $$3 = Math.max((int)$$3, (int)$$6.getY());
            $$4 = Math.max((int)$$4, (int)$$6.getZ());
        }
        return new Vec3i($$2, $$3, $$4);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, RandomSource $$3) {
        return ((StructurePoolElement)this.elements.get(0)).getShuffledJigsawBlocks($$0, $$1, $$2, $$3);
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager $$02, BlockPos $$1, Rotation $$2) {
        Stream $$32 = this.elements.stream().filter($$0 -> $$0 != EmptyPoolElement.INSTANCE).map($$3 -> $$3.getBoundingBox($$02, $$1, $$2));
        return (BoundingBox)BoundingBox.encapsulatingBoxes((Iterable<BoundingBox>)((Iterable)() -> ((Stream)$$32).iterator())).orElseThrow(() -> new IllegalStateException("Unable to calculate boundingbox for ListPoolElement"));
    }

    @Override
    public boolean place(StructureTemplateManager $$0, WorldGenLevel $$1, StructureManager $$2, ChunkGenerator $$3, BlockPos $$4, BlockPos $$5, Rotation $$6, BoundingBox $$7, RandomSource $$8, boolean $$9) {
        for (StructurePoolElement $$10 : this.elements) {
            if ($$10.place($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LIST;
    }

    @Override
    public StructurePoolElement setProjection(StructureTemplatePool.Projection $$0) {
        super.setProjection($$0);
        this.setProjectionOnEachElement($$0);
        return this;
    }

    public String toString() {
        return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining((CharSequence)", ")) + "]";
    }

    private void setProjectionOnEachElement(StructureTemplatePool.Projection $$0) {
        this.elements.forEach($$1 -> $$1.setProjection($$0));
    }
}