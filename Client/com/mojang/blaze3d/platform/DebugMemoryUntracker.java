/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.ClassNotFoundException
 *  java.lang.IllegalAccessException
 *  java.lang.Long
 *  java.lang.NoSuchFieldException
 *  java.lang.NoSuchMethodException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.invoke.MethodHandle
 *  java.lang.invoke.MethodHandles
 *  java.lang.invoke.MethodHandles$Lookup
 *  java.lang.reflect.Field
 *  java.lang.reflect.Method
 *  javax.annotation.Nullable
 *  org.lwjgl.system.Pointer
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GLX;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import org.lwjgl.system.Pointer;

public class DebugMemoryUntracker {
    @Nullable
    private static final MethodHandle UNTRACK = (MethodHandle)GLX.make(() -> {
        try {
            MethodHandles.Lookup $$0 = MethodHandles.lookup();
            Class $$1 = Class.forName((String)"org.lwjgl.system.MemoryManage$DebugAllocator");
            Method $$2 = $$1.getDeclaredMethod("untrack", new Class[]{Long.TYPE});
            $$2.setAccessible(true);
            Field $$3 = Class.forName((String)"org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
            $$3.setAccessible(true);
            Object $$4 = $$3.get(null);
            if ($$1.isInstance($$4)) {
                return $$0.unreflect($$2);
            }
            return null;
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException $$5) {
            throw new RuntimeException($$5);
        }
    });

    public static void untrack(long $$0) {
        if (UNTRACK == null) {
            return;
        }
        try {
            UNTRACK.invoke($$0);
        }
        catch (Throwable $$1) {
            throw new RuntimeException($$1);
        }
    }

    public static void untrack(Pointer $$0) {
        DebugMemoryUntracker.untrack($$0.address());
    }
}