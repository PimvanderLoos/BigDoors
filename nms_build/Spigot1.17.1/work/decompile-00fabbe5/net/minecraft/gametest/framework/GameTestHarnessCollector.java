package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class GameTestHarnessCollector {

    private static final char NOT_STARTED_TEST_CHAR = ' ';
    private static final char ONGOING_TEST_CHAR = '_';
    private static final char SUCCESSFUL_TEST_CHAR = '+';
    private static final char FAILED_OPTIONAL_TEST_CHAR = 'x';
    private static final char FAILED_REQUIRED_TEST_CHAR = 'X';
    private final Collection<GameTestHarnessInfo> tests = Lists.newArrayList();
    @Nullable
    private final Collection<GameTestHarnessListener> listeners = Lists.newArrayList();

    public GameTestHarnessCollector() {}

    public GameTestHarnessCollector(Collection<GameTestHarnessInfo> collection) {
        this.tests.addAll(collection);
    }

    public void a(GameTestHarnessInfo gametestharnessinfo) {
        this.tests.add(gametestharnessinfo);
        Collection collection = this.listeners;

        Objects.requireNonNull(gametestharnessinfo);
        collection.forEach(gametestharnessinfo::a);
    }

    public void a(GameTestHarnessListener gametestharnesslistener) {
        this.listeners.add(gametestharnesslistener);
        this.tests.forEach((gametestharnessinfo) -> {
            gametestharnessinfo.a(gametestharnesslistener);
        });
    }

    public void a(final Consumer<GameTestHarnessInfo> consumer) {
        this.a(new GameTestHarnessListener() {
            @Override
            public void a(GameTestHarnessInfo gametestharnessinfo) {}

            @Override
            public void b(GameTestHarnessInfo gametestharnessinfo) {}

            @Override
            public void c(GameTestHarnessInfo gametestharnessinfo) {
                consumer.accept(gametestharnessinfo);
            }
        });
    }

    public int a() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::r).count();
    }

    public int b() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::s).count();
    }

    public int c() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::k).count();
    }

    public boolean d() {
        return this.a() > 0;
    }

    public boolean e() {
        return this.b() > 0;
    }

    public Collection<GameTestHarnessInfo> f() {
        return (Collection) this.tests.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::r).collect(Collectors.toList());
    }

    public Collection<GameTestHarnessInfo> g() {
        return (Collection) this.tests.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::s).collect(Collectors.toList());
    }

    public int h() {
        return this.tests.size();
    }

    public boolean i() {
        return this.c() == this.h();
    }

    public String j() {
        StringBuffer stringbuffer = new StringBuffer();

        stringbuffer.append('[');
        this.tests.forEach((gametestharnessinfo) -> {
            if (!gametestharnessinfo.j()) {
                stringbuffer.append(' ');
            } else if (gametestharnessinfo.h()) {
                stringbuffer.append('+');
            } else if (gametestharnessinfo.i()) {
                stringbuffer.append((char) (gametestharnessinfo.r() ? 'X' : 'x'));
            } else {
                stringbuffer.append('_');
            }

        });
        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public String toString() {
        return this.j();
    }
}
