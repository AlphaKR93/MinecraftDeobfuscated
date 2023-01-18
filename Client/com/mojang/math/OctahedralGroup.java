/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.booleans.BooleanArrayList
 *  it.unimi.dsi.fastutil.booleans.BooleanList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Map
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 */
package com.mojang.math;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.SymmetricGroup3;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public enum OctahedralGroup implements StringRepresentable
{
    IDENTITY("identity", SymmetricGroup3.P123, false, false, false),
    ROT_180_FACE_XY("rot_180_face_xy", SymmetricGroup3.P123, true, true, false),
    ROT_180_FACE_XZ("rot_180_face_xz", SymmetricGroup3.P123, true, false, true),
    ROT_180_FACE_YZ("rot_180_face_yz", SymmetricGroup3.P123, false, true, true),
    ROT_120_NNN("rot_120_nnn", SymmetricGroup3.P231, false, false, false),
    ROT_120_NNP("rot_120_nnp", SymmetricGroup3.P312, true, false, true),
    ROT_120_NPN("rot_120_npn", SymmetricGroup3.P312, false, true, true),
    ROT_120_NPP("rot_120_npp", SymmetricGroup3.P231, true, false, true),
    ROT_120_PNN("rot_120_pnn", SymmetricGroup3.P312, true, true, false),
    ROT_120_PNP("rot_120_pnp", SymmetricGroup3.P231, true, true, false),
    ROT_120_PPN("rot_120_ppn", SymmetricGroup3.P231, false, true, true),
    ROT_120_PPP("rot_120_ppp", SymmetricGroup3.P312, false, false, false),
    ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", SymmetricGroup3.P213, true, true, true),
    ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", SymmetricGroup3.P213, false, false, true),
    ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", SymmetricGroup3.P321, true, true, true),
    ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", SymmetricGroup3.P321, false, true, false),
    ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", SymmetricGroup3.P132, true, true, true),
    ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", SymmetricGroup3.P132, true, false, false),
    ROT_90_X_NEG("rot_90_x_neg", SymmetricGroup3.P132, false, false, true),
    ROT_90_X_POS("rot_90_x_pos", SymmetricGroup3.P132, false, true, false),
    ROT_90_Y_NEG("rot_90_y_neg", SymmetricGroup3.P321, true, false, false),
    ROT_90_Y_POS("rot_90_y_pos", SymmetricGroup3.P321, false, false, true),
    ROT_90_Z_NEG("rot_90_z_neg", SymmetricGroup3.P213, false, true, false),
    ROT_90_Z_POS("rot_90_z_pos", SymmetricGroup3.P213, true, false, false),
    INVERSION("inversion", SymmetricGroup3.P123, true, true, true),
    INVERT_X("invert_x", SymmetricGroup3.P123, true, false, false),
    INVERT_Y("invert_y", SymmetricGroup3.P123, false, true, false),
    INVERT_Z("invert_z", SymmetricGroup3.P123, false, false, true),
    ROT_60_REF_NNN("rot_60_ref_nnn", SymmetricGroup3.P312, true, true, true),
    ROT_60_REF_NNP("rot_60_ref_nnp", SymmetricGroup3.P231, true, false, false),
    ROT_60_REF_NPN("rot_60_ref_npn", SymmetricGroup3.P231, false, false, true),
    ROT_60_REF_NPP("rot_60_ref_npp", SymmetricGroup3.P312, false, false, true),
    ROT_60_REF_PNN("rot_60_ref_pnn", SymmetricGroup3.P231, false, true, false),
    ROT_60_REF_PNP("rot_60_ref_pnp", SymmetricGroup3.P312, true, false, false),
    ROT_60_REF_PPN("rot_60_ref_ppn", SymmetricGroup3.P312, false, true, false),
    ROT_60_REF_PPP("rot_60_ref_ppp", SymmetricGroup3.P231, true, true, true),
    SWAP_XY("swap_xy", SymmetricGroup3.P213, false, false, false),
    SWAP_YZ("swap_yz", SymmetricGroup3.P132, false, false, false),
    SWAP_XZ("swap_xz", SymmetricGroup3.P321, false, false, false),
    SWAP_NEG_XY("swap_neg_xy", SymmetricGroup3.P213, true, true, false),
    SWAP_NEG_YZ("swap_neg_yz", SymmetricGroup3.P132, false, true, true),
    SWAP_NEG_XZ("swap_neg_xz", SymmetricGroup3.P321, true, false, true),
    ROT_90_REF_X_NEG("rot_90_ref_x_neg", SymmetricGroup3.P132, true, false, true),
    ROT_90_REF_X_POS("rot_90_ref_x_pos", SymmetricGroup3.P132, true, true, false),
    ROT_90_REF_Y_NEG("rot_90_ref_y_neg", SymmetricGroup3.P321, true, true, false),
    ROT_90_REF_Y_POS("rot_90_ref_y_pos", SymmetricGroup3.P321, false, true, true),
    ROT_90_REF_Z_NEG("rot_90_ref_z_neg", SymmetricGroup3.P213, false, true, true),
    ROT_90_REF_Z_POS("rot_90_ref_z_pos", SymmetricGroup3.P213, true, false, true);

    private final Matrix3f transformation;
    private final String name;
    @Nullable
    private Map<Direction, Direction> rotatedDirections;
    private final boolean invertX;
    private final boolean invertY;
    private final boolean invertZ;
    private final SymmetricGroup3 permutation;
    private static final OctahedralGroup[][] cayleyTable;
    private static final OctahedralGroup[] inverseTable;

    private OctahedralGroup(String $$0, SymmetricGroup3 $$1, boolean $$2, boolean $$3, boolean $$4) {
        this.name = $$0;
        this.invertX = $$2;
        this.invertY = $$3;
        this.invertZ = $$4;
        this.permutation = $$1;
        this.transformation = new Matrix3f().scaling($$2 ? -1.0f : 1.0f, $$3 ? -1.0f : 1.0f, $$4 ? -1.0f : 1.0f);
        this.transformation.mul((Matrix3fc)$$1.transformation());
    }

    private BooleanList packInversions() {
        return new BooleanArrayList(new boolean[]{this.invertX, this.invertY, this.invertZ});
    }

    public OctahedralGroup compose(OctahedralGroup $$0) {
        return cayleyTable[this.ordinal()][$$0.ordinal()];
    }

    public OctahedralGroup inverse() {
        return inverseTable[this.ordinal()];
    }

    public Matrix3f transformation() {
        return new Matrix3f((Matrix3fc)this.transformation);
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Direction rotate(Direction $$0) {
        if (this.rotatedDirections == null) {
            this.rotatedDirections = Maps.newEnumMap(Direction.class);
            Direction.Axis[] $$1 = Direction.Axis.values();
            for (Direction $$2 : Direction.values()) {
                Direction.Axis $$3 = $$2.getAxis();
                Direction.AxisDirection $$4 = $$2.getAxisDirection();
                Direction.Axis $$5 = $$1[this.permutation.permutation($$3.ordinal())];
                Direction.AxisDirection $$6 = this.inverts($$5) ? $$4.opposite() : $$4;
                Direction $$7 = Direction.fromAxisAndDirection($$5, $$6);
                this.rotatedDirections.put((Object)$$2, (Object)$$7);
            }
        }
        return (Direction)this.rotatedDirections.get((Object)$$0);
    }

    public boolean inverts(Direction.Axis $$0) {
        switch ($$0) {
            case X: {
                return this.invertX;
            }
            case Y: {
                return this.invertY;
            }
        }
        return this.invertZ;
    }

    public FrontAndTop rotate(FrontAndTop $$0) {
        return FrontAndTop.fromFrontAndTop(this.rotate($$0.front()), this.rotate($$0.top()));
    }

    static {
        cayleyTable = Util.make(new OctahedralGroup[OctahedralGroup.values().length][OctahedralGroup.values().length], $$02 -> {
            Map $$1 = (Map)Arrays.stream((Object[])OctahedralGroup.values()).collect(Collectors.toMap($$0 -> Pair.of((Object)((Object)$$0.permutation), (Object)$$0.packInversions()), $$0 -> $$0));
            for (OctahedralGroup $$2 : OctahedralGroup.values()) {
                for (OctahedralGroup $$3 : OctahedralGroup.values()) {
                    BooleanList $$4 = $$2.packInversions();
                    BooleanList $$5 = $$3.packInversions();
                    SymmetricGroup3 $$6 = $$3.permutation.compose($$2.permutation);
                    BooleanArrayList $$7 = new BooleanArrayList(3);
                    for (int $$8 = 0; $$8 < 3; ++$$8) {
                        $$7.add($$4.getBoolean($$8) ^ $$5.getBoolean($$2.permutation.permutation($$8)));
                    }
                    $$02[$$2.ordinal()][$$3.ordinal()] = (OctahedralGroup)$$1.get((Object)Pair.of((Object)((Object)$$6), (Object)$$7));
                }
            }
        });
        inverseTable = (OctahedralGroup[])Arrays.stream((Object[])OctahedralGroup.values()).map($$0 -> (OctahedralGroup)Arrays.stream((Object[])OctahedralGroup.values()).filter($$1 -> $$0.compose((OctahedralGroup)$$1) == IDENTITY).findAny().get()).toArray(OctahedralGroup[]::new);
    }
}