package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorStrollInside extends Behavior<EntityCreature> {

    private final float speedModifier;

    public BehaviorStrollInside(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        return !worldserver.g(entitycreature.getChunkCoordinates());
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BlockPosition blockposition = entitycreature.getChunkCoordinates();
        List<BlockPosition> list = (List) BlockPosition.b(blockposition.c(-1, -1, -1), blockposition.c(1, 1, 1)).map(BlockPosition::immutableCopy).collect(Collectors.toList());

        Collections.shuffle(list);
        Optional<BlockPosition> optional = list.stream().filter((blockposition1) -> {
            return !worldserver.g(blockposition1);
        }).filter((blockposition1) -> {
            return worldserver.a(blockposition1, (Entity) entitycreature);
        }).filter((blockposition1) -> {
            return worldserver.getCubes(entitycreature);
        }).findFirst();

        optional.ifPresent((blockposition1) -> {
            entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(blockposition1, this.speedModifier, 0)));
        });
    }
}
