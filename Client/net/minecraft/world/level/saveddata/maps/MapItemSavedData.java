/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  java.lang.IllegalArgumentException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import org.slf4j.Logger;

public class MapItemSavedData
extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAP_SIZE = 128;
    private static final int HALF_MAP_SIZE = 64;
    public static final int MAX_SCALE = 4;
    public static final int TRACKED_DECORATION_LIMIT = 256;
    public final int centerX;
    public final int centerZ;
    public final ResourceKey<Level> dimension;
    private final boolean trackingPosition;
    private final boolean unlimitedTracking;
    public final byte scale;
    public byte[] colors = new byte[16384];
    public final boolean locked;
    private final List<HoldingPlayer> carriedBy = Lists.newArrayList();
    private final Map<Player, HoldingPlayer> carriedByPlayers = Maps.newHashMap();
    private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
    final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
    private int trackedDecorationCount;

    private MapItemSavedData(int $$0, int $$1, byte $$2, boolean $$3, boolean $$4, boolean $$5, ResourceKey<Level> $$6) {
        this.scale = $$2;
        this.centerX = $$0;
        this.centerZ = $$1;
        this.dimension = $$6;
        this.trackingPosition = $$3;
        this.unlimitedTracking = $$4;
        this.locked = $$5;
        this.setDirty();
    }

    public static MapItemSavedData createFresh(double $$0, double $$1, byte $$2, boolean $$3, boolean $$4, ResourceKey<Level> $$5) {
        int $$6 = 128 * (1 << $$2);
        int $$7 = Mth.floor(($$0 + 64.0) / (double)$$6);
        int $$8 = Mth.floor(($$1 + 64.0) / (double)$$6);
        int $$9 = $$7 * $$6 + $$6 / 2 - 64;
        int $$10 = $$8 * $$6 + $$6 / 2 - 64;
        return new MapItemSavedData($$9, $$10, $$2, $$3, $$4, false, $$5);
    }

    public static MapItemSavedData createForClient(byte $$0, boolean $$1, ResourceKey<Level> $$2) {
        return new MapItemSavedData(0, 0, $$0, false, false, $$1, $$2);
    }

    public static MapItemSavedData load(CompoundTag $$0) {
        ResourceKey $$1 = (ResourceKey)DimensionType.parseLegacy(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$0.get("dimension"))).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + $$0.get("dimension")));
        int $$2 = $$0.getInt("xCenter");
        int $$3 = $$0.getInt("zCenter");
        byte $$4 = (byte)Mth.clamp((int)$$0.getByte("scale"), 0, 4);
        boolean $$5 = !$$0.contains("trackingPosition", 1) || $$0.getBoolean("trackingPosition");
        boolean $$6 = $$0.getBoolean("unlimitedTracking");
        boolean $$7 = $$0.getBoolean("locked");
        MapItemSavedData $$8 = new MapItemSavedData($$2, $$3, $$4, $$5, $$6, $$7, $$1);
        byte[] $$9 = $$0.getByteArray("colors");
        if ($$9.length == 16384) {
            $$8.colors = $$9;
        }
        ListTag $$10 = $$0.getList("banners", 10);
        for (int $$11 = 0; $$11 < $$10.size(); ++$$11) {
            MapBanner $$12 = MapBanner.load($$10.getCompound($$11));
            $$8.bannerMarkers.put((Object)$$12.getId(), (Object)$$12);
            $$8.addDecoration($$12.getDecoration(), null, $$12.getId(), $$12.getPos().getX(), $$12.getPos().getZ(), 180.0, $$12.getName());
        }
        ListTag $$13 = $$0.getList("frames", 10);
        for (int $$14 = 0; $$14 < $$13.size(); ++$$14) {
            MapFrame $$15 = MapFrame.load($$13.getCompound($$14));
            $$8.frameMarkers.put((Object)$$15.getId(), (Object)$$15);
            $$8.addDecoration(MapDecoration.Type.FRAME, null, "frame-" + $$15.getEntityId(), $$15.getPos().getX(), $$15.getPos().getZ(), $$15.getRotation(), null);
        }
        return $$8;
    }

    @Override
    public CompoundTag save(CompoundTag $$0) {
        ResourceLocation.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dimension.location()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$0.put("dimension", (Tag)$$1));
        $$0.putInt("xCenter", this.centerX);
        $$0.putInt("zCenter", this.centerZ);
        $$0.putByte("scale", this.scale);
        $$0.putByteArray("colors", this.colors);
        $$0.putBoolean("trackingPosition", this.trackingPosition);
        $$0.putBoolean("unlimitedTracking", this.unlimitedTracking);
        $$0.putBoolean("locked", this.locked);
        ListTag $$12 = new ListTag();
        for (MapBanner $$2 : this.bannerMarkers.values()) {
            $$12.add($$2.save());
        }
        $$0.put("banners", $$12);
        ListTag $$3 = new ListTag();
        for (MapFrame $$4 : this.frameMarkers.values()) {
            $$3.add($$4.save());
        }
        $$0.put("frames", $$3);
        return $$0;
    }

    public MapItemSavedData locked() {
        MapItemSavedData $$0 = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
        $$0.bannerMarkers.putAll(this.bannerMarkers);
        $$0.decorations.putAll(this.decorations);
        $$0.trackedDecorationCount = this.trackedDecorationCount;
        System.arraycopy((Object)this.colors, (int)0, (Object)$$0.colors, (int)0, (int)this.colors.length);
        $$0.setDirty();
        return $$0;
    }

    public MapItemSavedData scaled(int $$0) {
        return MapItemSavedData.createFresh(this.centerX, this.centerZ, (byte)Mth.clamp(this.scale + $$0, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
    }

    public void tickCarriedBy(Player $$0, ItemStack $$1) {
        CompoundTag $$10;
        if (!this.carriedByPlayers.containsKey((Object)$$0)) {
            HoldingPlayer $$2 = new HoldingPlayer($$0);
            this.carriedByPlayers.put((Object)$$0, (Object)$$2);
            this.carriedBy.add((Object)$$2);
        }
        if (!$$0.getInventory().contains($$1)) {
            this.removeDecoration($$0.getName().getString());
        }
        for (int $$3 = 0; $$3 < this.carriedBy.size(); ++$$3) {
            HoldingPlayer $$4 = (HoldingPlayer)this.carriedBy.get($$3);
            String $$5 = $$4.player.getName().getString();
            if ($$4.player.isRemoved() || !$$4.player.getInventory().contains($$1) && !$$1.isFramed()) {
                this.carriedByPlayers.remove((Object)$$4.player);
                this.carriedBy.remove((Object)$$4);
                this.removeDecoration($$5);
                continue;
            }
            if ($$1.isFramed() || $$4.player.level.dimension() != this.dimension || !this.trackingPosition) continue;
            this.addDecoration(MapDecoration.Type.PLAYER, $$4.player.level, $$5, $$4.player.getX(), $$4.player.getZ(), $$4.player.getYRot(), null);
        }
        if ($$1.isFramed() && this.trackingPosition) {
            ItemFrame $$6 = $$1.getFrame();
            BlockPos $$7 = $$6.getPos();
            MapFrame $$8 = (MapFrame)this.frameMarkers.get((Object)MapFrame.frameId($$7));
            if ($$8 != null && $$6.getId() != $$8.getEntityId() && this.frameMarkers.containsKey((Object)$$8.getId())) {
                this.removeDecoration("frame-" + $$8.getEntityId());
            }
            MapFrame $$9 = new MapFrame($$7, $$6.getDirection().get2DDataValue() * 90, $$6.getId());
            this.addDecoration(MapDecoration.Type.FRAME, $$0.level, "frame-" + $$6.getId(), $$7.getX(), $$7.getZ(), $$6.getDirection().get2DDataValue() * 90, null);
            this.frameMarkers.put((Object)$$9.getId(), (Object)$$9);
        }
        if (($$10 = $$1.getTag()) != null && $$10.contains("Decorations", 9)) {
            ListTag $$11 = $$10.getList("Decorations", 10);
            for (int $$12 = 0; $$12 < $$11.size(); ++$$12) {
                CompoundTag $$13 = $$11.getCompound($$12);
                if (this.decorations.containsKey((Object)$$13.getString("id"))) continue;
                this.addDecoration(MapDecoration.Type.byIcon($$13.getByte("type")), $$0.level, $$13.getString("id"), $$13.getDouble("x"), $$13.getDouble("z"), $$13.getDouble("rot"), null);
            }
        }
    }

    private void removeDecoration(String $$0) {
        MapDecoration $$1 = (MapDecoration)this.decorations.remove((Object)$$0);
        if ($$1 != null && $$1.getType().shouldTrackCount()) {
            --this.trackedDecorationCount;
        }
        this.setDecorationsDirty();
    }

    public static void addTargetDecoration(ItemStack $$0, BlockPos $$1, String $$2, MapDecoration.Type $$3) {
        ListTag $$5;
        if ($$0.hasTag() && $$0.getTag().contains("Decorations", 9)) {
            ListTag $$4 = $$0.getTag().getList("Decorations", 10);
        } else {
            $$5 = new ListTag();
            $$0.addTagElement("Decorations", $$5);
        }
        CompoundTag $$6 = new CompoundTag();
        $$6.putByte("type", $$3.getIcon());
        $$6.putString("id", $$2);
        $$6.putDouble("x", $$1.getX());
        $$6.putDouble("z", $$1.getZ());
        $$6.putDouble("rot", 180.0);
        $$5.add($$6);
        if ($$3.hasMapColor()) {
            CompoundTag $$7 = $$0.getOrCreateTagElement("display");
            $$7.putInt("MapColor", $$3.getMapColor());
        }
    }

    /*
     * WARNING - void declaration
     */
    private void addDecoration(MapDecoration.Type $$0, @Nullable LevelAccessor $$1, String $$2, double $$3, double $$4, double $$5, @Nullable Component $$6) {
        MapDecoration $$19;
        void $$17;
        MapDecoration $$18;
        int $$7 = 1 << this.scale;
        float $$8 = (float)($$3 - (double)this.centerX) / (float)$$7;
        float $$9 = (float)($$4 - (double)this.centerZ) / (float)$$7;
        byte $$10 = (byte)((double)($$8 * 2.0f) + 0.5);
        byte $$11 = (byte)((double)($$9 * 2.0f) + 0.5);
        int $$12 = 63;
        if ($$8 >= -63.0f && $$9 >= -63.0f && $$8 <= 63.0f && $$9 <= 63.0f) {
            byte $$13 = (byte)(($$5 += $$5 < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == Level.NETHER && $$1 != null) {
                int $$14 = (int)($$1.getLevelData().getDayTime() / 10L);
                $$13 = (byte)($$14 * $$14 * 34187121 + $$14 * 121 >> 15 & 0xF);
            }
        } else if ($$0 == MapDecoration.Type.PLAYER) {
            int $$15 = 320;
            if (Math.abs((float)$$8) < 320.0f && Math.abs((float)$$9) < 320.0f) {
                $$0 = MapDecoration.Type.PLAYER_OFF_MAP;
            } else if (this.unlimitedTracking) {
                $$0 = MapDecoration.Type.PLAYER_OFF_LIMITS;
            } else {
                this.removeDecoration($$2);
                return;
            }
            boolean $$16 = false;
            if ($$8 <= -63.0f) {
                $$10 = -128;
            }
            if ($$9 <= -63.0f) {
                $$11 = -128;
            }
            if ($$8 >= 63.0f) {
                $$10 = 127;
            }
            if ($$9 >= 63.0f) {
                $$11 = 127;
            }
        } else {
            this.removeDecoration($$2);
            return;
        }
        if (!($$18 = new MapDecoration($$0, $$10, $$11, (byte)$$17, $$6)).equals($$19 = (MapDecoration)this.decorations.put((Object)$$2, (Object)$$18))) {
            if ($$19 != null && $$19.getType().shouldTrackCount()) {
                --this.trackedDecorationCount;
            }
            if ($$0.shouldTrackCount()) {
                ++this.trackedDecorationCount;
            }
            this.setDecorationsDirty();
        }
    }

    @Nullable
    public Packet<?> getUpdatePacket(int $$0, Player $$1) {
        HoldingPlayer $$2 = (HoldingPlayer)this.carriedByPlayers.get((Object)$$1);
        if ($$2 == null) {
            return null;
        }
        return $$2.nextUpdatePacket($$0);
    }

    private void setColorsDirty(int $$0, int $$1) {
        this.setDirty();
        for (HoldingPlayer $$2 : this.carriedBy) {
            $$2.markColorsDirty($$0, $$1);
        }
    }

    private void setDecorationsDirty() {
        this.setDirty();
        this.carriedBy.forEach(HoldingPlayer::markDecorationsDirty);
    }

    public HoldingPlayer getHoldingPlayer(Player $$0) {
        HoldingPlayer $$1 = (HoldingPlayer)this.carriedByPlayers.get((Object)$$0);
        if ($$1 == null) {
            $$1 = new HoldingPlayer($$0);
            this.carriedByPlayers.put((Object)$$0, (Object)$$1);
            this.carriedBy.add((Object)$$1);
        }
        return $$1;
    }

    public boolean toggleBanner(LevelAccessor $$0, BlockPos $$1) {
        double $$2 = (double)$$1.getX() + 0.5;
        double $$3 = (double)$$1.getZ() + 0.5;
        int $$4 = 1 << this.scale;
        double $$5 = ($$2 - (double)this.centerX) / (double)$$4;
        double $$6 = ($$3 - (double)this.centerZ) / (double)$$4;
        int $$7 = 63;
        if ($$5 >= -63.0 && $$6 >= -63.0 && $$5 <= 63.0 && $$6 <= 63.0) {
            MapBanner $$8 = MapBanner.fromWorld($$0, $$1);
            if ($$8 == null) {
                return false;
            }
            if (this.bannerMarkers.remove((Object)$$8.getId(), (Object)$$8)) {
                this.removeDecoration($$8.getId());
                return true;
            }
            if (!this.isTrackedCountOverLimit(256)) {
                this.bannerMarkers.put((Object)$$8.getId(), (Object)$$8);
                this.addDecoration($$8.getDecoration(), $$0, $$8.getId(), $$2, $$3, 180.0, $$8.getName());
                return true;
            }
        }
        return false;
    }

    public void checkBanners(BlockGetter $$0, int $$1, int $$2) {
        Iterator $$3 = this.bannerMarkers.values().iterator();
        while ($$3.hasNext()) {
            MapBanner $$5;
            MapBanner $$4 = (MapBanner)$$3.next();
            if ($$4.getPos().getX() != $$1 || $$4.getPos().getZ() != $$2 || $$4.equals($$5 = MapBanner.fromWorld($$0, $$4.getPos()))) continue;
            $$3.remove();
            this.removeDecoration($$4.getId());
        }
    }

    public Collection<MapBanner> getBanners() {
        return this.bannerMarkers.values();
    }

    public void removedFromFrame(BlockPos $$0, int $$1) {
        this.removeDecoration("frame-" + $$1);
        this.frameMarkers.remove((Object)MapFrame.frameId($$0));
    }

    public boolean updateColor(int $$0, int $$1, byte $$2) {
        byte $$3 = this.colors[$$0 + $$1 * 128];
        if ($$3 != $$2) {
            this.setColor($$0, $$1, $$2);
            return true;
        }
        return false;
    }

    public void setColor(int $$0, int $$1, byte $$2) {
        this.colors[$$0 + $$1 * 128] = $$2;
        this.setColorsDirty($$0, $$1);
    }

    public boolean isExplorationMap() {
        for (MapDecoration $$0 : this.decorations.values()) {
            if ($$0.getType() != MapDecoration.Type.MANSION && $$0.getType() != MapDecoration.Type.MONUMENT) continue;
            return true;
        }
        return false;
    }

    public void addClientSideDecorations(List<MapDecoration> $$0) {
        this.decorations.clear();
        this.trackedDecorationCount = 0;
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            MapDecoration $$2 = (MapDecoration)$$0.get($$1);
            this.decorations.put((Object)("icon-" + $$1), (Object)$$2);
            if (!$$2.getType().shouldTrackCount()) continue;
            ++this.trackedDecorationCount;
        }
    }

    public Iterable<MapDecoration> getDecorations() {
        return this.decorations.values();
    }

    public boolean isTrackedCountOverLimit(int $$0) {
        return this.trackedDecorationCount >= $$0;
    }

    public class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX = 127;
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        HoldingPlayer(Player $$1) {
            this.player = $$1;
        }

        private MapPatch createPatch() {
            int $$0 = this.minDirtyX;
            int $$1 = this.minDirtyY;
            int $$2 = this.maxDirtyX + 1 - this.minDirtyX;
            int $$3 = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] $$4 = new byte[$$2 * $$3];
            for (int $$5 = 0; $$5 < $$2; ++$$5) {
                for (int $$6 = 0; $$6 < $$3; ++$$6) {
                    $$4[$$5 + $$6 * $$2] = MapItemSavedData.this.colors[$$0 + $$5 + ($$1 + $$6) * 128];
                }
            }
            return new MapPatch($$0, $$1, $$2, $$3, $$4);
        }

        @Nullable
        Packet<?> nextUpdatePacket(int $$0) {
            Collection<MapDecoration> $$4;
            MapPatch $$2;
            if (this.dirtyData) {
                this.dirtyData = false;
                MapPatch $$1 = this.createPatch();
            } else {
                $$2 = null;
            }
            if (this.dirtyDecorations && this.tick++ % 5 == 0) {
                this.dirtyDecorations = false;
                Collection $$3 = MapItemSavedData.this.decorations.values();
            } else {
                $$4 = null;
            }
            if ($$4 != null || $$2 != null) {
                return new ClientboundMapItemDataPacket($$0, MapItemSavedData.this.scale, MapItemSavedData.this.locked, $$4, $$2);
            }
            return null;
        }

        void markColorsDirty(int $$0, int $$1) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min((int)this.minDirtyX, (int)$$0);
                this.minDirtyY = Math.min((int)this.minDirtyY, (int)$$1);
                this.maxDirtyX = Math.max((int)this.maxDirtyX, (int)$$0);
                this.maxDirtyY = Math.max((int)this.maxDirtyY, (int)$$1);
            } else {
                this.dirtyData = true;
                this.minDirtyX = $$0;
                this.minDirtyY = $$1;
                this.maxDirtyX = $$0;
                this.maxDirtyY = $$1;
            }
        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    public static class MapPatch {
        public final int startX;
        public final int startY;
        public final int width;
        public final int height;
        public final byte[] mapColors;

        public MapPatch(int $$0, int $$1, int $$2, int $$3, byte[] $$4) {
            this.startX = $$0;
            this.startY = $$1;
            this.width = $$2;
            this.height = $$3;
            this.mapColors = $$4;
        }

        public void applyToMap(MapItemSavedData $$0) {
            for (int $$1 = 0; $$1 < this.width; ++$$1) {
                for (int $$2 = 0; $$2 < this.height; ++$$2) {
                    $$0.setColor(this.startX + $$1, this.startY + $$2, this.mapColors[$$1 + $$2 * this.width]);
                }
            }
        }
    }
}