/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();
    private static final int MAX_RENDER_DIST = 500;

    public StructureRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        ClientLevel $$6 = this.minecraft.level;
        DimensionType $$7 = $$6.dimensionType();
        BlockPos $$8 = new BlockPos($$5.getPosition().x, 0.0, $$5.getPosition().z);
        VertexConsumer $$9 = $$1.getBuffer(RenderType.lines());
        if (this.postMainBoxes.containsKey((Object)$$7)) {
            for (BoundingBox $$10 : ((Map)this.postMainBoxes.get((Object)$$7)).values()) {
                if (!$$8.closerThan($$10.getCenter(), 500.0)) continue;
                LevelRenderer.renderLineBox($$0, $$9, (double)$$10.minX() - $$2, (double)$$10.minY() - $$3, (double)$$10.minZ() - $$4, (double)($$10.maxX() + 1) - $$2, (double)($$10.maxY() + 1) - $$3, (double)($$10.maxZ() + 1) - $$4, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.postPiecesBoxes.containsKey((Object)$$7)) {
            for (Map.Entry $$11 : ((Map)this.postPiecesBoxes.get((Object)$$7)).entrySet()) {
                String $$12 = (String)$$11.getKey();
                BoundingBox $$13 = (BoundingBox)$$11.getValue();
                Boolean $$14 = (Boolean)((Map)this.startPiecesMap.get((Object)$$7)).get((Object)$$12);
                if (!$$8.closerThan($$13.getCenter(), 500.0)) continue;
                if ($$14.booleanValue()) {
                    LevelRenderer.renderLineBox($$0, $$9, (double)$$13.minX() - $$2, (double)$$13.minY() - $$3, (double)$$13.minZ() - $$4, (double)($$13.maxX() + 1) - $$2, (double)($$13.maxY() + 1) - $$3, (double)($$13.maxZ() + 1) - $$4, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
                    continue;
                }
                LevelRenderer.renderLineBox($$0, $$9, (double)$$13.minX() - $$2, (double)$$13.minY() - $$3, (double)$$13.minZ() - $$4, (double)($$13.maxX() + 1) - $$2, (double)($$13.maxY() + 1) - $$3, (double)($$13.maxZ() + 1) - $$4, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    public void addBoundingBox(BoundingBox $$0, List<BoundingBox> $$1, List<Boolean> $$2, DimensionType $$3) {
        if (!this.postMainBoxes.containsKey((Object)$$3)) {
            this.postMainBoxes.put((Object)$$3, (Object)Maps.newHashMap());
        }
        if (!this.postPiecesBoxes.containsKey((Object)$$3)) {
            this.postPiecesBoxes.put((Object)$$3, (Object)Maps.newHashMap());
            this.startPiecesMap.put((Object)$$3, (Object)Maps.newHashMap());
        }
        ((Map)this.postMainBoxes.get((Object)$$3)).put((Object)$$0.toString(), (Object)$$0);
        for (int $$4 = 0; $$4 < $$1.size(); ++$$4) {
            BoundingBox $$5 = (BoundingBox)$$1.get($$4);
            Boolean $$6 = (Boolean)$$2.get($$4);
            ((Map)this.postPiecesBoxes.get((Object)$$3)).put((Object)$$5.toString(), (Object)$$5);
            ((Map)this.startPiecesMap.get((Object)$$3)).put((Object)$$5.toString(), (Object)$$6);
        }
    }

    @Override
    public void clear() {
        this.postMainBoxes.clear();
        this.postPiecesBoxes.clear();
        this.startPiecesMap.clear();
    }
}