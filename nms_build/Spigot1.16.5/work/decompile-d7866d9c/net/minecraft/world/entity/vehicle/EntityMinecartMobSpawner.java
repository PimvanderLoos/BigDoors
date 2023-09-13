package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.MobSpawnerAbstract;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityMinecartMobSpawner extends EntityMinecartAbstract {

    private final MobSpawnerAbstract b = new MobSpawnerAbstract() {
        @Override
        public void a(int i) {
            EntityMinecartMobSpawner.this.world.broadcastEntityEffect(EntityMinecartMobSpawner.this, (byte) i);
        }

        @Override
        public World a() {
            return EntityMinecartMobSpawner.this.world;
        }

        @Override
        public BlockPosition b() {
            return EntityMinecartMobSpawner.this.getChunkCoordinates();
        }
    };

    public EntityMinecartMobSpawner(EntityTypes<? extends EntityMinecartMobSpawner> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityMinecartMobSpawner(World world, double d0, double d1, double d2) {
        super(EntityTypes.SPAWNER_MINECART, world, d0, d1, d2);
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.SPAWNER;
    }

    @Override
    public IBlockData q() {
        return Blocks.SPAWNER.getBlockData();
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.b.a(nbttagcompound);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.b.b(nbttagcompound);
    }

    @Override
    public void tick() {
        super.tick();
        this.b.c();
    }

    @Override
    public boolean cj() {
        return true;
    }
}
