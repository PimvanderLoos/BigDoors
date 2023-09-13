package net.minecraft.server;

import java.util.Random;

public class BiomeDecorator {

    protected boolean a;
    protected BlockPosition b;
    protected CustomWorldSettingsFinal c;
    protected WorldGenerator d = new WorldGenClay(4);
    protected WorldGenerator e;
    protected WorldGenerator f;
    protected WorldGenerator g;
    protected WorldGenerator h;
    protected WorldGenerator i;
    protected WorldGenerator j;
    protected WorldGenerator k;
    protected WorldGenerator l;
    protected WorldGenerator m;
    protected WorldGenerator n;
    protected WorldGenerator o;
    protected WorldGenerator p;
    protected WorldGenerator q;
    protected WorldGenFlowers r;
    protected WorldGenerator s;
    protected WorldGenerator t;
    protected WorldGenerator u;
    protected WorldGenerator v;
    protected WorldGenerator w;
    protected WorldGenerator x;
    protected int y;
    protected int z;
    protected float A;
    protected int B;
    protected int C;
    protected int D;
    protected int E;
    protected int F;
    protected int G;
    protected int H;
    protected int I;
    protected int J;
    protected int K;
    public boolean L;

    public BiomeDecorator() {
        this.e = new WorldGenSand(Blocks.SAND, 7);
        this.f = new WorldGenSand(Blocks.GRAVEL, 6);
        this.r = new WorldGenFlowers(Blocks.YELLOW_FLOWER, BlockFlowers.EnumFlowerVarient.DANDELION);
        this.s = new WorldGenMushrooms(Blocks.BROWN_MUSHROOM);
        this.t = new WorldGenMushrooms(Blocks.RED_MUSHROOM);
        this.u = new WorldGenHugeMushroom();
        this.v = new WorldGenReed();
        this.w = new WorldGenCactus();
        this.x = new WorldGenWaterLily();
        this.A = 0.1F;
        this.B = 2;
        this.C = 1;
        this.H = 1;
        this.I = 3;
        this.J = 1;
        this.L = true;
    }

    public void a(World world, Random random, BiomeBase biomebase, BlockPosition blockposition) {
        if (this.a) {
            throw new RuntimeException("Already decorating");
        } else {
            this.c = CustomWorldSettingsFinal.CustomWorldSettings.a(world.getWorldData().getGeneratorOptions()).b();
            this.b = blockposition;
            this.g = new WorldGenMinable(Blocks.DIRT.getBlockData(), this.c.J);
            this.h = new WorldGenMinable(Blocks.GRAVEL.getBlockData(), this.c.N);
            this.i = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.GRANITE), this.c.R);
            this.j = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.DIORITE), this.c.V);
            this.k = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.ANDESITE), this.c.Z);
            this.l = new WorldGenMinable(Blocks.COAL_ORE.getBlockData(), this.c.ad);
            this.m = new WorldGenMinable(Blocks.IRON_ORE.getBlockData(), this.c.ah);
            this.n = new WorldGenMinable(Blocks.GOLD_ORE.getBlockData(), this.c.al);
            this.o = new WorldGenMinable(Blocks.REDSTONE_ORE.getBlockData(), this.c.ap);
            this.p = new WorldGenMinable(Blocks.DIAMOND_ORE.getBlockData(), this.c.at);
            this.q = new WorldGenMinable(Blocks.LAPIS_ORE.getBlockData(), this.c.ax);
            this.a(biomebase, world, random);
            this.a = false;
        }
    }

    protected void a(BiomeBase biomebase, World world, Random random) {
        this.a(world, random);

        int i;
        int j;
        int k;

        for (i = 0; i < this.I; ++i) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            this.e.generate(world, random, world.q(this.b.a(j, 0, k)));
        }

        for (i = 0; i < this.J; ++i) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            this.d.generate(world, random, world.q(this.b.a(j, 0, k)));
        }

        for (i = 0; i < this.H; ++i) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            this.f.generate(world, random, world.q(this.b.a(j, 0, k)));
        }

        i = this.z;
        if (random.nextFloat() < this.A) {
            ++i;
        }

        int l;
        BlockPosition blockposition;

        for (j = 0; j < i; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            WorldGenTreeAbstract worldgentreeabstract = biomebase.a(random);

            worldgentreeabstract.e();
            blockposition = world.getHighestBlockYAt(this.b.a(k, 0, l));
            if (worldgentreeabstract.generate(world, random, blockposition)) {
                worldgentreeabstract.a(world, random, blockposition);
            }
        }

        for (j = 0; j < this.K; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            this.u.generate(world, random, world.getHighestBlockYAt(this.b.a(k, 0, l)));
        }

        BlockPosition blockposition1;
        int i1;
        int j1;

        for (j = 0; j < this.B; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() + 32;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                blockposition1 = this.b.a(k, j1, l);
                BlockFlowers.EnumFlowerVarient blockflowers_enumflowervarient = biomebase.a(random, blockposition1);
                BlockFlowers blockflowers = blockflowers_enumflowervarient.a().a();

                if (blockflowers.getBlockData().getMaterial() != Material.AIR) {
                    this.r.a(blockflowers, blockflowers_enumflowervarient);
                    this.r.generate(world, random, blockposition1);
                }
            }
        }

        for (j = 0; j < this.C; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                biomebase.b(random).generate(world, random, this.b.a(k, j1, l));
            }
        }

        for (j = 0; j < this.D; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                (new WorldGenDeadBush()).generate(world, random, this.b.a(k, j1, l));
            }
        }

        for (j = 0; j < this.y; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);

                BlockPosition blockposition2;

                for (blockposition1 = this.b.a(k, j1, l); blockposition1.getY() > 0; blockposition1 = blockposition2) {
                    blockposition2 = blockposition1.down();
                    if (!world.isEmpty(blockposition2)) {
                        break;
                    }
                }

                this.x.generate(world, random, blockposition1);
            }
        }

        for (j = 0; j < this.E; ++j) {
            if (random.nextInt(4) == 0) {
                k = random.nextInt(16) + 8;
                l = random.nextInt(16) + 8;
                BlockPosition blockposition3 = world.getHighestBlockYAt(this.b.a(k, 0, l));

                this.s.generate(world, random, blockposition3);
            }

            if (random.nextInt(8) == 0) {
                k = random.nextInt(16) + 8;
                l = random.nextInt(16) + 8;
                i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
                if (i1 > 0) {
                    j1 = random.nextInt(i1);
                    blockposition1 = this.b.a(k, j1, l);
                    this.t.generate(world, random, blockposition1);
                }
            }
        }

        if (random.nextInt(4) == 0) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            l = world.getHighestBlockYAt(this.b.a(j, 0, k)).getY() * 2;
            if (l > 0) {
                i1 = random.nextInt(l);
                this.s.generate(world, random, this.b.a(j, i1, k));
            }
        }

        if (random.nextInt(8) == 0) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            l = world.getHighestBlockYAt(this.b.a(j, 0, k)).getY() * 2;
            if (l > 0) {
                i1 = random.nextInt(l);
                this.t.generate(world, random, this.b.a(j, i1, k));
            }
        }

        for (j = 0; j < this.F; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                this.v.generate(world, random, this.b.a(k, j1, l));
            }
        }

        for (j = 0; j < 10; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                this.v.generate(world, random, this.b.a(k, j1, l));
            }
        }

        if (random.nextInt(32) == 0) {
            j = random.nextInt(16) + 8;
            k = random.nextInt(16) + 8;
            l = world.getHighestBlockYAt(this.b.a(j, 0, k)).getY() * 2;
            if (l > 0) {
                i1 = random.nextInt(l);
                (new WorldGenPumpkin()).generate(world, random, this.b.a(j, i1, k));
            }
        }

        for (j = 0; j < this.G; ++j) {
            k = random.nextInt(16) + 8;
            l = random.nextInt(16) + 8;
            i1 = world.getHighestBlockYAt(this.b.a(k, 0, l)).getY() * 2;
            if (i1 > 0) {
                j1 = random.nextInt(i1);
                this.w.generate(world, random, this.b.a(k, j1, l));
            }
        }

        if (this.L) {
            for (j = 0; j < 50; ++j) {
                k = random.nextInt(16) + 8;
                l = random.nextInt(16) + 8;
                i1 = random.nextInt(248) + 8;
                if (i1 > 0) {
                    j1 = random.nextInt(i1);
                    blockposition1 = this.b.a(k, j1, l);
                    (new WorldGenLiquids(Blocks.FLOWING_WATER)).generate(world, random, blockposition1);
                }
            }

            for (j = 0; j < 20; ++j) {
                k = random.nextInt(16) + 8;
                l = random.nextInt(16) + 8;
                i1 = random.nextInt(random.nextInt(random.nextInt(240) + 8) + 8);
                blockposition = this.b.a(k, i1, l);
                (new WorldGenLiquids(Blocks.FLOWING_LAVA)).generate(world, random, blockposition);
            }
        }

    }

    protected void a(World world, Random random) {
        this.a(world, random, this.c.K, this.g, this.c.L, this.c.M);
        this.a(world, random, this.c.O, this.h, this.c.P, this.c.Q);
        this.a(world, random, this.c.W, this.j, this.c.X, this.c.Y);
        this.a(world, random, this.c.S, this.i, this.c.T, this.c.U);
        this.a(world, random, this.c.aa, this.k, this.c.ab, this.c.ac);
        this.a(world, random, this.c.ae, this.l, this.c.af, this.c.ag);
        this.a(world, random, this.c.ai, this.m, this.c.aj, this.c.ak);
        this.a(world, random, this.c.am, this.n, this.c.an, this.c.ao);
        this.a(world, random, this.c.aq, this.o, this.c.ar, this.c.as);
        this.a(world, random, this.c.au, this.p, this.c.av, this.c.aw);
        this.b(world, random, this.c.ay, this.q, this.c.az, this.c.aA);
    }

    protected void a(World world, Random random, int i, WorldGenerator worldgenerator, int j, int k) {
        int l;

        if (k < j) {
            l = j;
            j = k;
            k = l;
        } else if (k == j) {
            if (j < 255) {
                ++k;
            } else {
                --j;
            }
        }

        for (l = 0; l < i; ++l) {
            BlockPosition blockposition = this.b.a(random.nextInt(16), random.nextInt(k - j) + j, random.nextInt(16));

            worldgenerator.generate(world, random, blockposition);
        }

    }

    protected void b(World world, Random random, int i, WorldGenerator worldgenerator, int j, int k) {
        for (int l = 0; l < i; ++l) {
            BlockPosition blockposition = this.b.a(random.nextInt(16), random.nextInt(k) + random.nextInt(k) + j - k, random.nextInt(16));

            worldgenerator.generate(world, random, blockposition);
        }

    }
}
