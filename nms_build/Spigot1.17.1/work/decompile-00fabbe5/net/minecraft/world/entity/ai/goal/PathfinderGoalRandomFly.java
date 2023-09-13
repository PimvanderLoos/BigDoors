package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRandomFly extends PathfinderGoalRandomStrollLand {

    public PathfinderGoalRandomFly(EntityCreature entitycreature, double d0) {
        super(entitycreature, d0);
    }

    @Nullable
    @Override
    protected Vec3D g() {
        Vec3D vec3d = null;

        if (this.mob.isInWater()) {
            vec3d = LandRandomPos.a(this.mob, 15, 15);
        }

        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.j();
        }

        return vec3d == null ? super.g() : vec3d;
    }

    @Nullable
    private Vec3D j() {
        BlockPosition blockposition = this.mob.getChunkCoordinates();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();
        Iterable<BlockPosition> iterable = BlockPosition.b(MathHelper.floor(this.mob.locX() - 3.0D), MathHelper.floor(this.mob.locY() - 6.0D), MathHelper.floor(this.mob.locZ() - 3.0D), MathHelper.floor(this.mob.locX() + 3.0D), MathHelper.floor(this.mob.locY() + 6.0D), MathHelper.floor(this.mob.locZ() + 3.0D));
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (!blockposition.equals(blockposition1)) {
                IBlockData iblockdata = this.mob.level.getType(blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition1, EnumDirection.DOWN));
                boolean flag = iblockdata.getBlock() instanceof BlockLeaves || iblockdata.a((Tag) TagsBlock.LOGS);

                if (flag && this.mob.level.isEmpty(blockposition1) && this.mob.level.isEmpty(blockposition_mutableblockposition.a((BaseBlockPosition) blockposition1, EnumDirection.UP))) {
                    return Vec3D.c((BaseBlockPosition) blockposition1);
                }
            }
        }

        return null;
    }
}
