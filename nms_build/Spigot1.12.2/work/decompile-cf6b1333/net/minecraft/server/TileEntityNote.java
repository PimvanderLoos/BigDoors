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

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
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
            IBlockData iblockdata = world.getType(blockposition.down());
            Material material = iblockdata.getMaterial();
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

            Block block = iblockdata.getBlock();

            if (block == Blocks.CLAY) {
                b0 = 5;
            }

            if (block == Blocks.GOLD_BLOCK) {
                b0 = 6;
            }

            if (block == Blocks.WOOL) {
                b0 = 7;
            }

            if (block == Blocks.PACKED_ICE) {
                b0 = 8;
            }

            if (block == Blocks.di) {
                b0 = 9;
            }

            world.playBlockAction(blockposition, Blocks.NOTEBLOCK, b0, this.note);
        }
    }
}
