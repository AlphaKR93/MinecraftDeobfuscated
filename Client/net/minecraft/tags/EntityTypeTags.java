/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class EntityTypeTags {
    public static final TagKey<EntityType<?>> SKELETONS = EntityTypeTags.create("skeletons");
    public static final TagKey<EntityType<?>> RAIDERS = EntityTypeTags.create("raiders");
    public static final TagKey<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.create("beehive_inhabitors");
    public static final TagKey<EntityType<?>> ARROWS = EntityTypeTags.create("arrows");
    public static final TagKey<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.create("impact_projectiles");
    public static final TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = EntityTypeTags.create("powder_snow_walkable_mobs");
    public static final TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = EntityTypeTags.create("axolotl_always_hostiles");
    public static final TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = EntityTypeTags.create("axolotl_hunt_targets");
    public static final TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = EntityTypeTags.create("freeze_immune_entity_types");
    public static final TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = EntityTypeTags.create("freeze_hurts_extra_types");
    public static final TagKey<EntityType<?>> FROG_FOOD = EntityTypeTags.create("frog_food");

    private EntityTypeTags() {
    }

    private static TagKey<EntityType<?>> create(String $$0) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation($$0));
    }
}