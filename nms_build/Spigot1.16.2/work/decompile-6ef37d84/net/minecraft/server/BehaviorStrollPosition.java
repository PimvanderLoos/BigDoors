package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class BehaviorStrollPosition extends Behavior<EntityCreature> {

    private final MemoryModuleType<GlobalPos> b;
    private long c;
    private final int d;
    private float e;

    public BehaviorStrollPosition(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = memorymoduletype;
        this.e = f;
        this.d = i;
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        Optional<GlobalPos> optional = entitycreature.getBehaviorController().getMemory(this.b);

        return optional.isPresent() && worldserver.getDimensionKey() == ((GlobalPos) optional.get()).getDimensionManager() && ((GlobalPos) optional.get()).getBlockPosition().a((IPosition) entitycreature.getPositionVector(), (double) this.d);
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i > this.c) {
            Optional<Vec3D> optional = Optional.ofNullable(RandomPositionGenerator.b(entitycreature, 8, 6));

            entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
                return new MemoryTarget(vec3d, this.e, 1);
            }));
            this.c = i + 180L;
        }

    }
}
