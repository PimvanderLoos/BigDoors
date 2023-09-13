package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public interface LootItemUser {

    default Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    default void validate(LootCollector lootcollector) {
        lootcollector.validateUser(this);
    }
}
