/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.ReflectiveOperationException
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.annotation.Annotation
 *  java.lang.reflect.InvocationTargetException
 *  java.lang.reflect.Method
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

public class GameTestRegistry {
    private static final Collection<TestFunction> TEST_FUNCTIONS = Lists.newArrayList();
    private static final Set<String> TEST_CLASS_NAMES = Sets.newHashSet();
    private static final Map<String, Consumer<ServerLevel>> BEFORE_BATCH_FUNCTIONS = Maps.newHashMap();
    private static final Map<String, Consumer<ServerLevel>> AFTER_BATCH_FUNCTIONS = Maps.newHashMap();
    private static final Collection<TestFunction> LAST_FAILED_TESTS = Sets.newHashSet();

    public static void register(Class<?> $$0) {
        Arrays.stream((Object[])$$0.getDeclaredMethods()).forEach(GameTestRegistry::register);
    }

    public static void register(Method $$0) {
        GameTestGenerator $$3;
        String $$1 = $$0.getDeclaringClass().getSimpleName();
        GameTest $$2 = (GameTest)$$0.getAnnotation(GameTest.class);
        if ($$2 != null) {
            TEST_FUNCTIONS.add((Object)GameTestRegistry.turnMethodIntoTestFunction($$0));
            TEST_CLASS_NAMES.add((Object)$$1);
        }
        if (($$3 = (GameTestGenerator)$$0.getAnnotation(GameTestGenerator.class)) != null) {
            TEST_FUNCTIONS.addAll(GameTestRegistry.useTestGeneratorMethod($$0));
            TEST_CLASS_NAMES.add((Object)$$1);
        }
        GameTestRegistry.registerBatchFunction($$0, BeforeBatch.class, BeforeBatch::batch, BEFORE_BATCH_FUNCTIONS);
        GameTestRegistry.registerBatchFunction($$0, AfterBatch.class, AfterBatch::batch, AFTER_BATCH_FUNCTIONS);
    }

    private static <T extends Annotation> void registerBatchFunction(Method $$0, Class<T> $$1, Function<T, String> $$2, Map<String, Consumer<ServerLevel>> $$3) {
        String $$5;
        Consumer $$6;
        Annotation $$4 = $$0.getAnnotation($$1);
        if ($$4 != null && ($$6 = (Consumer)$$3.putIfAbsent((Object)($$5 = (String)$$2.apply((Object)$$4)), GameTestRegistry.turnMethodIntoConsumer($$0))) != null) {
            throw new RuntimeException("Hey, there should only be one " + $$1 + " method per batch. Batch '" + $$5 + "' has more than one!");
        }
    }

    public static Collection<TestFunction> getTestFunctionsForClassName(String $$0) {
        return (Collection)TEST_FUNCTIONS.stream().filter($$1 -> GameTestRegistry.isTestFunctionPartOfClass($$1, $$0)).collect(Collectors.toList());
    }

    public static Collection<TestFunction> getAllTestFunctions() {
        return TEST_FUNCTIONS;
    }

    public static Collection<String> getAllTestClassNames() {
        return TEST_CLASS_NAMES;
    }

    public static boolean isTestClass(String $$0) {
        return TEST_CLASS_NAMES.contains((Object)$$0);
    }

    @Nullable
    public static Consumer<ServerLevel> getBeforeBatchFunction(String $$0) {
        return (Consumer)BEFORE_BATCH_FUNCTIONS.get((Object)$$0);
    }

    @Nullable
    public static Consumer<ServerLevel> getAfterBatchFunction(String $$0) {
        return (Consumer)AFTER_BATCH_FUNCTIONS.get((Object)$$0);
    }

    public static Optional<TestFunction> findTestFunction(String $$0) {
        return GameTestRegistry.getAllTestFunctions().stream().filter($$1 -> $$1.getTestName().equalsIgnoreCase($$0)).findFirst();
    }

    public static TestFunction getTestFunction(String $$0) {
        Optional<TestFunction> $$1 = GameTestRegistry.findTestFunction($$0);
        if (!$$1.isPresent()) {
            throw new IllegalArgumentException("Can't find the test function for " + $$0);
        }
        return (TestFunction)$$1.get();
    }

    private static Collection<TestFunction> useTestGeneratorMethod(Method $$0) {
        try {
            Object $$1 = $$0.getDeclaringClass().newInstance();
            return (Collection)$$0.invoke($$1, new Object[0]);
        }
        catch (ReflectiveOperationException $$2) {
            throw new RuntimeException((Throwable)$$2);
        }
    }

    private static TestFunction turnMethodIntoTestFunction(Method $$0) {
        GameTest $$1 = (GameTest)$$0.getAnnotation(GameTest.class);
        String $$2 = $$0.getDeclaringClass().getSimpleName();
        String $$3 = $$2.toLowerCase();
        String $$4 = $$3 + "." + $$0.getName().toLowerCase();
        String $$5 = $$1.template().isEmpty() ? $$4 : $$3 + "." + $$1.template();
        String $$6 = $$1.batch();
        Rotation $$7 = StructureUtils.getRotationForRotationSteps($$1.rotationSteps());
        return new TestFunction($$6, $$4, $$5, $$7, $$1.timeoutTicks(), $$1.setupTicks(), $$1.required(), $$1.requiredSuccesses(), $$1.attempts(), GameTestRegistry.turnMethodIntoConsumer($$0));
    }

    private static Consumer<?> turnMethodIntoConsumer(Method $$0) {
        return $$1 -> {
            try {
                Object $$2 = $$0.getDeclaringClass().newInstance();
                $$0.invoke($$2, new Object[]{$$1});
            }
            catch (InvocationTargetException $$3) {
                if ($$3.getCause() instanceof RuntimeException) {
                    throw (RuntimeException)$$3.getCause();
                }
                throw new RuntimeException($$3.getCause());
            }
            catch (ReflectiveOperationException $$4) {
                throw new RuntimeException((Throwable)$$4);
            }
        };
    }

    private static boolean isTestFunctionPartOfClass(TestFunction $$0, String $$1) {
        return $$0.getTestName().toLowerCase().startsWith($$1.toLowerCase() + ".");
    }

    public static Collection<TestFunction> getLastFailedTests() {
        return LAST_FAILED_TESTS;
    }

    public static void rememberFailedTest(TestFunction $$0) {
        LAST_FAILED_TESTS.add((Object)$$0);
    }

    public static void forgetFailedTests() {
        LAST_FAILED_TESTS.clear();
    }
}