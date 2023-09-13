package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorPlay extends Behavior<EntityCreature> {

    private static final int MAX_FLEE_XZ_DIST = 20;
    private static final int MAX_FLEE_Y_DIST = 8;
    private static final float FLEE_SPEED_MODIFIER = 0.6F;
    private static final float CHASE_SPEED_MODIFIER = 0.6F;
    private static final int MAX_CHASERS_PER_TARGET = 5;
    private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

    public BehaviorPlay() {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return worldserver.getRandom().nextInt(10) == 0 && this.hasFriendsNearby(entitycreature);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        EntityLiving entityliving = this.seeIfSomeoneIsChasingMe(entitycreature);

        if (entityliving != null) {
            this.fleeFromChaser(worldserver, entitycreature, entityliving);
        } else {
            Optional<EntityLiving> optional = this.findSomeoneBeingChased(entitycreature);

            if (optional.isPresent()) {
                chaseKid(entitycreature, (EntityLiving) optional.get());
            } else {
                this.findSomeoneToChase(entitycreature).ifPresent((entityliving1) -> {
                    chaseKid(entitycreature, entityliving1);
                });
            }
        }
    }

    private void fleeFromChaser(WorldServer worldserver, EntityCreature entitycreature, EntityLiving entityliving) {
        for (int i = 0; i < 10; ++i) {
            Vec3D vec3d = LandRandomPos.getPos(entitycreature, 20, 8);

            if (vec3d != null && worldserver.isVillage(new BlockPosition(vec3d))) {
                entitycreature.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(vec3d, 0.6F, 0)));
                return;
            }
        }

    }

    private static void chaseKid(EntityCreature entitycreature, EntityLiving entityliving) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityliving);
        behaviorcontroller.setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorPositionEntity(entityliving, true)));
        behaviorcontroller.setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorPositionEntity(entityliving, false), 0.6F, 1)));
    }

    private Optional<EntityLiving> findSomeoneToChase(EntityCreature entitycreature) {
        return this.getFriendsNearby(entitycreature).stream().findAny();
    }

    private Optional<EntityLiving> findSomeoneBeingChased(EntityCreature entitycreature) {
        Map<EntityLiving, Integer> map = this.checkHowManyChasersEachFriendHas(entitycreature);

        return map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((entry) -> {
            return (Integer) entry.getValue() > 0 && (Integer) entry.getValue() <= 5;
        }).map(Entry::getKey).findFirst();
    }

    private Map<EntityLiving, Integer> checkHowManyChasersEachFriendHas(EntityCreature entitycreature) {
        Map<EntityLiving, Integer> map = Maps.newHashMap();

        this.getFriendsNearby(entitycreature).stream().filter(this::isChasingSomeone).forEach((entityliving) -> {
            map.compute(this.whoAreYouChasing(entityliving), (entityliving1, integer) -> {
                return integer == null ? 1 : integer + 1;
            });
        });
        return map;
    }

    private List<EntityLiving> getFriendsNearby(EntityCreature entitycreature) {
        return (List) entitycreature.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
    }

    private EntityLiving whoAreYouChasing(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    @Nullable
    private EntityLiving seeIfSomeoneIsChasingMe(EntityLiving entityliving) {
        return (EntityLiving) ((List) entityliving.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get()).stream().filter((entityliving1) -> {
            return this.isFriendChasingMe(entityliving, entityliving1);
        }).findAny().orElse((Object) null);
    }

    private boolean isChasingSomeone(EntityLiving entityliving) {
        return entityliving.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private boolean isFriendChasingMe(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((entityliving2) -> {
            return entityliving2 == entityliving;
        }).isPresent();
    }

    private boolean hasFriendsNearby(EntityCreature entitycreature) {
        return entitycreature.getBrain().hasMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }
}
