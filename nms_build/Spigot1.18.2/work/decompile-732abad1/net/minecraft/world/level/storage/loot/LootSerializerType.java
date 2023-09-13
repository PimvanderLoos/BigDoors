package net.minecraft.world.level.storage.loot;

public class LootSerializerType<T> {

    private final LootSerializer<? extends T> serializer;

    public LootSerializerType(LootSerializer<? extends T> lootserializer) {
        this.serializer = lootserializer;
    }

    public LootSerializer<? extends T> getSerializer() {
        return this.serializer;
    }
}
