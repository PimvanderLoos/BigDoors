package net.minecraft.world.entity.schedule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import java.util.List;

public class ScheduleActivity {

    private final List<ActivityFrame> keyframes = Lists.newArrayList();
    private int previousIndex;

    public ScheduleActivity() {}

    public ImmutableList<ActivityFrame> a() {
        return ImmutableList.copyOf(this.keyframes);
    }

    public ScheduleActivity a(int i, float f) {
        this.keyframes.add(new ActivityFrame(i, f));
        this.b();
        return this;
    }

    public ScheduleActivity a(Collection<ActivityFrame> collection) {
        this.keyframes.addAll(collection);
        this.b();
        return this;
    }

    private void b() {
        Int2ObjectSortedMap<ActivityFrame> int2objectsortedmap = new Int2ObjectAVLTreeMap();

        this.keyframes.forEach((activityframe) -> {
            int2objectsortedmap.put(activityframe.a(), activityframe);
        });
        this.keyframes.clear();
        this.keyframes.addAll(int2objectsortedmap.values());
        this.previousIndex = 0;
    }

    public float a(int i) {
        if (this.keyframes.size() <= 0) {
            return 0.0F;
        } else {
            ActivityFrame activityframe = (ActivityFrame) this.keyframes.get(this.previousIndex);
            ActivityFrame activityframe1 = (ActivityFrame) this.keyframes.get(this.keyframes.size() - 1);
            boolean flag = i < activityframe.a();
            int j = flag ? 0 : this.previousIndex;
            float f = flag ? activityframe1.b() : activityframe.b();

            for (int k = j; k < this.keyframes.size(); ++k) {
                ActivityFrame activityframe2 = (ActivityFrame) this.keyframes.get(k);

                if (activityframe2.a() > i) {
                    break;
                }

                this.previousIndex = k;
                f = activityframe2.b();
            }

            return f;
        }
    }
}
