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
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class TryFindLand extends Behavior<EntityCreature> {

    private static final int COOLDOWN_TICKS = 60;
    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindLand(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.range = i;
        this.speedModifier = f;
    }

    protected void stop(WorldServer worldserver, EntityCreature entitycreature, long i) {
        this.nextOkStartTime = i + 60L;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return entitycreature.level.getFluidState(entitycreature.blockPosition()).is(TagsFluid.WATER);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i >= this.nextOkStartTime) {
            BlockPosition blockposition = entitycreature.blockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.of(entitycreature);
            Iterator iterator = BlockPosition.withinManhattan(blockposition, this.range, this.range, this.range).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (blockposition1.getX() != blockposition.getX() || blockposition1.getZ() != blockposition.getZ()) {
                    IBlockData iblockdata = worldserver.getBlockState(blockposition1);
                    IBlockData iblockdata1 = worldserver.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition1, EnumDirection.DOWN));

                    if (!iblockdata.is(Blocks.WATER) && worldserver.getFluidState(blockposition1).isEmpty() && iblockdata.getCollisionShape(worldserver, blockposition1, voxelshapecollision).isEmpty() && iblockdata1.isFaceSturdy(worldserver, blockposition_mutableblockposition, EnumDirection.UP)) {
                        this.nextOkStartTime = i + 60L;
                        BehaviorUtil.setWalkAndLookTargetMemories(entitycreature, blockposition1.immutable(), this.speedModifier, 1);
                        return;
                    }
                }
            }

        }
    }
}
