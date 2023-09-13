package net.minecraft.server;

public class LootEntryType extends LootSerializerType<LootEntryAbstract> {

    public LootEntryType(LootSerializer<? extends LootEntryAbstract> lootserializer) {
        super(lootserializer);
    }
}
