package net.minecraft.server;

public class TileEntityEnderChest extends TileEntity implements ITickable {

    public float a;
    public float f;
    public int g;
    private int h;

    public TileEntityEnderChest() {}

    public void e() {
        if (++this.h % 20 * 4 == 0) {
            this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
        }

        this.f = this.a;
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();
        float f = 0.1F;
        double d0;

        if (this.g > 0 && this.a == 0.0F) {
            double d1 = (double) i + 0.5D;

            d0 = (double) k + 0.5D;
            this.world.a((EntityHuman) null, d1, (double) j + 0.5D, d0, SoundEffects.aT, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (this.g == 0 && this.a > 0.0F || this.g > 0 && this.a < 1.0F) {
            float f1 = this.a;

            if (this.g > 0) {
                this.a += 0.1F;
            } else {
                this.a -= 0.1F;
            }

            if (this.a > 1.0F) {
                this.a = 1.0F;
            }

            float f2 = 0.5F;

            if (this.a < 0.5F && f1 >= 0.5F) {
                d0 = (double) i + 0.5D;
                double d2 = (double) k + 0.5D;

                this.world.a((EntityHuman) null, d0, (double) j + 0.5D, d2, SoundEffects.aS, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.a < 0.0F) {
                this.a = 0.0F;
            }
        }

    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.g = j;
            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void z() {
        this.invalidateBlockCache();
        super.z();
    }

    public void a() {
        ++this.g;
        this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
    }

    public void f() {
        --this.g;
        this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }
}
