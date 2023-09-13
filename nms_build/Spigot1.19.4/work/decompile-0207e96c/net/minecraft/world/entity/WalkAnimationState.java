package net.minecraft.world.entity;

import net.minecraft.util.MathHelper;

public class WalkAnimationState {

    private float speedOld;
    private float speed;
    private float position;

    public WalkAnimationState() {}

    public void setSpeed(float f) {
        this.speed = f;
    }

    public void update(float f, float f1) {
        this.speedOld = this.speed;
        this.speed += (f - this.speed) * f1;
        this.position += this.speed;
    }

    public float speed() {
        return this.speed;
    }

    public float speed(float f) {
        return MathHelper.lerp(f, this.speedOld, this.speed);
    }

    public float position() {
        return this.position;
    }

    public float position(float f) {
        return this.position - this.speed * (1.0F - f);
    }

    public boolean isMoving() {
        return this.speed > 1.0E-5F;
    }
}
