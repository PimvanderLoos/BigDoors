package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.world.level.storage.loot.LootItemUser;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public interface NumberProvider extends LootItemUser {

    float b(LootTableInfo loottableinfo);

    default int a(LootTableInfo loottableinfo) {
        return Math.round(this.b(loottableinfo));
    }

    LootNumberProviderType a();
}
