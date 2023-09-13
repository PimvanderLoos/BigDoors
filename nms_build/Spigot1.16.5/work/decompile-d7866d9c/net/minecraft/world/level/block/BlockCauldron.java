package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.IDyeable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBanner;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityBanner;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockCauldron extends Block {

    public static final BlockStateInteger LEVEL = BlockProperties.ar;
    private static final VoxelShape c = a(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape b = VoxelShapes.a(VoxelShapes.b(), VoxelShapes.a(a(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), a(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), a(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), BlockCauldron.c), OperatorBoolean.ONLY_FIRST);

    public BlockCauldron(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCauldron.LEVEL, 0));
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCauldron.b;
    }

    @Override
    public VoxelShape a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCauldron.c;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        int i = (Integer) iblockdata.get(BlockCauldron.LEVEL);
        float f = (float) blockposition.getY() + (6.0F + (float) (3 * i)) / 16.0F;

        if (!world.isClientSide && entity.isBurning() && i > 0 && entity.locY() <= (double) f) {
            entity.extinguish();
            this.a(world, blockposition, iblockdata, i - 1);
        }

    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.isEmpty()) {
            return EnumInteractionResult.PASS;
        } else {
            int i = (Integer) iblockdata.get(BlockCauldron.LEVEL);
            Item item = itemstack.getItem();

            if (item == Items.WATER_BUCKET) {
                if (i < 3 && !world.isClientSide) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        entityhuman.a(enumhand, new ItemStack(Items.BUCKET));
                    }

                    entityhuman.a(StatisticList.FILL_CAULDRON);
                    this.a(world, blockposition, iblockdata, 3);
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return EnumInteractionResult.a(world.isClientSide);
            } else if (item == Items.BUCKET) {
                if (i == 3 && !world.isClientSide) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                        if (itemstack.isEmpty()) {
                            entityhuman.a(enumhand, new ItemStack(Items.WATER_BUCKET));
                        } else if (!entityhuman.inventory.pickup(new ItemStack(Items.WATER_BUCKET))) {
                            entityhuman.drop(new ItemStack(Items.WATER_BUCKET), false);
                        }
                    }

                    entityhuman.a(StatisticList.USE_CAULDRON);
                    this.a(world, blockposition, iblockdata, 0);
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return EnumInteractionResult.a(world.isClientSide);
            } else {
                ItemStack itemstack1;

                if (item == Items.GLASS_BOTTLE) {
                    if (i > 0 && !world.isClientSide) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack1 = PotionUtil.a(new ItemStack(Items.POTION), Potions.WATER);
                            entityhuman.a(StatisticList.USE_CAULDRON);
                            itemstack.subtract(1);
                            if (itemstack.isEmpty()) {
                                entityhuman.a(enumhand, itemstack1);
                            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                                entityhuman.drop(itemstack1, false);
                            } else if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.a(world, blockposition, iblockdata, i - 1);
                    }

                    return EnumInteractionResult.a(world.isClientSide);
                } else if (item == Items.POTION && PotionUtil.d(itemstack) == Potions.WATER) {
                    if (i < 3 && !world.isClientSide) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack1 = new ItemStack(Items.GLASS_BOTTLE);
                            entityhuman.a(StatisticList.USE_CAULDRON);
                            entityhuman.a(enumhand, itemstack1);
                            if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.a(world, blockposition, iblockdata, i + 1);
                    }

                    return EnumInteractionResult.a(world.isClientSide);
                } else {
                    if (i > 0 && item instanceof IDyeable) {
                        IDyeable idyeable = (IDyeable) item;

                        if (idyeable.a(itemstack) && !world.isClientSide) {
                            idyeable.c(itemstack);
                            this.a(world, blockposition, iblockdata, i - 1);
                            entityhuman.a(StatisticList.CLEAN_ARMOR);
                            return EnumInteractionResult.SUCCESS;
                        }
                    }

                    if (i > 0 && item instanceof ItemBanner) {
                        if (TileEntityBanner.b(itemstack) > 0 && !world.isClientSide) {
                            itemstack1 = itemstack.cloneItemStack();
                            itemstack1.setCount(1);
                            TileEntityBanner.c(itemstack1);
                            entityhuman.a(StatisticList.CLEAN_BANNER);
                            if (!entityhuman.abilities.canInstantlyBuild) {
                                itemstack.subtract(1);
                                this.a(world, blockposition, iblockdata, i - 1);
                            }

                            if (itemstack.isEmpty()) {
                                entityhuman.a(enumhand, itemstack1);
                            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                                entityhuman.drop(itemstack1, false);
                            } else if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        return EnumInteractionResult.a(world.isClientSide);
                    } else if (i > 0 && item instanceof ItemBlock) {
                        Block block = ((ItemBlock) item).getBlock();

                        if (block instanceof BlockShulkerBox && !world.s_()) {
                            ItemStack itemstack2 = new ItemStack(Blocks.SHULKER_BOX, 1);

                            if (itemstack.hasTag()) {
                                itemstack2.setTag(itemstack.getTag().clone());
                            }

                            entityhuman.a(enumhand, itemstack2);
                            this.a(world, blockposition, iblockdata, i - 1);
                            entityhuman.a(StatisticList.CLEAN_SHULKER_BOX);
                            return EnumInteractionResult.SUCCESS;
                        } else {
                            return EnumInteractionResult.CONSUME;
                        }
                    } else {
                        return EnumInteractionResult.PASS;
                    }
                }
            }
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCauldron.LEVEL, MathHelper.clamp(i, 0, 3)), 2);
        world.updateAdjacentComparators(blockposition, this);
    }

    @Override
    public void c(World world, BlockPosition blockposition) {
        if (world.random.nextInt(20) == 1) {
            float f = world.getBiome(blockposition).getAdjustedTemperature(blockposition);

            if (f >= 0.15F) {
                IBlockData iblockdata = world.getType(blockposition);

                if ((Integer) iblockdata.get(BlockCauldron.LEVEL) < 3) {
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockCauldron.LEVEL), 2);
                }

            }
        }
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockCauldron.LEVEL);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCauldron.LEVEL);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
