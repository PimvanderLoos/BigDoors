package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.EntityHuman;

public class SensorNearestPlayers extends Sensor<EntityLiving> {

    public SensorNearestPlayers() {}

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }

    @Override
    protected void doTick(WorldServer worldserver, EntityLiving entityliving) {
        Stream stream = worldserver.players().stream().filter(IEntitySelector.NO_SPECTATORS).filter((entityplayer) -> {
            return entityliving.closerThan(entityplayer, 16.0D);
        });

        Objects.requireNonNull(entityliving);
        List<EntityHuman> list = (List) stream.sorted(Comparator.comparingDouble(entityliving::distanceToSqr)).collect(Collectors.toList());
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_PLAYERS, (Object) list);
        List<EntityHuman> list1 = (List) list.stream().filter((entityhuman) -> {
            return isEntityTargetable(entityliving, entityhuman);
        }).collect(Collectors.toList());

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, (Object) (list1.isEmpty() ? null : (EntityHuman) list1.get(0)));
        Optional<EntityHuman> optional = list1.stream().filter((entityhuman) -> {
            return isEntityAttackable(entityliving, entityhuman);
        }).findFirst();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, optional);
    }
}
