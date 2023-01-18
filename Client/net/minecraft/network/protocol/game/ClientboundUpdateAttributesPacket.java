/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket
implements Packet<ClientGamePacketListener> {
    private final int entityId;
    private final List<AttributeSnapshot> attributes;

    public ClientboundUpdateAttributesPacket(int $$0, Collection<AttributeInstance> $$1) {
        this.entityId = $$0;
        this.attributes = Lists.newArrayList();
        for (AttributeInstance $$2 : $$1) {
            this.attributes.add((Object)new AttributeSnapshot($$2.getAttribute(), $$2.getBaseValue(), (Collection<AttributeModifier>)$$2.getModifiers()));
        }
    }

    public ClientboundUpdateAttributesPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readVarInt();
        this.attributes = $$0.readList($$02 -> {
            ResourceLocation $$1 = $$02.readResourceLocation();
            Attribute $$2 = BuiltInRegistries.ATTRIBUTE.get($$1);
            double $$3 = $$02.readDouble();
            List $$4 = $$02.readList($$0 -> new AttributeModifier($$0.readUUID(), "Unknown synced attribute modifier", $$0.readDouble(), AttributeModifier.Operation.fromValue($$0.readByte())));
            return new AttributeSnapshot($$2, $$3, (Collection<AttributeModifier>)$$4);
        });
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entityId);
        $$0.writeCollection(this.attributes, ($$02, $$12) -> {
            $$02.writeResourceLocation(BuiltInRegistries.ATTRIBUTE.getKey($$12.getAttribute()));
            $$02.writeDouble($$12.getBase());
            $$02.writeCollection($$12.getModifiers(), ($$0, $$1) -> {
                $$0.writeUUID($$1.getId());
                $$0.writeDouble($$1.getAmount());
                $$0.writeByte($$1.getOperation().toValue());
            });
        });
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleUpdateAttributes(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<AttributeSnapshot> getValues() {
        return this.attributes;
    }

    public static class AttributeSnapshot {
        private final Attribute attribute;
        private final double base;
        private final Collection<AttributeModifier> modifiers;

        public AttributeSnapshot(Attribute $$0, double $$1, Collection<AttributeModifier> $$2) {
            this.attribute = $$0;
            this.base = $$1;
            this.modifiers = $$2;
        }

        public Attribute getAttribute() {
            return this.attribute;
        }

        public double getBase() {
            return this.base;
        }

        public Collection<AttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}