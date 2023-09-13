package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class WorldGenFeatureSmallTreeConfigurationConfiguration extends WorldGenFeatureTreeConfiguration {

    public final WorldGenFoilagePlacer a;
    public final int b;
    public final int c;
    public final int d;
    public final int f;
    public final int g;
    public final int h;
    public final int i;
    public final int j;
    public final int k;
    public final boolean l;

    protected WorldGenFeatureSmallTreeConfigurationConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, List<WorldGenFeatureTree> list, int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, boolean flag) {
        super(worldgenfeaturestateprovider, worldgenfeaturestateprovider1, list, i);
        this.a = worldgenfoilageplacer;
        this.b = j;
        this.c = k;
        this.d = l;
        this.f = i1;
        this.g = j1;
        this.h = k1;
        this.i = l1;
        this.j = i2;
        this.k = j2;
        this.l = flag;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("foliage_placer"), this.a.a(dynamicops)).put(dynamicops.createString("height_rand_a"), dynamicops.createInt(this.b)).put(dynamicops.createString("height_rand_b"), dynamicops.createInt(this.c)).put(dynamicops.createString("trunk_height"), dynamicops.createInt(this.d)).put(dynamicops.createString("trunk_height_random"), dynamicops.createInt(this.f)).put(dynamicops.createString("trunk_top_offset"), dynamicops.createInt(this.g)).put(dynamicops.createString("trunk_top_offset_random"), dynamicops.createInt(this.h)).put(dynamicops.createString("foliage_height"), dynamicops.createInt(this.i)).put(dynamicops.createString("foliage_height_random"), dynamicops.createInt(this.j)).put(dynamicops.createString("max_water_depth"), dynamicops.createInt(this.k)).put(dynamicops.createString("ignore_vines"), dynamicops.createBoolean(this.l));
        Dynamic<T> dynamic = new Dynamic(dynamicops, dynamicops.createMap(builder.build()));

        return dynamic.merge(super.a(dynamicops));
    }

    public static <T> WorldGenFeatureSmallTreeConfigurationConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration = WorldGenFeatureTreeConfiguration.b(dynamic);
        WorldGenFoilagePlacers<?> worldgenfoilageplacers = (WorldGenFoilagePlacers) IRegistry.v.get(new MinecraftKey((String) dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));

        return new WorldGenFeatureSmallTreeConfigurationConfiguration(worldgenfeaturetreeconfiguration.m, worldgenfeaturetreeconfiguration.n, worldgenfoilageplacers.a(dynamic.get("foliage_placer").orElseEmptyMap()), worldgenfeaturetreeconfiguration.o, worldgenfeaturetreeconfiguration.p, dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0), dynamic.get("trunk_height").asInt(-1), dynamic.get("trunk_height_random").asInt(0), dynamic.get("trunk_top_offset").asInt(0), dynamic.get("trunk_top_offset_random").asInt(0), dynamic.get("foliage_height").asInt(-1), dynamic.get("foliage_height_random").asInt(0), dynamic.get("max_water_depth").asInt(0), dynamic.get("ignore_vines").asBoolean(false));
    }

    public static class a extends WorldGenFeatureTreeConfiguration.a {

        private final WorldGenFoilagePlacer c;
        private List<WorldGenFeatureTree> d = ImmutableList.of();
        private int e;
        private int f;
        private int g;
        private int h = -1;
        private int i;
        private int j;
        private int k;
        private int l = -1;
        private int m;
        private int n;
        private boolean o;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer) {
            super(worldgenfeaturestateprovider, worldgenfeaturestateprovider1);
            this.c = worldgenfoilageplacer;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a a(List<WorldGenFeatureTree> list) {
            this.d = list;
            return this;
        }

        @Override
        public WorldGenFeatureSmallTreeConfigurationConfiguration.a d(int i) {
            this.e = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a b(int i) {
            this.f = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a c(int i) {
            this.g = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a e(int i) {
            this.h = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a f(int i) {
            this.i = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a g(int i) {
            this.j = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a h(int i) {
            this.k = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a i(int i) {
            this.l = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a j(int i) {
            this.m = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a k(int i) {
            this.n = i;
            return this;
        }

        public WorldGenFeatureSmallTreeConfigurationConfiguration.a a() {
            this.o = true;
            return this;
        }

        @Override
        public WorldGenFeatureSmallTreeConfigurationConfiguration b() {
            return new WorldGenFeatureSmallTreeConfigurationConfiguration(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o);
        }
    }
}
