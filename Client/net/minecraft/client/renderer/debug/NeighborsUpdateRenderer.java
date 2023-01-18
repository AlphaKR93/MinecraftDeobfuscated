/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.Sets
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap((Comparator)Ordering.natural().reverse());

    NeighborsUpdateRenderer(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void addUpdate(long $$02, BlockPos $$1) {
        Map $$2 = (Map)this.lastUpdate.computeIfAbsent((Object)$$02, $$0 -> Maps.newHashMap());
        int $$3 = (Integer)$$2.getOrDefault((Object)$$1, (Object)0);
        $$2.put((Object)$$1, (Object)($$3 + 1));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        long $$5 = this.minecraft.level.getGameTime();
        int $$6 = 200;
        double $$7 = 0.0025;
        HashSet $$8 = Sets.newHashSet();
        HashMap $$9 = Maps.newHashMap();
        VertexConsumer $$10 = $$1.getBuffer(RenderType.lines());
        Iterator $$11 = this.lastUpdate.entrySet().iterator();
        while ($$11.hasNext()) {
            Map.Entry $$12 = (Map.Entry)$$11.next();
            Long $$13 = (Long)$$12.getKey();
            Map $$14 = (Map)$$12.getValue();
            long $$15 = $$5 - $$13;
            if ($$15 > 200L) {
                $$11.remove();
                continue;
            }
            for (Map.Entry $$16 : $$14.entrySet()) {
                BlockPos $$17 = (BlockPos)$$16.getKey();
                Integer $$18 = (Integer)$$16.getValue();
                if (!$$8.add((Object)$$17)) continue;
                AABB $$19 = new AABB(BlockPos.ZERO).inflate(0.002).deflate(0.0025 * (double)$$15).move($$17.getX(), $$17.getY(), $$17.getZ()).move(-$$2, -$$3, -$$4);
                LevelRenderer.renderLineBox($$0, $$10, $$19.minX, $$19.minY, $$19.minZ, $$19.maxX, $$19.maxY, $$19.maxZ, 1.0f, 1.0f, 1.0f, 1.0f);
                $$9.put((Object)$$17, (Object)$$18);
            }
        }
        for (Map.Entry $$20 : $$9.entrySet()) {
            BlockPos $$21 = (BlockPos)$$20.getKey();
            Integer $$22 = (Integer)$$20.getValue();
            DebugRenderer.renderFloatingText(String.valueOf((Object)$$22), $$21.getX(), $$21.getY(), $$21.getZ(), -1);
        }
    }
}