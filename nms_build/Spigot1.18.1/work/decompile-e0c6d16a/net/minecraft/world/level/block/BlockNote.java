package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyInstrument;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockNote extends Block {

    public static final BlockStateEnum<BlockPropertyInstrument> INSTRUMENT = BlockProperties.NOTEBLOCK_INSTRUMENT;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateInteger NOTE = BlockProperties.NOTE;

    public BlockNote(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockNote.INSTRUMENT, BlockPropertyInstrument.HARP)).setValue(BlockNote.NOTE, 0)).setValue(BlockNote.POWERED, false));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockNote.INSTRUMENT, BlockPropertyInstrument.byState(blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos().below())));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? (IBlockData) iblockdata.setValue(BlockNote.INSTRUMENT, BlockPropertyInstrument.byState(iblockdata1)) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.hasNeighborSignal(blockposition);

        if (flag1 != (Boolean) iblockdata.getValue(BlockNote.POWERED)) {
            if (flag1) {
                this.playNote(world, blockposition);
            }

            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockNote.POWERED, flag1), 3);
        }

    }

    private void playNote(World world, BlockPosition blockposition) {
        if (world.getBlockState(blockposition.above()).isAir()) {
            world.blockEvent(blockposition, this, 0, 0);
        }

    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            iblockdata = (IBlockData) iblockdata.cycle(BlockNote.NOTE);
            world.setBlock(blockposition, iblockdata, 3);
            this.playNote(world, blockposition);
            entityhuman.awardStat(StatisticList.TUNE_NOTEBLOCK);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.playNote(world, blockposition);
            entityhuman.awardStat(StatisticList.PLAY_NOTEBLOCK);
        }
    }

    @Override
    public boolean triggerEvent(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        int k = (Integer) iblockdata.getValue(BlockNote.NOTE);
        float f = (float) Math.pow(2.0D, (double) (k - 12) / 12.0D);

        world.playSound((EntityHuman) null, blockposition, ((BlockPropertyInstrument) iblockdata.getValue(BlockNote.INSTRUMENT)).getSoundEvent(), SoundCategory.RECORDS, 3.0F, f);
        world.addParticle(Particles.NOTE, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.2D, (double) blockposition.getZ() + 0.5D, (double) k / 24.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockNote.INSTRUMENT, BlockNote.POWERED, BlockNote.NOTE);
    }
}
