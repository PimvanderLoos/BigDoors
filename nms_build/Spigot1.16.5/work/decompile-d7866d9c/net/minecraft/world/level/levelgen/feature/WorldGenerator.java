package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
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

    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> NO_OP = a("no_op", (WorldGenerator) (new WorldGenFeatureEmpty(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureTreeConfiguration> TREE = a("tree", (WorldGenerator) (new WorldGenTrees(WorldGenFeatureTreeConfiguration.a)));
    public static final WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> FLOWER = (WorldGenFlowers) a("flower", (WorldGenerator) (new WorldGenFeatureFlower(WorldGenFeatureRandomPatchConfiguration.a)));
    public static final WorldGenFlowers<WorldGenFeatureRandomPatchConfiguration> NO_BONEMEAL_FLOWER = (WorldGenFlowers) a("no_bonemeal_flower", (WorldGenerator) (new WorldGenFeatureFlower(WorldGenFeatureRandomPatchConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureRandomPatchConfiguration> RANDOM_PATCH = a("random_patch", (WorldGenerator) (new WorldGenFeatureRandomPatch(WorldGenFeatureRandomPatchConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureBlockPileConfiguration> BLOCK_PILE = a("block_pile", (WorldGenerator) (new WorldGenFeatureBlockPile(WorldGenFeatureBlockPileConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> SPRING_FEATURE = a("spring_feature", (WorldGenerator) (new WorldGenLiquids(WorldGenFeatureHellFlowingLavaConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CHORUS_PLANT = a("chorus_plant", (WorldGenerator) (new WorldGenFeatureChorusPlant(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> EMERALD_ORE = a("emerald_ore", (WorldGenerator) (new WorldGenFeatureReplaceBlock(WorldGenFeatureReplaceBlockConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VOID_START_PLATFORM = a("void_start_platform", (WorldGenerator) (new WorldGenFeatureEndPlatform(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> DESERT_WELL = a("desert_well", (WorldGenerator) (new WorldGenDesertWell(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> FOSSIL = a("fossil", (WorldGenerator) (new WorldGenFossils(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_RED_MUSHROOM = a("huge_red_mushroom", (WorldGenerator) (new WorldGenHugeMushroomRed(WorldGenFeatureMushroomConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> HUGE_BROWN_MUSHROOM = a("huge_brown_mushroom", (WorldGenerator) (new WorldGenHugeMushroomBrown(WorldGenFeatureMushroomConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ICE_SPIKE = a("ice_spike", (WorldGenerator) (new WorldGenPackedIce2(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> GLOWSTONE_BLOB = a("glowstone_blob", (WorldGenerator) (new WorldGenLightStone1(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> FREEZE_TOP_LAYER = a("freeze_top_layer", (WorldGenerator) (new WorldGenFeatureIceSnow(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> VINES = a("vines", (WorldGenerator) (new WorldGenVines(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> MONSTER_ROOM = a("monster_room", (WorldGenerator) (new WorldGenDungeons(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BLUE_ICE = a("blue_ice", (WorldGenerator) (new WorldGenFeatureBlueIce(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> ICEBERG = a("iceberg", (WorldGenerator) (new WorldGenFeatureIceburg(WorldGenFeatureLakeConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> FOREST_ROCK = a("forest_rock", (WorldGenerator) (new WorldGenTaigaStructure(WorldGenFeatureLakeConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> DISK = a("disk", (WorldGenerator) (new WorldGenFeatureCircle(WorldGenFeatureCircleConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> ICE_PATCH = a("ice_patch", (WorldGenerator) (new WorldGenPackedIce1(WorldGenFeatureCircleConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> LAKE = a("lake", (WorldGenerator) (new WorldGenLakes(WorldGenFeatureLakeConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> ORE = a("ore", (WorldGenerator) (new WorldGenMinable(WorldGenFeatureOreConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEndSpikeConfiguration> END_SPIKE = a("end_spike", (WorldGenerator) (new WorldGenEnder(WorldGenFeatureEndSpikeConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> END_ISLAND = a("end_island", (WorldGenerator) (new WorldGenEndIsland(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenEndGatewayConfiguration> END_GATEWAY = a("end_gateway", (WorldGenerator) (new WorldGenEndGateway(WorldGenEndGatewayConfiguration.a)));
    public static final WorldGenFeatureSeaGrass SEAGRASS = (WorldGenFeatureSeaGrass) a("seagrass", (WorldGenerator) (new WorldGenFeatureSeaGrass(WorldGenFeatureConfigurationChance.b)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> KELP = a("kelp", (WorldGenerator) (new WorldGenFeatureKelp(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_TREE = a("coral_tree", (WorldGenerator) (new WorldGenFeatureCoralTree(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_MUSHROOM = a("coral_mushroom", (WorldGenerator) (new WorldGenFeatureCoralMushroom(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> CORAL_CLAW = a("coral_claw", (WorldGenerator) (new WorldGenFeatureCoralClaw(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenDecoratorFrequencyConfiguration> SEA_PICKLE = a("sea_pickle", (WorldGenerator) (new WorldGenFeatureSeaPickel(WorldGenDecoratorFrequencyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureBlockConfiguration> SIMPLE_BLOCK = a("simple_block", (WorldGenerator) (new WorldGenFeatureBlock(WorldGenFeatureBlockConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureConfigurationChance> BAMBOO = a("bamboo", (WorldGenerator) (new WorldGenFeatureBamboo(WorldGenFeatureConfigurationChance.b)));
    public static final WorldGenerator<WorldGenFeatureHugeFungiConfiguration> HUGE_FUNGUS = a("huge_fungus", (WorldGenerator) (new WorldGenFeatureHugeFungi(WorldGenFeatureHugeFungiConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureBlockPileConfiguration> NETHER_FOREST_VEGETATION = a("nether_forest_vegetation", (WorldGenerator) (new WorldGenFeatureNetherForestVegetation(WorldGenFeatureBlockPileConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> WEEPING_VINES = a("weeping_vines", (WorldGenerator) (new WorldGenFeatureWeepingVines(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> TWISTING_VINES = a("twisting_vines", (WorldGenerator) (new WorldGenFeatureTwistingVines(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureBasaltColumnsConfiguration> BASALT_COLUMNS = a("basalt_columns", (WorldGenerator) (new WorldGenFeatureBasaltColumns(WorldGenFeatureBasaltColumnsConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureDeltaConfiguration> DELTA_FEATURE = a("delta_feature", (WorldGenerator) (new WorldGenFeatureDelta(WorldGenFeatureDeltaConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureRadiusConfiguration> NETHERRACK_REPLACE_BLOBS = a("netherrack_replace_blobs", (WorldGenerator) (new WorldGenFeatureNetherrackReplaceBlobs(WorldGenFeatureRadiusConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureFillConfiguration> FILL_LAYER = a("fill_layer", (WorldGenerator) (new WorldGenFeatureFill(WorldGenFeatureFillConfiguration.a)));
    public static final WorldGenBonusChest BONUS_CHEST = (WorldGenBonusChest) a("bonus_chest", (WorldGenerator) (new WorldGenBonusChest(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> BASALT_PILLAR = a("basalt_pillar", (WorldGenerator) (new WorldGenFeatureBasaltPillar(WorldGenFeatureEmptyConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> NO_SURFACE_ORE = a("no_surface_ore", (WorldGenerator) (new WorldGenFeatureNoSurfaceOre(WorldGenFeatureOreConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> RANDOM_SELECTOR = a("random_selector", (WorldGenerator) (new WorldGenFeatureRandomChoice(WorldGenFeatureRandomChoiceConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureRandom2> SIMPLE_RANDOM_SELECTOR = a("simple_random_selector", (WorldGenerator) (new WorldGenFeatureRandom2Configuration(WorldGenFeatureRandom2.a)));
    public static final WorldGenerator<WorldGenFeatureChoiceConfiguration> RANDOM_BOOLEAN_SELECTOR = a("random_boolean_selector", (WorldGenerator) (new WorldGenFeatureChoice(WorldGenFeatureChoiceConfiguration.a)));
    public static final WorldGenerator<WorldGenFeatureCompositeConfiguration> DECORATED = a("decorated", (WorldGenerator) (new WorldGenFeatureComposite(WorldGenFeatureCompositeConfiguration.a)));
    private final Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> a;

    private static <C extends WorldGenFeatureConfiguration, F extends WorldGenerator<C>> F a(String s, F f0) {
        return (WorldGenerator) IRegistry.a(IRegistry.FEATURE, s, (Object) f0);
    }

    public WorldGenerator(Codec<FC> codec) {
        this.a = codec.fieldOf("config").xmap((worldgenfeatureconfiguration) -> {
            return new WorldGenFeatureConfigured<>(this, worldgenfeatureconfiguration);
        }, (worldgenfeatureconfigured) -> {
            return worldgenfeatureconfigured.f;
        }).codec();
    }

    public Codec<WorldGenFeatureConfigured<FC, WorldGenerator<FC>>> a() {
        return this.a;
    }

    public WorldGenFeatureConfigured<FC, ?> b(FC fc) {
        return new WorldGenFeatureConfigured<>(this, fc);
    }

    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setTypeAndData(blockposition, iblockdata, 3);
    }

    public abstract boolean generate(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, FC fc);

    protected static boolean a(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
    }

    public static boolean b(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
    }

    public static boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return b(iblockdata.getBlock());
        });
    }

    public static boolean b(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, BlockBase.BlockData::isAir);
    }
}
