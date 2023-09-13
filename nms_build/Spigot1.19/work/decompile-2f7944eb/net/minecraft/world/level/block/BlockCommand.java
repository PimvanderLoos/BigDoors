package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.UtilColor;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.slf4j.Logger;

public class BlockCommand extends BlockTileEntity implements GameMasterBlock {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockStateDirection FACING = BlockDirectional.FACING;
    public static final BlockStateBoolean CONDITIONAL = BlockProperties.CONDITIONAL;
    private final boolean automatic;

    public BlockCommand(BlockBase.Info blockbase_info, boolean flag) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockCommand.FACING, EnumDirection.NORTH)).setValue(BlockCommand.CONDITIONAL, false));
        this.automatic = flag;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        TileEntityCommand tileentitycommand = new TileEntityCommand(blockposition, iblockdata);

        tileentitycommand.setAutomatic(this.automatic);
        return tileentitycommand;
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
                boolean flag1 = world.hasNeighborSignal(blockposition);
                boolean flag2 = tileentitycommand.isPowered();

                tileentitycommand.setPowered(flag1);
                if (!flag2 && !tileentitycommand.isAutomatic() && tileentitycommand.getMode() != TileEntityCommand.Type.SEQUENCE) {
                    if (flag1) {
                        tileentitycommand.markConditionMet();
                        world.scheduleTick(blockposition, (Block) this, 1);
                    }

                }
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        TileEntity tileentity = worldserver.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
            CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();
            boolean flag = !UtilColor.isNullOrEmpty(commandblocklistenerabstract.getCommand());
            TileEntityCommand.Type tileentitycommand_type = tileentitycommand.getMode();
            boolean flag1 = tileentitycommand.wasConditionMet();

            if (tileentitycommand_type == TileEntityCommand.Type.AUTO) {
                tileentitycommand.markConditionMet();
                if (flag1) {
                    this.execute(iblockdata, worldserver, blockposition, commandblocklistenerabstract, flag);
                } else if (tileentitycommand.isConditional()) {
                    commandblocklistenerabstract.setSuccessCount(0);
                }

                if (tileentitycommand.isPowered() || tileentitycommand.isAutomatic()) {
                    worldserver.scheduleTick(blockposition, (Block) this, 1);
                }
            } else if (tileentitycommand_type == TileEntityCommand.Type.REDSTONE) {
                if (flag1) {
                    this.execute(iblockdata, worldserver, blockposition, commandblocklistenerabstract, flag);
                } else if (tileentitycommand.isConditional()) {
                    commandblocklistenerabstract.setSuccessCount(0);
                }
            }

            worldserver.updateNeighbourForOutputSignal(blockposition, this);
        }

    }

    private void execute(IBlockData iblockdata, World world, BlockPosition blockposition, CommandBlockListenerAbstract commandblocklistenerabstract, boolean flag) {
        if (flag) {
            commandblocklistenerabstract.performCommand(world);
        } else {
            commandblocklistenerabstract.setSuccessCount(0);
        }

        executeChain(world, blockposition, (EnumDirection) iblockdata.getValue(BlockCommand.FACING));
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityCommand && entityhuman.canUseGameMasterBlocks()) {
            entityhuman.openCommandBlock((TileEntityCommand) tileentity);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityCommand ? ((TileEntityCommand) tileentity).getCommandBlock().getSuccessCount() : 0;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
            CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

            if (itemstack.hasCustomHoverName()) {
                commandblocklistenerabstract.setName(itemstack.getHoverName());
            }

            if (!world.isClientSide) {
                if (ItemBlock.getBlockEntityData(itemstack) == null) {
                    commandblocklistenerabstract.setTrackOutput(world.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
                    tileentitycommand.setAutomatic(this.automatic);
                }

                if (tileentitycommand.getMode() == TileEntityCommand.Type.SEQUENCE) {
                    boolean flag = world.hasNeighborSignal(blockposition);

                    tileentitycommand.setPowered(flag);
                }
            }

        }
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockCommand.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockCommand.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockCommand.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockCommand.FACING, BlockCommand.CONDITIONAL);
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) this.defaultBlockState().setValue(BlockCommand.FACING, blockactioncontext.getNearestLookingDirection().getOpposite());
    }

    private static void executeChain(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        GameRules gamerules = world.getGameRules();

        IBlockData iblockdata;
        int i;

        for (i = gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH); i-- > 0; enumdirection = (EnumDirection) iblockdata.getValue(BlockCommand.FACING)) {
            blockposition_mutableblockposition.move(enumdirection);
            iblockdata = world.getBlockState(blockposition_mutableblockposition);
            Block block = iblockdata.getBlock();

            if (!iblockdata.is(Blocks.CHAIN_COMMAND_BLOCK)) {
                break;
            }

            TileEntity tileentity = world.getBlockEntity(blockposition_mutableblockposition);

            if (!(tileentity instanceof TileEntityCommand)) {
                break;
            }

            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;

            if (tileentitycommand.getMode() != TileEntityCommand.Type.SEQUENCE) {
                break;
            }

            if (tileentitycommand.isPowered() || tileentitycommand.isAutomatic()) {
                CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

                if (tileentitycommand.markConditionMet()) {
                    if (!commandblocklistenerabstract.performCommand(world)) {
                        break;
                    }

                    world.updateNeighbourForOutputSignal(blockposition_mutableblockposition, block);
                } else if (tileentitycommand.isConditional()) {
                    commandblocklistenerabstract.setSuccessCount(0);
                }
            }
        }

        if (i <= 0) {
            int j = Math.max(gamerules.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);

            BlockCommand.LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
        }

    }
}
