package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootCollector {

    private final Multimap<String, String> problems;
    private final Supplier<String> context;
    private final LootContextParameterSet params;
    private final Function<MinecraftKey, LootItemCondition> conditionResolver;
    private final Set<MinecraftKey> visitedConditions;
    private final Function<MinecraftKey, LootTable> tableResolver;
    private final Set<MinecraftKey> visitedTables;
    private String contextCache;

    public LootCollector(LootContextParameterSet lootcontextparameterset, Function<MinecraftKey, LootItemCondition> function, Function<MinecraftKey, LootTable> function1) {
        this(HashMultimap.create(), () -> {
            return "";
        }, lootcontextparameterset, function, ImmutableSet.of(), function1, ImmutableSet.of());
    }

    public LootCollector(Multimap<String, String> multimap, Supplier<String> supplier, LootContextParameterSet lootcontextparameterset, Function<MinecraftKey, LootItemCondition> function, Set<MinecraftKey> set, Function<MinecraftKey, LootTable> function1, Set<MinecraftKey> set1) {
        this.problems = multimap;
        this.context = supplier;
        this.params = lootcontextparameterset;
        this.conditionResolver = function;
        this.visitedConditions = set;
        this.tableResolver = function1;
        this.visitedTables = set1;
    }

    private String b() {
        if (this.contextCache == null) {
            this.contextCache = (String) this.context.get();
        }

        return this.contextCache;
    }

    public void a(String s) {
        this.problems.put(this.b(), s);
    }

    public LootCollector b(String s) {
        return new LootCollector(this.problems, () -> {
            String s1 = this.b();

            return s1 + s;
        }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }

    public LootCollector a(String s, MinecraftKey minecraftkey) {
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.builder().addAll(this.visitedTables).add(minecraftkey).build();

        return new LootCollector(this.problems, () -> {
            String s1 = this.b();

            return s1 + s;
        }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, immutableset);
    }

    public LootCollector b(String s, MinecraftKey minecraftkey) {
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.builder().addAll(this.visitedConditions).add(minecraftkey).build();

        return new LootCollector(this.problems, () -> {
            String s1 = this.b();

            return s1 + s;
        }, this.params, this.conditionResolver, immutableset, this.tableResolver, this.visitedTables);
    }

    public boolean a(MinecraftKey minecraftkey) {
        return this.visitedTables.contains(minecraftkey);
    }

    public boolean b(MinecraftKey minecraftkey) {
        return this.visitedConditions.contains(minecraftkey);
    }

    public Multimap<String, String> a() {
        return ImmutableMultimap.copyOf(this.problems);
    }

    public void a(LootItemUser lootitemuser) {
        this.params.a(this, lootitemuser);
    }

    @Nullable
    public LootTable c(MinecraftKey minecraftkey) {
        return (LootTable) this.tableResolver.apply(minecraftkey);
    }

    @Nullable
    public LootItemCondition d(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.conditionResolver.apply(minecraftkey);
    }

    public LootCollector a(LootContextParameterSet lootcontextparameterset) {
        return new LootCollector(this.problems, this.context, lootcontextparameterset, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
    }
}
