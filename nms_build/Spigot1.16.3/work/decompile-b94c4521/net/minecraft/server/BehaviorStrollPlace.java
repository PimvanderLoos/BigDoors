package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;

public class BehaviorStrollPlace extends Behavior<EntityCreature> {

    private final MemoryModuleType<GlobalPos> b;
    private final int c;
    private final int d;
    private final float e;
    private long f;

    public BehaviorStrollPlace(MemoryModuleType<GlobalPos> memorymoduletype, float f, int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = memorymoduletype;
        this.e = f;
        this.c = i;
        this.d = j;
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        Optional<GlobalPos> optional = entitycreature.getBehaviorController().getMemory(this.b);

        return optional.isPresent() && worldserver.getDimensionKey() == ((GlobalPos) optional.get()).getDimensionManager() && ((GlobalPos) optional.get()).getBlockPosition().a((IPosition) entitycreature.getPositionVector(), (double) this.d);
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i > this.f) {
            BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();
            Optional<GlobalPos> optional = behaviorcontroller.getMemory(this.b);

            optional.ifPresent((globalpos) -> {
                behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(globalpos.getBlockPosition(), this.e, this.c)));
            });
            this.f = i + 80L;
        }

    }
}
