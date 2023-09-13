package net.minecraft.server;

public class EntityMinecartMobSpawner extends EntityMinecartAbstract {

    private final MobSpawnerAbstract a = new MobSpawnerAbstract() {
        public void a(int i) {
            EntityMinecartMobSpawner.this.world.broadcastEntityEffect(EntityMinecartMobSpawner.this, (byte) i);
        }

        public World a() {
            return EntityMinecartMobSpawner.this.world;
        }

        public BlockPosition b() {
            return new BlockPosition(EntityMinecartMobSpawner.this);
        }
    };

    public EntityMinecartMobSpawner(World world) {
        super(world);
    }

    public EntityMinecartMobSpawner(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        a(dataconvertermanager, EntityMinecartMobSpawner.class);
        dataconvertermanager.a(DataConverterTypes.ENTITY, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                String s = nbttagcompound.getString("id");

                if (EntityTypes.getName(EntityMinecartMobSpawner.class).equals(new MinecraftKey(s))) {
                    nbttagcompound.setString("id", TileEntity.a(TileEntityMobSpawner.class).toString());
                    dataconverter.a(DataConverterTypes.BLOCK_ENTITY, nbttagcompound, i);
                    nbttagcompound.setString("id", s);
                }

                return nbttagcompound;
            }
        });
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.SPAWNER;
    }

    public IBlockData x() {
        return Blocks.MOB_SPAWNER.getBlockData();
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a.a(nbttagcompound);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        this.a.b(nbttagcompound);
    }

    public void B_() {
        super.B_();
        this.a.c();
    }
}
