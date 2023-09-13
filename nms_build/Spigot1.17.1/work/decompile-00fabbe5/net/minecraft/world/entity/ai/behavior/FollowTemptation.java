package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.SystemUtils;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.player.EntityHuman;

public class FollowTemptation extends Behavior<EntityCreature> {

    public static final int TEMPTATION_COOLDOWN = 100;
    public static final double CLOSE_ENOUGH_DIST = 2.5D;
    private final Function<EntityLiving, Float> speedModifier;

    public FollowTemptation(Function<EntityLiving, Float> function) {
        super((Map) SystemUtils.a(() -> {
            Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();

            builder.put(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED);
            builder.put(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED);
            builder.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
            builder.put(MemoryModuleType.IS_TEMPTED, MemoryStatus.REGISTERED);
            builder.put(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_PRESENT);
            builder.put(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT);
            return builder.build();
        }));
        this.speedModifier = function;
    }

    protected float a(EntityCreature entitycreature) {
        return (Float) this.speedModifier.apply(entitycreature);
    }

    private Optional<EntityHuman> b(EntityCreature entitycreature) {
        return entitycreature.getBehaviorController().getMemory(MemoryModuleType.TEMPTING_PLAYER);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    protected boolean b(WorldServer worldserver, EntityCreature entitycreature, long i) {
        return this.b(entitycreature).isPresent() && !entitycreature.getBehaviorController().hasMemory(MemoryModuleType.BREED_TARGET);
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        entitycreature.getBehaviorController().setMemory(MemoryModuleType.IS_TEMPTED, (Object) true);
    }

    protected void c(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, (int) 100);
        behaviorcontroller.setMemory(MemoryModuleType.IS_TEMPTED, (Object) false);
        behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
        behaviorcontroller.removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void d(WorldServer worldserver, EntityCreature entitycreature, long i) {
        EntityHuman entityhuman = (EntityHuman) this.b(entitycreature).get();
        BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityhuman, true)));
        if (entitycreature.f((Entity) entityhuman) < 6.25D) {
            behaviorcontroller.removeMemory(MemoryModuleType.WALK_TARGET);
        } else {
            behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityhuman, false), this.a(entitycreature), 2)));
        }

    }
}
