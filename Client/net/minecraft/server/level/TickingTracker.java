/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Iterator
 */
package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.server.level.ChunkTracker;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.level.ChunkPos;

public class TickingTracker
extends ChunkTracker {
    private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
    protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
    private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();

    public TickingTracker() {
        super(34, 16, 256);
        this.chunks.defaultReturnValue((byte)33);
    }

    private SortedArraySet<Ticket<?>> getTickets(long $$02) {
        return (SortedArraySet)((Object)this.tickets.computeIfAbsent($$02, $$0 -> SortedArraySet.create(4)));
    }

    private int getTicketLevelAt(SortedArraySet<Ticket<?>> $$0) {
        return $$0.isEmpty() ? 34 : $$0.first().getTicketLevel();
    }

    public void addTicket(long $$0, Ticket<?> $$1) {
        SortedArraySet<Ticket<?>> $$2 = this.getTickets($$0);
        int $$3 = this.getTicketLevelAt($$2);
        $$2.add($$1);
        if ($$1.getTicketLevel() < $$3) {
            this.update($$0, $$1.getTicketLevel(), true);
        }
    }

    public void removeTicket(long $$0, Ticket<?> $$1) {
        SortedArraySet<Ticket<?>> $$2 = this.getTickets($$0);
        $$2.remove($$1);
        if ($$2.isEmpty()) {
            this.tickets.remove($$0);
        }
        this.update($$0, this.getTicketLevelAt($$2), false);
    }

    public <T> void addTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        this.addTicket($$1.toLong(), new Ticket<T>($$0, $$2, $$3));
    }

    public <T> void removeTicket(TicketType<T> $$0, ChunkPos $$1, int $$2, T $$3) {
        Ticket<T> $$4 = new Ticket<T>($$0, $$2, $$3);
        this.removeTicket($$1.toLong(), $$4);
    }

    public void replacePlayerTicketsLevel(int $$0) {
        ArrayList $$1 = new ArrayList();
        for (Long2ObjectMap.Entry $$2 : this.tickets.long2ObjectEntrySet()) {
            Iterator iterator = ((SortedArraySet)((Object)$$2.getValue())).iterator();
            while (iterator.hasNext()) {
                Ticket $$3 = (Ticket)iterator.next();
                if ($$3.getType() != TicketType.PLAYER) continue;
                $$1.add((Object)Pair.of((Object)$$3, (Object)$$2.getLongKey()));
            }
        }
        for (Pair $$4 : $$1) {
            Long $$5 = (Long)$$4.getSecond();
            Ticket $$6 = (Ticket)$$4.getFirst();
            this.removeTicket($$5, $$6);
            ChunkPos $$7 = new ChunkPos($$5);
            TicketType $$8 = $$6.getType();
            this.addTicket($$8, $$7, $$0, $$7);
        }
    }

    @Override
    protected int getLevelFromSource(long $$0) {
        SortedArraySet $$1 = (SortedArraySet)((Object)this.tickets.get($$0));
        if ($$1 == null || $$1.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        return ((Ticket)$$1.first()).getTicketLevel();
    }

    public int getLevel(ChunkPos $$0) {
        return this.getLevel($$0.toLong());
    }

    @Override
    protected int getLevel(long $$0) {
        return this.chunks.get($$0);
    }

    @Override
    protected void setLevel(long $$0, int $$1) {
        if ($$1 > 33) {
            this.chunks.remove($$0);
        } else {
            this.chunks.put($$0, (byte)$$1);
        }
    }

    public void runAllUpdates() {
        this.runUpdates(Integer.MAX_VALUE);
    }

    public String getTicketDebugString(long $$0) {
        SortedArraySet $$1 = (SortedArraySet)((Object)this.tickets.get($$0));
        if ($$1 == null || $$1.isEmpty()) {
            return "no_ticket";
        }
        return ((Ticket)$$1.first()).toString();
    }
}