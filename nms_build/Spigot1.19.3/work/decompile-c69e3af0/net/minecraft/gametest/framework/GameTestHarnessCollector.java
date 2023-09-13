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

    public void addTestToTrack(GameTestHarnessInfo gametestharnessinfo) {
        this.tests.add(gametestharnessinfo);
        Collection collection = this.listeners;

        Objects.requireNonNull(gametestharnessinfo);
        collection.forEach(gametestharnessinfo::addListener);
    }

    public void addListener(GameTestHarnessListener gametestharnesslistener) {
        this.listeners.add(gametestharnesslistener);
        this.tests.forEach((gametestharnessinfo) -> {
            gametestharnessinfo.addListener(gametestharnesslistener);
        });
    }

    public void addFailureListener(final Consumer<GameTestHarnessInfo> consumer) {
        this.addListener(new GameTestHarnessListener() {
            @Override
            public void testStructureLoaded(GameTestHarnessInfo gametestharnessinfo) {}

            @Override
            public void testPassed(GameTestHarnessInfo gametestharnessinfo) {}

            @Override
            public void testFailed(GameTestHarnessInfo gametestharnessinfo) {
                consumer.accept(gametestharnessinfo);
            }
        });
    }

    public int getFailedRequiredCount() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::hasFailed).filter(GameTestHarnessInfo::isRequired).count();
    }

    public int getFailedOptionalCount() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::hasFailed).filter(GameTestHarnessInfo::isOptional).count();
    }

    public int getDoneCount() {
        return (int) this.tests.stream().filter(GameTestHarnessInfo::isDone).count();
    }

    public boolean hasFailedRequired() {
        return this.getFailedRequiredCount() > 0;
    }

    public boolean hasFailedOptional() {
        return this.getFailedOptionalCount() > 0;
    }

    public Collection<GameTestHarnessInfo> getFailedRequired() {
        return (Collection) this.tests.stream().filter(GameTestHarnessInfo::hasFailed).filter(GameTestHarnessInfo::isRequired).collect(Collectors.toList());
    }

    public Collection<GameTestHarnessInfo> getFailedOptional() {
        return (Collection) this.tests.stream().filter(GameTestHarnessInfo::hasFailed).filter(GameTestHarnessInfo::isOptional).collect(Collectors.toList());
    }

    public int getTotalCount() {
        return this.tests.size();
    }

    public boolean isDone() {
        return this.getDoneCount() == this.getTotalCount();
    }

    public String getProgressBar() {
        StringBuffer stringbuffer = new StringBuffer();

        stringbuffer.append('[');
        this.tests.forEach((gametestharnessinfo) -> {
            if (!gametestharnessinfo.hasStarted()) {
                stringbuffer.append(' ');
            } else if (gametestharnessinfo.hasSucceeded()) {
                stringbuffer.append('+');
            } else if (gametestharnessinfo.hasFailed()) {
                stringbuffer.append((char) (gametestharnessinfo.isRequired() ? 'X' : 'x'));
            } else {
                stringbuffer.append('_');
            }

        });
        stringbuffer.append(']');
        return stringbuffer.toString();
    }

    public String toString() {
        return this.getProgressBar();
    }
}
