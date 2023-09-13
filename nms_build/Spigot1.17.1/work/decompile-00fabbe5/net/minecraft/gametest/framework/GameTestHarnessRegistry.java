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
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.EnumBlockRotation;

public class GameTestHarnessRegistry {

    private static final Collection<GameTestHarnessTestFunction> TEST_FUNCTIONS = Lists.newArrayList();
    private static final Set<String> TEST_CLASS_NAMES = Sets.newHashSet();
    private static final Map<String, Consumer<WorldServer>> BEFORE_BATCH_FUNCTIONS = Maps.newHashMap();
    private static final Map<String, Consumer<WorldServer>> AFTER_BATCH_FUNCTIONS = Maps.newHashMap();
    private static final Collection<GameTestHarnessTestFunction> LAST_FAILED_TESTS = Sets.newHashSet();

    public GameTestHarnessRegistry() {}

    public static void a(Class<?> oclass) {
        Arrays.stream(oclass.getDeclaredMethods()).forEach(GameTestHarnessRegistry::a);
    }

    public static void a(Method method) {
        String s = method.getDeclaringClass().getSimpleName();
        GameTest gametest = (GameTest) method.getAnnotation(GameTest.class);

        if (gametest != null) {
            GameTestHarnessRegistry.TEST_FUNCTIONS.add(c(method));
            GameTestHarnessRegistry.TEST_CLASS_NAMES.add(s);
        }

        GameTestGenerator gametestgenerator = (GameTestGenerator) method.getAnnotation(GameTestGenerator.class);

        if (gametestgenerator != null) {
            GameTestHarnessRegistry.TEST_FUNCTIONS.addAll(b(method));
            GameTestHarnessRegistry.TEST_CLASS_NAMES.add(s);
        }

        a(method, BeforeBatch.class, BeforeBatch::a, GameTestHarnessRegistry.BEFORE_BATCH_FUNCTIONS);
        a(method, AfterBatch.class, AfterBatch::a, GameTestHarnessRegistry.AFTER_BATCH_FUNCTIONS);
    }

    private static <T extends Annotation> void a(Method method, Class<T> oclass, Function<T, String> function, Map<String, Consumer<WorldServer>> map) {
        T t0 = method.getAnnotation(oclass);

        if (t0 != null) {
            String s = (String) function.apply(t0);
            Consumer<WorldServer> consumer = (Consumer) map.putIfAbsent(s, d(method));

            if (consumer != null) {
                throw new RuntimeException("Hey, there should only be one " + oclass + " method per batch. Batch '" + s + "' has more than one!");
            }
        }

    }

    public static Collection<GameTestHarnessTestFunction> a(String s) {
        return (Collection) GameTestHarnessRegistry.TEST_FUNCTIONS.stream().filter((gametestharnesstestfunction) -> {
            return a(gametestharnesstestfunction, s);
        }).collect(Collectors.toList());
    }

    public static Collection<GameTestHarnessTestFunction> a() {
        return GameTestHarnessRegistry.TEST_FUNCTIONS;
    }

    public static Collection<String> b() {
        return GameTestHarnessRegistry.TEST_CLASS_NAMES;
    }

    public static boolean b(String s) {
        return GameTestHarnessRegistry.TEST_CLASS_NAMES.contains(s);
    }

    @Nullable
    public static Consumer<WorldServer> c(String s) {
        return (Consumer) GameTestHarnessRegistry.BEFORE_BATCH_FUNCTIONS.get(s);
    }

    @Nullable
    public static Consumer<WorldServer> d(String s) {
        return (Consumer) GameTestHarnessRegistry.AFTER_BATCH_FUNCTIONS.get(s);
    }

    public static Optional<GameTestHarnessTestFunction> e(String s) {
        return a().stream().filter((gametestharnesstestfunction) -> {
            return gametestharnesstestfunction.a().equalsIgnoreCase(s);
        }).findFirst();
    }

    public static GameTestHarnessTestFunction f(String s) {
        Optional<GameTestHarnessTestFunction> optional = e(s);

        if (!optional.isPresent()) {
            throw new IllegalArgumentException("Can't find the test function for " + s);
        } else {
            return (GameTestHarnessTestFunction) optional.get();
        }
    }

    private static Collection<GameTestHarnessTestFunction> b(Method method) {
        try {
            Object object = method.getDeclaringClass().newInstance();

            return (Collection) method.invoke(object);
        } catch (ReflectiveOperationException reflectiveoperationexception) {
            throw new RuntimeException(reflectiveoperationexception);
        }
    }

    private static GameTestHarnessTestFunction c(Method method) {
        GameTest gametest = (GameTest) method.getAnnotation(GameTest.class);
        String s = method.getDeclaringClass().getSimpleName();
        String s1 = s.toLowerCase();
        String s2 = s1 + "." + method.getName().toLowerCase();
        String s3 = gametest.e().isEmpty() ? s2 : s1 + "." + gametest.e();
        String s4 = gametest.b();
        EnumBlockRotation enumblockrotation = GameTestHarnessStructures.a(gametest.c());

        return new GameTestHarnessTestFunction(s4, s2, s3, enumblockrotation, gametest.a(), gametest.f(), gametest.d(), gametest.h(), gametest.g(), d(method));
    }

    private static Consumer<?> d(Method method) {
        return (object) -> {
            try {
                Object object1 = method.getDeclaringClass().newInstance();

                method.invoke(object1, object);
            } catch (InvocationTargetException invocationtargetexception) {
                if (invocationtargetexception.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) invocationtargetexception.getCause();
                } else {
                    throw new RuntimeException(invocationtargetexception.getCause());
                }
            } catch (ReflectiveOperationException reflectiveoperationexception) {
                throw new RuntimeException(reflectiveoperationexception);
            }
        };
    }

    private static boolean a(GameTestHarnessTestFunction gametestharnesstestfunction, String s) {
        return gametestharnesstestfunction.a().toLowerCase().startsWith(s.toLowerCase() + ".");
    }

    public static Collection<GameTestHarnessTestFunction> c() {
        return GameTestHarnessRegistry.LAST_FAILED_TESTS;
    }

    public static void a(GameTestHarnessTestFunction gametestharnesstestfunction) {
        GameTestHarnessRegistry.LAST_FAILED_TESTS.add(gametestharnesstestfunction);
    }

    public static void d() {
        GameTestHarnessRegistry.LAST_FAILED_TESTS.clear();
    }
}
