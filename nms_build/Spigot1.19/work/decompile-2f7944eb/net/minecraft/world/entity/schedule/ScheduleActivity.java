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

    public ImmutableList<ActivityFrame> getKeyframes() {
        return ImmutableList.copyOf(this.keyframes);
    }

    public ScheduleActivity addKeyframe(int i, float f) {
        this.keyframes.add(new ActivityFrame(i, f));
        this.sortAndDeduplicateKeyframes();
        return this;
    }

    public ScheduleActivity addKeyframes(Collection<ActivityFrame> collection) {
        this.keyframes.addAll(collection);
        this.sortAndDeduplicateKeyframes();
        return this;
    }

    private void sortAndDeduplicateKeyframes() {
        Int2ObjectSortedMap<ActivityFrame> int2objectsortedmap = new Int2ObjectAVLTreeMap();

        this.keyframes.forEach((activityframe) -> {
            int2objectsortedmap.put(activityframe.getTimeStamp(), activityframe);
        });
        this.keyframes.clear();
        this.keyframes.addAll(int2objectsortedmap.values());
        this.previousIndex = 0;
    }

    public float getValueAt(int i) {
        if (this.keyframes.size() <= 0) {
            return 0.0F;
        } else {
            ActivityFrame activityframe = (ActivityFrame) this.keyframes.get(this.previousIndex);
            ActivityFrame activityframe1 = (ActivityFrame) this.keyframes.get(this.keyframes.size() - 1);
            boolean flag = i < activityframe.getTimeStamp();
            int j = flag ? 0 : this.previousIndex;
            float f = flag ? activityframe1.getValue() : activityframe.getValue();

            for (int k = j; k < this.keyframes.size(); ++k) {
                ActivityFrame activityframe2 = (ActivityFrame) this.keyframes.get(k);

                if (activityframe2.getTimeStamp() > i) {
                    break;
                }

                this.previousIndex = k;
                f = activityframe2.getValue();
            }

            return f;
        }
    }
}
