/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.EncoderException
 *  java.lang.Object
 *  java.lang.Throwable
 */
package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class SkipPacketException
extends EncoderException {
    public SkipPacketException(Throwable $$0) {
        super($$0);
    }
}