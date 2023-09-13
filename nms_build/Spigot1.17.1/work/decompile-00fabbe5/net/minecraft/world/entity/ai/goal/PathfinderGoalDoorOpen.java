package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalDoorOpen extends PathfinderGoalDoorInteract {

    private final boolean closeDoor;
    private int forgetTime;

    public PathfinderGoalDoorOpen(EntityInsentient entityinsentient, boolean flag) {
        super(entityinsentient);
        this.mob = entityinsentient;
        this.closeDoor = flag;
    }

    @Override
    public boolean b() {
        return this.closeDoor && this.forgetTime > 0 && super.b();
    }

    @Override
    public void c() {
        this.forgetTime = 20;
        this.a(true);
    }

    @Override
    public void d() {
        this.a(false);
    }

    @Override
    public void e() {
        --this.forgetTime;
        super.e();
    }
}
