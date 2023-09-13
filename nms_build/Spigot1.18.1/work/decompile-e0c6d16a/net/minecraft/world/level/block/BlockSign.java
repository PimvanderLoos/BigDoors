package net.minecraft.world.level.block;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyWood;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockSign extends BlockTileEntity implements IBlockWaterlogged {

    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final float AABB_OFFSET = 4.0F;
    protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private final BlockPropertyWood type;

    protected BlockSign(BlockBase.Info blockbase_info, BlockPropertyWood blockpropertywood) {
        super(blockbase_info);
        this.type = blockpropertywood;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockSign.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSign.SHAPE;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntitySign(blockposition, iblockdata);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        Item item = itemstack.getItem();
        boolean flag = item instanceof ItemDye;
        boolean flag1 = itemstack.is(Items.GLOW_INK_SAC);
        boolean flag2 = itemstack.is(Items.INK_SAC);
        boolean flag3 = (flag1 || flag || flag2) && entityhuman.getAbilities().mayBuild;

        if (world.isClientSide) {
            return flag3 ? EnumInteractionResult.SUCCESS : EnumInteractionResult.CONSUME;
        } else {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return EnumInteractionResult.PASS;
            } else {
                TileEntitySign tileentitysign = (TileEntitySign) tileentity;
                boolean flag4 = tileentitysign.hasGlowingText();

                if ((!flag1 || !flag4) && (!flag2 || flag4)) {
                    if (flag3) {
                        boolean flag5;

                        if (flag1) {
                            world.playSound((EntityHuman) null, blockposition, SoundEffects.GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            flag5 = tileentitysign.setHasGlowingText(true);
                            if (entityhuman instanceof EntityPlayer) {
                                CriterionTriggers.ITEM_USED_ON_BLOCK.trigger((EntityPlayer) entityhuman, blockposition, itemstack);
                            }
                        } else if (flag2) {
                            world.playSound((EntityHuman) null, blockposition, SoundEffects.INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            flag5 = tileentitysign.setHasGlowingText(false);
                        } else {
                            world.playSound((EntityHuman) null, blockposition, SoundEffects.DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            flag5 = tileentitysign.setColor(((ItemDye) item).getDyeColor());
                        }

                        if (flag5) {
                            if (!entityhuman.isCreative()) {
                                itemstack.shrink(1);
                            }

                            entityhuman.awardStat(StatisticList.ITEM_USED.get(item));
                        }
                    }

                    return tileentitysign.executeClickCommands((EntityPlayer) entityhuman) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
                } else {
                    return EnumInteractionResult.PASS;
                }
            }
        }
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockSign.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    public BlockPropertyWood type() {
        return this.type;
    }
}
