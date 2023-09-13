package net.minecraft.world.level.storage.loot.providers.nbt;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootNbtProviderType extends LootSerializerType<NbtProvider> {

    public LootNbtProviderType(LootSerializer<? extends NbtProvider> lootserializer) {
        super(lootserializer);
    }
}
