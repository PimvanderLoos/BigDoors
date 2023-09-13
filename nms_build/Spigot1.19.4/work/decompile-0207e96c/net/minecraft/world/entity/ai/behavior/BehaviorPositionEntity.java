package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3D;

public class BehaviorPositionEntity implements BehaviorPosition {

    private final Entity entity;
    private final boolean trackEyeHeight;

    public BehaviorPositionEntity(Entity entity, boolean flag) {
        this.entity = entity;
        this.trackEyeHeight = flag;
    }

    @Override
    public Vec3D currentPosition() {
        return this.trackEyeHeight ? this.entity.position().add(0.0D, (double) this.entity.getEyeHeight(), 0.0D) : this.entity.position();
    }

    @Override
    public BlockPosition currentBlockPosition() {
        return this.entity.blockPosition();
    }

    @Override
    public boolean isVisibleBy(EntityLiving entityliving) {
        Entity entity = this.entity;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving1 = (EntityLiving) entity;

            if (!entityliving1.isAlive()) {
                return false;
            } else {
                Optional<NearestVisibleLivingEntities> optional = entityliving.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

                return optional.isPresent() && ((NearestVisibleLivingEntities) optional.get()).contains(entityliving1);
            }
        } else {
            return true;
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public String toString() {
        return "EntityTracker for " + this.entity;
    }
}
