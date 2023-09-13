package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorNearestVillage extends Behavior<EntityVillager> {

    private final float b;
    private final int c;

    public BehaviorNearestVillage(float f, int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.b = f;
        this.c = i;
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        return !worldserver.b_(entityvillager.getChunkCoordinates());
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        VillagePlace villageplace = worldserver.x();
        int j = villageplace.a(SectionPosition.a(entityvillager.getChunkCoordinates()));
        Vec3D vec3d = null;

        for (int k = 0; k < 5; ++k) {
            Vec3D vec3d1 = RandomPositionGenerator.a((EntityCreature) entityvillager, 15, 7, (blockposition) -> {
                return (double) (-worldserver.b(SectionPosition.a(blockposition)));
            });

            if (vec3d1 != null) {
                int l = villageplace.a(SectionPosition.a(new BlockPosition(vec3d1)));

                if (l < j) {
                    vec3d = vec3d1;
                    break;
                }

                if (l == j) {
                    vec3d = vec3d1;
                }
            }
        }

        if (vec3d != null) {
            entityvillager.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, this.b, this.c)));
        }

    }
}
