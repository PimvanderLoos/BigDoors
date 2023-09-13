package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.UtilColor;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPositionTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerAbstract {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int EVENT_SPAWN = 1;
    private static WeightedRandomList<MobSpawnerData> EMPTY_POTENTIALS = WeightedRandomList.b();
    public int spawnDelay = 20;
    public WeightedRandomList<MobSpawnerData> spawnPotentials;
    public MobSpawnerData nextSpawnData;
    private double spin;
    private double oSpin;
    public int minSpawnDelay;
    public int maxSpawnDelay;
    public int spawnCount;
    @Nullable
    private Entity displayEntity;
    public int maxNearbyEntities;
    public int requiredPlayerRange;
    public int spawnRange;
    private final Random random;

    public MobSpawnerAbstract() {
        this.spawnPotentials = MobSpawnerAbstract.EMPTY_POTENTIALS;
        this.nextSpawnData = new MobSpawnerData();
        this.minSpawnDelay = 200;
        this.maxSpawnDelay = 800;
        this.spawnCount = 4;
        this.maxNearbyEntities = 6;
        this.requiredPlayerRange = 16;
        this.spawnRange = 4;
        this.random = new Random();
    }

    @Nullable
    public MinecraftKey getMobName(@Nullable World world, BlockPosition blockposition) {
        String s = this.nextSpawnData.getEntity().getString("id");

        try {
            return UtilColor.b(s) ? null : new MinecraftKey(s);
        } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
            MobSpawnerAbstract.LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", s, world != null ? world.getDimensionKey().a() : "<null>", blockposition.getX(), blockposition.getY(), blockposition.getZ());
            return null;
        }
    }

    public void setMobName(EntityTypes<?> entitytypes) {
        this.nextSpawnData.getEntity().setString("id", IRegistry.ENTITY_TYPE.getKey(entitytypes).toString());
    }

    private boolean c(World world, BlockPosition blockposition) {
        return world.isPlayerNearby((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, (double) this.requiredPlayerRange);
    }

    public void a(World world, BlockPosition blockposition) {
        if (!this.c(world, blockposition)) {
            this.oSpin = this.spin;
        } else {
            double d0 = (double) blockposition.getX() + world.random.nextDouble();
            double d1 = (double) blockposition.getY() + world.random.nextDouble();
            double d2 = (double) blockposition.getZ() + world.random.nextDouble();

            world.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            world.addParticle(Particles.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
        }

    }

    public void a(WorldServer worldserver, BlockPosition blockposition) {
        if (this.c(worldserver, blockposition)) {
            if (this.spawnDelay == -1) {
                this.d(worldserver, blockposition);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            } else {
                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    NBTTagCompound nbttagcompound = this.nextSpawnData.getEntity();
                    Optional<EntityTypes<?>> optional = EntityTypes.a(nbttagcompound);

                    if (!optional.isPresent()) {
                        this.d(worldserver, blockposition);
                        return;
                    }

                    NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
                    int j = nbttaglist.size();
                    double d0 = j >= 1 ? nbttaglist.h(0) : (double) blockposition.getX() + (worldserver.random.nextDouble() - worldserver.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double d1 = j >= 2 ? nbttaglist.h(1) : (double) (blockposition.getY() + worldserver.random.nextInt(3) - 1);
                    double d2 = j >= 3 ? nbttaglist.h(2) : (double) blockposition.getZ() + (worldserver.random.nextDouble() - worldserver.random.nextDouble()) * (double) this.spawnRange + 0.5D;

                    if (worldserver.b(((EntityTypes) optional.get()).a(d0, d1, d2)) && EntityPositionTypes.a((EntityTypes) optional.get(), worldserver, EnumMobSpawn.SPAWNER, new BlockPosition(d0, d1, d2), worldserver.getRandom())) {
                        Entity entity = EntityTypes.a(nbttagcompound, worldserver, (entity1) -> {
                            entity1.setPositionRotation(d0, d1, d2, entity1.getYRot(), entity1.getXRot());
                            return entity1;
                        });

                        if (entity == null) {
                            this.d(worldserver, blockposition);
                            return;
                        }

                        int k = worldserver.a(entity.getClass(), (new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1))).g((double) this.spawnRange)).size();

                        if (k >= this.maxNearbyEntities) {
                            this.d(worldserver, blockposition);
                            return;
                        }

                        entity.setPositionRotation(entity.locX(), entity.locY(), entity.locZ(), worldserver.random.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof EntityInsentient) {
                            EntityInsentient entityinsentient = (EntityInsentient) entity;

                            if (!entityinsentient.a((GeneratorAccess) worldserver, EnumMobSpawn.SPAWNER) || !entityinsentient.a((IWorldReader) worldserver)) {
                                continue;
                            }

                            if (this.nextSpawnData.getEntity().e() == 1 && this.nextSpawnData.getEntity().hasKeyOfType("id", 8)) {
                                ((EntityInsentient) entity).prepare(worldserver, worldserver.getDamageScaler(entity.getChunkCoordinates()), EnumMobSpawn.SPAWNER, (GroupDataEntity) null, (NBTTagCompound) null);
                            }
                        }

                        if (!worldserver.addAllEntitiesSafely(entity)) {
                            this.d(worldserver, blockposition);
                            return;
                        }

                        worldserver.triggerEffect(2004, blockposition, 0);
                        if (entity instanceof EntityInsentient) {
                            ((EntityInsentient) entity).doSpawnEffect();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.d(worldserver, blockposition);
                }

            }
        }
    }

    private void d(World world, BlockPosition blockposition) {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.b(this.random).ifPresent((mobspawnerdata) -> {
            this.setSpawnData(world, blockposition, mobspawnerdata);
        });
        this.a(world, blockposition, 1);
    }

    public void a(@Nullable World world, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        this.spawnDelay = nbttagcompound.getShort("Delay");
        List<MobSpawnerData> list = Lists.newArrayList();

        if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                list.add(new MobSpawnerData(nbttaglist.getCompound(i)));
            }
        }

        this.spawnPotentials = WeightedRandomList.a((List) list);
        if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
            this.setSpawnData(world, blockposition, new MobSpawnerData(1, nbttagcompound.getCompound("SpawnData")));
        } else if (!list.isEmpty()) {
            this.spawnPotentials.b(this.random).ifPresent((mobspawnerdata) -> {
                this.setSpawnData(world, blockposition, mobspawnerdata);
            });
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

        this.displayEntity = null;
    }

    public NBTTagCompound b(@Nullable World world, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = this.getMobName(world, blockposition);

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
            nbttagcompound.set("SpawnData", this.nextSpawnData.getEntity().clone());
            NBTTagList nbttaglist = new NBTTagList();

            if (this.spawnPotentials.c()) {
                nbttaglist.add(this.nextSpawnData.b());
            } else {
                Iterator iterator = this.spawnPotentials.d().iterator();

                while (iterator.hasNext()) {
                    MobSpawnerData mobspawnerdata = (MobSpawnerData) iterator.next();

                    nbttaglist.add(mobspawnerdata.b());
                }
            }

            nbttagcompound.set("SpawnPotentials", nbttaglist);
            return nbttagcompound;
        }
    }

    @Nullable
    public Entity a(World world) {
        if (this.displayEntity == null) {
            this.displayEntity = EntityTypes.a(this.nextSpawnData.getEntity(), world, Function.identity());
            if (this.nextSpawnData.getEntity().e() == 1 && this.nextSpawnData.getEntity().hasKeyOfType("id", 8) && this.displayEntity instanceof EntityInsentient) {
                ;
            }
        }

        return this.displayEntity;
    }

    public boolean a(World world, int i) {
        if (i == 1) {
            if (world.isClientSide) {
                this.spawnDelay = this.minSpawnDelay;
            }

            return true;
        } else {
            return false;
        }
    }

    public void setSpawnData(@Nullable World world, BlockPosition blockposition, MobSpawnerData mobspawnerdata) {
        this.nextSpawnData = mobspawnerdata;
    }

    public abstract void a(World world, BlockPosition blockposition, int i);

    public double a() {
        return this.spin;
    }

    public double b() {
        return this.oSpin;
    }
}
