/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class PoolElementStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final StructurePoolElement element;
    protected BlockPos position;
    private final int groundLevelDelta;
    protected final Rotation rotation;
    private final List<JigsawJunction> junctions = Lists.newArrayList();
    private final StructureTemplateManager structureTemplateManager;

    public PoolElementStructurePiece(StructureTemplateManager $$0, StructurePoolElement $$1, BlockPos $$2, int $$3, Rotation $$4, BoundingBox $$5) {
        super(StructurePieceType.JIGSAW, 0, $$5);
        this.structureTemplateManager = $$0;
        this.element = $$1;
        this.position = $$2;
        this.groundLevelDelta = $$3;
        this.rotation = $$4;
    }

    public PoolElementStructurePiece(StructurePieceSerializationContext $$0, CompoundTag $$12) {
        super(StructurePieceType.JIGSAW, $$12);
        this.structureTemplateManager = $$0.structureTemplateManager();
        this.position = new BlockPos($$12.getInt("PosX"), $$12.getInt("PosY"), $$12.getInt("PosZ"));
        this.groundLevelDelta = $$12.getInt("ground_level_delta");
        RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, $$0.registryAccess());
        this.element = (StructurePoolElement)StructurePoolElement.CODEC.parse($$2, (Object)$$12.getCompound("pool_element")).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseThrow(() -> new IllegalStateException("Invalid pool element found"));
        this.rotation = Rotation.valueOf($$12.getString("rotation"));
        this.boundingBox = this.element.getBoundingBox(this.structureTemplateManager, this.position, this.rotation);
        ListTag $$3 = $$12.getList("junctions", 10);
        this.junctions.clear();
        $$3.forEach($$1 -> this.junctions.add((Object)JigsawJunction.deserialize(new Dynamic($$2, $$1))));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$12) {
        $$12.putInt("PosX", this.position.getX());
        $$12.putInt("PosY", this.position.getY());
        $$12.putInt("PosZ", this.position.getZ());
        $$12.putInt("ground_level_delta", this.groundLevelDelta);
        RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, $$0.registryAccess());
        StructurePoolElement.CODEC.encodeStart($$2, (Object)this.element).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$12.put("pool_element", (Tag)$$1));
        $$12.putString("rotation", this.rotation.name());
        ListTag $$3 = new ListTag();
        for (JigsawJunction $$4 : this.junctions) {
            $$3.add((Tag)$$4.serialize($$2).getValue());
        }
        $$12.put("junctions", $$3);
    }

    @Override
    public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
        this.place($$0, $$1, $$2, $$3, $$4, $$6, false);
    }

    public void place(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, BlockPos $$5, boolean $$6) {
        this.element.place(this.structureTemplateManager, $$0, $$1, $$2, this.position, $$5, this.rotation, $$4, $$3, $$6);
    }

    @Override
    public void move(int $$0, int $$1, int $$2) {
        super.move($$0, $$1, $$2);
        this.position = this.position.offset($$0, $$1, $$2);
    }

    @Override
    public Rotation getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"<%s | %s | %s | %s>", (Object[])new Object[]{this.getClass().getSimpleName(), this.position, this.rotation, this.element});
    }

    public StructurePoolElement getElement() {
        return this.element;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }

    public void addJunction(JigsawJunction $$0) {
        this.junctions.add((Object)$$0);
    }

    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}