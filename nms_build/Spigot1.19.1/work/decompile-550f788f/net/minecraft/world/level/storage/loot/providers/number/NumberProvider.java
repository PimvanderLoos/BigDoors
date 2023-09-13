package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.world.level.storage.loot.LootItemUser;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public interface NumberProvider extends LootItemUser {

    float getFloat(LootTableInfo loottableinfo);

    default int getInt(LootTableInfo loottableinfo) {
        return Math.round(this.getFloat(loottableinfo));
    }

    LootNumberProviderType getType();
}
