/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Number
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Properties
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 *  java.util.function.UnaryOperator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import org.slf4j.Logger;

public abstract class Settings<T extends Settings<T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Properties properties;

    public Settings(Properties $$0) {
        this.properties = $$0;
    }

    public static Properties loadFromFile(Path $$0) {
        Properties $$1 = new Properties();
        try (InputStream $$2 = Files.newInputStream((Path)$$0, (OpenOption[])new OpenOption[0]);){
            $$1.load($$2);
        }
        catch (IOException $$3) {
            LOGGER.error("Failed to load properties from file: {}", (Object)$$0);
        }
        return $$1;
    }

    public void store(Path $$0) {
        try (OutputStream $$1 = Files.newOutputStream((Path)$$0, (OpenOption[])new OpenOption[0]);){
            this.properties.store($$1, "Minecraft server properties");
        }
        catch (IOException $$2) {
            LOGGER.error("Failed to store properties to file: {}", (Object)$$0);
        }
    }

    private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> $$0) {
        return $$1 -> {
            try {
                return (Number)$$0.apply($$1);
            }
            catch (NumberFormatException $$2) {
                return null;
            }
        };
    }

    protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> $$0, Function<String, V> $$1) {
        return $$2 -> {
            try {
                return $$0.apply(Integer.parseInt((String)$$2));
            }
            catch (NumberFormatException $$3) {
                return $$1.apply($$2);
            }
        };
    }

    @Nullable
    private String getStringRaw(String $$0) {
        return (String)this.properties.get((Object)$$0);
    }

    @Nullable
    protected <V> V getLegacy(String $$0, Function<String, V> $$1) {
        String $$2 = this.getStringRaw($$0);
        if ($$2 == null) {
            return null;
        }
        this.properties.remove((Object)$$0);
        return (V)$$1.apply((Object)$$2);
    }

    protected <V> V get(String $$0, Function<String, V> $$1, Function<V, String> $$2, V $$3) {
        String $$4 = this.getStringRaw($$0);
        Object $$5 = MoreObjects.firstNonNull((Object)($$4 != null ? $$1.apply((Object)$$4) : null), $$3);
        this.properties.put((Object)$$0, $$2.apply($$5));
        return (V)$$5;
    }

    protected <V> MutableValue<V> getMutable(String $$0, Function<String, V> $$1, Function<V, String> $$2, V $$3) {
        String $$4 = this.getStringRaw($$0);
        Object $$5 = MoreObjects.firstNonNull((Object)($$4 != null ? $$1.apply((Object)$$4) : null), $$3);
        this.properties.put((Object)$$0, $$2.apply($$5));
        return new MutableValue<Object>($$0, $$5, $$2);
    }

    protected <V> V get(String $$0, Function<String, V> $$1, UnaryOperator<V> $$22, Function<V, String> $$3, V $$4) {
        return this.get($$0, $$2 -> {
            Object $$3 = $$1.apply($$2);
            return $$3 != null ? $$22.apply($$3) : null;
        }, $$3, $$4);
    }

    protected <V> V get(String $$0, Function<String, V> $$1, V $$2) {
        return this.get($$0, $$1, Objects::toString, $$2);
    }

    protected <V> MutableValue<V> getMutable(String $$0, Function<String, V> $$1, V $$2) {
        return this.getMutable($$0, $$1, Objects::toString, $$2);
    }

    protected String get(String $$0, String $$1) {
        return this.get($$0, Function.identity(), Function.identity(), $$1);
    }

    @Nullable
    protected String getLegacyString(String $$0) {
        return (String)this.getLegacy($$0, Function.identity());
    }

    protected int get(String $$0, int $$1) {
        return this.get($$0, Settings.wrapNumberDeserializer(Integer::parseInt), Integer.valueOf((int)$$1));
    }

    protected MutableValue<Integer> getMutable(String $$0, int $$1) {
        return this.getMutable($$0, Settings.wrapNumberDeserializer(Integer::parseInt), $$1);
    }

    protected int get(String $$0, UnaryOperator<Integer> $$1, int $$2) {
        return this.get($$0, Settings.wrapNumberDeserializer(Integer::parseInt), $$1, Objects::toString, $$2);
    }

    protected long get(String $$0, long $$1) {
        return this.get($$0, Settings.wrapNumberDeserializer(Long::parseLong), $$1);
    }

    protected boolean get(String $$0, boolean $$1) {
        return this.get($$0, Boolean::valueOf, $$1);
    }

    protected MutableValue<Boolean> getMutable(String $$0, boolean $$1) {
        return this.getMutable($$0, Boolean::valueOf, $$1);
    }

    @Nullable
    protected Boolean getLegacyBoolean(String $$0) {
        return (Boolean)this.getLegacy($$0, Boolean::valueOf);
    }

    protected Properties cloneProperties() {
        Properties $$0 = new Properties();
        $$0.putAll((Map)this.properties);
        return $$0;
    }

    protected abstract T reload(RegistryAccess var1, Properties var2);

    public class MutableValue<V>
    implements Supplier<V> {
        private final String key;
        private final V value;
        private final Function<V, String> serializer;

        MutableValue(String $$1, V $$2, Function<V, String> $$3) {
            this.key = $$1;
            this.value = $$2;
            this.serializer = $$3;
        }

        public V get() {
            return this.value;
        }

        public T update(RegistryAccess $$0, V $$1) {
            Properties $$2 = Settings.this.cloneProperties();
            $$2.put((Object)this.key, this.serializer.apply($$1));
            return Settings.this.reload($$0, $$2);
        }
    }
}