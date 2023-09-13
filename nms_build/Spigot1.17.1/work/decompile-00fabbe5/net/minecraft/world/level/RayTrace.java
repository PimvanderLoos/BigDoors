package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
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
        this.collisionContext = VoxelShapeCollision.a(entity);
    }

    public Vec3D a() {
        return this.to;
    }

    public Vec3D b() {
        return this.from;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.block.get(iblockdata, iblockaccess, blockposition, this.collisionContext);
    }

    public VoxelShape a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.fluid.a(fluid) ? fluid.d(iblockaccess, blockposition) : VoxelShapes.a();
    }

    public static enum BlockCollisionOption implements RayTrace.c {

        COLLIDER(BlockBase.BlockData::b), OUTLINE(BlockBase.BlockData::a), VISUAL(BlockBase.BlockData::c);

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
        });

        private final Predicate<Fluid> canPick;

        private FluidCollisionOption(Predicate predicate) {
            this.canPick = predicate;
        }

        public boolean a(Fluid fluid) {
            return this.canPick.test(fluid);
        }
    }

    public interface c {

        VoxelShape get(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision);
    }
}
