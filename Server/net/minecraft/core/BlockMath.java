/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Supplier
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class BlockMath {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = (Map)Util.make(Maps.newEnumMap(Direction.class), $$0 -> {
        $$0.put((Enum)Direction.SOUTH, (Object)Transformation.identity());
        $$0.put((Enum)Direction.EAST, (Object)new Transformation(null, new Quaternionf().rotateY(1.5707964f), null, null));
        $$0.put((Enum)Direction.WEST, (Object)new Transformation(null, new Quaternionf().rotateY(-1.5707964f), null, null));
        $$0.put((Enum)Direction.NORTH, (Object)new Transformation(null, new Quaternionf().rotateY((float)Math.PI), null, null));
        $$0.put((Enum)Direction.UP, (Object)new Transformation(null, new Quaternionf().rotateX(-1.5707964f), null, null));
        $$0.put((Enum)Direction.DOWN, (Object)new Transformation(null, new Quaternionf().rotateX(1.5707964f), null, null));
    });
    public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = (Map)Util.make(Maps.newEnumMap(Direction.class), $$0 -> {
        for (Direction $$1 : Direction.values()) {
            $$0.put((Enum)$$1, (Object)((Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get((Object)$$1)).inverse());
        }
    });

    public static Transformation blockCenterToCorner(Transformation $$0) {
        Matrix4f $$1 = new Matrix4f().translation(0.5f, 0.5f, 0.5f);
        $$1.mul((Matrix4fc)$$0.getMatrix());
        $$1.translate(-0.5f, -0.5f, -0.5f);
        return new Transformation($$1);
    }

    public static Transformation blockCornerToCenter(Transformation $$0) {
        Matrix4f $$1 = new Matrix4f().translation(-0.5f, -0.5f, -0.5f);
        $$1.mul((Matrix4fc)$$0.getMatrix());
        $$1.translate(0.5f, 0.5f, 0.5f);
        return new Transformation($$1);
    }

    public static Transformation getUVLockTransform(Transformation $$0, Direction $$1, Supplier<String> $$2) {
        Direction $$3 = Direction.rotate($$0.getMatrix(), $$1);
        Transformation $$4 = $$0.inverse();
        if ($$4 == null) {
            LOGGER.warn((String)$$2.get());
            return new Transformation(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        Transformation $$5 = ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get((Object)$$1)).compose($$4).compose((Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get((Object)$$3));
        return BlockMath.blockCenterToCorner($$5);
    }
}