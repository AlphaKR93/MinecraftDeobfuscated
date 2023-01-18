/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 */
package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;

public record SpriteSourceType(Codec<? extends SpriteSource> codec) {
}