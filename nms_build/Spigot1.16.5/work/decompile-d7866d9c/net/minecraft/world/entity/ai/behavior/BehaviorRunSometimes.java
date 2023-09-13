package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.EntityLiving;

public class BehaviorRunSometimes<E extends EntityLiving> extends Behavior<E> {

    private boolean b;
    private boolean c;
    private final IntRange d;
    private final Behavior<? super E> e;
    private int f;

    public BehaviorRunSometimes(Behavior<? super E> behavior, IntRange intrange) {
        this(behavior, false, intrange);
    }

    public BehaviorRunSometimes(Behavior<? super E> behavior, boolean flag, IntRange intrange) {
        super(behavior.a);
        this.e = behavior;
        this.b = !flag;
        this.d = intrange;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        if (!this.e.a(worldserver, e0)) {
            return false;
        } else {
            if (this.b) {
                this.a(worldserver);
                this.b = false;
            }

            if (this.f > 0) {
                --this.f;
            }

            return !this.c && this.f == 0;
        }
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        this.e.a(worldserver, e0, i);
    }

    @Override
    protected boolean b(WorldServer worldserver, E e0, long i) {
        return this.e.b(worldserver, e0, i);
    }

    @Override
    protected void d(WorldServer worldserver, E e0, long i) {
        this.e.d(worldserver, e0, i);
        this.c = this.e.a() == Behavior.Status.RUNNING;
    }

    @Override
    protected void c(WorldServer worldserver, E e0, long i) {
        this.a(worldserver);
        this.e.c(worldserver, e0, i);
    }

    private void a(WorldServer worldserver) {
        this.f = this.d.a(worldserver.random);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    @Override
    public String toString() {
        return "RunSometimes: " + this.e;
    }
}
