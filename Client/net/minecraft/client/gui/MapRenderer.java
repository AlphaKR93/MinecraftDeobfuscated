/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.AutoCloseable
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.Objects
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;

public class MapRenderer
implements AutoCloseable {
    private static final ResourceLocation MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
    static final RenderType MAP_ICONS = RenderType.text(MAP_ICONS_LOCATION);
    private static final int WIDTH = 128;
    private static final int HEIGHT = 128;
    final TextureManager textureManager;
    private final Int2ObjectMap<MapInstance> maps = new Int2ObjectOpenHashMap();

    public MapRenderer(TextureManager $$0) {
        this.textureManager = $$0;
    }

    public void update(int $$0, MapItemSavedData $$1) {
        this.getOrCreateMapInstance($$0, $$1).forceUpload();
    }

    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, MapItemSavedData $$3, boolean $$4, int $$5) {
        this.getOrCreateMapInstance($$2, $$3).draw($$0, $$1, $$4, $$5);
    }

    private MapInstance getOrCreateMapInstance(int $$0, MapItemSavedData $$12) {
        return (MapInstance)this.maps.compute($$0, ($$1, $$2) -> {
            if ($$2 == null) {
                return new MapInstance((int)$$1, $$12);
            }
            $$2.replaceMapData($$12);
            return $$2;
        });
    }

    public void resetData() {
        for (MapInstance $$0 : this.maps.values()) {
            $$0.close();
        }
        this.maps.clear();
    }

    public void close() {
        this.resetData();
    }

    class MapInstance
    implements AutoCloseable {
        private MapItemSavedData data;
        private final DynamicTexture texture;
        private final RenderType renderType;
        private boolean requiresUpload = true;

        MapInstance(int $$0, MapItemSavedData $$1) {
            this.data = $$1;
            this.texture = new DynamicTexture(128, 128, true);
            ResourceLocation $$2 = MapRenderer.this.textureManager.register("map/" + $$0, this.texture);
            this.renderType = RenderType.text($$2);
        }

        void replaceMapData(MapItemSavedData $$0) {
            boolean $$1 = this.data != $$0;
            this.data = $$0;
            this.requiresUpload |= $$1;
        }

        public void forceUpload() {
            this.requiresUpload = true;
        }

        private void updateTexture() {
            for (int $$0 = 0; $$0 < 128; ++$$0) {
                for (int $$1 = 0; $$1 < 128; ++$$1) {
                    int $$2 = $$1 + $$0 * 128;
                    this.texture.getPixels().setPixelRGBA($$1, $$0, MaterialColor.getColorFromPackedId(this.data.colors[$$2]));
                }
            }
            this.texture.upload();
        }

        void draw(PoseStack $$0, MultiBufferSource $$1, boolean $$2, int $$3) {
            if (this.requiresUpload) {
                this.updateTexture();
                this.requiresUpload = false;
            }
            boolean $$4 = false;
            boolean $$5 = false;
            float $$6 = 0.0f;
            Matrix4f $$7 = $$0.last().pose();
            VertexConsumer $$8 = $$1.getBuffer(this.renderType);
            $$8.vertex($$7, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2($$3).endVertex();
            $$8.vertex($$7, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2($$3).endVertex();
            $$8.vertex($$7, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2($$3).endVertex();
            $$8.vertex($$7, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2($$3).endVertex();
            int $$9 = 0;
            for (MapDecoration $$10 : this.data.getDecorations()) {
                if ($$2 && !$$10.renderOnFrame()) continue;
                $$0.pushPose();
                $$0.translate(0.0f + (float)$$10.getX() / 2.0f + 64.0f, 0.0f + (float)$$10.getY() / 2.0f + 64.0f, -0.02f);
                $$0.mulPose(Axis.ZP.rotationDegrees((float)($$10.getRot() * 360) / 16.0f));
                $$0.scale(4.0f, 4.0f, 3.0f);
                $$0.translate(-0.125f, 0.125f, 0.0f);
                byte $$11 = $$10.getImage();
                float $$12 = (float)($$11 % 16 + 0) / 16.0f;
                float $$13 = (float)($$11 / 16 + 0) / 16.0f;
                float $$14 = (float)($$11 % 16 + 1) / 16.0f;
                float $$15 = (float)($$11 / 16 + 1) / 16.0f;
                Matrix4f $$16 = $$0.last().pose();
                float $$17 = -0.001f;
                VertexConsumer $$18 = $$1.getBuffer(MAP_ICONS);
                $$18.vertex($$16, -1.0f, 1.0f, (float)$$9 * -0.001f).color(255, 255, 255, 255).uv($$12, $$13).uv2($$3).endVertex();
                $$18.vertex($$16, 1.0f, 1.0f, (float)$$9 * -0.001f).color(255, 255, 255, 255).uv($$14, $$13).uv2($$3).endVertex();
                $$18.vertex($$16, 1.0f, -1.0f, (float)$$9 * -0.001f).color(255, 255, 255, 255).uv($$14, $$15).uv2($$3).endVertex();
                $$18.vertex($$16, -1.0f, -1.0f, (float)$$9 * -0.001f).color(255, 255, 255, 255).uv($$12, $$15).uv2($$3).endVertex();
                $$0.popPose();
                if ($$10.getName() != null) {
                    Font $$19 = Minecraft.getInstance().font;
                    Component $$20 = $$10.getName();
                    float $$21 = $$19.width($$20);
                    float f = 25.0f / $$21;
                    Objects.requireNonNull((Object)$$19);
                    float $$22 = Mth.clamp(f, 0.0f, 6.0f / 9.0f);
                    $$0.pushPose();
                    $$0.translate(0.0f + (float)$$10.getX() / 2.0f + 64.0f - $$21 * $$22 / 2.0f, 0.0f + (float)$$10.getY() / 2.0f + 64.0f + 4.0f, -0.025f);
                    $$0.scale($$22, $$22, 1.0f);
                    $$0.translate(0.0f, 0.0f, -0.1f);
                    $$19.drawInBatch($$20, 0.0f, 0.0f, -1, false, $$0.last().pose(), $$1, false, Integer.MIN_VALUE, $$3);
                    $$0.popPose();
                }
                ++$$9;
            }
        }

        public void close() {
            this.texture.close();
        }
    }
}