package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;

public enum EnumBlockSupport {

    FULL {
        @Override
        public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return Block.a(iblockdata.l(iblockaccess, blockposition), enumdirection);
        }
    },
    CENTER {
        private final int d = 1;
        private final VoxelShape e = Block.a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);

        @Override
        public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return !VoxelShapes.c(iblockdata.l(iblockaccess, blockposition).a(enumdirection), this.e, OperatorBoolean.ONLY_SECOND);
        }
    },
    RIGID {
        private final int d = 2;
        private final VoxelShape e;

        {
            this.e = VoxelShapes.a(VoxelShapes.b(), Block.a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
        }

        @Override
        public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return !VoxelShapes.c(iblockdata.l(iblockaccess, blockposition).a(enumdirection), this.e, OperatorBoolean.ONLY_SECOND);
        }
    };

    private EnumBlockSupport() {}

    public abstract boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);
}
