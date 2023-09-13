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
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public final class MinecraftServerBeans implements DynamicMBean {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftServer server;
    private final MBeanInfo mBeanInfo;
    private final Map<String, MinecraftServerBeans.a> attributeDescriptionByName;

    private MinecraftServerBeans(MinecraftServer minecraftserver) {
        this.attributeDescriptionByName = (Map) Stream.of(new MinecraftServerBeans.a("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class), new MinecraftServerBeans.a("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap((minecraftserverbeans_a) -> {
            return minecraftserverbeans_a.name;
        }, Function.identity()));
        this.server = minecraftserver;
        MBeanAttributeInfo[] ambeanattributeinfo = (MBeanAttributeInfo[]) this.attributeDescriptionByName.values().stream().map(MinecraftServerBeans.a::asMBeanAttributeInfo).toArray((i) -> {
            return new MBeanAttributeInfo[i];
        });

        this.mBeanInfo = new MBeanInfo(MinecraftServerBeans.class.getSimpleName(), "metrics for dedicated server", ambeanattributeinfo, (MBeanConstructorInfo[]) null, (MBeanOperationInfo[]) null, new MBeanNotificationInfo[0]);
    }

    public static void registerJmxMonitoring(MinecraftServer minecraftserver) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new MinecraftServerBeans(minecraftserver), new ObjectName("net.minecraft.server:type=Server"));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException malformedobjectnameexception) {
            MinecraftServerBeans.LOGGER.warn("Failed to initialise server as JMX bean", malformedobjectnameexception);
        }

    }

    private float getAverageTickTime() {
        return this.server.getAverageTickTime();
    }

    private long[] getTickTimes() {
        return this.server.tickTimes;
    }

    @Nullable
    public Object getAttribute(String s) {
        MinecraftServerBeans.a minecraftserverbeans_a = (MinecraftServerBeans.a) this.attributeDescriptionByName.get(s);

        return minecraftserverbeans_a == null ? null : minecraftserverbeans_a.getter.get();
    }

    public void setAttribute(Attribute attribute) {}

    public AttributeList getAttributes(String[] astring) {
        Stream stream = Arrays.stream(astring);
        Map map = this.attributeDescriptionByName;

        Objects.requireNonNull(this.attributeDescriptionByName);
        List<Attribute> list = (List) stream.map(map::get).filter(Objects::nonNull).map((minecraftserverbeans_a) -> {
            return new Attribute(minecraftserverbeans_a.name, minecraftserverbeans_a.getter.get());
        }).collect(Collectors.toList());

        return new AttributeList(list);
    }

    public AttributeList setAttributes(AttributeList attributelist) {
        return new AttributeList();
    }

    @Nullable
    public Object invoke(String s, Object[] aobject, String[] astring) {
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }

    private static final class a {

        final String name;
        final Supplier<Object> getter;
        private final String description;
        private final Class<?> type;

        a(String s, Supplier<Object> supplier, String s1, Class<?> oclass) {
            this.name = s;
            this.getter = supplier;
            this.description = s1;
            this.type = oclass;
        }

        private MBeanAttributeInfo asMBeanAttributeInfo() {
            return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
        }
    }
}
