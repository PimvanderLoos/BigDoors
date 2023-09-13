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
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockNote extends Block {

    public static final BlockStateEnum<BlockPropertyInstrument> INSTRUMENT = BlockProperties.aI;
    public static final BlockStateBoolean POWERED = BlockProperties.w;
    public static final BlockStateInteger NOTE = BlockProperties.ax;

    public BlockNote(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNote.INSTRUMENT, BlockPropertyInstrument.HARP)).set(BlockNote.NOTE, 0)).set(BlockNote.POWERED, false));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().down())));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? (IBlockData) iblockdata.set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(iblockdata1)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.isBlockIndirectlyPowered(blockposition);

        if (flag1 != (Boolean) iblockdata.get(BlockNote.POWERED)) {
            if (flag1) {
                this.play(world, blockposition);
            }

            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockNote.POWERED, flag1), 3);
        }

    }

    private void play(World world, BlockPosition blockposition) {
        if (world.getType(blockposition.up()).isAir()) {
            world.playBlockAction(blockposition, this, 0, 0);
        }

    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockNote.NOTE);
            world.setTypeAndData(blockposition, iblockdata, 3);
            this.play(world, blockposition);
            entityhuman.a(StatisticList.TUNE_NOTEBLOCK);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.play(world, blockposition);
            entityhuman.a(StatisticList.PLAY_NOTEBLOCK);
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        int k = (Integer) iblockdata.get(BlockNote.NOTE);
        float f = (float) Math.pow(2.0D, (double) (k - 12) / 12.0D);

        world.playSound((EntityHuman) null, blockposition, ((BlockPropertyInstrument) iblockdata.get(BlockNote.INSTRUMENT)).b(), SoundCategory.RECORDS, 3.0F, f);
        world.addParticle(Particles.NOTE, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.2D, (double) blockposition.getZ() + 0.5D, (double) k / 24.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNote.INSTRUMENT, BlockNote.POWERED, BlockNote.NOTE);
    }
}
