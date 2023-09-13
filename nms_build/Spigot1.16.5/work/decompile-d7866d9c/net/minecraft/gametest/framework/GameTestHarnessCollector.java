package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class GameTestHarnessCollector {

    private final Collection<GameTestHarnessInfo> a = Lists.newArrayList();
    @Nullable
    private Collection<GameTestHarnessListener> b = Lists.newArrayList();

    public GameTestHarnessCollector() {}

    public GameTestHarnessCollector(Collection<GameTestHarnessInfo> collection) {
        this.a.addAll(collection);
    }

    public void a(GameTestHarnessInfo gametestharnessinfo) {
        this.a.add(gametestharnessinfo);
        this.b.forEach(gametestharnessinfo::a);
    }

    public void a(GameTestHarnessListener gametestharnesslistener) {
        this.b.add(gametestharnesslistener);
        this.a.forEach((gametestharnessinfo) -> {
            gametestharnessinfo.a(gametestharnesslistener);
        });
    }

    public void a(final Consumer<GameTestHarnessInfo> consumer) {
        this.a(new GameTestHarnessListener() {
            @Override
            public void a(GameTestHarnessInfo gametestharnessinfo) {}

            @Override
            public void c(GameTestHarnessInfo gametestharnessinfo) {
                consumer.accept(gametestharnessinfo);
            }
        });
    }

    public int a() {
        return (int) this.a.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::q).count();
    }

    public int b() {
        return (int) this.a.stream().filter(GameTestHarnessInfo::i).filter(GameTestHarnessInfo::r).count();
    }

    public int c() {
        return (int) this.a.stream().filter(GameTestHarnessInfo::k).count();
    }

    public boolean d() {
        return this.a() > 0;
    }

    public boolean e() {
        return this.b() > 0;
    }

    public int h() {
        return this.a.size();
    }

    public boolean i() {
        return this.c() == this.h();
    }

    public String j() {
        StringBuffer stringbuffer = new StringBuffer();

        stringbuffer.append('[');
        this.a.forEach((gametestharnessinfo) -> {
            if (!gametestharnessinfo.j()) {
                stringbuffer.append(' ');
            } else if (gametestharnessinfo.h()) {
                stringbuffer.append('+');
            } else if (gametestharnessinfo.i()) {
                stringbuffer.append((char) (gametestharnessinfo.q() ? 'X' : 'x'));
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
