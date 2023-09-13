package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.phys.Vec3D;

public class LargeDripstoneFeature extends WorldGenerator<LargeDripstoneConfiguration> {

    public LargeDripstoneFeature(Codec<LargeDripstoneConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<LargeDripstoneConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        LargeDripstoneConfiguration largedripstoneconfiguration = (LargeDripstoneConfiguration) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();

        if (!DripstoneUtils.isEmptyOrWater(generatoraccessseed, blockposition)) {
            return false;
        } else {
            Optional<Column> optional = Column.scan(generatoraccessseed, blockposition, largedripstoneconfiguration.floorToCeilingSearchRange, DripstoneUtils::isEmptyOrWater, DripstoneUtils::isDripstoneBaseOrLava);

            if (optional.isPresent() && optional.get() instanceof Column.b) {
                Column.b column_b = (Column.b) optional.get();

                if (column_b.height() < 4) {
                    return false;
                } else {
                    int i = (int) ((float) column_b.height() * largedripstoneconfiguration.maxColumnRadiusToCaveHeightRatio);
                    int j = MathHelper.clamp(i, largedripstoneconfiguration.columnRadius.getMinValue(), largedripstoneconfiguration.columnRadius.getMaxValue());
                    int k = MathHelper.randomBetweenInclusive(randomsource, largedripstoneconfiguration.columnRadius.getMinValue(), j);
                    LargeDripstoneFeature.a largedripstonefeature_a = makeDripstone(blockposition.atY(column_b.ceiling() - 1), false, randomsource, k, largedripstoneconfiguration.stalactiteBluntness, largedripstoneconfiguration.heightScale);
                    LargeDripstoneFeature.a largedripstonefeature_a1 = makeDripstone(blockposition.atY(column_b.floor() + 1), true, randomsource, k, largedripstoneconfiguration.stalagmiteBluntness, largedripstoneconfiguration.heightScale);
                    LargeDripstoneFeature.b largedripstonefeature_b;

                    if (largedripstonefeature_a.isSuitableForWind(largedripstoneconfiguration) && largedripstonefeature_a1.isSuitableForWind(largedripstoneconfiguration)) {
                        largedripstonefeature_b = new LargeDripstoneFeature.b(blockposition.getY(), randomsource, largedripstoneconfiguration.windSpeed);
                    } else {
                        largedripstonefeature_b = LargeDripstoneFeature.b.noWind();
                    }

                    boolean flag = largedripstonefeature_a.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(generatoraccessseed, largedripstonefeature_b);
                    boolean flag1 = largedripstonefeature_a1.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(generatoraccessseed, largedripstonefeature_b);

                    if (flag) {
                        largedripstonefeature_a.placeBlocks(generatoraccessseed, randomsource, largedripstonefeature_b);
                    }

                    if (flag1) {
                        largedripstonefeature_a1.placeBlocks(generatoraccessseed, randomsource, largedripstonefeature_b);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstoneFeature.a makeDripstone(BlockPosition blockposition, boolean flag, RandomSource randomsource, int i, FloatProvider floatprovider, FloatProvider floatprovider1) {
        return new LargeDripstoneFeature.a(blockposition, flag, i, (double) floatprovider.sample(randomsource), (double) floatprovider1.sample(randomsource));
    }

    private void placeDebugMarkers(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, Column.b column_b, LargeDripstoneFeature.b largedripstonefeature_b) {
        generatoraccessseed.setBlock(largedripstonefeature_b.offset(blockposition.atY(column_b.ceiling() - 1)), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
        generatoraccessseed.setBlock(largedripstonefeature_b.offset(blockposition.atY(column_b.floor() + 1)), Blocks.GOLD_BLOCK.defaultBlockState(), 2);

        for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.atY(column_b.floor() + 2).mutable(); blockposition_mutableblockposition.getY() < column_b.ceiling() - 1; blockposition_mutableblockposition.move(EnumDirection.UP)) {
            BlockPosition blockposition1 = largedripstonefeature_b.offset(blockposition_mutableblockposition);

            if (DripstoneUtils.isEmptyOrWater(generatoraccessseed, blockposition1) || generatoraccessseed.getBlockState(blockposition1).is(Blocks.DRIPSTONE_BLOCK)) {
                generatoraccessseed.setBlock(blockposition1, Blocks.CREEPER_HEAD.defaultBlockState(), 2);
            }
        }

    }

    private static final class a {

        private BlockPosition root;
        private final boolean pointingUp;
        private int radius;
        private final double bluntness;
        private final double scale;

        a(BlockPosition blockposition, boolean flag, int i, double d0, double d1) {
            this.root = blockposition;
            this.pointingUp = flag;
            this.radius = i;
            this.bluntness = d0;
            this.scale = d1;
        }

        private int getHeight() {
            return this.getHeightAtRadius(0.0F);
        }

        private int getMinY() {
            return this.pointingUp ? this.root.getY() : this.root.getY() - this.getHeight();
        }

        private int getMaxY() {
            return !this.pointingUp ? this.root.getY() : this.root.getY() + this.getHeight();
        }

        boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(GeneratorAccessSeed generatoraccessseed, LargeDripstoneFeature.b largedripstonefeature_b) {
            while (this.radius > 1) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.root.mutable();
                int i = Math.min(10, this.getHeight());

                for (int j = 0; j < i; ++j) {
                    if (generatoraccessseed.getBlockState(blockposition_mutableblockposition).is(Blocks.LAVA)) {
                        return false;
                    }

                    if (DripstoneUtils.isCircleMostlyEmbeddedInStone(generatoraccessseed, largedripstonefeature_b.offset(blockposition_mutableblockposition), this.radius)) {
                        this.root = blockposition_mutableblockposition;
                        return true;
                    }

                    blockposition_mutableblockposition.move(this.pointingUp ? EnumDirection.DOWN : EnumDirection.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int getHeightAtRadius(float f) {
            return (int) DripstoneUtils.getDripstoneHeight((double) f, (double) this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, LargeDripstoneFeature.b largedripstonefeature_b) {
            for (int i = -this.radius; i <= this.radius; ++i) {
                for (int j = -this.radius; j <= this.radius; ++j) {
                    float f = MathHelper.sqrt((float) (i * i + j * j));

                    if (f <= (float) this.radius) {
                        int k = this.getHeightAtRadius(f);

                        if (k > 0) {
                            if ((double) randomsource.nextFloat() < 0.2D) {
                                k = (int) ((float) k * MathHelper.randomBetween(randomsource, 0.8F, 1.0F));
                            }

                            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.root.offset(i, 0, j).mutable();
                            boolean flag = false;
                            int l = this.pointingUp ? generatoraccessseed.getHeight(HeightMap.Type.WORLD_SURFACE_WG, blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getZ()) : Integer.MAX_VALUE;

                            for (int i1 = 0; i1 < k && blockposition_mutableblockposition.getY() < l; ++i1) {
                                BlockPosition blockposition = largedripstonefeature_b.offset(blockposition_mutableblockposition);

                                if (DripstoneUtils.isEmptyOrWaterOrLava(generatoraccessseed, blockposition)) {
                                    flag = true;
                                    Block block = Blocks.DRIPSTONE_BLOCK;

                                    generatoraccessseed.setBlock(blockposition, block.defaultBlockState(), 2);
                                } else if (flag && generatoraccessseed.getBlockState(blockposition).is(TagsBlock.BASE_STONE_OVERWORLD)) {
                                    break;
                                }

                                blockposition_mutableblockposition.move(this.pointingUp ? EnumDirection.UP : EnumDirection.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean isSuitableForWind(LargeDripstoneConfiguration largedripstoneconfiguration) {
            return this.radius >= largedripstoneconfiguration.minRadiusForWind && this.bluntness >= (double) largedripstoneconfiguration.minBluntnessForWind;
        }
    }

    private static final class b {

        private final int originY;
        @Nullable
        private final Vec3D windSpeed;

        b(int i, RandomSource randomsource, FloatProvider floatprovider) {
            this.originY = i;
            float f = floatprovider.sample(randomsource);
            float f1 = MathHelper.randomBetween(randomsource, 0.0F, 3.1415927F);

            this.windSpeed = new Vec3D((double) (MathHelper.cos(f1) * f), 0.0D, (double) (MathHelper.sin(f1) * f));
        }

        private b() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static LargeDripstoneFeature.b noWind() {
            return new LargeDripstoneFeature.b();
        }

        BlockPosition offset(BlockPosition blockposition) {
            if (this.windSpeed == null) {
                return blockposition;
            } else {
                int i = this.originY - blockposition.getY();
                Vec3D vec3d = this.windSpeed.scale((double) i);

                return blockposition.offset(MathHelper.floor(vec3d.x), 0, MathHelper.floor(vec3d.z));
            }
        }
    }
}
