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

public class WorldGenFoilagePlacerAcacia extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerAcacia> CODEC = RecordCodecBuilder.create((instance) -> {
        return b(instance).apply(instance, WorldGenFoilagePlacerAcacia::new);
    });

    public WorldGenFoilagePlacerAcacia(IntProvider intprovider, IntProvider intprovider1) {
        super(intprovider, intprovider1);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        boolean flag = worldgenfoilageplacer_a.c();
        BlockPosition blockposition = worldgenfoilageplacer_a.a().up(l);

        this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_a.b(), -1 - j, flag);
        this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k - 1, -j, flag);
        this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, blockposition, k + worldgenfoilageplacer_a.b() - 1, 0, flag);
    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return 0;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return j == 0 ? (i > 1 || k > 1) && i != 0 && k != 0 : i == l && k == l && l > 0;
    }
}
