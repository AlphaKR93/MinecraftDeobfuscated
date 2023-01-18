/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class MapFrame {
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;

    public MapFrame(BlockPos $$0, int $$1, int $$2) {
        this.pos = $$0;
        this.rotation = $$1;
        this.entityId = $$2;
    }

    public static MapFrame load(CompoundTag $$0) {
        BlockPos $$1 = NbtUtils.readBlockPos($$0.getCompound("Pos"));
        int $$2 = $$0.getInt("Rotation");
        int $$3 = $$0.getInt("EntityId");
        return new MapFrame($$1, $$2, $$3);
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.put("Pos", NbtUtils.writeBlockPos(this.pos));
        $$0.putInt("Rotation", this.rotation);
        $$0.putInt("EntityId", this.entityId);
        return $$0;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getId() {
        return MapFrame.frameId(this.pos);
    }

    public static String frameId(BlockPos $$0) {
        return "frame-" + $$0.getX() + "," + $$0.getY() + "," + $$0.getZ();
    }
}