package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.phys.Vec3D;

public class LargeDripstoneFeature extends WorldGenerator<LargeDripstoneConfiguration> {

    public LargeDripstoneFeature(Codec<LargeDripstoneConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<LargeDripstoneConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        LargeDripstoneConfiguration largedripstoneconfiguration = (LargeDripstoneConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();

        if (!DripstoneUtils.a((GeneratorAccess) generatoraccessseed, blockposition)) {
            return false;
        } else {
            Optional<Column> optional = Column.a(generatoraccessseed, blockposition, largedripstoneconfiguration.floorToCeilingSearchRange, DripstoneUtils::c, DripstoneUtils::a);

            if (optional.isPresent() && optional.get() instanceof Column.b) {
                Column.b column_b = (Column.b) optional.get();

                if (column_b.g() < 4) {
                    return false;
                } else {
                    int i = (int) ((float) column_b.g() * largedripstoneconfiguration.maxColumnRadiusToCaveHeightRatio);
                    int j = MathHelper.clamp(i, largedripstoneconfiguration.columnRadius.a(), largedripstoneconfiguration.columnRadius.b());
                    int k = MathHelper.b(random, largedripstoneconfiguration.columnRadius.a(), j);
                    LargeDripstoneFeature.a largedripstonefeature_a = a(blockposition.h(column_b.e() - 1), false, random, k, largedripstoneconfiguration.stalactiteBluntness, largedripstoneconfiguration.heightScale);
                    LargeDripstoneFeature.a largedripstonefeature_a1 = a(blockposition.h(column_b.f() + 1), true, random, k, largedripstoneconfiguration.stalagmiteBluntness, largedripstoneconfiguration.heightScale);
                    LargeDripstoneFeature.b largedripstonefeature_b;

                    if (largedripstonefeature_a.a(largedripstoneconfiguration) && largedripstonefeature_a1.a(largedripstoneconfiguration)) {
                        largedripstonefeature_b = new LargeDripstoneFeature.b(blockposition.getY(), random, largedripstoneconfiguration.windSpeed);
                    } else {
                        largedripstonefeature_b = LargeDripstoneFeature.b.a();
                    }

                    boolean flag = largedripstonefeature_a.a(generatoraccessseed, largedripstonefeature_b);
                    boolean flag1 = largedripstonefeature_a1.a(generatoraccessseed, largedripstonefeature_b);

                    if (flag) {
                        largedripstonefeature_a.a(generatoraccessseed, random, largedripstonefeature_b);
                    }

                    if (flag1) {
                        largedripstonefeature_a1.a(generatoraccessseed, random, largedripstonefeature_b);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstoneFeature.a a(BlockPosition blockposition, boolean flag, Random random, int i, FloatProvider floatprovider, FloatProvider floatprovider1) {
        return new LargeDripstoneFeature.a(blockposition, flag, i, (double) floatprovider.a(random), (double) floatprovider1.a(random));
    }

    private void a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, Column.b column_b, LargeDripstoneFeature.b largedripstonefeature_b) {
        generatoraccessseed.setTypeAndData(largedripstonefeature_b.a(blockposition.h(column_b.e() - 1)), Blocks.DIAMOND_BLOCK.getBlockData(), 2);
        generatoraccessseed.setTypeAndData(largedripstonefeature_b.a(blockposition.h(column_b.f() + 1)), Blocks.GOLD_BLOCK.getBlockData(), 2);

        for (BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.h(column_b.f() + 2).i(); blockposition_mutableblockposition.getY() < column_b.e() - 1; blockposition_mutableblockposition.c(EnumDirection.UP)) {
            BlockPosition blockposition1 = largedripstonefeature_b.a(blockposition_mutableblockposition);

            if (DripstoneUtils.a((GeneratorAccess) generatoraccessseed, blockposition1) || generatoraccessseed.getType(blockposition1).a(Blocks.DRIPSTONE_BLOCK)) {
                generatoraccessseed.setTypeAndData(blockposition1, Blocks.CREEPER_HEAD.getBlockData(), 2);
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

        private int a() {
            return this.a(0.0F);
        }

        private int b() {
            return this.pointingUp ? this.root.getY() : this.root.getY() - this.a();
        }

        private int c() {
            return !this.pointingUp ? this.root.getY() : this.root.getY() + this.a();
        }

        boolean a(GeneratorAccessSeed generatoraccessseed, LargeDripstoneFeature.b largedripstonefeature_b) {
            while (this.radius > 1) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.root.i();
                int i = Math.min(10, this.a());

                for (int j = 0; j < i; ++j) {
                    if (generatoraccessseed.getType(blockposition_mutableblockposition).a(Blocks.LAVA)) {
                        return false;
                    }

                    if (DripstoneUtils.a(generatoraccessseed, largedripstonefeature_b.a(blockposition_mutableblockposition), this.radius)) {
                        this.root = blockposition_mutableblockposition;
                        return true;
                    }

                    blockposition_mutableblockposition.c(this.pointingUp ? EnumDirection.DOWN : EnumDirection.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int a(float f) {
            return (int) DripstoneUtils.a((double) f, (double) this.radius, this.scale, this.bluntness);
        }

        void a(GeneratorAccessSeed generatoraccessseed, Random random, LargeDripstoneFeature.b largedripstonefeature_b) {
            for (int i = -this.radius; i <= this.radius; ++i) {
                for (int j = -this.radius; j <= this.radius; ++j) {
                    float f = MathHelper.c((float) (i * i + j * j));

                    if (f <= (float) this.radius) {
                        int k = this.a(f);

                        if (k > 0) {
                            if ((double) random.nextFloat() < 0.2D) {
                                k = (int) ((float) k * MathHelper.b(random, 0.8F, 1.0F));
                            }

                            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.root.c(i, 0, j).i();
                            boolean flag = false;

                            for (int l = 0; l < k; ++l) {
                                BlockPosition blockposition = largedripstonefeature_b.a(blockposition_mutableblockposition);

                                if (DripstoneUtils.b(generatoraccessseed, blockposition)) {
                                    flag = true;
                                    Block block = Blocks.DRIPSTONE_BLOCK;

                                    generatoraccessseed.setTypeAndData(blockposition, block.getBlockData(), 2);
                                } else if (flag && generatoraccessseed.getType(blockposition).a((Tag) TagsBlock.BASE_STONE_OVERWORLD)) {
                                    break;
                                }

                                blockposition_mutableblockposition.c(this.pointingUp ? EnumDirection.UP : EnumDirection.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean a(LargeDripstoneConfiguration largedripstoneconfiguration) {
            return this.radius >= largedripstoneconfiguration.minRadiusForWind && this.bluntness >= (double) largedripstoneconfiguration.minBluntnessForWind;
        }
    }

    private static final class b {

        private final int originY;
        @Nullable
        private final Vec3D windSpeed;

        b(int i, Random random, FloatProvider floatprovider) {
            this.originY = i;
            float f = floatprovider.a(random);
            float f1 = MathHelper.b(random, 0.0F, 3.1415927F);

            this.windSpeed = new Vec3D((double) (MathHelper.cos(f1) * f), 0.0D, (double) (MathHelper.sin(f1) * f));
        }

        private b() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static LargeDripstoneFeature.b a() {
            return new LargeDripstoneFeature.b();
        }

        BlockPosition a(BlockPosition blockposition) {
            if (this.windSpeed == null) {
                return blockposition;
            } else {
                int i = this.originY - blockposition.getY();
                Vec3D vec3d = this.windSpeed.a((double) i);

                return blockposition.b(vec3d.x, 0.0D, vec3d.z);
            }
        }
    }
}
