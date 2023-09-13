package net.minecraft.world.level.block;

import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.state.BlockBase;

public class BlockCarpet extends CarpetBlock {

    private final EnumColor color;

    protected BlockCarpet(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
    }

    public EnumColor getColor() {
        return this.color;
    }
}
