package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3D;

public class BehaviorStrollRandom extends Behavior<EntityCreature> {

    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;
    private final float speedModifier;
    private final int maxXyDist;
    private final int maxYDist;

    public BehaviorStrollRandom(float f) {
        this(f, 10, 7);
    }

    public BehaviorStrollRandom(float f, int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.speedModifier = f;
        this.maxXyDist = i;
        this.maxYDist = j;
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature, long i) {
        BlockPosition blockposition = entitycreature.getChunkCoordinates();

        if (worldserver.b(blockposition)) {
            this.a(entitycreature);
        } else {
            SectionPosition sectionposition = SectionPosition.a(blockposition);
            SectionPosition sectionposition1 = BehaviorUtil.a(worldserver, sectionposition, 2);

            if (sectionposition1 != sectionposition) {
                this.a(entitycreature, sectionposition1);
            } else {
                this.a(entitycreature);
            }
        }

    }

    private void a(EntityCreature entitycreature, SectionPosition sectionposition) {
        Optional<Vec3D> optional = Optional.ofNullable(DefaultRandomPos.a(entitycreature, this.maxXyDist, this.maxYDist, Vec3D.c((BaseBlockPosition) sectionposition.q()), 1.5707963705062866D));

        entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
            return new MemoryTarget(vec3d, this.speedModifier, 0);
        }));
    }

    private void a(EntityCreature entitycreature) {
        Optional<Vec3D> optional = Optional.ofNullable(LandRandomPos.a(entitycreature, this.maxXyDist, this.maxYDist));

        entitycreature.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, optional.map((vec3d) -> {
            return new MemoryTarget(vec3d, this.speedModifier, 0);
        }));
    }
}
