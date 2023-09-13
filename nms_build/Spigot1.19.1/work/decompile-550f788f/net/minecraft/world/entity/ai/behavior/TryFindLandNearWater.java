package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class TryFindLandNearWater extends Behavior<EntityCreature> {

    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindLandNearWater(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.range = i;
        this.speedModifier = f;
    }

    protected void stop(WorldServer worldserver, EntityCreature entitycreature, long i) {
        this.nextOkStartTime = i + 40L;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return !entitycreature.level.getFluidState(entitycreature.blockPosition()).is(TagsFluid.WATER);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i >= this.nextOkStartTime) {
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.of(entitycreature);
            BlockPosition blockposition = entitycreature.blockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = BlockPosition.withinManhattan(blockposition, this.range, this.range, this.range).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if ((blockposition1.getX() != blockposition.getX() || blockposition1.getZ() != blockposition.getZ()) && worldserver.getBlockState(blockposition1).getCollisionShape(worldserver, blockposition1, voxelshapecollision).isEmpty() && !worldserver.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition1, EnumDirection.DOWN)).getCollisionShape(worldserver, blockposition1, voxelshapecollision).isEmpty()) {
                    Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator1.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator1.next();

                        blockposition_mutableblockposition.setWithOffset(blockposition1, enumdirection);
                        if (worldserver.getBlockState(blockposition_mutableblockposition).isAir() && worldserver.getBlockState(blockposition_mutableblockposition.move(EnumDirection.DOWN)).is(Blocks.WATER)) {
                            this.nextOkStartTime = i + 40L;
                            BehaviorUtil.setWalkAndLookTargetMemories(entitycreature, blockposition1, this.speedModifier, 0);
                            return;
                        }
                    }
                }
            }

        }
    }
}
