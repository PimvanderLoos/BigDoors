package net.minecraft.world.item;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemFlintAndSteel extends Item {

    public ItemFlintAndSteel(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getPlayer();
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        if (!BlockCampfire.canLight(iblockdata) && !CandleBlock.canLight(iblockdata) && !CandleCakeBlock.canLight(iblockdata)) {
            BlockPosition blockposition1 = blockposition.relative(itemactioncontext.getClickedFace());

            if (BlockFireAbstract.canBePlacedAt(world, blockposition1, itemactioncontext.getHorizontalDirection())) {
                world.playSound(entityhuman, blockposition1, SoundEffects.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                IBlockData iblockdata1 = BlockFireAbstract.getState(world, blockposition1);

                world.setBlock(blockposition1, iblockdata1, 11);
                world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_PLACE, blockposition);
                ItemStack itemstack = itemactioncontext.getItemInHand();

                if (entityhuman instanceof EntityPlayer) {
                    CriterionTriggers.PLACED_BLOCK.trigger((EntityPlayer) entityhuman, blockposition1, itemstack);
                    itemstack.hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastBreakEvent(itemactioncontext.getHand());
                    });
                }

                return EnumInteractionResult.sidedSuccess(world.isClientSide());
            } else {
                return EnumInteractionResult.FAIL;
            }
        } else {
            world.playSound(entityhuman, blockposition, SoundEffects.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.LIT, true), 11);
            world.gameEvent((Entity) entityhuman, GameEvent.BLOCK_CHANGE, blockposition);
            if (entityhuman != null) {
                itemactioncontext.getItemInHand().hurtAndBreak(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastBreakEvent(itemactioncontext.getHand());
                });
            }

            return EnumInteractionResult.sidedSuccess(world.isClientSide());
        }
    }
}
