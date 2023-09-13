package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTree;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class WorldGenFeatureTreeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureTreeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("trunk_provider").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.trunkProvider;
        }), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.trunkPlacer;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("foliage_provider").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.foliageProvider;
        }), WorldGenFoilagePlacer.CODEC.fieldOf("foliage_placer").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.foliagePlacer;
        }), RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.rootPlacer;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("dirt_provider").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.dirtProvider;
        }), FeatureSize.CODEC.fieldOf("minimum_size").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.minimumSize;
        }), WorldGenFeatureTree.CODEC.listOf().fieldOf("decorators").forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.decorators;
        }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.ignoreVines;
        }), Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter((worldgenfeaturetreeconfiguration) -> {
            return worldgenfeaturetreeconfiguration.forceDirt;
        })).apply(instance, WorldGenFeatureTreeConfiguration::new);
    });
    public final WorldGenFeatureStateProvider trunkProvider;
    public final WorldGenFeatureStateProvider dirtProvider;
    public final TrunkPlacer trunkPlacer;
    public final WorldGenFeatureStateProvider foliageProvider;
    public final WorldGenFoilagePlacer foliagePlacer;
    public final Optional<RootPlacer> rootPlacer;
    public final FeatureSize minimumSize;
    public final List<WorldGenFeatureTree> decorators;
    public final boolean ignoreVines;
    public final boolean forceDirt;

    protected WorldGenFeatureTreeConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider, TrunkPlacer trunkplacer, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, Optional<RootPlacer> optional, WorldGenFeatureStateProvider worldgenfeaturestateprovider2, FeatureSize featuresize, List<WorldGenFeatureTree> list, boolean flag, boolean flag1) {
        this.trunkProvider = worldgenfeaturestateprovider;
        this.trunkPlacer = trunkplacer;
        this.foliageProvider = worldgenfeaturestateprovider1;
        this.foliagePlacer = worldgenfoilageplacer;
        this.rootPlacer = optional;
        this.dirtProvider = worldgenfeaturestateprovider2;
        this.minimumSize = featuresize;
        this.decorators = list;
        this.ignoreVines = flag;
        this.forceDirt = flag1;
    }

    public static class a {

        public final WorldGenFeatureStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        public final WorldGenFeatureStateProvider foliageProvider;
        private final WorldGenFoilagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private WorldGenFeatureStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<WorldGenFeatureTree> decorators;
        private boolean ignoreVines;
        private boolean forceDirt;

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, TrunkPlacer trunkplacer, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, Optional<RootPlacer> optional, FeatureSize featuresize) {
            this.decorators = ImmutableList.of();
            this.trunkProvider = worldgenfeaturestateprovider;
            this.trunkPlacer = trunkplacer;
            this.foliageProvider = worldgenfeaturestateprovider1;
            this.dirtProvider = WorldGenFeatureStateProvider.simple(Blocks.DIRT);
            this.foliagePlacer = worldgenfoilageplacer;
            this.rootPlacer = optional;
            this.minimumSize = featuresize;
        }

        public a(WorldGenFeatureStateProvider worldgenfeaturestateprovider, TrunkPlacer trunkplacer, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, WorldGenFoilagePlacer worldgenfoilageplacer, FeatureSize featuresize) {
            this(worldgenfeaturestateprovider, trunkplacer, worldgenfeaturestateprovider1, worldgenfoilageplacer, Optional.empty(), featuresize);
        }

        public WorldGenFeatureTreeConfiguration.a dirt(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
            this.dirtProvider = worldgenfeaturestateprovider;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a decorators(List<WorldGenFeatureTree> list) {
            this.decorators = list;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public WorldGenFeatureTreeConfiguration.a forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public WorldGenFeatureTreeConfiguration build() {
            return new WorldGenFeatureTreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
        }
    }
}
