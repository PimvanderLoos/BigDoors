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
    public boolean e(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.a(entityhuman, iblockdata, world, blockposition, false, entityhuman.b(EnumHand.MAIN_HAND));
        }

        return false;
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        World world = itemactioncontext.getWorld();

        if (!world.isClientSide && entityhuman != null) {
            BlockPosition blockposition = itemactioncontext.getClickPosition();

            this.a(entityhuman, world.getType(blockposition), world, blockposition, true, itemactioncontext.getItemStack());
        }

        return EnumInteractionResult.a(world.isClientSide);
    }

    private void a(EntityHuman entityhuman, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag, ItemStack itemstack) {
        if (entityhuman.isCreativeAndOp()) {
            Block block = iblockdata.getBlock();
            BlockStateList<Block, IBlockData> blockstatelist = block.getStates();
            Collection<IBlockState<?>> collection = blockstatelist.d();
            String s = IRegistry.BLOCK.getKey(block).toString();

            if (collection.isEmpty()) {
                a(entityhuman, (IChatBaseComponent) (new ChatMessage(this.getName() + ".empty", new Object[]{s})));
            } else {
                NBTTagCompound nbttagcompound = itemstack.a("DebugProperty");
                String s1 = nbttagcompound.getString(s);
                IBlockState<?> iblockstate = blockstatelist.a(s1);

                if (flag) {
                    if (iblockstate == null) {
                        iblockstate = (IBlockState) collection.iterator().next();
                    }

                    IBlockData iblockdata1 = a(iblockdata, iblockstate, entityhuman.eq());

                    generatoraccess.setTypeAndData(blockposition, iblockdata1, 18);
                    a(entityhuman, (IChatBaseComponent) (new ChatMessage(this.getName() + ".update", new Object[]{iblockstate.getName(), a(iblockdata1, iblockstate)})));
                } else {
                    iblockstate = (IBlockState) a((Iterable) collection, (Object) iblockstate, entityhuman.eq());
                    String s2 = iblockstate.getName();

                    nbttagcompound.setString(s, s2);
                    a(entityhuman, (IChatBaseComponent) (new ChatMessage(this.getName() + ".select", new Object[]{s2, a(iblockdata, iblockstate)})));
                }

            }
        }
    }

    private static <T extends Comparable<T>> IBlockData a(IBlockData iblockdata, IBlockState<T> iblockstate, boolean flag) {
        return (IBlockData) iblockdata.set(iblockstate, (Comparable) a((Iterable) iblockstate.getValues(), (Object) iblockdata.get(iblockstate), flag));
    }

    private static <T> T a(Iterable<T> iterable, @Nullable T t0, boolean flag) {
        return flag ? SystemUtils.b(iterable, t0) : SystemUtils.a(iterable, t0);
    }

    private static void a(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ((EntityPlayer) entityhuman).a(ichatbasecomponent, ChatMessageType.GAME_INFO, SystemUtils.b);
    }

    private static <T extends Comparable<T>> String a(IBlockData iblockdata, IBlockState<T> iblockstate) {
        return iblockstate.a(iblockdata.get(iblockstate));
    }
}
