package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsEntity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class ClimbOnTopOfPowderSnowGoal extends PathfinderGoal {

    private final EntityInsentient mob;
    private final World level;

    public ClimbOnTopOfPowderSnowGoal(EntityInsentient entityinsentient, World world) {
        this.mob = entityinsentient;
        this.level = world;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP));
    }

    @Override
    public boolean canUse() {
        boolean flag = this.mob.wasInPowderSnow || this.mob.isInPowderSnow;

        if (flag && this.mob.getType().is(TagsEntity.POWDER_SNOW_WALKABLE_MOBS)) {
            BlockPosition blockposition = this.mob.blockPosition().above();
            IBlockData iblockdata = this.level.getBlockState(blockposition);

            return iblockdata.is(Blocks.POWDER_SNOW) || iblockdata.getCollisionShape(this.level, blockposition) == VoxelShapes.empty();
        } else {
            return false;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.getJumpControl().jump();
    }
}
