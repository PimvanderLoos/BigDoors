package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTorchWall extends BlockTorch {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    protected static final float AABB_OFFSET = 2.5F;
    private static final Map<EnumDirection, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.a(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), EnumDirection.SOUTH, Block.a(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), EnumDirection.WEST, Block.a(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), EnumDirection.EAST, Block.a(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

    protected BlockTorchWall(BlockBase.Info blockbase_info, ParticleParam particleparam) {
        super(blockbase_info, particleparam);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockTorchWall.FACING, EnumDirection.NORTH));
    }

    @Override
    public String h() {
        return this.getItem().getName();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return h(iblockdata);
    }

    public static VoxelShape h(IBlockData iblockdata) {
        return (VoxelShape) BlockTorchWall.AABBS.get(iblockdata.get(BlockTorchWall.FACING));
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTorchWall.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return iblockdata1.d(iworldreader, blockposition1, enumdirection);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection[] aenumdirection = blockactioncontext.f();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.n().d()) {
                EnumDirection enumdirection1 = enumdirection.opposite();

                iblockdata = (IBlockData) iblockdata.set(BlockTorchWall.FACING, enumdirection1);
                if (iblockdata.canPlace(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.opposite() == iblockdata.get(BlockTorchWall.FACING) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : iblockdata;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTorchWall.FACING);
        double d0 = (double) blockposition.getX() + 0.5D;
        double d1 = (double) blockposition.getY() + 0.7D;
        double d2 = (double) blockposition.getZ() + 0.5D;
        double d3 = 0.22D;
        double d4 = 0.27D;
        EnumDirection enumdirection1 = enumdirection.opposite();

        world.addParticle(Particles.SMOKE, d0 + 0.27D * (double) enumdirection1.getAdjacentX(), d1 + 0.22D, d2 + 0.27D * (double) enumdirection1.getAdjacentZ(), 0.0D, 0.0D, 0.0D);
        world.addParticle(this.flameParticle, d0 + 0.27D * (double) enumdirection1.getAdjacentX(), d1 + 0.22D, d2 + 0.27D * (double) enumdirection1.getAdjacentZ(), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockTorchWall.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockTorchWall.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockTorchWall.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTorchWall.FACING);
    }
}
