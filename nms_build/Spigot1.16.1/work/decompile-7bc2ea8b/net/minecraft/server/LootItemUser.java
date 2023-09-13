package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface LootItemUser {

    default Set<LootContextParameter<?>> a() {
        return ImmutableSet.of();
    }

    default void a(LootCollector lootcollector) {
        lootcollector.a(this);
    }
}
