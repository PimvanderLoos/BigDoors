package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootItemUser;

public class LootContextParameterSet {

    private final Set<LootContextParameter<?>> required;
    private final Set<LootContextParameter<?>> all;

    LootContextParameterSet(Set<LootContextParameter<?>> set, Set<LootContextParameter<?>> set1) {
        this.required = ImmutableSet.copyOf(set);
        this.all = ImmutableSet.copyOf(Sets.union(set, set1));
    }

    public boolean isAllowed(LootContextParameter<?> lootcontextparameter) {
        return this.all.contains(lootcontextparameter);
    }

    public Set<LootContextParameter<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParameter<?>> getAllowed() {
        return this.all;
    }

    public String toString() {
        Joiner joiner = Joiner.on(", ");
        Iterator iterator = this.all.stream().map((lootcontextparameter) -> {
            String s = this.required.contains(lootcontextparameter) ? "!" : "";

            return s + lootcontextparameter.getName();
        }).iterator();

        return "[" + joiner.join(iterator) + "]";
    }

    public void validateUser(LootCollector lootcollector, LootItemUser lootitemuser) {
        Set<LootContextParameter<?>> set = lootitemuser.getReferencedContextParams();
        Set<LootContextParameter<?>> set1 = Sets.difference(set, this.all);

        if (!set1.isEmpty()) {
            lootcollector.reportProblem("Parameters " + set1 + " are not provided in this context");
        }

    }

    public static LootContextParameterSet.Builder builder() {
        return new LootContextParameterSet.Builder();
    }

    public static class Builder {

        private final Set<LootContextParameter<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParameter<?>> optional = Sets.newIdentityHashSet();

        public Builder() {}

        public LootContextParameterSet.Builder required(LootContextParameter<?> lootcontextparameter) {
            if (this.optional.contains(lootcontextparameter)) {
                throw new IllegalArgumentException("Parameter " + lootcontextparameter.getName() + " is already optional");
            } else {
                this.required.add(lootcontextparameter);
                return this;
            }
        }

        public LootContextParameterSet.Builder optional(LootContextParameter<?> lootcontextparameter) {
            if (this.required.contains(lootcontextparameter)) {
                throw new IllegalArgumentException("Parameter " + lootcontextparameter.getName() + " is already required");
            } else {
                this.optional.add(lootcontextparameter);
                return this;
            }
        }

        public LootContextParameterSet build() {
            return new LootContextParameterSet(this.required, this.optional);
        }
    }
}
