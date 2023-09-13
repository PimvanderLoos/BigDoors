package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeBase {

    private static final Logger x = LogManager.getLogger();
    protected static final IBlockData a = Blocks.STONE.getBlockData();
    protected static final IBlockData b = Blocks.AIR.getBlockData();
    protected static final IBlockData c = Blocks.BEDROCK.getBlockData();
    protected static final IBlockData d = Blocks.GRAVEL.getBlockData();
    protected static final IBlockData e = Blocks.RED_SANDSTONE.getBlockData();
    protected static final IBlockData f = Blocks.SANDSTONE.getBlockData();
    protected static final IBlockData g = Blocks.ICE.getBlockData();
    protected static final IBlockData h = Blocks.WATER.getBlockData();
    public static final RegistryBlockID<BiomeBase> i = new RegistryBlockID();
    protected static final NoiseGenerator3 j = new NoiseGenerator3(new Random(1234L), 1);
    protected static final NoiseGenerator3 k = new NoiseGenerator3(new Random(2345L), 1);
    protected static final WorldGenTallPlant l = new WorldGenTallPlant();
    protected static final WorldGenTrees m = new WorldGenTrees(false);
    protected static final WorldGenBigTree n = new WorldGenBigTree(false);
    protected static final WorldGenSwampTree o = new WorldGenSwampTree();
    public static final RegistryMaterials<MinecraftKey, BiomeBase> REGISTRY_ID = new RegistryMaterials();
    private final String y;
    private final float z;
    private final float A;
    private final float B;
    private final float C;
    private final int D;
    private final boolean E;
    private final boolean F;
    @Nullable
    private final String G;
    public IBlockData q;
    public IBlockData r;
    public BiomeDecorator s;
    protected List<BiomeBase.BiomeMeta> t;
    protected List<BiomeBase.BiomeMeta> u;
    protected List<BiomeBase.BiomeMeta> v;
    protected List<BiomeBase.BiomeMeta> w;

    public static int a(BiomeBase biomebase) {
        return BiomeBase.REGISTRY_ID.a((Object) biomebase);
    }

    @Nullable
    public static BiomeBase a(int i) {
        return (BiomeBase) BiomeBase.REGISTRY_ID.getId(i);
    }

    @Nullable
    public static BiomeBase b(BiomeBase biomebase) {
        return (BiomeBase) BiomeBase.i.fromId(a(biomebase));
    }

    protected BiomeBase(BiomeBase.a biomebase_a) {
        this.q = Blocks.GRASS.getBlockData();
        this.r = Blocks.DIRT.getBlockData();
        this.t = Lists.newArrayList();
        this.u = Lists.newArrayList();
        this.v = Lists.newArrayList();
        this.w = Lists.newArrayList();
        this.y = biomebase_a.a;
        this.z = biomebase_a.b;
        this.A = biomebase_a.c;
        this.B = biomebase_a.d;
        this.C = biomebase_a.e;
        this.D = biomebase_a.f;
        this.E = biomebase_a.g;
        this.F = biomebase_a.h;
        this.G = biomebase_a.i;
        this.s = this.a();
        this.u.add(new BiomeBase.BiomeMeta(EntitySheep.class, 12, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityPig.class, 10, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityChicken.class, 10, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityCow.class, 8, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntitySpider.class, 100, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityZombie.class, 95, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityZombieVillager.class, 5, 1, 1));
        this.t.add(new BiomeBase.BiomeMeta(EntitySkeleton.class, 100, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityCreeper.class, 100, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntitySlime.class, 100, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityEnderman.class, 10, 1, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityWitch.class, 5, 1, 1));
        this.v.add(new BiomeBase.BiomeMeta(EntitySquid.class, 10, 4, 4));
        this.w.add(new BiomeBase.BiomeMeta(EntityBat.class, 10, 8, 8));
    }

    protected BiomeDecorator a() {
        return new BiomeDecorator();
    }

    public boolean b() {
        return this.G != null;
    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (random.nextInt(10) == 0 ? BiomeBase.n : BiomeBase.m);
    }

    public WorldGenerator b(Random random) {
        return new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
    }

    public BlockFlowers.EnumFlowerVarient a(Random random, BlockPosition blockposition) {
        return random.nextInt(3) > 0 ? BlockFlowers.EnumFlowerVarient.DANDELION : BlockFlowers.EnumFlowerVarient.POPPY;
    }

    public List<BiomeBase.BiomeMeta> getMobs(EnumCreatureType enumcreaturetype) {
        switch (enumcreaturetype) {
        case MONSTER:
            return this.t;

        case CREATURE:
            return this.u;

        case WATER_CREATURE:
            return this.v;

        case AMBIENT:
            return this.w;

        default:
            return Collections.emptyList();
        }
    }

    public boolean c() {
        return this.p();
    }

    public boolean d() {
        return this.p() ? false : this.F;
    }

    public boolean e() {
        return this.getHumidity() > 0.85F;
    }

    public float f() {
        return 0.1F;
    }

    public final float a(BlockPosition blockposition) {
        if (blockposition.getY() > 64) {
            float f = (float) (BiomeBase.j.a((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F)) * 4.0D);

            return this.getTemperature() - (f + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.getTemperature();
        }
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        this.s.a(world, random, this, blockposition);
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public final void b(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        int k = world.getSeaLevel();
        IBlockData iblockdata = this.q;
        IBlockData iblockdata1 = this.r;
        int l = -1;
        int i1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int j1 = i & 15;
        int k1 = j & 15;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l1 = 255; l1 >= 0; --l1) {
            if (l1 <= random.nextInt(5)) {
                chunksnapshot.a(k1, l1, j1, BiomeBase.c);
            } else {
                IBlockData iblockdata2 = chunksnapshot.a(k1, l1, j1);

                if (iblockdata2.getMaterial() == Material.AIR) {
                    l = -1;
                } else if (iblockdata2.getBlock() == Blocks.STONE) {
                    if (l == -1) {
                        if (i1 <= 0) {
                            iblockdata = BiomeBase.b;
                            iblockdata1 = BiomeBase.a;
                        } else if (l1 >= k - 4 && l1 <= k + 1) {
                            iblockdata = this.q;
                            iblockdata1 = this.r;
                        }

                        if (l1 < k && (iblockdata == null || iblockdata.getMaterial() == Material.AIR)) {
                            if (this.a((BlockPosition) blockposition_mutableblockposition.c(i, l1, j)) < 0.15F) {
                                iblockdata = BiomeBase.g;
                            } else {
                                iblockdata = BiomeBase.h;
                            }
                        }

                        l = i1;
                        if (l1 >= k - 1) {
                            chunksnapshot.a(k1, l1, j1, iblockdata);
                        } else if (l1 < k - 7 - i1) {
                            iblockdata = BiomeBase.b;
                            iblockdata1 = BiomeBase.a;
                            chunksnapshot.a(k1, l1, j1, BiomeBase.d);
                        } else {
                            chunksnapshot.a(k1, l1, j1, iblockdata1);
                        }
                    } else if (l > 0) {
                        --l;
                        chunksnapshot.a(k1, l1, j1, iblockdata1);
                        if (l == 0 && iblockdata1.getBlock() == Blocks.SAND && i1 > 1) {
                            l = random.nextInt(4) + Math.max(0, l1 - 63);
                            iblockdata1 = iblockdata1.get(BlockSand.VARIANT) == BlockSand.EnumSandVariant.RED_SAND ? BiomeBase.e : BiomeBase.f;
                        }
                    }
                }
            }
        }

    }

    public Class<? extends BiomeBase> g() {
        return this.getClass();
    }

    public BiomeBase.EnumTemperature h() {
        return (double) this.getTemperature() < 0.2D ? BiomeBase.EnumTemperature.COLD : ((double) this.getTemperature() < 1.0D ? BiomeBase.EnumTemperature.MEDIUM : BiomeBase.EnumTemperature.WARM);
    }

    @Nullable
    public static BiomeBase getBiome(int i) {
        return getBiome(i, (BiomeBase) null);
    }

    public static BiomeBase getBiome(int i, BiomeBase biomebase) {
        BiomeBase biomebase1 = a(i);

        return biomebase1 == null ? biomebase : biomebase1;
    }

    public boolean i() {
        return false;
    }

    public final float j() {
        return this.z;
    }

    public final float getHumidity() {
        return this.C;
    }

    public final float m() {
        return this.A;
    }

    public final float getTemperature() {
        return this.B;
    }

    public final boolean p() {
        return this.E;
    }

    public static void q() {
        a(0, "ocean", new BiomeOcean((new BiomeBase.a("Ocean")).c(-1.0F).d(0.1F)));
        a(1, "plains", new BiomePlains(false, (new BiomeBase.a("Plains")).c(0.125F).d(0.05F).a(0.8F).b(0.4F)));
        a(2, "desert", new BiomeDesert((new BiomeBase.a("Desert")).c(0.125F).d(0.05F).a(2.0F).b(0.0F).a()));
        a(3, "extreme_hills", new BiomeBigHills(BiomeBigHills.Type.NORMAL, (new BiomeBase.a("Extreme Hills")).c(1.0F).d(0.5F).a(0.2F).b(0.3F)));
        a(4, "forest", new BiomeForest(BiomeForest.Type.NORMAL, (new BiomeBase.a("Forest")).a(0.7F).b(0.8F)));
        a(5, "taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("Taiga")).c(0.2F).d(0.2F).a(0.25F).b(0.8F)));
        a(6, "swampland", new BiomeSwamp((new BiomeBase.a("Swampland")).c(-0.2F).d(0.1F).a(0.8F).b(0.9F).a(14745518)));
        a(7, "river", new BiomeRiver((new BiomeBase.a("River")).c(-0.5F).d(0.0F)));
        a(8, "hell", new BiomeHell((new BiomeBase.a("Hell")).a(2.0F).b(0.0F).a()));
        a(9, "sky", new BiomeTheEnd((new BiomeBase.a("The End")).a()));
        a(10, "frozen_ocean", new BiomeOcean((new BiomeBase.a("FrozenOcean")).c(-1.0F).d(0.1F).a(0.0F).b(0.5F).b()));
        a(11, "frozen_river", new BiomeRiver((new BiomeBase.a("FrozenRiver")).c(-0.5F).d(0.0F).a(0.0F).b(0.5F).b()));
        a(12, "ice_flats", new BiomeIcePlains(false, (new BiomeBase.a("Ice Plains")).c(0.125F).d(0.05F).a(0.0F).b(0.5F).b()));
        a(13, "ice_mountains", new BiomeIcePlains(false, (new BiomeBase.a("Ice Mountains")).c(0.45F).d(0.3F).a(0.0F).b(0.5F).b()));
        a(14, "mushroom_island", new BiomeMushrooms((new BiomeBase.a("MushroomIsland")).c(0.2F).d(0.3F).a(0.9F).b(1.0F)));
        a(15, "mushroom_island_shore", new BiomeMushrooms((new BiomeBase.a("MushroomIslandShore")).c(0.0F).d(0.025F).a(0.9F).b(1.0F)));
        a(16, "beaches", new BiomeBeach((new BiomeBase.a("Beach")).c(0.0F).d(0.025F).a(0.8F).b(0.4F)));
        a(17, "desert_hills", new BiomeDesert((new BiomeBase.a("DesertHills")).c(0.45F).d(0.3F).a(2.0F).b(0.0F).a()));
        a(18, "forest_hills", new BiomeForest(BiomeForest.Type.NORMAL, (new BiomeBase.a("ForestHills")).c(0.45F).d(0.3F).a(0.7F).b(0.8F)));
        a(19, "taiga_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("TaigaHills")).a(0.25F).b(0.8F).c(0.45F).d(0.3F)));
        a(20, "smaller_extreme_hills", new BiomeBigHills(BiomeBigHills.Type.EXTRA_TREES, (new BiomeBase.a("Extreme Hills Edge")).c(0.8F).d(0.3F).a(0.2F).b(0.3F)));
        a(21, "jungle", new BiomeJungle(false, (new BiomeBase.a("Jungle")).a(0.95F).b(0.9F)));
        a(22, "jungle_hills", new BiomeJungle(false, (new BiomeBase.a("JungleHills")).c(0.45F).d(0.3F).a(0.95F).b(0.9F)));
        a(23, "jungle_edge", new BiomeJungle(true, (new BiomeBase.a("JungleEdge")).a(0.95F).b(0.8F)));
        a(24, "deep_ocean", new BiomeOcean((new BiomeBase.a("Deep Ocean")).c(-1.8F).d(0.1F)));
        a(25, "stone_beach", new BiomeStoneBeach((new BiomeBase.a("Stone Beach")).c(0.1F).d(0.8F).a(0.2F).b(0.3F)));
        a(26, "cold_beach", new BiomeBeach((new BiomeBase.a("Cold Beach")).c(0.0F).d(0.025F).a(0.05F).b(0.3F).b()));
        a(27, "birch_forest", new BiomeForest(BiomeForest.Type.BIRCH, (new BiomeBase.a("Birch Forest")).a(0.6F).b(0.6F)));
        a(28, "birch_forest_hills", new BiomeForest(BiomeForest.Type.BIRCH, (new BiomeBase.a("Birch Forest Hills")).c(0.45F).d(0.3F).a(0.6F).b(0.6F)));
        a(29, "roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new BiomeBase.a("Roofed Forest")).a(0.7F).b(0.8F)));
        a(30, "taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("Cold Taiga")).c(0.2F).d(0.2F).a(-0.5F).b(0.4F).b()));
        a(31, "taiga_cold_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("Cold Taiga Hills")).c(0.45F).d(0.3F).a(-0.5F).b(0.4F).b()));
        a(32, "redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new BiomeBase.a("Mega Taiga")).a(0.3F).b(0.8F).c(0.2F).d(0.2F)));
        a(33, "redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new BiomeBase.a("Mega Taiga Hills")).c(0.45F).d(0.3F).a(0.3F).b(0.8F)));
        a(34, "extreme_hills_with_trees", new BiomeBigHills(BiomeBigHills.Type.EXTRA_TREES, (new BiomeBase.a("Extreme Hills+")).c(1.0F).d(0.5F).a(0.2F).b(0.3F)));
        a(35, "savanna", new BiomeSavanna((new BiomeBase.a("Savanna")).c(0.125F).d(0.05F).a(1.2F).b(0.0F).a()));
        a(36, "savanna_rock", new BiomeSavanna((new BiomeBase.a("Savanna Plateau")).c(1.5F).d(0.025F).a(1.0F).b(0.0F).a()));
        a(37, "mesa", new BiomeMesa(false, false, (new BiomeBase.a("Mesa")).a(2.0F).b(0.0F).a()));
        a(38, "mesa_rock", new BiomeMesa(false, true, (new BiomeBase.a("Mesa Plateau F")).c(1.5F).d(0.025F).a(2.0F).b(0.0F).a()));
        a(39, "mesa_clear_rock", new BiomeMesa(false, false, (new BiomeBase.a("Mesa Plateau")).c(1.5F).d(0.025F).a(2.0F).b(0.0F).a()));
        a(127, "void", new BiomeVoid((new BiomeBase.a("The Void")).a()));
        a(129, "mutated_plains", new BiomePlains(true, (new BiomeBase.a("Sunflower Plains")).a("plains").c(0.125F).d(0.05F).a(0.8F).b(0.4F)));
        a(130, "mutated_desert", new BiomeDesert((new BiomeBase.a("Desert M")).a("desert").c(0.225F).d(0.25F).a(2.0F).b(0.0F).a()));
        a(131, "mutated_extreme_hills", new BiomeBigHills(BiomeBigHills.Type.MUTATED, (new BiomeBase.a("Extreme Hills M")).a("extreme_hills").c(1.0F).d(0.5F).a(0.2F).b(0.3F)));
        a(132, "mutated_forest", new BiomeForest(BiomeForest.Type.FLOWER, (new BiomeBase.a("Flower Forest")).a("forest").d(0.4F).a(0.7F).b(0.8F)));
        a(133, "mutated_taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("Taiga M")).a("taiga").c(0.3F).d(0.4F).a(0.25F).b(0.8F)));
        a(134, "mutated_swampland", new BiomeSwamp((new BiomeBase.a("Swampland M")).a("swampland").c(-0.1F).d(0.3F).a(0.8F).b(0.9F).a(14745518)));
        a(140, "mutated_ice_flats", new BiomeIcePlains(true, (new BiomeBase.a("Ice Plains Spikes")).a("ice_flats").c(0.425F).d(0.45000002F).a(0.0F).b(0.5F).b()));
        a(149, "mutated_jungle", new BiomeJungle(false, (new BiomeBase.a("Jungle M")).a("jungle").c(0.2F).d(0.4F).a(0.95F).b(0.9F)));
        a(151, "mutated_jungle_edge", new BiomeJungle(true, (new BiomeBase.a("JungleEdge M")).a("jungle_edge").c(0.2F).d(0.4F).a(0.95F).b(0.8F)));
        a(155, "mutated_birch_forest", new BiomeForestMutated((new BiomeBase.a("Birch Forest M")).a("birch_forest").c(0.2F).d(0.4F).a(0.6F).b(0.6F)));
        a(156, "mutated_birch_forest_hills", new BiomeForestMutated((new BiomeBase.a("Birch Forest Hills M")).a("birch_forest_hills").c(0.55F).d(0.5F).a(0.6F).b(0.6F)));
        a(157, "mutated_roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new BiomeBase.a("Roofed Forest M")).a("roofed_forest").c(0.2F).d(0.4F).a(0.7F).b(0.8F)));
        a(158, "mutated_taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new BiomeBase.a("Cold Taiga M")).a("taiga_cold").c(0.3F).d(0.4F).a(-0.5F).b(0.4F).b()));
        a(160, "mutated_redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new BiomeBase.a("Mega Spruce Taiga")).a("redwood_taiga").c(0.2F).d(0.2F).a(0.25F).b(0.8F)));
        a(161, "mutated_redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new BiomeBase.a("Redwood Taiga Hills M")).a("redwood_taiga_hills").c(0.2F).d(0.2F).a(0.25F).b(0.8F)));
        a(162, "mutated_extreme_hills_with_trees", new BiomeBigHills(BiomeBigHills.Type.MUTATED, (new BiomeBase.a("Extreme Hills+ M")).a("extreme_hills_with_trees").c(1.0F).d(0.5F).a(0.2F).b(0.3F)));
        a(163, "mutated_savanna", new BiomeSavannaMutated((new BiomeBase.a("Savanna M")).a("savanna").c(0.3625F).d(1.225F).a(1.1F).b(0.0F).a()));
        a(164, "mutated_savanna_rock", new BiomeSavannaMutated((new BiomeBase.a("Savanna Plateau M")).a("savanna_rock").c(1.05F).d(1.2125001F).a(1.0F).b(0.0F).a()));
        a(165, "mutated_mesa", new BiomeMesa(true, false, (new BiomeBase.a("Mesa (Bryce)")).a("mesa").a(2.0F).b(0.0F).a()));
        a(166, "mutated_mesa_rock", new BiomeMesa(false, true, (new BiomeBase.a("Mesa Plateau F M")).a("mesa_rock").c(0.45F).d(0.3F).a(2.0F).b(0.0F).a()));
        a(167, "mutated_mesa_clear_rock", new BiomeMesa(false, false, (new BiomeBase.a("Mesa Plateau M")).a("mesa_clear_rock").c(0.45F).d(0.3F).a(2.0F).b(0.0F).a()));
    }

    private static void a(int i, String s, BiomeBase biomebase) {
        BiomeBase.REGISTRY_ID.a(i, new MinecraftKey(s), biomebase);
        if (biomebase.b()) {
            BiomeBase.i.a(biomebase, a((BiomeBase) BiomeBase.REGISTRY_ID.get(new MinecraftKey(biomebase.G))));
        }

    }

    public static class a {

        private final String a;
        private float b = 0.1F;
        private float c = 0.2F;
        private float d = 0.5F;
        private float e = 0.5F;
        private int f = 16777215;
        private boolean g;
        private boolean h = true;
        @Nullable
        private String i;

        public a(String s) {
            this.a = s;
        }

        protected BiomeBase.a a(float f) {
            if (f > 0.1F && f < 0.2F) {
                throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
            } else {
                this.d = f;
                return this;
            }
        }

        protected BiomeBase.a b(float f) {
            this.e = f;
            return this;
        }

        protected BiomeBase.a c(float f) {
            this.b = f;
            return this;
        }

        protected BiomeBase.a d(float f) {
            this.c = f;
            return this;
        }

        protected BiomeBase.a a() {
            this.h = false;
            return this;
        }

        protected BiomeBase.a b() {
            this.g = true;
            return this;
        }

        protected BiomeBase.a a(int i) {
            this.f = i;
            return this;
        }

        protected BiomeBase.a a(String s) {
            this.i = s;
            return this;
        }
    }

    public static class BiomeMeta extends WeightedRandom.WeightedRandomChoice {

        public Class<? extends EntityInsentient> b;
        public int c;
        public int d;

        public BiomeMeta(Class<? extends EntityInsentient> oclass, int i, int j, int k) {
            super(i);
            this.b = oclass;
            this.c = j;
            this.d = k;
        }

        public String toString() {
            return this.b.getSimpleName() + "*(" + this.c + "-" + this.d + "):" + this.a;
        }
    }

    public static enum EnumTemperature {

        OCEAN, COLD, MEDIUM, WARM;

        private EnumTemperature() {}
    }
}
