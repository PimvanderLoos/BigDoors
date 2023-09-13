package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public abstract class WorldGenerator<C extends WorldGenFeatureConfiguration> {

    private static final List<BiomeBase.BiomeMeta> a = Lists.newArrayList();
    public static final StructureGenerator<WorldGenFeatureVillageConfiguration> e = new WorldGenVillage();
    public static final StructureGenerator<WorldGenMineshaftConfiguration> f = new WorldGenMineshaft();
    public static final StructureGenerator<WorldGenMansionConfiguration> g = new WorldGenWoodlandMansion();
    public static final StructureGenerator<WorldGenFeatureJunglePyramidConfiguration> h = new WorldGenFeatureJunglePyramid();
    public static final StructureGenerator<WorldGenFeatureDesertPyramidConfiguration> i = new WorldGenFeatureDesertPyramid();
    public static final StructureGenerator<WorldGenFeatureIglooConfiguration> j = new WorldGenFeatureIgloo();
    public static final StructureGenerator<WorldGenFeatureShipwreckConfiguration> k = new WorldGenFeatureShipwreck();
    public static final StructureGenerator<WorldGenFeatureSwampHutConfiguration> l = new WorldGenFeatureSwampHut();
    public static final StructureGenerator<WorldGenFeatureStrongholdConfiguration> m = new WorldGenStronghold();
    public static final StructureGenerator<WorldGenMonumentConfiguration> n = new WorldGenMonument();
    public static final StructureGenerator<WorldGenFeatureOceanRuinConfiguration> o = new WorldGenFeatureOceanRuin();
    public static final StructureGenerator<WorldGenNetherConfiguration> p = new WorldGenNether();
    public static final StructureGenerator<WorldGenEndCityConfiguration> q = new WorldGenEndCity();
    public static final StructureGenerator<WorldGenBuriedTreasureConfiguration> r = new WorldGenBuriedTreasure();
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> s = new WorldGenBigTree(false);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> t = new WorldGenForest(false, false);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> u = new WorldGenForest(false, true);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> v = new WorldGenGroundBush(Blocks.JUNGLE_LOG.getBlockData(), Blocks.OAK_LEAVES.getBlockData());
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> w = new WorldGenFeatureJungleTree(false, 4, Blocks.JUNGLE_LOG.getBlockData(), Blocks.JUNGLE_LEAVES.getBlockData(), true);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> x = new WorldGenTaiga1();
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> y = new WorldGenForestTree(false);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> z = new WorldGenAcaciaTree(false);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> A = new WorldGenTaiga2(false);
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> B = new WorldGenSwampTree();
    public static final WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> C = new WorldGenTrees(false);
    public static final WorldGenMegaTreeAbstract<WorldGenFeatureEmptyConfiguration> D = new WorldGenJungleTree(false, 10, 20, Blocks.JUNGLE_LOG.getBlockData(), Blocks.JUNGLE_LEAVES.getBlockData());
    public static final WorldGenMegaTreeAbstract<WorldGenFeatureEmptyConfiguration> E = new WorldGenMegaTree(false, false);
    public static final WorldGenMegaTreeAbstract<WorldGenFeatureEmptyConfiguration> F = new WorldGenMegaTree(false, true);
    public static final WorldGenFlowers G = new WorldGenFeatureFlower();
    public static final WorldGenFlowers H = new WorldGenFeatureFlowerForest();
    public static final WorldGenFlowers I = new WorldGenFeatureFlowerPlain();
    public static final WorldGenFlowers J = new WorldGenFeatureFlowerSwamp();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> K = new WorldGenFeatureJungleGrass();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> L = new WorldGenFeatureTaigaGrass();
    public static final WorldGenerator<WorldGenFeatureTallGrassConfiguration> M = new WorldGenGrass();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> N = new WorldGenFeatureEndPlatform();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> O = new WorldGenCactus();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> P = new WorldGenDeadBush();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> Q = new WorldGenDesertWell();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> R = new WorldGenFossils();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> S = new WorldGenFire();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> T = new WorldGenHugeMushroomRed();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> U = new WorldGenHugeMushroomBrown();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> V = new WorldGenPackedIce2();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> W = new WorldGenLightStone1();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> X = new WorldGenMelon();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> Y = new WorldGenPumpkin();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> Z = new WorldGenReed();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> aa = new WorldGenFeatureIceSnow();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ab = new WorldGenVines();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ac = new WorldGenWaterLily();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ad = new WorldGenDungeons();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> ae = new WorldGenFeatureBlueIce();
    public static final WorldGenerator<WorldGenFeatureIceburgConfiguration> af = new WorldGenFeatureIceburg();
    public static final WorldGenerator<WorldGenFeatureBlockOffsetConfiguration> ag = new WorldGenTaigaStructure();
    public static final WorldGenerator<WorldGenFeatureMushroomConfiguration> ah = new WorldGenMushrooms();
    public static final WorldGenerator<WorldGenFeatureCircleConfiguration> ai = new WorldGenFeatureCircle();
    public static final WorldGenerator<WorldGenFeatureDoublePlantConfiguration> aj = new WorldGenTallPlant();
    public static final WorldGenerator<WorldGenFeatureHellFlowingLavaConfiguration> ak = new WorldGenHellLava();
    public static final WorldGenerator<WorldGenFeatureRadiusConfiguration> al = new WorldGenPackedIce1();
    public static final WorldGenerator<WorldGenFeatureLakeConfiguration> am = new WorldGenLakes();
    public static final WorldGenerator<WorldGenFeatureOreConfiguration> an = new WorldGenMinable();
    public static final WorldGenerator<WorldGenFeatureRandomConfiguration> ao = new WorldGenFeatureRandom();
    public static final WorldGenerator<WorldGenFeatureRandomChoiceConfiguration> ap = new WorldGenFeatureRandomChoice();
    public static final WorldGenerator<WorldGenFeatureRandom2> aq = new WorldGenFeatureRandom2Configuration();
    public static final WorldGenerator<WorldGenFeatureChoiceConfiguration> ar = new WorldGenFeatureChoice();
    public static final WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> as = new WorldGenFeatureReplaceBlock();
    public static final WorldGenerator<WorldGenFeatureFlowingConfiguration> at = new WorldGenLiquids();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> au = new WorldGenEnder();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> av = new WorldGenEndIsland();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> aw = new WorldGenFeatureChorusPlant();
    public static final WorldGenerator<WorldGenEndGatewayConfiguration> ax = new WorldGenEndGateway();
    public static final WorldGenerator<WorldGenFeatureSeaGrassConfiguration> ay = new WorldGenFeatureSeaGrass();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> az = new WorldGenFeatureKelp();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> aA = new WorldGenFeatureCoralTree();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> aB = new WorldGenFeatureCoralMushroom();
    public static final WorldGenerator<WorldGenFeatureEmptyConfiguration> aC = new WorldGenFeatureCoralClaw();
    public static final WorldGenerator<WorldGenFeatureKelpConfiguration> aD = new WorldGenFeatureSeaPickel();
    public static final WorldGenerator<WorldGenFeatureBlockConfiguration> aE = new WorldGenFeatureBlock();
    public static final Map<String, StructureGenerator<?>> aF = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put("Village".toLowerCase(Locale.ROOT), WorldGenerator.e);
        hashmap.put("Mineshaft".toLowerCase(Locale.ROOT), WorldGenerator.f);
        hashmap.put("Mansion".toLowerCase(Locale.ROOT), WorldGenerator.g);
        hashmap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), WorldGenerator.h);
        hashmap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), WorldGenerator.i);
        hashmap.put("Igloo".toLowerCase(Locale.ROOT), WorldGenerator.j);
        hashmap.put("Shipwreck".toLowerCase(Locale.ROOT), WorldGenerator.k);
        hashmap.put("Swamp_Hut".toLowerCase(Locale.ROOT), WorldGenerator.l);
        hashmap.put("Stronghold".toLowerCase(Locale.ROOT), WorldGenerator.m);
        hashmap.put("Monument".toLowerCase(Locale.ROOT), WorldGenerator.n);
        hashmap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), WorldGenerator.o);
        hashmap.put("Fortress".toLowerCase(Locale.ROOT), WorldGenerator.p);
        hashmap.put("EndCity".toLowerCase(Locale.ROOT), WorldGenerator.q);
        hashmap.put("Buried_Treasure".toLowerCase(Locale.ROOT), WorldGenerator.r);
    });
    protected final boolean aG;

    public WorldGenerator() {
        this(false);
    }

    public WorldGenerator(boolean flag) {
        this.aG = flag;
    }

    public abstract boolean generate(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, C c0);

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.aG) {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
        } else {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
        }

    }

    public List<BiomeBase.BiomeMeta> d() {
        return WorldGenerator.a;
    }

    public static boolean a(GeneratorAccess generatoraccess, String s, BlockPosition blockposition) {
        return ((StructureGenerator) WorldGenerator.aF.get(s.toLowerCase(Locale.ROOT))).c(generatoraccess, blockposition);
    }
}
