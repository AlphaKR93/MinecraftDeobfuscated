/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ServerboundInteractPacket
implements Packet<ServerGamePacketListener> {
    private final int entityId;
    private final Action action;
    private final boolean usingSecondaryAction;
    static final Action ATTACK_ACTION = new Action(){

        @Override
        public ActionType getType() {
            return ActionType.ATTACK;
        }

        @Override
        public void dispatch(Handler $$0) {
            $$0.onAttack();
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
        }
    };

    private ServerboundInteractPacket(int $$0, boolean $$1, Action $$2) {
        this.entityId = $$0;
        this.action = $$2;
        this.usingSecondaryAction = $$1;
    }

    public static ServerboundInteractPacket createAttackPacket(Entity $$0, boolean $$1) {
        return new ServerboundInteractPacket($$0.getId(), $$1, ATTACK_ACTION);
    }

    public static ServerboundInteractPacket createInteractionPacket(Entity $$0, boolean $$1, InteractionHand $$2) {
        return new ServerboundInteractPacket($$0.getId(), $$1, new InteractionAction($$2));
    }

    public static ServerboundInteractPacket createInteractionPacket(Entity $$0, boolean $$1, InteractionHand $$2, Vec3 $$3) {
        return new ServerboundInteractPacket($$0.getId(), $$1, new InteractionAtLocationAction($$2, $$3));
    }

    public ServerboundInteractPacket(FriendlyByteBuf $$0) {
        this.entityId = $$0.readVarInt();
        ActionType $$1 = $$0.readEnum(ActionType.class);
        this.action = (Action)$$1.reader.apply((Object)$$0);
        this.usingSecondaryAction = $$0.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.entityId);
        $$0.writeEnum(this.action.getType());
        this.action.write($$0);
        $$0.writeBoolean(this.usingSecondaryAction);
    }

    @Override
    public void handle(ServerGamePacketListener $$0) {
        $$0.handleInteract(this);
    }

    @Nullable
    public Entity getTarget(ServerLevel $$0) {
        return $$0.getEntityOrPart(this.entityId);
    }

    public boolean isUsingSecondaryAction() {
        return this.usingSecondaryAction;
    }

    public void dispatch(Handler $$0) {
        this.action.dispatch($$0);
    }

    static interface Action {
        public ActionType getType();

        public void dispatch(Handler var1);

        public void write(FriendlyByteBuf var1);
    }

    static class InteractionAction
    implements Action {
        private final InteractionHand hand;

        InteractionAction(InteractionHand $$0) {
            this.hand = $$0;
        }

        private InteractionAction(FriendlyByteBuf $$0) {
            this.hand = $$0.readEnum(InteractionHand.class);
        }

        @Override
        public ActionType getType() {
            return ActionType.INTERACT;
        }

        @Override
        public void dispatch(Handler $$0) {
            $$0.onInteraction(this.hand);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeEnum(this.hand);
        }
    }

    static class InteractionAtLocationAction
    implements Action {
        private final InteractionHand hand;
        private final Vec3 location;

        InteractionAtLocationAction(InteractionHand $$0, Vec3 $$1) {
            this.hand = $$0;
            this.location = $$1;
        }

        private InteractionAtLocationAction(FriendlyByteBuf $$0) {
            this.location = new Vec3($$0.readFloat(), $$0.readFloat(), $$0.readFloat());
            this.hand = $$0.readEnum(InteractionHand.class);
        }

        @Override
        public ActionType getType() {
            return ActionType.INTERACT_AT;
        }

        @Override
        public void dispatch(Handler $$0) {
            $$0.onInteraction(this.hand, this.location);
        }

        @Override
        public void write(FriendlyByteBuf $$0) {
            $$0.writeFloat((float)this.location.x);
            $$0.writeFloat((float)this.location.y);
            $$0.writeFloat((float)this.location.z);
            $$0.writeEnum(this.hand);
        }
    }

    static enum ActionType {
        INTERACT((Function<FriendlyByteBuf, Action>)((Function)InteractionAction::new)),
        ATTACK((Function<FriendlyByteBuf, Action>)((Function)$$0 -> ATTACK_ACTION)),
        INTERACT_AT((Function<FriendlyByteBuf, Action>)((Function)InteractionAtLocationAction::new));

        final Function<FriendlyByteBuf, Action> reader;

        private ActionType(Function<FriendlyByteBuf, Action> $$0) {
            this.reader = $$0;
        }
    }

    public static interface Handler {
        public void onInteraction(InteractionHand var1);

        public void onInteraction(InteractionHand var1, Vec3 var2);

        public void onAttack();
    }
}