/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class FontTexture
extends AbstractTexture {
    private static final int SIZE = 256;
    private final ResourceLocation name;
    private final RenderType normalType;
    private final RenderType seeThroughType;
    private final RenderType polygonOffsetType;
    private final boolean colored;
    private final Node root;

    public FontTexture(ResourceLocation $$0, boolean $$1) {
        this.name = $$0;
        this.colored = $$1;
        this.root = new Node(0, 0, 256, 256);
        TextureUtil.prepareImage($$1 ? NativeImage.InternalGlFormat.RGBA : NativeImage.InternalGlFormat.RED, this.getId(), 256, 256);
        this.normalType = $$1 ? RenderType.text($$0) : RenderType.textIntensity($$0);
        this.seeThroughType = $$1 ? RenderType.textSeeThrough($$0) : RenderType.textIntensitySeeThrough($$0);
        this.polygonOffsetType = $$1 ? RenderType.textPolygonOffset($$0) : RenderType.textIntensityPolygonOffset($$0);
    }

    @Override
    public void load(ResourceManager $$0) {
    }

    @Override
    public void close() {
        this.releaseId();
    }

    @Nullable
    public BakedGlyph add(SheetGlyphInfo $$0) {
        if ($$0.isColored() != this.colored) {
            return null;
        }
        Node $$1 = this.root.insert($$0);
        if ($$1 != null) {
            this.bind();
            $$0.upload($$1.x, $$1.y);
            float $$2 = 256.0f;
            float $$3 = 256.0f;
            float $$4 = 0.01f;
            return new BakedGlyph(this.normalType, this.seeThroughType, this.polygonOffsetType, ((float)$$1.x + 0.01f) / 256.0f, ((float)$$1.x - 0.01f + (float)$$0.getPixelWidth()) / 256.0f, ((float)$$1.y + 0.01f) / 256.0f, ((float)$$1.y - 0.01f + (float)$$0.getPixelHeight()) / 256.0f, $$0.getLeft(), $$0.getRight(), $$0.getUp(), $$0.getDown());
        }
        return null;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    static class Node {
        final int x;
        final int y;
        private final int width;
        private final int height;
        @Nullable
        private Node left;
        @Nullable
        private Node right;
        private boolean occupied;

        Node(int $$0, int $$1, int $$2, int $$3) {
            this.x = $$0;
            this.y = $$1;
            this.width = $$2;
            this.height = $$3;
        }

        @Nullable
        Node insert(SheetGlyphInfo $$0) {
            if (this.left != null && this.right != null) {
                Node $$1 = this.left.insert($$0);
                if ($$1 == null) {
                    $$1 = this.right.insert($$0);
                }
                return $$1;
            }
            if (this.occupied) {
                return null;
            }
            int $$2 = $$0.getPixelWidth();
            int $$3 = $$0.getPixelHeight();
            if ($$2 > this.width || $$3 > this.height) {
                return null;
            }
            if ($$2 == this.width && $$3 == this.height) {
                this.occupied = true;
                return this;
            }
            int $$4 = this.width - $$2;
            int $$5 = this.height - $$3;
            if ($$4 > $$5) {
                this.left = new Node(this.x, this.y, $$2, this.height);
                this.right = new Node(this.x + $$2 + 1, this.y, this.width - $$2 - 1, this.height);
            } else {
                this.left = new Node(this.x, this.y, this.width, $$3);
                this.right = new Node(this.x, this.y + $$3 + 1, this.width, this.height - $$3 - 1);
            }
            return this.left.insert($$0);
        }
    }
}