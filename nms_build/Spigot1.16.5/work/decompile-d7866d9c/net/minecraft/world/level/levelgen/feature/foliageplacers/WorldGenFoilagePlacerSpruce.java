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

public class WorldGenFoilagePlacerSpruce extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerSpruce> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(IntSpread.a(0, 16, 8).fieldOf("trunk_height").forGetter((worldgenfoilageplacerspruce) -> {
            return worldgenfoilageplacerspruce.b;
        })).apply(instance, WorldGenFoilagePlacerSpruce::new);
    });
    private final IntSpread b;

    public WorldGenFoilagePlacerSpruce(IntSpread intspread, IntSpread intspread1, IntSpread intspread2) {
        super(intspread, intspread1);
        this.b = intspread2;
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
        return Math.max(4, i - this.b.a(random));
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i == l && k == l && l > 0;
    }
}
