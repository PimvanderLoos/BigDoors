package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class BehavorMove extends Behavior<EntityInsentient> {

    private int b;
    @Nullable
    private PathEntity c;
    @Nullable
    private BlockPosition d;
    private float e;

    public BehavorMove() {
        this(150, 250);
    }

    public BehavorMove(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        if (this.b > 0) {
            --this.b;
            return false;
        } else {
            BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean flag = this.a(entityinsentient, memorytarget);

            if (!flag && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
                this.d = memorytarget.a().b();
                return true;
            } else {
                behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
                if (flag) {
                    behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }

                return false;
            }
        }
    }

    protected boolean b(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (this.c != null && this.d != null) {
            Optional<MemoryTarget> optional = entityinsentient.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET);
            NavigationAbstract navigationabstract = entityinsentient.getNavigation();

            return !navigationabstract.m() && optional.isPresent() && !this.a(entityinsentient, (MemoryTarget) optional.get());
        } else {
            return false;
        }
    }

    protected void c(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (entityinsentient.getBehaviorController().hasMemory(MemoryModuleType.WALK_TARGET) && !this.a(entityinsentient, (MemoryTarget) entityinsentient.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET).get()) && entityinsentient.getNavigation().t()) {
            this.b = worldserver.getRandom().nextInt(40);
        }

        entityinsentient.getNavigation().o();
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.PATH);
        this.c = null;
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBehaviorController().setMemory(MemoryModuleType.PATH, (Object) this.c);
        entityinsentient.getNavigation().a(this.c, (double) this.e);
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        PathEntity pathentity = entityinsentient.getNavigation().k();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

        if (this.c != pathentity) {
            this.c = pathentity;
            behaviorcontroller.setMemory(MemoryModuleType.PATH, (Object) pathentity);
        }

        if (pathentity != null && this.d != null) {
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();

            if (memorytarget.a().b().j(this.d) > 4.0D && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
                this.d = memorytarget.a().b();
                this.a(worldserver, entityinsentient, i);
            }

        }
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget, long i) {
        BlockPosition blockposition = memorytarget.a().b();

        this.c = entityinsentient.getNavigation().a(blockposition, 0);
        this.e = memorytarget.b();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

        if (this.a(entityinsentient, memorytarget)) {
            behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean flag = this.c != null && this.c.j();

            if (flag) {
                behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!behaviorcontroller.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                behaviorcontroller.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object) i);
            }

            if (this.c != null) {
                return true;
            }

            Vec3D vec3d = RandomPositionGenerator.b((EntityCreature) entityinsentient, 10, 7, Vec3D.c((BaseBlockPosition) blockposition));

            if (vec3d != null) {
                this.c = entityinsentient.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.c != null;
            }
        }

        return false;
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget) {
        return memorytarget.a().b().k(entityinsentient.getChunkCoordinates()) <= memorytarget.c();
    }
}
