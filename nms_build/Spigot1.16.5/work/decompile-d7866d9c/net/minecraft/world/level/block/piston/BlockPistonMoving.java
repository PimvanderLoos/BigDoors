package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTileEntity;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockPistonMoving extends BlockTileEntity {

    public static final BlockStateDirection a = BlockPistonExtension.FACING;
    public static final BlockStateEnum<BlockPropertyPistonType> b = BlockPistonExtension.TYPE;

    public BlockPistonMoving(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPistonMoving.a, EnumDirection.NORTH)).set(BlockPistonMoving.b, BlockPropertyPistonType.DEFAULT));
    }

    @Nullable
    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return null;
    }

    public static TileEntity a(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        return new TileEntityPiston(iblockdata, enumdirection, flag, flag1);
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).l();
            }

        }
    }

    @Override
    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonMoving.a)).opposite());
        IBlockData iblockdata1 = generatoraccess.getType(blockposition1);

        if (iblockdata1.getBlock() instanceof BlockPiston && (Boolean) iblockdata1.get(BlockPiston.EXTENDED)) {
            generatoraccess.a(blockposition1, false);
        }

    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
            world.a(blockposition, false);
            return EnumInteractionResult.CONSUME;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        TileEntityPiston tileentitypiston = this.a((IBlockAccess) loottableinfo_builder.a(), new BlockPosition((Vec3D) loottableinfo_builder.a(LootContextParameters.ORIGIN)));

        return tileentitypiston == null ? Collections.emptyList() : tileentitypiston.k().a(loottableinfo_builder);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        TileEntityPiston tileentitypiston = this.a(iblockaccess, blockposition);

        return tileentitypiston != null ? tileentitypiston.a(iblockaccess, blockposition) : VoxelShapes.a();
    }

    @Nullable
    private TileEntityPiston a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockPistonMoving.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonMoving.a)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonMoving.a)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPistonMoving.a, BlockPistonMoving.b);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
