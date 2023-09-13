package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.util.MathHelper;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.world.level.block.state.properties.IBlockState;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemUtils {

    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
    private static final ExecutorService BOOTSTRAP_EXECUTOR = b("Bootstrap");
    private static final ExecutorService BACKGROUND_EXECUTOR = b("Main");
    private static final ExecutorService IO_POOL = m();
    public static LongSupplier timeSource = System::nanoTime;
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = (FileSystemProvider) FileSystemProvider.installedProviders().stream().filter((filesystemprovider) -> {
        return filesystemprovider.getScheme().equalsIgnoreCase("jar");
    }).findFirst().orElseThrow(() -> {
        return new IllegalStateException("No jar file system provider found");
    });
    static final Logger LOGGER = LogManager.getLogger();

    public SystemUtils() {}

    public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> a() {
        return Collectors.toMap(Entry::getKey, Entry::getValue);
    }

    public static <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Object object) {
        return iblockstate.a((Comparable) object);
    }

    public static String a(String s, @Nullable MinecraftKey minecraftkey) {
        return minecraftkey == null ? s + ".unregistered_sadface" : s + "." + minecraftkey.getNamespace() + "." + minecraftkey.getKey().replace('/', '.');
    }

    public static long getMonotonicMillis() {
        return getMonotonicNanos() / 1000000L;
    }

    public static long getMonotonicNanos() {
        return SystemUtils.timeSource.getAsLong();
    }

    public static long getTimeMillis() {
        return Instant.now().toEpochMilli();
    }

    private static ExecutorService b(String s) {
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
        Object object;

        if (i <= 0) {
            object = MoreExecutors.newDirectExecutorService();
        } else {
            object = new ForkJoinPool(i, (forkjoinpool) -> {
                ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(forkjoinpool) {
                    protected void onTermination(Throwable throwable) {
                        if (throwable != null) {
                            SystemUtils.LOGGER.warn("{} died", this.getName(), throwable);
                        } else {
                            SystemUtils.LOGGER.debug("{} shutdown", this.getName());
                        }

                        super.onTermination(throwable);
                    }
                };

                forkjoinworkerthread.setName("Worker-" + s + "-" + SystemUtils.WORKER_COUNT.getAndIncrement());
                return forkjoinworkerthread;
            }, SystemUtils::a, true);
        }

        return (ExecutorService) object;
    }

    public static Executor e() {
        return SystemUtils.BOOTSTRAP_EXECUTOR;
    }

    public static Executor f() {
        return SystemUtils.BACKGROUND_EXECUTOR;
    }

    public static Executor g() {
        return SystemUtils.IO_POOL;
    }

    public static void h() {
        a(SystemUtils.BACKGROUND_EXECUTOR);
        a(SystemUtils.IO_POOL);
    }

    private static void a(ExecutorService executorservice) {
        executorservice.shutdown();

        boolean flag;

        try {
            flag = executorservice.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedexception) {
            flag = false;
        }

        if (!flag) {
            executorservice.shutdownNow();
        }

    }

    private static ExecutorService m() {
        return Executors.newCachedThreadPool((runnable) -> {
            Thread thread = new Thread(runnable);

            thread.setName("IO-Worker-" + SystemUtils.WORKER_COUNT.getAndIncrement());
            thread.setUncaughtExceptionHandler(SystemUtils::a);
            return thread;
        });
    }

    public static <T> CompletableFuture<T> a(Throwable throwable) {
        CompletableFuture<T> completablefuture = new CompletableFuture();

        completablefuture.completeExceptionally(throwable);
        return completablefuture;
    }

    public static void b(Throwable throwable) {
        throw throwable instanceof RuntimeException ? (RuntimeException) throwable : new RuntimeException(throwable);
    }

    private static void a(Thread thread, Throwable throwable) {
        c(throwable);
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }

        if (throwable instanceof ReportedException) {
            DispenserRegistry.a(((ReportedException) throwable).a().e());
            System.exit(-1);
        }

        SystemUtils.LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
    }

    @Nullable
    public static Type<?> a(TypeReference typereference, String s) {
        return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : b(typereference, s);
    }

    @Nullable
    private static Type<?> b(TypeReference typereference, String s) {
        Type type = null;

        try {
            type = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(typereference, s);
        } catch (IllegalArgumentException illegalargumentexception) {
            SystemUtils.LOGGER.error("No data fixer registered for {}", s);
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw illegalargumentexception;
            }
        }

        return type;
    }

    public static Runnable a(String s, Runnable runnable) {
        return SharedConstants.IS_RUNNING_IN_IDE ? () -> {
            Thread thread = Thread.currentThread();
            String s1 = thread.getName();

            thread.setName(s);

            try {
                runnable.run();
            } finally {
                thread.setName(s1);
            }

        } : runnable;
    }

    public static SystemUtils.OS i() {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        return s.contains("win") ? SystemUtils.OS.WINDOWS : (s.contains("mac") ? SystemUtils.OS.OSX : (s.contains("solaris") ? SystemUtils.OS.SOLARIS : (s.contains("sunos") ? SystemUtils.OS.SOLARIS : (s.contains("linux") ? SystemUtils.OS.LINUX : (s.contains("unix") ? SystemUtils.OS.LINUX : SystemUtils.OS.UNKNOWN)))));
    }

    public static Stream<String> j() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();

        return runtimemxbean.getInputArguments().stream().filter((s) -> {
            return s.startsWith("-X");
        });
    }

    public static <T> T a(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T a(Iterable<T> iterable, @Nullable T t0) {
        Iterator<T> iterator = iterable.iterator();
        T t1 = iterator.next();

        if (t0 != null) {
            Object object = t1;

            while (object != t0) {
                if (iterator.hasNext()) {
                    object = iterator.next();
                }
            }

            if (iterator.hasNext()) {
                return iterator.next();
            }
        }

        return t1;
    }

    public static <T> T b(Iterable<T> iterable, @Nullable T t0) {
        Iterator<T> iterator = iterable.iterator();

        Object object;
        Object object1;

        for (object1 = null; iterator.hasNext(); object1 = object) {
            object = iterator.next();
            if (object == t0) {
                if (object1 == null) {
                    object1 = iterator.hasNext() ? Iterators.getLast(iterator) : t0;
                }
                break;
            }
        }

        return object1;
    }

    public static <T> T a(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T a(T t0, Consumer<T> consumer) {
        consumer.accept(t0);
        return t0;
    }

    public static <K> Strategy<K> k() {
        return SystemUtils.IdentityHashingStrategy.INSTANCE;
    }

    public static <V> CompletableFuture<List<V>> b(List<? extends CompletableFuture<? extends V>> list) {
        return (CompletableFuture) list.stream().reduce(CompletableFuture.completedFuture(Lists.newArrayList()), (completablefuture, completablefuture1) -> {
            return completablefuture1.thenCombine(completablefuture, (object, list1) -> {
                List<V> list2 = Lists.newArrayListWithCapacity(list1.size() + 1);

                list2.addAll(list1);
                list2.add(object);
                return list2;
            });
        }, (completablefuture, completablefuture1) -> {
            return completablefuture.thenCombine(completablefuture1, (list1, list2) -> {
                List<V> list3 = Lists.newArrayListWithCapacity(list1.size() + list2.size());

                list3.addAll(list1);
                list3.addAll(list2);
                return list3;
            });
        });
    }

    public static <V> CompletableFuture<List<V>> c(List<? extends CompletableFuture<? extends V>> list) {
        List<V> list1 = Lists.newArrayListWithCapacity(list.size());
        CompletableFuture<?>[] acompletablefuture = new CompletableFuture[list.size()];
        CompletableFuture<Void> completablefuture = new CompletableFuture();

        list.forEach((completablefuture1) -> {
            int i = list1.size();

            list1.add((Object) null);
            acompletablefuture[i] = completablefuture1.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completablefuture.completeExceptionally(throwable);
                } else {
                    list1.set(i, object);
                }

            });
        });
        return CompletableFuture.allOf(acompletablefuture).applyToEither(completablefuture, (ovoid) -> {
            return list1;
        });
    }

    public static <T> Stream<T> a(Optional<? extends T> optional) {
        return (Stream) DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <T> Optional<T> a(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        } else {
            runnable.run();
        }

        return optional;
    }

    public static Runnable a(Runnable runnable, Supplier<String> supplier) {
        return runnable;
    }

    public static void a(String s) {
        SystemUtils.LOGGER.error(s);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            n();
        }

    }

    public static <T extends Throwable> T c(T t0) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            SystemUtils.LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t0);
            n();
        }

        return t0;
    }

    private static void n() {
        while (true) {
            try {
                Thread.sleep(1000L);
                SystemUtils.LOGGER.error("paused");
            } catch (InterruptedException interruptedexception) {
                return;
            }
        }
    }

    public static String d(Throwable throwable) {
        return throwable.getCause() != null ? d(throwable.getCause()) : (throwable.getMessage() != null ? throwable.getMessage() : throwable.toString());
    }

    public static <T> T a(T[] at, Random random) {
        return at[random.nextInt(at.length)];
    }

    public static int a(int[] aint, Random random) {
        return aint[random.nextInt(aint.length)];
    }

    public static <T> T a(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    private static BooleanSupplier a(final Path path, final Path path1) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                try {
                    Files.move(path, path1);
                    return true;
                } catch (IOException ioexception) {
                    SystemUtils.LOGGER.error("Failed to rename", ioexception);
                    return false;
                }
            }

            public String toString() {
                return "rename " + path + " to " + path1;
            }
        };
    }

    private static BooleanSupplier a(final Path path) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists(path);
                    return true;
                } catch (IOException ioexception) {
                    SystemUtils.LOGGER.warn("Failed to delete", ioexception);
                    return false;
                }
            }

            public String toString() {
                return "delete old " + path;
            }
        };
    }

    private static BooleanSupplier b(final Path path) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                return !Files.exists(path, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + path + " is deleted";
            }
        };
    }

    private static BooleanSupplier c(final Path path) {
        return new BooleanSupplier() {
            public boolean getAsBoolean() {
                return Files.isRegularFile(path, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + path + " is present";
            }
        };
    }

    private static boolean a(BooleanSupplier... abooleansupplier) {
        BooleanSupplier[] abooleansupplier1 = abooleansupplier;
        int i = abooleansupplier.length;

        for (int j = 0; j < i; ++j) {
            BooleanSupplier booleansupplier = abooleansupplier1[j];

            if (!booleansupplier.getAsBoolean()) {
                SystemUtils.LOGGER.warn("Failed to execute {}", booleansupplier);
                return false;
            }
        }

        return true;
    }

    private static boolean a(int i, String s, BooleanSupplier... abooleansupplier) {
        for (int j = 0; j < i; ++j) {
            if (a(abooleansupplier)) {
                return true;
            }

            SystemUtils.LOGGER.error("Failed to {}, retrying {}/{}", s, j, i);
        }

        SystemUtils.LOGGER.error("Failed to {}, aborting, progress might be lost", s);
        return false;
    }

    public static void a(File file, File file1, File file2) {
        a(file.toPath(), file1.toPath(), file2.toPath());
    }

    public static void a(Path path, Path path1, Path path2) {
        boolean flag = true;

        if (!Files.exists(path, new LinkOption[0]) || a(10, "create backup " + path2, a(path2), a(path, path2), c(path2))) {
            if (a(10, "remove old " + path, a(path), b(path))) {
                if (!a(10, "replace " + path + " with " + path1, a(path1, path), c(path))) {
                    a(10, "restore " + path + " from " + path2, a(path2, path), c(path));
                }

            }
        }
    }

    public static int a(String s, int i, int j) {
        int k = s.length();
        int l;

        if (j >= 0) {
            for (l = 0; i < k && l < j; ++l) {
                if (Character.isHighSurrogate(s.charAt(i++)) && i < k && Character.isLowSurrogate(s.charAt(i))) {
                    ++i;
                }
            }
        } else {
            for (l = j; i > 0 && l < 0; ++l) {
                --i;
                if (Character.isLowSurrogate(s.charAt(i)) && i > 0 && Character.isHighSurrogate(s.charAt(i - 1))) {
                    --i;
                }
            }
        }

        return i;
    }

    public static Consumer<String> a(String s, Consumer<String> consumer) {
        return (s1) -> {
            consumer.accept(s + s1);
        };
    }

    public static DataResult<int[]> a(IntStream intstream, int i) {
        int[] aint = intstream.limit((long) (i + 1)).toArray();

        if (aint.length != i) {
            String s = "Input is not a list of " + i + " ints";

            return aint.length >= i ? DataResult.error(s, Arrays.copyOf(aint, i)) : DataResult.error(s);
        } else {
            return DataResult.success(aint);
        }
    }

    public static <T> DataResult<List<T>> a(List<T> list, int i) {
        if (list.size() != i) {
            String s = "Input is not a list of " + i + " elements";

            return list.size() >= i ? DataResult.error(s, list.subList(0, i)) : DataResult.error(s);
        } else {
            return DataResult.success(list);
        }
    }

    public static void l() {
        Thread thread = new Thread("Timer hack thread") {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException interruptedexception) {
                        SystemUtils.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                        return;
                    }
                }
            }
        };

        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(SystemUtils.LOGGER));
        thread.start();
    }

    public static void b(Path path, Path path1, Path path2) throws IOException {
        Path path3 = path.relativize(path2);
        Path path4 = path1.resolve(path3);

        Files.copy(path2, path4);
    }

    public static String a(String s, CharPredicate charpredicate) {
        return (String) s.toLowerCase(Locale.ROOT).chars().mapToObj((i) -> {
            return charpredicate.test((char) i) ? Character.toString((char) i) : "_";
        }).collect(Collectors.joining());
    }

    public static <T, R> Function<T, R> a(final Function<T, R> function) {
        return new Function<T, R>() {
            private final Map<T, R> cache = Maps.newHashMap();

            public R apply(T t0) {
                return this.cache.computeIfAbsent(t0, function);
            }

            public String toString() {
                return "memoize/1[function=" + function + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T, U, R> BiFunction<T, U, R> a(final BiFunction<T, U, R> bifunction) {
        return new BiFunction<T, U, R>() {
            private final Map<Pair<T, U>, R> cache = Maps.newHashMap();

            public R apply(T t0, U u0) {
                return this.cache.computeIfAbsent(Pair.of(t0, u0), (pair) -> {
                    return bifunction.apply(pair.getFirst(), pair.getSecond());
                });
            }

            public String toString() {
                return "memoize/2[function=" + bifunction + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static enum OS {

        LINUX, SOLARIS, WINDOWS {
            @Override
            protected String[] b(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        },
        OSX {
            @Override
            protected String[] b(URL url) {
                return new String[]{"open", url.toString()};
            }
        },
        UNKNOWN;

        OS() {}

        public void a(URL url) {
            try {
                Process process = (Process) AccessController.doPrivileged(() -> {
                    return Runtime.getRuntime().exec(this.b(url));
                });
                Iterator iterator = IOUtils.readLines(process.getErrorStream()).iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    SystemUtils.LOGGER.error(s);
                }

                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            } catch (IOException | PrivilegedActionException privilegedactionexception) {
                SystemUtils.LOGGER.error("Couldn't open url '{}'", url, privilegedactionexception);
            }

        }

        public void a(URI uri) {
            try {
                this.a(uri.toURL());
            } catch (MalformedURLException malformedurlexception) {
                SystemUtils.LOGGER.error("Couldn't open uri '{}'", uri, malformedurlexception);
            }

        }

        public void a(File file) {
            try {
                this.a(file.toURI().toURL());
            } catch (MalformedURLException malformedurlexception) {
                SystemUtils.LOGGER.error("Couldn't open file '{}'", file, malformedurlexception);
            }

        }

        protected String[] b(URL url) {
            String s = url.toString();

            if ("file".equals(url.getProtocol())) {
                s = s.replace("file:", "file://");
            }

            return new String[]{"xdg-open", s};
        }

        public void a(String s) {
            try {
                this.a((new URI(s)).toURL());
            } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
                SystemUtils.LOGGER.error("Couldn't open uri '{}'", s, urisyntaxexception);
            }

        }
    }

    private static enum IdentityHashingStrategy implements Strategy<Object> {

        INSTANCE;

        private IdentityHashingStrategy() {}

        public int hashCode(Object object) {
            return System.identityHashCode(object);
        }

        public boolean equals(Object object, Object object1) {
            return object == object1;
        }
    }
}
