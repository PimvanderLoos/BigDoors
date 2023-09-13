package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.LootItemUser;
import net.minecraft.world.level.storage.loot.LootTableInfo;

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
