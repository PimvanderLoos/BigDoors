package net.minecraft.server;

import javax.annotation.Nullable;

public class TileEntityMobSpawner extends TileEntity implements ITickable {

    private final MobSpawnerAbstract a = new MobSpawnerAbstract() {
        public void a(int i) {
            TileEntityMobSpawner.this.world.playBlockAction(TileEntityMobSpawner.this.position, Blocks.MOB_SPAWNER, i, 0);
        }

        public World a() {
            return TileEntityMobSpawner.this.world;
        }

        public BlockPosition b() {
            return TileEntityMobSpawner.this.position;
        }

        public void a(MobSpawnerData mobspawnerdata) {
            super.a(mobspawnerdata);
            if (this.a() != null) {
                IBlockData iblockdata = this.a().getType(this.b());

                this.a().notify(TileEntityMobSpawner.this.position, iblockdata, iblockdata, 4);
            }

        }
    };

    public TileEntityMobSpawner() {}

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                if (TileEntity.a(TileEntityMobSpawner.class).equals(new MinecraftKey(nbttagcompound.getString("id")))) {
                    if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
                        NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

                        for (int j = 0; j < nbttaglist.size(); ++j) {
                            NBTTagCompound nbttagcompound1 = nbttaglist.get(j);

                            nbttagcompound1.set("Entity", dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound1.getCompound("Entity"), i));
                        }
                    }

                    nbttagcompound.set("SpawnData", dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound.getCompound("SpawnData"), i));
                }

                return nbttagcompound;
            }
        });
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a.a(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.a.b(nbttagcompound);
        return nbttagcompound;
    }

    public void e() {
        this.a.c();
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 1, this.d());
    }

    public NBTTagCompound d() {
        NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

        nbttagcompound.remove("SpawnPotentials");
        return nbttagcompound;
    }

    public boolean c(int i, int j) {
        return this.a.b(i) ? true : super.c(i, j);
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public MobSpawnerAbstract getSpawner() {
        return this.a;
    }
}
