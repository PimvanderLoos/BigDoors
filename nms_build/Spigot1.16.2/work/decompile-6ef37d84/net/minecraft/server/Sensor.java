package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public abstract class Sensor<E extends EntityLiving> {

    private static final Random a = new Random();
    private static final PathfinderTargetCondition b = (new PathfinderTargetCondition()).a(16.0D).b().d();
    private static final PathfinderTargetCondition c = (new PathfinderTargetCondition()).a(16.0D).b().d().e();
    private final int d;
    private long e;

    public Sensor(int i) {
        this.d = i;
        this.e = (long) Sensor.a.nextInt(i);
    }

    public Sensor() {
        this(20);
    }

    public final void b(WorldServer worldserver, E e0) {
        if (--this.e <= 0L) {
            this.e = (long) this.d;
            this.a(worldserver, e0);
        }

    }

    protected abstract void a(WorldServer worldserver, E e0);

    public abstract Set<MemoryModuleType<?>> a();

    protected static boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving.getBehaviorController().b(MemoryModuleType.ATTACK_TARGET, (Object) entityliving1) ? Sensor.c.a(entityliving, entityliving1) : Sensor.b.a(entityliving, entityliving1);
    }
}
