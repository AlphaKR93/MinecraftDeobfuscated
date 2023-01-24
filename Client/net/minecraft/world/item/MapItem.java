/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.LinkedHashMultiset
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 */
package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem
extends ComplexItem {
    public static final int IMAGE_WIDTH = 128;
    public static final int IMAGE_HEIGHT = 128;
    private static final int DEFAULT_MAP_COLOR = -12173266;
    private static final String TAG_MAP = "map";
    public static final String MAP_SCALE_TAG = "map_scale_direction";
    public static final String MAP_LOCK_TAG = "map_to_lock";

    public MapItem(Item.Properties $$0) {
        super($$0);
    }

    public static ItemStack create(Level $$0, int $$1, int $$2, byte $$3, boolean $$4, boolean $$5) {
        ItemStack $$6 = new ItemStack(Items.FILLED_MAP);
        MapItem.createAndStoreSavedData($$6, $$0, $$1, $$2, $$3, $$4, $$5, $$0.dimension());
        return $$6;
    }

    @Nullable
    public static MapItemSavedData getSavedData(@Nullable Integer $$0, Level $$1) {
        return $$0 == null ? null : $$1.getMapData(MapItem.makeKey($$0));
    }

    @Nullable
    public static MapItemSavedData getSavedData(ItemStack $$0, Level $$1) {
        Integer $$2 = MapItem.getMapId($$0);
        return MapItem.getSavedData($$2, $$1);
    }

    @Nullable
    public static Integer getMapId(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        return $$1 != null && $$1.contains(TAG_MAP, 99) ? Integer.valueOf((int)$$1.getInt(TAG_MAP)) : null;
    }

    private static int createNewSavedData(Level $$0, int $$1, int $$2, int $$3, boolean $$4, boolean $$5, ResourceKey<Level> $$6) {
        MapItemSavedData $$7 = MapItemSavedData.createFresh($$1, $$2, (byte)$$3, $$4, $$5, $$6);
        int $$8 = $$0.getFreeMapId();
        $$0.setMapData(MapItem.makeKey($$8), $$7);
        return $$8;
    }

    private static void storeMapData(ItemStack $$0, int $$1) {
        $$0.getOrCreateTag().putInt(TAG_MAP, $$1);
    }

    private static void createAndStoreSavedData(ItemStack $$0, Level $$1, int $$2, int $$3, int $$4, boolean $$5, boolean $$6, ResourceKey<Level> $$7) {
        int $$8 = MapItem.createNewSavedData($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        MapItem.storeMapData($$0, $$8);
    }

    public static String makeKey(int $$0) {
        return "map_" + $$0;
    }

    public void update(Level $$0, Entity $$1, MapItemSavedData $$2) {
        if ($$0.dimension() != $$2.dimension || !($$1 instanceof Player)) {
            return;
        }
        int $$3 = 1 << $$2.scale;
        int $$4 = $$2.centerX;
        int $$5 = $$2.centerZ;
        int $$6 = Mth.floor($$1.getX() - (double)$$4) / $$3 + 64;
        int $$7 = Mth.floor($$1.getZ() - (double)$$5) / $$3 + 64;
        int $$8 = 128 / $$3;
        if ($$0.dimensionType().hasCeiling()) {
            $$8 /= 2;
        }
        MapItemSavedData.HoldingPlayer $$9 = $$2.getHoldingPlayer((Player)$$1);
        ++$$9.step;
        BlockPos.MutableBlockPos $$10 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$11 = new BlockPos.MutableBlockPos();
        boolean $$12 = false;
        for (int $$13 = $$6 - $$8 + 1; $$13 < $$6 + $$8; ++$$13) {
            if (($$13 & 0xF) != ($$9.step & 0xF) && !$$12) continue;
            $$12 = false;
            double $$14 = 0.0;
            for (int $$15 = $$7 - $$8 - 1; $$15 < $$7 + $$8; ++$$15) {
                MaterialColor.Brightness $$40;
                if ($$13 < 0 || $$15 < -1 || $$13 >= 128 || $$15 >= 128) continue;
                int $$16 = Mth.square($$13 - $$6) + Mth.square($$15 - $$7);
                boolean $$17 = $$16 > ($$8 - 2) * ($$8 - 2);
                int $$18 = ($$4 / $$3 + $$13 - 64) * $$3;
                int $$19 = ($$5 / $$3 + $$15 - 64) * $$3;
                LinkedHashMultiset $$20 = LinkedHashMultiset.create();
                LevelChunk $$21 = $$0.getChunk(SectionPos.blockToSectionCoord($$18), SectionPos.blockToSectionCoord($$19));
                if ($$21.isEmpty()) continue;
                int $$22 = 0;
                double $$23 = 0.0;
                if ($$0.dimensionType().hasCeiling()) {
                    int $$24 = $$18 + $$19 * 231871;
                    if ((($$24 = $$24 * $$24 * 31287121 + $$24 * 11) >> 20 & 1) == 0) {
                        $$20.add((Object)Blocks.DIRT.defaultBlockState().getMapColor($$0, BlockPos.ZERO), 10);
                    } else {
                        $$20.add((Object)Blocks.STONE.defaultBlockState().getMapColor($$0, BlockPos.ZERO), 100);
                    }
                    $$23 = 100.0;
                } else {
                    for (int $$25 = 0; $$25 < $$3; ++$$25) {
                        for (int $$26 = 0; $$26 < $$3; ++$$26) {
                            BlockState $$31;
                            $$10.set($$18 + $$25, 0, $$19 + $$26);
                            int $$27 = $$21.getHeight(Heightmap.Types.WORLD_SURFACE, $$10.getX(), $$10.getZ()) + 1;
                            if ($$27 > $$0.getMinBuildHeight() + 1) {
                                BlockState $$28;
                                do {
                                    $$10.setY(--$$27);
                                } while (($$28 = $$21.getBlockState($$10)).getMapColor($$0, $$10) == MaterialColor.NONE && $$27 > $$0.getMinBuildHeight());
                                if ($$27 > $$0.getMinBuildHeight() && !$$28.getFluidState().isEmpty()) {
                                    BlockState $$30;
                                    int $$29 = $$27 - 1;
                                    $$11.set($$10);
                                    do {
                                        $$11.setY($$29--);
                                        $$30 = $$21.getBlockState($$11);
                                        ++$$22;
                                    } while ($$29 > $$0.getMinBuildHeight() && !$$30.getFluidState().isEmpty());
                                    $$28 = this.getCorrectStateForFluidBlock($$0, $$28, $$10);
                                }
                            } else {
                                $$31 = Blocks.BEDROCK.defaultBlockState();
                            }
                            $$2.checkBanners($$0, $$10.getX(), $$10.getZ());
                            $$23 += (double)$$27 / (double)($$3 * $$3);
                            $$20.add((Object)$$31.getMapColor($$0, $$10));
                        }
                    }
                }
                $$22 /= $$3 * $$3;
                MaterialColor $$32 = (MaterialColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)$$20), (Object)MaterialColor.NONE);
                if ($$32 == MaterialColor.WATER) {
                    double $$33 = (double)$$22 * 0.1 + (double)($$13 + $$15 & 1) * 0.2;
                    if ($$33 < 0.5) {
                        MaterialColor.Brightness $$34 = MaterialColor.Brightness.HIGH;
                    } else if ($$33 > 0.9) {
                        MaterialColor.Brightness $$35 = MaterialColor.Brightness.LOW;
                    } else {
                        MaterialColor.Brightness $$36 = MaterialColor.Brightness.NORMAL;
                    }
                } else {
                    double $$37 = ($$23 - $$14) * 4.0 / (double)($$3 + 4) + ((double)($$13 + $$15 & 1) - 0.5) * 0.4;
                    if ($$37 > 0.6) {
                        MaterialColor.Brightness $$38 = MaterialColor.Brightness.HIGH;
                    } else if ($$37 < -0.6) {
                        MaterialColor.Brightness $$39 = MaterialColor.Brightness.LOW;
                    } else {
                        $$40 = MaterialColor.Brightness.NORMAL;
                    }
                }
                $$14 = $$23;
                if ($$15 < 0 || $$16 >= $$8 * $$8 || $$17 && ($$13 + $$15 & 1) == 0) continue;
                $$12 |= $$2.updateColor($$13, $$15, $$32.getPackedId($$40));
            }
        }
    }

    private BlockState getCorrectStateForFluidBlock(Level $$0, BlockState $$1, BlockPos $$2) {
        FluidState $$3 = $$1.getFluidState();
        if (!$$3.isEmpty() && !$$1.isFaceSturdy($$0, $$2, Direction.UP)) {
            return $$3.createLegacyBlock();
        }
        return $$1;
    }

    private static boolean isBiomeWatery(boolean[] $$0, int $$1, int $$2) {
        return $$0[$$2 * 128 + $$1];
    }

    public static void renderBiomePreviewMap(ServerLevel $$0, ItemStack $$1) {
        MapItemSavedData $$2 = MapItem.getSavedData($$1, (Level)$$0);
        if ($$2 == null) {
            return;
        }
        if ($$0.dimension() != $$2.dimension) {
            return;
        }
        int $$3 = 1 << $$2.scale;
        int $$4 = $$2.centerX;
        int $$5 = $$2.centerZ;
        boolean[] $$6 = new boolean[16384];
        int $$7 = $$4 / $$3 - 64;
        int $$8 = $$5 / $$3 - 64;
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        for (int $$10 = 0; $$10 < 128; ++$$10) {
            for (int $$11 = 0; $$11 < 128; ++$$11) {
                Holder $$12 = $$0.getBiome($$9.set(($$7 + $$11) * $$3, 0, ($$8 + $$10) * $$3));
                $$6[$$10 * 128 + $$11] = $$12.is(BiomeTags.WATER_ON_MAP_OUTLINES);
            }
        }
        for (int $$13 = 1; $$13 < 127; ++$$13) {
            for (int $$14 = 1; $$14 < 127; ++$$14) {
                int $$15 = 0;
                for (int $$16 = -1; $$16 < 2; ++$$16) {
                    for (int $$17 = -1; $$17 < 2; ++$$17) {
                        if ($$16 == 0 && $$17 == 0 || !MapItem.isBiomeWatery($$6, $$13 + $$16, $$14 + $$17)) continue;
                        ++$$15;
                    }
                }
                MaterialColor.Brightness $$18 = MaterialColor.Brightness.LOWEST;
                MaterialColor $$19 = MaterialColor.NONE;
                if (MapItem.isBiomeWatery($$6, $$13, $$14)) {
                    $$19 = MaterialColor.COLOR_ORANGE;
                    if ($$15 > 7 && $$14 % 2 == 0) {
                        switch (($$13 + (int)(Mth.sin((float)$$14 + 0.0f) * 7.0f)) / 8 % 5) {
                            case 0: 
                            case 4: {
                                $$18 = MaterialColor.Brightness.LOW;
                                break;
                            }
                            case 1: 
                            case 3: {
                                $$18 = MaterialColor.Brightness.NORMAL;
                                break;
                            }
                            case 2: {
                                $$18 = MaterialColor.Brightness.HIGH;
                            }
                        }
                    } else if ($$15 > 7) {
                        $$19 = MaterialColor.NONE;
                    } else if ($$15 > 5) {
                        $$18 = MaterialColor.Brightness.NORMAL;
                    } else if ($$15 > 3) {
                        $$18 = MaterialColor.Brightness.LOW;
                    } else if ($$15 > 1) {
                        $$18 = MaterialColor.Brightness.LOW;
                    }
                } else if ($$15 > 0) {
                    $$19 = MaterialColor.COLOR_BROWN;
                    $$18 = $$15 > 3 ? MaterialColor.Brightness.NORMAL : MaterialColor.Brightness.LOWEST;
                }
                if ($$19 == MaterialColor.NONE) continue;
                $$2.setColor($$13, $$14, $$19.getPackedId($$18));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack $$0, Level $$1, Entity $$2, int $$3, boolean $$4) {
        if ($$1.isClientSide) {
            return;
        }
        MapItemSavedData $$5 = MapItem.getSavedData($$0, $$1);
        if ($$5 == null) {
            return;
        }
        if ($$2 instanceof Player) {
            Player $$6 = (Player)$$2;
            $$5.tickCarriedBy($$6, $$0);
        }
        if (!$$5.locked && ($$4 || $$2 instanceof Player && ((Player)$$2).getOffhandItem() == $$0)) {
            this.update($$1, $$2, $$5);
        }
    }

    @Override
    @Nullable
    public Packet<?> getUpdatePacket(ItemStack $$0, Level $$1, Player $$2) {
        Integer $$3 = MapItem.getMapId($$0);
        MapItemSavedData $$4 = MapItem.getSavedData($$3, $$1);
        if ($$4 != null) {
            return $$4.getUpdatePacket($$3, $$2);
        }
        return null;
    }

    @Override
    public void onCraftedBy(ItemStack $$0, Level $$1, Player $$2) {
        CompoundTag $$3 = $$0.getTag();
        if ($$3 != null && $$3.contains(MAP_SCALE_TAG, 99)) {
            MapItem.scaleMap($$0, $$1, $$3.getInt(MAP_SCALE_TAG));
            $$3.remove(MAP_SCALE_TAG);
        } else if ($$3 != null && $$3.contains(MAP_LOCK_TAG, 1) && $$3.getBoolean(MAP_LOCK_TAG)) {
            MapItem.lockMap($$1, $$0);
            $$3.remove(MAP_LOCK_TAG);
        }
    }

    private static void scaleMap(ItemStack $$0, Level $$1, int $$2) {
        MapItemSavedData $$3 = MapItem.getSavedData($$0, $$1);
        if ($$3 != null) {
            int $$4 = $$1.getFreeMapId();
            $$1.setMapData(MapItem.makeKey($$4), $$3.scaled($$2));
            MapItem.storeMapData($$0, $$4);
        }
    }

    public static void lockMap(Level $$0, ItemStack $$1) {
        MapItemSavedData $$2 = MapItem.getSavedData($$1, $$0);
        if ($$2 != null) {
            int $$3 = $$0.getFreeMapId();
            String $$4 = MapItem.makeKey($$3);
            MapItemSavedData $$5 = $$2.locked();
            $$0.setMapData($$4, $$5);
            MapItem.storeMapData($$1, $$3);
        }
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        byte $$10;
        boolean $$9;
        Integer $$4 = MapItem.getMapId($$0);
        MapItemSavedData $$5 = $$1 == null ? null : MapItem.getSavedData($$4, $$1);
        CompoundTag $$6 = $$0.getTag();
        if ($$6 != null) {
            boolean $$7 = $$6.getBoolean(MAP_LOCK_TAG);
            byte $$8 = $$6.getByte(MAP_SCALE_TAG);
        } else {
            $$9 = false;
            $$10 = 0;
        }
        if ($$5 != null && ($$5.locked || $$9)) {
            $$2.add((Object)Component.translatable("filled_map.locked", $$4).withStyle(ChatFormatting.GRAY));
        }
        if ($$3.isAdvanced()) {
            if ($$5 != null) {
                if (!$$9 && $$10 == 0) {
                    $$2.add((Object)Component.translatable("filled_map.id", $$4).withStyle(ChatFormatting.GRAY));
                }
                int $$11 = Math.min((int)($$5.scale + $$10), (int)4);
                $$2.add((Object)Component.translatable("filled_map.scale", 1 << $$11).withStyle(ChatFormatting.GRAY));
                $$2.add((Object)Component.translatable("filled_map.level", $$11, 4).withStyle(ChatFormatting.GRAY));
            } else {
                $$2.add((Object)Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static int getColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTagElement("display");
        if ($$1 != null && $$1.contains("MapColor", 99)) {
            int $$2 = $$1.getInt("MapColor");
            return 0xFF000000 | $$2 & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(BlockTags.BANNERS)) {
            MapItemSavedData $$2;
            if (!$$0.getLevel().isClientSide && ($$2 = MapItem.getSavedData($$0.getItemInHand(), $$0.getLevel())) != null && !$$2.toggleBanner($$0.getLevel(), $$0.getClickedPos())) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.sidedSuccess($$0.getLevel().isClientSide);
        }
        return super.useOn($$0);
    }
}