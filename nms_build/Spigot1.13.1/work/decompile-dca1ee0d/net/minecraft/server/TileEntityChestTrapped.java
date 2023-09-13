package net.minecraft.server;

public class TileEntityChestTrapped extends TileEntityChest {

    public TileEntityChestTrapped() {
        super(TileEntityTypes.TRAPPED_CHEST);
    }

    protected void p() {
        super.p();
        this.world.applyPhysics(this.position.down(), this.getBlock().getBlock());
    }
}
