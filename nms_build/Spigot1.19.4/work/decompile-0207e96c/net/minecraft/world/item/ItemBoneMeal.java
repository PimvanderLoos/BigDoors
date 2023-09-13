package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.Particles;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCoralFanWallAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IBlockFragilePlantElement;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemBoneMeal extends Item {

    public static final int GRASS_SPREAD_WIDTH = 3;
    public static final int GRASS_SPREAD_HEIGHT = 1;
    public static final int GRASS_COUNT_MULTIPLIER = 3;

    public ItemBoneMeal(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        BlockPosition blockposition1 = blockposition.relative(itemactioncontext.getClickedFace());

        if (growCrop(itemactioncontext.getItemInHand(), world, blockposition)) {
            if (!world.isClientSide) {
                world.levelEvent(1505, blockposition, 0);
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            IBlockData iblockdata = world.getBlockState(blockposition);
            boolean flag = iblockdata.isFaceSturdy(world, blockposition, itemactioncontext.getClickedFace());

            if (flag && growWaterPlant(itemactioncontext.getItemInHand(), world, blockposition1, itemactioncontext.getClickedFace())) {
                if (!world.isClientSide) {
                    world.levelEvent(1505, blockposition1, 0);
                }

                return EnumInteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public static boolean growCrop(ItemStack itemstack, World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (iblockdata.getBlock() instanceof IBlockFragilePlantElement) {
            IBlockFragilePlantElement iblockfragileplantelement = (IBlockFragilePlantElement) iblockdata.getBlock();

            if (iblockfragileplantelement.isValidBonemealTarget(world, blockposition, iblockdata, world.isClientSide)) {
                if (world instanceof WorldServer) {
                    if (iblockfragileplantelement.isBonemealSuccess(world, world.random, blockposition, iblockdata)) {
                        iblockfragileplantelement.performBonemeal((WorldServer) world, world.random, blockposition, iblockdata);
                    }

                    itemstack.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean growWaterPlant(ItemStack itemstack, World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        if (world.getBlockState(blockposition).is(Blocks.WATER) && world.getFluidState(blockposition).getAmount() == 8) {
            if (!(world instanceof WorldServer)) {
                return true;
            } else {
                RandomSource randomsource = world.getRandom();
                int i = 0;

                while (i < 128) {
                    BlockPosition blockposition1 = blockposition;
                    IBlockData iblockdata = Blocks.SEAGRASS.defaultBlockState();
                    int j = 0;

                    while (true) {
                        if (j < i / 16) {
                            blockposition1 = blockposition1.offset(randomsource.nextInt(3) - 1, (randomsource.nextInt(3) - 1) * randomsource.nextInt(3) / 2, randomsource.nextInt(3) - 1);
                            if (!world.getBlockState(blockposition1).isCollisionShapeFullBlock(world, blockposition1)) {
                                ++j;
                                continue;
                            }
                        } else {
                            Holder<BiomeBase> holder = world.getBiome(blockposition1);

                            if (holder.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                                if (i == 0 && enumdirection != null && enumdirection.getAxis().isHorizontal()) {
                                    iblockdata = (IBlockData) BuiltInRegistries.BLOCK.getTag(TagsBlock.WALL_CORALS).flatMap((holderset_named) -> {
                                        return holderset_named.getRandomElement(world.random);
                                    }).map((holder1) -> {
                                        return ((Block) holder1.value()).defaultBlockState();
                                    }).orElse(iblockdata);
                                    if (iblockdata.hasProperty(BlockCoralFanWallAbstract.FACING)) {
                                        iblockdata = (IBlockData) iblockdata.setValue(BlockCoralFanWallAbstract.FACING, enumdirection);
                                    }
                                } else if (randomsource.nextInt(4) == 0) {
                                    iblockdata = (IBlockData) BuiltInRegistries.BLOCK.getTag(TagsBlock.UNDERWATER_BONEMEALS).flatMap((holderset_named) -> {
                                        return holderset_named.getRandomElement(world.random);
                                    }).map((holder1) -> {
                                        return ((Block) holder1.value()).defaultBlockState();
                                    }).orElse(iblockdata);
                                }
                            }

                            if (iblockdata.is(TagsBlock.WALL_CORALS, (blockbase_blockdata) -> {
                                return blockbase_blockdata.hasProperty(BlockCoralFanWallAbstract.FACING);
                            })) {
                                for (int k = 0; !iblockdata.canSurvive(world, blockposition1) && k < 4; ++k) {
                                    iblockdata = (IBlockData) iblockdata.setValue(BlockCoralFanWallAbstract.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource));
                                }
                            }

                            if (iblockdata.canSurvive(world, blockposition1)) {
                                IBlockData iblockdata1 = world.getBlockState(blockposition1);

                                if (iblockdata1.is(Blocks.WATER) && world.getFluidState(blockposition1).getAmount() == 8) {
                                    world.setBlock(blockposition1, iblockdata, 3);
                                } else if (iblockdata1.is(Blocks.SEAGRASS) && randomsource.nextInt(10) == 0) {
                                    ((IBlockFragilePlantElement) Blocks.SEAGRASS).performBonemeal((WorldServer) world, randomsource, blockposition1, iblockdata1);
                                }
                            }
                        }

                        ++i;
                        break;
                    }
                }

                itemstack.shrink(1);
                return true;
            }
        } else {
            return false;
        }
    }

    public static void addGrowthParticles(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        if (i == 0) {
            i = 15;
        }

        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        if (!iblockdata.isAir()) {
            double d0 = 0.5D;
            double d1;

            if (iblockdata.is(Blocks.WATER)) {
                i *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (iblockdata.isSolidRender(generatoraccess, blockposition)) {
                blockposition = blockposition.above();
                i *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = iblockdata.getShape(generatoraccess, blockposition).max(EnumDirection.EnumAxis.Y);
            }

            generatoraccess.addParticle(Particles.HAPPY_VILLAGER, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            RandomSource randomsource = generatoraccess.getRandom();

            for (int j = 0; j < i; ++j) {
                double d2 = randomsource.nextGaussian() * 0.02D;
                double d3 = randomsource.nextGaussian() * 0.02D;
                double d4 = randomsource.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = (double) blockposition.getX() + d5 + randomsource.nextDouble() * d0 * 2.0D;
                double d7 = (double) blockposition.getY() + randomsource.nextDouble() * d1;
                double d8 = (double) blockposition.getZ() + d5 + randomsource.nextDouble() * d0 * 2.0D;

                if (!generatoraccess.getBlockState(BlockPosition.containing(d6, d7, d8).below()).isAir()) {
                    generatoraccess.addParticle(Particles.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
                }
            }

        }
    }
}
