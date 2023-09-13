package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

public class SystemReport {

    public static final long BYTES_PER_MEBIBYTE = 1048576L;
    private static final long ONE_GIGA = 1000000000L;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String OPERATING_SYSTEM;
    private static final String JAVA_VERSION;
    private static final String JAVA_VM_VERSION;
    private final Map<String, String> entries = Maps.newLinkedHashMap();

    public SystemReport() {
        this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().getName());
        this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().getId());
        this.setDetail("Operating System", SystemReport.OPERATING_SYSTEM);
        this.setDetail("Java Version", SystemReport.JAVA_VERSION);
        this.setDetail("Java VM Version", SystemReport.JAVA_VM_VERSION);
        this.setDetail("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long i = runtime.maxMemory();
            long j = runtime.totalMemory();
            long k = runtime.freeMemory();
            long l = i / 1048576L;
            long i1 = j / 1048576L;
            long j1 = k / 1048576L;

            return k + " bytes (" + j1 + " MiB) / " + j + " bytes (" + i1 + " MiB) up to " + i + " bytes (" + l + " MiB)";
        });
        this.setDetail("CPUs", () -> {
            return String.valueOf(Runtime.getRuntime().availableProcessors());
        });
        this.ignoreErrors("hardware", () -> {
            this.putHardware(new SystemInfo());
        });
        this.setDetail("JVM Flags", () -> {
            List<String> list = (List) SystemUtils.getVmArguments().collect(Collectors.toList());

            return String.format(Locale.ROOT, "%d total; %s", list.size(), String.join(" ", list));
        });
    }

    public void setDetail(String s, String s1) {
        this.entries.put(s, s1);
    }

    public void setDetail(String s, Supplier<String> supplier) {
        try {
            this.setDetail(s, (String) supplier.get());
        } catch (Exception exception) {
            SystemReport.LOGGER.warn("Failed to get system info for {}", s, exception);
            this.setDetail(s, "ERR");
        }

    }

    private void putHardware(SystemInfo systeminfo) {
        HardwareAbstractionLayer hardwareabstractionlayer = systeminfo.getHardware();

        this.ignoreErrors("processor", () -> {
            this.putProcessor(hardwareabstractionlayer.getProcessor());
        });
        this.ignoreErrors("graphics", () -> {
            this.putGraphics(hardwareabstractionlayer.getGraphicsCards());
        });
        this.ignoreErrors("memory", () -> {
            this.putMemory(hardwareabstractionlayer.getMemory());
        });
    }

    private void ignoreErrors(String s, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            SystemReport.LOGGER.warn("Failed retrieving info for group {}", s, throwable);
        }

    }

    private void putPhysicalMemory(List<PhysicalMemory> list) {
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            PhysicalMemory physicalmemory = (PhysicalMemory) iterator.next();
            String s = String.format(Locale.ROOT, "Memory slot #%d ", i++);

            this.setDetail(s + "capacity (MB)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float) physicalmemory.getCapacity() / 1048576.0F);
            });
            this.setDetail(s + "clockSpeed (GHz)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float) physicalmemory.getClockSpeed() / 1.0E9F);
            });
            String s1 = s + "type";

            Objects.requireNonNull(physicalmemory);
            this.setDetail(s1, physicalmemory::getMemoryType);
        }

    }

    private void putVirtualMemory(VirtualMemory virtualmemory) {
        this.setDetail("Virtual memory max (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float) virtualmemory.getVirtualMax() / 1048576.0F);
        });
        this.setDetail("Virtual memory used (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float) virtualmemory.getVirtualInUse() / 1048576.0F);
        });
        this.setDetail("Swap memory total (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float) virtualmemory.getSwapTotal() / 1048576.0F);
        });
        this.setDetail("Swap memory used (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float) virtualmemory.getSwapUsed() / 1048576.0F);
        });
    }

    private void putMemory(GlobalMemory globalmemory) {
        this.ignoreErrors("physical memory", () -> {
            this.putPhysicalMemory(globalmemory.getPhysicalMemory());
        });
        this.ignoreErrors("virtual memory", () -> {
            this.putVirtualMemory(globalmemory.getVirtualMemory());
        });
    }

    private void putGraphics(List<GraphicsCard> list) {
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            GraphicsCard graphicscard = (GraphicsCard) iterator.next();
            String s = String.format(Locale.ROOT, "Graphics card #%d ", i++);
            String s1 = s + "name";

            Objects.requireNonNull(graphicscard);
            this.setDetail(s1, graphicscard::getName);
            s1 = s + "vendor";
            Objects.requireNonNull(graphicscard);
            this.setDetail(s1, graphicscard::getVendor);
            this.setDetail(s + "VRAM (MB)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float) graphicscard.getVRam() / 1048576.0F);
            });
            s1 = s + "deviceId";
            Objects.requireNonNull(graphicscard);
            this.setDetail(s1, graphicscard::getDeviceId);
            s1 = s + "versionInfo";
            Objects.requireNonNull(graphicscard);
            this.setDetail(s1, graphicscard::getVersionInfo);
        }

    }

    private void putProcessor(CentralProcessor centralprocessor) {
        ProcessorIdentifier processoridentifier = centralprocessor.getProcessorIdentifier();

        Objects.requireNonNull(processoridentifier);
        this.setDetail("Processor Vendor", processoridentifier::getVendor);
        Objects.requireNonNull(processoridentifier);
        this.setDetail("Processor Name", processoridentifier::getName);
        Objects.requireNonNull(processoridentifier);
        this.setDetail("Identifier", processoridentifier::getIdentifier);
        Objects.requireNonNull(processoridentifier);
        this.setDetail("Microarchitecture", processoridentifier::getMicroarchitecture);
        this.setDetail("Frequency (GHz)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float) processoridentifier.getVendorFreq() / 1.0E9F);
        });
        this.setDetail("Number of physical packages", () -> {
            return String.valueOf(centralprocessor.getPhysicalPackageCount());
        });
        this.setDetail("Number of physical CPUs", () -> {
            return String.valueOf(centralprocessor.getPhysicalProcessorCount());
        });
        this.setDetail("Number of logical CPUs", () -> {
            return String.valueOf(centralprocessor.getLogicalProcessorCount());
        });
    }

    public void appendToCrashReportString(StringBuilder stringbuilder) {
        stringbuilder.append("-- ").append("System Details").append(" --\n");
        stringbuilder.append("Details:");
        this.entries.forEach((s, s1) -> {
            stringbuilder.append("\n\t");
            stringbuilder.append(s);
            stringbuilder.append(": ");
            stringbuilder.append(s1);
        });
    }

    public String toLineSeparatedString() {
        return (String) this.entries.entrySet().stream().map((entry) -> {
            String s = (String) entry.getKey();

            return s + ": " + (String) entry.getValue();
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        String s = System.getProperty("os.name");

        OPERATING_SYSTEM = s + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
        s = System.getProperty("java.version");
        JAVA_VERSION = s + ", " + System.getProperty("java.vendor");
        s = System.getProperty("java.vm.name");
        JAVA_VM_VERSION = s + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
    }
}
