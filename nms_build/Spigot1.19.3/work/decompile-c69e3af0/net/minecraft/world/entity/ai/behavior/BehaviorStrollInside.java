package net.minecraft.world.entity.ai.behavior;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorStrollInside {

    public BehaviorStrollInside() {}

    public static BehaviorControl<EntityCreature> create(float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entitycreature, i) -> {
                    if (worldserver.canSeeSky(entitycreature.blockPosition())) {
                        return false;
                    } else {
                        BlockPosition blockposition = entitycreature.blockPosition();
                        List<BlockPosition> list = (List) BlockPosition.betweenClosedStream(blockposition.offset(-1, -1, -1), blockposition.offset(1, 1, 1)).map(BlockPosition::immutable).collect(Collectors.toList());

                        Collections.shuffle(list);
                        list.stream().filter((blockposition1) -> {
                            return !worldserver.canSeeSky(blockposition1);
                        }).filter((blockposition1) -> {
                            return worldserver.loadedAndEntityCanStandOn(blockposition1, entitycreature);
                        }).filter((blockposition1) -> {
                            return worldserver.noCollision((Entity) entitycreature);
                        }).findFirst().ifPresent((blockposition1) -> {
                            memoryaccessor.set(new MemoryTarget(blockposition1, f, 0));
                        });
                        return true;
                    }
                };
            });
        });
    }
}
