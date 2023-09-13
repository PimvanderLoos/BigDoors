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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class BehavorMove extends Behavior<EntityInsentient> {

    private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
    private int remainingCooldown;
    @Nullable
    private PathEntity path;
    @Nullable
    private BlockPosition lastTargetPos;
    private float speedModifier;

    public BehavorMove() {
        this(150, 250);
    }

    public BehavorMove(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        } else {
            BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean flag = this.a(entityinsentient, memorytarget);

            if (!flag && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
                this.lastTargetPos = memorytarget.a().b();
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
        if (this.path != null && this.lastTargetPos != null) {
            Optional<MemoryTarget> optional = entityinsentient.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET);
            NavigationAbstract navigationabstract = entityinsentient.getNavigation();

            return !navigationabstract.m() && optional.isPresent() && !this.a(entityinsentient, (MemoryTarget) optional.get());
        } else {
            return false;
        }
    }

    protected void c(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (entityinsentient.getBehaviorController().hasMemory(MemoryModuleType.WALK_TARGET) && !this.a(entityinsentient, (MemoryTarget) entityinsentient.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET).get()) && entityinsentient.getNavigation().t()) {
            this.remainingCooldown = worldserver.getRandom().nextInt(40);
        }

        entityinsentient.getNavigation().o();
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.PATH);
        this.path = null;
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBehaviorController().setMemory(MemoryModuleType.PATH, (Object) this.path);
        entityinsentient.getNavigation().a(this.path, (double) this.speedModifier);
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        PathEntity pathentity = entityinsentient.getNavigation().k();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

        if (this.path != pathentity) {
            this.path = pathentity;
            behaviorcontroller.setMemory(MemoryModuleType.PATH, (Object) pathentity);
        }

        if (pathentity != null && this.lastTargetPos != null) {
            MemoryTarget memorytarget = (MemoryTarget) behaviorcontroller.getMemory(MemoryModuleType.WALK_TARGET).get();

            if (memorytarget.a().b().j(this.lastTargetPos) > 4.0D && this.a(entityinsentient, memorytarget, worldserver.getTime())) {
                this.lastTargetPos = memorytarget.a().b();
                this.a(worldserver, entityinsentient, i);
            }

        }
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget, long i) {
        BlockPosition blockposition = memorytarget.a().b();

        this.path = entityinsentient.getNavigation().a(blockposition, 0);
        this.speedModifier = memorytarget.b();
        BehaviorController<?> behaviorcontroller = entityinsentient.getBehaviorController();

        if (this.a(entityinsentient, memorytarget)) {
            behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean flag = this.path != null && this.path.j();

            if (flag) {
                behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!behaviorcontroller.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                behaviorcontroller.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object) i);
            }

            if (this.path != null) {
                return true;
            }

            Vec3D vec3d = DefaultRandomPos.a((EntityCreature) entityinsentient, 10, 7, Vec3D.c((BaseBlockPosition) blockposition), 1.5707963705062866D);

            if (vec3d != null) {
                this.path = entityinsentient.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.path != null;
            }
        }

        return false;
    }

    private boolean a(EntityInsentient entityinsentient, MemoryTarget memorytarget) {
        return memorytarget.a().b().k(entityinsentient.getChunkCoordinates()) <= memorytarget.c();
    }
}
