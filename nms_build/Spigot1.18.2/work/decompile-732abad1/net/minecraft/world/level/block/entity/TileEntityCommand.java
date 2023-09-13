package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCommand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class TileEntityCommand extends TileEntity {

    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private final CommandBlockListenerAbstract commandBlock = new CommandBlockListenerAbstract() {
        @Override
        public void setCommand(String s) {
            super.setCommand(s);
            TileEntityCommand.this.setChanged();
        }

        @Override
        public WorldServer getLevel() {
            return (WorldServer) TileEntityCommand.this.level;
        }

        @Override
        public void onUpdated() {
            IBlockData iblockdata = TileEntityCommand.this.level.getBlockState(TileEntityCommand.this.worldPosition);

            this.getLevel().sendBlockUpdated(TileEntityCommand.this.worldPosition, iblockdata, iblockdata, 3);
        }

        @Override
        public Vec3D getPosition() {
            return Vec3D.atCenterOf(TileEntityCommand.this.worldPosition);
        }

        @Override
        public CommandListenerWrapper createCommandSourceStack() {
            return new CommandListenerWrapper(this, Vec3D.atCenterOf(TileEntityCommand.this.worldPosition), Vec2F.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity) null);
        }
    };

    public TileEntityCommand(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.COMMAND_BLOCK, blockposition, iblockdata);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        this.commandBlock.save(nbttagcompound);
        nbttagcompound.putBoolean("powered", this.isPowered());
        nbttagcompound.putBoolean("conditionMet", this.wasConditionMet());
        nbttagcompound.putBoolean("auto", this.isAutomatic());
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.commandBlock.load(nbttagcompound);
        this.powered = nbttagcompound.getBoolean("powered");
        this.conditionMet = nbttagcompound.getBoolean("conditionMet");
        this.setAutomatic(nbttagcompound.getBoolean("auto"));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.commandBlock;
    }

    public void setPowered(boolean flag) {
        this.powered = flag;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAutomatic() {
        return this.auto;
    }

    public void setAutomatic(boolean flag) {
        boolean flag1 = this.auto;

        this.auto = flag;
        if (!flag1 && flag && !this.powered && this.level != null && this.getMode() != TileEntityCommand.Type.SEQUENCE) {
            this.scheduleTick();
        }

    }

    public void onModeSwitch() {
        TileEntityCommand.Type tileentitycommand_type = this.getMode();

        if (tileentitycommand_type == TileEntityCommand.Type.AUTO && (this.powered || this.auto) && this.level != null) {
            this.scheduleTick();
        }

    }

    private void scheduleTick() {
        Block block = this.getBlockState().getBlock();

        if (block instanceof BlockCommand) {
            this.markConditionMet();
            this.level.scheduleTick(this.worldPosition, block, 1);
        }

    }

    public boolean wasConditionMet() {
        return this.conditionMet;
    }

    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            BlockPosition blockposition = this.worldPosition.relative(((EnumDirection) this.level.getBlockState(this.worldPosition).getValue(BlockCommand.FACING)).getOpposite());

            if (this.level.getBlockState(blockposition).getBlock() instanceof BlockCommand) {
                TileEntity tileentity = this.level.getBlockEntity(blockposition);

                this.conditionMet = tileentity instanceof TileEntityCommand && ((TileEntityCommand) tileentity).getCommandBlock().getSuccessCount() > 0;
            } else {
                this.conditionMet = false;
            }
        }

        return this.conditionMet;
    }

    public TileEntityCommand.Type getMode() {
        IBlockData iblockdata = this.getBlockState();

        return iblockdata.is(Blocks.COMMAND_BLOCK) ? TileEntityCommand.Type.REDSTONE : (iblockdata.is(Blocks.REPEATING_COMMAND_BLOCK) ? TileEntityCommand.Type.AUTO : (iblockdata.is(Blocks.CHAIN_COMMAND_BLOCK) ? TileEntityCommand.Type.SEQUENCE : TileEntityCommand.Type.REDSTONE));
    }

    public boolean isConditional() {
        IBlockData iblockdata = this.level.getBlockState(this.getBlockPos());

        return iblockdata.getBlock() instanceof BlockCommand ? (Boolean) iblockdata.getValue(BlockCommand.CONDITIONAL) : false;
    }

    public static enum Type {

        SEQUENCE, AUTO, REDSTONE;

        private Type() {}
    }
}
