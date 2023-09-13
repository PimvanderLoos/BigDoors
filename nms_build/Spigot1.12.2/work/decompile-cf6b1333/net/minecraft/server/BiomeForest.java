package net.minecraft.server;

import java.util.Random;

public class BiomeForest extends BiomeBase {

    protected static final WorldGenForest x = new WorldGenForest(false, true);
    protected static final WorldGenForest y = new WorldGenForest(false, false);
    protected static final WorldGenForestTree z = new WorldGenForestTree(false);
    private final BiomeForest.Type A;

    public BiomeForest(BiomeForest.Type biomeforest_type, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.A = biomeforest_type;
        this.s.z = 10;
        this.s.C = 2;
        if (this.A == BiomeForest.Type.FLOWER) {
            this.s.z = 6;
            this.s.B = 100;
            this.s.C = 1;
            this.u.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 4, 2, 3));
        }

        if (this.A == BiomeForest.Type.NORMAL) {
            this.u.add(new BiomeBase.BiomeMeta(EntityWolf.class, 5, 4, 4));
        }

        if (this.A == BiomeForest.Type.ROOFED) {
            this.s.z = -999;
        }

    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (this.A == BiomeForest.Type.ROOFED && random.nextInt(3) > 0 ? BiomeForest.z : (this.A != BiomeForest.Type.BIRCH && random.nextInt(5) != 0 ? (random.nextInt(10) == 0 ? BiomeForest.n : BiomeForest.m) : BiomeForest.y));
    }

    public BlockFlowers.EnumFlowerVarient a(Random random, BlockPosition blockposition) {
        if (this.A == BiomeForest.Type.FLOWER) {
            double d0 = MathHelper.a((1.0D + BiomeForest.k.a((double) blockposition.getX() / 48.0D, (double) blockposition.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
            BlockFlowers.EnumFlowerVarient blockflowers_enumflowervarient = BlockFlowers.EnumFlowerVarient.values()[(int) (d0 * (double) BlockFlowers.EnumFlowerVarient.values().length)];

            return blockflowers_enumflowervarient == BlockFlowers.EnumFlowerVarient.BLUE_ORCHID ? BlockFlowers.EnumFlowerVarient.POPPY : blockflowers_enumflowervarient;
        } else {
            return super.a(random, blockposition);
        }
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        if (this.A == BiomeForest.Type.ROOFED) {
            this.b(world, random, blockposition);
        }

        int i = random.nextInt(5) - 3;

        if (this.A == BiomeForest.Type.FLOWER) {
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
                BiomeForest.l.a(BlockTallPlant.EnumTallFlowerVariants.SYRINGA);
            } else if (k == 1) {
                BiomeForest.l.a(BlockTallPlant.EnumTallFlowerVariants.ROSE);
            } else if (k == 2) {
                BiomeForest.l.a(BlockTallPlant.EnumTallFlowerVariants.PAEONIA);
            }

            int l = 0;

            while (true) {
                if (l < 5) {
                    int i1 = random.nextInt(16) + 8;
                    int j1 = random.nextInt(16) + 8;
                    int k1 = random.nextInt(world.getHighestBlockYAt(blockposition.a(i1, 0, j1)).getY() + 32);

                    if (!BiomeForest.l.generate(world, random, new BlockPosition(blockposition.getX() + i1, k1, blockposition.getZ() + j1))) {
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
