package net.minecraft.world.entity.ai.gossip;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;

public enum ReputationType implements INamable {

    MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10), MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20), MINOR_POSITIVE("minor_positive", 1, 200, 1, 5), MAJOR_POSITIVE("major_positive", 5, 100, 0, 100), TRADING("trading", 1, 25, 2, 20);

    public static final int REPUTATION_CHANGE_PER_EVENT = 25;
    public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
    public static final int REPUTATION_CHANGE_PER_TRADE = 2;
    public final String id;
    public final int weight;
    public final int max;
    public final int decayPerDay;
    public final int decayPerTransfer;
    public static final Codec<ReputationType> CODEC = INamable.fromEnum(ReputationType::values);

    private ReputationType(String s, int i, int j, int k, int l) {
        this.id = s;
        this.weight = i;
        this.max = j;
        this.decayPerDay = k;
        this.decayPerTransfer = l;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
