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

public class WorldGenFoilagePlacerBush extends WorldGenFoilagePlacerBlob {

    public static final Codec<WorldGenFoilagePlacerBush> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, WorldGenFoilagePlacerBush::new);
    });

    public WorldGenFoilagePlacerBush(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1, i);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.BUSH_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = k + worldgenfoilageplacer_a.b() - 1 - i1;

            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.a(), j1, i1, worldgenfoilageplacer_a.c());
        }

    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && random.nextInt(2) == 0;
    }
}
