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

public class WorldGenFoilagePlacerJungle extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerJungle> CODEC = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter((worldgenfoilageplacerjungle) -> {
            return worldgenfoilageplacerjungle.height;
        })).apply(instance, WorldGenFoilagePlacerJungle::new);
    });
    protected final int height;

    public WorldGenFoilagePlacerJungle(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1);
        this.height = i;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.MEGA_JUNGLE_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        int i1 = worldgenfoilageplacer_a.c() ? j : 1 + random.nextInt(2);

        for (int j1 = l; j1 >= l - i1; --j1) {
            int k1 = k + worldgenfoilageplacer_a.b() + 1 - j1;

            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.a(), k1, j1, worldgenfoilageplacer_a.c());
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.height;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i + k >= 7 ? true : i * i + k * k > l * l;
    }
}
