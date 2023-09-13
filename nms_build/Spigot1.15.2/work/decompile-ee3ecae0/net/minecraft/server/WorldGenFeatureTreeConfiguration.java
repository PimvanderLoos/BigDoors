package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class WorldGenFeatureTreeConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureStateProvider m;
    public final WorldGenFeatureStateProvider n;
    public final List<WorldGenFeatureTree> o;
    public final int p;
    public transient boolean q;

    protected WorldGenFeatureTreeConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, List<WorldGenFeatureTree> list, int i) {
        this.m = worldgenfeaturestateprovider;
        this.n = worldgenfeaturestateprovider1;
        this.o = list;
        this.p = i;
    }

    public void a() {
        this.q = true;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("trunk_provider"), this.m.a(dynamicops)).put(dynamicops.createString("leaves_provider"), this.n.a(dynamicops)).put(dynamicops.createString("decorators"), dynamicops.createList(this.o.stream().map((worldgenfeaturetree) -> {
            return worldgenfeaturetree.a(dynamicops);
        }))).put(dynamicops.createString("base_height"), dynamicops.createInt(this.p));
        return new Dynamic(dynamicops, dynamicops.createMap(builder.build()));
    }

    public static <T> WorldGenFeatureTreeConfiguration b(Dynamic<T> dynamic) {
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders1 = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));

        return new WorldGenFeatureTreeConfiguration(worldgenfeaturestateproviders.a(dynamic.get("trunk_provider").orElseEmptyMap()), worldgenfeaturestateproviders1.a(dynamic.get("leaves_provider").orElseEmptyMap()), dynamic.get("decorators").asList((dynamic1) -> {
            return ((WorldGenFeatureTrees) IRegistry.w.get(new MinecraftKey((String) dynamic1.get("type").asString().orElseThrow(RuntimeException::new)))).a(dynamic1);
        }), dynamic.get("base_height").asInt(0));
    }

    public static class a {

        public final WorldGenFeatureStateProvider a;
        public final WorldGenFeatureStateProvider b;
        private List<WorldGenFeatureTree> c = Lists.newArrayList();
        private int d = 0;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1) {
            this.a = worldgenfeaturestateprovider;
            this.b = worldgenfeaturestateprovider1;
        }

        public WorldGenFeatureTreeConfiguration.a d(int i) {
            this.d = i;
            return this;
        }

        public WorldGenFeatureTreeConfiguration b() {
            return new WorldGenFeatureTreeConfiguration(this.a, this.b, this.c, this.d);
        }
    }
}
