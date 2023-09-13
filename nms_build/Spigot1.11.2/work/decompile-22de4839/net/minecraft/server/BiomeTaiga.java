package net.minecraft.server;

import java.util.Random;

public class BiomeTaiga extends BiomeBase {

    private static final WorldGenTaiga1 y = new WorldGenTaiga1();
    private static final WorldGenTaiga2 z = new WorldGenTaiga2(false);
    private static final WorldGenMegaTree A = new WorldGenMegaTree(false, false);
    private static final WorldGenMegaTree B = new WorldGenMegaTree(false, true);
    private static final WorldGenTaigaStructure C = new WorldGenTaigaStructure(Blocks.MOSSY_COBBLESTONE, 0);
    private final BiomeTaiga.Type D;

    public BiomeTaiga(BiomeTaiga.Type biometaiga_type, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.D = biometaiga_type;
        this.v.add(new BiomeBase.BiomeMeta(EntityWolf.class, 8, 4, 4));
        this.v.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 4, 2, 3));
        this.t.z = 10;
        if (biometaiga_type != BiomeTaiga.Type.MEGA && biometaiga_type != BiomeTaiga.Type.MEGA_SPRUCE) {
            this.t.C = 1;
            this.t.E = 1;
        } else {
            this.t.C = 7;
            this.t.D = 1;
            this.t.E = 3;
        }

    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) ((this.D == BiomeTaiga.Type.MEGA || this.D == BiomeTaiga.Type.MEGA_SPRUCE) && random.nextInt(3) == 0 ? (this.D != BiomeTaiga.Type.MEGA_SPRUCE && random.nextInt(13) != 0 ? BiomeTaiga.A : BiomeTaiga.B) : (random.nextInt(3) == 0 ? BiomeTaiga.y : BiomeTaiga.z));
    }

    public WorldGenerator b(Random random) {
        return random.nextInt(5) > 0 ? new WorldGenGrass(BlockLongGrass.EnumTallGrassType.FERN) : new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        int i;
        int j;
        int k;
        int l;

        if (this.D == BiomeTaiga.Type.MEGA || this.D == BiomeTaiga.Type.MEGA_SPRUCE) {
            i = random.nextInt(3);

            for (j = 0; j < i; ++j) {
                k = random.nextInt(16) + 8;
                l = random.nextInt(16) + 8;
                BlockPosition blockposition1 = world.getHighestBlockYAt(blockposition.a(k, 0, l));

                BiomeTaiga.C.generate(world, random, blockposition1);
            }
        }

        BiomeTaiga.m.a(BlockTallPlant.EnumTallFlowerVariants.FERN);

        for (i = 0; i < 7; ++i) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            l = random.nextInt(world.getHighestBlockYAt(blockposition.a(j, 0, k)).getY() + 32);
            BiomeTaiga.m.generate(world, random, blockposition.a(j, l, k));
        }

        super.a(world, random, blockposition);
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        if (this.D == BiomeTaiga.Type.MEGA || this.D == BiomeTaiga.Type.MEGA_SPRUCE) {
            this.r = Blocks.GRASS.getBlockData();
            this.s = Blocks.DIRT.getBlockData();
            if (d0 > 1.75D) {
                this.r = Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.COARSE_DIRT);
            } else if (d0 > -0.95D) {
                this.r = Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.PODZOL);
            }
        }

        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public static enum Type {

        NORMAL, MEGA, MEGA_SPRUCE;

        private Type() {}
    }
}
