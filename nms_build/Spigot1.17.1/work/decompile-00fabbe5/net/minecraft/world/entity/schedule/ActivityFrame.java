package net.minecraft.world.entity.schedule;

public class ActivityFrame {

    private final int timeStamp;
    private final float value;

    public ActivityFrame(int i, float f) {
        this.timeStamp = i;
        this.value = f;
    }

    public int a() {
        return this.timeStamp;
    }

    public float b() {
        return this.value;
    }
}
