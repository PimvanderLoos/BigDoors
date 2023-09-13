package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockGrowingAbstract extends Block {

    protected final EnumDirection growthDirection;
    protected final boolean scheduleFluidTicks;
    protected final VoxelShape shape;

    protected BlockGrowingAbstract(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag) {
        super(blockbase_info);
        this.growthDirection = enumdirection;
        this.shape = voxelshape;
        this.scheduleFluidTicks = flag;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(this.growthDirection));

        return !iblockdata.a((Block) this.d()) && !iblockdata.a(this.c()) ? this.a((GeneratorAccess) blockactioncontext.getWorld()) : this.c().getBlockData();
    }

    public IBlockData a(GeneratorAccess generatoraccess) {
        return this.getBlockData();
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.shift(this.growthDirection.opposite());
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return !this.h(iblockdata1) ? false : iblockdata1.a((Block) this.d()) || iblockdata1.a(this.c()) || iblockdata1.d(iworldreader, blockposition1, this.growthDirection);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(worldserver, blockposition)) {
            worldserver.b(blockposition, true);
        }

    }

    protected boolean h(IBlockData iblockdata) {
        return true;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.shape;
    }

    protected abstract BlockGrowingTop d();

    protected abstract Block c();
}
