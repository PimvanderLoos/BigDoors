package net.minecraft.world.entity.schedule;

public class ActivityFrame {

    private final int timeStamp;
    private final float value;

    public ActivityFrame(int i, float f) {
        this.timeStamp = i;
        this.value = f;
    }

    public int getTimeStamp() {
        return this.timeStamp;
    }

    public float getValue() {
        return this.value;
    }
}
