package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.LootItemUser;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public interface LootItemCondition extends LootItemUser, Predicate<LootTableInfo> {

    LootItemConditionType getType();

    @FunctionalInterface
    public interface a {

        LootItemCondition build();

        default LootItemCondition.a invert() {
            return LootItemConditionInverted.invert(this);
        }

        default LootItemConditionAlternative.a or(LootItemCondition.a lootitemcondition_a) {
            return LootItemConditionAlternative.alternative(this, lootitemcondition_a);
        }
    }
}
