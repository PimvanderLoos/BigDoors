package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorLookTarget extends Behavior<EntityLiving> {

    private final Predicate<EntityLiving> b;
    private final float c;

    public BehaviorLookTarget(EnumCreatureType enumcreaturetype, float f) {
        this((entityliving) -> {
            return enumcreaturetype.equals(entityliving.getEntityType().e());
        }, f);
    }

    public BehaviorLookTarget(EntityTypes<?> entitytypes, float f) {
        this((entityliving) -> {
            return entitytypes.equals(entityliving.getEntityType());
        }, f);
    }

    public BehaviorLookTarget(float f) {
        this((entityliving) -> {
            return true;
        }, f);
    }

    public BehaviorLookTarget(Predicate<EntityLiving> predicate, float f) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryStatus.VALUE_PRESENT));
        this.b = predicate;
        this.c = f * f;
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return ((List) entityliving.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_MOBS).get()).stream().anyMatch(this.b);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((list) -> {
            list.stream().filter(this.b).filter((entityliving1) -> {
                return entityliving1.h((Entity) entityliving) <= (double) this.c;
            }).findFirst().ifPresent((entityliving1) -> {
                behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving1, true)));
            });
        });
    }
}
