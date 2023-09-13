package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTurtleEgg extends Block {

    public static final int MAX_HATCH_LEVEL = 2;
    public static final int MIN_EGGS = 1;
    public static final int MAX_EGGS = 4;
    private static final VoxelShape ONE_EGG_AABB = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
    private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
    public static final BlockStateInteger HATCH = BlockProperties.HATCH;
    public static final BlockStateInteger EGGS = BlockProperties.EGGS;

    public BlockTurtleEgg(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockTurtleEgg.HATCH, 0)).setValue(BlockTurtleEgg.EGGS, 1));
    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        if (!entity.isSteppingCarefully()) {
            this.destroyEgg(world, iblockdata, blockposition, entity, 100);
        }

        super.stepOn(world, blockposition, iblockdata, entity);
    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if (!(entity instanceof EntityZombie)) {
            this.destroyEgg(world, iblockdata, blockposition, entity, 3);
        }

        super.fallOn(world, iblockdata, blockposition, entity, f);
    }

    private void destroyEgg(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, int i) {
        if (this.canDestroyEgg(world, entity)) {
            if (!world.isClientSide && world.random.nextInt(i) == 0 && iblockdata.is(Blocks.TURTLE_EGG)) {
                this.decreaseEggs(world, blockposition, iblockdata);
            }

        }
    }

    private void decreaseEggs(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = (Integer) iblockdata.getValue(BlockTurtleEgg.EGGS);

        if (i <= 1) {
            world.destroyBlock(blockposition, false);
        } else {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTurtleEgg.EGGS, i - 1), 2);
            world.gameEvent(GameEvent.BLOCK_DESTROY, blockposition, GameEvent.a.of(iblockdata));
            world.levelEvent(2001, blockposition, Block.getId(iblockdata));
        }

    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (this.shouldUpdateHatchLevel(worldserver) && onSand(worldserver, blockposition)) {
            int i = (Integer) iblockdata.getValue(BlockTurtleEgg.HATCH);

            if (i < 2) {
                worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + randomsource.nextFloat() * 0.2F);
                worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTurtleEgg.HATCH, i + 1), 2);
            } else {
                worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + randomsource.nextFloat() * 0.2F);
                worldserver.removeBlock(blockposition, false);

                for (int j = 0; j < (Integer) iblockdata.getValue(BlockTurtleEgg.EGGS); ++j) {
                    worldserver.levelEvent(2001, blockposition, Block.getId(iblockdata));
                    EntityTurtle entityturtle = (EntityTurtle) EntityTypes.TURTLE.create(worldserver);

                    if (entityturtle != null) {
                        entityturtle.setAge(-24000);
                        entityturtle.setHomePos(blockposition);
                        entityturtle.moveTo((double) blockposition.getX() + 0.3D + (double) j * 0.2D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.3D, 0.0F, 0.0F);
                        worldserver.addFreshEntity(entityturtle);
                    }
                }
            }
        }

    }

    public static boolean onSand(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return isSand(iblockaccess, blockposition.below());
    }

    public static boolean isSand(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getBlockState(blockposition).is(TagsBlock.SAND);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (onSand(world, blockposition) && !world.isClientSide) {
            world.levelEvent(2005, blockposition, 0);
        }

    }

    private boolean shouldUpdateHatchLevel(World world) {
        float f = world.getTimeOfDay(1.0F);

        return (double) f < 0.69D && (double) f > 0.65D ? true : world.random.nextInt(500) == 0;
    }

    @Override
    public void playerDestroy(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.playerDestroy(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        this.decreaseEggs(world, blockposition, iblockdata);
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return !blockactioncontext.isSecondaryUseActive() && blockactioncontext.getItemInHand().is(this.asItem()) && (Integer) iblockdata.getValue(BlockTurtleEgg.EGGS) < 4 ? true : super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());

        return iblockdata.is((Block) this) ? (IBlockData) iblockdata.setValue(BlockTurtleEgg.EGGS, Math.min(4, (Integer) iblockdata.getValue(BlockTurtleEgg.EGGS) + 1)) : super.getStateForPlacement(blockactioncontext);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Integer) iblockdata.getValue(BlockTurtleEgg.EGGS) > 1 ? BlockTurtleEgg.MULTIPLE_EGGS_AABB : BlockTurtleEgg.ONE_EGG_AABB;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTurtleEgg.HATCH, BlockTurtleEgg.EGGS);
    }

    private boolean canDestroyEgg(World world, Entity entity) {
        return !(entity instanceof EntityTurtle) && !(entity instanceof EntityBat) ? (!(entity instanceof EntityLiving) ? false : entity instanceof EntityHuman || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) : false;
    }
}
