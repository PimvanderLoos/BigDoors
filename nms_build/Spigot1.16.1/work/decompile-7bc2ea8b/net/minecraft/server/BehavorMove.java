package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;

public class BehavorMove extends Behavior<EntityInsentient> {

    @Nullable
    private PathEntity b;
    @Nullable
    private BlockPosition c;
    private float d;
    private int e;

    public BehavorMove(int i) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), i);
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();
        MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();
        boolean flag = this.a(entityinsentient, memorytarget);

        if (!flag && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
            this.c = memorytarget.a().b();
            return true;
        } else {
            behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
            if (flag) {
                behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }

            return false;
        }
    }

    protected boolean b(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (this.b != null && this.c != null) {
            Optional<MemoryTarget> optional = entityinsentient.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET);
            NavigationAbstract navigationabstract = entityinsentient.getNavigation();

            return !navigationabstract.m() && optional.isPresent() && !this.a(entityinsentient, (MemoryTarget) optional.get());
        } else {
            return false;
        }
    }

    protected void c(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getNavigation().o();
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.PATH);
        this.b = null;
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBehaviorController().setMemory(MemoryModuleType.PATH, (Object) this.b);
        entityinsentient.getNavigation().a(this.b, (double) this.d);
        this.e = worldserver.getRandom().nextInt(10);
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        --this.e;
        if (this.e <= 0) {
            PathEntity pathentity = entityinsentient.getNavigation().k();
            BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

            if (this.b != pathentity) {
                this.b = pathentity;
                behaviorcontroller.setMemory(MemoryModuleType.PATH, (Object) pathentity);
            }

            if (pathentity != null && this.c != null) {
                MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();

                if (memorytarget.a().b().j(this.c) > 4.0D && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
                    this.c = memorytarget.a().b();
                    this.a(worldserver, entityinsentient, i);
                }

            }
        }
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget, long i) {
        BlockPosition blockposition = memorytarget.a().b();

        this.b = entityinsentient.getNavigation().a(blockposition, 0);
        this.d = memorytarget.b();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

        if (this.a(entityinsentient, memorytarget)) {
            behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean flag = this.b != null && this.b.i();

            if (flag) {
                behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!behaviorcontroller.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                behaviorcontroller.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object) i);
            }

            if (this.b != null) {
                return true;
            }

            Vec3D vec3d = RandomPositionGenerator.b((EntityCreature) entityinsentient, 10, 7, Vec3D.c((BaseBlockPosition) blockposition));

            if (vec3d != null) {
                this.b = entityinsentient.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.b != null;
            }
        }

        return false;
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget) {
        return memorytarget.a().b().k(entityinsentient.getChunkCoordinates()) <= memorytarget.c();
    }
}
