package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class BehaviorOutside {

    public BehaviorOutside() {}

    public static OneShot<EntityLiving> create(float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    if (worldserver.canSeeSky(entityliving.blockPosition())) {
                        return false;
                    } else {
                        Optional<Vec3D> optional = Optional.ofNullable(getOutdoorPosition(worldserver, entityliving));

                        optional.ifPresent((vec3d) -> {
                            memoryaccessor.set(new MemoryTarget(vec3d, f, 0));
                        });
                        return true;
                    }
                };
            });
        });
    }

    @Nullable
    private static Vec3D getOutdoorPosition(WorldServer worldserver, EntityLiving entityliving) {
        RandomSource randomsource = entityliving.getRandom();
        BlockPosition blockposition = entityliving.blockPosition();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.offset(randomsource.nextInt(20) - 10, randomsource.nextInt(6) - 3, randomsource.nextInt(20) - 10);

            if (hasNoBlocksAbove(worldserver, entityliving, blockposition1)) {
                return Vec3D.atBottomCenterOf(blockposition1);
            }
        }

        return null;
    }

    public static boolean hasNoBlocksAbove(WorldServer worldserver, EntityLiving entityliving, BlockPosition blockposition) {
        return worldserver.canSeeSky(blockposition) && (double) worldserver.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, blockposition).getY() <= entityliving.getY();
    }
}
