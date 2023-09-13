package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerDarkOak extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerDarkOak> CODEC = RecordCodecBuilder.create((instance) -> {
        return b(instance).apply(instance, WorldGenFoilagePlacerDarkOak::new);
    });

    public WorldGenFoilagePlacerDarkOak(IntProvider intprovider, IntProvider intprovider1) {
        super(intprovider, intprovider1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.DARK_OAK_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        BlockPosition blockposition = worldgenfoilageplacer_a.a().up(l);
        boolean flag = worldgenfoilageplacer_a.c();

        if (flag) {
            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, -1, flag);
            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 3, 0, flag);
            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, 1, flag);
            if (random.nextBoolean()) {
                this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k, 2, flag);
            }
        } else {
            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 2, -1, flag);
            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + 1, 0, flag);
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 4;
    }

    @Override
    protected boolean b(Random random, int i, int j, int k, int l, boolean flag) {
        return j == 0 && flag && (i == -l || i >= l) && (k == -l || k >= l) ? true : super.b(random, i, j, k, l, flag);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return j == -1 && !flag ? i == l && k == l : (j == 1 ? i + k > l * 2 - 2 : false);
    }
}
