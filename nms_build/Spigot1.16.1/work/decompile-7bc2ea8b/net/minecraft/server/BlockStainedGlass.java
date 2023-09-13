package net.minecraft.server;

public class BlockStainedGlass extends BlockGlassAbstract implements IBeaconBeam {

    private final EnumColor color;

    public BlockStainedGlass(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
    }

    @Override
    public EnumColor a() {
        return this.color;
    }
}
