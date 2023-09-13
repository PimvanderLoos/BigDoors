package net.minecraft.server;

public class TileEntityNote extends TileEntity {

    public byte note;
    public boolean f;

    public TileEntityNote() {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setByte("note", this.note);
        nbttagcompound.setBoolean("powered", this.f);
        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.note = nbttagcompound.getByte("note");
        this.note = (byte) MathHelper.clamp(this.note, 0, 24);
        this.f = nbttagcompound.getBoolean("powered");
    }

    public void a() {
        this.note = (byte) ((this.note + 1) % 25);
        this.update();
    }

    public void play(World world, BlockPosition blockposition) {
        if (world.getType(blockposition.up()).getMaterial() == Material.AIR) {
            Material material = world.getType(blockposition.down()).getMaterial();
            byte b0 = 0;

            if (material == Material.STONE) {
                b0 = 1;
            }

            if (material == Material.SAND) {
                b0 = 2;
            }

            if (material == Material.SHATTERABLE) {
                b0 = 3;
            }

            if (material == Material.WOOD) {
                b0 = 4;
            }

            world.playBlockAction(blockposition, Blocks.NOTEBLOCK, b0, this.note);
        }
    }
}
