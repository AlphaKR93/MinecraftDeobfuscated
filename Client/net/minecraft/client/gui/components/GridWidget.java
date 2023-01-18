/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.client.gui.components;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class GridWidget
extends AbstractContainerWidget {
    private final List<AbstractWidget> children = new ArrayList();
    private final List<CellInhabitant> cellInhabitants = new ArrayList();
    private final LayoutSettings defaultCellSettings = LayoutSettings.defaults();

    public GridWidget() {
        this(0, 0);
    }

    public GridWidget(int $$0, int $$1) {
        this($$0, $$1, Component.empty());
    }

    public GridWidget(int $$0, int $$1, Component $$2) {
        super($$0, $$1, 0, 0, $$2);
    }

    public void pack() {
        int $$0 = 0;
        int $$1 = 0;
        for (CellInhabitant $$2 : this.cellInhabitants) {
            $$0 = Math.max((int)$$2.getLastOccupiedRow(), (int)$$0);
            $$1 = Math.max((int)$$2.getLastOccupiedColumn(), (int)$$1);
        }
        int[] $$3 = new int[$$1 + 1];
        int[] $$4 = new int[$$0 + 1];
        for (CellInhabitant $$5 : this.cellInhabitants) {
            Divisor $$6 = new Divisor($$5.getHeight(), $$5.occupiedRows);
            for (int $$7 = $$5.row; $$7 <= $$5.getLastOccupiedRow(); ++$$7) {
                $$4[$$7] = Math.max((int)$$4[$$7], (int)$$6.nextInt());
            }
            Divisor $$8 = new Divisor($$5.getWidth(), $$5.occupiedColumns);
            for (int $$9 = $$5.column; $$9 <= $$5.getLastOccupiedColumn(); ++$$9) {
                $$3[$$9] = Math.max((int)$$3[$$9], (int)$$8.nextInt());
            }
        }
        int[] $$10 = new int[$$1 + 1];
        int[] $$11 = new int[$$0 + 1];
        $$10[0] = 0;
        for (int $$12 = 1; $$12 <= $$1; ++$$12) {
            $$10[$$12] = $$10[$$12 - 1] + $$3[$$12 - 1];
        }
        $$11[0] = 0;
        for (int $$13 = 1; $$13 <= $$0; ++$$13) {
            $$11[$$13] = $$11[$$13 - 1] + $$4[$$13 - 1];
        }
        for (CellInhabitant $$14 : this.cellInhabitants) {
            int $$15 = 0;
            for (int $$16 = $$14.column; $$16 <= $$14.getLastOccupiedColumn(); ++$$16) {
                $$15 += $$3[$$16];
            }
            $$14.setX(this.getX() + $$10[$$14.column], $$15);
            int $$17 = 0;
            for (int $$18 = $$14.row; $$18 <= $$14.getLastOccupiedRow(); ++$$18) {
                $$17 += $$4[$$18];
            }
            $$14.setY(this.getY() + $$11[$$14.row], $$17);
        }
        this.width = $$10[$$1] + $$3[$$1];
        this.height = $$11[$$0] + $$4[$$0];
    }

    public <T extends AbstractWidget> T addChild(T $$0, int $$1, int $$2) {
        return this.addChild($$0, $$1, $$2, this.newCellSettings());
    }

    public <T extends AbstractWidget> T addChild(T $$0, int $$1, int $$2, LayoutSettings $$3) {
        return this.addChild($$0, $$1, $$2, 1, 1, $$3);
    }

    public <T extends AbstractWidget> T addChild(T $$0, int $$1, int $$2, int $$3, int $$4) {
        return this.addChild($$0, $$1, $$2, $$3, $$4, this.newCellSettings());
    }

    public <T extends AbstractWidget> T addChild(T $$0, int $$1, int $$2, int $$3, int $$4, LayoutSettings $$5) {
        if ($$3 < 1) {
            throw new IllegalArgumentException("Occupied rows must be at least 1");
        }
        if ($$4 < 1) {
            throw new IllegalArgumentException("Occupied columns must be at least 1");
        }
        this.cellInhabitants.add((Object)new CellInhabitant($$0, $$1, $$2, $$3, $$4, $$5));
        this.children.add($$0);
        return $$0;
    }

    @Override
    protected List<? extends AbstractWidget> getContainedChildren() {
        return this.children;
    }

    public LayoutSettings newCellSettings() {
        return this.defaultCellSettings.copy();
    }

    public LayoutSettings defaultCellSetting() {
        return this.defaultCellSettings;
    }

    public RowHelper createRowHelper(int $$0) {
        return new RowHelper($$0);
    }

    static class CellInhabitant
    extends AbstractContainerWidget.AbstractChildWrapper {
        final int row;
        final int column;
        final int occupiedRows;
        final int occupiedColumns;

        CellInhabitant(AbstractWidget $$0, int $$1, int $$2, int $$3, int $$4, LayoutSettings $$5) {
            super($$0, $$5.getExposed());
            this.row = $$1;
            this.column = $$2;
            this.occupiedRows = $$3;
            this.occupiedColumns = $$4;
        }

        public int getLastOccupiedRow() {
            return this.row + this.occupiedRows - 1;
        }

        public int getLastOccupiedColumn() {
            return this.column + this.occupiedColumns - 1;
        }
    }

    public final class RowHelper {
        private final int columns;
        private int index;

        RowHelper(int $$1) {
            this.columns = $$1;
        }

        public <T extends AbstractWidget> T addChild(T $$0) {
            return this.addChild($$0, 1);
        }

        public <T extends AbstractWidget> T addChild(T $$0, int $$1) {
            return this.addChild($$0, $$1, this.defaultCellSetting());
        }

        public <T extends AbstractWidget> T addChild(T $$0, LayoutSettings $$1) {
            return this.addChild($$0, 1, $$1);
        }

        public <T extends AbstractWidget> T addChild(T $$0, int $$1, LayoutSettings $$2) {
            int $$3 = this.index / this.columns;
            int $$4 = this.index % this.columns;
            if ($$4 + $$1 > this.columns) {
                ++$$3;
                $$4 = 0;
                this.index = Mth.roundToward(this.index, this.columns);
            }
            this.index += $$1;
            return GridWidget.this.addChild($$0, $$3, $$4, 1, $$1, $$2);
        }

        public LayoutSettings newCellSettings() {
            return GridWidget.this.newCellSettings();
        }

        public LayoutSettings defaultCellSetting() {
            return GridWidget.this.defaultCellSetting();
        }
    }
}