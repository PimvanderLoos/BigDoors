package net.minecraft.world.entity.vehicle;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.CommandBlockListenerAbstract;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

    public static final DataWatcherObject<String> DATA_ID_COMMAND_NAME = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.STRING);
    static final DataWatcherObject<IChatBaseComponent> DATA_ID_LAST_OUTPUT = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.COMPONENT);
    private final CommandBlockListenerAbstract commandBlock = new EntityMinecartCommandBlock.a();
    private static final int ACTIVATION_DELAY = 4;
    private int lastActivated;

    public EntityMinecartCommandBlock(EntityTypes<? extends EntityMinecartCommandBlock> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
        super(EntityTypes.COMMAND_BLOCK_MINECART, world, d0, d1, d2);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityMinecartCommandBlock.DATA_ID_COMMAND_NAME, "");
        this.getDataWatcher().register(EntityMinecartCommandBlock.DATA_ID_LAST_OUTPUT, ChatComponentText.EMPTY);
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.commandBlock.b(nbttagcompound);
        this.getDataWatcher().set(EntityMinecartCommandBlock.DATA_ID_COMMAND_NAME, this.getCommandBlock().getCommand());
        this.getDataWatcher().set(EntityMinecartCommandBlock.DATA_ID_LAST_OUTPUT, this.getCommandBlock().k());
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.commandBlock.a(nbttagcompound);
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK;
    }

    @Override
    public IBlockData r() {
        return Blocks.COMMAND_BLOCK.getBlockData();
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.commandBlock;
    }

    @Override
    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.tickCount - this.lastActivated >= 4) {
            this.getCommandBlock().a(this.level);
            this.lastActivated = this.tickCount;
        }

    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        return this.commandBlock.a(entityhuman);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityMinecartCommandBlock.DATA_ID_LAST_OUTPUT.equals(datawatcherobject)) {
            try {
                this.commandBlock.b((IChatBaseComponent) this.getDataWatcher().get(EntityMinecartCommandBlock.DATA_ID_LAST_OUTPUT));
            } catch (Throwable throwable) {
                ;
            }
        } else if (EntityMinecartCommandBlock.DATA_ID_COMMAND_NAME.equals(datawatcherobject)) {
            this.commandBlock.setCommand((String) this.getDataWatcher().get(EntityMinecartCommandBlock.DATA_ID_COMMAND_NAME));
        }

    }

    @Override
    public boolean cy() {
        return true;
    }

    public class a extends CommandBlockListenerAbstract {

        public a() {}

        @Override
        public WorldServer e() {
            return (WorldServer) EntityMinecartCommandBlock.this.level;
        }

        @Override
        public void f() {
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.DATA_ID_COMMAND_NAME, this.getCommand());
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.DATA_ID_LAST_OUTPUT, this.k());
        }

        @Override
        public Vec3D g() {
            return EntityMinecartCommandBlock.this.getPositionVector();
        }

        public EntityMinecartCommandBlock h() {
            return EntityMinecartCommandBlock.this;
        }

        @Override
        public CommandListenerWrapper getWrapper() {
            return new CommandListenerWrapper(this, EntityMinecartCommandBlock.this.getPositionVector(), EntityMinecartCommandBlock.this.br(), this.e(), 2, this.getName().getString(), EntityMinecartCommandBlock.this.getScoreboardDisplayName(), this.e().getMinecraftServer(), EntityMinecartCommandBlock.this);
        }
    }
}
