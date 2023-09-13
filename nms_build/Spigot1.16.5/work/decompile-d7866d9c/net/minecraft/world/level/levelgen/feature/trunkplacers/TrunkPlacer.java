package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.VirtualLevelWritable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public abstract class TrunkPlacer {

    public static final Codec<TrunkPlacer> c = IRegistry.TRUNK_PLACER_TYPE.dispatch(TrunkPlacer::a, TrunkPlacers::a);
    protected final int d;
    protected final int e;
    protected final int f;

    protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> a(Instance<P> instance) {
        return instance.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((trunkplacer) -> {
            return trunkplacer.d;
        }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((trunkplacer) -> {
            return trunkplacer.e;
        }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((trunkplacer) -> {
            return trunkplacer.f;
        }));
    }

    public TrunkPlacer(int i, int j, int k) {
        this.d = i;
        this.e = j;
        this.f = k;
    }

    protected abstract TrunkPlacers<?> a();

    public abstract List<WorldGenFoilagePlacer.b> a(VirtualLevelWritable virtuallevelwritable, Random random, int i, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    public int a(Random random) {
        return this.d + random.nextInt(this.e + 1) + random.nextInt(this.f + 1);
    }

    protected static void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata, StructureBoundingBox structureboundingbox) {
        WorldGenTrees.b(iworldwriter, blockposition, iblockdata);
        structureboundingbox.c(new StructureBoundingBox(blockposition, blockposition));
    }

    private static boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Block block = iblockdata.getBlock();

            return WorldGenerator.b(block) && !iblockdata.a(Blocks.GRASS_BLOCK) && !iblockdata.a(Blocks.MYCELIUM);
        });
    }

    protected static void a(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition) {
        if (!a((VirtualLevelReadable) virtuallevelwritable, blockposition)) {
            WorldGenTrees.b(virtuallevelwritable, blockposition, Blocks.DIRT.getBlockData());
        }

    }

    protected static boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (WorldGenTrees.e(virtuallevelwritable, blockposition)) {
            a(virtuallevelwritable, blockposition, worldgenfeaturetreeconfiguration.b.a(random, blockposition), structureboundingbox);
            set.add(blockposition.immutableCopy());
            return true;
        } else {
            return false;
        }
    }

    protected static void a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (WorldGenTrees.c(virtuallevelwritable, blockposition_mutableblockposition)) {
            a(virtuallevelwritable, random, (BlockPosition) blockposition_mutableblockposition, set, structureboundingbox, worldgenfeaturetreeconfiguration);
        }

    }
}
