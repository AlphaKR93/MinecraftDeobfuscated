/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.types.Type
 *  com.mojang.logging.LogUtils
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockEntityType<FurnaceBlockEntity> FURNACE = BlockEntityType.register("furnace", Builder.of(FurnaceBlockEntity::new, Blocks.FURNACE));
    public static final BlockEntityType<ChestBlockEntity> CHEST = BlockEntityType.register("chest", Builder.of(ChestBlockEntity::new, Blocks.CHEST));
    public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = BlockEntityType.register("trapped_chest", Builder.of(TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST));
    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = BlockEntityType.register("ender_chest", Builder.of(EnderChestBlockEntity::new, Blocks.ENDER_CHEST));
    public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = BlockEntityType.register("jukebox", Builder.of(JukeboxBlockEntity::new, Blocks.JUKEBOX));
    public static final BlockEntityType<DispenserBlockEntity> DISPENSER = BlockEntityType.register("dispenser", Builder.of(DispenserBlockEntity::new, Blocks.DISPENSER));
    public static final BlockEntityType<DropperBlockEntity> DROPPER = BlockEntityType.register("dropper", Builder.of(DropperBlockEntity::new, Blocks.DROPPER));
    public static final BlockEntityType<SignBlockEntity> SIGN = BlockEntityType.register("sign", Builder.of(SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN));
    public static final BlockEntityType<HangingSignBlockEntity> HANGING_SIGN = BlockEntityType.register("hanging_sign", Builder.of(HangingSignBlockEntity::new, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN));
    public static final BlockEntityType<SpawnerBlockEntity> MOB_SPAWNER = BlockEntityType.register("mob_spawner", Builder.of(SpawnerBlockEntity::new, Blocks.SPAWNER));
    public static final BlockEntityType<PistonMovingBlockEntity> PISTON = BlockEntityType.register("piston", Builder.of(PistonMovingBlockEntity::new, Blocks.MOVING_PISTON));
    public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = BlockEntityType.register("brewing_stand", Builder.of(BrewingStandBlockEntity::new, Blocks.BREWING_STAND));
    public static final BlockEntityType<EnchantmentTableBlockEntity> ENCHANTING_TABLE = BlockEntityType.register("enchanting_table", Builder.of(EnchantmentTableBlockEntity::new, Blocks.ENCHANTING_TABLE));
    public static final BlockEntityType<TheEndPortalBlockEntity> END_PORTAL = BlockEntityType.register("end_portal", Builder.of(TheEndPortalBlockEntity::new, Blocks.END_PORTAL));
    public static final BlockEntityType<BeaconBlockEntity> BEACON = BlockEntityType.register("beacon", Builder.of(BeaconBlockEntity::new, Blocks.BEACON));
    public static final BlockEntityType<SkullBlockEntity> SKULL = BlockEntityType.register("skull", Builder.of(SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD));
    public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = BlockEntityType.register("daylight_detector", Builder.of(DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR));
    public static final BlockEntityType<HopperBlockEntity> HOPPER = BlockEntityType.register("hopper", Builder.of(HopperBlockEntity::new, Blocks.HOPPER));
    public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = BlockEntityType.register("comparator", Builder.of(ComparatorBlockEntity::new, Blocks.COMPARATOR));
    public static final BlockEntityType<BannerBlockEntity> BANNER = BlockEntityType.register("banner", Builder.of(BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
    public static final BlockEntityType<StructureBlockEntity> STRUCTURE_BLOCK = BlockEntityType.register("structure_block", Builder.of(StructureBlockEntity::new, Blocks.STRUCTURE_BLOCK));
    public static final BlockEntityType<TheEndGatewayBlockEntity> END_GATEWAY = BlockEntityType.register("end_gateway", Builder.of(TheEndGatewayBlockEntity::new, Blocks.END_GATEWAY));
    public static final BlockEntityType<CommandBlockEntity> COMMAND_BLOCK = BlockEntityType.register("command_block", Builder.of(CommandBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
    public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = BlockEntityType.register("shulker_box", Builder.of(ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
    public static final BlockEntityType<BedBlockEntity> BED = BlockEntityType.register("bed", Builder.of(BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
    public static final BlockEntityType<ConduitBlockEntity> CONDUIT = BlockEntityType.register("conduit", Builder.of(ConduitBlockEntity::new, Blocks.CONDUIT));
    public static final BlockEntityType<BarrelBlockEntity> BARREL = BlockEntityType.register("barrel", Builder.of(BarrelBlockEntity::new, Blocks.BARREL));
    public static final BlockEntityType<SmokerBlockEntity> SMOKER = BlockEntityType.register("smoker", Builder.of(SmokerBlockEntity::new, Blocks.SMOKER));
    public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = BlockEntityType.register("blast_furnace", Builder.of(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE));
    public static final BlockEntityType<LecternBlockEntity> LECTERN = BlockEntityType.register("lectern", Builder.of(LecternBlockEntity::new, Blocks.LECTERN));
    public static final BlockEntityType<BellBlockEntity> BELL = BlockEntityType.register("bell", Builder.of(BellBlockEntity::new, Blocks.BELL));
    public static final BlockEntityType<JigsawBlockEntity> JIGSAW = BlockEntityType.register("jigsaw", Builder.of(JigsawBlockEntity::new, Blocks.JIGSAW));
    public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = BlockEntityType.register("campfire", Builder.of(CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
    public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = BlockEntityType.register("beehive", Builder.of(BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
    public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = BlockEntityType.register("sculk_sensor", Builder.of(SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR));
    public static final BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST = BlockEntityType.register("sculk_catalyst", Builder.of(SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST));
    public static final BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER = BlockEntityType.register("sculk_shrieker", Builder.of(SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER));
    public static final BlockEntityType<ChiseledBookShelfBlockEntity> CHISELED_BOOKSHELF = BlockEntityType.register("chiseled_bookshelf", Builder.of(ChiseledBookShelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF));
    private final BlockEntitySupplier<? extends T> factory;
    private final Set<Block> validBlocks;
    private final Type<?> dataType;

    @Nullable
    public static ResourceLocation getKey(BlockEntityType<?> $$0) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey($$0);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String $$0, Builder<T> $$1) {
        if ($$1.validBlocks.isEmpty()) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)$$0);
        }
        Type<?> $$2 = Util.fetchChoiceType(References.BLOCK_ENTITY, $$0);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, $$0, $$1.build($$2));
    }

    public BlockEntityType(BlockEntitySupplier<? extends T> $$0, Set<Block> $$1, Type<?> $$2) {
        this.factory = $$0;
        this.validBlocks = $$1;
        this.dataType = $$2;
    }

    @Nullable
    public T create(BlockPos $$0, BlockState $$1) {
        return this.factory.create($$0, $$1);
    }

    public boolean isValid(BlockState $$0) {
        return this.validBlocks.contains((Object)$$0.getBlock());
    }

    @Nullable
    public T getBlockEntity(BlockGetter $$0, BlockPos $$1) {
        BlockEntity $$2 = $$0.getBlockEntity($$1);
        if ($$2 == null || $$2.getType() != this) {
            return null;
        }
        return (T)$$2;
    }

    public static final class Builder<T extends BlockEntity> {
        private final BlockEntitySupplier<? extends T> factory;
        final Set<Block> validBlocks;

        private Builder(BlockEntitySupplier<? extends T> $$0, Set<Block> $$1) {
            this.factory = $$0;
            this.validBlocks = $$1;
        }

        public static <T extends BlockEntity> Builder<T> of(BlockEntitySupplier<? extends T> $$0, Block ... $$1) {
            return new Builder<T>($$0, (Set<Block>)ImmutableSet.copyOf((Object[])$$1));
        }

        public BlockEntityType<T> build(Type<?> $$0) {
            return new BlockEntityType<T>(this.factory, this.validBlocks, $$0);
        }
    }

    @FunctionalInterface
    static interface BlockEntitySupplier<T extends BlockEntity> {
        public T create(BlockPos var1, BlockState var2);
    }
}