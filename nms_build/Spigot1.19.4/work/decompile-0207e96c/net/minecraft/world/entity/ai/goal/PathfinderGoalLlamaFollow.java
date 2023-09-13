package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.decoration.EntityLeash;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalLlamaFollow extends PathfinderGoal {

    public final EntityLlama llama;
    private double speedModifier;
    private static final int CARAVAN_LIMIT = 8;
    private int distCheckCounter;

    public PathfinderGoalLlamaFollow(EntityLlama entityllama, double d0) {
        this.llama = entityllama;
        this.speedModifier = d0;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.llama.isLeashed() && !this.llama.inCaravan()) {
            List<Entity> list = this.llama.level.getEntities((Entity) this.llama, this.llama.getBoundingBox().inflate(9.0D, 4.0D, 9.0D), (entity) -> {
                EntityTypes<?> entitytypes = entity.getType();

                return entitytypes == EntityTypes.LLAMA || entitytypes == EntityTypes.TRADER_LLAMA;
            });
            EntityLlama entityllama = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            Entity entity;
            EntityLlama entityllama1;
            double d1;

            while (iterator.hasNext()) {
                entity = (Entity) iterator.next();
                entityllama1 = (EntityLlama) entity;
                if (entityllama1.inCaravan() && !entityllama1.hasCaravanTail()) {
                    d1 = this.llama.distanceToSqr((Entity) entityllama1);
                    if (d1 <= d0) {
                        d0 = d1;
                        entityllama = entityllama1;
                    }
                }
            }

            if (entityllama == null) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entity = (Entity) iterator.next();
                    entityllama1 = (EntityLlama) entity;
                    if (entityllama1.isLeashed() && !entityllama1.hasCaravanTail()) {
                        d1 = this.llama.distanceToSqr((Entity) entityllama1);
                        if (d1 <= d0) {
                            d0 = d1;
                            entityllama = entityllama1;
                        }
                    }
                }
            }

            if (entityllama == null) {
                return false;
            } else if (d0 < 4.0D) {
                return false;
            } else if (!entityllama.isLeashed() && !this.firstIsLeashed(entityllama, 1)) {
                return false;
            } else {
                this.llama.joinCaravan(entityllama);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
            double d0 = this.llama.distanceToSqr((Entity) this.llama.getCaravanHead());

            if (d0 > 676.0D) {
                if (this.speedModifier <= 3.0D) {
                    this.speedModifier *= 1.2D;
                    this.distCheckCounter = reducedTickDelay(40);
                    return true;
                }

                if (this.distCheckCounter == 0) {
                    return false;
                }
            }

            if (this.distCheckCounter > 0) {
                --this.distCheckCounter;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stop() {
        this.llama.leaveCaravan();
        this.speedModifier = 2.1D;
    }

    @Override
    public void tick() {
        if (this.llama.inCaravan()) {
            if (!(this.llama.getLeashHolder() instanceof EntityLeash)) {
                EntityLlama entityllama = this.llama.getCaravanHead();
                double d0 = (double) this.llama.distanceTo(entityllama);
                float f = 2.0F;
                Vec3D vec3d = (new Vec3D(entityllama.getX() - this.llama.getX(), entityllama.getY() - this.llama.getY(), entityllama.getZ() - this.llama.getZ())).normalize().scale(Math.max(d0 - 2.0D, 0.0D));

                this.llama.getNavigation().moveTo(this.llama.getX() + vec3d.x, this.llama.getY() + vec3d.y, this.llama.getZ() + vec3d.z, this.speedModifier);
            }
        }
    }

    private boolean firstIsLeashed(EntityLlama entityllama, int i) {
        if (i > 8) {
            return false;
        } else if (entityllama.inCaravan()) {
            if (entityllama.getCaravanHead().isLeashed()) {
                return true;
            } else {
                EntityLlama entityllama1 = entityllama.getCaravanHead();

                ++i;
                return this.firstIsLeashed(entityllama1, i);
            }
        } else {
            return false;
        }
    }
}
