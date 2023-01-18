/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalStructure
extends Structure {
    private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05f;
    private static final int MIN_Y_INDEX = 15;
    private final List<Setup> setups;
    public static final Codec<RuinedPortalStructure> CODEC = RecordCodecBuilder.create($$02 -> $$02.group(RuinedPortalStructure.settingsCodec($$02), (App)ExtraCodecs.nonEmptyList(Setup.CODEC.listOf()).fieldOf("setups").forGetter($$0 -> $$0.setups)).apply((Applicative)$$02, RuinedPortalStructure::new));

    public RuinedPortalStructure(Structure.StructureSettings $$0, List<Setup> $$1) {
        super($$0);
        this.setups = $$1;
    }

    public RuinedPortalStructure(Structure.StructureSettings $$0, Setup $$1) {
        this($$0, (List<Setup>)List.of((Object)((Object)$$1)));
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext $$0) {
        ResourceLocation $$102;
        RuinedPortalPiece.Properties $$1 = new RuinedPortalPiece.Properties();
        WorldgenRandom $$2 = $$0.random();
        Setup $$3 = null;
        if (this.setups.size() > 1) {
            float $$4 = 0.0f;
            for (Setup $$5 : this.setups) {
                $$4 += $$5.weight();
            }
            float $$6 = $$2.nextFloat();
            for (Setup $$7 : this.setups) {
                if (!(($$6 -= $$7.weight() / $$4) < 0.0f)) continue;
                $$3 = $$7;
                break;
            }
        } else {
            $$3 = (Setup)((Object)this.setups.get(0));
        }
        if ($$3 == null) {
            throw new IllegalStateException();
        }
        Setup $$8 = $$3;
        $$1.airPocket = RuinedPortalStructure.sample($$2, $$8.airPocketProbability());
        $$1.mossiness = $$8.mossiness();
        $$1.overgrown = $$8.overgrown();
        $$1.vines = $$8.vines();
        $$1.replaceWithBlackstone = $$8.replaceWithBlackstone();
        if ($$2.nextFloat() < 0.05f) {
            ResourceLocation $$9 = new ResourceLocation(STRUCTURE_LOCATION_GIANT_PORTALS[$$2.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
        } else {
            $$102 = new ResourceLocation(STRUCTURE_LOCATION_PORTALS[$$2.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
        }
        StructureTemplate $$11 = $$0.structureTemplateManager().getOrCreate($$102);
        Rotation $$12 = Util.getRandom(Rotation.values(), (RandomSource)$$2);
        Mirror $$13 = $$2.nextFloat() < 0.5f ? Mirror.NONE : Mirror.FRONT_BACK;
        BlockPos $$14 = new BlockPos($$11.getSize().getX() / 2, 0, $$11.getSize().getZ() / 2);
        ChunkGenerator $$15 = $$0.chunkGenerator();
        LevelHeightAccessor $$16 = $$0.heightAccessor();
        RandomState $$17 = $$0.randomState();
        BlockPos $$18 = $$0.chunkPos().getWorldPosition();
        BoundingBox $$19 = $$11.getBoundingBox($$18, $$12, $$14, $$13);
        BlockPos $$20 = $$19.getCenter();
        int $$21 = $$15.getBaseHeight($$20.getX(), $$20.getZ(), RuinedPortalPiece.getHeightMapType($$8.placement()), $$16, $$17) - 1;
        int $$22 = RuinedPortalStructure.findSuitableY($$2, $$15, $$8.placement(), $$1.airPocket, $$21, $$19.getYSpan(), $$19, $$16, $$17);
        BlockPos $$23 = new BlockPos($$18.getX(), $$22, $$18.getZ());
        return Optional.of((Object)((Object)new Structure.GenerationStub($$23, (Consumer<StructurePiecesBuilder>)((Consumer)$$10 -> {
            if ($$8.canBeCold()) {
                $$1.cold = RuinedPortalStructure.isCold($$23, $$0.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$23.getX()), QuartPos.fromBlock($$23.getY()), QuartPos.fromBlock($$23.getZ()), $$17.sampler()));
            }
            $$10.addPiece(new RuinedPortalPiece($$0.structureTemplateManager(), $$23, $$8.placement(), $$1, $$102, $$11, $$12, $$13, $$14));
        }))));
    }

    private static boolean sample(WorldgenRandom $$0, float $$1) {
        if ($$1 == 0.0f) {
            return false;
        }
        if ($$1 == 1.0f) {
            return true;
        }
        return $$0.nextFloat() < $$1;
    }

    private static boolean isCold(BlockPos $$0, Holder<Biome> $$1) {
        return $$1.value().coldEnoughToSnow($$0);
    }

    private static int findSuitableY(RandomSource $$0, ChunkGenerator $$1, RuinedPortalPiece.VerticalPlacement $$2, boolean $$32, int $$4, int $$5, BoundingBox $$6, LevelHeightAccessor $$7, RandomState $$8) {
        int $$22;
        int $$9 = $$7.getMinBuildHeight() + 15;
        if ($$2 == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
            if ($$32) {
                int $$10 = Mth.randomBetweenInclusive($$0, 32, 100);
            } else if ($$0.nextFloat() < 0.5f) {
                int $$11 = Mth.randomBetweenInclusive($$0, 27, 29);
            } else {
                int $$12 = Mth.randomBetweenInclusive($$0, 29, 100);
            }
        } else if ($$2 == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            int $$13 = $$4 - $$5;
            int $$14 = RuinedPortalStructure.getRandomWithinInterval($$0, 70, $$13);
        } else if ($$2 == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            int $$15 = $$4 - $$5;
            int $$16 = RuinedPortalStructure.getRandomWithinInterval($$0, $$9, $$15);
        } else if ($$2 == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
            int $$17 = $$4 - $$5 + Mth.randomBetweenInclusive($$0, 2, 8);
        } else {
            int $$18 = $$4;
        }
        ImmutableList $$19 = ImmutableList.of((Object)new BlockPos($$6.minX(), 0, $$6.minZ()), (Object)new BlockPos($$6.maxX(), 0, $$6.minZ()), (Object)new BlockPos($$6.minX(), 0, $$6.maxZ()), (Object)new BlockPos($$6.maxX(), 0, $$6.maxZ()));
        List $$20 = (List)$$19.stream().map($$3 -> $$1.getBaseColumn($$3.getX(), $$3.getZ(), $$7, $$8)).collect(Collectors.toList());
        Heightmap.Types $$21 = $$2 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
        block0: for ($$22 = $$18; $$22 > $$9; --$$22) {
            int $$23 = 0;
            for (NoiseColumn $$24 : $$20) {
                BlockState $$25 = $$24.getBlock($$22);
                if (!$$21.isOpaque().test((Object)$$25) || ++$$23 != 3) continue;
                break block0;
            }
        }
        return $$22;
    }

    private static int getRandomWithinInterval(RandomSource $$0, int $$1, int $$2) {
        if ($$1 < $$2) {
            return Mth.randomBetweenInclusive($$0, $$1, $$2);
        }
        return $$2;
    }

    @Override
    public StructureType<?> type() {
        return StructureType.RUINED_PORTAL;
    }

    public record Setup(RuinedPortalPiece.VerticalPlacement placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)RuinedPortalPiece.VerticalPlacement.CODEC.fieldOf("placement").forGetter(Setup::placement), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("air_pocket_probability").forGetter(Setup::airPocketProbability), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("mossiness").forGetter(Setup::mossiness), (App)Codec.BOOL.fieldOf("overgrown").forGetter(Setup::overgrown), (App)Codec.BOOL.fieldOf("vines").forGetter(Setup::vines), (App)Codec.BOOL.fieldOf("can_be_cold").forGetter(Setup::canBeCold), (App)Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(Setup::replaceWithBlackstone), (App)ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(Setup::weight)).apply((Applicative)$$0, Setup::new));
    }
}