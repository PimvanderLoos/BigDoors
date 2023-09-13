package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.phys.Vec3D;

public class BehaviorStrollRandomUnconstrained extends Behavior<EntityCreature> {

    private final float b;
    private final int c;
    private final int d;

    public BehaviorStrollRandomUnconstrained(float f) {
        this(f, 10, 7);
    }

    public BehaviorStrollRandomUnconstrained(float f, int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.b = f;
        this.c = i;
        this.d = j;
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        Optional<Vec3D> optional = Optional.ofNullable(RandomPositionGenerator.b(entitycreature, this.c, this.d));

        entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
            return new MemoryTarget(vec3d, this.b, 0);
        }));
    }
}
