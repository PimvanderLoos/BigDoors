package net.minecraft.server;

public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

    public static final DataWatcherObject<String> COMMAND = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.d);
    private static final DataWatcherObject<IChatBaseComponent> b = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.e);
    private final CommandBlockListenerAbstract c = new CommandBlockListenerAbstract() {
        public void i() {
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.b, this.l());
        }

        public BlockPosition getChunkCoordinates() {
            return new BlockPosition(EntityMinecartCommandBlock.this.locX, EntityMinecartCommandBlock.this.locY + 0.5D, EntityMinecartCommandBlock.this.locZ);
        }

        public Vec3D d() {
            return new Vec3D(EntityMinecartCommandBlock.this.locX, EntityMinecartCommandBlock.this.locY, EntityMinecartCommandBlock.this.locZ);
        }

        public World getWorld() {
            return EntityMinecartCommandBlock.this.world;
        }

        public Entity f() {
            return EntityMinecartCommandBlock.this;
        }

        public MinecraftServer C_() {
            return EntityMinecartCommandBlock.this.world.getMinecraftServer();
        }
    };
    private int d;

    public EntityMinecartCommandBlock(World world) {
        super(world);
    }

    public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityMinecartAbstract.a(dataconvertermanager, EntityMinecartCommandBlock.class);
        dataconvertermanager.a(DataConverterTypes.ENTITY, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                if (TileEntity.a(TileEntityCommand.class).equals(new MinecraftKey(nbttagcompound.getString("id")))) {
                    nbttagcompound.setString("id", "Control");
                    dataconverter.a(DataConverterTypes.BLOCK_ENTITY, nbttagcompound, i);
                    nbttagcompound.setString("id", "MinecartCommandBlock");
                }

                return nbttagcompound;
            }
        });
    }

    protected void i() {
        super.i();
        this.getDataWatcher().register(EntityMinecartCommandBlock.COMMAND, "");
        this.getDataWatcher().register(EntityMinecartCommandBlock.b, new ChatComponentText(""));
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.c.b(nbttagcompound);
        this.getDataWatcher().set(EntityMinecartCommandBlock.COMMAND, this.getCommandBlock().getCommand());
        this.getDataWatcher().set(EntityMinecartCommandBlock.b, this.getCommandBlock().l());
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        this.c.a(nbttagcompound);
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK;
    }

    public IBlockData x() {
        return Blocks.COMMAND_BLOCK.getBlockData();
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.c;
    }

    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.ticksLived - this.d >= 4) {
            this.getCommandBlock().a(this.world);
            this.d = this.ticksLived;
        }

    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        this.c.a(entityhuman);
        return false;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityMinecartCommandBlock.b.equals(datawatcherobject)) {
            try {
                this.c.b((IChatBaseComponent) this.getDataWatcher().get(EntityMinecartCommandBlock.b));
            } catch (Throwable throwable) {
                ;
            }
        } else if (EntityMinecartCommandBlock.COMMAND.equals(datawatcherobject)) {
            this.c.setCommand((String) this.getDataWatcher().get(EntityMinecartCommandBlock.COMMAND));
        }

    }

    public boolean bC() {
        return true;
    }
}
