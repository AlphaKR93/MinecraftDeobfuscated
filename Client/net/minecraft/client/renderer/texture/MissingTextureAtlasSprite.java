/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;

public final class MissingTextureAtlasSprite {
    private static final int MISSING_IMAGE_WIDTH = 16;
    private static final int MISSING_IMAGE_HEIGHT = 16;
    private static final String MISSING_TEXTURE_NAME = "missingno";
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    private static final AnimationMetadataSection EMPTY_ANIMATION_META = new AnimationMetadataSection((List<AnimationFrame>)ImmutableList.of((Object)new AnimationFrame(0, -1)), 16, 16, 1, false);
    @Nullable
    private static DynamicTexture missingTexture;

    private static NativeImage generateMissingImage(int $$0, int $$1) {
        NativeImage $$2 = new NativeImage($$0, $$1, false);
        int $$3 = -16777216;
        int $$4 = -524040;
        for (int $$5 = 0; $$5 < $$1; ++$$5) {
            for (int $$6 = 0; $$6 < $$0; ++$$6) {
                if ($$5 < $$1 / 2 ^ $$6 < $$0 / 2) {
                    $$2.setPixelRGBA($$6, $$5, -524040);
                    continue;
                }
                $$2.setPixelRGBA($$6, $$5, -16777216);
            }
        }
        return $$2;
    }

    public static SpriteContents create() {
        NativeImage $$0 = MissingTextureAtlasSprite.generateMissingImage(16, 16);
        return new SpriteContents(MISSING_TEXTURE_LOCATION, new FrameSize(16, 16), $$0, EMPTY_ANIMATION_META);
    }

    public static ResourceLocation getLocation() {
        return MISSING_TEXTURE_LOCATION;
    }

    public static DynamicTexture getTexture() {
        if (missingTexture == null) {
            NativeImage $$0 = MissingTextureAtlasSprite.generateMissingImage(16, 16);
            $$0.untrack();
            missingTexture = new DynamicTexture($$0);
            Minecraft.getInstance().getTextureManager().register(MISSING_TEXTURE_LOCATION, (AbstractTexture)missingTexture);
        }
        return missingTexture;
    }
}