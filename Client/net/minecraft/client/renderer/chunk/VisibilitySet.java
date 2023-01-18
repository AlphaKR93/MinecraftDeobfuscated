/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.BitSet
 *  java.util.Set
 */
package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.core.Direction;

public class VisibilitySet {
    private static final int FACINGS = Direction.values().length;
    private final BitSet data = new BitSet(FACINGS * FACINGS);

    public void add(Set<Direction> $$0) {
        for (Direction $$1 : $$0) {
            for (Direction $$2 : $$0) {
                this.set($$1, $$2, true);
            }
        }
    }

    public void set(Direction $$0, Direction $$1, boolean $$2) {
        this.data.set($$0.ordinal() + $$1.ordinal() * FACINGS, $$2);
        this.data.set($$1.ordinal() + $$0.ordinal() * FACINGS, $$2);
    }

    public void setAll(boolean $$0) {
        this.data.set(0, this.data.size(), $$0);
    }

    public boolean visibilityBetween(Direction $$0, Direction $$1) {
        return this.data.get($$0.ordinal() + $$1.ordinal() * FACINGS);
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append(' ');
        for (Direction $$1 : Direction.values()) {
            $$0.append(' ').append($$1.toString().toUpperCase().charAt(0));
        }
        $$0.append('\n');
        for (Direction $$2 : Direction.values()) {
            $$0.append($$2.toString().toUpperCase().charAt(0));
            for (Direction $$3 : Direction.values()) {
                if ($$2 == $$3) {
                    $$0.append("  ");
                    continue;
                }
                boolean $$4 = this.visibilityBetween($$2, $$3);
                $$0.append(' ').append($$4 ? (char)'Y' : 'n');
            }
            $$0.append('\n');
        }
        return $$0.toString();
    }
}