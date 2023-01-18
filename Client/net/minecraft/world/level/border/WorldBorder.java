/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.DynamicLike
 *  java.lang.Double
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder {
    public static final double MAX_SIZE = 5.9999968E7;
    public static final double MAX_CENTER_COORDINATE = 2.9999984E7;
    private final List<BorderChangeListener> listeners = Lists.newArrayList();
    private double damagePerBlock = 0.2;
    private double damageSafeZone = 5.0;
    private int warningTime = 15;
    private int warningBlocks = 5;
    private double centerX;
    private double centerZ;
    int absoluteMaxSize = 29999984;
    private BorderExtent extent = new StaticBorderExtent(5.9999968E7);
    public static final Settings DEFAULT_SETTINGS = new Settings(0.0, 0.0, 0.2, 5.0, 5, 15, 5.9999968E7, 0L, 0.0);

    public boolean isWithinBounds(BlockPos $$0) {
        return (double)($$0.getX() + 1) > this.getMinX() && (double)$$0.getX() < this.getMaxX() && (double)($$0.getZ() + 1) > this.getMinZ() && (double)$$0.getZ() < this.getMaxZ();
    }

    public boolean isWithinBounds(ChunkPos $$0) {
        return (double)$$0.getMaxBlockX() > this.getMinX() && (double)$$0.getMinBlockX() < this.getMaxX() && (double)$$0.getMaxBlockZ() > this.getMinZ() && (double)$$0.getMinBlockZ() < this.getMaxZ();
    }

    public boolean isWithinBounds(double $$0, double $$1) {
        return $$0 > this.getMinX() && $$0 < this.getMaxX() && $$1 > this.getMinZ() && $$1 < this.getMaxZ();
    }

    public boolean isWithinBounds(double $$0, double $$1, double $$2) {
        return $$0 > this.getMinX() - $$2 && $$0 < this.getMaxX() + $$2 && $$1 > this.getMinZ() - $$2 && $$1 < this.getMaxZ() + $$2;
    }

    public boolean isWithinBounds(AABB $$0) {
        return $$0.maxX > this.getMinX() && $$0.minX < this.getMaxX() && $$0.maxZ > this.getMinZ() && $$0.minZ < this.getMaxZ();
    }

    public BlockPos clampToBounds(double $$0, double $$1, double $$2) {
        return new BlockPos(Mth.clamp($$0, this.getMinX(), this.getMaxX()), $$1, Mth.clamp($$2, this.getMinZ(), this.getMaxZ()));
    }

    public double getDistanceToBorder(Entity $$0) {
        return this.getDistanceToBorder($$0.getX(), $$0.getZ());
    }

    public VoxelShape getCollisionShape() {
        return this.extent.getCollisionShape();
    }

    public double getDistanceToBorder(double $$0, double $$1) {
        double $$2 = $$1 - this.getMinZ();
        double $$3 = this.getMaxZ() - $$1;
        double $$4 = $$0 - this.getMinX();
        double $$5 = this.getMaxX() - $$0;
        double $$6 = Math.min((double)$$4, (double)$$5);
        $$6 = Math.min((double)$$6, (double)$$2);
        return Math.min((double)$$6, (double)$$3);
    }

    public boolean isInsideCloseToBorder(Entity $$0, AABB $$1) {
        double $$2 = Math.max((double)Mth.absMax($$1.getXsize(), $$1.getZsize()), (double)1.0);
        return this.getDistanceToBorder($$0) < $$2 * 2.0 && this.isWithinBounds($$0.getX(), $$0.getZ(), $$2);
    }

    public BorderStatus getStatus() {
        return this.extent.getStatus();
    }

    public double getMinX() {
        return this.extent.getMinX();
    }

    public double getMinZ() {
        return this.extent.getMinZ();
    }

    public double getMaxX() {
        return this.extent.getMaxX();
    }

    public double getMaxZ() {
        return this.extent.getMaxZ();
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(double $$0, double $$1) {
        this.centerX = $$0;
        this.centerZ = $$1;
        this.extent.onCenterChange();
        for (BorderChangeListener $$2 : this.getListeners()) {
            $$2.onBorderCenterSet(this, $$0, $$1);
        }
    }

    public double getSize() {
        return this.extent.getSize();
    }

    public long getLerpRemainingTime() {
        return this.extent.getLerpRemainingTime();
    }

    public double getLerpTarget() {
        return this.extent.getLerpTarget();
    }

    public void setSize(double $$0) {
        this.extent = new StaticBorderExtent($$0);
        for (BorderChangeListener $$1 : this.getListeners()) {
            $$1.onBorderSizeSet(this, $$0);
        }
    }

    public void lerpSizeBetween(double $$0, double $$1, long $$2) {
        this.extent = $$0 == $$1 ? new StaticBorderExtent($$1) : new MovingBorderExtent($$0, $$1, $$2);
        for (BorderChangeListener $$3 : this.getListeners()) {
            $$3.onBorderSizeLerping(this, $$0, $$1, $$2);
        }
    }

    protected List<BorderChangeListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(BorderChangeListener $$0) {
        this.listeners.add((Object)$$0);
    }

    public void removeListener(BorderChangeListener $$0) {
        this.listeners.remove((Object)$$0);
    }

    public void setAbsoluteMaxSize(int $$0) {
        this.absoluteMaxSize = $$0;
        this.extent.onAbsoluteMaxSizeChange();
    }

    public int getAbsoluteMaxSize() {
        return this.absoluteMaxSize;
    }

    public double getDamageSafeZone() {
        return this.damageSafeZone;
    }

    public void setDamageSafeZone(double $$0) {
        this.damageSafeZone = $$0;
        for (BorderChangeListener $$1 : this.getListeners()) {
            $$1.onBorderSetDamageSafeZOne(this, $$0);
        }
    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double $$0) {
        this.damagePerBlock = $$0;
        for (BorderChangeListener $$1 : this.getListeners()) {
            $$1.onBorderSetDamagePerBlock(this, $$0);
        }
    }

    public double getLerpSpeed() {
        return this.extent.getLerpSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int $$0) {
        this.warningTime = $$0;
        for (BorderChangeListener $$1 : this.getListeners()) {
            $$1.onBorderSetWarningTime(this, $$0);
        }
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int $$0) {
        this.warningBlocks = $$0;
        for (BorderChangeListener $$1 : this.getListeners()) {
            $$1.onBorderSetWarningBlocks(this, $$0);
        }
    }

    public void tick() {
        this.extent = this.extent.update();
    }

    public Settings createSettings() {
        return new Settings(this);
    }

    public void applySettings(Settings $$0) {
        this.setCenter($$0.getCenterX(), $$0.getCenterZ());
        this.setDamagePerBlock($$0.getDamagePerBlock());
        this.setDamageSafeZone($$0.getSafeZone());
        this.setWarningBlocks($$0.getWarningBlocks());
        this.setWarningTime($$0.getWarningTime());
        if ($$0.getSizeLerpTime() > 0L) {
            this.lerpSizeBetween($$0.getSize(), $$0.getSizeLerpTarget(), $$0.getSizeLerpTime());
        } else {
            this.setSize($$0.getSize());
        }
    }

    class StaticBorderExtent
    implements BorderExtent {
        private final double size;
        private double minX;
        private double minZ;
        private double maxX;
        private double maxZ;
        private VoxelShape shape;

        public StaticBorderExtent(double $$0) {
            this.size = $$0;
            this.updateBox();
        }

        @Override
        public double getMinX() {
            return this.minX;
        }

        @Override
        public double getMaxX() {
            return this.maxX;
        }

        @Override
        public double getMinZ() {
            return this.minZ;
        }

        @Override
        public double getMaxZ() {
            return this.maxZ;
        }

        @Override
        public double getSize() {
            return this.size;
        }

        @Override
        public BorderStatus getStatus() {
            return BorderStatus.STATIONARY;
        }

        @Override
        public double getLerpSpeed() {
            return 0.0;
        }

        @Override
        public long getLerpRemainingTime() {
            return 0L;
        }

        @Override
        public double getLerpTarget() {
            return this.size;
        }

        private void updateBox() {
            this.minX = Mth.clamp(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
            this.minZ = Mth.clamp(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
            this.maxX = Mth.clamp(WorldBorder.this.getCenterX() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
            this.maxZ = Mth.clamp(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
            this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor((double)this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor((double)this.getMinZ()), Math.ceil((double)this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil((double)this.getMaxZ())), BooleanOp.ONLY_FIRST);
        }

        @Override
        public void onAbsoluteMaxSizeChange() {
            this.updateBox();
        }

        @Override
        public void onCenterChange() {
            this.updateBox();
        }

        @Override
        public BorderExtent update() {
            return this;
        }

        @Override
        public VoxelShape getCollisionShape() {
            return this.shape;
        }
    }

    static interface BorderExtent {
        public double getMinX();

        public double getMaxX();

        public double getMinZ();

        public double getMaxZ();

        public double getSize();

        public double getLerpSpeed();

        public long getLerpRemainingTime();

        public double getLerpTarget();

        public BorderStatus getStatus();

        public void onAbsoluteMaxSizeChange();

        public void onCenterChange();

        public BorderExtent update();

        public VoxelShape getCollisionShape();
    }

    class MovingBorderExtent
    implements BorderExtent {
        private final double from;
        private final double to;
        private final long lerpEnd;
        private final long lerpBegin;
        private final double lerpDuration;

        MovingBorderExtent(double $$0, double $$1, long $$2) {
            this.from = $$0;
            this.to = $$1;
            this.lerpDuration = $$2;
            this.lerpBegin = Util.getMillis();
            this.lerpEnd = this.lerpBegin + $$2;
        }

        @Override
        public double getMinX() {
            return Mth.clamp(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMinZ() {
            return Mth.clamp(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxX() {
            return Mth.clamp(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getMaxZ() {
            return Mth.clamp(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
        }

        @Override
        public double getSize() {
            double $$0 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
            return $$0 < 1.0 ? Mth.lerp($$0, this.from, this.to) : this.to;
        }

        @Override
        public double getLerpSpeed() {
            return Math.abs((double)(this.from - this.to)) / (double)(this.lerpEnd - this.lerpBegin);
        }

        @Override
        public long getLerpRemainingTime() {
            return this.lerpEnd - Util.getMillis();
        }

        @Override
        public double getLerpTarget() {
            return this.to;
        }

        @Override
        public BorderStatus getStatus() {
            return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
        }

        @Override
        public void onCenterChange() {
        }

        @Override
        public void onAbsoluteMaxSizeChange() {
        }

        @Override
        public BorderExtent update() {
            if (this.getLerpRemainingTime() <= 0L) {
                return new StaticBorderExtent(this.to);
            }
            return this;
        }

        @Override
        public VoxelShape getCollisionShape() {
            return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor((double)this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor((double)this.getMinZ()), Math.ceil((double)this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil((double)this.getMaxZ())), BooleanOp.ONLY_FIRST);
        }
    }

    public static class Settings {
        private final double centerX;
        private final double centerZ;
        private final double damagePerBlock;
        private final double safeZone;
        private final int warningBlocks;
        private final int warningTime;
        private final double size;
        private final long sizeLerpTime;
        private final double sizeLerpTarget;

        Settings(double $$0, double $$1, double $$2, double $$3, int $$4, int $$5, double $$6, long $$7, double $$8) {
            this.centerX = $$0;
            this.centerZ = $$1;
            this.damagePerBlock = $$2;
            this.safeZone = $$3;
            this.warningBlocks = $$4;
            this.warningTime = $$5;
            this.size = $$6;
            this.sizeLerpTime = $$7;
            this.sizeLerpTarget = $$8;
        }

        Settings(WorldBorder $$0) {
            this.centerX = $$0.getCenterX();
            this.centerZ = $$0.getCenterZ();
            this.damagePerBlock = $$0.getDamagePerBlock();
            this.safeZone = $$0.getDamageSafeZone();
            this.warningBlocks = $$0.getWarningBlocks();
            this.warningTime = $$0.getWarningTime();
            this.size = $$0.getSize();
            this.sizeLerpTime = $$0.getLerpRemainingTime();
            this.sizeLerpTarget = $$0.getLerpTarget();
        }

        public double getCenterX() {
            return this.centerX;
        }

        public double getCenterZ() {
            return this.centerZ;
        }

        public double getDamagePerBlock() {
            return this.damagePerBlock;
        }

        public double getSafeZone() {
            return this.safeZone;
        }

        public int getWarningBlocks() {
            return this.warningBlocks;
        }

        public int getWarningTime() {
            return this.warningTime;
        }

        public double getSize() {
            return this.size;
        }

        public long getSizeLerpTime() {
            return this.sizeLerpTime;
        }

        public double getSizeLerpTarget() {
            return this.sizeLerpTarget;
        }

        public static Settings read(DynamicLike<?> $$0, Settings $$1) {
            double $$2 = Mth.clamp($$0.get("BorderCenterX").asDouble($$1.centerX), -2.9999984E7, 2.9999984E7);
            double $$3 = Mth.clamp($$0.get("BorderCenterZ").asDouble($$1.centerZ), -2.9999984E7, 2.9999984E7);
            double $$4 = $$0.get("BorderSize").asDouble($$1.size);
            long $$5 = $$0.get("BorderSizeLerpTime").asLong($$1.sizeLerpTime);
            double $$6 = $$0.get("BorderSizeLerpTarget").asDouble($$1.sizeLerpTarget);
            double $$7 = $$0.get("BorderSafeZone").asDouble($$1.safeZone);
            double $$8 = $$0.get("BorderDamagePerBlock").asDouble($$1.damagePerBlock);
            int $$9 = $$0.get("BorderWarningBlocks").asInt($$1.warningBlocks);
            int $$10 = $$0.get("BorderWarningTime").asInt($$1.warningTime);
            return new Settings($$2, $$3, $$8, $$7, $$9, $$10, $$4, $$5, $$6);
        }

        public void write(CompoundTag $$0) {
            $$0.putDouble("BorderCenterX", this.centerX);
            $$0.putDouble("BorderCenterZ", this.centerZ);
            $$0.putDouble("BorderSize", this.size);
            $$0.putLong("BorderSizeLerpTime", this.sizeLerpTime);
            $$0.putDouble("BorderSafeZone", this.safeZone);
            $$0.putDouble("BorderDamagePerBlock", this.damagePerBlock);
            $$0.putDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
            $$0.putDouble("BorderWarningBlocks", this.warningBlocks);
            $$0.putDouble("BorderWarningTime", this.warningTime);
        }
    }
}