/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.Deprecated
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.ThreadLocal
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class Block
extends BlockBehaviour
implements ItemLike {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Holder.Reference<Block> builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder(this);
    public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = new IdMapper();
    private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build((CacheLoader)new CacheLoader<VoxelShape, Boolean>(){

        public Boolean load(VoxelShape $$0) {
            return !Shapes.joinIsNotEmpty(Shapes.block(), $$0, BooleanOp.NOT_SAME);
        }
    });
    public static final int UPDATE_NEIGHBORS = 1;
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_INVISIBLE = 4;
    public static final int UPDATE_IMMEDIATE = 8;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = 32;
    public static final int UPDATE_MOVE_BY_PISTON = 64;
    public static final int UPDATE_SUPPRESS_LIGHT = 128;
    public static final int UPDATE_NONE = 4;
    public static final int UPDATE_ALL = 3;
    public static final int UPDATE_ALL_IMMEDIATE = 11;
    public static final float INDESTRUCTIBLE = -1.0f;
    public static final float INSTANT = 0.0f;
    public static final int UPDATE_LIMIT = 512;
    protected final StateDefinition<Block, BlockState> stateDefinition;
    private BlockState defaultBlockState;
    @Nullable
    private String descriptionId;
    @Nullable
    private Item item;
    private static final int CACHE_SIZE = 2048;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<BlockStatePairKey> $$0 = new Object2ByteLinkedOpenHashMap<BlockStatePairKey>(2048, 0.25f){

            protected void rehash(int $$0) {
            }
        };
        $$0.defaultReturnValue((byte)127);
        return $$0;
    });

    public static int getId(@Nullable BlockState $$0) {
        if ($$0 == null) {
            return 0;
        }
        int $$1 = BLOCK_STATE_REGISTRY.getId($$0);
        return $$1 == -1 ? 0 : $$1;
    }

    public static BlockState stateById(int $$0) {
        BlockState $$1 = BLOCK_STATE_REGISTRY.byId($$0);
        return $$1 == null ? Blocks.AIR.defaultBlockState() : $$1;
    }

    public static Block byItem(@Nullable Item $$0) {
        if ($$0 instanceof BlockItem) {
            return ((BlockItem)$$0).getBlock();
        }
        return Blocks.AIR;
    }

    public static BlockState pushEntitiesUp(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3) {
        VoxelShape $$4 = Shapes.joinUnoptimized($$0.getCollisionShape($$2, $$3), $$1.getCollisionShape($$2, $$3), BooleanOp.ONLY_SECOND).move($$3.getX(), $$3.getY(), $$3.getZ());
        if ($$4.isEmpty()) {
            return $$1;
        }
        List $$5 = $$2.getEntities(null, $$4.bounds());
        for (Entity $$6 : $$5) {
            double $$7 = Shapes.collide(Direction.Axis.Y, $$6.getBoundingBox().move(0.0, 1.0, 0.0), (Iterable<VoxelShape>)List.of((Object)$$4), -1.0);
            $$6.teleportRelative(0.0, 1.0 + $$7, 0.0);
        }
        return $$1;
    }

    public static VoxelShape box(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        return Shapes.box($$0 / 16.0, $$1 / 16.0, $$2 / 16.0, $$3 / 16.0, $$4 / 16.0, $$5 / 16.0);
    }

    public static BlockState updateFromNeighbourShapes(BlockState $$0, LevelAccessor $$1, BlockPos $$2) {
        BlockState $$3 = $$0;
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Direction $$5 : UPDATE_SHAPE_ORDER) {
            $$4.setWithOffset((Vec3i)$$2, $$5);
            $$3 = $$3.updateShape($$5, $$1.getBlockState($$4), $$1, $$2, $$4);
        }
        return $$3;
    }

    public static void updateOrDestroy(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, int $$4) {
        Block.updateOrDestroy($$0, $$1, $$2, $$3, $$4, 512);
    }

    public static void updateOrDestroy(BlockState $$0, BlockState $$1, LevelAccessor $$2, BlockPos $$3, int $$4, int $$5) {
        if ($$1 != $$0) {
            if ($$1.isAir()) {
                if (!$$2.isClientSide()) {
                    $$2.destroyBlock($$3, ($$4 & 0x20) == 0, null, $$5);
                }
            } else {
                $$2.setBlock($$3, $$1, $$4 & 0xFFFFFFDF, $$5);
            }
        }
    }

    public Block(BlockBehaviour.Properties $$0) {
        super($$0);
        String $$2;
        StateDefinition.Builder<Block, BlockState> $$1 = new StateDefinition.Builder<Block, BlockState>(this);
        this.createBlockStateDefinition($$1);
        this.stateDefinition = $$1.create((Function<Block, BlockState>)((Function)Block::defaultBlockState), BlockState::new);
        this.registerDefaultState(this.stateDefinition.any());
        if (SharedConstants.IS_RUNNING_IN_IDE && !($$2 = this.getClass().getSimpleName()).endsWith("Block")) {
            LOGGER.error("Block classes should end with Block and {} doesn't.", (Object)$$2);
        }
    }

    public static boolean isExceptionForConnection(BlockState $$0) {
        return $$0.getBlock() instanceof LeavesBlock || $$0.is(Blocks.BARRIER) || $$0.is(Blocks.CARVED_PUMPKIN) || $$0.is(Blocks.JACK_O_LANTERN) || $$0.is(Blocks.MELON) || $$0.is(Blocks.PUMPKIN) || $$0.is(BlockTags.SHULKER_BOXES);
    }

    public boolean isRandomlyTicking(BlockState $$0) {
        return this.isRandomlyTicking;
    }

    public static boolean shouldRenderFace(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3, BlockPos $$4) {
        BlockState $$5 = $$1.getBlockState($$4);
        if ($$0.skipRendering($$5, $$3)) {
            return false;
        }
        if ($$5.canOcclude()) {
            BlockStatePairKey $$6 = new BlockStatePairKey($$0, $$5, $$3);
            Object2ByteLinkedOpenHashMap $$7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
            byte $$8 = $$7.getAndMoveToFirst((Object)$$6);
            if ($$8 != 127) {
                return $$8 != 0;
            }
            VoxelShape $$9 = $$0.getFaceOcclusionShape($$1, $$2, $$3);
            if ($$9.isEmpty()) {
                return true;
            }
            VoxelShape $$10 = $$5.getFaceOcclusionShape($$1, $$4, $$3.getOpposite());
            boolean $$11 = Shapes.joinIsNotEmpty($$9, $$10, BooleanOp.ONLY_FIRST);
            if ($$7.size() == 2048) {
                $$7.removeLastByte();
            }
            $$7.putAndMoveToFirst((Object)$$6, (byte)($$11 ? 1 : 0));
            return $$11;
        }
        return true;
    }

    public static boolean canSupportRigidBlock(BlockGetter $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).isFaceSturdy($$0, $$1, Direction.UP, SupportType.RIGID);
    }

    public static boolean canSupportCenter(LevelReader $$0, BlockPos $$1, Direction $$2) {
        BlockState $$3 = $$0.getBlockState($$1);
        if ($$2 == Direction.DOWN && $$3.is(BlockTags.UNSTABLE_BOTTOM_CENTER)) {
            return false;
        }
        return $$3.isFaceSturdy($$0, $$1, $$2, SupportType.CENTER);
    }

    public static boolean isFaceFull(VoxelShape $$0, Direction $$1) {
        VoxelShape $$2 = $$0.getFaceShape($$1);
        return Block.isShapeFullBlock($$2);
    }

    public static boolean isShapeFullBlock(VoxelShape $$0) {
        return (Boolean)SHAPE_FULL_BLOCK_CACHE.getUnchecked((Object)$$0);
    }

    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return !Block.isShapeFullBlock($$0.getShape($$1, $$2)) && $$0.getFluidState().isEmpty();
    }

    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
    }

    public void destroy(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
    }

    public static List<ItemStack> getDrops(BlockState $$0, ServerLevel $$1, BlockPos $$2, @Nullable BlockEntity $$3) {
        LootContext.Builder $$4 = new LootContext.Builder($$1).withRandom($$1.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$2)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$3);
        return $$0.getDrops($$4);
    }

    public static List<ItemStack> getDrops(BlockState $$0, ServerLevel $$1, BlockPos $$2, @Nullable BlockEntity $$3, @Nullable Entity $$4, ItemStack $$5) {
        LootContext.Builder $$6 = new LootContext.Builder($$1).withRandom($$1.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf($$2)).withParameter(LootContextParams.TOOL, $$5).withOptionalParameter(LootContextParams.THIS_ENTITY, $$4).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$3);
        return $$0.getDrops($$6);
    }

    public static void dropResources(BlockState $$0, LootContext.Builder $$1) {
        ServerLevel $$22 = $$1.getLevel();
        BlockPos $$3 = new BlockPos($$1.getParameter(LootContextParams.ORIGIN));
        $$0.getDrops($$1).forEach($$2 -> Block.popResource((Level)$$22, $$3, $$2));
        $$0.spawnAfterBreak($$22, $$3, ItemStack.EMPTY, true);
    }

    public static void dropResources(BlockState $$0, Level $$1, BlockPos $$22) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, null).forEach($$2 -> Block.popResource($$1, $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, ItemStack.EMPTY, true);
        }
    }

    public static void dropResources(BlockState $$0, LevelAccessor $$1, BlockPos $$22, @Nullable BlockEntity $$3) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, $$3).forEach($$2 -> Block.popResource((Level)((ServerLevel)$$1), $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, ItemStack.EMPTY, true);
        }
    }

    public static void dropResources(BlockState $$0, Level $$1, BlockPos $$22, @Nullable BlockEntity $$3, Entity $$4, ItemStack $$5) {
        if ($$1 instanceof ServerLevel) {
            Block.getDrops($$0, (ServerLevel)$$1, $$22, $$3, $$4, $$5).forEach($$2 -> Block.popResource($$1, $$22, $$2));
            $$0.spawnAfterBreak((ServerLevel)$$1, $$22, $$5, true);
        }
    }

    public static void popResource(Level $$0, BlockPos $$1, ItemStack $$2) {
        float $$3 = EntityType.ITEM.getHeight() / 2.0f;
        double $$4 = (double)((float)$$1.getX() + 0.5f) + Mth.nextDouble($$0.random, -0.25, 0.25);
        double $$5 = (double)((float)$$1.getY() + 0.5f) + Mth.nextDouble($$0.random, -0.25, 0.25) - (double)$$3;
        double $$6 = (double)((float)$$1.getZ() + 0.5f) + Mth.nextDouble($$0.random, -0.25, 0.25);
        Block.popResource($$0, (Supplier<ItemEntity>)((Supplier)() -> new ItemEntity($$0, $$4, $$5, $$6, $$2)), $$2);
    }

    public static void popResourceFromFace(Level $$0, BlockPos $$1, Direction $$2, ItemStack $$3) {
        int $$4 = $$2.getStepX();
        int $$5 = $$2.getStepY();
        int $$6 = $$2.getStepZ();
        float $$7 = EntityType.ITEM.getWidth() / 2.0f;
        float $$8 = EntityType.ITEM.getHeight() / 2.0f;
        double $$9 = (double)((float)$$1.getX() + 0.5f) + ($$4 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)((float)$$4 * (0.5f + $$7)));
        double $$10 = (double)((float)$$1.getY() + 0.5f) + ($$5 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)((float)$$5 * (0.5f + $$8))) - (double)$$8;
        double $$11 = (double)((float)$$1.getZ() + 0.5f) + ($$6 == 0 ? Mth.nextDouble($$0.random, -0.25, 0.25) : (double)((float)$$6 * (0.5f + $$7)));
        double $$12 = $$4 == 0 ? Mth.nextDouble($$0.random, -0.1, 0.1) : (double)$$4 * 0.1;
        double $$13 = $$5 == 0 ? Mth.nextDouble($$0.random, 0.0, 0.1) : (double)$$5 * 0.1 + 0.1;
        double $$14 = $$6 == 0 ? Mth.nextDouble($$0.random, -0.1, 0.1) : (double)$$6 * 0.1;
        Block.popResource($$0, (Supplier<ItemEntity>)((Supplier)() -> new ItemEntity($$0, $$9, $$10, $$11, $$3, $$12, $$13, $$14)), $$3);
    }

    private static void popResource(Level $$0, Supplier<ItemEntity> $$1, ItemStack $$2) {
        if ($$0.isClientSide || $$2.isEmpty() || !$$0.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }
        ItemEntity $$3 = (ItemEntity)$$1.get();
        $$3.setDefaultPickUpDelay();
        $$0.addFreshEntity($$3);
    }

    protected void popExperience(ServerLevel $$0, BlockPos $$1, int $$2) {
        if ($$0.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ExperienceOrb.award($$0, Vec3.atCenterOf($$1), $$2);
        }
    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    public void wasExploded(Level $$0, BlockPos $$1, Explosion $$2) {
    }

    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.defaultBlockState();
    }

    public void playerDestroy(Level $$0, Player $$1, BlockPos $$2, BlockState $$3, @Nullable BlockEntity $$4, ItemStack $$5) {
        $$1.awardStat(Stats.BLOCK_MINED.get(this));
        $$1.causeFoodExhaustion(0.005f);
        Block.dropResources($$3, $$0, $$2, $$4, $$1, $$5);
    }

    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
    }

    public boolean isPossibleToRespawnInThis() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    public MutableComponent getName() {
        return Component.translatable(this.getDescriptionId());
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("block", BuiltInRegistries.BLOCK.getKey(this));
        }
        return this.descriptionId;
    }

    public void fallOn(Level $$0, BlockState $$1, BlockPos $$2, Entity $$3, float $$4) {
        $$3.causeFallDamage($$4, 1.0f, DamageSource.FALL);
    }

    public void updateEntityAfterFallOn(BlockGetter $$0, Entity $$1) {
        $$1.setDeltaMovement($$1.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }

    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack(this);
    }

    public float getFriction() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    protected void spawnDestroyParticles(Level $$0, Player $$1, BlockPos $$2, BlockState $$3) {
        $$0.levelEvent($$1, 2001, $$2, Block.getId($$3));
    }

    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        this.spawnDestroyParticles($$0, $$3, $$1, $$2);
        if ($$2.is(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.angerNearbyPiglins($$3, false);
        }
        $$0.gameEvent(GameEvent.BLOCK_DESTROY, $$1, GameEvent.Context.of($$3, $$2));
    }

    public void handlePrecipitation(BlockState $$0, Level $$1, BlockPos $$2, Biome.Precipitation $$3) {
    }

    public boolean dropFromExplosion(Explosion $$0) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
    }

    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateDefinition;
    }

    protected final void registerDefaultState(BlockState $$0) {
        this.defaultBlockState = $$0;
    }

    public final BlockState defaultBlockState() {
        return this.defaultBlockState;
    }

    public final BlockState withPropertiesOf(BlockState $$0) {
        BlockState $$1 = this.defaultBlockState();
        for (Property $$2 : $$0.getBlock().getStateDefinition().getProperties()) {
            if (!$$1.hasProperty($$2)) continue;
            $$1 = Block.copyProperty($$0, $$1, $$2);
        }
        return $$1;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState $$0, BlockState $$1, Property<T> $$2) {
        return (BlockState)$$1.setValue($$2, $$0.getValue($$2));
    }

    public SoundType getSoundType(BlockState $$0) {
        return this.soundType;
    }

    @Override
    public Item asItem() {
        if (this.item == null) {
            this.item = Item.byBlock(this);
        }
        return this.item;
    }

    public boolean hasDynamicShape() {
        return this.dynamicShape;
    }

    public String toString() {
        return "Block{" + BuiltInRegistries.BLOCK.getKey(this) + "}";
    }

    public void appendHoverText(ItemStack $$0, @Nullable BlockGetter $$1, List<Component> $$2, TooltipFlag $$3) {
    }

    @Override
    protected Block asBlock() {
        return this;
    }

    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> $$0) {
        return (ImmutableMap)this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap((Function)Function.identity(), $$0));
    }

    @Deprecated
    public Holder.Reference<Block> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    protected void tryDropExperience(ServerLevel $$0, BlockPos $$1, ItemStack $$2, IntProvider $$3) {
        int $$4;
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, $$2) == 0 && ($$4 = $$3.sample($$0.random)) > 0) {
            this.popExperience($$0, $$1, $$4);
        }
    }

    public static final class BlockStatePairKey {
        private final BlockState first;
        private final BlockState second;
        private final Direction direction;

        public BlockStatePairKey(BlockState $$0, BlockState $$1, Direction $$2) {
            this.first = $$0;
            this.second = $$1;
            this.direction = $$2;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if (!($$0 instanceof BlockStatePairKey)) {
                return false;
            }
            BlockStatePairKey $$1 = (BlockStatePairKey)$$0;
            return this.first == $$1.first && this.second == $$1.second && this.direction == $$1.direction;
        }

        public int hashCode() {
            int $$0 = this.first.hashCode();
            $$0 = 31 * $$0 + this.second.hashCode();
            $$0 = 31 * $$0 + this.direction.hashCode();
            return $$0;
        }
    }
}