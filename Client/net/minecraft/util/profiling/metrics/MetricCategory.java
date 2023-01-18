/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util.profiling.metrics;

public enum MetricCategory {
    PATH_FINDING("pathfinding"),
    EVENT_LOOPS("event-loops"),
    MAIL_BOXES("mailboxes"),
    TICK_LOOP("ticking"),
    JVM("jvm"),
    CHUNK_RENDERING("chunk rendering"),
    CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
    CPU("cpu"),
    GPU("gpu");

    private final String description;

    private MetricCategory(String $$0) {
        this.description = $$0;
    }

    public String getDescription() {
        return this.description;
    }
}