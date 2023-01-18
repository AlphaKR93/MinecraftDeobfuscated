/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Locale
 */
package net.minecraft.network.chat.contents;

import java.util.Locale;
import net.minecraft.network.chat.contents.TranslatableContents;

public class TranslatableFormatException
extends IllegalArgumentException {
    public TranslatableFormatException(TranslatableContents $$0, String $$1) {
        super(String.format((Locale)Locale.ROOT, (String)"Error parsing: %s: %s", (Object[])new Object[]{$$0, $$1}));
    }

    public TranslatableFormatException(TranslatableContents $$0, int $$1) {
        super(String.format((Locale)Locale.ROOT, (String)"Invalid index %d requested for %s", (Object[])new Object[]{$$1, $$0}));
    }

    public TranslatableFormatException(TranslatableContents $$0, Throwable $$1) {
        super(String.format((Locale)Locale.ROOT, (String)"Error while parsing: %s", (Object[])new Object[]{$$0}), $$1);
    }
}