package net.minecraft.world.entity.ai.sensing;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;

public class EntitySenses {

    private final EntityInsentient mob;
    private final IntSet seen = new IntOpenHashSet();
    private final IntSet unseen = new IntOpenHashSet();

    public EntitySenses(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void tick() {
        this.seen.clear();
        this.unseen.clear();
    }

    public boolean hasLineOfSight(Entity entity) {
        int i = entity.getId();

        if (this.seen.contains(i)) {
            return true;
        } else if (this.unseen.contains(i)) {
            return false;
        } else {
            this.mob.level.getProfiler().push("hasLineOfSight");
            boolean flag = this.mob.hasLineOfSight(entity);

            this.mob.level.getProfiler().pop();
            if (flag) {
                this.seen.add(i);
            } else {
                this.unseen.add(i);
            }

            return flag;
        }
    }
}
