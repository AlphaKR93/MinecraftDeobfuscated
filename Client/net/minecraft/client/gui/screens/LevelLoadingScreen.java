/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;

public class LevelLoadingScreen
extends Screen {
    private static final long NARRATION_DELAY_MS = 2000L;
    private final StoringChunkProgressListener progressListener;
    private long lastNarration = -1L;
    private boolean done;
    private static final Object2IntMap<ChunkStatus> COLORS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), $$0 -> {
        $$0.defaultReturnValue(0);
        $$0.put((Object)ChunkStatus.EMPTY, 0x545454);
        $$0.put((Object)ChunkStatus.STRUCTURE_STARTS, 0x999999);
        $$0.put((Object)ChunkStatus.STRUCTURE_REFERENCES, 6250897);
        $$0.put((Object)ChunkStatus.BIOMES, 8434258);
        $$0.put((Object)ChunkStatus.NOISE, 0xD1D1D1);
        $$0.put((Object)ChunkStatus.SURFACE, 7497737);
        $$0.put((Object)ChunkStatus.CARVERS, 7169628);
        $$0.put((Object)ChunkStatus.LIQUID_CARVERS, 3159410);
        $$0.put((Object)ChunkStatus.FEATURES, 2213376);
        $$0.put((Object)ChunkStatus.LIGHT, 0xCCCCCC);
        $$0.put((Object)ChunkStatus.SPAWN, 15884384);
        $$0.put((Object)ChunkStatus.HEIGHTMAPS, 0xEEEEEE);
        $$0.put((Object)ChunkStatus.FULL, 0xFFFFFF);
    });

    public LevelLoadingScreen(StoringChunkProgressListener $$0) {
        super(GameNarrator.NO_TITLE);
        this.progressListener = $$0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    @Override
    public void removed() {
        this.done = true;
        this.triggerImmediateNarration(true);
    }

    @Override
    protected void updateNarratedWidget(NarrationElementOutput $$0) {
        if (this.done) {
            $$0.add(NarratedElementType.TITLE, (Component)Component.translatable("narrator.loading.done"));
        } else {
            String $$1 = this.getFormattedProgress();
            $$0.add(NarratedElementType.TITLE, $$1);
        }
    }

    private String getFormattedProgress() {
        return Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        long $$4 = Util.getMillis();
        if ($$4 - this.lastNarration > 2000L) {
            this.lastNarration = $$4;
            this.triggerImmediateNarration(true);
        }
        int $$5 = this.width / 2;
        int $$6 = this.height / 2;
        int $$7 = 30;
        LevelLoadingScreen.renderChunks($$0, this.progressListener, $$5, $$6 + 30, 2, 0);
        String string = this.getFormattedProgress();
        Objects.requireNonNull((Object)this.font);
        LevelLoadingScreen.drawCenteredString($$0, this.font, string, $$5, $$6 - 9 / 2 - 30, 0xFFFFFF);
    }

    public static void renderChunks(PoseStack $$0, StoringChunkProgressListener $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = $$4 + $$5;
        int $$7 = $$1.getFullDiameter();
        int $$8 = $$7 * $$6 - $$5;
        int $$9 = $$1.getDiameter();
        int $$10 = $$9 * $$6 - $$5;
        int $$11 = $$2 - $$10 / 2;
        int $$12 = $$3 - $$10 / 2;
        int $$13 = $$8 / 2 + 1;
        int $$14 = -16772609;
        if ($$5 != 0) {
            LevelLoadingScreen.fill($$0, $$2 - $$13, $$3 - $$13, $$2 - $$13 + 1, $$3 + $$13, -16772609);
            LevelLoadingScreen.fill($$0, $$2 + $$13 - 1, $$3 - $$13, $$2 + $$13, $$3 + $$13, -16772609);
            LevelLoadingScreen.fill($$0, $$2 - $$13, $$3 - $$13, $$2 + $$13, $$3 - $$13 + 1, -16772609);
            LevelLoadingScreen.fill($$0, $$2 - $$13, $$3 + $$13 - 1, $$2 + $$13, $$3 + $$13, -16772609);
        }
        for (int $$15 = 0; $$15 < $$9; ++$$15) {
            for (int $$16 = 0; $$16 < $$9; ++$$16) {
                ChunkStatus $$17 = $$1.getStatus($$15, $$16);
                int $$18 = $$11 + $$15 * $$6;
                int $$19 = $$12 + $$16 * $$6;
                LevelLoadingScreen.fill($$0, $$18, $$19, $$18 + $$4, $$19 + $$4, COLORS.getInt((Object)$$17) | 0xFF000000);
            }
        }
    }
}