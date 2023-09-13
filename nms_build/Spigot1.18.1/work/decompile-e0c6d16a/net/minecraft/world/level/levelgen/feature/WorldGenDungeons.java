package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityTypes<?>[] MOBS = new EntityTypes[]{EntityTypes.SKELETON, EntityTypes.ZOMBIE, EntityTypes.ZOMBIE, EntityTypes.SPIDER};
    private static final IBlockData AIR = Blocks.CAVE_AIR.defaultBlockState();

    public WorldGenDungeons(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        Predicate<IBlockData> predicate = WorldGenerator.isReplaceable(TagsBlock.FEATURES_CANNOT_REPLACE.getName());
        BlockPosition blockposition = featureplacecontext.origin();
        Random random = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        boolean flag = true;
        int i = random.nextInt(2) + 2;
        int j = -i - 1;
        int k = i + 1;
        boolean flag1 = true;
        boolean flag2 = true;
        int l = random.nextInt(2) + 2;
        int i1 = -l - 1;
        int j1 = l + 1;
        int k1 = 0;

        BlockPosition blockposition1;
        int l1;
        int i2;
        int j2;

        for (l1 = j; l1 <= k; ++l1) {
            for (i2 = -1; i2 <= 4; ++i2) {
                for (j2 = i1; j2 <= j1; ++j2) {
                    blockposition1 = blockposition.offset(l1, i2, j2);
                    Material material = generatoraccessseed.getBlockState(blockposition1).getMaterial();
                    boolean flag3 = material.isSolid();

                    if (i2 == -1 && !flag3) {
                        return false;
                    }

                    if (i2 == 4 && !flag3) {
                        return false;
                    }

                    if ((l1 == j || l1 == k || j2 == i1 || j2 == j1) && i2 == 0 && generatoraccessseed.isEmptyBlock(blockposition1) && generatoraccessseed.isEmptyBlock(blockposition1.above())) {
                        ++k1;
                    }
                }
            }
        }

        if (k1 >= 1 && k1 <= 5) {
            for (l1 = j; l1 <= k; ++l1) {
                for (i2 = 3; i2 >= -1; --i2) {
                    for (j2 = i1; j2 <= j1; ++j2) {
                        blockposition1 = blockposition.offset(l1, i2, j2);
                        IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition1);

                        if (l1 != j && i2 != -1 && j2 != i1 && l1 != k && i2 != 4 && j2 != j1) {
                            if (!iblockdata.is(Blocks.CHEST) && !iblockdata.is(Blocks.SPAWNER)) {
                                this.safeSetBlock(generatoraccessseed, blockposition1, WorldGenDungeons.AIR, predicate);
                            }
                        } else if (blockposition1.getY() >= generatoraccessseed.getMinBuildHeight() && !generatoraccessseed.getBlockState(blockposition1.below()).getMaterial().isSolid()) {
                            generatoraccessseed.setBlock(blockposition1, WorldGenDungeons.AIR, 2);
                        } else if (iblockdata.getMaterial().isSolid() && !iblockdata.is(Blocks.CHEST)) {
                            if (i2 == -1 && random.nextInt(4) != 0) {
                                this.safeSetBlock(generatoraccessseed, blockposition1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
                            } else {
                                this.safeSetBlock(generatoraccessseed, blockposition1, Blocks.COBBLESTONE.defaultBlockState(), predicate);
                            }
                        }
                    }
                }
            }

            l1 = 0;

            while (l1 < 2) {
                i2 = 0;

                while (true) {
                    if (i2 < 3) {
                        label100:
                        {
                            j2 = blockposition.getX() + random.nextInt(i * 2 + 1) - i;
                            int k2 = blockposition.getY();
                            int l2 = blockposition.getZ() + random.nextInt(l * 2 + 1) - l;
                            BlockPosition blockposition2 = new BlockPosition(j2, k2, l2);

                            if (generatoraccessseed.isEmptyBlock(blockposition2)) {
                                int i3 = 0;
                                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator.hasNext()) {
                                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                                    if (generatoraccessseed.getBlockState(blockposition2.relative(enumdirection)).getMaterial().isSolid()) {
                                        ++i3;
                                    }
                                }

                                if (i3 == 1) {
                                    this.safeSetBlock(generatoraccessseed, blockposition2, StructurePiece.reorient(generatoraccessseed, blockposition2, Blocks.CHEST.defaultBlockState()), predicate);
                                    TileEntityLootable.setLootTable(generatoraccessseed, random, blockposition2, LootTables.SIMPLE_DUNGEON);
                                    break label100;
                                }
                            }

                            ++i2;
                            continue;
                        }
                    }

                    ++l1;
                    break;
                }
            }

            this.safeSetBlock(generatoraccessseed, blockposition, Blocks.SPAWNER.defaultBlockState(), predicate);
            TileEntity tileentity = generatoraccessseed.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawner().setEntityId(this.randomEntityId(random));
            } else {
                WorldGenDungeons.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockposition.getX(), blockposition.getY(), blockposition.getZ());
            }

            return true;
        } else {
            return false;
        }
    }

    private EntityTypes<?> randomEntityId(Random random) {
        return (EntityTypes) SystemUtils.getRandom((Object[]) WorldGenDungeons.MOBS, random);
    }
}
