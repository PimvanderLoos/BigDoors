package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class NumberProviders {

    public static final LootNumberProviderType CONSTANT = register("constant", new ConstantValue.b());
    public static final LootNumberProviderType UNIFORM = register("uniform", new UniformGenerator.a());
    public static final LootNumberProviderType BINOMIAL = register("binomial", new BinomialDistributionGenerator.a());
    public static final LootNumberProviderType SCORE = register("score", new ScoreboardValue.a());

    public NumberProviders() {}

    private static LootNumberProviderType register(String s, LootSerializer<? extends NumberProvider> lootserializer) {
        return (LootNumberProviderType) IRegistry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, new MinecraftKey(s), new LootNumberProviderType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", NumberProvider::getType).withInlineSerializer(NumberProviders.CONSTANT, new ConstantValue.a()).withDefaultType(NumberProviders.UNIFORM).build();
    }
}
