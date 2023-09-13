package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootEntryAlternatives extends LootEntryChildrenAbstract {

    LootEntryAlternatives(LootEntryAbstract[] alootentryabstract, LootItemCondition[] alootitemcondition) {
        super(alootentryabstract, alootitemcondition);
    }

    @Override
    public LootEntryType getType() {
        return LootEntries.ALTERNATIVES;
    }

    @Override
    protected LootEntryChildren compose(LootEntryChildren[] alootentrychildren) {
        switch (alootentrychildren.length) {
            case 0:
                return LootEntryAlternatives.ALWAYS_FALSE;
            case 1:
                return alootentrychildren[0];
            case 2:
                return alootentrychildren[0].or(alootentrychildren[1]);
            default:
                return (loottableinfo, consumer) -> {
                    LootEntryChildren[] alootentrychildren1 = alootentrychildren;
                    int i = alootentrychildren.length;

                    for (int j = 0; j < i; ++j) {
                        LootEntryChildren lootentrychildren = alootentrychildren1[j];

                        if (lootentrychildren.expand(loottableinfo, consumer)) {
                            return true;
                        }
                    }

                    return false;
                };
        }
    }

    @Override
    public void validate(LootCollector lootcollector) {
        super.validate(lootcollector);

        for (int i = 0; i < this.children.length - 1; ++i) {
            if (ArrayUtils.isEmpty(this.children[i].conditions)) {
                lootcollector.reportProblem("Unreachable entry!");
            }
        }

    }

    public static LootEntryAlternatives.a alternatives(LootEntryAbstract.a<?>... alootentryabstract_a) {
        return new LootEntryAlternatives.a(alootentryabstract_a);
    }

    public static <E> LootEntryAlternatives.a alternatives(Collection<E> collection, Function<E, LootEntryAbstract.a<?>> function) {
        Stream stream = collection.stream();

        Objects.requireNonNull(function);
        return new LootEntryAlternatives.a((LootEntryAbstract.a[]) stream.map(function::apply).toArray((i) -> {
            return new LootEntryAbstract.a[i];
        }));
    }

    public static class a extends LootEntryAbstract.a<LootEntryAlternatives.a> {

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
        protected LootEntryAlternatives.a getThis() {
            return this;
        }

        @Override
        public LootEntryAlternatives.a otherwise(LootEntryAbstract.a<?> lootentryabstract_a) {
            this.entries.add(lootentryabstract_a.build());
            return this;
        }

        @Override
        public LootEntryAbstract build() {
            return new LootEntryAlternatives((LootEntryAbstract[]) this.entries.toArray(new LootEntryAbstract[0]), this.getConditions());
        }
    }
}
