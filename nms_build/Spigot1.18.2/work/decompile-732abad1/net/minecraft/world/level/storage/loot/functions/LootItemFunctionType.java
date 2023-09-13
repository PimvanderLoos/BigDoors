package net.minecraft.world.level.storage.loot.functions;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootItemFunctionType extends LootSerializerType<LootItemFunction> {

    public LootItemFunctionType(LootSerializer<? extends LootItemFunction> lootserializer) {
        super(lootserializer);
    }
}
