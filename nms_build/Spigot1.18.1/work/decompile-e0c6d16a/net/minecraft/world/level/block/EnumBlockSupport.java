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
        public boolean isSupporting(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return Block.isFaceFull(iblockdata.getBlockSupportShape(iblockaccess, blockposition), enumdirection);
        }
    },
    CENTER {
        private final int CENTER_SUPPORT_WIDTH = 1;
        private final VoxelShape CENTER_SUPPORT_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);

        @Override
        public boolean isSupporting(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return !VoxelShapes.joinIsNotEmpty(iblockdata.getBlockSupportShape(iblockaccess, blockposition).getFaceShape(enumdirection), this.CENTER_SUPPORT_SHAPE, OperatorBoolean.ONLY_SECOND);
        }
    },
    RIGID {
        private final int RIGID_SUPPORT_WIDTH = 2;
        private final VoxelShape RIGID_SUPPORT_SHAPE;

        {
            this.RIGID_SUPPORT_SHAPE = VoxelShapes.join(VoxelShapes.block(), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
        }

        @Override
        public boolean isSupporting(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return !VoxelShapes.joinIsNotEmpty(iblockdata.getBlockSupportShape(iblockaccess, blockposition).getFaceShape(enumdirection), this.RIGID_SUPPORT_SHAPE, OperatorBoolean.ONLY_SECOND);
        }
    };

    EnumBlockSupport() {}

    public abstract boolean isSupporting(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection);
}
