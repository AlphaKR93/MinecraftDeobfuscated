/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Set
 */
package net.minecraft.client.multiplayer.chat.report;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;

public class ChatReportContextBuilder {
    final int leadingCount;
    private final List<Collector> activeCollectors = new ArrayList();

    public ChatReportContextBuilder(int $$0) {
        this.leadingCount = $$0;
    }

    public void collectAllContext(ChatLog $$0, IntCollection $$1, Handler $$2) {
        IntRBTreeSet $$3 = new IntRBTreeSet($$1);
        for (int $$4 = $$3.lastInt(); $$4 >= $$0.start() && (this.isActive() || !$$3.isEmpty()); --$$4) {
            LoggedChatEvent loggedChatEvent = $$0.lookup($$4);
            if (!(loggedChatEvent instanceof LoggedChatMessage.Player)) continue;
            LoggedChatMessage.Player $$5 = (LoggedChatMessage.Player)loggedChatEvent;
            boolean $$6 = this.acceptContext($$5.message());
            if ($$3.remove($$4)) {
                this.trackContext($$5.message());
                $$2.accept($$4, $$5);
                continue;
            }
            if (!$$6) continue;
            $$2.accept($$4, $$5);
        }
    }

    public void trackContext(PlayerChatMessage $$0) {
        this.activeCollectors.add((Object)new Collector($$0));
    }

    public boolean acceptContext(PlayerChatMessage $$0) {
        boolean $$1 = false;
        Iterator $$2 = this.activeCollectors.iterator();
        while ($$2.hasNext()) {
            Collector $$3 = (Collector)$$2.next();
            if (!$$3.accept($$0)) continue;
            $$1 = true;
            if (!$$3.isComplete()) continue;
            $$2.remove();
        }
        return $$1;
    }

    public boolean isActive() {
        return !this.activeCollectors.isEmpty();
    }

    public static interface Handler {
        public void accept(int var1, LoggedChatMessage.Player var2);
    }

    class Collector {
        private final Set<MessageSignature> lastSeenSignatures;
        private PlayerChatMessage lastChainMessage;
        private boolean collectingChain = true;
        private int count;

        Collector(PlayerChatMessage $$0) {
            this.lastSeenSignatures = new ObjectOpenHashSet($$0.signedBody().lastSeen().entries());
            this.lastChainMessage = $$0;
        }

        boolean accept(PlayerChatMessage $$0) {
            if ($$0.equals((Object)this.lastChainMessage)) {
                return false;
            }
            boolean $$1 = this.lastSeenSignatures.remove((Object)$$0.signature());
            if (this.collectingChain && this.lastChainMessage.sender().equals((Object)$$0.sender())) {
                if (this.lastChainMessage.link().isDescendantOf($$0.link())) {
                    $$1 = true;
                    this.lastChainMessage = $$0;
                } else {
                    this.collectingChain = false;
                }
            }
            if ($$1) {
                ++this.count;
            }
            return $$1;
        }

        boolean isComplete() {
            return this.count >= ChatReportContextBuilder.this.leadingCount || !this.collectingChain && this.lastSeenSignatures.isEmpty();
        }
    }
}