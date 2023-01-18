/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.LastSeenTrackedEntry;
import net.minecraft.network.chat.MessageSignature;

public class LastSeenMessagesValidator {
    private final int lastSeenCount;
    private final ObjectList<LastSeenTrackedEntry> trackedMessages = new ObjectArrayList();
    @Nullable
    private MessageSignature lastPendingMessage;

    public LastSeenMessagesValidator(int $$0) {
        this.lastSeenCount = $$0;
        for (int $$1 = 0; $$1 < $$0; ++$$1) {
            this.trackedMessages.add(null);
        }
    }

    public void addPending(MessageSignature $$0) {
        if (!$$0.equals((Object)this.lastPendingMessage)) {
            this.trackedMessages.add((Object)new LastSeenTrackedEntry($$0, true));
            this.lastPendingMessage = $$0;
        }
    }

    public int trackedMessagesCount() {
        return this.trackedMessages.size();
    }

    public boolean applyOffset(int $$0) {
        int $$1 = this.trackedMessages.size() - this.lastSeenCount;
        if ($$0 >= 0 && $$0 <= $$1) {
            this.trackedMessages.removeElements(0, $$0);
            return true;
        }
        return false;
    }

    public Optional<LastSeenMessages> applyUpdate(LastSeenMessages.Update $$0) {
        if (!this.applyOffset($$0.offset())) {
            return Optional.empty();
        }
        ObjectArrayList $$1 = new ObjectArrayList($$0.acknowledged().cardinality());
        if ($$0.acknowledged().length() > this.lastSeenCount) {
            return Optional.empty();
        }
        for (int $$2 = 0; $$2 < this.lastSeenCount; ++$$2) {
            boolean $$3 = $$0.acknowledged().get($$2);
            LastSeenTrackedEntry $$4 = (LastSeenTrackedEntry)((Object)this.trackedMessages.get($$2));
            if ($$3) {
                if ($$4 == null) {
                    return Optional.empty();
                }
                this.trackedMessages.set($$2, (Object)$$4.acknowledge());
                $$1.add((Object)$$4.signature());
                continue;
            }
            if ($$4 != null && !$$4.pending()) {
                return Optional.empty();
            }
            this.trackedMessages.set($$2, null);
        }
        return Optional.of((Object)((Object)new LastSeenMessages((List<MessageSignature>)$$1)));
    }
}