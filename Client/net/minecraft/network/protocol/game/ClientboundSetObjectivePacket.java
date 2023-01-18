/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket
implements Packet<ClientGamePacketListener> {
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    private final String objectiveName;
    private final Component displayName;
    private final ObjectiveCriteria.RenderType renderType;
    private final int method;

    public ClientboundSetObjectivePacket(Objective $$0, int $$1) {
        this.objectiveName = $$0.getName();
        this.displayName = $$0.getDisplayName();
        this.renderType = $$0.getRenderType();
        this.method = $$1;
    }

    public ClientboundSetObjectivePacket(FriendlyByteBuf $$0) {
        this.objectiveName = $$0.readUtf();
        this.method = $$0.readByte();
        if (this.method == 0 || this.method == 2) {
            this.displayName = $$0.readComponent();
            this.renderType = $$0.readEnum(ObjectiveCriteria.RenderType.class);
        } else {
            this.displayName = CommonComponents.EMPTY;
            this.renderType = ObjectiveCriteria.RenderType.INTEGER;
        }
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeUtf(this.objectiveName);
        $$0.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            $$0.writeComponent(this.displayName);
            $$0.writeEnum(this.renderType);
        }
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleAddObjective(this);
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public int getMethod() {
        return this.method;
    }

    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }
}