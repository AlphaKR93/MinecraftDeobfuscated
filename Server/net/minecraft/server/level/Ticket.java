/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.Objects
 */
package net.minecraft.server.level;

import java.util.Objects;
import net.minecraft.server.level.TicketType;

public final class Ticket<T>
implements Comparable<Ticket<?>> {
    private final TicketType<T> type;
    private final int ticketLevel;
    private final T key;
    private long createdTick;

    protected Ticket(TicketType<T> $$0, int $$1, T $$2) {
        this.type = $$0;
        this.ticketLevel = $$1;
        this.key = $$2;
    }

    public int compareTo(Ticket<?> $$0) {
        int $$1 = Integer.compare((int)this.ticketLevel, (int)$$0.ticketLevel);
        if ($$1 != 0) {
            return $$1;
        }
        int $$2 = Integer.compare((int)System.identityHashCode(this.type), (int)System.identityHashCode($$0.type));
        if ($$2 != 0) {
            return $$2;
        }
        return this.type.getComparator().compare(this.key, $$0.key);
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if (!($$0 instanceof Ticket)) {
            return false;
        }
        Ticket $$1 = (Ticket)$$0;
        return this.ticketLevel == $$1.ticketLevel && Objects.equals(this.type, $$1.type) && Objects.equals(this.key, $$1.key);
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.type, this.ticketLevel, this.key});
    }

    public String toString() {
        return "Ticket[" + this.type + " " + this.ticketLevel + " (" + this.key + ")] at " + this.createdTick;
    }

    public TicketType<T> getType() {
        return this.type;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    protected void setCreatedTick(long $$0) {
        this.createdTick = $$0;
    }

    protected boolean timedOut(long $$0) {
        long $$1 = this.type.timeout();
        return $$1 != 0L && $$0 - this.createdTick > $$1;
    }
}