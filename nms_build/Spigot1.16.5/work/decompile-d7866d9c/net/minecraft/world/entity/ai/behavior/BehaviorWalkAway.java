package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.RandomPositionGenerator;
import net.minecraft.world.phys.Vec3D;

public class BehaviorWalkAway<T> extends Behavior<EntityCreature> {

    private final MemoryModuleType<T> b;
    private final float c;
    private final int d;
    private final Function<T, Vec3D> e;

    public BehaviorWalkAway(MemoryModuleType<T> memorymoduletype, float f, int i, boolean flag, Function<T, Vec3D> function) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, flag ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = memorymoduletype;
        this.c = f;
        this.d = i;
        this.e = function;
    }

    public static BehaviorWalkAway<BlockPosition> a(MemoryModuleType<BlockPosition> memorymoduletype, float f, int i, boolean flag) {
        return new BehaviorWalkAway<>(memorymoduletype, f, i, flag, Vec3D::c);
    }

    public static BehaviorWalkAway<? extends Entity> b(MemoryModuleType<? extends Entity> memorymoduletype, float f, int i, boolean flag) {
        return new BehaviorWalkAway<>(memorymoduletype, f, i, flag, Entity::getPositionVector);
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        return this.b(entitycreature) ? false : entitycreature.getPositionVector().a((IPosition) this.a(entitycreature), (double) this.d);
    }

    private Vec3D a(EntityCreature entitycreature) {
        return (Vec3D) this.e.apply(entitycreature.getBehaviorController().getMemory(this.b).get());
    }

    private boolean b(EntityCreature entitycreature) {
        if (!entitycreature.getBehaviorController().hasMemory(MemoryModuleType.WALK_TARGET)) {
            return false;
        } else {
            MemoryTarget memorytarget = (MemoryTarget) entitycreature.getBehaviorController().getMemory(MemoryModuleType.WALK_TARGET).get();

            if (memorytarget.b() != this.c) {
                return false;
            } else {
                Vec3D vec3d = memorytarget.a().a().d(entitycreature.getPositionVector());
                Vec3D vec3d1 = this.a(entitycreature).d(entitycreature.getPositionVector());

                return vec3d.b(vec3d1) < 0.0D;
            }
        }
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        a(entitycreature, this.a(entitycreature), this.c);
    }

    private static void a(EntityCreature entitycreature, Vec3D vec3d, float f) {
        for (int i = 0; i < 10; ++i) {
            Vec3D vec3d1 = RandomPositionGenerator.d(entitycreature, 16, 7, vec3d);

            if (vec3d1 != null) {
                entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d1, f, 0)));
                return;
            }
        }

    }
}
