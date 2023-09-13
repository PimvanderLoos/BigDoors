package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BiomeIcePlains extends BiomeBase {

    private final boolean y;
    private final WorldGenPackedIce2 z = new WorldGenPackedIce2();
    private final WorldGenPackedIce1 A = new WorldGenPackedIce1(4);

    public BiomeIcePlains(boolean flag, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.y = flag;
        if (flag) {
            this.r = Blocks.SNOW.getBlockData();
        }

        this.v.clear();
        this.v.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 10, 2, 3));
        this.v.add(new BiomeBase.BiomeMeta(EntityPolarBear.class, 1, 1, 2));
        Iterator iterator = this.u.iterator();

        while (iterator.hasNext()) {
            BiomeBase.BiomeMeta biomebase_biomemeta = (BiomeBase.BiomeMeta) iterator.next();

            if (biomebase_biomemeta.b == EntitySkeleton.class) {
                iterator.remove();
            }
        }

        this.u.add(new BiomeBase.BiomeMeta(EntitySkeleton.class, 20, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntitySkeletonStray.class, 80, 4, 4));
    }

    public float f() {
        return 0.07F;
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        if (this.y) {
            int i;
            int j;
            int k;

            for (i = 0; i < 3; ++i) {
                j = random.nextInt(16) + 8;
                k = random.nextInt(16) + 8;
                this.z.generate(world, random, world.getHighestBlockYAt(blockposition.a(j, 0, k)));
            }

            for (i = 0; i < 2; ++i) {
                j = random.nextInt(16) + 8;
                k = random.nextInt(16) + 8;
                this.A.generate(world, random, world.getHighestBlockYAt(blockposition.a(j, 0, k)));
            }
        }

        super.a(world, random, blockposition);
    }

    public WorldGenTreeAbstract a(Random random) {
        return new WorldGenTaiga2(false);
    }
}
