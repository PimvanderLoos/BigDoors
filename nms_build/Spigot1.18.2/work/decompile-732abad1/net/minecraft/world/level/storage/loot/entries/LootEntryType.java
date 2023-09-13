package net.minecraft.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootEntryType extends LootSerializerType<LootEntryAbstract> {

    public LootEntryType(LootSerializer<? extends LootEntryAbstract> lootserializer) {
        super(lootserializer);
    }
}
