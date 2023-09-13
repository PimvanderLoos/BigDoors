package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootEntrySequence extends LootEntryChildrenAbstract {

    LootEntrySequence(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition) {
        super(alootentryabstract, alootitemcondition);
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.SEQUENCE;
    }

    @Override
    protected LootEntryChildren compose(LootEntryChildren[] alootentrychildren) {
        switch (alootentrychildren.length) {
            case 0:
                return LootEntrySequence.ALWAYS_TRUE;
            case 1:
                return alootentrychildren[0];
            case 2:
                return alootentrychildren[0].and(alootentrychildren[1]);
            default:
                return (loottableinfo, consumer) -> {
                    LootEntryChildren[] alootentrychildren1 = alootentrychildren;
                    int i = alootentrychildren.length;

                    for (int j = 0; j < i; ++j) {
                        LootEntryChildren lootentrychildren = alootentrychildren1[j];

                        if (!lootentrychildren.expand(loottableinfo, consumer)) {
                            return false;
                        }
                    }

                    return true;
                };
        }
    }

    public static LootEntrySequence.a sequential(LootEntryAbstract.a<?>... alootentryabstract_a) {
        return new LootEntrySequence.a(alootentryabstract_a);
    }

    public static class a extends LootEntryAbstract.a<LootEntrySequence.a> {

        private final List<LootEntryAbstract> entries = Lists.newArrayList();

        public a(LootEntryAbstract.a<?>... alootentryabstract_a) {
            LootEntryAbstract.a[] alootentryabstract_a1 = alootentryabstract_a;
            int i = alootentryabstract_a.length;

            for (int j = 0; j < i; ++j) {
                LootEntryAbstract.a<?> lootentryabstract_a = alootentryabstract_a1[j];

                this.entries.add(lootentryabstract_a.build());
            }

        }

        @Override
        protected LootEntrySequence.a getThis() {
            return this;
        }

        @Override
        public LootEntrySequence.a then(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.build());
            return this;
        }

        @Override
        public LootEntryAbstract build() {
            return new LootEntrySequence((LootEntryAbstract[]) this.entries.toArray(new LootEntryAbstract[0]), this.getConditions());
        }
    }
}
