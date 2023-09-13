package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class BehaviorOutside extends Behavior<EntityLiving> {

    private final float speedModifier;

    public BehaviorOutside(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<Vec3D> optional = Optional.ofNullable(this.getOutdoorPosition(worldserver, entityliving));

        if (optional.isPresent()) {
            entityliving.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
                return new MemoryTarget(vec3d, this.speedModifier, 0);
            }));
        }

    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return !worldserver.canSeeSky(entityliving.blockPosition());
    }

    @Nullable
    private Vec3D getOutdoorPosition(WorldServer worldserver, EntityLiving entityliving) {
        Random random = entityliving.getRandom();
        BlockPosition blockposition = entityliving.blockPosition();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

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
