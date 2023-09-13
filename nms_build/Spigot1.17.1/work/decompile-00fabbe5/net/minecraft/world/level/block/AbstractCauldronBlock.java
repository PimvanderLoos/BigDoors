package net.minecraft.world.level.block;

import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.WorldServer;
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
    private static final VoxelShape INSIDE = a(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.a(VoxelShapes.b(), VoxelShapes.a(a(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), a(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), a(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), AbstractCauldronBlock.INSIDE), OperatorBoolean.ONLY_FIRST);
    private final Map<Item, CauldronInteraction> interactions;

    public AbstractCauldronBlock(BlockBase.Info blockbase_info, Map<Item, CauldronInteraction> map) {
        super(blockbase_info);
        this.interactions = map;
    }

    protected double a(IBlockData iblockdata) {
        return 0.0D;
    }

    protected boolean a(IBlockData iblockdata, BlockPosition blockposition, Entity entity) {
        return entity.locY() < (double) blockposition.getY() + this.a(iblockdata) && entity.getBoundingBox().maxY > (double) blockposition.getY() + 0.25D;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        ItemStack itemstack = entityhuman.b(enumhand);
        CauldronInteraction cauldroninteraction = (CauldronInteraction) this.interactions.get(itemstack.getItem());

        return cauldroninteraction.interact(iblockdata, world, blockposition, entityhuman, enumhand, itemstack);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return AbstractCauldronBlock.SHAPE;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return AbstractCauldronBlock.INSIDE;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public abstract boolean c(IBlockData iblockdata);

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = PointedDripstoneBlock.a((World) worldserver, blockposition);

        if (blockposition1 != null) {
            FluidType fluidtype = PointedDripstoneBlock.b((World) worldserver, blockposition1);

            if (fluidtype != FluidTypes.EMPTY && this.a(fluidtype)) {
                this.a(iblockdata, (World) worldserver, blockposition, fluidtype);
            }

        }
    }

    protected boolean a(FluidType fluidtype) {
        return false;
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, FluidType fluidtype) {}
}
