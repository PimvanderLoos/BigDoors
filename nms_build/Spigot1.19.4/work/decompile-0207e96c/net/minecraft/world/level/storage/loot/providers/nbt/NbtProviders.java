package net.minecraft.world.level.storage.loot.providers.nbt;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class NbtProviders {

    public static final LootNbtProviderType STORAGE = register("storage", new StorageNbtProvider.a());
    public static final LootNbtProviderType CONTEXT = register("context", new ContextNbtProvider.c());

    public NbtProviders() {}

    private static LootNbtProviderType register(String s, LootSerializer<? extends NbtProvider> lootserializer) {
        return (LootNbtProviderType) IRegistry.register(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, new MinecraftKey(s), new LootNbtProviderType(lootserializer));
    }

    public static Object createGsonAdapter() {
        return JsonRegistry.builder(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, "provider", "type", NbtProvider::getType).withInlineSerializer(NbtProviders.CONTEXT, new ContextNbtProvider.b()).build();
    }
}
