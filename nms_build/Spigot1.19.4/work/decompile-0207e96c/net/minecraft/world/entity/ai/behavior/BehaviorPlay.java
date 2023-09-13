package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorPlay {

    private static final int MAX_FLEE_XZ_DIST = 20;
    private static final int MAX_FLEE_Y_DIST = 8;
    private static final float FLEE_SPEED_MODIFIER = 0.6F;
    private static final float CHASE_SPEED_MODIFIER = 0.6F;
    private static final int MAX_CHASERS_PER_TARGET = 5;
    private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

    public BehaviorPlay() {}

    public static BehaviorControl<EntityCreature> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.VISIBLE_VILLAGER_BABIES), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.INTERACTION_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entitycreature, i) -> {
                    if (worldserver.getRandom().nextInt(10) != 0) {
                        return false;
                    } else {
                        List<EntityLiving> list = (List) behaviorbuilder_b.get(memoryaccessor);
                        Optional<EntityLiving> optional = list.stream().filter((entityliving) -> {
                            return isFriendChasingMe(entitycreature, entityliving);
                        }).findAny();

                        if (!optional.isPresent()) {
                            Optional<EntityLiving> optional1 = findSomeoneBeingChased(list);

                            if (optional1.isPresent()) {
                                chaseKid(memoryaccessor3, memoryaccessor2, memoryaccessor1, (EntityLiving) optional1.get());
                                return true;
                            } else {
                                list.stream().findAny().ifPresent((entityliving) -> {
                                    chaseKid(memoryaccessor3, memoryaccessor2, memoryaccessor1, entityliving);
                                });
                                return true;
                            }
                        } else {
                            for (int j = 0; j < 10; ++j) {
                                Vec3D vec3d = LandRandomPos.getPos(entitycreature, 20, 8);

                                if (vec3d != null && worldserver.isVillage(BlockPosition.containing(vec3d))) {
                                    memoryaccessor1.set(new MemoryTarget(vec3d, 0.6F, 0));
                                    break;
                                }
                            }

                            return true;
                        }
                    }
                };
            });
        });
    }

    private static void chaseKid(MemoryAccessor<?, EntityLiving> memoryaccessor, MemoryAccessor<?, BehaviorPosition> memoryaccessor1, MemoryAccessor<?, MemoryTarget> memoryaccessor2, EntityLiving entityliving) {
        memoryaccessor.set(entityliving);
        memoryaccessor1.set(new BehaviorPositionEntity(entityliving, true));
        memoryaccessor2.set(new MemoryTarget(new BehaviorPositionEntity(entityliving, false), 0.6F, 1));
    }

    private static Optional<EntityLiving> findSomeoneBeingChased(List<EntityLiving> list) {
        Map<EntityLiving, Integer> map = checkHowManyChasersEachFriendHas(list);

        return map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((entry) -> {
            return (Integer) entry.getValue() > 0 && (Integer) entry.getValue() <= 5;
        }).map(Entry::getKey).findFirst();
    }

    private static Map<EntityLiving, Integer> checkHowManyChasersEachFriendHas(List<EntityLiving> list) {
        Map<EntityLiving, Integer> map = Maps.newHashMap();

        list.stream().filter(BehaviorPlay::isChasingSomeone).forEach((entityliving) -> {
            map.compute(whoAreYouChasing(entityliving), (entityliving1, integer) -> {
                return integer == null ? 1 : integer + 1;
            });
        });
        return map;
    }

    private static EntityLiving whoAreYouChasing(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    private static boolean isChasingSomeone(EntityLiving entityliving) {
        return entityliving.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private static boolean isFriendChasingMe(EntityLiving entityliving, EntityLiving entityliving1) {
        return entityliving1.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((entityliving2) -> {
            return entityliving2 == entityliving;
        }).isPresent();
    }
}
