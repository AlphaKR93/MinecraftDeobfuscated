/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.Arrays
 */
package com.mojang.realmsclient;

import java.util.Arrays;

public class KeyCombo {
    private final char[] chars;
    private int matchIndex;
    private final Runnable onCompletion;

    public KeyCombo(char[] $$0, Runnable $$1) {
        this.onCompletion = $$1;
        if ($$0.length < 1) {
            throw new IllegalArgumentException("Must have at least one char");
        }
        this.chars = $$0;
    }

    public KeyCombo(char[] $$0) {
        this($$0, () -> {});
    }

    public boolean keyPressed(char $$0) {
        if ($$0 == this.chars[this.matchIndex++]) {
            if (this.matchIndex == this.chars.length) {
                this.reset();
                this.onCompletion.run();
                return true;
            }
        } else {
            this.reset();
        }
        return false;
    }

    public void reset() {
        this.matchIndex = 0;
    }

    public String toString() {
        return "KeyCombo{chars=" + Arrays.toString((char[])this.chars) + ", matchIndex=" + this.matchIndex + "}";
    }
}