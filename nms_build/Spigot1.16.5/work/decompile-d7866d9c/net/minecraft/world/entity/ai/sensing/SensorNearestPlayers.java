package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.EntityHuman;

public class SensorNearestPlayers extends Sensor<EntityLiving> {

    public SensorNearestPlayers() {}

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving) {
        Stream stream = worldserver.getPlayers().stream().filter(IEntitySelector.g).filter((entityplayer) -> {
            return entityliving.a((Entity) entityplayer, 16.0D);
        });

        entityliving.getClass();
        List<EntityHuman> list = (List) stream.sorted(Comparator.comparingDouble(entityliving::h)).collect(Collectors.toList());
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_PLAYERS, (Object) list);
        List<EntityHuman> list1 = (List) list.stream().filter((entityhuman) -> {
            return a(entityliving, (EntityLiving) entityhuman);
        }).collect(Collectors.toList());

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, (Object) (list1.isEmpty() ? null : (EntityHuman) list1.get(0)));
        Optional<EntityHuman> optional = list1.stream().filter(IEntitySelector.f).findFirst();

        behaviorcontroller.setMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, optional);
    }
}
