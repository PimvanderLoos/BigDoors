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
    public boolean canUse() {
        return this.mob.level.isDay() && this.mob.getItemBySlot(EnumItemSlot.HEAD).isEmpty() && PathfinderGoalUtil.hasGroundPathNavigation(this.mob);
    }

    @Override
    public void start() {
        ((Navigation) this.mob.getNavigation()).setAvoidSun(true);
    }

    @Override
    public void stop() {
        if (PathfinderGoalUtil.hasGroundPathNavigation(this.mob)) {
            ((Navigation) this.mob.getNavigation()).setAvoidSun(false);
        }

    }
}
