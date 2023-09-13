package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;

public class PathfinderGoalRestrictSun extends PathfinderGoal {

    private final EntityCreature mob;

    public PathfinderGoalRestrictSun(EntityCreature entitycreature) {
        this.mob = entitycreature;
    }

    @Override
    public boolean a() {
        return this.mob.level.isDay() && this.mob.getEquipment(EnumItemSlot.HEAD).isEmpty() && PathfinderGoalUtil.a(this.mob);
    }

    @Override
    public void c() {
        ((Navigation) this.mob.getNavigation()).c(true);
    }

    @Override
    public void d() {
        if (PathfinderGoalUtil.a(this.mob)) {
            ((Navigation) this.mob.getNavigation()).c(false);
        }

    }
}
