package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.UtilColor;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockLectern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class ItemWrittenBook extends Item {

    public static final int TITLE_LENGTH = 16;
    public static final int TITLE_MAX_LENGTH = 32;
    public static final int PAGE_EDIT_LENGTH = 1024;
    public static final int PAGE_LENGTH = 32767;
    public static final int MAX_PAGES = 100;
    public static final int MAX_GENERATION = 2;
    public static final String TAG_TITLE = "title";
    public static final String TAG_FILTERED_TITLE = "filtered_title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_PAGES = "pages";
    public static final String TAG_FILTERED_PAGES = "filtered_pages";
    public static final String TAG_GENERATION = "generation";
    public static final String TAG_RESOLVED = "resolved";

    public ItemWrittenBook(Item.Info item_info) {
        super(item_info);
    }

    public static boolean a(@Nullable NBTTagCompound nbttagcompound) {
        if (!ItemBookAndQuill.a(nbttagcompound)) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("title", 8)) {
            return false;
        } else {
            String s = nbttagcompound.getString("title");

            return s.length() > 32 ? false : nbttagcompound.hasKeyOfType("author", 8);
        }
    }

    public static int d(ItemStack itemstack) {
        return itemstack.getTag().getInt("generation");
    }

    public static int k(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null ? nbttagcompound.getList("pages", 8).size() : 0;
    }

    @Override
    public IChatBaseComponent m(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null) {
            String s = nbttagcompound.getString("title");

            if (!UtilColor.b(s)) {
                return new ChatComponentText(s);
            }
        }

        return super.m(itemstack);
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        if (itemstack.hasTag()) {
            NBTTagCompound nbttagcompound = itemstack.getTag();
            String s = nbttagcompound.getString("author");

            if (!UtilColor.b(s)) {
                list.add((new ChatMessage("book.byAuthor", new Object[]{s})).a(EnumChatFormat.GRAY));
            }

            list.add((new ChatMessage("book.generation." + nbttagcompound.getInt("generation"))).a(EnumChatFormat.GRAY));
        }

    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.a(Blocks.LECTERN) ? (BlockLectern.a(itemactioncontext.getEntity(), world, blockposition, iblockdata, itemactioncontext.getItemStack()) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        entityhuman.openBook(itemstack, enumhand);
        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return InteractionResultWrapper.a(itemstack, world.isClientSide());
    }

    public static boolean a(ItemStack itemstack, @Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && !nbttagcompound.getBoolean("resolved")) {
            nbttagcompound.setBoolean("resolved", true);
            if (!a(nbttagcompound)) {
                return false;
            } else {
                NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    nbttaglist.set(i, (NBTBase) NBTTagString.a(a(commandlistenerwrapper, entityhuman, nbttaglist.getString(i))));
                }

                if (nbttagcompound.hasKeyOfType("filtered_pages", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("filtered_pages");
                    Iterator iterator = nbttagcompound1.getKeys().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        nbttagcompound1.setString(s, a(commandlistenerwrapper, entityhuman, nbttagcompound1.getString(s)));
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private static String a(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable EntityHuman entityhuman, String s) {
        Object object;

        try {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.b(s);

            object = ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatmutablecomponent, entityhuman, 0);
        } catch (Exception exception) {
            object = new ChatComponentText(s);
        }

        return IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return true;
    }
}
