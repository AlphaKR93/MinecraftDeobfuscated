/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.network.protocol.game;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundCommandSuggestionsPacket
implements Packet<ClientGamePacketListener> {
    private final int id;
    private final Suggestions suggestions;

    public ClientboundCommandSuggestionsPacket(int $$0, Suggestions $$1) {
        this.id = $$0;
        this.suggestions = $$1;
    }

    public ClientboundCommandSuggestionsPacket(FriendlyByteBuf $$0) {
        this.id = $$0.readVarInt();
        int $$12 = $$0.readVarInt();
        int $$2 = $$0.readVarInt();
        StringRange $$3 = StringRange.between((int)$$12, (int)($$12 + $$2));
        List $$4 = $$0.readList($$1 -> {
            String $$2 = $$1.readUtf();
            Component $$3 = (Component)$$1.readNullable(FriendlyByteBuf::readComponent);
            return new Suggestion($$3, $$2, (Message)$$3);
        });
        this.suggestions = new Suggestions($$3, $$4);
    }

    @Override
    public void write(FriendlyByteBuf $$0) {
        $$0.writeVarInt(this.id);
        $$0.writeVarInt(this.suggestions.getRange().getStart());
        $$0.writeVarInt(this.suggestions.getRange().getLength());
        $$0.writeCollection(this.suggestions.getList(), ($$02, $$12) -> {
            $$02.writeUtf($$12.getText());
            $$02.writeNullable($$12.getTooltip(), ($$0, $$1) -> $$0.writeComponent(ComponentUtils.fromMessage($$1)));
        });
    }

    @Override
    public void handle(ClientGamePacketListener $$0) {
        $$0.handleCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}