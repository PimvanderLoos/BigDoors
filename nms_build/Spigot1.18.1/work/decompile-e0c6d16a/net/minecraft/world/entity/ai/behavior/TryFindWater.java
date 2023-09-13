package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class TryFindWater extends Behavior<EntityCreature> {

    private final int range;
    private final float speedModifier;
    private long nextOkStartTime;

    public TryFindWater(int i, float f) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.range = i;
        this.speedModifier = f;
    }

    protected void stop(WorldServer worldserver, EntityCreature entitycreature, long i) {
        this.nextOkStartTime = i + 20L + 2L;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        return !entitycreature.level.getFluidState(entitycreature.blockPosition()).is((Tag) TagsFluid.WATER);
    }

    protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
        if (i >= this.nextOkStartTime) {
            BlockPosition blockposition = null;
            BlockPosition blockposition1 = null;
            BlockPosition blockposition2 = entitycreature.blockPosition();
            Iterable<BlockPosition> iterable = BlockPosition.withinManhattan(blockposition2, this.range, this.range, this.range);
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition3 = (BlockPosition) iterator.next();

                if (blockposition3.getX() != blockposition2.getX() || blockposition3.getZ() != blockposition2.getZ()) {
                    IBlockData iblockdata = entitycreature.level.getBlockState(blockposition3.above());
                    IBlockData iblockdata1 = entitycreature.level.getBlockState(blockposition3);

                    if (iblockdata1.is(Blocks.WATER)) {
                        if (iblockdata.isAir()) {
                            blockposition = blockposition3.immutable();
                            break;
                        }

                        if (blockposition1 == null && !blockposition3.closerThan((IPosition) entitycreature.position(), 1.5D)) {
                            blockposition1 = blockposition3.immutable();
                        }
                    }
                }
            }

            if (blockposition == null) {
                blockposition = blockposition1;
            }

            if (blockposition != null) {
                this.nextOkStartTime = i + 40L;
                BehaviorUtil.setWalkAndLookTargetMemories(entitycreature, blockposition, this.speedModifier, 0);
            }

        }
    }
}
