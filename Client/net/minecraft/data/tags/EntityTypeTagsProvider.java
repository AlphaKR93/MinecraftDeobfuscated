/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider
extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public EntityTypeTagsProvider(PackOutput $$02, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$02, Registries.ENTITY_TYPE, $$1, $$0 -> $$0.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.SKELETONS)).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.RAIDERS)).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.BEEHIVE_INHABITORS)).add(EntityType.BEE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.ARROWS)).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.IMPACT_PROJECTILES)).addTag((TagKey)EntityTypeTags.ARROWS)).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.AXOLOTL_HUNT_TARGETS)).add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES)).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)EntityTypeTags.FROG_FOOD)).add(EntityType.SLIME, EntityType.MAGMA_CUBE);
    }
}