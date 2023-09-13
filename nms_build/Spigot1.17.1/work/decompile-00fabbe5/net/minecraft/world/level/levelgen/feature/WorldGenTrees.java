package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShapeBitSet;
import net.minecraft.world.phys.shapes.VoxelShapeDiscrete;

public class WorldGenTrees extends WorldGenerator<WorldGenFeatureTreeConfiguration> {

    private static final int BLOCK_UPDATE_FLAGS = 19;

    public WorldGenTrees(Codec<WorldGenFeatureTreeConfiguration> codec) {
        super(codec);
    }

    public static boolean c(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return e(virtuallevelreadable, blockposition) || virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.a((Tag) TagsBlock.LOGS);
        });
    }

    private static boolean f(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.a(Blocks.VINE);
        });
    }

    private static boolean g(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.a(Blocks.WATER);
        });
    }

    public static boolean d(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.isAir() || iblockdata.a((Tag) TagsBlock.LEAVES);
        });
    }

    private static boolean h(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Material material = iblockdata.getMaterial();

            return material == Material.REPLACEABLE_PLANT;
        });
    }

    private static void b(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setTypeAndData(blockposition, iblockdata, 19);
    }

    public static boolean e(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return d(virtuallevelreadable, blockposition) || h(virtuallevelreadable, blockposition) || g(virtuallevelreadable, blockposition);
    }

    private boolean a(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition, BiConsumer<BlockPosition, IBlockData> biconsumer, BiConsumer<BlockPosition, IBlockData> biconsumer1, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        int i = worldgenfeaturetreeconfiguration.trunkPlacer.a(random);
        int j = worldgenfeaturetreeconfiguration.foliagePlacer.a(random, i, worldgenfeaturetreeconfiguration);
        int k = i - j;
        int l = worldgenfeaturetreeconfiguration.foliagePlacer.a(random, k);

        if (blockposition.getY() >= generatoraccessseed.getMinBuildHeight() + 1 && blockposition.getY() + i + 1 <= generatoraccessseed.getMaxBuildHeight()) {
            if (!worldgenfeaturetreeconfiguration.saplingProvider.a(random, blockposition).canPlace(generatoraccessseed, blockposition)) {
                return false;
            } else {
                OptionalInt optionalint = worldgenfeaturetreeconfiguration.minimumSize.c();
                int i1 = this.a(generatoraccessseed, i, blockposition, worldgenfeaturetreeconfiguration);

                if (i1 < i && (!optionalint.isPresent() || i1 < optionalint.getAsInt())) {
                    return false;
                } else {
                    List<WorldGenFoilagePlacer.a> list = worldgenfeaturetreeconfiguration.trunkPlacer.a(generatoraccessseed, biconsumer, random, i1, blockposition, worldgenfeaturetreeconfiguration);

                    list.forEach((worldgenfoilageplacer_a) -> {
                        worldgenfeaturetreeconfiguration.foliagePlacer.a(generatoraccessseed, biconsumer1, random, worldgenfeaturetreeconfiguration, i1, worldgenfoilageplacer_a, j, l);
                    });
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private int a(VirtualLevelReadable virtuallevelreadable, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j <= i + 1; ++j) {
            int k = worldgenfeaturetreeconfiguration.minimumSize.a(i, j);

            for (int l = -k; l <= k; ++l) {
                for (int i1 = -k; i1 <= k; ++i1) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, l, j, i1);
                    if (!c(virtuallevelreadable, blockposition_mutableblockposition) || !worldgenfeaturetreeconfiguration.ignoreVines && f(virtuallevelreadable, blockposition_mutableblockposition)) {
                        return j - 2;
                    }
                }
            }
        }

        return i;
    }

    @Override
    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        b(iworldwriter, blockposition, iblockdata);
    }

    @Override
    public final boolean generate(FeaturePlaceContext<WorldGenFeatureTreeConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        Random random = featureplacecontext.c();
        BlockPosition blockposition = featureplacecontext.d();
        WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration = (WorldGenFeatureTreeConfiguration) featureplacecontext.e();
        Set<BlockPosition> set = Sets.newHashSet();
        Set<BlockPosition> set1 = Sets.newHashSet();
        Set<BlockPosition> set2 = Sets.newHashSet();
        BiConsumer<BlockPosition, IBlockData> biconsumer = (blockposition1, iblockdata) -> {
            set.add(blockposition1.immutableCopy());
            generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 19);
        };
        BiConsumer<BlockPosition, IBlockData> biconsumer1 = (blockposition1, iblockdata) -> {
            set1.add(blockposition1.immutableCopy());
            generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 19);
        };
        BiConsumer<BlockPosition, IBlockData> biconsumer2 = (blockposition1, iblockdata) -> {
            set2.add(blockposition1.immutableCopy());
            generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 19);
        };
        boolean flag = this.a(generatoraccessseed, random, blockposition, biconsumer, biconsumer1, worldgenfeaturetreeconfiguration);

        if (flag && (!set.isEmpty() || !set1.isEmpty())) {
            if (!worldgenfeaturetreeconfiguration.decorators.isEmpty()) {
                List<BlockPosition> list = Lists.newArrayList(set);
                List<BlockPosition> list1 = Lists.newArrayList(set1);

                list.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                list1.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                worldgenfeaturetreeconfiguration.decorators.forEach((worldgenfeaturetree) -> {
                    worldgenfeaturetree.a(generatoraccessseed, biconsumer2, random, list, list1);
                });
            }

            return (Boolean) StructureBoundingBox.a(Iterables.concat(set, set1, set2)).map((structureboundingbox) -> {
                VoxelShapeDiscrete voxelshapediscrete = a((GeneratorAccess) generatoraccessseed, structureboundingbox, set, set2);

                DefinedStructure.a(generatoraccessseed, 3, voxelshapediscrete, structureboundingbox.g(), structureboundingbox.h(), structureboundingbox.i());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }

    private static VoxelShapeDiscrete a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Set<BlockPosition> set, Set<BlockPosition> set1) {
        List<Set<BlockPosition>> list = Lists.newArrayList();
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(structureboundingbox.c(), structureboundingbox.d(), structureboundingbox.e());
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            list.add(Sets.newHashSet());
        }

        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = Lists.newArrayList(set1).iterator();

        BlockPosition blockposition;

        while (iterator.hasNext()) {
            blockposition = (BlockPosition) iterator.next();
            if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
                voxelshapebitset.c(blockposition.getX() - structureboundingbox.g(), blockposition.getY() - structureboundingbox.h(), blockposition.getZ() - structureboundingbox.i());
            }
        }

        iterator = Lists.newArrayList(set).iterator();

        while (iterator.hasNext()) {
            blockposition = (BlockPosition) iterator.next();
            if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
                voxelshapebitset.c(blockposition.getX() - structureboundingbox.g(), blockposition.getY() - structureboundingbox.h(), blockposition.getZ() - structureboundingbox.i());
            }

            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection);
                if (!set.contains(blockposition_mutableblockposition)) {
                    IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

                    if (iblockdata.b(BlockProperties.DISTANCE)) {
                        ((Set) list.get(0)).add(blockposition_mutableblockposition.immutableCopy());
                        b(generatoraccess, blockposition_mutableblockposition, (IBlockData) iblockdata.set(BlockProperties.DISTANCE, 1));
                        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                            voxelshapebitset.c(blockposition_mutableblockposition.getX() - structureboundingbox.g(), blockposition_mutableblockposition.getY() - structureboundingbox.h(), blockposition_mutableblockposition.getZ() - structureboundingbox.i());
                        }
                    }
                }
            }
        }

        for (int l = 1; l < 6; ++l) {
            Set<BlockPosition> set2 = (Set) list.get(l - 1);
            Set<BlockPosition> set3 = (Set) list.get(l);
            Iterator iterator1 = set2.iterator();

            while (iterator1.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator1.next();

                if (structureboundingbox.b((BaseBlockPosition) blockposition1)) {
                    voxelshapebitset.c(blockposition1.getX() - structureboundingbox.g(), blockposition1.getY() - structureboundingbox.h(), blockposition1.getZ() - structureboundingbox.i());
                }

                EnumDirection[] aenumdirection1 = EnumDirection.values();
                int i1 = aenumdirection1.length;

                for (int j1 = 0; j1 < i1; ++j1) {
                    EnumDirection enumdirection1 = aenumdirection1[j1];

                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, enumdirection1);
                    if (!set2.contains(blockposition_mutableblockposition) && !set3.contains(blockposition_mutableblockposition)) {
                        IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition);

                        if (iblockdata1.b(BlockProperties.DISTANCE)) {
                            int k1 = (Integer) iblockdata1.get(BlockProperties.DISTANCE);

                            if (k1 > l + 1) {
                                IBlockData iblockdata2 = (IBlockData) iblockdata1.set(BlockProperties.DISTANCE, l + 1);

                                b(generatoraccess, blockposition_mutableblockposition, iblockdata2);
                                if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                                    voxelshapebitset.c(blockposition_mutableblockposition.getX() - structureboundingbox.g(), blockposition_mutableblockposition.getY() - structureboundingbox.h(), blockposition_mutableblockposition.getZ() - structureboundingbox.i());
                                }

                                set3.add(blockposition_mutableblockposition.immutableCopy());
                            }
                        }
                    }
                }
            }
        }

        return voxelshapebitset;
    }
}
