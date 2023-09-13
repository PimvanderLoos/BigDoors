package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String OPERATING_SYSTEM;
    private static final String JAVA_VERSION;
    private static final String JAVA_VM_VERSION;
    private final Map<String, String> entries = Maps.newLinkedHashMap();

    public SystemReport() {
        this.a("Minecraft Version", SharedConstants.getGameVersion().getName());
        this.a("Minecraft Version ID", SharedConstants.getGameVersion().getId());
        this.a("Operating System", SystemReport.OPERATING_SYSTEM);
        this.a("Java Version", SystemReport.JAVA_VERSION);
        this.a("Java VM Version", SystemReport.JAVA_VM_VERSION);
        this.a("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long i = runtime.maxMemory();
            long j = runtime.totalMemory();
            long k = runtime.freeMemory();
            long l = i / 1048576L;
            long i1 = j / 1048576L;
            long j1 = k / 1048576L;

            return k + " bytes (" + j1 + " MiB) / " + j + " bytes (" + i1 + " MiB) up to " + i + " bytes (" + l + " MiB)";
        });
        this.a("CPUs", () -> {
            return String.valueOf(Runtime.getRuntime().availableProcessors());
        });
        this.a("hardware", () -> {
            this.a(new SystemInfo());
        });
        this.a("JVM Flags", () -> {
            List<String> list = (List) SystemUtils.j().collect(Collectors.toList());

            return String.format("%d total; %s", list.size(), String.join(" ", list));
        });
    }

    public void a(String s, String s1) {
        this.entries.put(s, s1);
    }

    public void a(String s, Supplier<String> supplier) {
        try {
            this.a(s, (String) supplier.get());
        } catch (Exception exception) {
            SystemReport.LOGGER.warn("Failed to get system info for {}", s, exception);
            this.a(s, "ERR");
        }

    }

    private void a(SystemInfo systeminfo) {
        HardwareAbstractionLayer hardwareabstractionlayer = systeminfo.getHardware();

        this.a("processor", () -> {
            this.a(hardwareabstractionlayer.getProcessor());
        });
        this.a("graphics", () -> {
            this.b(hardwareabstractionlayer.getGraphicsCards());
        });
        this.a("memory", () -> {
            this.a(hardwareabstractionlayer.getMemory());
        });
    }

    private void a(String s, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            SystemReport.LOGGER.warn("Failed retrieving info for group {}", s, throwable);
        }

    }

    private void a(List<PhysicalMemory> list) {
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            PhysicalMemory physicalmemory = (PhysicalMemory) iterator.next();
            String s = String.format("Memory slot #%d ", i++);

            this.a(s + "capacity (MB)", () -> {
                return String.format("%.2f", (float) physicalmemory.getCapacity() / 1048576.0F);
            });
            this.a(s + "clockSpeed (GHz)", () -> {
                return String.format("%.2f", (float) physicalmemory.getClockSpeed() / 1.0E9F);
            });
            String s1 = s + "type";

            Objects.requireNonNull(physicalmemory);
            this.a(s1, physicalmemory::getMemoryType);
        }

    }

    private void a(VirtualMemory virtualmemory) {
        this.a("Virtual memory max (MB)", () -> {
            return String.format("%.2f", (float) virtualmemory.getVirtualMax() / 1048576.0F);
        });
        this.a("Virtual memory used (MB)", () -> {
            return String.format("%.2f", (float) virtualmemory.getVirtualInUse() / 1048576.0F);
        });
        this.a("Swap memory total (MB)", () -> {
            return String.format("%.2f", (float) virtualmemory.getSwapTotal() / 1048576.0F);
        });
        this.a("Swap memory used (MB)", () -> {
            return String.format("%.2f", (float) virtualmemory.getSwapUsed() / 1048576.0F);
        });
    }

    private void a(GlobalMemory globalmemory) {
        this.a("physical memory", () -> {
            this.a(globalmemory.getPhysicalMemory());
        });
        this.a("virtual memory", () -> {
            this.a(globalmemory.getVirtualMemory());
        });
    }

    private void b(List<GraphicsCard> list) {
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            GraphicsCard graphicscard = (GraphicsCard) iterator.next();
            String s = String.format("Graphics card #%d ", i++);
            String s1 = s + "name";

            Objects.requireNonNull(graphicscard);
            this.a(s1, graphicscard::getName);
            s1 = s + "vendor";
            Objects.requireNonNull(graphicscard);
            this.a(s1, graphicscard::getVendor);
            this.a(s + "VRAM (MB)", () -> {
                return String.format("%.2f", (float) graphicscard.getVRam() / 1048576.0F);
            });
            s1 = s + "deviceId";
            Objects.requireNonNull(graphicscard);
            this.a(s1, graphicscard::getDeviceId);
            s1 = s + "versionInfo";
            Objects.requireNonNull(graphicscard);
            this.a(s1, graphicscard::getVersionInfo);
        }

    }

    private void a(CentralProcessor centralprocessor) {
        ProcessorIdentifier processoridentifier = centralprocessor.getProcessorIdentifier();

        Objects.requireNonNull(processoridentifier);
        this.a("Processor Vendor", processoridentifier::getVendor);
        Objects.requireNonNull(processoridentifier);
        this.a("Processor Name", processoridentifier::getName);
        Objects.requireNonNull(processoridentifier);
        this.a("Identifier", processoridentifier::getIdentifier);
        Objects.requireNonNull(processoridentifier);
        this.a("Microarchitecture", processoridentifier::getMicroarchitecture);
        this.a("Frequency (GHz)", () -> {
            return String.format("%.2f", (float) processoridentifier.getVendorFreq() / 1.0E9F);
        });
        this.a("Number of physical packages", () -> {
            return String.valueOf(centralprocessor.getPhysicalPackageCount());
        });
        this.a("Number of physical CPUs", () -> {
            return String.valueOf(centralprocessor.getPhysicalProcessorCount());
        });
        this.a("Number of logical CPUs", () -> {
            return String.valueOf(centralprocessor.getLogicalProcessorCount());
        });
    }

    public void a(StringBuilder stringbuilder) {
        stringbuilder.append("-- ").append("System Details").append(" --\n");
        stringbuilder.append("Details:");
        this.entries.forEach((s, s1) -> {
            stringbuilder.append("\n\t");
            stringbuilder.append(s);
            stringbuilder.append(": ");
            stringbuilder.append(s1);
        });
    }

    public String a() {
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
