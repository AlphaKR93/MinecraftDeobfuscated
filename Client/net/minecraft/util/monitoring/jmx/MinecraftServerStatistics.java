/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Class
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.management.ManagementFactory
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Map
 *  java.util.Objects
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  javax.management.Attribute
 *  javax.management.AttributeList
 *  javax.management.DynamicMBean
 *  javax.management.InstanceAlreadyExistsException
 *  javax.management.MBeanAttributeInfo
 *  javax.management.MBeanInfo
 *  javax.management.MBeanNotificationInfo
 *  javax.management.MBeanRegistrationException
 *  javax.management.MalformedObjectNameException
 *  javax.management.NotCompliantMBeanException
 *  javax.management.ObjectName
 *  net.minecraft.server.MinecraftServer
 *  org.slf4j.Logger
 */
package net.minecraft.util.monitoring.jmx;

import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public final class MinecraftServerStatistics
implements DynamicMBean {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftServer server;
    private final MBeanInfo mBeanInfo;
    private final Map<String, AttributeDescription> attributeDescriptionByName = (Map)Stream.of((Object[])new AttributeDescription[]{new AttributeDescription("tickTimes", (Supplier<Object>)((Supplier)this::getTickTimes), "Historical tick times (ms)", long[].class), new AttributeDescription("averageTickTime", (Supplier<Object>)((Supplier)this::getAverageTickTime), "Current average tick time (ms)", Long.TYPE)}).collect(Collectors.toMap($$0 -> $$0.name, (Function)Function.identity()));

    private MinecraftServerStatistics(MinecraftServer $$02) {
        this.server = $$02;
        MBeanAttributeInfo[] $$1 = (MBeanAttributeInfo[])this.attributeDescriptionByName.values().stream().map(AttributeDescription::asMBeanAttributeInfo).toArray(MBeanAttributeInfo[]::new);
        this.mBeanInfo = new MBeanInfo(MinecraftServerStatistics.class.getSimpleName(), "metrics for dedicated server", $$1, null, null, new MBeanNotificationInfo[0]);
    }

    public static void registerJmxMonitoring(MinecraftServer $$0) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean((Object)new MinecraftServerStatistics($$0), new ObjectName("net.minecraft.server:type=Server"));
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException $$1) {
            LOGGER.warn("Failed to initialise server as JMX bean", $$1);
        }
    }

    private float getAverageTickTime() {
        return this.server.getAverageTickTime();
    }

    private long[] getTickTimes() {
        return this.server.tickTimes;
    }

    @Nullable
    public Object getAttribute(String $$0) {
        AttributeDescription $$1 = (AttributeDescription)this.attributeDescriptionByName.get((Object)$$0);
        return $$1 == null ? null : $$1.getter.get();
    }

    public void setAttribute(Attribute $$0) {
    }

    public AttributeList getAttributes(String[] $$02) {
        List $$1 = (List)Arrays.stream((Object[])$$02).map(arg_0 -> this.attributeDescriptionByName.get(arg_0)).filter(Objects::nonNull).map($$0 -> new Attribute($$0.name, $$0.getter.get())).collect(Collectors.toList());
        return new AttributeList($$1);
    }

    public AttributeList setAttributes(AttributeList $$0) {
        return new AttributeList();
    }

    @Nullable
    public Object invoke(String $$0, Object[] $$1, String[] $$2) {
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }

    static final class AttributeDescription {
        final String name;
        final Supplier<Object> getter;
        private final String description;
        private final Class<?> type;

        AttributeDescription(String $$0, Supplier<Object> $$1, String $$2, Class<?> $$3) {
            this.name = $$0;
            this.getter = $$1;
            this.description = $$2;
            this.type = $$3;
        }

        private MBeanAttributeInfo asMBeanAttributeInfo() {
            return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
        }
    }
}