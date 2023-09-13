package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class ItemDebugStick extends Item {

    public ItemDebugStick(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean canAttackBlock(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.handleInteraction(entityhuman, iblockdata, world, blockposition, false, entityhuman.getItemInHand(EnumHand.MAIN_HAND));
        }

        return false;
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getPlayer();
        World world = itemactioncontext.getLevel();

        if (!world.isClientSide && entityhuman != null) {
            BlockPosition blockposition = itemactioncontext.getClickedPos();

            if (!this.handleInteraction(entityhuman, world.getBlockState(blockposition), world, blockposition, true, itemactioncontext.getItemInHand())) {
                return EnumInteractionResult.FAIL;
            }
        }

        return EnumInteractionResult.sidedSuccess(world.isClientSide);
    }

    private boolean handleInteraction(EntityHuman entityhuman, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag, ItemStack itemstack) {
        if (!entityhuman.canUseGameMasterBlocks()) {
            return false;
        } else {
            Block block = iblockdata.getBlock();
            BlockStateList<Block, IBlockData> blockstatelist = block.getStateDefinition();
            Collection<IBlockState<?>> collection = blockstatelist.getProperties();
            String s = IRegistry.BLOCK.getKey(block).toString();

            if (collection.isEmpty()) {
                message(entityhuman, new ChatMessage(this.getDescriptionId() + ".empty", new Object[]{s}));
                return false;
            } else {
                NBTTagCompound nbttagcompound = itemstack.getOrCreateTagElement("DebugProperty");
                String s1 = nbttagcompound.getString(s);
                IBlockState<?> iblockstate = blockstatelist.getProperty(s1);

                if (flag) {
                    if (iblockstate == null) {
                        iblockstate = (IBlockState) collection.iterator().next();
                    }

                    IBlockData iblockdata1 = cycleState(iblockdata, iblockstate, entityhuman.isSecondaryUseActive());

                    generatoraccess.setBlock(blockposition, iblockdata1, 18);
                    message(entityhuman, new ChatMessage(this.getDescriptionId() + ".update", new Object[]{iblockstate.getName(), getNameHelper(iblockdata1, iblockstate)}));
                } else {
                    iblockstate = (IBlockState) getRelative(collection, iblockstate, entityhuman.isSecondaryUseActive());
                    String s2 = iblockstate.getName();

                    nbttagcompound.putString(s, s2);
                    message(entityhuman, new ChatMessage(this.getDescriptionId() + ".select", new Object[]{s2, getNameHelper(iblockdata, iblockstate)}));
                }

                return true;
            }
        }
    }

    private static <T extends Comparable<T>> IBlockData cycleState(IBlockData iblockdata, IBlockState<T> iblockstate, boolean flag) {
        return (IBlockData) iblockdata.setValue(iblockstate, (Comparable) getRelative(iblockstate.getPossibleValues(), iblockdata.getValue(iblockstate), flag));
    }

    private static <T> T getRelative(Iterable<T> iterable, @Nullable T t0, boolean flag) {
        return flag ? SystemUtils.findPreviousInIterable(iterable, t0) : SystemUtils.findNextInIterable(iterable, t0);
    }

    private static void message(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ((EntityPlayer) entityhuman).sendMessage(ichatbasecomponent, ChatMessageType.GAME_INFO, SystemUtils.NIL_UUID);
    }

    private static <T extends Comparable<T>> String getNameHelper(IBlockData iblockdata, IBlockState<T> iblockstate) {
        return iblockstate.getName(iblockdata.getValue(iblockstate));
    }
}
