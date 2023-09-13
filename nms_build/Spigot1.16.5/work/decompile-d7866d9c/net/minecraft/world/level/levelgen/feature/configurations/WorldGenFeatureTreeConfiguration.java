package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTree;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class WorldGenFeatureTreeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureTreeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.a.fieldOf("trunk_provider").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.b;
        }), WorldGenFeatureStateProvider.a.fieldOf("leaves_provider").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.c;
        }), WorldGenFoilagePlacer.d.fieldOf("foliage_placer").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.f;
        }), TrunkPlacer.c.fieldOf("trunk_placer").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.g;
        }), FeatureSize.a.fieldOf("minimum_size").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.h;
        }), WorldGenFeatureTree.c.listOf().fieldOf("decorators").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.d;
        }), Codec.INT.fieldOf("max_water_depth").orElse(0).forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.i;
        }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.j;
        }), HeightMap.Type.g.fieldOf("heightmap").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.l;
        })).apply(instance, WorldGenFeatureTreeConfiguration::new);
    });
    public final WorldGenFeatureStateProvider b;
    public final WorldGenFeatureStateProvider c;
    public final List<WorldGenFeatureTree> d;
    public transient boolean e;
    public final WorldGenFoilagePlacer f;
    public final TrunkPlacer g;
    public final FeatureSize h;
    public final int i;
    public final boolean j;
    public final HeightMap.Type l;

    protected WorldGenFeatureTreeConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, TrunkPlacer trunkplacer, FeatureSize featuresize, List<WorldGenFeatureTree> list, int i, boolean flag, HeightMap.Type heightmap_type) {
        this.b = worldgenfeaturestateprovider;
        this.c = worldgenfeaturestateprovider1;
        this.d = list;
        this.f = worldgenfoilageplacer;
        this.h = featuresize;
        this.g = trunkplacer;
        this.i = i;
        this.j = flag;
        this.l = heightmap_type;
    }

    public void b() {
        this.e = true;
    }

    public WorldGenFeatureTreeConfiguration a(List<WorldGenFeatureTree> list) {
        return new WorldGenFeatureTreeConfiguration(this.b, this.c, this.f, this.g, this.h, list, this.i, this.j, this.l);
    }

    public static class a {

        public final WorldGenFeatureStateProvider a;
        public final WorldGenFeatureStateProvider b;
        private final WorldGenFoilagePlacer c;
        private final TrunkPlacer d;
        private final FeatureSize e;
        private List<WorldGenFeatureTree> f = ImmutableList.of();
        private int g;
        private boolean h;
        private HeightMap.Type i;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, TrunkPlacer trunkplacer, FeatureSize featuresize) {
            this.i = HeightMap.Type.OCEAN_FLOOR;
            this.a = worldgenfeaturestateprovider;
            this.b = worldgenfeaturestateprovider1;
            this.c = worldgenfoilageplacer;
            this.d = trunkplacer;
            this.e = featuresize;
        }

        public WorldGenFeatureTreeConfiguration.a a(List<WorldGenFeatureTree> list) {
            this.f = list;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a a(int i) {
            this.g = i;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a a() {
            this.h = true;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a a(HeightMap.Type heightmap_type) {
            this.i = heightmap_type;
            return this;
        }

        public WorldGenFeatureTreeConfiguration b() {
            return new WorldGenFeatureTreeConfiguration(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i);
        }
    }
}
