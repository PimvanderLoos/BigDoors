package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public abstract class MobSpawnerAbstract {

    public int spawnDelay = 20;
    private final List<MobSpawnerData> mobs = Lists.newArrayList();
    private MobSpawnerData spawnData = new MobSpawnerData();
    private double d;
    private double e;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private Entity i;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    public MobSpawnerAbstract() {}

    @Nullable
    public MinecraftKey getMobName() {
        String s = this.spawnData.b().getString("id");

        return UtilColor.b(s) ? null : new MinecraftKey(s);
    }

    public void setMobName(@Nullable MinecraftKey minecraftkey) {
        if (minecraftkey != null) {
            this.spawnData.b().setString("id", minecraftkey.toString());
        }

    }

    private boolean h() {
        BlockPosition blockposition = this.b();

        return this.a().isPlayerNearby((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, (double) this.requiredPlayerRange);
    }

    public void c() {
        if (!this.h()) {
            this.e = this.d;
        } else {
            BlockPosition blockposition = this.b();

            if (this.a().isClientSide) {
                double d0 = (double) ((float) blockposition.getX() + this.a().random.nextFloat());
                double d1 = (double) ((float) blockposition.getY() + this.a().random.nextFloat());
                double d2 = (double) ((float) blockposition.getZ() + this.a().random.nextFloat());

                this.a().addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
                this.a().addParticle(EnumParticle.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                }

                this.e = this.d;
                this.d = (this.d + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
            } else {
                if (this.spawnDelay == -1) {
                    this.i();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    NBTTagCompound nbttagcompound = this.spawnData.b();
                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
                    World world = this.a();
                    int j = nbttaglist.size();
                    double d3 = j >= 1 ? nbttaglist.f(0) : (double) blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double d4 = j >= 2 ? nbttaglist.f(1) : (double) (blockposition.getY() + world.random.nextInt(3) - 1);
                    double d5 = j >= 3 ? nbttaglist.f(2) : (double) blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    Entity entity = ChunkRegionLoader.a(nbttagcompound, world, d3, d4, d5, false);

                    if (entity == null) {
                        return;
                    }

                    int k = world.a(entity.getClass(), (new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1))).g((double) this.spawnRange)).size();

                    if (k >= this.maxNearbyEntities) {
                        this.i();
                        return;
                    }

                    EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;

                    entity.setPositionRotation(entity.locX, entity.locY, entity.locZ, world.random.nextFloat() * 360.0F, 0.0F);
                    if (entityinsentient == null || entityinsentient.P() && entityinsentient.canSpawn()) {
                        if (this.spawnData.b().d() == 1 && this.spawnData.b().hasKeyOfType("id", 8) && entity instanceof EntityInsentient) {
                            ((EntityInsentient) entity).prepare(world.D(new BlockPosition(entity)), (GroupDataEntity) null);
                        }

                        ChunkRegionLoader.a(entity, world);
                        world.triggerEffect(2004, blockposition, 0);
                        if (entityinsentient != null) {
                            entityinsentient.doSpawnEffect();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.i();
                }
            }

        }
    }

    private void i() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;

            this.spawnDelay = this.minSpawnDelay + this.a().random.nextInt(i);
        }

        if (!this.mobs.isEmpty()) {
            this.a((MobSpawnerData) WeightedRandom.a(this.a().random, this.mobs));
        }

        this.a(1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.spawnDelay = nbttagcompound.getShort("Delay");
        this.mobs.clear();
        if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.mobs.add(new MobSpawnerData(nbttaglist.get(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
            this.a(new MobSpawnerData(1, nbttagcompound.getCompound("SpawnData")));
        } else if (!this.mobs.isEmpty()) {
            this.a((MobSpawnerData) WeightedRandom.a(this.a().random, this.mobs));
        }

        if (nbttagcompound.hasKeyOfType("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbttagcompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbttagcompound.getShort("MaxSpawnDelay");
            this.spawnCount = nbttagcompound.getShort("SpawnCount");
        }

        if (nbttagcompound.hasKeyOfType("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbttagcompound.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbttagcompound.getShort("RequiredPlayerRange");
        }

        if (nbttagcompound.hasKeyOfType("SpawnRange", 99)) {
            this.spawnRange = nbttagcompound.getShort("SpawnRange");
        }

        if (this.a() != null) {
            this.i = null;
        }

    }

    public NBTTagCompound b(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = this.getMobName();

        if (minecraftkey == null) {
            return nbttagcompound;
        } else {
            nbttagcompound.setShort("Delay", (short) this.spawnDelay);
            nbttagcompound.setShort("MinSpawnDelay", (short) this.minSpawnDelay);
            nbttagcompound.setShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
            nbttagcompound.setShort("SpawnCount", (short) this.spawnCount);
            nbttagcompound.setShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
            nbttagcompound.setShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
            nbttagcompound.setShort("SpawnRange", (short) this.spawnRange);
            nbttagcompound.set("SpawnData", this.spawnData.b().g());
            NBTTagList nbttaglist = new NBTTagList();

            if (this.mobs.isEmpty()) {
                nbttaglist.add(this.spawnData.a());
            } else {
                Iterator iterator = this.mobs.iterator();

                while (iterator.hasNext()) {
                    MobSpawnerData mobspawnerdata = (MobSpawnerData) iterator.next();

                    nbttaglist.add(mobspawnerdata.a());
                }
            }

            nbttagcompound.set("SpawnPotentials", nbttaglist);
            return nbttagcompound;
        }
    }

    public boolean b(int i) {
        if (i == 1 && this.a().isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    public void a(MobSpawnerData mobspawnerdata) {
        this.spawnData = mobspawnerdata;
    }

    public abstract void a(int i);

    public abstract World a();

    public abstract BlockPosition b();
}
