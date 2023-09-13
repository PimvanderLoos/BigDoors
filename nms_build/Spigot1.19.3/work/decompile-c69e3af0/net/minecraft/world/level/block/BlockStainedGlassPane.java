package net.minecraft.world.level.block;

import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockStainedGlassPane extends BlockIronBars implements IBeaconBeam {

    private final EnumColor color;

    public BlockStainedGlassPane(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockStainedGlassPane.NORTH, false)).setValue(BlockStainedGlassPane.EAST, false)).setValue(BlockStainedGlassPane.SOUTH, false)).setValue(BlockStainedGlassPane.WEST, false)).setValue(BlockStainedGlassPane.WATERLOGGED, false));
    }

    @Override
    public EnumColor getColor() {
        return this.color;
    }
}
