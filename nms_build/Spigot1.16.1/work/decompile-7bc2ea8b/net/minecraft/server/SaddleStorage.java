package net.minecraft.server;

import java.util.Random;

public class SaddleStorage {

    private final DataWatcher d;
    private final DataWatcherObject<Integer> e;
    private final DataWatcherObject<Boolean> f;
    public boolean a;
    public int b;
    public int c;

    public SaddleStorage(DataWatcher datawatcher, DataWatcherObject<Integer> datawatcherobject, DataWatcherObject<Boolean> datawatcherobject1) {
        this.d = datawatcher;
        this.e = datawatcherobject;
        this.f = datawatcherobject1;
    }

    public void a() {
        this.a = true;
        this.b = 0;
        this.c = (Integer) this.d.get(this.e);
    }

    public boolean a(Random random) {
        if (this.a) {
            return false;
        } else {
            this.a = true;
            this.b = 0;
            this.c = random.nextInt(841) + 140;
            this.d.set(this.e, this.c);
            return true;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("Saddle", this.hasSaddle());
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    public void setSaddle(boolean flag) {
        this.d.set(this.f, flag);
    }

    public boolean hasSaddle() {
        return (Boolean) this.d.get(this.f);
    }
}
