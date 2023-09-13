package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class WorldGenMegaTreeConfiguration extends WorldGenFeatureTreeConfiguration {

    public final int a;
    public final int b;

    protected WorldGenMegaTreeConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, List<WorldGenFeatureTree> list, int i, int j, int k) {
        super(worldgenfeaturestateprovider, worldgenfeaturestateprovider1, list, i);
        this.a = j;
        this.b = k;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Dynamic<T> dynamic = new Dynamic(dynamicops, dynamicops.createMap(ImmutableMap.of(dynamicops.createString("height_interval"), dynamicops.createInt(this.a), dynamicops.createString("crown_height"), dynamicops.createInt(this.b))));

        return dynamic.merge(super.a(dynamicops));
    }

    public static <T> WorldGenMegaTreeConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration = WorldGenFeatureTreeConfiguration.b(dynamic);

        return new WorldGenMegaTreeConfiguration(worldgenfeaturetreeconfiguration.m, worldgenfeaturetreeconfiguration.n, worldgenfeaturetreeconfiguration.o, worldgenfeaturetreeconfiguration.p, dynamic.get("height_interval").asInt(0), dynamic.get("crown_height").asInt(0));
    }

    public static class a extends WorldGenFeatureTreeConfiguration.a {

        private List<WorldGenFeatureTree> c = ImmutableList.of();
        private int d;
        private int e;
        private int f;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1) {
            super(worldgenfeaturestateprovider, worldgenfeaturestateprovider1);
        }

        public WorldGenMegaTreeConfiguration.a a(List<WorldGenFeatureTree> list) {
            this.c = list;
            return this;
        }

        @Override
        public WorldGenMegaTreeConfiguration.a d(int i) {
            this.d = i;
            return this;
        }

        public WorldGenMegaTreeConfiguration.a b(int i) {
            this.e = i;
            return this;
        }

        public WorldGenMegaTreeConfiguration.a c(int i) {
            this.f = i;
            return this;
        }

        @Override
        public WorldGenMegaTreeConfiguration b() {
            return new WorldGenMegaTreeConfiguration(this.a, this.b, this.c, this.d, this.e, this.f);
        }
    }
}
