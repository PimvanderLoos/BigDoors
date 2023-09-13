package net.minecraft.world.level.storage.loot.providers.nbt;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;

public class NbtProviders {

    public static final LootNbtProviderType STORAGE = a("storage", new StorageNbtProvider.a());
    public static final LootNbtProviderType CONTEXT = a("context", new ContextNbtProvider.c());

    public NbtProviders() {}

    private static LootNbtProviderType a(String s, LootSerializer<? extends NbtProvider> lootserializer) {
        return (LootNbtProviderType) IRegistry.a(IRegistry.LOOT_NBT_PROVIDER_TYPE, new MinecraftKey(s), (Object) (new LootNbtProviderType(lootserializer)));
    }

    public static Object a() {
        return JsonRegistry.a(IRegistry.LOOT_NBT_PROVIDER_TYPE, "provider", "type", NbtProvider::a).a(NbtProviders.CONTEXT, new ContextNbtProvider.b()).a();
    }
}
