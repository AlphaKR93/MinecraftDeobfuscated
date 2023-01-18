/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.MapCodec
 *  java.lang.Comparable
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collections
 *  java.util.List
 *  java.util.Locale
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.ToIntFunction
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockBehaviour
implements FeatureElement {
    protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final Material material;
    protected final boolean hasCollision;
    protected final float explosionResistance;
    protected final boolean isRandomlyTicking;
    protected final SoundType soundType;
    protected final float friction;
    protected final float speedFactor;
    protected final float jumpFactor;
    protected final boolean dynamicShape;
    protected final FeatureFlagSet requiredFeatures;
    protected final Properties properties;
    @Nullable
    protected ResourceLocation drops;

    public BlockBehaviour(Properties $$0) {
        this.material = $$0.material;
        this.hasCollision = $$0.hasCollision;
        this.drops = $$0.drops;
        this.explosionResistance = $$0.explosionResistance;
        this.isRandomlyTicking = $$0.isRandomlyTicking;
        this.soundType = $$0.soundType;
        this.friction = $$0.friction;
        this.speedFactor = $$0.speedFactor;
        this.jumpFactor = $$0.jumpFactor;
        this.dynamicShape = $$0.dynamicShape;
        this.requiredFeatures = $$0.requiredFeatures;
        this.properties = $$0;
    }

    @Deprecated
    public void updateIndirectNeighbourShapes(BlockState $$0, LevelAccessor $$1, BlockPos $$2, int $$3, int $$4) {
    }

    @Deprecated
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        switch ($$3) {
            case LAND: {
                return !$$0.isCollisionShapeFullBlock($$1, $$2);
            }
            case WATER: {
                return $$1.getFluidState($$2).is(FluidTags.WATER);
            }
            case AIR: {
                return !$$0.isCollisionShapeFullBlock($$1, $$2);
            }
        }
        return false;
    }

    @Deprecated
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        return $$0;
    }

    @Deprecated
    public boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        return false;
    }

    @Deprecated
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        DebugPackets.sendNeighborsUpdatePacket($$1, $$2);
    }

    @Deprecated
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
    }

    @Deprecated
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.hasBlockEntity() && !$$0.is($$3.getBlock())) {
            $$1.removeBlockEntity($$2);
        }
    }

    @Deprecated
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        return InteractionResult.PASS;
    }

    @Deprecated
    public boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        return false;
    }

    @Deprecated
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Deprecated
    public boolean useShapeForLightOcclusion(BlockState $$0) {
        return false;
    }

    @Deprecated
    public boolean isSignalSource(BlockState $$0) {
        return false;
    }

    @Deprecated
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return this.material.getPushReaction();
    }

    @Deprecated
    public FluidState getFluidState(BlockState $$0) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Deprecated
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return false;
    }

    public float getMaxHorizontalOffset() {
        return 0.25f;
    }

    public float getMaxVerticalOffset() {
        return 0.2f;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    @Deprecated
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return $$0;
    }

    @Deprecated
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0;
    }

    @Deprecated
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return this.material.isReplaceable() && ($$1.getItemInHand().isEmpty() || !$$1.getItemInHand().is(this.asItem()));
    }

    @Deprecated
    public boolean canBeReplaced(BlockState $$0, Fluid $$1) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDrops(BlockState $$0, LootContext.Builder $$1) {
        ResourceLocation $$2 = this.getLootTable();
        if ($$2 == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext $$3 = $$1.withParameter(LootContextParams.BLOCK_STATE, $$0).create(LootContextParamSets.BLOCK);
        ServerLevel $$4 = $$3.getLevel();
        LootTable $$5 = $$4.getServer().getLootTables().get($$2);
        return $$5.getRandomItems($$3);
    }

    @Deprecated
    public long getSeed(BlockState $$0, BlockPos $$1) {
        return Mth.getSeed($$1);
    }

    @Deprecated
    public VoxelShape getOcclusionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.getShape($$1, $$2);
    }

    @Deprecated
    public VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.getCollisionShape($$0, $$1, $$2, CollisionContext.empty());
    }

    @Deprecated
    public VoxelShape getInteractionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Shapes.empty();
    }

    @Deprecated
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        if ($$0.isSolidRender($$1, $$2)) {
            return $$1.getMaxLightLevel();
        }
        return $$0.propagatesSkylightDown($$1, $$2) ? 0 : 1;
    }

    @Nullable
    @Deprecated
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        return null;
    }

    @Deprecated
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return true;
    }

    @Deprecated
    public float getShadeBrightness(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.isCollisionShapeFullBlock($$1, $$2) ? 0.2f : 1.0f;
    }

    @Deprecated
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return 0;
    }

    @Deprecated
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.block();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.hasCollision ? $$0.getShape($$1, $$2) : Shapes.empty();
    }

    @Deprecated
    public boolean isCollisionShapeFullBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Block.isShapeFullBlock($$0.getCollisionShape($$1, $$2));
    }

    @Deprecated
    public boolean isOcclusionShapeFullBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return Block.isShapeFullBlock($$0.getOcclusionShape($$1, $$2));
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.getCollisionShape($$0, $$1, $$2, $$3);
    }

    @Deprecated
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        this.tick($$0, $$1, $$2, $$3);
    }

    @Deprecated
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
    }

    @Deprecated
    public float getDestroyProgress(BlockState $$0, Player $$1, BlockGetter $$2, BlockPos $$3) {
        float $$4 = $$0.getDestroySpeed($$2, $$3);
        if ($$4 == -1.0f) {
            return 0.0f;
        }
        int $$5 = $$1.hasCorrectToolForDrops($$0) ? 30 : 100;
        return $$1.getDestroySpeed($$0) / $$4 / (float)$$5;
    }

    @Deprecated
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
    }

    @Deprecated
    public void attack(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
    }

    @Deprecated
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return 0;
    }

    @Deprecated
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
    }

    @Deprecated
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return 0;
    }

    public final ResourceLocation getLootTable() {
        if (this.drops == null) {
            ResourceLocation $$0 = BuiltInRegistries.BLOCK.getKey(this.asBlock());
            this.drops = $$0.withPrefix("blocks/");
        }
        return this.drops;
    }

    @Deprecated
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MaterialColor defaultMaterialColor() {
        return (MaterialColor)this.properties.materialColor.apply((Object)this.asBlock().defaultBlockState());
    }

    public float defaultDestroyTime() {
        return this.properties.destroyTime;
    }

    public static class Properties {
        Material material;
        Function<BlockState, MaterialColor> materialColor;
        boolean hasCollision = true;
        SoundType soundType = SoundType.STONE;
        ToIntFunction<BlockState> lightEmission = $$0 -> 0;
        float explosionResistance;
        float destroyTime;
        boolean requiresCorrectToolForDrops;
        boolean isRandomlyTicking;
        float friction = 0.6f;
        float speedFactor = 1.0f;
        float jumpFactor = 1.0f;
        ResourceLocation drops;
        boolean canOcclude = true;
        boolean isAir;
        boolean spawnParticlesOnBreak = true;
        StateArgumentPredicate<EntityType<?>> isValidSpawn = ($$0, $$1, $$2, $$3) -> $$0.isFaceSturdy($$1, $$2, Direction.UP) && $$0.getLightEmission() < 14;
        StatePredicate isRedstoneConductor = ($$0, $$1, $$2) -> $$0.getMaterial().isSolidBlocking() && $$0.isCollisionShapeFullBlock($$1, $$2);
        StatePredicate isSuffocating;
        StatePredicate isViewBlocking = this.isSuffocating = ($$0, $$1, $$2) -> this.material.blocksMotion() && $$0.isCollisionShapeFullBlock($$1, $$2);
        StatePredicate hasPostProcess = ($$0, $$1, $$2) -> false;
        StatePredicate emissiveRendering = ($$0, $$1, $$2) -> false;
        boolean dynamicShape;
        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
        Function<BlockState, OffsetType> offsetType = $$0 -> OffsetType.NONE;

        private Properties(Material $$0, MaterialColor $$12) {
            this($$0, (Function<BlockState, MaterialColor>)((Function)$$1 -> $$12));
        }

        private Properties(Material $$02, Function<BlockState, MaterialColor> $$12) {
            this.material = $$02;
            this.materialColor = $$12;
        }

        public static Properties of(Material $$0) {
            return Properties.of($$0, $$0.getColor());
        }

        public static Properties of(Material $$0, DyeColor $$1) {
            return Properties.of($$0, $$1.getMaterialColor());
        }

        public static Properties of(Material $$0, MaterialColor $$1) {
            return new Properties($$0, $$1);
        }

        public static Properties of(Material $$0, Function<BlockState, MaterialColor> $$1) {
            return new Properties($$0, $$1);
        }

        public static Properties copy(BlockBehaviour $$0) {
            Properties $$1 = new Properties($$0.material, $$0.properties.materialColor);
            $$1.material = $$0.properties.material;
            $$1.destroyTime = $$0.properties.destroyTime;
            $$1.explosionResistance = $$0.properties.explosionResistance;
            $$1.hasCollision = $$0.properties.hasCollision;
            $$1.isRandomlyTicking = $$0.properties.isRandomlyTicking;
            $$1.lightEmission = $$0.properties.lightEmission;
            $$1.materialColor = $$0.properties.materialColor;
            $$1.soundType = $$0.properties.soundType;
            $$1.friction = $$0.properties.friction;
            $$1.speedFactor = $$0.properties.speedFactor;
            $$1.dynamicShape = $$0.properties.dynamicShape;
            $$1.canOcclude = $$0.properties.canOcclude;
            $$1.isAir = $$0.properties.isAir;
            $$1.requiresCorrectToolForDrops = $$0.properties.requiresCorrectToolForDrops;
            $$1.offsetType = $$0.properties.offsetType;
            $$1.spawnParticlesOnBreak = $$0.properties.spawnParticlesOnBreak;
            $$1.requiredFeatures = $$0.properties.requiredFeatures;
            return $$1;
        }

        public Properties noCollission() {
            this.hasCollision = false;
            this.canOcclude = false;
            return this;
        }

        public Properties noOcclusion() {
            this.canOcclude = false;
            return this;
        }

        public Properties friction(float $$0) {
            this.friction = $$0;
            return this;
        }

        public Properties speedFactor(float $$0) {
            this.speedFactor = $$0;
            return this;
        }

        public Properties jumpFactor(float $$0) {
            this.jumpFactor = $$0;
            return this;
        }

        public Properties sound(SoundType $$0) {
            this.soundType = $$0;
            return this;
        }

        public Properties lightLevel(ToIntFunction<BlockState> $$0) {
            this.lightEmission = $$0;
            return this;
        }

        public Properties strength(float $$0, float $$1) {
            return this.destroyTime($$0).explosionResistance($$1);
        }

        public Properties instabreak() {
            return this.strength(0.0f);
        }

        public Properties strength(float $$0) {
            this.strength($$0, $$0);
            return this;
        }

        public Properties randomTicks() {
            this.isRandomlyTicking = true;
            return this;
        }

        public Properties dynamicShape() {
            this.dynamicShape = true;
            return this;
        }

        public Properties noLootTable() {
            this.drops = BuiltInLootTables.EMPTY;
            return this;
        }

        public Properties dropsLike(Block $$0) {
            this.drops = $$0.getLootTable();
            return this;
        }

        public Properties air() {
            this.isAir = true;
            return this;
        }

        public Properties isValidSpawn(StateArgumentPredicate<EntityType<?>> $$0) {
            this.isValidSpawn = $$0;
            return this;
        }

        public Properties isRedstoneConductor(StatePredicate $$0) {
            this.isRedstoneConductor = $$0;
            return this;
        }

        public Properties isSuffocating(StatePredicate $$0) {
            this.isSuffocating = $$0;
            return this;
        }

        public Properties isViewBlocking(StatePredicate $$0) {
            this.isViewBlocking = $$0;
            return this;
        }

        public Properties hasPostProcess(StatePredicate $$0) {
            this.hasPostProcess = $$0;
            return this;
        }

        public Properties emissiveRendering(StatePredicate $$0) {
            this.emissiveRendering = $$0;
            return this;
        }

        public Properties requiresCorrectToolForDrops() {
            this.requiresCorrectToolForDrops = true;
            return this;
        }

        public Properties color(MaterialColor $$0) {
            this.materialColor = $$1 -> $$0;
            return this;
        }

        public Properties destroyTime(float $$0) {
            this.destroyTime = $$0;
            return this;
        }

        public Properties explosionResistance(float $$0) {
            this.explosionResistance = Math.max((float)0.0f, (float)$$0);
            return this;
        }

        public Properties offsetType(OffsetType $$0) {
            return this.offsetType((Function<BlockState, OffsetType>)((Function)$$1 -> $$0));
        }

        public Properties offsetType(Function<BlockState, OffsetType> $$0) {
            this.offsetType = $$0;
            return this;
        }

        public Properties noParticlesOnBreak() {
            this.spawnParticlesOnBreak = false;
            return this;
        }

        public Properties requiredFeatures(FeatureFlag ... $$0) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
            return this;
        }
    }

    public static interface StateArgumentPredicate<A> {
        public boolean test(BlockState var1, BlockGetter var2, BlockPos var3, A var4);
    }

    public static interface StatePredicate {
        public boolean test(BlockState var1, BlockGetter var2, BlockPos var3);
    }

    public static abstract class BlockStateBase
    extends StateHolder<Block, BlockState> {
        private final int lightEmission;
        private final boolean useShapeForLightOcclusion;
        private final boolean isAir;
        private final Material material;
        private final MaterialColor materialColor;
        private final float destroySpeed;
        private final boolean requiresCorrectToolForDrops;
        private final boolean canOcclude;
        private final StatePredicate isRedstoneConductor;
        private final StatePredicate isSuffocating;
        private final StatePredicate isViewBlocking;
        private final StatePredicate hasPostProcess;
        private final StatePredicate emissiveRendering;
        private final OffsetType offsetType;
        private final boolean spawnParticlesOnBreak;
        @Nullable
        protected Cache cache;
        private FluidState fluidState = Fluids.EMPTY.defaultFluidState();
        private boolean isRandomlyTicking;

        protected BlockStateBase(Block $$0, ImmutableMap<Property<?>, Comparable<?>> $$1, MapCodec<BlockState> $$2) {
            super($$0, $$1, $$2);
            Properties $$3 = $$0.properties;
            this.lightEmission = $$3.lightEmission.applyAsInt((Object)this.asState());
            this.useShapeForLightOcclusion = $$0.useShapeForLightOcclusion(this.asState());
            this.isAir = $$3.isAir;
            this.material = $$3.material;
            this.materialColor = (MaterialColor)$$3.materialColor.apply((Object)this.asState());
            this.destroySpeed = $$3.destroyTime;
            this.requiresCorrectToolForDrops = $$3.requiresCorrectToolForDrops;
            this.canOcclude = $$3.canOcclude;
            this.isRedstoneConductor = $$3.isRedstoneConductor;
            this.isSuffocating = $$3.isSuffocating;
            this.isViewBlocking = $$3.isViewBlocking;
            this.hasPostProcess = $$3.hasPostProcess;
            this.emissiveRendering = $$3.emissiveRendering;
            this.offsetType = (OffsetType)((Object)$$3.offsetType.apply((Object)this.asState()));
            this.spawnParticlesOnBreak = $$3.spawnParticlesOnBreak;
        }

        public void initCache() {
            this.fluidState = ((Block)this.owner).getFluidState(this.asState());
            this.isRandomlyTicking = ((Block)this.owner).isRandomlyTicking(this.asState());
            if (!this.getBlock().hasDynamicShape()) {
                this.cache = new Cache(this.asState());
            }
        }

        public Block getBlock() {
            return (Block)this.owner;
        }

        public Holder<Block> getBlockHolder() {
            return ((Block)this.owner).builtInRegistryHolder();
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean isValidSpawn(BlockGetter $$0, BlockPos $$1, EntityType<?> $$2) {
            return this.getBlock().properties.isValidSpawn.test(this.asState(), $$0, $$1, $$2);
        }

        public boolean propagatesSkylightDown(BlockGetter $$0, BlockPos $$1) {
            if (this.cache != null) {
                return this.cache.propagatesSkylightDown;
            }
            return this.getBlock().propagatesSkylightDown(this.asState(), $$0, $$1);
        }

        public int getLightBlock(BlockGetter $$0, BlockPos $$1) {
            if (this.cache != null) {
                return this.cache.lightBlock;
            }
            return this.getBlock().getLightBlock(this.asState(), $$0, $$1);
        }

        public VoxelShape getFaceOcclusionShape(BlockGetter $$0, BlockPos $$1, Direction $$2) {
            if (this.cache != null && this.cache.occlusionShapes != null) {
                return this.cache.occlusionShapes[$$2.ordinal()];
            }
            return Shapes.getFaceShape(this.getOcclusionShape($$0, $$1), $$2);
        }

        public VoxelShape getOcclusionShape(BlockGetter $$0, BlockPos $$1) {
            return this.getBlock().getOcclusionShape(this.asState(), $$0, $$1);
        }

        public boolean hasLargeCollisionShape() {
            return this.cache == null || this.cache.largeCollisionShape;
        }

        public boolean useShapeForLightOcclusion() {
            return this.useShapeForLightOcclusion;
        }

        public int getLightEmission() {
            return this.lightEmission;
        }

        public boolean isAir() {
            return this.isAir;
        }

        public MaterialColor getMapColor(BlockGetter $$0, BlockPos $$1) {
            return this.materialColor;
        }

        public BlockState rotate(Rotation $$0) {
            return this.getBlock().rotate(this.asState(), $$0);
        }

        public BlockState mirror(Mirror $$0) {
            return this.getBlock().mirror(this.asState(), $$0);
        }

        public RenderShape getRenderShape() {
            return this.getBlock().getRenderShape(this.asState());
        }

        public boolean emissiveRendering(BlockGetter $$0, BlockPos $$1) {
            return this.emissiveRendering.test(this.asState(), $$0, $$1);
        }

        public float getShadeBrightness(BlockGetter $$0, BlockPos $$1) {
            return this.getBlock().getShadeBrightness(this.asState(), $$0, $$1);
        }

        public boolean isRedstoneConductor(BlockGetter $$0, BlockPos $$1) {
            return this.isRedstoneConductor.test(this.asState(), $$0, $$1);
        }

        public boolean isSignalSource() {
            return this.getBlock().isSignalSource(this.asState());
        }

        public int getSignal(BlockGetter $$0, BlockPos $$1, Direction $$2) {
            return this.getBlock().getSignal(this.asState(), $$0, $$1, $$2);
        }

        public boolean hasAnalogOutputSignal() {
            return this.getBlock().hasAnalogOutputSignal(this.asState());
        }

        public int getAnalogOutputSignal(Level $$0, BlockPos $$1) {
            return this.getBlock().getAnalogOutputSignal(this.asState(), $$0, $$1);
        }

        public float getDestroySpeed(BlockGetter $$0, BlockPos $$1) {
            return this.destroySpeed;
        }

        public float getDestroyProgress(Player $$0, BlockGetter $$1, BlockPos $$2) {
            return this.getBlock().getDestroyProgress(this.asState(), $$0, $$1, $$2);
        }

        public int getDirectSignal(BlockGetter $$0, BlockPos $$1, Direction $$2) {
            return this.getBlock().getDirectSignal(this.asState(), $$0, $$1, $$2);
        }

        public PushReaction getPistonPushReaction() {
            return this.getBlock().getPistonPushReaction(this.asState());
        }

        public boolean isSolidRender(BlockGetter $$0, BlockPos $$1) {
            if (this.cache != null) {
                return this.cache.solidRender;
            }
            BlockState $$2 = this.asState();
            if ($$2.canOcclude()) {
                return Block.isShapeFullBlock($$2.getOcclusionShape($$0, $$1));
            }
            return false;
        }

        public boolean canOcclude() {
            return this.canOcclude;
        }

        public boolean skipRendering(BlockState $$0, Direction $$1) {
            return this.getBlock().skipRendering(this.asState(), $$0, $$1);
        }

        public VoxelShape getShape(BlockGetter $$0, BlockPos $$1) {
            return this.getShape($$0, $$1, CollisionContext.empty());
        }

        public VoxelShape getShape(BlockGetter $$0, BlockPos $$1, CollisionContext $$2) {
            return this.getBlock().getShape(this.asState(), $$0, $$1, $$2);
        }

        public VoxelShape getCollisionShape(BlockGetter $$0, BlockPos $$1) {
            if (this.cache != null) {
                return this.cache.collisionShape;
            }
            return this.getCollisionShape($$0, $$1, CollisionContext.empty());
        }

        public VoxelShape getCollisionShape(BlockGetter $$0, BlockPos $$1, CollisionContext $$2) {
            return this.getBlock().getCollisionShape(this.asState(), $$0, $$1, $$2);
        }

        public VoxelShape getBlockSupportShape(BlockGetter $$0, BlockPos $$1) {
            return this.getBlock().getBlockSupportShape(this.asState(), $$0, $$1);
        }

        public VoxelShape getVisualShape(BlockGetter $$0, BlockPos $$1, CollisionContext $$2) {
            return this.getBlock().getVisualShape(this.asState(), $$0, $$1, $$2);
        }

        public VoxelShape getInteractionShape(BlockGetter $$0, BlockPos $$1) {
            return this.getBlock().getInteractionShape(this.asState(), $$0, $$1);
        }

        public final boolean entityCanStandOn(BlockGetter $$0, BlockPos $$1, Entity $$2) {
            return this.entityCanStandOnFace($$0, $$1, $$2, Direction.UP);
        }

        public final boolean entityCanStandOnFace(BlockGetter $$0, BlockPos $$1, Entity $$2, Direction $$3) {
            return Block.isFaceFull(this.getCollisionShape($$0, $$1, CollisionContext.of($$2)), $$3);
        }

        public Vec3 getOffset(BlockGetter $$0, BlockPos $$1) {
            if (this.offsetType == OffsetType.NONE) {
                return Vec3.ZERO;
            }
            Block $$2 = this.getBlock();
            long $$3 = Mth.getSeed($$1.getX(), 0, $$1.getZ());
            float $$4 = $$2.getMaxHorizontalOffset();
            double $$5 = Mth.clamp(((double)((float)($$3 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-$$4), (double)$$4);
            double $$6 = this.offsetType == OffsetType.XYZ ? ((double)((float)($$3 >> 4 & 0xFL) / 15.0f) - 1.0) * (double)$$2.getMaxVerticalOffset() : 0.0;
            double $$7 = Mth.clamp(((double)((float)($$3 >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-$$4), (double)$$4);
            return new Vec3($$5, $$6, $$7);
        }

        public boolean triggerEvent(Level $$0, BlockPos $$1, int $$2, int $$3) {
            return this.getBlock().triggerEvent(this.asState(), $$0, $$1, $$2, $$3);
        }

        @Deprecated
        public void neighborChanged(Level $$0, BlockPos $$1, Block $$2, BlockPos $$3, boolean $$4) {
            this.getBlock().neighborChanged(this.asState(), $$0, $$1, $$2, $$3, $$4);
        }

        public final void updateNeighbourShapes(LevelAccessor $$0, BlockPos $$1, int $$2) {
            this.updateNeighbourShapes($$0, $$1, $$2, 512);
        }

        public final void updateNeighbourShapes(LevelAccessor $$0, BlockPos $$1, int $$2, int $$3) {
            this.getBlock();
            BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
            for (Direction $$5 : UPDATE_SHAPE_ORDER) {
                $$4.setWithOffset((Vec3i)$$1, $$5);
                $$0.neighborShapeChanged($$5.getOpposite(), this.asState(), $$4, $$1, $$2, $$3);
            }
        }

        public final void updateIndirectNeighbourShapes(LevelAccessor $$0, BlockPos $$1, int $$2) {
            this.updateIndirectNeighbourShapes($$0, $$1, $$2, 512);
        }

        public void updateIndirectNeighbourShapes(LevelAccessor $$0, BlockPos $$1, int $$2, int $$3) {
            this.getBlock().updateIndirectNeighbourShapes(this.asState(), $$0, $$1, $$2, $$3);
        }

        public void onPlace(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
            this.getBlock().onPlace(this.asState(), $$0, $$1, $$2, $$3);
        }

        public void onRemove(Level $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
            this.getBlock().onRemove(this.asState(), $$0, $$1, $$2, $$3);
        }

        public void tick(ServerLevel $$0, BlockPos $$1, RandomSource $$2) {
            this.getBlock().tick(this.asState(), $$0, $$1, $$2);
        }

        public void randomTick(ServerLevel $$0, BlockPos $$1, RandomSource $$2) {
            this.getBlock().randomTick(this.asState(), $$0, $$1, $$2);
        }

        public void entityInside(Level $$0, BlockPos $$1, Entity $$2) {
            this.getBlock().entityInside(this.asState(), $$0, $$1, $$2);
        }

        public void spawnAfterBreak(ServerLevel $$0, BlockPos $$1, ItemStack $$2, boolean $$3) {
            this.getBlock().spawnAfterBreak(this.asState(), $$0, $$1, $$2, $$3);
        }

        public List<ItemStack> getDrops(LootContext.Builder $$0) {
            return this.getBlock().getDrops(this.asState(), $$0);
        }

        public InteractionResult use(Level $$0, Player $$1, InteractionHand $$2, BlockHitResult $$3) {
            return this.getBlock().use(this.asState(), $$0, $$3.getBlockPos(), $$1, $$2, $$3);
        }

        public void attack(Level $$0, BlockPos $$1, Player $$2) {
            this.getBlock().attack(this.asState(), $$0, $$1, $$2);
        }

        public boolean isSuffocating(BlockGetter $$0, BlockPos $$1) {
            return this.isSuffocating.test(this.asState(), $$0, $$1);
        }

        public boolean isViewBlocking(BlockGetter $$0, BlockPos $$1) {
            return this.isViewBlocking.test(this.asState(), $$0, $$1);
        }

        public BlockState updateShape(Direction $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, BlockPos $$4) {
            return this.getBlock().updateShape(this.asState(), $$0, $$1, $$2, $$3, $$4);
        }

        public boolean isPathfindable(BlockGetter $$0, BlockPos $$1, PathComputationType $$2) {
            return this.getBlock().isPathfindable(this.asState(), $$0, $$1, $$2);
        }

        public boolean canBeReplaced(BlockPlaceContext $$0) {
            return this.getBlock().canBeReplaced(this.asState(), $$0);
        }

        public boolean canBeReplaced(Fluid $$0) {
            return this.getBlock().canBeReplaced(this.asState(), $$0);
        }

        public boolean canBeReplaced() {
            return this.getMaterial().isReplaceable();
        }

        public boolean canSurvive(LevelReader $$0, BlockPos $$1) {
            return this.getBlock().canSurvive(this.asState(), $$0, $$1);
        }

        public boolean hasPostProcess(BlockGetter $$0, BlockPos $$1) {
            return this.hasPostProcess.test(this.asState(), $$0, $$1);
        }

        @Nullable
        public MenuProvider getMenuProvider(Level $$0, BlockPos $$1) {
            return this.getBlock().getMenuProvider(this.asState(), $$0, $$1);
        }

        public boolean is(TagKey<Block> $$0) {
            return this.getBlock().builtInRegistryHolder().is($$0);
        }

        public boolean is(TagKey<Block> $$0, Predicate<BlockStateBase> $$1) {
            return this.is($$0) && $$1.test((Object)this);
        }

        public boolean is(HolderSet<Block> $$0) {
            return $$0.contains(this.getBlock().builtInRegistryHolder());
        }

        public Stream<TagKey<Block>> getTags() {
            return this.getBlock().builtInRegistryHolder().tags();
        }

        public boolean hasBlockEntity() {
            return this.getBlock() instanceof EntityBlock;
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockEntityType<T> $$1) {
            if (this.getBlock() instanceof EntityBlock) {
                return ((EntityBlock)((Object)this.getBlock())).getTicker($$0, this.asState(), $$1);
            }
            return null;
        }

        public boolean is(Block $$0) {
            return this.getBlock() == $$0;
        }

        public FluidState getFluidState() {
            return this.fluidState;
        }

        public boolean isRandomlyTicking() {
            return this.isRandomlyTicking;
        }

        public long getSeed(BlockPos $$0) {
            return this.getBlock().getSeed(this.asState(), $$0);
        }

        public SoundType getSoundType() {
            return this.getBlock().getSoundType(this.asState());
        }

        public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
            this.getBlock().onProjectileHit($$0, $$1, $$2, $$3);
        }

        public boolean isFaceSturdy(BlockGetter $$0, BlockPos $$1, Direction $$2) {
            return this.isFaceSturdy($$0, $$1, $$2, SupportType.FULL);
        }

        public boolean isFaceSturdy(BlockGetter $$0, BlockPos $$1, Direction $$2, SupportType $$3) {
            if (this.cache != null) {
                return this.cache.isFaceSturdy($$2, $$3);
            }
            return $$3.isSupporting(this.asState(), $$0, $$1, $$2);
        }

        public boolean isCollisionShapeFullBlock(BlockGetter $$0, BlockPos $$1) {
            if (this.cache != null) {
                return this.cache.isCollisionShapeFullBlock;
            }
            return this.getBlock().isCollisionShapeFullBlock(this.asState(), $$0, $$1);
        }

        protected abstract BlockState asState();

        public boolean requiresCorrectToolForDrops() {
            return this.requiresCorrectToolForDrops;
        }

        public OffsetType getOffsetType() {
            return this.offsetType;
        }

        public boolean shouldSpawnParticlesOnBreak() {
            return this.spawnParticlesOnBreak;
        }

        static final class Cache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
            protected final boolean solidRender;
            final boolean propagatesSkylightDown;
            final int lightBlock;
            @Nullable
            final VoxelShape[] occlusionShapes;
            protected final VoxelShape collisionShape;
            protected final boolean largeCollisionShape;
            private final boolean[] faceSturdy;
            protected final boolean isCollisionShapeFullBlock;

            Cache(BlockState $$02) {
                Block $$1 = $$02.getBlock();
                this.solidRender = $$02.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.propagatesSkylightDown = $$1.propagatesSkylightDown($$02, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.lightBlock = $$1.getLightBlock($$02, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                if (!$$02.canOcclude()) {
                    this.occlusionShapes = null;
                } else {
                    this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
                    VoxelShape $$2 = $$1.getOcclusionShape($$02, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                    Direction[] directionArray = DIRECTIONS;
                    int n = directionArray.length;
                    for (int i = 0; i < n; ++i) {
                        Direction $$3 = directionArray[i];
                        this.occlusionShapes[$$3.ordinal()] = Shapes.getFaceShape($$2, $$3);
                    }
                }
                this.collisionShape = $$1.getCollisionShape($$02, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
                if (!this.collisionShape.isEmpty() && $$02.getOffsetType() != OffsetType.NONE) {
                    throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", (Object[])new Object[]{BuiltInRegistries.BLOCK.getKey($$1)}));
                }
                this.largeCollisionShape = Arrays.stream((Object[])Direction.Axis.values()).anyMatch($$0 -> this.collisionShape.min((Direction.Axis)$$0) < 0.0 || this.collisionShape.max((Direction.Axis)$$0) > 1.0);
                this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];
                for (Direction $$4 : DIRECTIONS) {
                    for (SupportType $$5 : SupportType.values()) {
                        this.faceSturdy[Cache.getFaceSupportIndex((Direction)$$4, (SupportType)$$5)] = $$5.isSupporting($$02, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, $$4);
                    }
                }
                this.isCollisionShapeFullBlock = Block.isShapeFullBlock($$02.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
            }

            public boolean isFaceSturdy(Direction $$0, SupportType $$1) {
                return this.faceSturdy[Cache.getFaceSupportIndex($$0, $$1)];
            }

            private static int getFaceSupportIndex(Direction $$0, SupportType $$1) {
                return $$0.ordinal() * SUPPORT_TYPE_COUNT + $$1.ordinal();
            }
        }
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;

    }
}