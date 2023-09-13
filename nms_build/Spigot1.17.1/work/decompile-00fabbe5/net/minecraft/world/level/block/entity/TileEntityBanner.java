package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.item.EnumColor;
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
    private boolean receivedData;
    @Nullable
    private List<Pair<EnumBannerPatternType, EnumColor>> patterns;

    public TileEntityBanner(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BANNER, blockposition, iblockdata);
        this.baseColor = ((BlockBannerAbstract) iblockdata.getBlock()).getColor();
    }

    public TileEntityBanner(BlockPosition blockposition, IBlockData iblockdata, EnumColor enumcolor) {
        this(blockposition, iblockdata);
        this.baseColor = enumcolor;
    }

    @Nullable
    public static NBTTagList a(ItemStack itemstack) {
        NBTTagList nbttaglist = null;
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            nbttaglist = nbttagcompound.getList("Patterns", 10).clone();
        }

        return nbttaglist;
    }

    public void a(ItemStack itemstack, EnumColor enumcolor) {
        this.itemPatterns = a(itemstack);
        this.baseColor = enumcolor;
        this.patterns = null;
        this.receivedData = true;
        this.name = itemstack.hasName() ? itemstack.getName() : null;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.name != null ? this.name : new ChatMessage("block.minecraft.banner"));
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.itemPatterns != null) {
            nbttagcompound.set("Patterns", this.itemPatterns);
        }

        if (this.name != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.name));
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

        this.itemPatterns = nbttagcompound.getList("Patterns", 10);
        this.patterns = null;
        this.receivedData = true;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 6, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    public static int b(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
    }

    public List<Pair<EnumBannerPatternType, EnumColor>> c() {
        if (this.patterns == null && this.receivedData) {
            this.patterns = a(this.baseColor, this.itemPatterns);
        }

        return this.patterns;
    }

    public static List<Pair<EnumBannerPatternType, EnumColor>> a(EnumColor enumcolor, @Nullable NBTTagList nbttaglist) {
        List<Pair<EnumBannerPatternType, EnumColor>> list = Lists.newArrayList();

        list.add(Pair.of(EnumBannerPatternType.BASE, enumcolor));
        if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
                EnumBannerPatternType enumbannerpatterntype = EnumBannerPatternType.a(nbttagcompound.getString("Pattern"));

                if (enumbannerpatterntype != null) {
                    int j = nbttagcompound.getInt("Color");

                    list.add(Pair.of(enumbannerpatterntype, EnumColor.fromColorIndex(j)));
                }
            }
        }

        return list;
    }

    public static void c(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            if (!nbttaglist.isEmpty()) {
                nbttaglist.remove(nbttaglist.size() - 1);
                if (nbttaglist.isEmpty()) {
                    itemstack.removeTag("BlockEntityTag");
                }

            }
        }
    }

    public ItemStack f() {
        ItemStack itemstack = new ItemStack(BlockBanner.a(this.baseColor));

        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            itemstack.a("BlockEntityTag").set("Patterns", this.itemPatterns.clone());
        }

        if (this.name != null) {
            itemstack.a(this.name);
        }

        return itemstack;
    }

    public EnumColor g() {
        return this.baseColor;
    }
}
