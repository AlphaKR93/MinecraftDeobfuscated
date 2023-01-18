/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.phys.shapes;

public interface BooleanOp {
    public static final BooleanOp FALSE = ($$0, $$1) -> false;
    public static final BooleanOp NOT_OR = ($$0, $$1) -> !$$0 && !$$1;
    public static final BooleanOp ONLY_SECOND = ($$0, $$1) -> $$1 && !$$0;
    public static final BooleanOp NOT_FIRST = ($$0, $$1) -> !$$0;
    public static final BooleanOp ONLY_FIRST = ($$0, $$1) -> $$0 && !$$1;
    public static final BooleanOp NOT_SECOND = ($$0, $$1) -> !$$1;
    public static final BooleanOp NOT_SAME = ($$0, $$1) -> $$0 != $$1;
    public static final BooleanOp NOT_AND = ($$0, $$1) -> !$$0 || !$$1;
    public static final BooleanOp AND = ($$0, $$1) -> $$0 && $$1;
    public static final BooleanOp SAME = ($$0, $$1) -> $$0 == $$1;
    public static final BooleanOp SECOND = ($$0, $$1) -> $$1;
    public static final BooleanOp CAUSES = ($$0, $$1) -> !$$0 || $$1;
    public static final BooleanOp FIRST = ($$0, $$1) -> $$0;
    public static final BooleanOp CAUSED_BY = ($$0, $$1) -> $$0 || !$$1;
    public static final BooleanOp OR = ($$0, $$1) -> $$0 || $$1;
    public static final BooleanOp TRUE = ($$0, $$1) -> true;

    public boolean apply(boolean var1, boolean var2);
}