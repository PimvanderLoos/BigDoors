package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class TryLaySpawnOnWaterNearLand {

    public TryLaySpawnOnWaterNearLand() {}

    public static BehaviorControl<EntityLiving> create(Block block) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.present(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(MemoryModuleType.IS_PREGNANT)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, i) -> {
                    if (!entityliving.isInWater() && entityliving.isOnGround()) {
                        BlockPosition blockposition = entityliving.blockPosition().below();
                        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            EnumDirection enumdirection = (EnumDirection) iterator.next();
                            BlockPosition blockposition1 = blockposition.relative(enumdirection);

                            if (worldserver.getBlockState(blockposition1).getCollisionShape(worldserver, blockposition1).getFaceShape(EnumDirection.UP).isEmpty() && worldserver.getFluidState(blockposition1).is((FluidType) FluidTypes.WATER)) {
                                BlockPosition blockposition2 = blockposition1.above();

                                if (worldserver.getBlockState(blockposition2).isAir()) {
                                    worldserver.setBlock(blockposition2, block.defaultBlockState(), 3);
                                    worldserver.playSound((EntityHuman) null, (Entity) entityliving, SoundEffects.FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                    memoryaccessor2.erase();
                                    return true;
                                }
                            }
                        }

                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
