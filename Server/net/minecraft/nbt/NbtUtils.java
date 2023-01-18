/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.TagTypes;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public final class NbtUtils {
    private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt($$0 -> $$0.getInt(1)).thenComparingInt($$0 -> $$0.getInt(0)).thenComparingInt($$0 -> $$0.getInt(2));
    private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble($$0 -> $$0.getDouble(1)).thenComparingDouble($$0 -> $$0.getDouble(0)).thenComparingDouble($$0 -> $$0.getDouble(2));
    public static final String SNBT_DATA_TAG = "data";
    private static final char PROPERTIES_START = '{';
    private static final char PROPERTIES_END = '}';
    private static final String ELEMENT_SEPARATOR = ",";
    private static final char KEY_VALUE_SEPARATOR = ':';
    private static final Splitter COMMA_SPLITTER = Splitter.on((String)",");
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INDENT = 2;
    private static final int NOT_FOUND = -1;

    private NbtUtils() {
    }

    @Nullable
    public static GameProfile readGameProfile(CompoundTag $$0) {
        String $$1 = null;
        UUID $$2 = null;
        if ($$0.contains("Name", 8)) {
            $$1 = $$0.getString("Name");
        }
        if ($$0.hasUUID("Id")) {
            $$2 = $$0.getUUID("Id");
        }
        try {
            GameProfile $$3 = new GameProfile($$2, $$1);
            if ($$0.contains("Properties", 10)) {
                CompoundTag $$4 = $$0.getCompound("Properties");
                for (String $$5 : $$4.getAllKeys()) {
                    ListTag $$6 = $$4.getList($$5, 10);
                    for (int $$7 = 0; $$7 < $$6.size(); ++$$7) {
                        CompoundTag $$8 = $$6.getCompound($$7);
                        String $$9 = $$8.getString("Value");
                        if ($$8.contains("Signature", 8)) {
                            $$3.getProperties().put((Object)$$5, (Object)new Property($$5, $$9, $$8.getString("Signature")));
                            continue;
                        }
                        $$3.getProperties().put((Object)$$5, (Object)new Property($$5, $$9));
                    }
                }
            }
            return $$3;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static CompoundTag writeGameProfile(CompoundTag $$0, GameProfile $$1) {
        if (!StringUtil.isNullOrEmpty($$1.getName())) {
            $$0.putString("Name", $$1.getName());
        }
        if ($$1.getId() != null) {
            $$0.putUUID("Id", $$1.getId());
        }
        if (!$$1.getProperties().isEmpty()) {
            CompoundTag $$2 = new CompoundTag();
            for (String $$3 : $$1.getProperties().keySet()) {
                ListTag $$4 = new ListTag();
                for (Property $$5 : $$1.getProperties().get((Object)$$3)) {
                    CompoundTag $$6 = new CompoundTag();
                    $$6.putString("Value", $$5.getValue());
                    if ($$5.hasSignature()) {
                        $$6.putString("Signature", $$5.getSignature());
                    }
                    $$4.add($$6);
                }
                $$2.put($$3, $$4);
            }
            $$0.put("Properties", $$2);
        }
        return $$0;
    }

    @VisibleForTesting
    public static boolean compareNbt(@Nullable Tag $$0, @Nullable Tag $$1, boolean $$2) {
        if ($$0 == $$1) {
            return true;
        }
        if ($$0 == null) {
            return true;
        }
        if ($$1 == null) {
            return false;
        }
        if (!$$0.getClass().equals((Object)$$1.getClass())) {
            return false;
        }
        if ($$0 instanceof CompoundTag) {
            CompoundTag $$3 = (CompoundTag)$$0;
            CompoundTag $$4 = (CompoundTag)$$1;
            for (String $$5 : $$3.getAllKeys()) {
                Tag $$6 = $$3.get($$5);
                if (NbtUtils.compareNbt($$6, $$4.get($$5), $$2)) continue;
                return false;
            }
            return true;
        }
        if ($$0 instanceof ListTag && $$2) {
            ListTag $$7 = (ListTag)$$0;
            ListTag $$8 = (ListTag)$$1;
            if ($$7.isEmpty()) {
                return $$8.isEmpty();
            }
            for (int $$9 = 0; $$9 < $$7.size(); ++$$9) {
                Tag $$10 = $$7.get($$9);
                boolean $$11 = false;
                for (int $$12 = 0; $$12 < $$8.size(); ++$$12) {
                    if (!NbtUtils.compareNbt($$10, $$8.get($$12), $$2)) continue;
                    $$11 = true;
                    break;
                }
                if ($$11) continue;
                return false;
            }
            return true;
        }
        return $$0.equals($$1);
    }

    public static IntArrayTag createUUID(UUID $$0) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray($$0));
    }

    public static UUID loadUUID(Tag $$0) {
        if ($$0.getType() != IntArrayTag.TYPE) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayTag.TYPE.getName() + ", but found " + $$0.getType().getName() + ".");
        }
        int[] $$1 = ((IntArrayTag)$$0).getAsIntArray();
        if ($$1.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + $$1.length + ".");
        }
        return UUIDUtil.uuidFromIntArray($$1);
    }

    public static BlockPos readBlockPos(CompoundTag $$0) {
        return new BlockPos($$0.getInt("X"), $$0.getInt("Y"), $$0.getInt("Z"));
    }

    public static CompoundTag writeBlockPos(BlockPos $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putInt("X", $$0.getX());
        $$1.putInt("Y", $$0.getY());
        $$1.putInt("Z", $$0.getZ());
        return $$1;
    }

    public static BlockState readBlockState(HolderGetter<Block> $$0, CompoundTag $$1) {
        if (!$$1.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        }
        ResourceLocation $$2 = new ResourceLocation($$1.getString("Name"));
        Optional<Holder.Reference<Block>> $$3 = $$0.get(ResourceKey.create(Registries.BLOCK, $$2));
        if ($$3.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }
        Block $$4 = (Block)((Holder)$$3.get()).value();
        BlockState $$5 = $$4.defaultBlockState();
        if ($$1.contains("Properties", 10)) {
            CompoundTag $$6 = $$1.getCompound("Properties");
            StateDefinition<Block, BlockState> $$7 = $$4.getStateDefinition();
            for (String $$8 : $$6.getAllKeys()) {
                net.minecraft.world.level.block.state.properties.Property<?> $$9 = $$7.getProperty($$8);
                if ($$9 == null) continue;
                $$5 = NbtUtils.setValueHelper($$5, $$9, $$8, $$6, $$1);
            }
        }
        return $$5;
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S $$0, net.minecraft.world.level.block.state.properties.Property<T> $$1, String $$2, CompoundTag $$3, CompoundTag $$4) {
        Optional<T> $$5 = $$1.getValue($$3.getString($$2));
        if ($$5.isPresent()) {
            return (S)((StateHolder)$$0.setValue($$1, (Comparable)((Comparable)$$5.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{$$2, $$3.getString($$2), $$4.toString()});
        return $$0;
    }

    public static CompoundTag writeBlockState(BlockState $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString("Name", BuiltInRegistries.BLOCK.getKey($$0.getBlock()).toString());
        ImmutableMap<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> $$2 = $$0.getValues();
        if (!$$2.isEmpty()) {
            CompoundTag $$3 = new CompoundTag();
            for (Map.Entry $$4 : $$2.entrySet()) {
                net.minecraft.world.level.block.state.properties.Property $$5 = (net.minecraft.world.level.block.state.properties.Property)$$4.getKey();
                $$3.putString($$5.getName(), NbtUtils.getName($$5, (Comparable)$$4.getValue()));
            }
            $$1.put("Properties", $$3);
        }
        return $$1;
    }

    public static CompoundTag writeFluidState(FluidState $$0) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString("Name", BuiltInRegistries.FLUID.getKey($$0.getType()).toString());
        ImmutableMap<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> $$2 = $$0.getValues();
        if (!$$2.isEmpty()) {
            CompoundTag $$3 = new CompoundTag();
            for (Map.Entry $$4 : $$2.entrySet()) {
                net.minecraft.world.level.block.state.properties.Property $$5 = (net.minecraft.world.level.block.state.properties.Property)$$4.getKey();
                $$3.putString($$5.getName(), NbtUtils.getName($$5, (Comparable)$$4.getValue()));
            }
            $$1.put("Properties", $$3);
        }
        return $$1;
    }

    private static <T extends Comparable<T>> String getName(net.minecraft.world.level.block.state.properties.Property<T> $$0, Comparable<?> $$1) {
        return $$0.getName($$1);
    }

    public static String prettyPrint(Tag $$0) {
        return NbtUtils.prettyPrint($$0, false);
    }

    public static String prettyPrint(Tag $$0, boolean $$1) {
        return NbtUtils.prettyPrint(new StringBuilder(), $$0, 0, $$1).toString();
    }

    public static StringBuilder prettyPrint(StringBuilder $$0, Tag $$1, int $$2, boolean $$3) {
        switch ($$1.getId()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 8: {
                $$0.append((Object)$$1);
                break;
            }
            case 0: {
                break;
            }
            case 7: {
                ByteArrayTag $$4 = (ByteArrayTag)$$1;
                byte[] $$5 = $$4.getAsByteArray();
                int $$6 = $$5.length;
                NbtUtils.indent($$2, $$0).append("byte[").append($$6).append("] {\n");
                if ($$3) {
                    NbtUtils.indent($$2 + 1, $$0);
                    for (int $$7 = 0; $$7 < $$5.length; ++$$7) {
                        if ($$7 != 0) {
                            $$0.append(',');
                        }
                        if ($$7 % 16 == 0 && $$7 / 16 > 0) {
                            $$0.append('\n');
                            if ($$7 < $$5.length) {
                                NbtUtils.indent($$2 + 1, $$0);
                            }
                        } else if ($$7 != 0) {
                            $$0.append(' ');
                        }
                        $$0.append(String.format((Locale)Locale.ROOT, (String)"0x%02X", (Object[])new Object[]{$$5[$$7] & 0xFF}));
                    }
                } else {
                    NbtUtils.indent($$2 + 1, $$0).append(" // Skipped, supply withBinaryBlobs true");
                }
                $$0.append('\n');
                NbtUtils.indent($$2, $$0).append('}');
                break;
            }
            case 9: {
                ListTag $$8 = (ListTag)$$1;
                int $$9 = $$8.size();
                byte $$10 = $$8.getElementType();
                String $$11 = $$10 == 0 ? "undefined" : TagTypes.getType($$10).getPrettyName();
                NbtUtils.indent($$2, $$0).append("list<").append($$11).append(">[").append($$9).append("] [");
                if ($$9 != 0) {
                    $$0.append('\n');
                }
                for (int $$12 = 0; $$12 < $$9; ++$$12) {
                    if ($$12 != 0) {
                        $$0.append(",\n");
                    }
                    NbtUtils.indent($$2 + 1, $$0);
                    NbtUtils.prettyPrint($$0, $$8.get($$12), $$2 + 1, $$3);
                }
                if ($$9 != 0) {
                    $$0.append('\n');
                }
                NbtUtils.indent($$2, $$0).append(']');
                break;
            }
            case 11: {
                IntArrayTag $$13 = (IntArrayTag)$$1;
                int[] $$14 = $$13.getAsIntArray();
                int $$15 = 0;
                for (int $$16 : $$14) {
                    $$15 = Math.max((int)$$15, (int)String.format((Locale)Locale.ROOT, (String)"%X", (Object[])new Object[]{$$16}).length());
                }
                int $$17 = $$14.length;
                NbtUtils.indent($$2, $$0).append("int[").append($$17).append("] {\n");
                if ($$3) {
                    NbtUtils.indent($$2 + 1, $$0);
                    for (int $$18 = 0; $$18 < $$14.length; ++$$18) {
                        if ($$18 != 0) {
                            $$0.append(',');
                        }
                        if ($$18 % 16 == 0 && $$18 / 16 > 0) {
                            $$0.append('\n');
                            if ($$18 < $$14.length) {
                                NbtUtils.indent($$2 + 1, $$0);
                            }
                        } else if ($$18 != 0) {
                            $$0.append(' ');
                        }
                        $$0.append(String.format((Locale)Locale.ROOT, (String)("0x%0" + $$15 + "X"), (Object[])new Object[]{$$14[$$18]}));
                    }
                } else {
                    NbtUtils.indent($$2 + 1, $$0).append(" // Skipped, supply withBinaryBlobs true");
                }
                $$0.append('\n');
                NbtUtils.indent($$2, $$0).append('}');
                break;
            }
            case 10: {
                CompoundTag $$19 = (CompoundTag)$$1;
                ArrayList $$20 = Lists.newArrayList($$19.getAllKeys());
                Collections.sort((List)$$20);
                NbtUtils.indent($$2, $$0).append('{');
                if ($$0.length() - $$0.lastIndexOf("\n") > 2 * ($$2 + 1)) {
                    $$0.append('\n');
                    NbtUtils.indent($$2 + 1, $$0);
                }
                int $$21 = $$20.stream().mapToInt(String::length).max().orElse(0);
                String $$22 = Strings.repeat((String)" ", (int)$$21);
                for (int $$23 = 0; $$23 < $$20.size(); ++$$23) {
                    if ($$23 != 0) {
                        $$0.append(",\n");
                    }
                    String $$24 = (String)$$20.get($$23);
                    NbtUtils.indent($$2 + 1, $$0).append('\"').append($$24).append('\"').append((CharSequence)$$22, 0, $$22.length() - $$24.length()).append(": ");
                    NbtUtils.prettyPrint($$0, $$19.get($$24), $$2 + 1, $$3);
                }
                if (!$$20.isEmpty()) {
                    $$0.append('\n');
                }
                NbtUtils.indent($$2, $$0).append('}');
                break;
            }
            case 12: {
                LongArrayTag $$25 = (LongArrayTag)$$1;
                long[] $$26 = $$25.getAsLongArray();
                long $$27 = 0L;
                for (long $$28 : $$26) {
                    $$27 = Math.max((long)$$27, (long)String.format((Locale)Locale.ROOT, (String)"%X", (Object[])new Object[]{$$28}).length());
                }
                long $$29 = $$26.length;
                NbtUtils.indent($$2, $$0).append("long[").append($$29).append("] {\n");
                if ($$3) {
                    NbtUtils.indent($$2 + 1, $$0);
                    for (int $$30 = 0; $$30 < $$26.length; ++$$30) {
                        if ($$30 != 0) {
                            $$0.append(',');
                        }
                        if ($$30 % 16 == 0 && $$30 / 16 > 0) {
                            $$0.append('\n');
                            if ($$30 < $$26.length) {
                                NbtUtils.indent($$2 + 1, $$0);
                            }
                        } else if ($$30 != 0) {
                            $$0.append(' ');
                        }
                        $$0.append(String.format((Locale)Locale.ROOT, (String)("0x%0" + $$27 + "X"), (Object[])new Object[]{$$26[$$30]}));
                    }
                } else {
                    NbtUtils.indent($$2 + 1, $$0).append(" // Skipped, supply withBinaryBlobs true");
                }
                $$0.append('\n');
                NbtUtils.indent($$2, $$0).append('}');
                break;
            }
            default: {
                $$0.append("<UNKNOWN :(>");
            }
        }
        return $$0;
    }

    private static StringBuilder indent(int $$0, StringBuilder $$1) {
        int $$2 = $$1.lastIndexOf("\n") + 1;
        int $$3 = $$1.length() - $$2;
        for (int $$4 = 0; $$4 < 2 * $$0 - $$3; ++$$4) {
            $$1.append(' ');
        }
        return $$1;
    }

    public static CompoundTag update(DataFixer $$0, DataFixTypes $$1, CompoundTag $$2, int $$3) {
        return NbtUtils.update($$0, $$1, $$2, $$3, SharedConstants.getCurrentVersion().getWorldVersion());
    }

    public static CompoundTag update(DataFixer $$0, DataFixTypes $$1, CompoundTag $$2, int $$3, int $$4) {
        return (CompoundTag)$$0.update($$1.getType(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$2), $$3, $$4).getValue();
    }

    public static Component toPrettyComponent(Tag $$0) {
        return new TextComponentTagVisitor("", 0).visit($$0);
    }

    public static String structureToSnbt(CompoundTag $$0) {
        return new SnbtPrinterTagVisitor().visit(NbtUtils.packStructureTemplate($$0));
    }

    public static CompoundTag snbtToStructure(String $$0) throws CommandSyntaxException {
        return NbtUtils.unpackStructureTemplate(TagParser.parseTag($$0));
    }

    @VisibleForTesting
    static CompoundTag packStructureTemplate(CompoundTag $$02) {
        ListTag $$3;
        boolean $$12 = $$02.contains("palettes", 9);
        if ($$12) {
            ListTag $$22 = $$02.getList("palettes", 9).getList(0);
        } else {
            $$3 = $$02.getList("palette", 10);
        }
        ListTag $$4 = (ListTag)$$3.stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).map(NbtUtils::packBlockState).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
        $$02.put("palette", $$4);
        if ($$12) {
            ListTag $$5 = new ListTag();
            ListTag $$6 = $$02.getList("palettes", 9);
            $$6.stream().map(arg_0 -> ListTag.class.cast(arg_0)).forEach($$2 -> {
                CompoundTag $$3 = new CompoundTag();
                for (int $$4 = 0; $$4 < $$2.size(); ++$$4) {
                    $$3.putString($$4.getString($$4), NbtUtils.packBlockState($$2.getCompound($$4)));
                }
                $$5.add($$3);
            });
            $$02.put("palettes", $$5);
        }
        if ($$02.contains("entities", 9)) {
            ListTag $$7 = $$02.getList("entities", 10);
            ListTag $$8 = (ListTag)$$7.stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).sorted(Comparator.comparing($$0 -> $$0.getList("pos", 6), YXZ_LISTTAG_DOUBLE_COMPARATOR)).collect(Collectors.toCollection(ListTag::new));
            $$02.put("entities", $$8);
        }
        ListTag $$9 = (ListTag)$$02.getList("blocks", 10).stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).sorted(Comparator.comparing($$0 -> $$0.getList("pos", 3), YXZ_LISTTAG_INT_COMPARATOR)).peek($$1 -> $$1.putString("state", $$4.getString($$1.getInt("state")))).collect(Collectors.toCollection(ListTag::new));
        $$02.put(SNBT_DATA_TAG, $$9);
        $$02.remove("blocks");
        return $$02;
    }

    @VisibleForTesting
    static CompoundTag unpackStructureTemplate(CompoundTag $$0) {
        ListTag $$12 = $$0.getList("palette", 8);
        Map $$2 = (Map)$$12.stream().map(arg_0 -> StringTag.class.cast(arg_0)).map(StringTag::getAsString).collect(ImmutableMap.toImmutableMap((Function)Function.identity(), NbtUtils::unpackBlockState));
        if ($$0.contains("palettes", 9)) {
            $$0.put("palettes", (Tag)$$0.getList("palettes", 10).stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).map($$1 -> (ListTag)$$2.keySet().stream().map($$1::getString).map(NbtUtils::unpackBlockState).collect(Collectors.toCollection(ListTag::new))).collect(Collectors.toCollection(ListTag::new)));
            $$0.remove("palette");
        } else {
            $$0.put("palette", (Tag)$$2.values().stream().collect(Collectors.toCollection(ListTag::new)));
        }
        if ($$0.contains(SNBT_DATA_TAG, 9)) {
            Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
            $$3.defaultReturnValue(-1);
            for (int $$4 = 0; $$4 < $$12.size(); ++$$4) {
                $$3.put((Object)$$12.getString($$4), $$4);
            }
            ListTag $$5 = $$0.getList(SNBT_DATA_TAG, 10);
            for (int $$6 = 0; $$6 < $$5.size(); ++$$6) {
                CompoundTag $$7 = $$5.getCompound($$6);
                String $$8 = $$7.getString("state");
                int $$9 = $$3.getInt((Object)$$8);
                if ($$9 == -1) {
                    throw new IllegalStateException("Entry " + $$8 + " missing from palette");
                }
                $$7.putInt("state", $$9);
            }
            $$0.put("blocks", $$5);
            $$0.remove(SNBT_DATA_TAG);
        }
        return $$0;
    }

    @VisibleForTesting
    static String packBlockState(CompoundTag $$0) {
        StringBuilder $$12 = new StringBuilder($$0.getString("Name"));
        if ($$0.contains("Properties", 10)) {
            CompoundTag $$2 = $$0.getCompound("Properties");
            String $$3 = (String)$$2.getAllKeys().stream().sorted().map($$1 -> $$1 + ":" + $$2.get((String)$$1).getAsString()).collect(Collectors.joining((CharSequence)ELEMENT_SEPARATOR));
            $$12.append('{').append($$3).append('}');
        }
        return $$12.toString();
    }

    @VisibleForTesting
    static CompoundTag unpackBlockState(String $$0) {
        String $$6;
        CompoundTag $$1 = new CompoundTag();
        int $$22 = $$0.indexOf(123);
        if ($$22 >= 0) {
            String $$3 = $$0.substring(0, $$22);
            CompoundTag $$4 = new CompoundTag();
            if ($$22 + 2 <= $$0.length()) {
                String $$5 = $$0.substring($$22 + 1, $$0.indexOf(125, $$22));
                COMMA_SPLITTER.split((CharSequence)$$5).forEach($$2 -> {
                    List $$3 = COLON_SPLITTER.splitToList((CharSequence)$$2);
                    if ($$3.size() == 2) {
                        $$4.putString((String)$$3.get(0), (String)$$3.get(1));
                    } else {
                        LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", (Object)$$0);
                    }
                });
                $$1.put("Properties", $$4);
            }
        } else {
            $$6 = $$0;
        }
        $$1.putString("Name", $$6);
        return $$1;
    }
}