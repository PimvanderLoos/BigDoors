package net.minecraft.server;

public class BlockHay extends BlockRotatable {

    public BlockHay() {
        super(Material.GRASS, MaterialMapColor.u);
        this.w(this.blockStateList.getBlockData().set(BlockHay.AXIS, EnumDirection.EnumAxis.Y));
        this.a(CreativeModeTab.b);
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.e(f, 0.2F);
    }
}
