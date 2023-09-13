package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;

public class GameTestHarnessRegistry {

    private static final Collection<GameTestHarnessTestFunction> a = Lists.newArrayList();
    private static final Set<String> b = Sets.newHashSet();
    private static final Map<String, Consumer<WorldServer>> c = Maps.newHashMap();
    private static final Collection<GameTestHarnessTestFunction> d = Sets.newHashSet();

    public static Collection<GameTestHarnessTestFunction> a(String s) {
        return (Collection) GameTestHarnessRegistry.a.stream().filter((gametestharnesstestfunction) -> {
            return a(gametestharnesstestfunction, s);
        }).collect(Collectors.toList());
    }

    public static Collection<GameTestHarnessTestFunction> a() {
        return GameTestHarnessRegistry.a;
    }

    public static Collection<String> b() {
        return GameTestHarnessRegistry.b;
    }

    public static boolean b(String s) {
        return GameTestHarnessRegistry.b.contains(s);
    }

    @Nullable
    public static Consumer<WorldServer> c(String s) {
        return (Consumer) GameTestHarnessRegistry.c.get(s);
    }

    public static Optional<GameTestHarnessTestFunction> d(String s) {
        return a().stream().filter((gametestharnesstestfunction) -> {
            return gametestharnesstestfunction.a().equalsIgnoreCase(s);
        }).findFirst();
    }

    public static GameTestHarnessTestFunction e(String s) {
        Optional<GameTestHarnessTestFunction> optional = d(s);

        if (!optional.isPresent()) {
            throw new IllegalArgumentException("Can't find the test function for " + s);
        } else {
            return (GameTestHarnessTestFunction) optional.get();
        }
    }

    private static boolean a(GameTestHarnessTestFunction gametestharnesstestfunction, String s) {
        return gametestharnesstestfunction.a().toLowerCase().startsWith(s.toLowerCase() + ".");
    }

    public static Collection<GameTestHarnessTestFunction> c() {
        return GameTestHarnessRegistry.d;
    }

    public static void a(GameTestHarnessTestFunction gametestharnesstestfunction) {
        GameTestHarnessRegistry.d.add(gametestharnesstestfunction);
    }

    public static void d() {
        GameTestHarnessRegistry.d.clear();
    }
}
