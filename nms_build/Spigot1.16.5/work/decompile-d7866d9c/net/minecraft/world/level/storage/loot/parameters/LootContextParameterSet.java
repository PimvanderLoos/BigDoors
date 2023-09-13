package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootCollector;
import net.minecraft.world.level.storage.loot.LootItemUser;

public class LootContextParameterSet {

    private final Set<LootContextParameter<?>> a;
    private final Set<LootContextParameter<?>> b;

    private LootContextParameterSet(Set<LootContextParameter<?>> set, Set<LootContextParameter<?>> set1) {
        this.a = ImmutableSet.copyOf(set);
        this.b = ImmutableSet.copyOf(Sets.union(set, set1));
    }

    public Set<LootContextParameter<?>> getRequired() {
        return this.a;
    }

    public Set<LootContextParameter<?>> getOptional() {
        return this.b;
    }

    public String toString() {
        return "[" + Joiner.on(", ").join(this.b.stream().map((lootcontextparameter) -> {
            return (this.a.contains(lootcontextparameter) ? "!" : "") + lootcontextparameter.a();
        }).iterator()) + "]";
    }

    public void a(LootCollector lootcollector, LootItemUser lootitemuser) {
        Set<LootContextParameter<?>> set = lootitemuser.a();
        Set<LootContextParameter<?>> set1 = Sets.difference(set, this.b);

        if (!set1.isEmpty()) {
            lootcollector.a("Parameters " + set1 + " are not provided in this context");
        }

    }

    public static class Builder {

        private final Set<LootContextParameter<?>> a = Sets.newIdentityHashSet();
        private final Set<LootContextParameter<?>> b = Sets.newIdentityHashSet();

        public Builder() {}

        public LootContextParameterSet.Builder addRequired(LootContextParameter<?> lootcontextparameter) {
            if (this.b.contains(lootcontextparameter)) {
                throw new IllegalArgumentException("Parameter " + lootcontextparameter.a() + " is already optional");
            } else {
                this.a.add(lootcontextparameter);
                return this;
            }
        }

        public LootContextParameterSet.Builder addOptional(LootContextParameter<?> lootcontextparameter) {
            if (this.a.contains(lootcontextparameter)) {
                throw new IllegalArgumentException("Parameter " + lootcontextparameter.a() + " is already required");
            } else {
                this.b.add(lootcontextparameter);
                return this;
            }
        }

        public LootContextParameterSet build() {
            return new LootContextParameterSet(this.a, this.b);
        }
    }
}
