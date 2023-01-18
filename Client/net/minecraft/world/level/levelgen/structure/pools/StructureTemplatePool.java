/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructureTemplatePool {
    private static final int SIZE_UNSET = Integer.MIN_VALUE;
    private static final MutableObject<Codec<Holder<StructureTemplatePool>>> CODEC_REFERENCE = new MutableObject();
    public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ExtraCodecs.lazyInitializedCodec(() -> CODEC_REFERENCE.getValue()).fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), (App)Codec.mapPair((MapCodec)StructurePoolElement.CODEC.fieldOf("element"), (MapCodec)Codec.intRange((int)1, (int)150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter($$0 -> $$0.rawTemplates)).apply((Applicative)$$02, StructureTemplatePool::new));
    public static final Codec<Holder<StructureTemplatePool>> CODEC = Util.make(RegistryFileCodec.create(Registries.TEMPLATE_POOL, DIRECT_CODEC), arg_0 -> CODEC_REFERENCE.setValue(arg_0));
    private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final ObjectArrayList<StructurePoolElement> templates;
    private final Holder<StructureTemplatePool> fallback;
    private int maxSize = Integer.MIN_VALUE;

    public StructureTemplatePool(Holder<StructureTemplatePool> $$0, List<Pair<StructurePoolElement, Integer>> $$1) {
        this.rawTemplates = $$1;
        this.templates = new ObjectArrayList();
        for (Pair $$2 : $$1) {
            StructurePoolElement $$3 = (StructurePoolElement)$$2.getFirst();
            for (int $$4 = 0; $$4 < (Integer)$$2.getSecond(); ++$$4) {
                this.templates.add((Object)$$3);
            }
        }
        this.fallback = $$0;
    }

    public StructureTemplatePool(Holder<StructureTemplatePool> $$0, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> $$1, Projection $$2) {
        this.rawTemplates = Lists.newArrayList();
        this.templates = new ObjectArrayList();
        for (Pair $$3 : $$1) {
            StructurePoolElement $$4 = (StructurePoolElement)((Function)$$3.getFirst()).apply((Object)$$2);
            this.rawTemplates.add((Object)Pair.of((Object)$$4, (Object)((Integer)$$3.getSecond())));
            for (int $$5 = 0; $$5 < (Integer)$$3.getSecond(); ++$$5) {
                this.templates.add((Object)$$4);
            }
        }
        this.fallback = $$0;
    }

    public int getMaxSize(StructureTemplateManager $$02) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().filter($$0 -> $$0 != EmptyPoolElement.INSTANCE).mapToInt($$1 -> $$1.getBoundingBox($$02, BlockPos.ZERO, Rotation.NONE).getYSpan()).max().orElse(0);
        }
        return this.maxSize;
    }

    public Holder<StructureTemplatePool> getFallback() {
        return this.fallback;
    }

    public StructurePoolElement getRandomTemplate(RandomSource $$0) {
        return (StructurePoolElement)this.templates.get($$0.nextInt(this.templates.size()));
    }

    public List<StructurePoolElement> getShuffledTemplates(RandomSource $$0) {
        return Util.shuffledCopy(this.templates, $$0);
    }

    public int size() {
        return this.templates.size();
    }

    public static enum Projection implements StringRepresentable
    {
        TERRAIN_MATCHING("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());

        public static final StringRepresentable.EnumCodec<Projection> CODEC;
        private final String name;
        private final ImmutableList<StructureProcessor> processors;

        private Projection(String $$0, ImmutableList<StructureProcessor> $$1) {
            this.name = $$0;
            this.processors = $$1;
        }

        public String getName() {
            return this.name;
        }

        public static Projection byName(String $$0) {
            return CODEC.byName($$0);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Projection::values));
        }
    }
}