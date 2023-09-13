package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class LootEntries {

    public static final LootEntryType a = a("empty", new LootSelectorEmpty.a());
    public static final LootEntryType b = a("item", new LootItem.a());
    public static final LootEntryType c = a("loot_table", new LootSelectorLootTable.a());
    public static final LootEntryType d = a("dynamic", new LootSelectorDynamic.a());
    public static final LootEntryType e = a("tag", new LootSelectorTag.a());
    public static final LootEntryType f = a("alternatives", LootEntryChildrenAbstract.a(LootEntryAlternatives::new));
    public static final LootEntryType g = a("sequence", LootEntryChildrenAbstract.a(LootEntrySequence::new));
    public static final LootEntryType h = a("group", LootEntryChildrenAbstract.a(LootEntryGroup::new));

    private static LootEntryType a(String s, LootSerializer<? extends LootEntryAbstract> lootserializer) {
        return (LootEntryType) IRegistry.a(IRegistry.ao, new MinecraftKey(s), (Object) (new LootEntryType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.ao, "entry", "type", LootEntryAbstract::a).a();
    }
}
