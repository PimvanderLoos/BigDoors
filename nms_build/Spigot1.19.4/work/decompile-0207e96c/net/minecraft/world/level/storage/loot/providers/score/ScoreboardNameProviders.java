package net.minecraft.world.level.storage.loot.providers.score;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class ScoreboardNameProviders {

    public static final LootScoreProviderType FIXED = register("fixed", new FixedScoreboardNameProvider.a());
    public static final LootScoreProviderType CONTEXT = register("context", new ContextScoreboardNameProvider.b());

    public ScoreboardNameProviders() {}

    private static LootScoreProviderType register(String s, LootSerializer<? extends ScoreboardNameProvider> lootserializer) {
        return (LootScoreProviderType) IRegistry.register(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, new MinecraftKey(s), new LootScoreProviderType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", ScoreboardNameProvider::getType).withInlineSerializer(ScoreboardNameProviders.CONTEXT, new ContextScoreboardNameProvider.a()).build();
    }
}
