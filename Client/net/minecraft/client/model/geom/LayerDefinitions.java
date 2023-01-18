/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Collectors
 */
package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.WoodType;

public class LayerDefinitions {
    private static final CubeDeformation FISH_PATTERN_DEFORMATION = new CubeDeformation(0.008f);
    private static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0f);
    private static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5f);

    public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
        ImmutableMap.Builder $$0 = ImmutableMap.builder();
        LayerDefinition $$12 = LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f), 64, 64);
        LayerDefinition $$22 = LayerDefinition.create(HumanoidModel.createMesh(OUTER_ARMOR_DEFORMATION, 0.0f), 64, 32);
        LayerDefinition $$3 = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.02f), 0.0f), 64, 32);
        LayerDefinition $$4 = LayerDefinition.create(HumanoidModel.createMesh(INNER_ARMOR_DEFORMATION, 0.0f), 64, 32);
        LayerDefinition $$5 = MinecartModel.createBodyLayer();
        LayerDefinition $$6 = SkullModel.createMobHeadLayer();
        LayerDefinition $$7 = LayerDefinition.create(HorseModel.createBodyMesh(CubeDeformation.NONE), 64, 64);
        LayerDefinition $$8 = IllagerModel.createBodyLayer();
        LayerDefinition $$9 = CowModel.createBodyLayer();
        LayerDefinition $$10 = LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32);
        LayerDefinition $$11 = LayerDefinition.create(PiglinModel.createMesh(CubeDeformation.NONE), 64, 64);
        LayerDefinition $$122 = LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64);
        LayerDefinition $$13 = SkullModel.createHumanoidHeadLayer();
        LayerDefinition $$14 = LlamaModel.createBodyLayer(CubeDeformation.NONE);
        LayerDefinition $$15 = StriderModel.createBodyLayer();
        LayerDefinition $$16 = HoglinModel.createBodyLayer();
        LayerDefinition $$17 = SkeletonModel.createBodyLayer();
        LayerDefinition $$18 = LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64);
        LayerDefinition $$19 = SpiderModel.createSpiderBodyLayer();
        $$0.put((Object)ModelLayers.ALLAY, (Object)AllayModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ARMOR_STAND, (Object)ArmorStandModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ARMOR_STAND_INNER_ARMOR, (Object)ArmorStandArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
        $$0.put((Object)ModelLayers.ARMOR_STAND_OUTER_ARMOR, (Object)ArmorStandArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION));
        $$0.put((Object)ModelLayers.AXOLOTL, (Object)AxolotlModel.createBodyLayer());
        $$0.put((Object)ModelLayers.BANNER, (Object)BannerRenderer.createBodyLayer());
        $$0.put((Object)ModelLayers.BAT, (Object)BatModel.createBodyLayer());
        $$0.put((Object)ModelLayers.BED_FOOT, (Object)BedRenderer.createFootLayer());
        $$0.put((Object)ModelLayers.BED_HEAD, (Object)BedRenderer.createHeadLayer());
        $$0.put((Object)ModelLayers.BEE, (Object)BeeModel.createBodyLayer());
        $$0.put((Object)ModelLayers.BELL, (Object)BellRenderer.createBodyLayer());
        $$0.put((Object)ModelLayers.BLAZE, (Object)BlazeModel.createBodyLayer());
        $$0.put((Object)ModelLayers.BOOK, (Object)BookModel.createBodyLayer());
        $$0.put((Object)ModelLayers.CAT, (Object)$$10);
        $$0.put((Object)ModelLayers.CAT_COLLAR, (Object)LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01f)), 64, 32));
        $$0.put((Object)ModelLayers.CAMEL, (Object)CamelModel.createBodyLayer());
        $$0.put((Object)ModelLayers.CAVE_SPIDER, (Object)$$19);
        $$0.put((Object)ModelLayers.CHEST, (Object)ChestRenderer.createSingleBodyLayer());
        $$0.put((Object)ModelLayers.DOUBLE_CHEST_LEFT, (Object)ChestRenderer.createDoubleBodyLeftLayer());
        $$0.put((Object)ModelLayers.DOUBLE_CHEST_RIGHT, (Object)ChestRenderer.createDoubleBodyRightLayer());
        $$0.put((Object)ModelLayers.CHEST_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.CHICKEN, (Object)ChickenModel.createBodyLayer());
        $$0.put((Object)ModelLayers.COD, (Object)CodModel.createBodyLayer());
        $$0.put((Object)ModelLayers.COMMAND_BLOCK_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.CONDUIT_EYE, (Object)ConduitRenderer.createEyeLayer());
        $$0.put((Object)ModelLayers.CONDUIT_WIND, (Object)ConduitRenderer.createWindLayer());
        $$0.put((Object)ModelLayers.CONDUIT_SHELL, (Object)ConduitRenderer.createShellLayer());
        $$0.put((Object)ModelLayers.CONDUIT_CAGE, (Object)ConduitRenderer.createCageLayer());
        $$0.put((Object)ModelLayers.COW, (Object)$$9);
        $$0.put((Object)ModelLayers.CREEPER, (Object)CreeperModel.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.CREEPER_ARMOR, (Object)CreeperModel.createBodyLayer(new CubeDeformation(2.0f)));
        $$0.put((Object)ModelLayers.CREEPER_HEAD, (Object)$$6);
        $$0.put((Object)ModelLayers.DOLPHIN, (Object)DolphinModel.createBodyLayer());
        $$0.put((Object)ModelLayers.DONKEY, (Object)ChestedHorseModel.createBodyLayer());
        $$0.put((Object)ModelLayers.DRAGON_SKULL, (Object)DragonHeadModel.createHeadLayer());
        $$0.put((Object)ModelLayers.DROWNED, (Object)DrownedModel.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.DROWNED_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.DROWNED_OUTER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.DROWNED_OUTER_LAYER, (Object)DrownedModel.createBodyLayer(new CubeDeformation(0.25f)));
        $$0.put((Object)ModelLayers.ELDER_GUARDIAN, (Object)GuardianModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ELYTRA, (Object)ElytraModel.createLayer());
        $$0.put((Object)ModelLayers.ENDERMAN, (Object)EndermanModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ENDERMITE, (Object)EndermiteModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ENDER_DRAGON, (Object)EnderDragonRenderer.createBodyLayer());
        $$0.put((Object)ModelLayers.END_CRYSTAL, (Object)EndCrystalRenderer.createBodyLayer());
        $$0.put((Object)ModelLayers.EVOKER, (Object)$$8);
        $$0.put((Object)ModelLayers.EVOKER_FANGS, (Object)EvokerFangsModel.createBodyLayer());
        $$0.put((Object)ModelLayers.FOX, (Object)FoxModel.createBodyLayer());
        $$0.put((Object)ModelLayers.FROG, (Object)FrogModel.createBodyLayer());
        $$0.put((Object)ModelLayers.FURNACE_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.GHAST, (Object)GhastModel.createBodyLayer());
        $$0.put((Object)ModelLayers.GIANT, (Object)$$12);
        $$0.put((Object)ModelLayers.GIANT_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.GIANT_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.GLOW_SQUID, (Object)SquidModel.createBodyLayer());
        $$0.put((Object)ModelLayers.GOAT, (Object)GoatModel.createBodyLayer());
        $$0.put((Object)ModelLayers.GUARDIAN, (Object)GuardianModel.createBodyLayer());
        $$0.put((Object)ModelLayers.HOGLIN, (Object)$$16);
        $$0.put((Object)ModelLayers.HOPPER_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.HORSE, (Object)$$7);
        $$0.put((Object)ModelLayers.HORSE_ARMOR, (Object)LayerDefinition.create(HorseModel.createBodyMesh(new CubeDeformation(0.1f)), 64, 64));
        $$0.put((Object)ModelLayers.HUSK, (Object)$$12);
        $$0.put((Object)ModelLayers.HUSK_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.HUSK_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.ILLUSIONER, (Object)$$8);
        $$0.put((Object)ModelLayers.IRON_GOLEM, (Object)IronGolemModel.createBodyLayer());
        $$0.put((Object)ModelLayers.LEASH_KNOT, (Object)LeashKnotModel.createBodyLayer());
        $$0.put((Object)ModelLayers.LLAMA, (Object)$$14);
        $$0.put((Object)ModelLayers.LLAMA_DECOR, (Object)LlamaModel.createBodyLayer(new CubeDeformation(0.5f)));
        $$0.put((Object)ModelLayers.LLAMA_SPIT, (Object)LlamaSpitModel.createBodyLayer());
        $$0.put((Object)ModelLayers.MAGMA_CUBE, (Object)LavaSlimeModel.createBodyLayer());
        $$0.put((Object)ModelLayers.MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.MOOSHROOM, (Object)$$9);
        $$0.put((Object)ModelLayers.MULE, (Object)ChestedHorseModel.createBodyLayer());
        $$0.put((Object)ModelLayers.OCELOT, (Object)$$10);
        $$0.put((Object)ModelLayers.PANDA, (Object)PandaModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PARROT, (Object)ParrotModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PHANTOM, (Object)PhantomModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PIG, (Object)PigModel.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.PIG_SADDLE, (Object)PigModel.createBodyLayer(new CubeDeformation(0.5f)));
        $$0.put((Object)ModelLayers.PIGLIN, (Object)$$11);
        $$0.put((Object)ModelLayers.PIGLIN_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.PIGLIN_OUTER_ARMOR, (Object)$$3);
        $$0.put((Object)ModelLayers.PIGLIN_BRUTE, (Object)$$11);
        $$0.put((Object)ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, (Object)$$3);
        $$0.put((Object)ModelLayers.PIGLIN_HEAD, (Object)$$122);
        $$0.put((Object)ModelLayers.PILLAGER, (Object)$$8);
        $$0.put((Object)ModelLayers.PLAYER, (Object)LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
        $$0.put((Object)ModelLayers.PLAYER_HEAD, (Object)$$13);
        $$0.put((Object)ModelLayers.PLAYER_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.PLAYER_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.PLAYER_SLIM, (Object)LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
        $$0.put((Object)ModelLayers.PLAYER_SLIM_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.PLAYER_SLIM_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.PLAYER_SPIN_ATTACK, (Object)SpinAttackEffectLayer.createLayer());
        $$0.put((Object)ModelLayers.POLAR_BEAR, (Object)PolarBearModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PUFFERFISH_BIG, (Object)PufferfishBigModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PUFFERFISH_MEDIUM, (Object)PufferfishMidModel.createBodyLayer());
        $$0.put((Object)ModelLayers.PUFFERFISH_SMALL, (Object)PufferfishSmallModel.createBodyLayer());
        $$0.put((Object)ModelLayers.RABBIT, (Object)RabbitModel.createBodyLayer());
        $$0.put((Object)ModelLayers.RAVAGER, (Object)RavagerModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SALMON, (Object)SalmonModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SHEEP, (Object)SheepModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SHEEP_FUR, (Object)SheepFurModel.createFurLayer());
        $$0.put((Object)ModelLayers.SHIELD, (Object)ShieldModel.createLayer());
        $$0.put((Object)ModelLayers.SHULKER, (Object)ShulkerModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SHULKER_BULLET, (Object)ShulkerBulletModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SILVERFISH, (Object)SilverfishModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SKELETON, (Object)$$17);
        $$0.put((Object)ModelLayers.SKELETON_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.SKELETON_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.SKELETON_HORSE, (Object)$$7);
        $$0.put((Object)ModelLayers.SKELETON_SKULL, (Object)$$6);
        $$0.put((Object)ModelLayers.SLIME, (Object)SlimeModel.createInnerBodyLayer());
        $$0.put((Object)ModelLayers.SLIME_OUTER, (Object)SlimeModel.createOuterBodyLayer());
        $$0.put((Object)ModelLayers.SNOW_GOLEM, (Object)SnowGolemModel.createBodyLayer());
        $$0.put((Object)ModelLayers.SPAWNER_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.SPIDER, (Object)$$19);
        $$0.put((Object)ModelLayers.SQUID, (Object)SquidModel.createBodyLayer());
        $$0.put((Object)ModelLayers.STRAY, (Object)$$17);
        $$0.put((Object)ModelLayers.STRAY_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.STRAY_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.STRAY_OUTER_LAYER, (Object)LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25f), 0.0f), 64, 32));
        $$0.put((Object)ModelLayers.STRIDER, (Object)$$15);
        $$0.put((Object)ModelLayers.STRIDER_SADDLE, (Object)$$15);
        $$0.put((Object)ModelLayers.TADPOLE, (Object)TadpoleModel.createBodyLayer());
        $$0.put((Object)ModelLayers.TNT_MINECART, (Object)$$5);
        $$0.put((Object)ModelLayers.TRADER_LLAMA, (Object)$$14);
        $$0.put((Object)ModelLayers.TRIDENT, (Object)TridentModel.createLayer());
        $$0.put((Object)ModelLayers.TROPICAL_FISH_LARGE, (Object)TropicalFishModelB.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.TROPICAL_FISH_LARGE_PATTERN, (Object)TropicalFishModelB.createBodyLayer(FISH_PATTERN_DEFORMATION));
        $$0.put((Object)ModelLayers.TROPICAL_FISH_SMALL, (Object)TropicalFishModelA.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.TROPICAL_FISH_SMALL_PATTERN, (Object)TropicalFishModelA.createBodyLayer(FISH_PATTERN_DEFORMATION));
        $$0.put((Object)ModelLayers.TURTLE, (Object)TurtleModel.createBodyLayer());
        $$0.put((Object)ModelLayers.VEX, (Object)VexModel.createBodyLayer());
        $$0.put((Object)ModelLayers.VILLAGER, (Object)$$18);
        $$0.put((Object)ModelLayers.VINDICATOR, (Object)$$8);
        $$0.put((Object)ModelLayers.WARDEN, (Object)WardenModel.createBodyLayer());
        $$0.put((Object)ModelLayers.WANDERING_TRADER, (Object)$$18);
        $$0.put((Object)ModelLayers.WITCH, (Object)WitchModel.createBodyLayer());
        $$0.put((Object)ModelLayers.WITHER, (Object)WitherBossModel.createBodyLayer(CubeDeformation.NONE));
        $$0.put((Object)ModelLayers.WITHER_ARMOR, (Object)WitherBossModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
        $$0.put((Object)ModelLayers.WITHER_SKULL, (Object)WitherSkullRenderer.createSkullLayer());
        $$0.put((Object)ModelLayers.WITHER_SKELETON, (Object)$$17);
        $$0.put((Object)ModelLayers.WITHER_SKELETON_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.WITHER_SKELETON_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.WITHER_SKELETON_SKULL, (Object)$$6);
        $$0.put((Object)ModelLayers.WOLF, (Object)WolfModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ZOGLIN, (Object)$$16);
        $$0.put((Object)ModelLayers.ZOMBIE, (Object)$$12);
        $$0.put((Object)ModelLayers.ZOMBIE_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.ZOMBIE_OUTER_ARMOR, (Object)$$22);
        $$0.put((Object)ModelLayers.ZOMBIE_HEAD, (Object)$$13);
        $$0.put((Object)ModelLayers.ZOMBIE_HORSE, (Object)$$7);
        $$0.put((Object)ModelLayers.ZOMBIE_VILLAGER, (Object)ZombieVillagerModel.createBodyLayer());
        $$0.put((Object)ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, (Object)ZombieVillagerModel.createArmorLayer(INNER_ARMOR_DEFORMATION));
        $$0.put((Object)ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, (Object)ZombieVillagerModel.createArmorLayer(OUTER_ARMOR_DEFORMATION));
        $$0.put((Object)ModelLayers.ZOMBIFIED_PIGLIN, (Object)$$11);
        $$0.put((Object)ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, (Object)$$4);
        $$0.put((Object)ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, (Object)$$3);
        LayerDefinition $$20 = BoatModel.createBodyModel();
        LayerDefinition $$21 = ChestBoatModel.createBodyModel();
        LayerDefinition $$222 = RaftModel.createBodyModel();
        LayerDefinition $$23 = ChestRaftModel.createBodyModel();
        for (Boat.Type $$24 : Boat.Type.values()) {
            if ($$24 == Boat.Type.BAMBOO) {
                $$0.put((Object)ModelLayers.createBoatModelName($$24), (Object)$$222);
                $$0.put((Object)ModelLayers.createChestBoatModelName($$24), (Object)$$23);
                continue;
            }
            $$0.put((Object)ModelLayers.createBoatModelName($$24), (Object)$$20);
            $$0.put((Object)ModelLayers.createChestBoatModelName($$24), (Object)$$21);
        }
        LayerDefinition $$25 = SignRenderer.createSignLayer();
        WoodType.values().forEach($$2 -> $$0.put((Object)ModelLayers.createSignModelName($$2), (Object)$$25));
        LayerDefinition $$26 = HangingSignRenderer.createHangingSignLayer();
        WoodType.values().forEach($$2 -> $$0.put((Object)ModelLayers.createHangingSignModelName($$2), (Object)$$26));
        ImmutableMap $$27 = $$0.build();
        List $$28 = (List)ModelLayers.getKnownLocations().filter($$1 -> !$$27.containsKey($$1)).collect(Collectors.toList());
        if (!$$28.isEmpty()) {
            throw new IllegalStateException("Missing layer definitions: " + $$28);
        }
        return $$27;
    }
}