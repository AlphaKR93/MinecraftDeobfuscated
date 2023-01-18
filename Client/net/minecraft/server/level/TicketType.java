/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 */
package net.minecraft.server.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;

public class TicketType<T> {
    private final String name;
    private final Comparator<T> comparator;
    private final long timeout;
    public static final TicketType<Unit> START = TicketType.create("start", ($$0, $$1) -> 0);
    public static final TicketType<Unit> DRAGON = TicketType.create("dragon", ($$0, $$1) -> 0);
    public static final TicketType<ChunkPos> PLAYER = TicketType.create("player", Comparator.comparingLong(ChunkPos::toLong));
    public static final TicketType<ChunkPos> FORCED = TicketType.create("forced", Comparator.comparingLong(ChunkPos::toLong));
    public static final TicketType<ChunkPos> LIGHT = TicketType.create("light", Comparator.comparingLong(ChunkPos::toLong));
    public static final TicketType<BlockPos> PORTAL = TicketType.create("portal", Vec3i::compareTo, 300);
    public static final TicketType<Integer> POST_TELEPORT = TicketType.create("post_teleport", Integer::compareTo, 5);
    public static final TicketType<ChunkPos> UNKNOWN = TicketType.create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

    public static <T> TicketType<T> create(String $$0, Comparator<T> $$1) {
        return new TicketType<T>($$0, $$1, 0L);
    }

    public static <T> TicketType<T> create(String $$0, Comparator<T> $$1, int $$2) {
        return new TicketType<T>($$0, $$1, $$2);
    }

    protected TicketType(String $$0, Comparator<T> $$1, long $$2) {
        this.name = $$0;
        this.comparator = $$1;
        this.timeout = $$2;
    }

    public String toString() {
        return this.name;
    }

    public Comparator<T> getComparator() {
        return this.comparator;
    }

    public long timeout() {
        return this.timeout;
    }
}