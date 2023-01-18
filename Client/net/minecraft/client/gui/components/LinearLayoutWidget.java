/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.network.chat.Component;

public class LinearLayoutWidget
extends AbstractContainerWidget {
    private final Orientation orientation;
    private final List<ChildContainer> children = new ArrayList();
    private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList((List)Lists.transform(this.children, $$0 -> $$0.child));
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

    public LinearLayoutWidget(int $$0, int $$1, Orientation $$2) {
        this(0, 0, $$0, $$1, $$2);
    }

    public LinearLayoutWidget(int $$02, int $$1, int $$2, int $$3, Orientation $$4) {
        super($$02, $$1, $$2, $$3, Component.empty());
        this.orientation = $$4;
    }

    public void pack() {
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
        this.orientation.setSecondaryLength(this, $$1);
    }

    @Override
    protected List<? extends AbstractWidget> getContainedChildren() {
        return this.containedChildrenView;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public <T extends AbstractWidget> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends AbstractWidget> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add((Object)new ChildContainer($$0, $$1));
        return $$0;
    }

    public static enum Orientation {
        HORIZONTAL,
        VERTICAL;


        int getPrimaryLength(AbstractWidget $$0) {
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

        int getSecondaryLength(AbstractWidget $$0) {
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

        int getPrimaryPosition(AbstractWidget $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getX();
                case VERTICAL -> $$0.getY();
            };
        }

        int getSecondaryPosition(AbstractWidget $$0) {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case HORIZONTAL -> $$0.getY();
                case VERTICAL -> $$0.getX();
            };
        }

        void setSecondaryLength(AbstractWidget $$0, int $$1) {
            switch (this) {
                case HORIZONTAL: {
                    $$0.height = $$1;
                    break;
                }
                case VERTICAL: {
                    $$0.width = $$1;
                }
            }
        }
    }

    static class ChildContainer
    extends AbstractContainerWidget.AbstractChildWrapper {
        protected ChildContainer(AbstractWidget $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}