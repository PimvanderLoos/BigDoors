package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class WorldGenCaves extends WorldGenCarverAbstract<CaveCarverConfiguration> {

    public WorldGenCaves(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    public boolean a(CaveCarverConfiguration cavecarverconfiguration, Random random) {
        return random.nextFloat() <= cavecarverconfiguration.probability;
    }

    public boolean a(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, BitSet bitset) {
        int i = SectionPosition.c(this.d() * 2 - 1);
        int j = random.nextInt(random.nextInt(random.nextInt(this.a()) + 1) + 1);

        for (int k = 0; k < j; ++k) {
            double d0 = (double) chunkcoordintpair.a(random.nextInt(16));
            double d1 = (double) cavecarverconfiguration.y.a(random, carvingcontext);
            double d2 = (double) chunkcoordintpair.b(random.nextInt(16));
            double d3 = (double) cavecarverconfiguration.horizontalRadiusMultiplier.a(random);
            double d4 = (double) cavecarverconfiguration.verticalRadiusMultiplier.a(random);
            double d5 = (double) cavecarverconfiguration.floorLevel.a(random);
            WorldGenCarverAbstract.a worldgencarverabstract_a = (carvingcontext1, d6, d7, d8, l) -> {
                return a(d6, d7, d8, d5);
            };
            int l = 1;
            float f;

            if (random.nextInt(4) == 0) {
                double d6 = (double) cavecarverconfiguration.yScale.a(random);

                f = 1.0F + random.nextFloat() * 6.0F;
                this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, random.nextLong(), aquifer, d0, d1, d2, f, d6, bitset, worldgencarverabstract_a);
                l += random.nextInt(4);
            }

            for (int i1 = 0; i1 < l; ++i1) {
                float f1 = random.nextFloat() * 6.2831855F;

                f = (random.nextFloat() - 0.5F) / 4.0F;
                float f2 = this.a(random);
                int j1 = i - random.nextInt(i / 4);
                boolean flag = false;

                this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, random.nextLong(), aquifer, d0, d1, d2, d3, d4, f2, f1, f, 0, j1, this.b(), bitset, worldgencarverabstract_a);
            }
        }

        return true;
    }

    protected int a() {
        return 15;
    }

    protected float a(Random random) {
        float f = random.nextFloat() * 2.0F + random.nextFloat();

        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
        }

        return f;
    }

    protected double b() {
        return 1.0D;
    }

    protected void a(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, long i, Aquifer aquifer, double d0, double d1, double d2, float f, double d3, BitSet bitset, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        double d4 = 1.5D + (double) (MathHelper.sin(1.5707964F) * f);
        double d5 = d4 * d3;

        this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, i, aquifer, d0 + 1.0D, d1, d2, d4, d5, bitset, worldgencarverabstract_a);
    }

    protected void a(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, long i, Aquifer aquifer, double d0, double d1, double d2, double d3, double d4, float f, float f1, float f2, int j, int k, double d5, BitSet bitset, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        Random random = new Random(i);
        int l = random.nextInt(k / 2) + k / 4;
        boolean flag = random.nextInt(6) == 0;
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
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (i1 == l && f > 1.0F) {
                this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, random.nextLong(), aquifer, d0, d1, d2, d3, d4, random.nextFloat() * 0.5F + 0.5F, f1 - 1.5707964F, f2 / 3.0F, i1, k, 1.0D, bitset, worldgencarverabstract_a);
                this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, random.nextLong(), aquifer, d0, d1, d2, d3, d4, random.nextFloat() * 0.5F + 0.5F, f1 + 1.5707964F, f2 / 3.0F, i1, k, 1.0D, bitset, worldgencarverabstract_a);
                return;
            }

            if (random.nextInt(4) != 0) {
                if (!a(ichunkaccess.getPos(), d0, d2, i1, k, f)) {
                    return;
                }

                this.a(carvingcontext, cavecarverconfiguration, ichunkaccess, function, i, aquifer, d0, d1, d2, d6 * d3, d7 * d4, bitset, worldgencarverabstract_a);
            }
        }

    }

    private static boolean a(double d0, double d1, double d2, double d3) {
        return d1 <= d3 ? true : d0 * d0 + d1 * d1 + d2 * d2 >= 1.0D;
    }
}
