/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.ByteArrayOutputStream
 *  java.io.DataOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Short
 *  java.lang.String
 */
package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NetworkDataOutputStream {
    private final ByteArrayOutputStream outputStream;
    private final DataOutputStream dataOutputStream;

    public NetworkDataOutputStream(int $$0) {
        this.outputStream = new ByteArrayOutputStream($$0);
        this.dataOutputStream = new DataOutputStream((OutputStream)this.outputStream);
    }

    public void writeBytes(byte[] $$0) throws IOException {
        this.dataOutputStream.write($$0, 0, $$0.length);
    }

    public void writeString(String $$0) throws IOException {
        this.dataOutputStream.writeBytes($$0);
        this.dataOutputStream.write(0);
    }

    public void write(int $$0) throws IOException {
        this.dataOutputStream.write($$0);
    }

    public void writeShort(short $$0) throws IOException {
        this.dataOutputStream.writeShort((int)Short.reverseBytes((short)$$0));
    }

    public void writeInt(int $$0) throws IOException {
        this.dataOutputStream.writeInt(Integer.reverseBytes((int)$$0));
    }

    public void writeFloat(float $$0) throws IOException {
        this.dataOutputStream.writeInt(Integer.reverseBytes((int)Float.floatToIntBits((float)$$0)));
    }

    public byte[] toByteArray() {
        return this.outputStream.toByteArray();
    }

    public void reset() {
        this.outputStream.reset();
    }
}