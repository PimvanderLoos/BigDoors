package net.minecraft.server;

public class TileEntityLightDetector extends TileEntity implements ITickable {

    public TileEntityLightDetector() {}

    public void e() {
        if (this.world != null && !this.world.isClientSide && this.world.getTime() % 20L == 0L) {
            this.e = this.getBlock();
            if (this.e instanceof BlockDaylightDetector) {
                ((BlockDaylightDetector) this.e).c(this.world, this.position);
            }
        }

    }
}
