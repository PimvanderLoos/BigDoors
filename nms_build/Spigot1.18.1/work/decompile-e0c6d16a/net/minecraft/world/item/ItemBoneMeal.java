package net.minecraft.world.item;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
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
                Random random = world.getRandom();
                int i = 0;

                while (i < 128) {
                    BlockPosition blockposition1 = blockposition;
                    IBlockData iblockdata = Blocks.SEAGRASS.defaultBlockState();
                    int j = 0;

                    while (true) {
                        if (j < i / 16) {
                            blockposition1 = blockposition1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                            if (!world.getBlockState(blockposition1).isCollisionShapeFullBlock(world, blockposition1)) {
                                ++j;
                                continue;
                            }
                        } else {
                            Optional<ResourceKey<BiomeBase>> optional = world.getBiomeName(blockposition1);

                            if (Objects.equals(optional, Optional.of(Biomes.WARM_OCEAN))) {
                                if (i == 0 && enumdirection != null && enumdirection.getAxis().isHorizontal()) {
                                    iblockdata = (IBlockData) ((Block) TagsBlock.WALL_CORALS.getRandomElement(world.random)).defaultBlockState().setValue(BlockCoralFanWallAbstract.FACING, enumdirection);
                                } else if (random.nextInt(4) == 0) {
                                    iblockdata = ((Block) TagsBlock.UNDERWATER_BONEMEALS.getRandomElement(random)).defaultBlockState();
                                }
                            }

                            if (iblockdata.is((Tag) TagsBlock.WALL_CORALS)) {
                                for (int k = 0; !iblockdata.canSurvive(world, blockposition1) && k < 4; ++k) {
                                    iblockdata = (IBlockData) iblockdata.setValue(BlockCoralFanWallAbstract.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(random));
                                }
                            }

                            if (iblockdata.canSurvive(world, blockposition1)) {
                                IBlockData iblockdata1 = world.getBlockState(blockposition1);

                                if (iblockdata1.is(Blocks.WATER) && world.getFluidState(blockposition1).getAmount() == 8) {
                                    world.setBlock(blockposition1, iblockdata, 3);
                                } else if (iblockdata1.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                                    ((IBlockFragilePlantElement) Blocks.SEAGRASS).performBonemeal((WorldServer) world, random, blockposition1, iblockdata1);
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
            Random random = generatoraccess.getRandom();

            for (int j = 0; j < i; ++j) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = (double) blockposition.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double d7 = (double) blockposition.getY() + random.nextDouble() * d1;
                double d8 = (double) blockposition.getZ() + d5 + random.nextDouble() * d0 * 2.0D;

                if (!generatoraccess.getBlockState((new BlockPosition(d6, d7, d8)).below()).isAir()) {
                    generatoraccess.addParticle(Particles.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
                }
            }

        }
    }
}
