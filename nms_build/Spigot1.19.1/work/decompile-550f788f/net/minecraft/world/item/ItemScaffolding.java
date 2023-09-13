package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockScaffolding;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemScaffolding extends ItemBlock {

    public ItemScaffolding(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Nullable
    @Override
    public BlockActionContext updatePlacementContext(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        World world = blockactioncontext.getLevel();
        IBlockData iblockdata = world.getBlockState(blockposition);
        Block block = this.getBlock();

        if (!iblockdata.is(block)) {
            return BlockScaffolding.getDistance(world, blockposition) == 7 ? null : blockactioncontext;
        } else {
            EnumDirection enumdirection;

            if (blockactioncontext.isSecondaryUseActive()) {
                enumdirection = blockactioncontext.isInside() ? blockactioncontext.getClickedFace().getOpposite() : blockactioncontext.getClickedFace();
            } else {
                enumdirection = blockactioncontext.getClickedFace() == EnumDirection.UP ? blockactioncontext.getHorizontalDirection() : EnumDirection.UP;
            }

            int i = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable().move(enumdirection);

            while (i < 7) {
                if (!world.isClientSide && !world.isInWorldBounds(blockposition_mutableblockposition)) {
                    EntityHuman entityhuman = blockactioncontext.getPlayer();
                    int j = world.getMaxBuildHeight();

                    if (entityhuman instanceof EntityPlayer && blockposition_mutableblockposition.getY() >= j) {
                        ((EntityPlayer) entityhuman).sendSystemMessage(IChatBaseComponent.translatable("build.tooHigh", j - 1).withStyle(EnumChatFormat.RED), true);
                    }
                    break;
                }

                iblockdata = world.getBlockState(blockposition_mutableblockposition);
                if (!iblockdata.is(this.getBlock())) {
                    if (iblockdata.canBeReplaced(blockactioncontext)) {
                        return BlockActionContext.at(blockactioncontext, blockposition_mutableblockposition, enumdirection);
                    }
                    break;
                }

                blockposition_mutableblockposition.move(enumdirection);
                if (enumdirection.getAxis().isHorizontal()) {
                    ++i;
                }
            }

            return null;
        }
    }

    @Override
    protected boolean mustSurvive() {
        return false;
    }
}
