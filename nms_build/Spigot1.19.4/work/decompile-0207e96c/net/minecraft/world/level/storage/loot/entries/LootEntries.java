package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class LootEntries {

    public static final LootEntryType EMPTY = register("empty", new LootSelectorEmpty.a());
    public static final LootEntryType ITEM = register("item", new LootItem.a());
    public static final LootEntryType REFERENCE = register("loot_table", new LootSelectorLootTable.a());
    public static final LootEntryType DYNAMIC = register("dynamic", new LootSelectorDynamic.a());
    public static final LootEntryType TAG = register("tag", new LootSelectorTag.a());
    public static final LootEntryType ALTERNATIVES = register("alternatives", LootEntryChildrenAbstract.createSerializer(LootEntryAlternatives::new));
    public static final LootEntryType SEQUENCE = register("sequence", LootEntryChildrenAbstract.createSerializer(LootEntrySequence::new));
    public static final LootEntryType GROUP = register("group", LootEntryChildrenAbstract.createSerializer(LootEntryGroup::new));

    public LootEntries() {}

    private static LootEntryType register(String s, LootSerializer<? extends LootEntryAbstract> lootserializer) {
        return (LootEntryType) IRegistry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, new MinecraftKey(s), new LootEntryType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootEntryAbstract::getType).build();
    }
}
