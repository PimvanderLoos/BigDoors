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
    public boolean a() {
        EntityPlayer entityplayer = (EntityPlayer) this.entity.getOwner();
        boolean flag = entityplayer != null && !entityplayer.isSpectator() && !entityplayer.getAbilities().flying && !entityplayer.isInWater() && !entityplayer.isInPowderSnow;

        return !this.entity.isWillSit() && flag && this.entity.fH();
    }

    @Override
    public boolean C_() {
        return !this.isSittingOnShoulder;
    }

    @Override
    public void c() {
        this.owner = (EntityPlayer) this.entity.getOwner();
        this.isSittingOnShoulder = false;
    }

    @Override
    public void e() {
        if (!this.isSittingOnShoulder && !this.entity.isSitting() && !this.entity.isLeashed()) {
            if (this.entity.getBoundingBox().c(this.owner.getBoundingBox())) {
                this.isSittingOnShoulder = this.entity.b(this.owner);
            }

        }
    }
}
