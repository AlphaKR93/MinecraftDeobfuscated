/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.logging.LogUtils
 *  java.lang.Class
 *  java.lang.Deprecated
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.Optional
 *  java.util.Spliterator
 *  java.util.UUID
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class EntityType<T extends Entity>
implements FeatureElement,
EntityTypeTest<Entity, T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ENTITY_TAG = "EntityTag";
    private final Holder.Reference<EntityType<?>> builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
    private static final float MAGIC_HORSE_WIDTH = 1.3964844f;
    public static final EntityType<Allay> ALLAY = EntityType.register("allay", Builder.of(Allay::new, MobCategory.CREATURE).sized(0.35f, 0.6f).clientTrackingRange(8).updateInterval(2));
    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = EntityType.register("area_effect_cloud", Builder.of(AreaEffectCloud::new, MobCategory.MISC).fireImmune().sized(6.0f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<ArmorStand> ARMOR_STAND = EntityType.register("armor_stand", Builder.of(ArmorStand::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10));
    public static final EntityType<Arrow> ARROW = EntityType.register("arrow", Builder.of(Arrow::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<Axolotl> AXOLOTL = EntityType.register("axolotl", Builder.of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75f, 0.42f).clientTrackingRange(10));
    public static final EntityType<Bat> BAT = EntityType.register("bat", Builder.of(Bat::new, MobCategory.AMBIENT).sized(0.5f, 0.9f).clientTrackingRange(5));
    public static final EntityType<Bee> BEE = EntityType.register("bee", Builder.of(Bee::new, MobCategory.CREATURE).sized(0.7f, 0.6f).clientTrackingRange(8));
    public static final EntityType<Blaze> BLAZE = EntityType.register("blaze", Builder.of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6f, 1.8f).clientTrackingRange(8));
    public static final EntityType<Boat> BOAT = EntityType.register("boat", Builder.of(Boat::new, MobCategory.MISC).sized(1.375f, 0.5625f).clientTrackingRange(10));
    public static final EntityType<ChestBoat> CHEST_BOAT = EntityType.register("chest_boat", Builder.of(ChestBoat::new, MobCategory.MISC).sized(1.375f, 0.5625f).clientTrackingRange(10));
    public static final EntityType<Cat> CAT = EntityType.register("cat", Builder.of(Cat::new, MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(8));
    public static final EntityType<Camel> CAMEL = EntityType.register("camel", Builder.of(Camel::new, MobCategory.CREATURE).sized(1.7f, 2.375f).clientTrackingRange(10).requiredFeatures(FeatureFlags.UPDATE_1_20));
    public static final EntityType<CaveSpider> CAVE_SPIDER = EntityType.register("cave_spider", Builder.of(CaveSpider::new, MobCategory.MONSTER).sized(0.7f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Chicken> CHICKEN = EntityType.register("chicken", Builder.of(Chicken::new, MobCategory.CREATURE).sized(0.4f, 0.7f).clientTrackingRange(10));
    public static final EntityType<Cod> COD = EntityType.register("cod", Builder.of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5f, 0.3f).clientTrackingRange(4));
    public static final EntityType<Cow> COW = EntityType.register("cow", Builder.of(Cow::new, MobCategory.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
    public static final EntityType<Creeper> CREEPER = EntityType.register("creeper", Builder.of(Creeper::new, MobCategory.MONSTER).sized(0.6f, 1.7f).clientTrackingRange(8));
    public static final EntityType<Dolphin> DOLPHIN = EntityType.register("dolphin", Builder.of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9f, 0.6f));
    public static final EntityType<Donkey> DONKEY = EntityType.register("donkey", Builder.of(Donkey::new, MobCategory.CREATURE).sized(1.3964844f, 1.5f).clientTrackingRange(10));
    public static final EntityType<DragonFireball> DRAGON_FIREBALL = EntityType.register("dragon_fireball", Builder.of(DragonFireball::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Drowned> DROWNED = EntityType.register("drowned", Builder.of(Drowned::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ElderGuardian> ELDER_GUARDIAN = EntityType.register("elder_guardian", Builder.of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975f, 1.9975f).clientTrackingRange(10));
    public static final EntityType<EndCrystal> END_CRYSTAL = EntityType.register("end_crystal", Builder.of(EndCrystal::new, MobCategory.MISC).sized(2.0f, 2.0f).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<EnderDragon> ENDER_DRAGON = EntityType.register("ender_dragon", Builder.of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0f, 8.0f).clientTrackingRange(10));
    public static final EntityType<EnderMan> ENDERMAN = EntityType.register("enderman", Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.6f, 2.9f).clientTrackingRange(8));
    public static final EntityType<Endermite> ENDERMITE = EntityType.register("endermite", Builder.of(Endermite::new, MobCategory.MONSTER).sized(0.4f, 0.3f).clientTrackingRange(8));
    public static final EntityType<Evoker> EVOKER = EntityType.register("evoker", Builder.of(Evoker::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<EvokerFangs> EVOKER_FANGS = EntityType.register("evoker_fangs", Builder.of(EvokerFangs::new, MobCategory.MISC).sized(0.5f, 0.8f).clientTrackingRange(6).updateInterval(2));
    public static final EntityType<ExperienceOrb> EXPERIENCE_ORB = EntityType.register("experience_orb", Builder.of(ExperienceOrb::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(6).updateInterval(20));
    public static final EntityType<EyeOfEnder> EYE_OF_ENDER = EntityType.register("eye_of_ender", Builder.of(EyeOfEnder::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(4));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = EntityType.register("falling_block", Builder.of(FallingBlockEntity::new, MobCategory.MISC).sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(20));
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = EntityType.register("firework_rocket", Builder.of(FireworkRocketEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Fox> FOX = EntityType.register("fox", Builder.of(Fox::new, MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
    public static final EntityType<Frog> FROG = EntityType.register("frog", Builder.of(Frog::new, MobCategory.CREATURE).sized(0.5f, 0.5f).clientTrackingRange(10));
    public static final EntityType<Ghast> GHAST = EntityType.register("ghast", Builder.of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0f, 4.0f).clientTrackingRange(10));
    public static final EntityType<Giant> GIANT = EntityType.register("giant", Builder.of(Giant::new, MobCategory.MONSTER).sized(3.6f, 12.0f).clientTrackingRange(10));
    public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME = EntityType.register("glow_item_frame", Builder.of(GlowItemFrame::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<GlowSquid> GLOW_SQUID = EntityType.register("glow_squid", Builder.of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8f, 0.8f).clientTrackingRange(10));
    public static final EntityType<Goat> GOAT = EntityType.register("goat", Builder.of(Goat::new, MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
    public static final EntityType<Guardian> GUARDIAN = EntityType.register("guardian", Builder.of(Guardian::new, MobCategory.MONSTER).sized(0.85f, 0.85f).clientTrackingRange(8));
    public static final EntityType<Hoglin> HOGLIN = EntityType.register("hoglin", Builder.of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844f, 1.4f).clientTrackingRange(8));
    public static final EntityType<Horse> HORSE = EntityType.register("horse", Builder.of(Horse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<Husk> HUSK = EntityType.register("husk", Builder.of(Husk::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Illusioner> ILLUSIONER = EntityType.register("illusioner", Builder.of(Illusioner::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<IronGolem> IRON_GOLEM = EntityType.register("iron_golem", Builder.of(IronGolem::new, MobCategory.MISC).sized(1.4f, 2.7f).clientTrackingRange(10));
    public static final EntityType<ItemEntity> ITEM = EntityType.register("item", Builder.of(ItemEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(6).updateInterval(20));
    public static final EntityType<ItemFrame> ITEM_FRAME = EntityType.register("item_frame", Builder.of(ItemFrame::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<LargeFireball> FIREBALL = EntityType.register("fireball", Builder.of(LargeFireball::new, MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = EntityType.register("leash_knot", Builder.of(LeashFenceKnotEntity::new, MobCategory.MISC).noSave().sized(0.375f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<LightningBolt> LIGHTNING_BOLT = EntityType.register("lightning_bolt", Builder.of(LightningBolt::new, MobCategory.MISC).noSave().sized(0.0f, 0.0f).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<Llama> LLAMA = EntityType.register("llama", Builder.of(Llama::new, MobCategory.CREATURE).sized(0.9f, 1.87f).clientTrackingRange(10));
    public static final EntityType<LlamaSpit> LLAMA_SPIT = EntityType.register("llama_spit", Builder.of(LlamaSpit::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<MagmaCube> MAGMA_CUBE = EntityType.register("magma_cube", Builder.of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(2.04f, 2.04f).clientTrackingRange(8));
    public static final EntityType<Marker> MARKER = EntityType.register("marker", Builder.of(Marker::new, MobCategory.MISC).sized(0.0f, 0.0f).clientTrackingRange(0));
    public static final EntityType<Minecart> MINECART = EntityType.register("minecart", Builder.of(Minecart::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartChest> CHEST_MINECART = EntityType.register("chest_minecart", Builder.of(MinecartChest::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = EntityType.register("command_block_minecart", Builder.of(MinecartCommandBlock::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartFurnace> FURNACE_MINECART = EntityType.register("furnace_minecart", Builder.of(MinecartFurnace::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartHopper> HOPPER_MINECART = EntityType.register("hopper_minecart", Builder.of(MinecartHopper::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartSpawner> SPAWNER_MINECART = EntityType.register("spawner_minecart", Builder.of(MinecartSpawner::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartTNT> TNT_MINECART = EntityType.register("tnt_minecart", Builder.of(MinecartTNT::new, MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<Mule> MULE = EntityType.register("mule", Builder.of(Mule::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(8));
    public static final EntityType<MushroomCow> MOOSHROOM = EntityType.register("mooshroom", Builder.of(MushroomCow::new, MobCategory.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
    public static final EntityType<Ocelot> OCELOT = EntityType.register("ocelot", Builder.of(Ocelot::new, MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(10));
    public static final EntityType<Painting> PAINTING = EntityType.register("painting", Builder.of(Painting::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<Panda> PANDA = EntityType.register("panda", Builder.of(Panda::new, MobCategory.CREATURE).sized(1.3f, 1.25f).clientTrackingRange(10));
    public static final EntityType<Parrot> PARROT = EntityType.register("parrot", Builder.of(Parrot::new, MobCategory.CREATURE).sized(0.5f, 0.9f).clientTrackingRange(8));
    public static final EntityType<Phantom> PHANTOM = EntityType.register("phantom", Builder.of(Phantom::new, MobCategory.MONSTER).sized(0.9f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Pig> PIG = EntityType.register("pig", Builder.of(Pig::new, MobCategory.CREATURE).sized(0.9f, 0.9f).clientTrackingRange(10));
    public static final EntityType<Piglin> PIGLIN = EntityType.register("piglin", Builder.of(Piglin::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<PiglinBrute> PIGLIN_BRUTE = EntityType.register("piglin_brute", Builder.of(PiglinBrute::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Pillager> PILLAGER = EntityType.register("pillager", Builder.of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<PolarBear> POLAR_BEAR = EntityType.register("polar_bear", Builder.of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4f, 1.4f).clientTrackingRange(10));
    public static final EntityType<PrimedTnt> TNT = EntityType.register("tnt", Builder.of(PrimedTnt::new, MobCategory.MISC).fireImmune().sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(10));
    public static final EntityType<Pufferfish> PUFFERFISH = EntityType.register("pufferfish", Builder.of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7f, 0.7f).clientTrackingRange(4));
    public static final EntityType<Rabbit> RABBIT = EntityType.register("rabbit", Builder.of(Rabbit::new, MobCategory.CREATURE).sized(0.4f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Ravager> RAVAGER = EntityType.register("ravager", Builder.of(Ravager::new, MobCategory.MONSTER).sized(1.95f, 2.2f).clientTrackingRange(10));
    public static final EntityType<Salmon> SALMON = EntityType.register("salmon", Builder.of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7f, 0.4f).clientTrackingRange(4));
    public static final EntityType<Sheep> SHEEP = EntityType.register("sheep", Builder.of(Sheep::new, MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
    public static final EntityType<Shulker> SHULKER = EntityType.register("shulker", Builder.of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0f, 1.0f).clientTrackingRange(10));
    public static final EntityType<ShulkerBullet> SHULKER_BULLET = EntityType.register("shulker_bullet", Builder.of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(8));
    public static final EntityType<Silverfish> SILVERFISH = EntityType.register("silverfish", Builder.of(Silverfish::new, MobCategory.MONSTER).sized(0.4f, 0.3f).clientTrackingRange(8));
    public static final EntityType<Skeleton> SKELETON = EntityType.register("skeleton", Builder.of(Skeleton::new, MobCategory.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(8));
    public static final EntityType<SkeletonHorse> SKELETON_HORSE = EntityType.register("skeleton_horse", Builder.of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<Slime> SLIME = EntityType.register("slime", Builder.of(Slime::new, MobCategory.MONSTER).sized(2.04f, 2.04f).clientTrackingRange(10));
    public static final EntityType<SmallFireball> SMALL_FIREBALL = EntityType.register("small_fireball", Builder.of(SmallFireball::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<SnowGolem> SNOW_GOLEM = EntityType.register("snow_golem", Builder.of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7f, 1.9f).clientTrackingRange(8));
    public static final EntityType<Snowball> SNOWBALL = EntityType.register("snowball", Builder.of(Snowball::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<SpectralArrow> SPECTRAL_ARROW = EntityType.register("spectral_arrow", Builder.of(SpectralArrow::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<Spider> SPIDER = EntityType.register("spider", Builder.of(Spider::new, MobCategory.MONSTER).sized(1.4f, 0.9f).clientTrackingRange(8));
    public static final EntityType<Squid> SQUID = EntityType.register("squid", Builder.of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8f, 0.8f).clientTrackingRange(8));
    public static final EntityType<Stray> STRAY = EntityType.register("stray", Builder.of(Stray::new, MobCategory.MONSTER).sized(0.6f, 1.99f).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(8));
    public static final EntityType<Strider> STRIDER = EntityType.register("strider", Builder.of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9f, 1.7f).clientTrackingRange(10));
    public static final EntityType<Tadpole> TADPOLE = EntityType.register("tadpole", Builder.of(Tadpole::new, MobCategory.CREATURE).sized(Tadpole.HITBOX_WIDTH, Tadpole.HITBOX_HEIGHT).clientTrackingRange(10));
    public static final EntityType<ThrownEgg> EGG = EntityType.register("egg", Builder.of(ThrownEgg::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownEnderpearl> ENDER_PEARL = EntityType.register("ender_pearl", Builder.of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = EntityType.register("experience_bottle", Builder.of(ThrownExperienceBottle::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownPotion> POTION = EntityType.register("potion", Builder.of(ThrownPotion::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownTrident> TRIDENT = EntityType.register("trident", Builder.of(ThrownTrident::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<TraderLlama> TRADER_LLAMA = EntityType.register("trader_llama", Builder.of(TraderLlama::new, MobCategory.CREATURE).sized(0.9f, 1.87f).clientTrackingRange(10));
    public static final EntityType<TropicalFish> TROPICAL_FISH = EntityType.register("tropical_fish", Builder.of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5f, 0.4f).clientTrackingRange(4));
    public static final EntityType<Turtle> TURTLE = EntityType.register("turtle", Builder.of(Turtle::new, MobCategory.CREATURE).sized(1.2f, 0.4f).clientTrackingRange(10));
    public static final EntityType<Vex> VEX = EntityType.register("vex", Builder.of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4f, 0.8f).clientTrackingRange(8));
    public static final EntityType<Villager> VILLAGER = EntityType.register("villager", Builder.of(Villager::new, MobCategory.MISC).sized(0.6f, 1.95f).clientTrackingRange(10));
    public static final EntityType<Vindicator> VINDICATOR = EntityType.register("vindicator", Builder.of(Vindicator::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<WanderingTrader> WANDERING_TRADER = EntityType.register("wandering_trader", Builder.of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6f, 1.95f).clientTrackingRange(10));
    public static final EntityType<Warden> WARDEN = EntityType.register("warden", Builder.of(Warden::new, MobCategory.MONSTER).sized(0.9f, 2.9f).clientTrackingRange(16).fireImmune());
    public static final EntityType<Witch> WITCH = EntityType.register("witch", Builder.of(Witch::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<WitherBoss> WITHER = EntityType.register("wither", Builder.of(WitherBoss::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9f, 3.5f).clientTrackingRange(10));
    public static final EntityType<WitherSkeleton> WITHER_SKELETON = EntityType.register("wither_skeleton", Builder.of(WitherSkeleton::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7f, 2.4f).clientTrackingRange(8));
    public static final EntityType<WitherSkull> WITHER_SKULL = EntityType.register("wither_skull", Builder.of(WitherSkull::new, MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Wolf> WOLF = EntityType.register("wolf", Builder.of(Wolf::new, MobCategory.CREATURE).sized(0.6f, 0.85f).clientTrackingRange(10));
    public static final EntityType<Zoglin> ZOGLIN = EntityType.register("zoglin", Builder.of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844f, 1.4f).clientTrackingRange(8));
    public static final EntityType<Zombie> ZOMBIE = EntityType.register("zombie", Builder.of(Zombie::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ZombieHorse> ZOMBIE_HORSE = EntityType.register("zombie_horse", Builder.of(ZombieHorse::new, MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = EntityType.register("zombie_villager", Builder.of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = EntityType.register("zombified_piglin", Builder.of(ZombifiedPiglin::new, MobCategory.MONSTER).fireImmune().sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Player> PLAYER = EntityType.register("player", Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6f, 1.8f).clientTrackingRange(32).updateInterval(2));
    public static final EntityType<FishingHook> FISHING_BOBBER = EntityType.register("fishing_bobber", Builder.of(FishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(5));
    private final EntityFactory<T> factory;
    private final MobCategory category;
    private final ImmutableSet<Block> immuneTo;
    private final boolean serialize;
    private final boolean summon;
    private final boolean fireImmune;
    private final boolean canSpawnFarFromPlayer;
    private final int clientTrackingRange;
    private final int updateInterval;
    @Nullable
    private String descriptionId;
    @Nullable
    private Component description;
    @Nullable
    private ResourceLocation lootTable;
    private final EntityDimensions dimensions;
    private final FeatureFlagSet requiredFeatures;

    private static <T extends Entity> EntityType<T> register(String $$0, Builder<T> $$1) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, $$0, $$1.build($$0));
    }

    public static ResourceLocation getKey(EntityType<?> $$0) {
        return BuiltInRegistries.ENTITY_TYPE.getKey($$0);
    }

    public static Optional<EntityType<?>> byString(String $$0) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse($$0));
    }

    public EntityType(EntityFactory<T> $$0, MobCategory $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5, ImmutableSet<Block> $$6, EntityDimensions $$7, int $$8, int $$9, FeatureFlagSet $$10) {
        this.factory = $$0;
        this.category = $$1;
        this.canSpawnFarFromPlayer = $$5;
        this.serialize = $$2;
        this.summon = $$3;
        this.fireImmune = $$4;
        this.immuneTo = $$6;
        this.dimensions = $$7;
        this.clientTrackingRange = $$8;
        this.updateInterval = $$9;
        this.requiredFeatures = $$10;
    }

    @Nullable
    public T spawn(ServerLevel $$02, @Nullable ItemStack $$1, @Nullable Player $$2, BlockPos $$3, MobSpawnType $$4, boolean $$5, boolean $$6) {
        CompoundTag $$10;
        Consumer $$9;
        if ($$1 != null) {
            CompoundTag $$7 = $$1.getTag();
            Consumer<T> $$8 = EntityType.createDefaultStackConfig($$02, $$1, $$2);
        } else {
            $$9 = $$0 -> {};
            $$10 = null;
        }
        return this.spawn($$02, $$10, $$9, $$3, $$4, $$5, $$6);
    }

    public static <T extends Entity> Consumer<T> createDefaultStackConfig(ServerLevel $$02, ItemStack $$1, @Nullable Player $$2) {
        Consumer<T> $$3 = $$0 -> {};
        $$3 = EntityType.appendCustomNameConfig($$3, $$1);
        return EntityType.appendCustomEntityStackConfig($$3, $$02, $$1, $$2);
    }

    public static <T extends Entity> Consumer<T> appendCustomNameConfig(Consumer<T> $$0, ItemStack $$12) {
        if ($$12.hasCustomHoverName()) {
            return $$0.andThen($$1 -> $$1.setCustomName($$12.getHoverName()));
        }
        return $$0;
    }

    public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(Consumer<T> $$0, ServerLevel $$1, ItemStack $$2, @Nullable Player $$32) {
        CompoundTag $$4 = $$2.getTag();
        if ($$4 != null) {
            return $$0.andThen($$3 -> EntityType.updateCustomEntityTag($$1, $$32, $$3, $$4));
        }
        return $$0;
    }

    @Nullable
    public T spawn(ServerLevel $$0, BlockPos $$1, MobSpawnType $$2) {
        return this.spawn($$0, (CompoundTag)null, null, $$1, $$2, false, false);
    }

    @Nullable
    public T spawn(ServerLevel $$0, @Nullable CompoundTag $$1, @Nullable Consumer<T> $$2, BlockPos $$3, MobSpawnType $$4, boolean $$5, boolean $$6) {
        T $$7 = this.create($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        if ($$7 != null) {
            $$0.addFreshEntityWithPassengers((Entity)$$7);
        }
        return $$7;
    }

    @Nullable
    public T create(ServerLevel $$0, @Nullable CompoundTag $$1, @Nullable Consumer<T> $$2, BlockPos $$3, MobSpawnType $$4, boolean $$5, boolean $$6) {
        double $$9;
        T $$7 = this.create($$0);
        if ($$7 == null) {
            return null;
        }
        if ($$5) {
            ((Entity)$$7).setPos((double)$$3.getX() + 0.5, $$3.getY() + 1, (double)$$3.getZ() + 0.5);
            double $$8 = EntityType.getYOffset($$0, $$3, $$6, ((Entity)$$7).getBoundingBox());
        } else {
            $$9 = 0.0;
        }
        ((Entity)$$7).moveTo((double)$$3.getX() + 0.5, (double)$$3.getY() + $$9, (double)$$3.getZ() + 0.5, Mth.wrapDegrees($$0.random.nextFloat() * 360.0f), 0.0f);
        if ($$7 instanceof Mob) {
            Mob $$10 = (Mob)$$7;
            $$10.yHeadRot = $$10.getYRot();
            $$10.yBodyRot = $$10.getYRot();
            $$10.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$10.blockPosition()), $$4, null, $$1);
            $$10.playAmbientSound();
        }
        if ($$2 != null) {
            $$2.accept($$7);
        }
        return $$7;
    }

    protected static double getYOffset(LevelReader $$0, BlockPos $$1, boolean $$2, AABB $$3) {
        AABB $$4 = new AABB($$1);
        if ($$2) {
            $$4 = $$4.expandTowards(0.0, -1.0, 0.0);
        }
        Iterable $$5 = $$0.getCollisions(null, $$4);
        return 1.0 + Shapes.collide(Direction.Axis.Y, $$3, (Iterable<VoxelShape>)$$5, $$2 ? -2.0 : -1.0);
    }

    public static void updateCustomEntityTag(Level $$0, @Nullable Player $$1, @Nullable Entity $$2, @Nullable CompoundTag $$3) {
        if ($$3 == null || !$$3.contains(ENTITY_TAG, 10)) {
            return;
        }
        MinecraftServer $$4 = $$0.getServer();
        if ($$4 == null || $$2 == null) {
            return;
        }
        if (!($$0.isClientSide || !$$2.onlyOpCanSetNbt() || $$1 != null && $$4.getPlayerList().isOp($$1.getGameProfile()))) {
            return;
        }
        CompoundTag $$5 = $$2.saveWithoutId(new CompoundTag());
        UUID $$6 = $$2.getUUID();
        $$5.merge($$3.getCompound(ENTITY_TAG));
        $$2.setUUID($$6);
        $$2.load($$5);
    }

    public boolean canSerialize() {
        return this.serialize;
    }

    public boolean canSummon() {
        return this.summon;
    }

    public boolean fireImmune() {
        return this.fireImmune;
    }

    public boolean canSpawnFarFromPlayer() {
        return this.canSpawnFarFromPlayer;
    }

    public MobCategory getCategory() {
        return this.category;
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this));
        }
        return this.descriptionId;
    }

    public Component getDescription() {
        if (this.description == null) {
            this.description = Component.translatable(this.getDescriptionId());
        }
        return this.description;
    }

    public String toString() {
        return this.getDescriptionId();
    }

    public String toShortString() {
        int $$0 = this.getDescriptionId().lastIndexOf(46);
        return $$0 == -1 ? this.getDescriptionId() : this.getDescriptionId().substring($$0 + 1);
    }

    public ResourceLocation getDefaultLootTable() {
        if (this.lootTable == null) {
            ResourceLocation $$0 = BuiltInRegistries.ENTITY_TYPE.getKey(this);
            this.lootTable = $$0.withPrefix("entities/");
        }
        return this.lootTable;
    }

    public float getWidth() {
        return this.dimensions.width;
    }

    public float getHeight() {
        return this.dimensions.height;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    @Nullable
    public T create(Level $$0) {
        if (!this.isEnabled($$0.enabledFeatures())) {
            return null;
        }
        return this.factory.create(this, $$0);
    }

    public static Optional<Entity> create(CompoundTag $$0, Level $$12) {
        return Util.ifElse(EntityType.by($$0).map($$1 -> $$1.create($$12)), $$1 -> $$1.load($$0), () -> LOGGER.warn("Skipping Entity with id {}", (Object)$$0.getString("id")));
    }

    public AABB getAABB(double $$0, double $$1, double $$2) {
        float $$3 = this.getWidth() / 2.0f;
        return new AABB($$0 - (double)$$3, $$1, $$2 - (double)$$3, $$0 + (double)$$3, $$1 + (double)this.getHeight(), $$2 + (double)$$3);
    }

    public boolean isBlockDangerous(BlockState $$0) {
        if (this.immuneTo.contains((Object)$$0.getBlock())) {
            return false;
        }
        if (!this.fireImmune && WalkNodeEvaluator.isBurningBlock($$0)) {
            return true;
        }
        return $$0.is(Blocks.WITHER_ROSE) || $$0.is(Blocks.SWEET_BERRY_BUSH) || $$0.is(Blocks.CACTUS) || $$0.is(Blocks.POWDER_SNOW);
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> by(CompoundTag $$0) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation($$0.getString("id")));
    }

    @Nullable
    public static Entity loadEntityRecursive(CompoundTag $$0, Level $$1, Function<Entity, Entity> $$2) {
        return (Entity)EntityType.loadStaticEntity($$0, $$1).map($$2).map($$3 -> {
            if ($$0.contains("Passengers", 9)) {
                ListTag $$4 = $$0.getList("Passengers", 10);
                for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                    Entity $$6 = EntityType.loadEntityRecursive($$4.getCompound($$5), $$1, $$2);
                    if ($$6 == null) continue;
                    $$6.startRiding((Entity)$$3, true);
                }
            }
            return $$3;
        }).orElse(null);
    }

    public static Stream<Entity> loadEntitiesRecursive(final List<? extends Tag> $$0, final Level $$1) {
        final Spliterator $$2 = $$0.spliterator();
        return StreamSupport.stream((Spliterator)new Spliterator<Entity>(){

            public boolean tryAdvance(Consumer<? super Entity> $$02) {
                return $$2.tryAdvance($$2 -> EntityType.loadEntityRecursive((CompoundTag)$$2, $$1, (Function<Entity, Entity>)((Function)$$1 -> {
                    $$02.accept($$1);
                    return $$1;
                })));
            }

            public Spliterator<Entity> trySplit() {
                return null;
            }

            public long estimateSize() {
                return $$0.size();
            }

            public int characteristics() {
                return 1297;
            }
        }, (boolean)false);
    }

    private static Optional<Entity> loadStaticEntity(CompoundTag $$0, Level $$1) {
        try {
            return EntityType.create($$0, $$1);
        }
        catch (RuntimeException $$2) {
            LOGGER.warn("Exception loading entity: ", (Throwable)$$2);
            return Optional.empty();
        }
    }

    public int clientTrackingRange() {
        return this.clientTrackingRange;
    }

    public int updateInterval() {
        return this.updateInterval;
    }

    public boolean trackDeltas() {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != GLOW_ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean is(TagKey<EntityType<?>> $$0) {
        return this.builtInRegistryHolder.is($$0);
    }

    @Override
    @Nullable
    public T tryCast(Entity $$0) {
        return (T)($$0.getType() == this ? $$0 : null);
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return Entity.class;
    }

    @Deprecated
    public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final MobCategory category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int clientTrackingRange = 5;
        private int updateInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.scalable(0.6f, 1.8f);
        private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

        private Builder(EntityFactory<T> $$0, MobCategory $$1) {
            this.factory = $$0;
            this.category = $$1;
            this.canSpawnFarFromPlayer = $$1 == MobCategory.CREATURE || $$1 == MobCategory.MISC;
        }

        public static <T extends Entity> Builder<T> of(EntityFactory<T> $$0, MobCategory $$1) {
            return new Builder<T>($$0, $$1);
        }

        public static <T extends Entity> Builder<T> createNothing(MobCategory $$02) {
            return new Builder<Entity>(($$0, $$1) -> null, $$02);
        }

        public Builder<T> sized(float $$0, float $$1) {
            this.dimensions = EntityDimensions.scalable($$0, $$1);
            return this;
        }

        public Builder<T> noSummon() {
            this.summon = false;
            return this;
        }

        public Builder<T> noSave() {
            this.serialize = false;
            return this;
        }

        public Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> immuneTo(Block ... $$0) {
            this.immuneTo = ImmutableSet.copyOf((Object[])$$0);
            return this;
        }

        public Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        public Builder<T> clientTrackingRange(int $$0) {
            this.clientTrackingRange = $$0;
            return this;
        }

        public Builder<T> updateInterval(int $$0) {
            this.updateInterval = $$0;
            return this;
        }

        public Builder<T> requiredFeatures(FeatureFlag ... $$0) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset($$0);
            return this;
        }

        public EntityType<T> build(String $$0) {
            if (this.serialize) {
                Util.fetchChoiceType(References.ENTITY_TREE, $$0);
            }
            return new EntityType<T>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange, this.updateInterval, this.requiredFeatures);
        }
    }

    public static interface EntityFactory<T extends Entity> {
        public T create(EntityType<T> var1, Level var2);
    }
}