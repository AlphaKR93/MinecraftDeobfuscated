/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class VillageSectionsDebugRenderer
implements DebugRenderer.SimpleDebugRenderer {
    private static final int MAX_RENDER_DIST_FOR_VILLAGE_SECTIONS = 60;
    private final Set<SectionPos> villageSections = Sets.newHashSet();

    VillageSectionsDebugRenderer() {
    }

    @Override
    public void clear() {
        this.villageSections.clear();
    }

    public void setVillageSection(SectionPos $$0) {
        this.villageSections.add((Object)$$0);
    }

    public void setNotVillageSection(SectionPos $$0) {
        this.villageSections.remove((Object)$$0);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        this.doRender($$2, $$3, $$4);
        RenderSystem.disableBlend();
    }

    private void doRender(double $$0, double $$12, double $$2) {
        BlockPos $$3 = new BlockPos($$0, $$12, $$2);
        this.villageSections.forEach($$1 -> {
            if ($$3.closerThan($$1.center(), 60.0)) {
                VillageSectionsDebugRenderer.highlightVillageSection($$1);
            }
        });
    }

    private static void highlightVillageSection(SectionPos $$0) {
        float $$1 = 1.0f;
        BlockPos $$2 = $$0.center();
        BlockPos $$3 = $$2.offset(-1.0, -1.0, -1.0);
        BlockPos $$4 = $$2.offset(1.0, 1.0, 1.0);
        DebugRenderer.renderFilledBox($$3, $$4, 0.2f, 1.0f, 0.2f, 0.15f);
    }
}