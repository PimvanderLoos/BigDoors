package net.minecraft.world.entity.schedule;

import net.minecraft.core.IRegistry;

public class Activity {

    public static final Activity CORE = a("core");
    public static final Activity IDLE = a("idle");
    public static final Activity WORK = a("work");
    public static final Activity PLAY = a("play");
    public static final Activity REST = a("rest");
    public static final Activity MEET = a("meet");
    public static final Activity PANIC = a("panic");
    public static final Activity RAID = a("raid");
    public static final Activity PRE_RAID = a("pre_raid");
    public static final Activity HIDE = a("hide");
    public static final Activity FIGHT = a("fight");
    public static final Activity CELEBRATE = a("celebrate");
    public static final Activity ADMIRE_ITEM = a("admire_item");
    public static final Activity AVOID = a("avoid");
    public static final Activity RIDE = a("ride");
    public static final Activity PLAY_DEAD = a("play_dead");
    public static final Activity LONG_JUMP = a("long_jump");
    public static final Activity RAM = a("ram");
    private final String name;
    private final int hashCode;

    private Activity(String s) {
        this.name = s;
        this.hashCode = s.hashCode();
    }

    public String a() {
        return this.name;
    }

    private static Activity a(String s) {
        return (Activity) IRegistry.a(IRegistry.ACTIVITY, s, (Object) (new Activity(s)));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Activity activity = (Activity) object;

            return this.name.equals(activity.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.a();
    }
}
