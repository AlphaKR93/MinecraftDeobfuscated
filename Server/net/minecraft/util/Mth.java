/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.Locale
 *  java.util.UUID
 *  java.util.function.IntPredicate
 *  java.util.stream.IntStream
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.joml.Math
 */
package net.minecraft.util;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.NumberUtils;

public class Mth {
    private static final long UUID_VERSION = 61440L;
    private static final long UUID_VERSION_TYPE_4 = 16384L;
    private static final long UUID_VARIANT = -4611686018427387904L;
    private static final long UUID_VARIANT_2 = Long.MIN_VALUE;
    public static final float PI = (float)Math.PI;
    public static final float HALF_PI = 1.5707964f;
    public static final float TWO_PI = (float)Math.PI * 2;
    public static final float DEG_TO_RAD = (float)Math.PI / 180;
    public static final float RAD_TO_DEG = 57.295776f;
    public static final float EPSILON = 1.0E-5f;
    public static final float SQRT_OF_TWO = Mth.sqrt(2.0f);
    private static final float SIN_SCALE = 10430.378f;
    private static final float[] SIN = Util.make(new float[65536], $$0 -> {
        for (int $$1 = 0; $$1 < ((float[])$$0).length; ++$$1) {
            $$0[$$1] = (float)Math.sin((double)((double)$$1 * Math.PI * 2.0 / 65536.0));
        }
    });
    private static final RandomSource RANDOM = RandomSource.createThreadSafe();
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double ONE_SIXTH = 0.16666666666666666;
    private static final int FRAC_EXP = 8;
    private static final int LUT_SIZE = 257;
    private static final double FRAC_BIAS = Double.longBitsToDouble((long)4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    public static float sin(float $$0) {
        return SIN[(int)($$0 * 10430.378f) & 0xFFFF];
    }

    public static float cos(float $$0) {
        return SIN[(int)($$0 * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float $$0) {
        return (float)Math.sqrt((double)$$0);
    }

    public static int floor(float $$0) {
        int $$1 = (int)$$0;
        return $$0 < (float)$$1 ? $$1 - 1 : $$1;
    }

    public static int floor(double $$0) {
        int $$1 = (int)$$0;
        return $$0 < (double)$$1 ? $$1 - 1 : $$1;
    }

    public static long lfloor(double $$0) {
        long $$1 = (long)$$0;
        return $$0 < (double)$$1 ? $$1 - 1L : $$1;
    }

    public static float abs(float $$0) {
        return Math.abs((float)$$0);
    }

    public static int abs(int $$0) {
        return Math.abs((int)$$0);
    }

    public static int ceil(float $$0) {
        int $$1 = (int)$$0;
        return $$0 > (float)$$1 ? $$1 + 1 : $$1;
    }

    public static int ceil(double $$0) {
        int $$1 = (int)$$0;
        return $$0 > (double)$$1 ? $$1 + 1 : $$1;
    }

    public static int clamp(int $$0, int $$1, int $$2) {
        return Math.min((int)Math.max((int)$$0, (int)$$1), (int)$$2);
    }

    public static float clamp(float $$0, float $$1, float $$2) {
        if ($$0 < $$1) {
            return $$1;
        }
        return Math.min((float)$$0, (float)$$2);
    }

    public static double clamp(double $$0, double $$1, double $$2) {
        if ($$0 < $$1) {
            return $$1;
        }
        return Math.min((double)$$0, (double)$$2);
    }

    public static double clampedLerp(double $$0, double $$1, double $$2) {
        if ($$2 < 0.0) {
            return $$0;
        }
        if ($$2 > 1.0) {
            return $$1;
        }
        return Mth.lerp($$2, $$0, $$1);
    }

    public static float clampedLerp(float $$0, float $$1, float $$2) {
        if ($$2 < 0.0f) {
            return $$0;
        }
        if ($$2 > 1.0f) {
            return $$1;
        }
        return Mth.lerp($$2, $$0, $$1);
    }

    public static double absMax(double $$0, double $$1) {
        if ($$0 < 0.0) {
            $$0 = -$$0;
        }
        if ($$1 < 0.0) {
            $$1 = -$$1;
        }
        return Math.max((double)$$0, (double)$$1);
    }

    public static int floorDiv(int $$0, int $$1) {
        return Math.floorDiv((int)$$0, (int)$$1);
    }

    public static int nextInt(RandomSource $$0, int $$1, int $$2) {
        if ($$1 >= $$2) {
            return $$1;
        }
        return $$0.nextInt($$2 - $$1 + 1) + $$1;
    }

    public static float nextFloat(RandomSource $$0, float $$1, float $$2) {
        if ($$1 >= $$2) {
            return $$1;
        }
        return $$0.nextFloat() * ($$2 - $$1) + $$1;
    }

    public static double nextDouble(RandomSource $$0, double $$1, double $$2) {
        if ($$1 >= $$2) {
            return $$1;
        }
        return $$0.nextDouble() * ($$2 - $$1) + $$1;
    }

    public static boolean equal(float $$0, float $$1) {
        return Math.abs((float)($$1 - $$0)) < 1.0E-5f;
    }

    public static boolean equal(double $$0, double $$1) {
        return Math.abs((double)($$1 - $$0)) < (double)1.0E-5f;
    }

    public static int positiveModulo(int $$0, int $$1) {
        return Math.floorMod((int)$$0, (int)$$1);
    }

    public static float positiveModulo(float $$0, float $$1) {
        return ($$0 % $$1 + $$1) % $$1;
    }

    public static double positiveModulo(double $$0, double $$1) {
        return ($$0 % $$1 + $$1) % $$1;
    }

    public static boolean isMultipleOf(int $$0, int $$1) {
        return $$0 % $$1 == 0;
    }

    public static int wrapDegrees(int $$0) {
        int $$1 = $$0 % 360;
        if ($$1 >= 180) {
            $$1 -= 360;
        }
        if ($$1 < -180) {
            $$1 += 360;
        }
        return $$1;
    }

    public static float wrapDegrees(float $$0) {
        float $$1 = $$0 % 360.0f;
        if ($$1 >= 180.0f) {
            $$1 -= 360.0f;
        }
        if ($$1 < -180.0f) {
            $$1 += 360.0f;
        }
        return $$1;
    }

    public static double wrapDegrees(double $$0) {
        double $$1 = $$0 % 360.0;
        if ($$1 >= 180.0) {
            $$1 -= 360.0;
        }
        if ($$1 < -180.0) {
            $$1 += 360.0;
        }
        return $$1;
    }

    public static float degreesDifference(float $$0, float $$1) {
        return Mth.wrapDegrees($$1 - $$0);
    }

    public static float degreesDifferenceAbs(float $$0, float $$1) {
        return Mth.abs(Mth.degreesDifference($$0, $$1));
    }

    public static float rotateIfNecessary(float $$0, float $$1, float $$2) {
        float $$3 = Mth.degreesDifference($$0, $$1);
        float $$4 = Mth.clamp($$3, -$$2, $$2);
        return $$1 - $$4;
    }

    public static float approach(float $$0, float $$1, float $$2) {
        $$2 = Mth.abs($$2);
        if ($$0 < $$1) {
            return Mth.clamp($$0 + $$2, $$0, $$1);
        }
        return Mth.clamp($$0 - $$2, $$1, $$0);
    }

    public static float approachDegrees(float $$0, float $$1, float $$2) {
        float $$3 = Mth.degreesDifference($$0, $$1);
        return Mth.approach($$0, $$0 + $$3, $$2);
    }

    public static int getInt(String $$0, int $$1) {
        return NumberUtils.toInt((String)$$0, (int)$$1);
    }

    public static int smallestEncompassingPowerOfTwo(int $$0) {
        int $$1 = $$0 - 1;
        $$1 |= $$1 >> 1;
        $$1 |= $$1 >> 2;
        $$1 |= $$1 >> 4;
        $$1 |= $$1 >> 8;
        $$1 |= $$1 >> 16;
        return $$1 + 1;
    }

    public static boolean isPowerOfTwo(int $$0) {
        return $$0 != 0 && ($$0 & $$0 - 1) == 0;
    }

    public static int ceillog2(int $$0) {
        $$0 = Mth.isPowerOfTwo($$0) ? $$0 : Mth.smallestEncompassingPowerOfTwo($$0);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)$$0 * 125613361L >> 27) & 0x1F];
    }

    public static int log2(int $$0) {
        return Mth.ceillog2($$0) - (Mth.isPowerOfTwo($$0) ? 0 : 1);
    }

    public static int color(float $$0, float $$1, float $$2) {
        return FastColor.ARGB32.color(0, Mth.floor($$0 * 255.0f), Mth.floor($$1 * 255.0f), Mth.floor($$2 * 255.0f));
    }

    public static float frac(float $$0) {
        return $$0 - (float)Mth.floor($$0);
    }

    public static double frac(double $$0) {
        return $$0 - (double)Mth.lfloor($$0);
    }

    @Deprecated
    public static long getSeed(Vec3i $$0) {
        return Mth.getSeed($$0.getX(), $$0.getY(), $$0.getZ());
    }

    @Deprecated
    public static long getSeed(int $$0, int $$1, int $$2) {
        long $$3 = (long)($$0 * 3129871) ^ (long)$$2 * 116129781L ^ (long)$$1;
        $$3 = $$3 * $$3 * 42317861L + $$3 * 11L;
        return $$3 >> 16;
    }

    public static UUID createInsecureUUID(RandomSource $$0) {
        long $$1 = $$0.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        long $$2 = $$0.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID($$1, $$2);
    }

    public static UUID createInsecureUUID() {
        return Mth.createInsecureUUID(RANDOM);
    }

    public static double inverseLerp(double $$0, double $$1, double $$2) {
        return ($$0 - $$1) / ($$2 - $$1);
    }

    public static float inverseLerp(float $$0, float $$1, float $$2) {
        return ($$0 - $$1) / ($$2 - $$1);
    }

    public static boolean rayIntersectsAABB(Vec3 $$0, Vec3 $$1, AABB $$2) {
        double $$3 = ($$2.minX + $$2.maxX) * 0.5;
        double $$4 = ($$2.maxX - $$2.minX) * 0.5;
        double $$5 = $$0.x - $$3;
        if (Math.abs((double)$$5) > $$4 && $$5 * $$1.x >= 0.0) {
            return false;
        }
        double $$6 = ($$2.minY + $$2.maxY) * 0.5;
        double $$7 = ($$2.maxY - $$2.minY) * 0.5;
        double $$8 = $$0.y - $$6;
        if (Math.abs((double)$$8) > $$7 && $$8 * $$1.y >= 0.0) {
            return false;
        }
        double $$9 = ($$2.minZ + $$2.maxZ) * 0.5;
        double $$10 = ($$2.maxZ - $$2.minZ) * 0.5;
        double $$11 = $$0.z - $$9;
        if (Math.abs((double)$$11) > $$10 && $$11 * $$1.z >= 0.0) {
            return false;
        }
        double $$12 = Math.abs((double)$$1.x);
        double $$13 = Math.abs((double)$$1.y);
        double $$14 = Math.abs((double)$$1.z);
        double $$15 = $$1.y * $$11 - $$1.z * $$8;
        if (Math.abs((double)$$15) > $$7 * $$14 + $$10 * $$13) {
            return false;
        }
        $$15 = $$1.z * $$5 - $$1.x * $$11;
        if (Math.abs((double)$$15) > $$4 * $$14 + $$10 * $$12) {
            return false;
        }
        $$15 = $$1.x * $$8 - $$1.y * $$5;
        return Math.abs((double)$$15) < $$4 * $$13 + $$7 * $$12;
    }

    public static double atan2(double $$0, double $$1) {
        boolean $$5;
        boolean $$4;
        boolean $$3;
        double $$2 = $$1 * $$1 + $$0 * $$0;
        if (Double.isNaN((double)$$2)) {
            return Double.NaN;
        }
        boolean bl = $$3 = $$0 < 0.0;
        if ($$3) {
            $$0 = -$$0;
        }
        boolean bl2 = $$4 = $$1 < 0.0;
        if ($$4) {
            $$1 = -$$1;
        }
        boolean bl3 = $$5 = $$0 > $$1;
        if ($$5) {
            double $$6 = $$1;
            $$1 = $$0;
            $$0 = $$6;
        }
        double $$7 = Mth.fastInvSqrt($$2);
        $$1 *= $$7;
        double $$8 = FRAC_BIAS + ($$0 *= $$7);
        int $$9 = (int)Double.doubleToRawLongBits((double)$$8);
        double $$10 = ASIN_TAB[$$9];
        double $$11 = COS_TAB[$$9];
        double $$12 = $$8 - FRAC_BIAS;
        double $$13 = $$0 * $$11 - $$1 * $$12;
        double $$14 = (6.0 + $$13 * $$13) * $$13 * 0.16666666666666666;
        double $$15 = $$10 + $$14;
        if ($$5) {
            $$15 = 1.5707963267948966 - $$15;
        }
        if ($$4) {
            $$15 = Math.PI - $$15;
        }
        if ($$3) {
            $$15 = -$$15;
        }
        return $$15;
    }

    public static float invSqrt(float $$0) {
        return org.joml.Math.invsqrt((float)$$0);
    }

    public static double invSqrt(double $$0) {
        return org.joml.Math.invsqrt((double)$$0);
    }

    @Deprecated
    public static double fastInvSqrt(double $$0) {
        double $$1 = 0.5 * $$0;
        long $$2 = Double.doubleToRawLongBits((double)$$0);
        $$2 = 6910469410427058090L - ($$2 >> 1);
        $$0 = Double.longBitsToDouble((long)$$2);
        $$0 *= 1.5 - $$1 * $$0 * $$0;
        return $$0;
    }

    public static float fastInvCubeRoot(float $$0) {
        int $$1 = Float.floatToIntBits((float)$$0);
        $$1 = 1419967116 - $$1 / 3;
        float $$2 = Float.intBitsToFloat((int)$$1);
        $$2 = 0.6666667f * $$2 + 1.0f / (3.0f * $$2 * $$2 * $$0);
        $$2 = 0.6666667f * $$2 + 1.0f / (3.0f * $$2 * $$2 * $$0);
        return $$2;
    }

    /*
     * WARNING - void declaration
     */
    public static int hsvToRgb(float $$0, float $$1, float $$2) {
        void $$28;
        void $$27;
        void $$26;
        int $$3 = (int)($$0 * 6.0f) % 6;
        float $$4 = $$0 * 6.0f - (float)$$3;
        float $$5 = $$2 * (1.0f - $$1);
        float $$6 = $$2 * (1.0f - $$4 * $$1);
        float $$7 = $$2 * (1.0f - (1.0f - $$4) * $$1);
        switch ($$3) {
            case 0: {
                float $$8 = $$2;
                float $$9 = $$7;
                float $$10 = $$5;
                break;
            }
            case 1: {
                float $$11 = $$6;
                float $$12 = $$2;
                float $$13 = $$5;
                break;
            }
            case 2: {
                float $$14 = $$5;
                float $$15 = $$2;
                float $$16 = $$7;
                break;
            }
            case 3: {
                float $$17 = $$5;
                float $$18 = $$6;
                float $$19 = $$2;
                break;
            }
            case 4: {
                float $$20 = $$7;
                float $$21 = $$5;
                float $$22 = $$2;
                break;
            }
            case 5: {
                float $$23 = $$2;
                float $$24 = $$5;
                float $$25 = $$6;
                break;
            }
            default: {
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + $$0 + ", " + $$1 + ", " + $$2);
            }
        }
        return FastColor.ARGB32.color(0, Mth.clamp((int)($$26 * 255.0f), 0, 255), Mth.clamp((int)($$27 * 255.0f), 0, 255), Mth.clamp((int)($$28 * 255.0f), 0, 255));
    }

    public static int murmurHash3Mixer(int $$0) {
        $$0 ^= $$0 >>> 16;
        $$0 *= -2048144789;
        $$0 ^= $$0 >>> 13;
        $$0 *= -1028477387;
        $$0 ^= $$0 >>> 16;
        return $$0;
    }

    public static int binarySearch(int $$0, int $$1, IntPredicate $$2) {
        int $$3 = $$1 - $$0;
        while ($$3 > 0) {
            int $$4 = $$3 / 2;
            int $$5 = $$0 + $$4;
            if ($$2.test($$5)) {
                $$3 = $$4;
                continue;
            }
            $$0 = $$5 + 1;
            $$3 -= $$4 + 1;
        }
        return $$0;
    }

    public static float lerp(float $$0, float $$1, float $$2) {
        return $$1 + $$0 * ($$2 - $$1);
    }

    public static double lerp(double $$0, double $$1, double $$2) {
        return $$1 + $$0 * ($$2 - $$1);
    }

    public static double lerp2(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5) {
        return Mth.lerp($$1, Mth.lerp($$0, $$2, $$3), Mth.lerp($$0, $$4, $$5));
    }

    public static double lerp3(double $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, double $$8, double $$9, double $$10) {
        return Mth.lerp($$2, Mth.lerp2($$0, $$1, $$3, $$4, $$5, $$6), Mth.lerp2($$0, $$1, $$7, $$8, $$9, $$10));
    }

    public static float catmullrom(float $$0, float $$1, float $$2, float $$3, float $$4) {
        return 0.5f * (2.0f * $$2 + ($$3 - $$1) * $$0 + (2.0f * $$1 - 5.0f * $$2 + 4.0f * $$3 - $$4) * $$0 * $$0 + (3.0f * $$2 - $$1 - 3.0f * $$3 + $$4) * $$0 * $$0 * $$0);
    }

    public static double smoothstep(double $$0) {
        return $$0 * $$0 * $$0 * ($$0 * ($$0 * 6.0 - 15.0) + 10.0);
    }

    public static double smoothstepDerivative(double $$0) {
        return 30.0 * $$0 * $$0 * ($$0 - 1.0) * ($$0 - 1.0);
    }

    public static int sign(double $$0) {
        if ($$0 == 0.0) {
            return 0;
        }
        return $$0 > 0.0 ? 1 : -1;
    }

    public static float rotLerp(float $$0, float $$1, float $$2) {
        return $$1 + $$0 * Mth.wrapDegrees($$2 - $$1);
    }

    public static float triangleWave(float $$0, float $$1) {
        return (Math.abs((float)($$0 % $$1 - $$1 * 0.5f)) - $$1 * 0.25f) / ($$1 * 0.25f);
    }

    public static float square(float $$0) {
        return $$0 * $$0;
    }

    public static double square(double $$0) {
        return $$0 * $$0;
    }

    public static int square(int $$0) {
        return $$0 * $$0;
    }

    public static long square(long $$0) {
        return $$0 * $$0;
    }

    public static double clampedMap(double $$0, double $$1, double $$2, double $$3, double $$4) {
        return Mth.clampedLerp($$3, $$4, Mth.inverseLerp($$0, $$1, $$2));
    }

    public static float clampedMap(float $$0, float $$1, float $$2, float $$3, float $$4) {
        return Mth.clampedLerp($$3, $$4, Mth.inverseLerp($$0, $$1, $$2));
    }

    public static double map(double $$0, double $$1, double $$2, double $$3, double $$4) {
        return Mth.lerp(Mth.inverseLerp($$0, $$1, $$2), $$3, $$4);
    }

    public static float map(float $$0, float $$1, float $$2, float $$3, float $$4) {
        return Mth.lerp(Mth.inverseLerp($$0, $$1, $$2), $$3, $$4);
    }

    public static double wobble(double $$0) {
        return $$0 + (2.0 * RandomSource.create(Mth.floor($$0 * 3000.0)).nextDouble() - 1.0) * 1.0E-7 / 2.0;
    }

    public static int roundToward(int $$0, int $$1) {
        return Mth.positiveCeilDiv($$0, $$1) * $$1;
    }

    public static int positiveCeilDiv(int $$0, int $$1) {
        return -Math.floorDiv((int)(-$$0), (int)$$1);
    }

    public static int randomBetweenInclusive(RandomSource $$0, int $$1, int $$2) {
        return $$0.nextInt($$2 - $$1 + 1) + $$1;
    }

    public static float randomBetween(RandomSource $$0, float $$1, float $$2) {
        return $$0.nextFloat() * ($$2 - $$1) + $$1;
    }

    public static float normal(RandomSource $$0, float $$1, float $$2) {
        return $$1 + (float)$$0.nextGaussian() * $$2;
    }

    public static double lengthSquared(double $$0, double $$1) {
        return $$0 * $$0 + $$1 * $$1;
    }

    public static double length(double $$0, double $$1) {
        return Math.sqrt((double)Mth.lengthSquared($$0, $$1));
    }

    public static double lengthSquared(double $$0, double $$1, double $$2) {
        return $$0 * $$0 + $$1 * $$1 + $$2 * $$2;
    }

    public static double length(double $$0, double $$1, double $$2) {
        return Math.sqrt((double)Mth.lengthSquared($$0, $$1, $$2));
    }

    public static int quantize(double $$0, int $$1) {
        return Mth.floor($$0 / (double)$$1) * $$1;
    }

    public static IntStream outFromOrigin(int $$0, int $$1, int $$2) {
        return Mth.outFromOrigin($$0, $$1, $$2, 1);
    }

    public static IntStream outFromOrigin(int $$0, int $$1, int $$2, int $$32) {
        if ($$1 > $$2) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"upperbound %d expected to be > lowerBound %d", (Object[])new Object[]{$$2, $$1}));
        }
        if ($$32 < 1) {
            throw new IllegalArgumentException(String.format((Locale)Locale.ROOT, (String)"steps expected to be >= 1, was %d", (Object[])new Object[]{$$32}));
        }
        if ($$0 < $$1 || $$0 > $$2) {
            return IntStream.empty();
        }
        return IntStream.iterate((int)$$0, $$3 -> {
            int $$4 = Math.abs((int)($$0 - $$3));
            return $$0 - $$4 >= $$1 || $$0 + $$4 <= $$2;
        }, $$4 -> {
            int $$8;
            boolean $$7;
            boolean $$5 = $$4 <= $$0;
            int $$6 = Math.abs((int)($$0 - $$4));
            boolean bl = $$7 = $$0 + $$6 + $$32 <= $$2;
            if (!($$5 && $$7 || ($$8 = $$0 - $$6 - ($$5 ? $$32 : 0)) < $$1)) {
                return $$8;
            }
            return $$0 + $$6 + $$32;
        });
    }

    static {
        for (int $$02 = 0; $$02 < 257; ++$$02) {
            double $$1 = (double)$$02 / 256.0;
            double $$2 = Math.asin((double)$$1);
            Mth.COS_TAB[$$02] = Math.cos((double)$$2);
            Mth.ASIN_TAB[$$02] = $$2;
        }
    }
}