package net.minecraft.server;

import java.util.function.Predicate;

public interface LootItemCondition extends LootItemUser, Predicate<LootTableInfo> {

    LootItemConditionType b();

    @FunctionalInterface
    public interface a {

        LootItemCondition build();

        default LootItemCondition.a a() {
            return LootItemConditionInverted.a(this);
        }

        default LootItemConditionAlternative.a a(LootItemCondition.a lootitemcondition_a) {
            return LootItemConditionAlternative.a(this, lootitemcondition_a);
        }
    }
}
