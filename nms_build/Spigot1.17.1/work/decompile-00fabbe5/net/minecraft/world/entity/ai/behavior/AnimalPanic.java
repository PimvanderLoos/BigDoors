package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class AnimalPanic extends Behavior<EntityCreature> {

    private static final int PANIC_MIN_DURATION = 100;
    private static final int PANIC_MAX_DURATION = 120;
    private static final int PANIC_DISTANCE_HORIZANTAL = 5;
    private static final int PANIC_DISTANCE_VERTICAL = 4;
    private final float speedMultiplier;

    public AnimalPanic(float f) {
        super(ImmutableMap.of(MemoryModuleType.HURT_BY, MemoryStatus.VALUE_PRESENT), 100, 120);
        this.speedMultiplier = f;
    }

    protected boolean b(WorldServer worldserver, EntityCreature entitycreature, long i) {
        return true;
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        entitycreature.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
    }

    protected void d(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (entitycreature.getNavigation().m()) {
            Vec3D vec3d = LandRandomPos.a(entitycreature, 5, 4);

            if (vec3d != null) {
                entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, this.speedMultiplier, 0)));
            }
        }

    }
}
