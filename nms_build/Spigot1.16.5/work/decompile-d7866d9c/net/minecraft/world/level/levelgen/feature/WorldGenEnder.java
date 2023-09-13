package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockIronBars;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;
import net.minecraft.world.phys.AxisAlignedBB;

public class WorldGenEnder extends WorldGenerator<WorldGenFeatureEndSpikeConfiguration> {

    private static final LoadingCache<Long, List<WorldGenEnder.Spike>> a = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new WorldGenEnder.b());

    public WorldGenEnder(Codec<WorldGenFeatureEndSpikeConfiguration> codec) {
        super(codec);
    }

    public static List<WorldGenEnder.Spike> a(GeneratorAccessSeed generatoraccessseed) {
        Random random = new Random(generatoraccessseed.getSeed());
        long i = random.nextLong() & 65535L;

        return (List) WorldGenEnder.a.getUnchecked(i);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration) {
        List<WorldGenEnder.Spike> list = worldgenfeatureendspikeconfiguration.c();

        if (list.isEmpty()) {
            list = a(generatoraccessseed);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) iterator.next();

            if (worldgenender_spike.a(blockposition)) {
                this.a(generatoraccessseed, random, worldgenfeatureendspikeconfiguration, worldgenender_spike);
            }
        }

        return true;
    }

    private void a(WorldAccess worldaccess, Random random, WorldGenFeatureEndSpikeConfiguration worldgenfeatureendspikeconfiguration, WorldGenEnder.Spike worldgenender_spike) {
        int i = worldgenender_spike.c();
        Iterator iterator = BlockPosition.a(new BlockPosition(worldgenender_spike.a() - i, 0, worldgenender_spike.b() - i), new BlockPosition(worldgenender_spike.a() + i, worldgenender_spike.d() + 10, worldgenender_spike.b() + i)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (blockposition.distanceSquared((double) worldgenender_spike.a(), (double) blockposition.getY(), (double) worldgenender_spike.b(), false) <= (double) (i * i + 1) && blockposition.getY() < worldgenender_spike.d()) {
                this.a(worldaccess, blockposition, Blocks.OBSIDIAN.getBlockData());
            } else if (blockposition.getY() > 65) {
                this.a(worldaccess, blockposition, Blocks.AIR.getBlockData());
            }
        }

        if (worldgenender_spike.e()) {
            boolean flag = true;
            boolean flag1 = true;
            boolean flag2 = true;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    for (int l = 0; l <= 3; ++l) {
                        boolean flag3 = MathHelper.a(j) == 2;
                        boolean flag4 = MathHelper.a(k) == 2;
                        boolean flag5 = l == 3;

                        if (flag3 || flag4 || flag5) {
                            boolean flag6 = j == -2 || j == 2 || flag5;
                            boolean flag7 = k == -2 || k == 2 || flag5;
                            IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, flag6 && k != -2)).set(BlockIronBars.SOUTH, flag6 && k != 2)).set(BlockIronBars.WEST, flag7 && j != -2)).set(BlockIronBars.EAST, flag7 && j != 2);

                            this.a(worldaccess, blockposition_mutableblockposition.d(worldgenender_spike.a() + j, worldgenender_spike.d() + l, worldgenender_spike.b() + k), iblockdata);
                        }
                    }
                }
            }
        }

        EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) EntityTypes.END_CRYSTAL.a((World) worldaccess.getMinecraftWorld());

        entityendercrystal.setBeamTarget(worldgenfeatureendspikeconfiguration.d());
        entityendercrystal.setInvulnerable(worldgenfeatureendspikeconfiguration.b());
        entityendercrystal.setPositionRotation((double) worldgenender_spike.a() + 0.5D, (double) (worldgenender_spike.d() + 1), (double) worldgenender_spike.b() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        worldaccess.addEntity(entityendercrystal);
        this.a(worldaccess, new BlockPosition(worldgenender_spike.a(), worldgenender_spike.d(), worldgenender_spike.b()), Blocks.BEDROCK.getBlockData());
    }

    static class b extends CacheLoader<Long, List<WorldGenEnder.Spike>> {

        private b() {}

        public List<WorldGenEnder.Spike> load(Long olong) {
            List<Integer> list = (List) IntStream.range(0, 10).boxed().collect(Collectors.toList());

            Collections.shuffle(list, new Random(olong));
            List<WorldGenEnder.Spike> list1 = Lists.newArrayList();

            for (int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int l = (Integer) list.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;

                list1.add(new WorldGenEnder.Spike(j, k, i1, j1, flag));
            }

            return list1;
        }
    }

    public static class Spike {

        public static final Codec<WorldGenEnder.Spike> a = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("centerX").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.b;
            }), Codec.INT.fieldOf("centerZ").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.c;
            }), Codec.INT.fieldOf("radius").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.d;
            }), Codec.INT.fieldOf("height").orElse(0).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.e;
            }), Codec.BOOL.fieldOf("guarded").orElse(false).forGetter((worldgenender_spike) -> {
                return worldgenender_spike.f;
            })).apply(instance, WorldGenEnder.Spike::new);
        });
        private final int b;
        private final int c;
        private final int d;
        private final int e;
        private final boolean f;
        private final AxisAlignedBB g;

        public Spike(int i, int j, int k, int l, boolean flag) {
            this.b = i;
            this.c = j;
            this.d = k;
            this.e = l;
            this.f = flag;
            this.g = new AxisAlignedBB((double) (i - k), 0.0D, (double) (j - k), (double) (i + k), 256.0D, (double) (j + k));
        }

        public boolean a(BlockPosition blockposition) {
            return blockposition.getX() >> 4 == this.b >> 4 && blockposition.getZ() >> 4 == this.c >> 4;
        }

        public int a() {
            return this.b;
        }

        public int b() {
            return this.c;
        }

        public int c() {
            return this.d;
        }

        public int d() {
            return this.e;
        }

        public boolean e() {
            return this.f;
        }

        public AxisAlignedBB f() {
            return this.g;
        }
    }
}
