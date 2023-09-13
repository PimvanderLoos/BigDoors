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

public class WorldGenFoilagePlacerJungle extends WorldGenFoilagePlacer {

    public static final Codec<WorldGenFoilagePlacerJungle> a = RecordCodecBuilder.create((instance) -> {
        return b(instance).and(Codec.intRange(0, 16).fieldOf("height").forGetter((worldgenfoilageplacerjungle) -> {
            return worldgenfoilageplacerjungle.b;
        })).apply(instance, WorldGenFoilagePlacerJungle::new);
    });
    protected final int b;

    public WorldGenFoilagePlacerJungle(IntSpread intspread, IntSpread intspread1, int i) {
        super(intspread, intspread1);
        this.b = i;
    }

    @Override
    protected WorldGenFoilagePlacers<?> a() {
        return WorldGenFoilagePlacers.g;
    }

    @Override
    protected void a(VirtualLevelWritable virtuallevelwritable, Random random, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, int i, WorldGenFoilagePlacer.b worldgenfoilageplacer_b, int j, int k, Set<BlockPosition> set, int l, StructureBoundingBox structureboundingbox) {
        int i1 = worldgenfoilageplacer_b.c() ? j : 1 + random.nextInt(2);

        for (int j1 = l; j1 >= l - i1; --j1) {
            int k1 = k + worldgenfoilageplacer_b.b() + 1 - j1;

            this.a(virtuallevelwritable, random, worldgenfeaturetreeconfiguration, worldgenfoilageplacer_b.a(), k1, set, j1, worldgenfoilageplacer_b.c(), structureboundingbox);
        }

    }

    @Override
    public int a(Random random, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        return this.b;
    }

    @Override
    protected boolean a(Random random, int i, int j, int k, int l, boolean flag) {
        return i + k >= 7 ? true : i * i + k * k > l * l;
    }
}
