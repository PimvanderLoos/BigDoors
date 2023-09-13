package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
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
    private boolean sendToClient;
    private final CommandBlockListenerAbstract commandBlock = new CommandBlockListenerAbstract() {
        @Override
        public void setCommand(String s) {
            super.setCommand(s);
            TileEntityCommand.this.update();
        }

        @Override
        public WorldServer e() {
            return (WorldServer) TileEntityCommand.this.level;
        }

        @Override
        public void f() {
            IBlockData iblockdata = TileEntityCommand.this.level.getType(TileEntityCommand.this.worldPosition);

            this.e().notify(TileEntityCommand.this.worldPosition, iblockdata, iblockdata, 3);
        }

        @Override
        public Vec3D g() {
            return Vec3D.a((BaseBlockPosition) TileEntityCommand.this.worldPosition);
        }

        @Override
        public CommandListenerWrapper getWrapper() {
            return new CommandListenerWrapper(this, Vec3D.a((BaseBlockPosition) TileEntityCommand.this.worldPosition), Vec2F.ZERO, this.e(), 2, this.getName().getString(), this.getName(), this.e().getMinecraftServer(), (Entity) null);
        }
    };

    public TileEntityCommand(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.COMMAND_BLOCK, blockposition, iblockdata);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.commandBlock.a(nbttagcompound);
        nbttagcompound.setBoolean("powered", this.f());
        nbttagcompound.setBoolean("conditionMet", this.i());
        nbttagcompound.setBoolean("auto", this.g());
        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.commandBlock.b(nbttagcompound);
        this.powered = nbttagcompound.getBoolean("powered");
        this.conditionMet = nbttagcompound.getBoolean("conditionMet");
        this.b(nbttagcompound.getBoolean("auto"));
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        if (this.s()) {
            this.c(false);
            NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

            return new PacketPlayOutTileEntityData(this.worldPosition, 2, nbttagcompound);
        } else {
            return null;
        }
    }

    @Override
    public boolean isFilteredNBT() {
        return true;
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.commandBlock;
    }

    public void a(boolean flag) {
        this.powered = flag;
    }

    public boolean f() {
        return this.powered;
    }

    public boolean g() {
        return this.auto;
    }

    public void b(boolean flag) {
        boolean flag1 = this.auto;

        this.auto = flag;
        if (!flag1 && flag && !this.powered && this.level != null && this.t() != TileEntityCommand.Type.SEQUENCE) {
            this.v();
        }

    }

    public void h() {
        TileEntityCommand.Type tileentitycommand_type = this.t();

        if (tileentitycommand_type == TileEntityCommand.Type.AUTO && (this.powered || this.auto) && this.level != null) {
            this.v();
        }

    }

    private void v() {
        Block block = this.getBlock().getBlock();

        if (block instanceof BlockCommand) {
            this.j();
            this.level.getBlockTickList().a(this.worldPosition, block, 1);
        }

    }

    public boolean i() {
        return this.conditionMet;
    }

    public boolean j() {
        this.conditionMet = true;
        if (this.u()) {
            BlockPosition blockposition = this.worldPosition.shift(((EnumDirection) this.level.getType(this.worldPosition).get(BlockCommand.FACING)).opposite());

            if (this.level.getType(blockposition).getBlock() instanceof BlockCommand) {
                TileEntity tileentity = this.level.getTileEntity(blockposition);

                this.conditionMet = tileentity instanceof TileEntityCommand && ((TileEntityCommand) tileentity).getCommandBlock().j() > 0;
            } else {
                this.conditionMet = false;
            }
        }

        return this.conditionMet;
    }

    public boolean s() {
        return this.sendToClient;
    }

    public void c(boolean flag) {
        this.sendToClient = flag;
    }

    public TileEntityCommand.Type t() {
        IBlockData iblockdata = this.getBlock();

        return iblockdata.a(Blocks.COMMAND_BLOCK) ? TileEntityCommand.Type.REDSTONE : (iblockdata.a(Blocks.REPEATING_COMMAND_BLOCK) ? TileEntityCommand.Type.AUTO : (iblockdata.a(Blocks.CHAIN_COMMAND_BLOCK) ? TileEntityCommand.Type.SEQUENCE : TileEntityCommand.Type.REDSTONE));
    }

    public boolean u() {
        IBlockData iblockdata = this.level.getType(this.getPosition());

        return iblockdata.getBlock() instanceof BlockCommand ? (Boolean) iblockdata.get(BlockCommand.CONDITIONAL) : false;
    }

    public static enum Type {

        SEQUENCE, AUTO, REDSTONE;

        private Type() {}
    }
}
