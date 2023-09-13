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
import net.minecraft.network.chat.ChatComponentUtils;
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

    public static boolean makeSureTagIsValid(@Nullable NBTTagCompound nbttagcompound) {
        if (!ItemBookAndQuill.makeSureTagIsValid(nbttagcompound)) {
            return false;
        } else if (!nbttagcompound.contains("title", 8)) {
            return false;
        } else {
            String s = nbttagcompound.getString("title");

            return s.length() > 32 ? false : nbttagcompound.contains("author", 8);
        }
    }

    public static int getGeneration(ItemStack itemstack) {
        return itemstack.getTag().getInt("generation");
    }

    public static int getPageCount(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null ? nbttagcompound.getList("pages", 8).size() : 0;
    }

    @Override
    public IChatBaseComponent getName(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null) {
            String s = nbttagcompound.getString("title");

            if (!UtilColor.isNullOrEmpty(s)) {
                return IChatBaseComponent.literal(s);
            }
        }

        return super.getName(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        if (itemstack.hasTag()) {
            NBTTagCompound nbttagcompound = itemstack.getTag();
            String s = nbttagcompound.getString("author");

            if (!UtilColor.isNullOrEmpty(s)) {
                list.add(IChatBaseComponent.translatable("book.byAuthor", s).withStyle(EnumChatFormat.GRAY));
            }

            list.add(IChatBaseComponent.translatable("book.generation." + nbttagcompound.getInt("generation")).withStyle(EnumChatFormat.GRAY));
        }

    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();
        BlockPosition blockposition = itemactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        return iblockdata.is(Blocks.LECTERN) ? (BlockLectern.tryPlaceBook(itemactioncontext.getPlayer(), world, blockposition, iblockdata, itemactioncontext.getItemInHand()) ? EnumInteractionResult.sidedSuccess(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        entityhuman.openItemGui(itemstack, enumhand);
        entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
        return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
    }

    public static boolean resolveBookComponents(ItemStack itemstack, @Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && !nbttagcompound.getBoolean("resolved")) {
            nbttagcompound.putBoolean("resolved", true);
            if (!makeSureTagIsValid(nbttagcompound)) {
                return false;
            } else {
                NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);
                NBTTagList nbttaglist1 = new NBTTagList();

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = resolvePage(commandlistenerwrapper, entityhuman, nbttaglist.getString(i));

                    if (s.length() > 32767) {
                        return false;
                    }

                    nbttaglist1.add(i, (NBTBase) NBTTagString.valueOf(s));
                }

                if (nbttagcompound.contains("filtered_pages", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("filtered_pages");
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                    Iterator iterator = nbttagcompound1.getAllKeys().iterator();

                    while (iterator.hasNext()) {
                        String s1 = (String) iterator.next();
                        String s2 = resolvePage(commandlistenerwrapper, entityhuman, nbttagcompound1.getString(s1));

                        if (s2.length() > 32767) {
                            return false;
                        }

                        nbttagcompound2.putString(s1, s2);
                    }

                    nbttagcompound.put("filtered_pages", nbttagcompound2);
                }

                nbttagcompound.put("pages", nbttaglist1);
                return true;
            }
        } else {
            return false;
        }
    }

    private static String resolvePage(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable EntityHuman entityhuman, String s) {
        IChatMutableComponent ichatmutablecomponent;

        try {
            ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJsonLenient(s);
            ichatmutablecomponent = ChatComponentUtils.updateForEntity(commandlistenerwrapper, (IChatBaseComponent) ichatmutablecomponent, entityhuman, 0);
        } catch (Exception exception) {
            ichatmutablecomponent = IChatBaseComponent.literal(s);
        }

        return IChatBaseComponent.ChatSerializer.toJson(ichatmutablecomponent);
    }

    @Override
    public boolean isFoil(ItemStack itemstack) {
        return true;
    }
}
