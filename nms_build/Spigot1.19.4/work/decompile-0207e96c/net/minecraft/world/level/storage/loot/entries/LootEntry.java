package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public interface LootEntry {

    int getWeight(float f);

    void createItemStack(Consumer<ItemStack> consumer, LootTableInfo loottableinfo);
}
