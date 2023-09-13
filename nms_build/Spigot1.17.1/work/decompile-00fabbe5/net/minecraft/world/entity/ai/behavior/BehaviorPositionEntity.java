package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3D;

public class BehaviorPositionEntity implements BehaviorPosition {

    private final Entity entity;
    private final boolean trackEyeHeight;

    public BehaviorPositionEntity(Entity entity, boolean flag) {
        this.entity = entity;
        this.trackEyeHeight = flag;
    }

    @Override
    public Vec3D a() {
        return this.trackEyeHeight ? this.entity.getPositionVector().add(0.0D, (double) this.entity.getHeadHeight(), 0.0D) : this.entity.getPositionVector();
    }

    @Override
    public BlockPosition b() {
        return this.entity.getChunkCoordinates();
    }

    @Override
    public boolean a(EntityLiving entityliving) {
        if (!(this.entity instanceof EntityLiving)) {
            return true;
        } else {
            Optional<List<EntityLiving>> optional = entityliving.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

            return this.entity.isAlive() && optional.isPresent() && ((List) optional.get()).contains(this.entity);
        }
    }

    public Entity c() {
        return this.entity;
    }

    public String toString() {
        return "EntityTracker for " + this.entity;
    }
}
