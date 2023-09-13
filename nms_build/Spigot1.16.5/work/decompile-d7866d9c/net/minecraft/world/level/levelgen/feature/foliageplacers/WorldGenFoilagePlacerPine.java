package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.IntSpread;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFoilagePlacerPine extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerPine> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(IntSpread.a(0, 16, 8).fieldOf("height").forGetter((worldgenfoilageplacerpine) -> {
            return worldgenfoilageplacerpine.b;
        })).apply(instance, WorldGenFoilagePlacerPine::new);
    });
    private final IntSpread b;

    public WorldGenFoilagePlacerPine(IntSpread intspread, IntSpread intspread1, IntSpread intspread2) {
        super(intspread, intspread1);
        this.b = intspread2;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.c;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        int i1 = 0;

        for (int j1 = l; j1 >= l - j; --j1) {
            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_b.a(), i1, set, j1, worldgenfoilageplacer_b.c(), structureboundingbox);
            if (i1 >= 1 && j1 == l - j + 1) {
                --i1;
            } else if (i1 < k + worldgenfoilageplacer_b.b()) {
                ++i1;
            }
        }

    }

    @Override
    public int a(Random random, int i) {
        return super.a(random, i) + random.nextInt(i + 1);
    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.b.a(random);
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && l > 0;
    }
}
