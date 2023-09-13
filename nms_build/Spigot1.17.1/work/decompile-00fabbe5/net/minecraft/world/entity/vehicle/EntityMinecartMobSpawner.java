package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.MobSpawnerAbstract;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class EntityMinecartMobSpawner extends EntityMinecartAbstract {

    private final MobSpawnerAbstract spawner = new MobSpawnerAbstract() {
        @Override
        public void a(World world, BlockPosition blockposition, int i) {
            world.broadcastEntityEffect(EntityMinecartMobSpawner.this, (byte) i);
        }
    };
    private final Runnable ticker;

    public EntityMinecartMobSpawner(EntityTypes<? extends EntityMinecartMobSpawner> entitytypes, World world) {
        super(entitytypes, world);
        this.ticker = this.a(world);
    }

    public EntityMinecartMobSpawner(World world, double d0, double d1, double d2) {
        super(EntityTypes.SPAWNER_MINECART, world, d0, d1, d2);
        this.ticker = this.a(world);
    }

    private Runnable a(World world) {
        return world instanceof WorldServer ? () -> {
            this.spawner.a((WorldServer) world, this.getChunkCoordinates());
        } : () -> {
            this.spawner.a(world, this.getChunkCoordinates());
        };
    }

    @Override
    public EntityMinecartAbstract.EnumMinecartType getMinecartType() {
        return EntityMinecartAbstract.EnumMinecartType.SPAWNER;
    }

    @Override
    public IBlockData r() {
        return Blocks.SPAWNER.getBlockData();
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.spawner.a(this.level, this.getChunkCoordinates(), nbttagcompound);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.spawner.b(this.level, this.getChunkCoordinates(), nbttagcompound);
    }

    @Override
    public void a(byte b0) {
        this.spawner.a(this.level, b0);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticker.run();
    }

    public MobSpawnerAbstract w() {
        return this.spawner;
    }

    @Override
    public boolean cy() {
        return true;
    }
}
