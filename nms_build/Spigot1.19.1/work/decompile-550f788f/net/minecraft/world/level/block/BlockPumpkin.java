package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockPumpkin extends BlockStemmed {

    protected BlockPumpkin(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (itemstack.is(Items.SHEARS)) {
            if (!world.isClientSide) {
                EnumDirection enumdirection = movingobjectpositionblock.getDirection();
                EnumDirection enumdirection1 = enumdirection.getAxis() == EnumDirection.EnumAxis.Y ? entityhuman.getDirection().getOpposite() : enumdirection;

                world.playSound((EntityHuman) null, blockposition, SoundEffects.PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlock(blockposition, (IBlockData) Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockPumpkinCarved.FACING, enumdirection1), 11);
                EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + 0.5D + (double) enumdirection1.getStepX() * 0.65D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D + (double) enumdirection1.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));

                entityitem.setDeltaMovement(0.05D * (double) enumdirection1.getStepX() + world.random.nextDouble() * 0.02D, 0.05D, 0.05D * (double) enumdirection1.getStepZ() + world.random.nextDouble() * 0.02D);
                world.addFreshEntity(entityitem);
                itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(enumhand);
                });
                world.gameEvent((Entity) entityhuman, GameEvent.SHEAR, blockposition);
                entityhuman.awardStat(StatisticList.ITEM_USED.get(Items.SHEARS));
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    public BlockStem getStem() {
        return (BlockStem) Blocks.PUMPKIN_STEM;
    }

    @Override
    public BlockStemAttached getAttachedStem() {
        return (BlockStemAttached) Blocks.ATTACHED_PUMPKIN_STEM;
    }
}
