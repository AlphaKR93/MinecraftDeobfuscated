/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;

public class ChatLog {
    private final LoggedChatEvent[] buffer;
    private int nextId;

    public static Codec<ChatLog> codec(int $$0) {
        return Codec.list(LoggedChatEvent.CODEC).comapFlatMap($$1 -> {
            if ($$1.size() > $$0) {
                return DataResult.error((String)("Expected: a buffer of size less than or equal to " + $$0 + " but: " + $$1.size() + " is greater than " + $$0));
            }
            return DataResult.success((Object)new ChatLog($$0, (List<LoggedChatEvent>)$$1));
        }, ChatLog::loggedChatEvents);
    }

    public ChatLog(int $$0) {
        this.buffer = new LoggedChatEvent[$$0];
    }

    private ChatLog(int $$0, List<LoggedChatEvent> $$12) {
        this.buffer = (LoggedChatEvent[])$$12.toArray($$1 -> new LoggedChatEvent[$$0]);
        this.nextId = $$12.size();
    }

    private List<LoggedChatEvent> loggedChatEvents() {
        ArrayList $$0 = new ArrayList(this.size());
        for (int $$1 = this.start(); $$1 <= this.end(); ++$$1) {
            $$0.add((Object)this.lookup($$1));
        }
        return $$0;
    }

    public void push(LoggedChatEvent $$0) {
        this.buffer[this.index((int)this.nextId++)] = $$0;
    }

    @Nullable
    public LoggedChatEvent lookup(int $$0) {
        return $$0 >= this.start() && $$0 <= this.end() ? this.buffer[this.index($$0)] : null;
    }

    private int index(int $$0) {
        return $$0 % this.buffer.length;
    }

    public int start() {
        return Math.max((int)(this.nextId - this.buffer.length), (int)0);
    }

    public int end() {
        return this.nextId - 1;
    }

    private int size() {
        return this.end() - this.start() + 1;
    }
}