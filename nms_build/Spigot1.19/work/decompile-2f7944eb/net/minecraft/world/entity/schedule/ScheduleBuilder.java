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

    public ScheduleBuilder changeActivityAt(int i, Activity activity) {
        this.transitions.add(new ScheduleBuilder.a(i, activity));
        return this;
    }

    public Schedule build() {
        Set set = (Set) this.transitions.stream().map(ScheduleBuilder.a::getActivity).collect(Collectors.toSet());
        Schedule schedule = this.schedule;

        Objects.requireNonNull(this.schedule);
        set.forEach(schedule::ensureTimelineExistsFor);
        this.transitions.forEach((schedulebuilder_a) -> {
            Activity activity = schedulebuilder_a.getActivity();

            this.schedule.getAllTimelinesExceptFor(activity).forEach((scheduleactivity) -> {
                scheduleactivity.addKeyframe(schedulebuilder_a.getTime(), 0.0F);
            });
            this.schedule.getTimelineFor(activity).addKeyframe(schedulebuilder_a.getTime(), 1.0F);
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

        public int getTime() {
            return this.time;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }
}
