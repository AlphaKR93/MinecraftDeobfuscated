/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.Set
 *  org.slf4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class EntityUUIDFix
extends AbstractUUIDFix {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> ABSTRACT_HORSES = Sets.newHashSet();
    private static final Set<String> TAMEABLE_ANIMALS = Sets.newHashSet();
    private static final Set<String> ANIMALS = Sets.newHashSet();
    private static final Set<String> MOBS = Sets.newHashSet();
    private static final Set<String> LIVING_ENTITIES = Sets.newHashSet();
    private static final Set<String> PROJECTILES = Sets.newHashSet();

    public EntityUUIDFix(Schema $$0) {
        super($$0, References.ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.typeReference), $$0 -> {
            $$0 = $$0.update(DSL.remainderFinder(), EntityUUIDFix::updateEntityUUID);
            for (String $$1 : ABSTRACT_HORSES) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$1, EntityUUIDFix::updateAnimalOwner);
            }
            for (String $$2 : TAMEABLE_ANIMALS) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$2, EntityUUIDFix::updateAnimalOwner);
            }
            for (String $$3 : ANIMALS) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$3, EntityUUIDFix::updateAnimal);
            }
            for (String $$4 : MOBS) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$4, EntityUUIDFix::updateMob);
            }
            for (String $$5 : LIVING_ENTITIES) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$5, EntityUUIDFix::updateLivingEntity);
            }
            for (String $$6 : PROJECTILES) {
                $$0 = this.updateNamedChoice((Typed<?>)$$0, $$6, EntityUUIDFix::updateProjectile);
            }
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:bee", EntityUUIDFix::updateHurtBy);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:zombified_piglin", EntityUUIDFix::updateHurtBy);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:fox", EntityUUIDFix::updateFox);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:item", EntityUUIDFix::updateItem);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:shulker_bullet", EntityUUIDFix::updateShulkerBullet);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:area_effect_cloud", EntityUUIDFix::updateAreaEffectCloud);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:zombie_villager", EntityUUIDFix::updateZombieVillager);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:evoker_fangs", EntityUUIDFix::updateEvokerFangs);
            $$0 = this.updateNamedChoice((Typed<?>)$$0, "minecraft:piglin", EntityUUIDFix::updatePiglin);
            return $$0;
        });
    }

    private static Dynamic<?> updatePiglin(Dynamic<?> $$02) {
        return $$02.update("Brain", $$0 -> $$0.update("memories", $$02 -> $$02.update("minecraft:angry_at", $$0 -> (Dynamic)EntityUUIDFix.replaceUUIDString($$0, "value", "value").orElseGet(() -> {
            LOGGER.warn("angry_at has no value.");
            return $$0;
        }))));
    }

    private static Dynamic<?> updateEvokerFangs(Dynamic<?> $$0) {
        return (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "OwnerUUID", "Owner").orElse($$0);
    }

    private static Dynamic<?> updateZombieVillager(Dynamic<?> $$0) {
        return (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "ConversionPlayer", "ConversionPlayer").orElse($$0);
    }

    private static Dynamic<?> updateAreaEffectCloud(Dynamic<?> $$0) {
        return (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "OwnerUUID", "Owner").orElse($$0);
    }

    private static Dynamic<?> updateShulkerBullet(Dynamic<?> $$0) {
        $$0 = (Dynamic)EntityUUIDFix.replaceUUIDMLTag($$0, "Owner", "Owner").orElse($$0);
        return (Dynamic)EntityUUIDFix.replaceUUIDMLTag($$0, "Target", "Target").orElse((Object)$$0);
    }

    private static Dynamic<?> updateItem(Dynamic<?> $$0) {
        $$0 = (Dynamic)EntityUUIDFix.replaceUUIDMLTag($$0, "Owner", "Owner").orElse($$0);
        return (Dynamic)EntityUUIDFix.replaceUUIDMLTag($$0, "Thrower", "Thrower").orElse((Object)$$0);
    }

    private static Dynamic<?> updateFox(Dynamic<?> $$0) {
        Optional $$12 = $$0.get("TrustedUUIDs").result().map($$1 -> $$0.createList($$1.asStream().map($$0 -> (Dynamic)EntityUUIDFix.createUUIDFromML($$0).orElseGet(() -> {
            LOGGER.warn("Trusted contained invalid data.");
            return $$0;
        }))));
        return (Dynamic)DataFixUtils.orElse((Optional)$$12.map($$1 -> $$0.remove("TrustedUUIDs").set("Trusted", $$1)), $$0);
    }

    private static Dynamic<?> updateHurtBy(Dynamic<?> $$0) {
        return (Dynamic)EntityUUIDFix.replaceUUIDString($$0, "HurtBy", "HurtBy").orElse($$0);
    }

    private static Dynamic<?> updateAnimalOwner(Dynamic<?> $$0) {
        Dynamic<?> $$1 = EntityUUIDFix.updateAnimal($$0);
        return (Dynamic)EntityUUIDFix.replaceUUIDString($$1, "OwnerUUID", "Owner").orElse($$1);
    }

    private static Dynamic<?> updateAnimal(Dynamic<?> $$0) {
        Dynamic<?> $$1 = EntityUUIDFix.updateMob($$0);
        return (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$1, "LoveCause", "LoveCause").orElse($$1);
    }

    private static Dynamic<?> updateMob(Dynamic<?> $$02) {
        return EntityUUIDFix.updateLivingEntity($$02).update("Leash", $$0 -> (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "UUID", "UUID").orElse($$0));
    }

    public static Dynamic<?> updateLivingEntity(Dynamic<?> $$0) {
        return $$0.update("Attributes", $$1 -> $$0.createList($$1.asStream().map($$0 -> $$0.update("Modifiers", $$1 -> $$0.createList($$1.asStream().map($$0 -> (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "UUID", "UUID").orElse($$0)))))));
    }

    private static Dynamic<?> updateProjectile(Dynamic<?> $$0) {
        return (Dynamic)DataFixUtils.orElse((Optional)$$0.get("OwnerUUID").result().map($$1 -> $$0.remove("OwnerUUID").set("Owner", $$1)), $$0);
    }

    public static Dynamic<?> updateEntityUUID(Dynamic<?> $$0) {
        return (Dynamic)EntityUUIDFix.replaceUUIDLeastMost($$0, "UUID", "UUID").orElse($$0);
    }

    static {
        ABSTRACT_HORSES.add((Object)"minecraft:donkey");
        ABSTRACT_HORSES.add((Object)"minecraft:horse");
        ABSTRACT_HORSES.add((Object)"minecraft:llama");
        ABSTRACT_HORSES.add((Object)"minecraft:mule");
        ABSTRACT_HORSES.add((Object)"minecraft:skeleton_horse");
        ABSTRACT_HORSES.add((Object)"minecraft:trader_llama");
        ABSTRACT_HORSES.add((Object)"minecraft:zombie_horse");
        TAMEABLE_ANIMALS.add((Object)"minecraft:cat");
        TAMEABLE_ANIMALS.add((Object)"minecraft:parrot");
        TAMEABLE_ANIMALS.add((Object)"minecraft:wolf");
        ANIMALS.add((Object)"minecraft:bee");
        ANIMALS.add((Object)"minecraft:chicken");
        ANIMALS.add((Object)"minecraft:cow");
        ANIMALS.add((Object)"minecraft:fox");
        ANIMALS.add((Object)"minecraft:mooshroom");
        ANIMALS.add((Object)"minecraft:ocelot");
        ANIMALS.add((Object)"minecraft:panda");
        ANIMALS.add((Object)"minecraft:pig");
        ANIMALS.add((Object)"minecraft:polar_bear");
        ANIMALS.add((Object)"minecraft:rabbit");
        ANIMALS.add((Object)"minecraft:sheep");
        ANIMALS.add((Object)"minecraft:turtle");
        ANIMALS.add((Object)"minecraft:hoglin");
        MOBS.add((Object)"minecraft:bat");
        MOBS.add((Object)"minecraft:blaze");
        MOBS.add((Object)"minecraft:cave_spider");
        MOBS.add((Object)"minecraft:cod");
        MOBS.add((Object)"minecraft:creeper");
        MOBS.add((Object)"minecraft:dolphin");
        MOBS.add((Object)"minecraft:drowned");
        MOBS.add((Object)"minecraft:elder_guardian");
        MOBS.add((Object)"minecraft:ender_dragon");
        MOBS.add((Object)"minecraft:enderman");
        MOBS.add((Object)"minecraft:endermite");
        MOBS.add((Object)"minecraft:evoker");
        MOBS.add((Object)"minecraft:ghast");
        MOBS.add((Object)"minecraft:giant");
        MOBS.add((Object)"minecraft:guardian");
        MOBS.add((Object)"minecraft:husk");
        MOBS.add((Object)"minecraft:illusioner");
        MOBS.add((Object)"minecraft:magma_cube");
        MOBS.add((Object)"minecraft:pufferfish");
        MOBS.add((Object)"minecraft:zombified_piglin");
        MOBS.add((Object)"minecraft:salmon");
        MOBS.add((Object)"minecraft:shulker");
        MOBS.add((Object)"minecraft:silverfish");
        MOBS.add((Object)"minecraft:skeleton");
        MOBS.add((Object)"minecraft:slime");
        MOBS.add((Object)"minecraft:snow_golem");
        MOBS.add((Object)"minecraft:spider");
        MOBS.add((Object)"minecraft:squid");
        MOBS.add((Object)"minecraft:stray");
        MOBS.add((Object)"minecraft:tropical_fish");
        MOBS.add((Object)"minecraft:vex");
        MOBS.add((Object)"minecraft:villager");
        MOBS.add((Object)"minecraft:iron_golem");
        MOBS.add((Object)"minecraft:vindicator");
        MOBS.add((Object)"minecraft:pillager");
        MOBS.add((Object)"minecraft:wandering_trader");
        MOBS.add((Object)"minecraft:witch");
        MOBS.add((Object)"minecraft:wither");
        MOBS.add((Object)"minecraft:wither_skeleton");
        MOBS.add((Object)"minecraft:zombie");
        MOBS.add((Object)"minecraft:zombie_villager");
        MOBS.add((Object)"minecraft:phantom");
        MOBS.add((Object)"minecraft:ravager");
        MOBS.add((Object)"minecraft:piglin");
        LIVING_ENTITIES.add((Object)"minecraft:armor_stand");
        PROJECTILES.add((Object)"minecraft:arrow");
        PROJECTILES.add((Object)"minecraft:dragon_fireball");
        PROJECTILES.add((Object)"minecraft:firework_rocket");
        PROJECTILES.add((Object)"minecraft:fireball");
        PROJECTILES.add((Object)"minecraft:llama_spit");
        PROJECTILES.add((Object)"minecraft:small_fireball");
        PROJECTILES.add((Object)"minecraft:snowball");
        PROJECTILES.add((Object)"minecraft:spectral_arrow");
        PROJECTILES.add((Object)"minecraft:egg");
        PROJECTILES.add((Object)"minecraft:ender_pearl");
        PROJECTILES.add((Object)"minecraft:experience_bottle");
        PROJECTILES.add((Object)"minecraft:potion");
        PROJECTILES.add((Object)"minecraft:trident");
        PROJECTILES.add((Object)"minecraft:wither_skull");
    }
}