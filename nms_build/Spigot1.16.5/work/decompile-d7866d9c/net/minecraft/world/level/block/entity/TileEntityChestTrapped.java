package net.minecraft.world.level.block.entity;

public class TileEntityChestTrapped extends TileEntityChest {

    public TileEntityChestTrapped() {
        super(TileEntityTypes.TRAPPED_CHEST);
    }

    @Override
    protected void onOpen() {
        super.onOpen();
        this.world.applyPhysics(this.position.down(), this.getBlock().getBlock());
    }
}
