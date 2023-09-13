package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDeltaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureFillConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureHellFlowingLavaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureLakeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRadiusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public abstract class WorldGenerator<FC extends WorldGenFeatureConfiguration> {

    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> NO_OP = register("no_op", new WorldGenFeatureEmpty(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureTreeConfiguration> TREE = register("tree", new WorldGenTrees(WorldGenFeatureTreeConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRandomPatchConfiguration> FLOWER = register("flower", new WorldGenFeatureRandomPatch(WorldGenFeatureRandomPatchConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRandomPatchConfiguration> NO_BONEMEAL_FLOWER = register("no_bonemeal_flower", new WorldGenFeatureRandomPatch(WorldGenFeatureRandomPatchConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRandomPatchConfiguration> RANDOM_PATCH = register("random_patch", new WorldGenFeatureRandomPatch(WorldGenFeatureRandomPatchConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureBlockPileConfiguration> BLOCK_PILE = register("block_pile", new WorldGenFeatureBlockPile(WorldGenFeatureBlockPileConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> SPRING = register("spring_feature", new WorldGenLiquids(WorldGenFeatureHellFlowingLavaConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CHORUS_PLANT = register("chorus_plant", new WorldGenFeatureChorusPlant(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> REPLACE_SINGLE_BLOCK = register("replace_single_block", new WorldGenFeatureReplaceBlock(WorldGenFeatureReplaceBlockConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VOID_START_PLATFORM = register("void_start_platform", new WorldGenFeatureEndPlatform(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> DESERT_WELL = register("desert_well", new WorldGenDesertWell(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<FossilFeatureConfiguration> FOSSIL = register("fossil", new WorldGenFossils(FossilFeatureConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_RED_MUSHROOM = register("huge_red_mushroom", new WorldGenHugeMushroomRed(WorldGenFeatureMushroomConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new WorldGenHugeMushroomBrown(WorldGenFeatureMushroomConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ICE_SPIKE = register("ice_spike", new WorldGenPackedIce2(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> GLOWSTONE_BLOB = register("glowstone_blob", new WorldGenLightStone1(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> FREEZE_TOP_LAYER = register("freeze_top_layer", new WorldGenFeatureIceSnow(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VINES = register("vines", new WorldGenVines(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<BlockColumnConfiguration> BLOCK_COLUMN = register("block_column", new BlockColumnFeature(BlockColumnConfiguration.CODEC));
    public static final WorldGenerator<VegetationPatchConfiguration> VEGETATION_PATCH = register("vegetation_patch", new VegetationPatchFeature(VegetationPatchConfiguration.CODEC));
    public static final WorldGenerator<VegetationPatchConfiguration> WATERLOGGED_VEGETATION_PATCH = register("waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC));
    public static final WorldGenerator<RootSystemConfiguration> ROOT_SYSTEM = register("root_system", new RootSystemFeature(RootSystemConfiguration.CODEC));
    public static final WorldGenerator<MultifaceGrowthConfiguration> MULTIFACE_GROWTH = register("multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthConfiguration.CODEC));
    public static final WorldGenerator<UnderwaterMagmaConfiguration> UNDERWATER_MAGMA = register("underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> MONSTER_ROOM = register("monster_room", new WorldGenDungeons(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BLUE_ICE = register("blue_ice", new WorldGenFeatureBlueIce(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> ICEBERG = register("iceberg", new WorldGenFeatureIceburg(WorldGenFeatureLakeConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> FOREST_ROCK = register("forest_rock", new WorldGenTaigaStructure(WorldGenFeatureLakeConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> DISK = register("disk", new DiskFeature(WorldGenFeatureCircleConfiguration.CODEC));
    public static final WorldGenerator<WorldGenLakes.a> LAKE = register("lake", new WorldGenLakes(WorldGenLakes.a.CODEC));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> ORE = register("ore", new WorldGenMinable(WorldGenFeatureOreConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEndSpikeConfiguration> END_SPIKE = register("end_spike", new WorldGenEnder(WorldGenFeatureEndSpikeConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> END_ISLAND = register("end_island", new WorldGenEndIsland(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenEndGatewayConfiguration> END_GATEWAY = register("end_gateway", new WorldGenEndGateway(WorldGenEndGatewayConfiguration.CODEC));
    public static final WorldGenFeatureSeaGrass SEAGRASS = (WorldGenFeatureSeaGrass) register("seagrass", new WorldGenFeatureSeaGrass(WorldGenFeatureConfigurationChance.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> KELP = register("kelp", new WorldGenFeatureKelp(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_TREE = register("coral_tree", new WorldGenFeatureCoralTree(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_MUSHROOM = register("coral_mushroom", new WorldGenFeatureCoralMushroom(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_CLAW = register("coral_claw", new WorldGenFeatureCoralClaw(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenDecoratorFrequencyConfiguration> SEA_PICKLE = register("sea_pickle", new WorldGenFeatureSeaPickel(WorldGenDecoratorFrequencyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureBlockConfiguration> SIMPLE_BLOCK = register("simple_block", new WorldGenFeatureBlock(WorldGenFeatureBlockConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureConfigurationChance> BAMBOO = register("bamboo", new WorldGenFeatureBamboo(WorldGenFeatureConfigurationChance.CODEC));
    public static final WorldGenerator<WorldGenFeatureHugeFungiConfiguration> HUGE_FUNGUS = register("huge_fungus", new WorldGenFeatureHugeFungi(WorldGenFeatureHugeFungiConfiguration.CODEC));
    public static final WorldGenerator<NetherForestVegetationConfig> NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new WorldGenFeatureNetherForestVegetation(NetherForestVegetationConfig.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> WEEPING_VINES = register("weeping_vines", new WorldGenFeatureWeepingVines(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<TwistingVinesConfig> TWISTING_VINES = register("twisting_vines", new WorldGenFeatureTwistingVines(TwistingVinesConfig.CODEC));
    public static final WorldGenerator<WorldGenFeatureBasaltColumnsConfiguration> BASALT_COLUMNS = register("basalt_columns", new WorldGenFeatureBasaltColumns(WorldGenFeatureBasaltColumnsConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureDeltaConfiguration> DELTA_FEATURE = register("delta_feature", new WorldGenFeatureDelta(WorldGenFeatureDeltaConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRadiusConfiguration> REPLACE_BLOBS = register("netherrack_replace_blobs", new WorldGenFeatureNetherrackReplaceBlobs(WorldGenFeatureRadiusConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureFillConfiguration> FILL_LAYER = register("fill_layer", new WorldGenFeatureFill(WorldGenFeatureFillConfiguration.CODEC));
    public static final WorldGenBonusChest BONUS_CHEST = (WorldGenBonusChest) register("bonus_chest", new WorldGenBonusChest(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BASALT_PILLAR = register("basalt_pillar", new WorldGenFeatureBasaltPillar(WorldGenFeatureEmptyConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> SCATTERED_ORE = register("scattered_ore", new ScatteredOreFeature(WorldGenFeatureOreConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> RANDOM_SELECTOR = register("random_selector", new WorldGenFeatureRandomChoice(WorldGenFeatureRandomChoiceConfiguration.CODEC));
    public static final WorldGenerator<WorldGenFeatureRandom2> SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new WorldGenFeatureRandom2Configuration(WorldGenFeatureRandom2.CODEC));
    public static final WorldGenerator<WorldGenFeatureChoiceConfiguration> RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new WorldGenFeatureChoice(WorldGenFeatureChoiceConfiguration.CODEC));
    public static final WorldGenerator<GeodeConfiguration> GEODE = register("geode", new GeodeFeature(GeodeConfiguration.CODEC));
    public static final WorldGenerator<DripstoneClusterConfiguration> DRIPSTONE_CLUSTER = register("dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterConfiguration.CODEC));
    public static final WorldGenerator<LargeDripstoneConfiguration> LARGE_DRIPSTONE = register("large_dripstone", new LargeDripstoneFeature(LargeDripstoneConfiguration.CODEC));
    public static final WorldGenerator<PointedDripstoneConfiguration> POINTED_DRIPSTONE = register("pointed_dripstone", new PointedDripstoneFeature(PointedDripstoneConfiguration.CODEC));
    public static final WorldGenerator<SculkPatchConfiguration> SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchConfiguration.CODEC));
    private final Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> configuredCodec;

    private static <C extends WorldGenFeatureConfiguration, F extends WorldGenerator<C>> F register(String s, F f0) {
        return (WorldGenerator) IRegistry.register(BuiltInRegistries.FEATURE, s, f0);
    }

    public WorldGenerator(Codec<FC> codec) {
        this.configuredCodec = codec.fieldOf("config").xmap((worldgenfeatureconfiguration) -> {
            return new WorldGenFeatureConfigured<>(this, worldgenfeatureconfiguration);
        }, WorldGenFeatureConfigured::config).codec();
    }

    public Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> configuredCodec() {
        return this.configuredCodec;
    }

    protected void setBlock(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setBlock(blockposition, iblockdata, 3);
    }

    public static Predicate<IBlockData> isReplaceable(TagKey<Block> tagkey) {
        return (iblockdata) -> {
            return !iblockdata.is(tagkey);
        };
    }

    protected void safeSetBlock(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, IBlockData iblockdata, Predicate<IBlockData> predicate) {
        if (predicate.test(generatoraccessseed.getBlockState(blockposition))) {
            generatoraccessseed.setBlock(blockposition, iblockdata, 2);
        }

    }

    public abstract boolean place(FeaturePlaceContext<FC> featureplacecontext);

    public boolean place(FC fc, GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RandomSource randomsource, BlockPosition blockposition) {
        return generatoraccessseed.ensureCanWrite(blockposition) ? this.place(new FeaturePlaceContext<>(Optional.empty(), generatoraccessseed, chunkgenerator, randomsource, blockposition, fc)) : false;
    }

    protected static boolean isStone(IBlockData iblockdata) {
        return iblockdata.is(TagsBlock.BASE_STONE_OVERWORLD);
    }

    public static boolean isDirt(IBlockData iblockdata) {
        return iblockdata.is(TagsBlock.DIRT);
    }

    public static boolean isGrassOrDirt(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, WorldGenerator::isDirt);
    }

    public static boolean checkNeighbors(Function<BlockPosition, IBlockData> function, BlockPosition blockposition, Predicate<IBlockData> predicate) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
            if (predicate.test((IBlockData) function.apply(blockposition_mutableblockposition))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAdjacentToAir(Function<BlockPosition, IBlockData> function, BlockPosition blockposition) {
        return checkNeighbors(function, blockposition, BlockBase.BlockData::isAir);
    }

    protected void markAboveForPostProcessing(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i = 0; i < 2; ++i) {
            blockposition_mutableblockposition.move(EnumDirection.UP);
            if (generatoraccessseed.getBlockState(blockposition_mutableblockposition).isAir()) {
                return;
            }

            generatoraccessseed.getChunk(blockposition_mutableblockposition).markPosForPostprocessing(blockposition_mutableblockposition);
        }

    }
}
