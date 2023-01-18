/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util.profiling;

public final class ResultField
implements Comparable<ResultField> {
    public final double percentage;
    public final double globalPercentage;
    public final long count;
    public final String name;

    public ResultField(String $$0, double $$1, double $$2, long $$3) {
        this.name = $$0;
        this.percentage = $$1;
        this.globalPercentage = $$2;
        this.count = $$3;
    }

    public int compareTo(ResultField $$0) {
        if ($$0.percentage < this.percentage) {
            return -1;
        }
        if ($$0.percentage > this.percentage) {
            return 1;
        }
        return $$0.name.compareTo(this.name);
    }

    public int getColor() {
        return (this.name.hashCode() & 0xAAAAAA) + 0x444444;
    }
}