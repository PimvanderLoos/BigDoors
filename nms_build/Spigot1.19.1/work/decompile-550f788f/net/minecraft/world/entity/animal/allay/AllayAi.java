package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorFindAdmirableItem;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorPositionEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorSwim;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FlyingRandomStroll;
import net.minecraft.world.entity.ai.behavior.GoAndGiveItemsToTarget;
import net.minecraft.world.entity.ai.behavior.StayCloseToTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;

public class AllayAi {

    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_DEPOSIT_TARGET = 2.25F;
    private static final float SPEED_MULTIPLIER_WHEN_RETRIEVING_ITEM = 1.75F;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.5F;
    private static final int CLOSE_ENOUGH_TO_TARGET = 4;
    private static final int TOO_FAR_FROM_TARGET = 16;
    private static final int MAX_LOOK_DISTANCE = 6;
    private static final int MIN_WAIT_DURATION = 30;
    private static final int MAX_WAIT_DURATION = 60;
    private static final int TIME_TO_FORGET_NOTEBLOCK = 600;
    private static final int DISTANCE_TO_WANTED_ITEM = 32;

    public AllayAi() {}

    protected static BehaviorController<?> makeBrain(BehaviorController<Allay> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Allay> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new BehaviorSwim(0.8F), new AnimalPanic(2.5F), new BehaviorLook(45, 90), new BehavorMove(), new CountDownCooldownTicks(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(BehaviorController<Allay> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorFindAdmirableItem<>((allay) -> {
            return true;
        }, 1.75F, true, 32)), Pair.of(1, new GoAndGiveItemsToTarget<>(AllayAi::getItemDepositPosition, 2.25F)), Pair.of(2, new StayCloseToTarget<>(AllayAi::getItemDepositPosition, 4, 16, 2.25F)), Pair.of(3, new BehaviorRunSometimes<>(new BehaviorLookTarget((entityliving) -> {
            return true;
        }, 6.0F), UniformInt.of(30, 60))), Pair.of(4, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new FlyingRandomStroll(1.0F), 2), Pair.of(new BehaviorLookWalk(1.0F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1))))), ImmutableSet.of());
    }

    public static void updateActivity(Allay allay) {
        allay.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static void hearNoteblock(EntityLiving entityliving, BlockPosition blockposition) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        GlobalPos globalpos = GlobalPos.of(entityliving.getLevel().dimension(), blockposition);
        Optional<GlobalPos> optional = behaviorcontroller.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);

        if (optional.isEmpty()) {
            behaviorcontroller.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION, (Object) globalpos);
            behaviorcontroller.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, (int) 600);
        } else if (((GlobalPos) optional.get()).equals(globalpos)) {
            behaviorcontroller.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, (int) 600);
        }

    }

    private static Optional<BehaviorPosition> getItemDepositPosition(EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        Optional<GlobalPos> optional = behaviorcontroller.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);

        if (optional.isPresent()) {
            GlobalPos globalpos = (GlobalPos) optional.get();

            if (shouldDepositItemsAtLikedNoteblock(entityliving, behaviorcontroller, globalpos)) {
                return Optional.of(new BehaviorTarget(globalpos.pos().above()));
            }

            behaviorcontroller.eraseMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        }

        return getLikedPlayerPositionTracker(entityliving);
    }

    private static boolean shouldDepositItemsAtLikedNoteblock(EntityLiving entityliving, BehaviorController<?> behaviorcontroller, GlobalPos globalpos) {
        Optional<Integer> optional = behaviorcontroller.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        World world = entityliving.getLevel();

        return world.dimension() == globalpos.dimension() && world.getBlockState(globalpos.pos()).is(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<BehaviorPosition> getLikedPlayerPositionTracker(EntityLiving entityliving) {
        return getLikedPlayer(entityliving).map((entityplayer) -> {
            return new BehaviorPositionEntity(entityplayer, true);
        });
    }

    public static Optional<EntityPlayer> getLikedPlayer(EntityLiving entityliving) {
        World world = entityliving.getLevel();

        if (!world.isClientSide() && world instanceof WorldServer) {
            WorldServer worldserver = (WorldServer) world;
            Optional<UUID> optional = entityliving.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);

            if (optional.isPresent()) {
                Entity entity = worldserver.getEntity((UUID) optional.get());

                if (entity instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity;

                    if ((entityplayer.gameMode.isSurvival() || entityplayer.gameMode.isCreative()) && entityplayer.closerThan(entityliving, 64.0D)) {
                        return Optional.of(entityplayer);
                    }
                }

                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
