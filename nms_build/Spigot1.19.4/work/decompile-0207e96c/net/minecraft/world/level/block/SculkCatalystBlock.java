package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEventListener;

public class SculkCatalystBlock extends BlockTileEntity {

    public static final int PULSE_TICKS = 8;
    public static final BlockStateBoolean PULSE = BlockProperties.BLOOM;
    private final IntProvider xpRange = ConstantInt.of(5);

    public SculkCatalystBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(SculkCatalystBlock.PULSE, false));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(SculkCatalystBlock.PULSE);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(SculkCatalystBlock.PULSE)) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(SculkCatalystBlock.PULSE, false), 3);
        }

    }

    public static void bloom(WorldServer worldserver, BlockPosition blockposition, IBlockData iblockdata, RandomSource randomsource) {
        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(SculkCatalystBlock.PULSE, true), 3);
        worldserver.scheduleTick(blockposition, iblockdata.getBlock(), 8);
        worldserver.sendParticles(Particles.SCULK_SOUL, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.15D, (double) blockposition.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
        worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 2.0F, 0.6F + randomsource.nextFloat() * 0.4F);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new SculkCatalystBlockEntity(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> GameEventListener getListener(WorldServer worldserver, T t0) {
        if (t0 instanceof SculkCatalystBlockEntity) {
            SculkCatalystBlockEntity sculkcatalystblockentity = (SculkCatalystBlockEntity) t0;

            return sculkcatalystblockentity;
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return world.isClientSide ? null : createTickerHelper(tileentitytypes, TileEntityTypes.SCULK_CATALYST, SculkCatalystBlockEntity::serverTick);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public void spawnAfterBreak(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack, boolean flag) {
        super.spawnAfterBreak(iblockdata, worldserver, blockposition, itemstack, flag);
        if (flag) {
            this.tryDropExperience(worldserver, blockposition, itemstack, this.xpRange);
        }

    }
}
