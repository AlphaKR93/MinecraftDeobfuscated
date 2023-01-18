/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.border;

import net.minecraft.world.level.border.WorldBorder;

public interface BorderChangeListener {
    public void onBorderSizeSet(WorldBorder var1, double var2);

    public void onBorderSizeLerping(WorldBorder var1, double var2, double var4, long var6);

    public void onBorderCenterSet(WorldBorder var1, double var2, double var4);

    public void onBorderSetWarningTime(WorldBorder var1, int var2);

    public void onBorderSetWarningBlocks(WorldBorder var1, int var2);

    public void onBorderSetDamagePerBlock(WorldBorder var1, double var2);

    public void onBorderSetDamageSafeZOne(WorldBorder var1, double var2);

    public static class DelegateBorderChangeListener
    implements BorderChangeListener {
        private final WorldBorder worldBorder;

        public DelegateBorderChangeListener(WorldBorder $$0) {
            this.worldBorder = $$0;
        }

        @Override
        public void onBorderSizeSet(WorldBorder $$0, double $$1) {
            this.worldBorder.setSize($$1);
        }

        @Override
        public void onBorderSizeLerping(WorldBorder $$0, double $$1, double $$2, long $$3) {
            this.worldBorder.lerpSizeBetween($$1, $$2, $$3);
        }

        @Override
        public void onBorderCenterSet(WorldBorder $$0, double $$1, double $$2) {
            this.worldBorder.setCenter($$1, $$2);
        }

        @Override
        public void onBorderSetWarningTime(WorldBorder $$0, int $$1) {
            this.worldBorder.setWarningTime($$1);
        }

        @Override
        public void onBorderSetWarningBlocks(WorldBorder $$0, int $$1) {
            this.worldBorder.setWarningBlocks($$1);
        }

        @Override
        public void onBorderSetDamagePerBlock(WorldBorder $$0, double $$1) {
            this.worldBorder.setDamagePerBlock($$1);
        }

        @Override
        public void onBorderSetDamageSafeZOne(WorldBorder $$0, double $$1) {
            this.worldBorder.setDamageSafeZone($$1);
        }
    }
}