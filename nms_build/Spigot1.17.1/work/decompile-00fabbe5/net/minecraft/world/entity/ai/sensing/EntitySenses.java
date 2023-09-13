package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;

public class EntitySenses {

    private final EntityInsentient mob;
    private final List<Entity> seen = Lists.newArrayList();
    private final List<Entity> unseen = Lists.newArrayList();

    public EntitySenses(EntityInsentient entityinsentient) {
        this.mob = entityinsentient;
    }

    public void a() {
        this.seen.clear();
        this.unseen.clear();
    }

    public boolean a(Entity entity) {
        if (this.seen.contains(entity)) {
            return true;
        } else if (this.unseen.contains(entity)) {
            return false;
        } else {
            this.mob.level.getMethodProfiler().enter("hasLineOfSight");
            boolean flag = this.mob.hasLineOfSight(entity);

            this.mob.level.getMethodProfiler().exit();
            if (flag) {
                this.seen.add(entity);
            } else {
                this.unseen.add(entity);
            }

            return flag;
        }
    }
}
