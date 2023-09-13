package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockGrindstone extends BlockAttachable {

    public static final VoxelShape a = Block.a(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
    public static final VoxelShape b = Block.a(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
    public static final VoxelShape c = Block.a(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
    public static final VoxelShape d = Block.a(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
    public static final VoxelShape e = VoxelShapes.a(BlockGrindstone.a, BlockGrindstone.c);
    public static final VoxelShape f = VoxelShapes.a(BlockGrindstone.b, BlockGrindstone.d);
    public static final VoxelShape g = VoxelShapes.a(BlockGrindstone.e, BlockGrindstone.f);
    public static final VoxelShape h = VoxelShapes.a(BlockGrindstone.g, Block.a(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
    public static final VoxelShape i = Block.a(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
    public static final VoxelShape j = Block.a(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
    public static final VoxelShape k = Block.a(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
    public static final VoxelShape o = Block.a(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
    public static final VoxelShape p = VoxelShapes.a(BlockGrindstone.i, BlockGrindstone.k);
    public static final VoxelShape q = VoxelShapes.a(BlockGrindstone.j, BlockGrindstone.o);
    public static final VoxelShape r = VoxelShapes.a(BlockGrindstone.p, BlockGrindstone.q);
    public static final VoxelShape s = VoxelShapes.a(BlockGrindstone.r, Block.a(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
    public static final VoxelShape t = Block.a(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
    public static final VoxelShape v = Block.a(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
    public static final VoxelShape w = Block.a(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
    public static final VoxelShape x = Block.a(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
    public static final VoxelShape y = VoxelShapes.a(BlockGrindstone.t, BlockGrindstone.w);
    public static final VoxelShape z = VoxelShapes.a(BlockGrindstone.v, BlockGrindstone.x);
    public static final VoxelShape A = VoxelShapes.a(BlockGrindstone.y, BlockGrindstone.z);
    public static final VoxelShape B = VoxelShapes.a(BlockGrindstone.A, Block.a(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
    public static final VoxelShape C = Block.a(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
    public static final VoxelShape D = Block.a(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
    public static final VoxelShape E = Block.a(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
    public static final VoxelShape F = Block.a(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
    public static final VoxelShape G = VoxelShapes.a(BlockGrindstone.C, BlockGrindstone.E);
    public static final VoxelShape H = VoxelShapes.a(BlockGrindstone.D, BlockGrindstone.F);
    public static final VoxelShape I = VoxelShapes.a(BlockGrindstone.G, BlockGrindstone.H);
    public static final VoxelShape J = VoxelShapes.a(BlockGrindstone.I, Block.a(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape K = Block.a(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
    public static final VoxelShape L = Block.a(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
    public static final VoxelShape M = Block.a(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
    public static final VoxelShape N = Block.a(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
    public static final VoxelShape O = VoxelShapes.a(BlockGrindstone.K, BlockGrindstone.M);
    public static final VoxelShape P = VoxelShapes.a(BlockGrindstone.L, BlockGrindstone.N);
    public static final VoxelShape Q = VoxelShapes.a(BlockGrindstone.O, BlockGrindstone.P);
    public static final VoxelShape R = VoxelShapes.a(BlockGrindstone.Q, Block.a(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape S = Block.a(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
    public static final VoxelShape T = Block.a(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
    public static final VoxelShape U = Block.a(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
    public static final VoxelShape V = Block.a(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
    public static final VoxelShape W = VoxelShapes.a(BlockGrindstone.S, BlockGrindstone.U);
    public static final VoxelShape X = VoxelShapes.a(BlockGrindstone.T, BlockGrindstone.V);
    public static final VoxelShape Y = VoxelShapes.a(BlockGrindstone.W, BlockGrindstone.X);
    public static final VoxelShape Z = VoxelShapes.a(BlockGrindstone.Y, Block.a(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
    public static final VoxelShape aa = Block.a(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
    public static final VoxelShape ab = Block.a(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
    public static final VoxelShape ac = Block.a(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
    public static final VoxelShape ad = Block.a(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
    public static final VoxelShape ae = VoxelShapes.a(BlockGrindstone.aa, BlockGrindstone.ac);
    public static final VoxelShape af = VoxelShapes.a(BlockGrindstone.ab, BlockGrindstone.ad);
    public static final VoxelShape ag = VoxelShapes.a(BlockGrindstone.ae, BlockGrindstone.af);
    public static final VoxelShape ah = VoxelShapes.a(BlockGrindstone.ag, Block.a(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
    public static final VoxelShape ai = Block.a(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
    public static final VoxelShape aj = Block.a(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
    public static final VoxelShape ak = Block.a(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
    public static final VoxelShape al = Block.a(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
    public static final VoxelShape am = VoxelShapes.a(BlockGrindstone.ai, BlockGrindstone.ak);
    public static final VoxelShape an = VoxelShapes.a(BlockGrindstone.aj, BlockGrindstone.al);
    public static final VoxelShape ao = VoxelShapes.a(BlockGrindstone.am, BlockGrindstone.an);
    public static final VoxelShape ap = VoxelShapes.a(BlockGrindstone.ao, Block.a(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
    private static final IChatBaseComponent aD = new ChatMessage("container.grindstone_title");

    protected BlockGrindstone(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockGrindstone.FACING, EnumDirection.NORTH)).set(BlockGrindstone.FACE, BlockPropertyAttachPosition.WALL));
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    private VoxelShape l(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockGrindstone.FACING);

        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockGrindstone.FACE)) {
            case FLOOR:
                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    return BlockGrindstone.s;
                }

                return BlockGrindstone.h;
            case WALL:
                if (enumdirection == EnumDirection.NORTH) {
                    return BlockGrindstone.J;
                } else if (enumdirection == EnumDirection.SOUTH) {
                    return BlockGrindstone.B;
                } else {
                    if (enumdirection == EnumDirection.EAST) {
                        return BlockGrindstone.Z;
                    }

                    return BlockGrindstone.R;
                }
            case CEILING:
                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    return BlockGrindstone.ap;
                }

                return BlockGrindstone.ah;
            default:
                return BlockGrindstone.s;
        }
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.l(iblockdata);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.l(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openContainer(iblockdata.b(world, blockposition));
            entityhuman.a(StatisticList.INTERACT_WITH_GRINDSTONE);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerGrindstone(i, playerinventory, ContainerAccess.at(world, blockposition));
        }, BlockGrindstone.aD);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockGrindstone.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockGrindstone.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockGrindstone.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockGrindstone.FACING, BlockGrindstone.FACE);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
