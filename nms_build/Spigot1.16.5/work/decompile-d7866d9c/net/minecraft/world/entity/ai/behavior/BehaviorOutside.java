package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class BehaviorOutside extends Behavior<EntityLiving> {

    private final float b;

    public BehaviorOutside(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.b = f;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        Optional<Vec3D> optional = Optional.ofNullable(this.b(worldserver, entityliving));

        if (optional.isPresent()) {
            entityliving.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
                return new MemoryTarget(vec3d, this.b, 0);
            }));
        }

    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return !worldserver.e(entityliving.getChunkCoordinates());
    }

    @Nullable
    private Vec3D b(WorldServer worldserver, EntityLiving entityliving) {
        Random random = entityliving.getRandom();
        BlockPosition blockposition = entityliving.getChunkCoordinates();

        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.b(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (a(worldserver, entityliving, blockposition1)) {
                return Vec3D.c((BaseBlockPosition) blockposition1);
            }
        }

        return null;
    }

    public static boolean a(WorldServer worldserver, EntityLiving entityliving, BlockPosition blockposition) {
        return worldserver.e(blockposition) && (double) worldserver.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition).getY() <= entityliving.locY();
    }
}
