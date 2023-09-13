package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class NumberProviders {

    public static final LootNumberProviderType CONSTANT = a("constant", new ConstantValue.b());
    public static final LootNumberProviderType UNIFORM = a("uniform", new UniformGenerator.a());
    public static final LootNumberProviderType BINOMIAL = a("binomial", new BinomialDistributionGenerator.a());
    public static final LootNumberProviderType SCORE = a("score", new ScoreboardValue.a());

    public NumberProviders() {}

    private static LootNumberProviderType a(String s, LootSerializer<? extends NumberProvider> lootserializer) {
        return (LootNumberProviderType) IRegistry.a(IRegistry.LOOT_NUMBER_PROVIDER_TYPE, new MinecraftKey(s), (Object) (new LootNumberProviderType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", NumberProvider::a).a(NumberProviders.CONSTANT, new ConstantValue.a()).a(NumberProviders.UNIFORM).a();
    }
}
