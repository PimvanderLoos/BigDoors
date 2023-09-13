package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindLandNearWater {

    public TryFindLandNearWater() {}

    public static BehaviorControl<EntityCreature> create(int i, float f) {
        MutableLong mutablelong = new MutableLong(0L);

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entitycreature, j) -> {
                    if (worldserver.getFluidState(entitycreature.blockPosition()).is(TagsFluid.WATER)) {
                        return false;
                    } else if (j < mutablelong.getValue()) {
                        mutablelong.setValue(j + 40L);
                        return true;
                    } else {
                        VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.of(entitycreature);
                        BlockPosition blockposition = entitycreature.blockPosition();
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                        Iterator iterator = BlockPosition.withinManhattan(blockposition, i, i, i).iterator();

                        label45:
                        while (iterator.hasNext()) {
                            BlockPosition blockposition1 = (BlockPosition) iterator.next();

                            if ((blockposition1.getX() != blockposition.getX() || blockposition1.getZ() != blockposition.getZ()) && worldserver.getBlockState(blockposition1).getCollisionShape(worldserver, blockposition1, voxelshapecollision).isEmpty() && !worldserver.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition1, EnumDirection.DOWN)).getCollisionShape(worldserver, blockposition1, voxelshapecollision).isEmpty()) {
                                Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator1.hasNext()) {
                                    EnumDirection enumdirection = (EnumDirection) iterator1.next();

                                    blockposition_mutableblockposition.setWithOffset(blockposition1, enumdirection);
                                    if (worldserver.getBlockState(blockposition_mutableblockposition).isAir() && worldserver.getBlockState(blockposition_mutableblockposition.move(EnumDirection.DOWN)).is(Blocks.WATER)) {
                                        memoryaccessor2.set(new BehaviorTarget(blockposition1));
                                        memoryaccessor1.set(new MemoryTarget(new BehaviorTarget(blockposition1), f, 0));
                                        break label45;
                                    }
                                }
                            }
                        }

                        mutablelong.setValue(j + 40L);
                        return true;
                    }
                };
            });
        });
    }
}
