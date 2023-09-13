package net.minecraft.world.level.levelgen;

import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class OreVeinifier {

    private static final float RARITY = 1.0F;
    private static final float RIDGE_NOISE_FREQUENCY = 4.0F;
    private static final float THICKNESS = 0.08F;
    private static final float VEININESS_THRESHOLD = 0.5F;
    private static final double VEININESS_FREQUENCY = 1.5D;
    private static final int EDGE_ROUNDOFF_BEGIN = 20;
    private static final double MAX_EDGE_ROUNDOFF = 0.2D;
    private static final float VEIN_SOLIDNESS = 0.7F;
    private static final float MIN_RICHNESS = 0.1F;
    private static final float MAX_RICHNESS = 0.3F;
    private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
    private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
    private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;
    private final int veinMaxY;
    private final int veinMinY;
    private final IBlockData normalBlock;
    private final NoiseGeneratorNormal veininessNoiseSource;
    private final NoiseGeneratorNormal veinANoiseSource;
    private final NoiseGeneratorNormal veinBNoiseSource;
    private final NoiseGeneratorNormal gapNoise;
    private final int cellWidth;
    private final int cellHeight;

    public OreVeinifier(long i, IBlockData iblockdata, int j, int k, int l) {
        Random random = new Random(i);

        this.normalBlock = iblockdata;
        this.veininessNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
        this.veinANoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
        this.veinBNoiseSource = NoiseGeneratorNormal.a(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
        this.gapNoise = NoiseGeneratorNormal.a(new SimpleRandomSource(0L), -5, 1.0D);
        this.cellWidth = j;
        this.cellHeight = k;
        this.veinMaxY = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.maxY;
        }).max().orElse(l);
        this.veinMinY = Stream.of(OreVeinifier.a.values()).mapToInt((oreveinifier_a) -> {
            return oreveinifier_a.minY;
        }).min().orElse(l);
    }

    public void a(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, this.veininessNoiseSource, 1.5D, k, l);
    }

    public void b(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, this.veinANoiseSource, 4.0D, k, l);
    }

    public void c(double[] adouble, int i, int j, int k, int l) {
        this.a(adouble, i, j, this.veinBNoiseSource, 4.0D, k, l);
    }

    public void a(double[] adouble, int i, int j, NoiseGeneratorNormal noisegeneratornormal, double d0, int k, int l) {
        for (int i1 = 0; i1 < l; ++i1) {
            int j1 = i1 + k;
            int k1 = i * this.cellWidth;
            int l1 = j1 * this.cellHeight;
            int i2 = j * this.cellWidth;
            double d1;

            if (l1 >= this.veinMinY && l1 <= this.veinMaxY) {
                d1 = noisegeneratornormal.a((double) k1 * d0, (double) l1 * d0, (double) i2 * d0);
            } else {
                d1 = 0.0D;
            }

            adouble[i1] = d1;
        }

    }

    public IBlockData a(RandomSource randomsource, int i, int j, int k, double d0, double d1, double d2) {
        IBlockData iblockdata = this.normalBlock;
        OreVeinifier.a oreveinifier_a = this.a(d0, j);

        if (oreveinifier_a == null) {
            return iblockdata;
        } else if (randomsource.nextFloat() > 0.7F) {
            return iblockdata;
        } else if (this.a(d1, d2)) {
            double d3 = MathHelper.a(Math.abs(d0), 0.5D, 0.6000000238418579D, 0.10000000149011612D, 0.30000001192092896D);

            return (double) randomsource.nextFloat() < d3 && this.gapNoise.a((double) i, (double) j, (double) k) > -0.30000001192092896D ? (randomsource.nextFloat() < 0.02F ? oreveinifier_a.rawOreBlock : oreveinifier_a.ore) : oreveinifier_a.filler;
        } else {
            return iblockdata;
        }
    }

    private boolean a(double d0, double d1) {
        double d2 = Math.abs(1.0D * d0) - 0.07999999821186066D;
        double d3 = Math.abs(1.0D * d1) - 0.07999999821186066D;

        return Math.max(d2, d3) < 0.0D;
    }

    @Nullable
    private OreVeinifier.a a(double d0, int i) {
        OreVeinifier.a oreveinifier_a = d0 > 0.0D ? OreVeinifier.a.COPPER : OreVeinifier.a.IRON;
        int j = oreveinifier_a.maxY - i;
        int k = i - oreveinifier_a.minY;

        if (k >= 0 && j >= 0) {
            int l = Math.min(j, k);
            double d1 = MathHelper.a((double) l, 0.0D, 20.0D, -0.2D, 0.0D);

            return Math.abs(d0) + d1 < 0.5D ? null : oreveinifier_a;
        } else {
            return null;
        }
    }

    private static enum a {

        COPPER(Blocks.COPPER_ORE.getBlockData(), Blocks.RAW_COPPER_BLOCK.getBlockData(), Blocks.GRANITE.getBlockData(), 0, 50), IRON(Blocks.DEEPSLATE_IRON_ORE.getBlockData(), Blocks.RAW_IRON_BLOCK.getBlockData(), Blocks.TUFF.getBlockData(), -60, -8);

        final IBlockData ore;
        final IBlockData rawOreBlock;
        final IBlockData filler;
        final int minY;
        final int maxY;

        private a(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, int i, int j) {
            this.ore = iblockdata;
            this.rawOreBlock = iblockdata1;
            this.filler = iblockdata2;
            this.minY = i;
            this.maxY = j;
        }
    }
}
