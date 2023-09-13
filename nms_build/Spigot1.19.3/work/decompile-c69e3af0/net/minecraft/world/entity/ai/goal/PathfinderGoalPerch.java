package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.animal.EntityPerchable;

public class PathfinderGoalPerch extends PathfinderGoal {

    private final EntityPerchable entity;
    private EntityPlayer owner;
    private boolean isSittingOnShoulder;

    public PathfinderGoalPerch(EntityPerchable entityperchable) {
        this.entity = entityperchable;
    }

    @Override
    public boolean canUse() {
        EntityPlayer entityplayer = (EntityPlayer) this.entity.getOwner();
        boolean flag = entityplayer != null && !entityplayer.isSpectator() && !entityplayer.getAbilities().flying && !entityplayer.isInWater() && !entityplayer.isInPowderSnow;

        return !this.entity.isOrderedToSit() && flag && this.entity.canSitOnShoulder();
    }

    @Override
    public boolean isInterruptable() {
        return !this.isSittingOnShoulder;
    }

    @Override
    public void start() {
        this.owner = (EntityPlayer) this.entity.getOwner();
        this.isSittingOnShoulder = false;
    }

    @Override
    public void tick() {
        if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
            if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
                this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
            }

        }
    }
}
