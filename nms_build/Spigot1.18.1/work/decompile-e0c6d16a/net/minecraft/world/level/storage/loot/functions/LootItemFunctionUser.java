package net.minecraft.world.level.storage.loot.functions;

public interface LootItemFunctionUser<T> {

    T apply(LootItemFunction.a lootitemfunction_a);

    T unwrap();
}
