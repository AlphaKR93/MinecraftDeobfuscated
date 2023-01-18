/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Map
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.AllayRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.AxolotlRenderer;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EvokerFangsRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.GlowSquidRenderer;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.HoglinRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.MagmaCubeRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.SpectralArrowRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.StriderRenderer;
import net.minecraft.client.renderer.entity.TadpoleRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.TurtleRenderer;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.WardenRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.ZoglinRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class EntityRenderers {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_PLAYER_MODEL = "default";
    private static final Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS = Maps.newHashMap();
    private static final Map<String, EntityRendererProvider<AbstractClientPlayer>> PLAYER_PROVIDERS = ImmutableMap.of((Object)"default", $$0 -> new PlayerRenderer($$0, false), (Object)"slim", $$0 -> new PlayerRenderer($$0, true));

    private static <T extends Entity> void register(EntityType<? extends T> $$0, EntityRendererProvider<T> $$1) {
        PROVIDERS.put($$0, $$1);
    }

    public static Map<EntityType<?>, EntityRenderer<?>> createEntityRenderers(EntityRendererProvider.Context $$0) {
        ImmutableMap.Builder $$1 = ImmutableMap.builder();
        PROVIDERS.forEach(($$2, $$3) -> {
            try {
                $$1.put($$2, $$3.create($$0));
            }
            catch (Exception $$4) {
                throw new IllegalArgumentException("Failed to create model for " + BuiltInRegistries.ENTITY_TYPE.getKey((EntityType<?>)$$2), (Throwable)$$4);
            }
        });
        return $$1.build();
    }

    public static Map<String, EntityRenderer<? extends Player>> createPlayerRenderers(EntityRendererProvider.Context $$0) {
        ImmutableMap.Builder $$1 = ImmutableMap.builder();
        PLAYER_PROVIDERS.forEach(($$2, $$3) -> {
            try {
                $$1.put($$2, $$3.create($$0));
            }
            catch (Exception $$4) {
                throw new IllegalArgumentException("Failed to create player model for " + $$2, (Throwable)$$4);
            }
        });
        return $$1.build();
    }

    public static boolean validateRegistrations() {
        boolean $$0 = true;
        for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || PROVIDERS.containsKey((Object)entityType)) continue;
            LOGGER.warn("No renderer registered for {}", (Object)BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
            $$0 = false;
        }
        return !$$0;
    }

    static {
        EntityRenderers.register(EntityType.ALLAY, AllayRenderer::new);
        EntityRenderers.register(EntityType.AREA_EFFECT_CLOUD, NoopRenderer::new);
        EntityRenderers.register(EntityType.ARMOR_STAND, ArmorStandRenderer::new);
        EntityRenderers.register(EntityType.ARROW, TippableArrowRenderer::new);
        EntityRenderers.register(EntityType.AXOLOTL, AxolotlRenderer::new);
        EntityRenderers.register(EntityType.BAT, BatRenderer::new);
        EntityRenderers.register(EntityType.BEE, BeeRenderer::new);
        EntityRenderers.register(EntityType.BLAZE, BlazeRenderer::new);
        EntityRenderers.register(EntityType.BOAT, $$0 -> new BoatRenderer($$0, false));
        EntityRenderers.register(EntityType.CAT, CatRenderer::new);
        EntityRenderers.register(EntityType.CAMEL, $$0 -> new CamelRenderer($$0, ModelLayers.CAMEL));
        EntityRenderers.register(EntityType.CAVE_SPIDER, CaveSpiderRenderer::new);
        EntityRenderers.register(EntityType.CHEST_BOAT, $$0 -> new BoatRenderer($$0, true));
        EntityRenderers.register(EntityType.CHEST_MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.CHEST_MINECART));
        EntityRenderers.register(EntityType.CHICKEN, ChickenRenderer::new);
        EntityRenderers.register(EntityType.COD, CodRenderer::new);
        EntityRenderers.register(EntityType.COMMAND_BLOCK_MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.COMMAND_BLOCK_MINECART));
        EntityRenderers.register(EntityType.COW, CowRenderer::new);
        EntityRenderers.register(EntityType.CREEPER, CreeperRenderer::new);
        EntityRenderers.register(EntityType.DOLPHIN, DolphinRenderer::new);
        EntityRenderers.register(EntityType.DONKEY, $$0 -> new ChestedHorseRenderer($$0, 0.87f, ModelLayers.DONKEY));
        EntityRenderers.register(EntityType.DRAGON_FIREBALL, DragonFireballRenderer::new);
        EntityRenderers.register(EntityType.DROWNED, DrownedRenderer::new);
        EntityRenderers.register(EntityType.EGG, ThrownItemRenderer::new);
        EntityRenderers.register(EntityType.ELDER_GUARDIAN, ElderGuardianRenderer::new);
        EntityRenderers.register(EntityType.ENDERMAN, EndermanRenderer::new);
        EntityRenderers.register(EntityType.ENDERMITE, EndermiteRenderer::new);
        EntityRenderers.register(EntityType.ENDER_DRAGON, EnderDragonRenderer::new);
        EntityRenderers.register(EntityType.ENDER_PEARL, ThrownItemRenderer::new);
        EntityRenderers.register(EntityType.END_CRYSTAL, EndCrystalRenderer::new);
        EntityRenderers.register(EntityType.EVOKER, EvokerRenderer::new);
        EntityRenderers.register(EntityType.EVOKER_FANGS, EvokerFangsRenderer::new);
        EntityRenderers.register(EntityType.EXPERIENCE_BOTTLE, ThrownItemRenderer::new);
        EntityRenderers.register(EntityType.EXPERIENCE_ORB, ExperienceOrbRenderer::new);
        EntityRenderers.register(EntityType.EYE_OF_ENDER, $$0 -> new ThrownItemRenderer($$0, 1.0f, true));
        EntityRenderers.register(EntityType.FALLING_BLOCK, FallingBlockRenderer::new);
        EntityRenderers.register(EntityType.FIREBALL, $$0 -> new ThrownItemRenderer($$0, 3.0f, true));
        EntityRenderers.register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
        EntityRenderers.register(EntityType.FISHING_BOBBER, FishingHookRenderer::new);
        EntityRenderers.register(EntityType.FOX, FoxRenderer::new);
        EntityRenderers.register(EntityType.FROG, FrogRenderer::new);
        EntityRenderers.register(EntityType.FURNACE_MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.FURNACE_MINECART));
        EntityRenderers.register(EntityType.GHAST, GhastRenderer::new);
        EntityRenderers.register(EntityType.GIANT, $$0 -> new GiantMobRenderer($$0, 6.0f));
        EntityRenderers.register(EntityType.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
        EntityRenderers.register(EntityType.GLOW_SQUID, $$0 -> new GlowSquidRenderer($$0, new SquidModel<GlowSquid>($$0.bakeLayer(ModelLayers.GLOW_SQUID))));
        EntityRenderers.register(EntityType.GOAT, GoatRenderer::new);
        EntityRenderers.register(EntityType.GUARDIAN, GuardianRenderer::new);
        EntityRenderers.register(EntityType.HOGLIN, HoglinRenderer::new);
        EntityRenderers.register(EntityType.HOPPER_MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.HOPPER_MINECART));
        EntityRenderers.register(EntityType.HORSE, HorseRenderer::new);
        EntityRenderers.register(EntityType.HUSK, HuskRenderer::new);
        EntityRenderers.register(EntityType.ILLUSIONER, IllusionerRenderer::new);
        EntityRenderers.register(EntityType.IRON_GOLEM, IronGolemRenderer::new);
        EntityRenderers.register(EntityType.ITEM, ItemEntityRenderer::new);
        EntityRenderers.register(EntityType.ITEM_FRAME, ItemFrameRenderer::new);
        EntityRenderers.register(EntityType.LEASH_KNOT, LeashKnotRenderer::new);
        EntityRenderers.register(EntityType.LIGHTNING_BOLT, LightningBoltRenderer::new);
        EntityRenderers.register(EntityType.LLAMA, $$0 -> new LlamaRenderer($$0, ModelLayers.LLAMA));
        EntityRenderers.register(EntityType.LLAMA_SPIT, LlamaSpitRenderer::new);
        EntityRenderers.register(EntityType.MAGMA_CUBE, MagmaCubeRenderer::new);
        EntityRenderers.register(EntityType.MARKER, NoopRenderer::new);
        EntityRenderers.register(EntityType.MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.MINECART));
        EntityRenderers.register(EntityType.MOOSHROOM, MushroomCowRenderer::new);
        EntityRenderers.register(EntityType.MULE, $$0 -> new ChestedHorseRenderer($$0, 0.92f, ModelLayers.MULE));
        EntityRenderers.register(EntityType.OCELOT, OcelotRenderer::new);
        EntityRenderers.register(EntityType.PAINTING, PaintingRenderer::new);
        EntityRenderers.register(EntityType.PANDA, PandaRenderer::new);
        EntityRenderers.register(EntityType.PARROT, ParrotRenderer::new);
        EntityRenderers.register(EntityType.PHANTOM, PhantomRenderer::new);
        EntityRenderers.register(EntityType.PIG, PigRenderer::new);
        EntityRenderers.register(EntityType.PIGLIN, $$0 -> new PiglinRenderer($$0, ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false));
        EntityRenderers.register(EntityType.PIGLIN_BRUTE, $$0 -> new PiglinRenderer($$0, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, false));
        EntityRenderers.register(EntityType.PILLAGER, PillagerRenderer::new);
        EntityRenderers.register(EntityType.POLAR_BEAR, PolarBearRenderer::new);
        EntityRenderers.register(EntityType.POTION, ThrownItemRenderer::new);
        EntityRenderers.register(EntityType.PUFFERFISH, PufferfishRenderer::new);
        EntityRenderers.register(EntityType.RABBIT, RabbitRenderer::new);
        EntityRenderers.register(EntityType.RAVAGER, RavagerRenderer::new);
        EntityRenderers.register(EntityType.SALMON, SalmonRenderer::new);
        EntityRenderers.register(EntityType.SHEEP, SheepRenderer::new);
        EntityRenderers.register(EntityType.SHULKER, ShulkerRenderer::new);
        EntityRenderers.register(EntityType.SHULKER_BULLET, ShulkerBulletRenderer::new);
        EntityRenderers.register(EntityType.SILVERFISH, SilverfishRenderer::new);
        EntityRenderers.register(EntityType.SKELETON, SkeletonRenderer::new);
        EntityRenderers.register(EntityType.SKELETON_HORSE, $$0 -> new UndeadHorseRenderer($$0, ModelLayers.SKELETON_HORSE));
        EntityRenderers.register(EntityType.SLIME, SlimeRenderer::new);
        EntityRenderers.register(EntityType.SMALL_FIREBALL, $$0 -> new ThrownItemRenderer($$0, 0.75f, true));
        EntityRenderers.register(EntityType.SNOWBALL, ThrownItemRenderer::new);
        EntityRenderers.register(EntityType.SNOW_GOLEM, SnowGolemRenderer::new);
        EntityRenderers.register(EntityType.SPAWNER_MINECART, $$0 -> new MinecartRenderer($$0, ModelLayers.SPAWNER_MINECART));
        EntityRenderers.register(EntityType.SPECTRAL_ARROW, SpectralArrowRenderer::new);
        EntityRenderers.register(EntityType.SPIDER, SpiderRenderer::new);
        EntityRenderers.register(EntityType.SQUID, $$0 -> new SquidRenderer($$0, new SquidModel($$0.bakeLayer(ModelLayers.SQUID))));
        EntityRenderers.register(EntityType.STRAY, StrayRenderer::new);
        EntityRenderers.register(EntityType.STRIDER, StriderRenderer::new);
        EntityRenderers.register(EntityType.TADPOLE, TadpoleRenderer::new);
        EntityRenderers.register(EntityType.TNT, TntRenderer::new);
        EntityRenderers.register(EntityType.TNT_MINECART, TntMinecartRenderer::new);
        EntityRenderers.register(EntityType.TRADER_LLAMA, $$0 -> new LlamaRenderer($$0, ModelLayers.TRADER_LLAMA));
        EntityRenderers.register(EntityType.TRIDENT, ThrownTridentRenderer::new);
        EntityRenderers.register(EntityType.TROPICAL_FISH, TropicalFishRenderer::new);
        EntityRenderers.register(EntityType.TURTLE, TurtleRenderer::new);
        EntityRenderers.register(EntityType.VEX, VexRenderer::new);
        EntityRenderers.register(EntityType.VILLAGER, VillagerRenderer::new);
        EntityRenderers.register(EntityType.VINDICATOR, VindicatorRenderer::new);
        EntityRenderers.register(EntityType.WARDEN, WardenRenderer::new);
        EntityRenderers.register(EntityType.WANDERING_TRADER, WanderingTraderRenderer::new);
        EntityRenderers.register(EntityType.WITCH, WitchRenderer::new);
        EntityRenderers.register(EntityType.WITHER, WitherBossRenderer::new);
        EntityRenderers.register(EntityType.WITHER_SKELETON, WitherSkeletonRenderer::new);
        EntityRenderers.register(EntityType.WITHER_SKULL, WitherSkullRenderer::new);
        EntityRenderers.register(EntityType.WOLF, WolfRenderer::new);
        EntityRenderers.register(EntityType.ZOGLIN, ZoglinRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIE, ZombieRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIE_HORSE, $$0 -> new UndeadHorseRenderer($$0, ModelLayers.ZOMBIE_HORSE));
        EntityRenderers.register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
        EntityRenderers.register(EntityType.ZOMBIFIED_PIGLIN, $$0 -> new PiglinRenderer($$0, ModelLayers.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true));
    }
}