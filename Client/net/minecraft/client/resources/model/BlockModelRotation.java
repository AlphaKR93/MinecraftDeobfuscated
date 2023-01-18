/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.stream.Collectors
 *  org.joml.Quaternionf
 */
package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Transformation;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public enum BlockModelRotation implements ModelState
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final int DEGREES = 360;
    private static final Map<Integer, BlockModelRotation> BY_INDEX;
    private final Transformation transformation;
    private final OctahedralGroup actualRotation;
    private final int index;

    private static int getIndex(int $$0, int $$1) {
        return $$0 * 360 + $$1;
    }

    private BlockModelRotation(int $$0, int $$1) {
        this.index = BlockModelRotation.getIndex($$0, $$1);
        Quaternionf $$2 = new Quaternionf().rotateYXZ((float)(-$$1) * ((float)Math.PI / 180), (float)(-$$0) * ((float)Math.PI / 180), 0.0f);
        OctahedralGroup $$3 = OctahedralGroup.IDENTITY;
        for (int $$4 = 0; $$4 < $$1; $$4 += 90) {
            $$3 = $$3.compose(OctahedralGroup.ROT_90_Y_NEG);
        }
        for (int $$5 = 0; $$5 < $$0; $$5 += 90) {
            $$3 = $$3.compose(OctahedralGroup.ROT_90_X_NEG);
        }
        this.transformation = new Transformation(null, $$2, null, null);
        this.actualRotation = $$3;
    }

    @Override
    public Transformation getRotation() {
        return this.transformation;
    }

    public static BlockModelRotation by(int $$0, int $$1) {
        return (BlockModelRotation)BY_INDEX.get((Object)BlockModelRotation.getIndex(Mth.positiveModulo($$0, 360), Mth.positiveModulo($$1, 360)));
    }

    public OctahedralGroup actualRotation() {
        return this.actualRotation;
    }

    static {
        BY_INDEX = (Map)Arrays.stream((Object[])BlockModelRotation.values()).collect(Collectors.toMap($$0 -> $$0.index, $$0 -> $$0));
    }
}