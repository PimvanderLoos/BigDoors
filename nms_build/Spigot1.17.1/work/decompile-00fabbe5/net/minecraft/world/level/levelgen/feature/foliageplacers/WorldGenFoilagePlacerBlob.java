package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenFoilagePlacerBlob extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerBlob> CODEC = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, WorldGenFoilagePlacerBlob::new);
    });
    protected final int height;

    protected static <P extends WorldGenFoilagePlacerBlob> P3<Mu<P>, IntProvider, IntProvider, Integer> a(Instance<P> instance) {
        return b(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter((worldgenfoilageplacerblob) -> {
            return worldgenfoilageplacerblob.height;
        }));
    }

    public WorldGenFoilagePlacerBlob(IntProvider intprovider, IntProvider intprovider1, int i) {
        super(intprovider, intprovider1);
        this.height = i;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.BLOB_FOLIAGE_PLACER;
    }

    @Override
    protected void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.a worldgenfoilageplacer_a, int j, int k, int l) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = Math.max(k + worldgenfoilageplacer_a.b() - 1 - i1 / 2, 0);

            this.a(virtuallevelreadable, biconsumer, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_a.a(), j1, i1, worldgenfoilageplacer_a.c());
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.height;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && (random.nextInt(2) == 0 || j == 0);
    }
}
