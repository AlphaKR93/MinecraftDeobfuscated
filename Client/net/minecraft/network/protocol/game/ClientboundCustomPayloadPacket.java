/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomPayloadPacket
implements Packet<ClientGamePacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 0x100000;
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    public static final ResourceLocation DEBUG_PATHFINDING_PACKET = new ResourceLocation("debug/path");
    public static final ResourceLocation DEBUG_NEIGHBORSUPDATE_PACKET = new ResourceLocation("debug/neighbors_update");
    public static final ResourceLocation DEBUG_STRUCTURES_PACKET = new ResourceLocation("debug/structures");
    public static final ResourceLocation DEBUG_WORLDGENATTEMPT_PACKET = new ResourceLocation("debug/worldgen_attempt");
    public static final ResourceLocation DEBUG_POI_TICKET_COUNT_PACKET = new ResourceLocation("debug/poi_ticket_count");
    public static final ResourceLocation DEBUG_POI_ADDED_PACKET = new ResourceLocation("debug/poi_added");
    public static final ResourceLocation DEBUG_POI_REMOVED_PACKET = new ResourceLocation("debug/poi_removed");
    public static final ResourceLocation DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
    public static final ResourceLocation DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
    public static final ResourceLocation DEBUG_BRAIN = new ResourceLocation("debug/brain");
    public static final ResourceLocation DEBUG_BEE = new ResourceLocation("debug/bee");
    public static final ResourceLocation DEBUG_HIVE = new ResourceLocation("debug/hive");
    public static final ResourceLocation DEBUG_GAME_TEST_ADD_MARKER = new ResourceLocation("debug/game_test_add_marker");
    public static final ResourceLocation DEBUG_GAME_TEST_CLEAR = new ResourceLocation("debug/game_test_clear");
    public static final ResourceLocation DEBUG_RAIDS = new ResourceLocation("debug/raids");
    public static final ResourceLocation DEBUG_GAME_EVENT = new ResourceLocation("debug/game_event");
    public static final ResourceLocation DEBUG_GAME_EVENT_LISTENER = new ResourceLocation("debug/game_event_listeners");
    private final ResourceLocation identifier;
    private final FriendlyByteBuf data;

    public ClientboundCustomPayloadPacket(ResourceLocation $$0, FriendlyByteBuf $$1) {
        this.identifier = $$0;
        this.data = $$1;
        if ($$1.writerIndex() > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    public ClientboundCustomPayloadPacket(FriendlyByteBuf $$0) {
        this.identifier = $$0.readResourceLocation();
        int $$1 = $$0.readableBytes();
        if ($$1 < 0 || $$1 > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf($$0.readBytes($$1));
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeResourceLocation(this.identifier);
        $$0.writeBytes(this.data.copy());
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleCustomPayload(this);
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public FriendlyByteBuf getData() {
        return new FriendlyByteBuf(this.data.copy());
    }
}