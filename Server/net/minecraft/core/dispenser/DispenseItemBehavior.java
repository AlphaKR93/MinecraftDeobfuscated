/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Predicate
 *  org.slf4j.Logger
 */
package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public interface DispenseItemBehavior {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DispenseItemBehavior NOOP = ($$0, $$1) -> $$1;

    public ItemStack dispense(BlockSource var1, ItemStack var2);

    public static void bootStrap() {
        DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$1, ItemStack $$2) {
                Arrow $$3 = new Arrow($$0, $$1.x(), $$1.y(), $$1.z());
                $$3.pickup = AbstractArrow.Pickup.ALLOWED;
                return $$3;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$1, ItemStack $$2) {
                Arrow $$3 = new Arrow($$0, $$1.x(), $$1.y(), $$1.z());
                $$3.setEffectsFromItem($$2);
                $$3.pickup = AbstractArrow.Pickup.ALLOWED;
                return $$3;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$1, ItemStack $$2) {
                SpectralArrow $$3 = new SpectralArrow($$0, $$1.x(), $$1.y(), $$1.z());
                $$3.pickup = AbstractArrow.Pickup.ALLOWED;
                return $$3;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$12, ItemStack $$2) {
                return Util.make(new ThrownEgg($$0, $$12.x(), $$12.y(), $$12.z()), $$1 -> $$1.setItem($$2));
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$12, ItemStack $$2) {
                return Util.make(new Snowball($$0, $$12.x(), $$12.y(), $$12.z()), $$1 -> $$1.setItem($$2));
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior(){

            @Override
            protected Projectile getProjectile(Level $$0, Position $$12, ItemStack $$2) {
                return Util.make(new ThrownExperienceBottle($$0, $$12.x(), $$12.y(), $$12.z()), $$1 -> $$1.setItem($$2));
            }

            @Override
            protected float getUncertainty() {
                return super.getUncertainty() * 0.5f;
            }

            @Override
            protected float getPower() {
                return super.getPower() * 1.25f;
            }
        });
        DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior(){

            @Override
            public ItemStack dispense(BlockSource $$0, ItemStack $$1) {
                return new AbstractProjectileDispenseBehavior(){

                    @Override
                    protected Projectile getProjectile(Level $$0, Position $$12, ItemStack $$2) {
                        return Util.make(new ThrownPotion($$0, $$12.x(), $$12.y(), $$12.z()), $$1 -> $$1.setItem($$2));
                    }

                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense($$0, $$1);
            }
        });
        DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior(){

            @Override
            public ItemStack dispense(BlockSource $$0, ItemStack $$1) {
                return new AbstractProjectileDispenseBehavior(){

                    @Override
                    protected Projectile getProjectile(Level $$0, Position $$12, ItemStack $$2) {
                        return Util.make(new ThrownPotion($$0, $$12.x(), $$12.y(), $$12.z()), $$1 -> $$1.setItem($$2));
                    }

                    @Override
                    protected float getUncertainty() {
                        return super.getUncertainty() * 0.5f;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25f;
                    }
                }.dispense($$0, $$1);
            }
        });
        DefaultDispenseItemBehavior $$0 = new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> $$3 = ((SpawnEggItem)$$1.getItem()).getType($$1.getTag());
                try {
                    $$3.spawn($$0.getLevel(), $$1, null, (BlockPos)$$0.getPos().relative($$2), MobSpawnType.DISPENSER, $$2 != Direction.UP, false);
                }
                catch (Exception $$4) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", (Object)$$0.getPos(), (Object)$$4);
                    return ItemStack.EMPTY;
                }
                $$1.shrink(1);
                $$0.getLevel().gameEvent(null, GameEvent.ENTITY_PLACE, $$0.getPos());
                return $$1;
            }
        };
        for (SpawnEggItem $$1 : SpawnEggItem.eggs()) {
            DispenserBlock.registerBehavior($$1, $$0);
        }
        DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                Vec3i $$3 = $$0.getPos().relative($$2);
                ServerLevel $$4 = $$0.getLevel();
                ArmorStand $$5 = new ArmorStand($$4, (double)$$3.getX() + 0.5, $$3.getY(), (double)$$3.getZ() + 0.5);
                EntityType.updateCustomEntityTag($$4, null, $$5, $$1.getTag());
                $$5.setYRot($$2.toYRot());
                $$4.addFreshEntity($$5);
                $$1.shrink(1);
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$02, ItemStack $$1) {
                Vec3i $$2 = $$02.getPos().relative($$02.getBlockState().getValue(DispenserBlock.FACING));
                List $$3 = $$02.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB((BlockPos)$$2), $$0 -> {
                    if ($$0 instanceof Saddleable) {
                        Saddleable $$1 = (Saddleable)((Object)$$0);
                        return !$$1.isSaddled() && $$1.isSaddleable();
                    }
                    return false;
                });
                if (!$$3.isEmpty()) {
                    ((Saddleable)$$3.get(0)).equipSaddle(SoundSource.BLOCKS);
                    $$1.shrink(1);
                    this.setSuccess(true);
                    return $$1;
                }
                return super.execute($$02, $$1);
            }
        });
        OptionalDispenseItemBehavior $$2 = new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$02, ItemStack $$1) {
                Vec3i $$2 = $$02.getPos().relative($$02.getBlockState().getValue(DispenserBlock.FACING));
                List $$3 = $$02.getLevel().getEntitiesOfClass(AbstractHorse.class, new AABB((BlockPos)$$2), $$0 -> $$0.isAlive() && $$0.canWearArmor());
                for (AbstractHorse $$4 : $$3) {
                    if (!$$4.isArmor($$1) || $$4.isWearingArmor() || !$$4.isTamed()) continue;
                    $$4.getSlot(401).set($$1.split(1));
                    this.setSuccess(true);
                    return $$1;
                }
                return super.execute($$02, $$1);
            }
        };
        DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, $$2);
        DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, $$2);
        DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, $$2);
        DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, $$2);
        DispenserBlock.registerBehavior(Items.WHITE_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.ORANGE_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.CYAN_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.BLUE_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.BROWN_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.BLACK_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.GRAY_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.GREEN_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.LIME_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.PINK_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.PURPLE_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.RED_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.YELLOW_CARPET, $$2);
        DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$02, ItemStack $$1) {
                Vec3i $$2 = $$02.getPos().relative($$02.getBlockState().getValue(DispenserBlock.FACING));
                List $$3 = $$02.getLevel().getEntitiesOfClass(AbstractChestedHorse.class, new AABB((BlockPos)$$2), $$0 -> $$0.isAlive() && !$$0.hasChest());
                for (AbstractChestedHorse $$4 : $$3) {
                    if (!$$4.isTamed() || !$$4.getSlot(499).set($$1)) continue;
                    $$1.shrink(1);
                    this.setSuccess(true);
                    return $$1;
                }
                return super.execute($$02, $$1);
            }
        });
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                FireworkRocketEntity $$3 = new FireworkRocketEntity((Level)$$0.getLevel(), $$1, $$0.x(), $$0.y(), $$0.x(), true);
                DispenseItemBehavior.setEntityPokingOutOfBlock($$0, $$3, $$2);
                $$3.shoot($$2.getStepX(), $$2.getStepY(), $$2.getStepZ(), 0.5f, 1.0f);
                $$0.getLevel().addFreshEntity($$3);
                $$1.shrink(1);
                return $$1;
            }

            @Override
            protected void playSound(BlockSource $$0) {
                $$0.getLevel().levelEvent(1004, $$0.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$12) {
                Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                Position $$3 = DispenserBlock.getDispensePosition($$0);
                double $$4 = $$3.x() + (double)((float)$$2.getStepX() * 0.3f);
                double $$5 = $$3.y() + (double)((float)$$2.getStepY() * 0.3f);
                double $$6 = $$3.z() + (double)((float)$$2.getStepZ() * 0.3f);
                ServerLevel $$7 = $$0.getLevel();
                RandomSource $$8 = $$7.random;
                double $$9 = $$8.triangle($$2.getStepX(), 0.11485000000000001);
                double $$10 = $$8.triangle($$2.getStepY(), 0.11485000000000001);
                double $$11 = $$8.triangle($$2.getStepZ(), 0.11485000000000001);
                SmallFireball $$122 = new SmallFireball($$7, $$4, $$5, $$6, $$9, $$10, $$11);
                $$7.addFreshEntity(Util.make($$122, $$1 -> $$1.setItem($$12)));
                $$12.shrink(1);
                return $$12;
            }

            @Override
            protected void playSound(BlockSource $$0) {
                $$0.getLevel().levelEvent(1018, $$0.getPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
        DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
        DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE));
        DispenserBlock.registerBehavior(Items.BAMBOO_RAFT, new BoatDispenseItemBehavior(Boat.Type.BAMBOO));
        DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK, true));
        DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE, true));
        DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH, true));
        DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE, true));
        DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK, true));
        DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA, true));
        DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenseItemBehavior(Boat.Type.MANGROVE, true));
        DispenserBlock.registerBehavior(Items.BAMBOO_CHEST_RAFT, new BoatDispenseItemBehavior(Boat.Type.BAMBOO, true));
        DefaultDispenseItemBehavior $$3 = new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                DispensibleContainerItem $$2 = (DispensibleContainerItem)((Object)$$1.getItem());
                Vec3i $$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                ServerLevel $$4 = $$0.getLevel();
                if ($$2.emptyContents(null, $$4, (BlockPos)$$3, null)) {
                    $$2.checkExtraContent(null, $$4, $$1, (BlockPos)$$3);
                    return new ItemStack(Items.BUCKET);
                }
                return this.defaultDispenseItemBehavior.dispense($$0, $$1);
            }
        };
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.SALMON_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.COD_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, $$3);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            /*
             * WARNING - void declaration
             */
            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                void $$8;
                ItemStack $$6;
                Vec3i $$3;
                ServerLevel $$2 = $$0.getLevel();
                BlockState $$4 = $$2.getBlockState((BlockPos)($$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING))));
                Block $$5 = $$4.getBlock();
                if ($$5 instanceof BucketPickup) {
                    $$6 = ((BucketPickup)((Object)$$5)).pickupBlock($$2, (BlockPos)$$3, $$4);
                    if ($$6.isEmpty()) {
                        return super.execute($$0, $$1);
                    }
                } else {
                    return super.execute($$0, $$1);
                }
                $$2.gameEvent(null, GameEvent.FLUID_PICKUP, (BlockPos)$$3);
                Item $$7 = $$6.getItem();
                $$1.shrink(1);
                if ($$1.isEmpty()) {
                    return new ItemStack((ItemLike)$$8);
                }
                if (((DispenserBlockEntity)$$0.getEntity()).addItem(new ItemStack((ItemLike)$$8)) < 0) {
                    this.defaultDispenseItemBehavior.dispense($$0, new ItemStack((ItemLike)$$8));
                }
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                ServerLevel $$2 = $$0.getLevel();
                this.setSuccess(true);
                Direction $$3 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                Vec3i $$4 = $$0.getPos().relative($$3);
                BlockState $$5 = $$2.getBlockState((BlockPos)$$4);
                if (BaseFireBlock.canBePlacedAt($$2, (BlockPos)$$4, $$3)) {
                    $$2.setBlockAndUpdate((BlockPos)$$4, BaseFireBlock.getState($$2, (BlockPos)$$4));
                    $$2.gameEvent(null, GameEvent.BLOCK_PLACE, (BlockPos)$$4);
                } else if (CampfireBlock.canLight($$5) || CandleBlock.canLight($$5) || CandleCakeBlock.canLight($$5)) {
                    $$2.setBlockAndUpdate((BlockPos)$$4, (BlockState)$$5.setValue(BlockStateProperties.LIT, true));
                    $$2.gameEvent(null, GameEvent.BLOCK_CHANGE, (BlockPos)$$4);
                } else if ($$5.getBlock() instanceof TntBlock) {
                    TntBlock.explode($$2, (BlockPos)$$4);
                    $$2.removeBlock((BlockPos)$$4, false);
                } else {
                    this.setSuccess(false);
                }
                if (this.isSuccess() && $$1.hurt(1, $$2.random, null)) {
                    $$1.setCount(0);
                }
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                this.setSuccess(true);
                ServerLevel $$2 = $$0.getLevel();
                Vec3i $$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                if (BoneMealItem.growCrop($$1, $$2, (BlockPos)$$3) || BoneMealItem.growWaterPlant($$1, $$2, (BlockPos)$$3, null)) {
                    if (!$$2.isClientSide) {
                        $$2.levelEvent(1505, (BlockPos)$$3, 0);
                    }
                } else {
                    this.setSuccess(false);
                }
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                ServerLevel $$2 = $$0.getLevel();
                Vec3i $$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                PrimedTnt $$4 = new PrimedTnt($$2, (double)$$3.getX() + 0.5, $$3.getY(), (double)$$3.getZ() + 0.5, null);
                $$2.addFreshEntity($$4);
                $$2.playSound(null, $$4.getX(), $$4.getY(), $$4.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$2.gameEvent(null, GameEvent.ENTITY_PLACE, (BlockPos)$$3);
                $$1.shrink(1);
                return $$1;
            }
        });
        OptionalDispenseItemBehavior $$4 = new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                this.setSuccess(ArmorItem.dispenseArmor($$0, $$1));
                return $$1;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, $$4);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, $$4);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, $$4);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, $$4);
        DispenserBlock.registerBehavior(Items.PIGLIN_HEAD, $$4);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, $$4);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                ServerLevel $$2 = $$0.getLevel();
                Direction $$3 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                Vec3i $$4 = $$0.getPos().relative($$3);
                if ($$2.isEmptyBlock((BlockPos)$$4) && WitherSkullBlock.canSpawnMob($$2, (BlockPos)$$4, $$1)) {
                    $$2.setBlock((BlockPos)$$4, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, RotationSegment.convertToSegment($$3)), 3);
                    $$2.gameEvent(null, GameEvent.BLOCK_PLACE, (BlockPos)$$4);
                    BlockEntity $$5 = $$2.getBlockEntity((BlockPos)$$4);
                    if ($$5 instanceof SkullBlockEntity) {
                        WitherSkullBlock.checkSpawn($$2, (BlockPos)$$4, (SkullBlockEntity)$$5);
                    }
                    $$1.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor($$0, $$1));
                }
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior(){

            @Override
            protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
                ServerLevel $$2 = $$0.getLevel();
                Vec3i $$3 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                CarvedPumpkinBlock $$4 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                if ($$2.isEmptyBlock((BlockPos)$$3) && $$4.canSpawnGolem($$2, (BlockPos)$$3)) {
                    if (!$$2.isClientSide) {
                        $$2.setBlock((BlockPos)$$3, $$4.defaultBlockState(), 3);
                        $$2.gameEvent(null, GameEvent.BLOCK_PLACE, (BlockPos)$$3);
                    }
                    $$1.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor($$0, $$1));
                }
                return $$1;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());
        for (DyeColor $$5 : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor($$5).asItem(), new ShulkerBoxDispenseBehavior());
        }
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            private ItemStack takeLiquid(BlockSource $$0, ItemStack $$1, ItemStack $$2) {
                $$1.shrink(1);
                if ($$1.isEmpty()) {
                    $$0.getLevel().gameEvent(null, GameEvent.FLUID_PICKUP, $$0.getPos());
                    return $$2.copy();
                }
                if (((DispenserBlockEntity)$$0.getEntity()).addItem($$2.copy()) < 0) {
                    this.defaultDispenseItemBehavior.dispense($$0, $$2.copy());
                }
                return $$1;
            }

            @Override
            public ItemStack execute(BlockSource $$02, ItemStack $$1) {
                this.setSuccess(false);
                ServerLevel $$2 = $$02.getLevel();
                Vec3i $$3 = $$02.getPos().relative($$02.getBlockState().getValue(DispenserBlock.FACING));
                BlockState $$4 = $$2.getBlockState((BlockPos)$$3);
                if ($$4.is(BlockTags.BEEHIVES, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(BeehiveBlock.HONEY_LEVEL) && $$0.getBlock() instanceof BeehiveBlock)) && $$4.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)$$4.getBlock()).releaseBeesAndResetHoneyLevel($$2, $$4, (BlockPos)$$3, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.takeLiquid($$02, $$1, new ItemStack(Items.HONEY_BOTTLE));
                }
                if ($$2.getFluidState((BlockPos)$$3).is(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid($$02, $$1, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                }
                return super.execute($$02, $$1);
            }
        });
        DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                Direction $$2 = $$0.getBlockState().getValue(DispenserBlock.FACING);
                Vec3i $$3 = $$0.getPos().relative($$2);
                ServerLevel $$4 = $$0.getLevel();
                BlockState $$5 = $$4.getBlockState((BlockPos)$$3);
                this.setSuccess(true);
                if ($$5.is(Blocks.RESPAWN_ANCHOR)) {
                    if ($$5.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                        RespawnAnchorBlock.charge($$4, (BlockPos)$$3, $$5);
                        $$1.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return $$1;
                }
                return super.execute($$0, $$1);
            }
        });
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
        DispenserBlock.registerBehavior(Items.HONEYCOMB, new OptionalDispenseItemBehavior(){

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                Vec3i $$2 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                ServerLevel $$3 = $$0.getLevel();
                BlockState $$4 = $$3.getBlockState((BlockPos)$$2);
                Optional<BlockState> $$5 = HoneycombItem.getWaxed($$4);
                if ($$5.isPresent()) {
                    $$3.setBlockAndUpdate((BlockPos)$$2, (BlockState)$$5.get());
                    $$3.levelEvent(3003, (BlockPos)$$2, 0);
                    $$1.shrink(1);
                    this.setSuccess(true);
                    return $$1;
                }
                return super.execute($$0, $$1);
            }
        });
        DispenserBlock.registerBehavior(Items.POTION, new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource $$0, ItemStack $$1) {
                if (PotionUtils.getPotion($$1) != Potions.WATER) {
                    return this.defaultDispenseItemBehavior.dispense($$0, $$1);
                }
                ServerLevel $$2 = $$0.getLevel();
                BlockPos $$3 = $$0.getPos();
                Vec3i $$4 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
                if ($$2.getBlockState((BlockPos)$$4).is(BlockTags.CONVERTABLE_TO_MUD)) {
                    if (!$$2.isClientSide) {
                        for (int $$5 = 0; $$5 < 5; ++$$5) {
                            $$2.sendParticles(ParticleTypes.SPLASH, (double)$$3.getX() + $$2.random.nextDouble(), $$3.getY() + 1, (double)$$3.getZ() + $$2.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                        }
                    }
                    $$2.playSound(null, $$3, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                    $$2.gameEvent(null, GameEvent.FLUID_PLACE, $$3);
                    $$2.setBlockAndUpdate((BlockPos)$$4, Blocks.MUD.defaultBlockState());
                    return new ItemStack(Items.GLASS_BOTTLE);
                }
                return this.defaultDispenseItemBehavior.dispense($$0, $$1);
            }
        });
    }

    public static void setEntityPokingOutOfBlock(BlockSource $$0, Entity $$1, Direction $$2) {
        $$1.setPos($$0.x() + (double)$$2.getStepX() * (0.5000099999997474 - (double)$$1.getBbWidth() / 2.0), $$0.y() + (double)$$2.getStepY() * (0.5000099999997474 - (double)$$1.getBbHeight() / 2.0) - (double)$$1.getBbHeight() / 2.0, $$0.z() + (double)$$2.getStepZ() * (0.5000099999997474 - (double)$$1.getBbWidth() / 2.0));
    }
}