package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GrowingPlantConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SmallDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBasaltColumnsConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCompositeConfiguration;
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

    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> NO_OP = a("no_op", (WorldGenerator) (new WorldGenFeatureEmpty(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureTreeConfiguration> TREE = a("tree", (WorldGenerator) (new WorldGenTrees(WorldGenFeatureTreeConfiguration.CODEC)));
    public static final WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> FLOWER = (WorldGenFlowers) a("flower", (WorldGenerator) (new WorldGenFeatureFlower(WorldGenFeatureRandomPatchConfiguration.CODEC)));
    public static final WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> NO_BONEMEAL_FLOWER = (WorldGenFlowers) a("no_bonemeal_flower", (WorldGenerator) (new WorldGenFeatureFlower(WorldGenFeatureRandomPatchConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureRandomPatchConfiguration> RANDOM_PATCH = a("random_patch", (WorldGenerator) (new WorldGenFeatureRandomPatch(WorldGenFeatureRandomPatchConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureBlockPileConfiguration> BLOCK_PILE = a("block_pile", (WorldGenerator) (new WorldGenFeatureBlockPile(WorldGenFeatureBlockPileConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> SPRING = a("spring_feature", (WorldGenerator) (new WorldGenLiquids(WorldGenFeatureHellFlowingLavaConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CHORUS_PLANT = a("chorus_plant", (WorldGenerator) (new WorldGenFeatureChorusPlant(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> REPLACE_SINGLE_BLOCK = a("replace_single_block", (WorldGenerator) (new WorldGenFeatureReplaceBlock(WorldGenFeatureReplaceBlockConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VOID_START_PLATFORM = a("void_start_platform", (WorldGenerator) (new WorldGenFeatureEndPlatform(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> DESERT_WELL = a("desert_well", (WorldGenerator) (new WorldGenDesertWell(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<FossilFeatureConfiguration> FOSSIL = a("fossil", (WorldGenerator) (new WorldGenFossils(FossilFeatureConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_RED_MUSHROOM = a("huge_red_mushroom", (WorldGenerator) (new WorldGenHugeMushroomRed(WorldGenFeatureMushroomConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_BROWN_MUSHROOM = a("huge_brown_mushroom", (WorldGenerator) (new WorldGenHugeMushroomBrown(WorldGenFeatureMushroomConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ICE_SPIKE = a("ice_spike", (WorldGenerator) (new WorldGenPackedIce2(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> GLOWSTONE_BLOB = a("glowstone_blob", (WorldGenerator) (new WorldGenLightStone1(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> FREEZE_TOP_LAYER = a("freeze_top_layer", (WorldGenerator) (new WorldGenFeatureIceSnow(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VINES = a("vines", (WorldGenerator) (new WorldGenVines(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<GrowingPlantConfiguration> GROWING_PLANT = a("growing_plant", (WorldGenerator) (new GrowingPlantFeature(GrowingPlantConfiguration.CODEC)));
    public static final WorldGenerator<VegetationPatchConfiguration> VEGETATION_PATCH = a("vegetation_patch", (WorldGenerator) (new VegetationPatchFeature(VegetationPatchConfiguration.CODEC)));
    public static final WorldGenerator<VegetationPatchConfiguration> WATERLOGGED_VEGETATION_PATCH = a("waterlogged_vegetation_patch", (WorldGenerator) (new WaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC)));
    public static final WorldGenerator<RootSystemConfiguration> ROOT_SYSTEM = a("root_system", (WorldGenerator) (new RootSystemFeature(RootSystemConfiguration.CODEC)));
    public static final WorldGenerator<GlowLichenConfiguration> GLOW_LICHEN = a("glow_lichen", (WorldGenerator) (new GlowLichenFeature(GlowLichenConfiguration.CODEC)));
    public static final WorldGenerator<UnderwaterMagmaConfiguration> UNDERWATER_MAGMA = a("underwater_magma", (WorldGenerator) (new UnderwaterMagmaFeature(UnderwaterMagmaConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> MONSTER_ROOM = a("monster_room", (WorldGenerator) (new WorldGenDungeons(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BLUE_ICE = a("blue_ice", (WorldGenerator) (new WorldGenFeatureBlueIce(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> ICEBERG = a("iceberg", (WorldGenerator) (new WorldGenFeatureIceburg(WorldGenFeatureLakeConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> FOREST_ROCK = a("forest_rock", (WorldGenerator) (new WorldGenTaigaStructure(WorldGenFeatureLakeConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> DISK = a("disk", (WorldGenerator) (new WorldGenFeatureCircle(WorldGenFeatureCircleConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> ICE_PATCH = a("ice_patch", (WorldGenerator) (new WorldGenPackedIce1(WorldGenFeatureCircleConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> LAKE = a("lake", (WorldGenerator) (new WorldGenLakes(WorldGenFeatureLakeConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> ORE = a("ore", (WorldGenerator) (new WorldGenMinable(WorldGenFeatureOreConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEndSpikeConfiguration> END_SPIKE = a("end_spike", (WorldGenerator) (new WorldGenEnder(WorldGenFeatureEndSpikeConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> END_ISLAND = a("end_island", (WorldGenerator) (new WorldGenEndIsland(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenEndGatewayConfiguration> END_GATEWAY = a("end_gateway", (WorldGenerator) (new WorldGenEndGateway(WorldGenEndGatewayConfiguration.CODEC)));
    public static final WorldGenFeatureSeaGrass SEAGRASS = (WorldGenFeatureSeaGrass) a("seagrass", (WorldGenerator) (new WorldGenFeatureSeaGrass(WorldGenFeatureConfigurationChance.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> KELP = a("kelp", (WorldGenerator) (new WorldGenFeatureKelp(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_TREE = a("coral_tree", (WorldGenerator) (new WorldGenFeatureCoralTree(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_MUSHROOM = a("coral_mushroom", (WorldGenerator) (new WorldGenFeatureCoralMushroom(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_CLAW = a("coral_claw", (WorldGenerator) (new WorldGenFeatureCoralClaw(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenDecoratorFrequencyConfiguration> SEA_PICKLE = a("sea_pickle", (WorldGenerator) (new WorldGenFeatureSeaPickel(WorldGenDecoratorFrequencyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureBlockConfiguration> SIMPLE_BLOCK = a("simple_block", (WorldGenerator) (new WorldGenFeatureBlock(WorldGenFeatureBlockConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureConfigurationChance> BAMBOO = a("bamboo", (WorldGenerator) (new WorldGenFeatureBamboo(WorldGenFeatureConfigurationChance.CODEC)));
    public static final WorldGenerator<WorldGenFeatureHugeFungiConfiguration> HUGE_FUNGUS = a("huge_fungus", (WorldGenerator) (new WorldGenFeatureHugeFungi(WorldGenFeatureHugeFungiConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureBlockPileConfiguration> NETHER_FOREST_VEGETATION = a("nether_forest_vegetation", (WorldGenerator) (new WorldGenFeatureNetherForestVegetation(WorldGenFeatureBlockPileConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> WEEPING_VINES = a("weeping_vines", (WorldGenerator) (new WorldGenFeatureWeepingVines(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> TWISTING_VINES = a("twisting_vines", (WorldGenerator) (new WorldGenFeatureTwistingVines(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureBasaltColumnsConfiguration> BASALT_COLUMNS = a("basalt_columns", (WorldGenerator) (new WorldGenFeatureBasaltColumns(WorldGenFeatureBasaltColumnsConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureDeltaConfiguration> DELTA_FEATURE = a("delta_feature", (WorldGenerator) (new WorldGenFeatureDelta(WorldGenFeatureDeltaConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureRadiusConfiguration> REPLACE_BLOBS = a("netherrack_replace_blobs", (WorldGenerator) (new WorldGenFeatureNetherrackReplaceBlobs(WorldGenFeatureRadiusConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureFillConfiguration> FILL_LAYER = a("fill_layer", (WorldGenerator) (new WorldGenFeatureFill(WorldGenFeatureFillConfiguration.CODEC)));
    public static final WorldGenBonusChest BONUS_CHEST = (WorldGenBonusChest) a("bonus_chest", (WorldGenerator) (new WorldGenBonusChest(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BASALT_PILLAR = a("basalt_pillar", (WorldGenerator) (new WorldGenFeatureBasaltPillar(WorldGenFeatureEmptyConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> SCATTERED_ORE = a("scattered_ore", (WorldGenerator) (new ScatteredOreFeature(WorldGenFeatureOreConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> RANDOM_SELECTOR = a("random_selector", (WorldGenerator) (new WorldGenFeatureRandomChoice(WorldGenFeatureRandomChoiceConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureRandom2> SIMPLE_RANDOM_SELECTOR = a("simple_random_selector", (WorldGenerator) (new WorldGenFeatureRandom2Configuration(WorldGenFeatureRandom2.CODEC)));
    public static final WorldGenerator<WorldGenFeatureChoiceConfiguration> RANDOM_BOOLEAN_SELECTOR = a("random_boolean_selector", (WorldGenerator) (new WorldGenFeatureChoice(WorldGenFeatureChoiceConfiguration.CODEC)));
    public static final WorldGenerator<WorldGenFeatureCompositeConfiguration> DECORATED = a("decorated", (WorldGenerator) (new WorldGenFeatureComposite(WorldGenFeatureCompositeConfiguration.CODEC)));
    public static final WorldGenerator<GeodeConfiguration> GEODE = a("geode", (WorldGenerator) (new GeodeFeature(GeodeConfiguration.CODEC)));
    public static final WorldGenerator<DripstoneClusterConfiguration> DRIPSTONE_CLUSTER = a("dripstone_cluster", (WorldGenerator) (new DripstoneClusterFeature(DripstoneClusterConfiguration.CODEC)));
    public static final WorldGenerator<LargeDripstoneConfiguration> LARGE_DRIPSTONE = a("large_dripstone", (WorldGenerator) (new LargeDripstoneFeature(LargeDripstoneConfiguration.CODEC)));
    public static final WorldGenerator<SmallDripstoneConfiguration> SMALL_DRIPSTONE = a("small_dripstone", (WorldGenerator) (new SmallDripstoneFeature(SmallDripstoneConfiguration.CODEC)));
    private final Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> configuredCodec;

    private static <C extends WorldGenFeatureConfiguration, F extends WorldGenerator<C>> F a(String s, F f0) {
        return (WorldGenerator) IRegistry.a(IRegistry.FEATURE, s, (Object) f0);
    }

    public WorldGenerator(Codec<FC> codec) {
        this.configuredCodec = codec.fieldOf("config").xmap((worldgenfeatureconfiguration) -> {
            return new WorldGenFeatureConfigured<>(this, worldgenfeatureconfiguration);
        }, (worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.config;
        }).codec();
    }

    public Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> a() {
        return this.configuredCodec;
    }

    public WorldGenFeatureConfigured<FC, ?> b(FC fc) {
        return new WorldGenFeatureConfigured<>(this, fc);
    }

    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setTypeAndData(blockposition, iblockdata, 3);
    }

    public static Predicate<IBlockData> a(MinecraftKey minecraftkey) {
        Tag<Block> tag = TagsBlock.a().a(minecraftkey);

        return tag == null ? (iblockdata) -> {
            return true;
        } : (iblockdata) -> {
            return !iblockdata.a(tag);
        };
    }

    protected void a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, IBlockData iblockdata, Predicate<IBlockData> predicate) {
        if (predicate.test(generatoraccessseed.getType(blockposition))) {
            generatoraccessseed.setTypeAndData(blockposition, iblockdata, 2);
        }

    }

    public abstract boolean generate(FeaturePlaceContext<FC> featureplacecontext);

    protected static boolean a(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.BASE_STONE_OVERWORLD);
    }

    public static boolean b(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.DIRT);
    }

    public static boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, WorldGenerator::b);
    }

    public static boolean b(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, BlockBase.BlockData::isAir);
    }

    public static boolean a(Function<BlockPosition, IBlockData> function, BlockPosition blockposition, Predicate<IBlockData> predicate) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
            if (predicate.test((IBlockData) function.apply(blockposition_mutableblockposition))) {
                return true;
            }
        }

        return false;
    }

    public static boolean a(Function<BlockPosition, IBlockData> function, BlockPosition blockposition) {
        return a(function, blockposition, BlockBase.BlockData::isAir);
    }

    protected void a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 0; i < 2; ++i) {
            blockposition_mutableblockposition.c(EnumDirection.UP);
            if (generatoraccessseed.getType(blockposition_mutableblockposition).isAir()) {
                return;
            }

            generatoraccessseed.A(blockposition_mutableblockposition).e(blockposition_mutableblockposition);
        }

    }
}
