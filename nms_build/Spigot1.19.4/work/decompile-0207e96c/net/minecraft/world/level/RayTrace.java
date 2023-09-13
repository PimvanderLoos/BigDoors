package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class RayTrace {

    private final Vec3D from;
    private final Vec3D to;
    private final RayTrace.BlockCollisionOption block;
    private final RayTrace.FluidCollisionOption fluid;
    private final VoxelShapeCollision collisionContext;

    public RayTrace(Vec3D vec3d, Vec3D vec3d1, RayTrace.BlockCollisionOption raytrace_blockcollisionoption, RayTrace.FluidCollisionOption raytrace_fluidcollisionoption, Entity entity) {
        this.from = vec3d;
        this.to = vec3d1;
        this.block = raytrace_blockcollisionoption;
        this.fluid = raytrace_fluidcollisionoption;
        this.collisionContext = VoxelShapeCollision.of(entity);
    }

    public Vec3D getTo() {
        return this.to;
    }

    public Vec3D getFrom() {
        return this.from;
    }

    public VoxelShape getBlockShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.block.get(iblockdata, iblockaccess, blockposition, this.collisionContext);
    }

    public VoxelShape getFluidShape(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.fluid.canPick(fluid) ? fluid.getShape(iblockaccess, blockposition) : VoxelShapes.empty();
    }

    public static enum BlockCollisionOption implements RayTrace.c {

        COLLIDER(BlockBase.BlockData::getCollisionShape), OUTLINE(BlockBase.BlockData::getShape), VISUAL(BlockBase.BlockData::getVisualShape), FALLDAMAGE_RESETTING((iblockdata, iblockaccess, blockposition, voxelshapecollision) -> {
            return iblockdata.is(TagsBlock.FALL_DAMAGE_RESETTING) ? VoxelShapes.block() : VoxelShapes.empty();
        });

        private final RayTrace.c shapeGetter;

        private BlockCollisionOption(RayTrace.c raytrace_c) {
            this.shapeGetter = raytrace_c;
        }

        @Override
        public VoxelShape get(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
            return this.shapeGetter.get(iblockdata, iblockaccess, blockposition, voxelshapecollision);
        }
    }

    public static enum FluidCollisionOption {

        NONE((fluid) -> {
            return false;
        }), SOURCE_ONLY(Fluid::isSource), ANY((fluid) -> {
            return !fluid.isEmpty();
        }), WATER((fluid) -> {
            return fluid.is(TagsFluid.WATER);
        });

        private final Predicate<Fluid> canPick;

        private FluidCollisionOption(Predicate predicate) {
            this.canPick = predicate;
        }

        public boolean canPick(Fluid fluid) {
            return this.canPick.test(fluid);
        }
    }

    public interface c {

        VoxelShape get(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision);
    }
}
