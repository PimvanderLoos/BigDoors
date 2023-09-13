package net.minecraft.server;

public abstract class EntityIllagerAbstract extends EntityMonster {

    protected static final DataWatcherObject<Byte> a = DataWatcher.a(EntityIllagerAbstract.class, DataWatcherRegistry.a);

    protected EntityIllagerAbstract(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityIllagerAbstract.a, (byte) 0);
    }

    protected void a(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityIllagerAbstract.a);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityIllagerAbstract.a, (byte) (j & 255));
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ILLAGER;
    }
}
