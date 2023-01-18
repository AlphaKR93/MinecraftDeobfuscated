/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket
implements Packet<ClientGamePacketListener> {
    private static final int FLAG_AMBIENT = 1;
    private static final int FLAG_VISIBLE = 2;
    private static final int FLAG_SHOW_ICON = 4;
    private final int entityId;
    private final MobEffect effect;
    private final byte effectAmplifier;
    private final int effectDurationTicks;
    private final byte flags;
    @Nullable
    private final MobEffectInstance.FactorData factorData;

    public ClientboundUpdateMobEffectPacket(int $$0, MobEffectInstance $$1) {
        this.entityId = $$0;
        this.effect = $$1.getEffect();
        this.effectAmplifier = (byte)($$1.getAmplifier() & 0xFF);
        this.effectDurationTicks = $$1.getDuration();
        byte $$2 = 0;
        if ($$1.isAmbient()) {
            $$2 = (byte)($$2 | 1);
        }
        if ($$1.isVisible()) {
            $$2 = (byte)($$2 | 2);
        }
        if ($$1.showIcon()) {
            $$2 = (byte)($$2 | 4);
        }
        this.flags = $$2;
        this.factorData = (MobEffectInstance.FactorData)$$1.getFactorData().orElse(null);
    }

    public ClientboundUpdateMobEffectPacket(FriendlyByteBuf $$02) {
        this.entityId = $$02.readVarInt();
        this.effect = $$02.readById(BuiltInRegistries.MOB_EFFECT);
        this.effectAmplifier = $$02.readByte();
        this.effectDurationTicks = $$02.readVarInt();
        this.flags = $$02.readByte();
        this.factorData = (MobEffectInstance.FactorData)$$02.readNullable($$0 -> $$0.readWithCodec(MobEffectInstance.FactorData.CODEC));
    }

    @Override
    public void write(FriendlyByteBuf $$02) {
        $$02.writeVarInt(this.entityId);
        $$02.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
        $$02.writeByte(this.effectAmplifier);
        $$02.writeVarInt(this.effectDurationTicks);
        $$02.writeByte(this.flags);
        $$02.writeNullable(this.factorData, ($$0, $$1) -> $$0.writeWithCodec(MobEffectInstance.FactorData.CODEC, $$1));
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleUpdateMobEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public MobEffect getEffect() {
        return this.effect;
    }

    public byte getEffectAmplifier() {
        return this.effectAmplifier;
    }

    public int getEffectDurationTicks() {
        return this.effectDurationTicks;
    }

    public boolean isEffectVisible() {
        return (this.flags & 2) == 2;
    }

    public boolean isEffectAmbient() {
        return (this.flags & 1) == 1;
    }

    public boolean effectShowsIcon() {
        return (this.flags & 4) == 4;
    }

    @Nullable
    public MobEffectInstance.FactorData getFactorData() {
        return this.factorData;
    }
}