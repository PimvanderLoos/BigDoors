package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleBuilder {

    private final Schedule schedule;
    private final List<ScheduleBuilder.a> transitions = Lists.newArrayList();

    public ScheduleBuilder(Schedule schedule) {
        this.schedule = schedule;
    }

    public ScheduleBuilder a(int i, Activity activity) {
        this.transitions.add(new ScheduleBuilder.a(i, activity));
        return this;
    }

    public Schedule a() {
        Set set = (Set) this.transitions.stream().map(ScheduleBuilder.a::b).collect(Collectors.toSet());
        Schedule schedule = this.schedule;

        Objects.requireNonNull(this.schedule);
        set.forEach(schedule::a);
        this.transitions.forEach((schedulebuilder_a) -> {
            Activity activity = schedulebuilder_a.b();

            this.schedule.c(activity).forEach((scheduleactivity) -> {
                scheduleactivity.a(schedulebuilder_a.a(), 0.0F);
            });
            this.schedule.b(activity).a(schedulebuilder_a.a(), 1.0F);
        });
        return this.schedule;
    }

    private static class a {

        private final int time;
        private final Activity activity;

        public a(int i, Activity activity) {
            this.time = i;
            this.activity = activity;
        }

        public int a() {
            return this.time;
        }

        public Activity b() {
            return this.activity;
        }
    }
}
