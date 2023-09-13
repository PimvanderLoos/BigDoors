package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBed extends TileEntity {

    public EnumColor color;

    public TileEntityBed(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BED, blockposition, iblockdata);
        this.color = ((BlockBed) iblockdata.getBlock()).getColor();
    }

    public TileEntityBed(BlockPosition blockposition, IBlockData iblockdata, EnumColor enumcolor) {
        super(TileEntityTypes.BED, blockposition, iblockdata);
        this.color = enumcolor;
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    public EnumColor getColor() {
        return this.color;
    }

    public void setColor(EnumColor enumcolor) {
        this.color = enumcolor;
    }
}
