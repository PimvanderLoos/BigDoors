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
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        BlockPosition blockposition1 = blockposition.shift(itemactioncontext.getClickedFace());

        if (a(itemactioncontext.getItemStack(), world, blockposition)) {
            if (!world.isClientSide) {
                world.triggerEffect(1505, blockposition, 0);
            }

            return EnumInteractionResult.a(world.isClientSide);
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            boolean flag = iblockdata.d(world, blockposition, itemactioncontext.getClickedFace());

            if (flag && a(itemactioncontext.getItemStack(), world, blockposition1, itemactioncontext.getClickedFace())) {
                if (!world.isClientSide) {
                    world.triggerEffect(1505, blockposition1, 0);
                }

                return EnumInteractionResult.a(world.isClientSide);
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public static boolean a(ItemStack itemstack, World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() instanceof IBlockFragilePlantElement) {
            IBlockFragilePlantElement iblockfragileplantelement = (IBlockFragilePlantElement) iblockdata.getBlock();

            if (iblockfragileplantelement.a(world, blockposition, iblockdata, world.isClientSide)) {
                if (world instanceof WorldServer) {
                    if (iblockfragileplantelement.a(world, world.random, blockposition, iblockdata)) {
                        iblockfragileplantelement.a((WorldServer) world, world.random, blockposition, iblockdata);
                    }

                    itemstack.subtract(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean a(ItemStack itemstack, World world, BlockPosition blockposition, @Nullable EnumDirection enumdirection) {
        if (world.getType(blockposition).a(Blocks.WATER) && world.getFluid(blockposition).e() == 8) {
            if (!(world instanceof WorldServer)) {
                return true;
            } else {
                Random random = world.getRandom();
                int i = 0;

                while (i < 128) {
                    BlockPosition blockposition1 = blockposition;
                    IBlockData iblockdata = Blocks.SEAGRASS.getBlockData();
                    int j = 0;

                    while (true) {
                        if (j < i / 16) {
                            blockposition1 = blockposition1.c(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                            if (!world.getType(blockposition1).r(world, blockposition1)) {
                                ++j;
                                continue;
                            }
                        } else {
                            Optional<ResourceKey<BiomeBase>> optional = world.j(blockposition1);

                            if (Objects.equals(optional, Optional.of(Biomes.WARM_OCEAN)) || Objects.equals(optional, Optional.of(Biomes.DEEP_WARM_OCEAN))) {
                                if (i == 0 && enumdirection != null && enumdirection.n().d()) {
                                    iblockdata = (IBlockData) ((Block) TagsBlock.WALL_CORALS.a(world.random)).getBlockData().set(BlockCoralFanWallAbstract.FACING, enumdirection);
                                } else if (random.nextInt(4) == 0) {
                                    iblockdata = ((Block) TagsBlock.UNDERWATER_BONEMEALS.a(random)).getBlockData();
                                }
                            }

                            if (iblockdata.a((Tag) TagsBlock.WALL_CORALS)) {
                                for (int k = 0; !iblockdata.canPlace(world, blockposition1) && k < 4; ++k) {
                                    iblockdata = (IBlockData) iblockdata.set(BlockCoralFanWallAbstract.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random));
                                }
                            }

                            if (iblockdata.canPlace(world, blockposition1)) {
                                IBlockData iblockdata1 = world.getType(blockposition1);

                                if (iblockdata1.a(Blocks.WATER) && world.getFluid(blockposition1).e() == 8) {
                                    world.setTypeAndData(blockposition1, iblockdata, 3);
                                } else if (iblockdata1.a(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                                    ((IBlockFragilePlantElement) Blocks.SEAGRASS).a((WorldServer) world, random, blockposition1, iblockdata1);
                                }
                            }
                        }

                        ++i;
                        break;
                    }
                }

                itemstack.subtract(1);
                return true;
            }
        } else {
            return false;
        }
    }

    public static void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        if (i == 0) {
            i = 15;
        }

        IBlockData iblockdata = generatoraccess.getType(blockposition);

        if (!iblockdata.isAir()) {
            double d0 = 0.5D;
            double d1;

            if (iblockdata.a(Blocks.WATER)) {
                i *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (iblockdata.i(generatoraccess, blockposition)) {
                blockposition = blockposition.up();
                i *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = iblockdata.getShape(generatoraccess, blockposition).c(EnumDirection.EnumAxis.Y);
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

                if (!generatoraccess.getType((new BlockPosition(d6, d7, d8)).down()).isAir()) {
                    generatoraccess.addParticle(Particles.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
                }
            }

        }
    }
}
