package net.minecraft.world.level.storage.loot.providers.score;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class ScoreboardNameProviders {

    public static final LootScoreProviderType FIXED = a("fixed", new FixedScoreboardNameProvider.a());
    public static final LootScoreProviderType CONTEXT = a("context", new ContextScoreboardNameProvider.b());

    public ScoreboardNameProviders() {}

    private static LootScoreProviderType a(String s, LootSerializer<? extends ScoreboardNameProvider> lootserializer) {
        return (LootScoreProviderType) IRegistry.a(IRegistry.LOOT_SCORE_PROVIDER_TYPE, new MinecraftKey(s), (Object) (new LootScoreProviderType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", ScoreboardNameProvider::a).a(ScoreboardNameProviders.CONTEXT, new ContextScoreboardNameProvider.a()).a();
    }
}
