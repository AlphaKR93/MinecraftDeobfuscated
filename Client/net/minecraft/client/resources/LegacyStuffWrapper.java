/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Throwable
 */
package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class LegacyStuffWrapper {
    @Deprecated
    public static int[] getPixels(ResourceManager $$0, ResourceLocation $$1) throws IOException {
        try (InputStream $$2 = $$0.open($$1);){
            int[] nArray;
            block12: {
                NativeImage $$3 = NativeImage.read($$2);
                try {
                    nArray = $$3.makePixelArray();
                    if ($$3 == null) break block12;
                }
                catch (Throwable throwable) {
                    if ($$3 != null) {
                        try {
                            $$3.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                $$3.close();
            }
            return nArray;
        }
    }
}