package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class TileEntityBanner extends TileEntity implements INamableTileEntity {

    private String a;
    public EnumColor color;
    public NBTTagList patterns;
    private boolean h;
    private List<EnumBannerPatternType> i;
    private List<EnumColor> j;
    private String k;

    public TileEntityBanner() {
        this.color = EnumColor.BLACK;
    }

    public void a(ItemStack itemstack, boolean flag) {
        this.patterns = null;
        NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            this.patterns = nbttagcompound.getList("Patterns", 10).d();
        }

        this.color = flag ? d(itemstack) : ItemBanner.c(itemstack);
        this.i = null;
        this.j = null;
        this.k = "";
        this.h = true;
        this.a = itemstack.hasName() ? itemstack.getName() : null;
    }

    public String getName() {
        return this.hasCustomName() ? this.a : "banner";
    }

    public boolean hasCustomName() {
        return this.a != null && !this.a.isEmpty();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName(), new Object[0]));
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("Base", this.color.getInvColorIndex());
        if (this.patterns != null) {
            nbttagcompound.set("Patterns", this.patterns);
        }

        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.a);
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.a = nbttagcompound.getString("CustomName");
        }

        this.color = EnumColor.fromInvColorIndex(nbttagcompound.getInt("Base"));
        this.patterns = nbttagcompound.getList("Patterns", 10);
        this.i = null;
        this.j = null;
        this.k = null;
        this.h = true;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 6, this.d());
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    public static int b(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");

        return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
    }

    public static void c(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            if (!nbttaglist.isEmpty()) {
                nbttaglist.remove(nbttaglist.size() - 1);
                if (nbttaglist.isEmpty()) {
                    itemstack.getTag().remove("BlockEntityTag");
                    if (itemstack.getTag().isEmpty()) {
                        itemstack.setTag((NBTTagCompound) null);
                    }
                }

            }
        }
    }

    public ItemStack l() {
        ItemStack itemstack = ItemBanner.a(this.color, this.patterns);

        if (this.hasCustomName()) {
            itemstack.g(this.getName());
        }

        return itemstack;
    }

    public static EnumColor d(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");

        return nbttagcompound != null && nbttagcompound.hasKey("Base") ? EnumColor.fromInvColorIndex(nbttagcompound.getInt("Base")) : EnumColor.BLACK;
    }
}
