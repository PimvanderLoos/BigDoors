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
    public boolean canContinueToUse() {
        return this.closeDoor && this.forgetTime > 0 && super.canContinueToUse();
    }

    @Override
    public void start() {
        this.forgetTime = 20;
        this.setOpen(true);
    }

    @Override
    public void stop() {
        this.setOpen(false);
    }

    @Override
    public void tick() {
        --this.forgetTime;
        super.tick();
    }
}
