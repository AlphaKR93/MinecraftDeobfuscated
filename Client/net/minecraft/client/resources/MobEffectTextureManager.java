/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectTextureManager
extends TextureAtlasHolder {
    public MobEffectTextureManager(TextureManager $$0) {
        super($$0, new ResourceLocation("textures/atlas/mob_effects.png"), new ResourceLocation("mob_effects"));
    }

    public TextureAtlasSprite get(MobEffect $$0) {
        return this.getSprite(BuiltInRegistries.MOB_EFFECT.getKey($$0));
    }
}