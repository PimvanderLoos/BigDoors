package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.EntityInsentient;

public class ControllerJump implements Control {

    private final EntityInsentient mob;
    protected boolean jump;

    public ControllerJump(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void jump() {
        this.jump = true;
    }

    public void tick() {
        this.mob.setJumping(this.jump);
        this.jump = false;
    }
}
