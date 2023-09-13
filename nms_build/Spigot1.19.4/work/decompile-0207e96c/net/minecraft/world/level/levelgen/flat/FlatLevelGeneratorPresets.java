package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelGeneratorPresets {

    public static final ResourceKey<FlatLevelGeneratorPreset> CLASSIC_FLAT = register("classic_flat");
    public static final ResourceKey<FlatLevelGeneratorPreset> TUNNELERS_DREAM = register("tunnelers_dream");
    public static final ResourceKey<FlatLevelGeneratorPreset> WATER_WORLD = register("water_world");
    public static final ResourceKey<FlatLevelGeneratorPreset> OVERWORLD = register("overworld");
    public static final ResourceKey<FlatLevelGeneratorPreset> SNOWY_KINGDOM = register("snowy_kingdom");
    public static final ResourceKey<FlatLevelGeneratorPreset> BOTTOMLESS_PIT = register("bottomless_pit");
    public static final ResourceKey<FlatLevelGeneratorPreset> DESERT = register("desert");
    public static final ResourceKey<FlatLevelGeneratorPreset> REDSTONE_READY = register("redstone_ready");
    public static final ResourceKey<FlatLevelGeneratorPreset> THE_VOID = register("the_void");

    public FlatLevelGeneratorPresets() {}

    public static void bootstrap(BootstapContext<FlatLevelGeneratorPreset> bootstapcontext) {
        (new FlatLevelGeneratorPresets.a(bootstapcontext)).run();
    }

    private static ResourceKey<FlatLevelGeneratorPreset> register(String s) {
        return ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, new MinecraftKey(s));
    }

    private static class a {

        private final BootstapContext<FlatLevelGeneratorPreset> context;

        a(BootstapContext<FlatLevelGeneratorPreset> bootstapcontext) {
            this.context = bootstapcontext;
        }

        private void register(ResourceKey<FlatLevelGeneratorPreset> resourcekey, IMaterial imaterial, ResourceKey<BiomeBase> resourcekey1, Set<ResourceKey<StructureSet>> set, boolean flag, boolean flag1, WorldGenFlatLayerInfo... aworldgenflatlayerinfo) {
            HolderGetter<StructureSet> holdergetter = this.context.lookup(Registries.STRUCTURE_SET);
            HolderGetter<PlacedFeature> holdergetter1 = this.context.lookup(Registries.PLACED_FEATURE);
            HolderGetter<BiomeBase> holdergetter2 = this.context.lookup(Registries.BIOME);
            Stream stream = set.stream();

            Objects.requireNonNull(holdergetter);
            HolderSet.a<StructureSet> holderset_a = HolderSet.direct((List) stream.map(holdergetter::getOrThrow).collect(Collectors.toList()));
            GeneratorSettingsFlat generatorsettingsflat = new GeneratorSettingsFlat(Optional.of(holderset_a), holdergetter2.getOrThrow(resourcekey1), GeneratorSettingsFlat.createLakesList(holdergetter1));

            if (flag) {
                generatorsettingsflat.setDecoration();
            }

            if (flag1) {
                generatorsettingsflat.setAddLakes();
            }

            for (int i = aworldgenflatlayerinfo.length - 1; i >= 0; --i) {
                generatorsettingsflat.getLayersInfo().add(aworldgenflatlayerinfo[i]);
            }

            this.context.register(resourcekey, new FlatLevelGeneratorPreset(imaterial.asItem().builtInRegistryHolder(), generatorsettingsflat));
        }

        public void run() {
            this.register(FlatLevelGeneratorPresets.CLASSIC_FLAT, Blocks.GRASS_BLOCK, Biomes.PLAINS, ImmutableSet.of(BuiltinStructureSets.VILLAGES), false, false, new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK), new WorldGenFlatLayerInfo(2, Blocks.DIRT), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.TUNNELERS_DREAM, Blocks.STONE, Biomes.WINDSWEPT_HILLS, ImmutableSet.of(BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK), new WorldGenFlatLayerInfo(5, Blocks.DIRT), new WorldGenFlatLayerInfo(230, Blocks.STONE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.WATER_WORLD, Items.WATER_BUCKET, Biomes.DEEP_OCEAN, ImmutableSet.of(BuiltinStructureSets.OCEAN_RUINS, BuiltinStructureSets.SHIPWRECKS, BuiltinStructureSets.OCEAN_MONUMENTS), false, false, new WorldGenFlatLayerInfo(90, Blocks.WATER), new WorldGenFlatLayerInfo(5, Blocks.GRAVEL), new WorldGenFlatLayerInfo(5, Blocks.DIRT), new WorldGenFlatLayerInfo(5, Blocks.STONE), new WorldGenFlatLayerInfo(64, Blocks.DEEPSLATE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.OVERWORLD, Blocks.GRASS, Biomes.PLAINS, ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.PILLAGER_OUTPOSTS, BuiltinStructureSets.RUINED_PORTALS, BuiltinStructureSets.STRONGHOLDS), true, true, new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK), new WorldGenFlatLayerInfo(3, Blocks.DIRT), new WorldGenFlatLayerInfo(59, Blocks.STONE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.SNOWY_KINGDOM, Blocks.SNOW, Biomes.SNOWY_PLAINS, ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.IGLOOS), false, false, new WorldGenFlatLayerInfo(1, Blocks.SNOW), new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK), new WorldGenFlatLayerInfo(3, Blocks.DIRT), new WorldGenFlatLayerInfo(59, Blocks.STONE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.BOTTOMLESS_PIT, Items.FEATHER, Biomes.PLAINS, ImmutableSet.of(BuiltinStructureSets.VILLAGES), false, false, new WorldGenFlatLayerInfo(1, Blocks.GRASS_BLOCK), new WorldGenFlatLayerInfo(3, Blocks.DIRT), new WorldGenFlatLayerInfo(2, Blocks.COBBLESTONE));
            this.register(FlatLevelGeneratorPresets.DESERT, Blocks.SAND, Biomes.DESERT, ImmutableSet.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.DESERT_PYRAMIDS, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new WorldGenFlatLayerInfo(8, Blocks.SAND), new WorldGenFlatLayerInfo(52, Blocks.SANDSTONE), new WorldGenFlatLayerInfo(3, Blocks.STONE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.REDSTONE_READY, Items.REDSTONE, Biomes.DESERT, ImmutableSet.of(), false, false, new WorldGenFlatLayerInfo(116, Blocks.SANDSTONE), new WorldGenFlatLayerInfo(3, Blocks.STONE), new WorldGenFlatLayerInfo(1, Blocks.BEDROCK));
            this.register(FlatLevelGeneratorPresets.THE_VOID, Blocks.BARRIER, Biomes.THE_VOID, ImmutableSet.of(), true, false, new WorldGenFlatLayerInfo(1, Blocks.AIR));
        }
    }
}
