/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.MessageToByteEncoder
 *  java.lang.Exception
 *  java.lang.Object
 *  javax.crypto.Cipher
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;
import net.minecraft.network.CipherBase;

public class CipherEncoder
extends MessageToByteEncoder<ByteBuf> {
    private final CipherBase cipher;

    public CipherEncoder(Cipher $$0) {
        this.cipher = new CipherBase($$0);
    }

    protected void encode(ChannelHandlerContext $$0, ByteBuf $$1, ByteBuf $$2) throws Exception {
        this.cipher.encipher($$1, $$2);
    }
}