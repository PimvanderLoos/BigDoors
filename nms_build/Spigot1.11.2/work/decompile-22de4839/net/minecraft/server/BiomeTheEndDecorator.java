package net.minecraft.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BiomeTheEndDecorator extends BiomeDecorator {

    private static final LoadingCache<Long, WorldGenEnder.Spike[]> M = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new BiomeTheEndDecorator.SpikeCache(null));
    private final WorldGenEnder N = new WorldGenEnder();

    public BiomeTheEndDecorator() {}

    protected void a(BiomeBase biomebase, World world, Random random) {
        this.a(world, random);
        WorldGenEnder.Spike[] aworldgenender_spike = a(world);
        WorldGenEnder.Spike[] aworldgenender_spike1 = aworldgenender_spike;
        int i = aworldgenender_spike.length;

        for (int j = 0; j < i; ++j) {
            WorldGenEnder.Spike worldgenender_spike = aworldgenender_spike1[j];

            if (worldgenender_spike.a(this.b)) {
                this.N.a(worldgenender_spike);
                this.N.generate(world, random, new BlockPosition(worldgenender_spike.a(), 45, worldgenender_spike.b()));
            }
        }

    }

    public static WorldGenEnder.Spike[] a(World world) {
        Random random = new Random(world.getSeed());
        long i = random.nextLong() & 65535L;

        return (WorldGenEnder.Spike[]) BiomeTheEndDecorator.M.getUnchecked(Long.valueOf(i));
    }

    static class SpikeCache extends CacheLoader<Long, WorldGenEnder.Spike[]> {

        private SpikeCache() {}

        public WorldGenEnder.Spike[] a(Long olong) throws Exception {
            ArrayList arraylist = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(Integer.valueOf(0), Integer.valueOf(10)), DiscreteDomain.integers()));

            Collections.shuffle(arraylist, new Random(olong.longValue()));
            WorldGenEnder.Spike[] aworldgenender_spike = new WorldGenEnder.Spike[10];

            for (int i = 0; i < 10; ++i) {
                int j = (int) (42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int k = (int) (42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int l = ((Integer) arraylist.get(i)).intValue();
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;

                aworldgenender_spike[i] = new WorldGenEnder.Spike(j, k, i1, j1, flag);
            }

            return aworldgenender_spike;
        }

        public Object load(Object object) throws Exception {
            return this.a((Long) object);
        }

        SpikeCache(Object object) {
            this();
        }
    }
}
