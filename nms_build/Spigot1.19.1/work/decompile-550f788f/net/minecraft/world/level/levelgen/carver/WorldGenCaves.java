package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class WorldGenCaves extends WorldGenCarverAbstract<CaveCarverConfiguration> {

    public WorldGenCaves(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    public boolean isStartChunk(CaveCarverConfiguration cavecarverconfiguration, RandomSource randomsource) {
        return randomsource.nextFloat() <= cavecarverconfiguration.probability;
    }

    public boolean carve(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, RandomSource randomsource, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, CarvingMask carvingmask) {
        int i = SectionPosition.sectionToBlockCoord(this.getRange() * 2 - 1);
        int j = randomsource.nextInt(randomsource.nextInt(randomsource.nextInt(this.getCaveBound()) + 1) + 1);

        for (int k = 0; k < j; ++k) {
            double d0 = (double) chunkcoordintpair.getBlockX(randomsource.nextInt(16));
            double d1 = (double) cavecarverconfiguration.y.sample(randomsource, carvingcontext);
            double d2 = (double) chunkcoordintpair.getBlockZ(randomsource.nextInt(16));
            double d3 = (double) cavecarverconfiguration.horizontalRadiusMultiplier.sample(randomsource);
            double d4 = (double) cavecarverconfiguration.verticalRadiusMultiplier.sample(randomsource);
            double d5 = (double) cavecarverconfiguration.floorLevel.sample(randomsource);
            WorldGenCarverAbstract.a worldgencarverabstract_a = (carvingcontext1, d6, d7, d8, l) -> {
                return shouldSkip(d6, d7, d8, d5);
            };
            int l = 1;
            float f;

            if (randomsource.nextInt(4) == 0) {
                double d6 = (double) cavecarverconfiguration.yScale.sample(randomsource);

                f = 1.0F + randomsource.nextFloat() * 6.0F;
                this.createRoom(carvingcontext, cavecarverconfiguration, ichunkaccess, function, aquifer, d0, d1, d2, f, d6, carvingmask, worldgencarverabstract_a);
                l += randomsource.nextInt(4);
            }

            for (int i1 = 0; i1 < l; ++i1) {
                float f1 = randomsource.nextFloat() * 6.2831855F;

                f = (randomsource.nextFloat() - 0.5F) / 4.0F;
                float f2 = this.getThickness(randomsource);
                int j1 = i - randomsource.nextInt(i / 4);
                boolean flag = false;

                this.createTunnel(carvingcontext, cavecarverconfiguration, ichunkaccess, function, randomsource.nextLong(), aquifer, d0, d1, d2, d3, d4, f2, f1, f, 0, j1, this.getYScale(), carvingmask, worldgencarverabstract_a);
            }
        }

        return true;
    }

    protected int getCaveBound() {
        return 15;
    }

    protected float getThickness(RandomSource randomsource) {
        float f = randomsource.nextFloat() * 2.0F + randomsource.nextFloat();

        if (randomsource.nextInt(10) == 0) {
            f *= randomsource.nextFloat() * randomsource.nextFloat() * 3.0F + 1.0F;
        }

        return f;
    }

    protected double getYScale() {
        return 1.0D;
    }

    protected void createRoom(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, Aquifer aquifer, double d0, double d1, double d2, float f, double d3, CarvingMask carvingmask, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        double d4 = 1.5D + (double) (MathHelper.sin(1.5707964F) * f);
        double d5 = d4 * d3;

        this.carveEllipsoid(carvingcontext, cavecarverconfiguration, ichunkaccess, function, aquifer, d0 + 1.0D, d1, d2, d4, d5, carvingmask, worldgencarverabstract_a);
    }

    protected void createTunnel(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, long i, Aquifer aquifer, double d0, double d1, double d2, double d3, double d4, float f, float f1, float f2, int j, int k, double d5, CarvingMask carvingmask, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        RandomSource randomsource = RandomSource.create(i);
        int l = randomsource.nextInt(k / 2) + k / 4;
        boolean flag = randomsource.nextInt(6) == 0;
        float f3 = 0.0F;
        float f4 = 0.0F;

        for (int i1 = j; i1 < k; ++i1) {
            double d6 = 1.5D + (double) (MathHelper.sin(3.1415927F * (float) i1 / (float) k) * f);
            double d7 = d6 * d5;
            float f5 = MathHelper.cos(f2);

            d0 += (double) (MathHelper.cos(f1) * f5);
            d1 += (double) MathHelper.sin(f2);
            d2 += (double) (MathHelper.sin(f1) * f5);
            f2 *= flag ? 0.92F : 0.7F;
            f2 += f4 * 0.1F;
            f1 += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 2.0F;
            f3 += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 4.0F;
            if (i1 == l && f > 1.0F) {
                this.createTunnel(carvingcontext, cavecarverconfiguration, ichunkaccess, function, randomsource.nextLong(), aquifer, d0, d1, d2, d3, d4, randomsource.nextFloat() * 0.5F + 0.5F, f1 - 1.5707964F, f2 / 3.0F, i1, k, 1.0D, carvingmask, worldgencarverabstract_a);
                this.createTunnel(carvingcontext, cavecarverconfiguration, ichunkaccess, function, randomsource.nextLong(), aquifer, d0, d1, d2, d3, d4, randomsource.nextFloat() * 0.5F + 0.5F, f1 + 1.5707964F, f2 / 3.0F, i1, k, 1.0D, carvingmask, worldgencarverabstract_a);
                return;
            }

            if (randomsource.nextInt(4) != 0) {
                if (!canReach(ichunkaccess.getPos(), d0, d2, i1, k, f)) {
                    return;
                }

                this.carveEllipsoid(carvingcontext, cavecarverconfiguration, ichunkaccess, function, aquifer, d0, d1, d2, d6 * d3, d7 * d4, carvingmask, worldgencarverabstract_a);
            }
        }

    }

    private static boolean shouldSkip(double d0, double d1, double d2, double d3) {
        return d1 <= d3 ? true : d0 * d0 + d1 * d1 + d2 * d2 >= 1.0D;
    }
}
