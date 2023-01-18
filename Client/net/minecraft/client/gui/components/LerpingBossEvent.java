/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.UUID
 */
package net.minecraft.client.gui.components;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class LerpingBossEvent
extends BossEvent {
    private static final long LERP_MILLISECONDS = 100L;
    protected float targetPercent;
    protected long setTime;

    public LerpingBossEvent(UUID $$0, Component $$1, float $$2, BossEvent.BossBarColor $$3, BossEvent.BossBarOverlay $$4, boolean $$5, boolean $$6, boolean $$7) {
        super($$0, $$1, $$3, $$4);
        this.targetPercent = $$2;
        this.progress = $$2;
        this.setTime = Util.getMillis();
        this.setDarkenScreen($$5);
        this.setPlayBossMusic($$6);
        this.setCreateWorldFog($$7);
    }

    @Override
    public void setProgress(float $$0) {
        this.progress = this.getProgress();
        this.targetPercent = $$0;
        this.setTime = Util.getMillis();
    }

    @Override
    public float getProgress() {
        long $$0 = Util.getMillis() - this.setTime;
        float $$1 = Mth.clamp((float)$$0 / 100.0f, 0.0f, 1.0f);
        return Mth.lerp($$1, this.progress, this.targetPercent);
    }
}