package net.minecraft.server;

import java.util.Random;

public class BiomeForest extends BiomeBase {

    protected static final WorldGenForest y = new WorldGenForest(false, true);
    protected static final WorldGenForest z = new WorldGenForest(false, false);
    protected static final WorldGenForestTree A = new WorldGenForestTree(false);
    private final BiomeForest.Type B;

    public BiomeForest(BiomeForest.Type biomeforest_type, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.B = biomeforest_type;
        this.t.z = 10;
        this.t.C = 2;
        if (this.B == BiomeForest.Type.FLOWER) {
            this.t.z = 6;
            this.t.B = 100;
            this.t.C = 1;
            this.v.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 4, 2, 3));
        }

        if (this.B == BiomeForest.Type.NORMAL) {
            this.v.add(new BiomeBase.BiomeMeta(EntityWolf.class, 5, 4, 4));
        }

        if (this.B == BiomeForest.Type.ROOFED) {
            this.t.z = -999;
        }

    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (this.B == BiomeForest.Type.ROOFED && random.nextInt(3) > 0 ? BiomeForest.A : (this.B != BiomeForest.Type.BIRCH && random.nextInt(5) != 0 ? (random.nextInt(10) == 0 ? BiomeForest.o : BiomeForest.n) : BiomeForest.z));
    }

    public BlockFlowers.EnumFlowerVarient a(Random random, BlockPosition blockposition) {
        if (this.B == BiomeForest.Type.FLOWER) {
            double d0 = MathHelper.a((1.0D + BiomeForest.l.a((double) blockposition.getX() / 48.0D, (double) blockposition.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
            BlockFlowers.EnumFlowerVarient blockflowers_enumflowervarient = BlockFlowers.EnumFlowerVarient.values()[(int) (d0 * (double) BlockFlowers.EnumFlowerVarient.values().length)];

            return blockflowers_enumflowervarient == BlockFlowers.EnumFlowerVarient.BLUE_ORCHID ? BlockFlowers.EnumFlowerVarient.POPPY : blockflowers_enumflowervarient;
        } else {
            return super.a(random, blockposition);
        }
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        if (this.B == BiomeForest.Type.ROOFED) {
            this.b(world, random, blockposition);
        }

        int i = random.nextInt(5) - 3;

        if (this.B == BiomeForest.Type.FLOWER) {
            i += 2;
        }

        this.a(world, random, blockposition, i);
        super.a(world, random, blockposition);
    }

    protected void b(World world, Random random, BlockPosition blockposition) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int k = i * 4 + 1 + 8 + random.nextInt(3);
                int l = j * 4 + 1 + 8 + random.nextInt(3);
                BlockPosition blockposition1 = world.getHighestBlockYAt(blockposition.a(k, 0, l));

                if (random.nextInt(20) == 0) {
                    WorldGenHugeMushroom worldgenhugemushroom = new WorldGenHugeMushroom();

                    worldgenhugemushroom.generate(world, random, blockposition1);
                } else {
                    WorldGenTreeAbstract worldgentreeabstract = this.a(random);

                    worldgentreeabstract.e();
                    if (worldgentreeabstract.generate(world, random, blockposition1)) {
                        worldgentreeabstract.a(world, random, blockposition1);
                    }
                }
            }
        }

    }

    protected void a(World world, Random random, BlockPosition blockposition, int i) {
        int j = 0;

        while (j < i) {
            int k = random.nextInt(3);

            if (k == 0) {
                BiomeForest.m.a(BlockTallPlant.EnumTallFlowerVariants.SYRINGA);
            } else if (k == 1) {
                BiomeForest.m.a(BlockTallPlant.EnumTallFlowerVariants.ROSE);
            } else if (k == 2) {
                BiomeForest.m.a(BlockTallPlant.EnumTallFlowerVariants.PAEONIA);
            }

            int l = 0;

            while (true) {
                if (l < 5) {
                    int i1 = random.nextInt(16) + 8;
                    int j1 = random.nextInt(16) + 8;
                    int k1 = random.nextInt(world.getHighestBlockYAt(blockposition.a(i1, 0, j1)).getY() + 32);

                    if (!BiomeForest.m.generate(world, random, new BlockPosition(blockposition.getX() + i1, k1, blockposition.getZ() + j1))) {
                        ++l;
                        continue;
                    }
                }

                ++j;
                break;
            }
        }

    }

    public Class<? extends BiomeBase> g() {
        return BiomeForest.class;
    }

    public static enum Type {

        NORMAL, FLOWER, BIRCH, ROOFED;

        private Type() {}
    }
}
