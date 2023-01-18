/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

public interface LayoutSettings {
    public LayoutSettings padding(int var1);

    public LayoutSettings padding(int var1, int var2);

    public LayoutSettings padding(int var1, int var2, int var3, int var4);

    public LayoutSettings paddingLeft(int var1);

    public LayoutSettings paddingTop(int var1);

    public LayoutSettings paddingRight(int var1);

    public LayoutSettings paddingBottom(int var1);

    public LayoutSettings paddingHorizontal(int var1);

    public LayoutSettings paddingVertical(int var1);

    public LayoutSettings align(float var1, float var2);

    public LayoutSettings alignHorizontally(float var1);

    public LayoutSettings alignVertically(float var1);

    default public LayoutSettings alignHorizontallyLeft() {
        return this.alignHorizontally(0.0f);
    }

    default public LayoutSettings alignHorizontallyCenter() {
        return this.alignHorizontally(0.5f);
    }

    default public LayoutSettings alignHorizontallyRight() {
        return this.alignHorizontally(1.0f);
    }

    default public LayoutSettings alignVerticallyTop() {
        return this.alignVertically(0.0f);
    }

    default public LayoutSettings alignVerticallyMiddle() {
        return this.alignVertically(0.5f);
    }

    default public LayoutSettings alignVerticallyBottom() {
        return this.alignVertically(1.0f);
    }

    public LayoutSettings copy();

    public LayoutSettingsImpl getExposed();

    public static LayoutSettings defaults() {
        return new LayoutSettingsImpl();
    }

    public static class LayoutSettingsImpl
    implements LayoutSettings {
        public int paddingLeft;
        public int paddingTop;
        public int paddingRight;
        public int paddingBottom;
        public float xAlignment;
        public float yAlignment;

        public LayoutSettingsImpl() {
        }

        public LayoutSettingsImpl(LayoutSettingsImpl $$0) {
            this.paddingLeft = $$0.paddingLeft;
            this.paddingTop = $$0.paddingTop;
            this.paddingRight = $$0.paddingRight;
            this.paddingBottom = $$0.paddingBottom;
            this.xAlignment = $$0.xAlignment;
            this.yAlignment = $$0.yAlignment;
        }

        @Override
        public LayoutSettingsImpl padding(int $$0) {
            return this.padding($$0, $$0);
        }

        @Override
        public LayoutSettingsImpl padding(int $$0, int $$1) {
            return this.paddingHorizontal($$0).paddingVertical($$1);
        }

        @Override
        public LayoutSettingsImpl padding(int $$0, int $$1, int $$2, int $$3) {
            return this.paddingLeft($$0).paddingRight($$2).paddingTop($$1).paddingBottom($$3);
        }

        @Override
        public LayoutSettingsImpl paddingLeft(int $$0) {
            this.paddingLeft = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingTop(int $$0) {
            this.paddingTop = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingRight(int $$0) {
            this.paddingRight = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingBottom(int $$0) {
            this.paddingBottom = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingHorizontal(int $$0) {
            return this.paddingLeft($$0).paddingRight($$0);
        }

        @Override
        public LayoutSettingsImpl paddingVertical(int $$0) {
            return this.paddingTop($$0).paddingBottom($$0);
        }

        @Override
        public LayoutSettingsImpl align(float $$0, float $$1) {
            this.xAlignment = $$0;
            this.yAlignment = $$1;
            return this;
        }

        @Override
        public LayoutSettingsImpl alignHorizontally(float $$0) {
            this.xAlignment = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl alignVertically(float $$0) {
            this.yAlignment = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl copy() {
            return new LayoutSettingsImpl(this);
        }

        @Override
        public LayoutSettingsImpl getExposed() {
            return this;
        }
    }
}