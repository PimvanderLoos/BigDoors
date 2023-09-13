package net.minecraft.world.level.storage.loot;

public class LootSerializerType<T> {

    private final LootSerializer<? extends T> a;

    public LootSerializerType(LootSerializer<? extends T> lootserializer) {
        this.a = lootserializer;
    }

    public LootSerializer<? extends T> a() {
        return this.a;
    }
}
