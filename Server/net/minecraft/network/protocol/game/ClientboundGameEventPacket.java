/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundGameEventPacket
implements Packet<ClientGamePacketListener> {
    public static final Type NO_RESPAWN_BLOCK_AVAILABLE = new Type(0);
    public static final Type START_RAINING = new Type(1);
    public static final Type STOP_RAINING = new Type(2);
    public static final Type CHANGE_GAME_MODE = new Type(3);
    public static final Type WIN_GAME = new Type(4);
    public static final Type DEMO_EVENT = new Type(5);
    public static final Type ARROW_HIT_PLAYER = new Type(6);
    public static final Type RAIN_LEVEL_CHANGE = new Type(7);
    public static final Type THUNDER_LEVEL_CHANGE = new Type(8);
    public static final Type PUFFER_FISH_STING = new Type(9);
    public static final Type GUARDIAN_ELDER_EFFECT = new Type(10);
    public static final Type IMMEDIATE_RESPAWN = new Type(11);
    public static final int DEMO_PARAM_INTRO = 0;
    public static final int DEMO_PARAM_HINT_1 = 101;
    public static final int DEMO_PARAM_HINT_2 = 102;
    public static final int DEMO_PARAM_HINT_3 = 103;
    public static final int DEMO_PARAM_HINT_4 = 104;
    private final Type event;
    private final float param;

    public ClientboundGameEventPacket(Type $$0, float $$1) {
        this.event = $$0;
        this.param = $$1;
    }

    public ClientboundGameEventPacket(FriendlyByteBuf $$0) {
        this.event = (Type)Type.TYPES.get((int)$$0.readUnsignedByte());
        this.param = $$0.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.event.id);
        $$0.writeFloat(this.param);
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleGameEvent(this);
    }

    public Type getEvent() {
        return this.event;
    }

    public float getParam() {
        return this.param;
    }

    public static class Type {
        static final Int2ObjectMap<Type> TYPES = new Int2ObjectOpenHashMap();
        final int id;

        public Type(int $$0) {
            this.id = $$0;
            TYPES.put($$0, (Object)this);
        }
    }
}