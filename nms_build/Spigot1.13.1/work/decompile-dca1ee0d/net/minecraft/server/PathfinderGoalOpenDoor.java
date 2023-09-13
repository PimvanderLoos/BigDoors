package net.minecraft.server;

public class PathfinderGoalOpenDoor extends PathfinderGoalDoorInteract {

    private final boolean d;
    private int e;

    public PathfinderGoalOpenDoor(EntityInsentient entityinsentient, boolean flag) {
        super(entityinsentient);
        this.a = entityinsentient;
        this.d = flag;
    }

    public boolean b() {
        return this.d && this.e > 0 && super.b();
    }

    public void c() {
        this.e = 20;
        this.a(true);
    }

    public void d() {
        this.a(false);
    }

    public void e() {
        --this.e;
        super.e();
    }
}
