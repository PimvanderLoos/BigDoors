package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves;

public abstract class WorldGenSurfaceNetherAbstract extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    private long seed;
    private ImmutableMap<IBlockData, NoiseGeneratorOctaves> floorNoises = ImmutableMap.of();
    private ImmutableMap<IBlockData, NoiseGeneratorOctaves> ceilingNoises = ImmutableMap.of();
    private NoiseGeneratorOctaves patchNoise;

    public WorldGenSurfaceNetherAbstract(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int k1 = l + 1;
        int l1 = i & 15;
        int i2 = j & 15;
        int j2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int k2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        double d1 = 0.03125D;
        boolean flag = this.patchNoise.a((double) i * 0.03125D, 109.0D, (double) j * 0.03125D) * 75.0D + random.nextDouble() > 0.0D;
        IBlockData iblockdata2 = (IBlockData) ((Entry) this.ceilingNoises.entrySet().stream().max(Comparator.comparing((entry) -> {
            return ((NoiseGeneratorOctaves) entry.getValue()).a((double) i, (double) l, (double) j);
        })).get()).getKey();
        IBlockData iblockdata3 = (IBlockData) ((Entry) this.floorNoises.entrySet().stream().max(Comparator.comparing((entry) -> {
            return ((NoiseGeneratorOctaves) entry.getValue()).a((double) i, (double) l, (double) j);
        })).get()).getKey();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        IBlockData iblockdata4 = ichunkaccess.getType(blockposition_mutableblockposition.d(l1, 128, i2));

        for (int l2 = 127; l2 >= i1; --l2) {
            blockposition_mutableblockposition.d(l1, l2, i2);
            IBlockData iblockdata5 = ichunkaccess.getType(blockposition_mutableblockposition);
            int i3;

            if (iblockdata4.a(iblockdata.getBlock()) && (iblockdata5.isAir() || iblockdata5 == iblockdata1)) {
                for (i3 = 0; i3 < j2; ++i3) {
                    blockposition_mutableblockposition.c(EnumDirection.UP);
                    if (!ichunkaccess.getType(blockposition_mutableblockposition).a(iblockdata.getBlock())) {
                        break;
                    }

                    ichunkaccess.setType(blockposition_mutableblockposition, iblockdata2, false);
                }

                blockposition_mutableblockposition.d(l1, l2, i2);
            }

            if ((iblockdata4.isAir() || iblockdata4 == iblockdata1) && iblockdata5.a(iblockdata.getBlock())) {
                for (i3 = 0; i3 < k2 && ichunkaccess.getType(blockposition_mutableblockposition).a(iblockdata.getBlock()); ++i3) {
                    if (flag && l2 >= k1 - 4 && l2 <= k1 + 1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, this.c(), false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata3, false);
                    }

                    blockposition_mutableblockposition.c(EnumDirection.DOWN);
                }
            }

            iblockdata4 = iblockdata5;
        }

    }

    @Override
    public void a(long i) {
        if (this.seed != i || this.patchNoise == null || this.floorNoises.isEmpty() || this.ceilingNoises.isEmpty()) {
            this.floorNoises = a(this.a(), i);
            this.ceilingNoises = a(this.b(), i + (long) this.floorNoises.size());
            this.patchNoise = new NoiseGeneratorOctaves(new SeededRandom(i + (long) this.floorNoises.size() + (long) this.ceilingNoises.size()), ImmutableList.of(0));
        }

        this.seed = i;
    }

    private static ImmutableMap<IBlockData, NoiseGeneratorOctaves> a(ImmutableList<IBlockData> immutablelist, long i) {
        Builder<IBlockData, NoiseGeneratorOctaves> builder = new Builder();

        for (UnmodifiableIterator unmodifiableiterator = immutablelist.iterator(); unmodifiableiterator.hasNext(); ++i) {
            IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

            builder.put(iblockdata, new NoiseGeneratorOctaves(new SeededRandom(i), ImmutableList.of(-4)));
        }

        return builder.build();
    }

    protected abstract ImmutableList<IBlockData> a();

    protected abstract ImmutableList<IBlockData> b();

    protected abstract IBlockData c();
}
