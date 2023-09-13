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

    private final Multimap<String, String> a;
    private final Supplier<String> b;
    private final LootContextParameterSet c;
    private final Function<MinecraftKey, LootItemCondition> d;
    private final Set<MinecraftKey> e;
    private final Function<MinecraftKey, LootTable> f;
    private final Set<MinecraftKey> g;
    private String h;

    public LootCollector(LootContextParameterSet lootcontextparameterset, Function<MinecraftKey, LootItemCondition> function, Function<MinecraftKey, LootTable> function1) {
        this(HashMultimap.create(), () -> {
            return "";
        }, lootcontextparameterset, function, ImmutableSet.of(), function1, ImmutableSet.of());
    }

    public LootCollector(Multimap<String, String> multimap, Supplier<String> supplier, LootContextParameterSet lootcontextparameterset, Function<MinecraftKey, LootItemCondition> function, Set<MinecraftKey> set, Function<MinecraftKey, LootTable> function1, Set<MinecraftKey> set1) {
        this.a = multimap;
        this.b = supplier;
        this.c = lootcontextparameterset;
        this.d = function;
        this.e = set;
        this.f = function1;
        this.g = set1;
    }

    private String b() {
        if (this.h == null) {
            this.h = (String) this.b.get();
        }

        return this.h;
    }

    public void a(String s) {
        this.a.put(this.b(), s);
    }

    public LootCollector b(String s) {
        return new LootCollector(this.a, () -> {
            return this.b() + s;
        }, this.c, this.d, this.e, this.f, this.g);
    }

    public LootCollector a(String s, MinecraftKey minecraftkey) {
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.builder().addAll(this.g).add(minecraftkey).build();

        return new LootCollector(this.a, () -> {
            return this.b() + s;
        }, this.c, this.d, this.e, this.f, immutableset);
    }

    public LootCollector b(String s, MinecraftKey minecraftkey) {
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.builder().addAll(this.e).add(minecraftkey).build();

        return new LootCollector(this.a, () -> {
            return this.b() + s;
        }, this.c, this.d, immutableset, this.f, this.g);
    }

    public boolean a(MinecraftKey minecraftkey) {
        return this.g.contains(minecraftkey);
    }

    public boolean b(MinecraftKey minecraftkey) {
        return this.e.contains(minecraftkey);
    }

    public Multimap<String, String> a() {
        return ImmutableMultimap.copyOf(this.a);
    }

    public void a(LootItemUser lootitemuser) {
        this.c.a(this, lootitemuser);
    }

    @Nullable
    public LootTable c(MinecraftKey minecraftkey) {
        return (LootTable) this.f.apply(minecraftkey);
    }

    @Nullable
    public LootItemCondition d(MinecraftKey minecraftkey) {
        return (LootItemCondition) this.d.apply(minecraftkey);
    }

    public LootCollector a(LootContextParameterSet lootcontextparameterset) {
        return new LootCollector(this.a, this.b, lootcontextparameterset, this.d, this.e, this.f, this.g);
    }
}
