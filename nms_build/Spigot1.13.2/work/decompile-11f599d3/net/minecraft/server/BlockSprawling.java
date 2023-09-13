package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;

public class BlockSprawling extends Block {

    private static final EnumDirection[] t = EnumDirection.values();
    public static final BlockStateBoolean a = BlockProperties.D;
    public static final BlockStateBoolean b = BlockProperties.E;
    public static final BlockStateBoolean c = BlockProperties.F;
    public static final BlockStateBoolean o = BlockProperties.G;
    public static final BlockStateBoolean p = BlockProperties.B;
    public static final BlockStateBoolean q = BlockProperties.C;
    public static final Map<EnumDirection, BlockStateBoolean> r = (Map) SystemUtils.a((Object) Maps.newEnumMap(EnumDirection.class), (enummap) -> {
        enummap.put(EnumDirection.NORTH, BlockSprawling.a);
        enummap.put(EnumDirection.EAST, BlockSprawling.b);
        enummap.put(EnumDirection.SOUTH, BlockSprawling.c);
        enummap.put(EnumDirection.WEST, BlockSprawling.o);
        enummap.put(EnumDirection.UP, BlockSprawling.p);
        enummap.put(EnumDirection.DOWN, BlockSprawling.q);
    });
    protected final VoxelShape[] s;

    protected BlockSprawling(float f, Block.Info block_info) {
        super(block_info);
        this.s = this.a(f);
    }

    private VoxelShape[] a(float f) {
        float f1 = 0.5F - f;
        float f2 = 0.5F + f;
        VoxelShape voxelshape = Block.a((double) (f1 * 16.0F), (double) (f1 * 16.0F), (double) (f1 * 16.0F), (double) (f2 * 16.0F), (double) (f2 * 16.0F), (double) (f2 * 16.0F));
        VoxelShape[] avoxelshape = new VoxelShape[BlockSprawling.t.length];

        for (int i = 0; i < BlockSprawling.t.length; ++i) {
            EnumDirection enumdirection = BlockSprawling.t[i];

            avoxelshape[i] = VoxelShapes.create(0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentX() * 0.5D), 0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentY() * 0.5D), 0.5D + Math.min((double) (-f), (double) enumdirection.getAdjacentZ() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentX() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentY() * 0.5D), 0.5D + Math.max((double) f, (double) enumdirection.getAdjacentZ() * 0.5D));
        }

        VoxelShape[] avoxelshape1 = new VoxelShape[64];

        for (int j = 0; j < 64; ++j) {
            VoxelShape voxelshape1 = voxelshape;

            for (int k = 0; k < BlockSprawling.t.length; ++k) {
                if ((j & 1 << k) != 0) {
                    voxelshape1 = VoxelShapes.a(voxelshape1, avoxelshape[k]);
                }
            }

            avoxelshape1[j] = voxelshape1;
        }

        return avoxelshape1;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.s[this.k(iblockdata)];
    }

    protected int k(IBlockData iblockdata) {
        int i = 0;

        for (int j = 0; j < BlockSprawling.t.length; ++j) {
            if ((Boolean) iblockdata.get((IBlockState) BlockSprawling.r.get(BlockSprawling.t[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }
}
