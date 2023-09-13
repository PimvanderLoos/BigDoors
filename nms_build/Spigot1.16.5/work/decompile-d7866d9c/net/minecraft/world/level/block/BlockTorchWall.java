package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParam;
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

    public static final BlockStateDirection a = BlockFacingHorizontal.FACING;
    private static final Map<EnumDirection, VoxelShape> b = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.a(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), EnumDirection.SOUTH, Block.a(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), EnumDirection.WEST, Block.a(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), EnumDirection.EAST, Block.a(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

    protected BlockTorchWall(BlockBase.Info blockbase_info, ParticleParam particleparam) {
        super(blockbase_info, particleparam);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockTorchWall.a, EnumDirection.NORTH));
    }

    @Override
    public String i() {
        return this.getItem().getName();
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return h(iblockdata);
    }

    public static VoxelShape h(IBlockData iblockdata) {
        return (VoxelShape) BlockTorchWall.b.get(iblockdata.get(BlockTorchWall.a));
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTorchWall.a);
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
        EnumDirection[] aenumdirection = blockactioncontext.e();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.n().d()) {
                EnumDirection enumdirection1 = enumdirection.opposite();

                iblockdata = (IBlockData) iblockdata.set(BlockTorchWall.a, enumdirection1);
                if (iblockdata.canPlace(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.opposite() == iblockdata.get(BlockTorchWall.a) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : iblockdata;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockTorchWall.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockTorchWall.a)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockTorchWall.a)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTorchWall.a);
    }
}
