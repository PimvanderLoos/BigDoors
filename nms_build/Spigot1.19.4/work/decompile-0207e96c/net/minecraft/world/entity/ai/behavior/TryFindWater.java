package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindWater {

    public TryFindWater() {}

    public static BehaviorControl<EntityCreature> create(int i, float f) {
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entitycreature, j) -> {
                    if (worldserver.getFluidState(entitycreature.blockPosition()).is(TagsFluid.WATER)) {
                        return false;
                    } else if (j < mutablelong.getValue()) {
                        mutablelong.setValue(j + 20L + 2L);
                        return true;
                    } else {
                        BlockPosition blockposition = null;
                        BlockPosition blockposition1 = null;
                        BlockPosition blockposition2 = entitycreature.blockPosition();
                        Iterable<BlockPosition> iterable = BlockPosition.withinManhattan(blockposition2, i, i, i);
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

                                    if (blockposition1 == null && !blockposition3.closerToCenterThan(entitycreature.position(), 1.5D)) {
                                        blockposition1 = blockposition3.immutable();
                                    }
                                }
                            }
                        }

                        if (blockposition == null) {
                            blockposition = blockposition1;
                        }

                        if (blockposition != null) {
                            memoryaccessor2.set(new BehaviorTarget(blockposition));
                            memoryaccessor1.set(new MemoryTarget(new BehaviorTarget(blockposition), f, 0));
                        }

                        mutablelong.setValue(j + 40L);
                        return true;
                    }
                };
            });
        });
    }
}
