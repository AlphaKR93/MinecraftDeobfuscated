/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public abstract class TemplateStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final String templateName;
    protected StructureTemplate template;
    protected StructurePlaceSettings placeSettings;
    protected BlockPos templatePosition;

    public TemplateStructurePiece(StructurePieceType $$0, int $$1, StructureTemplateManager $$2, ResourceLocation $$3, String $$4, StructurePlaceSettings $$5, BlockPos $$6) {
        super($$0, $$1, $$2.getOrCreate($$3).getBoundingBox($$5, $$6));
        this.setOrientation(Direction.NORTH);
        this.templateName = $$4;
        this.templatePosition = $$6;
        this.template = $$2.getOrCreate($$3);
        this.placeSettings = $$5;
    }

    public TemplateStructurePiece(StructurePieceType $$0, CompoundTag $$1, StructureTemplateManager $$2, Function<ResourceLocation, StructurePlaceSettings> $$3) {
        super($$0, $$1);
        this.setOrientation(Direction.NORTH);
        this.templateName = $$1.getString("Template");
        this.templatePosition = new BlockPos($$1.getInt("TPX"), $$1.getInt("TPY"), $$1.getInt("TPZ"));
        ResourceLocation $$4 = this.makeTemplateLocation();
        this.template = $$2.getOrCreate($$4);
        this.placeSettings = (StructurePlaceSettings)$$3.apply((Object)$$4);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
    }

    protected ResourceLocation makeTemplateLocation() {
        return new ResourceLocation(this.templateName);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
        $$1.putInt("TPX", this.templatePosition.getX());
        $$1.putInt("TPY", this.templatePosition.getY());
        $$1.putInt("TPZ", this.templatePosition.getZ());
        $$1.putString("Template", this.templateName);
    }

    @Override
    public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
        this.placeSettings.setBoundingBox($$4);
        this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (this.template.placeInWorld($$0, this.templatePosition, $$6, this.placeSettings, $$3, 2)) {
            List<StructureTemplate.StructureBlockInfo> $$7 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
            for (StructureTemplate.StructureBlockInfo $$8 : $$7) {
                StructureMode $$9;
                if ($$8.nbt == null || ($$9 = StructureMode.valueOf($$8.nbt.getString("mode"))) != StructureMode.DATA) continue;
                this.handleDataMarker($$8.nbt.getString("metadata"), $$8.pos, $$0, $$3, $$4);
            }
            List<StructureTemplate.StructureBlockInfo> $$10 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW);
            for (StructureTemplate.StructureBlockInfo $$11 : $$10) {
                if ($$11.nbt == null) continue;
                String $$12 = $$11.nbt.getString("final_state");
                BlockState $$13 = Blocks.AIR.defaultBlockState();
                try {
                    $$13 = BlockStateParser.parseForBlock((HolderLookup<Block>)$$0.holderLookup(Registries.BLOCK), $$12, true).blockState();
                }
                catch (CommandSyntaxException $$14) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)$$12, (Object)$$11.pos);
                }
                $$0.setBlock($$11.pos, $$13, 3);
            }
        }
    }

    protected abstract void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5);

    @Override
    @Deprecated
    public void move(int $$0, int $$1, int $$2) {
        super.move($$0, $$1, $$2);
        this.templatePosition = this.templatePosition.offset($$0, $$1, $$2);
    }

    @Override
    public Rotation getRotation() {
        return this.placeSettings.getRotation();
    }

    public StructureTemplate template() {
        return this.template;
    }

    public BlockPos templatePosition() {
        return this.templatePosition;
    }

    public StructurePlaceSettings placeSettings() {
        return this.placeSettings;
    }
}