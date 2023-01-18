/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  java.lang.Object
 *  java.util.ArrayDeque
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.VisibleForTesting;

public class MessageSignatureCache {
    public static final int NOT_FOUND = -1;
    private static final int DEFAULT_CAPACITY = 128;
    private final MessageSignature[] entries;

    public MessageSignatureCache(int $$0) {
        this.entries = new MessageSignature[$$0];
    }

    public static MessageSignatureCache createDefault() {
        return new MessageSignatureCache(128);
    }

    public int pack(MessageSignature $$0) {
        for (int $$1 = 0; $$1 < this.entries.length; ++$$1) {
            if (!$$0.equals((Object)this.entries[$$1])) continue;
            return $$1;
        }
        return -1;
    }

    @Nullable
    public MessageSignature unpack(int $$0) {
        return this.entries[$$0];
    }

    public void push(PlayerChatMessage $$0) {
        List<MessageSignature> $$1 = $$0.signedBody().lastSeen().entries();
        ArrayDeque $$2 = new ArrayDeque($$1.size() + 1);
        $$2.addAll($$1);
        MessageSignature $$3 = $$0.signature();
        if ($$3 != null) {
            $$2.add((Object)$$3);
        }
        this.push((ArrayDeque<MessageSignature>)$$2);
    }

    @VisibleForTesting
    void push(List<MessageSignature> $$0) {
        this.push((ArrayDeque<MessageSignature>)new ArrayDeque($$0));
    }

    private void push(ArrayDeque<MessageSignature> $$0) {
        ObjectOpenHashSet $$1 = new ObjectOpenHashSet($$0);
        for (int $$2 = 0; !$$0.isEmpty() && $$2 < this.entries.length; ++$$2) {
            MessageSignature $$3 = this.entries[$$2];
            this.entries[$$2] = (MessageSignature)((Object)$$0.removeLast());
            if ($$3 == null || $$1.contains((Object)$$3)) continue;
            $$0.addFirst((Object)$$3);
        }
    }
}