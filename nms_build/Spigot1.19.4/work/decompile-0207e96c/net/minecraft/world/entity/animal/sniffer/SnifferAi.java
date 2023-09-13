package net.minecraft.world.entity.animal.sniffer;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorSwim;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import org.slf4j.Logger;

public class SnifferAi {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_LOOK_DISTANCE = 6;
    static final List<SensorType<? extends Sensor<? super Sniffer>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS);
    static final List<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.IS_PANICKING, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryModuleType.SNIFFER_DIGGING, MemoryModuleType.SNIFFER_HAPPY, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.BREED_TARGET, new MemoryModuleType[0]);
    private static final int SNIFFING_COOLDOWN_TICKS = 9600;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_SNIFFING = 1.25F;

    public SnifferAi() {}

    protected static BehaviorController<?> makeBrain(BehaviorController<Sniffer> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        initSniffingActivity(behaviorcontroller);
        initDigActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(Set.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Sniffer> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new BehaviorSwim(0.8F), new AnimalPanic(2.0F) {
            @Override
            protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
                entitycreature.getBrain().eraseMemory(MemoryModuleType.SNIFFER_DIGGING);
                entitycreature.getBrain().eraseMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
                ((Sniffer) entitycreature).transitionTo(Sniffer.a.IDLING);
                super.start(worldserver, entitycreature, i);
            }
        }, new BehavorMove(10000, 15000)));
    }

    private static void initSniffingActivity(BehaviorController<Sniffer> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.SNIFF, ImmutableList.of(Pair.of(0, new SnifferAi.e())), Set.of(Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryStatus.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initDigActivity(BehaviorController<Sniffer> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.DIG, ImmutableList.of(Pair.of(0, new SnifferAi.a(160, 180)), Pair.of(0, new SnifferAi.c(40))), Set.of(Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.SNIFFER_DIGGING, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initIdleActivity(BehaviorController<Sniffer> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorLook(45, 90)), Pair.of(0, new SnifferAi.b(40, 100)), Pair.of(0, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(BehaviorLookWalk.create(1.0F, 3), 2), Pair.of(new SnifferAi.d(40, 80), 1), Pair.of(new SnifferAi.f(40, 80), 1), Pair.of(new BehaviorMakeLoveAnimal(EntityTypes.SNIFFER, 1.0F), 1), Pair.of(BehaviorLookTarget.create(EntityTypes.PLAYER, 6.0F), 1), Pair.of(BehaviorStrollRandomUnconstrained.stroll(1.0F), 1), Pair.of(new BehaviorNop(5, 20), 2))))), Set.of(Pair.of(MemoryModuleType.SNIFFER_DIGGING, MemoryStatus.VALUE_ABSENT)));
    }

    static void updateActivity(Sniffer sniffer) {
        sniffer.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.DIG, Activity.SNIFF, Activity.IDLE));
    }

    private static class e extends Behavior<Sniffer> {

        e() {
            super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryStatus.VALUE_PRESENT), 600);
        }

        protected boolean checkExtraStartConditions(WorldServer worldserver, Sniffer sniffer) {
            return !sniffer.isPanicking() && !sniffer.isInWater();
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            if (sniffer.isPanicking() && !sniffer.isInWater()) {
                return false;
            } else {
                Optional<BlockPosition> optional = sniffer.getBrain().getMemory(MemoryModuleType.WALK_TARGET).map(MemoryTarget::getTarget).map(BehaviorPosition::currentBlockPosition);
                Optional<BlockPosition> optional1 = sniffer.getBrain().getMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);

                return !optional.isEmpty() && !optional1.isEmpty() ? ((BlockPosition) optional1.get()).equals(optional.get()) : false;
            }
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.SEARCHING);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            if (sniffer.canDig()) {
                sniffer.getBrain().setMemory(MemoryModuleType.SNIFFER_DIGGING, (Object) true);
            }

            sniffer.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            sniffer.getBrain().eraseMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
        }
    }

    private static class a extends Behavior<Sniffer> {

        a(int i, int j) {
            super(Map.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_DIGGING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT), i, j);
        }

        protected boolean checkExtraStartConditions(WorldServer worldserver, Sniffer sniffer) {
            return !sniffer.isPanicking() && !sniffer.isInWater();
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            return sniffer.getBrain().getMemory(MemoryModuleType.SNIFFER_DIGGING).isPresent() && !sniffer.isPanicking();
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.DIGGING);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 9600L);
        }
    }

    private static class c extends Behavior<Sniffer> {

        c(int i) {
            super(Map.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_DIGGING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_PRESENT), i, i);
        }

        protected boolean checkExtraStartConditions(WorldServer worldserver, Sniffer sniffer) {
            return true;
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            return sniffer.getBrain().getMemory(MemoryModuleType.SNIFFER_DIGGING).isPresent();
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.RISING);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            boolean flag = this.timedOut(i);

            sniffer.transitionTo(Sniffer.a.IDLING).onDiggingComplete(flag);
            sniffer.getBrain().eraseMemory(MemoryModuleType.SNIFFER_DIGGING);
            sniffer.getBrain().setMemory(MemoryModuleType.SNIFFER_HAPPY, (Object) true);
        }
    }

    private static class b extends Behavior<Sniffer> {

        b(int i, int j) {
            super(Map.of(MemoryModuleType.SNIFFER_HAPPY, MemoryStatus.VALUE_PRESENT), i, j);
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            return true;
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.FEELING_HAPPY);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.IDLING);
            sniffer.getBrain().eraseMemory(MemoryModuleType.SNIFFER_HAPPY);
        }
    }

    private static class d extends Behavior<Sniffer> {

        d(int i, int j) {
            super(Map.of(MemoryModuleType.SNIFFER_DIGGING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_HAPPY, MemoryStatus.VALUE_ABSENT), i, j);
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            return true;
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.SCENTING);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.IDLING);
        }
    }

    private static class f extends Behavior<Sniffer> {

        f(int i, int j) {
            super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT), i, j);
        }

        protected boolean checkExtraStartConditions(WorldServer worldserver, Sniffer sniffer) {
            return !sniffer.isBaby() && !sniffer.isInWater();
        }

        protected boolean canStillUse(WorldServer worldserver, Sniffer sniffer, long i) {
            return !sniffer.isPanicking();
        }

        protected void start(WorldServer worldserver, Sniffer sniffer, long i) {
            sniffer.transitionTo(Sniffer.a.SNIFFING);
        }

        protected void stop(WorldServer worldserver, Sniffer sniffer, long i) {
            boolean flag = this.timedOut(i);

            sniffer.transitionTo(Sniffer.a.IDLING);
            if (flag) {
                sniffer.calculateDigPosition().ifPresent((blockposition) -> {
                    sniffer.getBrain().setMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET, (Object) blockposition);
                    sniffer.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition, 1.25F, 0)));
                });
            }

        }
    }
}
