/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  java.lang.Boolean
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.ByteBuffer
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.BitSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix
extends DataFix {
    private static final int NORTH_WEST_MASK = 128;
    private static final int WEST_MASK = 64;
    private static final int SOUTH_WEST_MASK = 32;
    private static final int SOUTH_MASK = 16;
    private static final int SOUTH_EAST_MASK = 8;
    private static final int EAST_MASK = 4;
    private static final int NORTH_EAST_MASK = 2;
    private static final int NORTH_MASK = 1;
    static final Logger LOGGER = LogUtils.getLogger();
    static final BitSet VIRTUAL = new BitSet(256);
    static final BitSet FIX = new BitSet(256);
    static final Dynamic<?> PUMPKIN = BlockStateData.parse("{Name:'minecraft:pumpkin'}");
    static final Dynamic<?> SNOWY_PODZOL = BlockStateData.parse("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_GRASS = BlockStateData.parse("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_MYCELIUM = BlockStateData.parse("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    static final Dynamic<?> UPPER_SUNFLOWER = BlockStateData.parse("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_LILAC = BlockStateData.parse("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_TALL_GRASS = BlockStateData.parse("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_LARGE_FERN = BlockStateData.parse("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_ROSE_BUSH = BlockStateData.parse("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    static final Dynamic<?> UPPER_PEONY = BlockStateData.parse("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    static final Map<String, Dynamic<?>> FLOWER_POT_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"minecraft:air0", BlockStateData.parse("{Name:'minecraft:flower_pot'}"));
        $$0.put((Object)"minecraft:red_flower0", BlockStateData.parse("{Name:'minecraft:potted_poppy'}"));
        $$0.put((Object)"minecraft:red_flower1", BlockStateData.parse("{Name:'minecraft:potted_blue_orchid'}"));
        $$0.put((Object)"minecraft:red_flower2", BlockStateData.parse("{Name:'minecraft:potted_allium'}"));
        $$0.put((Object)"minecraft:red_flower3", BlockStateData.parse("{Name:'minecraft:potted_azure_bluet'}"));
        $$0.put((Object)"minecraft:red_flower4", BlockStateData.parse("{Name:'minecraft:potted_red_tulip'}"));
        $$0.put((Object)"minecraft:red_flower5", BlockStateData.parse("{Name:'minecraft:potted_orange_tulip'}"));
        $$0.put((Object)"minecraft:red_flower6", BlockStateData.parse("{Name:'minecraft:potted_white_tulip'}"));
        $$0.put((Object)"minecraft:red_flower7", BlockStateData.parse("{Name:'minecraft:potted_pink_tulip'}"));
        $$0.put((Object)"minecraft:red_flower8", BlockStateData.parse("{Name:'minecraft:potted_oxeye_daisy'}"));
        $$0.put((Object)"minecraft:yellow_flower0", BlockStateData.parse("{Name:'minecraft:potted_dandelion'}"));
        $$0.put((Object)"minecraft:sapling0", BlockStateData.parse("{Name:'minecraft:potted_oak_sapling'}"));
        $$0.put((Object)"minecraft:sapling1", BlockStateData.parse("{Name:'minecraft:potted_spruce_sapling'}"));
        $$0.put((Object)"minecraft:sapling2", BlockStateData.parse("{Name:'minecraft:potted_birch_sapling'}"));
        $$0.put((Object)"minecraft:sapling3", BlockStateData.parse("{Name:'minecraft:potted_jungle_sapling'}"));
        $$0.put((Object)"minecraft:sapling4", BlockStateData.parse("{Name:'minecraft:potted_acacia_sapling'}"));
        $$0.put((Object)"minecraft:sapling5", BlockStateData.parse("{Name:'minecraft:potted_dark_oak_sapling'}"));
        $$0.put((Object)"minecraft:red_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_red_mushroom'}"));
        $$0.put((Object)"minecraft:brown_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_brown_mushroom'}"));
        $$0.put((Object)"minecraft:deadbush0", BlockStateData.parse("{Name:'minecraft:potted_dead_bush'}"));
        $$0.put((Object)"minecraft:tallgrass2", BlockStateData.parse("{Name:'minecraft:potted_fern'}"));
        $$0.put((Object)"minecraft:cactus0", BlockStateData.getTag(2240));
    });
    static final Map<String, Dynamic<?>> SKULL_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        ChunkPalettedStorageFix.mapSkull($$0, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.mapSkull($$0, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.mapSkull($$0, 2, "zombie", "head");
        ChunkPalettedStorageFix.mapSkull($$0, 3, "player", "head");
        ChunkPalettedStorageFix.mapSkull($$0, 4, "creeper", "head");
        ChunkPalettedStorageFix.mapSkull($$0, 5, "dragon", "head");
    });
    static final Map<String, Dynamic<?>> DOOR_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        ChunkPalettedStorageFix.mapDoor($$0, "oak_door", 1024);
        ChunkPalettedStorageFix.mapDoor($$0, "iron_door", 1136);
        ChunkPalettedStorageFix.mapDoor($$0, "spruce_door", 3088);
        ChunkPalettedStorageFix.mapDoor($$0, "birch_door", 3104);
        ChunkPalettedStorageFix.mapDoor($$0, "jungle_door", 3120);
        ChunkPalettedStorageFix.mapDoor($$0, "acacia_door", 3136);
        ChunkPalettedStorageFix.mapDoor($$0, "dark_oak_door", 3152);
    });
    static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        for (int $$1 = 0; $$1 < 26; ++$$1) {
            $$0.put((Object)("true" + $$1), BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + $$1 + "'}}"));
            $$0.put((Object)("false" + $$1), BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + $$1 + "'}}"));
        }
    });
    private static final Int2ObjectMap<String> DYE_COLOR_MAP = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), $$0 -> {
        $$0.put(0, (Object)"white");
        $$0.put(1, (Object)"orange");
        $$0.put(2, (Object)"magenta");
        $$0.put(3, (Object)"light_blue");
        $$0.put(4, (Object)"yellow");
        $$0.put(5, (Object)"lime");
        $$0.put(6, (Object)"pink");
        $$0.put(7, (Object)"gray");
        $$0.put(8, (Object)"light_gray");
        $$0.put(9, (Object)"cyan");
        $$0.put(10, (Object)"purple");
        $$0.put(11, (Object)"blue");
        $$0.put(12, (Object)"brown");
        $$0.put(13, (Object)"green");
        $$0.put(14, (Object)"red");
        $$0.put(15, (Object)"black");
    });
    static final Map<String, Dynamic<?>> BED_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        for (Int2ObjectMap.Entry $$1 : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (Objects.equals((Object)$$1.getValue(), (Object)"red")) continue;
            ChunkPalettedStorageFix.addBeds($$0, $$1.getIntKey(), (String)$$1.getValue());
        }
    });
    static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        for (Int2ObjectMap.Entry $$1 : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (Objects.equals((Object)$$1.getValue(), (Object)"white")) continue;
            ChunkPalettedStorageFix.addBanners($$0, 15 - $$1.getIntKey(), (String)$$1.getValue());
        }
    });
    static final Dynamic<?> AIR;
    private static final int SIZE = 4096;

    public ChunkPalettedStorageFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    private static void mapSkull(Map<String, Dynamic<?>> $$0, int $$1, String $$2, String $$3) {
        $$0.put((Object)($$1 + "north"), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_" + $$3 + "',Properties:{facing:'north'}}"));
        $$0.put((Object)($$1 + "east"), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_" + $$3 + "',Properties:{facing:'east'}}"));
        $$0.put((Object)($$1 + "south"), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_" + $$3 + "',Properties:{facing:'south'}}"));
        $$0.put((Object)($$1 + "west"), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_" + $$3 + "',Properties:{facing:'west'}}"));
        for (int $$4 = 0; $$4 < 16; ++$$4) {
            $$0.put((Object)("" + $$1 + $$4), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_" + $$3 + "',Properties:{rotation:'" + $$4 + "'}}"));
        }
    }

    private static void mapDoor(Map<String, Dynamic<?>> $$0, String $$1, int $$2) {
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerrightfalsefalse"), BlockStateData.getTag($$2));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerrighttruefalse"), BlockStateData.getTag($$2 + 4));
        $$0.put((Object)("minecraft:" + $$1 + "eastlowerrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperleftfalsefalse"), BlockStateData.getTag($$2 + 8));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperleftfalsetrue"), BlockStateData.getTag($$2 + 10));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperrightfalsefalse"), BlockStateData.getTag($$2 + 9));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperrightfalsetrue"), BlockStateData.getTag($$2 + 11));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperrighttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "eastupperrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerrightfalsefalse"), BlockStateData.getTag($$2 + 3));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerrighttruefalse"), BlockStateData.getTag($$2 + 7));
        $$0.put((Object)("minecraft:" + $$1 + "northlowerrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperrightfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperrighttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "northupperrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerrightfalsefalse"), BlockStateData.getTag($$2 + 1));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerrighttruefalse"), BlockStateData.getTag($$2 + 5));
        $$0.put((Object)("minecraft:" + $$1 + "southlowerrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperrightfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperrighttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "southupperrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerrightfalsefalse"), BlockStateData.getTag($$2 + 2));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerrighttruefalse"), BlockStateData.getTag($$2 + 6));
        $$0.put((Object)("minecraft:" + $$1 + "westlowerrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperleftfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperleftfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperlefttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperlefttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperrightfalsefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperrightfalsetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperrighttruefalse"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        $$0.put((Object)("minecraft:" + $$1 + "westupperrighttruetrue"), BlockStateData.parse("{Name:'minecraft:" + $$1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }

    private static void addBeds(Map<String, Dynamic<?>> $$0, int $$1, String $$2) {
        $$0.put((Object)("southfalsefoot" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        $$0.put((Object)("westfalsefoot" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        $$0.put((Object)("northfalsefoot" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        $$0.put((Object)("eastfalsefoot" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        $$0.put((Object)("southfalsehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        $$0.put((Object)("westfalsehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        $$0.put((Object)("northfalsehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        $$0.put((Object)("eastfalsehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        $$0.put((Object)("southtruehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        $$0.put((Object)("westtruehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        $$0.put((Object)("northtruehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        $$0.put((Object)("easttruehead" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }

    private static void addBanners(Map<String, Dynamic<?>> $$0, int $$1, String $$2) {
        for (int $$3 = 0; $$3 < 16; ++$$3) {
            $$0.put((Object)($$3 + "_" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_banner',Properties:{rotation:'" + $$3 + "'}}"));
        }
        $$0.put((Object)("north_" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_banner',Properties:{facing:'north'}}"));
        $$0.put((Object)("south_" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_banner',Properties:{facing:'south'}}"));
        $$0.put((Object)("west_" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_banner',Properties:{facing:'west'}}"));
        $$0.put((Object)("east_" + $$1), BlockStateData.parse("{Name:'minecraft:" + $$2 + "_wall_banner',Properties:{facing:'east'}}"));
    }

    public static String getName(Dynamic<?> $$0) {
        return $$0.get("Name").asString("");
    }

    public static String getProperty(Dynamic<?> $$0, String $$1) {
        return $$0.get("Properties").get($$1).asString("");
    }

    public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> $$0, Dynamic<?> $$1) {
        int $$2 = $$0.getId($$1);
        if ($$2 == -1) {
            $$2 = $$0.add($$1);
        }
        return $$2;
    }

    private Dynamic<?> fix(Dynamic<?> $$0) {
        Optional $$1 = $$0.get("Level").result();
        if ($$1.isPresent() && ((Dynamic)$$1.get()).get("Sections").asStreamOpt().result().isPresent()) {
            return $$0.set("Level", new UpgradeChunk((Dynamic)$$1.get()).write());
        }
        return $$0;
    }

    public TypeRewriteRule makeRule() {
        Type $$0 = this.getInputSchema().getType(References.CHUNK);
        Type $$1 = this.getOutputSchema().getType(References.CHUNK);
        return this.writeFixAndRead("ChunkPalettedStorageFix", $$0, $$1, this::fix);
    }

    public static int getSideMask(boolean $$0, boolean $$1, boolean $$2, boolean $$3) {
        int $$4 = 0;
        if ($$2) {
            $$4 = $$1 ? ($$4 |= 2) : ($$0 ? ($$4 |= 0x80) : ($$4 |= 1));
        } else if ($$3) {
            $$4 = $$0 ? ($$4 |= 0x20) : ($$1 ? ($$4 |= 8) : ($$4 |= 0x10));
        } else if ($$1) {
            $$4 |= 4;
        } else if ($$0) {
            $$4 |= 0x40;
        }
        return $$4;
    }

    static {
        FIX.set(2);
        FIX.set(3);
        FIX.set(110);
        FIX.set(140);
        FIX.set(144);
        FIX.set(25);
        FIX.set(86);
        FIX.set(26);
        FIX.set(176);
        FIX.set(177);
        FIX.set(175);
        FIX.set(64);
        FIX.set(71);
        FIX.set(193);
        FIX.set(194);
        FIX.set(195);
        FIX.set(196);
        FIX.set(197);
        VIRTUAL.set(54);
        VIRTUAL.set(146);
        VIRTUAL.set(25);
        VIRTUAL.set(26);
        VIRTUAL.set(51);
        VIRTUAL.set(53);
        VIRTUAL.set(67);
        VIRTUAL.set(108);
        VIRTUAL.set(109);
        VIRTUAL.set(114);
        VIRTUAL.set(128);
        VIRTUAL.set(134);
        VIRTUAL.set(135);
        VIRTUAL.set(136);
        VIRTUAL.set(156);
        VIRTUAL.set(163);
        VIRTUAL.set(164);
        VIRTUAL.set(180);
        VIRTUAL.set(203);
        VIRTUAL.set(55);
        VIRTUAL.set(85);
        VIRTUAL.set(113);
        VIRTUAL.set(188);
        VIRTUAL.set(189);
        VIRTUAL.set(190);
        VIRTUAL.set(191);
        VIRTUAL.set(192);
        VIRTUAL.set(93);
        VIRTUAL.set(94);
        VIRTUAL.set(101);
        VIRTUAL.set(102);
        VIRTUAL.set(160);
        VIRTUAL.set(106);
        VIRTUAL.set(107);
        VIRTUAL.set(183);
        VIRTUAL.set(184);
        VIRTUAL.set(185);
        VIRTUAL.set(186);
        VIRTUAL.set(187);
        VIRTUAL.set(132);
        VIRTUAL.set(139);
        VIRTUAL.set(199);
        AIR = BlockStateData.getTag(0);
    }

    static final class UpgradeChunk {
        private int sides;
        private final Section[] sections = new Section[16];
        private final Dynamic<?> level;
        private final int x;
        private final int z;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public UpgradeChunk(Dynamic<?> $$0) {
            this.level = $$0;
            this.x = $$0.get("xPos").asInt(0) << 4;
            this.z = $$0.get("zPos").asInt(0) << 4;
            $$0.get("TileEntities").asStreamOpt().result().ifPresent($$02 -> $$02.forEach($$0 -> {
                int $$3;
                int $$1 = $$0.get("x").asInt(0) - this.x & 0xF;
                int $$2 = $$0.get("y").asInt(0);
                int $$4 = $$2 << 8 | ($$3 = $$0.get("z").asInt(0) - this.z & 0xF) << 4 | $$1;
                if (this.blockEntities.put($$4, $$0) != null) {
                    LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", new Object[]{this.x, this.z, $$1, $$2, $$3});
                }
            }));
            boolean $$1 = $$0.get("convertedFromAlphaFormat").asBoolean(false);
            $$0.get("Sections").asStreamOpt().result().ifPresent($$02 -> $$02.forEach($$0 -> {
                Section $$1 = new Section((Dynamic<?>)$$0);
                this.sides = $$1.upgrade(this.sides);
                this.sections[$$1.y] = $$1;
            }));
            for (Section $$2 : this.sections) {
                if ($$2 == null) continue;
                block14: for (Map.Entry $$3 : $$2.toFix.entrySet()) {
                    int $$4 = $$2.y << 12;
                    switch ((Integer)$$3.getKey()) {
                        case 2: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$7;
                                int $$5 = (Integer)intListIterator.next();
                                Dynamic<?> $$6 = this.getBlock($$5 |= $$4);
                                if (!"minecraft:grass_block".equals((Object)ChunkPalettedStorageFix.getName($$6)) || !"minecraft:snow".equals((Object)($$7 = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative($$5, Direction.UP))))) && !"minecraft:snow_layer".equals((Object)$$7)) continue;
                                this.setBlock($$5, SNOWY_GRASS);
                            }
                            continue block14;
                        }
                        case 3: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$10;
                                int $$8 = (Integer)intListIterator.next();
                                Dynamic<?> $$9 = this.getBlock($$8 |= $$4);
                                if (!"minecraft:podzol".equals((Object)ChunkPalettedStorageFix.getName($$9)) || !"minecraft:snow".equals((Object)($$10 = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative($$8, Direction.UP))))) && !"minecraft:snow_layer".equals((Object)$$10)) continue;
                                this.setBlock($$8, SNOWY_PODZOL);
                            }
                            continue block14;
                        }
                        case 110: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$13;
                                int $$11 = (Integer)intListIterator.next();
                                Dynamic<?> $$12 = this.getBlock($$11 |= $$4);
                                if (!"minecraft:mycelium".equals((Object)ChunkPalettedStorageFix.getName($$12)) || !"minecraft:snow".equals((Object)($$13 = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative($$11, Direction.UP))))) && !"minecraft:snow_layer".equals((Object)$$13)) continue;
                                this.setBlock($$11, SNOWY_MYCELIUM);
                            }
                            continue block14;
                        }
                        case 25: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int $$14 = (Integer)intListIterator.next();
                                Dynamic<?> $$15 = this.removeBlockEntity($$14 |= $$4);
                                if ($$15 == null) continue;
                                String $$16 = Boolean.toString((boolean)$$15.get("powered").asBoolean(false)) + (byte)Math.min((int)Math.max((int)$$15.get("note").asInt(0), (int)0), (int)24);
                                this.setBlock($$14, (Dynamic)NOTE_BLOCK_MAP.getOrDefault((Object)$$16, (Object)((Dynamic)NOTE_BLOCK_MAP.get((Object)"false0"))));
                            }
                            continue block14;
                        }
                        case 26: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$21;
                                int $$20;
                                int $$17 = (Integer)intListIterator.next();
                                Dynamic<?> $$18 = this.getBlockEntity($$17 |= $$4);
                                Dynamic<?> $$19 = this.getBlock($$17);
                                if ($$18 == null || ($$20 = $$18.get("color").asInt(0)) == 14 || $$20 < 0 || $$20 >= 16 || !BED_BLOCK_MAP.containsKey((Object)($$21 = ChunkPalettedStorageFix.getProperty($$19, "facing") + ChunkPalettedStorageFix.getProperty($$19, "occupied") + ChunkPalettedStorageFix.getProperty($$19, "part") + $$20))) continue;
                                this.setBlock($$17, (Dynamic)BED_BLOCK_MAP.get((Object)$$21));
                            }
                            continue block14;
                        }
                        case 176: 
                        case 177: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$26;
                                int $$25;
                                int $$22 = (Integer)intListIterator.next();
                                Dynamic<?> $$23 = this.getBlockEntity($$22 |= $$4);
                                Dynamic<?> $$24 = this.getBlock($$22);
                                if ($$23 == null || ($$25 = $$23.get("Base").asInt(0)) == 15 || $$25 < 0 || $$25 >= 16 || !BANNER_BLOCK_MAP.containsKey((Object)($$26 = ChunkPalettedStorageFix.getProperty($$24, (Integer)$$3.getKey() == 176 ? "rotation" : "facing") + "_" + $$25))) continue;
                                this.setBlock($$22, (Dynamic)BANNER_BLOCK_MAP.get((Object)$$26));
                            }
                            continue block14;
                        }
                        case 86: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$29;
                                int $$27 = (Integer)intListIterator.next();
                                Dynamic<?> $$28 = this.getBlock($$27 |= $$4);
                                if (!"minecraft:carved_pumpkin".equals((Object)ChunkPalettedStorageFix.getName($$28)) || !"minecraft:grass_block".equals((Object)($$29 = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative($$27, Direction.DOWN))))) && !"minecraft:dirt".equals((Object)$$29)) continue;
                                this.setBlock($$27, PUMPKIN);
                            }
                            continue block14;
                        }
                        case 140: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int $$30 = (Integer)intListIterator.next();
                                Dynamic<?> $$31 = this.removeBlockEntity($$30 |= $$4);
                                if ($$31 == null) continue;
                                String $$32 = $$31.get("Item").asString("") + $$31.get("Data").asInt(0);
                                this.setBlock($$30, (Dynamic)FLOWER_POT_MAP.getOrDefault((Object)$$32, (Object)((Dynamic)FLOWER_POT_MAP.get((Object)"minecraft:air0"))));
                            }
                            continue block14;
                        }
                        case 144: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String $$38;
                                int $$33 = (Integer)intListIterator.next();
                                Dynamic<?> $$34 = this.getBlockEntity($$33 |= $$4);
                                if ($$34 == null) continue;
                                String $$35 = String.valueOf((int)$$34.get("SkullType").asInt(0));
                                String $$36 = ChunkPalettedStorageFix.getProperty(this.getBlock($$33), "facing");
                                if ("up".equals((Object)$$36) || "down".equals((Object)$$36)) {
                                    String $$37 = $$35 + String.valueOf((int)$$34.get("Rot").asInt(0));
                                } else {
                                    $$38 = $$35 + $$36;
                                }
                                $$34.remove("SkullType");
                                $$34.remove("facing");
                                $$34.remove("Rot");
                                this.setBlock($$33, (Dynamic)SKULL_MAP.getOrDefault((Object)$$38, (Object)((Dynamic)SKULL_MAP.get((Object)"0north"))));
                            }
                            continue block14;
                        }
                        case 64: 
                        case 71: 
                        case 193: 
                        case 194: 
                        case 195: 
                        case 196: 
                        case 197: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                Dynamic<?> $$41;
                                int $$39 = (Integer)intListIterator.next();
                                Dynamic<?> $$40 = this.getBlock($$39 |= $$4);
                                if (!ChunkPalettedStorageFix.getName($$40).endsWith("_door") || !"lower".equals((Object)ChunkPalettedStorageFix.getProperty($$41 = this.getBlock($$39), "half"))) continue;
                                int $$42 = UpgradeChunk.relative($$39, Direction.UP);
                                Dynamic<?> $$43 = this.getBlock($$42);
                                String $$44 = ChunkPalettedStorageFix.getName($$41);
                                if (!$$44.equals((Object)ChunkPalettedStorageFix.getName($$43))) continue;
                                String $$45 = ChunkPalettedStorageFix.getProperty($$41, "facing");
                                String $$46 = ChunkPalettedStorageFix.getProperty($$41, "open");
                                String $$47 = $$1 ? "left" : ChunkPalettedStorageFix.getProperty($$43, "hinge");
                                String $$48 = $$1 ? "false" : ChunkPalettedStorageFix.getProperty($$43, "powered");
                                this.setBlock($$39, (Dynamic)DOOR_MAP.get((Object)($$44 + $$45 + "lower" + $$47 + $$46 + $$48)));
                                this.setBlock($$42, (Dynamic)DOOR_MAP.get((Object)($$44 + $$45 + "upper" + $$47 + $$46 + $$48)));
                            }
                            continue block14;
                        }
                        case 175: {
                            IntListIterator intListIterator = ((IntList)$$3.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int $$49 = (Integer)intListIterator.next();
                                Dynamic<?> $$50 = this.getBlock($$49 |= $$4);
                                if (!"upper".equals((Object)ChunkPalettedStorageFix.getProperty($$50, "half"))) continue;
                                Dynamic<?> $$51 = this.getBlock(UpgradeChunk.relative($$49, Direction.DOWN));
                                String $$52 = ChunkPalettedStorageFix.getName($$51);
                                if ("minecraft:sunflower".equals((Object)$$52)) {
                                    this.setBlock($$49, UPPER_SUNFLOWER);
                                    continue;
                                }
                                if ("minecraft:lilac".equals((Object)$$52)) {
                                    this.setBlock($$49, UPPER_LILAC);
                                    continue;
                                }
                                if ("minecraft:tall_grass".equals((Object)$$52)) {
                                    this.setBlock($$49, UPPER_TALL_GRASS);
                                    continue;
                                }
                                if ("minecraft:large_fern".equals((Object)$$52)) {
                                    this.setBlock($$49, UPPER_LARGE_FERN);
                                    continue;
                                }
                                if ("minecraft:rose_bush".equals((Object)$$52)) {
                                    this.setBlock($$49, UPPER_ROSE_BUSH);
                                    continue;
                                }
                                if (!"minecraft:peony".equals((Object)$$52)) continue;
                                this.setBlock($$49, UPPER_PEONY);
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Nullable
        private Dynamic<?> getBlockEntity(int $$0) {
            return (Dynamic)this.blockEntities.get($$0);
        }

        @Nullable
        private Dynamic<?> removeBlockEntity(int $$0) {
            return (Dynamic)this.blockEntities.remove($$0);
        }

        public static int relative(int $$0, Direction $$1) {
            switch ($$1.getAxis()) {
                case X: {
                    int $$2 = ($$0 & 0xF) + $$1.getAxisDirection().getStep();
                    return $$2 < 0 || $$2 > 15 ? -1 : $$0 & 0xFFFFFFF0 | $$2;
                }
                case Y: {
                    int $$3 = ($$0 >> 8) + $$1.getAxisDirection().getStep();
                    return $$3 < 0 || $$3 > 255 ? -1 : $$0 & 0xFF | $$3 << 8;
                }
                case Z: {
                    int $$4 = ($$0 >> 4 & 0xF) + $$1.getAxisDirection().getStep();
                    return $$4 < 0 || $$4 > 15 ? -1 : $$0 & 0xFFFFFF0F | $$4 << 4;
                }
            }
            return -1;
        }

        private void setBlock(int $$0, Dynamic<?> $$1) {
            if ($$0 < 0 || $$0 > 65535) {
                return;
            }
            Section $$2 = this.getSection($$0);
            if ($$2 == null) {
                return;
            }
            $$2.setBlock($$0 & 0xFFF, $$1);
        }

        @Nullable
        private Section getSection(int $$0) {
            int $$1 = $$0 >> 12;
            return $$1 < this.sections.length ? this.sections[$$1] : null;
        }

        public Dynamic<?> getBlock(int $$0) {
            if ($$0 < 0 || $$0 > 65535) {
                return AIR;
            }
            Section $$1 = this.getSection($$0);
            if ($$1 == null) {
                return AIR;
            }
            return $$1.getBlock($$0 & 0xFFF);
        }

        public Dynamic<?> write() {
            Dynamic $$0 = this.level;
            $$0 = this.blockEntities.isEmpty() ? $$0.remove("TileEntities") : $$0.set("TileEntities", $$0.createList(this.blockEntities.values().stream()));
            Dynamic $$1 = $$0.emptyMap();
            ArrayList $$2 = Lists.newArrayList();
            for (Section $$3 : this.sections) {
                if ($$3 == null) continue;
                $$2.add($$3.write());
                $$1 = $$1.set(String.valueOf((int)$$3.y), $$1.createIntList(Arrays.stream((int[])$$3.update.toIntArray())));
            }
            Dynamic $$4 = $$0.emptyMap();
            $$4 = $$4.set("Sides", $$4.createByte((byte)this.sides));
            $$4 = $$4.set("Indices", $$1);
            return $$0.set("UpgradeData", $$4).set("Sections", $$4.createList($$2.stream()));
        }
    }

    public static enum Direction {
        DOWN(AxisDirection.NEGATIVE, Axis.Y),
        UP(AxisDirection.POSITIVE, Axis.Y),
        NORTH(AxisDirection.NEGATIVE, Axis.Z),
        SOUTH(AxisDirection.POSITIVE, Axis.Z),
        WEST(AxisDirection.NEGATIVE, Axis.X),
        EAST(AxisDirection.POSITIVE, Axis.X);

        private final Axis axis;
        private final AxisDirection axisDirection;

        private Direction(AxisDirection $$0, Axis $$1) {
            this.axis = $$1;
            this.axisDirection = $$0;
        }

        public AxisDirection getAxisDirection() {
            return this.axisDirection;
        }

        public Axis getAxis() {
            return this.axis;
        }

        public static enum Axis {
            X,
            Y,
            Z;

        }

        public static enum AxisDirection {
            POSITIVE(1),
            NEGATIVE(-1);

            private final int step;

            private AxisDirection(int $$0) {
                this.step = $$0;
            }

            public int getStep() {
                return this.step;
            }
        }
    }

    static class DataLayer {
        private static final int SIZE = 2048;
        private static final int NIBBLE_SIZE = 4;
        private final byte[] data;

        public DataLayer() {
            this.data = new byte[2048];
        }

        public DataLayer(byte[] $$0) {
            this.data = $$0;
            if ($$0.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + $$0.length);
            }
        }

        public int get(int $$0, int $$1, int $$2) {
            int $$3 = this.getPosition($$1 << 8 | $$2 << 4 | $$0);
            if (this.isFirst($$1 << 8 | $$2 << 4 | $$0)) {
                return this.data[$$3] & 0xF;
            }
            return this.data[$$3] >> 4 & 0xF;
        }

        private boolean isFirst(int $$0) {
            return ($$0 & 1) == 0;
        }

        private int getPosition(int $$0) {
            return $$0 >> 1;
        }
    }

    static class Section {
        private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.create(32);
        private final List<Dynamic<?>> listTag;
        private final Dynamic<?> section;
        private final boolean hasData;
        final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap();
        final IntList update = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
        private final int[] buffer = new int[4096];

        public Section(Dynamic<?> $$0) {
            this.listTag = Lists.newArrayList();
            this.section = $$0;
            this.y = $$0.get("Y").asInt(0);
            this.hasData = $$0.get("Blocks").result().isPresent();
        }

        public Dynamic<?> getBlock(int $$0) {
            if ($$0 < 0 || $$0 > 4095) {
                return AIR;
            }
            Dynamic<?> $$1 = this.palette.byId(this.buffer[$$0]);
            return $$1 == null ? AIR : $$1;
        }

        public void setBlock(int $$0, Dynamic<?> $$1) {
            if (this.seen.add($$1)) {
                this.listTag.add("%%FILTER_ME%%".equals((Object)ChunkPalettedStorageFix.getName($$1)) ? AIR : $$1);
            }
            this.buffer[$$0] = ChunkPalettedStorageFix.idFor(this.palette, $$1);
        }

        public int upgrade(int $$02) {
            if (!this.hasData) {
                return $$02;
            }
            ByteBuffer $$1 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            DataLayer $$2 = (DataLayer)this.section.get("Data").asByteBufferOpt().map($$0 -> new DataLayer(DataFixUtils.toArray((ByteBuffer)$$0))).result().orElseGet(DataLayer::new);
            DataLayer $$3 = (DataLayer)this.section.get("Add").asByteBufferOpt().map($$0 -> new DataLayer(DataFixUtils.toArray((ByteBuffer)$$0))).result().orElseGet(DataLayer::new);
            this.seen.add(AIR);
            ChunkPalettedStorageFix.idFor(this.palette, AIR);
            this.listTag.add(AIR);
            for (int $$4 = 0; $$4 < 4096; ++$$4) {
                int $$5 = $$4 & 0xF;
                int $$6 = $$4 >> 8 & 0xF;
                int $$7 = $$4 >> 4 & 0xF;
                int $$8 = $$3.get($$5, $$6, $$7) << 12 | ($$1.get($$4) & 0xFF) << 4 | $$2.get($$5, $$6, $$7);
                if (FIX.get($$8 >> 4)) {
                    this.addFix($$8 >> 4, $$4);
                }
                if (VIRTUAL.get($$8 >> 4)) {
                    int $$9 = ChunkPalettedStorageFix.getSideMask($$5 == 0, $$5 == 15, $$7 == 0, $$7 == 15);
                    if ($$9 == 0) {
                        this.update.add($$4);
                    } else {
                        $$02 |= $$9;
                    }
                }
                this.setBlock($$4, BlockStateData.getTag($$8));
            }
            return $$02;
        }

        private void addFix(int $$0, int $$1) {
            IntList $$2 = (IntList)this.toFix.get($$0);
            if ($$2 == null) {
                $$2 = new IntArrayList();
                this.toFix.put($$0, (Object)$$2);
            }
            $$2.add($$1);
        }

        public Dynamic<?> write() {
            Dynamic $$0 = this.section;
            if (!this.hasData) {
                return $$0;
            }
            $$0 = $$0.set("Palette", $$0.createList(this.listTag.stream()));
            int $$1 = Math.max((int)4, (int)DataFixUtils.ceillog2((int)this.seen.size()));
            PackedBitStorage $$2 = new PackedBitStorage($$1, 4096);
            for (int $$3 = 0; $$3 < this.buffer.length; ++$$3) {
                $$2.set($$3, this.buffer[$$3]);
            }
            $$0 = $$0.set("BlockStates", $$0.createLongList(Arrays.stream((long[])$$2.getRaw())));
            $$0 = $$0.remove("Blocks");
            $$0 = $$0.remove("Data");
            $$0 = $$0.remove("Add");
            return $$0;
        }
    }
}