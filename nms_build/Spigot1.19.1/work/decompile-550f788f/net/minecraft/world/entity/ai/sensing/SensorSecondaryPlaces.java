package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.World;

public class SensorSecondaryPlaces extends Sensor<EntityVillager> {

    private static final int SCAN_RATE = 40;

    public SensorSecondaryPlaces() {
        super(40);
    }

    protected void doTick(WorldServer worldserver, EntityVillager entityvillager) {
        ResourceKey<World> resourcekey = worldserver.dimension();
        BlockPosition blockposition = entityvillager.blockPosition();
        List<GlobalPos> list = Lists.newArrayList();
        boolean flag = true;

        for (int i = -4; i <= 4; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -4; k <= 4; ++k) {
                    BlockPosition blockposition1 = blockposition.offset(i, j, k);

                    if (entityvillager.getVillagerData().getProfession().secondaryPoi().contains(worldserver.getBlockState(blockposition1).getBlock())) {
                        list.add(GlobalPos.of(resourcekey, blockposition1));
                    }
                }
            }
        }

        BehaviorController<?> behaviorcontroller = entityvillager.getBrain();

        if (!list.isEmpty()) {
            behaviorcontroller.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, (Object) list);
        } else {
            behaviorcontroller.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        }

    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}
