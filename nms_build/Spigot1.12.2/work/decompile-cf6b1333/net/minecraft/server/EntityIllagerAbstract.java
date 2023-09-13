package net.minecraft.server;

public abstract class EntityIllagerAbstract extends EntityMonster {

    protected static final DataWatcherObject<Byte> a = DataWatcher.a(EntityIllagerAbstract.class, DataWatcherRegistry.a);

    public EntityIllagerAbstract(World world) {
        super(world);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityIllagerAbstract.a, Byte.valueOf((byte) 0));
    }

    protected void a(int i, boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityIllagerAbstract.a)).byteValue();
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityIllagerAbstract.a, Byte.valueOf((byte) (j & 255)));
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ILLAGER;
    }
}
