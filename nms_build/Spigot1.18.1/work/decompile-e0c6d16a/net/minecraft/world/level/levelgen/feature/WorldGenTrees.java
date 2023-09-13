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

    public static boolean isFree(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return validTreePos(virtuallevelreadable, blockposition) || virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is((Tag) TagsBlock.LOGS);
        });
    }

    private static boolean isVine(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(Blocks.VINE);
        });
    }

    private static boolean isBlockWater(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(Blocks.WATER);
        });
    }

    public static boolean isAirOrLeaves(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.isAir() || iblockdata.is((Tag) TagsBlock.LEAVES);
        });
    }

    private static boolean isReplaceablePlant(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            Material material = iblockdata.getMaterial();

            return material == Material.REPLACEABLE_PLANT;
        });
    }

    private static void setBlockKnownShape(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setBlock(blockposition, iblockdata, 19);
    }

    public static boolean validTreePos(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return isAirOrLeaves(virtuallevelreadable, blockposition) || isReplaceablePlant(virtuallevelreadable, blockposition) || isBlockWater(virtuallevelreadable, blockposition);
    }

    private boolean doPlace(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition, BiConsumer<BlockPosition, IBlockData> biconsumer, BiConsumer<BlockPosition, IBlockData> biconsumer1, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        int i = worldgenfeaturetreeconfiguration.trunkPlacer.getTreeHeight(random);
        int j = worldgenfeaturetreeconfiguration.foliagePlacer.foliageHeight(random, i, worldgenfeaturetreeconfiguration);
        int k = i - j;
        int l = worldgenfeaturetreeconfiguration.foliagePlacer.foliageRadius(random, k);

        if (blockposition.getY() >= generatoraccessseed.getMinBuildHeight() + 1 && blockposition.getY() + i + 1 <= generatoraccessseed.getMaxBuildHeight()) {
            OptionalInt optionalint = worldgenfeaturetreeconfiguration.minimumSize.minClippedHeight();
            int i1 = this.getMaxFreeTreeHeight(generatoraccessseed, i, blockposition, worldgenfeaturetreeconfiguration);

            if (i1 < i && (!optionalint.isPresent() || i1 < optionalint.getAsInt())) {
                return false;
            } else {
                List<WorldGenFoilagePlacer.a> list = worldgenfeaturetreeconfiguration.trunkPlacer.placeTrunk(generatoraccessseed, biconsumer, random, i1, blockposition, worldgenfeaturetreeconfiguration);

                list.forEach((worldgenfoilageplacer_a) -> {
                    worldgenfeaturetreeconfiguration.foliagePlacer.createFoliage(generatoraccessseed, biconsumer1, random, worldgenfeaturetreeconfiguration, i1, worldgenfoilageplacer_a, j, l);
                });
                return true;
            }
        } else {
            return false;
        }
    }

    private int getMaxFreeTreeHeight(VirtualLevelReadable virtuallevelreadable, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j <= i + 1; ++j) {
            int k = worldgenfeaturetreeconfiguration.minimumSize.getSizeAtHeight(i, j);

            for (int l = -k; l <= k; ++l) {
                for (int i1 = -k; i1 <= k; ++i1) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, l, j, i1);
                    if (!isFree(virtuallevelreadable, blockposition_mutableblockposition) || !worldgenfeaturetreeconfiguration.ignoreVines && isVine(virtuallevelreadable, blockposition_mutableblockposition)) {
                        return j - 2;
                    }
                }
            }
        }

        return i;
    }

    @Override
    protected void setBlock(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        setBlockKnownShape(iworldwriter, blockposition, iblockdata);
    }

    @Override
    public final boolean place(FeaturePlaceContext<WorldGenFeatureTreeConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        Random random = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration = (WorldGenFeatureTreeConfiguration) featureplacecontext.config();
        Set<BlockPosition> set = Sets.newHashSet();
        Set<BlockPosition> set1 = Sets.newHashSet();
        Set<BlockPosition> set2 = Sets.newHashSet();
        BiConsumer<BlockPosition, IBlockData> biconsumer = (blockposition1, iblockdata) -> {
            set.add(blockposition1.immutable());
            generatoraccessseed.setBlock(blockposition1, iblockdata, 19);
        };
        BiConsumer<BlockPosition, IBlockData> biconsumer1 = (blockposition1, iblockdata) -> {
            set1.add(blockposition1.immutable());
            generatoraccessseed.setBlock(blockposition1, iblockdata, 19);
        };
        BiConsumer<BlockPosition, IBlockData> biconsumer2 = (blockposition1, iblockdata) -> {
            set2.add(blockposition1.immutable());
            generatoraccessseed.setBlock(blockposition1, iblockdata, 19);
        };
        boolean flag = this.doPlace(generatoraccessseed, random, blockposition, biconsumer, biconsumer1, worldgenfeaturetreeconfiguration);

        if (flag && (!set.isEmpty() || !set1.isEmpty())) {
            if (!worldgenfeaturetreeconfiguration.decorators.isEmpty()) {
                List<BlockPosition> list = Lists.newArrayList(set);
                List<BlockPosition> list1 = Lists.newArrayList(set1);

                list.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                list1.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                worldgenfeaturetreeconfiguration.decorators.forEach((worldgenfeaturetree) -> {
                    worldgenfeaturetree.place(generatoraccessseed, biconsumer2, random, list, list1);
                });
            }

            return (Boolean) StructureBoundingBox.encapsulatingPositions(Iterables.concat(set, set1, set2)).map((structureboundingbox) -> {
                VoxelShapeDiscrete voxelshapediscrete = updateLeaves(generatoraccessseed, structureboundingbox, set, set2);

                DefinedStructure.updateShapeAtEdge(generatoraccessseed, 3, voxelshapediscrete, structureboundingbox.minX(), structureboundingbox.minY(), structureboundingbox.minZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }

    private static VoxelShapeDiscrete updateLeaves(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Set<BlockPosition> set, Set<BlockPosition> set1) {
        List<Set<BlockPosition>> list = Lists.newArrayList();
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(structureboundingbox.getXSpan(), structureboundingbox.getYSpan(), structureboundingbox.getZSpan());
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            list.add(Sets.newHashSet());
        }

        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = Lists.newArrayList(set1).iterator();

        BlockPosition blockposition;

        while (iterator.hasNext()) {
            blockposition = (BlockPosition) iterator.next();
            if (structureboundingbox.isInside(blockposition)) {
                voxelshapebitset.fill(blockposition.getX() - structureboundingbox.minX(), blockposition.getY() - structureboundingbox.minY(), blockposition.getZ() - structureboundingbox.minZ());
            }
        }

        iterator = Lists.newArrayList(set).iterator();

        while (iterator.hasNext()) {
            blockposition = (BlockPosition) iterator.next();
            if (structureboundingbox.isInside(blockposition)) {
                voxelshapebitset.fill(blockposition.getX() - structureboundingbox.minX(), blockposition.getY() - structureboundingbox.minY(), blockposition.getZ() - structureboundingbox.minZ());
            }

            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection);
                if (!set.contains(blockposition_mutableblockposition)) {
                    IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition);

                    if (iblockdata.hasProperty(BlockProperties.DISTANCE)) {
                        ((Set) list.get(0)).add(blockposition_mutableblockposition.immutable());
                        setBlockKnownShape(generatoraccess, blockposition_mutableblockposition, (IBlockData) iblockdata.setValue(BlockProperties.DISTANCE, 1));
                        if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                            voxelshapebitset.fill(blockposition_mutableblockposition.getX() - structureboundingbox.minX(), blockposition_mutableblockposition.getY() - structureboundingbox.minY(), blockposition_mutableblockposition.getZ() - structureboundingbox.minZ());
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

                if (structureboundingbox.isInside(blockposition1)) {
                    voxelshapebitset.fill(blockposition1.getX() - structureboundingbox.minX(), blockposition1.getY() - structureboundingbox.minY(), blockposition1.getZ() - structureboundingbox.minZ());
                }

                EnumDirection[] aenumdirection1 = EnumDirection.values();
                int i1 = aenumdirection1.length;

                for (int j1 = 0; j1 < i1; ++j1) {
                    EnumDirection enumdirection1 = aenumdirection1[j1];

                    blockposition_mutableblockposition.setWithOffset(blockposition1, enumdirection1);
                    if (!set2.contains(blockposition_mutableblockposition) && !set3.contains(blockposition_mutableblockposition)) {
                        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition_mutableblockposition);

                        if (iblockdata1.hasProperty(BlockProperties.DISTANCE)) {
                            int k1 = (Integer) iblockdata1.getValue(BlockProperties.DISTANCE);

                            if (k1 > l + 1) {
                                IBlockData iblockdata2 = (IBlockData) iblockdata1.setValue(BlockProperties.DISTANCE, l + 1);

                                setBlockKnownShape(generatoraccess, blockposition_mutableblockposition, iblockdata2);
                                if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                                    voxelshapebitset.fill(blockposition_mutableblockposition.getX() - structureboundingbox.minX(), blockposition_mutableblockposition.getY() - structureboundingbox.minY(), blockposition_mutableblockposition.getZ() - structureboundingbox.minZ());
                                }

                                set3.add(blockposition_mutableblockposition.immutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelshapebitset;
    }
}
