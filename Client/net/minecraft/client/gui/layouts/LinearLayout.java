/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;

public class LinearLayout
extends AbstractLayout {
    private final Orientation orientation;
    private final List<ChildContainer> children = new ArrayList();
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

    public LinearLayout(int $$0, int $$1, Orientation $$2) {
        this(0, 0, $$0, $$1, $$2);
    }

    public LinearLayout(int $$0, int $$1, int $$2, int $$3, Orientation $$4) {
        super($$0, $$1, $$2, $$3);
        this.orientation = $$4;
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        if (this.children.isEmpty()) {
            return;
        }
        int $$0 = 0;
        int $$1 = this.orientation.getSecondaryLength(this);
        for (ChildContainer $$2 : this.children) {
            $$0 += this.orientation.getPrimaryLength($$2);
            $$1 = Math.max((int)$$1, (int)this.orientation.getSecondaryLength($$2));
        }
        int $$3 = this.orientation.getPrimaryLength(this) - $$0;
        int $$4 = this.orientation.getPrimaryPosition(this);
        Iterator $$5 = this.children.iterator();
        ChildContainer $$6 = (ChildContainer)$$5.next();
        this.orientation.setPrimaryPosition($$6, $$4);
        $$4 += this.orientation.getPrimaryLength($$6);
        if (this.children.size() >= 2) {
            Divisor $$7 = new Divisor($$3, this.children.size() - 1);
            while ($$7.hasNext()) {
                ChildContainer $$8 = (ChildContainer)$$5.next();
                this.orientation.setPrimaryPosition($$8, $$4 += $$7.nextInt());
                $$4 += this.orientation.getPrimaryLength($$8);
            }
        }
        int $$9 = this.orientation.getSecondaryPosition(this);
        for (ChildContainer $$10 : this.children) {
            this.orientation.setSecondaryPosition($$10, $$9, $$1);
        }
        switch (this.orientation) {
            case HORIZONTAL: {
                this.height = $$1;
                break;
            }
            case VERTICAL: {
                this.width = $$1;
            }
        }
    }

    @Override
    protected void visitChildren(Consumer<LayoutElement> $$0) {
        this.children.forEach($$1 -> $$0.accept((Object)$$1.child));
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public <T extends LayoutElement> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add((Object)new ChildContainer($$0, $$1));
        return $$0;
    }

    public static enum Orientation {
        HORIZONTAL,
        VERTICAL;


        int getPrimaryLength(LayoutElement $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getWidth();
                case VERTICAL -> $$0.getHeight();
            };
        }

        int getPrimaryLength(ChildContainer $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getWidth();
                case VERTICAL -> $$0.getHeight();
            };
        }

        int getSecondaryLength(LayoutElement $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getHeight();
                case VERTICAL -> $$0.getWidth();
            };
        }

        int getSecondaryLength(ChildContainer $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getHeight();
                case VERTICAL -> $$0.getWidth();
            };
        }

        void setPrimaryPosition(ChildContainer $$0, int $$1) {
            switch (this) {
                case HORIZONTAL: {
                    $$0.setX($$1, $$0.getWidth());
                    break;
                }
                case VERTICAL: {
                    $$0.setY($$1, $$0.getHeight());
                }
            }
        }

        void setSecondaryPosition(ChildContainer $$0, int $$1, int $$2) {
            switch (this) {
                case HORIZONTAL: {
                    $$0.setY($$1, $$2);
                    break;
                }
                case VERTICAL: {
                    $$0.setX($$1, $$2);
                }
            }
        }

        int getPrimaryPosition(LayoutElement $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getX();
                case VERTICAL -> $$0.getY();
            };
        }

        int getSecondaryPosition(LayoutElement $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getY();
                case VERTICAL -> $$0.getX();
            };
        }
    }

    static class ChildContainer
    extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}