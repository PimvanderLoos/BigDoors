package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BiomeDesert extends BiomeBase {

    public BiomeDesert(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.q = Blocks.SAND.getBlockData();
        this.r = Blocks.SAND.getBlockData();
        this.s.z = -999;
        this.s.D = 2;
        this.s.F = 50;
        this.s.G = 10;
        this.u.clear();
        this.u.add(new BiomeBase.BiomeMeta(EntityRabbit.class, 4, 2, 3));
        Iterator iterator = this.t.iterator();

        while (iterator.hasNext()) {
            BiomeBase.BiomeMeta biomebase_biomemeta = (BiomeBase.BiomeMeta) iterator.next();

            if (biomebase_biomemeta.b == EntityZombie.class || biomebase_biomemeta.b == EntityZombieVillager.class) {
                iterator.remove();
            }
        }

        this.t.add(new BiomeBase.BiomeMeta(EntityZombie.class, 19, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityZombieVillager.class, 1, 1, 1));
        this.t.add(new BiomeBase.BiomeMeta(EntityZombieHusk.class, 80, 4, 4));
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        super.a(world, random, blockposition);
        if (random.nextInt(1000) == 0) {
            int i = random.nextInt(16) + 8;
            int j = random.nextInt(16) + 8;
            BlockPosition blockposition1 = world.getHighestBlockYAt(blockposition.a(i, 0, j)).up();

            (new WorldGenDesertWell()).generate(world, random, blockposition1);
        }

        if (random.nextInt(64) == 0) {
            (new WorldGenFossils()).generate(world, random, blockposition);
        }

    }
}
