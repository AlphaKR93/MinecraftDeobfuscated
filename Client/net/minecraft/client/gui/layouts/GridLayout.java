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
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.util.Mth;

public class GridLayout
extends AbstractLayout {
    private final List<LayoutElement> children = new ArrayList();
    private final List<CellInhabitant> cellInhabitants = new ArrayList();
    private final LayoutSettings defaultCellSettings = LayoutSettings.defaults();
    private int rowSpacing = 0;
    private int columnSpacing = 0;

    public GridLayout() {
        this(0, 0);
    }

    public GridLayout(int $$0, int $$1) {
        super($$0, $$1, 0, 0);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        int $$0 = 0;
        int $$1 = 0;
        for (CellInhabitant $$2 : this.cellInhabitants) {
            $$0 = Math.max((int)$$2.getLastOccupiedRow(), (int)$$0);
            $$1 = Math.max((int)$$2.getLastOccupiedColumn(), (int)$$1);
        }
        int[] $$3 = new int[$$1 + 1];
        int[] $$4 = new int[$$0 + 1];
        for (CellInhabitant $$5 : this.cellInhabitants) {
            int $$6 = $$5.getHeight() - ($$5.occupiedRows - 1) * this.rowSpacing;
            Divisor $$7 = new Divisor($$6, $$5.occupiedRows);
            for (int $$8 = $$5.row; $$8 <= $$5.getLastOccupiedRow(); ++$$8) {
                $$4[$$8] = Math.max((int)$$4[$$8], (int)$$7.nextInt());
            }
            int $$9 = $$5.getWidth() - ($$5.occupiedColumns - 1) * this.columnSpacing;
            Divisor $$10 = new Divisor($$9, $$5.occupiedColumns);
            for (int $$11 = $$5.column; $$11 <= $$5.getLastOccupiedColumn(); ++$$11) {
                $$3[$$11] = Math.max((int)$$3[$$11], (int)$$10.nextInt());
            }
        }
        int[] $$12 = new int[$$1 + 1];
        int[] $$13 = new int[$$0 + 1];
        $$12[0] = 0;
        for (int $$14 = 1; $$14 <= $$1; ++$$14) {
            $$12[$$14] = $$12[$$14 - 1] + $$3[$$14 - 1] + this.columnSpacing;
        }
        $$13[0] = 0;
        for (int $$15 = 1; $$15 <= $$0; ++$$15) {
            $$13[$$15] = $$13[$$15 - 1] + $$4[$$15 - 1] + this.rowSpacing;
        }
        for (CellInhabitant $$16 : this.cellInhabitants) {
            int $$17 = 0;
            for (int $$18 = $$16.column; $$18 <= $$16.getLastOccupiedColumn(); ++$$18) {
                $$17 += $$3[$$18];
            }
            $$16.setX(this.getX() + $$12[$$16.column], $$17 += this.columnSpacing * ($$16.occupiedColumns - 1));
            int $$19 = 0;
            for (int $$20 = $$16.row; $$20 <= $$16.getLastOccupiedRow(); ++$$20) {
                $$19 += $$4[$$20];
            }
            $$16.setY(this.getY() + $$13[$$16.row], $$19 += this.rowSpacing * ($$16.occupiedRows - 1));
        }
        this.width = $$12[$$1] + $$3[$$1];
        this.height = $$13[$$0] + $$4[$$0];
    }

    public <T extends LayoutElement> T addChild(T $$0, int $$1, int $$2) {
        return this.addChild($$0, $$1, $$2, this.newCellSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, int $$1, int $$2, LayoutSettings $$3) {
        return this.addChild($$0, $$1, $$2, 1, 1, $$3);
    }

    public <T extends LayoutElement> T addChild(T $$0, int $$1, int $$2, int $$3, int $$4) {
        return this.addChild($$0, $$1, $$2, $$3, $$4, this.newCellSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, int $$1, int $$2, int $$3, int $$4, LayoutSettings $$5) {
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

    public GridLayout columnSpacing(int $$0) {
        this.columnSpacing = $$0;
        return this;
    }

    public GridLayout rowSpacing(int $$0) {
        this.rowSpacing = $$0;
        return this;
    }

    public GridLayout spacing(int $$0) {
        return this.columnSpacing($$0).rowSpacing($$0);
    }

    @Override
    protected void visitChildren(Consumer<LayoutElement> $$0) {
        this.children.forEach($$0);
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
    extends AbstractLayout.AbstractChildWrapper {
        final int row;
        final int column;
        final int occupiedRows;
        final int occupiedColumns;

        CellInhabitant(LayoutElement $$0, int $$1, int $$2, int $$3, int $$4, LayoutSettings $$5) {
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

        public <T extends LayoutElement> T addChild(T $$0) {
            return this.addChild($$0, 1);
        }

        public <T extends LayoutElement> T addChild(T $$0, int $$1) {
            return this.addChild($$0, $$1, this.defaultCellSetting());
        }

        public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
            return this.addChild($$0, 1, $$1);
        }

        public <T extends LayoutElement> T addChild(T $$0, int $$1, LayoutSettings $$2) {
            int $$3 = this.index / this.columns;
            int $$4 = this.index % this.columns;
            if ($$4 + $$1 > this.columns) {
                ++$$3;
                $$4 = 0;
                this.index = Mth.roundToward(this.index, this.columns);
            }
            this.index += $$1;
            return GridLayout.this.addChild($$0, $$3, $$4, 1, $$1, $$2);
        }

        public GridLayout getGrid() {
            return GridLayout.this;
        }

        public LayoutSettings newCellSettings() {
            return GridLayout.this.newCellSettings();
        }

        public LayoutSettings defaultCellSetting() {
            return GridLayout.this.defaultCellSetting();
        }
    }
}