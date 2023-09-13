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
import net.minecraft.world.level.IBlockAccess;
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
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (!BlockCampfire.h(iblockdata) && !CandleBlock.g(iblockdata) && !CandleCakeBlock.g(iblockdata)) {
            BlockPosition blockposition1 = blockposition.shift(itemactioncontext.getClickedFace());

            if (BlockFireAbstract.a(world, blockposition1, itemactioncontext.g())) {
                world.playSound(entityhuman, blockposition1, SoundEffects.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                IBlockData iblockdata1 = BlockFireAbstract.a((IBlockAccess) world, blockposition1);

                world.setTypeAndData(blockposition1, iblockdata1, 11);
                world.a((Entity) entityhuman, GameEvent.BLOCK_PLACE, blockposition);
                ItemStack itemstack = itemactioncontext.getItemStack();

                if (entityhuman instanceof EntityPlayer) {
                    CriterionTriggers.PLACED_BLOCK.a((EntityPlayer) entityhuman, blockposition1, itemstack);
                    itemstack.damage(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.broadcastItemBreak(itemactioncontext.getHand());
                    });
                }

                return EnumInteractionResult.a(world.isClientSide());
            } else {
                return EnumInteractionResult.FAIL;
            }
        } else {
            world.playSound(entityhuman, blockposition, SoundEffects.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.LIT, true), 11);
            world.a((Entity) entityhuman, GameEvent.BLOCK_PLACE, blockposition);
            if (entityhuman != null) {
                itemactioncontext.getItemStack().damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.broadcastItemBreak(itemactioncontext.getHand());
                });
            }

            return EnumInteractionResult.a(world.isClientSide());
        }
    }
}
