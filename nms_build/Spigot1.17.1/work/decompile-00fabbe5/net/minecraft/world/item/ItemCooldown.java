package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class ItemCooldown {

    public final Map<Item, ItemCooldown.Info> cooldowns = Maps.newHashMap();
    public int tickCount;

    public ItemCooldown() {}

    public boolean hasCooldown(Item item) {
        return this.a(item, 0.0F) > 0.0F;
    }

    public float a(Item item, float f) {
        ItemCooldown.Info itemcooldown_info = (ItemCooldown.Info) this.cooldowns.get(item);

        if (itemcooldown_info != null) {
            float f1 = (float) (itemcooldown_info.endTime - itemcooldown_info.startTime);
            float f2 = (float) itemcooldown_info.endTime - ((float) this.tickCount + f);

            return MathHelper.a(f2 / f1, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void a() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator iterator = this.cooldowns.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<Item, ItemCooldown.Info> entry = (Entry) iterator.next();

                if (((ItemCooldown.Info) entry.getValue()).endTime <= this.tickCount) {
                    iterator.remove();
                    this.c((Item) entry.getKey());
                }
            }
        }

    }

    public void setCooldown(Item item, int i) {
        this.cooldowns.put(item, new ItemCooldown.Info(this.tickCount, this.tickCount + i));
        this.b(item, i);
    }

    public void b(Item item) {
        this.cooldowns.remove(item);
        this.c(item);
    }

    protected void b(Item item, int i) {}

    protected void c(Item item) {}

    public class Info {

        final int startTime;
        public final int endTime;

        Info(int i, int j) {
            this.startTime = i;
            this.endTime = j;
        }
    }
}
