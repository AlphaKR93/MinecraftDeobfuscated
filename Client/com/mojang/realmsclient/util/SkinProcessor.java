/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.awt.Color
 *  java.awt.Graphics
 *  java.awt.Image
 *  java.awt.image.BufferedImage
 *  java.awt.image.DataBufferInt
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.annotation.Nullable;

public class SkinProcessor {
    private int[] pixels;
    private int width;
    private int height;

    @Nullable
    public BufferedImage process(@Nullable BufferedImage $$0) {
        boolean $$3;
        if ($$0 == null) {
            return null;
        }
        this.width = 64;
        this.height = 64;
        BufferedImage $$1 = new BufferedImage(this.width, this.height, 2);
        Graphics $$2 = $$1.getGraphics();
        $$2.drawImage((Image)$$0, 0, 0, null);
        boolean bl = $$3 = $$0.getHeight() == 32;
        if ($$3) {
            $$2.setColor(new Color(0, 0, 0, 0));
            $$2.fillRect(0, 32, 64, 32);
            $$2.drawImage((Image)$$1, 24, 48, 20, 52, 4, 16, 8, 20, null);
            $$2.drawImage((Image)$$1, 28, 48, 24, 52, 8, 16, 12, 20, null);
            $$2.drawImage((Image)$$1, 20, 52, 16, 64, 8, 20, 12, 32, null);
            $$2.drawImage((Image)$$1, 24, 52, 20, 64, 4, 20, 8, 32, null);
            $$2.drawImage((Image)$$1, 28, 52, 24, 64, 0, 20, 4, 32, null);
            $$2.drawImage((Image)$$1, 32, 52, 28, 64, 12, 20, 16, 32, null);
            $$2.drawImage((Image)$$1, 40, 48, 36, 52, 44, 16, 48, 20, null);
            $$2.drawImage((Image)$$1, 44, 48, 40, 52, 48, 16, 52, 20, null);
            $$2.drawImage((Image)$$1, 36, 52, 32, 64, 48, 20, 52, 32, null);
            $$2.drawImage((Image)$$1, 40, 52, 36, 64, 44, 20, 48, 32, null);
            $$2.drawImage((Image)$$1, 44, 52, 40, 64, 40, 20, 44, 32, null);
            $$2.drawImage((Image)$$1, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        $$2.dispose();
        this.pixels = ((DataBufferInt)$$1.getRaster().getDataBuffer()).getData();
        this.setNoAlpha(0, 0, 32, 16);
        if ($$3) {
            this.doLegacyTransparencyHack(32, 0, 64, 32);
        }
        this.setNoAlpha(0, 16, 64, 32);
        this.setNoAlpha(16, 48, 48, 64);
        return $$1;
    }

    private void doLegacyTransparencyHack(int $$0, int $$1, int $$2, int $$3) {
        for (int $$4 = $$0; $$4 < $$2; ++$$4) {
            for (int $$5 = $$1; $$5 < $$3; ++$$5) {
                int $$6 = this.pixels[$$4 + $$5 * this.width];
                if (($$6 >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int $$7 = $$0; $$7 < $$2; ++$$7) {
            for (int $$8 = $$1; $$8 < $$3; ++$$8) {
                int n = $$7 + $$8 * this.width;
                this.pixels[n] = this.pixels[n] & 0xFFFFFF;
            }
        }
    }

    private void setNoAlpha(int $$0, int $$1, int $$2, int $$3) {
        for (int $$4 = $$0; $$4 < $$2; ++$$4) {
            for (int $$5 = $$1; $$5 < $$3; ++$$5) {
                int n = $$4 + $$5 * this.width;
                this.pixels[n] = this.pixels[n] | 0xFF000000;
            }
        }
    }
}