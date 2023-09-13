package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class TryLaySpawnOnWaterNearLand extends Behavior<Frog> {

    private final Block spawnBlock;
    private final MemoryModuleType<?> memoryModule;

    public TryLaySpawnOnWaterNearLand(Block block, MemoryModuleType<?> memorymoduletype) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT));
        this.spawnBlock = block;
        this.memoryModule = memorymoduletype;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, Frog frog) {
        return !frog.isInWater() && frog.isOnGround();
    }

    protected void start(WorldServer worldserver, Frog frog, long i) {
        BlockPosition blockposition = frog.blockPosition().below();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (worldserver.getBlockState(blockposition1).getCollisionShape(worldserver, blockposition1).getFaceShape(EnumDirection.UP).isEmpty() && worldserver.getFluidState(blockposition1).is((FluidType) FluidTypes.WATER)) {
                BlockPosition blockposition2 = blockposition1.above();

                if (worldserver.getBlockState(blockposition2).isAir()) {
                    worldserver.setBlock(blockposition2, this.spawnBlock.defaultBlockState(), 3);
                    worldserver.playSound((EntityHuman) null, (Entity) frog, SoundEffects.FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    frog.getBrain().eraseMemory(this.memoryModule);
                    return;
                }
            }
        }

    }
}
