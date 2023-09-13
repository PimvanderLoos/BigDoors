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
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        if (!this.llama.isLeashed() && !this.llama.gm()) {
            List<Entity> list = this.llama.level.getEntities(this.llama, this.llama.getBoundingBox().grow(9.0D, 4.0D, 9.0D), (entity) -> {
                EntityTypes<?> entitytypes = entity.getEntityType();

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
                if (entityllama1.gm() && !entityllama1.gl()) {
                    d1 = this.llama.f((Entity) entityllama1);
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
                    if (entityllama1.isLeashed() && !entityllama1.gl()) {
                        d1 = this.llama.f((Entity) entityllama1);
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
            } else if (!entityllama.isLeashed() && !this.a(entityllama, 1)) {
                return false;
            } else {
                this.llama.a(entityllama);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean b() {
        if (this.llama.gm() && this.llama.gn().isAlive() && this.a(this.llama, 0)) {
            double d0 = this.llama.f((Entity) this.llama.gn());

            if (d0 > 676.0D) {
                if (this.speedModifier <= 3.0D) {
                    this.speedModifier *= 1.2D;
                    this.distCheckCounter = 40;
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
    public void d() {
        this.llama.gk();
        this.speedModifier = 2.1D;
    }

    @Override
    public void e() {
        if (this.llama.gm()) {
            if (!(this.llama.getLeashHolder() instanceof EntityLeash)) {
                EntityLlama entityllama = this.llama.gn();
                double d0 = (double) this.llama.e((Entity) entityllama);
                float f = 2.0F;
                Vec3D vec3d = (new Vec3D(entityllama.locX() - this.llama.locX(), entityllama.locY() - this.llama.locY(), entityllama.locZ() - this.llama.locZ())).d().a(Math.max(d0 - 2.0D, 0.0D));

                this.llama.getNavigation().a(this.llama.locX() + vec3d.x, this.llama.locY() + vec3d.y, this.llama.locZ() + vec3d.z, this.speedModifier);
            }
        }
    }

    private boolean a(EntityLlama entityllama, int i) {
        if (i > 8) {
            return false;
        } else if (entityllama.gm()) {
            if (entityllama.gn().isLeashed()) {
                return true;
            } else {
                EntityLlama entityllama1 = entityllama.gn();

                ++i;
                return this.a(entityllama1, i);
            }
        } else {
            return false;
        }
    }
}
