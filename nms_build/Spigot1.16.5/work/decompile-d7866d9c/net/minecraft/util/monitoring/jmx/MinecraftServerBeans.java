package net.minecraft.util.monitoring.jmx;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MinecraftServerBeans implements DynamicMBean {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer b;
    private final MBeanInfo c;
    private final Map<String, MinecraftServerBeans.a> d;

    private MinecraftServerBeans(MinecraftServer minecraftserver) {
        this.d = (Map) Stream.of(new MinecraftServerBeans.a("tickTimes", this::b, "Historical tick times (ms)", long[].class), new MinecraftServerBeans.a("averageTickTime", this::a, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap((minecraftserverbeans_a) -> {
            return minecraftserverbeans_a.a;
        }, Function.identity()));
        this.b = minecraftserver;
        MBeanAttributeInfo[] ambeanattributeinfo = (MBeanAttributeInfo[]) this.d.values().stream().map((object) -> {
            return ((MinecraftServerBeans.a) object).a();
        }).toArray((i) -> {
            return new MBeanAttributeInfo[i];
        });

        this.c = new MBeanInfo(MinecraftServerBeans.class.getSimpleName(), "metrics for dedicated server", ambeanattributeinfo, (MBeanConstructorInfo[]) null, (MBeanOperationInfo[]) null, new MBeanNotificationInfo[0]);
    }

    public static void a(MinecraftServer minecraftserver) {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new MinecraftServerBeans(minecraftserver), new ObjectName("net.minecraft.server:type=Server"));
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException malformedobjectnameexception) {
            MinecraftServerBeans.LOGGER.warn("Failed to initialise server as JMX bean", malformedobjectnameexception);
        }

    }

    private float a() {
        return this.b.aO();
    }

    private long[] b() {
        return this.b.h;
    }

    @Nullable
    public Object getAttribute(String s) {
        MinecraftServerBeans.a minecraftserverbeans_a = (MinecraftServerBeans.a) this.d.get(s);

        return minecraftserverbeans_a == null ? null : minecraftserverbeans_a.b.get();
    }

    public void setAttribute(Attribute attribute) {}

    public AttributeList getAttributes(String[] astring) {
        Stream stream = Arrays.stream(astring);
        Map map = this.d;

        this.d.getClass();
        List<Attribute> list = (List) stream.map(map::get).filter(Objects::nonNull).map((minecraftserverbeans_a) -> {
            return new Attribute(minecraftserverbeans_a.a, minecraftserverbeans_a.b.get());
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
        return this.c;
    }

    static final class a {

        private final String a;
        private final Supplier<Object> b;
        private final String c;
        private final Class<?> d;

        private a(String s, Supplier<Object> supplier, String s1, Class<?> oclass) {
            this.a = s;
            this.b = supplier;
            this.c = s1;
            this.d = oclass;
        }

        private MBeanAttributeInfo a() {
            return new MBeanAttributeInfo(this.a, this.d.getSimpleName(), this.c, true, false, false);
        }
    }
}
