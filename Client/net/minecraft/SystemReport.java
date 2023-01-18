/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 *  oshi.SystemInfo
 *  oshi.hardware.CentralProcessor
 *  oshi.hardware.CentralProcessor$ProcessorIdentifier
 *  oshi.hardware.GlobalMemory
 *  oshi.hardware.GraphicsCard
 *  oshi.hardware.HardwareAbstractionLayer
 *  oshi.hardware.PhysicalMemory
 *  oshi.hardware.VirtualMemory
 */
package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

public class SystemReport {
    public static final long BYTES_PER_MEBIBYTE = 0x100000L;
    private static final long ONE_GIGA = 1000000000L;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String OPERATING_SYSTEM = System.getProperty((String)"os.name") + " (" + System.getProperty((String)"os.arch") + ") version " + System.getProperty((String)"os.version");
    private static final String JAVA_VERSION = System.getProperty((String)"java.version") + ", " + System.getProperty((String)"java.vendor");
    private static final String JAVA_VM_VERSION = System.getProperty((String)"java.vm.name") + " (" + System.getProperty((String)"java.vm.info") + "), " + System.getProperty((String)"java.vm.vendor");
    private final Map<String, String> entries = Maps.newLinkedHashMap();

    public SystemReport() {
        this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().getName());
        this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().getId());
        this.setDetail("Operating System", OPERATING_SYSTEM);
        this.setDetail("Java Version", JAVA_VERSION);
        this.setDetail("Java VM Version", JAVA_VM_VERSION);
        this.setDetail("Memory", (Supplier<String>)((Supplier)() -> {
            Runtime $$0 = Runtime.getRuntime();
            long $$1 = $$0.maxMemory();
            long $$2 = $$0.totalMemory();
            long $$3 = $$0.freeMemory();
            long $$4 = $$1 / 0x100000L;
            long $$5 = $$2 / 0x100000L;
            long $$6 = $$3 / 0x100000L;
            return $$3 + " bytes (" + $$6 + " MiB) / " + $$2 + " bytes (" + $$5 + " MiB) up to " + $$1 + " bytes (" + $$4 + " MiB)";
        }));
        this.setDetail("CPUs", (Supplier<String>)((Supplier)() -> String.valueOf((int)Runtime.getRuntime().availableProcessors())));
        this.ignoreErrors("hardware", () -> this.putHardware(new SystemInfo()));
        this.setDetail("JVM Flags", (Supplier<String>)((Supplier)() -> {
            List $$0 = (List)Util.getVmArguments().collect(Collectors.toList());
            return String.format((Locale)Locale.ROOT, (String)"%d total; %s", (Object[])new Object[]{$$0.size(), String.join((CharSequence)" ", (Iterable)$$0)});
        }));
    }

    public void setDetail(String $$0, String $$1) {
        this.entries.put((Object)$$0, (Object)$$1);
    }

    public void setDetail(String $$0, Supplier<String> $$1) {
        try {
            this.setDetail($$0, (String)$$1.get());
        }
        catch (Exception $$2) {
            LOGGER.warn("Failed to get system info for {}", (Object)$$0, (Object)$$2);
            this.setDetail($$0, "ERR");
        }
    }

    private void putHardware(SystemInfo $$0) {
        HardwareAbstractionLayer $$1 = $$0.getHardware();
        this.ignoreErrors("processor", () -> this.putProcessor($$1.getProcessor()));
        this.ignoreErrors("graphics", () -> this.putGraphics((List<GraphicsCard>)$$1.getGraphicsCards()));
        this.ignoreErrors("memory", () -> this.putMemory($$1.getMemory()));
    }

    private void ignoreErrors(String $$0, Runnable $$1) {
        try {
            $$1.run();
        }
        catch (Throwable $$2) {
            LOGGER.warn("Failed retrieving info for group {}", (Object)$$0, (Object)$$2);
        }
    }

    private void putPhysicalMemory(List<PhysicalMemory> $$0) {
        int $$1 = 0;
        for (PhysicalMemory $$2 : $$0) {
            String $$3 = String.format((Locale)Locale.ROOT, (String)"Memory slot #%d ", (Object[])new Object[]{$$1++});
            this.setDetail($$3 + "capacity (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$2.getCapacity() / 1048576.0f))})));
            this.setDetail($$3 + "clockSpeed (GHz)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$2.getClockSpeed() / 1.0E9f))})));
            this.setDetail($$3 + "type", (Supplier<String>)((Supplier)() -> ((PhysicalMemory)$$2).getMemoryType()));
        }
    }

    private void putVirtualMemory(VirtualMemory $$0) {
        this.setDetail("Virtual memory max (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$0.getVirtualMax() / 1048576.0f))})));
        this.setDetail("Virtual memory used (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$0.getVirtualInUse() / 1048576.0f))})));
        this.setDetail("Swap memory total (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$0.getSwapTotal() / 1048576.0f))})));
        this.setDetail("Swap memory used (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$0.getSwapUsed() / 1048576.0f))})));
    }

    private void putMemory(GlobalMemory $$0) {
        this.ignoreErrors("physical memory", () -> this.putPhysicalMemory((List<PhysicalMemory>)$$0.getPhysicalMemory()));
        this.ignoreErrors("virtual memory", () -> this.putVirtualMemory($$0.getVirtualMemory()));
    }

    private void putGraphics(List<GraphicsCard> $$0) {
        int $$1 = 0;
        for (GraphicsCard $$2 : $$0) {
            String $$3 = String.format((Locale)Locale.ROOT, (String)"Graphics card #%d ", (Object[])new Object[]{$$1++});
            this.setDetail($$3 + "name", (Supplier<String>)((Supplier)() -> ((GraphicsCard)$$2).getName()));
            this.setDetail($$3 + "vendor", (Supplier<String>)((Supplier)() -> ((GraphicsCard)$$2).getVendor()));
            this.setDetail($$3 + "VRAM (MB)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$2.getVRam() / 1048576.0f))})));
            this.setDetail($$3 + "deviceId", (Supplier<String>)((Supplier)() -> ((GraphicsCard)$$2).getDeviceId()));
            this.setDetail($$3 + "versionInfo", (Supplier<String>)((Supplier)() -> ((GraphicsCard)$$2).getVersionInfo()));
        }
    }

    private void putProcessor(CentralProcessor $$0) {
        CentralProcessor.ProcessorIdentifier $$1 = $$0.getProcessorIdentifier();
        this.setDetail("Processor Vendor", (Supplier<String>)((Supplier)() -> ((CentralProcessor.ProcessorIdentifier)$$1).getVendor()));
        this.setDetail("Processor Name", (Supplier<String>)((Supplier)() -> ((CentralProcessor.ProcessorIdentifier)$$1).getName()));
        this.setDetail("Identifier", (Supplier<String>)((Supplier)() -> ((CentralProcessor.ProcessorIdentifier)$$1).getIdentifier()));
        this.setDetail("Microarchitecture", (Supplier<String>)((Supplier)() -> ((CentralProcessor.ProcessorIdentifier)$$1).getMicroarchitecture()));
        this.setDetail("Frequency (GHz)", (Supplier<String>)((Supplier)() -> String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$1.getVendorFreq() / 1.0E9f))})));
        this.setDetail("Number of physical packages", (Supplier<String>)((Supplier)() -> String.valueOf((int)$$0.getPhysicalPackageCount())));
        this.setDetail("Number of physical CPUs", (Supplier<String>)((Supplier)() -> String.valueOf((int)$$0.getPhysicalProcessorCount())));
        this.setDetail("Number of logical CPUs", (Supplier<String>)((Supplier)() -> String.valueOf((int)$$0.getLogicalProcessorCount())));
    }

    public void appendToCrashReportString(StringBuilder $$0) {
        $$0.append("-- ").append("System Details").append(" --\n");
        $$0.append("Details:");
        this.entries.forEach(($$1, $$2) -> {
            $$0.append("\n\t");
            $$0.append($$1);
            $$0.append(": ");
            $$0.append($$2);
        });
    }

    public String toLineSeparatedString() {
        return (String)this.entries.entrySet().stream().map($$0 -> (String)$$0.getKey() + ": " + (String)$$0.getValue()).collect(Collectors.joining((CharSequence)System.lineSeparator()));
    }
}