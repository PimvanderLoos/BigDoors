package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockSprawling extends Block {

    private static final EnumDirection[] i = EnumDirection.values();
    public static final BlockStateBoolean a = BlockProperties.I;
    public static final BlockStateBoolean b = BlockProperties.J;
    public static final BlockStateBoolean c = BlockProperties.K;
    public static final BlockStateBoolean d = BlockProperties.L;
    public static final BlockStateBoolean e = BlockProperties.G;
    public static final BlockStateBoolean f = BlockProperties.H;
    public static final Map<EnumDirection, BlockStateBoolean> g = (Map) SystemUtils.a((Object) Maps.newEnumMap(EnumDirection.class), (enummap) -> {
        enummap.put(EnumDirection.NORTH, BlockSprawling.a);
        enummap.put(EnumDirection.EAST, BlockSprawling.b);
        enummap.put(EnumDirection.SOUTH, BlockSprawling.c);
        enummap.put(EnumDirection.WEST, BlockSprawling.d);
        enummap.put(EnumDirection.UP, BlockSprawling.e);
        enummap.put(EnumDirection.DOWN, BlockSprawling.f);
    });
    protected final VoxelShape[] h;

    protected BlockSprawling(float f, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.h = this.a(f);
    }

    private VoxelShape[] a(float f) {
        float f1 = 0.5F - f;
        float f2 = 0.5F + f;
        VoxelShape voxelshape = Block.a((double) (f1 * 16.0F), (double) (f1 * 16.0F), (double) (f1 * 16.0F), (double) (f2 * 16.0F), (double) (f2 * 16.0F), (double) (f2 * 16.0F));
        VoxelShape[] avoxelshape = new VoxelShape[BlockSprawling.i.length];

        for (int i = 0; i < BlockSprawling.i.length; ++i) {
            EnumDirection enumdirection = BlockSprawling.i[i];

            avoxelshape[i] = VoxelShapes.create(0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentX() * 0.5D), 0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentY() * 0.5D), 0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentZ() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentX() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentY() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentZ() * 0.5D));
        }

        VoxelShape[] avoxelshape1 = new VoxelShape[64];

        for (int j = 0; j < 64; ++j) {
            VoxelShape voxelshape1 = voxelshape;

            for (int k = 0; k < BlockSprawling.i.length; ++k) {
                if ((j & 1 << k) != 0) {
                    voxelshape1 = VoxelShapes.a(voxelshape1, avoxelshape[k]);
                }
            }

            avoxelshape1[j] = voxelshape1;
        }

        return avoxelshape1;
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.h[this.h(iblockdata)];
    }

    protected int h(IBlockData iblockdata) {
        int i = 0;

        for (int j = 0; j < BlockSprawling.i.length; ++j) {
            if ((Boolean) iblockdata.get((IBlockState) BlockSprawling.g.get(BlockSprawling.i[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }
}
