package net.minecraft.world.level.storage.loot.predicates;

public interface LootItemConditionUser<T> {

    T when(LootItemCondition.a lootitemcondition_a);

    T unwrap();
}
