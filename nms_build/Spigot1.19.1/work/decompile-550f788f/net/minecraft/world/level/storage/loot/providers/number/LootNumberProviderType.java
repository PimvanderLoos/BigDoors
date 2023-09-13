package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootNumberProviderType extends LootSerializerType<NumberProvider> {

    public LootNumberProviderType(LootSerializer<? extends NumberProvider> lootserializer) {
        super(lootserializer);
    }
}
