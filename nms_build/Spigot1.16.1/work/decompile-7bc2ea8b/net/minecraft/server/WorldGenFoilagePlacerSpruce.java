package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;

public class WorldGenFoilagePlacerSpruce extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerSpruce> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(instance.group(Codec.INT.fieldOf("trunk_height").forGetter((worldgenfoilageplacerspruce) -> {
            return worldgenfoilageplacerspruce.b;
        }), Codec.INT.fieldOf("trunk_height_random").forGetter((worldgenfoilageplacerspruce) -> {
            return worldgenfoilageplacerspruce.c;
        }))).apply(instance, WorldGenFoilagePlacerSpruce::new);
    });
    private final int b;
    private final int c;

    public WorldGenFoilagePlacerSpruce(int i, int j, int k, int l, int i1, int j1) {
        super(i, j, k, l);
        this.b = i1;
        this.c = j1;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.b;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        BlockPosition blockposition = worldgenfoilageplacer_b.a();
        int i1 = random.nextInt(2);
        int j1 = 1;
        byte b0 = 0;

        for (int k1 = l; k1 >= -j; --k1) {
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, blockposition, i1, set, k1, worldgenfoilageplacer_b.c(), structureboundingbox);
            if (i1 >= j1) {
                i1 = b0;
                b0 = 1;
                j1 = Math.min(j1 + 1, k + worldgenfoilageplacer_b.b());
            } else {
                ++i1;
            }
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return Math.max(4, i - this.b - random.nextInt(this.c + 1));
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && l > 0;
    }
}
