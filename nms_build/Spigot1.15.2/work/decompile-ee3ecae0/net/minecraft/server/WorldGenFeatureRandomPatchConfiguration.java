package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGenFeatureRandomPatchConfiguration implements WorldGenFeatureConfiguration {

    public final WorldGenFeatureStateProvider a;
    public final WorldGenBlockPlacer b;
    public final Set<Block> c;
    public final Set<IBlockData> d;
    public final int f;
    public final int g;
    public final int h;
    public final int i;
    public final boolean j;
    public final boolean k;
    public final boolean l;

    private WorldGenFeatureRandomPatchConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer, Set<Block> set, Set<IBlockData> set1, int i, int j, int k, int l, boolean flag, boolean flag1, boolean flag2) {
        this.a = worldgenfeaturestateprovider;
        this.b = worldgenblockplacer;
        this.c = set;
        this.d = set1;
        this.f = i;
        this.g = j;
        this.h = k;
        this.i = l;
        this.j = flag;
        this.k = flag1;
        this.l = flag2;
    }

    @Override
    public <T> Dynamic<T> a(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("state_provider"), this.a.a(dynamicops)).put(dynamicops.createString("block_placer"), this.b.a(dynamicops)).put(dynamicops.createString("whitelist"), dynamicops.createList(this.c.stream().map((block) -> {
            return IBlockData.a(dynamicops, block.getBlockData()).getValue();
        }))).put(dynamicops.createString("blacklist"), dynamicops.createList(this.d.stream().map((iblockdata) -> {
            return IBlockData.a(dynamicops, iblockdata).getValue();
        }))).put(dynamicops.createString("tries"), dynamicops.createInt(this.f)).put(dynamicops.createString("xspread"), dynamicops.createInt(this.g)).put(dynamicops.createString("yspread"), dynamicops.createInt(this.h)).put(dynamicops.createString("zspread"), dynamicops.createInt(this.i)).put(dynamicops.createString("can_replace"), dynamicops.createBoolean(this.j)).put(dynamicops.createString("project"), dynamicops.createBoolean(this.k)).put(dynamicops.createString("need_water"), dynamicops.createBoolean(this.l));
        return new Dynamic(dynamicops, dynamicops.createMap(builder.build()));
    }

    public static <T> WorldGenFeatureRandomPatchConfiguration a(Dynamic<T> dynamic) {
        WorldGenFeatureStateProviders<?> worldgenfeaturestateproviders = (WorldGenFeatureStateProviders) IRegistry.t.get(new MinecraftKey((String) dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        WorldGenBlockPlacers<?> worldgenblockplacers = (WorldGenBlockPlacers) IRegistry.u.get(new MinecraftKey((String) dynamic.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));

        return new WorldGenFeatureRandomPatchConfiguration(worldgenfeaturestateproviders.a(dynamic.get("state_provider").orElseEmptyMap()), worldgenblockplacers.a(dynamic.get("block_placer").orElseEmptyMap()), (Set) dynamic.get("whitelist").asList(IBlockData::a).stream().map(IBlockData::getBlock).collect(Collectors.toSet()), Sets.newHashSet(dynamic.get("blacklist").asList(IBlockData::a)), dynamic.get("tries").asInt(128), dynamic.get("xspread").asInt(7), dynamic.get("yspread").asInt(3), dynamic.get("zspread").asInt(7), dynamic.get("can_replace").asBoolean(false), dynamic.get("project").asBoolean(true), dynamic.get("need_water").asBoolean(false));
    }

    public static class a {

        private final WorldGenFeatureStateProvider a;
        private final WorldGenBlockPlacer b;
        private Set<Block> c = ImmutableSet.of();
        private Set<IBlockData> d = ImmutableSet.of();
        private int e = 64;
        private int f = 7;
        private int g = 3;
        private int h = 7;
        private boolean i;
        private boolean j = true;
        private boolean k = false;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenBlockPlacer worldgenblockplacer) {
            this.a = worldgenfeaturestateprovider;
            this.b = worldgenblockplacer;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(Set<Block> set) {
            this.c = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(Set<IBlockData> set) {
            this.d = set;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a(int i) {
            this.e = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b(int i) {
            this.f = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c(int i) {
            this.g = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a d(int i) {
            this.h = i;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a a() {
            this.i = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a b() {
            this.j = false;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration.a c() {
            this.k = true;
            return this;
        }

        public WorldGenFeatureRandomPatchConfiguration d() {
            return new WorldGenFeatureRandomPatchConfiguration(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k);
        }
    }
}
