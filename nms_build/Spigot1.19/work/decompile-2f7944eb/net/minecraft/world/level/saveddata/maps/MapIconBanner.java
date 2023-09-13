package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBanner;

public class MapIconBanner {

    private final BlockPosition pos;
    private final EnumColor color;
    @Nullable
    private final IChatBaseComponent name;

    public MapIconBanner(BlockPosition blockposition, EnumColor enumcolor, @Nullable IChatBaseComponent ichatbasecomponent) {
        this.pos = blockposition;
        this.color = enumcolor;
        this.name = ichatbasecomponent;
    }

    public static MapIconBanner load(NBTTagCompound nbttagcompound) {
        BlockPosition blockposition = GameProfileSerializer.readBlockPos(nbttagcompound.getCompound("Pos"));
        EnumColor enumcolor = EnumColor.byName(nbttagcompound.getString("Color"), EnumColor.WHITE);
        IChatMutableComponent ichatmutablecomponent = nbttagcompound.contains("Name") ? IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("Name")) : null;

        return new MapIconBanner(blockposition, enumcolor, ichatmutablecomponent);
    }

    @Nullable
    public static MapIconBanner fromWorld(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityBanner) {
            TileEntityBanner tileentitybanner = (TileEntityBanner) tileentity;
            EnumColor enumcolor = tileentitybanner.getBaseColor();
            IChatBaseComponent ichatbasecomponent = tileentitybanner.hasCustomName() ? tileentitybanner.getCustomName() : null;

            return new MapIconBanner(blockposition, enumcolor, ichatbasecomponent);
        } else {
            return null;
        }
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public EnumColor getColor() {
        return this.color;
    }

    public MapIcon.Type getDecoration() {
        switch (this.color) {
            case WHITE:
                return MapIcon.Type.BANNER_WHITE;
            case ORANGE:
                return MapIcon.Type.BANNER_ORANGE;
            case MAGENTA:
                return MapIcon.Type.BANNER_MAGENTA;
            case LIGHT_BLUE:
                return MapIcon.Type.BANNER_LIGHT_BLUE;
            case YELLOW:
                return MapIcon.Type.BANNER_YELLOW;
            case LIME:
                return MapIcon.Type.BANNER_LIME;
            case PINK:
                return MapIcon.Type.BANNER_PINK;
            case GRAY:
                return MapIcon.Type.BANNER_GRAY;
            case LIGHT_GRAY:
                return MapIcon.Type.BANNER_LIGHT_GRAY;
            case CYAN:
                return MapIcon.Type.BANNER_CYAN;
            case PURPLE:
                return MapIcon.Type.BANNER_PURPLE;
            case BLUE:
                return MapIcon.Type.BANNER_BLUE;
            case BROWN:
                return MapIcon.Type.BANNER_BROWN;
            case GREEN:
                return MapIcon.Type.BANNER_GREEN;
            case RED:
                return MapIcon.Type.BANNER_RED;
            case BLACK:
            default:
                return MapIcon.Type.BANNER_BLACK;
        }
    }

    @Nullable
    public IChatBaseComponent getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            MapIconBanner mapiconbanner = (MapIconBanner) object;

            return Objects.equals(this.pos, mapiconbanner.pos) && this.color == mapiconbanner.color && Objects.equals(this.name, mapiconbanner.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pos, this.color, this.name});
    }

    public NBTTagCompound save() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.put("Pos", GameProfileSerializer.writeBlockPos(this.pos));
        nbttagcompound.putString("Color", this.color.getName());
        if (this.name != null) {
            nbttagcompound.putString("Name", IChatBaseComponent.ChatSerializer.toJson(this.name));
        }

        return nbttagcompound;
    }

    public String getId() {
        int i = this.pos.getX();

        return "banner-" + i + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}
