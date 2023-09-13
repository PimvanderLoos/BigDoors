package net.minecraft.world.level.storage.loot.providers.score;

import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootSerializerType;

public class LootScoreProviderType extends LootSerializerType<ScoreboardNameProvider> {

    public LootScoreProviderType(LootSerializer<? extends ScoreboardNameProvider> lootserializer) {
        super(lootserializer);
    }
}
