package net.minecraft.server;

import com.mojang.datafixers.Products.P5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerBlob extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerBlob> a = RecordCodecBuilder.create((instance) -> {
        return a(instance).apply(instance, WorldGenFoilagePlacerBlob::new);
    });
    protected final int b;

    protected static <P extends WorldGenFoilagePlacerBlob> P5<Mu<P>, Integer, Integer, Integer, Integer, Integer> a(Instance<P> instance) {
        return b(instance).and(Codec.INT.fieldOf("height").forGetter((worldgenfoilageplacerblob) -> {
            return worldgenfoilageplacerblob.b;
        }));
    }

    protected WorldGenFoilagePlacerBlob(int i, int j, int k, int l, int i1, WorldGenFoilagePlacers<?> worldgenfoilageplacers) {
        super(i, j, k, l);
        this.b = i1;
    }

    public WorldGenFoilagePlacerBlob(int i, int j, int k, int l, int i1) {
        this(i, j, k, l, i1, WorldGenFoilagePlacers.a);
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.a;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        for (int i1 = l; i1 >= l - j; --i1) {
            int j1 = Math.max(k + worldgenfoilageplacer_b.b() - 1 - i1 / 2, 0);

            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_b.a(), j1, set, i1, worldgenfoilageplacer_b.c(), structureboundingbox);
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.b;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && (random.nextInt(2) == 0 || j == 0);
    }
}
