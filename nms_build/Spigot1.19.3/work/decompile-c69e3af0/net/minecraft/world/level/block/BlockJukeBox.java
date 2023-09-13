package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityJukeBox;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockJukeBox extends BlockTileEntity {

    public static final BlockStateBoolean HAS_RECORD = BlockProperties.HAS_RECORD;

    protected BlockJukeBox(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockJukeBox.HAS_RECORD, false));
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        super.setPlacedBy(world, blockposition, iblockdata, entityliving, itemstack);
        NBTTagCompound nbttagcompound = ItemBlock.getBlockEntityData(itemstack);

        if (nbttagcompound != null && nbttagcompound.contains("RecordItem")) {
            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockJukeBox.HAS_RECORD, true), 2);
        }

    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.getValue(BlockJukeBox.HAS_RECORD)) {
            this.dropRecording(world, blockposition);
            iblockdata = (IBlockData) iblockdata.setValue(BlockJukeBox.HAS_RECORD, false);
            world.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, blockposition, GameEvent.a.of(iblockdata));
            world.setBlock(blockposition, iblockdata, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entityhuman, iblockdata));
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public void setRecord(@Nullable Entity entity, GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        TileEntity tileentity = generatoraccess.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityJukeBox) {
            TileEntityJukeBox tileentityjukebox = (TileEntityJukeBox) tileentity;

            tileentityjukebox.setRecord(itemstack.copy());
            tileentityjukebox.playRecord();
            generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockJukeBox.HAS_RECORD, true), 2);
            generatoraccess.gameEvent(GameEvent.BLOCK_CHANGE, blockposition, GameEvent.a.of(entity, iblockdata));
        }

    }

    public void dropRecording(World world, BlockPosition blockposition) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityJukeBox) {
                TileEntityJukeBox tileentityjukebox = (TileEntityJukeBox) tileentity;
                ItemStack itemstack = tileentityjukebox.getRecord();

                if (!itemstack.isEmpty()) {
                    world.levelEvent(1010, blockposition, 0);
                    tileentityjukebox.clearContent();
                    float f = 0.7F;
                    double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemStack itemstack1 = itemstack.copy();
                    EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack1);

                    entityitem.setDefaultPickUpDelay();
                    world.addFreshEntity(entityitem);
                }
            }
        }
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            this.dropRecording(world, blockposition);
            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityJukeBox(blockposition, iblockdata);
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityJukeBox) {
            Item item = ((TileEntityJukeBox) tileentity).getRecord().getItem();

            if (item instanceof ItemRecord) {
                return ((ItemRecord) item).getAnalogOutput();
            }
        }

        return 0;
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockJukeBox.HAS_RECORD);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return (Boolean) iblockdata.getValue(BlockJukeBox.HAS_RECORD) ? createTickerHelper(tileentitytypes, TileEntityTypes.JUKEBOX, TileEntityJukeBox::playRecordTick) : null;
    }
}
