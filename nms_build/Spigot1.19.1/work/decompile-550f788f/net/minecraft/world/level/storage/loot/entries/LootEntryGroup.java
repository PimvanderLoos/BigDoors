package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootEntryGroup extends LootEntryChildrenAbstract {

    LootEntryGroup(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition) {
        super(alootentryabstract, alootitemcondition);
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.GROUP;
    }

    @Override
    protected LootEntryChildren compose(LootEntryChildren[] alootentrychildren) {
        switch (alootentrychildren.length) {
            case 0:
                return LootEntryGroup.ALWAYS_TRUE;
            case 1:
                return alootentrychildren[0];
            case 2:
                LootEntryChildren lootentrychildren = alootentrychildren[0];
                LootEntryChildren lootentrychildren1 = alootentrychildren[1];

                return (loottableinfo, consumer) -> {
                    lootentrychildren.expand(loottableinfo, consumer);
                    lootentrychildren1.expand(loottableinfo, consumer);
                    return true;
                };
            default:
                return (loottableinfo, consumer) -> {
                    LootEntryChildren[] alootentrychildren1 = alootentrychildren;
                    int i = alootentrychildren.length;

                    for (int j = 0; j < i; ++j) {
                        LootEntryChildren lootentrychildren2 = alootentrychildren1[j];

                        lootentrychildren2.expand(loottableinfo, consumer);
                    }

                    return true;
                };
        }
    }

    public static LootEntryGroup.a list(LootEntryAbstract.a<?>... alootentryabstract_a) {
        return new LootEntryGroup.a(alootentryabstract_a);
    }

    public static class a extends LootEntryAbstract.a<LootEntryGroup.a> {

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
        protected LootEntryGroup.a getThis() {
            return this;
        }

        @Override
        public LootEntryGroup.a append(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.build());
            return this;
        }

        @Override
        public LootEntryAbstract build() {
            return new LootEntryGroup((LootEntryAbstract[]) this.entries.toArray(new LootEntryAbstract[0]), this.getConditions());
        }
    }
}
