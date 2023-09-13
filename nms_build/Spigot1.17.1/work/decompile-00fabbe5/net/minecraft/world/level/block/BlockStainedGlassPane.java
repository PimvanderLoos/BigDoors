package net.minecraft.world.level.block;

import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockStainedGlassPane extends BlockIronBars implements IBeaconBeam {

    private final EnumColor color;

    public BlockStainedGlassPane(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockStainedGlassPane.NORTH, false)).set(BlockStainedGlassPane.EAST, false)).set(BlockStainedGlassPane.SOUTH, false)).set(BlockStainedGlassPane.WEST, false)).set(BlockStainedGlassPane.WATERLOGGED, false));
    }

    @Override
    public EnumColor a() {
        return this.color;
    }
}
