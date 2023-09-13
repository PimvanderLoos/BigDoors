package net.minecraft.world.level.storage.loot.predicates;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootItemConditionType extends LootSerializerType<LootItemCondition> {

    public LootItemConditionType(LootSerializer<? extends LootItemCondition> lootserializer) {
        super(lootserializer);
    }
}
