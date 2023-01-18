/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.util;

public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A $$0, B $$1) {
        this.a = $$0;
        this.b = $$1;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A $$0) {
        this.a = $$0;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B $$0) {
        this.b = $$0;
    }
}