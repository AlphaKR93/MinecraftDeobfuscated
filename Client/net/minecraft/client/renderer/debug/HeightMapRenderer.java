/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map$Entry
 *  java.util.function.Supplier
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public class HeightMapRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private static final int CHUNK_DIST = 2;
    private static final float BOX_HEIGHT = 0.09375f;

    public HeightMapRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        ClientLevel $$5 = this.minecraft.level;
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        BlockPos $$6 = new BlockPos($$2, 0.0, $$4);
        Tesselator $$7 = Tesselator.getInstance();
        BufferBuilder $$8 = $$7.getBuilder();
        $$8.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int $$9 = -2; $$9 <= 2; ++$$9) {
            for (int $$10 = -2; $$10 <= 2; ++$$10) {
                ChunkAccess $$11 = $$5.getChunk($$6.offset($$9 * 16, 0, $$10 * 16));
                for (Map.Entry $$12 : $$11.getHeightmaps()) {
                    Heightmap.Types $$13 = (Heightmap.Types)$$12.getKey();
                    ChunkPos $$14 = $$11.getPos();
                    Vector3f $$15 = this.getColor($$13);
                    for (int $$16 = 0; $$16 < 16; ++$$16) {
                        for (int $$17 = 0; $$17 < 16; ++$$17) {
                            int $$18 = SectionPos.sectionToBlockCoord($$14.x, $$16);
                            int $$19 = SectionPos.sectionToBlockCoord($$14.z, $$17);
                            float $$20 = (float)((double)((float)$$5.getHeight($$13, $$18, $$19) + (float)$$13.ordinal() * 0.09375f) - $$3);
                            LevelRenderer.addChainedFilledBoxVertices($$8, (double)((float)$$18 + 0.25f) - $$2, $$20, (double)((float)$$19 + 0.25f) - $$4, (double)((float)$$18 + 0.75f) - $$2, $$20 + 0.09375f, (double)((float)$$19 + 0.75f) - $$4, $$15.x(), $$15.y(), $$15.z(), 1.0f);
                        }
                    }
                }
            }
        }
        $$7.end();
    }

    private Vector3f getColor(Heightmap.Types $$0) {
        switch ($$0) {
            case WORLD_SURFACE_WG: {
                return new Vector3f(1.0f, 1.0f, 0.0f);
            }
            case OCEAN_FLOOR_WG: {
                return new Vector3f(1.0f, 0.0f, 1.0f);
            }
            case WORLD_SURFACE: {
                return new Vector3f(0.0f, 0.7f, 0.0f);
            }
            case OCEAN_FLOOR: {
                return new Vector3f(0.0f, 0.0f, 0.5f);
            }
            case MOTION_BLOCKING: {
                return new Vector3f(0.0f, 0.3f, 0.3f);
            }
            case MOTION_BLOCKING_NO_LEAVES: {
                return new Vector3f(0.0f, 0.5f, 0.5f);
            }
        }
        return new Vector3f(0.0f, 0.0f, 0.0f);
    }
}