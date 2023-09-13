package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public abstract class AbstractCauldronBlock extends Block {

    private static final int SIDE_THICKNESS = 2;
    private static final int LEG_WIDTH = 4;
    private static final int LEG_HEIGHT = 3;
    private static final int LEG_DEPTH = 2;
    protected static final int FLOOR_LEVEL = 4;
    private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.join(VoxelShapes.block(), VoxelShapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), AbstractCauldronBlock.INSIDE), OperatorBoolean.ONLY_FIRST);
    private final Map<Item, CauldronInteraction> interactions;

    public AbstractCauldronBlock(BlockBase.Info blockbase_info, Map<Item, CauldronInteraction> map) {
        super(blockbase_info);
        this.interactions = map;
    }

    protected double getContentHeight(IBlockData iblockdata) {
        return 0.0D;
    }

    protected boolean isEntityInsideContent(IBlockData iblockdata, BlockPosition blockposition, Entity entity) {
        return entity.getY() < (double) blockposition.getY() + this.getContentHeight(iblockdata) && entity.getBoundingBox().maxY > (double) blockposition.getY() + 0.25D;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        CauldronInteraction cauldroninteraction = (CauldronInteraction) this.interactions.get(itemstack.getItem());

        return cauldroninteraction.interact(iblockdata, world, blockposition, entityhuman, enumhand, itemstack);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return AbstractCauldronBlock.SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return AbstractCauldronBlock.INSIDE;
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public abstract boolean isFull(IBlockData iblockdata);

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        BlockPosition blockposition1 = PointedDripstoneBlock.findStalactiteTipAboveCauldron(worldserver, blockposition);

        if (blockposition1 != null) {
            FluidType fluidtype = PointedDripstoneBlock.getCauldronFillFluidType(worldserver, blockposition1);

            if (fluidtype != FluidTypes.EMPTY && this.canReceiveStalactiteDrip(fluidtype)) {
                this.receiveStalactiteDrip(iblockdata, worldserver, blockposition, fluidtype);
            }

        }
    }

    protected boolean canReceiveStalactiteDrip(FluidType fluidtype) {
        return false;
    }

    protected void receiveStalactiteDrip(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {}
}
