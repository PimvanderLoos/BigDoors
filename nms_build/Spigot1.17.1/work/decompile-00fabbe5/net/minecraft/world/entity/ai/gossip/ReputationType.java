package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum ReputationType {

    MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10), MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20), MINOR_POSITIVE("minor_positive", 1, 200, 1, 5), MAJOR_POSITIVE("major_positive", 5, 100, 0, 100), TRADING("trading", 1, 25, 2, 20);

    public static final int REPUTATION_CHANGE_PER_EVENT = 25;
    public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
    public static final int REPUTATION_CHANGE_PER_TRADE = 2;
    public final String id;
    public final int weight;
    public final int max;
    public final int decayPerDay;
    public final int decayPerTransfer;
    private static final Map<String, ReputationType> BY_ID = (Map) Stream.of(values()).collect(ImmutableMap.toImmutableMap((reputationtype) -> {
        return reputationtype.id;
    }, Function.identity()));

    private ReputationType(String s, int i, int j, int k, int l) {
        this.id = s;
        this.weight = i;
        this.max = j;
        this.decayPerDay = k;
        this.decayPerTransfer = l;
    }

    @Nullable
    public static ReputationType a(String s) {
        return (ReputationType) ReputationType.BY_ID.get(s);
    }
}
