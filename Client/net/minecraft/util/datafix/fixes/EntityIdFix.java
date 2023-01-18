/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class EntityIdFix
extends DataFix {
    private static final Map<String, String> ID_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), $$0 -> {
        $$0.put((Object)"AreaEffectCloud", (Object)"minecraft:area_effect_cloud");
        $$0.put((Object)"ArmorStand", (Object)"minecraft:armor_stand");
        $$0.put((Object)"Arrow", (Object)"minecraft:arrow");
        $$0.put((Object)"Bat", (Object)"minecraft:bat");
        $$0.put((Object)"Blaze", (Object)"minecraft:blaze");
        $$0.put((Object)"Boat", (Object)"minecraft:boat");
        $$0.put((Object)"CaveSpider", (Object)"minecraft:cave_spider");
        $$0.put((Object)"Chicken", (Object)"minecraft:chicken");
        $$0.put((Object)"Cow", (Object)"minecraft:cow");
        $$0.put((Object)"Creeper", (Object)"minecraft:creeper");
        $$0.put((Object)"Donkey", (Object)"minecraft:donkey");
        $$0.put((Object)"DragonFireball", (Object)"minecraft:dragon_fireball");
        $$0.put((Object)"ElderGuardian", (Object)"minecraft:elder_guardian");
        $$0.put((Object)"EnderCrystal", (Object)"minecraft:ender_crystal");
        $$0.put((Object)"EnderDragon", (Object)"minecraft:ender_dragon");
        $$0.put((Object)"Enderman", (Object)"minecraft:enderman");
        $$0.put((Object)"Endermite", (Object)"minecraft:endermite");
        $$0.put((Object)"EyeOfEnderSignal", (Object)"minecraft:eye_of_ender_signal");
        $$0.put((Object)"FallingSand", (Object)"minecraft:falling_block");
        $$0.put((Object)"Fireball", (Object)"minecraft:fireball");
        $$0.put((Object)"FireworksRocketEntity", (Object)"minecraft:fireworks_rocket");
        $$0.put((Object)"Ghast", (Object)"minecraft:ghast");
        $$0.put((Object)"Giant", (Object)"minecraft:giant");
        $$0.put((Object)"Guardian", (Object)"minecraft:guardian");
        $$0.put((Object)"Horse", (Object)"minecraft:horse");
        $$0.put((Object)"Husk", (Object)"minecraft:husk");
        $$0.put((Object)"Item", (Object)"minecraft:item");
        $$0.put((Object)"ItemFrame", (Object)"minecraft:item_frame");
        $$0.put((Object)"LavaSlime", (Object)"minecraft:magma_cube");
        $$0.put((Object)"LeashKnot", (Object)"minecraft:leash_knot");
        $$0.put((Object)"MinecartChest", (Object)"minecraft:chest_minecart");
        $$0.put((Object)"MinecartCommandBlock", (Object)"minecraft:commandblock_minecart");
        $$0.put((Object)"MinecartFurnace", (Object)"minecraft:furnace_minecart");
        $$0.put((Object)"MinecartHopper", (Object)"minecraft:hopper_minecart");
        $$0.put((Object)"MinecartRideable", (Object)"minecraft:minecart");
        $$0.put((Object)"MinecartSpawner", (Object)"minecraft:spawner_minecart");
        $$0.put((Object)"MinecartTNT", (Object)"minecraft:tnt_minecart");
        $$0.put((Object)"Mule", (Object)"minecraft:mule");
        $$0.put((Object)"MushroomCow", (Object)"minecraft:mooshroom");
        $$0.put((Object)"Ozelot", (Object)"minecraft:ocelot");
        $$0.put((Object)"Painting", (Object)"minecraft:painting");
        $$0.put((Object)"Pig", (Object)"minecraft:pig");
        $$0.put((Object)"PigZombie", (Object)"minecraft:zombie_pigman");
        $$0.put((Object)"PolarBear", (Object)"minecraft:polar_bear");
        $$0.put((Object)"PrimedTnt", (Object)"minecraft:tnt");
        $$0.put((Object)"Rabbit", (Object)"minecraft:rabbit");
        $$0.put((Object)"Sheep", (Object)"minecraft:sheep");
        $$0.put((Object)"Shulker", (Object)"minecraft:shulker");
        $$0.put((Object)"ShulkerBullet", (Object)"minecraft:shulker_bullet");
        $$0.put((Object)"Silverfish", (Object)"minecraft:silverfish");
        $$0.put((Object)"Skeleton", (Object)"minecraft:skeleton");
        $$0.put((Object)"SkeletonHorse", (Object)"minecraft:skeleton_horse");
        $$0.put((Object)"Slime", (Object)"minecraft:slime");
        $$0.put((Object)"SmallFireball", (Object)"minecraft:small_fireball");
        $$0.put((Object)"SnowMan", (Object)"minecraft:snowman");
        $$0.put((Object)"Snowball", (Object)"minecraft:snowball");
        $$0.put((Object)"SpectralArrow", (Object)"minecraft:spectral_arrow");
        $$0.put((Object)"Spider", (Object)"minecraft:spider");
        $$0.put((Object)"Squid", (Object)"minecraft:squid");
        $$0.put((Object)"Stray", (Object)"minecraft:stray");
        $$0.put((Object)"ThrownEgg", (Object)"minecraft:egg");
        $$0.put((Object)"ThrownEnderpearl", (Object)"minecraft:ender_pearl");
        $$0.put((Object)"ThrownExpBottle", (Object)"minecraft:xp_bottle");
        $$0.put((Object)"ThrownPotion", (Object)"minecraft:potion");
        $$0.put((Object)"Villager", (Object)"minecraft:villager");
        $$0.put((Object)"VillagerGolem", (Object)"minecraft:villager_golem");
        $$0.put((Object)"Witch", (Object)"minecraft:witch");
        $$0.put((Object)"WitherBoss", (Object)"minecraft:wither");
        $$0.put((Object)"WitherSkeleton", (Object)"minecraft:wither_skeleton");
        $$0.put((Object)"WitherSkull", (Object)"minecraft:wither_skull");
        $$0.put((Object)"Wolf", (Object)"minecraft:wolf");
        $$0.put((Object)"XPOrb", (Object)"minecraft:xp_orb");
        $$0.put((Object)"Zombie", (Object)"minecraft:zombie");
        $$0.put((Object)"ZombieHorse", (Object)"minecraft:zombie_horse");
        $$0.put((Object)"ZombieVillager", (Object)"minecraft:zombie_villager");
    });

    public EntityIdFix(Schema $$0, boolean $$1) {
        super($$0, $$1);
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType $$02 = this.getInputSchema().findChoiceType(References.ENTITY);
        TaggedChoice.TaggedChoiceType $$1 = this.getOutputSchema().findChoiceType(References.ENTITY);
        Type $$2 = this.getInputSchema().getType(References.ITEM_STACK);
        Type $$3 = this.getOutputSchema().getType(References.ITEM_STACK);
        return TypeRewriteRule.seq((TypeRewriteRule)this.convertUnchecked("item stack entity name hook converter", $$2, $$3), (TypeRewriteRule)this.fixTypeEverywhere("EntityIdFix", (Type)$$02, (Type)$$1, $$0 -> $$02 -> $$02.mapFirst($$0 -> (String)ID_MAP.getOrDefault($$0, $$0))));
    }
}