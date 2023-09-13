package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class LootEntries {

    public static final LootEntryType EMPTY = a("empty", new LootSelectorEmpty.a());
    public static final LootEntryType ITEM = a("item", new LootItem.a());
    public static final LootEntryType REFERENCE = a("loot_table", new LootSelectorLootTable.a());
    public static final LootEntryType DYNAMIC = a("dynamic", new LootSelectorDynamic.a());
    public static final LootEntryType TAG = a("tag", new LootSelectorTag.a());
    public static final LootEntryType ALTERNATIVES = a("alternatives", LootEntryChildrenAbstract.a(LootEntryAlternatives::new));
    public static final LootEntryType SEQUENCE = a("sequence", LootEntryChildrenAbstract.a(LootEntrySequence::new));
    public static final LootEntryType GROUP = a("group", LootEntryChildrenAbstract.a(LootEntryGroup::new));

    public LootEntries() {}

    private static LootEntryType a(String s, LootSerializer<? extends LootEntryAbstract> lootserializer) {
        return (LootEntryType) IRegistry.a(IRegistry.LOOT_POOL_ENTRY_TYPE, new MinecraftKey(s), (Object) (new LootEntryType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootEntryAbstract::a).a();
    }
}
