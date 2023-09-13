package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class BehaviorForgetAnger {

    public BehaviorForgetAnger() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ANGRY_AT)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    Optional.ofNullable(worldserver.getEntity((UUID) behaviorbuilder_b.get(memoryaccessor))).map((entity) -> {
                        EntityLiving entityliving1;

                        if (entity instanceof EntityLiving) {
                            EntityLiving entityliving2 = (EntityLiving) entity;

                            entityliving1 = entityliving2;
                        } else {
                            entityliving1 = null;
                        }

                        return entityliving1;
                    }).filter(EntityLiving::isDeadOrDying).filter((entityliving1) -> {
                        return entityliving1.getType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS);
                    }).ifPresent((entityliving1) -> {
                        memoryaccessor.erase();
                    });
                    return true;
                };
            });
        });
    }
}
