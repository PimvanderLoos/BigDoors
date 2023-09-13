package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BlockBanner;
import net.minecraft.world.level.block.BlockBannerAbstract;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBanner extends TileEntity implements INamableTileEntity {

    public static final int MAX_PATTERNS = 6;
    public static final String TAG_PATTERNS = "Patterns";
    public static final String TAG_PATTERN = "Pattern";
    public static final String TAG_COLOR = "Color";
    @Nullable
    private IChatBaseComponent name;
    public EnumColor baseColor;
    @Nullable
    public NBTTagList itemPatterns;
    @Nullable
    private List<Pair<Holder<EnumBannerPatternType>, EnumColor>> patterns;

    public TileEntityBanner(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BANNER, blockposition, iblockdata);
        this.baseColor = ((BlockBannerAbstract) iblockdata.getBlock()).getColor();
    }

    public TileEntityBanner(BlockPosition blockposition, IBlockData iblockdata, EnumColor enumcolor) {
        this(blockposition, iblockdata);
        this.baseColor = enumcolor;
    }

    @Nullable
    public static NBTTagList getItemPatterns(ItemStack itemstack) {
        NBTTagList nbttaglist = null;
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null && nbttagcompound.contains("Patterns", 9)) {
            nbttaglist = nbttagcompound.getList("Patterns", 10).copy();
        }

        return nbttaglist;
    }

    public void fromItem(ItemStack itemstack, EnumColor enumcolor) {
        this.baseColor = enumcolor;
        this.fromItem(itemstack);
    }

    public void fromItem(ItemStack itemstack) {
        this.itemPatterns = getItemPatterns(itemstack);
        this.patterns = null;
        this.name = itemstack.hasCustomHoverName() ? itemstack.getHoverName() : null;
    }

    @Override
    public IChatBaseComponent getName() {
        return (IChatBaseComponent) (this.name != null ? this.name : IChatBaseComponent.translatable("block.minecraft.banner"));
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }

    public void setCustomName(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (this.itemPatterns != null) {
            nbttagcompound.put("Patterns", this.itemPatterns);
        }

        if (this.name != null) {
            nbttagcompound.putString("CustomName", IChatBaseComponent.ChatSerializer.toJson(this.name));
        }

    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.contains("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("CustomName"));
        }

        this.itemPatterns = nbttagcompound.getList("Patterns", 10);
        this.patterns = null;
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static int getPatternCount(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        return nbttagcompound != null && nbttagcompound.contains("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
    }

    public List<Pair<Holder<EnumBannerPatternType>, EnumColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = createPatterns(this.baseColor, this.itemPatterns);
        }

        return this.patterns;
    }

    public static List<Pair<Holder<EnumBannerPatternType>, EnumColor>> createPatterns(EnumColor enumcolor, @Nullable NBTTagList nbttaglist) {
        List<Pair<Holder<EnumBannerPatternType>, EnumColor>> list = Lists.newArrayList();

        list.add(Pair.of(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(BannerPatterns.BASE), enumcolor));
        if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
                Holder<EnumBannerPatternType> holder = EnumBannerPatternType.byHash(nbttagcompound.getString("Pattern"));

                if (holder != null) {
                    int j = nbttagcompound.getInt("Color");

                    list.add(Pair.of(holder, EnumColor.byId(j)));
                }
            }
        }

        return list;
    }

    public static void removeLastPattern(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null && nbttagcompound.contains("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            if (!nbttaglist.isEmpty()) {
                nbttaglist.remove(nbttaglist.size() - 1);
                if (nbttaglist.isEmpty()) {
                    nbttagcompound.remove("Patterns");
                }

                ItemBlock.setBlockEntityData(itemstack, TileEntityTypes.BANNER, nbttagcompound);
            }
        }
    }

    public ItemStack getItem() {
        ItemStack itemstack = new ItemStack(BlockBanner.byColor(this.baseColor));

        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.put("Patterns", this.itemPatterns.copy());
            ItemBlock.setBlockEntityData(itemstack, this.getType(), nbttagcompound);
        }

        if (this.name != null) {
            itemstack.setHoverName(this.name);
        }

        return itemstack;
    }

    public EnumColor getBaseColor() {
        return this.baseColor;
    }
}
