package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootTableInfo;

@FunctionalInterface
interface LootEntryChildren {

    LootEntryChildren ALWAYS_FALSE = (loottableinfo, consumer) -> {
        return false;
    };
    LootEntryChildren ALWAYS_TRUE = (loottableinfo, consumer) -> {
        return true;
    };

    boolean expand(LootTableInfo loottableinfo, Consumer<LootEntry> consumer);

    default LootEntryChildren and(LootEntryChildren lootentrychildren) {
        Objects.requireNonNull(lootentrychildren);
        return (loottableinfo, consumer) -> {
            return this.expand(loottableinfo, consumer) && lootentrychildren.expand(loottableinfo, consumer);
        };
    }

    default LootEntryChildren or(LootEntryChildren lootentrychildren) {
        Objects.requireNonNull(lootentrychildren);
        return (loottableinfo, consumer) -> {
            return this.expand(loottableinfo, consumer) || lootentrychildren.expand(loottableinfo, consumer);
        };
    }
}
