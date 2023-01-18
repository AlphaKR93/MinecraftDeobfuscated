/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.IntFunction
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum ParticleStatus implements OptionEnum
{
    ALL(0, "options.particles.all"),
    DECREASED(1, "options.particles.decreased"),
    MINIMAL(2, "options.particles.minimal");

    private static final IntFunction<ParticleStatus> BY_ID;
    private final int id;
    private final String key;

    private ParticleStatus(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ParticleStatus byId(int $$0) {
        return (ParticleStatus)BY_ID.apply($$0);
    }

    static {
        BY_ID = ByIdMap.continuous(ParticleStatus::getId, ParticleStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}