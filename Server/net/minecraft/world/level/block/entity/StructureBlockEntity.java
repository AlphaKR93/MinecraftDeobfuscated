/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class StructureBlockEntity
extends BlockEntity {
    private static final int SCAN_CORNER_BLOCKS_RANGE = 5;
    public static final int MAX_OFFSET_PER_AXIS = 48;
    public static final int MAX_SIZE_PER_AXIS = 48;
    public static final String AUTHOR_TAG = "author";
    private ResourceLocation structureName;
    private String author = "";
    private String metaData = "";
    private BlockPos structurePos = new BlockPos(0, 1, 0);
    private Vec3i structureSize = Vec3i.ZERO;
    private Mirror mirror = Mirror.NONE;
    private Rotation rotation = Rotation.NONE;
    private StructureMode mode;
    private boolean ignoreEntities = true;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1.0f;
    private long seed;

    public StructureBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.STRUCTURE_BLOCK, $$0, $$1);
        this.mode = $$1.getValue(StructureBlock.MODE);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putString("name", this.getStructureName());
        $$0.putString(AUTHOR_TAG, this.author);
        $$0.putString("metadata", this.metaData);
        $$0.putInt("posX", this.structurePos.getX());
        $$0.putInt("posY", this.structurePos.getY());
        $$0.putInt("posZ", this.structurePos.getZ());
        $$0.putInt("sizeX", this.structureSize.getX());
        $$0.putInt("sizeY", this.structureSize.getY());
        $$0.putInt("sizeZ", this.structureSize.getZ());
        $$0.putString("rotation", this.rotation.toString());
        $$0.putString("mirror", this.mirror.toString());
        $$0.putString("mode", this.mode.toString());
        $$0.putBoolean("ignoreEntities", this.ignoreEntities);
        $$0.putBoolean("powered", this.powered);
        $$0.putBoolean("showair", this.showAir);
        $$0.putBoolean("showboundingbox", this.showBoundingBox);
        $$0.putFloat("integrity", this.integrity);
        $$0.putLong("seed", this.seed);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.setStructureName($$0.getString("name"));
        this.author = $$0.getString(AUTHOR_TAG);
        this.metaData = $$0.getString("metadata");
        int $$1 = Mth.clamp($$0.getInt("posX"), -48, 48);
        int $$2 = Mth.clamp($$0.getInt("posY"), -48, 48);
        int $$3 = Mth.clamp($$0.getInt("posZ"), -48, 48);
        this.structurePos = new BlockPos($$1, $$2, $$3);
        int $$4 = Mth.clamp($$0.getInt("sizeX"), 0, 48);
        int $$5 = Mth.clamp($$0.getInt("sizeY"), 0, 48);
        int $$6 = Mth.clamp($$0.getInt("sizeZ"), 0, 48);
        this.structureSize = new Vec3i($$4, $$5, $$6);
        try {
            this.rotation = Rotation.valueOf($$0.getString("rotation"));
        }
        catch (IllegalArgumentException $$7) {
            this.rotation = Rotation.NONE;
        }
        try {
            this.mirror = Mirror.valueOf($$0.getString("mirror"));
        }
        catch (IllegalArgumentException $$8) {
            this.mirror = Mirror.NONE;
        }
        try {
            this.mode = StructureMode.valueOf($$0.getString("mode"));
        }
        catch (IllegalArgumentException $$9) {
            this.mode = StructureMode.DATA;
        }
        this.ignoreEntities = $$0.getBoolean("ignoreEntities");
        this.powered = $$0.getBoolean("powered");
        this.showAir = $$0.getBoolean("showair");
        this.showBoundingBox = $$0.getBoolean("showboundingbox");
        this.integrity = $$0.contains("integrity") ? $$0.getFloat("integrity") : 1.0f;
        this.seed = $$0.getLong("seed");
        this.updateBlockState();
    }

    private void updateBlockState() {
        if (this.level == null) {
            return;
        }
        BlockPos $$0 = this.getBlockPos();
        BlockState $$1 = this.level.getBlockState($$0);
        if ($$1.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock($$0, (BlockState)$$1.setValue(StructureBlock.MODE, this.mode), 2);
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean usedBy(Player $$0) {
        if (!$$0.canUseGameMasterBlocks()) {
            return false;
        }
        if ($$0.getCommandSenderWorld().isClientSide) {
            $$0.openStructureBlock(this);
        }
        return true;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String $$0) {
        this.setStructureName(StringUtil.isNullOrEmpty($$0) ? null : ResourceLocation.tryParse($$0));
    }

    public void setStructureName(@Nullable ResourceLocation $$0) {
        this.structureName = $$0;
    }

    public void createdBy(LivingEntity $$0) {
        this.author = $$0.getName().getString();
    }

    public BlockPos getStructurePos() {
        return this.structurePos;
    }

    public void setStructurePos(BlockPos $$0) {
        this.structurePos = $$0;
    }

    public Vec3i getStructureSize() {
        return this.structureSize;
    }

    public void setStructureSize(Vec3i $$0) {
        this.structureSize = $$0;
    }

    public Mirror getMirror() {
        return this.mirror;
    }

    public void setMirror(Mirror $$0) {
        this.mirror = $$0;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation $$0) {
        this.rotation = $$0;
    }

    public String getMetaData() {
        return this.metaData;
    }

    public void setMetaData(String $$0) {
        this.metaData = $$0;
    }

    public StructureMode getMode() {
        return this.mode;
    }

    public void setMode(StructureMode $$0) {
        this.mode = $$0;
        BlockState $$1 = this.level.getBlockState(this.getBlockPos());
        if ($$1.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(this.getBlockPos(), (BlockState)$$1.setValue(StructureBlock.MODE, $$0), 2);
        }
    }

    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean $$0) {
        this.ignoreEntities = $$0;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float $$0) {
        this.integrity = $$0;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long $$0) {
        this.seed = $$0;
    }

    public boolean detectSize() {
        if (this.mode != StructureMode.SAVE) {
            return false;
        }
        BlockPos $$0 = this.getBlockPos();
        int $$12 = 80;
        BlockPos $$2 = new BlockPos($$0.getX() - 80, this.level.getMinBuildHeight(), $$0.getZ() - 80);
        BlockPos $$3 = new BlockPos($$0.getX() + 80, this.level.getMaxBuildHeight() - 1, $$0.getZ() + 80);
        Stream<BlockPos> $$4 = this.getRelatedCorners($$2, $$3);
        return StructureBlockEntity.calculateEnclosingBoundingBox($$0, $$4).filter($$1 -> {
            int $$2 = $$1.maxX() - $$1.minX();
            int $$3 = $$1.maxY() - $$1.minY();
            int $$4 = $$1.maxZ() - $$1.minZ();
            if ($$2 > 1 && $$3 > 1 && $$4 > 1) {
                this.structurePos = new BlockPos($$1.minX() - $$0.getX() + 1, $$1.minY() - $$0.getY() + 1, $$1.minZ() - $$0.getZ() + 1);
                this.structureSize = new Vec3i($$2 - 1, $$3 - 1, $$4 - 1);
                this.setChanged();
                BlockState $$5 = this.level.getBlockState($$0);
                this.level.sendBlockUpdated($$0, $$5, $$5, 3);
                return true;
            }
            return false;
        }).isPresent();
    }

    private Stream<BlockPos> getRelatedCorners(BlockPos $$02, BlockPos $$1) {
        return BlockPos.betweenClosedStream($$02, $$1).filter($$0 -> this.level.getBlockState((BlockPos)$$0).is(Blocks.STRUCTURE_BLOCK)).map(this.level::getBlockEntity).filter($$0 -> $$0 instanceof StructureBlockEntity).map($$0 -> (StructureBlockEntity)$$0).filter($$0 -> $$0.mode == StructureMode.CORNER && Objects.equals((Object)this.structureName, (Object)$$0.structureName)).map(BlockEntity::getBlockPos);
    }

    private static Optional<BoundingBox> calculateEnclosingBoundingBox(BlockPos $$0, Stream<BlockPos> $$1) {
        Iterator $$2 = $$1.iterator();
        if (!$$2.hasNext()) {
            return Optional.empty();
        }
        BlockPos $$3 = (BlockPos)$$2.next();
        BoundingBox $$4 = new BoundingBox($$3);
        if ($$2.hasNext()) {
            $$2.forEachRemaining($$4::encapsulate);
        } else {
            $$4.encapsulate($$0);
        }
        return Optional.of((Object)$$4);
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    /*
     * WARNING - void declaration
     */
    public boolean saveStructure(boolean $$0) {
        void $$6;
        if (this.mode != StructureMode.SAVE || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        Vec3i $$1 = this.getBlockPos().offset(this.structurePos);
        ServerLevel $$2 = (ServerLevel)this.level;
        StructureTemplateManager $$3 = $$2.getStructureManager();
        try {
            StructureTemplate $$4 = $$3.getOrCreate(this.structureName);
        }
        catch (ResourceLocationException $$5) {
            return false;
        }
        $$6.fillFromWorld(this.level, (BlockPos)$$1, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        $$6.setAuthor(this.author);
        if ($$0) {
            try {
                return $$3.save(this.structureName);
            }
            catch (ResourceLocationException $$7) {
                return false;
            }
        }
        return true;
    }

    public boolean loadStructure(ServerLevel $$0) {
        return this.loadStructure($$0, true);
    }

    public static RandomSource createRandom(long $$0) {
        if ($$0 == 0L) {
            return RandomSource.create(Util.getMillis());
        }
        return RandomSource.create($$0);
    }

    /*
     * WARNING - void declaration
     */
    public boolean loadStructure(ServerLevel $$0, boolean $$1) {
        void $$5;
        if (this.mode != StructureMode.LOAD || this.structureName == null) {
            return false;
        }
        StructureTemplateManager $$2 = $$0.getStructureManager();
        try {
            Optional<StructureTemplate> $$3 = $$2.get(this.structureName);
        }
        catch (ResourceLocationException $$4) {
            return false;
        }
        if (!$$5.isPresent()) {
            return false;
        }
        return this.loadStructure($$0, $$1, (StructureTemplate)$$5.get());
    }

    public boolean loadStructure(ServerLevel $$0, boolean $$1, StructureTemplate $$2) {
        Vec3i $$4;
        boolean $$5;
        BlockPos $$3 = this.getBlockPos();
        if (!StringUtil.isNullOrEmpty($$2.getAuthor())) {
            this.author = $$2.getAuthor();
        }
        if (!($$5 = this.structureSize.equals($$4 = $$2.getSize()))) {
            this.structureSize = $$4;
            this.setChanged();
            BlockState $$6 = $$0.getBlockState($$3);
            $$0.sendBlockUpdated($$3, $$6, $$6, 3);
        }
        if (!$$1 || $$5) {
            StructurePlaceSettings $$7 = new StructurePlaceSettings().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);
            if (this.integrity < 1.0f) {
                $$7.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0f, 1.0f))).setRandom(StructureBlockEntity.createRandom(this.seed));
            }
            Vec3i $$8 = $$3.offset(this.structurePos);
            $$2.placeInWorld($$0, (BlockPos)$$8, (BlockPos)$$8, $$7, StructureBlockEntity.createRandom(this.seed), 2);
            return true;
        }
        return false;
    }

    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        ServerLevel $$0 = (ServerLevel)this.level;
        StructureTemplateManager $$1 = $$0.getStructureManager();
        $$1.remove(this.structureName);
    }

    public boolean isStructureLoadable() {
        if (this.mode != StructureMode.LOAD || this.level.isClientSide || this.structureName == null) {
            return false;
        }
        ServerLevel $$0 = (ServerLevel)this.level;
        StructureTemplateManager $$1 = $$0.getStructureManager();
        try {
            return $$1.get(this.structureName).isPresent();
        }
        catch (ResourceLocationException $$2) {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean $$0) {
        this.powered = $$0;
    }

    public boolean getShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean $$0) {
        this.showAir = $$0;
    }

    public boolean getShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean $$0) {
        this.showBoundingBox = $$0;
    }

    private static /* synthetic */ void lambda$loadStructure$5(ServerLevel $$0, BlockPos $$1) {
        $$0.setBlock($$1, Blocks.STRUCTURE_VOID.defaultBlockState(), 2);
    }

    public static enum UpdateType {
        UPDATE_DATA,
        SAVE_AREA,
        LOAD_AREA,
        SCAN_AREA;

    }
}